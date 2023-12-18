# JSON Commands Thing

Data-driven JSON commands that can be created and modified by standard datapacks.

## Usage

To make use of JSON commands, you'll want to create an instance
of [`JsonCommandManager`](./src/main/java/dev/denimred/littlethings/commands/json/JsonCommandManager.java). The
constructor takes the
namespace to operate under, and optionally a `Gson` instance to handle the initial deserialization of the command
objects.

Since **command execution code cannot be defined in JSON**, you will want to register your command executables
via `setExecutable`. The method takes the executable's ID as well as the executable function itself. The executable's
name can either be a `String` or a `ResourceLocation`. If a string is used, the associated resource location will use
the `JsonCommandManager`'s namespace while the string itself will be the resource location's path.

Additionally, if you use custom or otherwise non-vanilla argument types, you can register their deserializers
via `setArgumentDeserializer`.

Once you've got a completed `JsonCommandManager` instance, you must attach it to the game so that it can read data and
register commands at appropriate times. As such, make sure to do **both** of the following:

1. Using your command registration event of choice, attach the `CommandDispatcher` to the manager via `setDispatcher`.
    * In Fabric, you would use the `CommandRegistrationCallback` event to do this.
2. Hook the manager into the datapack resource manager. `JsonCommandManager` is an instance
   of `PreparableReloadListener`, and so can be attached to the game like similar objects.
    * In Fabric, you would use `ResourceManagerHelper` to obtain the datapack resource manager, then register
      the `JsonCommandManager` via `registerReloadListener`.

Once both steps are complete, your `JsonCommandManager` instance should be fully functional and will begin reading
your `commands.json` whenever datapacks load/reload.

## `commands.json`

The `commands.json` file stores the actual JSON command data used by `JsonCommandManager`. It functions similarly to the
vanilla [`sounds.json`](https://minecraft.fandom.com/wiki/Sounds.json) and must be placed at the root of its associated
namespaced data folder.

### Datagen

[`JsonCommandProvider`](./src/main/java/dev/denimred/littlethings/commands/json/datagen/JsonCommandProvider.java) is
available if
you wish to define your `commands.json` in code through the vanilla data generation system.

This may seem pointless, but it's actually quite valuable as it lets datapacks override your command configuration to
change command names, permissions, etc. while still letting you have some measure of protection against bugs that can
occur from writing an invalid json.

### Kotlin

Like all other Little Things modules, JSON Commands makes use of `@NotNullEverything`, so Kotlin should be able to
properly determine nullability for Java interop without needing `!!` not-null assertions.

Also, a number of basic DSL-ish functions are available to make interop with Kotlin a bit nicer. This includes a few
type aliases for the vanilla command system, and some functions to turn the datagen builder into something more like a
DSL.

See the `kotlin` sources under the `main` source set for more information.

### Schema

The rough schema for `commands.json` is the following:

```json5
// data/testmod/commands.json
{
  // This is literal command you will execute, i.e. "/example"
  // Note that all command and argument names must work as a valid resource location path.
  // Keep in mind that the "root" command must be a literal; i.e. must not contain an 'argument' field.
  "example": {
    // This is the permission level required for a user to execute this command or any of its children.
    "level": 3,
    // ... You can use either an integer as above, or one of the following enum values:
    // ... level 0
    "level": "all",
    // ... level 1
    "level": "moderators",
    // ... level 2
    "level": "gamemasters",
    // ... level 3
    "level": "admins",
    // ... level 4
    "level": "owners",
    // The executable refers to the executable command you registered when creating your JsonCommandManager instance.
    // This is optional. If not defined, the command/argument will not be given a command executable.
    "executable": "testmod:example",
    // ... You can omit the namespace if it matches the namespace that the commands.json file is under.
    // This is different from most usages of resource locations in that it won't necessarily default to 'minecraft'.
    "executable": "example",
    // ... If the executable name matches the path of the command/argument, it can be replaced with a boolean.
    "executable": true,
    // ... Using false like this is pointless, just omit the field instead.
    "executable": false
    // (Technically this example would probably cause an exception as it redefines "executable" and "level" multiple times.)
  },
  // Here's another, slightly more complex, example.
  "another_example": {
    // Arguments are defined under an object, as their keys are used for their argument/literal names.
    "arguments": {
      // This is an example of an argument that has no argument type or parameters.
      // This is interpreted as a literal argument, similar to the root command, i.e. "/another_example literal_arg".
      "literal_arg": {
        // This points to the executable used in the first example.
        // A bit odd, but it works. Can be handy for oddly branching commands.
        // You can end up with confusing errors if you do this, so be sure you understand how it works.
        "executable": "example"
      },
      // Non-literal argument names will still appear when displaying command autocompletion.
      // I recommend using argument names that describe the purpose of the argument.
      "integer_arg": {
        // Here's a fully qualified executable name under a child of the root command.
        // Note the forward slash. The default executable name is effectively the path to the executing argument.
        // This particular example could be replaced with a boolean, as it matches the current argument's path.
        "executable": "testmod:another_example/integer_arg",
        // There's a number of argument types available, see below for all the default options.
        "type": "brigadier:integer",
        // Not all argument types have/require parameters.
        // In this case (for an integer argument type), the "min" and "max" parameters are available.
        "parameters": {
          // Some parameters are optional, others are not. Be sure to read the argument type's specification.
          "min": 0,
          // In this case, both of these parameters are optional and default to the min/max possible integer values, respectively.
          "max": 100
        }
      },
      // This is an argument that has no parameters, making it very simple to define.
      "simple_arg": {
        "executable": true,
        "type": "brigadier:bool"
      }
    }
  }
}
```

Here's a few examples that will result in exceptions during parsing/registration:

```json5
// data/testmod/commands.json
{
  // This command name isn't a valid resource location path. Note the uppercase letters and spaces.
  "BAD COMMAND NAME": {
    "executable": true
  },
  // This command's root contains an argument type, and is therefor non-literal.
  // Non-literal root commands are not allowed, and so this would result in an error during registration.
  "non_literal_root": {
    "type": "minecraft:block_pos"
  },
  // This command could cause a problem due to a misinterpretation of the schema.
  // Command executables, by default, are the path of the argument defining them.
  // However, you *cannot* reference another command's executable by simply pointing to its path.
  // In this example, "bad_arg" will cause an exception during parsing, as its executable doesn't exist.
  "missing_executable": {
    "arguments": {
      "good_arg": {
        "executable": "some_valid_executable"
      },
      "bad_arg": {
        "executable": "missing_executable/good_arg"
      }
    }
  }
}
```

### Argument Types

There's quite a lot of argument types available by default. Here's a list of all of them along with their parameters.

Also, since argument types use normal resource location parsing, you can omit the namespace if it's `minecraft`. For
example, instead of using `minecraft:block_state`, you could simply use `block_state`.

* `brigadier:bool`
* `brigadier:float`
    * `min` Optional, defaults to `-Float.MAX_VALUE`
    * `max` Optional, defaults to `Float.MAX_VALUE`
* `brigadier:double`
    * `min` Optional, defaults to `-Double.MAX_VALUE`
    * `max` Optional, defaults to `Double.MAX_VALUE`
* `brigadier:integer`
    * `min` Optional, defaults to `Integer.MIN_VALUE`
    * `max` Optional, defaults to `Integer.MAX_VALUE`
* `brigadier:long`
    * `min` Optional, defaults to `Long.MIN_VALUE`
    * `max` Optional, defaults to `Long.MAX_VALUE`
* `brigadier:string`
    * `type` Optional, defaults to `greedy`
        * `word` Consumes a single word.
        * `phrase` Consumes either a single word, or a string delimited by quotes.
        * `greedy` Consumes the rest of the command input as an unquoted string.
* `minecraft:entity`
    * `amount` Required
        * `single` Targets a single entity.
        * `multiple` Targets multiple entities.
    * `type` Required
        * `players` Targets only players.
        * `entities` Targets all types of entities.
* `minecraft:game_profile`
* `minecraft:block_pos`
* `minecraft:column_pos`
* `minecraft:vec3`
* `minecraft:vec2`
* `minecraft:block_state`
* `minecraft:block_predicate`
* `minecraft:item_stack`
* `minecraft:item_predicate`
* `minecraft:color`
* `minecraft:component`
* `minecraft:message`
* `minecraft:nbt_compound_tag`
* `minecraft:nbt_tag`
* `minecraft:nbt_path`
* `minecraft:objective`
* `minecraft:objective_criteria`
* `minecraft:operation`
* `minecraft:particle`
* `minecraft:angle`
* `minecraft:rotation`
* `minecraft:scoreboard_slot`
* `minecraft:score_holder`
    * `amount` Required
        * `single` Targets a single score holder.
        * `multiple` Targets a multiple score holders.
* `minecraft:swizzle`
* `minecraft:team`
* `minecraft:item_slot`
* `minecraft:resource_location`
* `minecraft:mob_effect`
* `minecraft:function`
* `minecraft:entity_anchor`
* `minecraft:int_range`
* `minecraft:float_range`
* `minecraft:item_enchantment`
* `minecraft:entity_summon`
* `minecraft:dimension`
* `minecraft:time`
* `minecraft:uuid`
* `minecraft:resource`
    * `registry` Required, defines the registry under which the resources can be selected from.
* `minecraft:resource_or_tag`
    * `registry` Required, defines the registry under which the resources or tags can be selected from.