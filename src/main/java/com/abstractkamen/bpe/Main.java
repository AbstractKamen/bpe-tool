package com.abstractkamen.bpe;

import com.abstractkamen.bpe.structures.BytePair;
import com.abstractkamen.bpe.structures.IntList;
import com.abstractkamen.bpe.visitor.BpeIterationVisitor;
import com.abstractkamen.bpe.visitor.BpeStdLoggingVisitor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class Main {

  public static void main(String[] args) throws IOException {

    if (args.length < 2) help();

  }

  public static IntList getInitialTokens(InputStream in) throws IOException {
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