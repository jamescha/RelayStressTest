package relay.test.runner;

import java.util.Date;

public class Latency {
		Long startTime;
		Long endTime;
		Long sum;
		Long count;
		
		public Latency() {
			sum = new Long(0);
			count = new Long(0);
		}
	
		public void setStartTime() {
			this.startTime = new Date().getTime();
		}
		
		public void setEndTime() {
			this.endTime = new Date().getTime();
			count++;
		}
		
		public Long getStartTime() {
			return startTime;
		}
		
		public long getEndTime() {
			return endTime;
		}
		
		public Long getLatency() {
			return (getEndTime() - getStartTime());
		}
		
		public Long getAverage() {
			sum += getLatency();
			return sum/count;
		}
		
		public Long getCount() {
			return count;
		}
}
