/*
 * Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.aver.ptz;

import java.util.Collections;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.avispl.symphony.dal.communicator.aver.ptz.interfaces.DevelopmentTest;

/**
 * Unit test for UDP Communicator
 * Send and retrieve data success
 *
 * @author Harry
 * @version 1.0
 * @since 1.0
 */
public class UDPCommunicatorTest {
	UDPCommunicator udpCommunicator;

	@Before
	public void setUp() throws Exception {
		udpCommunicator = new UDPCommunicator();
		udpCommunicator.setHost("***REMOVED***");
		udpCommunicator.setPort(52381);
		udpCommunicator.setCommandErrorList(Collections.singletonList(""));
		udpCommunicator.setCommandSuccessList(Collections.singletonList(""));
		udpCommunicator.init();
		udpCommunicator.connect();
	}

	@After
	public void destroy() {
		udpCommunicator.disconnect();
	}

	/**
	 * Test UDPCommunicator#send success
	 * Expect send inquiry command and receive success reply packet from UDP server
	 */
	@Test
	@Category(DevelopmentTest.class)
	public void testSendData() throws Exception {
		byte[] data = new byte[] { 0x01, 0x10, 0x00, 0x05, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x09, 0x04, 0x00, (byte) 0xFF };
		byte[] expectedResponse = new byte[] { 0x01, 0x11, 0x00, 0x04, 0x00, 0x00, 0x00, 0x01, (byte) 0x90, 0x50, 0x02, (byte) 0xFF };
		byte[] actualResponse = udpCommunicator.send(data);

		Assert.assertArrayEquals(expectedResponse, actualResponse);
	}
}
