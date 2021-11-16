/*
 * Copyright (c) 2021 AVI-SPL Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param;

import java.util.Arrays;
import java.util.Optional;

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

	/**
	 * This method is used to get AE Mode by name
	 *
	 * @param name is the name of ae mode that want to get
	 * @return AEMode is the wb mode that want to get
	 */
	public static AEMode getByName(String name) {
		Optional<AEMode> mode = Arrays.stream(AEMode.values()).filter(com -> com.getName().equals(name)).findFirst();
		return mode.orElse(null);
	}
}
