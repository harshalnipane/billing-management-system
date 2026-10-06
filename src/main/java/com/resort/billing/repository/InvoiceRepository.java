package com.resort.billing.repository;

import com.resort.billing.config.AppConfig;
import com.resort.billing.model.*;
import com.resort.billing.service.GstCalculationService;
import com.resort.billing.service.IndianCurrencyHelper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InvoiceRepository {

    private final Map<String, Invoice> invoiceStorage = new ConcurrentHashMap<>();
    private final AtomicInteger sequenceGenerator = new AtomicInteger(101);

    public InvoiceRepository() {
        seedSampleInvoices();
    }

    public synchronized String generateNextInvoiceNumber() {
        int seq = sequenceGenerator.getAndIncrement();
        return String.format("RES/2026-27/%05d", seq);
    }

    public void save(Invoice invoice) {
        if (invoice.getInvoiceNumber() == null || invoice.getInvoiceNumber().isEmpty()) {
            invoice.setInvoiceNumber(generateNextInvoiceNumber());
        }
        invoiceStorage.put(invoice.getInvoiceNumber(), invoice);
    }

    public Optional<Invoice> findById(String invoiceNumber) {
        return Optional.ofNullable(invoiceStorage.get(invoiceNumber));
    }

    public List<Invoice> findAll() {
        List<Invoice> list = new ArrayList<>(invoiceStorage.values());
        list.sort((a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()));
        return list;
    }

    public List<Invoice> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return findAll();
        }
        String q = query.toLowerCase().trim();
        List<Invoice> filtered = new ArrayList<>();
        for (Invoice inv : invoiceStorage.values()) {
            boolean match = (inv.getInvoiceNumber() != null && inv.getInvoiceNumber().toLowerCase().contains(q))
                || (inv.getGuest() != null && inv.getGuest().getFullName() != null && inv.getGuest().getFullName().toLowerCase().contains(q))
                || (inv.getGuest() != null && inv.getGuest().getPhoneNumber() != null && inv.getGuest().getPhoneNumber().contains(q))
                || (inv.getBooking() != null && inv.getBooking().getVillaNumber() != null && inv.getBooking().getVillaNumber().toLowerCase().contains(q));
            if (match) {
                filtered.add(inv);
            }
        }
        filtered.sort((a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()));
        return filtered;
    }

    private void seedSampleInvoices() {
        GstCalculationService calcService = new GstCalculationService();

        // Sample 1: Luxury Pool Villa (Inter-State IGST - Guest from Mumbai, Maharashtra 27 to Resort in Goa 30)
        {
            Invoice inv1 = new Invoice();
            inv1.setInvoiceNumber("RES/2026-27/00101");
            inv1.setInvoiceDate("2026-10-04");
            inv1.setResortProfile(AppConfig.RESORT_PROFILE);

            Guest g1 = new Guest("G-101", "Vikramaditya Singhania", "9820145678", "vikram.s@singhaniacorp.in",
                    "42 Nariman Point, Marine Drive", "Mumbai", "Maharashtra", "27",
                    "27AAACS1420M1ZK", "Singhania Holdings Pvt Ltd", "PASSPORT", "Z5891402");
            inv1.setGuest(g1);

            VillaBooking b1 = new VillaBooking("B-8801", "Villa 101 - Ocean Breeze",
                    "Royal Private Pool Villa (2 BHK)", "2026-10-01 14:00", "2026-10-04 11:00",
                    3, 2, 1, 1, 18500.0, 3000.0, "MAP");
            inv1.setBooking(b1);

            List<BillItem> items1 = new ArrayList<>();
            // Room Accommodation (Tariff > 7500 -> 18% GST)
            items1.add(new BillItem("ITM-1", ItemCategory.ACCOMMODATION,
                    "Royal Private Pool Villa (3 Nights @ ₹18,500/night)", "996311", 3, 18500.0, 18.0));
            // Extra Bed
            items1.add(new BillItem("ITM-2", ItemCategory.EXTRA_BED,
                    "Extra Rollaway Bed with Linen (3 Nights @ ₹3,000/night)", "996311", 3, 3000.0, 18.0));
            // F&B Dining
            items1.add(new BillItem("ITM-3", ItemCategory.FOOD_AND_BEVERAGE,
                    "In-Villa Fine Dining & Poolside Barbecue", "996331", 1, 14200.0, 18.0));
            // Spa
            items1.add(new BillItem("ITM-4", ItemCategory.SPA_AND_WELLNESS,
                    "Couple Ayurvedic Abhyanga & Aroma Therapy Spa (90 mins)", "999721", 1, 8500.0, 18.0));
            // Airport Transfer
            items1.add(new BillItem("ITM-5", ItemCategory.AIRPORT_TRANSFER,
                    "Luxury Airport Pickup & Drop (Mercedes V-Class)", "996412", 2, 3500.0, 5.0));

            GstCalculationService.CalculationResult res1 = calcService.calculateBill(items1, 5000.0, 0.0, 30000.0, true);
            applyCalcToInvoice(inv1, res1, "CREDIT_CARD", "HDFC_TXN_889211", "PAID");
            save(inv1);
        }

        // Sample 2: Garden Villa (Intra-State CGST+SGST - Local Guest from Panaji, Goa 30)
        {
            Invoice inv2 = new Invoice();
            inv2.setInvoiceNumber("RES/2026-27/00102");
            inv2.setInvoiceDate("2026-10-05");
            inv2.setResortProfile(AppConfig.RESORT_PROFILE);

            Guest g2 = new Guest("G-102", "Ananya Deshmukh", "9890512345", "ananya.d@gmail.com",
                    "Altinho Hill View Road", "Panaji", "Goa", "30",
                    "", "", "AADHAAR", "4598-1204-7712");
            inv2.setGuest(g2);

            VillaBooking b2 = new VillaBooking("B-8802", "Cottage 204 - Frangipani",
                    "Heritage Garden Villa", "2026-10-03 14:00", "2026-10-05 11:00",
                    2, 2, 0, 0, 6800.0, 0.0, "CP");
            inv2.setBooking(b2);

            List<BillItem> items2 = new ArrayList<>();
            // Room Accommodation (Tariff <= 7500 -> 12% GST)
            items2.add(new BillItem("ITM-6", ItemCategory.ACCOMMODATION,
                    "Heritage Garden Villa (2 Nights @ ₹6,800/night)", "996311", 2, 6800.0, 12.0));
            // F&B
            items2.add(new BillItem("ITM-7", ItemCategory.FOOD_AND_BEVERAGE,
                    "Spice Garden Cafe - Goan Seafood Platter & Drinks", "996331", 1, 4600.0, 5.0));
            // Spa
            items2.add(new BillItem("ITM-8", ItemCategory.SPA_AND_WELLNESS,
                    "Rejuvenating Swedish Massage (60 mins)", "999721", 1, 3500.0, 18.0));

            GstCalculationService.CalculationResult res2 = calcService.calculateBill(items2, 0.0, 10.0, 10000.0, false);
            applyCalcToInvoice(inv2, res2, "UPI", "UPI/GOA/20261005/77621", "PAID");
            save(inv2);
        }
    }

    private void applyCalcToInvoice(Invoice inv, GstCalculationService.CalculationResult res,
                                    String paymentMode, String txnRef, String status) {
        inv.setItems(res.items);
        inv.setInterState(res.isInterState);
        inv.setPlaceOfSupply(res.isInterState ? (inv.getGuest().getState() + " (" + inv.getGuest().getStateCode() + ")") : (inv.getResortProfile().getState() + " (" + inv.getResortProfile().getStateCode() + ")"));
        inv.setSubtotalGross(res.grossSubtotal);
        inv.setTotalDiscount(res.totalDiscount);
        inv.setTotalTaxable(res.taxableAmount);
        inv.setTotalCgst(res.cgstAmount);
        inv.setTotalSgst(res.sgstAmount);
        inv.setTotalIgst(res.igstAmount);
        inv.setTotalTax(res.totalTax);
        inv.setRoundOff(res.roundOff);
        inv.setGrandTotal(res.grandTotal);
        inv.setAmountInWords(res.amountInWords);
        inv.setTaxSlabs(res.slabBreakdowns);

        PaymentDetails p = new PaymentDetails();
        p.setPaymentMode(paymentMode);
        p.setTransactionReference(txnRef);
        p.setPaymentStatus(status);
        p.setAdvancePaid(res.advancePaid);
        p.setTotalDiscount(res.totalDiscount);
        p.setRoundOff(res.roundOff);
        p.setGrandTotal(res.grandTotal);
        p.setBalanceDue(res.balanceDue);
        p.setPaymentDate(inv.getInvoiceDate());
        inv.setPayment(p);
    }
}
