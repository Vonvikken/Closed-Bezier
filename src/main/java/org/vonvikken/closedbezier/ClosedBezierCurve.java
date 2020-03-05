/*
 * Class:     org.vonvikken.closedbezier.ClosedBezierCurve
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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.shape.SVGPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ClosedBezierCurve extends SVGPath {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClosedBezierCurve.class);

  private static final String FIRST_POINT = "M %.2f %.2f C %.2f %.2f %.2f %.2f %.2f %.2f";
  private static final String OTHER_CURVES = "S %.2f %.2f %.2f %.2f";
  private static final String CURVE_CSS = "curve";

  private final PolarPoint point1;
  private final PolarPoint point2;
  private final PolarPoint point3;
  private final PolarPoint point4;
  private final List<ControlPoint> controlPoints;

  private final ObjectExpression<Point2D> center;
  private final DoubleProperty magnitude1 = new SimpleDoubleProperty();
  private final DoubleProperty phase1 = new SimpleDoubleProperty();
  private final DoubleProperty magnitude2 = new SimpleDoubleProperty();
  private final DoubleProperty phase2 = new SimpleDoubleProperty();
  private final DoubleProperty magnitude3 = new SimpleDoubleProperty();
  private final DoubleProperty phase3 = new SimpleDoubleProperty();
  private final DoubleProperty magnitude4 = new SimpleDoubleProperty();
  private final DoubleProperty phase4 = new SimpleDoubleProperty();
  private final DoubleProperty controlPointDistance = new SimpleDoubleProperty();

  ClosedBezierCurve(final ObjectExpression<Point2D> center) {

    this.center = center;

    this.point1 = this.getPoint(this.magnitude1, this.phase1, Quadrant.UPPER_RIGHT);
    this.point2 = this.getPoint(this.magnitude2, this.phase2, Quadrant.UPPER_LEFT);
    this.point3 = this.getPoint(this.magnitude3, this.phase3, Quadrant.LOWER_LEFT);
    this.point4 = this.getPoint(this.magnitude4, this.phase4, Quadrant.LOWER_RIGHT);

    final ControlPoint[] control1 = this.addControlPoints(this.point1);
    final ControlPoint[] control2 = this.addControlPoints(this.point2);
    final ControlPoint[] control3 = this.addControlPoints(this.point3);
    final ControlPoint[] control4 = this.addControlPoints(this.point4);

    this.controlPoints =
        Stream.of(control1, control2, control3, control4)
            .map(Arrays::asList)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

    final Consumer<PolarPoint> addListenerConsumer =
        pt -> {
          pt.normalizedMagnitudeProperty().addListener(obs -> this.updatePath());
          pt.normalizedPhaseProperty().addListener(obs -> this.updatePath());
          pt.centerXProperty().addListener(obs -> this.updatePath());
          pt.centerYProperty().addListener(obs -> this.updatePath());
        };

    Arrays.asList(this.point1, this.point2, this.point3, this.point4).forEach(addListenerConsumer);
    this.controlPoints.forEach(addListenerConsumer);

    this.getStyleClass().add(ClosedBezierCurve.CURVE_CSS);

    this.updatePath();
  }

  private PolarPoint getPoint(
      final DoubleExpression magnitude, final DoubleExpression phase, final Quadrant quadrant) {

    final var point = new PolarPoint(quadrant);
    point.normalizedMagnitudeProperty().bind(magnitude);
    point.normalizedPhaseProperty().bind(phase);
    point
        .centerXProperty()
        .bind(Bindings.createDoubleBinding(() -> this.center.get().getX(), this.center));
    point
        .centerYProperty()
        .bind(Bindings.createDoubleBinding(() -> this.center.get().getY(), this.center));

    return point;
  }

  private ControlPoint[] addControlPoints(final PolarPoint node) {

    final var controlA = new ControlPoint(node, false);
    final var controlB = new ControlPoint(node, true);

    controlA.distanceProperty().bind(this.controlPointDistance);
    controlB.distanceProperty().bind(this.controlPointDistance);

    return new ControlPoint[] {controlA, controlB};
  }

  private void updatePath() {

    final List<PolarPoint> points =
        Arrays.asList(this.point1, this.point2, this.point3, this.point4);
    final int max = Math.min(points.size(), this.controlPoints.size() / 2) - 1;
    final String path =
        IntStream.rangeClosed(0, max)
            .mapToObj(
                i -> {
                  final String str;
                  if (i == 0) {
                    final PolarPoint firstPoint = points.get(i);
                    final PolarPoint secondPoint = points.get(i + 1);
                    final ControlPoint firstCP = this.controlPoints.get(i * 2 + 1);
                    final ControlPoint secondCP = this.controlPoints.get((i + 1) * 2);
                    str =
                        String.format(
                            Locale.ENGLISH,
                            ClosedBezierCurve.FIRST_POINT,
                            firstPoint.getX(),
                            firstPoint.getY(),
                            firstCP.getX(),
                            firstCP.getY(),
                            secondCP.getX(),
                            secondCP.getY(),
                            secondPoint.getX(),
                            secondPoint.getY());
                  } else if (i == max) {
                    str =
                        String.format(
                            Locale.ENGLISH,
                            ClosedBezierCurve.OTHER_CURVES,
                            this.controlPoints.get(0).getX(),
                            this.controlPoints.get(0).getY(),
                            points.get(0).getX(),
                            points.get(0).getY());
                  } else {
                    str =
                        String.format(
                            Locale.ENGLISH,
                            ClosedBezierCurve.OTHER_CURVES,
                            this.controlPoints.get((i + 1) * 2).getX(),
                            this.controlPoints.get((i + 1) * 2).getY(),
                            points.get(i + 1).getX(),
                            points.get(i + 1).getY());
                  }

                  return str;
                })
            .collect(Collectors.joining(" ", "", " Z"));

    ClosedBezierCurve.LOGGER.debug(path);
    this.contentProperty().set(path);
  }

  PolarPoint getPoint1() {
    return this.point1;
  }

  PolarPoint getPoint2() {
    return this.point2;
  }

  PolarPoint getPoint3() {
    return this.point3;
  }

  PolarPoint getPoint4() {
    return this.point4;
  }

  ControlPoint[] getControlPointsForNode(final int node) {
    return new ControlPoint[] {
      this.controlPoints.get(2 * node), this.controlPoints.get(2 * node + 1)
    };
  }

  DoubleProperty magnitude1Property() {
    return this.magnitude1;
  }

  DoubleProperty phase1Property() {
    return this.phase1;
  }

  DoubleProperty magnitude2Property() {
    return this.magnitude2;
  }

  DoubleProperty phase2Property() {
    return this.phase2;
  }

  DoubleProperty magnitude3Property() {
    return this.magnitude3;
  }

  DoubleProperty phase3Property() {
    return this.phase3;
  }

  DoubleProperty magnitude4Property() {
    return this.magnitude4;
  }

  DoubleProperty phase4Property() {
    return this.phase4;
  }

  DoubleProperty controlPointDistanceProperty() {
    return this.controlPointDistance;
  }
}
