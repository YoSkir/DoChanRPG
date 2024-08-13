package ys.gme.dochanrpg;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import ys.gme.dochanrpg.circle.CircleField;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Pane pane=new Pane();
        pane.setStyle("-fx-background-color: #2e362e");

        Scene scene = new Scene(pane, Constant.WIDTH, Constant.HEIGHT);

        stage.setTitle("兜醬你好");
        stage.setScene(scene);
        stage.show();
        CircleField circleField =new CircleField(pane);
        circleField.start();
    }

    public static void main(String[] args) {
        launch();
    }
}