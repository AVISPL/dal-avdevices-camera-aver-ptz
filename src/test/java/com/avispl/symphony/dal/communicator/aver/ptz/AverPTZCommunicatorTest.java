/*
 * Copyright (c) 2021 AVI-SPL Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.aver.ptz;

import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.HASH;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.IRIS_LEVELS;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.SHUTTER_VALUES;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZUtils.buildSendPacket;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZUtils.convertOneByteNumberToTwoBytesArray;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.error.CommandFailureException;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.ReplyPacket;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.StatisticsProperty;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.PayloadCategory;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.PayloadType;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.command.Command;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.command.CommandType;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.AEMode;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.BacklightStatus;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.FocusControl;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.FocusMode;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.PowerStatus;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.SlowPanTiltStatus;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.SlowShutterStatus;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.WBMode;
import com.avispl.symphony.dal.communicator.aver.ptz.interfaces.DevelopmentTest;

/**
 * Unit test for AverPTZ Communicator
 * Send success packet by override method read and doneReading
 *
 * @author Harry
 * @version 1.0
 * @since 1.0
 */

public class AverPTZCommunicatorTest {
	private AverPTZCommunicator averPTZCommunicator;
	private ExtendedStatistics extendedStatistic;
	private List<AdvancedControllableProperty> advancedControllableProperties;
	private Map<String, String> stats;

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		averPTZCommunicator = new AverPTZCommunicator();
		averPTZCommunicator.setHost("***REMOVED***");
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
		PowerStatus status = (PowerStatus) averPTZCommunicator.digestResponse(ReplyPacket.POWER_ON.getCode(), 1, CommandType.INQUIRY, Command.POWER);
		Assert.assertEquals(PowerStatus.ON, status);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect status power off
	 */
	@Test
	public void testDigestResponseInquiryCommandPowerOff() {
		PowerStatus status = (PowerStatus) averPTZCommunicator.digestResponse(ReplyPacket.POWER_OFF.getCode(), 1, CommandType.INQUIRY, Command.POWER);
		Assert.assertEquals(PowerStatus.OFF, status);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect auto focus mode
	 */
	@Test
	public void testDigestResponseInquiryCommandAutoFocus() {
		FocusMode mode = (FocusMode) averPTZCommunicator.digestResponse(ReplyPacket.FOCUS_AUTO_MODE.getCode(), 1, CommandType.INQUIRY, Command.FOCUS_MODE);
		Assert.assertEquals(FocusMode.AUTO, mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect manual focus mode
	 */
	@Test
	public void testDigestResponseInquiryCommandManualFocus() {
		FocusMode mode = (FocusMode) averPTZCommunicator.digestResponse(ReplyPacket.FOCUS_MANUAL_MODE.getCode(), 1, CommandType.INQUIRY, Command.FOCUS_MODE);
		Assert.assertEquals(FocusMode.MANUAL, mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect AE full auto mode
	 */
	@Test
	public void testDigestResponseInquiryCommandAEFullAutoMode() {
		AEMode mode = (AEMode) averPTZCommunicator.digestResponse(ReplyPacket.AE_FULL_AUTO_MODE.getCode(), 1, CommandType.INQUIRY, Command.AE_MODE);
		Assert.assertEquals(AEMode.FULL_AUTO, mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect AE manual mode
	 */
	@Test
	public void testDigestResponseInquiryCommandAEManualMode() {
		AEMode mode = (AEMode) averPTZCommunicator.digestResponse(ReplyPacket.AE_MANUAL_MODE.getCode(), 1, CommandType.INQUIRY, Command.AE_MODE);
		Assert.assertEquals(AEMode.MANUAL, mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect AE shutter priority mode
	 */
	@Test
	public void testDigestResponseInquiryCommandAEShutterPriorityMode() {
		AEMode mode = (AEMode) averPTZCommunicator.digestResponse(ReplyPacket.AE_SHUTTER_PRIORITY_MODE.getCode(), 1, CommandType.INQUIRY, Command.AE_MODE);
		Assert.assertEquals(AEMode.SHUTTER_PRIORITY, mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect AE full auto mode
	 */
	@Test
	public void testDigestResponseInquiryCommandAEIrisPriorityMode() {
		AEMode mode = (AEMode) averPTZCommunicator.digestResponse(ReplyPacket.AE_IRIS_PRIORITY_MODE.getCode(), 1, CommandType.INQUIRY, Command.AE_MODE);
		Assert.assertEquals(AEMode.IRIS_PRIORITY, mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect status auto slow shutter on
	 */
	@Test
	public void testDigestResponseInquiryCommandSlowShutterOn() {
		SlowShutterStatus status = (SlowShutterStatus) averPTZCommunicator.digestResponse(ReplyPacket.AUTO_SLOW_SHUTTER_ON.getCode(), 1, CommandType.INQUIRY, Command.AUTO_SLOW_SHUTTER);
		Assert.assertEquals(SlowShutterStatus.ON, status);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect status auto slow shutter off
	 */
	@Test
	public void testDigestResponseInquiryCommandSlowShutterOff() {
		SlowShutterStatus status = (SlowShutterStatus) averPTZCommunicator.digestResponse(ReplyPacket.AUTO_SLOW_SHUTTER_OFF.getCode(), 1, CommandType.INQUIRY, Command.AUTO_SLOW_SHUTTER);
		Assert.assertEquals(SlowShutterStatus.OFF, status);
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
		WBMode mode = (WBMode) averPTZCommunicator.digestResponse(ReplyPacket.WB_AUTO_MODE.getCode(), 1, CommandType.INQUIRY, Command.WB_MODE);
		Assert.assertEquals(WBMode.AUTO, mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect WB indoor mode
	 */
	@Test
	public void testDigestResponseInquiryCommandWBIndoorMode() {
		WBMode mode = (WBMode) averPTZCommunicator.digestResponse(ReplyPacket.WB_INDOOR_MODE.getCode(), 1, CommandType.INQUIRY, Command.WB_MODE);
		Assert.assertEquals(WBMode.INDOOR, mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect WB outdoor mode
	 */
	@Test
	public void testDigestResponseInquiryCommandWBOutdoorMode() {
		WBMode mode = (WBMode) averPTZCommunicator.digestResponse(ReplyPacket.WB_OUTDOOR_MODE.getCode(), 1, CommandType.INQUIRY, Command.WB_MODE);
		Assert.assertEquals(WBMode.OUTDOOR, mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect WB one push mode
	 */
	@Test
	public void testDigestResponseInquiryCommandWBOnePushMode() {
		WBMode mode = (WBMode) averPTZCommunicator.digestResponse(ReplyPacket.WB_ONE_PUSH_MODE.getCode(), 1, CommandType.INQUIRY, Command.WB_MODE);
		Assert.assertEquals(WBMode.ONE_PUSH_WB, mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect WB manual mode
	 */
	@Test
	public void testDigestResponseInquiryCommandWBManualMode() {
		WBMode mode = (WBMode) averPTZCommunicator.digestResponse(ReplyPacket.WB_MANUAL_MODE.getCode(), 1, CommandType.INQUIRY, Command.WB_MODE);
		Assert.assertEquals(WBMode.MANUAL, mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect Backlight on
	 */
	@Test
	public void testDigestResponseInquiryCommandBacklightOn() {
		BacklightStatus mode = (BacklightStatus) averPTZCommunicator.digestResponse(ReplyPacket.BACKLIGHT_ON.getCode(), 1, CommandType.INQUIRY, Command.BACKLIGHT);
		Assert.assertEquals(BacklightStatus.ON, mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect Backlight off
	 */
	@Test
	public void testDigestResponseInquiryCommandBacklightOff() {
		BacklightStatus mode = (BacklightStatus) averPTZCommunicator.digestResponse(ReplyPacket.BACKLIGHT_OFF.getCode(), 1, CommandType.INQUIRY, Command.BACKLIGHT);
		Assert.assertEquals(BacklightStatus.OFF, mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect Slow pan tilt on
	 */
	@Test
	public void testDigestResponseInquiryCommandSlowPanTiltOn() {
		SlowPanTiltStatus mode = (SlowPanTiltStatus) averPTZCommunicator.digestResponse(ReplyPacket.SLOW_PAN_TILT_ON.getCode(), 1, CommandType.INQUIRY, Command.SLOW_PAN_TILT);
		Assert.assertEquals(SlowPanTiltStatus.ON, mode);
	}

	/**
	 * Test AverPTZCommunicator#digestResponse success
	 * Expect Slow pan tilt off
	 */
	@Test
	public void testDigestResponseInquiryCommandSlowPanTiltOff() {
		SlowPanTiltStatus mode = (SlowPanTiltStatus) averPTZCommunicator.digestResponse(ReplyPacket.SLOW_PAN_TILT_OFF.getCode(), 1, CommandType.INQUIRY, Command.SLOW_PAN_TILT);
		Assert.assertEquals(SlowPanTiltStatus.OFF, mode);
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
	@Category(DevelopmentTest.class)
	public void testDoneReadingControlCommand() throws Exception {
		// Power On (Just test only 1 command represented for control command)
		byte[] response = averPTZCommunicator.send(buildSendPacket(1, 1, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				PayloadCategory.CAMERA.getCode(), Command.POWER.getCode(), PowerStatus.ON.getCode()));
		averPTZCommunicator.digestResponse(response, 1, CommandType.COMMAND, null);
		Assert.assertArrayEquals(ReplyPacket.COMPLETION.getCode(), response);
	}

	/**
	 * Test AverPTZCommunicator#doneReading success
	 * Expect send inquiry command of power and receive status "On" or "Off"
	 */
	@Test()
	@Category(DevelopmentTest.class)
	public void testDoneReadingInquiryCommand() throws Exception {
		// Inquiry Power (Just test only 1 command represented for inquiry command)
		byte[] response = averPTZCommunicator.send(buildSendPacket(1, 2, PayloadType.INQUIRY.getCode(),
				CommandType.INQUIRY.getCode(), PayloadCategory.CAMERA.getCode(), Command.POWER.getCode()));

		PowerStatus status = (PowerStatus) averPTZCommunicator.digestResponse(response, 2, CommandType.INQUIRY, Command.POWER);
		Assert.assertTrue(status.equals(PowerStatus.ON) || status.equals(PowerStatus.OFF));
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
	@Category(DevelopmentTest.class)
	public void testDoneReadingWithOnlyException() throws Exception {
		// Send control command to change focus mode to "Auto"
		byte[] responseFocusAuto = averPTZCommunicator.send(buildSendPacket(1, 3, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				PayloadCategory.CAMERA.getCode(), Command.FOCUS_MODE.getCode(), FocusMode.AUTO.getCode()));
		averPTZCommunicator.digestResponse(responseFocusAuto, 3, CommandType.COMMAND, null);

		// Exception rule for command failure exception
		exceptionRule.expect(CommandFailureException.class);
		exceptionRule.expectMessage("Error response received from: ***REMOVED***. Request: 01,00,00,06,00,00,00,04,81,01,04,08,20,FF; response: 01,11,00,04,00,00,00,04,90,60,41,FF");

		// Send control command to trigger focus far
		// Throw command failure exception in doneReading() because change focus position while in "Auto" mode
		byte[] responseFocusFar = averPTZCommunicator.send(buildSendPacket(1, 4, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				PayloadCategory.CAMERA.getCode(), Command.FOCUS.getCode(), FocusControl.FAR.getCode()));

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
	@Category(DevelopmentTest.class)
	public void testDoneReadingWithExceptionAndHasCompletionPacketLeftToRead() throws Exception {
		// Send control command to change AE mode to "FullAuto"
		byte[] responseAEFullAutoMode = averPTZCommunicator.send(buildSendPacket(1, 5, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				PayloadCategory.CAMERA.getCode(), Command.AE_MODE.getCode(), AEMode.FULL_AUTO.getCode()));
		averPTZCommunicator.digestResponse(responseAEFullAutoMode, 5, CommandType.COMMAND, null);

		// Exception rule for command failure exception
		exceptionRule.expect(CommandFailureException.class);
		exceptionRule.expectMessage("Error response received from: ***REMOVED***. Request: 01,00,00,09,00,00,00,06,81,01,04,4A,00,00,00,0A,FF; response: 01,11,00,04,00,00,00,06,90,60,41,FF");

		int shutterSpeed = 10;
		// Send control command to change Shutter speed
		// Throw command failure exception in doneReading() because change focus position while in "Auto" mode
		byte[] responseShutterDirect = averPTZCommunicator.send(buildSendPacket(1, 6, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
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
				CommandType.INQUIRY.getCode(), PayloadCategory.CAMERA.getCode(), Command.POWER.getCode()));

		String status = (String) averPTZCommunicator.digestResponse(responsePowerInq, 7, CommandType.INQUIRY, Command.POWER);
		Assert.assertTrue(status.equalsIgnoreCase("On") || status.equalsIgnoreCase("Off"));
	}

	/**
	 * Test AxisCommunicator#getMultipleStatistics success
	 * Expect device info get data success
	 */
	@Test
	@Category(DevelopmentTest.class)
	public void testAverPTZCommunicatorDeviceInfo() {
		extendedStatistic = (ExtendedStatistics) averPTZCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistic.getStatistics();

		Assert.assertEquals("AVer Information Co.", stats.get(StatisticsProperty.DEVICE_INFORMATION.getName() + HASH + StatisticsProperty.DEVICE_MFG.getName()));
		Assert.assertEquals("PTZ330", stats.get(StatisticsProperty.DEVICE_INFORMATION.getName() + HASH + StatisticsProperty.DEVICE_MODEL.getName()));
		Assert.assertEquals("5310505800460", stats.get(StatisticsProperty.DEVICE_INFORMATION.getName() + HASH + StatisticsProperty.DEVICE_SERIAL_NUMBER.getName()));
		Assert.assertEquals("0.0.0003.72", stats.get(StatisticsProperty.DEVICE_INFORMATION.getName() + HASH + StatisticsProperty.DEVICE_FIRMWARE_VERSION.getName()));
		int preset = Integer.parseInt(stats.get(StatisticsProperty.DEVICE_INFORMATION.getName() + HASH + StatisticsProperty.DEVICE_LAST_PRESET_RECALLED.getName()));
		Assert.assertTrue(preset >= 0 && preset <= 255);
	}

	/**
	 * Test AxisCommunicator#getMultipleStatistics success
	 * Expect power status get data success
	 */
	@Test
	@Category(DevelopmentTest.class)
	public void testAverPTZCommunicatorPowerStatus() {
		// Merge test power on/off into 1 test case because if we turn off -> on, it needs to wait about 1min to reboot -> cannot test other testcases.
		extendedStatistic = (ExtendedStatistics) averPTZCommunicator.getMultipleStatistics().get(0);
		advancedControllableProperties = extendedStatistic.getControllableProperties();

		for (AdvancedControllableProperty property : advancedControllableProperties) {
			String propertyName = property.getName();
			if (propertyName.equalsIgnoreCase(Command.POWER.getName())) {
				Assert.assertTrue((int) property.getValue() == 1 || (int) property.getValue() == 0);
				return;
			}
		}
	}

	/**
	 * Test AxisCommunicator#getMultipleStatistics success
	 * Expect auto focus mode get data success
	 */
	@Test
	@Category(DevelopmentTest.class)
	public void testAverPTZCommunicatorAutoFocusMode() throws Exception {
		// Change to auto-focus mode
		byte[] response = averPTZCommunicator.send(buildSendPacket(1, 8, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				PayloadCategory.CAMERA.getCode(), Command.FOCUS_MODE.getCode(), FocusMode.AUTO.getCode()));
		averPTZCommunicator.digestResponse(response, 8, CommandType.COMMAND, null);

		extendedStatistic = (ExtendedStatistics) averPTZCommunicator.getMultipleStatistics().get(0);
		advancedControllableProperties = extendedStatistic.getControllableProperties();

		for (AdvancedControllableProperty property : advancedControllableProperties) {
			String propertyName = property.getName();
			if (propertyName.equalsIgnoreCase(Command.FOCUS.getName() + HASH + Command.FOCUS_MODE.getName())) {
				Assert.assertEquals(0, property.getValue());
				return;
			}
		}
	}

	/**
	 * Test AxisCommunicator#getMultipleStatistics success
	 * Expect manual focus mode get data success
	 */
	@Test
	@Category(DevelopmentTest.class)
	public void testAverPTZCommunicatorManualFocusMode() throws Exception {
		// Change to manual focus mode
		byte[] response = averPTZCommunicator.send(buildSendPacket(1, 9, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				PayloadCategory.CAMERA.getCode(), Command.FOCUS_MODE.getCode(), FocusMode.MANUAL.getCode()));
		averPTZCommunicator.digestResponse(response, 9, CommandType.COMMAND, null);

		extendedStatistic = (ExtendedStatistics) averPTZCommunicator.getMultipleStatistics().get(0);
		advancedControllableProperties = extendedStatistic.getControllableProperties();

		for (AdvancedControllableProperty property : advancedControllableProperties) {
			String propertyName = property.getName();
			if (propertyName.equalsIgnoreCase(Command.FOCUS.getName() + HASH + Command.FOCUS_MODE.getName())) {
				Assert.assertEquals(1, property.getValue());
				return;
			}
		}
	}

	/**
	 * Test AxisCommunicator#getMultipleStatistics success
	 * Expect backlight status on get data success
	 */
	@Test
	@Category(DevelopmentTest.class)
	public void testAverPTZCommunicatorBacklightStatusOn() throws Exception {
		// Change to AE full auto mode
		byte[] responseAEMode = averPTZCommunicator.send(buildSendPacket(1, 10, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				PayloadCategory.CAMERA.getCode(), Command.AE_MODE.getCode(), AEMode.FULL_AUTO.getCode()));
		averPTZCommunicator.digestResponse(responseAEMode, 10, CommandType.COMMAND, null);

		// Change to backlight status on mode
		byte[] responseBacklight = averPTZCommunicator.send(buildSendPacket(1, 11, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				PayloadCategory.CAMERA.getCode(), Command.BACKLIGHT.getCode(), BacklightStatus.ON.getCode()));
		averPTZCommunicator.digestResponse(responseBacklight, 11, CommandType.COMMAND, null);

		extendedStatistic = (ExtendedStatistics) averPTZCommunicator.getMultipleStatistics().get(0);
		advancedControllableProperties = extendedStatistic.getControllableProperties();

		for (AdvancedControllableProperty property : advancedControllableProperties) {
			String propertyName = property.getName();
			if (propertyName.equalsIgnoreCase(Command.EXPOSURE.getName() + HASH + Command.BACKLIGHT.getName())) {
				Assert.assertEquals(1, (int) property.getValue());
				return;
			}
		}
	}

	/**
	 * Test AxisCommunicator#getMultipleStatistics success
	 * Expect backlight status off get data success
	 */
	@Test
	@Category(DevelopmentTest.class)
	public void testAverPTZCommunicatorBacklightStatusOff() throws Exception {
		// Change to AE full auto mode
		byte[] responseAEMode = averPTZCommunicator.send(buildSendPacket(1, 12, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				PayloadCategory.CAMERA.getCode(), Command.AE_MODE.getCode(), AEMode.FULL_AUTO.getCode()));
		averPTZCommunicator.digestResponse(responseAEMode, 12, CommandType.COMMAND, null);

		// Change to backlight status off mode
		byte[] responseBacklight = averPTZCommunicator.send(buildSendPacket(1, 13, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				PayloadCategory.CAMERA.getCode(), Command.BACKLIGHT.getCode(), BacklightStatus.OFF.getCode()));
		averPTZCommunicator.digestResponse(responseBacklight, 13, CommandType.COMMAND, null);

		extendedStatistic = (ExtendedStatistics) averPTZCommunicator.getMultipleStatistics().get(0);
		advancedControllableProperties = extendedStatistic.getControllableProperties();

		for (AdvancedControllableProperty property : advancedControllableProperties) {
			String propertyName = property.getName();
			if (propertyName.equalsIgnoreCase(Command.EXPOSURE.getName() + HASH + Command.BACKLIGHT.getName())) {
				Assert.assertEquals(0, (int) property.getValue());
				return;
			}
		}
	}

	/**
	 * Test AxisCommunicator#getMultipleStatistics success
	 * Expect slow pan tilt status on get data success
	 */
	@Test
	@Category(DevelopmentTest.class)
	public void testAverPTZCommunicatorSlowPanTiltStatusOn() throws Exception {
		// Change to slow pan tilt status on mode
		byte[] response = averPTZCommunicator.send(buildSendPacket(1, 14, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				PayloadCategory.PAN_TILTER.getCode(), Command.SLOW_PAN_TILT.getCode(), SlowPanTiltStatus.ON.getCode()));
		averPTZCommunicator.digestResponse(response, 14, CommandType.COMMAND, null);

		extendedStatistic = (ExtendedStatistics) averPTZCommunicator.getMultipleStatistics().get(0);
		advancedControllableProperties = extendedStatistic.getControllableProperties();

		for (AdvancedControllableProperty property : advancedControllableProperties) {
			String propertyName = property.getName();
			if (propertyName.equalsIgnoreCase(Command.PAN_TILT_DRIVE.getName() + HASH + Command.SLOW_PAN_TILT.getName())) {
				Assert.assertEquals(1, (int) property.getValue());
				return;
			}
		}
	}

	/**
	 * Test AxisCommunicator#getMultipleStatistics success
	 * Expect slow pan tilt status off get data success
	 */
	@Test
	@Category(DevelopmentTest.class)
	public void testAverPTZCommunicatorSlowPanTiltStatusOff() throws Exception {
		// Change to slow pan tilt status off mode
		byte[] response = averPTZCommunicator.send(buildSendPacket(1, 15, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				PayloadCategory.PAN_TILTER.getCode(), Command.SLOW_PAN_TILT.getCode(), SlowPanTiltStatus.OFF.getCode()));
		averPTZCommunicator.digestResponse(response, 15, CommandType.COMMAND, null);

		extendedStatistic = (ExtendedStatistics) averPTZCommunicator.getMultipleStatistics().get(0);
		advancedControllableProperties = extendedStatistic.getControllableProperties();

		for (AdvancedControllableProperty property : advancedControllableProperties) {
			String propertyName = property.getName();
			if (propertyName.equalsIgnoreCase(Command.PAN_TILT_DRIVE.getName() + HASH + Command.SLOW_PAN_TILT.getName())) {
				Assert.assertEquals(0, (int) property.getValue());
				return;
			}
		}
	}

	/**
	 * Test AxisCommunicator#getMultipleStatistics success
	 * Expect AE full auto mode get data success
	 */
	@Test
	@Category(DevelopmentTest.class)
	public void testAverPTZCommunicatorAEFullAutoMode() throws Exception {
		// Change to AE full auto mode
		byte[] responseAEMode = averPTZCommunicator.send(buildSendPacket(1, 16, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				PayloadCategory.CAMERA.getCode(), Command.AE_MODE.getCode(), AEMode.FULL_AUTO.getCode()));
		averPTZCommunicator.digestResponse(responseAEMode, 16, CommandType.COMMAND, null);

		extendedStatistic = (ExtendedStatistics) averPTZCommunicator.getMultipleStatistics().get(0);
		advancedControllableProperties = extendedStatistic.getControllableProperties();

		for (AdvancedControllableProperty property : advancedControllableProperties) {
			if (property.getName().equalsIgnoreCase(Command.EXPOSURE.getName() + HASH + Command.AE_MODE.getName())) {
				String aeMode = (String) property.getValue();
				Assert.assertEquals(AEMode.FULL_AUTO.getName(), aeMode);
				return;
			}
		}

		// Current value
		String value = stats.get(Command.EXPOSURE.getName() + HASH + Command.EXP_COMP_CURRENT.getName());
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
		}
	}

	/**
	 * Test AxisCommunicator#getMultipleStatistics success
	 * Expect AE manual mode get data success
	 */
	@Test
	@Category(DevelopmentTest.class)
	public void testAverPTZCommunicatorAEManualMode() throws Exception {
		// Change to AE manual mode
		byte[] responseAEMode = averPTZCommunicator.send(buildSendPacket(1, 17, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				PayloadCategory.CAMERA.getCode(), Command.AE_MODE.getCode(), AEMode.MANUAL.getCode()));
		averPTZCommunicator.digestResponse(responseAEMode, 17, CommandType.COMMAND, null);

		extendedStatistic = (ExtendedStatistics) averPTZCommunicator.getMultipleStatistics().get(0);
		advancedControllableProperties = extendedStatistic.getControllableProperties();

		for (AdvancedControllableProperty property : advancedControllableProperties) {
			if (property.getName().equalsIgnoreCase(Command.EXPOSURE.getName() + HASH + Command.AE_MODE.getName())) {
				String aeMode = (String) property.getValue();
				Assert.assertEquals(AEMode.MANUAL.getName(), aeMode);
				return;
			}
		}

		// Value text
		String value = stats.get(Command.EXPOSURE.getName() + HASH + Command.SHUTTER_CURRENT.getName());
		Assert.assertTrue(SHUTTER_VALUES.contains(value));

		value = stats.get(Command.EXPOSURE.getName() + HASH + Command.GAIN_CURRENT.getName());
		Assert.assertTrue(Integer.parseInt(value) >= 0 && Integer.parseInt(value) <= 48);

		value = stats.get(Command.EXPOSURE.getName() + HASH + Command.IRIS_CURRENT.getName());
		Assert.assertTrue(IRIS_LEVELS.contains(value));

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
	}

	/**
	 * Test AxisCommunicator#getMultipleStatistics success
	 * Expect AE iris priority mode get data success
	 */
	@Test
	@Category(DevelopmentTest.class)
	public void testAverPTZCommunicatorAEIrisPriorityMode() throws Exception {
		// Change to AE iris priority mode
		byte[] responseAEMode = averPTZCommunicator.send(buildSendPacket(1, 18, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				PayloadCategory.CAMERA.getCode(), Command.AE_MODE.getCode(), AEMode.IRIS_PRIORITY.getCode()));
		averPTZCommunicator.digestResponse(responseAEMode, 18, CommandType.COMMAND, null);

		extendedStatistic = (ExtendedStatistics) averPTZCommunicator.getMultipleStatistics().get(0);
		advancedControllableProperties = extendedStatistic.getControllableProperties();

		for (AdvancedControllableProperty property : advancedControllableProperties) {
			if (property.getName().equalsIgnoreCase(Command.EXPOSURE.getName() + HASH + Command.AE_MODE.getName())) {
				String aeMode = (String) property.getValue();
				Assert.assertEquals(AEMode.IRIS_PRIORITY.getName(), aeMode);
				return;
			}
		}

		// Value text
		String value = stats.get(Command.EXPOSURE.getName() + HASH + Command.EXP_COMP_CURRENT.getName());
		Assert.assertTrue(Integer.parseInt(value) >= -4 && Integer.parseInt(value) <= 4);

		value = stats.get(Command.EXPOSURE.getName() + HASH + Command.GAIN_LIMIT_CURRENT.getName());
		Assert.assertTrue(Integer.parseInt(value) >= 24 && Integer.parseInt(value) <= 48);

		value = stats.get(Command.EXPOSURE.getName() + HASH + Command.IRIS_CURRENT.getName());
		Assert.assertTrue(IRIS_LEVELS.contains(value));

		// Value for slider
		for (AdvancedControllableProperty property : advancedControllableProperties) {
			if (property.getName().equalsIgnoreCase(Command.EXPOSURE.getName() + HASH + Command.EXP_COMP_DIRECT.getName())) {
				Assert.assertTrue((float) property.getValue() >= 1 && (float) property.getValue() <= 9);
			}

			if (property.getName().equalsIgnoreCase(Command.EXPOSURE.getName() + HASH + Command.GAIN_LIMIT_DIRECT.getName())) {
				Assert.assertTrue((float) property.getValue() >= 0 && (float) property.getValue() <= 8);
			}

			if (property.getName().equalsIgnoreCase(Command.EXPOSURE.getName() + HASH + Command.IRIS_DIRECT.getName())) {
				Assert.assertTrue((float) property.getValue() >= 0 && (float) property.getValue() <= 13);
			}
		}
	}

	/**
	 * Test AxisCommunicator#getMultipleStatistics success
	 * Expect AE shutter priority mode get data success
	 */
	@Test
	@Category(DevelopmentTest.class)
	public void testAverPTZCommunicatorAEShutterPriorityMode() throws Exception {
		// Change to AE shutter priority mode
		byte[] responseAEMode = averPTZCommunicator.send(buildSendPacket(1, 19, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				PayloadCategory.CAMERA.getCode(), Command.AE_MODE.getCode(), AEMode.SHUTTER_PRIORITY.getCode()));
		averPTZCommunicator.digestResponse(responseAEMode, 19, CommandType.COMMAND, null);

		extendedStatistic = (ExtendedStatistics) averPTZCommunicator.getMultipleStatistics().get(0);
		advancedControllableProperties = extendedStatistic.getControllableProperties();

		for (AdvancedControllableProperty property : advancedControllableProperties) {
			if (property.getName().equalsIgnoreCase(Command.EXPOSURE.getName() + HASH + Command.AE_MODE.getName())) {
				String aeMode = (String) property.getValue();
				Assert.assertEquals(AEMode.SHUTTER_PRIORITY.getName(), aeMode);
				return;
			}
		}

		// Value text
		String value = stats.get(Command.EXPOSURE.getName() + HASH + Command.EXP_COMP_CURRENT.getName());
		Assert.assertTrue(Integer.parseInt(value) >= -4 && Integer.parseInt(value) <= 4);

		value = stats.get(Command.EXPOSURE.getName() + HASH + Command.GAIN_LIMIT_CURRENT.getName());
		Assert.assertTrue(Integer.parseInt(value) >= 24 && Integer.parseInt(value) <= 48);

		value = stats.get(Command.EXPOSURE.getName() + HASH + Command.SHUTTER_CURRENT.getName());
		Assert.assertTrue(SHUTTER_VALUES.contains(value));

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
	}

	/**
	 * Test AxisCommunicator#getMultipleStatistics success
	 * Expect slow shutter status on in AE full auto mode get data success
	 */
	@Test
	@Category(DevelopmentTest.class)
	public void testAverPTZCommunicatorSlowShutterStatusOnAEFullAutoMode() throws Exception {
		// Change to AE full auto mode
		byte[] responseAEMode = averPTZCommunicator.send(buildSendPacket(1, 20, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				PayloadCategory.CAMERA.getCode(), Command.AE_MODE.getCode(), AEMode.FULL_AUTO.getCode()));
		averPTZCommunicator.digestResponse(responseAEMode, 20, CommandType.COMMAND, null);

		// Change to slow shutter status on mode
		byte[] response = averPTZCommunicator.send(buildSendPacket(1, 21, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				PayloadCategory.CAMERA.getCode(), Command.AUTO_SLOW_SHUTTER.getCode(), SlowShutterStatus.ON.getCode()));
		averPTZCommunicator.digestResponse(response, 21, CommandType.COMMAND, null);

		extendedStatistic = (ExtendedStatistics) averPTZCommunicator.getMultipleStatistics().get(0);
		advancedControllableProperties = extendedStatistic.getControllableProperties();

		for (AdvancedControllableProperty property : advancedControllableProperties) {
			String propertyName = property.getName();
			if (propertyName.equalsIgnoreCase(Command.EXPOSURE.getName() + HASH + Command.AUTO_SLOW_SHUTTER.getName())) {
				Assert.assertEquals(1, (int) property.getValue());
				return;
			}
		}
	}

	/**
	 * Test AxisCommunicator#getMultipleStatistics success
	 * Expect slow shutter status off in AE full auto mode get data success
	 */
	@Test
	@Category(DevelopmentTest.class)
	public void testAverPTZCommunicatorSlowShutterStatusOffAEFullAutoMode() throws Exception {
		// Change to AE full auto mode
		byte[] responseAEMode = averPTZCommunicator.send(buildSendPacket(1, 22, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				PayloadCategory.CAMERA.getCode(), Command.AE_MODE.getCode(), AEMode.FULL_AUTO.getCode()));
		averPTZCommunicator.digestResponse(responseAEMode, 22, CommandType.COMMAND, null);

		// Change to slow shutter status off mode
		byte[] response = averPTZCommunicator.send(buildSendPacket(1, 23, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				PayloadCategory.CAMERA.getCode(), Command.AUTO_SLOW_SHUTTER.getCode(), SlowShutterStatus.OFF.getCode()));
		averPTZCommunicator.digestResponse(response, 23, CommandType.COMMAND, null);

		extendedStatistic = (ExtendedStatistics) averPTZCommunicator.getMultipleStatistics().get(0);
		advancedControllableProperties = extendedStatistic.getControllableProperties();

		for (AdvancedControllableProperty property : advancedControllableProperties) {
			String propertyName = property.getName();
			if (propertyName.equalsIgnoreCase(Command.EXPOSURE.getName() + HASH + Command.AUTO_SLOW_SHUTTER.getName())) {
				Assert.assertEquals(0, (int) property.getValue());
				return;
			}
		}
	}

	/**
	 * Test AxisCommunicator#getMultipleStatistics success
	 * Expect slow shutter status on in AE iris priority mode get data success
	 */
	@Test
	@Category(DevelopmentTest.class)
	public void testAverPTZCommunicatorSlowShutterStatusOnAEIrisPriorityMode() throws Exception {
		// Change to AE iris priority mode
		byte[] responseAEMode = averPTZCommunicator.send(buildSendPacket(1, 24, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				PayloadCategory.CAMERA.getCode(), Command.AE_MODE.getCode(), AEMode.IRIS_PRIORITY.getCode()));
		averPTZCommunicator.digestResponse(responseAEMode, 24, CommandType.COMMAND, null);

		// Change to slow shutter status on mode
		byte[] response = averPTZCommunicator.send(buildSendPacket(1, 25, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				PayloadCategory.CAMERA.getCode(), Command.AUTO_SLOW_SHUTTER.getCode(), SlowShutterStatus.ON.getCode()));
		averPTZCommunicator.digestResponse(response, 25, CommandType.COMMAND, null);

		extendedStatistic = (ExtendedStatistics) averPTZCommunicator.getMultipleStatistics().get(0);
		advancedControllableProperties = extendedStatistic.getControllableProperties();

		for (AdvancedControllableProperty property : advancedControllableProperties) {
			String propertyName = property.getName();
			if (propertyName.equalsIgnoreCase(Command.EXPOSURE.getName() + HASH + Command.AUTO_SLOW_SHUTTER.getName())) {
				Assert.assertEquals(1, (int) property.getValue());
				return;
			}
		}
	}

	/**
	 * Test AxisCommunicator#getMultipleStatistics success
	 * Expect slow shutter status off in AE iris priority mode get data success
	 */
	@Test
	@Category(DevelopmentTest.class)
	public void testAverPTZCommunicatorSlowShutterStatusOffAEIrisPriorityMode() throws Exception {
		// Change to AE iris priority mode
		byte[] responseAEMode = averPTZCommunicator.send(buildSendPacket(1, 26, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				PayloadCategory.CAMERA.getCode(), Command.AE_MODE.getCode(), AEMode.IRIS_PRIORITY.getCode()));
		averPTZCommunicator.digestResponse(responseAEMode, 26, CommandType.COMMAND, null);

		// Change to slow shutter status on mode
		byte[] response = averPTZCommunicator.send(buildSendPacket(1, 27, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				PayloadCategory.CAMERA.getCode(), Command.AUTO_SLOW_SHUTTER.getCode(), SlowShutterStatus.OFF.getCode()));
		averPTZCommunicator.digestResponse(response, 27, CommandType.COMMAND, null);

		extendedStatistic = (ExtendedStatistics) averPTZCommunicator.getMultipleStatistics().get(0);
		advancedControllableProperties = extendedStatistic.getControllableProperties();

		for (AdvancedControllableProperty property : advancedControllableProperties) {
			String propertyName = property.getName();
			if (propertyName.equalsIgnoreCase(Command.EXPOSURE.getName() + HASH + Command.AUTO_SLOW_SHUTTER.getName())) {
				Assert.assertEquals(0, (int) property.getValue());
				return;
			}
		}
	}

	/**
	 * Test AxisCommunicator#getMultipleStatistics success
	 * Expect WB auto mode get data success
	 */
	@Test
	@Category(DevelopmentTest.class)
	public void testAverPTZCommunicatorWBAutoMode() throws Exception {
		// Change to WB auto mode
		byte[] responseAEMode = averPTZCommunicator.send(buildSendPacket(1, 28, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				PayloadCategory.CAMERA.getCode(), Command.WB_MODE.getCode(), WBMode.AUTO.getCode()));
		averPTZCommunicator.digestResponse(responseAEMode, 28, CommandType.COMMAND, null);

		extendedStatistic = (ExtendedStatistics) averPTZCommunicator.getMultipleStatistics().get(0);
		advancedControllableProperties = extendedStatistic.getControllableProperties();

		for (AdvancedControllableProperty property : advancedControllableProperties) {
			if (property.getName().equalsIgnoreCase(Command.IMAGE_PROCESS.getName() + HASH + Command.WB_MODE.getName())) {
				String wbMode = (String) property.getValue();
				Assert.assertEquals(WBMode.AUTO.getName(), wbMode);
				return;
			}
		}
	}

	/**
	 * Test AxisCommunicator#getMultipleStatistics success
	 * Expect WB indoor mode get data success
	 */
	@Test
	@Category(DevelopmentTest.class)
	public void testAverPTZCommunicatorWBIndoorMode() throws Exception {
		// Change to WB indoor mode
		byte[] responseAEMode = averPTZCommunicator.send(buildSendPacket(1, 29, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				PayloadCategory.CAMERA.getCode(), Command.WB_MODE.getCode(), WBMode.INDOOR.getCode()));
		averPTZCommunicator.digestResponse(responseAEMode, 29, CommandType.COMMAND, null);

		extendedStatistic = (ExtendedStatistics) averPTZCommunicator.getMultipleStatistics().get(0);
		advancedControllableProperties = extendedStatistic.getControllableProperties();

		for (AdvancedControllableProperty property : advancedControllableProperties) {
			if (property.getName().equalsIgnoreCase(Command.IMAGE_PROCESS.getName() + HASH + Command.WB_MODE.getName())) {
				String wbMode = (String) property.getValue();
				Assert.assertEquals(WBMode.INDOOR.getName(), wbMode);
				return;
			}
		}
	}

	/**
	 * Test AxisCommunicator#getMultipleStatistics success
	 * Expect WB outdoor mode get data success
	 */
	@Test
	@Category(DevelopmentTest.class)
	public void testAverPTZCommunicatorWBOutdoorMode() throws Exception {
		// Change to WB outdoor mode
		byte[] responseAEMode = averPTZCommunicator.send(buildSendPacket(1, 30, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				PayloadCategory.CAMERA.getCode(), Command.WB_MODE.getCode(), WBMode.OUTDOOR.getCode()));
		averPTZCommunicator.digestResponse(responseAEMode, 30, CommandType.COMMAND, null);

		extendedStatistic = (ExtendedStatistics) averPTZCommunicator.getMultipleStatistics().get(0);
		advancedControllableProperties = extendedStatistic.getControllableProperties();

		for (AdvancedControllableProperty property : advancedControllableProperties) {
			if (property.getName().equalsIgnoreCase(Command.IMAGE_PROCESS.getName() + HASH + Command.WB_MODE.getName())) {
				String wbMode = (String) property.getValue();
				Assert.assertEquals(WBMode.OUTDOOR.getName(), wbMode);
				return;
			}
		}
	}

	/**
	 * Test AxisCommunicator#getMultipleStatistics success
	 * Expect WB one push mode get data success
	 */
	@Test
	@Category(DevelopmentTest.class)
	public void testAverPTZCommunicatorWBOnePushMode() throws Exception {
		// Change to WB one push mode
		byte[] responseAEMode = averPTZCommunicator.send(buildSendPacket(1, 31, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				PayloadCategory.CAMERA.getCode(), Command.WB_MODE.getCode(), WBMode.ONE_PUSH_WB.getCode()));
		averPTZCommunicator.digestResponse(responseAEMode, 31, CommandType.COMMAND, null);

		extendedStatistic = (ExtendedStatistics) averPTZCommunicator.getMultipleStatistics().get(0);
		advancedControllableProperties = extendedStatistic.getControllableProperties();
		stats = extendedStatistic.getStatistics();

		// Check contain button trigger one push WB
		Assert.assertTrue(stats.containsKey(Command.IMAGE_PROCESS.getName() + HASH + Command.WB_ONE_PUSH_TRIGGER.getName()));

		for (AdvancedControllableProperty property : advancedControllableProperties) {
			if (property.getName().equalsIgnoreCase(Command.IMAGE_PROCESS.getName() + HASH + Command.WB_MODE.getName())) {
				String wbMode = (String) property.getValue();
				Assert.assertEquals(WBMode.ONE_PUSH_WB.getName(), wbMode);
				return;
			}
		}
	}

	/**
	 * Test AxisCommunicator#getMultipleStatistics success
	 * Expect WB manual mode get data success
	 */
	@Test
	@Category(DevelopmentTest.class)
	public void testAverPTZCommunicatorWBManualMode() throws Exception {
		// Change to WB manual mode
		byte[] responseAEMode = averPTZCommunicator.send(buildSendPacket(1, 32, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(),
				PayloadCategory.CAMERA.getCode(), Command.WB_MODE.getCode(), WBMode.MANUAL.getCode()));
		averPTZCommunicator.digestResponse(responseAEMode, 32, CommandType.COMMAND, null);

		extendedStatistic = (ExtendedStatistics) averPTZCommunicator.getMultipleStatistics().get(0);
		advancedControllableProperties = extendedStatistic.getControllableProperties();
		stats = extendedStatistic.getStatistics();

		// Check current RGain value
		String value = stats.get(Command.IMAGE_PROCESS.getName() + HASH + Command.RGAIN_INQ.getName());
		Assert.assertTrue(Integer.parseInt(value) >= 0 && Integer.parseInt(value) <= 255);

		// Check current BGain value
		value = stats.get(Command.IMAGE_PROCESS.getName() + HASH + Command.BGAIN_INQ.getName());
		Assert.assertTrue(Integer.parseInt(value) >= 0 && Integer.parseInt(value) <= 255);

		for (AdvancedControllableProperty property : advancedControllableProperties) {
			if (property.getName().equalsIgnoreCase(Command.IMAGE_PROCESS.getName() + HASH + Command.WB_MODE.getName())) {
				String wbMode = (String) property.getValue();
				Assert.assertEquals(WBMode.MANUAL.getName(), wbMode);
			}
		}
	}
}
