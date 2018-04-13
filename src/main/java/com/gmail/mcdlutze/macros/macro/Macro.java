package com.gmail.mcdlutze.macros.macro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Macro {
    private String name;
    private List<String> lines;
    // TODO add isHard

    public Macro(String name) {
        this.name = name;
        this.lines = new ArrayList<>();
    }

    public Macro(String name, List<String> lines) {
        this(name);
        this.lines = lines;
    }

    public String getName() {
        return name;
    }

    public void addLine(String line) {
        lines.add(line);
    }

    public void editLine(int line, String edit) {
        lines.set(line, edit);
    }

    public void insertLine(int line, String text) {
        lines.add(line, text);
    }

    public void removeLine(int line) {
        lines.remove(line);
    }

    public void rename(String name) {
        this.name = name;
    }

    public Macro copy() {
        Macro copy = new Macro(name);
        copy.lines = new ArrayList<>(lines);
        return copy;
    }

    public int length() {
        return lines.size();
    }

    public String getLine(int line) {
        return lines.get(line);
    }

    public List<String> getLines() {
        return Collections.unmodifiableList(lines);
    }

    public List<String> getFilledLines(String[] args) {
        return lines.stream().map(s -> fillArgs(s, args)).collect(Collectors.toList());
    }

    private String fillArgs(String line, String[] macroArgs) {
        StringBuffer filled = new StringBuffer();
        Pattern pattern = Pattern.compile("\\{(\\d+)}");
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            try {
                int index = Integer.parseInt(matcher.group(1));
                String macroArg = macroArgs[index];
                matcher.appendReplacement(filled, macroArg);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                // swallow
            }
        }
        matcher.appendTail(filled);
        return filled.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Macro macro = (Macro) o;
        return Objects.equals(name, macro.name) &&
                Objects.equals(lines, macro.lines);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, lines);
    }
}
