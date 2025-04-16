package com.abstractkamen.bpe.commands;

import java.io.IOException;

public interface CommandFactory {
    Command createCommand(String inputPath, String outputPath) throws IOException;
}
