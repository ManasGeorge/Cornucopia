package com.cornucopia.cornucopia_app.model;

import org.junit.Test;

import java.util.Date;

import static junit.framework.Assert.assertEquals;

/**
 * Tests that ExpirationStatus is set correctly
 */
public class ExpirationStatusTest {
    @Test
    public void fromIngredientExpired_1Minute() {
        // Expiration date was just a second ago
        Date date = new Date();
        date.setTime(date.getTime() - 1000 * 60);
        assertEquals(ExpirationStatus.EXPIRED, ExpirationStatus.fromIngredientExpirationDate(date));
    }

    @Test
    public void fromIngredientExpiringSoon_1Minute() {
        // Expiration date is a minute away
        Date date = new Date();
        date.setTime(date.getTime() + 1000 * 60);
        assertEquals(ExpirationStatus.EXPIRING_SOON, ExpirationStatus.fromIngredientExpirationDate(date));
    }

    @Test
    public void fromIngredientExpiringSoon_2Days() {
        // Expiration date is 2 days away
        Date date = new Date();
        date.setTime(date.getTime() + 1000 * 60 * 60 * 24 * 2);
        assertEquals(ExpirationStatus.EXPIRING_SOON, ExpirationStatus.fromIngredientExpirationDate(date));
    }

    @Test
    public void fromIngredientNotExpiring_4Days() {
        // Expiration date is 4 days away
        Date date = new Date();
        date.setTime(date.getTime() + 1000 * 60 * 60 * 24 * 4);
        assertEquals(ExpirationStatus.NOT_EXPIRED, ExpirationStatus.fromIngredientExpirationDate(date));
    }
}
