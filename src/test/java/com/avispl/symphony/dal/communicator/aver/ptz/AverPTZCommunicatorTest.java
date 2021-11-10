/*
 * Copyright (c) 2021 AVI-SPL Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.aver.ptz;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.avispl.symphony.dal.communicator.aver.ptz.enums.ReplyPacket;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.command.Command;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.command.CommandType;

/**
 * Unit test for AverPTZ Communicator
 * Send success packet by override method read and doneReading
 *
 * @author Harry
 * @version 1.0
 * @since 1.0
 */
public class AverPTZCommunicatorTest {
	AverPTZCommunicator averPTZCommunicator;

	@Before
	public void setUp() throws Exception {
		averPTZCommunicator = new AverPTZCommunicator();
		averPTZCommunicator.setHost("172.31.254.204");
		averPTZCommunicator.setPort(52381);
		averPTZCommunicator.setLogin("root");
		averPTZCommunicator.setPassword("1234");
		averPTZCommunicator.init();
		averPTZCommunicator.connect();
	}

	@After
	public void destroy() {
		averPTZCommunicator.disconnect();
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect not throw exception
	 */
	@Test
	public void testDigestResponseControlCommand() {
		String status = (String) averPTZCommunicator.digestResponse(ReplyPacket.COMPLETION.getCode(), 1, CommandType.COMMAND, null);
		Assert.assertNull(status);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect status power on
	 */
	@Test
	public void testDigestResponseInquiryCommandPowerOn() {
		String status = (String) averPTZCommunicator.digestResponse(ReplyPacket.POWER_ON.getCode(), 1, CommandType.INQUIRY, Command.POWER.getName());
		Assert.assertEquals("On", status);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect status power off
	 */
	@Test
	public void testDigestResponseInquiryCommandPowerOff() {
		String status = (String) averPTZCommunicator.digestResponse(ReplyPacket.POWER_OFF.getCode(), 1, CommandType.INQUIRY, Command.POWER.getName());
		Assert.assertEquals("Off", status);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect auto focus mode
	 */
	@Test
	public void testDigestResponseInquiryCommandAutoFocus() {
		String mode = (String) averPTZCommunicator.digestResponse(ReplyPacket.FOCUS_AUTO_MODE.getCode(), 1, CommandType.INQUIRY, Command.FOCUS_MODE.getName());
		Assert.assertEquals("Auto", mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect manual focus mode
	 */
	@Test
	public void testDigestResponseInquiryCommandManualFocus() {
		String mode = (String) averPTZCommunicator.digestResponse(ReplyPacket.FOCUS_MANUAL_MODE.getCode(), 1, CommandType.INQUIRY, Command.FOCUS_MODE.getName());
		Assert.assertEquals("Manual", mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect AE full auto mode
	 */
	@Test
	public void testDigestResponseInquiryCommandAEFullAutoMode() {
		String mode = (String) averPTZCommunicator.digestResponse(ReplyPacket.AE_FULL_AUTO_MODE.getCode(), 1, CommandType.INQUIRY, Command.AE_MODE.getName());
		Assert.assertEquals("FullAuto", mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect AE manual mode
	 */
	@Test
	public void testDigestResponseInquiryCommandAEManualMode() {
		String mode = (String) averPTZCommunicator.digestResponse(ReplyPacket.AE_MANUAL_MODE.getCode(), 1, CommandType.INQUIRY, Command.AE_MODE.getName());
		Assert.assertEquals("Manual", mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect AE shutter priority mode
	 */
	@Test
	public void testDigestResponseInquiryCommandAEShutterPriorityMode() {
		String mode = (String) averPTZCommunicator.digestResponse(ReplyPacket.AE_SHUTTER_PRIORITY_MODE.getCode(), 1, CommandType.INQUIRY, Command.AE_MODE.getName());
		Assert.assertEquals("ShutterPriority", mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect AE full auto mode
	 */
	@Test
	public void testDigestResponseInquiryCommandAEIrisPriorityMode() {
		String mode = (String) averPTZCommunicator.digestResponse(ReplyPacket.AE_IRIS_PRIORITY_MODE.getCode(), 1, CommandType.INQUIRY, Command.AE_MODE.getName());
		Assert.assertEquals("IrisPriority", mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect status auto slow shutter on
	 */
	@Test
	public void testDigestResponseInquiryCommandSlowShutterOn() {
		String status = (String) averPTZCommunicator.digestResponse(ReplyPacket.AUTO_SLOW_SHUTTER_ON.getCode(), 1, CommandType.INQUIRY, Command.AUTO_SLOW_SHUTTER.getName());
		Assert.assertEquals("On", status);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect status auto slow shutter off
	 */
	@Test
	public void testDigestResponseInquiryCommandSlowShutterOff() {
		String status = (String) averPTZCommunicator.digestResponse(ReplyPacket.AUTO_SLOW_SHUTTER_OFF.getCode(), 1, CommandType.INQUIRY, Command.AUTO_SLOW_SHUTTER.getName());
		Assert.assertEquals("Off", status);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect shutter speed value is 15
	 */
	@Test
	public void testDigestResponseInquiryCommandShutterSpeed() {
		int value = (int) averPTZCommunicator.digestResponse(ReplyPacket.SHUTTER_SPEED.getCode(), 1, CommandType.INQUIRY, Command.SHUTTER_DIRECT.getName());
		Assert.assertEquals(15, value);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect iris level value is 13
	 */
	@Test
	public void testDigestResponseInquiryCommandIrisLevel() {
		int value = (int) averPTZCommunicator.digestResponse(ReplyPacket.IRIS_LEVEL.getCode(), 1, CommandType.INQUIRY, Command.IRIS_DIRECT.getName());
		Assert.assertEquals(13, value);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect gain level value is 45
	 */
	@Test
	public void testDigestResponseInquiryCommandGainLevel() {
		int value = (int) averPTZCommunicator.digestResponse(ReplyPacket.GAIN_LEVEL.getCode(), 1, CommandType.INQUIRY, Command.GAIN_DIRECT.getName());
		Assert.assertEquals(45, value);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect RGain value is 33
	 */
	@Test
	public void testDigestResponseInquiryCommandRGainValue() {
		int value = (int) averPTZCommunicator.digestResponse(ReplyPacket.RGAIN.getCode(), 1, CommandType.INQUIRY, Command.RGAIN.getName());
		Assert.assertEquals(33, value);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect BGain value is 52
	 */
	@Test
	public void testDigestResponseInquiryCommandBGainValue() {
		int value = (int) averPTZCommunicator.digestResponse(ReplyPacket.BGAIN.getCode(), 1, CommandType.INQUIRY, Command.BGAIN.getName());
		Assert.assertEquals(52, value);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect gain limit level value is 8
	 */
	@Test
	public void testDigestResponseInquiryCommandGainLimitLevel() {
		int value = (int) averPTZCommunicator.digestResponse(ReplyPacket.GAIN_LIMIT_LEVEL.getCode(), 1, CommandType.INQUIRY, Command.GAIN_LIMIT_DIRECT.getName());
		Assert.assertEquals(8, value);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect exposure value is 9
	 */
	@Test
	public void testDigestResponseInquiryCommandExposureValue() {
		int value = (int) averPTZCommunicator.digestResponse(ReplyPacket.EXPOSURE_VALUE.getCode(), 1, CommandType.INQUIRY, Command.EXP_COMP_DIRECT.getName());
		Assert.assertEquals(9, value);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect last preset recalled value is 2
	 */
	@Test
	public void testDigestResponseInquiryCommandLastPresetRecalled() {
		int value = (int) averPTZCommunicator.digestResponse(ReplyPacket.LAST_PRESET_RECALLED.getCode(), 1, CommandType.INQUIRY, Command.PRESET.getName());
		Assert.assertEquals(2, value);
	}
}
