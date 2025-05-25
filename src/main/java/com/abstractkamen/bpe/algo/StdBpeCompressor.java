package com.abstractkamen.bpe.algo;

import com.abstractkamen.bpe.structures.BPFreq;
import com.abstractkamen.bpe.structures.CompressionResult;
import com.abstractkamen.bpe.structures.IntList;

public class StdBpeCompressor extends BpeCompressorAbstract implements BpeCompressor {

  public StdBpeCompressor() {
    super("UTF-8");
  }

  public StdBpeCompressor(String charSet) {
    super(charSet);
  }

  @Override
  public CompressionResult compressTokens(IntList tokensIn, int maxIterations) {

    IntList tokensOut = tokensIn;
    initFreq(tokensIn);
    while (true) {
      if (maxIterations == 0) break;
      BPFreq maxPair = freqHeap.pop();

      frequencies.remove(maxPair.getBytePair().hashCode());

      final int maxFrequency = maxPair.getFrequency();
      if (maxFrequency == 1) break;
      final int maxTokenIndex = pairs.size();
      pairs.add(maxPair.getBytePair());
      tokensOut = replaceMaxPairToken(tokensIn, tokensOut, maxPair.getBytePair(), maxTokenIndex);

      IntList temp = tokensIn;
      tokensIn = tokensOut;
      tokensOut = temp;
      maxIterations--;
    }

    return new CompressionResult(tokensOut, pairs);
  }
}
