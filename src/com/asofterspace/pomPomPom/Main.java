/**
 * Unlicensed code created by A Softer Space, 2019
 * www.asofterspace.com/licenses/unlicense.txt
 */
package com.asofterspace.pomPomPom;

import com.asofterspace.toolbox.io.Directory;
import com.asofterspace.toolbox.io.File;
import com.asofterspace.toolbox.Utils;

import java.util.ArrayList;
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
			System.out.println("Please use the directory in which you want to start analyzing poms as argument.");
			return;
		}

		Directory parentDir = new Directory(args[0]);

		boolean recursive = true;
		List<File> allFiles = parentDir.getAllFiles(recursive);

		List<File> poms = new ArrayList<>();

		System.out.println("Found the following poms:");

		for (File file : allFiles) {
			if ("pom.xml".equals(file.getLocalFilename().toLowerCase())) {
				poms.add(file);
				System.out.println(file.getCanonicalFilename());
			}
		}
	}

}
