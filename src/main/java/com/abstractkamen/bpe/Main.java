package com.abstractkamen.bpe;

import com.abstractkamen.bpe.structures.BytePair;
import com.abstractkamen.bpe.structures.IntList;
import com.abstractkamen.bpe.visitor.BpeIterationVisitor;
import com.abstractkamen.bpe.visitor.BpeStdLoggingVisitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

  private static String BPE_COMPRESS = "bpec";
  private static String BPE_DECOMPRESS = "bped";

  public static void main(String[] args) throws IOException {
    String command = "";
    int i = 0;
    for (; i < args.length; ++i) {
      if (BPE_COMPRESS.equals(args[i]) || BPE_DECOMPRESS.equals(args[i])) {
        command = args[i];
        break;
      }
    }
    if (command == "") {
      help();
      return;
    }

    String inputFilePath = null;
    String outputFilePath = null;

    final Map<String, Object> flags = new HashMap<>();
    i++;
    for (; i < args.length; ++i) {
      final String arg = args[i];
      if (arg.startsWith("-") && arg.endsWith("-i")) {
        try {
          int maxIterations = Integer.parseInt(args[i + 1]);
          flags.put("maxIteration", maxIterations);
        } catch (ArrayIndexOutOfBoundsException e) {
          System.out.printf("Compress file%n    bpec -i [maxIterations is expected] <input file path> <output file path>%n", args[i + 1]);
        } catch (NumberFormatException e) {
          System.out.printf("Compress file%n    bpec -i [%s is not a valid number] <input file path> <output file path>%n", args[i + 1]);
        }
        i += 2;
      } else if (arg.startsWith("-") && arg.endsWith("-debug")) {
        flags.put("debug", true);
      } else if (inputFilePath == null) {
        inputFilePath = arg;
      } else if (outputFilePath == null) {
        outputFilePath = arg;
      }
    }

    if (inputFilePath == null || outputFilePath == null) {
      help();
      return;
    }

    switch (command) {
      case "bpec" -> {

        try (final var is = Files.newInputStream(Paths.get(inputFilePath));
             final var foos = Files.newOutputStream(Paths.get(outputFilePath))
        ) {
          final IntList initialTokens = getInitialTokens(is);
          final boolean debug = (boolean) flags.getOrDefault("debug", false);
          final int maxIterations = (int) flags.getOrDefault("maxIteration", Integer.MAX_VALUE);

          BpeIterationVisitor visitor = BpeIterationVisitor.EMPTY_VISITOR;
          if (debug) {
            visitor = new BpeStdLoggingVisitor();
          }

          final BpeCompress bpeCompress = new BpeCompress(visitor);
          bpeCompress.compressTokens(initialTokens, maxIterations);

          final IntList compressedTokens = bpeCompress.getCompressedTokens();
          final List<BytePair> pairs = bpeCompress.getPairs();

          final String smallPairs = pairs.stream().map(BytePair::toSmallString).collect(Collectors.joining());
          foos.write(smallPairs.getBytes(StandardCharsets.UTF_8));
          foos.write(DELIMITER.getBytes(StandardCharsets.UTF_8));
          foos.write(compressedTokens.toActualString().getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
          System.out.println(e.getMessage());
        }
      }
      case "bped" -> {
        try (final var reader = new InputStreamReader(Files.newInputStream(Paths.get(inputFilePath)));
             final var foos = Files.newOutputStream(Paths.get(outputFilePath))
        ) {
          final List<BytePair> pairs = new ArrayList<>();
          final IntList tokensIn = new IntList();

          int current, prev = -1;
          final int[] buffer = new int[DELIMITER.length()];
          Arrays.fill(buffer, -1);
          while ((current = reader.read()) != -1) {
            if (current == (int) '<') {
              buffer[0] = current;
              int j = 1;

              boolean delimiterFound = true;
              for (; j < DELIMITER.length(); ++j) {
                int nextChar = reader.read();
                if (nextChar == -1) {
                  delimiterFound = false;
                  break;
                }

                buffer[j] = nextChar;
                if (buffer[j] != (int) DELIMITER.charAt(j)) {
                  delimiterFound = false;
                  break;
                }
              }

              // delimiter is found read all text after it as tokensIn
              if (delimiterFound) {
                while ((current = reader.read()) != -1) {
                  tokensIn.add(current);
                }
                break;
              } else {
                System.out.println("buffer mismatch " + Arrays.toString(buffer));
                // delimiter mismatch
                // add all buffer tokensIn as pairs
                int k = 0;
                for (; k <= j && buffer[k] != -1; k += 2) {
                  pairs.add(new BytePair(buffer[k], buffer[k + 1]));
                }
                // read extra token in case delimiter length not even
                if (j % 2 != 0) {
                  prev = buffer[k - 1];
                }
                Arrays.fill(buffer, -1);
              }

            } else if (prev != -1){
              pairs.add(new BytePair(prev, current));
            } else {
              prev = current;
            }
          }
          System.out.println("TokensIN");
          System.out.println(tokensIn);
          System.out.println("Pairs");
          System.out.println(pairs.size());
          final BpeDecompress bpeDecompress = new BpeDecompress();
          bpeDecompress.decompressTokens(tokensIn, pairs);
          System.out.println(tokensIn.toActualString());
          final IntList decompressedTokens = bpeDecompress.getDecompressedTokens();
          System.out.println(decompressedTokens.toActualString());
          //foos.write(decompressedTokens.toActualString().getBytes(StandardCharsets.UTF_8));
        }
      }
      default -> help();
    }

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
    System.out.printf("Compress file%n    bpec -i [maxIterations] -debug <input file path> <output file path>%nDecompress file%n    bped <input file path> <output file path>%n");
  }

  public static final String DELIMITER = "<BPE_>";
}