/*
 * Class:     org.vonvikken.closedbezier.PolarPoint
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
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleDoubleProperty;

class PolarPoint {

  private final DoubleProperty normalizedMagnitude = new SimpleDoubleProperty();
  private final DoubleProperty normalizedPhase = new SimpleDoubleProperty();
  private final DoubleProperty centerX = new SimpleDoubleProperty();
  private final DoubleProperty centerY = new SimpleDoubleProperty();
  protected final ReadOnlyDoubleWrapper absoluteMagnitude = new ReadOnlyDoubleWrapper();
  private final ReadOnlyDoubleWrapper absolutePhase = new ReadOnlyDoubleWrapper();
  private final ReadOnlyDoubleWrapper coordinateX = new ReadOnlyDoubleWrapper();
  private final ReadOnlyDoubleWrapper coordinateY = new ReadOnlyDoubleWrapper();
  private final Quadrant quadrant;

  PolarPoint(final Quadrant quadrant) {

    this.quadrant = quadrant;
    final var angleOffset = Math.PI / 2.0 * quadrant.getOffset();

    this.absoluteMagnitude.bind(
        Bindings.createDoubleBinding(
            () -> {
              final var xOffset = this.centerX.get();
              final var yOffset = this.centerY.get();
              return this.normalizedMagnitude.get() * Math.min(xOffset, yOffset);
            },
            this.normalizedMagnitude,
            this.centerX,
            this.centerY));

    this.absolutePhase.bind(
        Bindings.createDoubleBinding(
            () -> Math.toRadians(this.normalizedPhase.get() * 90.0), this.normalizedPhase));

    this.coordinateX.bind(
        Bindings.createDoubleBinding(
            () ->
                this.absoluteMagnitude.get() * Math.cos(this.absolutePhase.get() + angleOffset)
                    + this.centerX.get(),
            this.absoluteMagnitude,
            this.absolutePhase,
            this.centerX,
            this.centerY));

    this.coordinateY.bind(
        Bindings.createDoubleBinding(
            () ->
                this.absoluteMagnitude.get() * Math.sin(this.absolutePhase.get() + angleOffset)
                    + this.centerY.get(),
            this.absoluteMagnitude,
            this.absolutePhase,
            this.centerX,
            this.centerY));
  }

  DoubleProperty normalizedMagnitudeProperty() {
    return this.normalizedMagnitude;
  }

  DoubleProperty normalizedPhaseProperty() {
    return this.normalizedPhase;
  }

  DoubleProperty centerXProperty() {
    return this.centerX;
  }

  DoubleProperty centerYProperty() {
    return this.centerY;
  }

  ReadOnlyDoubleProperty xProperty() {
    return this.coordinateX.getReadOnlyProperty();
  }

  double getX() {
    return this.coordinateX.get();
  }

  ReadOnlyDoubleProperty yProperty() {
    return this.coordinateY.getReadOnlyProperty();
  }

  double getY() {
    return this.coordinateY.get();
  }

  Quadrant getQuadrant() {
    return this.quadrant;
  }
}
