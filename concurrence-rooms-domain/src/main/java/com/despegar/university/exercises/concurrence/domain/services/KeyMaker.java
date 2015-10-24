package com.despegar.university.exercises.concurrence.domain.services;

import javax.annotation.PostConstruct;

import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.despegar.cfa.library.zookeeperrecipes.connector.ZKConnector;
import com.despegar.cfa.library.zookeeperrecipes.recipes.configurations.ConfigurationManager;
import com.despegar.cfa.library.zookeeperrecipes.recipes.semaphore.Semaphore;
import com.despegar.cfa.library.zookeeperrecipes.recipes.semaphore.SemaphoreManager;
import com.despegar.cfa.library.zookeeperrecipes.recipes.sharedvalue.SharedValueManager;
import com.despegar.university.exercises.concurrence.domain.configurations.ConfigurationKeys;
import com.despegar.university.exercises.concurrence.domain.exceptions.BookingServiceException;
import com.despegar.university.exercises.concurrence.domain.utils.DateUtils;

@Component
public class KeyMaker {

    private static final String REQUEST_ID_FORMAT = "%s-%s";
    private static final String HOTEL_KEY_FORMAT = "%s | %s";
    private static final String LAST_ID = "bookingId";

    @Autowired
    private SharedValueManager sharedValueManager;
    @Autowired
    private DateUtils dateUtils;
    @Autowired
    private ConfigurationManager configurationManager;

    private SemaphoreManager semaphoreManager;

    private Semaphore semaphore;

    @PostConstruct
    public void init() {
        Integer id = this.sharedValueManager.get(LAST_ID);
        this.initializeSemaphoreManager();
        this.semaphore = this.semaphoreManager.create("lastId");

        if (id == null) {
            this.sharedValueManager.save(LAST_ID, 1);
        }
    }

    private void initializeSemaphoreManager() {
        String semaphorePath = this.configurationManager.getStringValue(ConfigurationKeys.ZOOKEPER_SEMAPHORE_PATH);
        ZKConnector connector = new ZKConnector(
            this.configurationManager.getStringValue(ConfigurationKeys.ZOOKEPER_CONNECTOR_STRING));
        long timeout = this.configurationManager.getIntValue(ConfigurationKeys.ZOOKEPER_SEMAPHORE_TIMEOUT);

        this.semaphoreManager = new SemaphoreManager(semaphorePath, connector, timeout);
    }

    public String makeBookingId(String requestId) {

        if (this.semaphore.lock()) {
            try {
                Integer value = this.sharedValueManager.get(LAST_ID);
                this.sharedValueManager.save(LAST_ID, value + 1);
                return String.format(REQUEST_ID_FORMAT, requestId, value);
            } finally {
                this.semaphore.unlock();
            }
        } else {
            throw new BookingServiceException("An error ocurred while trying to book.");
        }

    }

    public String makeHotelKey(LocalDateTime date, String hotelId) {
        return String.format(HOTEL_KEY_FORMAT, DateUtils.toString(date), hotelId);
    }
}
