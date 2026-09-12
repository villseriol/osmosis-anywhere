// This software is released into the Public Domain.  See copying.txt for details.
package io.github.villseriol.osmosis.anywhere.v0_6;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.openstreetmap.osmosis.core.container.v0_6.BoundContainer;
import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Bound;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;


public class OsmosisRotator {
    private static final double MAX_LATITUDE = 90;
    private static final double MAX_LONGITUDE = 180;

    private final Rotation rotation;

    public OsmosisRotator(final double sourceLatitude, final double sourceLongitude, final double destinationLatitude,
            final double destinationLongitude) {
        rotation = new Rotation(new Vector3D(Math.toRadians(sourceLongitude), Math.toRadians(sourceLatitude)),
                new Vector3D(Math.toRadians(destinationLongitude), Math.toRadians(destinationLatitude)));
    }


    public NodeContainer visit(final NodeContainer container) {
        NodeContainer writeableContainer = (NodeContainer) container.getWriteableInstance();
        Node node = writeableContainer.getEntity();

        Vector3D moved = rotation
                .applyTo(new Vector3D(Math.toRadians(node.getLongitude()), Math.toRadians(node.getLatitude())));

        node.setLatitude(Math.toDegrees(moved.getDelta()));
        node.setLongitude(Math.toDegrees(moved.getAlpha()));

        return writeableContainer;
    }


    public BoundContainer visit(final BoundContainer container) {
        Bound bound = container.getEntity();

        double[] cornerLatitudes = new double[4];
        cornerLatitudes[0] = bound.getTop();
        cornerLatitudes[1] = bound.getTop();
        cornerLatitudes[2] = bound.getBottom();
        cornerLatitudes[3] = bound.getBottom();

        double[] cornerLongitudes = new double[4];
        cornerLongitudes[0] = bound.getLeft();
        cornerLongitudes[1] = bound.getRight();
        cornerLongitudes[2] = bound.getRight();
        cornerLongitudes[3] = bound.getLeft();

        double top = -MAX_LATITUDE;
        double bottom = MAX_LATITUDE;
        double left = MAX_LONGITUDE;
        double right = -MAX_LONGITUDE;

        for (int corner = 0; corner < cornerLatitudes.length; corner++) {
            Vector3D moved = rotation.applyTo(
                    new Vector3D(Math.toRadians(cornerLongitudes[corner]), Math.toRadians(cornerLatitudes[corner])));
            double latitude = Math.toDegrees(moved.getDelta());
            double longitude = Math.toDegrees(moved.getAlpha());

            top = Math.max(top, latitude);
            bottom = Math.min(bottom, latitude);
            left = Math.min(left, longitude);
            right = Math.max(right, longitude);
        }

        if (bound.getRight() - bound.getLeft() >= 2 * MAX_LONGITUDE) {
            left = bound.getLeft();
            right = bound.getRight();
        }

        return new BoundContainer(new Bound(right, left, top, bottom, bound.getOrigin()));
    }
}
