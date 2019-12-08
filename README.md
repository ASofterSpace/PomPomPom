# PomPomPom

**Class:** A Softer Space Internal

**Language:** Java

**Platform:** Windows / Linux

This aggregates pom files and lists all their dependencies.

## Setup

Download our Toolbox-Java (which is a separate project here on github) into an adjacent directory on your hard drive.

Start the build by calling under Windows:

```
build.bat
```

Or under Linux:

```
build.sh
```

## Run

To start up the PomPomPom project after it has been built, you can call under Windows:

```
run.bat folder [pom] [pom] [pom]
```

Or under Linux:

```
run.sh folder [pom] [pom] [pom]
```

In each case, you can give several arguments.

The first argument is mandatory and should point to the directory of the project whose pom files you want to analyze.

Each other argument is the path to a particular parent pom file which contains additional version numbers that should be interwoven with the rest of the output, but whose dependencies will not be reported in the output (as they are not part of the sub-project that you are looking at.)

## License

We at A Softer Space really love the Unlicense, which pretty much allows anyone to do anything with this source code.
For more info, see the file UNLICENSE.

If you desperately need to use this source code under a different license, [contact us](mailto:moya@asofterspace.com) - I am sure we can figure something out.
