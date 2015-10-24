package com.despegar.university.exercises.concurrence.domain.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.Days;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class DateUtils {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    public static synchronized String toString(LocalDateTime date) {
        return date != null ? SDF.format(date.toDate()) : null;
    }

    public static synchronized List<LocalDateTime> getDatesRange(LocalDateTime since, LocalDateTime until) {
        List<LocalDateTime> range = new ArrayList<LocalDateTime>();

        if (until.isBefore(since)) {
            throw new RuntimeException("until cannot be less than since");
        }

        Integer days = Days.daysBetween(since, until).getDays();

        for (int i = 0; i <= days; i++) {
            range.add(since.plusDays(i));
        }

        return range;
    }

}
