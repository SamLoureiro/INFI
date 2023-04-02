package com.example.demo;

import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttSubscription;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

import java.util.Arrays;

public class SubscribeSample {

    public static void SubscribeMQTT() {

        String broker = "ssl://e491d61b34f64c1182124c028d1f05fa.s2.eu.hivemq.cloud:8883";
        String topic = "Test";
        String username = "samloureiro";
        String password = "12345678";
        String clientid = "INFI_2022_2";
        String content = "Is it working?";
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
            client.setCallback(new MqttCallback() {
                @Override
                public void disconnected(MqttDisconnectResponse response) {
                    System.out.println("disconnected " + response);
                    try {
                        // Reconnect when the connection is lost
                        client.reconnect();
                    } catch (MqttException e) {
                        System.out.println("Error reconnecting: " + e.getMessage());
                    }
                }
                @Override
                public void mqttErrorOccurred(MqttException e) {
                    System.out.println("MQTT error occurred " + e.getMessage());
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    String payload = new String(mqttMessage.getPayload());
                    if(payload.equals("disconnect")) {
                        System.out.println("A tentar desconectar...\n");
                        client.disconnectForcibly();
                        client.close();
                        System.out.println("Disconnected\n");
                    }
                    System.out.println("Message arrived on topic " + s + " with payload " + payload);
                }

                @Override
                public void deliveryComplete(IMqttToken iMqttToken) {
                    System.out.println("Delivery complete for token " + iMqttToken);
                }

                @Override
                public void connectComplete(boolean b, String s) {
                    System.out.println("Connection complete with result " + b + " and server URI " + s);
                    try {
                        // Subscribe again when the connection is established or re-established
                        MqttSubscription subscription = new MqttSubscription(topic, qos);
                        IMqttToken token = client.subscribe(new MqttSubscription[]{subscription});
                        token.waitForCompletion();
                    } catch (MqttException e) {
                        System.out.println("Error subscribing: " + e.getMessage());
                    }
                }

                @Override
                public void authPacketArrived(int i, MqttProperties mqttProperties) {
                    System.out.println("Authentication packet arrived with type " + i + " and properties " + mqttProperties);
                }

            });
            // connect
            IMqttToken token = client.connect(options);
            token.waitForCompletion();
            // create message and setup QoS
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            MqttSubscription subscription = new MqttSubscription(topic, qos);
            token = client.subscribe(new MqttSubscription[] { subscription });
            token.waitForCompletion();
            System.out.println("Subscribed to topic " + topic);
            while (true) {
                // Keep the connection alive
                Thread.sleep(1000);
            }
        } catch (MqttException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
