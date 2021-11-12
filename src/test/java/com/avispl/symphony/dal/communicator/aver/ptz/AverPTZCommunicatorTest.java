/*
 * Copyright (c) 2021 AVI-SPL Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.aver.ptz;

import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.HASH;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZUtils.buildSendPacket;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZUtils.convertOneByteNumberToTwoBytesArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.error.CommandFailureException;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.ReplyPacket;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.StatisticsProperty;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.Category;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.PayloadType;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.command.Command;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.command.CommandType;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.AEMode;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.FocusControl;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.FocusMode;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.PowerStatus;

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
	ExtendedStatistics extendedStatistic;
	List<AdvancedControllableProperty> advancedControllableProperties;
	Map<String, String> stats;

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

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
		String status = (String) averPTZCommunicator.digestResponse(ReplyPacket.POWER_ON.getCode(), 1, CommandType.INQUIRY, Command.POWER);
		Assert.assertEquals("On", status);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect status power off
	 */
	@Test
	public void testDigestResponseInquiryCommandPowerOff() {
		String status = (String) averPTZCommunicator.digestResponse(ReplyPacket.POWER_OFF.getCode(), 1, CommandType.INQUIRY, Command.POWER);
		Assert.assertEquals("Off", status);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect auto focus mode
	 */
	@Test
	public void testDigestResponseInquiryCommandAutoFocus() {
		String mode = (String) averPTZCommunicator.digestResponse(ReplyPacket.FOCUS_AUTO_MODE.getCode(), 1, CommandType.INQUIRY, Command.FOCUS_MODE);
		Assert.assertEquals("Auto", mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect manual focus mode
	 */
	@Test
	public void testDigestResponseInquiryCommandManualFocus() {
		String mode = (String) averPTZCommunicator.digestResponse(ReplyPacket.FOCUS_MANUAL_MODE.getCode(), 1, CommandType.INQUIRY, Command.FOCUS_MODE);
		Assert.assertEquals("Manual", mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect AE full auto mode
	 */
	@Test
	public void testDigestResponseInquiryCommandAEFullAutoMode() {
		String mode = (String) averPTZCommunicator.digestResponse(ReplyPacket.AE_FULL_AUTO_MODE.getCode(), 1, CommandType.INQUIRY, Command.AE_MODE);
		Assert.assertEquals("FullAuto", mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect AE manual mode
	 */
	@Test
	public void testDigestResponseInquiryCommandAEManualMode() {
		String mode = (String) averPTZCommunicator.digestResponse(ReplyPacket.AE_MANUAL_MODE.getCode(), 1, CommandType.INQUIRY, Command.AE_MODE);
		Assert.assertEquals("Manual", mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect AE shutter priority mode
	 */
	@Test
	public void testDigestResponseInquiryCommandAEShutterPriorityMode() {
		String mode = (String) averPTZCommunicator.digestResponse(ReplyPacket.AE_SHUTTER_PRIORITY_MODE.getCode(), 1, CommandType.INQUIRY, Command.AE_MODE);
		Assert.assertEquals("ShutterPriority", mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect AE full auto mode
	 */
	@Test
	public void testDigestResponseInquiryCommandAEIrisPriorityMode() {
		String mode = (String) averPTZCommunicator.digestResponse(ReplyPacket.AE_IRIS_PRIORITY_MODE.getCode(), 1, CommandType.INQUIRY, Command.AE_MODE);
		Assert.assertEquals("IrisPriority", mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect status auto slow shutter on
	 */
	@Test
	public void testDigestResponseInquiryCommandSlowShutterOn() {
		String status = (String) averPTZCommunicator.digestResponse(ReplyPacket.AUTO_SLOW_SHUTTER_ON.getCode(), 1, CommandType.INQUIRY, Command.AUTO_SLOW_SHUTTER);
		Assert.assertEquals("On", status);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect status auto slow shutter off
	 */
	@Test
	public void testDigestResponseInquiryCommandSlowShutterOff() {
		String status = (String) averPTZCommunicator.digestResponse(ReplyPacket.AUTO_SLOW_SHUTTER_OFF.getCode(), 1, CommandType.INQUIRY, Command.AUTO_SLOW_SHUTTER);
		Assert.assertEquals("Off", status);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect shutter speed value is 15
	 */
	@Test
	public void testDigestResponseInquiryCommandShutterSpeed() {
		int value = (int) averPTZCommunicator.digestResponse(ReplyPacket.SHUTTER_SPEED.getCode(), 1, CommandType.INQUIRY, Command.SHUTTER_DIRECT);
		Assert.assertEquals(15, value);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect iris level value is 13
	 */
	@Test
	public void testDigestResponseInquiryCommandIrisLevel() {
		int value = (int) averPTZCommunicator.digestResponse(ReplyPacket.IRIS_LEVEL.getCode(), 1, CommandType.INQUIRY, Command.IRIS_DIRECT);
		Assert.assertEquals(13, value);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect gain level value is 45
	 */
	@Test
	public void testDigestResponseInquiryCommandGainLevel() {
		int value = (int) averPTZCommunicator.digestResponse(ReplyPacket.GAIN_LEVEL.getCode(), 1, CommandType.INQUIRY, Command.GAIN_DIRECT);
		Assert.assertEquals(45, value);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect RGain value is 33
	 */
	@Test
	public void testDigestResponseInquiryCommandRGainValue() {
		int value = (int) averPTZCommunicator.digestResponse(ReplyPacket.RGAIN.getCode(), 1, CommandType.INQUIRY, Command.RGAIN_INQ);
		Assert.assertEquals(33, value);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect BGain value is 52
	 */
	@Test
	public void testDigestResponseInquiryCommandBGainValue() {
		int value = (int) averPTZCommunicator.digestResponse(ReplyPacket.BGAIN.getCode(), 1, CommandType.INQUIRY, Command.BGAIN_INQ);
		Assert.assertEquals(52, value);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect gain limit level value is 8
	 */
	@Test
	public void testDigestResponseInquiryCommandGainLimitLevel() {
		int value = (int) averPTZCommunicator.digestResponse(ReplyPacket.GAIN_LIMIT_LEVEL.getCode(), 1, CommandType.INQUIRY, Command.GAIN_LIMIT_DIRECT);
		Assert.assertEquals(8, value);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect exposure value is 9
	 */
	@Test
	public void testDigestResponseInquiryCommandExposureValue() {
		int value = (int) averPTZCommunicator.digestResponse(ReplyPacket.EXPOSURE_VALUE.getCode(), 1, CommandType.INQUIRY, Command.EXP_COMP_DIRECT);
		Assert.assertEquals(9, value);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect last preset recalled value is 2
	 */
	@Test
	public void testDigestResponseInquiryCommandLastPresetRecalled() {
		int value = (int) averPTZCommunicator.digestResponse(ReplyPacket.LAST_PRESET_RECALLED.getCode(), 1, CommandType.INQUIRY, Command.PRESET);
		Assert.assertEquals(2, value);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect WB auto mode
	 */
	@Test
	public void testDigestResponseInquiryCommandWBAutoMode() {
		String mode = (String) averPTZCommunicator.digestResponse(ReplyPacket.WB_AUTO_MODE.getCode(), 1, CommandType.INQUIRY, Command.WB_MODE);
		Assert.assertEquals("Auto", mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect WB indoor mode
	 */
	@Test
	public void testDigestResponseInquiryCommandWBIndoorMode() {
		String mode = (String) averPTZCommunicator.digestResponse(ReplyPacket.WB_INDOOR_MODE.getCode(), 1, CommandType.INQUIRY, Command.WB_MODE);
		Assert.assertEquals("Indoor", mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect WB outdoor mode
	 */
	@Test
	public void testDigestResponseInquiryCommandWBOutdoorMode() {
		String mode = (String) averPTZCommunicator.digestResponse(ReplyPacket.WB_OUTDOOR_MODE.getCode(), 1, CommandType.INQUIRY, Command.WB_MODE);
		Assert.assertEquals("Outdoor", mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect WB one push mode
	 */
	@Test
	public void testDigestResponseInquiryCommandWBOnePushMode() {
		String mode = (String) averPTZCommunicator.digestResponse(ReplyPacket.WB_ONE_PUSH_MODE.getCode(), 1, CommandType.INQUIRY, Command.WB_MODE);
		Assert.assertEquals("OnePushWB", mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect WB manual mode
	 */
	@Test
	public void testDigestResponseInquiryCommandWBManualMode() {
		String mode = (String) averPTZCommunicator.digestResponse(ReplyPacket.WB_MANUAL_MODE.getCode(), 1, CommandType.INQUIRY, Command.WB_MODE);
		Assert.assertEquals("Manual", mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect Backlight on
	 */
	@Test
	public void testDigestResponseInquiryCommandBacklightOn() {
		String mode = (String) averPTZCommunicator.digestResponse(ReplyPacket.BACKLIGHT_ON.getCode(), 1, CommandType.INQUIRY, Command.BACKLIGHT);
		Assert.assertEquals("On", mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect Backlight off
	 */
	@Test
	public void testDigestResponseInquiryCommandBacklightOff() {
		String mode = (String) averPTZCommunicator.digestResponse(ReplyPacket.BACKLIGHT_OFF.getCode(), 1, CommandType.INQUIRY, Command.BACKLIGHT);
		Assert.assertEquals("Off", mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect Slow pan tilt on
	 */
	@Test
	public void testDigestResponseInquiryCommandSlowPanTiltOn() {
		String mode = (String) averPTZCommunicator.digestResponse(ReplyPacket.SLOW_PAN_TILT_ON.getCode(), 1, CommandType.INQUIRY, Command.SLOW_PAN_TILT);
		Assert.assertEquals("On", mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect Slow pan tilt off
	 */
	@Test
	public void testDigestResponseInquiryCommandSlowPanTiltOff() {
		String mode = (String) averPTZCommunicator.digestResponse(ReplyPacket.SLOW_PAN_TILT_OFF.getCode(), 1, CommandType.INQUIRY, Command.SLOW_PAN_TILT);
		Assert.assertEquals("Off", mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse throw exception
	 * Expect throw an illegal state exception with message "Unexpected reply"
	 */
	@Test()
	public void testDigestResponseInquiryCommandUnexpectedReply() {
		exceptionRule.expect(IllegalStateException.class);
		exceptionRule.expectMessage("Unexpected reply");

		// Throw an illegal state exception with message "Unexpected reply"
		averPTZCommunicator.digestResponse(ReplyPacket.UNEXPECTED_REPLY.getCode(), 1, CommandType.COMMAND, null);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse throw exception
	 * Expect throw an illegal state exception with message "Unexpected sequence number"
	 */
	@Test()
	public void testDigestResponseInquiryCommandUnexpectedSequenceNumber() {
		exceptionRule.expect(IllegalStateException.class);
		exceptionRule.expectMessage("Unexpected sequence number");

		// Throw an illegal state exception with message "Unexpected sequence number"
		averPTZCommunicator.digestResponse(ReplyPacket.COMPLETION.getCode(), 2, CommandType.COMMAND, null);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse throw exception
	 * Expect throw an illegal state exception with message "Unexpected completion packet"
	 */
	@Test()
	public void testDigestResponseInquiryCommandUnexpectedCompletionPacket() {
		exceptionRule.expect(IllegalStateException.class);
		exceptionRule.expectMessage("Unexpected completion packet");

		// Throw an illegal state exception with message "Unexpected completion packet"
		averPTZCommunicator.digestResponse(ReplyPacket.ACK.getCode(), 1, CommandType.COMMAND, null);
	}

	/**
	 * Test AverPTZCommunicator#doneReading success
	 * Expect send control command of power and receive completion packet
	 */
	@Test()
	public void testDoneReadingControlCommand() throws Exception {
		// Power On (Just test only 1 command represented for control command)
		byte[] response = averPTZCommunicator.send(buildSendPacket(1, 1, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				Category.CAMERA.getCode(), Command.POWER.getCode(), PowerStatus.ON.getCode()));
		averPTZCommunicator.digestResponse(response, 1, CommandType.COMMAND, null);
		Assert.assertArrayEquals(ReplyPacket.COMPLETION.getCode(), response);
	}

	/**
	 * Test AverPTZCommunicator#doneReading success
	 * Expect send inquiry command of power and receive status "On" or "Off"
	 */
	@Test()
	public void testDoneReadingInquiryCommand() throws Exception {
		// Inquiry Power (Just test only 1 command represented for inquiry command)
		byte[] response = averPTZCommunicator.send(buildSendPacket(1, 2, PayloadType.INQUIRY.getCode(),
				CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(), Command.POWER.getCode()));

		String status = (String) averPTZCommunicator.digestResponse(response, 2, CommandType.INQUIRY, Command.POWER);
		Assert.assertTrue(status.equalsIgnoreCase("On") || status.equalsIgnoreCase("Off"));
	}

	/**
	 * Test AverPTZCommunicator#doneReading throw exception
	 * Expect throw a command failure exception + illegal exception
	 * Steps:
	 * 1. Send control command to change focus mode to "Auto"
	 * 2. Send control command to trigger focus far/near
	 * => Throw command failure exception in doneReading() because change focus position while in "Auto" mode
	 * => Throw illegal exception in digestResponse() because the response is not completion packet
	 */
	@Test()
	public void testDoneReadingWithOnlyException() throws Exception {
		// Send control command to change focus mode to "Auto"
		byte[] responseFocusAuto = averPTZCommunicator.send(buildSendPacket(1, 3, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				Category.CAMERA.getCode(), Command.FOCUS_MODE.getCode(), FocusMode.AUTO.getCode()));
		averPTZCommunicator.digestResponse(responseFocusAuto, 3, CommandType.COMMAND, null);

		// Exception rule for command failure exception
		exceptionRule.expect(CommandFailureException.class);
		exceptionRule.expectMessage("Error response received from: 172.31.254.204. Request: 01,00,00,06,00,00,00,04,81,01,04,08,02,FF; response: 01,11,00,04,00,00,00,04,90,60,41,FF");

		// Send control command to trigger focus far
		// Throw command failure exception in doneReading() because change focus position while in "Auto" mode
		byte[] responseFocusFar = averPTZCommunicator.send(buildSendPacket(1, 4, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				Category.CAMERA.getCode(), Command.FOCUS.getCode(), FocusControl.FAR.getCode()));

		// Exception rule for illegal exception
		exceptionRule.expect(IllegalStateException.class);
		exceptionRule.expectMessage("Unexpected completion packet");

		// Throw illegal exception in digestResponse() because the response is not completion packet
		averPTZCommunicator.digestResponse(responseFocusFar, 4, CommandType.COMMAND, null);
	}


	/**
	 * Test AverPTZCommunicator#doneReading throw exception and read the completion packet left
	 * Expect throw a command failure exception + illegal exception
	 * Steps:
	 * 1. Send control command to change AE mode to "FullAuto"
	 * 2. Send control command to change Shutter speed
	 * => Throw command failure exception in doneReading() because change focus position while in "Auto" mode
	 * The order of reply packet: ACK -> ERROR -> COMPLETION
	 * => Before throw error, need to read COMPLETION packet left (but not return it to read() method)
	 * => Throw illegal exception in digestResponse() because the response is not completion packet (Error packet)
	 * 3. Finally, Send inquiry command of power and receive status "On" or "Off"
	 * => Ensure that not receive COMPLETION packet of control command sent before at step 2
	 */
	@Test()
	public void testDoneReadingWithExceptionAndHasCompletionPacketLeftToRead() throws Exception {
		// Send control command to change AE mode to "FullAuto"
		byte[] responseAEFullAutoMode = averPTZCommunicator.send(buildSendPacket(1, 5, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				Category.CAMERA.getCode(), Command.AE_MODE.getCode(), AEMode.FULL_AUTO.getCode()));
		averPTZCommunicator.digestResponse(responseAEFullAutoMode, 5, CommandType.COMMAND, null);

		// Exception rule for command failure exception
		exceptionRule.expect(CommandFailureException.class);
		exceptionRule.expectMessage("Error response received from: 172.31.254.204. Request: 01,00,00,09,00,00,00,06,81,01,04,4A,00,00,00,0A,FF; response: 01,11,00,04,00,00,00,06,90,60,41,FF");

		int shutterSpeed = 10;
		// Send control command to change Shutter speed
		// Throw command failure exception in doneReading() because change focus position while in "Auto" mode
		byte[] responseShutterDirect = averPTZCommunicator.send(buildSendPacket(1, 6, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.SHUTTER_DIRECT.getCode(), convertOneByteNumberToTwoBytesArray((byte) shutterSpeed)));

		// Exception rule for illegal exception
		exceptionRule.expect(IllegalStateException.class);
		exceptionRule.expectMessage("Unexpected completion packet");

		// Throw illegal exception in digestResponse() because the response is not completion packet (Error packet)
		averPTZCommunicator.digestResponse(responseShutterDirect, 6, CommandType.COMMAND, null);

		/*
		Finally, Send inquiry command of power and receive status "On" or "Off"
		=> Ensure that not receive COMPLETION packet of control command sent before at step 2
		 */
		byte[] responsePowerInq = averPTZCommunicator.send(buildSendPacket(1, 7, PayloadType.INQUIRY.getCode(),
				CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(), Command.POWER.getCode()));

		String status = (String) averPTZCommunicator.digestResponse(responsePowerInq, 7, CommandType.INQUIRY, Command.POWER);
		Assert.assertTrue(status.equalsIgnoreCase("On") || status.equalsIgnoreCase("Off"));
	}

	/**
	 * Test AxisCommunicator#getMultipleStatistics success
	 * Expect device info get data success
	 */
	@Test
	public void testAverPTZCommunicatorDeviceInfo() {
		extendedStatistic = (ExtendedStatistics) averPTZCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistic.getStatistics();

		Assert.assertEquals("AVer Information Co.", stats.get(StatisticsProperty.DEVICE_INFORMATION.getName() + HASH + StatisticsProperty.DEVICE_MFG.getName()));
		Assert.assertEquals("PTZ330", stats.get(StatisticsProperty.DEVICE_INFORMATION.getName() + HASH + StatisticsProperty.DEVICE_MODEL.getName()));
		Assert.assertEquals("5310505800460", stats.get(StatisticsProperty.DEVICE_INFORMATION.getName() + HASH + StatisticsProperty.DEVICE_SERIAL_NUMBER.getName()));
		Assert.assertEquals("0.0.0003.72", stats.get(StatisticsProperty.DEVICE_INFORMATION.getName() + HASH + StatisticsProperty.DEVICE_FIRMWARE_VERSION.getName()));
		Assert.assertEquals("6", stats.get(StatisticsProperty.DEVICE_INFORMATION.getName() + HASH + StatisticsProperty.DEVICE_LAST_PRESET_RECALLED.getName()));
	}

	/**
	 * Test AxisCommunicator#getMultipleStatistics success
	 * Expect power status get data success
	 */
	@Test
	public void testAverPTZCommunicatorPowerStatus() {
		extendedStatistic = (ExtendedStatistics) averPTZCommunicator.getMultipleStatistics().get(0);
		advancedControllableProperties = extendedStatistic.getControllableProperties();

		for (AdvancedControllableProperty property : advancedControllableProperties) {
			if (property.getName().equalsIgnoreCase(Command.POWER.getName())) {
				Assert.assertTrue((int) property.getValue() == 1 || (int) property.getValue() == 0);
			}
		}
	}

	/**
	 * Test AxisCommunicator#getMultipleStatistics success
	 * Expect focus mode get data success
	 */
	@Test
	public void testAverPTZCommunicatorFocusMode() {
		extendedStatistic = (ExtendedStatistics) averPTZCommunicator.getMultipleStatistics().get(0);
		advancedControllableProperties = extendedStatistic.getControllableProperties();

		for (AdvancedControllableProperty property : advancedControllableProperties) {
			if (property.getName().equalsIgnoreCase(Command.FOCUS.getName() + HASH + Command.FOCUS_MODE.getName())) {
				Assert.assertTrue((int) property.getValue() == 1 || (int) property.getValue() == 0);
			}
		}
	}

	/**
	 * Test AxisCommunicator#getMultipleStatistics success
	 * Expect backlight status get data success
	 */
	@Test
	public void testAverPTZCommunicatorBacklightStatus() {
		extendedStatistic = (ExtendedStatistics) averPTZCommunicator.getMultipleStatistics().get(0);
		advancedControllableProperties = extendedStatistic.getControllableProperties();

		for (AdvancedControllableProperty property : advancedControllableProperties) {
			if (property.getName().equalsIgnoreCase(Command.EXPOSURE.getName() + HASH + Command.BACKLIGHT.getName())) {
				Assert.assertTrue((int) property.getValue() == 1 || (int) property.getValue() == 0);
			}
		}
	}

	/**
	 * Test AxisCommunicator#getMultipleStatistics success
	 * Expect backlight status get data success
	 */
	@Test
	public void testAverPTZCommunicatorSlowPanTiltStatus() {
		extendedStatistic = (ExtendedStatistics) averPTZCommunicator.getMultipleStatistics().get(0);
		advancedControllableProperties = extendedStatistic.getControllableProperties();

		for (AdvancedControllableProperty property : advancedControllableProperties) {
			if (property.getName().equalsIgnoreCase(Command.PAN_TILT_DRIVE.getName() + HASH + Command.SLOW_PAN_TILT.getName())) {
				Assert.assertTrue((int) property.getValue() == 1 || (int) property.getValue() == 0);
			}
		}
	}

	/**
	 * Test AxisCommunicator#getMultipleStatistics success
	 * Expect AE mode get data success
	 */
	@Test
	public void testAverPTZCommunicatorAEMode() {
		extendedStatistic = (ExtendedStatistics) averPTZCommunicator.getMultipleStatistics().get(0);
		advancedControllableProperties = extendedStatistic.getControllableProperties();
		stats = extendedStatistic.getStatistics();

		String aeMode = "FullAuto", value;
		List<String> shutterValues = new ArrayList<>(
				Arrays.asList("1/32K", "1/16K", "1/8K", "1/4K", "1/2K", "1/1K", "1/480", "1/240", "1/120", "1/60", "1/30", "1/20", "1/10", "1/5", "1/2", "1/1"));
		List<String> irisLevels = new ArrayList<>(
				Arrays.asList("0", "F14", "F11", "F8.0", "F6.8", "F5.6", "F4.8", "F4.0", "F3.4", "F2.8", "F2.4", "F2.0", "F1.8", "F1.6"));

		for (AdvancedControllableProperty property : advancedControllableProperties) {
			if (property.getName().equalsIgnoreCase(Command.EXPOSURE.getName() + HASH + Command.AE_MODE.getName())) {
				aeMode = (String) property.getValue();
				Assert.assertTrue(aeMode.equals("FullAuto") || aeMode.equals("IrisPriority") || aeMode.equals("ShutterPriority") || aeMode.equals("Manual"));
			}
		}

		switch (aeMode) {
			case "FullAuto": {

				// Current value
				value = stats.get(Command.EXPOSURE.getName() + HASH + Command.EXP_COMP_CURRENT.getName());
				Assert.assertTrue(Integer.parseInt(value) >= -4 && Integer.parseInt(value) <= 4);

				value = stats.get(Command.EXPOSURE.getName() + HASH + Command.GAIN_LIMIT_CURRENT.getName());
				Assert.assertTrue(Integer.parseInt(value) >= 24 && Integer.parseInt(value) <= 48);

				// Value for slider
				for (AdvancedControllableProperty property : advancedControllableProperties) {
					if (property.getName().equalsIgnoreCase(Command.EXPOSURE.getName() + HASH + Command.EXP_COMP_DIRECT.getName())) {

						Assert.assertTrue((float) property.getValue() >= 1 && (float) property.getValue() <= 9);
					}

					if (property.getName().equalsIgnoreCase(Command.EXPOSURE.getName() + HASH + Command.GAIN_LIMIT_DIRECT.getName())) {

						Assert.assertTrue((float) property.getValue() >= 0 && (float) property.getValue() <= 8);
					}

					if (property.getName().equalsIgnoreCase(Command.EXPOSURE.getName() + HASH + Command.AUTO_SLOW_SHUTTER.getName())) {
						Assert.assertTrue((int) property.getValue() == 1 || (int) property.getValue() == 0);
					}
				}
				break;
			}

			case "ShutterPriority": {
				// Value text
				value = stats.get(Command.EXPOSURE.getName() + HASH + Command.EXP_COMP_CURRENT.getName());
				Assert.assertTrue(Integer.parseInt(value) >= -4 && Integer.parseInt(value) <= 4);

				value = stats.get(Command.EXPOSURE.getName() + HASH + Command.GAIN_LIMIT_CURRENT.getName());
				Assert.assertTrue(Integer.parseInt(value) >= 24 && Integer.parseInt(value) <= 48);

				value = stats.get(Command.EXPOSURE.getName() + HASH + Command.SHUTTER_CURRENT.getName());
				Assert.assertTrue(shutterValues.contains(value));

				// Value for slider
				for (AdvancedControllableProperty property : advancedControllableProperties) {
					if (property.getName().equalsIgnoreCase(Command.EXPOSURE.getName() + HASH + Command.EXP_COMP_DIRECT.getName())) {
						Assert.assertTrue((float) property.getValue() >= 1 && (float) property.getValue() <= 9);
					}

					if (property.getName().equalsIgnoreCase(Command.EXPOSURE.getName() + HASH + Command.GAIN_LIMIT_DIRECT.getName())) {
						Assert.assertTrue((float) property.getValue() >= 0 && (float) property.getValue() <= 8);
					}

					if (property.getName().equalsIgnoreCase(Command.EXPOSURE.getName() + HASH + Command.SHUTTER_DIRECT.getName())) {
						Assert.assertTrue((float) property.getValue() >= 0 && (float) property.getValue() <= 15);
					}
				}
				break;
			}

			case "IrisPriority": {
				// Value text
				value = stats.get(Command.EXPOSURE.getName() + HASH + Command.EXP_COMP_CURRENT.getName());
				Assert.assertTrue(Integer.parseInt(value) >= -4 && Integer.parseInt(value) <= 4);

				value = stats.get(Command.EXPOSURE.getName() + HASH + Command.GAIN_LIMIT_CURRENT.getName());
				Assert.assertTrue(Integer.parseInt(value) >= 24 && Integer.parseInt(value) <= 48);

				value = stats.get(Command.EXPOSURE.getName() + HASH + Command.IRIS_CURRENT.getName());
				Assert.assertTrue(irisLevels.contains(value));

				// Value for slider
				for (AdvancedControllableProperty property : advancedControllableProperties) {
					if (property.getName().equalsIgnoreCase(Command.EXPOSURE.getName() + HASH + Command.EXP_COMP_DIRECT.getName())) {
						Assert.assertTrue((float) property.getValue() >= 1 && (float) property.getValue() <= 9);
					}

					if (property.getName().equalsIgnoreCase(Command.EXPOSURE.getName() + HASH + Command.GAIN_LIMIT_DIRECT.getName())) {
						Assert.assertTrue((float) property.getValue() >= 0 && (float) property.getValue() <= 8);
					}

					if (property.getName().equalsIgnoreCase(Command.EXPOSURE.getName() + HASH + Command.AUTO_SLOW_SHUTTER.getName())) {
						Assert.assertTrue((int) property.getValue() == 1 || (int) property.getValue() == 0);
					}

					if (property.getName().equalsIgnoreCase(Command.EXPOSURE.getName() + HASH + Command.IRIS_DIRECT.getName())) {
						Assert.assertTrue((float) property.getValue() >= 0 && (float) property.getValue() <= 13);
					}
				}
				break;
			}

			case "Manual": {
				// Value text
				value = stats.get(Command.EXPOSURE.getName() + HASH + Command.SHUTTER_CURRENT.getName());
				Assert.assertTrue(shutterValues.contains(value));

				value = stats.get(Command.EXPOSURE.getName() + HASH + Command.GAIN_CURRENT.getName());
				Assert.assertTrue(Integer.parseInt(value) >= 0 && Integer.parseInt(value) <= 48);

				value = stats.get(Command.EXPOSURE.getName() + HASH + Command.IRIS_CURRENT.getName());
				Assert.assertTrue(irisLevels.contains(value));

				// Value for slider
				for (AdvancedControllableProperty property : advancedControllableProperties) {
					if (property.getName().equalsIgnoreCase(Command.EXPOSURE.getName() + HASH + Command.SHUTTER_DIRECT.getName())) {
						Assert.assertTrue((float) property.getValue() >= 0 && (float) property.getValue() <= 15);
					}

					if (property.getName().equalsIgnoreCase(Command.EXPOSURE.getName() + HASH + Command.GAIN_DIRECT.getName())) {
						Assert.assertTrue((float) property.getValue() >= 0 && (float) property.getValue() <= 48);
					}

					if (property.getName().equalsIgnoreCase(Command.EXPOSURE.getName() + HASH + Command.IRIS_DIRECT.getName())) {
						Assert.assertTrue((float) property.getValue() >= 0 && (float) property.getValue() <= 13);
					}
				}
				break;
			}
		}
	}

	/**
	 * Test AxisCommunicator#getMultipleStatistics success
	 * Expect WB mode get data success
	 */
	@Test
	public void testAverPTZCommunicatorWBMode() {
		extendedStatistic = (ExtendedStatistics) averPTZCommunicator.getMultipleStatistics().get(0);
		advancedControllableProperties = extendedStatistic.getControllableProperties();
		stats = extendedStatistic.getStatistics();

		for (AdvancedControllableProperty property : advancedControllableProperties) {
			if (property.getName().equalsIgnoreCase(Command.IMAGE_PROCESS.getName() + HASH + Command.WB_MODE.getName())) {
				String wbMode = (String) property.getValue();
				Assert.assertTrue(wbMode.equals("Auto") || wbMode.equals("Manual") || wbMode.equals("Indoor") || wbMode.equals("Outdoor") || wbMode.equals("OnePushWB"));
			}
		}
	}
}
