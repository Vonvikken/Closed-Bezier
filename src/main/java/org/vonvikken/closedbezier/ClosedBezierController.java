/*
 * Class:     org.vonvikken.closedbezier.ClosedBezierController
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
import java.util.stream.Stream;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class ClosedBezierController {

  private static final double POINT_RADIUS = 4.0;
  private static final String POLYGON_LINE_CSS = "polygon";
  private static final String POINT_CSS = "point";
  private static final String RADIUS_CSS = "radius";
  private static final String CONTROL_POINT_CSS = "control-point";
  private static final String CONTROL_LINE_CSS = "control-line";
  private static final String CURVE_CSS = "curve";

  private final BooleanProperty pointsVisible = new SimpleBooleanProperty();
  private final BooleanProperty radiiVisible = new SimpleBooleanProperty();
  private final BooleanProperty polygonVisible = new SimpleBooleanProperty();
  private final BooleanProperty controlsVisible = new SimpleBooleanProperty();
  private final BooleanProperty curveVisible = new SimpleBooleanProperty();

  private ObjectExpression<Point2D> center;
  private ClosedBezierCurve bezier;

  @FXML private Pane root;

  @FXML private Label labelX;

  @FXML private Label labelY;

  @FXML
  private void initialize() {

    this.root.setOnMouseMoved(
        event -> {
          this.labelX.setText(String.format("%.2f", event.getX()));
          this.labelY.setText(String.format("%.2f", event.getY()));
        });

    this.center =
        Bindings.createObjectBinding(
            () -> new Point2D(this.root.getWidth() / 2.0, this.root.getHeight() / 2.0),
            this.root.widthProperty(),
            this.root.heightProperty());

    this.bezier = new ClosedBezierCurve(this.center);
    final PolarPoint point1 = this.bezier.getPoint1();
    final PolarPoint point2 = this.bezier.getPoint2();
    final PolarPoint point3 = this.bezier.getPoint3();
    final PolarPoint point4 = this.bezier.getPoint4();

    // region Points (graphical)
    final var circlePoint1 =
        ClosedBezierController.drawPoint(
            point1, ClosedBezierController.POINT_CSS, this.pointsVisible);
    final var circlePoint2 =
        ClosedBezierController.drawPoint(
            point2, ClosedBezierController.POINT_CSS, this.pointsVisible);
    final var circlePoint3 =
        ClosedBezierController.drawPoint(
            point3, ClosedBezierController.POINT_CSS, this.pointsVisible);
    final var circlePoint4 =
        ClosedBezierController.drawPoint(
            point4, ClosedBezierController.POINT_CSS, this.pointsVisible);
    this.root.getChildren().addAll(circlePoint1, circlePoint2, circlePoint3, circlePoint4);
    // endregion

    // region Radii
    final var radius1 = this.drawRadius(point1);
    final var radius2 = this.drawRadius(point2);
    final var radius3 = this.drawRadius(point3);
    final var radius4 = this.drawRadius(point4);
    this.root.getChildren().addAll(radius1, radius2, radius3, radius4);
    // endregion

    // region Lines
    final var line12 =
        ClosedBezierController.drawLine(
            point1, point2, ClosedBezierController.POLYGON_LINE_CSS, this.polygonVisible);
    final var line23 =
        ClosedBezierController.drawLine(
            point2, point3, ClosedBezierController.POLYGON_LINE_CSS, this.polygonVisible);
    final var line34 =
        ClosedBezierController.drawLine(
            point3, point4, ClosedBezierController.POLYGON_LINE_CSS, this.polygonVisible);
    final var line41 =
        ClosedBezierController.drawLine(
            point4, point1, ClosedBezierController.POLYGON_LINE_CSS, this.polygonVisible);
    this.root.getChildren().addAll(line12, line23, line34, line41);
    // endregion

    // region Control points
    final ControlPoint[] control1 = this.bezier.getControlPointsForNode(0);
    final ControlPoint[] control2 = this.bezier.getControlPointsForNode(1);
    final ControlPoint[] control3 = this.bezier.getControlPointsForNode(2);
    final ControlPoint[] control4 = this.bezier.getControlPointsForNode(3);

    Stream.of(control1, control2, control3, control4)
        .map(Arrays::asList)
        .flatMap(Collection::stream)
        .map(
            point ->
                ClosedBezierController.drawPoint(
                    point, ClosedBezierController.CONTROL_POINT_CSS, this.controlsVisible))
        .forEach(this.root.getChildren()::add);

    Stream.of(control1, control2, control3, control4)
        .map(
            points ->
                ClosedBezierController.drawLine(
                    points[0],
                    points[1],
                    ClosedBezierController.CONTROL_LINE_CSS,
                    this.controlsVisible))
        .forEach(this.root.getChildren()::add);
    // endregion

    // region Curves
    this.bezier.getStyleClass().add(ClosedBezierController.CURVE_CSS);
    this.bezier.visibleProperty().bind(this.curveVisible);
    this.root.getChildren().add(this.bezier);
    // endregion
  }

  private static Circle drawPoint(
      final PolarPoint point, final String cssClass, final BooleanExpression visibleProperty) {

    final var circle = new Circle(ClosedBezierController.POINT_RADIUS);
    circle.getStyleClass().add(cssClass);
    circle.centerXProperty().bind(point.xProperty());
    circle.centerYProperty().bind(point.yProperty());
    circle.visibleProperty().bind(visibleProperty);
    return circle;
  }

  private static Line drawLine(
      final PolarPoint start,
      final PolarPoint end,
      final String cssClass,
      final BooleanExpression visibleProperty) {

    final var line = new Line();
    line.getStyleClass().add(cssClass);
    line.startXProperty().bind(start.xProperty());
    line.startYProperty().bind(start.yProperty());
    line.endXProperty().bind(end.xProperty());
    line.endYProperty().bind(end.yProperty());
    line.visibleProperty().bind(visibleProperty);
    return line;
  }

  private Line drawRadius(final PolarPoint end) {

    final var line = new Line();
    line.getStyleClass().add(ClosedBezierController.RADIUS_CSS);
    line.startXProperty()
        .bind(Bindings.createDoubleBinding(() -> this.center.get().getX(), this.center));
    line.startYProperty()
        .bind(Bindings.createDoubleBinding(() -> this.center.get().getY(), this.center));
    line.endXProperty().bind(end.xProperty());
    line.endYProperty().bind(end.yProperty());
    line.visibleProperty().bind(this.radiiVisible);
    return line;
  }

  DoubleProperty magnitude1Property() {
    return this.bezier.magnitude1Property();
  }

  DoubleProperty phase1Property() {
    return this.bezier.phase1Property();
  }

  DoubleProperty magnitude2Property() {
    return this.bezier.magnitude2Property();
  }

  DoubleProperty phase2Property() {
    return this.bezier.phase2Property();
  }

  DoubleProperty magnitude3Property() {
    return this.bezier.magnitude3Property();
  }

  DoubleProperty phase3Property() {
    return this.bezier.phase3Property();
  }

  DoubleProperty magnitude4Property() {
    return this.bezier.magnitude4Property();
  }

  DoubleProperty phase4Property() {
    return this.bezier.phase4Property();
  }

  DoubleProperty controlPointDistanceProperty() {
    return this.bezier.controlPointDistanceProperty();
  }

  BooleanProperty controlsVisibleProperty() {
    return this.controlsVisible;
  }

  BooleanProperty curveVisibleProperty() {
    return this.curveVisible;
  }

  BooleanProperty pointsVisibleProperty() {
    return this.pointsVisible;
  }

  BooleanProperty polygonVisibleProperty() {
    return this.polygonVisible;
  }

  BooleanProperty radiiVisibleProperty() {
    return this.radiiVisible;
  }
}
