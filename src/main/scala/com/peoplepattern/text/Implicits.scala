package com.peoplepattern.text

object Implicits {

  implicit class StringWithAnalysis(str: String) {

    lazy val lang: Option[String] = LanguageIdentifier.classify(str).map(_._1)

    lazy val bundle: LangBundle = LangBundle.bundleForLang(lang)

    lazy val tokens: Vector[String] = bundle.tokens(str)

    lazy val terms: Set[String] = bundle.terms(tokens)

    lazy val termsPlus: Set[String] = bundle.termsPlus(tokens)

    lazy val termBigrams: Set[String] = termNgrams(2, 2)

    lazy val termTrigrams: Set[String] = termNgrams(3, 3)

    def termNgrams(min: Int, max: Int) = bundle.termNgrams(tokens, min, max)
  }
}
