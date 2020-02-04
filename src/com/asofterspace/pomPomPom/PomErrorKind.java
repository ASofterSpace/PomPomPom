/**
 * Unlicensed code created by A Softer Space, 2020
 * www.asofterspace.com/licenses/unlicense.txt
 */
package com.asofterspace.pomPomPom;


public enum PomErrorKind {

	FILE_NOT_FOUND("This file could not be found"),
	FILE_EMPTY("This file is empty"),
	XML_INVALID("The XML in this file is invalid"),
	PARENT_MISSING("The parent entry is missing"),
	PARENT_ARTIFACT_ID_MISSING("The parent's artifactId is missing"),
	PARENT_GROUP_ID_MISSING("The parent's groupId is missing"),
	PARENT_VERSION_MISSING("The parent's version is missing"),
	SCOPE_EMPTY("A <scope> is empty"),
	SCOPE_FOR_DOUBLE_NOT_TEST("A <scope> for a double is not set to test");


	private String description;

	PomErrorKind(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
