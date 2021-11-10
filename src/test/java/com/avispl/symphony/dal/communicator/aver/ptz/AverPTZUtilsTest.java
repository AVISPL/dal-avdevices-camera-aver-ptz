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

import com.avispl.symphony.dal.communicator.aver.ptz.enums.SendPacket;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.Category;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.PayloadType;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.command.Command;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.command.CommandType;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.AEMode;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.BGainControl;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.BacklightStatus;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.FocusControl;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.FocusMode;
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
		byte[] actualPacketPowerOn = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.POWER.getCode(), PowerStatus.ON.getCode());

		assertArrayEquals(SendPacket.POWER_ON.getCode(), actualPacketPowerOn);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for power off which match expected packet
	 */
	@Test
	public void testBuildSendPacketPowerOff() {
		byte[] actualPacketPowerOff = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.POWER.getCode(), PowerStatus.OFF.getCode());

		assertArrayEquals(SendPacket.POWER_OFF.getCode(), actualPacketPowerOff);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for zoom tele which match expected packet
	 */
	@Test
	public void testBuildSendPacketZoomTele() {
		byte[] actualPacketZoomTele = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.ZOOM.getCode(), ZoomControl.TELE.getCode());

		assertArrayEquals(SendPacket.ZOOM_TELE.getCode(), actualPacketZoomTele);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for zoom wide which match expected packet
	 */
	@Test
	public void testBuildSendPacketZoomWide() {
		byte[] actualPacketZoomWide = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.ZOOM.getCode(), ZoomControl.WIDE.getCode());

		assertArrayEquals(SendPacket.ZOOM_WIDE.getCode(), actualPacketZoomWide);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for focus far which match expected packet
	 */
	@Test
	public void testBuildSendPacketFocusFar() {
		byte[] actualPacketFocusFar = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.FOCUS.getCode(), FocusControl.FAR.getCode());

		assertArrayEquals(SendPacket.FOCUS_FAR.getCode(), actualPacketFocusFar);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for focus near which match expected packet
	 */
	@Test
	public void testBuildSendPacketFocusNear() {
		byte[] actualPacketFocusNear = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.FOCUS.getCode(), FocusControl.NEAR.getCode());

		assertArrayEquals(SendPacket.FOCUS_NEAR.getCode(), actualPacketFocusNear);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for auto-focus near which match expected packet
	 */
	@Test
	public void testBuildSendPacketAutoFocus() {
		byte[] actualPacketFocusAutoFocus = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.FOCUS_MODE.getCode(), FocusMode.AUTO.getCode());
		assertArrayEquals(SendPacket.FOCUS_AUTO_MODE.getCode(), actualPacketFocusAutoFocus);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for manual-focus near which match expected packet
	 */
	@Test
	public void testBuildSendPacketManualFocus() {
		byte[] actualPacketFocusManualFocus = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.FOCUS_MODE.getCode(), FocusMode.MANUAL.getCode());

		assertArrayEquals(SendPacket.FOCUS_MANUAL_MODE.getCode(), actualPacketFocusManualFocus);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for focus one push near which match expected packet
	 */
	@Test
	public void testBuildSendPacketFocusOnePush() {
		byte[] actualPacketFocusOnePush = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.FOCUS_ONE_PUSH.getCode());

		assertArrayEquals(SendPacket.FOCUS_ONE_PUSH_MODE.getCode(), actualPacketFocusOnePush);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for WB auto mode which match expected packet
	 */
	@Test
	public void testBuildSendPacketWBAutoMode() {
		byte[] actualPacketWBAuto = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.WB_MODE.getCode(), WBMode.AUTO.getCode());

		assertArrayEquals(SendPacket.WB_AUTO_MODE.getCode(), actualPacketWBAuto);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for WB indoor mode which match expected packet
	 */
	@Test
	public void testBuildSendPacketWBIndoorMode() {
		byte[] actualPacketWBIndoor = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.WB_MODE.getCode(), WBMode.INDOOR.getCode());

		assertArrayEquals(SendPacket.WB_INDOOR_MODE.getCode(), actualPacketWBIndoor);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for WB outdoor mode which match expected packet
	 */
	@Test
	public void testBuildSendPacketWBOutdoorMode() {
		byte[] actualPacketWBOutdoor = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.WB_MODE.getCode(), WBMode.OUTDOOR.getCode());

		assertArrayEquals(SendPacket.WB_OUTDOOR_MODE.getCode(), actualPacketWBOutdoor);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for WB one push mode which match expected packet
	 */
	@Test
	public void testBuildSendPacketWBOnePushMode() {
		byte[] actualPacketWBOnePush = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.WB_MODE.getCode(), WBMode.ONE_PUSH_WB.getCode());

		assertArrayEquals(SendPacket.WB_ONE_PUSH_MODE.getCode(), actualPacketWBOnePush);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for WB manual mode which match expected packet
	 */
	@Test
	public void testBuildSendPacketWBManualMode() {
		byte[] actualPacketWBManual = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.WB_MODE.getCode(), WBMode.MANUAL.getCode());

		assertArrayEquals(SendPacket.WB_MANUAL_MODE.getCode(), actualPacketWBManual);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for WB one push trigger which match expected packet
	 */
	@Test
	public void testBuildSendPacketWBOnePushTrigger() {
		byte[] actualPacketWBOnePushTrigger = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.WB_ONE_PUSH_TRIGGER.getCode());

		assertArrayEquals(SendPacket.WB_ONE_PUSH_TRIGGER.getCode(), actualPacketWBOnePushTrigger);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for RGain up control which match expected packet
	 */
	@Test
	public void testBuildSendPacketRGainUp() {
		byte[] actualPacketRGainUp = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.RGAIN.getCode(), RGainControl.UP.getCode());

		assertArrayEquals(SendPacket.RGAIN_UP.getCode(), actualPacketRGainUp);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for RGain down control which match expected packet
	 */
	@Test
	public void testBuildSendPacketRGainDown() {
		byte[] actualPacketRGainDown = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.RGAIN.getCode(), RGainControl.DOWN.getCode());

		assertArrayEquals(SendPacket.RGAIN_DOWN.getCode(), actualPacketRGainDown);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for BGain up control which match expected packet
	 */
	@Test
	public void testBuildSendPacketBGainUp() {
		byte[] actualPacketBGainUp = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.BGAIN.getCode(), BGainControl.UP.getCode());

		assertArrayEquals(SendPacket.BGAIN_UP.getCode(), actualPacketBGainUp);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for BGain down control which match expected packet
	 */
	@Test
	public void testBuildSendPacketBGainDown() {
		byte[] actualPacketBGainDown = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.BGAIN.getCode(), BGainControl.DOWN.getCode());

		assertArrayEquals(SendPacket.BGAIN_DOWN.getCode(), actualPacketBGainDown);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for AE full auto mode which match expected packet
	 */
	@Test
	public void testBuildSendPacketAEFullAutoMode() {
		byte[] actualPacketAEFullAuto = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.AE_MODE.getCode(), AEMode.FULL_AUTO.getCode());

		assertArrayEquals(SendPacket.AE_FULL_AUTO_MODE.getCode(), actualPacketAEFullAuto);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for AE manual mode which match expected packet
	 */
	@Test
	public void testBuildSendPacketAEManualMode() {
		byte[] actualPacketAEManual = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.AE_MODE.getCode(), AEMode.MANUAL.getCode());

		assertArrayEquals(SendPacket.AE_MANUAL_MODE.getCode(), actualPacketAEManual);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for AE shutter priority mode which match expected packet
	 */
	@Test
	public void testBuildSendPacketAEShutterPriorityMode() {
		byte[] actualPacketAEShutterPriority = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.AE_MODE.getCode(), AEMode.SHUTTER_PRIORITY.getCode());

		assertArrayEquals(SendPacket.AE_SHUTTER_PRIORITY_MODE.getCode(), actualPacketAEShutterPriority);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for AE iris priority mode which match expected packet
	 */
	@Test
	public void testBuildSendPacketAEIrisPriorityMode() {
		byte[] actualPacketAEIrisPriority = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.AE_MODE.getCode(), AEMode.IRIS_PRIORITY.getCode());

		assertArrayEquals(SendPacket.AE_IRIS_PRIORITY_MODE.getCode(), actualPacketAEIrisPriority);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for Slow shutter on which match expected packet
	 */
	@Test
	public void testBuildSendPacketSlowShutterOn() {
		byte[] actualPacketSlowShutterOn = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.AUTO_SLOW_SHUTTER.getCode(), SlowShutterStatus.ON.getCode());

		assertArrayEquals(SendPacket.SLOW_SHUTTER_ON.getCode(), actualPacketSlowShutterOn);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for Slow shutter off which match expected packet
	 */
	@Test
	public void testBuildSendPacketSlowShutterOff() {
		byte[] actualPacketSlowShutterOff = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.AUTO_SLOW_SHUTTER.getCode(), SlowShutterStatus.OFF.getCode());

		assertArrayEquals(SendPacket.SLOW_SHUTTER_OFF.getCode(), actualPacketSlowShutterOff);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for shutter direct which match expected packet
	 */
	@Test
	public void testBuildSendPacketShutterDirect() {
		int shutterPosition = 40;

		byte[] actualPacketShutterDirect = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.SHUTTER_DIRECT.getCode(), convertOneByteNumberToTwoBytesArray((byte) shutterPosition));

		assertArrayEquals(SendPacket.SHUTTER_DIRECT.getCode(), actualPacketShutterDirect);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for iris direct which match expected packet
	 */
	@Test
	public void testBuildSendPacketIrisDirect() {
		int irisPosition = 9;

		byte[] actualPacketIrisDirect = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.IRIS_DIRECT.getCode(), convertOneByteNumberToTwoBytesArray((byte) irisPosition));

		assertArrayEquals(SendPacket.IRIS_DIRECT.getCode(), actualPacketIrisDirect);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for gain direct which match expected packet
	 */
	@Test
	public void testBuildSendPacketGainDirect() {
		int gainPosition = 23;

		byte[] actualPacketGainDirect = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.GAIN_DIRECT.getCode(), convertOneByteNumberToTwoBytesArray((byte) gainPosition));

		assertArrayEquals(SendPacket.GAIN_DIRECT.getCode(), actualPacketGainDirect);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for gain limit direct which match expected packet
	 */
	@Test
	public void testBuildSendPacketGainLimitDirect() {
		int gainPosition = 23;

		byte[] actualPacketGainLimitDirect = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.GAIN_LIMIT_DIRECT.getCode(), (byte) gainPosition);

		assertArrayEquals(SendPacket.GAIN_LIMIT_DIRECT.getCode(), actualPacketGainLimitDirect);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for gain exp comp direct which match expected packet
	 */
	@Test
	public void testBuildSendPacketExpCompDirect() {
		int expComp = 5;

		byte[] actualPacketExpCompDirect = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.EXP_COMP_DIRECT.getCode(), convertOneByteNumberToTwoBytesArray((byte) expComp));

		assertArrayEquals(SendPacket.EXP_COMP_DIRECT.getCode(), actualPacketExpCompDirect);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for backlight on which match expected packet
	 */
	@Test
	public void testBuildSendPacketBacklightOn() {
		byte[] actualPacketBackLightOn = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.BACKLIGHT.getCode(), BacklightStatus.ON.getCode());

		assertArrayEquals(SendPacket.BACKLIGHT_ON.getCode(), actualPacketBackLightOn);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for backlight off which match expected packet
	 */
	@Test
	public void testBuildSendPacketBacklightOff() {
		byte[] actualPacketBackLightOff = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.BACKLIGHT.getCode(), BacklightStatus.OFF.getCode());

		assertArrayEquals(SendPacket.BACKLIGHT_OFF.getCode(), actualPacketBackLightOff);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for preset set which match expected packet
	 */
	@Test
	public void testBuildSendPacketPresetSet() {
		int preset = 2;
		byte[] actualPacketSetPreset = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.PRESET.getCode(), PresetControl.SET.getCode(), (byte) preset);

		assertArrayEquals(SendPacket.PRESET_SET.getCode(), actualPacketSetPreset);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for preset recall which match expected packet
	 */
	@Test
	public void testBuildSendPacketPresetRecall() {
		int preset = 2;
		byte[] actualPacketLoadPreset = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.CAMERA.getCode(),
				Command.PRESET.getCode(), PresetControl.RECALL.getCode(), (byte) preset);

		assertArrayEquals(SendPacket.PRESET_RECALL.getCode(), actualPacketLoadPreset);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for slow pan-tilt on which match expected packet
	 */
	@Test
	public void testBuildSendPacketSlowPanTiltOn() {
		byte[] actualPacketSlowPanTiltOn = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.SLOW_PAN_TILT.getCode(), SlowPanTiltStatus.ON.getCode());

		assertArrayEquals(SendPacket.SLOW_PAN_TILT_ON.getCode(), actualPacketSlowPanTiltOn);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for slow pan-tilt off which match expected packet
	 */
	@Test
	public void testBuildSendPacketSlowPanTiltOff() {
		byte[] actualPacketSlowPanTiltOff = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.SLOW_PAN_TILT.getCode(), SlowPanTiltStatus.OFF.getCode());

		assertArrayEquals(SendPacket.SLOW_PAN_TILT_OFF.getCode(), actualPacketSlowPanTiltOff);
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

		byte[] actualPacketPanTiltUp = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());

		assertArrayEquals(SendPacket.PAN_TILT_DRIVE_UP.getCode(), actualPacketPanTiltUp);
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

		byte[] actualPacketPanTiltDown = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());

		assertArrayEquals(SendPacket.PAN_TILT_DRIVE_DOWN.getCode(), actualPacketPanTiltDown);
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

		byte[] actualPacketPanTiltLeft = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());

		assertArrayEquals(SendPacket.PAN_TILT_DRIVE_LEFT.getCode(), actualPacketPanTiltLeft);
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

		byte[] actualPacketPanTiltRight = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());

		assertArrayEquals(SendPacket.PAN_TILT_DRIVE_RIGHT.getCode(), actualPacketPanTiltRight);
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

		byte[] actualPacketPanTiltDownLeft = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());

		assertArrayEquals(SendPacket.PAN_TILT_DRIVE_DOWN_LEFT.getCode(), actualPacketPanTiltDownLeft);
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

		byte[] actualPacketPanTiltDownRight = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());

		assertArrayEquals(SendPacket.PAN_TILT_DRIVE_DOWN_RIGHT.getCode(), actualPacketPanTiltDownRight);
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

		byte[] actualPacketPanTiltUpLeft = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());

		assertArrayEquals(SendPacket.PAN_TILT_DRIVE_UP_LEFT.getCode(), actualPacketPanTiltUpLeft);
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

		byte[] actualPacketPanTiltUpRight = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray());

		assertArrayEquals(SendPacket.PAN_TILT_DRIVE_UP_RIGHT.getCode(), actualPacketPanTiltUpRight);
	}

	/**
	 * Test AverPTZUtils#buildSendPacket success
	 * Expect build a control command for pan-tilt drive home which match expected packet
	 */
	@Test
	public void testBuildSendPacketPanTiltDriveHome() {
		byte[] actualPacketPanTiltHome = buildSendPacket(cameraID, sequenceNumber, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), Category.PAN_TILTER.getCode(),
				Command.PAN_TILT_HOME.getCode());

		assertArrayEquals(SendPacket.PAN_TILT_DRIVE_HOME.getCode(), actualPacketPanTiltHome);
	}

	/**
	 * Test AverPTZUtils#buildSendString success
	 * Expect build an inquiry command for power status which match expected string
	 */
	@Test
	public void testBuildSendPacketPowerStatusInq() {
		byte[] actualPacketPowerInq = buildSendPacket(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(), Command.POWER.getCode());

		assertArrayEquals(SendPacket.POWER_STATUS_INQ.getCode(), actualPacketPowerInq);
	}

	/**
	 * Test AverPTZUtils#buildSendString success
	 * Expect build an inquiry command for focus status which match expected string
	 */
	@Test
	public void testBuildSendPacketFocusStatusInq() {
		byte[] actualPacketFocusInq = buildSendPacket(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(), Command.FOCUS_MODE.getCode());

		assertArrayEquals(SendPacket.FOCUS_STATUS_INQ.getCode(), actualPacketFocusInq);
	}

	/**
	 * Test AverPTZUtils#buildSendString success
	 * Expect build an inquiry command for AE Mode which match expected string
	 */
	@Test
	public void testBuildSendPacketAEModeInq() {
		byte[] actualPacketAEModeInq = buildSendPacket(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(), Command.AE_MODE.getCode());

		assertArrayEquals(SendPacket.AE_MODE_INQ.getCode(), actualPacketAEModeInq);
	}

	/**
	 * Test AverPTZUtils#buildSendString success
	 * Expect build an inquiry command for slow shutter which match expected string
	 */
	@Test
	public void testBuildSendPacketSlowShutterInq() {
		byte[] actualPacketSlowShutterInq = buildSendPacket(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(),
				Command.AUTO_SLOW_SHUTTER.getCode());

		assertArrayEquals(SendPacket.SLOW_SHUTTER_STATUS_INQ.getCode(), actualPacketSlowShutterInq);
	}

	/**
	 * Test AverPTZUtils#buildSendString success
	 * Expect build an inquiry command for shutter position which match expected string
	 */
	@Test
	public void testBuildSendPacketShutterPosInq() {
		byte[] actualPacketShutterPosInq = buildSendPacket(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(),
				new byte[] { Command.SHUTTER_DIRECT.getCode()[0] });

		assertArrayEquals(SendPacket.SHUTTER_POS_INQ.getCode(), actualPacketShutterPosInq);
	}

	/**
	 * Test AverPTZUtils#buildSendString success
	 * Expect build an inquiry command for iris position which match expected string
	 */
	@Test
	public void testBuildSendPacketIrisPosInq() {
		byte[] actualPacketIrisPosInq = buildSendPacket(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(),
				new byte[] { Command.IRIS_DIRECT.getCode()[0] });

		assertArrayEquals(SendPacket.IRIS_POS_INQ.getCode(), actualPacketIrisPosInq);
	}

	/**
	 * Test AverPTZUtils#buildSendString success
	 * Expect build an inquiry command for gain position which match expected string
	 */
	@Test
	public void testBuildSendPacketGainPosInq() {
		byte[] actualPacketGainPosInq = buildSendPacket(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(),
				new byte[] { Command.GAIN_DIRECT.getCode()[0] });

		assertArrayEquals(SendPacket.GAIN_POS_INQ.getCode(), actualPacketGainPosInq);
	}

	/**
	 * Test AverPTZUtils#buildSendString success
	 * Expect build an inquiry command for gain limit position which match expected string
	 */
	@Test
	public void testBuildSendPacketGainLimitPosInq() {
		byte[] actualPacketAEGainPosInq = buildSendPacket(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(),
				Command.GAIN_LIMIT_DIRECT.getCode());

		assertArrayEquals(SendPacket.GAIN_LIMIT_POS_INQ.getCode(), actualPacketAEGainPosInq);
	}

	/**
	 * Test AverPTZUtils#buildSendString success
	 * Expect build an inquiry command for exposure position which match expected string
	 */
	@Test
	public void testBuildSendPacketExpCompPosInq() {
		byte[] actualPacketExpCompPosInq = buildSendPacket(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(),
				new byte[] { Command.EXP_COMP_DIRECT.getCode()[0] });

		assertArrayEquals(SendPacket.EXP_COMP_POS_INQ.getCode(), actualPacketExpCompPosInq);
	}

	/**
	 * Test AverPTZUtils#buildSendString success
	 * Expect build an inquiry command for backlight status which match expected string
	 */
	@Test
	public void testBuildSendPacketBacklightInq() {
		byte[] actualPacketExpBacklightInq = buildSendPacket(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(),
				Command.BACKLIGHT.getCode());

		assertArrayEquals(SendPacket.BACKLIGHT_INQ.getCode(), actualPacketExpBacklightInq);
	}

	/**
	 * Test AverPTZUtils#buildSendString success
	 * Expect build an inquiry command for WB mode which match expected string
	 */
	@Test
	public void testBuildSendPacketWBModeInq() {
		byte[] actualPacketWBModeInq = buildSendPacket(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(), Command.WB_MODE.getCode());

		assertArrayEquals(SendPacket.WB_MODE_INQ.getCode(), actualPacketWBModeInq);
	}

	/**
	 * Test AverPTZUtils#buildSendString success
	 * Expect build an inquiry command for RGain value which match expected string
	 */
	@Test
	public void testBuildSendPacketRGainInq() {
		byte[] actualPacketRGainInq = buildSendPacket(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(), Command.RGAIN_INQ.getCode());

		assertArrayEquals(SendPacket.RGAIN_INQ.getCode(), actualPacketRGainInq);
	}

	/**
	 * Test AverPTZUtils#buildSendString success
	 * Expect build an inquiry command for BGain value which match expected string
	 */
	@Test
	public void testBuildSendPacketBGainInq() {
		byte[] actualPacketBGainInq = buildSendPacket(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(), Command.BGAIN_INQ.getCode());

		assertArrayEquals(SendPacket.BGAIN_INQ.getCode(), actualPacketBGainInq);
	}

	/**
	 * Test AverPTZUtils#buildSendString success
	 * Expect build an inquiry command for slow pan-tilt status which match expected string
	 */
	@Test
	public void testBuildSendPacketSlowPanTiltInq() {
		byte[] actualPacketSlowPanTiltInq = buildSendPacket(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.PAN_TILTER.getCode(),
				Command.SLOW_PAN_TILT.getCode());

		assertArrayEquals(SendPacket.SLOW_PAN_TILT_INQ.getCode(), actualPacketSlowPanTiltInq);
	}

	/**
	 * Test AverPTZUtils#buildSendString success
	 * Expect build an inquiry command for last preset recalled which match expected string
	 */
	@Test
	public void testBuildSendPacketPresetRecallInq() {
		byte[] actualPacketSlowPanTiltInq = buildSendPacket(cameraID, sequenceNumber, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), Category.CAMERA.getCode(),
				Command.PRESET.getCode());

		assertArrayEquals(SendPacket.LAST_PRESET_RECALLED.getCode(), actualPacketSlowPanTiltInq);
	}
}
