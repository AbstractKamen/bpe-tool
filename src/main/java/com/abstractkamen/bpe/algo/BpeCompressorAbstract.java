package com.abstractkamen.bpe.algo;

import com.abstractkamen.bpe.structures.BPFreq;
import com.abstractkamen.bpe.structures.BytePair;
import com.abstractkamen.bpe.structures.BytePairs;
import com.abstractkamen.bpe.structures.BinaryMinHeap;
import com.abstractkamen.bpe.structures.IntList;

import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public abstract class BpeCompressorAbstract {

  protected final Map<Integer, BPFreq> frequencies;
  protected final BinaryMinHeap<BPFreq> freqHeap;
  protected final BytePairs pairs;

  protected BpeCompressorAbstract(String charSet) {
    this.pairs = new BytePairs(Charset.forName(charSet));
    this.frequencies = new HashMap<>(pairs.size());
    this.freqHeap = new BinaryMinHeap<>(Comparator.comparingInt(BPFreq::getFrequency).reversed(), pairs.size());
  }

  protected final IntList replaceMaxPairToken(IntList tokensIn, IntList tokensOut, BytePair maxPair, int maxTokenIndex) {
    if (tokensOut == null || tokensOut == tokensIn) {
      tokensOut = new IntList(tokensIn.size());
    } else {
      tokensOut.clear();
    }
    int token, nextToken, prevToken = -1;
    for (int i = 0; i < tokensIn.size(); ++i) {
      token = tokensIn.get(i);
      if (prevToken != -1) {
        if (maxPair.left == prevToken && maxPair.right == token) {
          tokensOut.add(maxTokenIndex);
          if (i + 1 < tokensIn.size()) {
            nextToken = tokensIn.get(++i);
            /*
             * Decrementing the next pair:
             *
             * if we have "asasas" -> "as" is most frequent
             * by replacing "as" we also need to decrement the next possible pair "sa"
             *
             */
            final int key = BytePair.getBPHashCode(token, nextToken);
            final BPFreq bpFreq = frequencies.get(key);
            if (bpFreq != null) {
              if (bpFreq.getFrequency() > 1) {
                freqHeap.decreaseKey(bpFreq, BPFreq::decrement);
              } else {
                frequencies.remove(key);
              }
            }
            token = nextToken;
          } else {
            token = -1;
          }
          /*
           * Incrementing prev and next pairs with current maxIndex
           *
           * if we have "basasass" -> "as" is most frequent
           * we replace it with "X" -> "bXXXs"
           * we need to add/increment prev token combined with maxTokenIndex
           */
          if (tokensOut.size() > 1) {
            final int tokenBeforePrev = tokensOut.get(tokensOut.size() - 2);
            final int newPrevPair = BytePair.getBPHashCode(tokenBeforePrev, maxTokenIndex);
            BPFreq prevPairFreq = frequencies.get(newPrevPair);
            if (prevPairFreq != null) {
              freqHeap.increaseKey(prevPairFreq, BPFreq::increment);
            } else {
              prevPairFreq = new BPFreq(new BytePair(tokenBeforePrev, maxTokenIndex));
              frequencies.put(newPrevPair, prevPairFreq);
              freqHeap.push(prevPairFreq);
            }
          }
        } else {
          tokensOut.add(prevToken);
        }
      }
      prevToken = token;
    }
    if (prevToken != -1) {
      tokensOut.add(prevToken);
    }
    return tokensOut;
  }

  protected final void initFreq(IntList tokens) {
    int token, prevToken = -1;
    for (int i = 0; i < tokens.size(); ++i) {
      token = tokens.get(i);
      if (prevToken != -1) {
        final int left = prevToken, right = token;
        final int bytePair = BytePair.getBPHashCode(prevToken, token);
        frequencies.compute(bytePair, (_, oldV) -> {
          if (oldV == null) {
            return new BPFreq(new BytePair(left, right));
          } else {
            oldV.increment();
            return oldV;
          }
        });
      }
      prevToken = token;
    }
    freqHeap.addAll(frequencies.values());
  }
}
