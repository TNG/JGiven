package com.tngtech.jgiven.examples.datatable.model;

public class Address {
    String street;
    String zipCode;
    String city;
    String state;
    String country;

    private Address() {
        super();
    }

    public static class AddressBuilder {
        Address instance;

        public AddressBuilder street( String street ) {
            this.instance.street = street;
            return this;
        }

        public AddressBuilder zipCode( String zipCode ) {
            this.instance.zipCode = zipCode;
            return this;
        }

        public AddressBuilder city( String city ) {
            this.instance.city = city;
            return this;
        }

        public AddressBuilder state( String state ) {
            this.instance.state = state;
            return this;
        }

        public AddressBuilder country( String country ) {
            this.instance.country = country;
            return this;
        }

        public Address build() {
            return instance;
        }
    }

    public static AddressBuilder builder() {
        AddressBuilder b = new AddressBuilder();
        b.instance = new Address();
        return b;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

}
