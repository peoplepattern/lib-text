package com.peoplepattern.text

import org.scalatest._

class JaLangBundleSpec extends FlatSpec {

  "JaLangBundle" should "tokenize basic text" in {
    val text = "お寿司が食べたい。"
    val expected = Vector("お", "寿司", "が", "食べ", "たい", "。")
    assert(JaLangBundle.tokens(text) == expected)
  }

  it should "handle empty strings" in {
    val text = ""
    val expected = Vector.empty[String]
    assert(JaLangBundle.tokens(text) == expected)
  }

  it should "handle western punctuation" in {
    val text = "お寿司が食べたい."
    val expected = Vector("お", "寿司", "が", "食べ", "たい", ".")
    assert(JaLangBundle.tokens(text) == expected)
  }

  it should "handle hashtags and URLs" in {
    val text = "スターバックス今年最後の限定ドリンクは、まるで”飲むデザート”？！http://bit.ly/1k464Fq #starbucks #deseart"
    val expected = Vector("スター", "バックス", "今年", "最後", "の", "限定", "ドリンク", "は", "、", "まるで", "”", "飲む", "デザート", "”", "？", "！", "http://bit.ly/1k464Fq", "#starbucks", "#deseart")
    assert(JaLangBundle.tokens(text) == expected)
  }

  it should "get Japanese hashtags" in {
    val text = "究極サンタサクヤちゃんの画面写真です！！！#パズドラ "
    val expected = Vector("究極", "サンタサクヤ", "ちゃん", "の", "画面", "写真", "です", "！", "！", "！", "#パズドラ")
    assert(JaLangBundle.tokens(text) == expected)
  }

  it should "handle a hashtag in the middle of a text" in {
    val text = "ブログ更新☆ 「第5回東京ガールギークディナー」で女性の役に立つお話を聞いてきたよ #TGGD: テック系女子のイベントで女性のキャリアや考え方に役立つお話を聞いてきましたよ。ふむふむ＆あるある！という感じでとても楽しい... http://bit.ly/16bZHC5 "
    val expected = Vector("ブログ", "更新", "☆", "「", "第", "5", "回", "東京", "ガールギークディナー", "」", "で", "女性", "の", "役に立つ", "お話", "を", "聞い", "て", "き", "た", "よ", "#TGGD", ":", "テック", "系", "女子", "の", "イベント", "で", "女性", "の", "キャリア", "や", "考え方", "に", "役立つ", "お話", "を", "聞い", "て", "き", "まし", "た", "よ", "。", "ふむふむ", "＆", "ある", "ある", "！", "という", "感じ", "で", "とても", "楽しい", "...", "http://bit.ly/16bZHC5")
    assert(JaLangBundle.tokens(text) == expected)
  }
}
