package org.network.imple.client;

import org.network.abstracts.client.AbstractClient;
import org.network.contract.Reader;
import org.network.contract.Writer;
import org.network.factory.imple.StreamFactoryHandler;

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
