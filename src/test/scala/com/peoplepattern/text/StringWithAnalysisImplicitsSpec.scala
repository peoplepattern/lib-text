package com.peoplepattern.text

// ScalaTest
import org.scalatest._
import com.peoplepattern.text.Implicits._

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

  describe("str.lang") {
    it("should predict language from text") {
      val tweet = "Gronk makes history: 1st player to have multiple games of 3 or more receiving TDs @RobGronkowski #crazyfootballmomma  @NFL 🔥🏈🔥🏈 #ballout"
      assert(tweet.lang == Some("en"))
    }
  }
}
