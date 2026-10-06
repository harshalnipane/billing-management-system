package com.resort.billing.model;

public class VillaBooking {
    private String bookingId;
    private String villaNumber;
    private String villaType;
    private String checkInDate;   // e.g. "2026-10-10 14:00"
    private String checkOutDate;  // e.g. "2026-10-13 11:00"
    private int totalNights;
    private int adultsCount;
    private int childrenCount;
    private int extraBedCount;
    private double baseRatePerNight;
    private double extraBedRatePerNight;
    private String mealPlan; // EP, CP, MAP, AP

    public VillaBooking() {
        this.totalNights = 1;
        this.adultsCount = 2;
        this.mealPlan = "CP";
    }

    public VillaBooking(String bookingId, String villaNumber, String villaType, 
                        String checkInDate, String checkOutDate, int totalNights, 
                        int adultsCount, int childrenCount, int extraBedCount, 
                        double baseRatePerNight, double extraBedRatePerNight, String mealPlan) {
        this.bookingId = bookingId;
        this.villaNumber = villaNumber;
        this.villaType = villaType;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalNights = Math.max(1, totalNights);
        this.adultsCount = adultsCount;
        this.childrenCount = childrenCount;
        this.extraBedCount = extraBedCount;
        this.baseRatePerNight = baseRatePerNight;
        this.extraBedRatePerNight = extraBedRatePerNight;
        this.mealPlan = mealPlan;
    }

    public double calculateTotalRoomAccommodationGross() {
        double roomTotal = baseRatePerNight * totalNights;
        double extraBedTotal = (extraBedCount * extraBedRatePerNight) * totalNights;
        return roomTotal + extraBedTotal;
    }

    // Getters and Setters
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getVillaNumber() { return villaNumber; }
    public void setVillaNumber(String villaNumber) { this.villaNumber = villaNumber; }

    public String getVillaType() { return villaType; }
    public void setVillaType(String villaType) { this.villaType = villaType; }

    public String getCheckInDate() { return checkInDate; }
    public void setCheckInDate(String checkInDate) { this.checkInDate = checkInDate; }

    public String getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(String checkOutDate) { this.checkOutDate = checkOutDate; }

    public int getTotalNights() { return totalNights; }
    public void setTotalNights(int totalNights) { this.totalNights = Math.max(1, totalNights); }

    public int getAdultsCount() { return adultsCount; }
    public void setAdultsCount(int adultsCount) { this.adultsCount = adultsCount; }

    public int getChildrenCount() { return childrenCount; }
    public void setChildrenCount(int childrenCount) { this.childrenCount = childrenCount; }

    public int getExtraBedCount() { return extraBedCount; }
    public void setExtraBedCount(int extraBedCount) { this.extraBedCount = extraBedCount; }

    public double getBaseRatePerNight() { return baseRatePerNight; }
    public void setBaseRatePerNight(double baseRatePerNight) { this.baseRatePerNight = baseRatePerNight; }

    public double getExtraBedRatePerNight() { return extraBedRatePerNight; }
    public void setExtraBedRatePerNight(double extraBedRatePerNight) { this.extraBedRatePerNight = extraBedRatePerNight; }

    public String getMealPlan() { return mealPlan; }
    public void setMealPlan(String mealPlan) { this.mealPlan = mealPlan; }
}
