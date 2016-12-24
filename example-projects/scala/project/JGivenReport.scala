import java.io._
import java.nio.file.Paths
import com.tngtech.jgiven.report.ReportGenerator

object JGivenReport {
  def report(sourceDir: File, targetDir: File) = {
    val reportGenerator = new ReportGenerator()
    reportGenerator.setSourceDirectory(sourceDir)
    reportGenerator.setTargetDirectory(targetDir)
    reportGenerator.generate()
  }
}