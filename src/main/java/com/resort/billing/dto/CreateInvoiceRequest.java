package com.resort.billing.dto;

import com.resort.billing.model.BillItem;
import com.resort.billing.model.Guest;
import com.resort.billing.model.PaymentDetails;
import com.resort.billing.model.VillaBooking;
import java.util.ArrayList;
import java.util.List;

public class CreateInvoiceRequest {
    private Guest guest;
    private VillaBooking booking;
    private List<BillItem> items = new ArrayList<>();
    private PaymentDetails payment;
    private Boolean isInterStateOverride;
    private String notes;

    public CreateInvoiceRequest() {}

    // Getters and Setters
    public Guest getGuest() { return guest; }
    public void setGuest(Guest guest) { this.guest = guest; }

    public VillaBooking getBooking() { return booking; }
    public void setBooking(VillaBooking booking) { this.booking = booking; }

    public List<BillItem> getItems() { return items; }
    public void setItems(List<BillItem> items) { this.items = items; }

    public PaymentDetails getPayment() { return payment; }
    public void setPayment(PaymentDetails payment) { this.payment = payment; }

    public Boolean getIsInterStateOverride() { return isInterStateOverride; }
    public void setIsInterStateOverride(Boolean isInterStateOverride) { this.isInterStateOverride = isInterStateOverride; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
