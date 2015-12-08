package com.peoplepattern.text

import scala.collection.mutable
import java.net.{ URL, MalformedURLException }

object StringUtil {

  /** Determine if the string is null or only contains white-space */
  def isBlank(s: String): Boolean = Option(s) match {
    case Some(str) => str.trim.isEmpty
    case _ => true
  }

  /** Counts of the character n-grams in the string */
  def charNgrams(s: String, min: Int, max: Int): Map[String, Int] = {
    val mmap = mutable.Map.empty[String, Int].withDefaultValue(0)
    for {
      len <- min to max
      ngram <- s.sliding(len)
    } mmap(ngram) += 1
    mmap.toMap
  }

  /** Counts of the character 2-grams in the string */
  def charBigrams(s: String) = charNgrams(s, 2, 2)

  /** Whether the string contains a URL */
  def isUrl(s: String): Boolean = try {
    val u = new URL(s)
    u != null
  } catch {
    case _: MalformedURLException => false
  }

  /**
   * Simplify a URL into something less unique
   *
   * Eg simplifyUrl(http://money.cnn.com/video/technology/2015/11/30/tech-gift-guide-selfie-gadgets.cnnmoney/index.html)
   * is simplified to money.cnn.com/video
   */
  def simplifyUrl(u: URL): String = simplifyUrl(u, None, 1, false)

  /**
   * Simplify a URL into something less unique
   *
   * @param u the URL
   * @param prefix prefix to append to the simplified URL, e.g. "url:"
   * @param path the number of parts of the path to keep, e.g. with path = 2 "video/technology/2015" -> "video/technology"
   * @param keepProtocol if true, keep the "http" or "https" part of the URL
   */
  def simplifyUrl(
    u: URL,
    prefix: Option[String],
    path: Int,
    keepProtocol: Boolean): String = {
    val sb = new StringBuilder
    prefix foreach { p =>
      sb ++= p
    }
    if (keepProtocol) {
      sb ++= u.getProtocol ++= "://"
    }
    sb ++= u.getHost
    if (path > 0) {
      val urlPath = u.getPath
      val simplifiedPath = urlPath.split("/").dropWhile(_.isEmpty).take(path)
      if (simplifiedPath.nonEmpty) {
        sb ++= "/" ++= simplifiedPath.mkString("/")
      }
    }
    sb.toString
  }

  /** Convert a string to Some(s) if it's not blank or null, otherwise None */
  def asOpt(s: String): Option[String] = {
    if (isBlank(s)) None else Some(s)
  }
}
