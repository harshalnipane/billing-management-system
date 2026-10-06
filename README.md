# 🏖️ Serene Palms Luxury Villa & Resort — Billing Management System
### Enterprise Java Backend & Light-Themed Web Application with Indian GST Engine

A complete, production-grade **Billing Management System** specifically architected for luxury villas, beach resorts, and boutique hospitality properties under the **Indian Goods and Services Tax (GST) Act & Council Rules**.

---

## 🌟 Key Features & Indian GST Compliance

### 1. Indian GST Hospitality Rules Engine
- **Accommodation Services (`SAC: 996311`)**:
  - Declared Room / Villa Tariff $\le$ **₹7,500/night**: **12% GST** (6% CGST + 6% SGST or 12% IGST)
  - Declared Room / Villa Tariff $>$ **₹7,500/night**: **18% GST** (9% CGST + 9% SGST or 18% IGST)
  - Automatic slab classification based on room base tariff.
- **Extra Bed & Additional Guest (`SAC: 996311`)**:
  - Taxed in alignment with the underlying accommodation tariff slab (12% or 18%).
- **Food & Beverage / Restaurant Dining (`SAC: 996331`)**:
  - Standard restaurant dining at 5% GST without ITC.
  - Specified premises / in-villa fine dining at 18% GST with ITC.
- **Spa, Ayurvedic & Wellness Treatments (`SAC: 999721`)**:
  - 18% GST (9% CGST + 9% SGST or 18% IGST).
- **Chauffeur & Airport Transport (`SAC: 996412`)**:
  - 5% GST on passenger transport.
- **Recreational Activities & Water Sports (`SAC: 999699`)**:
  - 18% GST.
- **Intra-State vs Inter-State Supply Determination**:
  - Auto-checks Guest State / GSTIN (e.g., Maharashtra `27`, Karnataka `29`, Delhi `07`) vs Resort State (Goa `30`).
  - **Intra-State Supply**: Splits GST equally into **CGST (50%) + SGST (50%)**.
  - **Inter-State Supply**: Applies **Integrated GST (IGST 100%)**.
  - Manual toggle override (`AUTO`, `INTRA`, `INTER`) available.
- **Proportional Discount Apportionment (CGST Section 15(3))**:
  - Flat or percentage discounts are attributed proportionally across billable line items prior to calculating GST.
- **Rounding Off to Nearest Rupee (CGST Rule 54)**:
  - Exact round-off calculation (HALF_UP) to ensure integer rupee settlement totals.
- **Authentic Indian Currency in Words**:
  - Converts grand total into Indian numbering system words (Crores, Lakhs, Thousands, Rupees, and Paise).

---

## 🏛️ System Architecture

```
Billing Management System/
├── pom.xml                                    # Standard Maven Project Descriptor (Java 21+)
├── run.bat                                    # 1-Click Windows Batch Runner
├── run.ps1                                    # PowerShell Runner
├── test_calc.json / test_create.json         # API Payload samples
├── src/
│   ├── main/
│   │   ├── java/com/resort/billing/
│   │   │   ├── BillingApplication.java        # Main HTTP REST Server & Bootstrap
│   │   │   ├── config/
│   │   │   │   └── AppConfig.java             # Resort profile, 30+ State codes, Villa presets
│   │   │   ├── model/
│   │   │   │   ├── Guest.java                 # Guest profile (KYC, B2B GSTIN, State code)
│   │   │   │   ├── VillaBooking.java          # Stay details (Nights, Occupancy, Extra beds)
│   │   │   │   ├── BillItem.java              # Itemized line item with SAC, rates, taxes
│   │   │   │   ├── ItemCategory.java          # Service categories & SAC enum
│   │   │   │   ├── TaxSlabBreakdown.java      # Tax summary grouped by slab (0%, 5%, 12%, 18%)
│   │   │   │   ├── PaymentDetails.java        # Payment modes, advance paid, balance due
│   │   │   │   ├── ResortProfile.java         # Property GSTIN, PAN, Bank settlement details
│   │   │   │   └── Invoice.java               # Aggregate Root for Tax Invoices
│   │   │   ├── dto/
│   │   │   │   ├── CalculateBillRequest.java  # Real-time draft preview request
│   │   │   │   ├── CalculateBillResponse.java # Computed taxes, items, slabs, words
│   │   │   │   ├── CreateInvoiceRequest.java  # Final invoice persistence request
│   │   │   │   └── DashboardSummary.java      # KPI metrics & analytics response
│   │   │   ├── service/
│   │   │   │   ├── GstCalculationService.java # Core Indian GST Calculation Engine
│   │   │   │   ├── InvoiceService.java        # Business logic coordinator
│   │   │   │   └── IndianCurrencyHelper.java  # Lakhs/Crores number to words converter
│   │   │   ├── repository/
│   │   │   │   └── InvoiceRepository.java     # Thread-safe in-memory store + preloaded data
│   │   │   ├── controller/
│   │   │   │   ├── BillingApiController.java  # REST API Handler (/api/billing/...)
│   │   │   │   └── StaticFileHandler.java     # High-speed static web asset server
│   │   │   └── util/
│   │   │       ├── JsonUtils.java             # Zero-dependency reflection JSON engine
│   │   │       └── ValidationHelper.java      # Indian GSTIN & PAN format validators
│   │   └── resources/static/
│   │       ├── index.html                     # Light-themed Responsive Single-Page App
│   │       ├── css/styles.css                 # Clean resort palette (Creams, Bronze, Sage)
│   │       └── js/app.js                      # Reactive live calculator & print engine
│   └── test/java/com/resort/billing/
│       └── GstCalculationTest.java            # Comprehensive JUnit test suite
```

---

## 🚀 How to Run the Application

The backend requires **Java 21 or higher** (tested and verified on **Java 25 LTS**). It runs standalone with zero external downloads required!

### Option 1: 1-Click Launch (Windows Batch)
Double-click `run.bat` or run in terminal:
```cmd
.\run.bat
```

### Option 2: PowerShell
```powershell
.\run.ps1
```

### Option 3: Manual Direct Run
```powershell
# 1. Compile
$files = (Get-ChildItem -Recurse -Filter *.java src\main\java).FullName
javac -encoding UTF-8 -d bin $files

# 2. Run
java -cp bin com.resort.billing.BillingApplication 8080
```

Once started, open your web browser at:
👉 **`http://localhost:8080`**

---

## 🧪 Running Automated Unit Tests
To execute the automated verification test suite:
```powershell
$testFiles = (Get-ChildItem -Recurse -Filter *.java src).FullName
javac -encoding UTF-8 -d bin $testFiles
java -cp bin com.resort.billing.GstCalculationTest
```
**Test Output:**
```
Running GST Calculation Engine Tests...
[PASS] Room Tariff Threshold Test (<= 7500 -> 12%, > 7500 -> 18%)
[PASS] Intra-State CGST + SGST Test (50/50 split)
[PASS] Inter-State IGST Test (100% IGST)
[PASS] Discount Deduction Test (Section 15(3) proportional discount)
[PASS] Indian Number to Words Test: Rupees Twenty Three Thousand Six Hundred Only
>>> ALL 5 TEST SUITES PASSED SUCCESSFULLY! <<<
```

---

## 📡 REST API Specification

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/health` | Health check & tax engine status |
| `GET` | `/api/config` | Resort profile, Indian state codes, villa types, SAC codes |
| `POST` | `/api/billing/calculate` | Real-time preview calculation of GST, items, & grand total |
| `POST` | `/api/billing/invoices` | Generate and save official GST Tax Invoice |
| `GET` | `/api/billing/invoices` | List all invoices with optional search `?q=...` |
| `GET` | `/api/billing/invoices/{id}` | Get specific invoice details by invoice number |
| `GET` | `/api/billing/dashboard` | Resort revenue, CGST, SGST, IGST, and pending dues metrics |

---

## 🎨 Frontend Design Aesthetics
- **Light Theme**: Built with soft warm creams (`#F7F5F0`), pure white cards (`#FFFFFF`), bronze accents (`#8C6D3B`), coastal sage (`#1F6E68`), and clean typography (`Cinzel`, `Outfit`, `Inter`).
- **Real-Time Live Calculation**: As dates, room category, extra beds, meals, spa services, or discounts change, the tax summary card dynamically updates with zero latency.
- **Printable Tax Invoice Modal**: Formats a tax invoice compliant with Indian GST rules, complete with SAC tables, state codes, bank details, and an authorized signatory block ready for `Ctrl+P` or PDF export.
#   b i l l i n g - m a n a g e m e n t - s y s t e m  
 #   b i l l i n g - m a n a g e m e n t - s y s t e m  
 