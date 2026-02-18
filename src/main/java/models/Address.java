package models;

public class Address {

    private String city;
    private String state;
    private int zipCode;
    private String country;

    public Address(String city, String state, int zipCode, String country) {
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
    }

    public Address() {
    }

    public String getCity() { return city; }
    public String getState() { return state; }
    public int getZipCode() { return zipCode; }
    public String getCountry() { return country; }

    @Override
    public String toString() {
        return city + ", " + state + ", " + zipCode + ", " + country;
    }
}
