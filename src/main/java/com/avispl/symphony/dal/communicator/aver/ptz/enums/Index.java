package com.avispl.symphony.dal.communicator.aver.ptz.enums;

public enum Index {
	ZERO("(0)"),
	ONE("(1)"),
	TWO("(2)"),
	THREE("(3)"),
	FOUR("(4)"),
	FIVE("(5)"),
	SIX("(6)"),
	SEVEN("(7)"),
	EIGHT("(8)"),
	NINE("(9)"),
	TEN("(10)");

	private final String name;

	Index(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}
}
