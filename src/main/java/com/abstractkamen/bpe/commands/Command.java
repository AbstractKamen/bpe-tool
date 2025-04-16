package com.abstractkamen.bpe.commands;

import java.util.Map;

public interface Command extends AutoCloseable{
    void executeCommand(Map<String, Object> flags) throws Exception;
}
