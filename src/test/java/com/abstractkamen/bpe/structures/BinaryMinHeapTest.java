package com.abstractkamen.bpe.structures;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class BinaryMinHeapTest {
  @Test
  public void decreaseKey_test() {
    final BinaryMinHeap<Integer> h = BinaryMinHeap.createComparable();
    h.push(1);
    h.push(4);
    h.push(3);
    h.push(7);
    h.push(3);
    h.push(123);
    h.push(13);
    h.push(17);
    h.push(1);
    h.push(5);
    h.decreaseKey(5, k -> Integer.MIN_VALUE);
    int prev;
    assertEquals(Integer.MIN_VALUE, prev = h.pop());
    while (!h.isEmpty()) {
      final int min = h.pop();
      System.out.println(min);
      assertTrue(min >= prev);
      prev = min;
    }
  }

  @Test
  public void increaseKey_test() {
    final BinaryMinHeap<Integer> h = BinaryMinHeap.createComparable();
    h.push(1);
    h.push(4);
    h.push(3);
    h.push(7);
    h.push(3);
    h.push(123);
    h.push(13);
    h.push(17);
    h.push(1);
    h.push(Integer.MIN_VALUE);
    int prev;
    h.increaseKey(Integer.MIN_VALUE, k -> 5);
    assertEquals("Expected first popped element to be '1'",1, prev = h.pop());
    h.increaseKey(3, x -> Integer.MAX_VALUE);
    while (!h.isEmpty()) {
      final int min = h.pop();
      assertTrue(min >= prev);
      prev = min;
    }
    assertEquals("Expected last popped element to be 'Integer.MAX_VALUE'",Integer.MAX_VALUE, prev);
  }

  @Test
  public void push_pop_size_randomTest() {
    for (int j = 0; j < 10; j++) {
      // arrange
      final BinaryMinHeap<Integer> heap = BinaryMinHeap.createComparable();
      // act
      final int maxSize = 1000;
      final long expecteSize = new Random().ints(maxSize, -10000, 10000).distinct().peek(heap::push).count();
      assertEquals(expecteSize, heap.size());
      // assert
      final List<Integer> expected = new ArrayList<>(heap.size());
      final List<Integer> actual = new ArrayList<>(heap.size());
      while (!heap.isEmpty()) {
        final int popped = heap.pop();
        actual.add(popped);
        expected.add(popped);
      }
      expected.sort(Integer::compare);
      assertEquals(expected, actual);
    }
  }

  @Test
  public void push_pop_size_duplicateTest() {
    // arrange
    final BinaryMinHeap<Integer> heap = BinaryMinHeap.createComparable();
    // act
    final int maxSize = 1000;
    IntStream.generate(() -> 0).limit(maxSize).forEach(heap::push);
    assertEquals(maxSize, heap.size());
    // assert
    final List<Integer> expected = new ArrayList<>(heap.size());
    final List<Integer> actual = new ArrayList<>(heap.size());
    while (!heap.isEmpty()) {
      final int popped = heap.pop();
      actual.add(popped);
      expected.add(popped);
    }
    expected.sort(Integer::compare);
    assertEquals(expected, actual);
  }

  @Test
  public void push_sizeTest() {
    final BinaryMinHeap<Integer> heap = BinaryMinHeap.createComparable();
    for (int i = 0; i < 10; i++) {
      final int expectedSize = i + 1;
      assertEquals(expectedSize, heap.push((int) (Math.random() * i * 1000 - 1500)));
    }
  }

  @Test
  public void peekTest() {
    final BinaryMinHeap<Integer> heap = BinaryMinHeap.createComparable();
    // when empty
    final Integer actual = heap.peek();
    assertNull(actual);
    // when size == 1
    heap.push(0);
    assertEquals(0, heap.peek().intValue());
    // when size 2
    heap.push(1);
    assertEquals(0, heap.peek().intValue());
    // when size 10
    IntStream.range(2, 10).forEach(heap::push);
    assertEquals(0, heap.peek().intValue());
    // when we add a new min value
    heap.push(-1);
    assertEquals(-1, heap.peek().intValue());
  }

  @Test(expected = NoSuchElementException.class)
  public void pop_shouldThrow_whenEmpty() {
    BinaryMinHeap.createComparable().pop();
  }
}