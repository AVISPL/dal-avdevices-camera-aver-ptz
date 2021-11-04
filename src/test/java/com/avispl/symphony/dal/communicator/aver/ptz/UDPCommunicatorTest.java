/*
 * Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.aver.ptz;

import java.util.Collections;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.avispl.symphony.dal.communicator.aver.ptz.mock.UDPServer;

/**
 * Unit test for UDP Communicator
 * Send and retrieve data success
 *
 * @author Hieu.LeMinh
 * @version 1.0.0
 * @since 1.0.0
 */
public class UDPCommunicatorTest {
	UDPCommunicator udpCommunicator;
	UDPServer UDPServer;

	@Before
	public void setUp() throws Exception {
		UDPServer = new UDPServer();
		UDPServer.setPort(52381);
		UDPServer.init();
		UDPServer.start();

		udpCommunicator = new UDPCommunicator();
		udpCommunicator.setHost("localhost");
		udpCommunicator.setPort(52381);
		udpCommunicator.setCommandErrorList(Collections.singletonList(""));
		udpCommunicator.setCommandSuccessList(Collections.singletonList(""));
		udpCommunicator.init();
		udpCommunicator.connect();
	}

	@After
	public void destroy() {
		udpCommunicator.destroy();
		UDPServer.destroy();
	}

	/**
	 * Test UDPCommunicator#send success
	 * Expect send and receive the same data from UDP server
	 */
	@Test
	public void testSendData() throws Exception {
		byte[] data = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x00, 0x02, (byte) 0xFF };
		byte[] response = udpCommunicator.send(data);

		Assert.assertArrayEquals(data, response);
	}
}
