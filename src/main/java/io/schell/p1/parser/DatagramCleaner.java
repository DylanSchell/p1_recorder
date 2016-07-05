package io.schell.p1.parser;

class DatagramCleaner {

    static String[] asArray(String source) {
        source = source.replaceAll("\\r", "");
        return source.split("\\n");
    }
}