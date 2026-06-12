name := """hello-jgiven-scala"""

version := "1.0"
val jgivenVersion = "2.0.3"

scalaVersion := "3.3.8"

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  "com.tngtech.jgiven" % "jgiven-junit6" % jgivenVersion % Test,
  "net.aichler" % "jupiter-interface" % JupiterKeys.jupiterVersion.value % Test,
  "org.scalatest" %% "scalatest-core" % "3.2.19" % Test,
  "org.scalatest" %% "scalatest-shouldmatchers" % "3.2.19" % Test,
  "org.slf4j" % "slf4j-simple" % "2.0.16" % Test
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
