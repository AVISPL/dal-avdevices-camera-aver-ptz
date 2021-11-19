/*
 * Copyright (c) 2021 AVI-SPL Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.aver.ptz;

import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.HASH;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZUtils.convertOneByteNumberToTwoBytesArray;
import static org.mockito.Mockito.times;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
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
import com.avispl.symphony.dal.communicator.aver.ptz.interfaces.MockTest;

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
	@Category(MockTest.class)
	public void testPowerOn() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.POWER.getName());
		controllableProperty.setValue("1");

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.POWER, PowerStatus.ON.getCode());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.POWER, PowerStatus.ON.getCode());
	}

	/**
	 * Set power property with value 0 (Off)
	 * Expect verify with method powerOff
	 */
	@Test
	@Category(MockTest.class)
	public void testPowerOff() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.POWER.getName());
		controllableProperty.setValue("0");

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.POWER, PowerStatus.OFF.getCode());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.POWER, PowerStatus.OFF.getCode());
	}

	/**
	 * Set zoom tele property with value null
	 * Expect verify with method zoomTele
	 */
	@Test
	@Category(MockTest.class)
	public void testZoomTete() throws IOException {
		int zoomSpeed = 1;
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.ZOOM.getName() + HASH + ZoomControl.TELE.getName());
		controllableProperty.setValue(null);

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.ZOOM, (byte) (ZoomControl.TELE.getCode() + zoomSpeed));
		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.ZOOM, ZoomControl.STOP.getCode());

		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.ZOOM, (byte) (ZoomControl.TELE.getCode() + zoomSpeed));
	}

	/**
	 * Set zoom wide property with value null
	 * Expect verify with method zoomWide
	 */
	@Test
	@Category(MockTest.class)
	public void testZoomWide() throws IOException {
		int zoomSpeed = 1;
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.ZOOM.getName() + HASH + ZoomControl.WIDE.getName());
		controllableProperty.setValue(null);

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.ZOOM, (byte) (ZoomControl.WIDE.getCode() + zoomSpeed));
		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.ZOOM, ZoomControl.STOP.getCode());

		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.ZOOM, (byte) (ZoomControl.WIDE.getCode() + zoomSpeed));
	}

	/**
	 * Set focus mode property with value 0 (Auto mode)
	 * Expect verify with method autoFocus
	 */
	@Test
	@Category(MockTest.class)
	public void testAutoFocusMode() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.FOCUS.getName() + HASH + Command.FOCUS_MODE.getName());
		controllableProperty.setValue("0");

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.FOCUS_MODE, FocusMode.AUTO.getCode());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.FOCUS_MODE, FocusMode.AUTO.getCode());
	}

	/**
	 * Set focus mode property with value 1 (Manual mode)
	 * Expect verify with method autoFocus
	 */
	@Test
	@Category(MockTest.class)
	public void testManualFocusMode() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.FOCUS.getName() + HASH + Command.FOCUS_MODE.getName());
		controllableProperty.setValue("1");

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.FOCUS_MODE, FocusMode.MANUAL.getCode());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.FOCUS_MODE, FocusMode.MANUAL.getCode());
	}

	/**
	 * Set focus one push mode property with value null
	 * Expect verify with method onePushFocus
	 */
	@Test
	@Category(MockTest.class)
	public void testOnePushFocusMode() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.FOCUS.getName() + HASH + Command.FOCUS_ONE_PUSH.getName());
		controllableProperty.setValue(null);

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.FOCUS_ONE_PUSH);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.FOCUS_ONE_PUSH);
	}

	/**
	 * Set focus control far property with value null
	 * Expect verify with method focusFar
	 */
	@Test
	@Category(MockTest.class)
	public void testFocusFar() throws IOException {
		int focusSpeed = 1;
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.FOCUS.getName() + HASH + FocusControl.FAR.getName());
		controllableProperty.setValue(null);

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.FOCUS, (byte) (FocusControl.FAR.getCode() + focusSpeed));
		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.FOCUS, FocusControl.STOP.getCode());

		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.FOCUS, (byte) (FocusControl.FAR.getCode() + focusSpeed));
	}

	/**
	 * Set focus control near property with value null
	 * Expect verify with method focusNear
	 */
	@Test
	@Category(MockTest.class)
	public void testFocusNear() throws IOException {
		int focusSpeed = 1;
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.FOCUS.getName() + HASH + FocusControl.NEAR.getName());
		controllableProperty.setValue(null);

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.FOCUS, (byte) (FocusControl.NEAR.getCode() + focusSpeed));
		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.FOCUS, FocusControl.STOP.getCode());

		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.FOCUS, (byte) (FocusControl.NEAR.getCode() + focusSpeed));
	}

	/**
	 * Set backlight property with value 1 (On)
	 * Expect verify with method backlightOn
	 */
	@Test
	@Category(MockTest.class)
	public void testBacklightOn() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.BACKLIGHT.getName());
		controllableProperty.setValue("1");

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.BACKLIGHT, BacklightStatus.ON.getCode());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.BACKLIGHT, BacklightStatus.ON.getCode());
	}

	/**
	 * Set backlight property with value 0 (Off)
	 * Expect verify with method backlightOff
	 */
	@Test
	@Category(MockTest.class)
	public void testBacklightOff() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.BACKLIGHT.getName());
		controllableProperty.setValue("0");

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.BACKLIGHT, BacklightStatus.OFF.getCode());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.BACKLIGHT, BacklightStatus.OFF.getCode());
	}

	/**
	 * Set AEMode property with value "FullAuto"
	 * Expect verify with method aeFullAuto
	 */
	@Test
	@Category(MockTest.class)
	public void testAEFullAutoMode() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.AE_MODE.getName());
		controllableProperty.setValue(AEMode.FULL_AUTO.getName());

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.AE_MODE, AEMode.FULL_AUTO.getCode());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.AE_MODE, AEMode.FULL_AUTO.getCode());
	}

	/**
	 * Set AEMode property with value "IrisPriority"
	 * Expect verify with method aeIrisPriority
	 */
	@Test
	@Category(MockTest.class)
	public void testAEIrisPriorityMode() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.AE_MODE.getName());
		controllableProperty.setValue(AEMode.IRIS_PRIORITY.getName());

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.AE_MODE, AEMode.IRIS_PRIORITY.getCode());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.AE_MODE, AEMode.IRIS_PRIORITY.getCode());
	}

	/**
	 * Set AEMode property with value "ShutterPriority"
	 * Expect verify with method aeShutterPriority
	 */
	@Test
	@Category(MockTest.class)
	public void testAEShutterPriorityMode() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.AE_MODE.getName());
		controllableProperty.setValue(AEMode.SHUTTER_PRIORITY.getName());

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.AE_MODE, AEMode.SHUTTER_PRIORITY.getCode());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.AE_MODE, AEMode.SHUTTER_PRIORITY.getCode());
	}

	/**
	 * Set AEMode property with value "Manual"
	 * Expect verify with method aeManual
	 */
	@Test
	@Category(MockTest.class)
	public void testAEManualMode() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.AE_MODE.getName());
		controllableProperty.setValue(AEMode.MANUAL.getName());

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.AE_MODE, AEMode.MANUAL.getCode());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.AE_MODE, AEMode.MANUAL.getCode());
	}

	/**
	 * Set exp comp direct property with value 1 (exposure value)
	 * Expect verify with method expCompDirect
	 */
	@Test
	@Category(MockTest.class)
	public void testExpCompDirect() throws IOException {
		float exposureValue = 1;
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.EXP_COMP_DIRECT.getName());
		controllableProperty.setValue(exposureValue);

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.EXP_COMP_DIRECT, convertOneByteNumberToTwoBytesArray((byte) exposureValue));
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.EXP_COMP_DIRECT, convertOneByteNumberToTwoBytesArray((byte) exposureValue));
	}

	/**
	 * Set gain limit direct property with value 1 (gain limit level)
	 * Expect verify with method gainLimitDirect
	 */
	@Test
	@Category(MockTest.class)
	public void testGainLimitDirect() throws IOException {
		float gainLimitLevel = 1;
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.GAIN_LIMIT_DIRECT.getName());
		controllableProperty.setValue(gainLimitLevel);

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.GAIN_LIMIT_DIRECT, (byte) gainLimitLevel);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.GAIN_LIMIT_DIRECT, (byte) gainLimitLevel);
	}

	/**
	 * Set gain direct property with value 1 (gain level)
	 * Expect verify with method gainDirect
	 */
	@Test
	@Category(MockTest.class)
	public void testGainDirect() throws IOException {
		float gainLevel = 1;
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.GAIN_DIRECT.getName());
		controllableProperty.setValue(gainLevel);

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.GAIN_DIRECT, convertOneByteNumberToTwoBytesArray((byte) gainLevel));
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.GAIN_DIRECT, convertOneByteNumberToTwoBytesArray((byte) gainLevel));
	}

	/**
	 * Set iris direct property with value 1 (iris level)
	 * Expect verify with method irisDirect
	 */
	@Test
	@Category(MockTest.class)
	public void testIrisDirect() throws IOException {
		float irisLevel = 1;
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.IRIS_DIRECT.getName());
		controllableProperty.setValue(irisLevel);

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.IRIS_DIRECT, convertOneByteNumberToTwoBytesArray((byte) irisLevel));
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.IRIS_DIRECT, convertOneByteNumberToTwoBytesArray((byte) irisLevel));
	}

	/**
	 * Set shutter direct property with value 1 (shutter level)
	 * Expect verify with method shutterDirect
	 */
	@Test
	@Category(MockTest.class)
	public void testShutterDirect() throws IOException {
		float shutterLevel = 1;
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.SHUTTER_DIRECT.getName());
		controllableProperty.setValue(shutterLevel);

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.SHUTTER_DIRECT, convertOneByteNumberToTwoBytesArray((byte) shutterLevel));
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.SHUTTER_DIRECT, convertOneByteNumberToTwoBytesArray((byte) shutterLevel));
	}

	/**
	 * Set auto slow shutter property with value 1 (On)
	 * Expect verify with method slowShutterOn
	 */
	@Test
	@Category(MockTest.class)
	public void testAutoSlowShutterOn() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.AUTO_SLOW_SHUTTER.getName());
		controllableProperty.setValue("1");

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.AUTO_SLOW_SHUTTER, SlowShutterStatus.ON.getCode());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.AUTO_SLOW_SHUTTER, SlowShutterStatus.ON.getCode());
	}

	/**
	 * Set auto slow shutter property with value 0 (Off)
	 * Expect verify with method slowShutterOff
	 */
	@Test
	@Category(MockTest.class)
	public void testAutoSlowShutterOff() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.AUTO_SLOW_SHUTTER.getName());
		controllableProperty.setValue("0");

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.AUTO_SLOW_SHUTTER, SlowShutterStatus.OFF.getCode());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.AUTO_SLOW_SHUTTER, SlowShutterStatus.OFF.getCode());
	}

	/**
	 * Set WBMode property with value "Auto"
	 * Expect verify with method wbAuto
	 */
	@Test
	@Category(MockTest.class)
	public void testWBAutoMode() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.IMAGE_PROCESS.getName() + HASH + Command.WB_MODE.getName());
		controllableProperty.setValue(WBMode.AUTO.getName());

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.WB_MODE, WBMode.AUTO.getCode());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.WB_MODE, WBMode.AUTO.getCode());
	}

	/**
	 * Set WBMode property with value "Indoor"
	 * Expect verify with method wbIndoor
	 */
	@Test
	@Category(MockTest.class)
	public void testWBIndoorMode() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.IMAGE_PROCESS.getName() + HASH + Command.WB_MODE.getName());
		controllableProperty.setValue(WBMode.INDOOR.getName());

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.WB_MODE, WBMode.INDOOR.getCode());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.WB_MODE, WBMode.INDOOR.getCode());
	}

	/**
	 * Set WBMode property with value "Outdoor"
	 * Expect verify with method wbOutdoor
	 */
	@Test
	@Category(MockTest.class)
	public void testWBOutdoorMode() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.IMAGE_PROCESS.getName() + HASH + Command.WB_MODE.getName());
		controllableProperty.setValue(WBMode.OUTDOOR.getName());

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.WB_MODE, WBMode.OUTDOOR.getCode());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.WB_MODE, WBMode.OUTDOOR.getCode());
	}

	/**
	 * Set WBMode property with value "OnePushWB"
	 * Expect verify with method wbOnePush
	 */
	@Test
	@Category(MockTest.class)
	public void testWBOnePushMode() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.IMAGE_PROCESS.getName() + HASH + Command.WB_MODE.getName());
		controllableProperty.setValue(WBMode.ONE_PUSH_WB.getName());

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.WB_MODE, WBMode.ONE_PUSH_WB.getCode());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.WB_MODE, WBMode.ONE_PUSH_WB.getCode());
	}

	/**
	 * Set WB one push trigger property with value null
	 * Expect verify with method wbOnePushTrigger
	 */
	@Test
	@Category(MockTest.class)
	public void testWBOnePushTriggerMode() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.IMAGE_PROCESS.getName() + HASH + Command.WB_ONE_PUSH_TRIGGER.getName());
		controllableProperty.setValue(null);

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.WB_ONE_PUSH_TRIGGER);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.WB_ONE_PUSH_TRIGGER);
	}

	/**
	 * Set RGain up property with value null
	 * Expect verify with method rGainUp
	 */
	@Test
	@Category(MockTest.class)
	public void testRGainUp() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.IMAGE_PROCESS.getName() + HASH + Command.RGAIN.getName() + RGainControl.UP.getName());
		controllableProperty.setValue(null);

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.RGAIN, RGainControl.UP.getCode());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.RGAIN, RGainControl.UP.getCode());
	}

	/**
	 * Set RGain down property with value null
	 * Expect verify with method rGainDown
	 */
	@Test
	@Category(MockTest.class)
	public void testRGainDown() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.IMAGE_PROCESS.getName() + HASH + Command.RGAIN.getName() + RGainControl.DOWN.getName());
		controllableProperty.setValue(null);

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.RGAIN, RGainControl.DOWN.getCode());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.RGAIN, RGainControl.DOWN.getCode());
	}

	/**
	 * Set BGain up property with value null
	 * Expect verify with method bGainUp
	 */
	@Test
	@Category(MockTest.class)
	public void testBGainUp() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.IMAGE_PROCESS.getName() + HASH + Command.BGAIN.getName() + BGainControl.UP.getName());
		controllableProperty.setValue(null);

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.BGAIN, BGainControl.UP.getCode());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.BGAIN, BGainControl.UP.getCode());
	}

	/**
	 * Set BGain down property with value null
	 * Expect verify with method bGainDown
	 */
	@Test
	@Category(MockTest.class)
	public void testBGainDown() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.IMAGE_PROCESS.getName() + HASH + Command.BGAIN.getName() + BGainControl.DOWN.getName());
		controllableProperty.setValue(null);

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.CAMERA, Command.BGAIN, BGainControl.DOWN.getCode());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.CAMERA, Command.BGAIN, BGainControl.DOWN.getCode());
	}

	/**
	 * Set pan tilt drive up property with value null
	 * Expect verify with method panTiltUp
	 */
	@Test
	@Category(MockTest.class)
	public void testPanTiltDriveUp() throws IOException {
		int panSpeed = 1, tiltSpeed = 1;
		ByteArrayOutputStream outputStreamDrive = new ByteArrayOutputStream();
		outputStreamDrive.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStreamDrive.write(PanTiltDrive.UP.getCode());

		ByteArrayOutputStream outputStreamStop = new ByteArrayOutputStream();
		outputStreamStop.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStreamStop.write(PanTiltDrive.STOP.getCode());

		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.UP.getName());
		controllableProperty.setValue(null);

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, outputStreamDrive.toByteArray());
		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, outputStreamStop.toByteArray());

		averPTZCommunicator.controlProperty(controllableProperty);

		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, outputStreamDrive.toByteArray());
	}

	/**
	 * Set pan tilt drive down property with value null
	 * Expect verify with method panTiltDown
	 */
	@Test
	@Category(MockTest.class)
	public void testPanTiltDriveDown() throws IOException {
		int panSpeed = 1, tiltSpeed = 1;
		ByteArrayOutputStream outputStreamDrive = new ByteArrayOutputStream();
		outputStreamDrive.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStreamDrive.write(PanTiltDrive.DOWN.getCode());

		ByteArrayOutputStream outputStreamStop = new ByteArrayOutputStream();
		outputStreamStop.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStreamStop.write(PanTiltDrive.STOP.getCode());

		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.DOWN.getName());
		controllableProperty.setValue(null);

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, outputStreamDrive.toByteArray());
		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, outputStreamStop.toByteArray());

		averPTZCommunicator.controlProperty(controllableProperty);

		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, outputStreamDrive.toByteArray());
	}

	/**
	 * Set pan tilt drive left property with value null
	 * Expect verify with method panTiltLeft
	 */
	@Test
	@Category(MockTest.class)
	public void testPanTiltDriveLeft() throws IOException {
		int panSpeed = 1, tiltSpeed = 1;
		ByteArrayOutputStream outputStreamDrive = new ByteArrayOutputStream();
		outputStreamDrive.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStreamDrive.write(PanTiltDrive.LEFT.getCode());

		ByteArrayOutputStream outputStreamStop = new ByteArrayOutputStream();
		outputStreamStop.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStreamStop.write(PanTiltDrive.STOP.getCode());

		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.LEFT.getName());
		controllableProperty.setValue(null);

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, outputStreamDrive.toByteArray());
		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, outputStreamStop.toByteArray());

		averPTZCommunicator.controlProperty(controllableProperty);

		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, outputStreamDrive.toByteArray());
	}

	/**
	 * Set pan tilt drive right property with value null
	 * Expect verify with method panTiltRight
	 */
	@Test
	@Category(MockTest.class)
	public void testPanTiltDriveRight() throws IOException {
		int panSpeed = 1, tiltSpeed = 1;
		ByteArrayOutputStream outputStreamDrive = new ByteArrayOutputStream();
		outputStreamDrive.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStreamDrive.write(PanTiltDrive.RIGHT.getCode());

		ByteArrayOutputStream outputStreamStop = new ByteArrayOutputStream();
		outputStreamStop.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStreamStop.write(PanTiltDrive.STOP.getCode());

		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.RIGHT.getName());
		controllableProperty.setValue(null);

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, outputStreamDrive.toByteArray());
		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, outputStreamStop.toByteArray());

		averPTZCommunicator.controlProperty(controllableProperty);

		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, outputStreamDrive.toByteArray());
	}

	/**
	 * Set pan tilt drive up left property with value null
	 * Expect verify with method panTiltUpLeft
	 */
	@Test
	@Category(MockTest.class)
	public void testPanTiltDriveUpLeft() throws IOException {
		int panSpeed = 1, tiltSpeed = 1;
		ByteArrayOutputStream outputStreamDrive = new ByteArrayOutputStream();
		outputStreamDrive.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStreamDrive.write(PanTiltDrive.UP_LEFT.getCode());

		ByteArrayOutputStream outputStreamStop = new ByteArrayOutputStream();
		outputStreamStop.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStreamStop.write(PanTiltDrive.STOP.getCode());

		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.UP_LEFT.getName());
		controllableProperty.setValue(null);

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, outputStreamDrive.toByteArray());
		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, outputStreamStop.toByteArray());

		averPTZCommunicator.controlProperty(controllableProperty);

		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, outputStreamDrive.toByteArray());
	}

	/**
	 * Set pan tilt drive up right property with value null
	 * Expect verify with method panTiltUpRight
	 */
	@Test
	@Category(MockTest.class)
	public void testPanTiltDriveUpRight() throws IOException {
		int panSpeed = 1, tiltSpeed = 1;
		ByteArrayOutputStream outputStreamDrive = new ByteArrayOutputStream();
		outputStreamDrive.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStreamDrive.write(PanTiltDrive.UP_RIGHT.getCode());

		ByteArrayOutputStream outputStreamStop = new ByteArrayOutputStream();
		outputStreamStop.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStreamStop.write(PanTiltDrive.STOP.getCode());

		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.UP_RIGHT.getName());
		controllableProperty.setValue(null);

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, outputStreamDrive.toByteArray());
		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, outputStreamStop.toByteArray());

		averPTZCommunicator.controlProperty(controllableProperty);

		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, outputStreamDrive.toByteArray());
	}

	/**
	 * Set pan tilt drive down left property with value null
	 * Expect verify with method panTiltDownLeft
	 */
	@Test
	@Category(MockTest.class)
	public void testPanTiltDriveDownLeft() throws IOException {
		int panSpeed = 1, tiltSpeed = 1;
		ByteArrayOutputStream outputStreamDrive = new ByteArrayOutputStream();
		outputStreamDrive.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStreamDrive.write(PanTiltDrive.DOWN_LEFT.getCode());

		ByteArrayOutputStream outputStreamStop = new ByteArrayOutputStream();
		outputStreamStop.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStreamStop.write(PanTiltDrive.STOP.getCode());

		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.DOWN_LEFT.getName());
		controllableProperty.setValue(null);

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, outputStreamDrive.toByteArray());
		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, outputStreamStop.toByteArray());

		averPTZCommunicator.controlProperty(controllableProperty);

		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, outputStreamDrive.toByteArray());
	}

	/**
	 * Set pan tilt drive down right property with value null
	 * Expect verify with method panTiltDownRight
	 */
	@Test
	@Category(MockTest.class)
	public void testPanTiltDriveDownRight() throws IOException {
		int panSpeed = 1, tiltSpeed = 1;
		ByteArrayOutputStream outputStreamDrive = new ByteArrayOutputStream();
		outputStreamDrive.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStreamDrive.write(PanTiltDrive.DOWN_RIGHT.getCode());

		ByteArrayOutputStream outputStreamStop = new ByteArrayOutputStream();
		outputStreamStop.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
		outputStreamStop.write(PanTiltDrive.STOP.getCode());

		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.DOWN_RIGHT.getName());
		controllableProperty.setValue(null);

		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, outputStreamDrive.toByteArray());
		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, outputStreamStop.toByteArray());

		averPTZCommunicator.controlProperty(controllableProperty);

		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, outputStreamDrive.toByteArray());
	}

	/**
	 * Set pan tilt home property with value null
	 * Expect verify with method panTiltHome
	 */
	@Test
	@Category(MockTest.class)
	public void testPanTiltHome() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + Command.PAN_TILT_HOME.getName());
		controllableProperty.setValue(null);
		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_HOME);
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_HOME);
	}

	/**
	 * Set slow pan tilt property with value 1 (On)
	 * Expect verify with method slowPanTiltOn
	 */
	@Test
	@Category(MockTest.class)
	public void testSlowPanTiltOn() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + Command.SLOW_PAN_TILT.getName());
		controllableProperty.setValue("1");
		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.PAN_TILTER, Command.SLOW_PAN_TILT, SlowPanTiltStatus.ON.getCode());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.PAN_TILTER, Command.SLOW_PAN_TILT, SlowPanTiltStatus.ON.getCode());
	}

	/**
	 * Set slow pan tilt property with value 0 (Off)
	 * Expect verify with method slowPanTiltOff
	 */
	@Test
	@Category(MockTest.class)
	public void testSlowPanTiltOff() throws IOException {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.PAN_TILT_DRIVE.getName() + HASH + Command.SLOW_PAN_TILT.getName());
		controllableProperty.setValue("0");
		Mockito.doNothing().when(averPTZCommunicator).performControl(PayloadCategory.PAN_TILTER, Command.SLOW_PAN_TILT, SlowPanTiltStatus.OFF.getCode());
		averPTZCommunicator.controlProperty(controllableProperty);
		Mockito.verify(averPTZCommunicator, times(1)).performControl(PayloadCategory.PAN_TILTER, Command.SLOW_PAN_TILT, SlowPanTiltStatus.OFF.getCode());
	}

	/**
	 * Set controllable property with RGAIN_INQ property
	 * Expect throw IllegalStateException with right message
	 */
	@Test
	@Category(MockTest.class)
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
	@Category(MockTest.class)
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
	@Category(MockTest.class)
	public void testInvalidExposureProperty() throws IOException {
		exceptionRule.expect(IllegalStateException.class);
		exceptionRule.expectMessage("Unexpected value: " + Command.WB_MODE);

		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(Command.EXPOSURE.getName() + HASH + Command.WB_MODE.getName());
		controllableProperty.setValue(null);
		averPTZCommunicator.controlProperty(controllableProperty);
	}
}
