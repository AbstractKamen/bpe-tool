package com.abstractkamen.bpe.structures;

public class BytePair {

  private final int left;
  private final int right;

  public BytePair(int left, int right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public int hashCode() {
    return 31 * left + 17 * right;
  }

  public boolean matches(int left, int right) {
    return this.left == left && this.right == right;
  }

  public int left() {
    return left;
  }

  public int right() {
    return right;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof BytePair that
      && this.left == that.left
      && this.right == that.right;
  }

  public String toSmallString() {
    return (char) left + "" + (char) right;
  }

  @Override
  public String toString() {
    return String.format("'%c%c' : [%d][%d]", (char) left, (char) right, left, right);
  }
}