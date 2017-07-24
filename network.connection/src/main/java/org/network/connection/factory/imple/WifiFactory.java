package org.network.connection.factory.imple;

import org.logger.LoggerAPI;
import org.network.connection.contract.Client;
import org.network.connection.contract.Server;
import org.network.connection.contract.Wifi;
import org.network.connection.factory.abstracts.AbstractWifi;
import org.network.connection.imple.client.ClientImpl;
import org.network.connection.imple.server.ServerImpl;
import org.worker.multithread.contracts.Work;
import org.worker.multithread.thread.WorkerThread;

public class WifiFactory extends AbstractWifi {

	private static Wifi wifi;

	private WifiFactory() {
		startMemoryMonitorThread();
	}

	public static Wifi getInstance() {
		if (wifi == null) {
			wifi = new WifiFactory();
		}
		return wifi;
	}

	@Override
	public Server getServer() {
		if (getServerRef() == null) {
			setServerRef(new ServerImpl());
		}
		return getServerRef().get();
	}

	@Override
	public Client getClient() {
		if (getClientRef() == null)
			setClientRef(new ClientImpl());
		return getClientRef().get();
	}

	private void startMemoryMonitorThread() {
		WorkerThread.getWorker().startWorking(new Work() {
			public void doWork() {
				while (true) {
					Runtime runtime = Runtime.getRuntime();
					try {
						if (((runtime.totalMemory() - runtime.freeMemory()) / 1000000) > 10) {
							LoggerAPI.logInfo(
									"Total Memory:" + (runtime.totalMemory() / 1000000) + " M.B." + " free Memory:"
											+ (runtime.freeMemory() / 1000000) + " M.B. " + " memory consumed by app:"
											+ ((runtime.totalMemory() - runtime.freeMemory()) / 1000000) + " M.B.");
							runtime.gc();
						}
						Thread.sleep(100000);
					} catch (InterruptedException e) {
						LoggerAPI.logError(e);
					}
				}

			}
		});
	}

}
