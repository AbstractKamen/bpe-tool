package com.abstractkamen.bpe.structures;

import java.util.Arrays;

public class IntList {
  private int[] tokens;
  private int size;

  public IntList() {
    this.tokens = new int[16];
  }

  public void add(int token) {
    if (tokens.length <= size + 1) {
      final int newLength = ArraySupport.newLength(tokens.length, 16, tokens.length >> 1);
      this.tokens = Arrays.copyOf(this.tokens, newLength);
    }
    assert tokens.length > size + 1;
    this.tokens[size++] = token;
  }

  public int get(int i) {
    assert i < size;
    return tokens[i];
  }

  public int size() {
    return size;
  }

  public void clear() {
    this.size = 0;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    for (int i = 0; i < this.size; ++i) {
      sb.append((char) this.tokens[i]);
    }
    return sb.toString();
  }

}