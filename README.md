# LittleThings

A little library with little things for a large game. Offers a handful of utilities to aid development in useful (or
useless) ways. This includes static analysis annotations, easy-to-use abstractions, and more.

## Features

### Static Analysis Annotations

#### [@NbtType](src/main/java/dev/denimred/littlethings/annotations/NbtType.java)

A type-qualifying annotation that indicates the provided value must be a valid NBT type constant. Helps prevent the use
of simple integers over the constants available in `Tag` and verifies that the input is a valid type.

#### [@NotNullEverything](src/main/java/dev/denimred/littlethings/annotations/NotNullEverything.java)

An annotation that can be applied to entire classes or packages to assume *everything* is not null by default. Or,
y'know, you could just use [Kotlin](https://kotlinlang.org/) if you're into that.

#### [@Resource](src/main/java/dev/denimred/littlethings/annotations/Resource.java) (and variants)

A type-qualifying annotation applicable to strings that indicates the provided string must be part of a valid resource
location. Handy in some cases. Offers namespace-specific and path-specific variants.

### Miscellaneous Little Things

#### [Facets](src/main/java/dev/denimred/littlethings/facet/Facet.java)

An abstraction around item stack NBT data that includes type checking and helps reduce bugs caused by typos.

[//]: # (TODO: Describe Facets with more detail)

#### [TODO Function and Error](src/main/java/dev/denimred/littlethings/misc/Todo.java)

A Java version of [Kotlin's `TODO` function](https://kotlinlang.org/docs/idioms.html#mark-code-as-incomplete-todo),
sorta. Will always throw a special `Todo` error, optionally with a message attached. Unfortunately does not create a
todo entry in the IDE. Still, might be handy for quick testing or iteration.

## Usage

The project is currently not hosted on a maven repo anywhere, so you will either need to build it locally or use
something like [JitPack](https://www.jitpack.io/) to access a usable dependency.

LittleThings is meant to be directly included into projects that use it. This can be done by using
the [shadow plugin](https://imperceptiblethoughts.com/shadow/introduction/) and relocating the package, or some other
means (e.g. [Jar-in-Jar](https://forge.gemwire.uk/wiki/Jar-in-Jar)).

Alternatively, you can simply copy/paste code from this repo into projects that use it. Just be sure to relocate the
packages to prevent duplicate classpath dangers.

Also keep in mind the project's [MIT license](LICENSE).