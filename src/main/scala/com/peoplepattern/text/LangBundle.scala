package com.peoplepattern.text

import scala.collection.mutable.{ Buffer, Set => MSet }
import java.util.regex.{ Pattern, Matcher }

/**
 * Language-specific (or general) text processing utility
 */
trait LangBundle {

  private val ellipseSubRe = "\\.{2,}|…".r

  private def ellipseSub(text: String) = ellipseSubRe.replaceAllIn(text, "...")

  private def reOR(parts: String*) = "(" + parts.toList.mkString("|") + ")"

  private val p = {
    val html_chars = """&\w+;""" // separates the junk that comes from > and < and &
    val numbers_commas = """[\-\$]?\d{1,3}(?:,\d{3})+""" // like 2,000,000
    val times = """\d?\d:\d{2}""" // like 2:12
    val acronyms = """(?:\w{1}\.{1})+""" // like U.T.
    val emails = """[\w\.\d]+@[\w\.\d]+\.[\w]+""" //catch email addresses
    val money = """-?\$?\d+[.]\d+%?""" //Catch money numerics
    val urls = """https?://[-_/~%\w\d\.]*[_/~\w\d]""" // Catch url addresses
    val slashes = """[\w]+(?:[/\-][\w]+)+""" //Grammatical / -
    val sideways_text_emoji = """>?[:;=]['\-D\)\]\(\[pPdoO/\*3\\]+"""
    val hearts = "<+/?3+" // <3
    val possessive_mentions = """@\w+""" //splits possessive off of @jimbob's
    val possessive_hashtags = """#\w+""" //splits possessive off of #tcot's
    val tags_contractions = """[\w]+['‘’][\w]+""" //don't split don't and can't and it's
    val ellipses = """\.{3}"""
    val en_em_dash = """-{2,3}""" //Catch en and em dashes
    val punct = """["“”‘’'.?!…,:;»«()]""" //punctuation to split on
    val all_other = """[^\s]""" //Split any other weird chars that may have been missed

    val tags_mentions = """[\w#@\d%$\u00B0]+""" //Group all of these things together
    val other_punct = "[\u2014\u2013]" //Catch unicode em/en dash

    val emoji_block0 = "[\u2600-\u27BF]"
    val emoji_block1 = "[\uD83C][\uDF00-\uDFFF]"
    val emoji_block1b = "[\uD83D][\uDC00-\uDE4F]"
    val emoji_block2 = "[\uD83D][\uDE80-\uDEFF]"

    val reParts = reOR(
      html_chars,
      numbers_commas,
      times,
      money,
      acronyms,
      possessive_mentions,
      possessive_hashtags,
      tags_contractions,
      emails,
      urls,
      sideways_text_emoji,
      ellipses,
      en_em_dash,
      slashes,
      punct,
      tags_mentions,
      emoji_block0,
      emoji_block1,
      emoji_block1b,
      emoji_block2,
      hearts,
      other_punct,
      all_other
    )
    Pattern.compile(reParts, Pattern.UNICODE_CHARACTER_CLASS)
  }

  /**
   * Parse text into an array of String
   *
   * @param text text to tokenize
   */
  def tokens(text: String): Vector[String] = {
    val b = Buffer.empty[String]
    val m = p.matcher(ellipseSub(text))
    while (m.find) b += m.group
    b.toVector
  }

  def stopwords: Set[String]

  def isContentTerm(term: String) = {
    term.forall(_.isLetter) && !stopwords(term.toLowerCase)
  }

  def terms(text: String): Set[String] = terms(tokens(text))

  def terms(tokens: Seq[String]): Set[String] = {
    tokens.filter(isContentTerm).map(_.toLowerCase).toSet
  }

  private def isSocialThingChar(c: Char): Boolean = {
    c.isLetter || c.isDigit || c == '_'
  }

  def isHashtag(term: String) = {
    term.size >= 2 && term(0) == '#' && term.tail.forall(isSocialThingChar)
  }

  def isMention(term: String) = {
    term.size >= 2 && term(0) == '@' && term.tail.forall(isSocialThingChar)
  }

  /**
   * Word-only terms plus hashtags, emoji, @-mentions
   */
  def termsPlus(text: String): Set[String] = termsPlus(tokens(text))

  /**
   * Word-only terms plus hashtags, emoji, @-mentions
   */
  def termsPlus(tokens: Seq[String]): Set[String] = {
    tokens.filter(w =>
      isContentTerm(w) || isMention(w) || isHashtag(w)
    ).map(_.toLowerCase).toSet
  }

  def termNgrams(text: String, min: Int, max: Int): Set[String] = {
    termNgrams(tokens(text), min, max)
  }

  def termNgrams(tokens: Seq[String], min: Int, max: Int): Set[String] = {
    val seqs = Buffer.empty[Vector[String]]
    val thisbf = Buffer.empty[String]
    for (token <- tokens) {
      if (isContentTerm(token)) {
        thisbf += token.toLowerCase
      } else {
        if (thisbf.nonEmpty) {
          seqs += thisbf.toVector
        }
        thisbf.clear()
      }
    }
    val termNgrams = MSet.empty[String]
    for {
      n <- min to max
      seq <- seqs if seq.size >= n
    } termNgrams ++= seq.sliding(n).map(_.mkString(" "))
    termNgrams.toSet
  }

  def termBigrams(text: String) = termNgrams(text, 2, 2)

  def termBigrams(tokens: Seq[String]) = termNgrams(tokens, 2, 2)

  def termTrigrams(text: String) = termNgrams(text, 3, 3)

  def termTrigrams(tokens: Seq[String]) = termNgrams(tokens, 3, 3)
}

object LangBundle {

  import scala.io.Source

  def srcFromResource(path: String) = {
    Source.fromInputStream(getClass.getResourceAsStream(path))
  }

  def stopwords(lang: String) = {
    val src = srcFromResource(s"/$lang/stopwords.txt")
    try {
      src.getLines.toSet
    } finally {
      src.close()
    }
  }

  def mkBundle(lang: String) = {
    val stops = stopwords(lang)
    new LangBundle {
      val stopwords = stops
    }
  }

  lazy val de = mkBundle("de")
  lazy val en = mkBundle("en")
  lazy val es = mkBundle("es")
  lazy val fr = mkBundle("fr")
  lazy val in = mkBundle("in")
  lazy val ja = mkBundle("ja") // TODO improved tokenizer
  lazy val ms = mkBundle("ms")
  lazy val nl = mkBundle("nl")
  lazy val pt = mkBundle("pt")
  lazy val sv = mkBundle("sv")
  lazy val tr = mkBundle("tr")

  def langs = Set(
    "de",
    "en",
    "es",
    "fr",
    "in",
    "ja",
    "ms",
    "nl",
    "pt",
    "sv",
    "tr"
  )

  lazy val unk = {
    val stops: Set[String] = langs.flatMap(stopwords)
    new LangBundle {
      val stopwords = stops
    }
  }

  def bundleForLang(lang: Option[String]): LangBundle = lang match {
    case Some("de") => de
    case Some("en") => en
    case Some("es") => es
    case Some("fr") => fr
    case Some("in") => in
    case Some("ja") => ja
    case Some("ms") => ms
    case Some("nl") => nl
    case Some("pt") => pt
    case Some("sv") => sv
    case Some("tr") => tr
    case _ => unk
  }
}
