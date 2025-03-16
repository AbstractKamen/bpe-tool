package com.abstractkamen.array;

public class ArraySupport {

  public static final int SOFT_MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 8;

  public static int newLength(int oldLength, int minGrowth, int prefGrowth) {
    int prefLength = oldLength + Math.max(minGrowth, prefGrowth);
    if (0 < prefLength && prefLength <= SOFT_MAX_ARRAY_LENGTH) {
      return prefLength;
    } else {
      return Math.max(oldLength + minGrowth, SOFT_MAX_ARRAY_LENGTH);
    }
  }

}