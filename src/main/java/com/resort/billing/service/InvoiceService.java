package com.resort.billing.service;

import com.resort.billing.config.AppConfig;
import com.resort.billing.dto.*;
import com.resort.billing.model.*;
import com.resort.billing.repository.InvoiceRepository;

import java.text.SimpleDateFormat;
import java.util.*;

public class InvoiceService {

    private final InvoiceRepository repository;
    private final GstCalculationService calculationService;

    public InvoiceService(InvoiceRepository repository, GstCalculationService calculationService) {
        this.repository = repository;
        this.calculationService = calculationService;
    }

    public CalculateBillResponse calculate(CalculateBillRequest req) {
        if (req == null) req = new CalculateBillRequest();

        // 1. Prepare items list including room stay + extra bed if booking is provided
        List<BillItem> allItems = new ArrayList<>();

        if (req.getBooking() != null) {
            VillaBooking b = req.getBooking();
            int nights = Math.max(1, b.getTotalNights());
            double baseRate = b.getBaseRatePerNight();

            // Auto-determine accommodation GST slab
            double roomGstRate = calculationService.getAccommodationGstRate(baseRate);

            String villaDesc = (b.getVillaType() != null ? b.getVillaType() : "Villa Accommodation") 
                + " (" + (b.getVillaNumber() != null ? b.getVillaNumber() : "Stay") 
                + ", " + nights + " Night" + (nights > 1 ? "s" : "") 
                + " @ " + IndianCurrencyHelper.formatRupees(baseRate) + "/night)";

            BillItem roomItem = new BillItem("ITM-ROOM", ItemCategory.ACCOMMODATION,
                    villaDesc, "996311", nights, baseRate, roomGstRate);
            allItems.add(roomItem);

            // Extra bed if any
            if (b.getExtraBedCount() > 0 && b.getExtraBedRatePerNight() > 0) {
                double bedRate = b.getExtraBedRatePerNight();
                String bedDesc = "Extra Bed (" + b.getExtraBedCount() + " Bed" + (b.getExtraBedCount() > 1 ? "s" : "") 
                    + " × " + nights + " Nights @ " + IndianCurrencyHelper.formatRupees(bedRate) + "/night)";
                BillItem bedItem = new BillItem("ITM-EXTRA-BED", ItemCategory.EXTRA_BED,
                        bedDesc, "996311", b.getExtraBedCount() * nights, bedRate, roomGstRate);
                allItems.add(bedItem);
            }
        }

        // Add additional items (F&B, Spa, Activities, etc.)
        if (req.getAdditionalItems() != null) {
            allItems.addAll(req.getAdditionalItems());
        }

        // Determine Inter-State vs Intra-State
        String guestStateCode = req.getGuest() != null ? req.getGuest().getStateCode() : null;
        boolean isInterState = calculationService.determineIsInterState(
                AppConfig.RESORT_PROFILE.getStateCode(), guestStateCode, req.getIsInterStateOverride());

        GstCalculationService.CalculationResult calcRes = calculationService.calculateBill(
                allItems, req.getDiscountFlat(), req.getDiscountPercentage(), req.getAdvancePaid(), isInterState);

        CalculateBillResponse resp = new CalculateBillResponse();
        resp.setItems(calcRes.items);
        resp.setInterState(calcRes.isInterState);
        resp.setSupplyType(calcRes.isInterState ? "INTER_STATE (Integrated GST - IGST)" : "INTRA_STATE (Central GST + State GST - CGST & SGST)");
        resp.setGrossSubtotal(calcRes.grossSubtotal);
        resp.setTotalDiscount(calcRes.totalDiscount);
        resp.setTaxableAmount(calcRes.taxableAmount);
        resp.setCgstAmount(calcRes.cgstAmount);
        resp.setSgstAmount(calcRes.sgstAmount);
        resp.setIgstAmount(calcRes.igstAmount);
        resp.setTotalTax(calcRes.totalTax);
        resp.setRoundOff(calcRes.roundOff);
        resp.setGrandTotal(calcRes.grandTotal);
        resp.setAdvancePaid(calcRes.advancePaid);
        resp.setBalanceDue(calcRes.balanceDue);
        resp.setAmountInWords(calcRes.amountInWords);
        resp.setSlabBreakdowns(calcRes.slabBreakdowns);

        return resp;
    }

    public Invoice createInvoice(CreateInvoiceRequest req) {
        CalculateBillRequest calcReq = new CalculateBillRequest();
        calcReq.setGuest(req.getGuest());
        calcReq.setBooking(req.getBooking());
        calcReq.setAdditionalItems(req.getItems());
        if (req.getPayment() != null) {
            calcReq.setDiscountFlat(req.getPayment().getDiscountFlat());
            calcReq.setDiscountPercentage(req.getPayment().getDiscountPercentage());
            calcReq.setAdvancePaid(req.getPayment().getAdvancePaid());
        }
        calcReq.setIsInterStateOverride(req.getIsInterStateOverride());

        CalculateBillResponse calcResp = calculate(calcReq);

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(repository.generateNextInvoiceNumber());
        invoice.setInvoiceDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        invoice.setResortProfile(AppConfig.RESORT_PROFILE);
        invoice.setGuest(req.getGuest());
        invoice.setBooking(req.getBooking());
        invoice.setItems(calcResp.getItems());
        invoice.setInterState(calcResp.isInterState());

        String pos = (calcResp.isInterState() && req.getGuest() != null && req.getGuest().getState() != null)
                ? (req.getGuest().getState() + " (" + req.getGuest().getStateCode() + ")")
                : (AppConfig.RESORT_PROFILE.getState() + " (" + AppConfig.RESORT_PROFILE.getStateCode() + ")");
        invoice.setPlaceOfSupply(pos);

        invoice.setSubtotalGross(calcResp.getGrossSubtotal());
        invoice.setTotalDiscount(calcResp.getTotalDiscount());
        invoice.setTotalTaxable(calcResp.getTaxableAmount());
        invoice.setTotalCgst(calcResp.getCgstAmount());
        invoice.setTotalSgst(calcResp.getSgstAmount());
        invoice.setTotalIgst(calcResp.getIgstAmount());
        invoice.setTotalTax(calcResp.getTotalTax());
        invoice.setRoundOff(calcResp.getRoundOff());
        invoice.setGrandTotal(calcResp.getGrandTotal());
        invoice.setAmountInWords(calcResp.getAmountInWords());
        invoice.setTaxSlabs(calcResp.getSlabBreakdowns());
        invoice.setNotes(req.getNotes());

        PaymentDetails p = req.getPayment() != null ? req.getPayment() : new PaymentDetails();
        p.setTotalDiscount(calcResp.getTotalDiscount());
        p.setRoundOff(calcResp.getRoundOff());
        p.setGrandTotal(calcResp.getGrandTotal());
        p.setBalanceDue(calcResp.getBalanceDue());
        if (p.getPaymentDate() == null || p.getPaymentDate().isEmpty()) {
            p.setPaymentDate(invoice.getInvoiceDate());
        }
        if (p.getBalanceDue() <= 0.0) {
            p.setPaymentStatus("PAID");
        } else if (p.getAdvancePaid() > 0.0) {
            p.setPaymentStatus("PARTIAL");
        } else {
            p.setPaymentStatus("PENDING");
        }
        invoice.setPayment(p);

        repository.save(invoice);
        return invoice;
    }

    public List<Invoice> getAllInvoices(String query) {
        return repository.search(query);
    }

    public Optional<Invoice> getInvoice(String invoiceNumber) {
        return repository.findById(invoiceNumber);
    }

    public DashboardSummary getDashboardSummary() {
        List<Invoice> all = repository.findAll();
        DashboardSummary sum = new DashboardSummary();
        sum.setTotalInvoices(all.size());

        double grossRev = 0.0;
        double taxableRev = 0.0;
        double cgst = 0.0;
        double sgst = 0.0;
        double igst = 0.0;
        double advance = 0.0;
        double pending = 0.0;

        for (Invoice inv : all) {
            grossRev += inv.getGrandTotal();
            taxableRev += inv.getTotalTaxable();
            cgst += inv.getTotalCgst();
            sgst += inv.getTotalSgst();
            igst += inv.getTotalIgst();
            if (inv.getPayment() != null) {
                advance += inv.getPayment().getAdvancePaid();
                pending += inv.getPayment().getBalanceDue();
            }
        }

        sum.setTotalRevenue(round(grossRev));
        sum.setTaxableRevenue(round(taxableRev));
        sum.setTotalCgst(round(cgst));
        sum.setTotalSgst(round(sgst));
        sum.setTotalIgst(round(igst));
        sum.setTotalTax(round(cgst + sgst + igst));
        sum.setTotalAdvanceCollected(round(advance));
        sum.setTotalPendingDue(round(pending));

        int recentLimit = Math.min(5, all.size());
        sum.setRecentInvoices(all.subList(0, recentLimit));
        return sum;
    }

    private static double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }
}
