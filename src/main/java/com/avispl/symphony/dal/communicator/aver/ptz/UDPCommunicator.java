package com.avispl.symphony.dal.communicator.aver.ptz;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.avispl.symphony.api.dal.dto.control.ConnectionState;
import com.avispl.symphony.api.dal.error.CommandFailureException;
import com.avispl.symphony.dal.BaseDevice;
import com.avispl.symphony.dal.communicator.Communicator;
import com.avispl.symphony.dal.communicator.ConnectionStatus;

public class UDPCommunicator extends BaseDevice implements Communicator {
	protected static final char[] hexArray = "0123456789ABCDEF".toCharArray();

	private DatagramSocket datagramSocket;
	private int port;
	private InetAddress address;
	private int timeout = 4000;
	private int bufferLength = 24;
	private List<String> commandErrorList;
	private List<String> commandSuccessList;

	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final ConnectionStatus status = new ConnectionStatus();

	/**
	 * Empty constructor
	 */
	public UDPCommunicator() {
		// Do nothing
	}

	/**
	 * This method returns the device UPD timeout
	 *
	 * @return int This returns the current UDP timeout.
	 */
	public int getTimeout() {
		return this.timeout;
	}

	/**
	 * This method is used set the device UDP timeout
	 *
	 * @param timeout This is the UDP timeout to set
	 */
	public void setTimeout(int timeout) {
		if (this.isInitialized()) {
			throw new IllegalStateException("Cannot change properties after init() was called");
		} else {
			this.timeout = timeout;
		}
	}

	/**
	 * This method returns the device UPD buffer length
	 *
	 * @return int This returns the current UDP buffer length.
	 */
	public int getBufferLength() {
		return this.bufferLength;
	}

	/**
	 * This method is used set the device UDP buffer length
	 *
	 * @param bufferLength This is the UDP buffer length to set
	 */
	public void setBufferLength(int bufferLength) {
		if (this.isInitialized()) {
			throw new IllegalStateException("Cannot change properties after init() was called");
		} else {
			this.bufferLength = bufferLength;
		}
	}

	/**
	 * This method returns the device UPD port
	 *
	 * @return int This returns the current UDP port.
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * This method is used set the device UDP port
	 *
	 * @param port This is the UDP port to set
	 */
	public void setPort(int port) {
		if (this.isInitialized()) {
			throw new IllegalStateException("Cannot change properties after init() was called");
		} else {
			this.port = port;
		}
	}

	/**
	 * This method returns the list of command error
	 *
	 * @return List<String> This returns the current list of command error
	 */
	public List<String> getCommandErrorList() {
		return this.commandErrorList;
	}

	/**
	 * This method is used set the list of command error
	 *
	 * @param commandErrorList This is the list of command error to set
	 */
	protected void setCommandErrorList(List<String> commandErrorList) {
		if (this.isInitialized()) {
			throw new IllegalStateException("Cannot change properties after init() was called");
		} else {
			this.commandErrorList = commandErrorList;
		}
	}

	/**
	 * This method returns the list of command success
	 *
	 * @return List<String> This returns the current list of command success
	 */
	public List<String> getCommandSuccessList() {
		return this.commandSuccessList;
	}

	/**
	 * This method is used set the list of command success
	 *
	 * @param commandSuccessList This is the list of command success to set
	 */
	protected void setCommandSuccessList(List<String> commandSuccessList) {
		if (this.isInitialized()) {
			throw new IllegalStateException("Cannot change properties after init() was called");
		} else {
			this.commandSuccessList = commandSuccessList;
		}
	}

	/**
	 * This method is used to create a connection
	 */
	@Override
	public void connect() {
		if (!this.isInitialized()) {
			throw new IllegalStateException("UDPCommunicator cannot be used before init() is called");
		} else {
			if (this.logger.isTraceEnabled()) {
				this.logger.trace("Connecting to: " + this.host + " port: " + this.port);
			}

			Lock writeLock = this.lock.writeLock();
			writeLock.lock();

			try {
				if (!this.isChannelConnected()) {
					this.createChannel();
					this.status.setLastTimestamp(System.currentTimeMillis());
					this.status.setConnectionState(ConnectionState.Connected);
					this.status.setLastError(null);
				}
			} catch (Exception ex) {
				if (this.logger.isErrorEnabled()) {
					this.logger.error("Error connecting to: " + this.host + " port: " + this.port, ex);
				}

				this.status.setLastError(ex);
				this.status.setConnectionState(ConnectionState.Failed);
				this.destroyChannel();
				throw ex;
			} finally {
				writeLock.unlock();
			}
		}
	}

	/**
	 * This method is used to disconnect from the device
	 */
	@Override
	public void disconnect() {
		if (this.logger.isTraceEnabled()) {
			this.logger.trace("Disconnecting from: " + this.host + " port: " + this.port);
		}

		Lock writeLock = this.lock.writeLock();
		writeLock.lock();

		try {
			this.destroyChannel();
			this.status.setConnectionState(ConnectionState.Disconnected);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * This method is used to get current connection status from the device
	 */
	@Override
	public ConnectionStatus getConnectionStatus() {
		Lock readLock = this.lock.readLock();
		readLock.lock();

		ConnectionStatus status;
		try {
			status = this.status.copyOf();
		} finally {
			readLock.unlock();
		}

		return status;
	}

	/**
	 * This method is used to create a channel actually create a socket
	 */
	private void createChannel() {
		try {
			if (this.datagramSocket == null || this.datagramSocket.isClosed() || !this.datagramSocket.isConnected()) {
				this.address = InetAddress.getByName(this.host);
				this.datagramSocket = new DatagramSocket();
				this.datagramSocket.setSoTimeout(this.timeout);
			}

		} catch (IOException ex) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("Error create UDP socket channel", ex);
			}
		}
	}

	/**
	 * This method is used to destroy a channel actually destroy a socket
	 */
	public void destroyChannel() {
		if (null != this.datagramSocket) {
			try {
				if (this.datagramSocket.isConnected()) {
					this.datagramSocket.close();
				}
			} catch (Exception ex) {
				if (this.logger.isWarnEnabled()) {
					this.logger.warn("error seen on destroyChannel", ex);
				}
			}

			this.datagramSocket = null;
		}

	}

	/**
	 * This method is used to check if a channel is connected or not
	 */
	private boolean isChannelConnected() {
		return null != this.datagramSocket && this.datagramSocket.isConnected();
	}

	/**
	 * This method is used to send a command to a device
	 *
	 * @param data This is the data to be sent
	 * @return byte[] This returns the reply received from the device.
	 */
	protected byte[] send(byte[] data) throws Exception {
		if (!this.isInitialized()) {
			throw new IllegalStateException("ShellCommunicator cannot be used before init() is called");
		} else if (null == data) {
			throw new IllegalArgumentException("Send data is null");
		} else {
			if (this.logger.isTraceEnabled()) {
				this.logger.trace("Sending command: " + getHexByteString(data) + " to: " + this.host + " port: " + this.port);
			}

			Lock writeLock = this.lock.writeLock();
			writeLock.lock();

			byte[] response;
			try {
				response = this.send(data, true);
			} finally {
				writeLock.unlock();
			}

			return response;
		}
	}

	/**
	 * This method is used to generate a string from a byte array
	 *
	 * @param bytes This is the byte array to convert to a String
	 * @return String This returns the generated String.
	 */
	public static String getHexByteString(byte[] bytes) {
		return getHexByteString(null, ",", null, bytes);
	}

	public static String getHexByteString(CharSequence prefix, CharSequence separator, CharSequence suffix, byte[] bytes) {
		byte[] data = new byte[bytes.length];
		System.arraycopy(bytes, 0, data, 0, bytes.length);

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < data.length; ++i) {
			if (i > 0) {
				sb.append(separator);
			}

			int v = data[i] & 255;
			if (prefix != null) {
				sb.append(prefix);
			}

			sb.append(hexArray[v >> 4]);
			sb.append(hexArray[v & 15]);
			if (suffix != null) {
				sb.append(suffix);
			}
		}

		return sb.toString();
	}

	private byte[] send(byte[] data, boolean retryOnError) throws Exception {
		try {
			if (!this.isChannelConnected()) {
				this.createChannel();
				this.status.setLastTimestamp(System.currentTimeMillis());
				this.status.setConnectionState(ConnectionState.Connected);
				this.status.setLastError(null);
			}

			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Sending: " + getHexByteString(data) + " to: " + this.host + " port: " + this.port);
			}

			byte[] response = this.internalSend(data);

			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Received response: " + getHexByteString(response) + " from: " + this.host + " port: " + this.port);
			}

			if (this.logger.isTraceEnabled()) {
				this.logger.trace("Received response: " + getHexByteString(response) + " from: " + this.host + " port: " + this.port);
			}

			this.status.setLastTimestamp(System.currentTimeMillis());
			return response;
		} catch (CommandFailureException ex1) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("Command failed " + getHexByteString(data) + " to: " + this.host + " port: " + this.port + " connection state: " + this.status.getConnectionState(), ex1);
			}

			this.status.setLastTimestamp(System.currentTimeMillis());
			throw ex1;
		} catch (SocketTimeoutException ex2) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug(
						"Time out while sending command: " + getHexByteString(data) + " to: " + this.host + " port: " + this.port + " connection state: " + this.status.getConnectionState() + " error: ", ex2);
			}
			this.status.setLastError(ex2);
			this.status.setConnectionState(ConnectionState.Unknown);
			this.destroyChannel();
			if (retryOnError) {
				return this.send(data, false);
			} else {
				throw ex2;
			}
		} catch (Exception ex3) {
			if (ex3 instanceof InterruptedException) {
				if (this.logger.isDebugEnabled()) {
					this.logger.debug(
							"Interrupted while sending command: " + getHexByteString(data) + " to: " + this.host + " port: " + this.port + " connection state: " + this.status.getConnectionState() + " error: ",
							ex3);
				}
			} else if (this.logger.isErrorEnabled()) {
				this.logger.error("Error sending command: " + getHexByteString(data) + " to: " + this.host + " port: " + this.port + " connection state: " + this.status.getConnectionState() + " error: ",
						ex3);
			}

			this.status.setLastError(ex3);
			this.status.setConnectionState(ConnectionState.Failed);
			this.destroyChannel();
			if (retryOnError) {
				return this.send(data, false);
			} else {
				throw ex3;
			}
		}
	}

	private byte[] internalSend(byte[] outputData) throws Exception {
		DatagramPacket request = new DatagramPacket(outputData, outputData.length, this.address, this.port);
		this.write(request);

		return this.read(outputData);
	}

	private void write(DatagramPacket request) throws Exception {
		this.datagramSocket.send(request);
	}

	protected byte[] read(byte[] command) throws Exception {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("DEBUG - UDP Communicator reading after command text \"" + getHexByteString(command) + "\" was sent to host " + this.host);
		}

		byte[] buffer = new byte[this.bufferLength];
		DatagramPacket response = new DatagramPacket(buffer, buffer.length);
		this.datagramSocket.receive(response);
		return response.getData();
	}

	protected boolean doneReading(String command, String response) throws CommandFailureException {
		Iterator<String> iterator = this.commandErrorList.iterator();

		String string;
		do {
			if (!iterator.hasNext()) {
				iterator = this.commandSuccessList.iterator();

				do {
					if (!iterator.hasNext()) {
						return false;
					}

					string = iterator.next();
				} while (!response.endsWith(string));

				if (this.logger.isTraceEnabled()) {
					this.logger.trace("Done reading, found success string: " + string + " from: " + this.host + " port: " + this.port);
				}

				return true;
			}

			string = iterator.next();
		} while (!response.endsWith(string));

		if (this.logger.isTraceEnabled()) {
			this.logger.trace("Done reading, found error string: " + string + " from: " + this.host + " port: " + this.port);
		}

		throw new CommandFailureException(this.host, command, response);
	}

	@Override
	protected void internalDestroy() {
		if (this.logger.isTraceEnabled()) {
			this.logger.trace("Destroying communication channel to: " + this.host + " port: " + this.port);
		}

		this.destroyChannel();
		this.status.setConnectionState(ConnectionState.Disconnected);
		super.internalDestroy();
	}

	@Override
	protected void internalInit() throws Exception {
		super.internalInit();

		if (null != this.datagramSocket) {
			this.destroyChannel();
		}

		if (this.port <= 0) {
			throw new IllegalStateException("Invalid port property: " + this.port + " (must be positive number)");
		} else if (null != this.commandSuccessList && !this.commandSuccessList.isEmpty()) {
			if (null == this.commandErrorList || this.commandErrorList.isEmpty()) {
				throw new IllegalStateException("Invalid commandErrorList property (must be non-empty list)");
			}
		} else {
			throw new IllegalStateException("Invalid commandSuccessList property (must be non-empty list)");
		}
	}
}