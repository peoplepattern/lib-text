package com.peoplepattern.text

import java.net.{ URL, MalformedURLException }

object Implicits {

  /** Helpers to provide access to LangBundle and StringUtil stuff on strings */
  implicit class StringWithAnalysis(str: String) {

    /** The detected language of the string, if predicted */
    lazy val lang: Option[String] = LanguageIdentifier.classify(str).map(_._1)

    /** The language bundle for the string based on it's predicted language */
    lazy val bundle: LangBundle = LangBundle.bundleForLang(lang)

    /** The tokens of the string using language specific tokenization */
    lazy val tokens: Vector[String] = bundle.tokens(str)

    /** The terms of the string using language specific tokens & stopwords */
    lazy val terms: Set[String] = bundle.terms(tokens)

    /** The terms + hashtags + @-mentions of the string */
    lazy val termsPlus: Set[String] = bundle.termsPlus(tokens)

    /** The term bigrams of the string */
    lazy val termBigrams: Set[String] = termNgrams(2, 2)

    /** The term 3-grams of the string */
    lazy val termTrigrams: Set[String] = termNgrams(3, 3)

    /**
     * Extract term n-grams from the string
     *
     * @param min the minimum n-gram length to extract
     * @param max the maximum n-gram length to extract
     */
    def termNgrams(min: Int, max: Int) = bundle.termNgrams(tokens, min, max)

    /** Whether the string is empty or only white-space */
    def isBlank: Boolean = StringUtil.isBlank(str)

    /** Whether the string is *not* empty or only white-space */
    def nonBlank: Boolean = !isBlank

    /**
     * Extract counts of the char n-grams in the string
     *
     * @param min the minimum n-gram length to extract
     * @param max the maximum n-gram length to extract
     */
    def charNgrams(min: Int, max: Int): Map[String, Int] = StringUtil.charNgrams(str, min, max)

    /** Extract counts of the char bigrams in the string */
    def charBigrams: Map[String, Int] = charNgrams(2, 2)

    /** The term as a URL, if it can be parsed as such */
    lazy val asUrl: Option[URL] = try {
      Some(new URL(str))
    } catch {
      case _: MalformedURLException => None
    }

    /** The terms as a simplified URL if it can be parsed as such */
    lazy val simplifiedUrl: Option[String] = asUrl.map(StringUtil.simplifyUrl)

    /** Whether the string can be parsed as a URL */
    def isUrl: Boolean = asUrl.nonEmpty

    /** Some(this) if the string is non-empty and doesn't contain only white-space */
    def asOpt: Option[String] = StringUtil.asOpt(str)
  }
}
