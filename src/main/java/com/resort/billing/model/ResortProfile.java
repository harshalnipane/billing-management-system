package com.resort.billing.model;

public class ResortProfile {
    private String resortName;
    private String tagline;
    private String address;
    private String city;
    private String state;
    private String stateCode;
    private String gstin;
    private String pan;
    private String contactPhone;
    private String contactEmail;
    private String website;
    private String bankName;
    private String bankAccountNumber;
    private String bankIfsc;
    private String bankBranch;
    private String upiId;

    public ResortProfile() {}

    public static ResortProfile getDefaultProfile() {
        ResortProfile p = new ResortProfile();
        p.setResortName("Serene Palms Luxury Villa & Beach Resort");
        p.setTagline("Exclusive Coastal Sanctuary & Private Pool Villas");
        p.setAddress("Sinquerim Beach Road, Candolim, North Goa");
        p.setCity("Candolim");
        p.setState("Goa");
        p.setStateCode("30");
        p.setGstin("30AABCR1234F1Z5");
        p.setPan("AABCR1234F");
        p.setContactPhone("+91 832 249 8800");
        p.setContactEmail("reservations@serenepalmsresort.in");
        p.setWebsite("www.serenepalmsresort.in");
        p.setBankName("HDFC Bank Ltd");
        p.setBankAccountNumber("50200088921473");
        p.setBankIfsc("HDFC0000452");
        p.setBankBranch("Candolim, Goa");
        p.setUpiId("serenepalms@hdfcbank");
        return p;
    }

    // Getters and Setters
    public String getResortName() { return resortName; }
    public void setResortName(String resortName) { this.resortName = resortName; }

    public String getTagline() { return tagline; }
    public void setTagline(String tagline) { this.tagline = tagline; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getStateCode() { return stateCode; }
    public void setStateCode(String stateCode) { this.stateCode = stateCode; }

    public String getGstin() { return gstin; }
    public void setGstin(String gstin) { this.gstin = gstin; }

    public String getPan() { return pan; }
    public void setPan(String pan) { this.pan = pan; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getBankAccountNumber() { return bankAccountNumber; }
    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }

    public String getBankIfsc() { return bankIfsc; }
    public void setBankIfsc(String bankIfsc) { this.bankIfsc = bankIfsc; }

    public String getBankBranch() { return bankBranch; }
    public void setBankBranch(String bankBranch) { this.bankBranch = bankBranch; }

    public String getUpiId() { return upiId; }
    public void setUpiId(String upiId) { this.upiId = upiId; }
}
