package relay.unicast.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

import relay.test.runner.Latency;

public class StressTestServer implements Runnable{
	
	private int port;
	Latency latency;
	
	public StressTestServer(int port) {
		this.port = port;
	}
	
	public StressTestServer(int port, Latency latency) {
		this.port = port;
		this.latency = latency;
	}
	
	@Override
	public void run() {
		System.setProperty("java.net.preferIPv4Stack", "true");
		DatagramSocket datagramSocket = null;
		try {
			datagramSocket = new DatagramSocket(port, InetAddress.getLoopbackAddress());
			byte[] buffer = new byte[1028];
			DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
			System.out.println("Server up for Port: " + port);
			while(true) {
				datagramSocket.receive(datagramPacket);
				String message = new String(datagramPacket.getData(),0, datagramPacket.getLength());
				if (!message.equals("Allonsy")) {
					System.err.println("Incorrect " + message + " from " + datagramPacket.getSocketAddress());
				} else {
					latency.setEndTime();
					System.out.println("Port "+ port + " Latency: " + latency.getLatency() + " milliseconds.");
					//System.out.println("Correct Message Recieved: " + message + ". At with server port: " + datagramSocket.getLocalPort());
				}
			}
		} catch (IOException e) {
			System.err.println(e);
		} finally {
			datagramSocket.close();
			System.out.println("Shuting down unicast...");
		}
	}
	
	public static void main(String[] args) {
		if ( args.length > 0) {
			for (String port : args) {
				(new Thread(new StressTestServer(Integer.parseInt(port)))).start();
			}
		} else {
			System.err.println("usage StressTestServer port 'message' Example StressTestServer 9090 Hello");
		}
	}
	
	public static void main(String[] args, HashMap<Integer, Latency> latencys) {
		if ( args.length > 0) {
			for (String port : args) {
				(new Thread(new StressTestServer(Integer.parseInt(port), latencys.get(Integer.parseInt(port))))).start();
			}
		} else {
			System.err.println("usage StressTestServer port 'message' Example StressTestServer 9090 Hello");
		}
	}
}
