package org.network.abstracts.common;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.server.client.contract.InitConnection;
import org.server.client.contract.IpAddressDetail;
import org.server.client.contract.Work;
import org.server.client.logger.LoggerAPI;
import org.server.client.thread.ThreadUtilityFactory;
import org.server.client.thread.WorkerThread;

public abstract class AbstractInitConnection implements InitConnection {

	protected final static String PRIVATE_IP_PREFIX = "192.168.0.";

	public void _init(String address, int port, int numberOfConnections) {
		initialize(address, port, numberOfConnections);
	}

	protected abstract void initialize(String address, int port, int numberOfConnections);

	public List<IpAddressDetail> findIpOnThisNetwork(boolean blockingCall) {
		List<IpAddressDetail> listOfActiveIpOnThisNetwork = new LinkedList<IpAddressDetail>();
		try {
			/*
			 * String systemIp = siteLocalAddress; for (int counter =
			 * systemIp.length(); counter > 0; --counter) { if
			 * (systemIp.charAt(counter - 1) == '.') { systemIp =
			 * systemIp.substring(0, counter); break; } }
			 */
			startThreadToFindReachableNetwork(PRIVATE_IP_PREFIX, listOfActiveIpOnThisNetwork, blockingCall);
		} catch (Exception ex) {
			LoggerAPI.logError(ex);
		}
		return listOfActiveIpOnThisNetwork;
	}

	public void startThreadToFindReachableNetwork(final String systemIp, final List<IpAddressDetail> ipLists,
			boolean blockingCall) {

		final Map<Object, Object> threadLocalMap = ThreadUtilityFactory.getInstance().getMap();
		WorkerThread.getWorker().startWorking(new Work() {

			@Override
			public void doWork() {
				getActiveIPs(0, 254 / 2, systemIp, ipLists);
				threadLocalMap.put("firstWorkerDone", true);
			}
		});
		WorkerThread.getWorker().startWorking(new Work() {

			@Override
			public void doWork() {
				getActiveIPs(254 / 2, 254, systemIp, ipLists);
				threadLocalMap.put("secondWorkerDone", true);
			}
		});
		while (blockingCall) {
			try {
				if (threadLocalMap.get("firstWorkerDone") != null && threadLocalMap.get("secondWorkerDone") != null
						&& (boolean) threadLocalMap.get("firstWorkerDone")
						&& (boolean) threadLocalMap.get("secondWorkerDone")) {
					break;
				} else
					Thread.sleep(1000);
			} catch (Exception ex) {
				LoggerAPI.logError(ex);
			}
		}
	}

	public List<IpAddressDetail> getActiveIPs(int counterInitValue, int counterLimit, String systemIp,
			List<IpAddressDetail> ipLists)

	{
		List<String> listOfIpsOfThisMachine = new ArrayList<String>();
		try {
			InetAddress[] inetAddress = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
			for (InetAddress address : inetAddress) {
				listOfIpsOfThisMachine.add(address.getHostName());
			}
		} catch (Exception ex) {
			LoggerAPI.logError(ex);
		}
		try {
			for (int counter = counterInitValue; counter <= counterLimit; counter++) {
				InetAddress address = InetAddress.getByName((systemIp + new Integer(counter)).toString());
				if (!listOfIpsOfThisMachine.contains(address.getHostAddress()) && address.isReachable(800)) {
					LoggerAPI.logInfo("Name:" + address.getHostName() + "Address :" + address.getHostAddress()
							+ " is reachable.");
					IpAddressDetail ipDetail = new IpAddressDetail() {
						private String name;
						private String ipAdress;

						@Override
						public String getName() {
							return name;
						}

						@Override
						public String getIpAddress() {
							return ipAdress;
						}

						public void setName(String name) {
							this.name = name;
						}

						public void setIpAddress(String ipAddress) {
							this.ipAdress = ipAddress;
						}
					};
					ipDetail.setIpAddress(address.getHostAddress());
					ipDetail.setName(address.getHostName());
					ipLists.add(ipDetail);
					Thread.sleep(10);
				}
			}
		} catch (Exception ex) {
			LoggerAPI.logError(ex);
		}
		return ipLists;
	}
}
