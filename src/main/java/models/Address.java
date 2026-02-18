package models;

public class Address {

    private String city;
    private String state;
    private String zipCode;
    private String country;

    public Address(String city, String state, String zipCode, String country) {
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
    }

    public Address() {
    }

    public String getCity() { return city; }
    public String getState() { return state; }
    public String getZipCode() { return zipCode; }
    public String getCountry() { return country; }

    @Override
    public String toString() {
        return city + ", " + state + ", " + zipCode + ", " + country;
    }
}
