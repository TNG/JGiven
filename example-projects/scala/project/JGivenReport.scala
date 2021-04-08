import com.tngtech.jgiven.report.ReportGenerator
import com.tngtech.jgiven.report.html5.Html5ReportConfig

import java.io._

object JGivenReport {
  def report(sourceDir: File, targetDir: File): Unit = {
    val config = new Html5ReportConfig()
    config.setSourceDir(sourceDir)
    config.setTargetDir(targetDir)
    ReportGenerator.generateHtml5Report().generateWithConfig(config)
  }
}
