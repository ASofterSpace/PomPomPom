/**
 * Unlicensed code created by A Softer Space, 2019
 * www.asofterspace.com/licenses/unlicense.txt
 */
package com.asofterspace.pomPomPom;

import com.asofterspace.toolbox.io.Directory;
import com.asofterspace.toolbox.io.File;
import com.asofterspace.toolbox.io.SimpleFile;
import com.asofterspace.toolbox.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class Main {

	public final static String PROGRAM_TITLE = "PomPomPom";
	public final static String VERSION_NUMBER = "0.0.0.1(" + Utils.TOOLBOX_VERSION_NUMBER + ")";
	public final static String VERSION_DATE = "8. December 2019";

	public static void main(String[] args) {

		// let the Utils know in what program it is being used
		Utils.setProgramTitle(PROGRAM_TITLE);
		Utils.setVersionNumber(VERSION_NUMBER);
		Utils.setVersionDate(VERSION_DATE);

		if (args.length > 0) {
			if (args[0].equals("--version")) {
				System.out.println(Utils.getFullProgramIdentifierWithDate());
				return;
			}

			if (args[0].equals("--version_for_zip")) {
				System.out.println("version " + Utils.getVersionNumber());
				return;
			}
		}

		if (args.length < 1) {
			System.out.println("You seem to have called PomPomPom without any arguments.");
			System.out.println("Please use the directory in which you want to start analyzing poms as first argument.");
			System.out.println("(You can also use several arguments - then the first is interpreted as directory, and every other as path to a pom which is added purely for referencing - so as a parent containing versions etc. - but which is not included in the dependency analysis!)");
			return;
		}

		Directory parentDir = new Directory(args[0]);

		boolean recursive = true;
		List<File> allFiles = parentDir.getAllFiles(recursive);

		List<PomFile> poms = new ArrayList<>();

		System.out.println("Found the following poms:");

		for (File file : allFiles) {
			if ("pom.xml".equals(file.getLocalFilename().toLowerCase())) {
				poms.add(new PomFile(file));
				System.out.println(file.getCanonicalFilename());
			}
		}

		List<PomFile> parentPoms = new ArrayList<>();

		for (int i = 1; i < args.length; i++) {
			parentPoms.add(new PomFile(args[i]));
		}

		List<PomFile> allPoms = new ArrayList<>();
		allPoms.addAll(poms);
		allPoms.addAll(parentPoms);

		PomFile.initAll(allPoms);

		List<Dependency> dependencies = new ArrayList<>();

		for (PomFile pom : poms) {
			List<Dependency> pomDeps = pom.getDependencies();
			for (Dependency pomDep : pomDeps) {
				if (!dependencies.contains(pomDep)) {
					dependencies.add(pomDep);
				}
			}
		}

		Collections.sort(dependencies, new Comparator<Dependency>() {
			public int compare(Dependency a, Dependency b) {
				return a.compareTo(b);
			}
		});

		StringBuilder result = new StringBuilder();

		for (Dependency dependency : dependencies) {
			result.append(dependency.getGroupId());
			result.append(" > ");
			result.append(dependency.getArtifactId());
			result.append(" | ");
			result.append(dependency.getVersion());
			result.append("\n");
		}

		SimpleFile outputFile = new SimpleFile("output.txt");
		System.out.println("");
		System.out.println("The output will now be written to " + outputFile.getCanonicalFilename());

		outputFile.setContent(result);
		outputFile.save();

		System.out.println("The output has been saved - have a nice day! :)");
	}

}
