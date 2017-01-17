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
- `/macro remove {macro name}`
  - Delete the given macro.
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
/macro add joke {1}
/mr joke Briefly_Elated HAHAHA
```
###### Output:
```
/op Briefly_Elated
Briefly_Elated is now an operator!
/deop Briefly_Elated
Briefly_Elated is no longer an operator!
HAHAHA
```
