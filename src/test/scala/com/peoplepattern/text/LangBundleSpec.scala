package com.peoplepattern.text

// ScalaTest
import org.scalatest._

class LangBundleSpec extends FunSpec with Matchers {
  describe("LangBundle.en.tokenize") {
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

      val actual = LangBundle.en.tokens(tweet)

      info(s"tweet = ${tweet}")
      info(s"tokens = ${actual}")

      assert(actual === expected)
    }
  }

  describe("LangBundle.en.terms") {
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

      val actual = LangBundle.en.terms(tweet)

      info(s"tweet = ${tweet}")
      info(s"terms = ${actual}")

      assert(actual === expected)
    }
  }

  describe("LangBundle.en.termBigrams") {
    it("should be able to get term bigrams from Tweets") {
      val tweet = "Gronk makes history: 1st player to have multiple games of 3 or more receiving TDs @RobGronkowski #crazyfootballmomma  @NFL 🔥🏈🔥🏈 #ballout"

      val expected = Set(
        "gronk makes",
        "makes history",
        "multiple games",
        "receiving tds"
      )

      val actual = LangBundle.en.termBigrams(tweet)

      info(s"tweet = ${tweet}")
      info(s"termBigrams = ${actual}")

      assert(actual === expected)
    }
  }
}
