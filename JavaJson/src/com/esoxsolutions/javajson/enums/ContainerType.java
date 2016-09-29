package com.esoxsolutions.javajson.enums;

public enum ContainerType {

	NONE, SET;

	public String toString() {
		switch (this) {
		case SET:
			return "@set";
		case NONE:
			return "";
		default:
			return "";
		}
	}
}
