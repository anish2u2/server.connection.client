package org.network.connection.contract;

public interface RequestListener {

	public void processRequest(Reader reader, Writer writer);

}
