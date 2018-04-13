package com.gmail.mcdlutze.macros.argument;

import org.junit.Test;

import static org.junit.Assert.*;

public class VerifiedArgumentTest {

    private VerifiedArgument<?> sut;

    @Test
    public void absentTest() {
        sut = VerifiedArgument.absent();
        assertFalse(sut.isPresent());
        assertFalse(sut.isValid());
        assertNull(sut.get());
    }

    @Test
    public void invalidTest() {
        sut = VerifiedArgument.invalid();
        assertTrue(sut.isPresent());
        assertFalse(sut.isValid());
        assertNull(sut.get());
    }

    @Test
    public void validTest() {
        sut = VerifiedArgument.of("valid");
        assertTrue(sut.isPresent());
        assertTrue(sut.isValid());
        assertEquals("valid", sut.get());
    }

}