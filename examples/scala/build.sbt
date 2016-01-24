name := """hello-jgiven-scala"""

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq( 
    "org.scalatest" %% "scalatest" % "2.2.4" % "test",
    "junit" % "junit" % "4.12" % "test",
    "com.tngtech.jgiven" % "jgiven-junit" % "0.11.0" % "test",
    "com.novocode" % "junit-interface" % "0.8" % "test",
    "org.slf4j" % "slf4j-simple" % "1.7.13" % "test"    
    )

testOptions += Tests.Argument(TestFrameworks.JUnit, 
	"-Djgiven.report.text.color=false", 
	"-Dorg.slf4j.simpleLogger.defaultLogLevel=info")

val jgivenReport = taskKey[Unit]("Creates the JGiven HTML report")

jgivenReport := {
  val jsonDir = new File("jgiven-reports")
  val htmlDir = target.value / "jgiven-html-report"
  
  JGivenReport.report(jsonDir, htmlDir)
  println("created JGiven report ")
}

fork in run := true
fork in Test := true