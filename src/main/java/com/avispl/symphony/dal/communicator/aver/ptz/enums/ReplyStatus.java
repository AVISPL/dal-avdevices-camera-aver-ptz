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
	;

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
