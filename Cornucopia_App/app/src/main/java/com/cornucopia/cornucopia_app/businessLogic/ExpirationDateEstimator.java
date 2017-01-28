package com.cornucopia.cornucopia_app.businessLogic;

import android.support.annotation.NonNull;

import java.util.Date;

/**
 * TODO: Make this estimate expiration dates based on the ingredient name instead of a generic week
 */
class ExpirationDateEstimator {
    static Date estimateExpirationDate(@NonNull String ingredientName) {
        Date date = new Date();
        date.setTime(date.getTime() + 1000 * 60 * 60 * 24 * 7); // A week is always the answer
        return date;
    }
}
