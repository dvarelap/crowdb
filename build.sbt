
lazy val libs = Seq(
	// Test libs
  "junit" % "junit" % "4.12",
  "org.specs2" %% "specs2" % "3.3.1",
  "org.specs2" %% "specs2-mock" % "3.6.1",
	"org.specs2" %% "specs2-junit" % "3.6.1",
	"com.github.mauricio" %% "postgresql-async" % "0.2.16",
	"com.twitter" %% "util-core" % "6.24.0"
)

lazy val crowdb = (project in file("."))
	.settings(
    scalaVersion                 := "2.11.7",
		scalaSource in Compile       := baseDirectory.value / "app",
		scalaSource in Test          := baseDirectory.value / "test",
		resourceDirectory in Compile := baseDirectory.value / "config",
		resourceDirectory in Test    := baseDirectory.value / "test-config"
	)
	.settings(libraryDependencies ++= libs)
	.dependsOn(crowdbMacro, crowdbCommon)


lazy val macroLibs = Seq(
	// "com.github.mauricio" %% "postgresql-async" % "0.2.14",
	// "org.scala-lang.modules" %% "scala-async" % "0.9.1",
	"org.scala-lang" % "scala-reflect" % "2.11.7",
	"com.github.mauricio" %% "postgresql-async" % "0.2.16"
	// "com.vividsolutions" % "jts" % "1.13",
	// "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"
)

lazy val crowdbMacro = (project in file("crowdb-macro"))
	.settings(scalaVersion := "2.11.7")
	.settings(libraryDependencies ++= macroLibs)
  .dependsOn(crowdbCommon)

lazy val crowdbCommon = (project in file("crowdb-common"))
	.settings(scalaVersion := "2.11.7")
