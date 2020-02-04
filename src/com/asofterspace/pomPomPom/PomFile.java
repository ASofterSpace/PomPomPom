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


	/**
	 * You can construct a PomFile instance directly from a path name.
	 */
	public PomFile(String fullyQualifiedFileName) {

		super(fullyQualifiedFileName);
	}

	/**
	 * You can construct a PomFile instance by basing it on an existing file object.
	 */
	public PomFile(File regularFile) {
		super(regularFile);
	}

	/**
	 * Initializes all the given pom files together,
	 * establishing parent-child-links if possible
	 */
	public static void initAll(List<PomFile> pomFiles, List<PomError> encounteredErrors) {

		// first ensure that they all have been initialized
		for (PomFile pom : pomFiles) {
			try {
				pom.loadPomContents(encounteredErrors);
			} catch (Exception e) {
				encounteredErrors.add(new PomError(PomErrorKind.XML_INVALID, pom));
			}
		}

		// now link them up
		for (PomFile pom : pomFiles) {
			if ((pom.parentGroupId == null) || (pom.parentArtifactId == null) || (pom.parentVersion == null)) {
				if ((pom.parentGroupId == null) || (pom.parentArtifactId == null) || (pom.parentVersion == null)) {
					encounteredErrors.add(new PomError(PomErrorKind.PARENT_MISSING, pom));
					continue;
				}
				if (pom.parentGroupId == null) {
					encounteredErrors.add(new PomError(PomErrorKind.PARENT_GROUP_ID_MISSING, pom));
				}
				if (pom.parentArtifactId == null) {
					encounteredErrors.add(new PomError(PomErrorKind.PARENT_ARTIFACT_ID_MISSING, pom));
				}
				if (pom.parentVersion == null) {
					encounteredErrors.add(new PomError(PomErrorKind.PARENT_VERSION_MISSING, pom));
				}
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
			try {
				pom.updateDependencyVersions();
			} catch (Exception e) {
				encounteredErrors.add(new PomError(PomErrorKind.XML_INVALID, pom));
			}
		}

		// and finally validate the result!
		for (PomFile pom : pomFiles) {
			try {
				pom.validateDependencies(encounteredErrors);
			} catch (Exception e) {
				encounteredErrors.add(new PomError(PomErrorKind.XML_INVALID, pom));
			}
		}
	}

	/**
	 * Initializes this one pom file
	 */
	public void loadPomContents(List<PomError> encounteredErrors) {

		initialized = true;

		if (!exists()) {
			encounteredErrors.add(new PomError(PomErrorKind.FILE_NOT_FOUND, this));
			return;
		}

		XmlElement root = getRoot();

		if (root == null) {
			encounteredErrors.add(new PomError(PomErrorKind.XML_INVALID, this));
			return;
		}

		XmlElement parentInfo = root.getChild("parent");
		XmlElement directGroupId = root.getChild("groupId");
		XmlElement directArtifactId = root.getChild("artifactId");
		XmlElement directVersion = root.getChild("version");

		if (parentInfo != null) {
			XmlElement curEl = parentInfo.getChild("groupId");
			if (curEl != null) {
				this.parentGroupId = curEl.getInnerText().trim();
			}
			curEl = parentInfo.getChild("artifactId");
			if (curEl != null) {
				this.parentArtifactId = curEl.getInnerText().trim();
			}
			curEl = parentInfo.getChild("version");
			if (curEl != null) {
				this.parentVersion = curEl.getInnerText().trim();
			}
		}

		if (directGroupId != null) {
			this.groupId = directGroupId.getInnerText().trim();
		} else {
			this.groupId = this.parentGroupId;
		}

		if (directArtifactId != null) {
			this.artifactId = directArtifactId.getInnerText().trim();
		} else {
			this.artifactId = this.parentArtifactId;
		}

		if (directVersion != null) {
			this.version = directVersion.getInnerText().trim();
		} else {
			this.version = this.parentVersion;
		}

		dependencies = new ArrayList<>();

		XmlElement deps = root.getChild("dependencies");
		if (deps != null) {
			List<XmlElement> depList = deps.getChildren("dependency");
			for (XmlElement depEl : depList) {
				Dependency dep = new Dependency(depEl, this, encounteredErrors);
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
					Dependency dep = new Dependency(plugin, this, encounteredErrors);
					if (!dependencies.contains(dep)) {
						dependencies.add(dep);
					}
				}
			}
		}

		managedDependencies = new ArrayList<>();

		XmlElement manDeps = root.getChild("dependencyManagement");
		if (manDeps != null) {
			XmlElement curDeps = manDeps.getChild("dependencies");
			if (curDeps != null) {
				List<XmlElement> depList = curDeps.getChildren("dependency");
				for (XmlElement depEl : depList) {
					Dependency dep = new Dependency(depEl, this, encounteredErrors);
					if (!managedDependencies.contains(dep)) {
						managedDependencies.add(dep);
					}
				}
			}
		}

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
				// get from dependency management (potentially of parent)
				PomFile curFile = this;
				while ((ver == null) && (curFile != null)) {
					List<Dependency> manDeps = curFile.getManagedDependencies();
					for (Dependency manDep : manDeps) {
						if (dep.equals(manDep)) {
							ver = manDep.getVersion();
							dep.setVersion(ver);
						}
					}
					curFile = curFile.getParent();
				}
			}

			if (ver == null) {
				continue;
			}

			if (ver.startsWith("${") && ver.endsWith("}")) {
				String searchProp = ver.substring(2);
				searchProp = searchProp.substring(0, searchProp.length() - 1);
				PomFile curFile = this;
				while (curFile != null) {
					Map<String, String> curProps = curFile.getProperties();
					String newVersion = curProps.get(searchProp);
					while ((newVersion != null) && (newVersion.startsWith("${") && newVersion.endsWith("}"))) {
						searchProp = newVersion.substring(2);
						searchProp = searchProp.substring(0, searchProp.length() - 1);
						newVersion = curProps.get(searchProp);
					}
					if (newVersion != null) {
						dep.setVersion(newVersion);
						break;
					}
					curFile = curFile.getParent();
				}
			}
		}
	}

	private void validateDependencies(List<PomError> encounteredErrors) {

		// do not validate the managed ones, as they do not need e.g. a scope
		for (Dependency dep : dependencies) {
			dep.validate(encounteredErrors);
		}
	}

	String getScopeFromParentsForDependency(Dependency searchingForDep) {
		for (Dependency dep : managedDependencies) {
			if (searchingForDep.equals(dep)) {
				if ((dep.getScope() != null) && (!"".equals(dep.getScope()))) {
					return dep.getScope();
				}
			}
		}

		if (parent != null) {
			return parent.getScopeFromParentsForDependency(searchingForDep);
		}

		return null;
	}

	private void ensureLoaded() {

		if (!initialized) {
			List<PomError> encounteredErrors = new ArrayList<>();
			loadPomContents(encounteredErrors);
		}
	}

	public String getGroupId() {

		ensureLoaded();

		return groupId;
	}

	public String getArtifactId() {

		ensureLoaded();

		return artifactId;
	}

	public String getVersion() {

		ensureLoaded();

		return version;
	}

	public List<Dependency> getDependencies() {

		ensureLoaded();

		return dependencies;
	}

	public List<Dependency> getManagedDependencies() {

		ensureLoaded();

		return managedDependencies;
	}

	public Map<String, String> getProperties() {

		ensureLoaded();

		return properties;
	}

	public PomFile getParent() {

		return parent;
	}

	@Override
	public boolean equals(Object other) {

		if (other == null) {
			return false;
		}

		if (other instanceof PomFile) {
			PomFile otherPomFile = (PomFile) other;
			return getCanonicalFilename().equals(otherPomFile.getCanonicalFilename());
		}

		return false;
	}

	@Override
	public int hashCode() {
		return getCanonicalFilename().hashCode();
	}
}
