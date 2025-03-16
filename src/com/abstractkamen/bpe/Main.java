package com.abstractkamen.bpe;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

  public static void main(String[] args) throws IOException {

    if (args.length < 2) help();
    //final Path inputPath = Paths.get(args[0]);
    //try (BufferedReader fileReader = Files.newBufferedReader(inputPath)) {
    final String text = """
                        The original BPE algorithm operates by iteratively replacing the most common contiguous sequences of characters
                        in a target text with unused 'placeholder' bytes. The iteration ends when no sequences can be found, leaving
                        the target text effectively compressed. Decompression can be performed by reversing this process, querying
                        known placeholder terms against their corresponding denoted sequence, using a lookup table. In the original
                        paper, this lookup table is encoded and stored alongside the compressed text.
                        """;
    // final Map<BytePair, Integer> frequencies = getFrequencies(in);

    final int asciiCapacity = Byte.MAX_VALUE - Byte.MIN_VALUE;
    final List<BytePair> pairs = new ArrayList<>(asciiCapacity);
    for (int i = 0; i < asciiCapacity; ++i) {
      pairs.add(i, new BytePair(i, -1));
    }

    final Map<BytePair, Integer> frequencies = new HashMap<>();
    final ByteArrayInputStream in = new ByteArrayInputStream(text.getBytes());
    Tokens tokensIn = getInitialTokens(in);
    Tokens tokensOut = null;
    int limit = 100;
    int i = 1;
    while (true) {
      if (limit == 0) break;
      frequencies.clear();
      System.out.printf("%nSTART ITERATION %d *****************************%n", i);
      final BytePair maxPair = getMaxPair(tokensIn, frequencies);

      assert maxPair != null;
      final Integer maxFrequency = frequencies.get(maxPair);
      System.out.printf("Most frequent pair = %s %d%n", maxPair, maxFrequency);
      if (maxFrequency == null || maxFrequency == 1) {
        System.out.println("Cannot compress any further, breaking...");
        break;
      }
      final int maxTokenIndex = pairs.size();
      pairs.add(maxPair);
      tokensOut = replaceMaxPairToken(tokensIn, tokensOut, maxPair, maxTokenIndex);
      showTokens(pairs, tokensIn);
      System.out.println("    *********************\n");
      showTokens(pairs, tokensOut);
      System.out.printf("Compressed[%s] from input[%s]%n", humanReadableSize(tokensOut.size()), humanReadableSize(tokensIn.size()));
      System.out.printf("END ITERATION %d*****************************%n", i);

      Tokens temp = tokensIn;
      tokensIn = tokensOut;
      tokensOut = temp;
      limit--;
      i++;
    }
    System.out.println("pairs");
    for (BytePair pair : pairs) {
      System.out.println(pair);
    }


  }

  private static Tokens replaceMaxPairToken(Tokens tokensIn, Tokens tokensOut, BytePair maxPair, int maxTokenIndex) {
    if (tokensOut == null) {
      tokensOut = new Tokens();
    } else {
      tokensOut.clear();
    }
    int token, prevToken = -1;
    for (int i = 0; i < tokensIn.size() - 1; ++i) {
      token = tokensIn.get(i);
      if (prevToken != -1) {
        if (maxPair.matches(prevToken, token)) {
          tokensOut.add(maxTokenIndex);
          token = tokensIn.get(++i);
        } else {
          tokensOut.add(prevToken);
        }
      }
      prevToken = token;
    }
    tokensOut.add(prevToken);
    return tokensOut;
  }

  private static Tokens getInitialTokens(ByteArrayInputStream in) throws IOException {
    try (final var reader = new InputStreamReader(in)) {
      final Tokens tokens = new Tokens();
      int current = -1;
      while ((current = reader.read()) != -1) {
        tokens.add(current);
      }
      return tokens;
    }
  }

  private static BytePair getMaxPair(Tokens tokens, Map<BytePair, Integer> frequencies) {
    BytePair maxPair = null;
    int maxPairFreq = -1;
    int token, prevToken = -1;
    for (int i = 0; i < tokens.size(); ++i) {
      token = tokens.get(i);
      if (prevToken != -1) {
        final BytePair bytePair = new BytePair(prevToken, token);
        final int freq = frequencies.compute(bytePair, (_, oldV) -> oldV == null ? 1 : oldV + 1);
        if (maxPairFreq < freq) {
          maxPair = bytePair;
          maxPairFreq = freq;
        }
      }
      prevToken = token;
    }
    return maxPair;
  }

  private static void showTokens(List<BytePair> pairs, Tokens tokens) {
    final var sb = new StringBuilder();

    for (int i = 0, token; i < tokens.size(); ++i) {
      token = tokens.get(i);
      assert token < pairs.size();
      if (pairs.get(token).left() == token) {
        sb.append((char) token);
      } else {
        sb.append(String.format("[%d]", token));
      }
    }
    System.out.println(sb);
  }

  private static final String[] SIZE_UNITS = {"B", "KB", "MB", "GB"};

  private static String humanReadableSize(double size) {
    int unitIndex = 0;
    while (size >= 1024 && unitIndex < SIZE_UNITS.length - 1) {
      size /= 1024;
      unitIndex++;
    }

    return String.format("%.2f %s", size, SIZE_UNITS[unitIndex]);
  }

  private static void help() {
    System.out.printf("bpe <input file path> <output file path>%n");
  }

}