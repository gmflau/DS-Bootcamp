package simpleStreaming;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.text.DecimalFormat;

public class RunFeeder {


    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("need to set hostname and port in pom.xml or at the command line");
            System.exit(-1);
        }

        startMasterServer(args);
    }

    private static void startMasterServer(String[] args) {
        InetAddress hostAddress = null;
        try {
            hostAddress = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        int portNumber = Integer.parseInt(args[1]);
        boolean listening = true;

        System.out.println("Starting Server Socket");
        try (ServerSocket serverSocket = new ServerSocket(portNumber, 50, hostAddress)) {
            while (listening) {
                new MultiServerThread(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }

    public static class MultiServerThread extends Thread {
        private Socket socket = null;

        public MultiServerThread(Socket socket) {
            super("MultiServerThread");
            this.socket = socket;
        }

        public void run() {

            System.out.println("Starting Thread");
            try (
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream()));
            ) {
                while (!out.checkError()) {
                   // String nxtMessage = StringUtils.join(nextEventList(), ' ');
                	
                    System.out.println("Sending Sensor Data...");
                	List<String> sensorDataList = nextSensorDataBatch();
                	for (int indx = 0; indx < sensorDataList.size(); indx++) {
                		out.println(sensorDataList.get(indx));
                	}
                    Thread.sleep(5000);   // sleeps for 5 seconds
                }
                socket.close();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            
            System.out.println("Shutting Down Thread");
        }
    }
    
 

    private static Random rand = new Random();
   
    
    private static List<String> getSensorInventory() {
    	List<String> sensorInventory = new ArrayList<String>();
    	
    	String fileName = "/Users/gilbertlau/demo/sensor_gen_inventory.csv";
    	String line = null;
    	
    	try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                String[] token = line.split(",");
                sensorInventory.add(token[1]);
            }   

            // Always close files.
            bufferedReader.close();         
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + fileName + "'");                  
        }
    	
    	return sensorInventory;
    }
    
    
    private static String generateSensorRecord(String sensor_id, String sensor_data_type) {
    	List<String> isActive = Arrays.asList("true", "false");
        DecimalFormat df = new DecimalFormat("####.##");
    	  	
    	String sensorRecord = sensor_id + ',';
    	int randomNum = rand.nextInt(isActive.size());
    	sensorRecord += isActive.get(randomNum) + ',';
    	sensorRecord += sensor_data_type + ',';
    	switch(sensor_data_type) {
    		case "temperature" :
    			sensorRecord += df.format(rand.nextFloat()*120) + ',';
    		case "movement" :
    			sensorRecord += String.valueOf(rand.nextInt(101)) + ',';
    		case "humidity" :
    			sensorRecord += df.format(rand.nextFloat()*100) + ',';
    	SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
    	ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
    	sensorRecord += ft.format(Date.from(utc.toInstant()));
    	}
//        System.out.println(sensorRecord);
    	return sensorRecord;
    }
    
    
    private static List<String> nextSensorDataBatch() {
    	List<String> nextBatch = new ArrayList<String>();
    	
    	List<String> sensor_inventory = getSensorInventory();
        List<String> sensorDataTypeList = Arrays.asList("temperature", "movement", "humidity");

    	for (int indx = 0; indx < sensor_inventory.size(); indx++) {
            for (int sensor_indx = 0; sensor_indx < sensorDataTypeList.size(); sensor_indx++) {
                nextBatch.add(generateSensorRecord(sensor_inventory.get(indx), sensorDataTypeList.get(sensor_indx)));
            }
    	}
    	return nextBatch;
    }
    	

}
