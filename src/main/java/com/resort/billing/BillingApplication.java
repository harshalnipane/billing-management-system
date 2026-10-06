package com.resort.billing;

import com.resort.billing.controller.BillingApiController;
import com.resort.billing.controller.StaticFileHandler;
import com.resort.billing.repository.InvoiceRepository;
import com.resort.billing.service.GstCalculationService;
import com.resort.billing.service.InvoiceService;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Main Application Runner for Villa and Resort Billing Management System.
 * Starts high-performance zero-dependency REST & Web server.
 */
public class BillingApplication {

    public static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {}
        }

        try {
            // Initialize Core Architecture Layers
            GstCalculationService gstCalculationService = new GstCalculationService();
            InvoiceRepository invoiceRepository = new InvoiceRepository();
            InvoiceService invoiceService = new InvoiceService(invoiceRepository, gstCalculationService);

            // Locate Static Resources directory
            String staticDir = "src/main/resources/static";
            if (!new File(staticDir).exists()) {
                if (new File("static").exists()) {
                    staticDir = "static";
                } else if (new File("../src/main/resources/static").exists()) {
                    staticDir = "../src/main/resources/static";
                }
            }

            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.setExecutor(Executors.newFixedThreadPool(16));

            // REST API Handlers
            BillingApiController apiController = new BillingApiController(invoiceService, gstCalculationService);
            server.createContext("/api", apiController);

            // Static Web Frontend Handler
            StaticFileHandler staticHandler = new StaticFileHandler(staticDir);
            server.createContext("/", staticHandler);

            server.start();

            printBanner(port, staticDir);

        } catch (Exception e) {
            System.err.println("Fatal: Failed to start Billing Application server: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void printBanner(int port, String staticDir) {
        System.out.println("================================================================================");
        System.out.println("   SERENE PALMS LUXURY VILLA & RESORT - BILLING MANAGEMENT SYSTEM");
        System.out.println("   Compliant with Indian Goods and Services Tax (GST) Act & Council Rules");
        System.out.println("================================================================================");
        System.out.println("   [+] Application Server Status : ONLINE");
        System.out.println("   [+] Web Application & UI     : http://localhost:" + port);
        System.out.println("   [+] Health Check API         : http://localhost:" + port + "/api/health");
        System.out.println("   [+] Billing Config API       : http://localhost:" + port + "/api/config");
        System.out.println("   [+] Invoices REST API        : http://localhost:" + port + "/api/billing/invoices");
        System.out.println("   [+] Dashboard Summary API    : http://localhost:" + port + "/api/billing/dashboard");
        System.out.println("   [+] Static Frontend Path     : " + new File(staticDir).getAbsolutePath());
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("   GST Tax Slabs Configured:");
        System.out.println("     * Room Tariff <= \u20B97,500/night : 12% GST (6% CGST + 6% SGST / 12% IGST)");
        System.out.println("     * Room Tariff > \u20B97,500/night  : 18% GST (9% CGST + 9% SGST / 18% IGST)");
        System.out.println("     * F&B Restaurant Dining       : 5% (Standard) / 18% (Specified luxury premises)");
        System.out.println("     * Spa & Wellness Services     : 18% GST (SAC: 999721)");
        System.out.println("     * Chauffeur / Airport Pick-up : 5% GST (SAC: 996412)");
        System.out.println("================================================================================");
    }
}
