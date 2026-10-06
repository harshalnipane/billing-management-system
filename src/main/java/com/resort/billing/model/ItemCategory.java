package com.resort.billing.model;

/**
 * Service/Product Categories for Resort and Villa billing with standard Indian SAC codes.
 */
public enum ItemCategory {
    ACCOMMODATION("996311", "Room / Villa Stay Accommodation"),
    EXTRA_BED("996311", "Extra Bed / Additional Guest Tariff"),
    FOOD_AND_BEVERAGE("996331", "Restaurant / In-Villa Dining"),
    SPA_AND_WELLNESS("999721", "Spa, Ayurvedic & Wellness Services"),
    RESORT_ACTIVITIES("999699", "Water Sports & Recreational Activities"),
    AIRPORT_TRANSFER("996412", "Chauffeur & Airport Transport"),
    LAUNDRY_SERVICE("999799", "Laundry & Valet Services"),
    MINI_BAR("996332", "Mini Bar & Confectioneries"),
    BANQUET_EVENT("997212", "Banquet Lawn & Private Event"),
    MISCELLANEOUS("999900", "Miscellaneous Services");

    private final String defaultSacCode;
    private final String description;

    ItemCategory(String defaultSacCode, String description) {
        this.defaultSacCode = defaultSacCode;
        this.description = description;
    }

    public String getDefaultSacCode() {
        return defaultSacCode;
    }

    public String getDescription() {
        return description;
    }
}
