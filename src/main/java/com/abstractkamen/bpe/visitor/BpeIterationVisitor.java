package com.abstractkamen.bpe.visitor;

import com.abstractkamen.bpe.structures.BytePair;
import com.abstractkamen.bpe.structures.IntList;

import java.util.List;
import java.util.Map;

public interface BpeIterationVisitor {
  void visitIterationStart(int iteration, IntList tokensIn, List<BytePair> pairs, Map<BytePair, Integer> frequencies);

  void visitPairFrequenciesFound(int iteration, IntList tokensIn, List<BytePair> pairs, Map<BytePair, Integer> frequencies, BytePair maxPair, Integer maxFrequency);

  void visitMaxCompressionAchieved(int iteration, IntList tokensIn, List<BytePair> pairs, Map<BytePair, Integer> frequencies);

  void visitIterationEnd(int iteration, IntList tokensIn, IntList tokensOut, List<BytePair> pairs, Map<BytePair, Integer> frequencies);

  // @formatter:off
  BpeIterationVisitor EMPTY_VISITOR = new BpeIterationVisitor() {
    public void visitIterationStart(int iteration, IntList tokensIn, List<BytePair> pairs, Map<BytePair, Integer> frequencies) {}
    public void visitPairFrequenciesFound(int iteration, IntList tokensIn, List<BytePair> pairs, Map<BytePair, Integer> frequencies, BytePair maxPair, Integer maxFrequency) {}
    public void visitMaxCompressionAchieved(int iteration, IntList tokensIn, List<BytePair> pairs, Map<BytePair, Integer> frequencies) {}
    public void visitIterationEnd(int iteration, IntList tokensIn, IntList tokensOut, List<BytePair> pairs, Map<BytePair, Integer> frequencies) {}
  };
  // @formatter:on
}
