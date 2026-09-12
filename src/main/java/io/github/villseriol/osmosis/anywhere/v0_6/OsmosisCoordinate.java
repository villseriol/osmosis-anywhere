// This software is released into the Public Domain.  See copying.txt for details.
package io.github.villseriol.osmosis.anywhere.v0_6;

public class OsmosisCoordinate {
    private static final double MAX_LATITUDE = 90;
    private static final double MAX_LONGITUDE = 180;

    private final double latitude;
    private final double longitude;

    public OsmosisCoordinate(final String value) {
        String[] parts = value.split(",", -1);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Coordinate \"" + value + "\" must be in the form \"lat,lon\".");
        }

        latitude = Double.parseDouble(parts[0].trim());
        longitude = Double.parseDouble(parts[1].trim());
    }


    public void validate() {
        if (!isLatitudeValid() || !isLongitudeValid()) {
            throw new IllegalArgumentException(
                    "Coordinate (" + latitude + ", " + longitude + ") is outside the valid range.");
        }
    }


    public boolean isValid() {
        return isLatitudeValid() && isLongitudeValid();
    }


    public boolean isLatitudeValid() {
        return isValidCoordinate(latitude, MAX_LATITUDE);
    }


    public boolean isLongitudeValid() {
        return isValidCoordinate(longitude, MAX_LONGITUDE);
    }


    public double getLatitude() {
        return latitude;
    }


    public double getLongitude() {
        return longitude;
    }


    private static boolean isValidCoordinate(double value, double limit) {
        return Double.isFinite(value) && Math.abs(value) <= limit;
    }

}
