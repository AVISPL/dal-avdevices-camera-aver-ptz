/*
 * Copyright (c) 2021 AVI-SPL Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.command;

/**
 * This class is used to define type of command
 *
 * @author Harry
 * @version 1.0
 * @since 1.0
 */
public enum CommandType {
	COMMAND((byte) 0x01),
	INQUIRY((byte) 0x09);

	private final byte code;

	CommandType(byte code) {
		this.code = code;
	}

	/**
	 * Retrieves {@code {@link #code}}
	 *
	 * @return value of {@link #code}
	 */
	public byte getCode() {
		return code;
	}
}
