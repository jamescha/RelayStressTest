package relay.pipe.client;

import java.io.IOException;

import com.google.common.base.CaseFormat;
import com.google.common.net.HostAndPort;
import com.pelco.stargate.application.Configuration;
import com.pelco.stargate.relay.client.MessageTypes.Params;
import com.pelco.stargate.relay.client.MessageTypes.RelayConfiguation;
import com.pelco.stargate.relay.client.ZeroMQRelayClient;

public class StressTestRelayPipe {
	
	static ZeroMQRelayClient relayC;
	static String token;
	RelayConfiguation relayConfig;
	
	public StressTestRelayPipe(int port) {
		Configuration.ZMQ config = new Configuration.ZMQ();
		relayC = new ZeroMQRelayClient(config);
		config.port = 8042;
		relayConfig = new RelayConfiguation(
		          HostAndPort.fromParts("234.0.0.1", 1234),
		          HostAndPort.fromParts("127.0.0.1", port),
		          6000);
		token = relayC.addRelay(relayConfig);
		relayConfig.setGroup(token);
	}
	
	public void addPipe (int port) {
		
		relayConfig.setDestinationPort(port);
		
	    relayC.addRelay(relayConfig);
	}
	
	public void startRefreshLoop () {
		(new Thread (new RefreshLoop())).start();
	}
	
	private static class RefreshLoop implements Runnable {
		@Override
		public void run() {
			
			try {
			     try {
			    	 while(true){
			    		 Thread.sleep(5000);
			    		 relayC.refresh(token);
			    	 }
			     } catch(RuntimeException e) {
			    	 e.printStackTrace();
			     } catch (InterruptedException e) {
					e.printStackTrace();
				}
			} finally {
				try {
					relayC.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public static void main(String[] args) {
		System.out.println(args.length);
		
		if (args.length > 0) {
			switch (args[0]) {
			case "--ports":
				StressTestRelayPipe stressTestRelayPipe = new StressTestRelayPipe(Integer.parseInt(args[1]));
				for (int i=2; i < args.length; i++) {
					stressTestRelayPipe.addPipe(Integer.parseInt(args[i]));
				}
				stressTestRelayPipe.startRefreshLoop();
				break;
			default:
				break;
			}
		} else {
			System.err.println("usage StressTestRelayPipe portnumbers Example StressTestRelayPipe 9090 9091 9092");
		}
	}
}
