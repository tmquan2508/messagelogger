package com.tmquan2508.messagelogger.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.proxy.Player;
import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommandListener {

    private final Logger logger;
    private final Path logDirectory;

    public CommandListener(Logger logger, Path dataDirectory) {
        this.logger = logger;
        this.logDirectory = dataDirectory.resolve("logs");
        try {
            if (!Files.exists(this.logDirectory)) {
                Files.createDirectories(this.logDirectory);
            }
        } catch (IOException e) {
            logger.error("Could not create log directory: " + this.logDirectory.toString(), e);
        }
    }

    @Subscribe
    public void onCommandExecute(CommandExecuteEvent event) {
        String commandLine = event.getCommand();

        String logEntry;
        if (event.getCommandSource() instanceof Player) {
            Player player = (Player) event.getCommandSource();
            String playerName = player.getUsername();
            logEntry = String.format("[%s] [COMMAND] %s: /%s",
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                    playerName,
                    commandLine);
        } else {
            logEntry = String.format("[%s] [CONSOLE_COMMAND]: %s",
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                    commandLine);
        }

        logger.info(logEntry);
        logToFile("commands.log", logEntry);
    }

    private void logToFile(String fileName, String message) {
        if (Files.notExists(this.logDirectory)) {
            logger.warn("Log directory does not exist, attempting to create: " + this.logDirectory.toString());
            try {
                Files.createDirectories(this.logDirectory);
            } catch (IOException e) {
                logger.error("Failed to create log directory on demand: " + this.logDirectory.toString(), e);
                return;
            }
        }
        File logFile = this.logDirectory.resolve(fileName).toFile();

        try (FileWriter fw = new FileWriter(logFile, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(message);
        } catch (IOException e) {
            logger.error("Could not write to command log file: " + logFile.getAbsolutePath(), e);
        }
    }
}