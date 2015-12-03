package com.peoplepattern.text

import org.scalatest._

class LanguageIdentifierSpec extends FlatSpec {
  val enTxt = "this is english"
  val frTxt = "ceci est le français"

  val impl = LanguageIdentifier

  "LanguageIdentifier default impl" should "predict en" in {
    assert(impl.classify(enTxt).map(_._1) == Some("en"))
  }

  it should "predict fr" in {
    assert(impl.classify(frTxt).map(_._1) == Some("fr"))
  }

  it should "summarize text with the right frequency" in {
    val sum = impl.summarize(Seq(enTxt, enTxt, enTxt, frTxt, frTxt))
    assert(sum.size == 2)
    assert(sum(0)._1 == "en")
    assert(sum(0)._3 == 0.6)
    assert(sum(1)._1 == "fr")
    assert(sum(1)._3 == 0.4)
  }
}
