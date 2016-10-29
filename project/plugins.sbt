resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.typesafeIvyRepo("releases"),
  "JGit Repository" at "http://download.eclipse.org/jgit/maven"
)

libraryDependencies += "com.puppycrawl.tools" % "checkstyle" % "5.5"

