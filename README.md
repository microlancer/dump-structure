# dump-structure

If you run `activator compile` you will get:

```
[info] Loading project definition from /Volumes/src/work/dump-structure/project
[info] Updating {file:/Volumes/src/work/dump-structure/project/}dump-structure-build...
[info] Resolving org.fusesource.jansi#jansi;1.4 ...
[info] Done updating.
[info] Compiling 1 Scala source to /Volumes/src/work/dump-structure/project/target/scala-2.10/sbt-0.13/classes...
[info] Set current project to hello (in build file:/Volumes/src/work/dump-structure/)
[success] Total time: 0 s, completed Oct 28, 2016 9:08:13 PM
```

So this project looks perfectly fine.

However, when you try to import the project into IntelliJ 2016.2.5 you will get the error:

```
[error] Reference to undefined setting:
[error]
[error]   */*:sbtStructureOutputFile from */*:dumpStructure ((org.jetbrains.sbt.CreateTasks) CreateTasks.scala:15)
[error]
[error] Use 'last' for the full log.
[error] Not a valid key: dump-structure (similar: build-structure, buildStructure)
[error] */*:dump-structure
[error]                   ^
```
