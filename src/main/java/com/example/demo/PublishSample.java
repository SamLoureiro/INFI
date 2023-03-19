package com.example.demo;

import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.IMqttToken;

public class PublishSample {

    public static void PublishMQTT() {

        String broker = "tcp://broker.emqx.io:1883";
        String topic = "mqtt/test";
        String username = "emqx";
        String password = "public";
        String clientid = "publish_client";
        String content = "Hello World";
        int qos = 0;
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttAsyncClient client = new MqttAsyncClient(broker, clientid, persistence);
            MqttConnectionOptions options = new MqttConnectionOptions();
            options.setUserName(username);
            options.setPassword(password.getBytes());
            options.setConnectionTimeout(60);
            options.setKeepAliveInterval(60);
            // connect
            IMqttToken token = client.connect(options);
            token.waitForCompletion();
            // create message and setup QoS
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            // publish message
            token = client.publish(topic, message);
            token.waitForCompletion();
            System.out.println("Message published");
            System.out.println("topic: " + topic);
            System.out.println("message content: " + content);
            // disconnect
            client.disconnect();
            // close client
            client.close();
        } catch (MqttException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

