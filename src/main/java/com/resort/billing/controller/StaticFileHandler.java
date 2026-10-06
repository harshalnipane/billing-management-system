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
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        // 1. Try loading from Classpath (when running inside packaged JAR or container)
        byte[] content = loadFromClasspath("static" + path);
        if (content == null && !path.contains(".")) {
            // SPA fallback to index.html
            content = loadFromClasspath("static/index.html");
            if (content != null) {
                path = "/index.html";
            }
        }

        // 2. Fallback to filesystem if not found in classpath (local dev environment)
        if (content == null && staticDir != null) {
            Path filePath = Paths.get(staticDir, path);
            if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
                filePath = Paths.get(staticDir, "index.html");
            }
            if (Files.exists(filePath)) {
                content = Files.readAllBytes(filePath);
            }
        }

        if (content == null) {
            String notFound = "404 Not Found - Frontend static file not found: " + path;
            byte[] notFoundBytes = notFound.getBytes("UTF-8");
            exchange.sendResponseHeaders(404, notFoundBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(notFoundBytes);
            }
            return;
        }

        String mime = getMimeType(path);
        exchange.getResponseHeaders().set("Content-Type", mime + "; charset=UTF-8");
        exchange.getResponseHeaders().set("Cache-Control", "no-cache");

        exchange.sendResponseHeaders(200, content.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(content);
        }
    }

    private byte[] loadFromClasspath(String resourcePath) {
        if (!resourcePath.startsWith("/")) {
            resourcePath = "/" + resourcePath;
        }
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) return null;
            return is.readAllBytes();
        } catch (Exception ignored) {
            return null;
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
