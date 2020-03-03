/*
 * Class:     org.vonvikken.closedbezier.ControlPoint
 * Author:    Vincenzo Stornanti
 *
 * Copyright 2020 Vincenzo Stornanti
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vonvikken.closedbezier;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

class ControlPoint extends PolarPoint {

    private final DoubleProperty distance = new SimpleDoubleProperty();

    ControlPoint(final PolarPoint node, final boolean opposite) {

        super(node.getQuadrant());
        this.centerXProperty().bind(Bindings.createDoubleBinding(node::getX, node.xProperty()));
        this.centerYProperty().bind(Bindings.createDoubleBinding(node::getY, node.yProperty()));

        this.absoluteMagnitude.unbind();
        this.absoluteMagnitude.bind(this.distance);

        this.normalizedPhaseProperty().bind(node.normalizedPhaseProperty().add(opposite ? -1.0 : 1.0));
    }

    DoubleProperty distanceProperty() {
        return this.distance;
    }
}
