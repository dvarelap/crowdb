lazy val commonSettings: Seq[Def.Setting[_]] = Seq(
  scalaVersion  := "2.11.7",
  version       := "0.2.0-SNAPSHOT",
  organization  := "com.github.dvarelap",
  publishArtifact in (Compile, packageDoc) := false, // disable publishing the main API jar
  publishArtifact in (Compile, packageSrc) := false // disable publishing the main sources jar
)

lazy val testLibs = Seq(
	// Test libs
  "junit" % "junit" % "4.12",
  "org.specs2" %% "specs2" % "3.3.1",
  "org.specs2" %% "specs2-mock" % "3.6.1",
	"org.specs2" %% "specs2-junit" % "3.6.1"
)

lazy val commonLibs = Seq(
	"com.github.mauricio" %% "postgresql-async" % "0.2.16",
	"com.twitter" %% "util-core" % "6.24.0",
  "com.twitter" %% "bijection-util" % "0.8.1"

)

lazy val macroLibs = Seq(
	"org.scala-lang" % "scala-reflect" % "2.11.7",
	"com.github.mauricio" %% "postgresql-async" % "0.2.16"
)

lazy val crowdb = (project in file("."))
  .settings(commonSettings: _*)
	.settings(
    name                         := "crowdb",
		scalaSource in Compile       := baseDirectory.value / "app",
		scalaSource in Test          := baseDirectory.value / "test",
		resourceDirectory in Compile := baseDirectory.value / "config",
		resourceDirectory in Test    := baseDirectory.value / "test-config"
	)
	.settings(libraryDependencies ++= commonLibs)
	.settings(libraryDependencies ++= testLibs)
	.dependsOn(crowdbMacro, crowdbCommon)
  .aggregate(crowdbMacro, crowdbCommon)

lazy val crowdbMacro = (project in file("crowdb-macro"))
  .settings(commonSettings: _*)
	.settings(name := "crowdb-macro")
	.settings(libraryDependencies ++= macroLibs)
  .dependsOn(crowdbCommon)

lazy val crowdbCommon = (project in file("crowdb-common"))
  .settings(commonSettings: _*)
	.settings(name := "crowdb-common")
  .settings(libraryDependencies ++= commonLibs)


resolvers += "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases"
