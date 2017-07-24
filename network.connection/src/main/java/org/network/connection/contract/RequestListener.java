package org.network.contract;

public interface RequestListener {

	public void processRequest(Reader reader, Writer writer);

}
