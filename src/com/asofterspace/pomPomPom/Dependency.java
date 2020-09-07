/**
 * Unlicensed code created by A Softer Space, 2019
 * www.asofterspace.com/licenses/unlicense.txt
 */
package com.asofterspace.pomPomPom;

import com.asofterspace.toolbox.io.XmlElement;

import java.util.List;


public class Dependency {

	private PomFile pom;
	private String groupId;
	private String artifactId;
	private String version;
	private String scope;


	public Dependency(XmlElement el, PomFile pom, List<PomError> encounteredErrors) {

		this.pom = pom;

		XmlElement curEl = el.getChild("groupId");
		if (curEl != null) {
			this.groupId = curEl.getInnerText().trim();
		}

		curEl = el.getChild("artifactId");
		if (curEl != null) {
			this.artifactId = curEl.getInnerText().trim();
		}

		curEl = el.getChild("version");
		if (curEl != null) {
			this.version = curEl.getInnerText().trim();
		}

		curEl = el.getChild("scope");
		if (curEl != null) {
			this.scope = curEl.getInnerText().trim();
			if ("".equals(scope)) {
				encounteredErrors.add(new PomError(PomErrorKind.SCOPE_EMPTY, pom));
			}
		}
	}

	public void validate(List<PomError> encounteredErrors) {

		String curScope = this.scope;

		if (curScope == null) {
			curScope = pom.getScopeFromParentsForDependency(this);
		}

		if (!"test".equals(curScope)) {
			if (((groupId != null) && groupId.endsWith(".doubles")) || ((artifactId != null) && artifactId.contains(".doubles."))) {
				if (!(pom.getArtifactId().contains(".doubles.") || pom.getArtifactId().endsWith(".doubles") ||
					pom.getArtifactId().contains(".itest.") || pom.getArtifactId().endsWith(".itest") ||
					pom.getArtifactId().contains(".vtest.") || pom.getArtifactId().endsWith(".vtest") ||
					pom.getAbsoluteFilename().contains("/itest/") || pom.getAbsoluteFilename().contains("\\itest\\") ||
					pom.getAbsoluteFilename().contains("/vtest/") || pom.getAbsoluteFilename().contains("\\vtest\\"))) {
					encounteredErrors.add(new PomError(PomErrorKind.SCOPE_FOR_DOUBLE_NOT_TEST, pom));
				}
			}
		}
	}

	public String getGroupId() {
		String res = groupId;
		if ((res == null) || res.equals("")) {
			res = pom.getGroupId();
		}
		if (res == null) {
			return "";
		}
		return res;
	}

	public String getArtifactId() {
		if (artifactId == null) {
			return "";
		}
		return artifactId;
	}

	public String getVersion() {
		if (version == null) {
			return "";
		}
		return version;
	}

	public String getScope() {
		if (scope == null) {
			return "";
		}
		return scope;
	}

	public void setVersion(String newVersion) {
		this.version = newVersion;
	}

	public PomFile getPom() {
		return pom;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (other instanceof Dependency) {
			Dependency otherDep = (Dependency) other;
			return getGroupId().equals(otherDep.getGroupId()) && getArtifactId().equals(otherDep.getArtifactId());
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (groupId == null) {
			return 0;
		}
		return groupId.hashCode();
	}

	public int compareTo(Object other) {
		if (other == null) {
			return -1;
		}
		if (other instanceof Dependency) {
			Dependency otherDependency = (Dependency) other;
			// first compare group-ids (any case)
			int result = getGroupId().toLowerCase().compareTo(otherDependency.getGroupId().toLowerCase());
			if (result == 0) {
				// if they are the same, compare artifact ids (exact case)
				result = getGroupId().compareTo(otherDependency.getGroupId());
			}
			if (result == 0) {
				// if they are the same, compare artifact ids (any case)
				result = getArtifactId().toLowerCase().compareTo(otherDependency.getArtifactId().toLowerCase());
			}
			if (result == 0) {
				// if still the same, compare artifact ids (exact case)
				result = getArtifactId().compareTo(otherDependency.getArtifactId());
			}
			return result;
		}
		return -1;
	}

}
