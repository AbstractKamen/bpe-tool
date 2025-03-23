package com.abstractkamen.bpe;

import com.abstractkamen.bpe.structures.BytePair;
import com.abstractkamen.bpe.structures.IntList;
import com.abstractkamen.bpe.visitor.BpeIterationVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BpeCompress {

  private final BpeIterationVisitor visitor;
  private final Map<BytePair, Integer> frequencies;
  private final List<BytePair> pairs;
  private IntList compressedTokens;


  public BpeCompress() {
    this(EMPTY_VISITOR);
  }

  public BpeCompress(BpeIterationVisitor visitor) {
    this.visitor = visitor;
    this.frequencies = new HashMap<>();
    this.pairs = initPairsAscii();
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

  public List<BytePair> getPairs() {
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
    tokensOut.add(prevToken);
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

  private static List<BytePair> initPairsAscii() {
    final int asciiCapacity = Byte.MAX_VALUE - Byte.MIN_VALUE;
    final List<BytePair> asciiPairs = new ArrayList<>(asciiCapacity); ;
    for (int i = 0; i < asciiCapacity; ++i) {
      asciiPairs.add(i, new BytePair(i, -1));
    }
    return asciiPairs;
  }

  // @formatter:off
  public static final BpeIterationVisitor EMPTY_VISITOR = new BpeIterationVisitor() {
    public void visitIterationStart(int iteration, IntList tokensIn, List<BytePair> pairs, Map<BytePair, Integer> frequencies) {}
    public void visitPairFrequenciesFound(int iteration, IntList tokensIn, List<BytePair> pairs, Map<BytePair, Integer> frequencies, BytePair maxPair, Integer maxFrequency) {}
    public void visitMaxCompressionAchieved(int iteration, IntList tokensIn, List<BytePair> pairs, Map<BytePair, Integer> frequencies) {}
    public void visitIterationEnd(int iteration, IntList tokensIn, IntList tokensOut, List<BytePair> pairs, Map<BytePair, Integer> frequencies) {}
  };
  // @formatter:on
}
