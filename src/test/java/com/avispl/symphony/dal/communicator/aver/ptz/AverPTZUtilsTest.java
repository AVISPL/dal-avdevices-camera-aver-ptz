/*
 * Copyright (c) 2021 AVI-SPL Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.aver.ptz;

import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZUtils.buildSendString;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZUtils.oneByteToTwoBytes;
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
	 * Test AverPTZUtils#buildSendString success
	 * Expect build send string for control command which match expected string
	 */
	@Test
	public void testBuildControlCommandSendString() throws IOException {
		// Power on
		byte[] expectedPacketPowerOn = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x01, 0x04, 0x00, 0x02, (byte) 0xFF };
		byte[] actualPacketPowerOn = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.POWER.getCode(), PowerStatus.ON.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketPowerOn, actualPacketPowerOn);

		// Power off
		byte[] expectedPacketPowerOff = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x02, (byte) 0x81, 0x01, 0x04, 0x00, 0x03, (byte) 0xFF };
		byte[] actualPacketPowerOff = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.POWER.getCode(), PowerStatus.OFF.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketPowerOff, actualPacketPowerOff);

		// Zoom tele
		byte[] expectedPacketZoomTele = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x03, (byte) 0x81, 0x01, 0x04, 0x07, 0x02, (byte) 0xFF };
		byte[] actualPacketZoomTele = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.ZOOM.getCode(), ZoomControl.TELE.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketZoomTele, actualPacketZoomTele);

		// Zoom wide
		byte[] expectedPacketZoomWide = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x04, (byte) 0x81, 0x01, 0x04, 0x07, 0x03, (byte) 0xFF };
		byte[] actualPacketZoomWide = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.ZOOM.getCode(), ZoomControl.WIDE.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketZoomWide, actualPacketZoomWide);

		// Focus far
		byte[] expectedPacketFocusFar = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x05, (byte) 0x81, 0x01, 0x04, 0x08, 0x02, (byte) 0xFF };
		byte[] actualPacketFocusFar = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.FOCUS.getCode(), FocusControl.FAR.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketFocusFar, actualPacketFocusFar);

		// Focus near
		byte[] expectedPacketFocusNear = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x06, (byte) 0x81, 0x01, 0x04, 0x08, 0x03, (byte) 0xFF };
		byte[] actualPacketFocusNear = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.FOCUS.getCode(), FocusControl.NEAR.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketFocusNear, actualPacketFocusNear);

		// Focus auto-focus
		byte[] expectedPacketFocusAutoFocus = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x07, (byte) 0x81, 0x01, 0x04, 0x38, 0x02, (byte) 0xFF };
		byte[] actualPacketFocusAutoFocus = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.FOCUS_MODE.getCode(), FocusStatus.AUTO.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketFocusAutoFocus, actualPacketFocusAutoFocus);

		// Focus manual-focus
		byte[] expectedPacketFocusManualFocus = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x08, (byte) 0x81, 0x01, 0x04, 0x38, 0x03, (byte) 0xFF };
		byte[] actualPacketFocusManualFocus = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.FOCUS_MODE.getCode(), FocusStatus.MANUAL.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketFocusManualFocus, actualPacketFocusManualFocus);

		// Focus one push
		byte[] expectedPacketFocusOnePush = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x09, (byte) 0x81, 0x01, 0x04, 0x18, 0x01, (byte) 0xFF };
		byte[] actualPacketFocusOnePush = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.FOCUS_ONE_PUSH.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketFocusOnePush, actualPacketFocusOnePush);

		// WB auto mode
		byte[] expectedPacketWBAuto = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x0A, (byte) 0x81, 0x01, 0x04, 0x35, 0x00, (byte) 0xFF };
		byte[] actualPacketWBAuto = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.WB_MODE.getCode(), WBMode.AUTO.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketWBAuto, actualPacketWBAuto);

		// WB indoor mode
		byte[] expectedPacketWBIndoor = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x0B, (byte) 0x81, 0x01, 0x04, 0x35, 0x01, (byte) 0xFF };
		byte[] actualPacketWBIndoor = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.WB_MODE.getCode(), WBMode.INDOOR.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketWBIndoor, actualPacketWBIndoor);

		// WB outdoor mode
		byte[] expectedPacketWBOutdoor = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x0C, (byte) 0x81, 0x01, 0x04, 0x35, 0x02, (byte) 0xFF };
		byte[] actualPacketWBOutdoor = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.WB_MODE.getCode(), WBMode.OUTDOOR.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketWBOutdoor, actualPacketWBOutdoor);

		// WB one push mode
		byte[] expectedPacketWBOnePush = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x0D, (byte) 0x81, 0x01, 0x04, 0x35, 0x03, (byte) 0xFF };
		byte[] actualPacketWBOnePush = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.WB_MODE.getCode(), WBMode.ONE_PUSH_WB.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketWBOnePush, actualPacketWBOnePush);

		// WB manual mode
		byte[] expectedPacketWBManual = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x0E, (byte) 0x81, 0x01, 0x04, 0x35, 0x05, (byte) 0xFF };
		byte[] actualPacketWBManual = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.WB_MODE.getCode(), WBMode.MANUAL.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketWBManual, actualPacketWBManual);

		// WB one push trigger
		byte[] expectedPacketWBOnePushTrigger = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x0F, (byte) 0x81, 0x01, 0x04, 0x10, 0x05, (byte) 0xFF };
		byte[] actualPacketWBOnePushTrigger = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.WB_ONE_PUSH_TRIGGER.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketWBOnePushTrigger, actualPacketWBOnePushTrigger);

		// RGain up
		byte[] expectedPacketRGainUp = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x10, (byte) 0x81, 0x01, 0x04, 0x03, 0x02, (byte) 0xFF };
		byte[] actualPacketRGainUp = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.RGAIN.getCode(), RGainControl.UP.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketRGainUp, actualPacketRGainUp);

		// RGain down
		byte[] expectedPacketRGainDown = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x11, (byte) 0x81, 0x01, 0x04, 0x03, 0x03, (byte) 0xFF };
		byte[] actualPacketRGainDown = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.RGAIN.getCode(), RGainControl.DOWN.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketRGainDown, actualPacketRGainDown);

		// BGain up
		byte[] expectedPacketBGainUp = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x12, (byte) 0x81, 0x01, 0x04, 0x04, 0x02, (byte) 0xFF };
		byte[] actualPacketBGainUp = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.BGAIN.getCode(), BGainControl.UP.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketBGainUp, actualPacketBGainUp);

		// RGain down
		byte[] expectedPacketBGainDown = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x13, (byte) 0x81, 0x01, 0x04, 0x04, 0x03, (byte) 0xFF };
		byte[] actualPacketBGainDown = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.BGAIN.getCode(), BGainControl.DOWN.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketBGainDown, actualPacketBGainDown);

		// AE full auto mode
		byte[] expectedPacketAEFullAuto = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x14, (byte) 0x81, 0x01, 0x04, 0x39, 0x00, (byte) 0xFF };
		byte[] actualPacketAEFullAuto = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.AE_MODE.getCode(), AEMode.FULL_AUTO.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketAEFullAuto, actualPacketAEFullAuto);

		// AE manual mode
		byte[] expectedPacketAEManual = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x15, (byte) 0x81, 0x01, 0x04, 0x39, 0x03, (byte) 0xFF };
		byte[] actualPacketAEManual = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.AE_MODE.getCode(), AEMode.MANUAL.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketAEManual, actualPacketAEManual);

		// AE shutter priority mode
		byte[] expectedPacketAEShutterPriority = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x16, (byte) 0x81, 0x01, 0x04, 0x39, 0x0A, (byte) 0xFF };
		byte[] actualPacketAEShutterPriority = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.AE_MODE.getCode(), AEMode.SHUTTER_PRIORITY.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketAEShutterPriority, actualPacketAEShutterPriority);

		// AE iris priority mode
		byte[] expectedPacketAEIrisPriority = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x17, (byte) 0x81, 0x01, 0x04, 0x39, 0x0B, (byte) 0xFF };
		byte[] actualPacketAEIrisPriority = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.AE_MODE.getCode(), AEMode.IRIS_PRIORITY.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketAEIrisPriority, actualPacketAEIrisPriority);

		// Slow shutter auto on
		byte[] expectedPacketSlowShutterAuto = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x18, (byte) 0x81, 0x01, 0x04, 0x5A, 0x02, (byte) 0xFF };
		byte[] actualPacketSlowShutterAuto = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.SLOW_SHUTTER.getCode(), SlowShutterStatus.ON.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketSlowShutterAuto, actualPacketSlowShutterAuto);

		// Slow shutter auto off
		byte[] expectedPacketSlowShutterManual = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x19, (byte) 0x81, 0x01, 0x04, 0x5A, 0x03, (byte) 0xFF };
		byte[] actualPacketSlowShutterManual = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.SLOW_SHUTTER.getCode(), SlowShutterStatus.OFF.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketSlowShutterManual, actualPacketSlowShutterManual);

		// Shutter direct
		int shutterPosition = 40;
		byte[] expectedPacketShutterDirect = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x1A, (byte) 0x81, 0x01, 0x04, 0x4A, 0x00, 0x00, 0x02, 0x08, (byte) 0xFF };
		byte[] actualPacketShutterDirect = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.SHUTTER_DIRECT.getCode(), oneByteToTwoBytes((byte) shutterPosition));
		sequenceNumber++;
		assertArrayEquals(expectedPacketShutterDirect, actualPacketShutterDirect);

		// Iris direct
		int irisPosition = 9;
		byte[] expectedPacketIrisDirect = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x1B, (byte) 0x81, 0x01, 0x04, 0x4B, 0x00, 0x00, 0x00, 0x09, (byte) 0xFF };
		byte[] actualPacketIrisDirect = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.IRIS_DIRECT.getCode(), oneByteToTwoBytes((byte) irisPosition));
		sequenceNumber++;
		assertArrayEquals(expectedPacketIrisDirect, actualPacketIrisDirect);

		// Gain direct
		int gainPosition = 23;
		byte[] expectedPacketGainDirect = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x1C, (byte) 0x81, 0x01, 0x04, 0x4C, 0x00, 0x00, 0x01, 0x07, (byte) 0xFF };
		byte[] actualPacketGainDirect = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.GAIN_DIRECT.getCode(), oneByteToTwoBytes((byte) gainPosition));
		sequenceNumber++;
		assertArrayEquals(expectedPacketGainDirect, actualPacketGainDirect);

		// Gain limit direct
		byte[] expectedPacketGainLimitDirect = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x1D, (byte) 0x81, 0x01, 0x04, 0x2C, 0x17, (byte) 0xFF };
		byte[] actualPacketGainLimitDirect = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.GAIN_LIMIT_DIRECT.getCode(), (byte) gainPosition);
		sequenceNumber++;
		assertArrayEquals(expectedPacketGainLimitDirect, actualPacketGainLimitDirect);

		// ExpComp direct
		int expComp = 5;
		byte[] expectedPacketExpCompDirect = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x1E, (byte) 0x81, 0x01, 0x04, 0x4E, 0x00, 0x00, 0x00, 0x05, (byte) 0xFF };
		byte[] actualPacketExpCompDirect = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.EXP_COMP_DIRECT.getCode(), oneByteToTwoBytes((byte) expComp));
		sequenceNumber++;
		assertArrayEquals(expectedPacketExpCompDirect, actualPacketExpCompDirect);

		// Backlight on
		byte[] expectedPacketBackLightOn = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x1F, (byte) 0x81, 0x01, 0x04, 0x33, 0x02, (byte) 0xFF };
		byte[] actualPacketBackLightOn = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.BACKLIGHT.getCode(), BacklightStatus.ON.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketBackLightOn, actualPacketBackLightOn);

		// Backlight off
		byte[] expectedPacketBackLightOff = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x20, (byte) 0x81, 0x01, 0x04, 0x33, 0x03, (byte) 0xFF };
		byte[] actualPacketBackLightOff = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.BACKLIGHT.getCode(), BacklightStatus.OFF.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketBackLightOff, actualPacketBackLightOff);

		// Preset
		int preset = 2;
		// Set preset
		byte[] expectedPacketSetPreset = new byte[] { 0x01, 0x00, 0x00, 0x07, 0x00, 0x00, 0x00, 0x21, (byte) 0x81, 0x01, 0x04, 0x3F, 0x01, (byte) preset, (byte) 0xFF };
		byte[] actualPacketSetPreset = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.PRESET.getCode(), PresetControl.SET.getCode(), (byte) preset);
		sequenceNumber++;
		assertArrayEquals(expectedPacketSetPreset, actualPacketSetPreset);

		// Set preset
		byte[] expectedPacketLoadPreset = new byte[] { 0x01, 0x00, 0x00, 0x07, 0x00, 0x00, 0x00, 0x22, (byte) 0x81, 0x01, 0x04, 0x3F, 0x02, (byte) preset, (byte) 0xFF };
		byte[] actualPacketLoadPreset = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.PRESET.getCode(), PresetControl.RECALL.getCode(), (byte) preset);
		sequenceNumber++;
		assertArrayEquals(expectedPacketLoadPreset, actualPacketLoadPreset);

		// Pan-tilt drive
		int panSpeed = 20;
		int tiltSpeed = 15;
		ByteArrayOutputStream outputStream;

		// Pan-tilt up
		outputStream = new ByteArrayOutputStream();
		outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStream.write(PanTiltDrive.UP.getCode());
		byte[] expectedPacketPanTiltUp = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x23, (byte) 0x81, 0x01, 0x06, 0x01, (byte) panSpeed, (byte) tiltSpeed, 0x03, 0x01, (byte) 0xFF };
		byte[] actualPacketPanTiltUp = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());
		sequenceNumber++;
		assertArrayEquals(expectedPacketPanTiltUp, actualPacketPanTiltUp);

		// Pan-tilt down
		outputStream = new ByteArrayOutputStream();
		outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStream.write(PanTiltDrive.DOWN.getCode());
		byte[] expectedPacketPanTiltDown = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x24, (byte) 0x81, 0x01, 0x06, 0x01, (byte) panSpeed, (byte) tiltSpeed, 0x03, 0x02, (byte) 0xFF };
		byte[] actualPacketPanTiltDown = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());
		sequenceNumber++;
		assertArrayEquals(expectedPacketPanTiltDown, actualPacketPanTiltDown);

		// Pan-tilt left
		outputStream = new ByteArrayOutputStream();
		outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStream.write(PanTiltDrive.LEFT.getCode());
		byte[] expectedPacketPanTiltLeft = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x25, (byte) 0x81, 0x01, 0x06, 0x01, (byte) panSpeed, (byte) tiltSpeed, 0x01, 0x03, (byte) 0xFF };
		byte[] actualPacketPanTiltLeft = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());
		sequenceNumber++;
		assertArrayEquals(expectedPacketPanTiltLeft, actualPacketPanTiltLeft);

		// Pan-tilt right
		outputStream = new ByteArrayOutputStream();
		outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStream.write(PanTiltDrive.RIGHT.getCode());
		byte[] expectedPacketPanTiltRight = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x26, (byte) 0x81, 0x01, 0x06, 0x01, (byte) panSpeed, (byte) tiltSpeed, 0x02, 0x03, (byte) 0xFF };
		byte[] actualPacketPanTiltRight = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());
		sequenceNumber++;
		assertArrayEquals(expectedPacketPanTiltRight, actualPacketPanTiltRight);

		// Pan-tilt up left
		outputStream = new ByteArrayOutputStream();
		outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStream.write(PanTiltDrive.UP_LEFT.getCode());
		byte[] expectedPacketPanTilUpLeft = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x27, (byte) 0x81, 0x01, 0x06, 0x01, (byte) panSpeed, (byte) tiltSpeed, 0x01, 0x01, (byte) 0xFF };
		byte[] actualPacketPanTiltUpLeft = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());
		sequenceNumber++;
		assertArrayEquals(expectedPacketPanTilUpLeft, actualPacketPanTiltUpLeft);

		// Pan-tilt up right
		outputStream = new ByteArrayOutputStream();
		outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStream.write(PanTiltDrive.UP_RIGHT.getCode());
		byte[] expectedPacketPanTilUpRight = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x28, (byte) 0x81, 0x01, 0x06, 0x01, (byte) panSpeed, (byte) tiltSpeed, 0x02, 0x01, (byte) 0xFF };
		byte[] actualPacketPanTiltUpRight = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());
		sequenceNumber++;
		assertArrayEquals(expectedPacketPanTilUpRight, actualPacketPanTiltUpRight);

		// Pan-tilt down left
		outputStream = new ByteArrayOutputStream();
		outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStream.write(PanTiltDrive.DOWN_LEFT.getCode());
		byte[] expectedPacketPanTilDownLeft = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x29, (byte) 0x81, 0x01, 0x06, 0x01, (byte) panSpeed, (byte) tiltSpeed, 0x01, 0x02, (byte) 0xFF };
		byte[] actualPacketPanTiltDownLeft = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());
		sequenceNumber++;
		assertArrayEquals(expectedPacketPanTilDownLeft, actualPacketPanTiltDownLeft);

		// Pan-tilt down right
		outputStream = new ByteArrayOutputStream();
		outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStream.write(PanTiltDrive.DOWN_RIGHT.getCode());
		byte[] expectedPacketPanTilDownRight = new byte[] { 0x01, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x2A, (byte) 0x81, 0x01, 0x06, 0x01, (byte) panSpeed, (byte) tiltSpeed, 0x02, 0x02, (byte) 0xFF };
		byte[] actualPacketPanTiltDownRight = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());
		sequenceNumber++;
		assertArrayEquals(expectedPacketPanTilDownRight, actualPacketPanTiltDownRight);

		// Pan-tilt home
		byte[] expectedPacketPanTiltHome = new byte[] { 0x01, 0x00, 0x00, 0x05, 0x00, 0x00, 0x00, 0x2B, (byte) 0x81, 0x01, 0x06, 0x04, (byte) 0xFF };
		byte[] actualPacketPanTiltHome = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_HOME.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketPanTiltHome, actualPacketPanTiltHome);

		// Slow pan-tilt on
		byte[] expectedPacketSlowPanTiltOn = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x2C, (byte) 0x81, 0x01, 0x06, 0x44, 0x02, (byte) 0xFF };
		byte[] actualPacketSlowPanTiltOn = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.SLOW_PAN_TILT.getCode(), SlowPanTiltStatus.ON.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketSlowPanTiltOn, actualPacketSlowPanTiltOn);

		// Slow pan-tilt off
		byte[] expectedPacketSlowPanTiltOff = new byte[] { 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x2D, (byte) 0x81, 0x01, 0x06, 0x44, 0x03, (byte) 0xFF };
		byte[] actualPacketSlowPanTiltOff = buildSendString(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.SLOW_PAN_TILT.getCode(), SlowPanTiltStatus.OFF.getCode());
		assertArrayEquals(expectedPacketSlowPanTiltOff, actualPacketSlowPanTiltOff);
	}

	/**
	 * Test AverPTZUtils#buildSendString success
	 * Expect build send string for inquiry command which match expected string
	 */
	@Test
	public void testBuildInquiryCommandSendString() {
		// Inquiry power status
		byte[] expectedPacketPowerInq = new byte[] { 0x01, 0x10, 0x00, 0x05, 0x00, 0x00, 0x00, 0x01, (byte) 0x81, 0x09, 0x04, 0x00, (byte) 0xFF };
		byte[] actualPacketPowerInq = buildSendString(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(), Command.POWER.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketPowerInq, actualPacketPowerInq);

		// Inquiry focus status
		byte[] expectedPacketFocusInq = new byte[] { 0x01, 0x10, 0x00, 0x05, 0x00, 0x00, 0x00, 0x02, (byte) 0x81, 0x09, 0x04, 0x38, (byte) 0xFF };
		byte[] actualPacketFocusInq = buildSendString(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(), Command.FOCUS_MODE.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketFocusInq, actualPacketFocusInq);

		// Inquiry AE Mode
		byte[] expectedPacketAEModeInq = new byte[] { 0x01, 0x10, 0x00, 0x05, 0x00, 0x00, 0x00, 0x03, (byte) 0x81, 0x09, 0x04, 0x39, (byte) 0xFF };
		byte[] actualPacketAEModeInq = buildSendString(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(), Command.AE_MODE.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketAEModeInq, actualPacketAEModeInq);

		// Inquiry Slow shutter status
		byte[] expectedPacketSlowShutterInq = new byte[] { 0x01, 0x10, 0x00, 0x05, 0x00, 0x00, 0x00, 0x04, (byte) 0x81, 0x09, 0x04, 0x5A, (byte) 0xFF };
		byte[] actualPacketSlowShutterInq = buildSendString(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(),
				Command.SLOW_SHUTTER.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketSlowShutterInq, actualPacketSlowShutterInq);

		// Inquiry Shutter position
		byte[] expectedPacketShutterPosInq = new byte[] { 0x01, 0x10, 0x00, 0x05, 0x00, 0x00, 0x00, 0x05, (byte) 0x81, 0x09, 0x04, 0x4A, (byte) 0xFF };
		byte[] actualPacketShutterPosInq = buildSendString(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(),
				new byte[] { Command.SHUTTER_DIRECT.getCode()[0] });
		sequenceNumber++;
		assertArrayEquals(expectedPacketShutterPosInq, actualPacketShutterPosInq);

		// Inquiry Iris position
		byte[] expectedPacketIrisPosInq = new byte[] { 0x01, 0x10, 0x00, 0x05, 0x00, 0x00, 0x00, 0x06, (byte) 0x81, 0x09, 0x04, 0x4B, (byte) 0xFF };
		byte[] actualPacketIrisPosInq = buildSendString(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(),
				new byte[] { Command.IRIS_DIRECT.getCode()[0] });
		sequenceNumber++;
		assertArrayEquals(expectedPacketIrisPosInq, actualPacketIrisPosInq);

		// Inquiry Gain position
		byte[] expectedPacketGainPosInq = new byte[] { 0x01, 0x10, 0x00, 0x05, 0x00, 0x00, 0x00, 0x07, (byte) 0x81, 0x09, 0x04, 0x4C, (byte) 0xFF };
		byte[] actualPacketGainPosInq = buildSendString(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(),
				new byte[] { Command.GAIN_DIRECT.getCode()[0] });
		sequenceNumber++;
		assertArrayEquals(expectedPacketGainPosInq, actualPacketGainPosInq);

		// Inquiry AEGain limit position
		byte[] expectedPacketAEGainPosInq = new byte[] { 0x01, 0x10, 0x00, 0x05, 0x00, 0x00, 0x00, 0x08, (byte) 0x81, 0x09, 0x04, 0x2C, (byte) 0xFF };
		byte[] actualPacketAEGainPosInq = buildSendString(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(),
				Command.GAIN_LIMIT_DIRECT.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketAEGainPosInq, actualPacketAEGainPosInq);

		// Inquiry ExpComp position
		byte[] expectedPacketExpCompPosInq = new byte[] { 0x01, 0x10, 0x00, 0x05, 0x00, 0x00, 0x00, 0x09, (byte) 0x81, 0x09, 0x04, 0x4E, (byte) 0xFF };
		byte[] actualPacketExpCompPosInq = buildSendString(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(),
				new byte[] { Command.EXP_COMP_DIRECT.getCode()[0] });
		sequenceNumber++;
		assertArrayEquals(expectedPacketExpCompPosInq, actualPacketExpCompPosInq);

		// Inquiry Backlight status
		byte[] expectedPacketBacklightInq = new byte[] { 0x01, 0x10, 0x00, 0x05, 0x00, 0x00, 0x00, 0x0A, (byte) 0x81, 0x09, 0x04, 0x33, (byte) 0xFF };
		byte[] actualPacketExpBacklightInq = buildSendString(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(),
				Command.BACKLIGHT.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketBacklightInq, actualPacketExpBacklightInq);

		// Inquiry WB Mode
		byte[] expectedPacketWBModeInq = new byte[] { 0x01, 0x10, 0x00, 0x05, 0x00, 0x00, 0x00, 0x0B, (byte) 0x81, 0x09, 0x04, 0x35, (byte) 0xFF };
		byte[] actualPacketWBModeInq = buildSendString(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(), Command.WB_MODE.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketWBModeInq, actualPacketWBModeInq);

		// Inquiry Slow Pan-tilt status
		byte[] expectedPacketSlowPanTiltInq = new byte[] { 0x01, 0x10, 0x00, 0x05, 0x00, 0x00, 0x00, 0x0C, (byte) 0x81, 0x09, 0x06, 0x44, (byte) 0xFF };
		byte[] actualPacketSlowPanTiltInq = buildSendString(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.PAN_TILTER.getCode(),
				Command.SLOW_PAN_TILT.getCode());
		sequenceNumber++;
		assertArrayEquals(expectedPacketSlowPanTiltInq, actualPacketSlowPanTiltInq);
	}
}
