/*
 * Copyright (c) 2021 AVI-SPL Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.aver.ptz;

import com.avispl.symphony.dal.communicator.RestCommunicator;

/**
 * This class is used to getting monitoring properties:
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
public class AverPTZRestCommunicator extends RestCommunicator {
	private String deviceMfg;
	private String deviceModel;
	private String deviceSerialNumber;
	private String deviceFirmwareVersion;

	/**
	 * Empty constructor
	 */
	public AverPTZRestCommunicator() {
		// Do nothing
	}

	/**
	 * This method is used to get instance of this class
	 */
	public static AverPTZRestCommunicator getInstance() {
		return SingletonHelper.INSTANCE;
	}

	/**
	 * This method is used to create new instance of this class
	 */
	private static class SingletonHelper {
		private static final AverPTZRestCommunicator INSTANCE = new AverPTZRestCommunicator();
	}

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

	/**
	 * This is a method used to authenticate
	 */
	@Override
	protected void authenticate() {
		// Do nothing
	}

	/**
	 * This method used to get data from device
	 */
	public void getData() throws Exception {
		String url = "http://" + this.host + "//storks/?cmd=get_sys_stat";
		String response = doGet(url);
		digestResponse(response);
	}

	/**
	 * This method used to digest a response from device
	 *
	 * @param responseData This is a response data
	 */
	private void digestResponse(String responseData) {
		String[] responses = responseData.split(";");
		String[] values;
		for (String response : responses) {
			values = response.split("=");
			if (response.startsWith("mac")) {
				if (values[1].startsWith("00:18:1A")) {
					setDeviceMfg("AVer Information Co.");
				}
			} else if (response.startsWith("fw_ver")) {
				setDeviceFirmwareVersion(values[1]);
			} else if (response.startsWith("model_name")) {
				setDeviceModel(values[1]);
			} else if (response.startsWith("sn")) {
				setDeviceSerialNumber(values[1]);
			}
		}
	}

}
