package relay.test.runner;

import java.util.Iterator;

import relay.multicast.client.StressTestClient;
import relay.pipe.client.StressTestRelayPipe;
import relay.unicast.server.StressTestServer;

public class StressTestRunner {
	public static void main(String[] args) throws InterruptedException {
		if(args.length > 0) {
			//usage
		} else {
			for (int i = 0; i < args.length; i++) {
				switch (args[i]) {
					
				}
			}
		}
	}
	
	private static void someTest() throws InterruptedException {
		StressTestClient stressTestClient = new StressTestClient();
		//String[] ports = {"9090","9091","9092","9093","9094","9095","9096","9097","9098","9099"};
		String[] ports = new String[1];
		/*for (int i = 0; i < 100; i++) {
			ports[i] = Integer.toString(9090+i);
		}*/
		Integer number = 9090;
		
		while(true) {
			ports[0] = (number).toString();
			number++;
			StressTestServer.main(ports);
			StressTestRelayPipe.main(ports);
			stressTestClient.run();
			
			Thread.sleep(1000);
		}
	}
	
	
	
}
