package com.abstractkamen.bpe.structures;

public class BPFreq {

  private final BytePair bytePair;
  private int frequency;

  public BPFreq(BytePair bytePair) {
    this.bytePair = bytePair;
    this.frequency = 1;
  }

  public BPFreq increment() {
    frequency++;
    return this;
  }

  public BPFreq decrement() {
    frequency--;
    return this;
  }

  public BytePair getBytePair() {
    return bytePair;
  }

  public int getFrequency() {
    return frequency;
  }

  @Override
  public String toString() {
    return bytePair + "->freq[" + frequency + "]";
  }
}