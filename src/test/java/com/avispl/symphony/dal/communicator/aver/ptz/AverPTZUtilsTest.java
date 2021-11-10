/*
 * Copyright (c) 2021 AVI-SPL Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.aver.ptz;

import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZUtils.buildSendPacket;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZUtils.convertOneByteNumberToTwoBytesArray;
import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.Category;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.PayloadType;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.command.Command;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.command.CommandType;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.AEMode;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.BGainControl;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.BacklightStatus;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.FocusControl;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.FocusStatus;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.PanTiltDrive;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.PowerStatus;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.PresetControl;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.RGainControl;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.SlowPanTiltStatus;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.SlowShutterStatus;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.WBMode;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.ZoomControl;

/**
 * Unit test for AverPTZ Utils - method build send string
 * Build send string which match expected string
 *
 * @author Harry
 * @version 1.0
 * @since 1.0
 */
public class AverPTZUtilsTest {
	int cameraID = 1;
	int sequenceNumber = 1;
	int panSpeed = 20;
	int tiltSpeed = 15;
	ByteArrayOutputStream outputStream;

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for power on which match expected packet
	 */
	@Test
	public void testBuildSendPacketPowerOn() {
		byte[] expectedPacketPowerOn = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x00, 0x02, (byte) 0xFF };
		byte[] actualPacketPowerOn = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.POWER.getCode(), PowerStatus.ON.getCode());

		assertArrayEquals(expectedPacketPowerOn, actualPacketPowerOn);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for power off which match expected packet
	 */
	@Test
	public void testBuildSendPacketPowerOff() {
		byte[] expectedPacketPowerOff = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x00, 0x03, (byte) 0xFF };
		byte[] actualPacketPowerOff = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.POWER.getCode(), PowerStatus.OFF.getCode());

		assertArrayEquals(expectedPacketPowerOff, actualPacketPowerOff);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for zoom tele which match expected packet
	 */
	@Test
	public void testBuildSendPacketZoomTele() {
		byte[] expectedPacketZoomTele = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x07, 0x02, (byte) 0xFF };
		byte[] actualPacketZoomTele = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.ZOOM.getCode(), ZoomControl.TELE.getCode());

		assertArrayEquals(expectedPacketZoomTele, actualPacketZoomTele);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for zoom wide which match expected packet
	 */
	@Test
	public void testBuildSendPacketZoomWide() {
		byte[] expectedPacketZoomWide = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x07, 0x03, (byte) 0xFF };
		byte[] actualPacketZoomWide = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.ZOOM.getCode(), ZoomControl.WIDE.getCode());

		assertArrayEquals(expectedPacketZoomWide, actualPacketZoomWide);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for focus far which match expected packet
	 */
	@Test
	public void testBuildSendPacketFocusFar() {
		byte[] expectedPacketFocusFar = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x08, 0x02, (byte) 0xFF };
		byte[] actualPacketFocusFar = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.FOCUS.getCode(), FocusControl.FAR.getCode());

		assertArrayEquals(expectedPacketFocusFar, actualPacketFocusFar);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for focus near which match expected packet
	 */
	@Test
	public void testBuildSendPacketFocusNear() {
		byte[] expectedPacketFocusNear = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x08, 0x03, (byte) 0xFF };
		byte[] actualPacketFocusNear = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.FOCUS.getCode(), FocusControl.NEAR.getCode());

		assertArrayEquals(expectedPacketFocusNear, actualPacketFocusNear);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for auto-focus near which match expected packet
	 */
	@Test
	public void testBuildSendPacketAutoFocus() {
		byte[] expectedPacketFocusAutoFocus = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x38, 0x02, (byte) 0xFF };
		byte[] actualPacketFocusAutoFocus = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.FOCUS_MODE.getCode(), FocusStatus.AUTO.getCode());
		assertArrayEquals(expectedPacketFocusAutoFocus, actualPacketFocusAutoFocus);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for manual-focus near which match expected packet
	 */
	@Test
	public void testBuildSendPacketManualFocus() {
		byte[] expectedPacketFocusManualFocus = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x38, 0x03, (byte) 0xFF };
		byte[] actualPacketFocusManualFocus = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.FOCUS_MODE.getCode(), FocusStatus.MANUAL.getCode());

		assertArrayEquals(expectedPacketFocusManualFocus, actualPacketFocusManualFocus);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for focus one push near which match expected packet
	 */
	@Test
	public void testBuildSendPacketFocusOnePush() {
		byte[] expectedPacketFocusOnePush = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x18, 0x01, (byte) 0xFF };
		byte[] actualPacketFocusOnePush = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.FOCUS_ONE_PUSH.getCode());

		assertArrayEquals(expectedPacketFocusOnePush, actualPacketFocusOnePush);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for WB auto mode which match expected packet
	 */
	@Test
	public void testBuildSendPacketWBAutoMode() {
		byte[] expectedPacketWBAuto = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x35, 0x00, (byte) 0xFF };
		byte[] actualPacketWBAuto = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.WB_MODE.getCode(), WBMode.AUTO.getCode());

		assertArrayEquals(expectedPacketWBAuto, actualPacketWBAuto);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for WB indoor mode which match expected packet
	 */
	@Test
	public void testBuildSendPacketWBIndoorMode() {
		byte[] expectedPacketWBIndoor = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x35, 0x01, (byte) 0xFF };
		byte[] actualPacketWBIndoor = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.WB_MODE.getCode(), WBMode.INDOOR.getCode());

		assertArrayEquals(expectedPacketWBIndoor, actualPacketWBIndoor);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for WB outdoor mode which match expected packet
	 */
	@Test
	public void testBuildSendPacketWBOutdoorMode() {
		byte[] expectedPacketWBOutdoor = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x35, 0x02, (byte) 0xFF };
		byte[] actualPacketWBOutdoor = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.WB_MODE.getCode(), WBMode.OUTDOOR.getCode());

		assertArrayEquals(expectedPacketWBOutdoor, actualPacketWBOutdoor);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for WB one push mode which match expected packet
	 */
	@Test
	public void testBuildSendPacketWBOnePushMode() {
		byte[] expectedPacketWBOnePush = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x35, 0x03, (byte) 0xFF };
		byte[] actualPacketWBOnePush = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.WB_MODE.getCode(), WBMode.ONE_PUSH_WB.getCode());

		assertArrayEquals(expectedPacketWBOnePush, actualPacketWBOnePush);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for WB manual mode which match expected packet
	 */
	@Test
	public void testBuildSendPacketWBManualMode() {
		byte[] expectedPacketWBManual = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x35, 0x05, (byte) 0xFF };
		byte[] actualPacketWBManual = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.WB_MODE.getCode(), WBMode.MANUAL.getCode());

		assertArrayEquals(expectedPacketWBManual, actualPacketWBManual);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for WB one push trigger which match expected packet
	 */
	@Test
	public void testBuildSendPacketWBOnePushTrigger() {
		byte[] expectedPacketWBOnePushTrigger = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x10, 0x05, (byte) 0xFF };
		byte[] actualPacketWBOnePushTrigger = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.WB_ONE_PUSH_TRIGGER.getCode());

		assertArrayEquals(expectedPacketWBOnePushTrigger, actualPacketWBOnePushTrigger);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for RGain up control which match expected packet
	 */
	@Test
	public void testBuildSendPacketRGainUp() {
		byte[] expectedPacketRGainUp = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x03, 0x02, (byte) 0xFF };
		byte[] actualPacketRGainUp = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.RGAIN.getCode(), RGainControl.UP.getCode());

		assertArrayEquals(expectedPacketRGainUp, actualPacketRGainUp);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for RGain down control which match expected packet
	 */
	@Test
	public void testBuildSendPacketRGainDown() {
		byte[] expectedPacketRGainDown = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x03, 0x03, (byte) 0xFF };
		byte[] actualPacketRGainDown = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.RGAIN.getCode(), RGainControl.DOWN.getCode());

		assertArrayEquals(expectedPacketRGainDown, actualPacketRGainDown);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for BGain up control which match expected packet
	 */
	@Test
	public void testBuildSendPacketBGainUp() {
		byte[] expectedPacketBGainUp = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x04, 0x02, (byte) 0xFF };
		byte[] actualPacketBGainUp = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.BGAIN.getCode(), BGainControl.UP.getCode());

		assertArrayEquals(expectedPacketBGainUp, actualPacketBGainUp);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for BGain down control which match expected packet
	 */
	@Test
	public void testBuildSendPacketBGainDown() {
		byte[] expectedPacketBGainDown = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x04, 0x03, (byte) 0xFF };
		byte[] actualPacketBGainDown = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.BGAIN.getCode(), BGainControl.DOWN.getCode());

		assertArrayEquals(expectedPacketBGainDown, actualPacketBGainDown);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for AE full auto mode which match expected packet
	 */
	@Test
	public void testBuildSendPacketAEFullAutoMode() {
		byte[] expectedPacketAEFullAuto = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x39, 0x00, (byte) 0xFF };
		byte[] actualPacketAEFullAuto = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.AE_MODE.getCode(), AEMode.FULL_AUTO.getCode());

		assertArrayEquals(expectedPacketAEFullAuto, actualPacketAEFullAuto);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for AE manual mode which match expected packet
	 */
	@Test
	public void testBuildSendPacketAEManualMode() {
		byte[] expectedPacketAEManual = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x39, 0x03, (byte) 0xFF };
		byte[] actualPacketAEManual = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.AE_MODE.getCode(), AEMode.MANUAL.getCode());

		assertArrayEquals(expectedPacketAEManual, actualPacketAEManual);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for AE shutter priority mode which match expected packet
	 */
	@Test
	public void testBuildSendPacketAEShutterPriorityMode() {
		byte[] expectedPacketAEShutterPriority = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x39, 0x0A, (byte) 0xFF };
		byte[] actualPacketAEShutterPriority = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.AE_MODE.getCode(), AEMode.SHUTTER_PRIORITY.getCode());

		assertArrayEquals(expectedPacketAEShutterPriority, actualPacketAEShutterPriority);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for AE iris priority mode which match expected packet
	 */
	@Test
	public void testBuildSendPacketAEIrisPriorityMode() {
		byte[] expectedPacketAEIrisPriority = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x39, 0x0B, (byte) 0xFF };
		byte[] actualPacketAEIrisPriority = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.AE_MODE.getCode(), AEMode.IRIS_PRIORITY.getCode());

		assertArrayEquals(expectedPacketAEIrisPriority, actualPacketAEIrisPriority);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for Slow shutter on which match expected packet
	 */
	@Test
	public void testBuildSendPacketSlowShutterOn() {
		byte[] expectedPacketSlowShutterAuto = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x5A, 0x02, (byte) 0xFF };
		byte[] actualPacketSlowShutterAuto = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.SLOW_SHUTTER.getCode(), SlowShutterStatus.ON.getCode());

		assertArrayEquals(expectedPacketSlowShutterAuto, actualPacketSlowShutterAuto);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for Slow shutter off which match expected packet
	 */
	@Test
	public void testBuildSendPacketSlowShutterOff() {
		byte[] expectedPacketSlowShutterManual = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x5A, 0x03, (byte) 0xFF };
		byte[] actualPacketSlowShutterManual = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.SLOW_SHUTTER.getCode(), SlowShutterStatus.OFF.getCode());

		assertArrayEquals(expectedPacketSlowShutterManual, actualPacketSlowShutterManual);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for shutter direct which match expected packet
	 */
	@Test
	public void testBuildSendPacketShutterDirect() {
		int shutterPosition = 40;

		byte[] expectedPacketShutterDirect = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x4A, 0x00, 0x00, 0x02, 0x08, (byte) 0xFF };
		byte[] actualPacketShutterDirect = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.SHUTTER_DIRECT.getCode(), convertOneByteNumberToTwoBytesArray((byte) shutterPosition));

		assertArrayEquals(expectedPacketShutterDirect, actualPacketShutterDirect);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for iris direct which match expected packet
	 */
	@Test
	public void testBuildSendPacketIrisDirect() {
		int irisPosition = 9;

		byte[] expectedPacketIrisDirect = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x4B, 0x00, 0x00, 0x00, 0x09, (byte) 0xFF };
		byte[] actualPacketIrisDirect = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.IRIS_DIRECT.getCode(), convertOneByteNumberToTwoBytesArray((byte) irisPosition));

		assertArrayEquals(expectedPacketIrisDirect, actualPacketIrisDirect);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for gain direct which match expected packet
	 */
	@Test
	public void testBuildSendPacketGainDirect() {
		int gainPosition = 23;

		byte[] expectedPacketGainDirect = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x4C, 0x00, 0x00, 0x01, 0x07, (byte) 0xFF };
		byte[] actualPacketGainDirect = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.GAIN_DIRECT.getCode(), convertOneByteNumberToTwoBytesArray((byte) gainPosition));

		assertArrayEquals(expectedPacketGainDirect, actualPacketGainDirect);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for gain limit direct which match expected packet
	 */
	@Test
	public void testBuildSendPacketGainLimitDirect() {
		int gainPosition = 23;

		byte[] expectedPacketGainLimitDirect = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x2C, 0x17, (byte) 0xFF };
		byte[] actualPacketGainLimitDirect = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.GAIN_LIMIT_DIRECT.getCode(), (byte) gainPosition);

		assertArrayEquals(expectedPacketGainLimitDirect, actualPacketGainLimitDirect);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for gain exp comp direct which match expected packet
	 */
	@Test
	public void testBuildSendPacketExpCompDirect() {
		int expComp = 5;

		byte[] expectedPacketExpCompDirect = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x4E, 0x00, 0x00, 0x00, 0x05, (byte) 0xFF };
		byte[] actualPacketExpCompDirect = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.EXP_COMP_DIRECT.getCode(), convertOneByteNumberToTwoBytesArray((byte) expComp));

		assertArrayEquals(expectedPacketExpCompDirect, actualPacketExpCompDirect);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for backlight on which match expected packet
	 */
	@Test
	public void testBuildSendPacketBacklightOn() {
		byte[] expectedPacketBackLightOn = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x33, 0x02, (byte) 0xFF };
		byte[] actualPacketBackLightOn = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.BACKLIGHT.getCode(), BacklightStatus.ON.getCode());

		assertArrayEquals(expectedPacketBackLightOn, actualPacketBackLightOn);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for backlight off which match expected packet
	 */
	@Test
	public void testBuildSendPacketBacklightOff() {
		byte[] expectedPacketBackLightOff = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x33, 0x03, (byte) 0xFF };
		byte[] actualPacketBackLightOff = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.BACKLIGHT.getCode(), BacklightStatus.OFF.getCode());

		assertArrayEquals(expectedPacketBackLightOff, actualPacketBackLightOff);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for preset set which match expected packet
	 */
	@Test
	public void testBuildSendPacketPresetSet() {
		int preset = 2;
		byte[] expectedPacketSetPreset = new byte[] { 0x01, 0x00, 0x00, 0x07, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x3F, 0x01, (byte) preset, (byte) 0xFF };
		byte[] actualPacketSetPreset = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.PRESET.getCode(), PresetControl.SET.getCode(), (byte) preset);

		assertArrayEquals(expectedPacketSetPreset, actualPacketSetPreset);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for preset recall which match expected packet
	 */
	@Test
	public void testBuildSendPacketPresetRecall() {
		int preset = 2;
		byte[] expectedPacketLoadPreset = new byte[] { 0x01, 0x00, 0x00, 0x07, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x3F, 0x02, (byte) preset, (byte) 0xFF };
		byte[] actualPacketLoadPreset = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.PRESET.getCode(), PresetControl.RECALL.getCode(), (byte) preset);

		assertArrayEquals(expectedPacketLoadPreset, actualPacketLoadPreset);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for slow pan-tilt on which match expected packet
	 */
	@Test
	public void testBuildSendPacketSlowPanTiltOn() {
		byte[] expectedPacketSlowPanTiltOn = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x06, 0x44, 0x02, (byte) 0xFF };
		byte[] actualPacketSlowPanTiltOn = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.SLOW_PAN_TILT.getCode(), SlowPanTiltStatus.ON.getCode());

		assertArrayEquals(expectedPacketSlowPanTiltOn, actualPacketSlowPanTiltOn);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for slow pan-tilt off which match expected packet
	 */
	@Test
	public void testBuildSendPacketSlowPanTiltOff() {
		byte[] expectedPacketSlowPanTiltOff = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x06, 0x44, 0x03, (byte) 0xFF };
		byte[] actualPacketSlowPanTiltOff = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.SLOW_PAN_TILT.getCode(), SlowPanTiltStatus.OFF.getCode());

		assertArrayEquals(expectedPacketSlowPanTiltOff, actualPacketSlowPanTiltOff);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for pan-tilt drive up which match expected packet
	 */
	@Test
	public void testBuildSendPacketPanTiltDriveUp() throws IOException {
		outputStream = new ByteArrayOutputStream();
		outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStream.write(PanTiltDrive.UP.getCode());

		byte[] expectedPacketPanTiltUp = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x06, 0x01, (byte) panSpeed, (byte) tiltSpeed, 0x03, 0x01, (byte) 0xFF };
		byte[] actualPacketPanTiltUp = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());

		assertArrayEquals(expectedPacketPanTiltUp, actualPacketPanTiltUp);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for pan-tilt drive down which match expected packet
	 */
	@Test
	public void testBuildSendPacketPanTiltDriveDown() throws IOException {
		outputStream = new ByteArrayOutputStream();
		outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStream.write(PanTiltDrive.DOWN.getCode());

		byte[] expectedPacketPanTiltDown = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x06, 0x01, (byte) panSpeed, (byte) tiltSpeed, 0x03, 0x02, (byte) 0xFF };
		byte[] actualPacketPanTiltDown = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());

		assertArrayEquals(expectedPacketPanTiltDown, actualPacketPanTiltDown);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for pan-tilt drive left which match expected packet
	 */
	@Test
	public void testBuildSendPacketPanTiltDriveLeft() throws IOException {
		outputStream = new ByteArrayOutputStream();
		outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStream.write(PanTiltDrive.LEFT.getCode());

		byte[] expectedPacketPanTiltLeft = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x06, 0x01, (byte) panSpeed, (byte) tiltSpeed, 0x01, 0x03, (byte) 0xFF };
		byte[] actualPacketPanTiltLeft = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());

		assertArrayEquals(expectedPacketPanTiltLeft, actualPacketPanTiltLeft);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for pan-tilt drive right which match expected packet
	 */
	@Test
	public void testBuildSendPacketPanTiltDriveRight() throws IOException {
		outputStream = new ByteArrayOutputStream();
		outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStream.write(PanTiltDrive.RIGHT.getCode());

		byte[] expectedPacketPanTiltRight = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x06, 0x01, (byte) panSpeed, (byte) tiltSpeed, 0x02, 0x03, (byte) 0xFF };
		byte[] actualPacketPanTiltRight = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());

		assertArrayEquals(expectedPacketPanTiltRight, actualPacketPanTiltRight);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for pan-tilt drive down left which match expected packet
	 */
	@Test
	public void testBuildSendPacketPanTiltDriveDownLeft() throws IOException {
		outputStream = new ByteArrayOutputStream();
		outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStream.write(PanTiltDrive.DOWN_LEFT.getCode());

		byte[] expectedPacketPanTilDownLeft = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x06, 0x01, (byte) panSpeed, (byte) tiltSpeed, 0x01, 0x02, (byte) 0xFF };
		byte[] actualPacketPanTiltDownLeft = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());

		assertArrayEquals(expectedPacketPanTilDownLeft, actualPacketPanTiltDownLeft);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for pan-tilt drive down right which match expected packet
	 */
	@Test
	public void testBuildSendPacketPanTiltDriveDownRight() throws IOException {
		outputStream = new ByteArrayOutputStream();
		outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStream.write(PanTiltDrive.DOWN_RIGHT.getCode());

		byte[] expectedPacketPanTilDownRight = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x06, 0x01, (byte) panSpeed, (byte) tiltSpeed, 0x02, 0x02, (byte) 0xFF };
		byte[] actualPacketPanTiltDownRight = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());

		assertArrayEquals(expectedPacketPanTilDownRight, actualPacketPanTiltDownRight);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for pan-tilt drive up left which match expected packet
	 */
	@Test
	public void testBuildSendPacketPanTiltDriveUpLeft() throws IOException {
		outputStream = new ByteArrayOutputStream();
		outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStream.write(PanTiltDrive.UP_LEFT.getCode());

		byte[] expectedPacketPanTilUpLeft = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x06, 0x01, (byte) panSpeed, (byte) tiltSpeed, 0x01, 0x01, (byte) 0xFF };
		byte[] actualPacketPanTiltUpLeft = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());

		assertArrayEquals(expectedPacketPanTilUpLeft, actualPacketPanTiltUpLeft);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for pan-tilt drive up right which match expected packet
	 */
	@Test
	public void testBuildSendPacketPanTiltDriveUpRight() throws IOException {
		outputStream = new ByteArrayOutputStream();
		outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStream.write(PanTiltDrive.UP_RIGHT.getCode());

		byte[] expectedPacketPanTilUpRight = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x06, 0x01, (byte) panSpeed, (byte) tiltSpeed, 0x02, 0x01, (byte) 0xFF };
		byte[] actualPacketPanTiltUpRight = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());

		assertArrayEquals(expectedPacketPanTilUpRight, actualPacketPanTiltUpRight);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for pan-tilt drive home which match expected packet
	 */
	@Test
	public void testBuildSendPacketPanTiltDriveHome() {
		byte[] expectedPacketPanTiltHome = new byte[] { 0x01, 0x00, 0x00, 0x05, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x06, 0x04, (byte) 0xFF };
		byte[] actualPacketPanTiltHome = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_HOME.getCode());

		assertArrayEquals(expectedPacketPanTiltHome, actualPacketPanTiltHome);
	}

	/**
	 * Test AverPTZUtils#buildSendString success
	 * Expect build an inquiry command for power status which match expected string
	 */
	@Test
	public void testBuildSendPacketPowerStatusInq() {
		byte[] expectedPacketPowerInq = new byte[] { 0x01, 0x10, 0x00, 0x05, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x09, 0x04, 0x00, (byte) 0xFF };
		byte[] actualPacketPowerInq = buildSendPacket(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(), Command.POWER.getCode());

		assertArrayEquals(expectedPacketPowerInq, actualPacketPowerInq);
	}

	/**
	 * Test AverPTZUtils#buildSendString success
	 * Expect build an inquiry command for focus status which match expected string
	 */
	@Test
	public void testBuildSendPacketFocusStatusInq() {
		byte[] expectedPacketFocusInq = new byte[] { 0x01, 0x10, 0x00, 0x05, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x09, 0x04, 0x38, (byte) 0xFF };
		byte[] actualPacketFocusInq = buildSendPacket(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(), Command.FOCUS_MODE.getCode());

		assertArrayEquals(expectedPacketFocusInq, actualPacketFocusInq);
	}

	/**
	 * Test AverPTZUtils#buildSendString success
	 * Expect build an inquiry command for AE Mode which match expected string
	 */
	@Test
	public void testBuildSendPacketAEModeInq() {
		byte[] expectedPacketAEModeInq = new byte[] { 0x01, 0x10, 0x00, 0x05, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x09, 0x04, 0x39, (byte) 0xFF };
		byte[] actualPacketAEModeInq = buildSendPacket(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(), Command.AE_MODE.getCode());

		assertArrayEquals(expectedPacketAEModeInq, actualPacketAEModeInq);
	}

	/**
	 * Test AverPTZUtils#buildSendString success
	 * Expect build an inquiry command for slow shutter which match expected string
	 */
	@Test
	public void testBuildSendPacketSlowShutterInq() {
		byte[] expectedPacketSlowShutterInq = new byte[] { 0x01, 0x10, 0x00, 0x05, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x09, 0x04, 0x5A, (byte) 0xFF };
		byte[] actualPacketSlowShutterInq = buildSendPacket(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(),
				Command.SLOW_SHUTTER.getCode());

		assertArrayEquals(expectedPacketSlowShutterInq, actualPacketSlowShutterInq);
	}

	/**
	 * Test AverPTZUtils#buildSendString success
	 * Expect build an inquiry command for shutter position which match expected string
	 */
	@Test
	public void testBuildSendPacketShutterPosInq() {
		byte[] expectedPacketShutterPosInq = new byte[] { 0x01, 0x10, 0x00, 0x05, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x09, 0x04, 0x4A, (byte) 0xFF };
		byte[] actualPacketShutterPosInq = buildSendPacket(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(),
				new byte[] { Command.SHUTTER_DIRECT.getCode()[0] });

		assertArrayEquals(expectedPacketShutterPosInq, actualPacketShutterPosInq);
	}

	/**
	 * Test AverPTZUtils#buildSendString success
	 * Expect build an inquiry command for iris position which match expected string
	 */
	@Test
	public void testBuildSendPacketIrisPosInq() {
		byte[] expectedPacketIrisPosInq = new byte[] { 0x01, 0x10, 0x00, 0x05, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x09, 0x04, 0x4B, (byte) 0xFF };
		byte[] actualPacketIrisPosInq = buildSendPacket(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(),
				new byte[] { Command.IRIS_DIRECT.getCode()[0] });

		assertArrayEquals(expectedPacketIrisPosInq, actualPacketIrisPosInq);
	}

	/**
	 * Test AverPTZUtils#buildSendString success
	 * Expect build an inquiry command for gain position which match expected string
	 */
	@Test
	public void testBuildSendPacketGainPosInq() {
		byte[] expectedPacketGainPosInq = new byte[] { 0x01, 0x10, 0x00, 0x05, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x09, 0x04, 0x4C, (byte) 0xFF };
		byte[] actualPacketGainPosInq = buildSendPacket(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(),
				new byte[] { Command.GAIN_DIRECT.getCode()[0] });

		assertArrayEquals(expectedPacketGainPosInq, actualPacketGainPosInq);
	}

	/**
	 * Test AverPTZUtils#buildSendString success
	 * Expect build an inquiry command for gain limit position which match expected string
	 */
	@Test
	public void testBuildSendPacketGainLimitPosInq() {
		byte[] expectedPacketAEGainPosInq = new byte[] { 0x01, 0x10, 0x00, 0x05, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x09, 0x04, 0x2C, (byte) 0xFF };
		byte[] actualPacketAEGainPosInq = buildSendPacket(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(),
				Command.GAIN_LIMIT_DIRECT.getCode());

		assertArrayEquals(expectedPacketAEGainPosInq, actualPacketAEGainPosInq);
	}

	/**
	 * Test AverPTZUtils#buildSendString success
	 * Expect build an inquiry command for exposure position which match expected string
	 */
	@Test
	public void testBuildSendPacketExpCompPosInq() {
		byte[] expectedPacketExpCompPosInq = new byte[] { 0x01, 0x10, 0x00, 0x05, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x09, 0x04, 0x4E, (byte) 0xFF };
		byte[] actualPacketExpCompPosInq = buildSendPacket(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(),
				new byte[] { Command.EXP_COMP_DIRECT.getCode()[0] });

		assertArrayEquals(expectedPacketExpCompPosInq, actualPacketExpCompPosInq);
	}

	/**
	 * Test AverPTZUtils#buildSendString success
	 * Expect build an inquiry command for backlight status which match expected string
	 */
	@Test
	public void testBuildSendPacketBacklightInq() {
		byte[] expectedPacketBacklightInq = new byte[] { 0x01, 0x10, 0x00, 0x05, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x09, 0x04, 0x33, (byte) 0xFF };
		byte[] actualPacketExpBacklightInq = buildSendPacket(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(),
				Command.BACKLIGHT.getCode());

		assertArrayEquals(expectedPacketBacklightInq, actualPacketExpBacklightInq);
	}

	/**
	 * Test AverPTZUtils#buildSendString success
	 * Expect build an inquiry command for WB mode which match expected string
	 */
	@Test
	public void testBuildSendPacketWBModeInq() {
		byte[] expectedPacketWBModeInq = new byte[] { 0x01, 0x10, 0x00, 0x05, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x09, 0x04, 0x35, (byte) 0xFF };
		byte[] actualPacketWBModeInq = buildSendPacket(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(), Command.WB_MODE.getCode());

		assertArrayEquals(expectedPacketWBModeInq, actualPacketWBModeInq);
	}

	/**
	 * Test AverPTZUtils#buildSendString success
	 * Expect build an inquiry command for slow pan-tilt status which match expected string
	 */
	@Test
	public void testBuildSendPacketSlowPanTiltInq() {
		byte[] expectedPacketSlowPanTiltInq = new byte[] { 0x01, 0x10, 0x00, 0x05, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x09, 0x06, 0x44, (byte) 0xFF };
		byte[] actualPacketSlowPanTiltInq = buildSendPacket(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.PAN_TILTER.getCode(),
				Command.SLOW_PAN_TILT.getCode());

		assertArrayEquals(expectedPacketSlowPanTiltInq, actualPacketSlowPanTiltInq);
	}
}
