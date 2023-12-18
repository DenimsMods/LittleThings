# TODO Thing

The littlest of Little Things.
Introduces a simple `TODO` function that always throws an exception.
This was inspired by a feature in Kotlin, and this Thing makes it available for Java as well.

It will not add a TODO entry in your IDE, but it still has value.
It abuses generic types to allow it to satisfy any and all type requirements since it never actually returns.
`@Contract` annotations are also present to inform static analysis tools that the function will always fail.

Since it will cause runtime errors by design, I recommend using a `compileOnly` dependency at most.
This Thing has no value in production code, and should never be expected to be present at runtime outside of
development.

...I had to carefully avoid using the word "todo" in any way when writing the JavaDocs since that would cause a TODO
entry to appear in my IDE...
