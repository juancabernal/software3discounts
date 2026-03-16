package com.co.eatupapi.dto.inventory.location;

import com.co.eatupapi.domain.inventory.location.ScheduleLocation;

public class LocationResponseDTO {
    private String id;
    private String name;
    private String city;
    private String address;
    private boolean active;
    private String email;
    private String phoneNumber;
    private ScheduleLocation scheduleLocation;

    public LocationResponseDTO(String id, String name, String city, String address, boolean active, String email, String phoneNumber, ScheduleLocation scheduleLocation) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.address = address;
        this.active = active;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.scheduleLocation = scheduleLocation;
    }

    public String getId() {return id;}
    public String getName() {return name;}
    public String getCity() {return city;}
    public String getAddress() {return address;}
    public boolean isActive() {return active;}
    public String getEmail() {return email;}
    public String getPhoneNumber() {return phoneNumber;}
    public ScheduleLocation getScheduleLocation() {return scheduleLocation;}
}
