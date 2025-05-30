package com.abstractkamen.bpe.algo;

import com.abstractkamen.bpe.structures.BytePair;
import com.abstractkamen.bpe.structures.IntList;

import java.util.List;

public class BpeDecompressor {

  private IntList decompressedTokens;

  public void decompressTokens(IntList tokensIn, List<BytePair> pairs) {
    this.decompressedTokens = tokensIn;
    IntList tokensOut = tokensIn;
    BytePair maxPair;
    int maxPairIndex = pairs.size() - 1;
    while (maxPairIndex > 0 && (maxPair = pairs.get(maxPairIndex)).right > -1) {
      this.decompressedTokens = replaceMaxPairToken(tokensIn, tokensOut, maxPair, maxPairIndex--);
      IntList temp = tokensIn;
      tokensOut = decompressedTokens;
      tokensIn = tokensOut;
      tokensOut = temp;
    }
  }

  private static IntList replaceMaxPairToken(IntList tokensIn, IntList tokensOut, BytePair maxPair, int maxPairIndex) {
    if (tokensOut == null || tokensOut == tokensIn) {
      tokensOut = new IntList(tokensIn.size());
    } else {
      tokensOut.clear();
    }
    for (int i = 0; i < tokensIn.size(); ++i) {
      int token = tokensIn.get(i);
      if (token == maxPairIndex) {
        tokensOut.add(maxPair.left);
        tokensOut.add(maxPair.right);
      } else {
        tokensOut.add(token);
      }
    }
    return tokensOut;
  }

  public IntList getDecompressedTokens() {
    return decompressedTokens;
  }
}
