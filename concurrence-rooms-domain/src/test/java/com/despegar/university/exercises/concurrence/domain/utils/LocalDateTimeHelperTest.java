package com.despegar.university.exercises.concurrence.domain.utils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.text.ParseException;
import java.util.Date;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.testng.annotations.Test;

import com.despegar.cfa.ids.common.domain.utils.DateUtils;

@Test
public class LocalDateTimeHelperTest {

    public void fromDate_ok() {
        Date date = new Date();

        LocalDateTime actual = DateUtils.toLocalDateTime(date);

        assertNotNull(actual);
        assertEquals(actual.toDate(), date);
    }

    public void fromDate_null() {
        Date date = null;

        LocalDateTime actual = DateUtils.toLocalDateTime(date);

        assertNull(actual);
    }

    public void toDate_ok() {
        LocalDateTime date = LocalDateTime.now();

        Date actual = DateUtils.toDate(date);

        assertNotNull(actual);
        assertEquals(actual, date.toDate());
    }

    public void toDate_null() {
        LocalDateTime date = null;

        Date actual = DateUtils.toDate(date);

        assertNull(actual);
    }

    public void toDate_string() throws ParseException {
        Date actual = DateUtils.toDate("1970-01-01");

        assertEquals(new LocalDateTime(1970, 1, 1, 0, 0).toDate(), actual);
    }

    public void toDate_stringFormat() throws ParseException {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");

        Date actual1 = DateUtils.toDate("01-01-1970", formatter);
        Date actual2 = DateUtils.toDate(null, formatter);

        assertEquals(new LocalDateTime(1970, 1, 1, 0, 0).toDate(), actual1);
        assertNull(actual2);
    }


}
