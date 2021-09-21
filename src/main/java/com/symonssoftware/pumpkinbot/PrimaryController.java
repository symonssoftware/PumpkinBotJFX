package com.symonssoftware.pumpkinbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class PrimaryController {

    @FXML
    private ProgressIndicator pIndicator;
    
    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
    
    @FXML
    private void disableRobotButtonPressed() throws IOException, InterruptedException {
        runScript("/home/ubuntu/Scripts/disableRobot.sh", "Robot is Disabled");              
     }
    
    @FXML
    private void teleopModeButtonPressed() throws IOException, InterruptedException {
        runScript("/home/ubuntu/Scripts/teleopRobot.sh", "Robot is in Teleop Mode");
    }

    @FXML
    private void autonomousModeButtonPressed() throws IOException, InterruptedException {
        runScript("/home/ubuntu/Scripts/autoRobot.sh", "Robot is in Auto Mode");
     }

    @FXML
    private void microRosAgentButtonPressed() throws IOException, InterruptedException {
        runScript("/home/ubuntu/Scripts/startMicroROSAgent.sh", "MicroRos Agent Started");
     }

    @FXML
    private void lidarDemoButtonPressed() throws IOException, InterruptedException {
        runScript("/home/ubuntu/Scripts/lidarDemo.sh", "LIDAR Demo Started");
    }
    
    @FXML
    private void quitButtonPressed() throws IOException {

        Platform.exit();
        System.exit(0);
    }
 
    private void showAlertWithDefaultHeaderText(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private static String output(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + System.getProperty("line.separator"));
            }
        } finally {
            br.close();
        }
        return sb.toString();
    }
    
    public void runScript(String script, String msg) throws InterruptedException {
        
        // Create a Runnable
        Runnable task = new Runnable()
        {
            public void run() {
                // Update the Process Indicator on the JavaFx Application Thread        
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        pIndicator.setVisible(true);
                    }
                });

                ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", script);

                try {
                    Process process = pb.start();
                    int errCode = process.waitFor();

                    // Update the Process Indicator on the JavaFx Application Thread        
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            pIndicator.setVisible(false);

                            if (errCode == 0) {
                                showAlertWithDefaultHeaderText("Script Results", msg);
                            } else {
                                try {
                                    showAlertWithDefaultHeaderText("Script Results", output(process.getInputStream()));
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    });

                   

                } catch (IOException e) {
                    System.out.print("error");
                    e.printStackTrace();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        };

        // Run the task in a background thread
        Thread backgroundThread = new Thread(task);
        // Terminate the running thread if the application exits
        backgroundThread.setDaemon(true);
        // Start the thread
        backgroundThread.start();
        
     }
}
