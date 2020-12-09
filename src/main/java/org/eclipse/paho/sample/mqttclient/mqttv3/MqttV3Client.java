package org.eclipse.paho.sample.mqttclient.mqttv3;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import pt.rics.uninova.demo.constants.Constants;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import pt.rics.uninova.demo.gui.Mode;
import pt.rics.uninova.demo.gui.PublisherGUIFrame;
import pt.rics.uninova.demo.gui.SubscriberGUIFrame;

public final class MqttV3Client implements MqttCallback {

    MqttV3Connection v3ConnectionParameters;

    MqttAsyncClient v3Client;
    Mode mode;
    PublisherGUIFrame pubFrame;
    SubscriberGUIFrame subFrame;
    // To allow a graceful disconnect.
    final Thread mainThread = Thread.currentThread();
    static volatile boolean keepRunning = true;

    /**
     * Initialises the MQTTv3 Executor
     * @param mode - The mode to run in (PUB / SUB)
     * @throws org.eclipse.paho.client.mqttv3.MqttException
     */
    public MqttV3Client(Mode mode) throws MqttException{
        this.createClient();
        
        if(mode == Mode.PUB){
            this.pubFrame = new PublisherGUIFrame(this, false);
            this.pubFrame.addWindowListener(new WindowAdapter(){
                @Override
                public void windowClosing(WindowEvent we) {
                    if (JOptionPane.showConfirmDialog(pubFrame, 
                        "Are you sure you want to close this window?", "Close Window?", 
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
                        try {
                            disconnectClient();
                            closeClientAndExit();
                        } catch (MqttException ex) {
                            Logger.getLogger(MqttV3Client.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        pubFrame.dispose();
                        pubFrame.setVisible(false);
                    }
                }
                
            });
            this.pubFrame.setVisible(true);
            
        }
        if(mode == Mode.SUB){
            this.subFrame = new SubscriberGUIFrame(this, false);
            this.subFrame.addWindowListener(new WindowAdapter(){
                @Override
                public void windowClosing(WindowEvent we) {
                    if (JOptionPane.showConfirmDialog(subFrame, 
                        "Are you sure you want to close this window?", "Close Window?", 
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
                        try {
                            disconnectClient();
                            closeClientAndExit();
                        } catch (MqttException ex) {
                            Logger.getLogger(MqttV3Client.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        subFrame.dispose();
                        subFrame.setVisible(false);
                    }
                }
                
            });
            this.subFrame.setVisible(true);
        }
        this.mode = mode;
    }

    public void createClient() throws MqttException{
        // Create Client
        this.v3ConnectionParameters = new MqttV3Connection();

        this.v3Client = new MqttAsyncClient(this.v3ConnectionParameters.getHostURI(),
                        this.v3ConnectionParameters.getClientID(), new MemoryPersistence());
        this.v3Client.setCallback(this);

        // Connect to Server
        logMessage(String.format("Connecting to MQTT Broker: %s, Client ID: %s", v3Client.getServerURI(),
                        v3Client.getClientId()), true);
        IMqttToken connectToken = v3Client.connect(v3ConnectionParameters.getConOpts());
        connectToken.waitForCompletion(Constants.ACTION_TIMEOUT);
        logMessage(String.format("Client ID: %s connected successfully!", v3Client.getClientId()), true);
        
        //execute();        //Uncomment if not using GUIs, terminates program with CTR+C
    }

    public void createClient(String clientID) throws MqttException{
        // Create Client
        this.v3ConnectionParameters = new MqttV3Connection(clientID);

        this.v3Client = new MqttAsyncClient(this.v3ConnectionParameters.getHostURI(),
                        this.v3ConnectionParameters.getClientID(), new MemoryPersistence());
        this.v3Client.setCallback(this);

        // Connect to Server
        logMessage(String.format("Connecting to MQTT Broker: %s, Client ID: %s", v3Client.getServerURI(),
                        v3Client.getClientId()), true);
        IMqttToken connectToken = v3Client.connect(v3ConnectionParameters.getConOpts());
        connectToken.waitForCompletion(Constants.ACTION_TIMEOUT);
        
        logMessage(String.format("Client ID: %s connected successfully!", v3Client.getClientId()), true);

        //execute();    //Uncomment if not using GUIs
    }

    private void execute() throws MqttException{
        if (this.mode == Mode.SUB) {
            addShutdownHook();

            while (keepRunning) {
                    // Do nothing
            }
            disconnectClient();
            closeClientAndExit();
        }
    }

    public void publish(String topic, int qos, String message, boolean retain) throws MqttException{
        logMessage(String.format("Publishing message to %s", topic), true);
        publishMessage(message.getBytes(), qos, retain, topic);

        //disconnectClient();   //Uncomment if not using GUIs

        // Close the client
        //closeClientAndExit(); //Uncomment if not using GUIs
    }

    public void publishFile(String topic, int qos, String filename, boolean retain) throws MqttException{

        logMessage(String.format("Publishing file from %s to %s", filename, topic), true);
        Path path = Paths.get(filename);
        try {
                byte[] data  = Files.readAllBytes(path);
                publishMessage(data, qos, retain, topic);

                //disconnectClient(); //Uncomment if not using GUIs

                // Close the client
                //closeClientAndExit();     //Uncomment if not using GUIs
        } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
    }

    public void subscribe(String topic, int qos) throws MqttException{
        logMessage(String.format("Subscribing to %s, with QoS %d", topic, qos), true);
        IMqttToken subToken = this.v3Client.subscribe(topic, qos);
        subToken.waitForCompletion(Constants.ACTION_TIMEOUT);
    }
    
    public void subscribe(String[] topics, int[] qos) throws MqttException{
        logMessage(String.format("Subscribing to %s, with QoS %s", Arrays.toString(topics), Arrays.toString(qos)), true);
        IMqttToken subToken = this.v3Client.subscribe(topics, qos);
        subToken.waitForCompletion(Constants.ACTION_TIMEOUT);
    }
    
    public void unsubscribe(String topic) throws MqttException{
        logMessage(String.format("Unubscribing to %s", topic), true);
        IMqttToken subToken = this.v3Client.unsubscribe(topic);
        subToken.waitForCompletion(Constants.ACTION_TIMEOUT);
    }
    
    public void unsubscribe(String[] topics) throws MqttException{
        logMessage(String.format("Unubscribing to %s", Arrays.toString(topics)), true);
        IMqttToken subToken = this.v3Client.unsubscribe(topics);
        subToken.waitForCompletion(Constants.ACTION_TIMEOUT);
    }

    /**
     * Simple helper function to publish a message.
     * 
     * @param payload
     * @param qos
     * @param retain
     * @param topic
     * @throws MqttPersistenceException
     * @throws MqttException
     */
    private void publishMessage(byte[] payload, int qos, boolean retain, String topic)
                throws MqttPersistenceException, MqttException {
        MqttMessage v3Message = new MqttMessage(payload);
        v3Message.setQos(qos);
        v3Message.setRetained(retain);
        IMqttDeliveryToken deliveryToken = v3Client.publish(topic, v3Message);
        deliveryToken.waitForCompletion(Constants.ACTION_TIMEOUT);
    }

    /**
     * Log a message to the console, nothing fancy.
     * 
     * @param message
     * @param isDebug
     */
    private void logMessage(String message, boolean isDebug) {
        if ((Constants.DEBUG == true && isDebug == true) || isDebug == false) {
                System.out.println(message);
        }
    }

    /**
     * Log an error to the console
     * 
     * @param error
     */
    private void logError(String error) {
        if (Constants.QUIET == false) {
                System.err.println(error);
        }
    }

    public void disconnectClient() throws MqttException {
        // Disconnect
        logMessage("Disconnecting from server.", true);
        IMqttToken disconnectToken = v3Client.disconnect();
        disconnectToken.waitForCompletion(Constants.ACTION_TIMEOUT);
    }

    public void closeClientAndExit() {
        // Close the client
        logMessage("Closing Connection.", true);
        try {
                this.v3Client.close();
                logMessage("Client Closed.", true);
                //System.exit(0);       //Uncomment if not using GUIs, terminates program with CTR+C
                //mainThread.join();    //Uncomment if not using GUIs, terminates program with CTR+C
        } catch (MqttException e) {
                // End the Application
                //System.exit(1);   //Uncomment if not using GUIs,  terminates program with CTR+C
        }
    }

    /**
     * Adds a shutdown hook, that will gracefully disconnect the client when a
     * CTRL+C rolls in.
     */
    public void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                        keepRunning = false;
                }
        });
    }

    @Override
    public void connectionLost(Throwable cause) {

        if (v3ConnectionParameters.isAutomaticReconnectEnabled()) {
                logMessage(String.format("The connection to the server was lost, cause: %s. Waiting to reconnect.",
                                cause.getMessage()), true);
        } else {
                logMessage(String.format("The connection to the server was lost, cause: %s. Closing Client",
                                cause.getMessage()), true);
                closeClientAndExit();
        }

    }
     

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String messageContent = new String(message.getPayload());
        if (Constants.VERBOSE) {
                logMessage(String.format("%s %s", topic, messageContent), false);
        } else {
                logMessage(messageContent, false);
        }
        subFrame.displayMessages(messageContent);

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        logMessage(String.format("Message %d was delivered.", token.getMessageId()), true);
    }


}
