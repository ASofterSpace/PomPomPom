/**
 * Unlicensed code created by A Softer Space, 2019
 * www.asofterspace.com/licenses/unlicense.txt
 */
package com.asofterspace.pomPomPom;

import com.asofterspace.toolbox.io.XmlElement;


public class Dependency {

	private String groupId;
	private String artifactId;
	private String version;


	public Dependency(XmlElement el) {

		XmlElement curEl = el.getChild("groupId");
		if (curEl != null) {
			this.groupId = curEl.getInnerText();
		}

		curEl = el.getChild("artifactId");
		if (curEl != null) {
			this.artifactId = curEl.getInnerText();
		}

		curEl = el.getChild("version");
		if (curEl != null) {
			this.version = curEl.getInnerText();
		}
	}

	public String getGroupId() {
		return groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String newVersion) {
		this.version = newVersion;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (other instanceof Dependency) {
			Dependency otherDep = (Dependency) other;
			return (groupId.equals(otherDep.getGroupId()) && artifactId.equals(otherDep.getArtifactId()));
		}
		return false;
	}

	@Override
	public int hashCode() {
		return groupId.hashCode();
	}

	public int compareTo(Object other) {
		if (other == null) {
			return -1;
		}
		if (other instanceof Dependency) {
			Dependency otherDependency = (Dependency) other;
			if (groupId.equals(otherDependency.getGroupId())) {
				return artifactId.toLowerCase().compareTo(otherDependency.getArtifactId().toLowerCase());
			}
			return groupId.toLowerCase().compareTo(otherDependency.getGroupId().toLowerCase());
		}
		return -1;
	}

}
