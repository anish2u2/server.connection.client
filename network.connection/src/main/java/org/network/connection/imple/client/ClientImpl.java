package org.network.connection.imple.client;

import org.network.connection.abstracts.client.AbstractClient;
import org.network.connection.contract.Reader;
import org.network.connection.contract.Writer;
import org.network.connection.factory.imple.StreamFactoryHandler;

public class ClientImpl extends AbstractClient {

	@Override
	public Writer getWriter() {
		Writer writer = StreamFactoryHandler.getInstance().getWriteStream();
		writer.setSocket(getSocket());
		return writer;
	}

	@Override
	public Reader getReader() {
		Reader reader = StreamFactoryHandler.getInstance().getReader();
		reader.setSocket(getSocket());
		return reader;
	}

	@Override
	public void shutDown() {
		shutDownClient();
	}

}
