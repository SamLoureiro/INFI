package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.eclipse.paho.mqttv5.common.MqttException;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 320, 240);
            stage.setTitle("Hello!");
            stage.setScene(scene);
            stage.show();
            MQTT.SetupMQTTServer();
            // Create a new thread for SubscribeSample and start it
            Thread subscribeThread = new Thread(MQTT::SubscribeTopic);
            subscribeThread.start();

            // Create a new thread for PublishSample and start it
            Thread publishThread = new Thread(PublishSample::PublishMQTT);
            publishThread.start();
        } catch(Exception e) {
            //System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}

