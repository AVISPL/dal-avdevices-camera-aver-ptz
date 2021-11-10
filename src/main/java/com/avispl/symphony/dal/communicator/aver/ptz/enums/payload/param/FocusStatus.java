/*
 * Copyright (c) 2021 AVI-SPL Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param;

/**
 * This class is used to define focus status
 *
 * @author Harry
 * @version 1.0
 * @since 1.0
 */
public enum FocusStatus {
	AUTO("Auto", (byte) 0x02),
	MANUAL("Manual", (byte) 0x03);

	private final String name;
	private final byte code;

	FocusStatus(String name, byte code) {
		this.name = name;
		this.code = code;
	}

	/**
	 * Retrieves {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
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
