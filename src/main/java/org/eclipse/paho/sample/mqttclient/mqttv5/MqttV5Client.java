package org.eclipse.paho.sample.mqttclient.mqttv5;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttPersistenceException;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import pt.rics.uninova.demo.constants.Constants;
import pt.rics.uninova.demo.gui.Mode;
import pt.rics.uninova.demo.gui.PublisherGUIFrame;
import pt.rics.uninova.demo.gui.SubscriberGUIFrame;

public class MqttV5Client implements MqttCallback {

    MqttV5Connection connectionParams;
    MqttAsyncClient v5Client;
    Mode mode;
    PublisherGUIFrame pubFrame;
    SubscriberGUIFrame subFrame;
    // To allow for a graceful disconnect
    final Thread mainThread = Thread.currentThread();
    static volatile boolean keepRunning = true;

    /**
     * Initialises the MQTTv5 Executor
     * 
     * @param mode
     *            - The mode to run in (PUB / SUB)
     * @throws org.eclipse.paho.mqttv5.common.MqttException
     */
    public MqttV5Client(Mode mode) throws MqttException {
        this.createClient();
        if(mode == Mode.PUB){
            this.pubFrame = new PublisherGUIFrame(this, true);
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
                            Logger.getLogger(MqttV5Client.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        pubFrame.dispose();
                        pubFrame.setVisible(false);
                    }
                }
                
            });
            this.pubFrame.setVisible(true);
        }
        if(mode == Mode.SUB){
            this.subFrame = new SubscriberGUIFrame(this, true);
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
                            Logger.getLogger(MqttV5Client.class.getName()).log(Level.SEVERE, null, ex);
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
        // Create the client.
        this.connectionParams = new MqttV5Connection();
        
        MemoryPersistence persistence = new MemoryPersistence();
        this.v5Client = new MqttAsyncClient(connectionParams.getHostURI(), connectionParams.getClientID(), persistence);
        this.v5Client.setCallback(this);

        // Connect to the server
        logMessage(String.format("Connecting to MQTT Broker %s, Client ID: %s", v5Client.getServerURI(),
                        v5Client.getClientId()), true);

        IMqttToken connectToken = v5Client.connect(connectionParams.getConOpts());
        connectToken.waitForCompletion(Constants.ACTION_TIMEOUT);
        
        logMessage(String.format("Client ID: %s connected successfully!", v5Client.getClientId()), true);
        
        //execute();        //Uncomment if not using GUIs, terminates program with CTR+C
    }
    
     public void createClient(String clientID) throws MqttException{
        // Create the client.
        this.connectionParams = new MqttV5Connection();
        
        MemoryPersistence persistence = new MemoryPersistence();
        this.v5Client = new MqttAsyncClient(connectionParams.getHostURI(), connectionParams.getClientID(), persistence);
        this.v5Client.setCallback(this);

        // Connect to the server
        logMessage(String.format("Connecting to MQTT Broker %s, Client ID: %s", v5Client.getServerURI(),
                        v5Client.getClientId()), true);

        IMqttToken connectToken = v5Client.connect(connectionParams.getConOpts());
        connectToken.waitForCompletion(Constants.ACTION_TIMEOUT);
        
        logMessage(String.format("Client ID: %s connected successfully!", v5Client.getClientId()), true);
        
        //execute();        //Uncomment if not using GUIs, terminates program with CTR+C
    }
     
    private void execute() throws MqttException{
        if (mode == Mode.SUB) {   //Uncomment if not using GUIs
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
     
    public void publishFile(String topic, int qos, String filename, boolean retain) throws IOException, MqttException{
        
        logMessage(String.format("Publishing file from %s to %s.", filename, topic), true);
        Path path = Paths.get(filename);
        byte[] data = Files.readAllBytes(path);
        publishMessage(data, qos, retain, topic);
        
        //disconnectClient();   //Uncomment if not using GUIs
        
        // Close the client
        //closeClientAndExit(); //Uncomment if not using GUIs
    }
    
    public void subscribe(String topic, int qos) throws MqttException{
        logMessage(String.format("Subscribing to %s, with QoS %d", topic, qos), true);
        IMqttToken subToken = this.v5Client.subscribe(topic, qos);
        subToken.waitForCompletion(Constants.ACTION_TIMEOUT);
    }
    
    public void subscribe(String[] topics, int[] qos) throws MqttException{
        logMessage(String.format("Subscribing to %s, with QoS %s", Arrays.toString(topics), Arrays.toString(qos)), true);
        IMqttToken subToken = this.v5Client.subscribe(topics, qos);
        subToken.waitForCompletion(Constants.ACTION_TIMEOUT);
    }
    
    public void unsubscribe(String topic) throws MqttException{
        logMessage(String.format("Unubscribing to %s", topic), true);
        IMqttToken subToken = this.v5Client.unsubscribe(topic);
        subToken.waitForCompletion(Constants.ACTION_TIMEOUT);
    }
    
    public void unsubscribe(String[] topics) throws MqttException{
        logMessage(String.format("Unubscribing to %s", Arrays.toString(topics)), true);
        IMqttToken subToken = this.v5Client.unsubscribe(topics);
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
        MqttMessage v5Message = new MqttMessage(payload);
        v5Message.setQos(qos);
        v5Message.setRetained(retain);
        IMqttToken deliveryToken = v5Client.publish(topic, v5Message);
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

    public void disconnectClient() throws MqttException {
        // Disconnect
        logMessage("Disconnecting from server.", true);
        IMqttToken disconnectToken = v5Client.disconnect();
        disconnectToken.waitForCompletion(Constants.ACTION_TIMEOUT);
    }

    public void closeClientAndExit() {
        // Close the client
        logMessage("Closing Connection.", true);
        try {
                this.v5Client.close();
                logMessage("Client Closed.", true);
                //System.exit(0);       //Uncomment if not using GUIs, terminates program with CTR+C
                //mainThread.join();    //Uncomment if not using GUIs, terminates program with CTR+C
        } catch (MqttException e) {
                // End the Application
                //System.exit(1);   //Uncomment if not using GUIs,  terminates program with CTR+C
        }

    }

    @Override
    public void disconnected(MqttDisconnectResponse disconnectResponse) {
        String cause = null;
        if (disconnectResponse.getException().getMessage() != null) {
                cause = disconnectResponse.getException().getMessage();
        } else {
                cause = disconnectResponse.getReasonString();
        }
        if (connectionParams.isAutomaticReconnectEnabled()) {
                logMessage(String.format("The connection to the server was lost, cause: %s. Waiting to reconnect.", cause),
                                true);
        } else {
                logMessage(String.format("The connection to the server was lost, cause: %s. Closing Client", cause), true);
                closeClientAndExit();
        }

    }

    @Override
    public void mqttErrorOccurred(MqttException exception) {
        logError(String.format("An MQTT error occurred: %s", exception.getMessage()));
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
    public void deliveryComplete(IMqttToken token) {
        logMessage(String.format("Message %d was delivered.", token.getMessageId()), true);
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        logMessage(String.format("Connection to %s complete. Reconnect=%b", serverURI, reconnect), true);
    }

    @Override
    public void authPacketArrived(int reasonCode, MqttProperties properties) {
        logError(String.format("Auth packet received, this client does not currently support them. Reason Code: %d.",
                reasonCode));
    }
    
    

}
