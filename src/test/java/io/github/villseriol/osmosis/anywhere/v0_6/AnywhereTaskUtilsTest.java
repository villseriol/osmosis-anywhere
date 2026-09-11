// This software is released into the Public Domain.  See copying.txt for details.
package io.github.villseriol.osmosis.anywhere.v0_6;

import org.junit.Assert;
import org.junit.Test;
import org.openstreetmap.osmosis.testutil.AbstractDataTest;


public class AnywhereTaskUtilsTest extends AbstractDataTest {
    private static final double LIMIT = 180;
    private static final double DELTA = 1e-9;

    @Test
    public void clampCoordinateLeavesInRangeValuesUnchanged() {
        Assert.assertEquals(0, AnywhereTaskUtils.clampCoordinate(0, LIMIT), 0);
        Assert.assertEquals(154.485769, AnywhereTaskUtils.clampCoordinate(154.485769, LIMIT), 0);
        Assert.assertEquals(-154.485769, AnywhereTaskUtils.clampCoordinate(-154.485769, LIMIT), 0);
    }


    @Test
    public void clampCoordinateKeepsEdges() {
        Assert.assertEquals(180, AnywhereTaskUtils.clampCoordinate(180, LIMIT), 0);
        Assert.assertEquals(-180, AnywhereTaskUtils.clampCoordinate(-180, LIMIT), 0);
    }


    @Test
    public void clampCoordinateWrapsPastEdges() {
        Assert.assertEquals(-170, AnywhereTaskUtils.clampCoordinate(190, LIMIT), DELTA);
        Assert.assertEquals(170, AnywhereTaskUtils.clampCoordinate(-190, LIMIT), DELTA);
    }


    @Test
    public void clampCoordinateWrapsMultiplePeriods() {
        Assert.assertEquals(0, AnywhereTaskUtils.clampCoordinate(360, LIMIT), DELTA);
        Assert.assertEquals(0, AnywhereTaskUtils.clampCoordinate(-360, LIMIT), DELTA);
        Assert.assertEquals(-80, AnywhereTaskUtils.clampCoordinate(1000, LIMIT), DELTA);
        Assert.assertEquals(80, AnywhereTaskUtils.clampCoordinate(-1000, LIMIT), DELTA);
    }


    @Test
    public void clampCoordinateLandsOnSameSignEdge() {
        Assert.assertEquals(180, AnywhereTaskUtils.clampCoordinate(540, LIMIT), 0);
        Assert.assertEquals(-180, AnywhereTaskUtils.clampCoordinate(-540, LIMIT), 0);
    }


    @Test
    public void clampCoordinateUsesLimit() {
        Assert.assertEquals(-80, AnywhereTaskUtils.clampCoordinate(100, 90), DELTA);
        Assert.assertEquals(80, AnywhereTaskUtils.clampCoordinate(-100, 90), DELTA);
        Assert.assertEquals(90, AnywhereTaskUtils.clampCoordinate(270, 90), 0);
    }


    @Test
    public void tryParseCoordinateParsesValidValues() {
        Assert.assertEquals(0, AnywhereTaskUtils.tryParseCoordinate("0", LIMIT), 0);
        Assert.assertEquals(12.5, AnywhereTaskUtils.tryParseCoordinate("12.5", LIMIT), 0);
        Assert.assertEquals(-3.25, AnywhereTaskUtils.tryParseCoordinate("-3.25", LIMIT), 0);
        Assert.assertEquals(134.485769, AnywhereTaskUtils.tryParseCoordinate("134.485769", LIMIT), 0);
    }


    @Test
    public void tryParseCoordinateTrimsWhitespace() {
        Assert.assertEquals(5, AnywhereTaskUtils.tryParseCoordinate(" 5 ", LIMIT), 0);
        Assert.assertEquals(-7, AnywhereTaskUtils.tryParseCoordinate("\t-7\n", LIMIT), 0);
    }


    @Test
    public void tryParseCoordinateAcceptsLimits() {
        Assert.assertEquals(180, AnywhereTaskUtils.tryParseCoordinate("180", LIMIT), 0);
        Assert.assertEquals(-180, AnywhereTaskUtils.tryParseCoordinate("-180", LIMIT), 0);
    }


    @Test
    public void tryParseCoordinateRejectsNonNumbers() {
        assertRejected("abc", NumberFormatException.class);
        assertRejected("", NumberFormatException.class);
        assertRejected("   ", NumberFormatException.class);
        assertRejected("1,5", NumberFormatException.class);
    }


    @Test
    public void tryParseCoordinateRejectsNonFiniteValues() {
        assertRejected("NaN", IllegalArgumentException.class);
        assertRejected("Infinity", IllegalArgumentException.class);
        assertRejected("-Infinity", IllegalArgumentException.class);
    }


    @Test
    public void tryParseCoordinateRejectsOutOfRangeValues() {
        assertRejected("180.0000001", IllegalArgumentException.class);
        assertRejected("-180.0000001", IllegalArgumentException.class);
        assertRejected("1000", IllegalArgumentException.class);
    }


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
