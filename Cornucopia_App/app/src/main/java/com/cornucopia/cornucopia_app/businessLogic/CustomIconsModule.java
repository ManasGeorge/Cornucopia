package com.cornucopia.cornucopia_app.businessLogic;

import com.joanzapata.iconify.Icon;
import com.joanzapata.iconify.IconFontDescriptor;


public class CustomIconsModule implements IconFontDescriptor {
    public enum CustomIcons implements Icon {
        grocery_check('\ue900'),
        pantry_add('\ue901'),
        pantry_check('\ue902');

        char character;
        CustomIcons(char character) {
            this.character = character;
        }

        @Override
        public String key() {
            return name().replace('_', '-');
        }

        @Override
        public char character() {
            return character;
        }
    }

    @Override
    public String ttfFileName() {
        return "fonts/custom-icons.ttf";
    }

    @Override
    public Icon[] characters() {
        return CustomIcons.values();
    }
}
