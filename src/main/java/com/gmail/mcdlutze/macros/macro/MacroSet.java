package com.gmail.mcdlutze.macros.macro;

import java.util.*;

public class MacroSet {

    private final Map<String, Macro> macros = new HashMap<>();

    public boolean containsMacro(String name) {
        return macros.containsKey(name);
    }

    public Macro getMacro(String name) {
        return macros.get(name);
    }

    public void putMacro(String name, Macro macro) {
        macros.put(name, macro);
    }

    public void removeMacro(String name) {
        macros.remove(name);
    }

    public Set<String> names() {
        return macros.keySet();
    }

    public Collection<Macro> macros() {
        return macros.values();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MacroSet macroSet = (MacroSet) o;
        return Objects.equals(macros, macroSet.macros);
    }

    @Override
    public int hashCode() {
        return Objects.hash(macros);
    }
}
