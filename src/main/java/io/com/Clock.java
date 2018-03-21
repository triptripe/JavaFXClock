package io.com;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.util.Calendar;


public class Clock extends Application {

    //Появляющаяся фотография
    private final String clockImage = "photo.jpg";
    //Дефолтный цвет часов
    private final Color clockColor = Color.GREEN;
    //центр
    private final static double size = 200.0;

    private static int lengthPhoto = 360;
    private static int lengthDefault = 360;
    private static int angleStart;
    private static int delta = -6;
    private static Arc arc = new Arc();
    private static Arc arcDefault = new Arc();
    private static SimpleIntegerProperty hour = new SimpleIntegerProperty(0);
    private static SimpleIntegerProperty minute = new SimpleIntegerProperty(0);
    private static SimpleIntegerProperty second = new SimpleIntegerProperty(0);

    @Override
    public void start(final Stage stage) throws Exception {
        startTick();
        Parent mainGroup = new Group(backGround(), backGroundDefault(), minute(), hour(),
                second(), groupSegments(), center(), groupLable());
        Scene scene = new Scene(mainGroup, size * 3, size * 3, Color.TRANSPARENT);
        final double[] x = new double[1];
        final double[] y = new double[1];
        mainGroup.setOnMousePressed(mouseEvent -> {
            x[0] = stage.getX() - mouseEvent.getScreenX();
            y[0] = stage.getY() - mouseEvent.getScreenY();
            scene.setCursor(Cursor.MOVE);
        });

        mainGroup.setOnMouseDragged(mouseEvent -> {
            stage.setX(mouseEvent.getScreenX() + x[0]);
            stage.setY(mouseEvent.getScreenY() + y[0]);

        });

        mainGroup.setOnMouseReleased(event -> scene.setCursor(Cursor.DEFAULT));
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
    }


    //Часовая стрелка
    private Node hour() {
        Rotate rotate = new Rotate(0.0, size, size);
        rotate.angleProperty().bind(hour.multiply(30).add(minute.multiply(0.5)));
        Line hand = new Line(size, size, size, size * 0.6);
        hand.setStyle("-fx-stroke-width: 3");
        hand.getTransforms().add(rotate);

        return hand;
    }

    //Минутная стрелка
    private Node minute() {
        Rotate rotate = new Rotate(0.0, size, size);
        rotate.angleProperty().bind(minute.multiply(6));
        Line hand = new Line(size, size, size, size * 0.4);
        hand.setStyle("-fx-stroke-width: 2");
        hand.getTransforms().add(rotate);

        return hand;
    }

    //Секундная стрелка
    private Node second() {
        Rotate rotate = new Rotate(0.0, size, size);
        rotate.angleProperty().bind(second.multiply(6));
        Line hand = new Line(size, size, size, size * 0.2);
        hand.setStroke(Color.BLACK);
        hand.getTransforms().add(rotate);

        return hand;
    }

    //Деления на циферблате
    private Node groupSegments() {
        Group segments = new Group();
        for (int n = 0; n < 60; n++) {
            segments.getChildren().add(oneSegment(n));
        }

        return segments;
    }

    //label для 12, 3, 6, 9 часов
    private Node groupLable() {
        Group segments = new Group();
        Label label1 = new Label("9");
        label1.setStyle("-fx-font-weight: bold");
        label1.setTranslateX(size * 0.02);
        label1.setTranslateY(size * 0.96);
        segments.getChildren().add(label1);

        Label label2 = new Label("12");
        label2.setStyle("-fx-font-weight: bold");
        label2.setTranslateX(size * 0.97);
        label2.setTranslateY(size * 0.023);
        segments.getChildren().add(label2);

        Label label3 = new Label("6");
        label3.setStyle("-fx-font-weight: bold");
        label3.setTranslateX(size * 0.99);
        label3.setTranslateY(size * 1.9);
        segments.getChildren().add(label3);

        Label label4 = new Label("3");
        label4.setStyle("-fx-font-weight: bold");
        label4.setTranslateX(size * 1.93);
        label4.setTranslateY(size * 0.96);
        segments.getChildren().add(label4);

        return segments;
    }

    //Одно деление
    private Node oneSegment(double n) {
        double angle = 6 * n;
        Rotate rotate = new Rotate(angle, size, size);
        Line mark;
        if (n % 15 == 0) {
            mark = new Line(size, size * 0.1, size, size * 0.3);
        } else if (n % 5 == 0)
            mark = new Line(size, size * 0.1, size, size * 0.22);
        else
            mark = new Line(size, size * 0.1, size, size * 0.15);
        mark.setStroke(Color.RED);
        mark.getTransforms().add(rotate);

        return mark;
    }

    //Центральная точка
    private Node center() {
        double radius = 0.02 * size;

        return new Circle(size, size, radius);
    }

    //Фотография
    private Node backGround() throws Exception {
        Calendar calendarBegin = Calendar.getInstance();
        hour.set(calendarBegin.get(Calendar.HOUR));
        minute.set(calendarBegin.get(Calendar.MINUTE));
        second.set(calendarBegin.get(Calendar.SECOND));

        arc.setCenterX(size);
        arc.setCenterY(size);
        arc.setRadiusX(size);
        arc.setRadiusY(size);
        angleStart = 90 - calendarBegin.get(Calendar.SECOND) * 6;
        arc.setStartAngle(angleStart);
        arc.setLength(lengthPhoto);
        arc.setType(ArcType.ROUND);
        arc.setFill(new ImagePattern(new Image(clockImage)));

        return arc;
    }

    //Зеленый фон
    private Node backGroundDefault() throws Exception {
        arcDefault.setCenterX(size);
        arcDefault.setCenterY(size);
        arcDefault.setRadiusX(size);
        arcDefault.setRadiusY(size);
        arcDefault.setStartAngle(angleStart);
        arcDefault.setLength(lengthDefault);
        arcDefault.setType(ArcType.ROUND);
        arcDefault.setFill(clockColor);

        return arcDefault;
    }

    //Запускаем анимацию
    private static void startTick() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            Calendar calendar = Calendar.getInstance();
            hour.set(calendar.get(Calendar.HOUR));
            minute.set(calendar.get(Calendar.MINUTE));
            second.set(calendar.get(Calendar.SECOND));

            lengthDefault += delta;
            arcDefault.setLength(lengthDefault);
            if (lengthDefault == -360)
                lengthDefault = 360;
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}