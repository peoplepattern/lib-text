package com.peoplepattern.text

// ScalaTest
import org.scalatest._
import com.peoplepattern.text.Implicits._
import java.net.URL

class StringWithAnalysisImplicitSpec extends FunSpec with Matchers {
  describe("str.tokenize") {
    it("should be able to tokenize a Tweet") {
      val tweet = "Gronk makes history: 1st player to have multiple games of 3 or more receiving TDs @RobGronkowski #crazyfootballmomma  @NFL 🔥🏈🔥🏈 #ballout"

      val expected = Vector(
        "Gronk",
        "makes",
        "history",
        ":",
        "1st",
        "player",
        "to",
        "have",
        "multiple",
        "games",
        "of",
        "3",
        "or",
        "more",
        "receiving",
        "TDs",
        "@RobGronkowski",
        "#crazyfootballmomma",
        "@NFL",
        "🔥",
        "🏈",
        "🔥",
        "🏈",
        "#ballout"
      )

      assert(tweet.tokens === expected)
    }
  }

  describe("str.terms") {
    it("should be able to get terms from a Tweet") {
      val tweet = "Gronk makes history: 1st player to have multiple games of 3 or more receiving TDs @RobGronkowski #crazyfootballmomma  @NFL 🔥🏈🔥🏈 #ballout"

      val expected = Set(
        "gronk",
        "makes",
        "history",
        "player",
        "multiple",
        "games",
        "receiving",
        "tds"
      )

      assert(tweet.terms === expected)
    }
  }

  describe("str.termBigrams") {
    it("should be able to get term bigrams from Tweets") {
      val tweet = "Gronk makes history: 1st player to have multiple games of 3 or more receiving TDs @RobGronkowski #crazyfootballmomma  @NFL 🔥🏈🔥🏈 #ballout"

      val expected = Set(
        "gronk makes",
        "makes history",
        "multiple games",
        "receiving tds"
      )

      assert(tweet.termBigrams === expected)
    }
  }

  describe("str.termTrigrams") {
    it("should be able to get term trigrams from Tweets") {
      val tweet = "Gronk makes history: 1st player to have multiple games of 3 or more receiving TDs @RobGronkowski #crazyfootballmomma  @NFL 🔥🏈🔥🏈 #ballout"
      val expected = Set("gronk makes history")
      assert(tweet.termTrigrams === expected)
    }
  }

  describe("str.lang") {
    it("should predict language from text") {
      val tweet = "Gronk makes history: 1st player to have multiple games of 3 or more receiving TDs @RobGronkowski #crazyfootballmomma  @NFL 🔥🏈🔥🏈 #ballout"
      assert(tweet.lang == Some("en"))
    }
  }

  describe("str.termsPlus") {
    it("should get terms, @-mentions and hashtags from a tweet") {
      val tweet = "Gronk makes history: 1st player to have multiple games of 3 or more receiving TDs @RobGronkowski #crazyfootballmomma  @NFL 🔥🏈🔥🏈 #ballout"

      val expected = Set(
        "gronk",
        "makes",
        "history",
        "player",
        "multiple",
        "games",
        "receiving",
        "tds",
        "@robgronkowski",
        "#crazyfootballmomma",
        "@nfl",
        "#ballout"
      )

      assert(tweet.termsPlus === expected)
    }
  }

  describe("str.isBlank") {
    it("should identify \"\" is blank") {
      assert("".isBlank)
    }

    it("should identify all white-space strings are blank") {
      assert(" \n\t \t".isBlank)
    }

    it("should not identifier a non-empty string") {
      assert(!"hello".isBlank)
    }

    it("should identify null is blank") {
      assert(null.asInstanceOf[String].isBlank) // Scala is amazing
    }
  }

  describe("str.nonBlank") {
    it("should not identify \"\" as non-blank") {
      assert(!"".nonBlank)
    }

    it("should not identify all white-space strings are non-blank") {
      assert(!" \n \t \t\t".nonBlank)
    }

    it("should identify non-empty string as non-blank") {
      assert("hello".nonBlank)
    }

    it("should not identify null as non-blank") {
      assert(!null.asInstanceOf[String].nonBlank)
    }
  }

  describe("StringUtil.charNgrams") {
    it("should extract bigrams with (2, 2)") {
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
      assert(testStr.charNgrams(2, 2) == expected)
    }

    it("should not throw out \\t and \\n etc") {
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
      assert(testStr.charNgrams(2, 2) == expected)
    }

    it("should extract bigrams and trigrams with (2, 3)") {
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
      assert(testStr.charNgrams(2, 3) == expected)
    }
  }

  describe("str.charBigrams") {
    it("should produce the same output as charNgrams(2, 2)") {
      val testStr = "Knox on fox in socks in box."
      assert(testStr.charBigrams == testStr.charNgrams(2, 2))
    }
  }

  describe("str.asUrl") {
    it("should correctly transform a URL string to a URL") {
      assert("http://google.com".asUrl == Some(new URL("http://google.com")))
    }

    it("should convert a non-URL to None") {
      assert("hello".asUrl == None)
    }
  }

  val cnnUrlNoQuery = "http://money.cnn.com/video/technology/2015/11/30/tech-gift-guide-selfie-gadgets.cnnmoney/index.html"
  val dollarShaveClubUrl = "http://try.dollarshaveclub.com/disrupt-out-desk5/?utm_medium=display&utm_source=outbrain&utm_campaign=disrupt-5&utm_content=meet-man&utm_term=46032174&cvosrc=display.outbrain.disrupt-5_meet-man"
  val githubUrlWithSsl = "https://peoplepattern.github.io/lib-text/"

  describe("str.isUrl") {
    it("should predict URLs are URLs") {
      assert(cnnUrlNoQuery.isUrl)
      assert(dollarShaveClubUrl.isUrl)
      assert(githubUrlWithSsl.isUrl)
    }

    it("should predict obviously non-URLs are not") {
      assert(!"hello".isUrl)
      assert(!"this.is.the".isUrl)
    }
  }

  describe("str.simplifyUrl") {
    it("should by default extract URL host and part of the path") {
      assert(cnnUrlNoQuery.simplifiedUrl == Some("money.cnn.com/video"))
      assert(dollarShaveClubUrl.simplifiedUrl == Some("try.dollarshaveclub.com/disrupt-out-desk5"))
      assert(githubUrlWithSsl.simplifiedUrl == Some("peoplepattern.github.io/lib-text"))
    }
  }

  describe("str.asOpt") {
    it("should convert non-blank string to Some") {
      assert("hello".asOpt == Some("hello"))
    }

    it("should convert null to None") {
      assert(null.asInstanceOf[String].asOpt == None)
    }

    it("should convert only white-space strings to None") {
      assert(" \n\t\t ".asOpt == None)
    }
  }
}
