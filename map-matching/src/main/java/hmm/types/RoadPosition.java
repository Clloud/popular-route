/**
 * Copyright (C) 2015-2016, BMW Car IT GmbH and BMW AG
 * Author: Stefan Holder (stefan.holder@bmw.de)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hmm.types;


/**
 * Default type to represent the position of a vehicle on the road network.
 * It is also possible to use a custom road position class instead.
 */
public class RoadPosition {

    /**
     * ID of the edge, on which the vehicle is positioned.
     */
    public final long edgeId;

    /**
     * Line segment index on the edge.
     */
    public int lineIndex = 0;

    /**
     * Position on the edge from beginning as a number in the interval [0,1].
     */
    public final double fraction;

    public final Point position;

    public RoadPosition(long edgeId, double fraction, Point position) {
        if (fraction < 0.0 || fraction > 1.0) {
            throw new IllegalArgumentException();
        }

        this.edgeId = edgeId;
        this.fraction = fraction;
        this.position = position;
    }

    public RoadPosition(long edgeId, double fraction, double x, double y) {
        this(edgeId, fraction, new Point(x, y));
    }

    public RoadPosition(long edgeId, int lineIndex, double fraction, Point position) {
        this(edgeId, fraction, position);
        this.lineIndex = lineIndex;
    }

    public RoadPosition(long edgeId, int lineIndex, double fraction, double x, double y) {
        this(edgeId, fraction, x, y);
        this.lineIndex = lineIndex;
    }

    @Override
    public String toString() {
        return "RoadPosition [edgeId=" + edgeId + ", fraction=" + fraction
                + ", position=" + position + "]";
    }

}
