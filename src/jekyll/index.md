---
layout: page
title: A little text processing library
---

This is lib-text, a little text processing library. It supports language
identification, tokenization, stopword filtering and provides some
useful helper functions. The tokenization has been tuned to work
well with text conventions commonly used in social media such as
Twitter, and supports URLs, emoji, hashtags, emails and @-mentions
cleanly. Stopword filtering is currently supported for

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

With more to come.

## Usage

Add to your build.sbt file:

    resolvers += "peoplepattern" at "https://dl.bintray.com/peoplepattern/maven/"

    libraryDependencies += "com.peoplepattern" %% "lib-text" % "0.3"

## Example

    import com.peoplepattern.text.Implicits._

    val txt = "Did you get your personalised print with your copy of #MadeintheAM on Black Friday? If not, there's still time! http://www.myplaydirect.com/one-direction"

    txt.lang
    // Some(en)

    txt.tokens
    // Vector(Did, you, get, your, personalised, print, with, your, copy, of, #MadeintheAM, on, Black, Friday, ?, If, not, ,, there's, still, time, !, http://www.myplaydirect.com/one-direction)

    txt.terms
    // Set(print, personalised, black, copy, friday, time)

    txt.termsPlus
    // Set(print, personalised, black, #madeintheam, copy, friday, time)

    txt.termBigrams
    // Set(black friday, personalised print)


## Scaladoc

Full API docs are available, for each published version:

- [v0.3]({{ site.baseurl }}/v0.3/api)
- [v0.2]({{ site.baseurl }}/v0.2/api)
- [v0.1]({{ site.baseurl }}/v0.1/api)


Developed with ❤️ at [People Pattern Corporation](https://peoplepattern.com)

[![People Pattern logo]({{ site.baseurl }}/public/pp.png)](https://peoplepattern.com)
