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
import com.despegar.university.exercises.concurrence.domain.exceptions.HotelsDAOException;
import com.despegar.university.exercises.concurrence.domain.model.HotelInfo;

@Component
public class HotelValuesDAO {

    private static final String GET_ERROR = "Error in get value for key %s";
    private static final String SAVE_ERROR = "Error saving value. key: %s value: %s";

    private static final Logger LOGGER = LoggerFactory.getLogger(HotelValuesDAO.class);

    @Autowired
    private CassandraConnector connector;
    @Autowired
    private ConfigurationManager configurationManager;
    @Autowired
    private JsonSerializer serializer;

    public HotelInfo get(String key) {
        try {
            String serializedValue = StringUtils.fromBytes(
                this.connector.readValue(ColumnFamilies.HOTELS_COLUMN_FAMILY, "value", key), StandardCharsets.UTF_8);
            return this.serializer.deserialize(serializedValue, HotelInfo.class);
        } catch (Exception e) {
            LOGGER.error(String.format(GET_ERROR, key));
            return null;
        }
    }

    public void save(String key, HotelInfo value) {
        try {
            String serializedValue = this.serializer.serialize(value);

            this.connector.writeValue(ColumnFamilies.HOTELS_COLUMN_FAMILY, "value", key,
                this.configurationManager.getIntValue(ConfigurationKeys.CASSANDRA_TTL), serializedValue.getBytes());
        } catch (Exception e) {
            String error = String.format(SAVE_ERROR, key, value);
            LOGGER.error(error);
            throw new HotelsDAOException(error, e);
        }
    }

}
