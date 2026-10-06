package com.resort.billing.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StaticFileHandler implements HttpHandler {

    private final String staticDir;

    public StaticFileHandler(String staticDir) {
        this.staticDir = staticDir;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path == null || path.equals("/") || path.isEmpty()) {
            path = "/index.html";
        }

        // Prevent path traversal
        path = path.replace("..", "");

        Path filePath = Paths.get(staticDir, path);
        if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
            // Fallback to index.html for single-page routing
            filePath = Paths.get(staticDir, "index.html");
        }

        if (!Files.exists(filePath)) {
            String notFound = "404 Not Found - Frontend static file not found at " + filePath.toAbsolutePath();
            exchange.sendResponseHeaders(404, notFound.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(notFound.getBytes());
            }
            return;
        }

        String mime = getMimeType(filePath.getFileName().toString());
        exchange.getResponseHeaders().set("Content-Type", mime + "; charset=UTF-8");
        exchange.getResponseHeaders().set("Cache-Control", "no-cache");

        byte[] content = Files.readAllBytes(filePath);
        exchange.sendResponseHeaders(200, content.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(content);
        }
    }

    private String getMimeType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".html") || lower.endsWith(".htm")) return "text/html";
        if (lower.endsWith(".css")) return "text/css";
        if (lower.endsWith(".js")) return "application/javascript";
        if (lower.endsWith(".json")) return "application/json";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        if (lower.endsWith(".ico")) return "image/x-icon";
        return "text/plain";
    }
}
