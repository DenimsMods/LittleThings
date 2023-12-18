# LittleThings

A little library with little things for a large game. Offers a handful of utilities to aid development in useful (or
useless) ways. This includes static analysis annotations, easy-to-use abstractions, and more.

## Things

* ### [Annotations Thing](./things/annotations)
* ### [Facets Thing](./things/facets)
* ### [JSON Commands Thing](./things/json-commands)
* ### [TODO Thing](./things/todo)

## Usage

The project is currently not hosted on a maven repo anywhere, so you will either need to build it locally or use
something like [JitPack](https://www.jitpack.io/) to access a usable dependency.

Little Things modules are meant to be directly included into projects that use it. This can be done by using
the [shadow plugin](https://imperceptiblethoughts.com/shadow/introduction/) and relocating the package, or some other
means (e.g. [Jar-in-Jar](https://forge.gemwire.uk/wiki/Jar-in-Jar)). If you're using Loom or one of its derivatives,
the `include` configuration may be of use.

Alternatively, you can simply copy/paste code from this repo into projects that use it. Just be sure to relocate the
packages to prevent duplicate classpath dangers.

Also keep in mind the project's [MIT license](./LICENSE).
