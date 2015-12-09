package com.peoplepattern.text

// ScalaTest
import org.scalatest._

class LangBundleSpec extends FunSpec with Matchers {
  describe("LangBundle") {
    it("should have a bundle available for each of the declared languages") {
      for (lang <- LangBundle.langs) {
        assert(LangBundle.bundleForLang(Some(lang)) != LangBundle.unk)
      }
    }
  }

  describe("LangBundle.unk") {
    it("should have all the other languages' stopwords indexed") {
      for (lang <- LangBundle.langs) {
        assert(LangBundle.bundleForLang(Some(lang)).stopwords.subsetOf(LangBundle.unk.stopwords))
      }
    }
  }

  describe("LangBundle(en).isHashtag") {
    it("should identify #this_is_the_best_day_EVER is a hashtag") {
      assert(LangBundle("en").isHashtag("#this_is_the_best_day_EVER"))
    }

    it("should not identify #^*! is a hashtag") {
      assert(!LangBundle("en").isHashtag("#^*!"))
    }
  }

  describe("LangBundle(en).tokenize") {
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

      val actual = LangBundle("en").tokens(tweet)

      info(s"tweet = ${tweet}")
      info(s"tokens = ${actual}")

      assert(actual === expected)
    }
  }

  describe("LangBundle(en).terms") {
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

      val actual = LangBundle("en").terms(tweet)

      info(s"tweet = ${tweet}")
      info(s"terms = ${actual}")

      assert(actual === expected)
    }
  }

  describe("LangBundle(en).termsPlus") {
    it("should not throw out hashtags and @-mentions") {
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

      assert(LangBundle("en").termsPlus(tweet) === expected)
    }
  }

  describe("LangBundle(en).termBigrams") {
    it("should be able to get term bigrams from Tweets") {
      val tweet = "Gronk makes history: 1st player to have multiple games of 3 or more receiving TDs @RobGronkowski #crazyfootballmomma  @NFL 🔥🏈🔥🏈 #ballout"

      val expected = Set(
        "gronk makes",
        "makes history",
        "multiple games",
        "receiving tds"
      )

      assert(LangBundle("en").termBigrams(tweet) === expected)
      assert(LangBundle("en").termBigrams(LangBundle("en").tokens(tweet)) === expected)
    }
  }

  describe("LangBundle(en).termTrigrams") {
    it("should be able to get term bigrams from Tweets") {
      val tweet = "Gronk makes history: 1st player to have multiple games of 3 or more receiving TDs @RobGronkowski #crazyfootballmomma  @NFL 🔥🏈🔥🏈 #ballout"

      val expected = Set("gronk makes history")

      assert(LangBundle("en").termTrigrams(tweet) === expected)
      assert(LangBundle("en").termTrigrams(LangBundle("en").tokens(tweet)) === expected)
    }
  }
}
