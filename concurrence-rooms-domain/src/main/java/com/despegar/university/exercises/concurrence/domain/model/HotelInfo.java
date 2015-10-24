package com.despegar.university.exercises.concurrence.domain.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class HotelInfo {

    private String id;
    private String hotelId;
    private Integer rooms;
    private List<String> bookings;

    public HotelInfo() {

    }

    public HotelInfo(String id, String hotelId, Integer rooms) {
        this.id = id;
        this.hotelId = hotelId;
        this.rooms = rooms;
        this.bookings = new ArrayList<String>();
    }

    public HotelInfo(String id, String hotelId, Integer rooms, List<String> bookings) {
        this.id = id;
        this.hotelId = hotelId;
        this.rooms = rooms;
        this.bookings = bookings;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHotelId() {
        return this.hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public Integer getRooms() {
        return this.rooms;
    }

    public void setRooms(Integer rooms) {
        this.rooms = rooms;
    }

    public List<String> getBookings() {
        return this.bookings;
    }

    public void setBookings(List<String> bookings) {
        this.bookings = bookings;
    }

    public boolean hasDisponibility() {
        return this.rooms > this.bookings.size();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
