/**
 * Unlicensed code created by A Softer Space, 2020
 * www.asofterspace.com/licenses/unlicense.txt
 */
package com.asofterspace.pomPomPom;


public class PomError {

	private PomErrorKind kind;

	private PomFile file;


	public PomError(PomErrorKind kind, PomFile file) {

		this.kind = kind;

		this.file = file;
	}

	public PomErrorKind getKind() {
		return kind;
	}

	public String getFilename() {
		return file.getAbsoluteFilename();
	}

	@Override
	public boolean equals(Object other) {

		if (other == null) {
			return false;
		}

		if (other instanceof PomError) {
			PomError otherError = (PomError) other;
			return kind.equals(otherError.kind) && file.equals(otherError.file);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return kind.hashCode() + file.hashCode();
	}
}
