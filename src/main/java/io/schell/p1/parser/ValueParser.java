package io.schell.p1.parser;

interface ValueParser<T> {
    T parse(String value);
}