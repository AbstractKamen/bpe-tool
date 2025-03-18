package com.abstractkamen.bpe;

import com.abstractkamen.bpe.structures.IntList;
import com.abstractkamen.bpe.visitor.BpeStdLoggingVisitor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

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


    final ByteArrayInputStream in = new ByteArrayInputStream(text.getBytes());

    IntList tokensIn = getInitialTokens(in);
    final BpeCompress bpeCompress = new BpeCompress(new BpeStdLoggingVisitor());
    bpeCompress.compressTokens(tokensIn, 100);

  }

  private static IntList getInitialTokens(ByteArrayInputStream in) throws IOException {
    try (final var reader = new InputStreamReader(in)) {
      final IntList tokens = new IntList();
      int current = -1;
      while ((current = reader.read()) != -1) {
        tokens.add(current);
      }
      return tokens;
    }
  }

  private static void help() {
    System.out.printf("bpe <input file path> <output file path>%n");
  }

}