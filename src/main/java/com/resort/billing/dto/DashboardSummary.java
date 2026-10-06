package com.resort.billing.dto;

import com.resort.billing.model.Invoice;
import java.util.ArrayList;
import java.util.List;

public class DashboardSummary {
    private int totalInvoices;
    private double totalRevenue;
    private double totalTaxableRevenue;
    private double totalCgst;
    private double totalSgst;
    private double totalIgst;
    private double totalTax;
    private double totalAdvanceCollected;
    private double totalPendingDue;
    private List<Invoice> recentInvoices = new ArrayList<>();

    public DashboardSummary() {}

    // Getters and Setters
    public int getTotalInvoices() { return totalInvoices; }
    public void setTotalInvoices(int totalInvoices) { this.totalInvoices = totalInvoices; }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

    public double getTotalTaxableRevenue() { return totalTaxableRevenue; }
    public void setTaxableRevenue(double totalTaxableRevenue) { this.totalTaxableRevenue = totalTaxableRevenue; }

    public double getTotalCgst() { return totalCgst; }
    public void setTotalCgst(double totalCgst) { this.totalCgst = totalCgst; }

    public double getTotalSgst() { return totalSgst; }
    public void setTotalSgst(double totalSgst) { this.totalSgst = totalSgst; }

    public double getTotalIgst() { return totalIgst; }
    public void setTotalIgst(double totalIgst) { this.totalIgst = totalIgst; }

    public double getTotalTax() { return totalTax; }
    public void setTotalTax(double totalTax) { this.totalTax = totalTax; }

    public double getTotalAdvanceCollected() { return totalAdvanceCollected; }
    public void setTotalAdvanceCollected(double totalAdvanceCollected) { this.totalAdvanceCollected = totalAdvanceCollected; }

    public double getTotalPendingDue() { return totalPendingDue; }
    public void setTotalPendingDue(double totalPendingDue) { this.totalPendingDue = totalPendingDue; }

    public List<Invoice> getRecentInvoices() { return recentInvoices; }
    public void setRecentInvoices(List<Invoice> recentInvoices) { this.recentInvoices = recentInvoices; }
}
