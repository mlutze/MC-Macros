# Minecraft Macros

##### A simple Bukkit plugin for Minecraft chat macros

## Overview

It is not uncommon for a player to want to be able to send the same message or command, or set of messages or commands, rather frequently. This plugin allows for the creation of macros, short commands that send longer chains of messages or commands automatically.

The plugin further allows creation of custom commands, termed *hard macros*, which perform the same function as regular macros, but are accessible as if they were their own standalone commands.

## Commands

The plugin includes several commands for creating and modifying macros, each prefixed with `macro`, and each with an alias prefixed with `m`. The following is a snippet of the `plugin.yml` file.

```yaml
commands:
   macroadd:
      description: Add an line to a macro. Use "{number}" to allow arguments.
      aliases: madd
      usage: /macroadd {macro name} {text}
      permission: Macros.user
   macrocopy:
      description: Copy a macro.
      aliases: mcopy
      usage: /macrocopy {macro name}, {copy name}
      permission: Macros.user
   macrodelete:
      description: Delete a macro.
      aliases: mdelete
      usage: /macrodelete {macro name}
      permission: Macros.user
   macrodictate:
      description: Begin recording a new macro through chat.
      aliases: mdictate
      usage: /macrodictate {macro name}
      permission: Macros.user
   macroedit:
      description: Edit a line in a macro.
      aliases: medit
      usage: /macroedit {macro name} {line} {text}
      permission: Macros.user
   macroharden:
      description: Allow use of a macro as a custom command.
      aliases: mharden
      usage: /macroharden {macro name}
      permission: Macros.user
   macroinsert:
      description: Insert a line into a macro.
      aliases: minsert
      usage: /macroinsert {macro name} {line} {text}
      permission: Macros.user
   macrolist:
      description: List your macros.
      aliases: mlist
      usage: /macrolist
      permission: Macros.user
   macronew:
      description: Create a new macro.
      aliases: mnew
      usage: /macronew {macro name} [{text}]
      permission: Macros.user
   macroremove:
      description: Remove a line from a macro.
      aliases: mremove
      usage: /macroremove {macro name} {line}
      permission: Macros.user
   macrorename:
      description: Rename a macro.
      aliases: mrename
      usage: /macrorename {macro name} {new name}
      permission: Macros.user
   macrorun:
      description: Run a macro.
      aliases: mrun
      usage: /macrorun {macro name} [{arguments}]
      permission: Macros.user
   macrosoften:
      description: Disallow use of a macro as a custom command.
      aliases: msoften
      usage: /macrosoften {macro name}
      permission: Macros.user
   macroview:
      description: View the contents of a macro.
      aliases: mview
      usage: /macroview {macro name} [{arguments}]
      permission: Macros.user
```

## Macro Arguments

The macros you create can allow for arguments for versatility. Any text in the macro of the form `{n}`, where `n` is an integer, will be replaced by the text given in the corresponding argument when the macro is run. An argument containing a space can be created by enclosing it in quotation marks, while an empty string can be represented by an empty pair of quotation marks (`""`).

###### Input:
```
/mnew myMacro
/madd myMacro First argument: {0}
/madd myMacro Second argument: {1}
/madd myMacro First and second arguments: {0} and {1}
/mrun myMacro cool "cool beans"
```

###### Output:
```
First argument: cool
Second argument: cool beans
First and second arguments: cool and cool beans
```

## Hard Macros

A macro can be hardened by use of the `macroharden` (`mharden`) command in order to make it accessible as if it were a command, rather than having to use the `macrorun` (`mrun`) command.

###### Input:
```
/mnew myMacro
/madd myMacro Running the macro.
/mharden myMacro
/myMacro
```

###### Output:
```
Running the macro.
```

## Dictation

Instead of repeating the `macroadd` (`madd`) command for each line of the macro, the `macrodictate` (`mdictate`) command can be used to create and add lines to a command fluently. Each line typed in chat is captured and added to the macro. Two forward slashes `//` indicate the end of dictation.

###### Input:
```
/mdictate myMacro
Hello. I'm going to run command "{0}"!
/{0}
//
/mrun myMacro ping
```

###### Output:
```
Hello. I'm going to run command "ping"!
/ping
```
