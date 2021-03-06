package com.symonssoftware.pumpkinbot;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.WindowEvent;

/**
 * JavaFX App
 */
public class App extends Application {

    private static final int TOAST_MSG_TIME = 1000; //3.5 seconds
    private static final int FADE_IN_TIME = 500; //0.5 seconds
    private static final int FADE_OUT_TIME = 500; //0.5 seconds
    
    private static Scene scene;
    public static Stage currentStage;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 640, 480);
        currentStage = stage;
        stage.setScene(scene);
        stage.show();
        
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - 640) / 2);
        stage.setY((screenBounds.getHeight() - 480) / 2);

        // For now, when they close out the app by tapping the 'x' on the
        // primary stage window, we'll kill the app.
        stage.setOnCloseRequest((WindowEvent we) -> {
            Platform.exit();
            System.exit(0);
        });
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
    
    public static void makeToast(String msg) {
        Toast.makeText(currentStage, msg, TOAST_MSG_TIME, FADE_IN_TIME, FADE_OUT_TIME);
    }

}