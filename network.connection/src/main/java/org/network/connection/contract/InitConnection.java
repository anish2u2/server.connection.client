package org.network.connection.contract;

public interface InitConnection {

	public void _init(String address, int port, int numberOfConnections);
	public void shutDown();
}
