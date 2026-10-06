package com.resort.billing.service;

import com.resort.billing.model.BillItem;
import com.resort.billing.model.ItemCategory;
import com.resort.billing.model.TaxSlabBreakdown;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Indian Goods and Services Tax (GST) Calculation Service
 * compliant with Indian CGST, SGST, and IGST Acts for Hotels, Resorts, and Villas.
 */
public class GstCalculationService {

    /**
     * Determines GST rate for accommodation based on declared tariff per room/unit per night:
     * - Tariff <= ₹7,500: 12% GST (6% CGST + 6% SGST or 12% IGST)
     * - Tariff > ₹7,500: 18% GST (9% CGST + 9% SGST or 18% IGST)
     */
    public double getAccommodationGstRate(double tariffPerNight) {
        if (tariffPerNight <= 7500.0) {
            return 12.0;
        } else {
            return 18.0;
        }
    }

    /**
     * Suggests default GST rate for a given category and accommodation base tariff context.
     */
    public double getDefaultGstRateForCategory(ItemCategory category, double roomBaseTariff) {
        if (category == null) return 18.0;

        switch (category) {
            case ACCOMMODATION:
            case EXTRA_BED:
                return getAccommodationGstRate(roomBaseTariff);

            case FOOD_AND_BEVERAGE:
                // Under Indian GST notifications, restaurants at hotel premises with declared tariff
                // >= ₹7,500 are charged at 18%, while standard standalone/budget hotel restaurants are at 5%.
                return roomBaseTariff >= 7500.0 ? 18.0 : 5.0;

            case SPA_AND_WELLNESS:
            case BANQUET_EVENT:
            case LAUNDRY_SERVICE:
            case MINI_BAR:
            case MISCELLANEOUS:
                return 18.0;

            case RESORT_ACTIVITIES:
                return 18.0;

            case AIRPORT_TRANSFER:
                return 5.0; // Passenger transport service (cab/van without fuel credit)

            default:
                return 18.0;
        }
    }

    /**
     * Checks if supply is Inter-State (IGST) or Intra-State (CGST + SGST).
     * @param resortStateCode Resort's 2-digit GST state code (e.g., "30" for Goa)
     * @param guestStateCode Guest's 2-digit GST state code (e.g., "27" for Maharashtra)
     * @param override Explicit manual override from user if provided
     */
    public boolean determineIsInterState(String resortStateCode, String guestStateCode, Boolean override) {
        if (override != null) {
            return override;
        }
        if (guestStateCode != null && !guestStateCode.trim().isEmpty() 
            && resortStateCode != null && !resortStateCode.trim().isEmpty()) {
            return !resortStateCode.trim().equals(guestStateCode.trim());
        }
        return false; // Default to intra-state if guest state not specified
    }

    /**
     * Calculates tax, discounts, and totals for a list of bill items.
     */
    public CalculationResult calculateBill(List<BillItem> items, 
                                          double discountFlat, 
                                          double discountPercent, 
                                          double advancePaid, 
                                          boolean isInterState) {
        if (items == null) {
            items = new ArrayList<>();
        }

        // 1. Calculate Gross Subtotal
        double grossSubtotal = 0.0;
        for (BillItem item : items) {
            double lineGross = round(item.getQuantity() * item.getUnitRate());
            item.setGrossAmount(lineGross);
            grossSubtotal += lineGross;
        }

        // 2. Compute Total Discount
        double calculatedDiscount = 0.0;
        if (discountPercent > 0) {
            calculatedDiscount = round(grossSubtotal * (discountPercent / 100.0));
        }
        if (discountFlat > 0) {
            calculatedDiscount += discountFlat;
        }
        calculatedDiscount = Math.min(calculatedDiscount, grossSubtotal);

        // 3. Distribute Discount Proportionally across items as per Section 15(3) of CGST Act
        double totalTaxable = 0.0;
        for (BillItem item : items) {
            double itemDiscount = 0.0;
            if (grossSubtotal > 0 && calculatedDiscount > 0) {
                itemDiscount = round(calculatedDiscount * (item.getGrossAmount() / grossSubtotal));
            }
            item.setDiscountAmount(itemDiscount);
            double taxable = Math.max(0.0, round(item.getGrossAmount() - itemDiscount));
            item.setTaxableAmount(taxable);
            totalTaxable += taxable;
        }

        // 4. Calculate Taxes per Item and Slab Breakdowns
        Map<Double, TaxSlabBreakdown> slabMap = new TreeMap<>();
        double totalCgst = 0.0;
        double totalSgst = 0.0;
        double totalIgst = 0.0;

        for (BillItem item : items) {
            double rate = item.getGstRate();
            double taxable = item.getTaxableAmount();

            if (isInterState) {
                double igstRate = rate;
                double igstAmt = round(taxable * (igstRate / 100.0));
                item.setIgstRate(igstRate);
                item.setIgstAmount(igstAmt);
                item.setCgstRate(0.0);
                item.setCgstAmount(0.0);
                item.setSgstRate(0.0);
                item.setSgstAmount(0.0);
                item.setTotalAmount(round(taxable + igstAmt));

                totalIgst += igstAmt;
            } else {
                double halfRate = rate / 2.0;
                double cgstAmt = round(taxable * (halfRate / 100.0));
                double sgstAmt = round(taxable * (halfRate / 100.0));
                item.setCgstRate(halfRate);
                item.setCgstAmount(cgstAmt);
                item.setSgstRate(halfRate);
                item.setSgstAmount(sgstAmt);
                item.setIgstRate(0.0);
                item.setIgstAmount(0.0);
                item.setTotalAmount(round(taxable + cgstAmt + sgstAmt));

                totalCgst += cgstAmt;
                totalSgst += sgstAmt;
            }

            // Group into slab breakdown
            TaxSlabBreakdown breakdown = slabMap.computeIfAbsent(rate, r -> new TaxSlabBreakdown(r));
            breakdown.addTaxable(taxable, isInterState);
        }

        double totalTax = round(totalCgst + totalSgst + totalIgst);
        double exactTotal = round(totalTaxable + totalTax);

        // 5. Rounding off as per Indian CGST Rule 54
        long roundedGrandTotal = Math.round(exactTotal);
        double roundOff = round(roundedGrandTotal - exactTotal);

        double balanceDue = Math.max(0.0, round(roundedGrandTotal - advancePaid));

        CalculationResult result = new CalculationResult();
        result.items = items;
        result.isInterState = isInterState;
        result.grossSubtotal = round(grossSubtotal);
        result.totalDiscount = round(calculatedDiscount);
        result.taxableAmount = round(totalTaxable);
        result.cgstAmount = round(totalCgst);
        result.sgstAmount = round(totalSgst);
        result.igstAmount = round(totalIgst);
        result.totalTax = totalTax;
        result.roundOff = roundOff;
        result.grandTotal = (double) roundedGrandTotal;
        result.advancePaid = round(advancePaid);
        result.balanceDue = balanceDue;
        result.slabBreakdowns = new ArrayList<>(slabMap.values());
        result.amountInWords = IndianCurrencyHelper.convertToWords(roundedGrandTotal);

        return result;
    }

    private static double round(double val) {
        return BigDecimal.valueOf(val).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public static class CalculationResult {
        public List<BillItem> items;
        public boolean isInterState;
        public double grossSubtotal;
        public double totalDiscount;
        public double taxableAmount;
        public double cgstAmount;
        public double sgstAmount;
        public double igstAmount;
        public double totalTax;
        public double roundOff;
        public double grandTotal;
        public double advancePaid;
        public double balanceDue;
        public String amountInWords;
        public List<TaxSlabBreakdown> slabBreakdowns;
    }
}
