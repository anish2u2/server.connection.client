package org.network.connection.factory.contracts;

import org.network.connection.contract.Reader;
import org.network.connection.contract.Writer;

public interface StreamFactory {

	public Writer getWriteStream();

	public Reader getReader();

}
