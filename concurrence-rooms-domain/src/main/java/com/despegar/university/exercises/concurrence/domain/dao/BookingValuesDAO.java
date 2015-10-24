package com.despegar.university.exercises.concurrence.domain.dao;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.despegar.cfa.ids.common.domain.utils.StringUtils;
import com.despegar.cfa.library.cassandra.CassandraConnector;
import com.despegar.cfa.library.utils.serializer.JsonSerializer;
import com.despegar.cfa.library.zookeeperrecipes.recipes.configurations.ConfigurationManager;
import com.despegar.university.exercises.concurrence.domain.configurations.ConfigurationKeys;
import com.despegar.university.exercises.concurrence.domain.dao.columnfamilies.ColumnFamilies;
import com.despegar.university.exercises.concurrence.domain.exceptions.BookingDAOException;
import com.despegar.university.exercises.concurrence.domain.model.BookingRequestDTO;

@Component
public class BookingValuesDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingValuesDAO.class);

    @Autowired
    private CassandraConnector connector;
    @Autowired
    private JsonSerializer serializer;
    @Autowired
    private ConfigurationManager configurationManager;

    public BookingRequestDTO get(String key) {
        try {
            String serializedValue = StringUtils.fromBytes(
                this.connector.readValue(ColumnFamilies.BOOKINGS_COLUMN_FAMILY, "value", key), StandardCharsets.UTF_8);
            return this.serializer.deserialize(serializedValue, BookingRequestDTO.class);
        } catch (Exception e) {
            LOGGER.error(String.format("Cannot get BookingRequest with key %s", key), e);
            return null;
        }
    }

    public void save(String key, BookingRequestDTO value) {
        String serializedValue;
        try {
            serializedValue = this.serializer.serialize(value);
            this.connector.writeValue(ColumnFamilies.BOOKINGS_COLUMN_FAMILY, "value", key,
                this.configurationManager.getIntValue(ConfigurationKeys.CASSANDRA_TTL), serializedValue.getBytes());
        } catch (Exception e) {
            LOGGER.error(String.format("Error saving BookingRequest with id %s", key), e);
            throw new BookingDAOException();
        }
    }
}
