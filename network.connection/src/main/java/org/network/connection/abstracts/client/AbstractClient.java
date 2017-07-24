package org.network.connection.abstracts.client;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

import org.logger.LoggerAPI;
import org.network.connection.abstracts.common.AbstractInitConnection;
import org.network.connection.contract.Client;
import org.network.connection.contract.IpAddressDetail;

public abstract class AbstractClient extends AbstractInitConnection implements Client {

	private WeakReference<Socket> clientSocket;

	@Override
	protected void initialize(String address, int port, int numberOfConnections) {
		Socket client = null;
		try {
			client = new Socket(address, port);
		} catch (Exception ex) {
			try {
				client = new Socket(InetAddress.getByName(address), port);
			} catch (IOException e) {
				try {
					client = new Socket(InetAddress.getByAddress(address.getBytes()), port);
				} catch (IOException e1) {
					LoggerAPI.logError(e1);
				}
				LoggerAPI.logError(e);
			}
			LoggerAPI.logError(ex);
		}
		clientSocket = new WeakReference<Socket>(client);

	}

	protected Socket getSocket() {
		return clientSocket.get();
	}

	protected void shutDownClient() {
		try {
			this.clientSocket.get().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<IpAddressDetail> getActiveAddress(boolean blockingCall) {
		try {
			// --Commenting these codes as we know that private network always
			// found on 192.168.xxx.xxx ips.
			/*
			 * String siteLocalAddress = null; for
			 * (@SuppressWarnings("rawtypes") Enumeration networkInterface =
			 * NetworkInterface.getNetworkInterfaces(); networkInterface
			 * .hasMoreElements();) { NetworkInterface network =
			 * (NetworkInterface) networkInterface.nextElement(); for
			 * (@SuppressWarnings("rawtypes") Enumeration inetAddress =
			 * network.getInetAddresses(); inetAddress.hasMoreElements();) {
			 * InetAddress address = (InetAddress) inetAddress.nextElement();
			 * LoggerAPI.logInfo("---------------------------------");
			 * LoggerAPI.logInfo("Host address:" + address.getHostAddress());
			 * LoggerAPI.logInfo("Is loop back address:" +
			 * address.isLoopbackAddress()); LoggerAPI.logInfo(
			 * "Is Site Local back address:" + address.isSiteLocalAddress());
			 * LoggerAPI.logInfo("Is Link Local back address:" +
			 * address.isLinkLocalAddress()); LoggerAPI.logInfo(
			 * "Is Any Local back address:" + address.isAnyLocalAddress());
			 * LoggerAPI.logInfo("---------------------------------"); if
			 * (address.isSiteLocalAddress() &&
			 * address.getHostAddress().startsWith(PRIVATE_IP_PREFIX)) {
			 * LoggerAPI.logInfo("Found Private-IP:" + address.getHostName());
			 * siteLocalAddress = address.getHostAddress(); break; } } if
			 * (siteLocalAddress != null) break; }
			 */
			return findIpOnThisNetwork(blockingCall);
		} catch (Exception ex) {
			LoggerAPI.logError(ex);
		}
		return null;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		clientSocket = null;
	}
}
