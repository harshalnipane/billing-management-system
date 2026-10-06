package com.resort.billing.config;

import com.resort.billing.model.ResortProfile;

import java.util.*;

public class AppConfig {

    public static final ResortProfile RESORT_PROFILE = ResortProfile.getDefaultProfile();

    // Indian States and GST State Codes (2-digit)
    public static final Map<String, String> INDIAN_STATES = new LinkedHashMap<>();

    static {
        INDIAN_STATES.put("01", "Jammu and Kashmir");
        INDIAN_STATES.put("02", "Himachal Pradesh");
        INDIAN_STATES.put("03", "Punjab");
        INDIAN_STATES.put("04", "Chandigarh");
        INDIAN_STATES.put("05", "Uttarakhand");
        INDIAN_STATES.put("06", "Haryana");
        INDIAN_STATES.put("07", "Delhi");
        INDIAN_STATES.put("08", "Rajasthan");
        INDIAN_STATES.put("09", "Uttar Pradesh");
        INDIAN_STATES.put("10", "Bihar");
        INDIAN_STATES.put("11", "Sikkim");
        INDIAN_STATES.put("12", "Arunachal Pradesh");
        INDIAN_STATES.put("13", "Nagaland");
        INDIAN_STATES.put("14", "Manipur");
        INDIAN_STATES.put("15", "Mizoram");
        INDIAN_STATES.put("16", "Tripura");
        INDIAN_STATES.put("17", "Meghalaya");
        INDIAN_STATES.put("18", "Assam");
        INDIAN_STATES.put("19", "West Bengal");
        INDIAN_STATES.put("20", "Jharkhand");
        INDIAN_STATES.put("21", "Odisha");
        INDIAN_STATES.put("22", "Chhattisgarh");
        INDIAN_STATES.put("23", "Madhya Pradesh");
        INDIAN_STATES.put("24", "Gujarat");
        INDIAN_STATES.put("27", "Maharashtra");
        INDIAN_STATES.put("29", "Karnataka");
        INDIAN_STATES.put("30", "Goa");
        INDIAN_STATES.put("32", "Kerala");
        INDIAN_STATES.put("33", "Tamil Nadu");
        INDIAN_STATES.put("36", "Telangana");
        INDIAN_STATES.put("37", "Andhra Pradesh");
    }

    public static class VillaTypeOption {
        public String id;
        public String name;
        public double defaultRate;
        public String description;

        public VillaTypeOption(String id, String name, double defaultRate, String description) {
            this.id = id;
            this.name = name;
            this.defaultRate = defaultRate;
            this.description = description;
        }
    }

    public static final List<VillaTypeOption> VILLA_TYPES = Arrays.asList(
        new VillaTypeOption("VILLA-ROYAL", "Royal Private Pool Villa (2 BHK)", 18500.0, "Sea-view infinity pool, private sun deck, butler service"),
        new VillaTypeOption("VILLA-SUNSET", "Sunset Beach Cottage (1 BHK)", 9500.0, "Direct beach access, plunge tub, open air garden shower"),
        new VillaTypeOption("SUITE-PRESIDENTIAL", "Presidential Lagoon Suite", 25000.0, "3-bedroom luxury suite, jacuzzi, private cabana, personal chef"),
        new VillaTypeOption("VILLA-GARDEN", "Heritage Garden Villa", 6800.0, "Lush tropical view, colonial architecture, patio veranda"),
        new VillaTypeOption("ROOM-DELUXE", "Deluxe Resort Room", 5200.0, "Balcony with pool view, king bed, marble bath")
    );
}
