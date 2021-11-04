/*
 * Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.aver.ptz.mock;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Mock UDP Server for testing
 *
 * @author Hieu.LeMinh
 * @version 1.0.0
 * @since 1.0.0
 */
public class UDPServer extends Thread{
	protected static final char[] hexArray = "0123456789ABCDEF".toCharArray();
	protected final Log logger = LogFactory.getLog(this.getClass());

	private DatagramSocket socket;
	private int port;

	/**
	 * Constructor for MockUDPServer to create a socket
	 */
	public UDPServer() {
		// Do nothing
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
		this.port = port;
	}

	/**
	 * This method is used to init server
	 */
	public void init() {
		try {
			socket = new DatagramSocket(this.port);
		} catch (SocketException ex) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("Error create UDP socket server", ex);
			}
		}

		if (this.logger.isTraceEnabled()) {
			this.logger.trace("Server started at port: " + this.port);
		}
	}

	/**
	 * This method is used to start listening at port to receive packet from client
	 */
	@Override
	public void run() {
		byte[] buffer = new byte[24];
		DatagramPacket request = new DatagramPacket(buffer, buffer.length);

		try {
			socket.receive(request);
		} catch (IOException ex) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("Error receive from port", ex);
			}
		}

		InetAddress clientAddress = request.getAddress();
		int clientPort = request.getPort();

		if (logger.isTraceEnabled()) {
			this.logger.trace("Packet Received from Ip Address:" + clientAddress + ", port:" + clientPort);
			this.logger.trace("Message Received: " + getHexByteString(request.getData()));
		}

		buffer = new byte[request.getLength()];
		System.arraycopy(request.getData(), request.getOffset(), buffer, 0, request.getLength());
		DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);

		try {
			socket.send(response);
		} catch (IOException ex) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("Error sending response", ex);
			}
		}
	}

	/**
	 * This method is used to destroy a channel actually destroy a socket
	 */
	public void destroy() {
		if (null != this.socket) {
			try {
				this.socket.close();
			} catch (Exception ex) {
				if (this.logger.isWarnEnabled()) {
					this.logger.warn("error seen on destroy server", ex);
				}
			}

			this.socket = null;
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
}
