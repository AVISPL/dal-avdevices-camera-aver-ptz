/*
 * Copyright (c) 2021 AVI-SPL Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.aver.ptz.enums.payload;

/**
 * This class is used to define payload category
 *
 * @author Harry
 * @version 1.0
 * @since 1.0
 */
public enum PayloadCategory {
	INTERFACE((byte) 0x00),
	CAMERA((byte) 0x04),
	PAN_TILTER((byte) 0x06);

	private final byte code;

	PayloadCategory(byte code) {
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
