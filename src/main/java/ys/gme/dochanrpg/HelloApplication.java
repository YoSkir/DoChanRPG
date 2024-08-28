package ys.gme.dochanrpg;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import ys.gme.dochanrpg.circle.CircleManager;
import ys.gme.dochanrpg.controller.DataShowController;
import ys.gme.dochanrpg.data.DataManager;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Pane pane=new Pane();
        pane.setStyle("-fx-background-color: #2e362e");

        Scene scene = new Scene(pane, Constant.WIDTH, Constant.HEIGHT);

        stage.setTitle("兜醬你好");
        stage.setScene(scene);
        stage.show();
        DataManager dataManager=new DataManager();
        CircleManager circleManager =new CircleManager(pane,dataManager);
        circleManager.start();

        //數據
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("data-show.fxml"));
        Scene dataScene=new Scene(fxmlLoader.load());
        Stage dataStage=new Stage();
        dataStage.setTitle("數據");
        dataStage.setScene(dataScene);
        dataStage.show();

        DataShowController dataShowController=fxmlLoader.getController();
        dataShowController.setDataList(dataManager.getDataShowList());
    }

    public static void main(String[] args) {
        launch();
    }
}