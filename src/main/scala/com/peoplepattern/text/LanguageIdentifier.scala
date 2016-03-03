package com.peoplepattern.text

import com.carrotsearch.labs.langid.LangIdV3
import com.carrotsearch.labs.langid.Model

import scala.collection.JavaConverters._
import scala.collection.mutable

// Typesafe config
import com.typesafe.config.ConfigFactory

trait LanguageIdentifier {

  import LanguageIdentifier.{ defaultThreshold, defaultFrequency, defaultMinTextSize }

  /**
   * Given a list of strings, returns an ordered list of unique identified
   * languages using ISO-639-1 langauge code.
   *
   * This is built to classify many text entries, with the assumption that
   * we only neeed to knowabout the most common langagues in the texts.
   *
   * Change threshold and frequency to deal with outlier data. Increasing
   * threshold increases the confidence of identified languages, while
   * increasing frequency reduces impact of minor second language usage.
   *
   * @param texts the texts to classify and summarize
   * @param threshold the
   *
   * @return Vector of 3-tuples (lang-code, avg-lang-classification-score, frequency)
   */
  def summarize(
    texts: TraversableOnce[String],
    threshold: Double,
    frequency: Double,
    minTextSize: Int): Vector[(String, Double, Double)]

  /**
   * Given a list of strings, returns an ordered list of unique identified
   * languages using ISO-639-1 langauge code.
   *
   * This is built to classify many text entries, with the assumption that
   * we only neeed to knowabout the most common langagues in the texts.
   *
   * Change threshold and frequency to deal with outlier data. Increasing
   * threshold increases the confidence of identified languages, while
   * increasing frequency reduces impact of minor second language usage.
   *
   * @return Vector of 3-tuples (lang-code, avg-lang-classification-score, frequency)
   */
  def summarize(texts: TraversableOnce[String]): Vector[(String, Double, Double)] =
    summarize(texts, defaultThreshold, defaultFrequency, defaultMinTextSize)

  /**
   * Classify an indivitual text for the language of the text
   *
   * @param text the text to classify
   * @param threshold the minimum score threshold to consider a valid prediction
   * @param minTextSize the minimum length for the text to make a prediction
   * @return a pair of code (ISO-639-1 langauge code) and prediction score
   *         if a prediction could be made, otherwise None
   */
  def classify(
    text: String,
    threshold: Double,
    minTextSize: Int): Option[(String, Double)]

  /**
   * Classify an indivitual text for the language of the text
   *
   * This method should use sensible defaults for the `threshold` and
   * `minTextScore` parameters
   *
   * @return a pair of code (ISO-639-1 langauge code) and prediction score
   *         if a prediction could be made, otherwise None
   */
  def classify(text: String): Option[(String, Double)] =
    classify(text, defaultThreshold, defaultMinTextSize)
}

@SerialVersionUID(1)
object LanguageIdentifier extends LanguageIdentifier with Serializable {

  private val config = ConfigFactory.load

  lazy val defaultThreshold = config.getDouble("lang.classify.default_threshold")
  lazy val defaultFrequency = config.getDouble("lang.classify.default_frequency")
  lazy val defaultMinTextSize = config.getInt("lang.classify.min_text_size")

  @transient lazy val model = Model.defaultModel

  def summarize(
    texts: TraversableOnce[String],
    threshold: Double,
    frequency: Double,
    minTextSize: Int): Vector[(String, Double, Double)] = {

    val rollup = mutable.HashMap.empty[String, List[Double]].withDefaultValue(List.empty)

    val preds = texts.filter(_.size > 10).map { x =>
      classify(x, threshold, minTextSize)
    }

    for {
      pred <- preds
      (lang, score) <- pred
    } rollup(lang) = score :: rollup(lang)

    val minCount = math.max(1, texts.size * frequency)

    val filteredSummedRollup = for {
      (key, vals) <- rollup if vals.size >= minCount
    } yield (key, vals.sum / vals.size, vals.size.toDouble / texts.size)

    filteredSummedRollup.toVector.sortBy(-_._3)
  }

  def classify(
    text: String,
    threshold: Double,
    minTextSize: Int): Option[(String, Double)] = {

    val normalizedText = compress(removeNonLanguage(text))
    if (normalizedText.size > minTextSize) {

      // If the threshold is 0; give raw results, don't normalize, save time.
      if (threshold == 0.0) {
        val pred = new LangIdV3(model).classify(normalizedText, false)
        Some((pred.langCode, pred.confidence))
      } else {

        val langid = new LangIdV3(model)
        langid.reset()
        langid.append(normalizedText)

        // Choose the top-ranking language above the threshold level.
        // Note: choosing a high threshold means fallback probably wont happen
        val validLanguages = langid.rank(true).asScala.filter(_.confidence >= threshold)

        if (validLanguages.length > 0) {
          val pred = validLanguages.maxBy(_.confidence)
          Some((pred.langCode, pred.confidence))
        } else None
      }
    } else None
  }

  /**
   * Remove @-mentions, hashtags and weblinks.
   */
  private def removeNonLanguage(text: String) = {
    var s = text.split("\\s+")
    s = s.filter(_.nonEmpty)
    s = s.filter(_.head.isLetter)
    s = s.filter(_.exists(_.isLetter))
    s = s.filter(_.size >= 2)
    s.mkString(" ")
  }

  /**
   * Take any threepeats or greater down to two characters.
   * E.g. looooooovvvvve to loovve
   */
  private def compress(text: String) =
    text.replaceAll("(.)\\1+", "$1$1").trim
}
