/*
 * Class:     org.vonvikken.closedbezier.Main
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

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public final class Main extends Application {

    public static void main(final String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {

        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ClosedBezier.fxml"));
        final Parent root = loader.load();
        final ClosedBezierController controller = loader.getController();

        // region Test code
        controller.magnitude1Property().set(1.0);
        controller.phase1Property().set(0.15);
        controller.magnitude2Property().set(0.75);
        controller.phase2Property().set(0.3);
        controller.magnitude3Property().set(0.5);
        controller.phase3Property().set(0.5);
        controller.magnitude4Property().set(0.25);
        controller.phase4Property().set(0.6);

        controller.controlPointDistanceProperty().set(100.0);

        controller.polygonVisibleProperty().set(false);
        controller.radiiVisibleProperty().set(false);
        controller.controlsVisibleProperty().set(true);
        controller.curveVisibleProperty().set(true);
        controller.pointsVisibleProperty().set(true);
        // endregion

        // region Timeline
        final var timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.setAutoReverse(true);
        timeline.getKeyFrames()
                .add(new KeyFrame(Duration.seconds(2.0),
                                  new KeyValue(controller.magnitude1Property(), 0.1),
                                  new KeyValue(controller.phase1Property(), 0.85),
                                  new KeyValue(controller.magnitude2Property(), 0.1),
                                  new KeyValue(controller.phase2Property(), 0.85),
                                  new KeyValue(controller.magnitude3Property(), 0.9),
                                  new KeyValue(controller.phase3Property(), 0.2),
                                  new KeyValue(controller.magnitude4Property(), 0.75),
                                  new KeyValue(controller.phase4Property(), 0.1)));

        timeline.play();
        // endregion

        primaryStage.setTitle("Closed Bézier curve");
        primaryStage.setScene(new Scene(root, 1600, 1200));
        primaryStage.show();
    }
}
