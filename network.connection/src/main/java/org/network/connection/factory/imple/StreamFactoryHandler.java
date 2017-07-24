package org.network.factory.imple;

import org.network.factory.abstracts.AbstractStreamFactory;
import org.network.factory.contracts.StreamFactory;

public class StreamFactoryHandler extends AbstractStreamFactory {

	private static StreamFactory streamFactory;

	private StreamFactoryHandler() {

	}

	public static StreamFactory getInstance() {
		if (streamFactory == null) {
			streamFactory = new StreamFactoryHandler();
		}

		return streamFactory;
	}

}
