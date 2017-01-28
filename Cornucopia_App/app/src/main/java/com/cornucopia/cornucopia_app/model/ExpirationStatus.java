package com.cornucopia.cornucopia_app.model;

import android.support.annotation.ColorRes;

import com.cornucopia.cornucopia_app.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Stores information and utility info for expiring ingredients
 */
public enum ExpirationStatus {
    EXPIRED,
    EXPIRING_SOON,
    NOT_EXPIRED,;

    @SuppressWarnings("FieldCanBeLocal")
    private static int EXPIRING_SOON_DAY_LENGTH = 3;

    public static ExpirationStatus fromIngredientExpirationDate(Date expirationDate) {
        Calendar ingredientDate = Calendar.getInstance();
        ingredientDate.setTime(expirationDate);
        Calendar now = Calendar.getInstance();

        if (now.after(ingredientDate)) {
            return EXPIRED;
        }
        now.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) + EXPIRING_SOON_DAY_LENGTH);
        if (now.after(ingredientDate)) {
            return EXPIRING_SOON;
        }

        return NOT_EXPIRED;
    }

    @ColorRes
    public int getExpirationColor() {
        switch (this) {
            case EXPIRED:
                return R.color.expirationRed;
            case EXPIRING_SOON:
                return R.color.expirationOrange;
            case NOT_EXPIRED:
                return R.color.expirationGrey;
        }
        return R.color.expirationGrey;
    }
}
