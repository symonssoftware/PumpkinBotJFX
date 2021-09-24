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
import javafx.stage.Window;


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
        runStartMicroRosAgentScript();
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
        alert.initOwner(App.currentStage.getScene().getWindow());
        alert.show();
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
        Runnable task = () -> {
            // Update the Process Indicator on the JavaFx Application Thread
            Platform.runLater(() -> {
                pIndicator.setVisible(true);
            });
            
            ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", script);
            
            try {
                Process process = pb.start();
                int errCode = process.waitFor();
                
                // Update the Process Indicator on the JavaFx Application Thread
                Platform.runLater(() -> {
                    pIndicator.setVisible(false);
                    
                    if (errCode == 0) {
                        App.makeToast(msg);
                    } else {
                        try {
                            showAlertWithDefaultHeaderText("Script Results", output(process.getInputStream()));
                        } catch (IOException ex) {
                        }
                    }
                });
                
            } catch (IOException e) {
                System.out.print("error");
            } catch (InterruptedException ex) {
            }
        };

        // Run the task in a background thread
        Thread backgroundThread = new Thread(task);
        // Terminate the running thread if the application exits
        backgroundThread.setDaemon(true);
        // Start the thread
        backgroundThread.start();
    }

    public void runStartMicroRosAgentScript() throws InterruptedException {

        // Create a Runnable
        Runnable task = () -> {
            // Update the Process Indicator on the JavaFx Application Thread
            Platform.runLater(() -> {
                pIndicator.setVisible(true);
            });
            
            System.out.println("Starting Power Off Script");

            ProcessBuilder powerOffTeensyPb
                    = new ProcessBuilder("/bin/bash", "-c", "/home/ubuntu/Scripts/powerOffTeensy.sh");

            try {
                int errCode = powerOffTeensyPb.start().waitFor();

                // We need this since the bash script returns immediately but the python script takes time
                Thread.sleep(6000);
                
                if (errCode == 0)
                {
                    System.out.println("powerOffTeensyPb Successfully Completed");
                }
                else
                {
                    System.out.print("powerOffTeensyPb - error: ");
                    System.out.println(errCode);
                }

            } catch (IOException | InterruptedException ex) {
                System.out.println("powerOffTeensyPb-IOException");
            }

            System.out.println("Starting MicroROS Agent Script");
            
            ProcessBuilder startMicroRosAgentPb
                    = new ProcessBuilder("/bin/bash", "-c", "/home/ubuntu/Scripts/startMicroROSAgent.sh");
            try {
                startMicroRosAgentPb.start();

                // We need this since the bash script returns immediately but the agent takes time to start
                Thread.sleep(3000);
                
                System.out.println("startMicroRosAgentPb Successfully Completed");

            } catch (IOException | InterruptedException ex) {
                System.out.println("startMicroRosAgentPb-IOException");
            }

            System.out.println("Starting Power On Script");
            
            ProcessBuilder powerOnTeensyPb
                    = new ProcessBuilder("/bin/bash", "-c", "/home/ubuntu/Scripts/powerOnTeensy.sh");
            try {
                //powerOnTeensyPb.start();
                int errCode = powerOnTeensyPb.start().waitFor();

                // We need this since the bash script returns immediately but the python script takes time
                Thread.sleep(2000);
                
                if (errCode == 0)
                {
                    System.out.println("powerOnTeensyPb Successfully Completed");
                }
                else
                {
                    System.out.print("powerOnTeensyPb - error: ");
                    System.out.println(errCode);
                }
            } catch (IOException | InterruptedException ex) {
                System.out.println("powerOnTeensyPb-IOException");
            }

            // Update the Process Indicator on the JavaFx Application Thread
            Platform.runLater(() -> {
                pIndicator.setVisible(false);
            });
        };

        // Run the task in a background thread
        Thread backgroundThread = new Thread(task);
        // Terminate the running thread if the application exits
        backgroundThread.setDaemon(true);
        // Start the thread
        backgroundThread.start();
    }
}
