package com.despegar.university.exercises.concurrence.domain.services;

import java.util.List;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.despegar.university.exercises.concurrence.domain.dao.BookingValuesDAO;
import com.despegar.university.exercises.concurrence.domain.exceptions.BookingServiceException;
import com.despegar.university.exercises.concurrence.domain.model.BookingRequestDTO;
import com.despegar.university.exercises.concurrence.domain.model.HotelInfo;
import com.despegar.university.exercises.concurrence.domain.utils.DateUtils;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

@Service
public class BookingService {

    private static final String PROCESSING_REQUEST = "Processing booking request %s";

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingService.class);

    private static Predicate<HotelInfo> DISPONIBILITY_PREDICATE = new Predicate<HotelInfo>() {

        @Override
        public boolean apply(HotelInfo input) {
            return input.hasDisponibility();
        }
    };

    @Autowired
    private BookingValuesDAO bookingDAO;
    @Autowired
    private HotelsService hotelsService;
    @Autowired
    private KeyMaker keyMaker;

    public String reserve(BookingRequestDTO request) {
        // LOGGER.info(String.format(PROCESSING_REQUEST, request));
        // List<LocalDateTime> days = DateUtils.getDatesRange(request.getSince(), request.getUntil());
        //
        // Semaphore semaphore = this.semaphoreManager.create(request.getRequestId());
        // if(semaphore.lock()){
        // try{
        // List<HotelInfo> hotels = this.hotelsService.getHotelsInfoForDays(request.getHotelId(), days);
        // boolean hasDisponibility = Iterables.all(hotels, DISPONIBILITY_PREDICATE);
        // if (hasDisponibility) {
        // String bookingId = this.keyMaker.makeBookingId(request.getRequestId());
        // this.doReserve(hotels, bookingId);
        //
        // this.bookingDAO.save(bookingId, request);
        // this.hotelsService.saveHotels(hotels);
        //
        // return bookingId;
        // } else {
        // throw new BookingServiceException("Cannot process booking");
        // }
        // }
        // finally{
        // semaphore.unlock();
        // }



        LOGGER.info(String.format(PROCESSING_REQUEST, request));
        List<LocalDateTime> days = DateUtils.getDatesRange(request.getSince(), request.getUntil());

        List<HotelInfo> hotels = this.hotelsService.getHotelsInfoForDays(request.getHotelId(), days);
        boolean hasDisponibility = Iterables.all(hotels, DISPONIBILITY_PREDICATE);

        if (hasDisponibility) {
            String bookingId = this.keyMaker.makeBookingId(request.getRequestId());
            this.doReserve(hotels, bookingId);

            this.bookingDAO.save(bookingId, request);
            this.hotelsService.saveHotels(hotels);

            return bookingId;
        } else {
            throw new BookingServiceException("Cannot process booking");
        }
    }

    private void doReserve(List<HotelInfo> hotels, String bookingId) {
        for (HotelInfo hotel : hotels) {
            hotel.getBookings().add(bookingId);
        }
    }

    public BookingRequestDTO searchBooking(String id) {
        return this.bookingDAO.get(id);
    }

}
