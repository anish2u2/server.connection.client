package org.network.factory.contracts;

import org.network.contract.Reader;
import org.network.contract.Writer;

public interface StreamFactory {

	public Writer getWriteStream();

	public Reader getReader();

}
