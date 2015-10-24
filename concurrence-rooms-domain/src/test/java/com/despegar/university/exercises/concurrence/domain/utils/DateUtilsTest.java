package com.despegar.university.exercises.concurrence.domain.utils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import java.util.List;

import org.joda.time.LocalDateTime;
import org.testng.annotations.Test;

@Test
public class DateUtilsTest {

    public void toStringTest() {
        String expected = "2015-01-10";

        LocalDateTime date = new LocalDateTime(2015, 01, 10, 12, 00);
        String actual = DateUtils.toString(date);

        assertEquals(actual, expected);
    }

    public void rangeTest() {
        LocalDateTime since = new LocalDateTime(2015, 05, 01, 00, 00);
        LocalDateTime until = new LocalDateTime(2015, 05, 10, 00, 00);

        List<LocalDateTime> range = DateUtils.getDatesRange(since, until);

        assertNotNull(range);
        assertFalse(range.isEmpty());
        assertEquals(range.size(), 10);
    }

    @Test(expectedExceptions = {RuntimeException.class})
    public void invalidRangeTest() {
        LocalDateTime since = new LocalDateTime(2015, 05, 10, 00, 00);
        LocalDateTime until = new LocalDateTime(2015, 05, 01, 00, 00);

        DateUtils.getDatesRange(since, until);

        fail();
    }

}
