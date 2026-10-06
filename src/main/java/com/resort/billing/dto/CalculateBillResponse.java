package com.resort.billing.dto;

import com.resort.billing.model.BillItem;
import com.resort.billing.model.TaxSlabBreakdown;
import java.util.ArrayList;
import java.util.List;

public class CalculateBillResponse {
    private List<BillItem> items = new ArrayList<>();
    private boolean isInterState;
    private String supplyType; // "INTRA_STATE (CGST + SGST)" or "INTER_STATE (IGST)"
    private double grossSubtotal;
    private double totalDiscount;
    private double taxableAmount;
    private double cgstAmount;
    private double sgstAmount;
    private double igstAmount;
    private double totalTax;
    private double roundOff;
    private double grandTotal;
    private double advancePaid;
    private double balanceDue;
    private String amountInWords;
    private List<TaxSlabBreakdown> slabBreakdowns = new ArrayList<>();

    public CalculateBillResponse() {}

    // Getters and Setters
    public List<BillItem> getItems() { return items; }
    public void setItems(List<BillItem> items) { this.items = items; }

    public boolean isInterState() { return isInterState; }
    public void setInterState(boolean interState) { isInterState = interState; }

    public String getSupplyType() { return supplyType; }
    public void setSupplyType(String supplyType) { this.supplyType = supplyType; }

    public double getGrossSubtotal() { return grossSubtotal; }
    public void setGrossSubtotal(double grossSubtotal) { this.grossSubtotal = grossSubtotal; }

    public double getTotalDiscount() { return totalDiscount; }
    public void setTotalDiscount(double totalDiscount) { this.totalDiscount = totalDiscount; }

    public double getTaxableAmount() { return taxableAmount; }
    public void setTaxableAmount(double taxableAmount) { this.taxableAmount = taxableAmount; }

    public double getCgstAmount() { return cgstAmount; }
    public void setCgstAmount(double cgstAmount) { this.cgstAmount = cgstAmount; }

    public double getSgstAmount() { return sgstAmount; }
    public void setSgstAmount(double sgstAmount) { this.sgstAmount = sgstAmount; }

    public double getIgstAmount() { return igstAmount; }
    public void setIgstAmount(double igstAmount) { this.igstAmount = igstAmount; }

    public double getTotalTax() { return totalTax; }
    public void setTotalTax(double totalTax) { this.totalTax = totalTax; }

    public double getRoundOff() { return roundOff; }
    public void setRoundOff(double roundOff) { this.roundOff = roundOff; }

    public double getGrandTotal() { return grandTotal; }
    public void setGrandTotal(double grandTotal) { this.grandTotal = grandTotal; }

    public double getAdvancePaid() { return advancePaid; }
    public void setAdvancePaid(double advancePaid) { this.advancePaid = advancePaid; }

    public double getBalanceDue() { return balanceDue; }
    public void setBalanceDue(double balanceDue) { this.balanceDue = balanceDue; }

    public String getAmountInWords() { return amountInWords; }
    public void setAmountInWords(String amountInWords) { this.amountInWords = amountInWords; }

    public List<TaxSlabBreakdown> getSlabBreakdowns() { return slabBreakdowns; }
    public void setSlabBreakdowns(List<TaxSlabBreakdown> slabBreakdowns) { this.slabBreakdowns = slabBreakdowns; }
}
