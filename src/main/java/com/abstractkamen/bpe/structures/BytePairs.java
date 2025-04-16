package com.abstractkamen.bpe.structures;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class BytePairs extends ArrayList<BytePair> {

    private int charSetSize = 0;
    private final Charset charset;

    public BytePairs(Charset charset) {
        this.charset = charset;
        final CharsetEncoder encoder = charset.newEncoder();

        for (char c = 0; c < Character.MAX_VALUE; c++) {
            if (encoder.canEncode(c)) {
                add(new BytePair(c, -1));
                charSetSize++;
            }
        }
    }

    public byte[] getNewEncodedPairs() {
        return stream().skip(getCharSetSize()).map(BytePair::toSmallString)
                .collect(Collectors.joining())
                .getBytes(charset);
    }

    public int getCharSetSize() {
        return charSetSize;
    }

    public Charset getCharset() {
        return charset;
    }

    @Override
    public String toString() {
        return stream().map(BytePair::toSmallString).collect(Collectors.joining(", ", "[", "["));
    }
}
