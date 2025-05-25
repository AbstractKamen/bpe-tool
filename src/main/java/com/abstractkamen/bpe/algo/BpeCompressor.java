package com.abstractkamen.bpe.algo;

import com.abstractkamen.bpe.structures.CompressionResult;
import com.abstractkamen.bpe.structures.IntList;

public interface BpeCompressor {
  CompressionResult compressTokens(IntList tokensIn, int maxIterations);
}
