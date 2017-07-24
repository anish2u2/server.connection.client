package org.network.factory.abstracts;

import java.lang.ref.WeakReference;

import org.network.contract.Client;
import org.network.contract.Server;
import org.network.contract.Wifi;

public abstract class AbstractWifi implements Wifi {

	private WeakReference<Server> serverRef;
	private WeakReference<Client> clientRef;

	protected WeakReference<Server> getServerRef() {
		return serverRef;
	}

	protected void setServerRef(Server serverRef) {
		this.serverRef = new WeakReference<Server>(serverRef);
	}

	protected WeakReference<Client> getClientRef() {
		return clientRef;
	}

	protected void setClientRef(Client clientRef) {
		this.clientRef = new WeakReference<Client>(clientRef);
	}

	@Override
	public void connect(String address, int port, int numberOfCOnnections) throws Exception {
		if (getServer() != null)
			getServer()._init(address, port, numberOfCOnnections);
		if (getClient() != null)
			getClient()._init(address, port, numberOfCOnnections);

	}

	@Override
	public void disconnect() throws Exception {
		if (getServer() != null)
			getServer().shutDown();
		if (getClient() != null)
			getClient().shutDown();
	}

}
