/*
 * Copyright (c) 2021 AVI-SPL Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.aver.ptz.enums;

/**
 * This class is used to define reply status code
 *
 * @author Harry
 * @version 1.0
 * @since 1.0
 */
public enum ReplyStatus {
	COMPLETION(new byte[] { (byte) 0x90, 0x51, (byte) 0xFF }),
	ACK(new byte[] { (byte) 0x90, 0x41, (byte) 0xFF }),
	SYNTAX_ERROR_CONTROL(new byte[] { (byte) 0x90, 0x61, 0x02, (byte) 0xFF }),
	SYNTAX_ERROR_INQUIRY(new byte[] { (byte) 0x90, 0x60, 0x02, (byte) 0xFF }),
	COMMAND_BUFFER_FULL_CONTROL(new byte[] { (byte) 0x90, 0x61, 0x03, (byte) 0xFF }),
	COMMAND_BUFFER_FULL_INQUIRY(new byte[] { (byte) 0x90, 0x60, 0x03, (byte) 0xFF }),
	NO_SOCKET_CONTROL(new byte[] { (byte) 0x90, 0x61, 0x05, (byte) 0xFF }),
	NO_SOCKET_INQUIRY(new byte[] { (byte) 0x90, 0x60, 0x05, (byte) 0xFF }),
	COMMAND_NOT_EXECUTABLE_CONTROL(new byte[] { (byte) 0x90, 0x61, 0x41, (byte) 0xFF }),
	COMMAND_NOT_EXECUTABLE_INQUIRY(new byte[] { (byte) 0x90, 0x60, 0x41, (byte) 0xFF });

	private final byte[] code;

	ReplyStatus(byte[] code) {
		this.code = code;
	}

	/**
	 * Retrieves {@code {@link #code}}
	 *
	 * @return value of {@link #code}
	 */
	public byte[] getCode() {
		return code;
	}
}
