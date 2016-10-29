import sbt._
import sbt.Keys._

import scala.collection.mutable.ListBuffer

// Adapted from https://github.com/ymasory/sbt-code-quality.g8
object CheckstyleSettings {

  val checkstyle = TaskKey[Unit]("checkstyle", "run checkstyle, placing results in target/checkstyle")
  val checkstyleTask = checkstyle <<=
    (streams, baseDirectory, sourceDirectory in Sources, target) map {
      (streams, base, src, target) =>
        import com.puppycrawl.tools.checkstyle.Main.{main => CsMain}
        import streams.log
        val outputDir = (target / "checkstyle").mkdirs
        val outputFile = (target / "checkstyle" / "checkstyle-report.xml").getAbsolutePath
        val inputDir = src.getAbsolutePath

        val args = List(
          "-c", (base / "project" / "checkstyle-config.xml").getAbsolutePath,
          "-f", "xml",
          "-r", inputDir,
          "-o", outputFile
        )
        log info ("Running checkstyle...")
        trappingExits {
          CsMain(args.toArray)
          log info "got here"
        }

        // Print out results.
        val files = processXMLReport(outputFile)

        var infosCount = 0
        var warningsCount = 0
        var errorsCount = 0

        files foreach { file =>
          file.problems foreach {
            case p: Info =>
              infosCount += 1
              log info file.name + ":" + p.line + ":" + p.col + ": " + p.message
            case p: Warning =>
              warningsCount += 1
              log warn file.name + ":" + p.line + ":" + p.col + ": " + p.message
            case p: Error =>
              errorsCount += 1
              log error file.name + ":" + p.line + ":" + p.col + ": " + p.message
          }
        }

        log info "Processed " + files.size + " file(s)"
        log info "Found " + errorsCount + " errors"
        log info "Found " + warningsCount + " warnings"
        log info "Found " + infosCount + " infos"
    }

  def trappingExits(thunk: => Unit): Unit = {
    val originalSecManager = System.getSecurityManager
    case class NoExitsException() extends SecurityException
    System setSecurityManager new SecurityManager() {
      import java.security.Permission
      override def checkPermission(perm: Permission) {
        if (perm.getName startsWith "exitVM") throw NoExitsException()
      }
    }
    try {
      thunk
    } catch {
      case _: NoExitsException =>
      case e : Throwable =>
    } finally {
      System setSecurityManager originalSecManager
    }
  }

  def processXMLReport(path: String): Seq[File] = {
    val files = new ListBuffer[File]()
    val result = scala.xml.XML.loadFile(path)
    (result \ "file") map { file =>
      val filename = (file \ "@name").text
      val problems = new ListBuffer[Problem]()
      (file \ "error") map { problem =>
        val severity = (problem \ "@severity").text
        if (severity == "info") {
          problems += Info((problem \ "@line").text.toInt, (problem \ "@column").text.toInt, (problem \ "@message").text)
        }
        else if (severity == "warning") {
          problems += Warning((problem \ "@line").text.toInt, (problem \ "@column").text.toInt, (problem \ "@message").text)
        }
        else if (severity == "error") {
          problems += Error((problem \ "@line").text.toInt, (problem \ "@column").text.toInt, (problem \ "@message").text)
        }
      }
      files += File(filename, problems.toList)
    }
    files.toList
  }
}

case class File(name: String, problems: Seq[Problem])

class Problem(line: Int, col: Int, message: String)
case class Info(line: Int, col: Int, message: String) extends Problem(line, col, message)
case class Warning(line: Int, col: Int, message: String) extends Problem(line, col, message)
case class Error(line: Int, col: Int, message: String) extends Problem(line, col, message)
