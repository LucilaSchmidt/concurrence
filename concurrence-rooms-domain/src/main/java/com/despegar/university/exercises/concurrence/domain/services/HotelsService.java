package com.despegar.university.exercises.concurrence.domain.services;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.despegar.university.exercises.concurrence.domain.dao.HotelValuesDAO;
import com.despegar.university.exercises.concurrence.domain.exceptions.HotelsServiceException;
import com.despegar.university.exercises.concurrence.domain.model.HotelInfo;
import com.despegar.university.exercises.concurrence.domain.model.HotelRecordDTO;
import com.despegar.university.exercises.concurrence.domain.utils.DateUtils;

@Service
public class HotelsService {

    private static final String INVALID_HOTEL_ID = "Invalid hotelId %s for date %s";

    private static final String RECORDING_HOTEL = "Recording hotel with id %s for day %s with rooms %s";

    private static final String PROCESSING_RECORDS = "Processing records %s";

    private static final Logger LOGGER = LoggerFactory.getLogger(HotelsService.class);

    @Autowired
    private HotelValuesDAO hotelsDAO;
    @Autowired
    private KeyMaker keyMaker;

    public List<HotelInfo> getHotelsInfoForDays(String hotelId, List<LocalDateTime> days) {

        List<HotelInfo> hotels = new ArrayList<HotelInfo>();

        for (LocalDateTime day : days) {
            hotels.add(this.getHotel(hotelId, day));
        }

        return hotels;
    }

    public HotelInfo getHotelInfo(String hotelId, LocalDateTime date) {
        return this.getHotel(hotelId, date);
    }

    private HotelInfo getHotel(String hotelId, LocalDateTime date) {
        String key = this.keyMaker.makeHotelKey(date, hotelId);

        HotelInfo hotel = this.hotelsDAO.get(key);
        if (hotel != null) {
            return hotel;
        } else {
            throw new HotelsServiceException(String.format(INVALID_HOTEL_ID, hotelId, DateUtils.toString(date)));
        }
    }

    public void saveHotels(List<HotelInfo> hotels) {
        for (HotelInfo hotelInfo : hotels) {
            this.hotelsDAO.save(hotelInfo.getId(), hotelInfo);
        }
    }

    public void recordHotels(List<HotelRecordDTO> hotelsInfo) {
        LOGGER.info(String.format(PROCESSING_RECORDS, hotelsInfo));
        for (HotelRecordDTO hotelDTO : hotelsInfo) {
            this.recordHotel(hotelDTO);
        }
    }

    private void recordHotel(HotelRecordDTO hotelDTO) {
        List<LocalDateTime> dates = DateUtils.getDatesRange(hotelDTO.getSince(), hotelDTO.getUntil());

        for (LocalDateTime date : dates) {
            LOGGER
                .info(String.format(RECORDING_HOTEL, hotelDTO.getHotelId(), DateUtils.toString(date), hotelDTO.getRooms()));

            String key = this.keyMaker.makeHotelKey(date, hotelDTO.getHotelId());
            this.hotelsDAO.save(key, new HotelInfo(key, hotelDTO.getHotelId(), hotelDTO.getRooms()));
        }
    }

}
