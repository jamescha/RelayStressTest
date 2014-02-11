package relay.multicast.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class StressTestClient implements Runnable{
	
	
	public StressTestClient() {
	}
	
	@Override
	public void run() {
		
		MulticastSocket multicastSocket = null;
		
		try {
			multicastSocket = new MulticastSocket(1234);
			InetAddress address = InetAddress.getByName("234.0.0.1");
			multicastSocket.joinGroup(address);
			String message = new String("Allonsy");
			DatagramPacket datagramPacket = new DatagramPacket(message.getBytes(), message.length(), address, 1234);
			
			multicastSocket.send(datagramPacket);
			/*while(true) {
				Thread.sleep(0);
				multicastSocket.send(datagramPacket);
			}*/
		} catch (IOException e) {
			e.printStackTrace();
		} /*catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/ finally {
			multicastSocket.close();
		}
	}
	public static void main(String[] args) {
		if (args.length > 0) {
			(new Thread(new StressTestClient())).start();
		} else {
			System.err.println("usage StressTestClient 'message' Example StressTestClient Hello");
		}
		
	}
}
