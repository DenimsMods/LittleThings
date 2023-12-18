# Annotations Thing

### [@NbtType](src/main/java/dev/denimred/littlethings/annotations/NbtType.java)

A type-qualifying annotation that indicates the provided value must be a valid NBT type constant. Helps prevent the use
of simple integers over the constants available in `Tag` and verifies that the input is a valid type.

### [@NotNullEverything](src/main/java/dev/denimred/littlethings/annotations/NotNullEverything.java)

An annotation that can be applied to entire classes or packages to assume *everything* is not null by default. Or,
y'know, you could just use [Kotlin](https://kotlinlang.org/) if you're into that.

### [@Resource](src/main/java/dev/denimred/littlethings/annotations/Resource.java) (and variants)

A type-qualifying annotation applicable to strings that indicates the provided string must be part of a valid resource
location. Handy in some cases. Offers namespace-specific and path-specific variants.