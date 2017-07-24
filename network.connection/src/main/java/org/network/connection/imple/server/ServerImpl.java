package org.network.connection.imple.server;

import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.PriorityQueue;

import org.logger.LoggerAPI;
import org.network.connection.abstracts.server.AbstractServer;
import org.network.connection.contract.Reader;
import org.network.connection.contract.RequestAware;
import org.network.connection.contract.Writer;
import org.network.connection.factory.contracts.StreamFactory;
import org.network.connection.factory.imple.StreamFactoryHandler;
import org.worker.multithread.contracts.ThreadUtility;
import org.worker.multithread.contracts.Work;
import org.worker.multithread.contracts.Worker;
import org.worker.multithread.thread.ThreadUtilityFactory;
import org.worker.multithread.thread.WorkerThread;

public class ServerImpl extends AbstractServer {

	private static final PriorityQueue<RequestAware> PRIORITY_QUEUE = new PriorityQueue<RequestAware>();

	private static StreamFactory streamFactory;

	static {
		streamFactory = StreamFactoryHandler.getInstance();
	}

	public Writer getWriter() throws Exception {
		ThreadUtility threadUtility = ThreadUtilityFactory.getInstance();
		RequestAware requestAware = threadUtility.get("currentThreadRequest") != null
				? (RequestAware) threadUtility.get("currentThreadRequest") : PRIORITY_QUEUE.poll();
		if (threadUtility.get("isRequestAwareObjectFetched") == null)
			synchronized (PRIORITY_QUEUE) {
				LoggerAPI.logInfo("Waiting for PRIORITY_QUEUE .. ");
				PRIORITY_QUEUE.wait();
				LoggerAPI.logInfo("PRIORITY_QUEUE has notified..");
				requestAware = PRIORITY_QUEUE.poll();
				threadUtility.add("currentThreadRequest", requestAware);
				threadUtility.add("isRequestAwareObjectFetched", true);
			}
		else {
			requestAware = (RequestAware) threadUtility.get("currentThreadRequest");
		}
		if (requestAware == null)
			requestAware = (RequestAware) threadUtility.get("currentThreadRequest");
		return requestAware.getRequestWriter();
	}

	public Reader getReader() throws Exception {
		ThreadUtility threadUtility = ThreadUtilityFactory.getInstance();
		RequestAware requestAware = threadUtility.get("currentThreadRequest") != null
				? (RequestAware) threadUtility.get("currentThreadRequest") : PRIORITY_QUEUE.poll();
		if (threadUtility.get("isRequestAwareObjectFetched") == null)
			synchronized (PRIORITY_QUEUE) {
				LoggerAPI.logInfo("Waiting for PRIORITY_QUEUE .. while fetching reader");
				PRIORITY_QUEUE.wait();
				LoggerAPI.logInfo("PRIORITY_QUEUE has notified.. while fetching reader");
				requestAware = PRIORITY_QUEUE.poll();
				threadUtility.add("currentThreadRequest", requestAware);
				threadUtility.add("isRequestAwareObjectFetched", true);
			}
		else {
			requestAware = (RequestAware) threadUtility.get("currentThreadRequest");
		}

		if (requestAware == null)
			requestAware = (RequestAware) threadUtility.get("currentThreadRequest");
		return requestAware.getRequestReader();
	}

	protected void addRequest(final Socket socket) {
		LoggerAPI.logInfo("Adding request to queue..");
		PRIORITY_QUEUE.add(new RequestAware() {
			private Writer writer;
			private Reader reader;
			private boolean notify;

			private Date requestTime = new Date();

			public void onRequest() {
				try {
					socket.setKeepAlive(true);
				} catch (SocketException e) {
					e.printStackTrace();
				}
				writer = streamFactory.getWriteStream();
				reader = streamFactory.getReader();
				writer.setSocket(socket);
				reader.setSocket(socket);
			}

			public Date getDate() {
				return requestTime;
			}

			public Writer getRequestWriter() {
				return this.writer;
			}

			public Reader getRequestReader() {
				return this.reader;
			}

			public boolean isNotifyCalled() {
				return notify;
			}

			public void notifyMe() {
				notify = true;

			}

			@Override
			public int compareTo(RequestAware o) {
				return (this.getDate().after(o.getDate())) == true ? -1 : 1;
			}

		});
		LoggerAPI.logInfo("request added to queue..:" + socket.getPort() + " ");
	}

	public void serveRequest() {
		LoggerAPI.logInfo("Calling serve request..");
		Worker worker = WorkerThread.getWorker();
		worker.startWorking(new Work() {
			public void doWork() {
				try {
					LoggerAPI.logInfo("Starting work of serv request..");
					while (true) {
						if (!PRIORITY_QUEUE.isEmpty()) {
							LoggerAPI.logInfo("request queue is not empty..");
							PRIORITY_QUEUE.peek().onRequest();
							synchronized (PRIORITY_QUEUE) {
								PRIORITY_QUEUE.notifyAll();
							}
							LoggerAPI.logInfo("All threads are notified for handling this request..");
						}
						Thread.sleep(700);
					}

				} catch (Exception ex) {
					LoggerAPI.logError(ex);
				}
			}
		});
		LoggerAPI.logInfo("Serv request started..");
	}

	@Override
	public void shutDown() {
		shutDownServer();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		PRIORITY_QUEUE.remove();
		streamFactory = null;
	}
}
