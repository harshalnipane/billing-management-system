package com.resort.billing.model;

public class TaxSlabBreakdown {
    private double ratePercent;
    private double taxableAmount;
    private double cgstRate;
    private double cgstAmount;
    private double sgstRate;
    private double sgstAmount;
    private double igstRate;
    private double igstAmount;
    private double totalTax;

    public TaxSlabBreakdown() {}

    public TaxSlabBreakdown(double ratePercent) {
        this.ratePercent = ratePercent;
    }

    public void addTaxable(double taxable, boolean isInterState) {
        this.taxableAmount = round(this.taxableAmount + taxable);
        if (isInterState) {
            this.igstRate = ratePercent;
            this.igstAmount = round(this.igstAmount + (taxable * (ratePercent / 100.0)));
            this.cgstRate = 0.0;
            this.cgstAmount = 0.0;
            this.sgstRate = 0.0;
            this.sgstAmount = 0.0;
        } else {
            this.cgstRate = ratePercent / 2.0;
            this.sgstRate = ratePercent / 2.0;
            this.cgstAmount = round(this.cgstAmount + (taxable * (cgstRate / 100.0)));
            this.sgstAmount = round(this.sgstAmount + (taxable * (sgstRate / 100.0)));
            this.igstRate = 0.0;
            this.igstAmount = 0.0;
        }
        this.totalTax = round(this.cgstAmount + this.sgstAmount + this.igstAmount);
    }

    private static double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }

    // Getters and Setters
    public double getRatePercent() { return ratePercent; }
    public void setRatePercent(double ratePercent) { this.ratePercent = ratePercent; }

    public double getTaxableAmount() { return taxableAmount; }
    public void setTaxableAmount(double taxableAmount) { this.taxableAmount = taxableAmount; }

    public double getCgstRate() { return cgstRate; }
    public void setCgstRate(double cgstRate) { this.cgstRate = cgstRate; }

    public double getCgstAmount() { return cgstAmount; }
    public void setCgstAmount(double cgstAmount) { this.cgstAmount = cgstAmount; }

    public double getSgstRate() { return sgstRate; }
    public void setSgstRate(double sgstRate) { this.sgstRate = sgstRate; }

    public double getSgstAmount() { return sgstAmount; }
    public void setSgstAmount(double sgstAmount) { this.sgstAmount = sgstAmount; }

    public double getIgstRate() { return igstRate; }
    public void setIgstRate(double igstRate) { this.igstRate = igstRate; }

    public double getIgstAmount() { return igstAmount; }
    public void setIgstAmount(double igstAmount) { this.igstAmount = igstAmount; }

    public double getTotalTax() { return totalTax; }
    public void setTotalTax(double totalTax) { this.totalTax = totalTax; }
}
