package com.abstractkamen.bpe.commands;

import com.abstractkamen.bpe.algo.BpeDecompressor;
import com.abstractkamen.bpe.structures.BytePair;
import com.abstractkamen.bpe.structures.BytePairs;
import com.abstractkamen.bpe.structures.IntList;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class BpeDecompressCommand implements Command {

    private final InputStreamReader reader;
    private final OutputStream out;

    public BpeDecompressCommand(String inputFilePath, String outputFilePath) throws IOException {
        this.reader = new InputStreamReader(Files.newInputStream(Paths.get(inputFilePath)));
        this.out = Files.newOutputStream(Paths.get(outputFilePath));
    }

    @Override
    public void executeCommand(Map<String, Object> flags) throws Exception {
        int ch = -1;
        final char[] charSetName = new char[32];
        final char nullChar = (char) -1;
        Arrays.fill(charSetName, nullChar);
        boolean charSetDelimiterFound = true;
        int chsIndex = 0;
        CharsetName:
        while ((ch = reader.read()) != -1 && chsIndex < charSetName.length) {
            if (ch == BpeConstants.BPE_CHARSET_DELIMITER.charAt(0)) {
                for (int j = 1; j < BpeConstants.BPE_CHARSET_DELIMITER.length(); ++j) {
                    int nextChar = reader.read();
                    if (nextChar != BpeConstants.BPE_CHARSET_DELIMITER.charAt(j)) {
                        charSetDelimiterFound = false;
                        break CharsetName;
                    }
                }
                break CharsetName;
            } else {
                charSetName[chsIndex++] = (char) ch;
            }
        }
        final String nameString;
        if (charSetName[charSetName.length - 1] == nullChar) {
            int i = 0;
            for (; i < charSetName.length && charSetName[i] != nullChar; i++) ;
            nameString = new String(Arrays.copyOfRange(charSetName, 0, i));
        } else {
            nameString = new String(charSetName);
        }

        final Charset charset = CompletableFuture.completedFuture(nameString)
                .thenApply(Charset::forName)
                .exceptionally(e -> {
                    System.out.printf("WARN: Unexpected charset %s. Using default %s%n", nameString, StandardCharsets.US_ASCII.name());
                    return StandardCharsets.US_ASCII;
                }).join();

        if (!charSetDelimiterFound) {
            throw new UnsupportedOperationException("NOT IMPLEMENTED");
            // TODO
        }

        final List<BytePair> pairs = new BytePairs(charset);
        final IntList tokensIn = new IntList();

        int current, prev = -1;
        final int[] pairsDelBuf = new int[BpeConstants.BPE_PAIRS_DELIMITER.length()];
        Arrays.fill(pairsDelBuf, -1);
        while ((current = reader.read()) != -1) {

            if (current == (int) BpeConstants.BPE_PAIRS_DELIMITER.charAt(0)) {
                pairsDelBuf[0] = current;

                boolean delimiterFound = true;
                for (int j = 1; j < BpeConstants.BPE_PAIRS_DELIMITER.length(); ++j) {
                    int nextChar = reader.read();
                    if (nextChar == -1) break;
                    pairsDelBuf[j] = nextChar;
                    if (nextChar != (int) BpeConstants.BPE_PAIRS_DELIMITER.charAt(j)) {
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
                    // delimiter doesn't match buffer
                    System.out.println("buffer mismatch " + Arrays.toString(pairsDelBuf));
                    // add all buffer tokensIn as pairs

                    // if prev has a value use it as left-most
                    if (prev != -1) {
                        for (int k = 0; k < pairsDelBuf.length; k += 2) {
                            if (pairsDelBuf[k] == -1 || pairsDelBuf[k + 1] == -1) break;
                            pairs.add(new BytePair(prev, pairsDelBuf[k]));
                            prev = pairsDelBuf[k + 1];
                            pairsDelBuf[k] = -1;
                            pairsDelBuf[k + 1] = -1;
                        }
                    } else {
                        for (int k = 0; k < pairsDelBuf.length; k += 2) {
                            if (pairsDelBuf[k] == -1 || pairsDelBuf[k + 1] == -1) break;
                            pairs.add(new BytePair(pairsDelBuf[k], pairsDelBuf[k + 1]));
                            pairsDelBuf[k] = -1;
                            pairsDelBuf[k + 1] = -1;
                        }
                    }
                    // if the buffer is not even store last value in prev
                    for (int i = 0; i < pairsDelBuf.length; i++) {
                        if (pairsDelBuf[i] != -1) {
                            prev = pairsDelBuf[i];
                        }
                        pairsDelBuf[i] = -1;
                    }
                }

            } else if (prev != -1) {
                pairs.add(new BytePair(prev, current));
                prev = -1;
            } else {
                prev = current;
            }
        }
        final BpeDecompressor bpeDecompressor = new BpeDecompressor();
        bpeDecompressor.decompressTokens(tokensIn, pairs);
        final IntList decompressedTokens = bpeDecompressor.getDecompressedTokens();

        out.write(decompressedTokens.toActualString().getBytes(charset));
    }

    @Override
    public void close() throws Exception {
        reader.close();
        out.close();
    }
}
