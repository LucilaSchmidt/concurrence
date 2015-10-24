package com.despegar.university.exercises.concurrence.domain.dao.columnfamilies;

import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.serializers.StringSerializer;

public class ColumnFamilies {

    public static ColumnFamily<String, String> HOTELS_COLUMN_FAMILY = new ColumnFamily<String, String>("HotelValues",
        StringSerializer.get(), StringSerializer.get());

    public static ColumnFamily<String, String> BOOKINGS_COLUMN_FAMILY = new ColumnFamily<String, String>("BookingValues",
        StringSerializer.get(), StringSerializer.get());

}
