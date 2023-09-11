package org.mj.Threads;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.mj.Interfaces.IThread;
import org.mj.Models.User;

public class MQTTClientThread extends Thread implements IThread {
    private final String _content;
    private final int _IDTextingFriend;
    private final User _currentUser;

    public MQTTClientThread(String content, User currentUser, int IDTextingFriend){
        _content = content;
        _currentUser = currentUser;
        _IDTextingFriend = IDTextingFriend;
    }

    public void run() {
        String broker = "tcp://40.115.61.160:1883";
        String topic = "mqtt/"+_IDTextingFriend+"/"+_currentUser.ID_User;
        String username = _currentUser.Login;
        String password = _currentUser.Password;
        String clientid = String.valueOf(_currentUser.Name);

        int qos = 0;

        try {
            MqttClient client = new MqttClient(broker ,clientid, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            options.setConnectionTimeout(60);
            options.setKeepAliveInterval(60);

            client.connect(options);

            MqttMessage message = new MqttMessage((_content).getBytes());
            message.setQos(qos);

            client.publish(topic, message);
            System.out.println("Message published");
            System.out.println("topic: " + topic);
            System.out.println("message content: " + _content);

            // disconnect
            client.disconnect();
            // close client
            client.close();
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }
}
