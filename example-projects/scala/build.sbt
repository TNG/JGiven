name := """hello-jgiven-scala"""

version := "1.0"
val jgivenVersion = "1.2.0"

scalaVersion := "2.13.5"

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  "com.tngtech.jgiven" % "jgiven-junit5" % jgivenVersion % Test,
  "net.aichler" % "jupiter-interface" % JupiterKeys.jupiterVersion.value % Test,
  "org.scalatest" %% "scalatest-core" % "3.2.7" % Test,
  "org.scalatest" %% "scalatest-shouldmatchers" % "3.2.7" % Test,
  "org.slf4j" % "slf4j-simple" % "1.7.30" % Test
)

Test / javaOptions += s"-Djgiven.report.dir=${target.value / "jgiven-reports" / "json"}"

val jgivenReport = taskKey[Unit]("Creates the JGiven HTML report")

jgivenReport := {
  val jsonDir = target.value / "jgiven-reports" / "json"
  val htmlDir = target.value / "jgiven-reports" / "html"

  JGivenReport.report(jsonDir, htmlDir)
  println("created JGiven report ")
}

Test / fork := true
