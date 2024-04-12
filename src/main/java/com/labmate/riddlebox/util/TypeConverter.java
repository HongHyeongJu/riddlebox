package com.labmate.riddlebox.util;

import com.labmate.riddlebox.enumpackage.GameLevel;
import com.labmate.riddlebox.enumpackage.ImageType;

public class TypeConverter {

    public static ImageType convertImageTypeFromString(String imageTypeString) {
        if (imageTypeString == null) {
            throw new IllegalArgumentException("Image type string cannot be null");
        }

        switch (imageTypeString.toUpperCase()) {
            case "THUMBNAIL":
                return ImageType.THUMBNAIL;
            case "ILLUSTRATION":
                return ImageType.ILLUSTRATION;
            case "TEMPORARY":
                return ImageType.TEMPORARY;
            case "LOGO":
                return ImageType.LOGO;
            case "SCREENSHOT":
                return ImageType.SCREENSHOT;
            default:
                throw new IllegalArgumentException("Invalid image type string: " + imageTypeString);
        }
    }


    public static GameLevel convertGameLevelFromString(String gameLevelString) {
        if (gameLevelString == null) {
            throw new IllegalArgumentException("Game level string cannot be null");
        }

        switch (gameLevelString.toUpperCase()) {
            case "EASY":
                return GameLevel.EASY;
            case "NORMAL":
                return GameLevel.NORMAL;
            case "HARD":
                return GameLevel.HARD;
            default:
                throw new IllegalArgumentException("Invalid game level string: " + gameLevelString);
        }
    }


}
