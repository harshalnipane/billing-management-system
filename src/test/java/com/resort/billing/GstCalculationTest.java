package com.resort.billing;

import com.resort.billing.model.*;
import com.resort.billing.service.GstCalculationService;
import com.resort.billing.service.IndianCurrencyHelper;

import java.util.ArrayList;
import java.util.List;

public class GstCalculationTest {

    public static void main(String[] args) {
        System.out.println("Running GST Calculation Engine Tests...");
        GstCalculationService service = new GstCalculationService();

        testTariffThreshold(service);
        testIntraStateCalculation(service);
        testInterStateCalculation(service);
        testDiscountProportion(service);
        testCurrencyToWords();

        System.out.println(">>> ALL 5 TEST SUITES PASSED SUCCESSFULLY! <<<");
    }

    private static void testTariffThreshold(GstCalculationService service) {
        double rateLow = service.getAccommodationGstRate(5000.0);
        assertDouble(rateLow, 12.0, "Tariff <= 7500 must be 12% GST");

        double rateBoundary = service.getAccommodationGstRate(7500.0);
        assertDouble(rateBoundary, 12.0, "Tariff == 7500 must be 12% GST");

        double rateHigh = service.getAccommodationGstRate(12000.0);
        assertDouble(rateHigh, 18.0, "Tariff > 7500 must be 18% GST");
        System.out.println("[PASS] Room Tariff Threshold Test");
    }

    private static void testIntraStateCalculation(GstCalculationService service) {
        List<BillItem> items = new ArrayList<>();
        items.add(new BillItem("1", ItemCategory.ACCOMMODATION, "Room", "996311", 1, 6000.0, 12.0));

        GstCalculationService.CalculationResult res = service.calculateBill(items, 0, 0, 0, false);
        assertDouble(res.taxableAmount, 6000.0, "Taxable amount");
        assertDouble(res.cgstAmount, 360.0, "CGST must be 6% of 6000 = 360");
        assertDouble(res.sgstAmount, 360.0, "SGST must be 6% of 6000 = 360");
        assertDouble(res.igstAmount, 0.0, "IGST must be 0 for intra-state");
        assertDouble(res.grandTotal, 6720.0, "Grand total must be 6720");
        System.out.println("[PASS] Intra-State CGST + SGST Test");
    }

    private static void testInterStateCalculation(GstCalculationService service) {
        List<BillItem> items = new ArrayList<>();
        items.add(new BillItem("1", ItemCategory.ACCOMMODATION, "Villa", "996311", 2, 10000.0, 18.0));

        GstCalculationService.CalculationResult res = service.calculateBill(items, 0, 0, 0, true);
        assertDouble(res.taxableAmount, 20000.0, "Taxable amount");
        assertDouble(res.cgstAmount, 0.0, "CGST must be 0 for inter-state");
        assertDouble(res.sgstAmount, 0.0, "SGST must be 0 for inter-state");
        assertDouble(res.igstAmount, 3600.0, "IGST must be 18% of 20000 = 3600");
        assertDouble(res.grandTotal, 23600.0, "Grand total must be 23600");
        System.out.println("[PASS] Inter-State IGST Test");
    }

    private static void testDiscountProportion(GstCalculationService service) {
        List<BillItem> items = new ArrayList<>();
        items.add(new BillItem("1", ItemCategory.ACCOMMODATION, "Villa", "996311", 1, 10000.0, 18.0));

        // 10% discount on 10,000 = 1,000 discount -> Taxable 9,000 -> 18% IGST = 1,620 -> Total 10,620
        GstCalculationService.CalculationResult res = service.calculateBill(items, 0, 10.0, 0, true);
        assertDouble(res.totalDiscount, 1000.0, "Total discount");
        assertDouble(res.taxableAmount, 9000.0, "Taxable amount after discount");
        assertDouble(res.igstAmount, 1620.0, "IGST on discounted taxable");
        assertDouble(res.grandTotal, 10620.0, "Grand total after discount & tax");
        System.out.println("[PASS] Discount Deduction Test");
    }

    private static void testCurrencyToWords() {
        String words = IndianCurrencyHelper.convertToWords(23600.0);
        if (!words.contains("Twenty Three Thousand Six Hundred")) {
            throw new AssertionError("Unexpected words output: " + words);
        }
        System.out.println("[PASS] Indian Number to Words Test: " + words);
    }

    private static void assertDouble(double actual, double expected, String msg) {
        if (Math.abs(actual - expected) > 0.01) {
            throw new AssertionError(msg + " -> Expected: " + expected + ", Got: " + actual);
        }
    }
}
