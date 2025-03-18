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

}
