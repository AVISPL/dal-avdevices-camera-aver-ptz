/*
 * Copyright (c) 2021 AVI-SPL Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param;

/**
 * This class is used to define AEMode
 *
 * @author Harry
 * @version 1.0
 * @since 1.0
 */
public enum AEMode {
	FULL_AUTO("FullAuto", (byte) 0x00),
	MANUAL("Manual", (byte) 0x03),
	SHUTTER_PRIORITY("ShutterPriority", (byte) 0x0A),
	IRIS_PRIORITY("IrisPriority", (byte) 0x0B);

	private final String name;
	private final byte code;

	AEMode(String name, byte code) {
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
