/*
 * Copyright (c) 2021 AVI-SPL Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.aver.ptz.enums;

/**
 * This class is used to define name of statistics properties
 *
 * @author Harry
 * @version 1.0
 * @since 1.0
 */
public enum StatisticsProperty {
	DEVICE_INFORMATION("DeviceInformation"),
	DEVICE_MFG("DeviceMFG"),
	DEVICE_MODEL("DeviceModel"),
	DEVICE_SERIAL_NUMBER("DeviceSerialNumber"),
	DEVICE_FIRMWARE_VERSION("DeviceFirmwareVersion");

	private final String name;

	StatisticsProperty(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}
}
