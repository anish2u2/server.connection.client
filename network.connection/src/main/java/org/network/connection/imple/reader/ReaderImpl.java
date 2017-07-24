package org.network.imple.reader;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;

import org.network.abstracts.reader.AbstractReader;
import org.network.contract.Reader.RESPONSE_TYPE;
import org.network.contract.StreamInitializer;

public class ReaderImpl extends AbstractReader {

	public Object read(RESPONSE_TYPE responseType) {
		Object response = null;

		try {
			if (stream == null || stream.get() == null)
				stream = new WeakReference<InputStream>(getSocket().getInputStream());
			if (RESPONSE_TYPE.INPUT_STREAM == responseType) {
				response = stream.get();
			} else if (RESPONSE_TYPE.STRING == responseType) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(stream.get()));
				response = reader.readLine();
			} else {
				ByteManipulator byteManipulator = new ByteManipulator();
				byte[] buffer = new byte[4096];
				while ((stream.get().read(buffer)) != -1) {
					LoggerAPI.logInfo("buffer:" + new String(buffer));
					byteManipulator.readBytes(buffer);
				}
				response = byteManipulator.getByteData();
			}
		} catch (Exception e) {
			LoggerAPI.logError(e);
		}

		return response;
	}

	public final class ByteManipulator {
		private StringBuffer buffer;

		public ByteManipulator() {
			buffer = new StringBuffer();
		}

		public void readBytes(byte[] data) {
			buffer.append(new String(data));
		}

		public byte[] getByteData() {
			return buffer.toString().getBytes();
		}

		public String getStringValue() {
			return buffer.toString();
		}
	}

	@Override
	public String getRequestAddress() {
		String address = getSocket().getLocalAddress().getCanonicalHostName();
		return address;
	}

	public InputStream getInputStream() {
		if (stream == null || stream.get() == null)
			try {
				stream = new WeakReference<InputStream>(getSocket().getInputStream());
			} catch (IOException e) {
				LoggerAPI.logError(e);
			}
		return stream.get();
	}

	@Override
	public void readFile(String dirPath) {
		try {
			DataInputStream stream = new DataInputStream(getInputStream());
			while (!isClosed()) {
				String fileName = stream.readUTF();
				if (fileName.equals(StreamInitializer.END_CONNECTION))
					return;
				Long fileSize = stream.readLong();
				LoggerAPI.logInfo("File Size:" + fileSize);
				FileOutputStream outputStream = new FileOutputStream(new File(FileUtility.getFolderForTheFileType(FileUtility.getFileExtension(fileName), dirPath), fileName));
				String content;
				while (!(content = stream.readUTF()).equalsIgnoreCase("\n")) {
					outputStream.write(Base64.decode(content));
				}
				outputStream.flush();
				outputStream.close();
				LoggerAPI.logInfo("File success fullly readed.");
			}
		} catch (Exception ex) {
			LoggerAPI.logError(ex);
		}
	}
}
