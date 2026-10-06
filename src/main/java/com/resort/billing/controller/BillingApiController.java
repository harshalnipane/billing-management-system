package com.resort.billing.controller;

import com.resort.billing.config.AppConfig;
import com.resort.billing.dto.*;
import com.resort.billing.model.*;
import com.resort.billing.service.GstCalculationService;
import com.resort.billing.service.InvoiceService;
import com.resort.billing.util.JsonUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BillingApiController implements HttpHandler {

    private final InvoiceService invoiceService;
    private final GstCalculationService calculationService;

    public BillingApiController(InvoiceService invoiceService, GstCalculationService calculationService) {
        this.invoiceService = invoiceService;
        this.calculationService = calculationService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod().toUpperCase();
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();

        // Handle CORS preflight
        if ("OPTIONS".equals(method)) {
            sendCorsResponse(exchange, 204, "");
            return;
        }

        try {
            if (path.equals("/api/health") && "GET".equals(method)) {
                Map<String, Object> health = new LinkedHashMap<>();
                health.put("status", "UP");
                health.put("service", "Villa & Resort Billing Management System");
                health.put("taxEngine", "Indian GST Council (CGST/SGST/IGST)");
                health.put("timestamp", System.currentTimeMillis());
                sendJsonResponse(exchange, 200, JsonUtils.toJson(health));
                return;
            }

            if (path.equals("/api/config") && "GET".equals(method)) {
                Map<String, Object> cfg = new LinkedHashMap<>();
                cfg.put("resort", AppConfig.RESORT_PROFILE);
                cfg.put("states", AppConfig.INDIAN_STATES);
                cfg.put("villaTypes", AppConfig.VILLA_TYPES);
                List<Map<String, String>> categories = new ArrayList<>();
                for (ItemCategory cat : ItemCategory.values()) {
                    Map<String, String> c = new LinkedHashMap<>();
                    c.put("name", cat.name());
                    c.put("sacCode", cat.getDefaultSacCode());
                    c.put("description", cat.getDescription());
                    categories.add(c);
                }
                cfg.put("categories", categories);
                sendJsonResponse(exchange, 200, JsonUtils.toJson(cfg));
                return;
            }

            if (path.equals("/api/billing/calculate") && "POST".equals(method)) {
                String body = readRequestBody(exchange);
                CalculateBillRequest req = parseCalculateRequest(body);
                CalculateBillResponse resp = invoiceService.calculate(req);
                sendJsonResponse(exchange, 200, JsonUtils.toJson(resp));
                return;
            }

            if (path.equals("/api/billing/invoices") && "POST".equals(method)) {
                String body = readRequestBody(exchange);
                CreateInvoiceRequest req = parseCreateInvoiceRequest(body);
                Invoice created = invoiceService.createInvoice(req);
                sendJsonResponse(exchange, 201, JsonUtils.toJson(created));
                return;
            }

            if (path.equals("/api/billing/invoices") && "GET".equals(method)) {
                String query = getQueryParam(uri.getQuery(), "q");
                List<Invoice> list = invoiceService.getAllInvoices(query);
                sendJsonResponse(exchange, 200, JsonUtils.toJson(list));
                return;
            }

            if (path.startsWith("/api/billing/invoices/") && "GET".equals(method)) {
                String invNum = URLDecoder.decode(path.substring("/api/billing/invoices/".length()), StandardCharsets.UTF_8);
                Optional<Invoice> opt = invoiceService.getInvoice(invNum);
                if (opt.isPresent()) {
                    sendJsonResponse(exchange, 200, JsonUtils.toJson(opt.get()));
                } else {
                    sendErrorResponse(exchange, 404, "Invoice not found: " + invNum);
                }
                return;
            }

            if (path.equals("/api/billing/dashboard") && "GET".equals(method)) {
                DashboardSummary sum = invoiceService.getDashboardSummary();
                sendJsonResponse(exchange, 200, JsonUtils.toJson(sum));
                return;
            }

            sendErrorResponse(exchange, 404, "Endpoint not found: " + path);
        } catch (Exception ex) {
            ex.printStackTrace();
            sendErrorResponse(exchange, 500, "Internal Server Error: " + ex.getMessage());
        }
    }

    private CalculateBillRequest parseCalculateRequest(String json) {
        Map<String, Object> map = JsonUtils.parseMap(json);
        CalculateBillRequest req = new CalculateBillRequest();

        if (map.containsKey("guest") && map.get("guest") instanceof Map) {
            req.setGuest(parseGuest((Map<String, Object>) map.get("guest")));
        }
        if (map.containsKey("booking") && map.get("booking") instanceof Map) {
            req.setBooking(parseBooking((Map<String, Object>) map.get("booking")));
        }
        if (map.containsKey("additionalItems") && map.get("additionalItems") instanceof List) {
            req.setAdditionalItems(parseBillItems((List<Object>) map.get("additionalItems")));
        }
        if (map.containsKey("discountFlat")) req.setDiscountFlat(asDouble(map.get("discountFlat")));
        if (map.containsKey("discountPercentage")) req.setDiscountPercentage(asDouble(map.get("discountPercentage")));
        if (map.containsKey("advancePaid")) req.setAdvancePaid(asDouble(map.get("advancePaid")));
        if (map.containsKey("isInterStateOverride") && map.get("isInterStateOverride") != null) {
            req.setIsInterStateOverride(Boolean.TRUE.equals(map.get("isInterStateOverride")));
        }

        return req;
    }

    private CreateInvoiceRequest parseCreateInvoiceRequest(String json) {
        Map<String, Object> map = JsonUtils.parseMap(json);
        CreateInvoiceRequest req = new CreateInvoiceRequest();

        if (map.containsKey("guest") && map.get("guest") instanceof Map) {
            req.setGuest(parseGuest((Map<String, Object>) map.get("guest")));
        }
        if (map.containsKey("booking") && map.get("booking") instanceof Map) {
            req.setBooking(parseBooking((Map<String, Object>) map.get("booking")));
        }
        if (map.containsKey("items") && map.get("items") instanceof List) {
            req.setItems(parseBillItems((List<Object>) map.get("items")));
        }
        if (map.containsKey("payment") && map.get("payment") instanceof Map) {
            req.setPayment(parsePayment((Map<String, Object>) map.get("payment")));
        }
        if (map.containsKey("notes")) req.setNotes((String) map.get("notes"));
        if (map.containsKey("isInterStateOverride") && map.get("isInterStateOverride") != null) {
            req.setIsInterStateOverride(Boolean.TRUE.equals(map.get("isInterStateOverride")));
        }

        return req;
    }

    private Guest parseGuest(Map<String, Object> m) {
        Guest g = new Guest();
        g.setId((String) m.get("id"));
        g.setFullName((String) m.get("fullName"));
        g.setPhoneNumber((String) m.get("phoneNumber"));
        g.setEmail((String) m.get("email"));
        g.setAddress((String) m.get("address"));
        g.setCity((String) m.get("city"));
        g.setState((String) m.get("state"));
        g.setStateCode((String) m.get("stateCode"));
        g.setGstin((String) m.get("gstin"));
        g.setCompanyName((String) m.get("companyName"));
        g.setIdProofType((String) m.get("idProofType"));
        g.setIdProofNumber((String) m.get("idProofNumber"));
        return g;
    }

    private VillaBooking parseBooking(Map<String, Object> m) {
        VillaBooking b = new VillaBooking();
        b.setBookingId((String) m.get("bookingId"));
        b.setVillaNumber((String) m.get("villaNumber"));
        b.setVillaType((String) m.get("villaType"));
        b.setCheckInDate((String) m.get("checkInDate"));
        b.setCheckOutDate((String) m.get("checkOutDate"));
        if (m.containsKey("totalNights")) b.setTotalNights(asInt(m.get("totalNights")));
        if (m.containsKey("adultsCount")) b.setAdultsCount(asInt(m.get("adultsCount")));
        if (m.containsKey("childrenCount")) b.setChildrenCount(asInt(m.get("childrenCount")));
        if (m.containsKey("extraBedCount")) b.setExtraBedCount(asInt(m.get("extraBedCount")));
        if (m.containsKey("baseRatePerNight")) b.setBaseRatePerNight(asDouble(m.get("baseRatePerNight")));
        if (m.containsKey("extraBedRatePerNight")) b.setExtraBedRatePerNight(asDouble(m.get("extraBedRatePerNight")));
        b.setMealPlan((String) m.get("mealPlan"));
        return b;
    }

    private List<BillItem> parseBillItems(List<Object> list) {
        List<BillItem> items = new ArrayList<>();
        for (Object o : list) {
            if (o instanceof Map) {
                Map<String, Object> m = (Map<String, Object>) o;
                BillItem item = new BillItem();
                item.setId((String) m.get("id"));
                if (m.containsKey("category") && m.get("category") != null) {
                    try {
                        item.setCategory(ItemCategory.valueOf((String) m.get("category")));
                    } catch (Exception ignored) {
                        item.setCategory(ItemCategory.MISCELLANEOUS);
                    }
                }
                item.setDescription((String) m.get("description"));
                item.setSacCode((String) m.get("sacCode"));
                if (m.containsKey("quantity")) item.setQuantity(asDouble(m.get("quantity")));
                if (m.containsKey("unitRate")) item.setUnitRate(asDouble(m.get("unitRate")));
                if (m.containsKey("gstRate")) item.setGstRate(asDouble(m.get("gstRate")));
                items.add(item);
            }
        }
        return items;
    }

    private PaymentDetails parsePayment(Map<String, Object> m) {
        PaymentDetails p = new PaymentDetails();
        p.setPaymentMode((String) m.get("paymentMode"));
        p.setTransactionReference((String) m.get("transactionReference"));
        p.setPaymentStatus((String) m.get("paymentStatus"));
        if (m.containsKey("advancePaid")) p.setAdvancePaid(asDouble(m.get("advancePaid")));
        if (m.containsKey("discountFlat")) p.setDiscountFlat(asDouble(m.get("discountFlat")));
        if (m.containsKey("discountPercentage")) p.setDiscountPercentage(asDouble(m.get("discountPercentage")));
        return p;
    }

    private double asDouble(Object o) {
        if (o instanceof Number) return ((Number) o).doubleValue();
        if (o instanceof String) {
            try { return Double.parseDouble((String) o); } catch (Exception ignored) {}
        }
        return 0.0;
    }

    private int asInt(Object o) {
        if (o instanceof Number) return ((Number) o).intValue();
        if (o instanceof String) {
            try { return Integer.parseInt((String) o); } catch (Exception ignored) {}
        }
        return 0;
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int nRead;
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void sendCorsResponse(HttpExchange exchange, int statusCode, String msg) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.sendResponseHeaders(statusCode, msg.isEmpty() ? -1 : msg.length());
        if (!msg.isEmpty()) {
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(msg.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    private void sendErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("error", true);
        err.put("statusCode", statusCode);
        err.put("message", message);
        sendJsonResponse(exchange, statusCode, JsonUtils.toJson(err));
    }

    private String getQueryParam(String query, String paramName) {
        if (query == null || query.isEmpty()) return null;
        for (String pair : query.split("&")) {
            String[] parts = pair.split("=");
            if (parts.length > 0 && parts[0].equals(paramName)) {
                return parts.length > 1 ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8) : "";
            }
        }
        return null;
    }
}
