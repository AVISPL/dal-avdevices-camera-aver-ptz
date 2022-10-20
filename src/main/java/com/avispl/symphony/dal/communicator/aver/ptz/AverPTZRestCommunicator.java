/*
 * Copyright (c) 2021 AVI-SPL Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.aver.ptz;

import com.avispl.symphony.dal.communicator.RestCommunicator;
import com.avispl.symphony.dal.communicator.aver.ptz.dto.DeviceInfo;

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
	/**
	 * {@inheritdoc}
	 * This is a method used to authenticate
	 */
	@Override
	protected void authenticate() {
		// Do nothing
	}

	/**
	 * This method used to get data from device
	 */
	public DeviceInfo getDeviceInfo() throws Exception {
		String response = doGet(AverPTZConstants.GET_DEVICE_INFO_URL);
		return digestResponse(response);
	}

	/**
	 * This method used to digest a response from device
	 *
	 * @param responseData This is a response data
	 */
	private DeviceInfo digestResponse(String responseData) {
		DeviceInfo deviceInfo = new DeviceInfo();
		String[] responses = responseData.split(AverPTZConstants.SEMICOLON);
		String[] values;

		for (String response : responses) {
			values = response.split(AverPTZConstants.EQUAL);
			if (response.startsWith(AverPTZConstants.MAC_LABEL)) {
				if (values[1].startsWith(AverPTZConstants.MAC_PREFIX)) {
					deviceInfo.setDeviceMfg(AverPTZConstants.DEVICE_MFG);
				}
			} else if (response.startsWith(AverPTZConstants.DEVICE_FIRMWARE_VERSION_LABEL)) {
				deviceInfo.setDeviceFirmwareVersion(values[1]);
			} else if (response.startsWith(AverPTZConstants.DEVICE_MODEL_LABEL)) {
				deviceInfo.setDeviceModel(values[1]);
			} else if (response.startsWith(AverPTZConstants.DEVICE_SERIAL_NUMBER_VERSION_LABEL)) {
				deviceInfo.setDeviceSerialNumber(values[1]);
			}
		}

		return deviceInfo;
	}
}
