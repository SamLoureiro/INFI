module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires org.bouncycastle.provider;
    requires org.bouncycastle.pkix;
    requires org.eclipse.paho.mqttv5.client;
    requires org.eclipse.paho.client.mqttv3;

    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
}