package com.gmail.mcdlutze.macros.macro;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MacroTest {

    private Macro sut;

    @Test
    public void successfulFillTest() {
        sut = new Macro("myMacro", Arrays.asList("1st arg: {0}", "2nd arg: {1} (more text)"), false);

        String[] args = "arg1 arg2".split(" ");

        List<String> expected = Arrays.asList("1st arg: arg1", "2nd arg: arg2 (more text)");
        List<String> actual = sut.getFilledLines(args);

        assertEquals(expected, actual);
    }

    @Test
    public void ignoreInvalidArgSpaceTest() {
        sut = new Macro("myMacro", Collections.singletonList("1st arg: {invalid}"), false);

        String[] args = "arg1 arg2".split(" ");

        List<String> expected = Collections.singletonList("1st arg: {invalid}");
        List<String> actual = sut.getFilledLines(args);

        assertEquals(expected, actual);
    }

    @Test
    public void ignoreInvalidOverflowArgSpaceTest() {
        sut = new Macro("myMacro", Collections.singletonList("1st arg: {999999999999999}"), false);

        String[] args = "arg1 arg2".split(" ");

        List<String> expected = Collections.singletonList("1st arg: {999999999999999}");
        List<String> actual = sut.getFilledLines(args);

        assertEquals(expected, actual);
    }

    @Test
    public void ignoreOutOfBoundsArgSpaceTest() {
        sut = new Macro("myMacro", Collections.singletonList("1st arg: {5}"), false);

        String[] args = "arg1 arg2".split(" ");

        List<String> expected = Collections.singletonList("1st arg: {5}");
        List<String> actual = sut.getFilledLines(args);

        assertEquals(expected, actual);
    }

}