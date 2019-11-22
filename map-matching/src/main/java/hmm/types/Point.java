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

import java.io.Serializable;

/**
 * Represents a spatial point.
 */
public class Point {

    final public double longitude;
    final public double latitude;

    public Point(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "Point [longitude=" + longitude + ", latitude=" + latitude + "]";
    }
}
