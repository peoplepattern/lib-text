# lib-text

A little text processing library for Scala.

## Overview

This is a little text processing library which supports language
identification, tokenization, stopword filtering and provides some
useful helper functions. The tokenization has been tuned to work
well with text conventions commonly used in social media such as
Twitter, and supports URLs, emoji, hashtags, emails and @-mentions
cleanly. Stopword filtering is currently suppported for

- German
- English
- Spanish
- French
- Indonesian
- Japanese
- Malay
- Dutch
- Portuguese
- Swedish
- Turkish

More to come.

## Usage

Add to your project dependencies:

```scala
resolvers += Resolver.url("peoplepattern-levar", url("https://dl.bintray.com/peoplepattern/releases/"))(Resolver.ivyStylePatterns))

libraryDependencies += "com.peoplepattern" %% "lib-text" % VERSION
```
where VERSION = the current version (e.g. "0.1")

## Example

```scala
scala> import com.peoplepattern.text.Implicits._

scala> val txt = "Did you get your personalised print with your copy of #MadeintheAM on Black Friday? If not, there's still time! http://www.myplaydirect.com/one-direction"
txt: String = Did you get your personalised print with your copy of #MadeintheAM on Black Friday? If not, there's still time! http://www.myplaydirect.com/one-direction

scala> txt.lang
res1: Option[String] = Some(en)

scala> txt.tokens
res2: Vector[String] = Vector(Did, you, get, your, personalised, print, with, your, copy, of, #MadeintheAM, on, Black, Friday, ?, If, not, ,, there's, still, time, !, http://www.myplaydirect.com/one-direction)

scala> txt.terms
res3: Set[String] = Set(print, personalised, black, copy, friday, time)

scala> txt.termsPlus
res4: Set[String] = Set(print, personalised, black, #madeintheam, copy, friday, time)

scala> txt.termBigrams
res5: Set[String] = Set(black friday, personalised print)
```

## License

lib-text is open source and licensed under the [Apache License 2.0](LICENSE.txt).

## Acknowledgements

Developed with love at [People Pattern Corporation](https://peoplepattern.com)

[![People Pattern logo](pp.png)](https://peoplepattern.com)
