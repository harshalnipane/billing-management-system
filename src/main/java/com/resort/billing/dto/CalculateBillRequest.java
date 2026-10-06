package com.resort.billing.dto;

import com.resort.billing.model.BillItem;
import com.resort.billing.model.Guest;
import com.resort.billing.model.VillaBooking;
import java.util.ArrayList;
import java.util.List;

public class CalculateBillRequest {
    private Guest guest;
    private VillaBooking booking;
    private List<BillItem> additionalItems = new ArrayList<>();
    private double discountFlat;
    private double discountPercentage;
    private double advancePaid;
    private Boolean isInterStateOverride; // null = auto detect based on guest state vs resort state

    public CalculateBillRequest() {}

    // Getters and Setters
    public Guest getGuest() { return guest; }
    public void setGuest(Guest guest) { this.guest = guest; }

    public VillaBooking getBooking() { return booking; }
    public void setBooking(VillaBooking booking) { this.booking = booking; }

    public List<BillItem> getAdditionalItems() { return additionalItems; }
    public void setAdditionalItems(List<BillItem> additionalItems) { this.additionalItems = additionalItems; }

    public double getDiscountFlat() { return discountFlat; }
    public void setDiscountFlat(double discountFlat) { this.discountFlat = discountFlat; }

    public double getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(double discountPercentage) { this.discountPercentage = discountPercentage; }

    public double getAdvancePaid() { return advancePaid; }
    public void setAdvancePaid(double advancePaid) { this.advancePaid = advancePaid; }

    public Boolean getIsInterStateOverride() { return isInterStateOverride; }
    public void setIsInterStateOverride(Boolean isInterStateOverride) { this.isInterStateOverride = isInterStateOverride; }
}
