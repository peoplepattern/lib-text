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


## License

lib-text is open source and licensed under the [Apache License 2.0](LICENSE.txt).

## Acknowledgements

Developed with love at [People Pattern Corporation](https://peoplepattern.com)

[![People Pattern logo](pp.png)](https://peoplepattern.com)
