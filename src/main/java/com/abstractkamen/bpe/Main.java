package com.abstractkamen.bpe;

import com.abstractkamen.bpe.commands.BpeCompressCommand;
import com.abstractkamen.bpe.commands.BpeDecompressCommand;
import com.abstractkamen.bpe.commands.CommandFactory;
import com.abstractkamen.bpe.structures.BytePair;
import com.abstractkamen.bpe.structures.IntList;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    private static String BPE_COMPRESS = "bpec";
    private static String BPE_DECOMPRESS = "bped";

    public static void main(String[] args) {
        String command = null;
        int i = 0;
        for (; i < args.length; ++i) {
            if (BPE_COMPRESS.equals(args[i]) || BPE_DECOMPRESS.equals(args[i])) {
                command = args[i];
                break;
            }
        }
        if (command == null) {
            help();
            return;
        }

        String inputFilePath = null;
        String outputFilePath = null;

        final Map<String, Object> flags = new HashMap<>();
        i++;
        for (; i < args.length; ++i) {
            final String arg = args[i];
            switch (arg) {
                case "-i" -> {
                    try {
                        int maxIterations = Integer.parseInt(args[i + 1]);
                        flags.put("maxIteration", maxIterations);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.printf("Compress file%n    bpec -i [maxIterations is expected] -in <input file path> -out <output file path>%n");
                    } catch (NumberFormatException e) {
                        System.out.printf("Compress file%n    bpec -i [%s is not a valid number] -in <input file path> -out <output file path>%n", args[i + 1]);
                    }
                    i += 1;
                }
                case "-debug" -> flags.put("debug", true);
                case "-charset" -> {
                    switch (i + 1 < args.length ? args[i + 1] : null) {
                        case "utf8" -> flags.put("charset", StandardCharsets.UTF_8.name());
                        case null, default -> flags.put("charset", StandardCharsets.US_ASCII.name());
                    }
                }
                case "-in" -> inputFilePath = i + 1 < args.length ? args[i + 1] : null;
                case "-out" -> outputFilePath = i + 1 < args.length ? args[i + 1] : null;
            }
        }

        if (inputFilePath == null || outputFilePath == null) {
            help();
            return;
        }

        switch (command) {
            case "bpec" -> executeCommand(inputFilePath, outputFilePath, flags, BpeCompressCommand::new);
            case "bped" -> executeCommand(inputFilePath, outputFilePath, flags, BpeDecompressCommand::new);
            default -> help();
        }

    }

    private static void executeCommand(String inputFilePath, String outputFilePath, Map<String, Object> flags, CommandFactory commandFactory) {
        try (var c = commandFactory.createCommand(inputFilePath, outputFilePath)) {
            c.executeCommand(flags);
        } catch (Exception e) {
            final Object debug = flags.get("debug");
            if (debug != null && (Boolean)debug) {
                e.printStackTrace();
            }
            System.out.println(e.getMessage());
            help();
        }
    }

    private static void help() {
        System.out.printf("Compress file%n    bpec -i [maxIterations] -debug -in <input file path> -out <output file path>%nDecompress file%n    bped <input file path> <output file path>%n");
    }

}