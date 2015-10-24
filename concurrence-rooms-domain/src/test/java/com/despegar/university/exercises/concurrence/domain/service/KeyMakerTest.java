package com.despegar.university.exercises.concurrence.domain.service;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.despegar.cfa.library.zookeeperrecipes.recipes.semaphore.Semaphore;
import com.despegar.cfa.library.zookeeperrecipes.recipes.sharedvalue.SharedValueManager;
import com.despegar.university.exercises.concurrence.domain.services.KeyMaker;

@Test
public class KeyMakerTest {

    @InjectMocks
    private KeyMaker keyMaker;

    @Mock
    private Semaphore semaphore;

    @Mock
    private SharedValueManager sharedValueManager;

    @Captor
    private ArgumentCaptor<Integer> valueCaptor;

    @BeforeClass
    public void init() {
        initMocks(this);
    }

    public void makeBookingIdTest() {
        when(this.sharedValueManager.get(eq("bookingId"))).thenReturn(5);
        when(this.semaphore.lock()).thenReturn(true);

        String value = this.keyMaker.makeBookingId("a");

        assertEquals(value, "a-5");

        verify(this.sharedValueManager).save(eq("bookingId"), this.valueCaptor.capture());

        Integer captured = this.valueCaptor.getValue();

        assertNotNull(value);
        assertEquals(captured.intValue(), 6);
    }
}
