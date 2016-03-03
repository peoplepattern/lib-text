package com.peoplepattern.text

import scala.collection.mutable.Buffer
import scala.collection.JavaConverters._
import com.atilika.kuromoji.ipadic.Tokenizer

/**
 * Custom language bundle for Japanese
 *
 * Uses Kuromoji for tokenization https://github.com/atilika/kuromoji
 */
@SerialVersionUID(1)
class JaLangBundle(val stopwords: Set[String])
    extends LangBundle with Serializable {

  @transient lazy val tokenizer = new Tokenizer

  /**
   * Whether the char is in the ASCII range
   */
  private def asciichar(c: Char): Boolean = 0.toChar <= c && c <= 255.toChar

  private def asciiSplit(text: String): Vector[(String, Boolean)] = {
    var tmp = text
    val buf = Buffer.empty[(String, Boolean)]
    while (tmp.nonEmpty) {
      val ascii = tmp.takeWhile(asciichar)
      if (ascii.nonEmpty) {
        buf += ascii.toString -> true
        tmp = tmp.drop(ascii.size)
      }

      val nonascii = tmp.takeWhile(!asciichar(_))
      if (nonascii.nonEmpty) {
        buf += nonascii.toString -> false
        tmp = tmp.drop(nonascii.size)
      }
    }
    buf.toVector
  }

  /**
   * Pure japanese text tokenization using Kuromoji
   */
  def jatokens(text: String): Vector[String] = {
    tokenizer.tokenize(text).asScala.toVector.map(_.getSurface)
  }

  override def tokens(text: String) = {
    /* Split the text into block of ASCII and non-ASCII
     * - Tokenize the ASCII with the default method to get hashtags, URLs, emails etc
     * - Tokenize the non-ASCII with Kuromoji
     */
    val tokens = asciiSplit(text).flatMap {
      case (block, isAscii) => {
        if (isAscii) {
          super.tokens(block)
        } else {
          jatokens(block)
        }
      }
    }

    if (tokens.contains("#")) {
      // Post process YET AGAIN to make sure we merge in any Japanese hashtags
      val buf = Buffer.empty[String]
      var j = 0
      while (j < tokens.size) {
        if (tokens(j) == "#" && j + 1 < tokens.size && tokens(j + 1).nonEmpty && tokens(j + 1).head.isLetter) {
          buf += (tokens(j) ++ tokens(j + 1))
          j += 2
        } else {
          buf += tokens(j)
          j += 1
        }
      }
      buf.toVector
    } else {
      tokens
    }
  }
}

object JaLangBundle extends JaLangBundle(LangBundle.stopwords("ja"))
