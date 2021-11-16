/*
 * Copyright (c) 2021 AVI-SPL Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.aver.ptz;

import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.DEVICE_FIRMWARE_VERSION_LABEL;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.DEVICE_MFG;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.DEVICE_MODEL_LABEL;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.DEVICE_SERIAL_NUMBER_VERSION_LABEL;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.EQUAL;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.GET_DEVICE_INFO_URL;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.HTTP_PREFIX;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.MAC_LABEL;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.MAC_PREFIX;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.SEMICOLON;

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
		String url = HTTP_PREFIX + this.host + GET_DEVICE_INFO_URL;
		String response = doGet(url);
		return digestResponse(response);
	}

	/**
	 * This method used to digest a response from device
	 *
	 * @param responseData This is a response data
	 */
	private DeviceInfo digestResponse(String responseData) {
		DeviceInfo deviceInfo = new DeviceInfo();
		String[] responses = responseData.split(SEMICOLON);
		String[] values;

		for (String response : responses) {
			values = response.split(EQUAL);
			if (response.startsWith(MAC_LABEL)) {
				if (values[1].startsWith(MAC_PREFIX)) {
					deviceInfo.setDeviceMfg(DEVICE_MFG);
				}
			} else if (response.startsWith(DEVICE_FIRMWARE_VERSION_LABEL)) {
				deviceInfo.setDeviceFirmwareVersion(values[1]);
			} else if (response.startsWith(DEVICE_MODEL_LABEL)) {
				deviceInfo.setDeviceModel(values[1]);
			} else if (response.startsWith(DEVICE_SERIAL_NUMBER_VERSION_LABEL)) {
				deviceInfo.setDeviceSerialNumber(values[1]);
			}
		}

		return deviceInfo;
	}
}
