package com.gmail.mcdlutze.macros;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class MacroSet extends HashMap<String, List<String>>implements MetadataValue {

	private static final long serialVersionUID = -6271478335621885519L;

	public boolean asBoolean() {
		return isEmpty();
	}

	public byte asByte() {
		return (byte) (isEmpty() ? 1 : 0);
	}

	public double asDouble() {
		return isEmpty() ? 1 : 0;
	}

	public float asFloat() {
		return isEmpty() ? 1 : 0;
	}

	public int asInt() {
		return isEmpty() ? 1 : 0;
	}

	public long asLong() {
		return isEmpty() ? 1 : 0;
	}

	public short asShort() {
		return (short) (isEmpty() ? 1 : 0);
	}

	public String asString() {
		StringBuilder sb = new StringBuilder();
		boolean firstLine = true;
		for (String name : keySet()) {
			if (!firstLine) {
				sb.append('\n');
			}
			sb.append(name);
			firstLine = false;
		}
		return sb.toString();
	}

	public Plugin getOwningPlugin() {
		return Main.main;
	}

	public void invalidate() {
	}

	public Object value() {
		return this;
	}

	public void newMacro(String name) {
		put(name, new LinkedList<String>());
	}

	public void newMacro(String name, String line) {
		newMacro(name);
		addLine(name, line);
	}

	public void addLine(String macro, String line) {
		get(macro).add(line);
	}

	public int editLine(String macro, int line, String edit) {
		List<String> lines = get(macro);
		if (edit.isEmpty()) {
			if (line < lines.size()) {
				lines.remove(line);
				return line;
			} else {
				return lines.size();
			}
		} else {
			if (line < lines.size()) {
				lines.set(line, edit);
				return line;
			} else {
				lines.add(edit);
				return lines.size() - 1;
			}
		}
	}
}
