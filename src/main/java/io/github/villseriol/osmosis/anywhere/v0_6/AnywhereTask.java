// This software is released into the Public Domain.  See copying.txt for details.
package io.github.villseriol.osmosis.anywhere.v0_6;

import java.util.Map;

import org.openstreetmap.osmosis.core.OsmosisRuntimeException;
import org.openstreetmap.osmosis.core.container.v0_6.BoundContainer;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.core.task.v0_6.SinkSource;


public class AnywhereTask implements SinkSource {
    private Sink sink;
    private final AnywhereCoordinate source;
    private final AnywhereCoordinate destination;
    private AnywhereRotator rotator;

    /**
     * Creates a new instance.
     *
     * @param src The coordinate to rotate from, in the form "lat,lon".
     * @param dest The coordinate to rotate onto, in the form "lat,lon".
     */
    public AnywhereTask(final String src, final String dest) {
        try {
            source = new AnywhereCoordinate(src);
            source.validate();
        } catch (IllegalArgumentException e) {
            throw new OsmosisRuntimeException("Argument src \"" + src + "\" is invalid: " + e.getMessage(), e);
        }

        try {
            destination = new AnywhereCoordinate(dest);
            destination.validate();
        } catch (IllegalArgumentException e) {
            throw new OsmosisRuntimeException("Argument dest \"" + dest + "\" is invalid: " + e.getMessage(), e);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void process(EntityContainer entityContainer) {
        if (entityContainer instanceof BoundContainer) {
            sink.process(rotator.visit((BoundContainer) entityContainer));
            return;
        }

        if (entityContainer instanceof NodeContainer) {
            sink.process(rotator.visit((NodeContainer) entityContainer));
            return;
        }

        sink.process(entityContainer);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(Map<String, Object> metaData) {
        rotator = new AnywhereRotator(source.getLatitude(), source.getLongitude(), destination.getLatitude(),
                destination.getLongitude());

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
