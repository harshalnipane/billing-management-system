package com.resort.billing.model;

public class BillItem {
    private String id;
    private ItemCategory category;
    private String description;
    private String sacCode;
    private double quantity;
    private double unitRate;
    private double grossAmount;
    private double discountAmount;
    private double taxableAmount;
    private double gstRate; // Total GST Rate (e.g., 12.0 or 18.0)
    private double cgstRate;
    private double cgstAmount;
    private double sgstRate;
    private double sgstAmount;
    private double igstRate;
    private double igstAmount;
    private double totalAmount;

    public BillItem() {
        this.quantity = 1.0;
    }

    public BillItem(String id, ItemCategory category, String description, String sacCode, 
                    double quantity, double unitRate, double gstRate) {
        this.id = id;
        this.category = category;
        this.description = description;
        this.sacCode = sacCode != null ? sacCode : (category != null ? category.getDefaultSacCode() : "996311");
        this.quantity = quantity;
        this.unitRate = unitRate;
        this.grossAmount = quantity * unitRate;
        this.gstRate = gstRate;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public ItemCategory getCategory() { return category; }
    public void setCategory(ItemCategory category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSacCode() { return sacCode; }
    public void setSacCode(String sacCode) { this.sacCode = sacCode; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public double getUnitRate() { return unitRate; }
    public void setUnitRate(double unitRate) { this.unitRate = unitRate; }

    public double getGrossAmount() { return grossAmount; }
    public void setGrossAmount(double grossAmount) { this.grossAmount = grossAmount; }

    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }

    public double getTaxableAmount() { return taxableAmount; }
    public void setTaxableAmount(double taxableAmount) { this.taxableAmount = taxableAmount; }

    public double getGstRate() { return gstRate; }
    public void setGstRate(double gstRate) { this.gstRate = gstRate; }

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

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
}
