package com.example.demo;

import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttSecurityException;
import org.eclipse.paho.mqttv5.common.MqttSubscription;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

public class MQTT {

    static String broker = "ssl://e491d61b34f64c1182124c028d1f05fa.s2.eu.hivemq.cloud:8883";
    static String topic = "Test";
    static String username = "samloureiro";
    static String password = "12345678";
    static String clientid = "INFI_2022_2";
    static String content = "Is it working?";
    static int qos = 2;
    static MemoryPersistence persistence = new MemoryPersistence();
    static MqttAsyncClient client;
    static MqttConnectionOptions options;
    static IMqttToken token;

    public static void SetupMQTTServer() {
        try {
            client = new MqttAsyncClient(broker, clientid, persistence);
            options = new MqttConnectionOptions();
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
                    if (payload.equals("disconnect")) {
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
                        token = client.subscribe(new MqttSubscription[]{subscription});
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
            token = client.connect(options);
            token.waitForCompletion();
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    public static void SubscribeTopic() {
        try {
            MqttSubscription subscription = new MqttSubscription(topic, qos);
            token = client.subscribe(new MqttSubscription[]{subscription});
            token.waitForCompletion();
            System.out.println("Subscribed to topic " + topic);
            while (true) {
                // Keep the connection alive
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void PublishMessage() {
        while (true) {
            try {
                // Publish "Hello World" to the subscribed topic every 5 seconds
                MqttMessage message = new MqttMessage(content.getBytes());
                message.setQos(qos);
                client.publish(topic, message);
                Thread.sleep(5000);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}