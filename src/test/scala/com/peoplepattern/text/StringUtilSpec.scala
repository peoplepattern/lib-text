package com.peoplepattern.text

import org.scalatest._
import java.net.URL

class StringUtilSpec extends FlatSpec {

  "StringUtil.isBlank" should "declare \"hello\" is not blank" in {
    assert(!StringUtil.isBlank("hello"))
  }

  it should "declare \"\" is blank" in {
    assert(StringUtil.isBlank(""))
  }

  it should "declare null is blank" in {
    assert(StringUtil.isBlank(null))
  }

  it should "declare that a string with only white-space is blank" in {
    assert(StringUtil.isBlank("\t\n\t "))
  }

  "StringUtil.charNgrams" should "extract bigrams with (2, 2)" in {
    val testStr = "Knox on fox in socks in box."
    val expected = Map(
      " b" -> 1,
      " f" -> 1,
      " i" -> 2,
      " o" -> 1,
      " s" -> 1,
      "Kn" -> 1,
      "bo" -> 1,
      "ck" -> 1,
      "fo" -> 1,
      "in" -> 2,
      "ks" -> 1,
      "n " -> 3,
      "no" -> 1,
      "oc" -> 1,
      "on" -> 1,
      "ox" -> 3,
      "s " -> 1,
      "so" -> 1,
      "x " -> 2,
      "x." -> 1)
    assert(StringUtil.charNgrams(testStr, 2, 2) == expected)
  }

  it should "not throw out \\t and \\n etc" in {
    val testStr = " Knox\ton\nfox \t in socks in box.\n"
    val expected = Map(
      " \t" -> 1,
      "\t " -> 1,
      "\nf" -> 1,
      ".\n" -> 1,
      " K" -> 1,
      " b" -> 1,
      " i" -> 2,
      "\to" -> 1,
      " s" -> 1,
      "Kn" -> 1,
      "bo" -> 1,
      "ck" -> 1,
      "fo" -> 1,
      "in" -> 2,
      "ks" -> 1,
      "n\n" -> 1,
      "n " -> 2,
      "no" -> 1,
      "oc" -> 1,
      "on" -> 1,
      "ox" -> 3,
      "s " -> 1,
      "so" -> 1,
      "x " -> 1,
      "x\t" -> 1,
      "x." -> 1)
    assert(StringUtil.charNgrams(testStr, 2, 2) == expected)
  }

  it should "extract bigrams and trigrams with (2, 3)" in {
    val testStr = "Knox on fox in socks in box."
    val expected = Map(
      " b" -> 1,
      " bo" -> 1,
      " f" -> 1,
      " fo" -> 1,
      " i" -> 2,
      " in" -> 2,
      " o" -> 1,
      " on" -> 1,
      " s" -> 1,
      " so" -> 1,
      "Kn" -> 1,
      "Kno" -> 1,
      "bo" -> 1,
      "box" -> 1,
      "ck" -> 1,
      "cks" -> 1,
      "fo" -> 1,
      "fox" -> 1,
      "in" -> 2,
      "in " -> 2,
      "ks" -> 1,
      "ks " -> 1,
      "n " -> 3,
      "n b" -> 1,
      "n f" -> 1,
      "n s" -> 1,
      "no" -> 1,
      "nox" -> 1,
      "oc" -> 1,
      "ock" -> 1,
      "on" -> 1,
      "on " -> 1,
      "ox" -> 3,
      "ox " -> 2,
      "ox." -> 1,
      "s " -> 1,
      "s i" -> 1,
      "so" -> 1,
      "soc" -> 1,
      "x " -> 2,
      "x i" -> 1,
      "x o" -> 1,
      "x." -> 1)
    assert(StringUtil.charNgrams(testStr, 2, 3) == expected)
  }

  "StringUtil.charBigrams" should "produce the same output as charNgrams(2, 2)" in {
    val testStr = "Knox on fox in socks in box."
    assert(StringUtil.charBigrams(testStr) == StringUtil.charNgrams(testStr, 2, 2))
  }

  val cnnUrlNoQuery = "http://money.cnn.com/video/technology/2015/11/30/tech-gift-guide-selfie-gadgets.cnnmoney/index.html"
  val dollarShaveClubUrl = "http://try.dollarshaveclub.com/disrupt-out-desk5/?utm_medium=display&utm_source=outbrain&utm_campaign=disrupt-5&utm_content=meet-man&utm_term=46032174&cvosrc=display.outbrain.disrupt-5_meet-man"
  val githubUrlWithSsl = "https://peoplepattern.github.io/lib-text/"

  "StringUtil.isUrl" should "predict URLs are URLs" in {
    assert(StringUtil.isUrl(cnnUrlNoQuery))
    assert(StringUtil.isUrl(dollarShaveClubUrl))
    assert(StringUtil.isUrl(githubUrlWithSsl))
  }

  it should "predict obviously non-URLs are not" in {
    assert(!StringUtil.isUrl("hello"))
    assert(!StringUtil.isUrl("this.is.the"))
  }

  "StringUtil.simplifyUrl" should "by default extract URL host and part of the path" in {
    assert(StringUtil.simplifyUrl(new URL(cnnUrlNoQuery)) == "money.cnn.com/video")
    assert(StringUtil.simplifyUrl(new URL(dollarShaveClubUrl)) == "try.dollarshaveclub.com/disrupt-out-desk5")
    assert(StringUtil.simplifyUrl(new URL(githubUrlWithSsl)) == "peoplepattern.github.io/lib-text")
  }

  it should "get the protocol right when asked" in {
    assert(StringUtil.simplifyUrl(new URL(cnnUrlNoQuery), prefix = None, path = 1, keepProtocol = true) == "http://money.cnn.com/video")
    assert(StringUtil.simplifyUrl(new URL(githubUrlWithSsl), prefix = None, path = 1, keepProtocol = true) == "https://peoplepattern.github.io/lib-text")
  }

  it should "support path > 1" in {
    assert(StringUtil.simplifyUrl(new URL(cnnUrlNoQuery), prefix = None, path = 3, keepProtocol = false) == "money.cnn.com/video/technology/2015")
    assert(StringUtil.simplifyUrl(new URL(dollarShaveClubUrl), prefix = None, path = 3, keepProtocol = false) == "try.dollarshaveclub.com/disrupt-out-desk5")
    assert(StringUtil.simplifyUrl(new URL(githubUrlWithSsl), prefix = None, path = 3, keepProtocol = false) == "peoplepattern.github.io/lib-text")
  }

  it should "add a prefix if applicable" in {
    assert(StringUtil.simplifyUrl(new URL(cnnUrlNoQuery), prefix = Some("url:"), path = 3, keepProtocol = true) == "url:http://money.cnn.com/video/technology/2015")
    assert(StringUtil.simplifyUrl(new URL(dollarShaveClubUrl), prefix = Some("url:"), path = 3, keepProtocol = true) == "url:http://try.dollarshaveclub.com/disrupt-out-desk5")
    assert(StringUtil.simplifyUrl(new URL(githubUrlWithSsl), prefix = Some("url:"), path = 3, keepProtocol = true) == "url:https://peoplepattern.github.io/lib-text")
  }

  "StringUtil.asOpt" should "convert non-blank string to Some" in {
    assert(StringUtil.asOpt("hello") == Some("hello"))
  }

  it should "convert null to None" in {
    assert(StringUtil.asOpt(null) == None)
  }

  it should "convert only white-space strings to None" in {
    assert(StringUtil.asOpt(" \n\t\t ") == None)
  }
}
