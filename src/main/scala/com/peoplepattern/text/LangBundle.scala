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

  /** Language specific stopwords */
  def stopwords: Set[String]

  /** Whether the string is probably a linguistic term with meaning */
  def isContentTerm(term: String) = {
    term.forall(_.isLetter) && !stopwords(term.toLowerCase)
  }

  /** Tokenize the string and extract the set of terms */
  def terms(text: String): Set[String] = terms(tokens(text))

  /** Extract terms from the sequence of tokens */
  def terms(tokens: Seq[String]): Set[String] = {
    tokens.filter(isContentTerm).map(_.toLowerCase).toSet
  }

  private def isSocialThingChar(c: Char): Boolean = {
    c.isLetter || c.isDigit || c == '_'
  }

  /** Whether the string can could be a social media hashtag */
  def isHashtag(term: String) = {
    term.size >= 2 && term(0) == '#' && term.tail.forall(isSocialThingChar)
  }

  /** Whether the string could be a social media @-mention */
  def isMention(term: String) = {
    term.size >= 2 && term(0) == '@' && term.tail.forall(isSocialThingChar)
  }

  /**
   * Tokenize the string and extract terms plus hashtags, emoji, @-mentions
   */
  def termsPlus(text: String): Set[String] = termsPlus(tokens(text))

  /**
   * Extract terms plus hashtags, emoji, @-mentions from the token sequence
   */
  def termsPlus(tokens: Seq[String]): Set[String] = {
    tokens.filter(w =>
      isContentTerm(w) || isMention(w) || isHashtag(w)
    ).map(_.toLowerCase).toSet
  }

  /**
   * Extract the set of term-only n-grams from the text
   *
   * For example from the text "this is the winning team" only the bigram
   * "winning team" would be extracted
   *
   * @param text the text to extract n-grams from
   * @param min the minimum length of extracted n-grams
   * @param max the maximum length of extracted n-grams
   */
  def termNgrams(text: String, min: Int, max: Int): Set[String] = {
    termNgrams(tokens(text), min, max)
  }

  /**
   * Extract the set of term-only n-grams from the token sequence
   *
   * For example from the text "this is the winning team" only the bigram
   * "winning team" would be extracted
   *
   * @param tokens the token sequence to extract n-grams from
   * @param min the minimum length of extracted n-grams
   * @param max the maximum length of extracted n-grams
   */
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

  /**
   * Extract the set of term-only bigrams from the text
   *
   * For example from the text "this is the winning team" only the bigram
   * "winning team" would be extracted
   *
   * @param text the text to extract n-grams from
   */
  def termBigrams(text: String) = termNgrams(text, 2, 2)

  /**
   * Extract the set of term-only bigrams from the token sequence
   *
   * For example from the text "this is the winning team" only the bigram
   * "winning team" would be extracted
   *
   * @param tokens the token sequence to extract n-grams from
   */
  def termBigrams(tokens: Seq[String]) = termNgrams(tokens, 2, 2)

  /**
   * Extract the set of term-only bigrams from the text
   *
   * For example from the text "this is red sox nation" only the trigram
   * "red sox nation" would be extracted
   *
   * @param text the text to extract n-grams from
   */
  def termTrigrams(text: String) = termNgrams(text, 3, 3)

  /**
   * Extract the set of term-only bigrams from the text
   *
   * For example from the text "this is red sox nation" only the trigram
   * "red sox nation" would be extracted
   *
   * @param tokens the token sequence to extract n-grams from
   */
  def termTrigrams(tokens: Seq[String]) = termNgrams(tokens, 3, 3)
}

/** Helpers and language-specifi [[LangBundle]]s **/
object LangBundle {

  import scala.io.Source

  private def srcFromResource(path: String) = {
    Source.fromInputStream(getClass.getResourceAsStream(path))
  }

  private def stopwords(lang: String) = {
    val src = srcFromResource(s"/$lang/stopwords.txt")
    try {
      src.getLines.toSet
    } finally {
      src.close()
    }
  }

  private def mkBundle(lang: String) = {
    val stops = stopwords(lang)
    new LangBundle {
      val stopwords = stops
    }
  }

  /** The [[LangBundle]] for German */
  lazy val de = mkBundle("de")

  /** The [[LangBundle]] for English */
  lazy val en = mkBundle("en")

  /** The [[LangBundle]] for Spanish */
  lazy val es = mkBundle("es")

  /** The [[LangBundle]] for French */
  lazy val fr = mkBundle("fr")

  /** The [[LangBundle]] for Indonesian */
  lazy val in = mkBundle("in")

  /**
   * The [[LangBundle]] for Japanese
   *
   * TODO improved tokenizer
   */
  lazy val ja = mkBundle("ja")

  /** The [[LangBundle]] for Malay */
  lazy val ms = mkBundle("ms")

  /** The [[LangBundle]] for Dutch */
  lazy val nl = mkBundle("nl")

  /** The [[LangBundle]] for Portuguese */
  lazy val pt = mkBundle("pt")

  /** The [[LangBundle]] for Swedish */
  lazy val sv = mkBundle("sv")

  /** The [[LangBundle]] for Turkish */
  lazy val tr = mkBundle("tr")

  /** The [[LangBundle]] for Armenian */
  lazy val ar = mkBundle("ar")

  /** The set of supported languages */
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
    "tr",
    "ar"
  )

  /**
   * A language bundle for text for which we don't have an identified language
   *
   * Uses all known stopwords
   */
  lazy val unk = {
    val stops: Set[String] = langs.flatMap(stopwords)
    new LangBundle {
      val stopwords = stops
    }
  }

  /**
   * Look up the [[LangBundle]] by language code
   *
   * @param lang two-letter ISO 639-1 language code
   */
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
    case Some("ar") => ar
    case _ => unk
  }
}
