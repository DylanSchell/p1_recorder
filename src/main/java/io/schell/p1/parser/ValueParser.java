package io.schell.p1.parser;

public interface ValueParser<T> {
    T parse(String value);
}