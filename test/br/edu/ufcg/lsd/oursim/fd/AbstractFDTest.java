package br.edu.ufcg.lsd.oursim.fd;

import junit.framework.Assert;

import org.junit.Test;

public class AbstractFDTest {

	@Test
	public void testSettersAndGetters() {
		SimpleFailureDetector fd = new SimpleFailureDetector();
		fd.registerMonitored("id", 0, 0, 0);
		
		fd.setTimeout("id", 100);
		Assert.assertEquals(100, fd.getTimeout("id").longValue());
		
		fd.setPingInterval("id", 100);
		Assert.assertEquals(100, fd.getTimeToNextPing("id", 0).longValue());
	}
	
	private class SimpleFailureDetector extends AbstractFailureDetector {
		@Override
		public boolean messageReceived(String id, long now, MessageType type) {
			return false;
		}

		@Override
		public boolean updatePingSample(String id, long lastPingTimestamp,
				long interArrivalMean, long interArrivalStdDev) {
			return false;
		}

		@Override
		public void registerMonitored(String id, long now, long timeout,
				long pingInterval) {
			Monitored m = new Monitored(id);
			addMonitored(m);
		}

		@Override
		public boolean isFailed(String id, long now) {
			return false;
		}
	}
}
