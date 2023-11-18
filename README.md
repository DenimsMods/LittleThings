# LittleThings

A little library with little things for a large game. Offers a handful of utilities to aid development in useful (or
useless) ways. This includes static analysis annotations, easy-to-use abstractions, and more.

## Features

### [@NotNullEverything](src/main/java/dev/denimred/littlethings/annotations/NotNullEverything.java)

An annotation that can be applied to entire classes or packages to assume *everything* is not null by default. Or,
y'know, you could just use [Kotlin](https://kotlinlang.org/) if you're into that.

Does nothing at runtime, like most other static analysis annotations.

### [@Resource](src/main/java/dev/denimred/littlethings/annotations/Resource.java) (and variants)

An annotation applicable to strings that indicates the provided string must be part of a valid resource location. Handy
in some cases, but only offers static analysis checking and doesn't do anything at runtime.

Offers namespace-specific and path-specific variants.

## Usage

The project is currently not hosted on a maven repo anywhere, so you will either need to build it locally or use
something like [JitPack](https://www.jitpack.io/) to access a usable dependency.

LittleThings is meant to be directly included into projects that use it. This can be done by using
the [shadow plugin](https://imperceptiblethoughts.com/shadow/introduction/) and relocating the package, or some other
means (e.g. [Jar-in-Jar](https://forge.gemwire.uk/wiki/Jar-in-Jar)).

Alternatively, you can simply copy/paste code from this repo into projects that use it. Just be sure to relocate the
packages to prevent duplicate classpath dangers.

Also keep in mind the project's [MIT license](LICENSE).