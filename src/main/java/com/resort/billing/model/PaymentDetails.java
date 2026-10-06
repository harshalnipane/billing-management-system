package com.resort.billing.model;

public class PaymentDetails {
    private String paymentMode; // CASH, CREDIT_CARD, UPI, NET_BANKING
    private String transactionReference;
    private String paymentStatus; // PAID, PARTIAL, PENDING
    private double advancePaid;
    private double discountFlat;
    private double discountPercentage;
    private double totalDiscount;
    private double roundOff; // CGST Rule 54 rounding to nearest integer
    private double grandTotal;
    private double balanceDue;
    private String paymentDate;

    public PaymentDetails() {
        this.paymentMode = "UPI";
        this.paymentStatus = "PAID";
    }

    // Getters and Setters
    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public String getTransactionReference() { return transactionReference; }
    public void setTransactionReference(String transactionReference) { this.transactionReference = transactionReference; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public double getAdvancePaid() { return advancePaid; }
    public void setAdvancePaid(double advancePaid) { this.advancePaid = advancePaid; }

    public double getDiscountFlat() { return discountFlat; }
    public void setDiscountFlat(double discountFlat) { this.discountFlat = discountFlat; }

    public double getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(double discountPercentage) { this.discountPercentage = discountPercentage; }

    public double getTotalDiscount() { return totalDiscount; }
    public void setTotalDiscount(double totalDiscount) { this.totalDiscount = totalDiscount; }

    public double getRoundOff() { return roundOff; }
    public void setRoundOff(double roundOff) { this.roundOff = roundOff; }

    public double getGrandTotal() { return grandTotal; }
    public void setGrandTotal(double grandTotal) { this.grandTotal = grandTotal; }

    public double getBalanceDue() { return balanceDue; }
    public void setBalanceDue(double balanceDue) { this.balanceDue = balanceDue; }

    public String getPaymentDate() { return paymentDate; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }
}
