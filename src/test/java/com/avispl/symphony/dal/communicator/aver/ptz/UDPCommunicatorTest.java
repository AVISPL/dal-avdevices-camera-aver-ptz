/*
 * Copyright (c) 2015-2021 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.aver.ptz;

import java.util.Collections;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for UDP Communicator
 * Send and retrieve data success
 */
public class UDPCommunicatorTest {
	UDPCommunicator udpCommunicator = new UDPCommunicator();

	@Before
	public void setUp() throws Exception {
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
	}

	/**
	 * Test UDPCommunicator#send success
	 * Expect send and receive the same data from dummy server
	 */
	@Test
	public void testSendData() throws Exception {
		byte[] data = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x00, 0x02, (byte) 0xFF };

		try {
			byte[] response = udpCommunicator.send(data);
			Assert.assertArrayEquals(data, response);
		} catch (Exception e) {
			System.out.println(e);
			throw e;
		}

	}
}
