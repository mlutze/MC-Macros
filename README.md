# Minecraft Macros

##### A simple Bukkit plugin for minecraft chat macros


## Overview

It is not uncommon for a player to want to be able to send the same message or command, or set of messages or commands, rather frequently. This plugin allows for the creation of macros, short commands that send longer chains of messages or commands automatically.

## Commands

The plugin technically only includes one command (as well as one shortcut), but has several sub-commands:

- `/macro help [{sub-command}]`
  - Display help information on a particular sub-command, or general help information if none is given.
- `/macro new {macro name} [{text}]`
  - Create a new macro with the given name and optional first line.
- `/macro add {macro name} {text}`
  - Add the given line to the given macro.
- `/macro edit {macro name} {line} {text}`
  - Replace the line in the macro with the given text.
  - Hitting tab after the line variable will automatically fill the chatbar with the line text.
- `/macro insert {macro name} {line} {text}`
  - Insert the given line into the macro.
- `/macro remove {macro name} {line}`
  - Remove the given line from the macro.
- `/macro delete {macro name}`
  - Delete the given macro.
- `/macro rename {old name} {new name}`
  - Rename the given macro with the given new name.
- `/macro copy {macro name} {copy name}`
  - Copy the given macro to a new macro with the given name.
- `/macro dictate {macro name}
  - Create a new macro with the given name and enter dicatation mode.
  - While in dictation mode, any commands or messages typed will be captured and added to the macro.
  - Type `//` to exit dictation mode.
- `/macro list`
  - List your macros.
- `/macro view {macro name}`
  - View the contents of a particular macro.
- `/macro run {macro name} [{arguments}]`
  - Run you macro with the given arguments.
  - This command can be executed shorthand with `/mr {macro name} [{arguments}]`

## Macro Arguments

The macros you create can allow for arguments for versitility. Any text in the macro of the form `{n}`, where `n` is an integer, will be replaced by the text given in the corresponding argument when the macro is run.

## Example Usage

###### Input:
```
/macro new test This is the {0} test of the test macro.
/macro run test first
```
###### Output:
```
This is the first test of the test macro.
```

###### Input:
```
/macro new joke
/macro add joke /op {0}
/macro add joke {0} is now an operator!
/macro add joke /deop {0}
/macro add joke {0} is no longer an operator!
/macro insert joke 0 {1}
/macro add joke {1}
/mr joke Briefly_Elated HAHAHA
```
###### Output:
```
HAHAHA
/op Briefly_Elated
Briefly_Elated is now an operator!
/deop Briefly_Elated
Briefly_Elated is no longer an operator!
HAHAHA
```

###### Input:
```
/macro dictate dictation
Hello, everyone.
I am currently dictating.
No one can see this until I run the macro.
/deop {0}
That command will not execute until I run the macro.
Sorry, {0}.
//
/mr dictation Sad_Larry
```

###### Output:
```
Hello, everyone.
I am currently dictating.
No one can see this until I run the macro.
/deop Sad_Larry
That command will not execute until I run the macro.
Sorry, Sad_Larry.
```
