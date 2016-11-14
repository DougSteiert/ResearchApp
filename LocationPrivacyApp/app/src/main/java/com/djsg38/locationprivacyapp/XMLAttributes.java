package com.djsg38.locationprivacyapp;

public class XMLAttributes {

    protected String toponymName = "toponymName";
    protected String name = "name";
    protected double lat = 0;
    protected double lng = 0;
    protected String geonameId = "geonameId";
    protected String countryCode = "countryCode";
    protected String countryName = "countryName";
    protected String fcl = "fcl";
    protected String fcode = "fcode";
    protected double distance = 0;

    public String getToponymName() {
        return toponymName;
    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getGeonameId() {
        return geonameId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getFcl() {
        return fcl;
    }

    public String getFcode() {
        return fcode;
    }

    public double getDistance() {
        return distance;
    }
}
