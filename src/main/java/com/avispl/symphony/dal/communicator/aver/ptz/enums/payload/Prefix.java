/*
 * Copyright (c) 2021 AVI-SPL Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.aver.ptz.enums.payload;

/**
 * This class is used to define prefix
 *
 * @author Harry
 * @version 1.0
 * @since 1.0
 */
public enum Prefix {
	PAYLOAD_TYPE((byte) 0x01),
	COMMAND((byte) 0x80);

	private final byte code;

	Prefix(byte code) {
		this.code = code;
	}

	/**
	 * Retrieves {@code {@link #code}}
	 *
	 * @return value of {@link #code}
	 */
	public byte getPrefixCode() {
		return code;
	}
}
