package com.abstractkamen.bpe.visitor;

import com.abstractkamen.bpe.structures.BPFreq;
import com.abstractkamen.bpe.structures.BytePair;
import com.abstractkamen.bpe.Print;
import com.abstractkamen.bpe.structures.IntList;

import java.util.List;
import java.util.Map;

public class BpeStdLoggingVisitor implements BpeIterationVisitor {

  @Override
  public void visitIterationStart(int iteration, IntList tokensIn, List<BytePair> pairs, Map<Integer, BPFreq> frequencies) {
    System.out.printf("%nSTART ITERATION %d *****************************%n", iteration);
  }

  @Override
  public void visitPairFrequenciesFound(int iteration, IntList tokensIn, List<BytePair> pairs, Map<Integer, BPFreq> frequencies, BytePair maxPair, Integer maxFrequency) {
    System.out.printf("Most frequent pair = %s %d%n", maxPair, maxFrequency);
  }

  @Override
  public void visitMaxCompressionAchieved(int iteration, IntList tokensIn, List<BytePair> pairs, Map<Integer, BPFreq> frequencies) {
    System.out.println("Cannot compress any further, breaking...");
  }

  @Override
  public void visitIterationEnd(int iteration, IntList tokensIn, IntList tokensOut, List<BytePair> pairs, Map<Integer, BPFreq> frequencies) {
    Print.showTokens(pairs, tokensIn);
    System.out.println("    *********************\n");
    Print.showTokens(pairs, tokensOut);
    System.out.printf("Compressed[%s] from input[%s]%n", Print.humanReadableSize(tokensOut.size()), Print.humanReadableSize(tokensIn.size()));
    System.out.printf("END ITERATION %d*****************************%n", iteration);
  }
}
