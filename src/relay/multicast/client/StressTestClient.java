package relay.multicast.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;

import relay.test.runner.Latency;

public class StressTestClient implements Runnable{
	Integer port;
	HashMap<Integer, Latency> latencys;
	
	public StressTestClient(int port) {
		this.port = port;
	}
	
	public StressTestClient(int port, HashMap<Integer, Latency> latencys) {
		this.port = port;
		this.latencys = latencys;
	}
	
	@Override
	public void run() {
		
		MulticastSocket multicastSocket = null;
		
		try {
			multicastSocket = new MulticastSocket(port);
			InetAddress address = InetAddress.getByName("234.0.0.1");
			multicastSocket.joinGroup(address);
			String message = new String("Allonsy");
			DatagramPacket datagramPacket = new DatagramPacket(message.getBytes(), message.length(), address, port);
			
			System.out.println("Multicast: " + port);
			
			while(true) {
				for (Latency latency : latencys.values()) {
					latency.setStartTime();
				}
				
				multicastSocket.send(datagramPacket);
				//System.out.println("Message: " + message + " sent.");
				Thread.sleep(1000);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			multicastSocket.close();
			System.out.println("Shuting down multicast....");
		}
	}
	
	public static void main(String[] args, HashMap<Integer, Latency> latencys) {
		if (args.length > 0) {
			for (String port : args) {
				(new Thread(new StressTestClient(Integer.parseInt(port), latencys))).start();
			}
		} else {
			System.err.println("usage StressTestClient port Example StressTestClient 1234");
		}
	}
	
	public static void main(String[] args) {
		if (args.length > 0) {
			for (String port : args) {
				(new Thread(new StressTestClient(Integer.parseInt(port)))).start();
			}
		} else {
			System.err.println("usage StressTestClient port Example StressTestClient 1234");
		}
		
	}
}
