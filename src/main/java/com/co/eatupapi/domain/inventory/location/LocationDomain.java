package com.co.eatupapi.domain.inventory.location;

public class LocationDomain {
    private String name;
    private String city;
    private String address;
    private boolean active;
    private String email;
    private String phoneNumber;
    private ScheduleLocation scheduleLocation;

    public LocationDomain(final String name, String city, final String address, final boolean active, final String email, final String phoneNumber, final ScheduleLocation scheduleLocation) {
        setName(name);
        setCity(city);
        setAddress(address);
        setActive(active);
        setEmail(email);
        setPhoneNumber(phoneNumber);
        setScheduleLocation(scheduleLocation);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public ScheduleLocation getScheduleLocation() {
        return scheduleLocation;
    }

    public void setScheduleLocation(ScheduleLocation scheduleLocation) {
        this.scheduleLocation = scheduleLocation;
    }

}
