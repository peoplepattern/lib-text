package com.peoplepattern.text

import org.scalatest._
import java.io._

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

  it should "serialize OK" in {
    val original = LanguageIdentifier
    val bos = new ByteArrayOutputStream()
    val bytes = try {
      val oos = new ObjectOutputStream(bos)
      oos.writeObject(original)
      bos.toByteArray
    } finally {
      bos.close()
    }
    val bis = new ByteArrayInputStream(bytes)
    val unmarshalled = try {
      val ois = new ObjectInputStream(bis)
      ois.readObject.asInstanceOf[LanguageIdentifier]
    } finally {
      bis.close()
    }
    assert(original.classify(enTxt) == unmarshalled.classify(enTxt))
    assert(original.classify(frTxt) == unmarshalled.classify(frTxt))
  }
}
