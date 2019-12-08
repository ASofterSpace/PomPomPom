/**
 * Unlicensed code created by A Softer Space, 2019
 * www.asofterspace.com/licenses/unlicense.txt
 */
package com.asofterspace.pomPomPom;

import com.asofterspace.toolbox.io.File;
import com.asofterspace.toolbox.io.XmlElement;
import com.asofterspace.toolbox.io.XmlFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PomFile extends XmlFile {

	private String groupId;
	private String artifactId;
	private String version;

	private String parentGroupId;
	private String parentArtifactId;
	private String parentVersion;

	private PomFile parent;

	private List<Dependency> dependencies;
	private List<Dependency> managedDependencies;
	private Map<String, String> properties;

	private boolean initialized = false;


	public PomFile(File regularFile) {
		super(regularFile);
	}

	/**
	 * Initializes all the given pom files together,
	 * establishing parent-child-links if possible
	 */
	public static void initAll(List<PomFile> pomFiles) {

		// first ensure that they all have been initialized
		for (PomFile pom : pomFiles) {
			pom.loadPomContents();
		}

		// now link them up
		for (PomFile pom : pomFiles) {
			if ((pom.parentGroupId == null) || (pom.parentArtifactId == null) || (pom.parentVersion == null)) {
				continue;
			}
			for (PomFile possibleParentPom : pomFiles) {
				if (pom.parentGroupId.equals(possibleParentPom.groupId) &&
					pom.parentArtifactId.equals(possibleParentPom.artifactId) &&
					pom.parentVersion.equals(possibleParentPom.version)) {
					pom.parent = possibleParentPom;
					break;
				}
			}
		}

		// now update what needs to be updated AFTER the link-up!
		for (PomFile pom : pomFiles) {
			pom.updateDependencyVersions();
		}
	}

	/**
	 * Initializes this one pom file
	 */
	public void loadPomContents() {

		initialized = true;

		XmlElement root = getRoot();

		XmlElement parentInfo = root.getChild("parent");
		XmlElement directGroupId = root.getChild("groupId");
		XmlElement directArtifactId = root.getChild("artifactId");
		XmlElement directVersion = root.getChild("version");

		if (parentInfo != null) {
			XmlElement curEl = parentInfo.getChild("groupId");
			if (curEl != null) {
				this.parentGroupId = curEl.getInnerText();
			}
			curEl = parentInfo.getChild("artifactId");
			if (curEl != null) {
				this.parentArtifactId = curEl.getInnerText();
			}
			curEl = parentInfo.getChild("version");
			if (curEl != null) {
				this.parentVersion = curEl.getInnerText();
			}
		}

		if (directGroupId != null) {
			this.groupId = directGroupId.getInnerText();
		} else {
			this.groupId = this.parentGroupId;
		}

		if (directArtifactId != null) {
			this.artifactId = directArtifactId.getInnerText();
		} else {
			this.artifactId = this.parentArtifactId;
		}

		if (directVersion != null) {
			this.version = directVersion.getInnerText();
		} else {
			this.version = this.parentVersion;
		}

		dependencies = new ArrayList<>();

		XmlElement deps = root.getChild("dependencies");
		if (deps != null) {
			List<XmlElement> depList = deps.getChildren("dependency");
			for (XmlElement depEl : depList) {
				Dependency dep = new Dependency(depEl);
				if (!dependencies.contains(dep)) {
					dependencies.add(dep);
				}
			}
		}

		XmlElement build = root.getChild("build");
		if (build != null) {
			XmlElement plugins = build.getChild("plugins");
			if (plugins != null) {
				List<XmlElement> pluginList = plugins.getChildren("plugin");
				for (XmlElement plugin : pluginList) {
					Dependency dep = new Dependency(plugin);
					if (!dependencies.contains(dep)) {
						dependencies.add(dep);
					}
				}
			}
		}

		managedDependencies = new ArrayList<>();

		// TODO - add managed dependencies

		properties = new HashMap<>();

		XmlElement propEl = root.getChild("properties");
		if (propEl != null) {
			List<XmlElement> children = propEl.getChildNodes();
			for (XmlElement child : children) {
				properties.put(child.getTagName(), child.getInnerText());
			}
		}

		updateDependencyVersions();
	}

	private void updateDependencyVersions() {

		for (Dependency dep : dependencies) {
			String ver = dep.getVersion();
			if (ver == null) {
				// TODO :: get from dependency management (potentially of parent)

			} else if (ver.startsWith("${") && ver.endsWith("}")) {
				String searchProp = ver.substring(2);
				searchProp = searchProp.substring(0, searchProp.length() - 1);
				PomFile curFile = this;
				while (curFile != null) {
					Map<String, String> curProps = curFile.getProperties();
					String newVersion = curProps.get(searchProp);
					if (newVersion != null) {
						dep.setVersion(newVersion);
						break;
					}
					curFile = curFile.getParent();
				}
			}
		}
	}

	public String getGroupId() {

		if (!initialized) {
			loadPomContents();
		}

		return groupId;
	}

	public String getArtifactId() {

		if (!initialized) {
			loadPomContents();
		}

		return artifactId;
	}

	public String getVersion() {

		if (!initialized) {
			loadPomContents();
		}

		return version;
	}

	public List<Dependency> getDependencies() {

		if (!initialized) {
			loadPomContents();
		}

		return dependencies;
	}

	public Map<String, String> getProperties() {

		if (!initialized) {
			loadPomContents();
		}

		return properties;
	}

	public PomFile getParent() {

		return parent;
	}

}
