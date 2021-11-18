/*
 * Copyright (c) 2021 AVI-SPL Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.aver.ptz;

import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.HASH;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZUtils.convertOneByteNumberToTwoBytesArray;
import static org.mockito.Mockito.times;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.PayloadCategory;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.command.Command;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.AEMode;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.BGainControl;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.BacklightStatus;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.FocusControl;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.FocusMode;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.PanTiltDrive;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.PowerStatus;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.RGainControl;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.SlowPanTiltStatus;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.SlowShutterStatus;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.WBMode;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.ZoomControl;

/**
 * Unit test for Control AverPTZ Communicator
 *
 * @author Harry
 * @version 1.0
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class AverPTZCommunicatorControlTest {
	@Spy
	@InjectMocks
	private AverPTZCommunicator averPTZCommunicator;

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	/**
	 * Set power property with value 1 (On)
	 * Expect verify with method powerOn
	 */
	@Test
	public void testPowerOn() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.POWER.getName());
		controllableProperty.setValue("1");
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.POWER, PowerStatus.ON.getCode());
	}

	/**
	 * Set power property with value 0 (Off)
	 * Expect verify with method powerOff
	 */
	@Test
	public void testPowerOff() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.POWER.getName());
		controllableProperty.setValue("0");
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.POWER, PowerStatus.OFF.getCode());
	}

	/**
	 * Set zoom tele property with value null
	 * Expect verify with method zoomTele
	 */
	@Test
	public void testZoomTete() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.ZOOM.getName() + HASH + ZoomControl.TELE.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.ZOOM, ZoomControl.TELE.getCode());
	}

	/**
	 * Set zoom wide property with value null
	 * Expect verify with method zoomWide
	 */
	@Test
	public void testZoomWide() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.ZOOM.getName() + HASH + ZoomControl.WIDE.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.ZOOM, ZoomControl.WIDE.getCode());
	}

	/**
	 * Set focus mode property with value 0 (Auto mode)
	 * Expect verify with method autoFocus
	 */
	@Test
	public void testAutoFocusMode() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.FOCUS.getName() + HASH + Command.FOCUS_MODE.getName());
		controllableProperty.setValue("0");
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.FOCUS_MODE, FocusMode.AUTO.getCode());
	}

	/**
	 * Set focus mode property with value 1 (Manual mode)
	 * Expect verify with method autoFocus
	 */
	@Test
	public void testManualFocusMode() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.FOCUS.getName() + HASH + Command.FOCUS_MODE.getName());
		controllableProperty.setValue("1");
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.FOCUS_MODE, FocusMode.MANUAL.getCode());
	}

	/**
	 * Set focus one push mode property with value null
	 * Expect verify with method onePushFocus
	 */
	@Test
	public void testOnePushFocusMode() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.FOCUS.getName() + HASH + Command.FOCUS_ONE_PUSH.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.FOCUS_ONE_PUSH);
	}

	/**
	 * Set focus control far property with value null
	 * Expect verify with method focusFar
	 */
	@Test
	public void testFocusFar() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.FOCUS.getName() + HASH + FocusControl.FAR.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.FOCUS, FocusControl.FAR.getCode());
	}

	/**
	 * Set focus control near property with value null
	 * Expect verify with method focusNear
	 */
	@Test
	public void testFocusNear() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.FOCUS.getName() + HASH + FocusControl.NEAR.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.FOCUS, FocusControl.NEAR.getCode());
	}

	/**
	 * Set backlight property with value 1 (On)
	 * Expect verify with method backlightOn
	 */
	@Test
	public void testBacklightOn() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.BACKLIGHT.getName());
		controllableProperty.setValue("1");
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.BACKLIGHT, BacklightStatus.ON.getCode());
	}

	/**
	 * Set backlight property with value 0 (Off)
	 * Expect verify with method backlightOff
	 */
	@Test
	public void testBacklightOff() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.BACKLIGHT.getName());
		controllableProperty.setValue("0");
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.BACKLIGHT, BacklightStatus.OFF.getCode());
	}

	/**
	 * Set AEMode property with value "FullAuto"
	 * Expect verify with method aeFullAuto
	 */
	@Test
	public void testAEFullAutoMode() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.AE_MODE.getName());
		controllableProperty.setValue(AEMode.FULL_AUTO.getName());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.AE_MODE, AEMode.FULL_AUTO.getCode());
	}

	/**
	 * Set AEMode property with value "IrisPriority"
	 * Expect verify with method aeIrisPriority
	 */
	@Test
	public void testAEIrisPriorityMode() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.AE_MODE.getName());
		controllableProperty.setValue(AEMode.IRIS_PRIORITY.getName());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.AE_MODE, AEMode.IRIS_PRIORITY.getCode());
	}

	/**
	 * Set AEMode property with value "ShutterPriority"
	 * Expect verify with method aeShutterPriority
	 */
	@Test
	public void testAEShutterPriorityMode() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.AE_MODE.getName());
		controllableProperty.setValue(AEMode.SHUTTER_PRIORITY.getName());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.AE_MODE, AEMode.SHUTTER_PRIORITY.getCode());
	}

	/**
	 * Set AEMode property with value "Manual"
	 * Expect verify with method aeManual
	 */
	@Test
	public void testAEManualMode() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.AE_MODE.getName());
		controllableProperty.setValue(AEMode.MANUAL.getName());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.AE_MODE, AEMode.MANUAL.getCode());
	}

	/**
	 * Set exp comp direct property with value 1 (exposure value)
	 * Expect verify with method expCompDirect
	 */
	@Test
	public void testExpCompDirect() throws IOException {
		float exposureValue = 1;
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.EXP_COMP_DIRECT.getName());
		controllableProperty.setValue(exposureValue);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.EXP_COMP_DIRECT, convertOneByteNumberToTwoBytesArray((byte) ((int) exposureValue)));
	}

	/**
	 * Set gain limit direct property with value 1 (gain limit level)
	 * Expect verify with method gainLimitDirect
	 */
	@Test
	public void testGainLimitDirect() throws IOException {
		float gainLimitLevel = 1;
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.GAIN_LIMIT_DIRECT.getName());
		controllableProperty.setValue(gainLimitLevel);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.GAIN_LIMIT_DIRECT, convertOneByteNumberToTwoBytesArray((byte) ((int) gainLimitLevel)));
	}

	/**
	 * Set gain direct property with value 1 (gain level)
	 * Expect verify with method gainDirect
	 */
	@Test
	public void testGainDirect() throws IOException {
		float gainLevel = 1;
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.GAIN_DIRECT.getName());
		controllableProperty.setValue(gainLevel);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.GAIN_LIMIT_DIRECT, (byte) ((int) gainLevel));
	}

	/**
	 * Set iris direct property with value 1 (iris level)
	 * Expect verify with method irisDirect
	 */
	@Test
	public void testIrisDirect() throws IOException {
		float irisLevel = 1;
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.IRIS_DIRECT.getName());
		controllableProperty.setValue(irisLevel);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.GAIN_LIMIT_DIRECT, convertOneByteNumberToTwoBytesArray((byte) ((int) irisLevel)));
	}

	/**
	 * Set shutter direct property with value 1 (shutter level)
	 * Expect verify with method shutterDirect
	 */
	@Test
	public void testShutterDirect() throws IOException {
		float shutterLevel = 1;
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.SHUTTER_DIRECT.getName());
		controllableProperty.setValue(shutterLevel);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.SHUTTER_DIRECT, convertOneByteNumberToTwoBytesArray((byte) ((int) shutterLevel)));
	}

	/**
	 * Set auto slow shutter property with value 1 (On)
	 * Expect verify with method slowShutterOn
	 */
	@Test
	public void testAutoSlowShutterOn() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.AUTO_SLOW_SHUTTER.getName());
		controllableProperty.setValue("1");
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.AUTO_SLOW_SHUTTER, SlowShutterStatus.ON.getCode());
	}

	/**
	 * Set auto slow shutter property with value 0 (Off)
	 * Expect verify with method slowShutterOff
	 */
	@Test
	public void testAutoSlowShutterOff() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.AUTO_SLOW_SHUTTER.getName());
		controllableProperty.setValue("0");
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.AUTO_SLOW_SHUTTER, SlowShutterStatus.OFF.getCode());
	}

	/**
	 * Set WBMode property with value "Auto"
	 * Expect verify with method wbAuto
	 */
	@Test
	public void testWBAutoMode() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.IMAGE_PROCESS.getName() + HASH + Command.WB_MODE.getName());
		controllableProperty.setValue(WBMode.AUTO.getName());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.WB_MODE, WBMode.AUTO.getCode());
	}

	/**
	 * Set WBMode property with value "Indoor"
	 * Expect verify with method wbIndoor
	 */
	@Test
	public void testWBIndoorMode() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.IMAGE_PROCESS.getName() + HASH + Command.WB_MODE.getName());
		controllableProperty.setValue(WBMode.INDOOR.getName());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.WB_MODE, WBMode.INDOOR.getCode());
	}

	/**
	 * Set WBMode property with value "Outdoor"
	 * Expect verify with method wbOutdoor
	 */
	@Test
	public void testWBOutdoorMode() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.IMAGE_PROCESS.getName() + HASH + Command.WB_MODE.getName());
		controllableProperty.setValue(WBMode.OUTDOOR.getName());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.WB_MODE, WBMode.OUTDOOR.getCode());
	}

	/**
	 * Set WBMode property with value "OnePushWB"
	 * Expect verify with method wbOnePush
	 */
	@Test
	public void testWBOnePushMode() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.IMAGE_PROCESS.getName() + HASH + Command.WB_MODE.getName());
		controllableProperty.setValue(WBMode.ONE_PUSH_WB.getName());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.WB_MODE, WBMode.ONE_PUSH_WB.getCode());
	}

	/**
	 * Set WB one push trigger property with value null
	 * Expect verify with method wbOnePushTrigger
	 */
	@Test
	public void testWBOnePushTriggerMode() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.IMAGE_PROCESS.getName() + HASH + Command.WB_ONE_PUSH_TRIGGER.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.WB_ONE_PUSH_TRIGGER);
	}

	/**
	 * Set RGain up property with value null
	 * Expect verify with method rGainUp
	 */
	@Test
	public void testRGainUp() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.IMAGE_PROCESS.getName() + HASH + Command.RGAIN.getName() + RGainControl.UP.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.RGAIN, RGainControl.UP.getCode());
	}

	/**
	 * Set RGain down property with value null
	 * Expect verify with method rGainDown
	 */
	@Test
	public void testRGainDown() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.IMAGE_PROCESS.getName() + HASH + Command.RGAIN.getName() + RGainControl.DOWN.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.RGAIN, RGainControl.DOWN.getCode());
	}

	/**
	 * Set BGain up property with value null
	 * Expect verify with method bGainUp
	 */
	@Test
	public void testBGainUp() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.IMAGE_PROCESS.getName() + HASH + Command.BGAIN.getName() + BGainControl.UP.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.BGAIN, BGainControl.UP.getCode());
	}

	/**
	 * Set BGain down property with value null
	 * Expect verify with method bGainDown
	 */
	@Test
	public void testBGainDown() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.IMAGE_PROCESS.getName() + HASH + Command.BGAIN.getName() + BGainControl.DOWN.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.BGAIN, BGainControl.DOWN.getCode());
	}

	/**
	 * Set pan tilt drive up property with value null
	 * Expect verify with method panTiltUp
	 */
	@Test
	public void testPanTiltDriveUp() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.UP.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, PanTiltDrive.UP.getCode());
	}

	/**
	 * Set pan tilt drive down property with value null
	 * Expect verify with method panTiltDown
	 */
	@Test
	public void testPanTiltDriveDown() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.DOWN.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, PanTiltDrive.DOWN.getCode());
	}

	/**
	 * Set pan tilt drive left property with value null
	 * Expect verify with method panTiltLeft
	 */
	@Test
	public void testPanTiltDriveLeft() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.LEFT.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, PanTiltDrive.LEFT.getCode());
	}

	/**
	 * Set pan tilt drive right property with value null
	 * Expect verify with method panTiltRight
	 */
	@Test
	public void testPanTiltDriveRight() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.RIGHT.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, PanTiltDrive.RIGHT.getCode());
	}

	/**
	 * Set pan tilt drive up left property with value null
	 * Expect verify with method panTiltUpLeft
	 */
	@Test
	public void testPanTiltDriveUpLeft() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.UP_LEFT.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, PanTiltDrive.UP_LEFT.getCode());
	}

	/**
	 * Set pan tilt drive up right property with value null
	 * Expect verify with method panTiltUpRight
	 */
	@Test
	public void testPanTiltDriveUpRight() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.UP_RIGHT.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, PanTiltDrive.UP_RIGHT.getCode());
	}

	/**
	 * Set pan tilt drive down left property with value null
	 * Expect verify with method panTiltDownLeft
	 */
	@Test
	public void testPanTiltDriveDownLeft() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.DOWN_LEFT.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, PanTiltDrive.DOWN_LEFT.getCode());
	}

	/**
	 * Set pan tilt drive down right property with value null
	 * Expect verify with method panTiltDownRight
	 */
	@Test
	public void testPanTiltDriveDownRight() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.DOWN_RIGHT.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, PanTiltDrive.DOWN_RIGHT.getCode());
	}

	/**
	 * Set pan tilt home property with value null
	 * Expect verify with method panTiltHome
	 */
	@Test
	public void testPanTiltHome() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + Command.PAN_TILT_HOME.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_HOME);
	}

	/**
	 * Set slow pan tilt property with value 1 (On)
	 * Expect verify with method slowPanTiltOn
	 */
	@Test
	public void testSlowPanTiltOn() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + Command.SLOW_PAN_TILT.getName());
		controllableProperty.setValue("1");
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.PAN_TILTER, Command.SLOW_PAN_TILT, SlowPanTiltStatus.ON.getCode());
	}

	/**
	 * Set slow pan tilt property with value 0 (Off)
	 * Expect verify with method slowPanTiltOff
	 */
	@Test
	public void testSlowPanTiltOff() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + Command.SLOW_PAN_TILT.getName());
		controllableProperty.setValue("0");
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.PAN_TILTER, Command.SLOW_PAN_TILT, SlowPanTiltStatus.OFF.getCode());
	}

	/**
	 * Set controllable property with RGAIN_INQ property
	 * Expect throw IllegalStateException with right message
	 */
	@Test
	public void testInvalidControlProperty() throws IOException {
		exceptionRule.expect(IllegalStateException.class);
		exceptionRule.expectMessage("Unexpected value: " + Command.RGAIN_INQ);

		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.RGAIN_INQ.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
	}

	/**
	 * Set controllable property with Image process + AE mode property (AE mode not belong to Image process)
	 * Expect throw IllegalStateException with right message
	 */
	@Test
	public void testInvalidImageProcessProperty() throws IOException {
		String property = Command.IMAGE_PROCESS.getName() + HASH + Command.AE_MODE.getName();
		String[] splitProperty = property.split(String.valueOf(HASH));

		exceptionRule.expect(IllegalStateException.class);
		exceptionRule.expectMessage("Unexpected value: " + Arrays.toString(splitProperty));

		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(property);
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
	}

	/**
	 * Set controllable property with Exposure + WB mode property (WB mode not belong to Exposure)
	 * Expect throw IllegalStateException with right message
	 */
	@Test
	public void testInvalidExposureProperty() throws IOException {
		exceptionRule.expect(IllegalStateException.class);
		exceptionRule.expectMessage("Unexpected value: " + Command.WB_MODE);

		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.WB_MODE.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
	}
}
