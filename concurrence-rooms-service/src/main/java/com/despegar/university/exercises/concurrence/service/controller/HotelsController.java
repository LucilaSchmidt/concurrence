package com.despegar.university.exercises.concurrence.service.controller;

import java.util.List;

import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.despegar.university.exercises.concurrence.domain.exceptions.HotelsServiceException;
import com.despegar.university.exercises.concurrence.domain.model.HotelInfo;
import com.despegar.university.exercises.concurrence.domain.model.HotelRecordDTO;
import com.despegar.university.exercises.concurrence.domain.services.HotelsService;
import com.despegar.university.exercises.concurrence.domain.utils.DateUtils;
import com.despegar.university.exercises.concurrence.service.editor.DateEditor;

@RestController
@RequestMapping(value = "/hotels")
public class HotelsController {

    private static final String HOTEL_NOT_FOUND = "Hotel with id %s not found for day %s";

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDateTime.class, new DateEditor());
    }

    @Autowired
    private HotelsService hotelsService;
    @Autowired
    private DateUtils dateUtils;

    @ResponseBody
    @RequestMapping(value = "/{hotelId}/{date}", method = RequestMethod.GET)
    public Object hotelStatus(@PathVariable String hotelId, @PathVariable LocalDateTime date) throws Exception {
        try {
            HotelInfo response = this.hotelsService.getHotelInfo(hotelId, date);

            return new ResponseEntity<Object>(response, HttpStatus.OK);
        } catch (HotelsServiceException e) {
            return new ResponseEntity<Object>(String.format(HOTEL_NOT_FOUND, hotelId, this.dateUtils.toString(date)),
                HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/record", method = RequestMethod.POST)
    public void saveHotels(@RequestBody List<HotelRecordDTO> hotelsInfo) {
        this.hotelsService.recordHotels(hotelsInfo);
    }
}
