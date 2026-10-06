package com.resort.billing.model;

import java.util.ArrayList;
import java.util.List;

public class Invoice {
    private String invoiceNumber;
    private String invoiceDate;
    private String invoiceType; // "TAX_INVOICE"
    private ResortProfile resortProfile;
    private Guest guest;
    private VillaBooking booking;
    private List<BillItem> items = new ArrayList<>();
    private boolean isInterState;
    private String placeOfSupply;
    private double subtotalGross;
    private double totalDiscount;
    private double totalTaxable;
    private double totalCgst;
    private double totalSgst;
    private double totalIgst;
    private double totalTax;
    private double roundOff;
    private double grandTotal;
    private String amountInWords;
    private PaymentDetails payment;
    private List<TaxSlabBreakdown> taxSlabs = new ArrayList<>();
    private String notes;
    private long createdAt;

    public Invoice() {
        this.createdAt = System.currentTimeMillis();
        this.invoiceType = "TAX_INVOICE";
    }

    // Getters and Setters
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public String getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(String invoiceDate) { this.invoiceDate = invoiceDate; }

    public String getInvoiceType() { return invoiceType; }
    public void setInvoiceType(String invoiceType) { this.invoiceType = invoiceType; }

    public ResortProfile getResortProfile() { return resortProfile; }
    public void setResortProfile(ResortProfile resortProfile) { this.resortProfile = resortProfile; }

    public Guest getGuest() { return guest; }
    public void setGuest(Guest guest) { this.guest = guest; }

    public VillaBooking getBooking() { return booking; }
    public void setBooking(VillaBooking booking) { this.booking = booking; }

    public List<BillItem> getItems() { return items; }
    public void setItems(List<BillItem> items) { this.items = items; }

    public boolean isInterState() { return isInterState; }
    public void setInterState(boolean interState) { isInterState = interState; }

    public String getPlaceOfSupply() { return placeOfSupply; }
    public void setPlaceOfSupply(String placeOfSupply) { this.placeOfSupply = placeOfSupply; }

    public double getSubtotalGross() { return subtotalGross; }
    public void setSubtotalGross(double subtotalGross) { this.subtotalGross = subtotalGross; }

    public double getTotalDiscount() { return totalDiscount; }
    public void setTotalDiscount(double totalDiscount) { this.totalDiscount = totalDiscount; }

    public double getTotalTaxable() { return totalTaxable; }
    public void setTotalTaxable(double totalTaxable) { this.totalTaxable = totalTaxable; }

    public double getTotalCgst() { return totalCgst; }
    public void setTotalCgst(double totalCgst) { this.totalCgst = totalCgst; }

    public double getTotalSgst() { return totalSgst; }
    public void setTotalSgst(double totalSgst) { this.totalSgst = totalSgst; }

    public double getTotalIgst() { return totalIgst; }
    public void setTotalIgst(double totalIgst) { this.totalIgst = totalIgst; }

    public double getTotalTax() { return totalTax; }
    public void setTotalTax(double totalTax) { this.totalTax = totalTax; }

    public double getRoundOff() { return roundOff; }
    public void setRoundOff(double roundOff) { this.roundOff = roundOff; }

    public double getGrandTotal() { return grandTotal; }
    public void setGrandTotal(double grandTotal) { this.grandTotal = grandTotal; }

    public String getAmountInWords() { return amountInWords; }
    public void setAmountInWords(String amountInWords) { this.amountInWords = amountInWords; }

    public PaymentDetails getPayment() { return payment; }
    public void setPayment(PaymentDetails payment) { this.payment = payment; }

    public List<TaxSlabBreakdown> getTaxSlabs() { return taxSlabs; }
    public void setTaxSlabs(List<TaxSlabBreakdown> taxSlabs) { this.taxSlabs = taxSlabs; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
