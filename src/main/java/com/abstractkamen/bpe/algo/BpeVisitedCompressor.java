package com.abstractkamen.bpe.algo;

import com.abstractkamen.bpe.structures.BPFreq;
import com.abstractkamen.bpe.structures.CompressionResult;
import com.abstractkamen.bpe.structures.IntList;
import com.abstractkamen.bpe.visitor.BpeCompressionVisitor;

public class BpeVisitedCompressor extends BpeCompressorAbstract implements BpeCompressor {

  private final BpeCompressionVisitor visitor;


  public BpeVisitedCompressor(BpeCompressionVisitor visitor) {
    this(visitor, "UTF-8");
  }

  public BpeVisitedCompressor(BpeCompressionVisitor visitor, String charSet) {
    super(charSet);
    this.visitor = visitor;
  }

  @Override
  public CompressionResult compressTokens(IntList tokensIn, int maxIterations) {

    IntList tokensOut = tokensIn;
    initFreq(tokensIn);
    int i = 1;
    while (true) {
      if (maxIterations == 0) break;
      visitor.visitIterationStart(i, tokensIn, pairs, frequencies);
      BPFreq maxPair = freqHeap.pop();

      frequencies.remove(maxPair.getBytePair().hashCode());

      final int maxFrequency = maxPair.getFrequency();
      visitor.visitPairFrequenciesFound(i, tokensIn, pairs, frequencies, maxPair.getBytePair(), maxFrequency);
      if (maxFrequency == 1) {
        visitor.visitMaxCompressionAchieved(i, tokensIn, pairs, frequencies);
        break;
      }
      final int maxTokenIndex = pairs.size();
      pairs.add(maxPair.getBytePair());
      tokensOut = replaceMaxPairToken(tokensIn, tokensOut, maxPair.getBytePair(), maxTokenIndex);
      visitor.visitIterationEnd(i, tokensIn, tokensOut, pairs, frequencies);

      IntList temp = tokensIn;
      tokensIn = tokensOut;
      tokensOut = temp;
      maxIterations--;
      i++;
    }

    return new CompressionResult(tokensOut, pairs);
  }

}