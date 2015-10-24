package com.despegar.university.exercises.concurrence.domain.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.List;

import org.joda.time.LocalDateTime;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.despegar.university.exercises.concurrence.domain.dao.BookingValuesDAO;
import com.despegar.university.exercises.concurrence.domain.model.BookingRequestDTO;
import com.despegar.university.exercises.concurrence.domain.model.HotelInfo;
import com.despegar.university.exercises.concurrence.domain.services.BookingService;
import com.despegar.university.exercises.concurrence.domain.services.HotelsService;
import com.despegar.university.exercises.concurrence.domain.services.KeyMaker;
import com.google.common.collect.Lists;

public class BookingServiceTest {

    @InjectMocks
    private BookingService bookingService;
    @Mock
    private BookingValuesDAO bookingDAO;
    @Mock
    private HotelsService hotelsService;

    @Mock
    private KeyMaker keyMaker;

    private static final String HOTEL_OK_ID = "hotelOk";
    private static final String HOTEL_ERROR_ID = "hotelError";
    private static final String REQUEST_ID = "requestId";
    private static final String ID = "requestId-1";

    private static final LocalDateTime SINCE = new LocalDateTime(2015, 01, 01, 00, 00);
    private static final LocalDateTime UNTIL = new LocalDateTime(2015, 01, 02, 00, 00);


    @BeforeClass
    public void init() {
        initMocks(this);
        HotelInfo hotelOk20150101 = this.createHotel(HOTEL_OK_ID, 10, Lists.newArrayList());
        HotelInfo hotelOk20150102 = this.createHotel(HOTEL_OK_ID, 10, Lists.newArrayList());

        HotelInfo hotelError20150101 = this.createHotel(HOTEL_ERROR_ID, 1, Lists.newArrayList("request1"));
        HotelInfo hotelError20150102 = this.createHotel(HOTEL_ERROR_ID, 1, Lists.newArrayList("request1"));

        when(this.hotelsService.getHotelsInfoForDays(eq(HOTEL_OK_ID), anyListOf(LocalDateTime.class))).thenReturn(
            Lists.newArrayList(hotelOk20150101, hotelOk20150102));
        when(this.hotelsService.getHotelsInfoForDays(eq(HOTEL_ERROR_ID), anyListOf(LocalDateTime.class))).thenReturn(
            Lists.newArrayList(hotelError20150101, hotelError20150102));

        when(this.keyMaker.makeBookingId(any(String.class))).thenReturn(ID);
    }

    @Test
    public void bookingOkTest() {

        BookingRequestDTO request = this.createBooking(HOTEL_OK_ID, REQUEST_ID, SINCE, UNTIL);

        String result = this.bookingService.reserve(request);

        verify(this.bookingDAO).save(eq(ID), eq(request));
        assertEquals(result, ID);
    }

    @Test(expectedExceptions = {RuntimeException.class})
    public void bookingErrorTest() {
        this.bookingService.reserve(this.createBooking(HOTEL_ERROR_ID, REQUEST_ID, SINCE, UNTIL));
        fail();
    }

    private BookingRequestDTO createBooking(String hotelId, String requestId, LocalDateTime since, LocalDateTime until) {
        BookingRequestDTO booking = new BookingRequestDTO();

        booking.setHotelId(hotelId);
        booking.setRequestId(requestId);
        booking.setSince(since);
        booking.setUntil(until);

        return booking;
    }

    private HotelInfo createHotel(String hotelId, Integer rooms, List<String> bookingIds) {
        HotelInfo hotel = new HotelInfo();

        hotel.setBookings(bookingIds);
        hotel.setId(hotelId);
        hotel.setRooms(rooms);

        return hotel;
    }

}
