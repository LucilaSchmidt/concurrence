package com.despegar.university.exercises.concurrence.domain.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.LocalDateTime;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.internal.verification.VerificationModeFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.despegar.university.exercises.concurrence.domain.dao.HotelValuesDAO;
import com.despegar.university.exercises.concurrence.domain.model.HotelInfo;
import com.despegar.university.exercises.concurrence.domain.model.HotelRecordDTO;
import com.despegar.university.exercises.concurrence.domain.services.HotelsService;
import com.despegar.university.exercises.concurrence.domain.services.KeyMaker;
import com.google.common.collect.Lists;

@Test
public class HotelsServiceTest {

    private static final String HOTEL_ID = "hotelId";
    private static final String KEY = "2015-01-01 | hotelId";
    private static final String KEY_2 = "2015-01-02 | hotelId";

    @InjectMocks
    private HotelsService hotelsService;

    @Mock
    private HotelValuesDAO hotelsDAO;

    private LocalDateTime date = new LocalDateTime(2015, 01, 01, 00, 00, 00);

    @Captor
    private ArgumentCaptor<String> keyCaptor;

    @Captor
    private ArgumentCaptor<HotelInfo> hotelCaptor;

    @Spy
    private KeyMaker keyMaker;

    @BeforeClass
    public void init() {
        initMocks(this);
    }

    @BeforeMethod
    public void resetMocks() {
        reset(this.hotelsDAO);
    }

    public void getHotelInfoTest() {
        HotelInfo value = new HotelInfo(KEY, HOTEL_ID, 10, Lists.newArrayList());
        when(this.hotelsDAO.get(eq(KEY))).thenReturn(value);

        HotelInfo info = this.hotelsService.getHotelInfo(HOTEL_ID, this.date);

        assertNotNull(info);
        assertEquals(info, value);
    }

    public void saveHotelsTest() {
        ArrayList<HotelInfo> hotels = Lists.newArrayList(new HotelInfo(KEY, HOTEL_ID, 10),
            new HotelInfo(KEY_2, HOTEL_ID, 20));

        this.hotelsService.saveHotels(hotels);

        verify(this.hotelsDAO, VerificationModeFactory.times(hotels.size())).save(any(String.class), any(HotelInfo.class));
    }

    public void recordHotelsTest() {
        HotelRecordDTO hotelRecordDTO = new HotelRecordDTO();
        hotelRecordDTO.setSince(this.date);
        hotelRecordDTO.setUntil(this.date);
        hotelRecordDTO.setHotelId(HOTEL_ID);
        hotelRecordDTO.setRooms(10);

        ArrayList<HotelRecordDTO> hotels = Lists.newArrayList(hotelRecordDTO);

        this.hotelsService.recordHotels(hotels);

        verify(this.hotelsDAO).save(this.keyCaptor.capture(), this.hotelCaptor.capture());

        List<String> keys = this.keyCaptor.getAllValues();
        List<HotelInfo> values = this.hotelCaptor.getAllValues();

        assertFalse(CollectionUtils.isEmpty(keys));
        assertFalse(CollectionUtils.isEmpty(values));

        assertEquals(keys.size(), 1);
        assertEquals(values.size(), 1);

        assertEquals(keys.get(0), KEY);
        assertEquals(values.get(0).getId(), KEY);
        assertEquals(values.get(0).getHotelId(), HOTEL_ID);
        assertEquals(values.get(0).getRooms().intValue(), 10);
        assertNotNull(values.get(0).getBookings());
    }
}
