/*
 * Copyright (c) 2021 AVI-SPL Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.aver.ptz;

import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.BYTE_SUFFIX;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.Prefix;

/**
 * Utils Class use for:
 * <li>Build a send string for command to be sent</li>
 * <li>Split 1 int number to byte number array (4 bytes)</li>
 * <li>Split 1 byte number to byte number array (2 bytes)</li>
 *
 * @author Harry
 * @version 1.0
 * @since 1.0
 */
public class AverPTZUtils {
	/**
	 * This method is used to build a command to be sent according to Aver Protocol
	 *
	 * @param cameraID This is int value representing the camera ID
	 * @param sequenceNumber This is the int value representing the sequence number of command to be sent
	 * @param payloadType This is the byte value representing the payload type code
	 * @param commandType This is  the byte value representing the command type code
	 * @param category This is  the byte value representing the category code
	 * @param command This is the byte array representing the command to be sent
	 * @param param This is the byte array representing the parameter values to be sent
	 * @return byte[] This returns the array to be sent to the display
	 */
	public static byte[] buildSendPacket(int cameraID, int sequenceNumber, byte payloadType, byte commandType, byte category, byte[] command, byte... param) {
		List<Byte> bytes = new ArrayList<>();

		// Add payload type
		bytes.add(Prefix.PAYLOAD_TYPE.getPrefixCode());
		bytes.add(payloadType);

		// Build payload string
		byte[] payload = buildPayload(cameraID, commandType, category, command, param);

		// Add payload length
		bytes.add((byte) 0x00);
		bytes.add((byte) (payload.length));

		// Add sequence number
		byte[] sequence = convertIntToByteArray(sequenceNumber);
		for (byte b : sequence) {
			bytes.add(b);
		}

		for (byte b : payload) {
			bytes.add(b);
		}

		byte[] byteArray = new byte[bytes.size()];
		for (int i = 0; i < bytes.size(); i++) {
			byteArray[i] = bytes.get(i);
		}

		return byteArray;
	}

	/**
	 * This method is used to build a payload string to be sent according to Aver Protocol
	 *
	 * @param cameraID This is int value representing the camera ID
	 * @param commandType This is the byte value representing command type code
	 * @param category This is the byte value representing the category code
	 * @param command This is the byte array representing the command to be sent
	 * @param param This is the byte array representing the parameter values to be sent
	 * @return byte[] This returns the payload packet to be sent to the display
	 */
	public static byte[] buildPayload(int cameraID, byte commandType, byte category, byte[] command, byte... param) {
		List<Byte> bytes = new ArrayList<>();

		bytes.add((byte) (Prefix.COMMAND.getPrefixCode() + cameraID));
		bytes.add(commandType);

		bytes.add(category);

		for (byte b : command) {
			bytes.add(b);
		}

		if (param != null) {
			for (byte b : param) {
				bytes.add(b);
			}
		}

		bytes.add(BYTE_SUFFIX);

		byte[] byteArray = new byte[bytes.size()];
		for (int i = 0; i < bytes.size(); i++) {
			byteArray[i] = bytes.get(i);
		}

		return byteArray;
	}


	/**
	 * This method is used to convert int number to byte array (4 bytes)
	 *
	 * @param number This is int value representing the number to be converted from int to byte array
	 * @return byte[] This returns the byte array
	 */
	public static byte[] convertIntToByteArray(int number) {
		return ByteBuffer.allocate(4).putInt(number).array();
	}

	/**
	 * This method is used to convert 1 byte number to array of 2 bytes
	 *
	 * @param number This is byte value representing the number to be converted from 1 byte to array of 2 bytes
	 * @return byte[] This returns the array of 2 bytes
	 */
	public static byte[] convertOneByteNumberToTwoBytesArray(byte number) {
		byte[] byteArray = new byte[2];
		byteArray[0] = (byte) (number / 16);
		byteArray[1] = (byte) (number % 16);
		return byteArray;
	}
}
