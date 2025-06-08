package com.tmquan2508.messagelogger;

import com.google.inject.Inject;
import com.tmquan2508.messagelogger.listeners.ChatListener;
import com.tmquan2508.messagelogger.listeners.CommandListener;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
        id = "messagelogger",
        name = "Message Logger",
        version = "1.0.0",
        description = "A plugin to log player chats and commands, and a forcesay command.",
        authors = {"tmquan2508"}
)
public class MessageLogger {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    @Inject
    public MessageLogger(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;

        logger.info("Message logger plugin has been enabled!");
        logger.info("Data directory: " + this.dataDirectory.toString());
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        ChatListener chatListener = new ChatListener(logger, dataDirectory);
        CommandListener commandListener = new CommandListener(logger, dataDirectory);
        server.getEventManager().register(this, chatListener);
        server.getEventManager().register(this, commandListener);
        logger.info("Chat and Command listeners have been registered.");
    }
}