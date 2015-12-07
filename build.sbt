name := "lib-text"

organization := "com.peoplepattern"

scalaVersion := "2.10.6"

scalacOptions in ThisBuild ++= Seq(
  "-unchecked",
  "-feature",
  "-deprecation",
  "-language:_",
  "-Xlint",
  "-Xfatal-warnings",
  "-Ywarn-dead-code",
  "-target:jvm-1.7",
  "-encoding",
  "UTF-8"
)

libraryDependencies ++= Seq(
  "com.carrotsearch" % "langid-java" % "1.0.0",
  "com.typesafe"     % "config"      % "1.3.0",
  "org.scalatest"    %% "scalatest"  % "2.2.5" % "test"
)

scalariformSettings

licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

homepage := Some(url("http://peoplepattern.github.io/lib-text"))

crossScalaVersions := Seq("2.10.6", "2.11.7")

releaseCrossBuild := true

bintrayOrganization := Some("peoplepattern")

bintrayReleaseOnPublish := true

site.settings

site.includeScaladoc()

ghpages.settings

git.remoteRepo := "git@github.com:peoplepattern/lib-text.git"

site.jekyllSupport()
