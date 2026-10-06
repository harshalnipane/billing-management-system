package com.resort.billing.model;

public class Guest {
    private String id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String address;
    private String city;
    private String state;
    private String stateCode; // 2-digit Indian State Code
    private String gstin;     // Optional for B2B billing
    private String companyName;
    private String idProofType; // AADHAAR, PASSPORT, DRIVING_LICENSE, VOTER_ID
    private String idProofNumber;

    public Guest() {}

    public Guest(String id, String fullName, String phoneNumber, String email, 
                 String address, String city, String state, String stateCode, 
                 String gstin, String companyName, String idProofType, String idProofNumber) {
        this.id = id;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.city = city;
        this.state = state;
        this.stateCode = stateCode;
        this.gstin = gstin;
        this.companyName = companyName;
        this.idProofType = idProofType;
        this.idProofNumber = idProofNumber;
    }

    public boolean isB2b() {
        return gstin != null && !gstin.trim().isEmpty();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

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

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getIdProofType() { return idProofType; }
    public void setIdProofType(String idProofType) { this.idProofType = idProofType; }

    public String getIdProofNumber() { return idProofNumber; }
    public void setIdProofNumber(String idProofNumber) { this.idProofNumber = idProofNumber; }
}
