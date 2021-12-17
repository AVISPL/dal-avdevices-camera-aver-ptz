/*
 * Copyright (c) 2021 AVI-SPL Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.aver.ptz.dto;

/**
 * This class is used to store device information:
 *
 * - Device Mfg
 * - Device Model
 * - Serial Number
 * - Firmware Version
 *
 * @author Harry
 * @version 1.0
 * @since 1.0
 */
public class DeviceInfo {
	private String deviceMfg;
	private String deviceModel;
	private String deviceSerialNumber;
	private String deviceFirmwareVersion;

	/**
	 * Retrieves {@code {@link #deviceMfg}}
	 *
	 * @return value of {@link #deviceMfg}
	 */
	public String getDeviceMfg() {
		return deviceMfg;
	}

	/**
	 * Sets {@code deviceMfg}
	 *
	 * @param deviceMfg the {@code java.lang.String} field
	 */
	public void setDeviceMfg(String deviceMfg) {
		this.deviceMfg = deviceMfg;
	}

	/**
	 * Retrieves {@code {@link #deviceModel}}
	 *
	 * @return value of {@link #deviceModel}
	 */
	public String getDeviceModel() {
		return deviceModel;
	}

	/**
	 * Sets {@code deviceModel}
	 *
	 * @param deviceModel the {@code java.lang.String} field
	 */
	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

	/**
	 * Retrieves {@code {@link #deviceSerialNumber}}
	 *
	 * @return value of {@link #deviceSerialNumber}
	 */
	public String getDeviceSerialNumber() {
		return deviceSerialNumber;
	}

	/**
	 * Sets {@code deviceSerialNumber}
	 *
	 * @param deviceSerialNumber the {@code java.lang.String} field
	 */
	public void setDeviceSerialNumber(String deviceSerialNumber) {
		this.deviceSerialNumber = deviceSerialNumber;
	}

	/**
	 * Retrieves {@code {@link #deviceFirmwareVersion}}
	 *
	 * @return value of {@link #deviceFirmwareVersion}
	 */
	public String getDeviceFirmwareVersion() {
		return deviceFirmwareVersion;
	}

	/**
	 * Sets {@code deviceFirmwareVersion}
	 *
	 * @param deviceFirmwareVersion the {@code java.lang.String} field
	 */
	public void setDeviceFirmwareVersion(String deviceFirmwareVersion) {
		this.deviceFirmwareVersion = deviceFirmwareVersion;
	}
}
