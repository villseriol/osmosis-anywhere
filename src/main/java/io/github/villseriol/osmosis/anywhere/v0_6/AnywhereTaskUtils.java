// This software is released into the Public Domain.  See copying.txt for details.
package io.github.villseriol.osmosis.anywhere.v0_6;

public final class AnywhereTaskUtils {

    private AnywhereTaskUtils() {
    }


    /**
     * Parses one coordinate of an offset string.
     *
     * @param value The coordinate text to parse.
     * @param limit The largest allowed absolute value.
     * @return The parsed coordinate.
     * @throws NumberFormatException if the value is not a number.
     * @throws IllegalArgumentException if the value is not finite or its
     *         absolute value exceeds the limit.
     */
    static double tryParseCoordinate(String value, double limit) {
        double result = Double.parseDouble(value.trim());

        if (!Double.isFinite(result)) {
            throw new IllegalArgumentException("Coordinate " + result + " is not finite.");
        }

        if (Math.abs(result) > limit) {
            throw new IllegalArgumentException(
                    "Coordinate " + result + " is outside the range [-" + limit + ", " + limit + "].");
        }

        return result;
    }


    /**
     * Wraps a coordinate into the range [-limit, limit], e.g. a limit of 180
     * wraps longitudes around the antimeridian.
     *
     * @param value The coordinate to wrap.
     * @param limit The positive bound of the range.
     * @return The wrapped coordinate.
     */
    static double clampCoordinate(double value, double limit) {
        // Return in-range values untouched; the modulo arithmetic below can
        // introduce floating point noise even when no wrap is needed.
        if (value >= -limit && value <= limit) {
            return value;
        }

        double period = 2 * limit;
        double wrapped = ((value + limit) % period + period) % period - limit;
        // Keep limit as limit rather than flipping it to -limit.
        if (wrapped == -limit && value > 0) {
            wrapped = limit;
        }

        return wrapped;
    }

}
