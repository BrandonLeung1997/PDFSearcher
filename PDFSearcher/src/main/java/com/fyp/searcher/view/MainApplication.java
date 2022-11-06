package com.fyp.searcher.view;

import com.fyp.searcher.util.DragResize;
import com.sun.javafx.application.HostServicesDelegate;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MainApplication extends Application {


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/com/fyp/searcher/view/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setMaxWidth(screenBounds.getWidth());
        stage.setMaxHeight(screenBounds.getHeight());
        stage.setWidth(screenBounds.getWidth()/1.1);
        stage.setHeight(screenBounds.getHeight()/1.1);

        stage.setX(0);
        stage.setY(0);

        stage.setScene(scene);
        DragResize.addResizeListener(stage);
        //stage.setFullScreen(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}