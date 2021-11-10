/*
 * Copyright (c) 2021 AVI-SPL Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param;

/**
 * This class is used to define pan tilt drive
 *
 * @author Harry
 * @version 1.0
 * @since 1.0
 */
public enum PanTiltDrive {
	UP("Up", new byte[] { 0x03, 0x01 }),
	DOWN("Down", new byte[] { 0x03, 0x02 }),
	LEFT("Left", new byte[] { 0x01, 0x03 }),
	RIGHT("Right", new byte[] { 0x02, 0x03 }),
	UP_LEFT("UpLeft", new byte[] { 0x01, 0x01 }),
	UP_RIGHT("UpRight", new byte[] { 0x02, 0x01 }),
	DOWN_LEFT("DownLeft", new byte[] { 0x01, 0x02 }),
	DOWN_RIGHT("DownRight", new byte[] { 0x02, 0x02 });

	private final String name;
	private final byte[] code;

	PanTiltDrive(String name, byte[] code) {
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
	public byte[] getCode() {
		return code;
	}
}
