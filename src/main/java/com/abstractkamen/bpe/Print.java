package com.abstractkamen.bpe;

import com.abstractkamen.bpe.structures.BytePair;
import com.abstractkamen.bpe.structures.IntList;

import java.util.List;

public class Print {

  private static final String[] SIZE_UNITS = {"B", "KB", "MB", "GB"};

  public static void showTokens(List<BytePair> pairs, IntList tokens) {
    final String s = convertTokensToString_highlightReplaced(pairs, tokens);
    System.out.println(s);
  }

  private static String convertTokensToString_highlightReplaced(List<BytePair> pairs, IntList tokens) {
    final var sb = new StringBuilder();

    for (int i = 0, token; i < tokens.size(); ++i) {
      token = tokens.get(i);
      if (token < pairs.size() && pairs.get(token).left() == token) {
        sb.append((char) token);
      } else {
        sb.append(String.format("[%d]", token));
      }
    }
    return sb.toString();
  }

  public static String humanReadableSize(double size) {
    int unitIndex = 0;
    while (size >= 1024 && unitIndex < SIZE_UNITS.length - 1) {
      size /= 1024;
      unitIndex++;
    }

    return String.format("%.2f %s", size, SIZE_UNITS[unitIndex]);
  }
}
