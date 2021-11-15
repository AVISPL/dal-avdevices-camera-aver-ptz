/*
 * Copyright (c) 2021 AVI-SPL Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.aver.ptz;

import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.HASH;
import static org.mockito.Mockito.times;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.command.Command;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.AEMode;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.BGainControl;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.FocusControl;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.PanTiltDrive;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.RGainControl;
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

	/**
	 * Set power property with value 1 (On)
	 * Expect verify with method powerOn
	 */
	@Test
	public void testPowerOn() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.POWER.getName());
		controllableProperty.setValue("1");
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).powerOn();
	}

	/**
	 * Set power property with value 0 (Off)
	 * Expect verify with method powerOff
	 */
	@Test
	public void testPowerOff() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.POWER.getName());
		controllableProperty.setValue("0");
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).powerOff();
	}

	/**
	 * Set zoom tele property with value null
	 * Expect verify with method zoomTele
	 */
	@Test
	public void testZoomTete() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.ZOOM.getName() + HASH + ZoomControl.TELE.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).zoomTele();
	}

	/**
	 * Set zoom tele property with value null
	 * Expect verify with method zoomWide
	 */
	@Test
	public void testZoomWide() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.ZOOM.getName() + HASH + ZoomControl.WIDE.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).zoomWide();
	}

	/**
	 * Set focus mode property with value 0 (Auto mode)
	 * Expect verify with method autoFocus
	 */
	@Test
	public void testAutoFocusMode() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.FOCUS.getName() + HASH + Command.FOCUS_MODE.getName());
		controllableProperty.setValue("0");
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).autoFocus();
	}

	/**
	 * Set focus mode property with value 1 (Manual mode)
	 * Expect verify with method autoFocus
	 */
	@Test
	public void testManualFocusMode() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.FOCUS.getName() + HASH + Command.FOCUS_MODE.getName());
		controllableProperty.setValue("0");
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).autoFocus();
	}

	/**
	 * Set focus one push mode property with value null
	 * Expect verify with method onePushFocus
	 */
	@Test
	public void testOnePushFocusMode() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.FOCUS.getName() + HASH + Command.FOCUS_ONE_PUSH.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).onePushFocus();
	}

	/**
	 * Set focus control far property with value null
	 * Expect verify with method focusFar
	 */
	@Test
	public void testFocusFar() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.FOCUS.getName() + HASH + FocusControl.FAR.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).focusFar();
	}

	/**
	 * Set focus control near property with value null
	 * Expect verify with method focusNear
	 */
	@Test
	public void testFocusNear() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.FOCUS.getName() + HASH + FocusControl.NEAR.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).focusNear();
	}

	/**
	 * Set backlight property with value 1 (On)
	 * Expect verify with method backlightOn
	 */
	@Test
	public void testBacklightOn() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.BACKLIGHT.getName());
		controllableProperty.setValue("1");
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).backlightOn();
	}

	/**
	 * Set backlight property with value 0 (Off)
	 * Expect verify with method backlightOff
	 */
	@Test
	public void testBacklightOff() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.BACKLIGHT.getName());
		controllableProperty.setValue("0");
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).backlightOff();
	}

	/**
	 * Set AEMode property with value "FullAuto"
	 * Expect verify with method aeFullAuto
	 */
	@Test
	public void testAEFullAutoMode() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.AE_MODE.getName());
		controllableProperty.setValue(AEMode.FULL_AUTO.getName());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).aeFullAuto();
	}

	/**
	 * Set AEMode property with value "IrisPriority"
	 * Expect verify with method aeIrisPriority
	 */
	@Test
	public void testAEIrisPriorityMode() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.AE_MODE.getName());
		controllableProperty.setValue(AEMode.IRIS_PRIORITY.getName());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).aeIrisPriority();
	}

	/**
	 * Set AEMode property with value "ShutterPriority"
	 * Expect verify with method aeShutterPriority
	 */
	@Test
	public void testAEShutterPriorityMode() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.AE_MODE.getName());
		controllableProperty.setValue(AEMode.SHUTTER_PRIORITY.getName());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).aeShutterPriority();
	}

	/**
	 * Set AEMode property with value "Manual"
	 * Expect verify with method aeManual
	 */
	@Test
	public void testAEManualMode() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.AE_MODE.getName());
		controllableProperty.setValue(AEMode.MANUAL.getName());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).aeManual();
	}

	/**
	 * Set exp comp direct property with value 1 (exposure value)
	 * Expect verify with method expCompDirect
	 */
	@Test
	public void testExpCompDirect() {
		float exposureValue = 1;
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.EXP_COMP_DIRECT.getName());
		controllableProperty.setValue(exposureValue);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).expCompDirect((int) exposureValue);
	}

	/**
	 * Set gain limit direct property with value 1 (gain limit level)
	 * Expect verify with method gainLimitDirect
	 */
	@Test
	public void testGainLimitDirect() {
		float gainLimitLevel = 1;
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.GAIN_LIMIT_DIRECT.getName());
		controllableProperty.setValue(gainLimitLevel);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).gainLimitDirect((int) gainLimitLevel);
	}

	/**
	 * Set gain direct property with value 1 (gain level)
	 * Expect verify with method gainDirect
	 */
	@Test
	public void testGainDirect() {
		float gainLevel = 1;
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.GAIN_DIRECT.getName());
		controllableProperty.setValue(gainLevel);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).gainDirect((int) gainLevel);
	}

	/**
	 * Set iris direct property with value 1 (iris level)
	 * Expect verify with method irisDirect
	 */
	@Test
	public void testIrisDirect() {
		float irisLevel = 1;
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.IRIS_DIRECT.getName());
		controllableProperty.setValue(irisLevel);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).irisDirect((int) irisLevel);
	}

	/**
	 * Set shutter direct property with value 1 (shutter level)
	 * Expect verify with method shutterDirect
	 */
	@Test
	public void testShutterDirect() {
		float shutterLevel = 1;
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.SHUTTER_DIRECT.getName());
		controllableProperty.setValue(shutterLevel);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).shutterDirect((int) shutterLevel);
	}

	/**
	 * Set auto slow shutter property with value 1 (On)
	 * Expect verify with method slowShutterOn
	 */
	@Test
	public void testAutoSlowShutterOn() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.AUTO_SLOW_SHUTTER.getName());
		controllableProperty.setValue("1");
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).slowShutterOn();
	}

	/**
	 * Set auto slow shutter property with value 0 (Off)
	 * Expect verify with method slowShutterOff
	 */
	@Test
	public void testAutoSlowShutterOff() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.AUTO_SLOW_SHUTTER.getName());
		controllableProperty.setValue("0");
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).slowShutterOff();
	}

	/**
	 * Set WBMode property with value "Auto"
	 * Expect verify with method wbAuto
	 */
	@Test
	public void testWBAutoMode() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.IMAGE_PROCESS.getName() + HASH + Command.WB_MODE.getName());
		controllableProperty.setValue(WBMode.AUTO.getName());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).wbAuto();
	}

	/**
	 * Set WBMode property with value "Indoor"
	 * Expect verify with method wbIndoor
	 */
	@Test
	public void testWBIndoorMode() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.IMAGE_PROCESS.getName() + HASH + Command.WB_MODE.getName());
		controllableProperty.setValue(WBMode.INDOOR.getName());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).wbIndoor();
	}

	/**
	 * Set WBMode property with value "Outdoor"
	 * Expect verify with method wbOutdoor
	 */
	@Test
	public void testWBOutdoorMode() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.IMAGE_PROCESS.getName() + HASH + Command.WB_MODE.getName());
		controllableProperty.setValue(WBMode.OUTDOOR.getName());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).wbOutdoor();
	}

	/**
	 * Set WBMode property with value "OnePushWB"
	 * Expect verify with method wbOnePush
	 */
	@Test
	public void testWBOnePushMode() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.IMAGE_PROCESS.getName() + HASH + Command.WB_MODE.getName());
		controllableProperty.setValue(WBMode.ONE_PUSH_WB.getName());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).wbOnePush();
	}

	/**
	 * Set WB one push trigger property with value null
	 * Expect verify with method wbOnePushTrigger
	 */
	@Test
	public void testWBOnePushTriggerMode() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.IMAGE_PROCESS.getName() + HASH + Command.WB_ONE_PUSH_TRIGGER.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).wbOnePushTrigger();
	}

	/**
	 * Set RGain up property with value null
	 * Expect verify with method rGainUp
	 */
	@Test
	public void testRGainUp() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.IMAGE_PROCESS.getName() + HASH + Command.RGAIN.getName() + RGainControl.UP.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).rGainUp();
	}

	/**
	 * Set RGain down property with value null
	 * Expect verify with method rGainDown
	 */
	@Test
	public void testRGainDown() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.IMAGE_PROCESS.getName() + HASH + Command.RGAIN.getName() + RGainControl.DOWN.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).rGainDown();
	}

	/**
	 * Set BGain up property with value null
	 * Expect verify with method bGainUp
	 */
	@Test
	public void testBGainUp() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.IMAGE_PROCESS.getName() + HASH + Command.BGAIN.getName() + BGainControl.UP.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).bGainUp();
	}

	/**
	 * Set BGain down property with value null
	 * Expect verify with method bGainDown
	 */
	@Test
	public void testBGainDown() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.IMAGE_PROCESS.getName() + HASH + Command.BGAIN.getName() + BGainControl.DOWN.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).bGainDown();
	}

	/**
	 * Set pan tilt drive up property with value null
	 * Expect verify with method panTiltUp
	 */
	@Test
	public void testPanTiltDriveUp() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.UP.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).panTiltUp();
	}

	/**
	 * Set pan tilt drive down property with value null
	 * Expect verify with method panTiltDown
	 */
	@Test
	public void testPanTiltDriveDown() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.DOWN.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).panTiltDown();
	}

	/**
	 * Set pan tilt drive left property with value null
	 * Expect verify with method panTiltLeft
	 */
	@Test
	public void testPanTiltDriveLeft() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.LEFT.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).panTiltLeft();
	}

	/**
	 * Set pan tilt drive right property with value null
	 * Expect verify with method panTiltRight
	 */
	@Test
	public void testPanTiltDriveRight() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.RIGHT.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).panTiltRight();
	}

	/**
	 * Set pan tilt drive up left property with value null
	 * Expect verify with method panTiltUpLeft
	 */
	@Test
	public void testPanTiltDriveUpLeft() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.UP_LEFT.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).panTiltUpLeft();
	}

	/**
	 * Set pan tilt drive up right property with value null
	 * Expect verify with method panTiltUpRight
	 */
	@Test
	public void testPanTiltDriveUpRight() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.UP_RIGHT.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).panTiltUpRight();
	}

	/**
	 * Set pan tilt drive down left property with value null
	 * Expect verify with method panTiltDownLeft
	 */
	@Test
	public void testPanTiltDriveDownLeft() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.DOWN_LEFT.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).panTiltDownLeft();
	}

	/**
	 * Set pan tilt drive down right property with value null
	 * Expect verify with method panTiltDownRight
	 */
	@Test
	public void testPanTiltDriveDownRight() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.DOWN_RIGHT.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).pantTiltDownRight();
	}

	/**
	 * Set pan tilt home property with value null
	 * Expect verify with method panTiltHome
	 */
	@Test
	public void testPanTiltHome() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + Command.PAN_TILT_HOME.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).panTiltHome();
	}

	/**
	 * Set slow pan tilt property with value 1 (On)
	 * Expect verify with method slowPanTiltOn
	 */
	@Test
	public void testSlowPanTiltOn() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + Command.SLOW_PAN_TILT.getName());
		controllableProperty.setValue("1");
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).slowPanTiltOn();
	}

	/**
	 * Set slow pan tilt property with value 0 (Off)
	 * Expect verify with method slowPanTiltOff
	 */
	@Test
	public void testSlowPanTiltOff() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + Command.SLOW_PAN_TILT.getName());
		controllableProperty.setValue("0");
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).slowPanTiltOff();
	}
}
