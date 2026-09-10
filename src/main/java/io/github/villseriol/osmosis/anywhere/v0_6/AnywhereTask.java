// This software is released into the Public Domain.  See copying.txt for details.
package io.github.villseriol.osmosis.anywhere.v0_6;

import java.util.Map;

import org.openstreetmap.osmosis.core.OsmosisRuntimeException;
import org.openstreetmap.osmosis.core.container.v0_6.BoundContainer;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Bound;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.core.task.v0_6.SinkSource;


public class AnywhereTask implements SinkSource {
    private static final double MAX_LONGITUDE = 180;

    // Largest offsets that can still map a valid coordinate onto another valid
    // coordinate: latitude spans [-90, 90] and longitude spans [-180, 180].
    private static final double MAX_LATITUDE_OFFSET = 180;
    private static final double MAX_LONGITUDE_OFFSET = 360;

    private Sink sink;
    private final double latitudeOffset;
    private final double longitudeOffset;

    /**
     * Creates a new instance.
     *
     * @param offset The offset to apply, in the form "lat,lon" (e.g.
     *        "-12.5,3.25").
     */
    public AnywhereTask(final String offset) {
        String[] parts = offset.split(",", -1);
        if (parts.length != 2) {
            throw new OsmosisRuntimeException("Offset \"" + offset + "\" must be in the form \"lat,lon\".");
        }

        try {
            latitudeOffset = AnywhereTaskUtils.tryParseCoordinate(parts[0], MAX_LATITUDE_OFFSET);
            longitudeOffset = AnywhereTaskUtils.tryParseCoordinate(parts[1], MAX_LONGITUDE_OFFSET);
        } catch (IllegalArgumentException e) {
            throw new OsmosisRuntimeException("Offset \"" + offset + "\" is invalid: " + e.getMessage(), e);
        }
    }


    /**
     * Shifts a bounding box by the offsets. Bound has no setters, so a new
     * instance is built.
     */
    private Bound shiftBound(Bound bound) {
        // Clamp rather than fail: the box is only an envelope, and every node
        // inside it has already been checked against the valid latitude range.
        double top = Math.min(90, Math.max(-90, bound.getTop() + latitudeOffset));
        double bottom = Math.min(90, Math.max(-90, bound.getBottom() + latitudeOffset));

        double left = bound.getLeft();
        double right = bound.getRight();
        // A box spanning every longitude would collapse to zero width if both
        // edges wrapped to the same value, so leave it as is.
        if (right - left < 360) {
            left = AnywhereTaskUtils.clampCoordinate(left + longitudeOffset, MAX_LONGITUDE);
            right = AnywhereTaskUtils.clampCoordinate(right + longitudeOffset, MAX_LONGITUDE);
        }

        return new Bound(right, left, top, bottom, bound.getOrigin());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void process(EntityContainer entityContainer) {
        if (entityContainer instanceof BoundContainer) {
            sink.process(new BoundContainer(shiftBound(((BoundContainer) entityContainer).getEntity())));
            return;
        }

        if (!(entityContainer instanceof NodeContainer)) {
            sink.process(entityContainer);
            return;
        }

        NodeContainer writeableContainer = (NodeContainer) entityContainer.getWriteableInstance();
        Node node = writeableContainer.getEntity();

        double latitude = node.getLatitude() + latitudeOffset;
        if (latitude < -90 || latitude > 90) {
            throw new OsmosisRuntimeException("Node " + node.getId() + " latitude " + node.getLatitude()
                    + " is out of range after applying offset " + latitudeOffset + ".");
        }

        node.setLatitude(latitude);
        node.setLongitude(AnywhereTaskUtils.clampCoordinate(node.getLongitude() + longitudeOffset, MAX_LONGITUDE));

        sink.process(writeableContainer);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(Map<String, Object> metaData) {
        sink.initialize(metaData);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void complete() {
        sink.complete();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        sink.close();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setSink(Sink sink) {
        this.sink = sink;
    }

}
