package com.tmquan2508.messagelogger.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
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

public class ChatListener {

    private final Logger logger;
    private final Path logDirectory;

    public ChatListener(Logger logger, Path dataDirectory) {
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
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        String playerName = player.getUsername();

        String logEntry = String.format("[%s] [CHAT] %s: %s",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                playerName,
                message);

        logger.info(logEntry);
        logToFile("chat.log", logEntry);
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
            logger.error("Could not write to chat log file: " + logFile.getAbsolutePath(), e);
        }
    }
}