/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaserialport;

/**
 *
 * @author nicod
 */
public interface IMqttDataHandler {
    public void messageArrived(String channel, String message);
}
