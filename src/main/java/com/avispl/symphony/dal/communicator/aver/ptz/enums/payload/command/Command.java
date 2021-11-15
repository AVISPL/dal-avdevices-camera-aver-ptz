/*
 * Copyright (c) 2021 AVI-SPL Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.command;

import java.util.Arrays;
import java.util.Optional;

/**
 * This class is used to define command used to build payload
 *
 * @author Harry
 * @version 1.0
 * @since 1.0
 */
public enum Command {
	POWER("Power", new byte[] { 0x00 }),
	ZOOM("ZoomControl", new byte[] { 0x07 }),
	FOCUS("FocusControl", new byte[] { 0x08 }),
	FOCUS_MODE("FocusMode", new byte[] { 0x38 }),
	FOCUS_ONE_PUSH("OnePush", new byte[] { 0x18, 0x01 }),
	IMAGE_PROCESS("ImageProcessControl", null),
	WB_MODE("WBMode", new byte[] { 0x35 }),
	WB_ONE_PUSH_TRIGGER("OnePushTrigger", new byte[] { 0x10, 0x05 }),
	RGAIN("RGain", new byte[] { 0x03 }),
	RGAIN_INQ("RGainCurrent", new byte[] { 0x43 }),
	BGAIN("BGain", new byte[] { 0x04 }),
	BGAIN_INQ("BGainCurrent", new byte[] { 0x44 }),
	AE_MODE("AEMode", new byte[] { 0x39 }),
	EXPOSURE("ExposureControl", null),
	AUTO_SLOW_SHUTTER("AutoSlowShutter", new byte[] { 0x5A }),
	SHUTTER_DIRECT("ShutterSpeed", new byte[] { 0x4A, 0x00, 0x00 }),
	SHUTTER_CURRENT("ShutterSpeedCurrent", null),
	IRIS_DIRECT("IrisLevel", new byte[] { 0x4B, 0x00, 0x00 }),
	IRIS_CURRENT("IrisLevelCurrent", null),
	GAIN_DIRECT("GainLevel", new byte[] { 0x4C, 0x00, 0x00 }),
	GAIN_CURRENT("GainLevelCurrent", null),
	GAIN_LIMIT_DIRECT("GainLimitLevel", new byte[] { 0x2C }),
	GAIN_LIMIT_CURRENT("GainLimitLevelCurrent", null),
	EXP_COMP_DIRECT("ExposureValue", new byte[] { 0x4E, 0x00, 0x00 }),
	EXP_COMP_CURRENT("ExposureValueCurrent", null),
	BACKLIGHT("Backlight", new byte[] { 0x33 }),
	PRESET("PresetControl", new byte[] { 0x3F }),
	PAN_TILT_DRIVE("PanTiltControl", new byte[] { 0x01 }),
	PAN_TILT_HOME("Home", new byte[] { 0x04 }),
	SLOW_PAN_TILT("PanTiltSlowMode", new byte[] { 0x044 });

	private final String name;
	private final byte[] code;

	Command(String name, byte[] code) {
		this.name = name;
		this.code = code;
	}

	/**
	 * Retrieves {@code {@link #name}}
	 *
	 * @return value of {@link #code}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves {@code {@link #code}}
	 *
	 * @return value of {@link #code}
	 */
	public byte[] getCode() {
		return code;
	}

	/**
	 * This method is used to get Command by name
	 *
	 * @param name is the name of command that want to get
	 * @return Command is the command that want to get
	 */
	public static Command getByName(String name) {
		Optional<Command> command = Arrays.stream(Command.values()).filter(com -> com.getName().equals(name)).findFirst();
		return command.orElse(null);
	}
}
