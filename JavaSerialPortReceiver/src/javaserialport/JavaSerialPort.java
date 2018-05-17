/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaserialport;

/**
 *
 * @author Arthur
 */
public class JavaSerialPort {

    public static void main(String[] args) throws Exception {
        String beginOfTemp = "#";
        String beginOfHeartbeat = "@";
        String beginOfAccelero = "$";
        String beginOfXAxis = "<";
        String beginOfYAxis = ">";
        String endOfData = "]";
        final int MINIMUM_HEARTBEAT = 40;
        final int MAXIMUM_HEARTBEAT = 200;

        // First create an object of SerialLineReceiver using the non-default constructor
        // 0 = index of com port (not the COM number from windows!)
        // 9600 = baudrate
        // false = debugging, set to true to see more messages in the console
        SerialLineReceiver receiver = new SerialLineReceiver(0, 115200, false);
        
        MqttDataService mqttDataService = new MqttDataService();
                
        // To receive data from the serial port device you need to register a listener.
        // This is an instance of SerialPortLineListener that has a method serialLineEvent().
        // This method is called from inside SerialLineReceiver every time the serial port
        // received a data stream that contains a newline (\n).
        // The data is past using an object of type SerialData. You can access the data as a String
        // or as an array of bytes.
        receiver.setLineListener(new SerialPortLineListener() {
            @Override
            public void serialLineEvent(SerialData data) {
                String dataString = data.getDataAsString();
                
                int indexTemp = dataString.indexOf(beginOfTemp);
                int indexHeartbeat = dataString.indexOf(beginOfHeartbeat);
                int indexAccelero = dataString.indexOf(beginOfAccelero);
                int indexEnd = dataString.indexOf(endOfData);
                int indexY = dataString.indexOf(beginOfXAxis);
                int indexZ = dataString.indexOf(beginOfYAxis);
                
                if (indexTemp >= 0 && indexHeartbeat >= 0 && indexAccelero >= 0 && indexEnd >= 0 && indexY >= 0 && indexZ >=0) {
                
                    mqttDataService.sendData(dataString);
//                vanaf hier is de code louter ter controle om te controleren of de inkomende data wel juist is 
                    String Temp = dataString.substring(indexTemp+1, indexHeartbeat);
                    String hb = dataString.substring(indexHeartbeat+1, indexAccelero);
                    int hbint = Integer.parseInt(hb);
                    String acc = dataString.substring(indexAccelero+1, indexEnd);
                    String X = dataString.substring(indexAccelero+1, indexY);
                    String Y = dataString.substring(indexY+1, indexZ);
                    String Z = dataString.substring(indexZ+1, indexEnd);
                    System.out.println("Temperature: " + Temp);
                    if (hbint < MAXIMUM_HEARTBEAT && hbint > MINIMUM_HEARTBEAT) {
                        System.out.println("Heartbeat: " + hb);
                    } else {
                        System.out.println("No valid heartbeat!");
                    }
                    
                    System.out.println("Accelero: " + acc);
                    System.out.print("X: " + X);
                    System.out.print("\t");
                    System.out.print("Y: " + Y);
                    System.out.print("\t");
                    System.out.println("Z: " + Z);
                     
                } else {
                    System.out.println("No valid data!");
                    
                
                }
                
                System.out.println("Received data from the serial port: " + dataString);
                System.out.println("");
                System.out.println("");
            }
        });
    }
}


/*


*/