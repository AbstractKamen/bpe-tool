package com.abstractkamen.bpe.algo;

import com.abstractkamen.bpe.structures.BytePair;
import com.abstractkamen.bpe.structures.BytePairs;
import com.abstractkamen.bpe.structures.IntList;
import com.abstractkamen.bpe.visitor.BpeIterationVisitor;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class BpeCompressor {

    private final BpeIterationVisitor visitor;
    private final Map<BytePair, Integer> frequencies;
    private final BytePairs pairs;
    private IntList compressedTokens;


    public BpeCompressor() {
        this(BpeIterationVisitor.EMPTY_VISITOR, "UTF-8");
    }

    public BpeCompressor(BpeIterationVisitor visitor, String charSet) {
        this.visitor = visitor;
        this.frequencies = new HashMap<>();
        this.pairs = new BytePairs(Charset.forName(charSet));
    }

    public void compressTokens(IntList tokensIn, int maxIterations) {

        IntList tokensOut = tokensIn;
        int i = 1;
        while (true) {
            if (maxIterations == 0) break;
            frequencies.clear();
            visitor.visitIterationStart(i, tokensIn, pairs, frequencies);
            final BytePair maxPair = getMaxPair(tokensIn, frequencies);

            assert maxPair != null;
            final Integer maxFrequency = frequencies.get(maxPair);
            visitor.visitPairFrequenciesFound(i, tokensIn, pairs, frequencies, maxPair, maxFrequency);
            if (maxFrequency == null || maxFrequency == 1) {
                visitor.visitMaxCompressionAchieved(i, tokensIn, pairs, frequencies);
                break;
            }
            final int maxTokenIndex = pairs.size();
            pairs.add(maxPair);
            tokensOut = replaceMaxPairToken(tokensIn, tokensOut, maxPair, maxTokenIndex);
            visitor.visitIterationEnd(i, tokensIn, tokensOut, pairs, frequencies);

            IntList temp = tokensIn;
            tokensIn = tokensOut;
            tokensOut = temp;
            maxIterations--;
            i++;
        }

        this.compressedTokens = tokensOut;
    }

    public IntList getCompressedTokens() {
        return compressedTokens;
    }

    public Map<BytePair, Integer> getFrequencies() {
        return frequencies;
    }

    public BytePairs getPairs() {
        return pairs;
    }

    private static IntList replaceMaxPairToken(IntList tokensIn, IntList tokensOut, BytePair maxPair, int maxTokenIndex) {
        if (tokensOut == null || tokensOut == tokensIn) {
            tokensOut = new IntList();
        } else {
            tokensOut.clear();
        }
        int token, prevToken = -1;
        for (int i = 0; i < tokensIn.size(); ++i) {
            token = tokensIn.get(i);
            if (prevToken != -1) {
                if (maxPair.matches(prevToken, token)) {
                    tokensOut.add(maxTokenIndex);
                    token = tokensIn.get(++i);
                } else {
                    tokensOut.add(prevToken);
                }
            }
            prevToken = token;
        }
        if (prevToken != -1) tokensOut.add(prevToken);
        return tokensOut;
    }

    private static BytePair getMaxPair(IntList tokens, Map<BytePair, Integer> frequencies) {
        BytePair maxPair = null;
        int maxPairFreq = -1;
        int token, prevToken = -1;
        for (int i = 0; i < tokens.size(); ++i) {
            token = tokens.get(i);
            if (prevToken != -1) {
                final BytePair bytePair = new BytePair(prevToken, token);
                final int freq = frequencies.compute(bytePair, (_, oldV) -> oldV == null ? 1 : oldV + 1);
                if (maxPairFreq < freq) {
                    maxPair = bytePair;
                    maxPairFreq = freq;
                }
            }
            prevToken = token;
        }
        return maxPair;
    }
}
