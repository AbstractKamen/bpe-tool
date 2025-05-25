package com.abstractkamen.bpe.commands;

import com.abstractkamen.bpe.algo.BpeCompressor;
import com.abstractkamen.bpe.algo.BpeVisitedCompressor;
import com.abstractkamen.bpe.algo.StdBpeCompressor;
import com.abstractkamen.bpe.structures.BytePairs;
import com.abstractkamen.bpe.structures.CompressionResult;
import com.abstractkamen.bpe.structures.IntList;
import com.abstractkamen.bpe.visitor.StdLoggingBpeCompressionVisitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class BpeCompressCommand implements Command {

    private final InputStream in;
    private final OutputStream out;

    public BpeCompressCommand(String inputFilePath, String outputFilePath) throws IOException {
        this.in = Files.newInputStream(Paths.get(inputFilePath));
        this.out = Files.newOutputStream(Paths.get(outputFilePath));
    }

    @Override
    public void executeCommand(Map<String, Object> flags) throws IOException {
        final IntList initialTokens = getInitialTokens(in);
        final boolean debug = (boolean) flags.getOrDefault("debug", false);
        final int maxIterations = (int) flags.getOrDefault("maxIteration", Integer.MAX_VALUE);
        final String charSet = (String) flags.getOrDefault("charset", StandardCharsets.UTF_8.name());

        final BpeCompressor bpeCompressor;
        if (debug) {
            bpeCompressor = new BpeVisitedCompressor(new StdLoggingBpeCompressionVisitor(), charSet);
        } else {
            bpeCompressor = new StdBpeCompressor(charSet);
        }
        final CompressionResult compressionResult = bpeCompressor.compressTokens(initialTokens, maxIterations);

        final IntList compressedTokens = compressionResult.compressedTokens();
        final BytePairs pairs = compressionResult.pairs();
        final Charset pairsCharset = pairs.getCharset();
        out.write(pairsCharset.name().getBytes());
        out.write(BpeConstants.BPE_CHARSET_DELIMITER.getBytes());
        out.write(pairs.getNewEncodedPairs());
        out.write(BpeConstants.BPE_PAIRS_DELIMITER.getBytes());
        out.write(compressedTokens.toActualString().getBytes(pairsCharset));
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

    @Override
    public void close() throws Exception {
        in.close();
        out.close();
    }
}
