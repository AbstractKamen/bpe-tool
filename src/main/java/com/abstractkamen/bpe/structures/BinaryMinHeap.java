package com.abstractkamen.bpe.structures;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.UnaryOperator;

public class BinaryMinHeap<T> {

  protected static final int DEFAULT_CAPACITY = 16;
  private final Comparator<T> comparator;
  private Object[] items;
  private final Map<T, Integer> indexByT;
  private int size;

  /**
   * Create an {@code BinaryMinHeap<T>} with a custom comparator and capacity.
   *
   * @param comparator
   *   custom comparator
   * @param capacity
   *   initial capacity
   */
  public BinaryMinHeap(Comparator<T> comparator, int capacity) {
    if (capacity < 1) {
      throw new IllegalArgumentException("capacity cannot be below 1");
    }
    this.comparator = comparator;
    this.items = new Object[capacity];
    this.indexByT = new HashMap<>(capacity);
  }

  /**
   * Create an {@code BinaryMinHeap<T>} with a custom comparator.
   *
   * @param comparator
   *   custom comparator
   */
  public BinaryMinHeap(Comparator<T> comparator) {
    this(comparator, DEFAULT_CAPACITY);
  }

  /**
   * Create an {@code BinaryMinHeap<T>} with natural order comparator in a type safe way.
   *
   * @param <T>
   *   comparable type
   */
  public static <T extends Comparable<T>> BinaryMinHeap<T> createComparable() {
    final Comparator<T> c = Comparable::compareTo;
    return new BinaryMinHeap<>(c);
  }

  /**
   * Create an {@code BinaryMinHeap<T>} with natural order comparator in a type safe way.
   *
   * @param capacity
   *   initial capacity
   * @param <T>
   *   comparable type
   */
  public static <T extends Comparable<T>> BinaryMinHeap<T> createComparable(int capacity) {
    final Comparator<T> c = Comparable::compareTo;
    return new BinaryMinHeap<>(c, capacity);
  }

  public int size() {
    return size;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public void addAll(Iterable<T> it) {
    if (it == null) return;
    for (T t : it) {
      push(t);
    }
  }

  public int push(T item) {
    final int i = size;
    if (items.length <= i + 1) {
      final int newLength = ArraySupport.newLength(items.length, 16, items.length >> 1);
      items = Arrays.copyOf(items, newLength);
    }
    items[i] = item;
    indexByT.putIfAbsent(item, i);
    heapifyUp(items, comparator, i, indexByT);
    ++size;
    return size;
  }

  @SuppressWarnings("unchecked")
  public T peek() {
    return isEmpty() ? null : (T) items[0];
  }

  @SuppressWarnings("unchecked")
  public T pop() {
    if (size == 0) {
      throw new NoSuchElementException();
    } else {
      final T result = (T) items[0];
      indexByT.remove(result);
      --size;
      if (size > 0) {
        items[0] = items[size];
        heapifyDown(items, comparator, 0, size, indexByT);
      }
      return result;
    }
  }

  public void increaseKey(T key, UnaryOperator<T> increment) {
    int i = indexByT.getOrDefault(key, -1);
    if (i >= 0) {
      indexByT.remove(key);
      key = increment.apply(key);
      indexByT.put(key, i);
      heapifyUp(items, comparator, i, indexByT);
    }
  }

  public void decreaseKey(T key, UnaryOperator<T> decrement) {
    int i = indexByT.getOrDefault(key, -1);
    if (i >= 0) {
      indexByT.remove(key);
      key = decrement.apply(key);
      indexByT.put(key, i);
      heapifyDown(items, comparator, i, size, indexByT);
    }
  }

  @Override
  public String toString() {
    return "not in order" + Arrays.toString(items);
  }

  public Comparator<T> comparator() {
    return comparator;
  }

  protected static <T> void heapifyDown(Object[] items, Comparator<T> comparator, int i, int size, Map<T, Integer> indexByT) {
    int half = size >>> 1;
    while (i < half) {
      final int smallest = smallestChild(i, size, comparator, items);
      if (smallest == i) {
        return;
      }
      swap(items, i, smallest, indexByT);
      i = smallest;
    }
  }

  private static <T> int smallestChild(int i, int size, Comparator<T> comparator, Object[] items) {
    final int left = (i << 1) + 1;
    final int right = left + 1;
    int smallest = i;
    if (left < size && greaterThanOrEqual(smallest, left, comparator, items)) {
      smallest = left;
    }
    if (right < size && greaterThanOrEqual(smallest, right, comparator, items)) {
      smallest = right;
    }
    return smallest;
  }

  protected static <T> void heapifyUp(Object[] items, Comparator<T> comparator, int i, Map<T, Integer> indexByT) {
    while (i > 0) {
      final int parent = (i - 1) >>> 1;
      if (greaterThanOrEqual(parent, i, comparator, items)) {
        swap(items, i, parent, indexByT);
        i = parent;
      } else {
        return;
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static <T> void swap(Object[] items, int a, int b, Map<T, Integer> indexByT) {
    final Object tempA = items[a];
    Object A = items[a] = items[b];
    Object B = items[b] = tempA;

    indexByT.put((T) A, a);
    indexByT.put((T) B, b);
  }

  @SuppressWarnings("unchecked")
  private static <T> boolean greaterThanOrEqual(int a, int b, Comparator<T> c, Object[] items) {
    return c.compare((T) items[a], (T) items[b]) >= 0;
  }
}
