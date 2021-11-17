/*
 * Copyright (c) 2021 AVI-SPL Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.aver.ptz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class is used for Aver PTZ to save all Constants
 *
 * @author Harry
 * @version 1.0
 * @since 1.0
 */
public class AverPTZConstants {
	public static final byte BYTE_SUFFIX = (byte) 0xFF;
	public static final char HASH = '#';
	public static final String SEMICOLON = ";";
	public static final String EQUAL = "=";
	public static final String HTTP_PREFIX = "http://";
	public static final String GET_DEVICE_INFO_URL = "//storks/?cmd=get_sys_stat";
	public static final String MAC_PREFIX = "00:18:1A";
	public static final String DEVICE_MFG = "AVer Information Co.";
	public static final String MAC_LABEL = "mac";
	public static final String DEVICE_FIRMWARE_VERSION_LABEL = "fw_ver";
	public static final String DEVICE_MODEL_LABEL = "model_name";
	public static final String DEVICE_SERIAL_NUMBER_VERSION_LABEL = "sn";
	public static final String LABEL_START_EXPOSURE_VALUE = "-4";
	public static final String LABEL_END_EXPOSURE_VALUE = "4";
	public static final float RANGE_START_EXPOSURE_VALUE = 1F;
	public static final float RANGE_END_EXPOSURE_VALUE = 9F;
	public static final String LABEL_START_GAIN_LIMIT_LEVEL = "24";
	public static final String LABEL_END_GAIN_LIMIT_LEVEL = "48";
	public static final float RANGE_START_GAIN_LIMIT_LEVEL = 0F;
	public static final float RANGE_END_GAIN_LIMIT_LEVEL = 8F;
	public static final String LABEL_START_SHUTTER_SPEED = "1/32K";
	public static final String LABEL_END_SHUTTER_SPEED = "1/1";
	public static final float RANGE_START_SHUTTER_SPEED = 0F;
	public static final float RANGE_END_SHUTTER_SPEED = 15F;
	public static final String LABEL_START_IRIS_LEVEL = "0";
	public static final String LABEL_END_IRIS_LEVEL = "F1.6";
	public static final float RANGE_START_IRIS_LEVEL = 0F;
	public static final float RANGE_END_IRIS_LEVEL = 13F;
	public static final String LABEL_START_GAIN_LEVEL = "0";
	public static final String LABEL_END_GAIN_LEVEL = "48";
	public static final float RANGE_START_GAIN_LEVEL = 0F;
	public static final float RANGE_END_GAIN_LEVEL = 48F;
	public static final List<String> SHUTTER_VALUES = new ArrayList<>(
			Arrays.asList("1/32K", "1/16K", "1/8K", "1/4K", "1/2K", "1/1K", "1/480", "1/240", "1/120", "1/60", "1/30", "1/20", "1/10", "1/5", "1/2", "1/1"));
	public static final List<String> IRIS_LEVELS = new ArrayList<>(
			Arrays.asList("0", "F14", "F11", "F8.0", "F6.8", "F5.6", "F4.8", "F4.0", "F3.4", "F2.8", "F2.4", "F2.0", "F1.8", "F1.6"));
	public static final String SWITCH_STATUS_ON = "1";
	public static final String SWITCH_STATUS_OFF = "0";
	public static final long PAN_TILT_PERIOD = 3000;
	public static final String PLUS = "+";
	public static final String MINUS = "-";
	public static final String NONE_VALUE = "None";
}
