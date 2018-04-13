package com.gmail.mcdlutze.macros.argument;

import java.util.Objects;

public class VerifiedArgument<T> {
    private static final VerifiedArgument<?> ABSENT = new VerifiedArgument<>(null, false, false);
    private static final VerifiedArgument<?> INVALID = new VerifiedArgument<>(null, false, true);

    private final T argument;
    private final boolean valid;
    private final boolean present;

    private VerifiedArgument(T argument, boolean valid, boolean present) {
        this.argument = argument;
        this.valid = valid;
        this.present = present;
    }

    public static <T> VerifiedArgument<T> of(T argument) {
        return new VerifiedArgument<>(argument, true, true);
    }

    @SuppressWarnings("unchecked")
    public static <T> VerifiedArgument<T> invalid() {
        return (VerifiedArgument<T>) INVALID;
    }

    @SuppressWarnings("unchecked")
    public static <T> VerifiedArgument<T> absent() {
        return (VerifiedArgument<T>) ABSENT;
    }

    public T get() {
        return argument;
    }

    public boolean isPresent() {
        return present;
    }

    public boolean isValid() {
        return valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VerifiedArgument<?> that = (VerifiedArgument<?>) o;
        return valid == that.valid &&
                present == that.present &&
                Objects.equals(argument, that.argument);
    }

    @Override
    public int hashCode() {
        return Objects.hash(argument, valid, present);
    }

}
