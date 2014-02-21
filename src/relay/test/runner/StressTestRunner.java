package relay.test.runner;

import java.util.ArrayList;
import java.util.HashMap;

import relay.multicast.client.StressTestClient;
import relay.pipe.client.StressTestRelayPipe;
import relay.unicast.server.StressTestServer;

public class StressTestRunner {
	static ArrayList<String> pipePorts = new ArrayList<String>();
	static ArrayList<String> unicastPorts = new ArrayList<String>();
	static ArrayList<String> multicastPorts = new ArrayList<String>();
	
	static Boolean hasPipes = false;
	static Boolean hasMulticast = false;
	static Boolean getlatency = false;
	static Boolean getHelp = false;
	
	static HashMap<Integer, Latency> latencys = new HashMap<Integer, Latency>();
 	
	public static void main(String[] args) throws InterruptedException {
		if(args.length == 0) {
		} else {
			loop: for (int i = 0; i < args.length; i++) {
				switch (args[i]) {
					case "--help":
						getHelp = true;
						break loop;
					case "--pipes":
						hasPipes = true;
						pipePorts.add("--ports");
						i++;
						if(Integer.parseInt(args[i]) > 0){
							Integer multiPort = 1234;
							Integer uniPort = 9090;
							multicastPorts.add(Integer.toString(multiPort));
							for( int j = 0; j < Integer.parseInt(args[i]); j++ ) {
								Latency temps = new Latency();
								unicastPorts.add(Integer.toString(uniPort));
								latencys.put(uniPort, temps);
								pipePorts.add(Integer.toString(multiPort));
								pipePorts.add(Integer.toString(uniPort));
								uniPort++;
							}
						}
					break;
					case "--multicast":
						hasMulticast = true;
						i++;
						if(Integer.parseInt(args[i]) > 0) {
							Integer multiPort = 1234;
							for(int j = 0; j < Integer.parseInt(args[i])-1; j++) {
								multicastPorts.add(Integer.toString(multiPort));
							}
						}
						break;
					case "--latency":
						getlatency=true;
					break;
				}
			}
			if(!getHelp)
			{
				if(hasPipes) {
					String[] pipePortsArray = new String[pipePorts.size()];
					String[] uniPortsArray = new String[unicastPorts.size()];
					pipePortsArray = pipePorts.toArray(pipePortsArray);
					uniPortsArray = unicastPorts.toArray(uniPortsArray);
					if(getlatency) {
						StressTestRelayPipe.main(pipePortsArray);
						StressTestServer.main(uniPortsArray, latencys);
					} else {
						StressTestRelayPipe.main(pipePortsArray);
						StressTestServer.main(uniPortsArray);
					}
				} else { //Default to increasing in size every 2 seconds
					(new Thread(new IncrementUpByOnePipe())).start();
				}
				
				if(hasMulticast) {
					String[] multiPortsArray = new String[multicastPorts.size()];
					multiPortsArray = multicastPorts.toArray(multiPortsArray);
					if(getlatency) {
						StressTestClient.main(multiPortsArray, latencys);
					} else {
						StressTestClient.main(multiPortsArray);
					}
					
				} else {
					(new Thread(new IncrementUpByOneMulticast())).start();
				}
			}
			
			if (getlatency) {
				(new Thread(new RunningAverage())).start();
			}
		}
	}
	
	private static class IncrementUpByOnePipe implements Runnable {
		Integer uniPort = 9090;
		Integer multiPort = 1234;
		String[] pipePortsArray = new String[1];
		ArrayList<String> pipeList = new ArrayList<String>();
		StressTestRelayPipe pipe = new StressTestRelayPipe(multiPort, uniPort);
		
		@Override
		public void run() {
			pipe.startRefreshLoop();
			pipeList.add(Integer.toString(uniPort));
			while(true) {
				pipePortsArray = pipeList.toArray(pipePortsArray);
				pipe.main(pipePortsArray);
				pipe.addPipe(multiPort++, uniPort++);
				pipeList.set(0, Integer.toString(uniPort));
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	
	private static class IncrementUpByOneMulticast implements Runnable {
		
		Integer numberOfPorts = 0;
		
		String[] multicastPortsArray = new String [1];
		ArrayList<String> multicastPorts = new ArrayList<String>();
		
		
		public IncrementUpByOneMulticast() {
			multicastPorts.add(Integer.toString(1234));
		}
		
		@Override
		public void run() {
			while(true) {
				numberOfPorts++;
				
				System.out.println("Multicast count: " + numberOfPorts);
				
				multicastPortsArray =  multicastPorts.toArray(multicastPortsArray);
				if (getlatency) {
					StressTestClient.main(multicastPortsArray, latencys);
				} else {
					StressTestClient.main(multicastPortsArray);
				}
				
				multicastPorts.set(0, Integer.toString(1234));
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static class RunningAverage implements Runnable {
		private Long average = new Long (0);
		private Long sum = new Long(0);
		private Long count = new Long(0);
		
		public Long getCount() {
			return count;
		}
		
		public Long getSum() {
			return sum;
		}

		public Long getAverage() {
			return average;
		}
		
		public void setAverage(Long average) {
			this.average = average;
		}

		@Override
		public void run() {
			
			while(true) {

				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				for (Latency latency : latencys.values()) {
					sum += latency.getLatency();
					count += latency.getCount();
				}
				
				setAverage(getSum()/getCount());
				
				System.out.println("Average Latency: " + getAverage());
			}
		}
	}
}
