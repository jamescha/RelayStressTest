package relay.test.runner;

import java.util.ArrayList;

import relay.multicast.client.StressTestClient;
import relay.pipe.client.StressTestRelayPipe;
import relay.unicast.server.StressTestServer;

public class StressTestRunner {
	static ArrayList<String> pipePorts = new ArrayList<String>();
	static ArrayList<String> unicastPorts = new ArrayList<String>();
	static ArrayList<String> multicastPorts = new ArrayList<String>();
	
	static Boolean hasPipes = false;
	static Boolean hasUnicast = false;
	static Boolean hasMulticast = false;
	static Boolean latency = false;
	
	public static void main(String[] temp) throws InterruptedException {
		String[] args = {"--pipes","1234","9090","1235","9091","1236","9092","1237","9093"};
		if(args.length == 0) {
		} else {
			loop: for (int i = 0; i < args.length; i++) {
				switch (args[i]) {
					case "--help":
						break loop;
					case "--pipes":
						hasPipes = true;
						pipePorts.add("--ports");
						unicastPorts.add("--ports");
						multicastPorts.add("--ports");
						i++;
						while (i < args.length) {
							try {
								Integer.parseInt(args[i]);
								pipePorts.add(args[i]);
								pipePorts.add(args[i+1]);
								multicastPorts.add(args[i]);
								unicastPorts.add(args[i+1]);
								i+=2;
							} catch (NumberFormatException e) {
								break;
							}
						}
					break;
					case "--unicast":
						hasUnicast = true;
						unicastPorts.add("--ports");
						i++;
						while (i<args.length) {
							try {
								Integer.parseInt(args[i]);
								unicastPorts.add(args[i]);
								i++;
							} catch (NumberFormatException e) {
								break;
							}
						}
						break;
					case "--multicast":
						hasMulticast = true;
						multicastPorts.add("--ports");
						i++;
						while (i<args.length) {
							try {
								Integer.parseInt(args[i]);
								multicastPorts.add(args[i]);
								i++;
							} catch (NumberFormatException e) {
								break;
							}
						}
						break;
					case "--latency":
						latency=true;
					break;
				}
			}
			
			if(hasPipes) {
				System.out.println(pipePorts.size());
				String[] pipePortsArray = new String[pipePorts.size()];
				pipePortsArray = pipePorts.toArray(pipePortsArray);
				System.out.println(pipePortsArray.length);
				StressTestRelayPipe.main(pipePortsArray);
			} else { //Default to increasing in size
				(new Thread(new incrementUpByOnePipe())).start();
			}
		}
	}
	
	private static class incrementUpByOnePipe implements Runnable {
		Integer ports = 9090;
		String[] pipePortsArray = new String[1];
		ArrayList<String> pipeList = new ArrayList<String>();
		
		public incrementUpByOnePipe() {
			pipeList.add(Integer.toString(ports));
		}
		
		@Override
		public void run() {
			while(true) {
				pipePortsArray = pipeList.toArray(pipePortsArray);
				pipeList.set(0, Integer.toString(ports++));
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	
	private static class incrementUpByOneMulticast implements Runnable {
		
		Integer ports = 1234;
		String[] multicastPortsArray = new String [1];
		ArrayList<String> multicastPorts = new ArrayList<String>();
		public incrementUpByOneMulticast() {
			multicastPorts.add(Integer.toString(ports));
		}
		
		@Override
		public void run() {
			multicastPortsArray =  multicastPorts.toArray(multicastPortsArray);
			multicastPorts.set(0, Integer.toString(ports++));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
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
