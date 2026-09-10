// This software is released into the Public Domain.  See copying.txt for details.
package io.github.villseriol.osmosis.anywhere.v0_6;

import org.junit.Assert;
import org.junit.Test;
import org.openstreetmap.osmosis.testutil.AbstractDataTest;


public class AnywhereTaskUtilsTest extends AbstractDataTest {
    private static final double LIMIT = 180;
    private static final double DELTA = 1e-9;

    /**
     * Values already in range are returned exactly, with no floating point
     * noise from the wrapping arithmetic.
     */
    @Test
    public void clampCoordinateLeavesInRangeValuesUnchanged() {
        Assert.assertEquals(0, AnywhereTaskUtils.clampCoordinate(0, LIMIT), 0);
        Assert.assertEquals(154.485769, AnywhereTaskUtils.clampCoordinate(154.485769, LIMIT), 0);
        Assert.assertEquals(-154.485769, AnywhereTaskUtils.clampCoordinate(-154.485769, LIMIT), 0);
    }


    /**
     * Both edges of the range are in range and are kept as they are.
     */
    @Test
    public void clampCoordinateKeepsEdges() {
        Assert.assertEquals(180, AnywhereTaskUtils.clampCoordinate(180, LIMIT), 0);
        Assert.assertEquals(-180, AnywhereTaskUtils.clampCoordinate(-180, LIMIT), 0);
    }


    /**
     * Values just past either edge wrap around to the other side.
     */
    @Test
    public void clampCoordinateWrapsPastEdges() {
        Assert.assertEquals(-170, AnywhereTaskUtils.clampCoordinate(190, LIMIT), DELTA);
        Assert.assertEquals(170, AnywhereTaskUtils.clampCoordinate(-190, LIMIT), DELTA);
    }


    /**
     * Values more than one period out of range wrap all the way back.
     */
    @Test
    public void clampCoordinateWrapsMultiplePeriods() {
        Assert.assertEquals(0, AnywhereTaskUtils.clampCoordinate(360, LIMIT), DELTA);
        Assert.assertEquals(0, AnywhereTaskUtils.clampCoordinate(-360, LIMIT), DELTA);
        Assert.assertEquals(-80, AnywhereTaskUtils.clampCoordinate(1000, LIMIT), DELTA);
        Assert.assertEquals(80, AnywhereTaskUtils.clampCoordinate(-1000, LIMIT), DELTA);
    }


    /**
     * A value that wraps exactly onto an edge lands on the edge it came from,
     * so positive values end at +limit rather than flipping to -limit.
     */
    @Test
    public void clampCoordinateLandsOnSameSignEdge() {
        Assert.assertEquals(180, AnywhereTaskUtils.clampCoordinate(540, LIMIT), 0);
        Assert.assertEquals(-180, AnywhereTaskUtils.clampCoordinate(-540, LIMIT), 0);
    }


    /**
     * The limit sets both the range and the period.
     */
    @Test
    public void clampCoordinateUsesLimit() {
        Assert.assertEquals(-80, AnywhereTaskUtils.clampCoordinate(100, 90), DELTA);
        Assert.assertEquals(80, AnywhereTaskUtils.clampCoordinate(-100, 90), DELTA);
        Assert.assertEquals(90, AnywhereTaskUtils.clampCoordinate(270, 90), 0);
    }


    /**
     * Plain, negative and decimal numbers within the limit are parsed as is.
     */
    @Test
    public void tryParseCoordinateParsesValidValues() {
        Assert.assertEquals(0, AnywhereTaskUtils.tryParseCoordinate("0", LIMIT), 0);
        Assert.assertEquals(12.5, AnywhereTaskUtils.tryParseCoordinate("12.5", LIMIT), 0);
        Assert.assertEquals(-3.25, AnywhereTaskUtils.tryParseCoordinate("-3.25", LIMIT), 0);
        Assert.assertEquals(134.485769, AnywhereTaskUtils.tryParseCoordinate("134.485769", LIMIT), 0);
    }


    /**
     * Surrounding whitespace is ignored, so "lat, lon" style offsets work.
     */
    @Test
    public void tryParseCoordinateTrimsWhitespace() {
        Assert.assertEquals(5, AnywhereTaskUtils.tryParseCoordinate(" 5 ", LIMIT), 0);
        Assert.assertEquals(-7, AnywhereTaskUtils.tryParseCoordinate("\t-7\n", LIMIT), 0);
    }


    /**
     * Values exactly at either limit are accepted.
     */
    @Test
    public void tryParseCoordinateAcceptsLimits() {
        Assert.assertEquals(180, AnywhereTaskUtils.tryParseCoordinate("180", LIMIT), 0);
        Assert.assertEquals(-180, AnywhereTaskUtils.tryParseCoordinate("-180", LIMIT), 0);
    }


    /**
     * Text that is not a number is rejected with a NumberFormatException.
     */
    @Test
    public void tryParseCoordinateRejectsNonNumbers() {
        assertRejected("abc", NumberFormatException.class);
        assertRejected("", NumberFormatException.class);
        assertRejected("   ", NumberFormatException.class);
        assertRejected("1,5", NumberFormatException.class);
    }


    /**
     * NaN and infinities parse as doubles but are rejected as coordinates.
     */
    @Test
    public void tryParseCoordinateRejectsNonFiniteValues() {
        assertRejected("NaN", IllegalArgumentException.class);
        assertRejected("Infinity", IllegalArgumentException.class);
        assertRejected("-Infinity", IllegalArgumentException.class);
    }


    /**
     * Values past either limit are rejected.
     */
    @Test
    public void tryParseCoordinateRejectsOutOfRangeValues() {
        assertRejected("180.0000001", IllegalArgumentException.class);
        assertRejected("-180.0000001", IllegalArgumentException.class);
        assertRejected("1000", IllegalArgumentException.class);
    }


    /**
     * The limit argument sets the accepted range.
     */
    @Test
    public void tryParseCoordinateUsesLimit() {
        Assert.assertEquals(90, AnywhereTaskUtils.tryParseCoordinate("90", 90), 0);
        try {
            AnywhereTaskUtils.tryParseCoordinate("90.5", 90);
            Assert.fail("Expected 90.5 to be rejected with a limit of 90.");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }


    /**
     * Asserts that parsing a value against LIMIT throws exactly the given
     * exception type. An exact match is used because NumberFormatException is a
     * subclass of IllegalArgumentException.
     */
    private static void assertRejected(String value, Class<? extends IllegalArgumentException> expected) {
        try {
            AnywhereTaskUtils.tryParseCoordinate(value, LIMIT);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Wrong exception for \"" + value + "\".", expected, e.getClass());
            return;
        }

        Assert.fail("Expected \"" + value + "\" to be rejected.");
    }
}
