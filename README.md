# lib-text

A little text processing library for Scala.

[![Build Status](https://travis-ci.org/peoplepattern/lib-text.svg?branch=master)](https://travis-ci.org/peoplepattern/lib-text)
[![Coverage Status](https://coveralls.io/repos/peoplepattern/lib-text/badge.svg?branch=master&service=github)](https://coveralls.io/github/peoplepattern/lib-text?branch=master)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/peoplepattern/lib-text?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

## Overview

This is a little text processing library which supports language
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

More to come.

## Usage

Add to your project dependencies:

```scala
resolvers += "peoplepattern" at "https://dl.bintray.com/peoplepattern/maven/"

libraryDependencies += "com.peoplepattern" %% "lib-text" % VERSION
```
where VERSION = the current version (e.g. "0.1")

## Example

```scala
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
```

## License

lib-text is open source and licensed under the [Apache License 2.0](LICENSE.txt).

## Acknowledgements

Developed with :heart: at [People Pattern Corporation](https://peoplepattern.com)

[![People Pattern logo](pp.png)](https://peoplepattern.com)
