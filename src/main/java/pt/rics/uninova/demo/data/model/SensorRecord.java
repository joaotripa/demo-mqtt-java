/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.rics.uninova.demo.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author joao
 */
public class SensorRecord {
    
    @JsonProperty
    private String sensorID;
    
    @JsonProperty
    private String sensorDescription;
    
    @JsonProperty
    private int sensorValue;
    
    public SensorRecord() {    
    }
    
    public SensorRecord(String ID, String description, int value) {
        this.sensorID = ID;
        this.sensorDescription = description;
        this.sensorValue = value;
    }

    public String getSensorID(){
        return this.sensorID;
    }
    
    public String getSensorDescription(){
        return this.sensorDescription;
    }
    
    public int getSensorValue(){
        return this.sensorValue;
    }
    
    public String toString() {
        return new com.google.gson.Gson().toJson(this);
    }
}
