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

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for power which match expected packet
	 */
	@Test
	public void testBuildSendPacketPower() {
		// Power on
		byte[] expectedPacketPowerOn = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x00, 0x02, (byte) 0xFF };
		byte[] actualPacketPowerOn = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.POWER.getCode(), PowerStatus.ON.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketPowerOn, actualPacketPowerOn);

		// Power off
		byte[] expectedPacketPowerOff = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x02, (byte) 0x81, 0x01, 0x04, 0x00, 0x03, (byte) 0xFF };
		byte[] actualPacketPowerOff = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.POWER.getCode(), PowerStatus.OFF.getCode());
		assertArrayEquals(expectedPacketPowerOff, actualPacketPowerOff);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for zoom which match expected packet
	 */
	@Test
	public void testBuildSendPacketZoom() {
// Zoom tele
		byte[] expectedPacketZoomTele = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x07, 0x02, (byte) 0xFF };
		byte[] actualPacketZoomTele = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.ZOOM.getCode(), ZoomControl.TELE.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketZoomTele, actualPacketZoomTele);

		// Zoom wide
		byte[] expectedPacketZoomWide = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x02, (byte) 0x81, 0x01, 0x04, 0x07, 0x03, (byte) 0xFF };
		byte[] actualPacketZoomWide = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.ZOOM.getCode(), ZoomControl.WIDE.getCode());
		assertArrayEquals(expectedPacketZoomWide, actualPacketZoomWide);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for focus which match expected packet
	 */
	@Test
	public void testBuildSendPacketFocus() {
		// Focus far
		byte[] expectedPacketFocusFar = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x08, 0x02, (byte) 0xFF };
		byte[] actualPacketFocusFar = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.FOCUS.getCode(), FocusControl.FAR.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketFocusFar, actualPacketFocusFar);

		// Focus near
		byte[] expectedPacketFocusNear = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x02, (byte) 0x81, 0x01, 0x04, 0x08, 0x03, (byte) 0xFF };
		byte[] actualPacketFocusNear = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.FOCUS.getCode(), FocusControl.NEAR.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketFocusNear, actualPacketFocusNear);

		// Focus auto-focus
		byte[] expectedPacketFocusAutoFocus = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x03, (byte) 0x81, 0x01, 0x04, 0x38, 0x02, (byte) 0xFF };
		byte[] actualPacketFocusAutoFocus = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.FOCUS_MODE.getCode(), FocusStatus.AUTO.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketFocusAutoFocus, actualPacketFocusAutoFocus);

		// Focus manual-focus
		byte[] expectedPacketFocusManualFocus = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x04, (byte) 0x81, 0x01, 0x04, 0x38, 0x03, (byte) 0xFF };
		byte[] actualPacketFocusManualFocus = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.FOCUS_MODE.getCode(), FocusStatus.MANUAL.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketFocusManualFocus, actualPacketFocusManualFocus);

		// Focus one push
		byte[] expectedPacketFocusOnePush = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x05, (byte) 0x81, 0x01, 0x04, 0x18, 0x01, (byte) 0xFF };
		byte[] actualPacketFocusOnePush = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.FOCUS_ONE_PUSH.getCode());
		assertArrayEquals(expectedPacketFocusOnePush, actualPacketFocusOnePush);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for WB control which match expected packet
	 */
	@Test
	public void testBuildSendPacketWB() {
		// WB auto mode
		byte[] expectedPacketWBAuto = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x35, 0x00, (byte) 0xFF };
		byte[] actualPacketWBAuto = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.WB_MODE.getCode(), WBMode.AUTO.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketWBAuto, actualPacketWBAuto);

		// WB indoor mode
		byte[] expectedPacketWBIndoor = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x02, (byte) 0x81, 0x01, 0x04, 0x35, 0x01, (byte) 0xFF };
		byte[] actualPacketWBIndoor = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.WB_MODE.getCode(), WBMode.INDOOR.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketWBIndoor, actualPacketWBIndoor);

		// WB outdoor mode
		byte[] expectedPacketWBOutdoor = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x03, (byte) 0x81, 0x01, 0x04, 0x35, 0x02, (byte) 0xFF };
		byte[] actualPacketWBOutdoor = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.WB_MODE.getCode(), WBMode.OUTDOOR.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketWBOutdoor, actualPacketWBOutdoor);

		// WB one push mode
		byte[] expectedPacketWBOnePush = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x04, (byte) 0x81, 0x01, 0x04, 0x35, 0x03, (byte) 0xFF };
		byte[] actualPacketWBOnePush = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.WB_MODE.getCode(), WBMode.ONE_PUSH_WB.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketWBOnePush, actualPacketWBOnePush);

		// WB manual mode
		byte[] expectedPacketWBManual = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x05, (byte) 0x81, 0x01, 0x04, 0x35, 0x05, (byte) 0xFF };
		byte[] actualPacketWBManual = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.WB_MODE.getCode(), WBMode.MANUAL.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketWBManual, actualPacketWBManual);

		// WB one push trigger
		byte[] expectedPacketWBOnePushTrigger = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x06, (byte) 0x81, 0x01, 0x04, 0x10, 0x05, (byte) 0xFF };
		byte[] actualPacketWBOnePushTrigger = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.WB_ONE_PUSH_TRIGGER.getCode());
		assertArrayEquals(expectedPacketWBOnePushTrigger, actualPacketWBOnePushTrigger);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for RGain control which match expected packet
	 */
	@Test
	public void testBuildSendPacketRGain() {
		// RGain up
		byte[] expectedPacketRGainUp = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x03, 0x02, (byte) 0xFF };
		byte[] actualPacketRGainUp = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.RGAIN.getCode(), RGainControl.UP.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketRGainUp, actualPacketRGainUp);

		// RGain down
		byte[] expectedPacketRGainDown = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x02, (byte) 0x81, 0x01, 0x04, 0x03, 0x03, (byte) 0xFF };
		byte[] actualPacketRGainDown = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.RGAIN.getCode(), RGainControl.DOWN.getCode());
		assertArrayEquals(expectedPacketRGainDown, actualPacketRGainDown);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for BGain control which match expected packet
	 */
	@Test
	public void testBuildSendPacketBGain() {
		// BGain up
		byte[] expectedPacketBGainUp = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x04, 0x02, (byte) 0xFF };
		byte[] actualPacketBGainUp = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.BGAIN.getCode(), BGainControl.UP.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketBGainUp, actualPacketBGainUp);

		// BGain down
		byte[] expectedPacketBGainDown = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x02, (byte) 0x81, 0x01, 0x04, 0x04, 0x03, (byte) 0xFF };
		byte[] actualPacketBGainDown = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.BGAIN.getCode(), BGainControl.DOWN.getCode());
		assertArrayEquals(expectedPacketBGainDown, actualPacketBGainDown);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for AEMode which match expected packet
	 */
	@Test
	public void testBuildSendPacketAEMode() {
		// AE full auto mode
		byte[] expectedPacketAEFullAuto = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x39, 0x00, (byte) 0xFF };
		byte[] actualPacketAEFullAuto = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.AE_MODE.getCode(), AEMode.FULL_AUTO.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketAEFullAuto, actualPacketAEFullAuto);

		// AE manual mode
		byte[] expectedPacketAEManual = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x02, (byte) 0x81, 0x01, 0x04, 0x39, 0x03, (byte) 0xFF };
		byte[] actualPacketAEManual = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.AE_MODE.getCode(), AEMode.MANUAL.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketAEManual, actualPacketAEManual);

		// AE shutter priority mode
		byte[] expectedPacketAEShutterPriority = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x03, (byte) 0x81, 0x01, 0x04, 0x39, 0x0A, (byte) 0xFF };
		byte[] actualPacketAEShutterPriority = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.AE_MODE.getCode(), AEMode.SHUTTER_PRIORITY.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketAEShutterPriority, actualPacketAEShutterPriority);

		// AE iris priority mode
		byte[] expectedPacketAEIrisPriority = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x04, (byte) 0x81, 0x01, 0x04, 0x39, 0x0B, (byte) 0xFF };
		byte[] actualPacketAEIrisPriority = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.AE_MODE.getCode(), AEMode.IRIS_PRIORITY.getCode());
		assertArrayEquals(expectedPacketAEIrisPriority, actualPacketAEIrisPriority);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for Slow shutter which match expected packet
	 */
	@Test
	public void testBuildSendPacketSlowShutter() {
		// Slow shutter auto on
		byte[] expectedPacketSlowShutterAuto = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x5A, 0x02, (byte) 0xFF };
		byte[] actualPacketSlowShutterAuto = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.SLOW_SHUTTER.getCode(), SlowShutterStatus.ON.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketSlowShutterAuto, actualPacketSlowShutterAuto);

		// Slow shutter auto off
		byte[] expectedPacketSlowShutterManual = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x02, (byte) 0x81, 0x01, 0x04, 0x5A, 0x03, (byte) 0xFF };
		byte[] actualPacketSlowShutterManual = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.SLOW_SHUTTER.getCode(), SlowShutterStatus.OFF.getCode());
		assertArrayEquals(expectedPacketSlowShutterManual, actualPacketSlowShutterManual);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for direct which match expected packet
	 */
	@Test
	public void testBuildSendPacketDirect() {
		// Shutter direct
		int shutterPosition = 40;
		byte[] expectedPacketShutterDirect = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x4A, 0x00, 0x00, 0x02, 0x08, (byte) 0xFF };
		byte[] actualPacketShutterDirect = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.SHUTTER_DIRECT.getCode(), convertOneByteNumberToTwoBytesArray((byte) shutterPosition));
		sequenceNumber++;
		assertArrayEquals(expectedPacketShutterDirect, actualPacketShutterDirect);

		// Iris direct
		int irisPosition = 9;
		byte[] expectedPacketIrisDirect = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x02, (byte) 0x81, 0x01, 0x04, 0x4B, 0x00, 0x00, 0x00, 0x09, (byte) 0xFF };
		byte[] actualPacketIrisDirect = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.IRIS_DIRECT.getCode(), convertOneByteNumberToTwoBytesArray((byte) irisPosition));
		sequenceNumber++;
		assertArrayEquals(expectedPacketIrisDirect, actualPacketIrisDirect);

		// Gain direct
		int gainPosition = 23;
		byte[] expectedPacketGainDirect = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x03, (byte) 0x81, 0x01, 0x04, 0x4C, 0x00, 0x00, 0x01, 0x07, (byte) 0xFF };
		byte[] actualPacketGainDirect = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.GAIN_DIRECT.getCode(), convertOneByteNumberToTwoBytesArray((byte) gainPosition));
		sequenceNumber++;
		assertArrayEquals(expectedPacketGainDirect, actualPacketGainDirect);

		// Gain limit direct
		byte[] expectedPacketGainLimitDirect = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x04, (byte) 0x81, 0x01, 0x04, 0x2C, 0x17, (byte) 0xFF };
		byte[] actualPacketGainLimitDirect = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.GAIN_LIMIT_DIRECT.getCode(), (byte) gainPosition);
		sequenceNumber++;
		assertArrayEquals(expectedPacketGainLimitDirect, actualPacketGainLimitDirect);

		// ExpComp direct
		int expComp = 5;
		byte[] expectedPacketExpCompDirect = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x05, (byte) 0x81, 0x01, 0x04, 0x4E, 0x00, 0x00, 0x00, 0x05, (byte) 0xFF };
		byte[] actualPacketExpCompDirect = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.EXP_COMP_DIRECT.getCode(), convertOneByteNumberToTwoBytesArray((byte) expComp));
		assertArrayEquals(expectedPacketExpCompDirect, actualPacketExpCompDirect);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for backlight which match expected packet
	 */
	@Test
	public void testBuildSendPacketBacklight() {
		// Backlight on
		byte[] expectedPacketBackLightOn = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x33, 0x02, (byte) 0xFF };
		byte[] actualPacketBackLightOn = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.BACKLIGHT.getCode(), BacklightStatus.ON.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketBackLightOn, actualPacketBackLightOn);

		// Backlight off
		byte[] expectedPacketBackLightOff = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x02, (byte) 0x81, 0x01, 0x04, 0x33, 0x03, (byte) 0xFF };
		byte[] actualPacketBackLightOff = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.BACKLIGHT.getCode(), BacklightStatus.OFF.getCode());
		assertArrayEquals(expectedPacketBackLightOff, actualPacketBackLightOff);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for preset which match expected packet
	 */
	@Test
	public void testBuildSendPacketPreset() {
		// Preset
		int preset = 2;
		// Set preset
		byte[] expectedPacketSetPreset = new byte[] { 0x01, 0x00, 0x00, 0x07, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x3F, 0x01, (byte) preset, (byte) 0xFF };
		byte[] actualPacketSetPreset = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.PRESET.getCode(), PresetControl.SET.getCode(), (byte) preset);
		sequenceNumber++;
		assertArrayEquals(expectedPacketSetPreset, actualPacketSetPreset);

		// Set preset
		byte[] expectedPacketLoadPreset = new byte[] { 0x01, 0x00, 0x00, 0x07, 0x00, 0x00, 0x00, 0x02, (byte) 0x81, 0x01, 0x04, 0x3F, 0x02, (byte) preset, (byte) 0xFF };
		byte[] actualPacketLoadPreset = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.PRESET.getCode(), PresetControl.RECALL.getCode(), (byte) preset);
		assertArrayEquals(expectedPacketLoadPreset, actualPacketLoadPreset);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for slow pan-tilt which match expected packet
	 */
	@Test
	public void testBuildSendPacketSlowPanTilt() {
		// Slow pan-tilt on
		byte[] expectedPacketSlowPanTiltOn = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x06, 0x44, 0x02, (byte) 0xFF };
		byte[] actualPacketSlowPanTiltOn = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.SLOW_PAN_TILT.getCode(), SlowPanTiltStatus.ON.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketSlowPanTiltOn, actualPacketSlowPanTiltOn);

		// Slow pan-tilt off
		byte[] expectedPacketSlowPanTiltOff = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x02, (byte) 0x81, 0x01, 0x06, 0x44, 0x03, (byte) 0xFF };
		byte[] actualPacketSlowPanTiltOff = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.SLOW_PAN_TILT.getCode(), SlowPanTiltStatus.OFF.getCode());
		assertArrayEquals(expectedPacketSlowPanTiltOff, actualPacketSlowPanTiltOff);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for pan-tilt drive which match expected packet
	 */
	@Test
	public void testBuildSendPacketPanTiltDrive() throws IOException {
		// Pan-tilt drive
		int panSpeed = 20;
		int tiltSpeed = 15;
		ByteArrayOutputStream outputStream;

		// Pan-tilt up
		outputStream = new ByteArrayOutputStream();
		outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStream.write(PanTiltDrive.UP.getCode());
		byte[] expectedPacketPanTiltUp = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x06, 0x01, (byte) panSpeed, (byte) tiltSpeed, 0x03, 0x01, (byte) 0xFF };
		byte[] actualPacketPanTiltUp = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());
		sequenceNumber++;
		assertArrayEquals(expectedPacketPanTiltUp, actualPacketPanTiltUp);

		// Pan-tilt down
		outputStream = new ByteArrayOutputStream();
		outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStream.write(PanTiltDrive.DOWN.getCode());
		byte[] expectedPacketPanTiltDown = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x02, (byte) 0x81, 0x01, 0x06, 0x01, (byte) panSpeed, (byte) tiltSpeed, 0x03, 0x02, (byte) 0xFF };
		byte[] actualPacketPanTiltDown = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());
		sequenceNumber++;
		assertArrayEquals(expectedPacketPanTiltDown, actualPacketPanTiltDown);

		// Pan-tilt left
		outputStream = new ByteArrayOutputStream();
		outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStream.write(PanTiltDrive.LEFT.getCode());
		byte[] expectedPacketPanTiltLeft = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x03, (byte) 0x81, 0x01, 0x06, 0x01, (byte) panSpeed, (byte) tiltSpeed, 0x01, 0x03, (byte) 0xFF };
		byte[] actualPacketPanTiltLeft = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());
		sequenceNumber++;
		assertArrayEquals(expectedPacketPanTiltLeft, actualPacketPanTiltLeft);

		// Pan-tilt right
		outputStream = new ByteArrayOutputStream();
		outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStream.write(PanTiltDrive.RIGHT.getCode());
		byte[] expectedPacketPanTiltRight = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x04, (byte) 0x81, 0x01, 0x06, 0x01, (byte) panSpeed, (byte) tiltSpeed, 0x02, 0x03, (byte) 0xFF };
		byte[] actualPacketPanTiltRight = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());
		sequenceNumber++;
		assertArrayEquals(expectedPacketPanTiltRight, actualPacketPanTiltRight);

		// Pan-tilt up left
		outputStream = new ByteArrayOutputStream();
		outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStream.write(PanTiltDrive.UP_LEFT.getCode());
		byte[] expectedPacketPanTilUpLeft = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x05, (byte) 0x81, 0x01, 0x06, 0x01, (byte) panSpeed, (byte) tiltSpeed, 0x01, 0x01, (byte) 0xFF };
		byte[] actualPacketPanTiltUpLeft = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());
		sequenceNumber++;
		assertArrayEquals(expectedPacketPanTilUpLeft, actualPacketPanTiltUpLeft);

		// Pan-tilt up right
		outputStream = new ByteArrayOutputStream();
		outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStream.write(PanTiltDrive.UP_RIGHT.getCode());
		byte[] expectedPacketPanTilUpRight = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x06, (byte) 0x81, 0x01, 0x06, 0x01, (byte) panSpeed, (byte) tiltSpeed, 0x02, 0x01, (byte) 0xFF };
		byte[] actualPacketPanTiltUpRight = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());
		sequenceNumber++;
		assertArrayEquals(expectedPacketPanTilUpRight, actualPacketPanTiltUpRight);

		// Pan-tilt down left
		outputStream = new ByteArrayOutputStream();
		outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStream.write(PanTiltDrive.DOWN_LEFT.getCode());
		byte[] expectedPacketPanTilDownLeft = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x07, (byte) 0x81, 0x01, 0x06, 0x01, (byte) panSpeed, (byte) tiltSpeed, 0x01, 0x02, (byte) 0xFF };
		byte[] actualPacketPanTiltDownLeft = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());
		sequenceNumber++;
		assertArrayEquals(expectedPacketPanTilDownLeft, actualPacketPanTiltDownLeft);

		// Pan-tilt down right
		outputStream = new ByteArrayOutputStream();
		outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStream.write(PanTiltDrive.DOWN_RIGHT.getCode());
		byte[] expectedPacketPanTilDownRight = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x08, (byte) 0x81, 0x01, 0x06, 0x01, (byte) panSpeed, (byte) tiltSpeed, 0x02, 0x02, (byte) 0xFF };
		byte[] actualPacketPanTiltDownRight = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());
		sequenceNumber++;
		assertArrayEquals(expectedPacketPanTilDownRight, actualPacketPanTiltDownRight);

		// Pan-tilt home
		byte[] expectedPacketPanTiltHome = new byte[] { 0x01, 0x00, 0x00, 0x05, 0x00, 0x00, 0x00, 0x09, (byte) 0x81, 0x01, 0x06, 0x04, (byte) 0xFF };
		byte[] actualPacketPanTiltHome = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_HOME.getCode());
		sequenceNumber++;
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
