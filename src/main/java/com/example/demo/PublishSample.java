package com.example.demo;

import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;

public class PublishSample {

    public static void PublishMQTT() {

        String broker = "ssl://e491d61b34f64c1182124c028d1f05fa.s2.eu.hivemq.cloud:8883";
        String topic = "Test";
        String username = "samloureiro";
        String password = "12345678";
        String clientid = "INFI_2022_1";
        String content = "Hello World";
        int qos = 2;
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttAsyncClient client = new MqttAsyncClient(broker, clientid, persistence);
            MqttConnectionOptions options = new MqttConnectionOptions();
            options.setUserName(username);
            options.setPassword(password.getBytes());
            options.setConnectionTimeout(60);
            options.setKeepAliveInterval(60);
            options.setAutomaticReconnect(true);

            // connect
            IMqttToken token = client.connect(options);
            token.waitForCompletion();

            while (true) {
                // Publish "Hello World" to the subscribed topic every 5 seconds
                MqttMessage message = new MqttMessage(content.getBytes());
                message.setQos(qos);
                client.publish(topic, message);
                Thread.sleep(5000);
            }
        } catch (MqttException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
