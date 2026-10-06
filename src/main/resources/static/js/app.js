// Villa and Resort Billing Management System Frontend Application

let appConfig = null;
let currentItems = [];
let allInvoices = [];
let debounceTimer = null;
let latestCalcResponse = null;

document.addEventListener("DOMContentLoaded", () => {
  initApp();
});

async function initApp() {
  try {
    const res = await fetch("/api/config");
    if (res.ok) {
      appConfig = await res.json();
      populateConfigUI();
    }
  } catch (err) {
    console.warn("Could not fetch remote config, using defaults:", err);
  }

  setupDefaultStayDates();
  setupSampleBillingData();
  await refreshInvoicesList();
  calculatePreview();
}

function setupDefaultStayDates() {
  const today = new Date();
  const checkIn = new Date(today);
  const checkOut = new Date(today);
  checkOut.setDate(checkOut.getDate() + 3);

  document.getElementById("checkInDate").value = formatDateIso(checkIn);
  document.getElementById("checkOutDate").value = formatDateIso(checkOut);
  document.getElementById("stayNights").value = 3;
}

function formatDateIso(d) {
  const yyyy = d.getFullYear();
  const mm = String(d.getMonth() + 1).padStart(2, '0');
  const dd = String(d.getDate()).padStart(2, '0');
  return `${yyyy}-${mm}-${dd}`;
}

function autoCalcDates() {
  const inVal = document.getElementById("checkInDate").value;
  const outVal = document.getElementById("checkOutDate").value;
  if (inVal && outVal) {
    const d1 = new Date(inVal);
    const d2 = new Date(outVal);
    const diffTime = d2.getTime() - d1.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    if (diffDays > 0) {
      document.getElementById("stayNights").value = diffDays;
    }
  }
}

function populateConfigUI() {
  if (!appConfig) return;

  if (appConfig.resort) {
    document.getElementById("headerResortName").textContent = appConfig.resort.resortName;
    document.getElementById("headerGstin").textContent = "GSTIN: " + appConfig.resort.gstin;
  }

  // Populate States Dropdown
  const stateSelect = document.getElementById("guestStateSelect");
  stateSelect.innerHTML = "";
  if (appConfig.states) {
    for (const [code, name] of Object.entries(appConfig.states)) {
      const opt = document.createElement("option");
      opt.value = code;
      opt.textContent = `${name} (${code})`;
      if (code === "27") { // Default Maharashtra
        opt.selected = true;
      }
      stateSelect.appendChild(opt);
    }
  }

  // Populate Villa Presets
  const villaSelect = document.getElementById("villaPresetSelect");
  villaSelect.innerHTML = "";
  if (appConfig.villaTypes) {
    appConfig.villaTypes.forEach(v => {
      const opt = document.createElement("option");
      opt.value = v.id;
      opt.textContent = `${v.name} - ₹${v.defaultRate.toLocaleString('en-IN')}/night`;
      opt.dataset.rate = v.defaultRate;
      opt.dataset.name = v.name;
      villaSelect.appendChild(opt);
    });
  }

  onGuestStateChange();
}

function setupSampleBillingData() {
  document.getElementById("guestFullName").value = "Vikramaditya Singhania";
  document.getElementById("guestPhone").value = "9820145678";
  document.getElementById("guestEmail").value = "vikram.s@singhaniacorp.in";
  document.getElementById("guestGstin").value = "27AAACS1420M1ZK";
  document.getElementById("companyName").value = "Singhania Holdings Pvt Ltd";
  document.getElementById("guestAddress").value = "42 Nariman Point, Marine Drive, Mumbai";
  document.getElementById("guestIdType").value = "PASSPORT";
  document.getElementById("guestIdNumber").value = "Z5891402";

  document.getElementById("villaNumber").value = "Villa 101 - Ocean Breeze";
  document.getElementById("baseTariffPerNight").value = 18500;
  document.getElementById("adultsCount").value = 2;
  document.getElementById("childrenCount").value = 1;
  document.getElementById("extraBedCount").value = 1;
  document.getElementById("extraBedRate").value = 3000;
  document.getElementById("mealPlan").value = "MAP";

  document.getElementById("discountFlat").value = 5000;
  document.getElementById("discountPercent").value = 0;
  document.getElementById("advancePaid").value = 30000;
  document.getElementById("transactionRef").value = "HDFC_TXN_889211";
  document.getElementById("invoiceNotes").value = "VIP Guest. Complimentary Airport Transfer included.";

  // Sample additional items
  currentItems = [
    {
      id: "ITM-FB-1",
      category: "FOOD_AND_BEVERAGE",
      description: "In-Villa Private Chef Barbecue & Poolside Dining",
      sacCode: "996331",
      quantity: 1,
      unitRate: 14200,
      gstRate: 18
    },
    {
      id: "ITM-SPA-1",
      category: "SPA_AND_WELLNESS",
      description: "Couple Ayurvedic Abhyanga & Aroma Therapy Spa (90m)",
      sacCode: "999721",
      quantity: 1,
      unitRate: 8500,
      gstRate: 18
    },
    {
      id: "ITM-TRN-1",
      category: "AIRPORT_TRANSFER",
      description: "Luxury Airport Transfer (Mercedes V-Class)",
      sacCode: "996412",
      quantity: 2,
      unitRate: 3500,
      gstRate: 5
    }
  ];

  renderItemsTable();
  updateRoomGstSlab();
}

function onGuestStateChange() {
  const sel = document.getElementById("guestStateSelect");
  const code = sel.value;
  const name = sel.options[sel.selectedIndex]?.textContent || "";
  document.getElementById("stateCodeHint").textContent = `Place of Supply: ${name}`;
  updateSupplyTypeUI();
}

function onGuestGstinInput() {
  const gstin = document.getElementById("guestGstin").value.trim().toUpperCase();
  if (gstin.length >= 2) {
    const code = gstin.substring(0, 2);
    const sel = document.getElementById("guestStateSelect");
    for (let i = 0; i < sel.options.length; i++) {
      if (sel.options[i].value === code) {
        sel.selectedIndex = i;
        onGuestStateChange();
        break;
      }
    }
  }
  updateSupplyTypeUI();
}

function updateRoomGstSlab() {
  const rate = parseFloat(document.getElementById("baseTariffPerNight").value) || 0;
  const badge = document.getElementById("roomGstRateBadge");
  const notice = document.getElementById("tariffRuleNotice");

  if (rate <= 7500) {
    badge.textContent = "GST Slab: 12% (≤ ₹7,500/night)";
    badge.style.background = "#E8F5E9";
    badge.style.color = "#2E7D32";
    notice.textContent = "Current tariff ₹" + rate.toLocaleString('en-IN') + " qualifies for 12% GST slab (6% CGST + 6% SGST)";
  } else {
    badge.textContent = "GST Slab: 18% (> ₹7,500/night)";
    badge.style.background = "#FFF3E0";
    badge.style.color = "#E65100";
    notice.textContent = "Current tariff ₹" + rate.toLocaleString('en-IN') + " qualifies for luxury 18% GST slab (9% CGST + 9% SGST)";
  }
}

function onVillaPresetChange() {
  const sel = document.getElementById("villaPresetSelect");
  const opt = sel.options[sel.selectedIndex];
  if (opt && opt.dataset.rate) {
    document.getElementById("baseTariffPerNight").value = opt.dataset.rate;
    updateRoomGstSlab();
  }
}

function updateSupplyTypeUI() {
  const guestCode = document.getElementById("guestStateSelect").value;
  const resortCode = appConfig?.resort?.stateCode || "30";
  const override = document.getElementById("supplyOverrideSelect")?.value || "AUTO";

  let isInterState = false;
  if (override === "INTER") {
    isInterState = true;
  } else if (override === "INTRA") {
    isInterState = false;
  } else {
    isInterState = guestCode !== resortCode;
  }

  const indicator = document.getElementById("supplyTypeIndicator");
  const summaryBadge = document.getElementById("supplyBadgeSummary");
  const intraRows = document.getElementById("intraStateTaxRows");
  const interRows = document.getElementById("interStateTaxRows");

  if (isInterState) {
    indicator.className = "tax-status-pill status-inter";
    indicator.textContent = "INTER-STATE (IGST 100%)";
    summaryBadge.className = "tax-status-pill status-inter";
    summaryBadge.textContent = "INTER-STATE (IGST)";
    intraRows.style.display = "none";
    interRows.style.display = "block";
  } else {
    indicator.className = "tax-status-pill status-intra";
    indicator.textContent = "INTRA-STATE (CGST 50% + SGST 50%)";
    summaryBadge.className = "tax-status-pill status-intra";
    summaryBadge.textContent = "INTRA-STATE (CGST + SGST)";
    intraRows.style.display = "block";
    interRows.style.display = "none";
  }
}

function renderItemsTable() {
  const tbody = document.getElementById("itemsTableBody");
  tbody.innerHTML = "";

  if (currentItems.length === 0) {
    tbody.innerHTML = `<tr><td colspan="10" style="text-align: center; color: var(--text-muted); padding: 1.5rem;">No additional items added. Room stay tariff is calculated automatically.</td></tr>`;
    return;
  }

  currentItems.forEach((item, idx) => {
    const gross = (item.quantity * item.unitRate);
    const estTax = (gross * (item.gstRate / 100));
    const total = gross + estTax;

    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td><span class="item-badge ${getCategoryBadgeClass(item.category)}">${formatCategory(item.category)}</span></td>
      <td><code>${item.sacCode || '996311'}</code></td>
      <td style="font-weight: 500;">${item.description}</td>
      <td style="text-align: center;">${item.quantity}</td>
      <td style="text-align: right;">₹ ${item.unitRate.toLocaleString('en-IN', {minimumFractionDigits: 2})}</td>
      <td style="text-align: center;"><span class="gst-slab-pill">${item.gstRate}%</span></td>
      <td style="text-align: right;">₹ ${gross.toLocaleString('en-IN', {minimumFractionDigits: 2})}</td>
      <td style="text-align: right; color: var(--accent-teal);">₹ ${estTax.toLocaleString('en-IN', {minimumFractionDigits: 2})}</td>
      <td style="text-align: right; font-weight: 600;">₹ ${total.toLocaleString('en-IN', {minimumFractionDigits: 2})}</td>
      <td style="text-align: center;">
        <button type="button" class="btn btn-danger-outline btn-sm" onclick="removeItem(${idx})">🗑️</button>
      </td>
    `;
    tbody.appendChild(tr);
  });
}

function getCategoryBadgeClass(cat) {
  switch (cat) {
    case "ACCOMMODATION": return "badge-accommodation";
    case "FOOD_AND_BEVERAGE": return "badge-fb";
    case "SPA_AND_WELLNESS": return "badge-spa";
    case "AIRPORT_TRANSFER": return "badge-transport";
    default: return "badge-other";
  }
}

function formatCategory(cat) {
  if (!cat) return "Other";
  return cat.replace(/_/g, " ").toLowerCase().replace(/\b\w/g, l => l.toUpperCase());
}

function addPresetItem(cat, desc, sac, qty, rate, gst) {
  currentItems.push({
    id: "ITM-" + Date.now(),
    category: cat,
    description: desc,
    sacCode: sac,
    quantity: qty,
    unitRate: rate,
    gstRate: gst
  });
  renderItemsTable();
  calculatePreview();
}

function removeItem(index) {
  currentItems.splice(index, 1);
  renderItemsTable();
  calculatePreview();
}

// Quick Add Modal Logic
function showQuickAddModal() {
  document.getElementById("quickAddModal").style.display = "block";
}

function closeQuickAddModal() {
  document.getElementById("quickAddModal").style.display = "none";
}

function onModalCategoryChange() {
  const cat = document.getElementById("modalCategory").value;
  const sacInput = document.getElementById("modalSac");
  const gstSel = document.getElementById("modalGstRate");

  switch (cat) {
    case "FOOD_AND_BEVERAGE":
      sacInput.value = "996331";
      gstSel.value = "18";
      break;
    case "SPA_AND_WELLNESS":
      sacInput.value = "999721";
      gstSel.value = "18";
      break;
    case "AIRPORT_TRANSFER":
      sacInput.value = "996412";
      gstSel.value = "5";
      break;
    case "RESORT_ACTIVITIES":
      sacInput.value = "999699";
      gstSel.value = "18";
      break;
    case "LAUNDRY_SERVICE":
      sacInput.value = "999799";
      gstSel.value = "18";
      break;
    case "MINI_BAR":
      sacInput.value = "996332";
      gstSel.value = "18";
      break;
    case "BANQUET_EVENT":
      sacInput.value = "997212";
      gstSel.value = "18";
      break;
    default:
      sacInput.value = "999900";
      gstSel.value = "18";
  }
}

function confirmAddModalItem() {
  const desc = document.getElementById("modalDesc").value.trim();
  if (!desc) {
    alert("Please enter item description");
    return;
  }
  const cat = document.getElementById("modalCategory").value;
  const sac = document.getElementById("modalSac").value;
  const gst = parseFloat(document.getElementById("modalGstRate").value);
  const qty = parseFloat(document.getElementById("modalQty").value) || 1;
  const rate = parseFloat(document.getElementById("modalRate").value) || 0;

  currentItems.push({
    id: "ITM-" + Date.now(),
    category: cat,
    description: desc,
    sacCode: sac,
    quantity: qty,
    unitRate: rate,
    gstRate: gst
  });

  closeQuickAddModal();
  renderItemsTable();
  calculatePreview();
}

// Live GST Calculation Preview
function calculatePreview() {
  clearTimeout(debounceTimer);
  debounceTimer = setTimeout(executeCalculatePreview, 120);
}

async function executeCalculatePreview() {
  updateSupplyTypeUI();

  const reqPayload = buildCalculatePayload();

  try {
    const res = await fetch("/api/billing/calculate", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(reqPayload)
    });

    if (res.ok) {
      latestCalcResponse = await res.json();
      renderLiveSummary(latestCalcResponse);
    }
  } catch (err) {
    console.error("Calculation preview error:", err);
  }
}

function buildCalculatePayload() {
  const stateSel = document.getElementById("guestStateSelect");
  const stateCode = stateSel?.value || "30";
  const stateName = stateSel?.options[stateSel.selectedIndex]?.text?.split(" (")[0] || "Goa";

  const guest = {
    fullName: document.getElementById("guestFullName").value,
    phoneNumber: document.getElementById("guestPhone").value,
    email: document.getElementById("guestEmail").value,
    address: document.getElementById("guestAddress").value,
    state: stateName,
    stateCode: stateCode,
    gstin: document.getElementById("guestGstin").value.trim().toUpperCase(),
    companyName: document.getElementById("companyName").value,
    idProofType: document.getElementById("guestIdType").value,
    idProofNumber: document.getElementById("guestIdNumber").value
  };

  const booking = {
    bookingId: "RES-" + Math.floor(1000 + Math.random() * 9000),
    villaNumber: document.getElementById("villaNumber").value,
    villaType: document.getElementById("villaPresetSelect").options[document.getElementById("villaPresetSelect").selectedIndex]?.textContent?.split(" - ")[0] || "Villa Stay",
    checkInDate: document.getElementById("checkInDate").value,
    checkOutDate: document.getElementById("checkOutDate").value,
    totalNights: parseInt(document.getElementById("stayNights").value) || 1,
    adultsCount: parseInt(document.getElementById("adultsCount").value) || 2,
    childrenCount: parseInt(document.getElementById("childrenCount").value) || 0,
    extraBedCount: parseInt(document.getElementById("extraBedCount").value) || 0,
    baseRatePerNight: parseFloat(document.getElementById("baseTariffPerNight").value) || 0,
    extraBedRatePerNight: parseFloat(document.getElementById("extraBedRate").value) || 0,
    mealPlan: document.getElementById("mealPlan").value
  };

  const overrideVal = document.getElementById("supplyOverrideSelect")?.value;
  let override = null;
  if (overrideVal === "INTER") override = true;
  if (overrideVal === "INTRA") override = false;

  return {
    guest: guest,
    booking: booking,
    additionalItems: currentItems,
    discountFlat: parseFloat(document.getElementById("discountFlat").value) || 0,
    discountPercentage: parseFloat(document.getElementById("discountPercent").value) || 0,
    advancePaid: parseFloat(document.getElementById("advancePaid").value) || 0,
    isInterStateOverride: override
  };
}

function renderLiveSummary(calc) {
  if (!calc) return;

  document.getElementById("summaryGross").textContent = formatInr(calc.grossSubtotal);
  document.getElementById("summaryDiscount").textContent = "- " + formatInr(calc.totalDiscount);
  document.getElementById("summaryTaxable").textContent = formatInr(calc.taxableAmount);

  document.getElementById("summaryCgst").textContent = formatInr(calc.cgstAmount);
  document.getElementById("summarySgst").textContent = formatInr(calc.sgstAmount);
  document.getElementById("summaryIgst").textContent = formatInr(calc.igstAmount);
  document.getElementById("summaryTotalTax").textContent = formatInr(calc.totalTax);
  document.getElementById("summaryRoundOff").textContent = (calc.roundOff >= 0 ? "+ " : "- ") + formatInr(Math.abs(calc.roundOff));

  document.getElementById("summaryGrandTotal").textContent = formatInr(calc.grandTotal);
  document.getElementById("summaryAdvance").textContent = "- " + formatInr(calc.advancePaid);
  document.getElementById("summaryBalanceDue").textContent = formatInr(calc.balanceDue);
  document.getElementById("summaryAmountWords").textContent = calc.amountInWords || "Rupees Zero Only";

  // Tax Slabs Table
  const slabBody = document.getElementById("slabTableBody");
  slabBody.innerHTML = "";
  if (calc.slabBreakdowns && calc.slabBreakdowns.length > 0) {
    calc.slabBreakdowns.forEach(s => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td><strong>${s.ratePercent}%</strong></td>
        <td>₹ ${s.taxableAmount.toLocaleString('en-IN', {minimumFractionDigits: 2})}</td>
        <td>₹ ${s.cgstAmount.toLocaleString('en-IN', {minimumFractionDigits: 2})}</td>
        <td>₹ ${s.sgstAmount.toLocaleString('en-IN', {minimumFractionDigits: 2})}</td>
        <td>₹ ${s.igstAmount.toLocaleString('en-IN', {minimumFractionDigits: 2})}</td>
        <td><strong>₹ ${s.totalTax.toLocaleString('en-IN', {minimumFractionDigits: 2})}</strong></td>
      `;
      slabBody.appendChild(tr);
    });
  }
}

function formatInr(val) {
  if (val === undefined || val === null || isNaN(val)) return "₹ 0.00";
  return "₹ " + val.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

// Generate Invoice Submission
async function submitInvoice() {
  const calcPayload = buildCalculatePayload();

  const req = {
    guest: calcPayload.guest,
    booking: calcPayload.booking,
    items: currentItems,
    payment: {
      paymentMode: document.getElementById("paymentMode").value,
      transactionReference: document.getElementById("transactionRef").value,
      advancePaid: calcPayload.advancePaid,
      discountFlat: calcPayload.discountFlat,
      discountPercentage: calcPayload.discountPercentage
    },
    isInterStateOverride: calcPayload.isInterStateOverride,
    notes: document.getElementById("invoiceNotes").value
  };

  try {
    const res = await fetch("/api/billing/invoices", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(req)
    });

    if (res.ok) {
      const createdInvoice = await res.json();
      await refreshInvoicesList();
      renderInvoiceModal(createdInvoice);
    } else {
      const err = await res.json();
      alert("Error generating invoice: " + (err.message || "Server Error"));
    }
  } catch (err) {
    alert("Network error creating invoice: " + err);
  }
}

// Preview Modal with current draft data
function previewCurrentBillModal() {
  if (!latestCalcResponse) return;

  const calcPayload = buildCalculatePayload();
  const mockInvoice = {
    invoiceNumber: "DRAFT-PREVIEW",
    invoiceDate: formatDateIso(new Date()),
    invoiceType: "TAX_INVOICE (DRAFT)",
    resortProfile: appConfig?.resort || {},
    guest: calcPayload.guest,
    booking: calcPayload.booking,
    items: latestCalcResponse.items,
    isInterState: latestCalcResponse.isInterState,
    placeOfSupply: latestCalcResponse.isInterState ? (calcPayload.guest.state + " (" + calcPayload.guest.stateCode + ")") : "Goa (30)",
    subtotalGross: latestCalcResponse.grossSubtotal,
    totalDiscount: latestCalcResponse.totalDiscount,
    totalTaxable: latestCalcResponse.taxableAmount,
    totalCgst: latestCalcResponse.cgstAmount,
    totalSgst: latestCalcResponse.sgstAmount,
    totalIgst: latestCalcResponse.igstAmount,
    totalTax: latestCalcResponse.totalTax,
    roundOff: latestCalcResponse.roundOff,
    grandTotal: latestCalcResponse.grandTotal,
    amountInWords: latestCalcResponse.amountInWords,
    taxSlabs: latestCalcResponse.slabBreakdowns,
    payment: {
      paymentMode: document.getElementById("paymentMode").value,
      transactionReference: document.getElementById("transactionRef").value,
      advancePaid: latestCalcResponse.advancePaid,
      balanceDue: latestCalcResponse.balanceDue,
      paymentStatus: latestCalcResponse.balanceDue <= 0 ? "PAID" : "PARTIAL"
    },
    notes: document.getElementById("invoiceNotes").value
  };

  renderInvoiceModal(mockInvoice);
}

// Render Full GST Tax Invoice Sheet
function renderInvoiceModal(inv) {
  const container = document.getElementById("invoiceModalContent");
  const resort = inv.resortProfile || appConfig?.resort || {};
  const guest = inv.guest || {};
  const booking = inv.booking || {};
  const payment = inv.payment || {};

  let itemsRows = "";
  (inv.items || []).forEach((item, i) => {
    const cgstCell = inv.isInterState ? "<td>-</td>" : `<td>${item.cgstRate}%<br>₹${(item.cgstAmount || 0).toFixed(2)}</td>`;
    const sgstCell = inv.isInterState ? "<td>-</td>" : `<td>${item.sgstRate}%<br>₹${(item.sgstAmount || 0).toFixed(2)}</td>`;
    const igstCell = inv.isInterState ? `<td>${item.igstRate}%<br>₹${(item.igstAmount || 0).toFixed(2)}</td>` : "<td>-</td>";

    itemsRows += `
      <tr>
        <td style="text-align: center;">${i + 1}</td>
        <td><strong>${item.description}</strong></td>
        <td style="text-align: center;"><code>${item.sacCode || '996311'}</code></td>
        <td style="text-align: center;">${item.quantity}</td>
        <td style="text-align: right;">₹ ${(item.unitRate || 0).toLocaleString('en-IN', {minimumFractionDigits: 2})}</td>
        <td style="text-align: right;">₹ ${(item.discountAmount || 0).toLocaleString('en-IN', {minimumFractionDigits: 2})}</td>
        <td style="text-align: right;">₹ ${(item.taxableAmount || 0).toLocaleString('en-IN', {minimumFractionDigits: 2})}</td>
        ${cgstCell}
        ${sgstCell}
        ${igstCell}
        <td style="text-align: right; font-weight: 600;">₹ ${(item.totalAmount || 0).toLocaleString('en-IN', {minimumFractionDigits: 2})}</td>
      </tr>
    `;
  });

  let slabRows = "";
  (inv.taxSlabs || []).forEach(s => {
    slabRows += `
      <tr>
        <td>SAC 9963 / ${s.ratePercent}% GST Slab</td>
        <td>₹ ${s.taxableAmount.toLocaleString('en-IN', {minimumFractionDigits: 2})}</td>
        <td>₹ ${s.cgstAmount.toLocaleString('en-IN', {minimumFractionDigits: 2})}</td>
        <td>₹ ${s.sgstAmount.toLocaleString('en-IN', {minimumFractionDigits: 2})}</td>
        <td>₹ ${s.igstAmount.toLocaleString('en-IN', {minimumFractionDigits: 2})}</td>
        <td style="font-weight: 600;">₹ ${s.totalTax.toLocaleString('en-IN', {minimumFractionDigits: 2})}</td>
      </tr>
    `;
  });

  container.innerHTML = `
    <div class="tax-invoice-sheet">
      
      <!-- Top Header -->
      <div class="inv-header">
        <div>
          <div class="inv-resort-title">${resort.resortName || 'Serene Palms Luxury Villa & Resort'}</div>
          <div style="font-size: 0.82rem; color: #4B5563; margin-top: 2px;">${resort.tagline || 'Coastal Sanctuary & Private Pool Villas'}</div>
          <div style="font-size: 0.8rem; color: #374151; margin-top: 4px;">
            ${resort.address || 'Candolim Beach Road, North Goa'}, ${resort.city || 'Goa'} - 403515<br>
            <strong>GSTIN:</strong> ${resort.gstin || '30AABCR1234F1Z5'} | <strong>PAN:</strong> ${resort.pan || 'AABCR1234F'} | <strong>State:</strong> ${resort.state || 'Goa'} (Code: ${resort.stateCode || '30'})<br>
            <strong>Contact:</strong> ${resort.contactPhone || '+91 832 249 8800'} | <strong>Email:</strong> ${resort.contactEmail || 'billing@serenepalmsresort.in'}
          </div>
        </div>
        <div class="inv-badge-title">
          <h2>TAX INVOICE</h2>
          <div style="font-size: 0.78rem; color: #6B7280; font-style: italic;">Original for Recipient</div>
          <div style="font-size: 0.88rem; font-weight: 700; margin-top: 6px; color: #8C6D3B;">${inv.invoiceNumber}</div>
          <div style="font-size: 0.82rem;"><strong>Date:</strong> ${inv.invoiceDate}</div>
          <div style="font-size: 0.8rem;"><strong>Place of Supply:</strong> ${inv.placeOfSupply}</div>
        </div>
      </div>

      <!-- Guest & Stay Info Grid -->
      <div class="inv-info-grid">
        <div>
          <div style="font-weight: 700; color: #8C6D3B; margin-bottom: 4px; text-transform: uppercase; font-size: 0.78rem; letter-spacing: 0.5px;">Billed To (Guest Details)</div>
          <div style="font-size: 0.95rem; font-weight: 600;">${guest.fullName || 'Guest'}</div>
          ${guest.companyName ? `<div style="font-weight: 600; color: #1F2937;">${guest.companyName}</div>` : ''}
          <div style="font-size: 0.82rem; color: #4B5563;">
            ${guest.address || ''}<br>
            <strong>State:</strong> ${guest.state || ''} (Code: ${guest.stateCode || ''})<br>
            <strong>Mobile:</strong> ${guest.phoneNumber || ''} | <strong>Email:</strong> ${guest.email || ''}<br>
            ${guest.gstin ? `<strong style="color: #1F6E68;">Guest GSTIN (B2B): ${guest.gstin}</strong><br>` : ''}
            <strong>ID Proof:</strong> ${guest.idProofType || 'Aadhaar'}: ${guest.idProofNumber || 'Verified'}
          </div>
        </div>

        <div>
          <div style="font-weight: 700; color: #8C6D3B; margin-bottom: 4px; text-transform: uppercase; font-size: 0.78rem; letter-spacing: 0.5px;">Reservation & Stay Details</div>
          <div style="font-size: 0.82rem; line-height: 1.5;">
            <strong>Villa / Unit:</strong> ${booking.villaNumber || ''} (${booking.villaType || ''})<br>
            <strong>Check-In:</strong> ${booking.checkInDate || ''} | <strong>Check-Out:</strong> ${booking.checkOutDate || ''}<br>
            <strong>Total Stay:</strong> ${booking.totalNights || 1} Night(s) | <strong>Meal Plan:</strong> ${booking.mealPlan || 'CP'}<br>
            <strong>Occupancy:</strong> ${booking.adultsCount || 2} Adults, ${booking.childrenCount || 0} Child, ${booking.extraBedCount || 0} Extra Bed<br>
            <strong>Supply Classification:</strong> <span style="font-weight: 600; color: ${inv.isInterState ? '#1565C0' : '#2E7D32'};">${inv.isInterState ? 'Inter-State Supply (IGST)' : 'Intra-State Supply (CGST + SGST)'}</span>
          </div>
        </div>
      </div>

      <!-- Items Table -->
      <table class="inv-items-table">
        <thead>
          <tr>
            <th style="width: 30px;">#</th>
            <th>Item Description</th>
            <th style="width: 65px;">SAC</th>
            <th style="width: 45px;">Qty</th>
            <th style="width: 85px;">Rate (₹)</th>
            <th style="width: 75px;">Disc (₹)</th>
            <th style="width: 90px;">Taxable (₹)</th>
            ${inv.isInterState ? '' : '<th style="width: 75px;">CGST</th><th style="width: 75px;">SGST</th>'}
            ${inv.isInterState ? '<th style="width: 75px;">IGST</th>' : ''}
            <th style="width: 100px;">Total (₹)</th>
          </tr>
        </thead>
        <tbody>
          ${itemsRows}
        </tbody>
      </table>

      <!-- Summary & Totals Grid -->
      <div style="display: grid; grid-template-columns: 1fr 340px; gap: 1.5rem; margin-bottom: 1rem;">
        
        <div>
          <div style="font-weight: 700; font-size: 0.78rem; color: #374151; margin-bottom: 4px;">HSN / SAC Tax Slab Analysis</div>
          <table class="inv-tax-summary-table">
            <thead>
              <tr>
                <th>SAC Description</th>
                <th>Taxable Val</th>
                <th>CGST</th>
                <th>SGST</th>
                <th>IGST</th>
                <th>Total Tax</th>
              </tr>
            </thead>
            <tbody>
              ${slabRows}
            </tbody>
          </table>

          <div style="background: #F9FAFB; border: 1px solid #E5E7EB; padding: 0.65rem 0.85rem; border-radius: 4px; font-size: 0.8rem; margin-top: 0.75rem;">
            <strong>Amount Chargeable in Words:</strong><br>
            <span style="font-style: italic; color: #1F2937;">${inv.amountInWords || 'Rupees Zero Only'}</span>
          </div>
        </div>

        <!-- Right Totals Table -->
        <div style="background: #FAF8F5; border: 1px solid #E8E3D9; border-radius: 6px; padding: 0.85rem; font-size: 0.84rem;">
          <div style="display: flex; justify-content: space-between; padding: 3px 0;">
            <span>Subtotal Gross:</span>
            <span>₹ ${(inv.subtotalGross || 0).toLocaleString('en-IN', {minimumFractionDigits: 2})}</span>
          </div>
          <div style="display: flex; justify-content: space-between; padding: 3px 0; color: #DC2626;">
            <span>Total Discount:</span>
            <span>- ₹ ${(inv.totalDiscount || 0).toLocaleString('en-IN', {minimumFractionDigits: 2})}</span>
          </div>
          <div style="display: flex; justify-content: space-between; padding: 3px 0; font-weight: 600;">
            <span>Taxable Value:</span>
            <span>₹ ${(inv.totalTaxable || 0).toLocaleString('en-IN', {minimumFractionDigits: 2})}</span>
          </div>

          <hr style="border: none; border-top: 1px solid #E2DDD5; margin: 4px 0;">

          ${!inv.isInterState ? `
            <div style="display: flex; justify-content: space-between; padding: 3px 0;">
              <span>Central GST (CGST):</span>
              <span>₹ ${(inv.totalCgst || 0).toLocaleString('en-IN', {minimumFractionDigits: 2})}</span>
            </div>
            <div style="display: flex; justify-content: space-between; padding: 3px 0;">
              <span>State GST (SGST):</span>
              <span>₹ ${(inv.totalSgst || 0).toLocaleString('en-IN', {minimumFractionDigits: 2})}</span>
            </div>
          ` : `
            <div style="display: flex; justify-content: space-between; padding: 3px 0;">
              <span>Integrated GST (IGST):</span>
              <span>₹ ${(inv.totalIgst || 0).toLocaleString('en-IN', {minimumFractionDigits: 2})}</span>
            </div>
          `}

          <div style="display: flex; justify-content: space-between; padding: 3px 0;">
            <span>Round Off (Rule 54):</span>
            <span>${(inv.roundOff || 0) >= 0 ? '+' : ''}₹ ${(inv.roundOff || 0).toFixed(2)}</span>
          </div>

          <div style="display: flex; justify-content: space-between; padding: 8px 0; border-top: 2px solid #8C6D3B; font-size: 1.1rem; font-weight: 700; color: #8C6D3B;">
            <span>Grand Total:</span>
            <span>₹ ${(inv.grandTotal || 0).toLocaleString('en-IN', {minimumFractionDigits: 2})}</span>
          </div>

          <div style="display: flex; justify-content: space-between; padding: 3px 0; color: #16A34A;">
            <span>Advance Paid:</span>
            <span>- ₹ ${(payment.advancePaid || 0).toLocaleString('en-IN', {minimumFractionDigits: 2})}</span>
          </div>

          <div style="display: flex; justify-content: space-between; padding: 6px 0; font-weight: 700; background: #FFF4E5; padding: 6px 8px; border-radius: 4px; color: #9A3412; margin-top: 4px;">
            <span>Balance Due:</span>
            <span>₹ ${(payment.balanceDue || 0).toLocaleString('en-IN', {minimumFractionDigits: 2})}</span>
          </div>
        </div>

      </div>

      <!-- Bank Details & Signatory Section -->
      <div class="inv-bank-section">
        <div>
          <strong>Bank Account Details for Direct Transfer (RTGS / NEFT / IMPS):</strong><br>
          Bank: ${resort.bankName || 'HDFC Bank Ltd'} | A/C No: ${resort.bankAccountNumber || '50200088921473'}<br>
          IFSC Code: ${resort.bankIfsc || 'HDFC0000452'} | Branch: ${resort.bankBranch || 'Candolim, Goa'}<br>
          Resort UPI VPA: <code>${resort.upiId || 'serenepalms@hdfcbank'}</code><br>
          <div style="font-size: 0.74rem; color: #6B7280; margin-top: 4px;">
            Terms: 1. Interest @ 18% p.a. will be charged if unpaid after 15 days. 2. Subject to Goa Jurisdiction only.
          </div>
        </div>

        <div>
          <div class="signatory-box">
            <div style="font-weight: 600; font-size: 0.8rem; color: #1F2937;">For ${resort.resortName || 'Serene Palms Resort'}</div>
            <div style="font-size: 0.75rem; color: #6B7280;">Authorized Signatory</div>
          </div>
        </div>
      </div>

    </div>
  `;

  document.getElementById("invoiceModal").style.display = "block";
}

function closeInvoiceModal() {
  document.getElementById("invoiceModal").style.display = "none";
}

// Invoices List Management
async function refreshInvoicesList() {
  try {
    const res = await fetch("/api/billing/invoices");
    if (res.ok) {
      allInvoices = await res.json();
      document.getElementById("invoiceCountBadge").textContent = allInvoices.length;
      renderInvoicesList(allInvoices);
      updateDashboardData();
    }
  } catch (err) {
    console.warn("Could not fetch invoices list:", err);
  }
}

function renderInvoicesList(invoices) {
  const tbody = document.getElementById("invoicesListTableBody");
  tbody.innerHTML = "";

  if (invoices.length === 0) {
    tbody.innerHTML = `<tr><td colspan="11" style="text-align: center; color: var(--text-muted); padding: 2rem;">No invoices generated yet.</td></tr>`;
    return;
  }

  invoices.forEach(inv => {
    const tr = document.createElement("tr");
    const statusClass = inv.payment?.paymentStatus === "PAID" ? "badge-transport" : (inv.payment?.paymentStatus === "PARTIAL" ? "badge-accommodation" : "badge-fb");
    
    tr.innerHTML = `
      <td><strong>${inv.invoiceNumber}</strong></td>
      <td>${inv.invoiceDate}</td>
      <td>${inv.guest?.fullName || '-'}</td>
      <td>${inv.booking?.villaNumber || '-'}</td>
      <td><span class="tax-status-pill ${inv.isInterState ? 'status-inter' : 'status-intra'}">${inv.isInterState ? 'IGST' : 'CGST+SGST'}</span></td>
      <td style="text-align: right;">₹ ${(inv.totalTaxable || 0).toLocaleString('en-IN', {minimumFractionDigits: 2})}</td>
      <td style="text-align: right; color: var(--accent-teal);">₹ ${(inv.totalTax || 0).toLocaleString('en-IN', {minimumFractionDigits: 2})}</td>
      <td style="text-align: right; font-weight: 600;">₹ ${(inv.grandTotal || 0).toLocaleString('en-IN', {minimumFractionDigits: 2})}</td>
      <td style="text-align: right; color: #B45309;">₹ ${(inv.payment?.balanceDue || 0).toLocaleString('en-IN', {minimumFractionDigits: 2})}</td>
      <td style="text-align: center;"><span class="item-badge ${statusClass}">${inv.payment?.paymentStatus || 'PAID'}</span></td>
      <td style="text-align: center;">
        <button class="btn btn-secondary btn-sm" onclick="viewInvoiceDetails('${inv.invoiceNumber}')">
          👁️ View & Print
        </button>
      </td>
    `;
    tbody.appendChild(tr);
  });
}

function filterInvoices(query) {
  if (!query || query.trim() === "") {
    renderInvoicesList(allInvoices);
    return;
  }
  const q = query.toLowerCase().trim();
  const filtered = allInvoices.filter(inv => {
    return (inv.invoiceNumber && inv.invoiceNumber.toLowerCase().includes(q))
      || (inv.guest?.fullName && inv.guest.fullName.toLowerCase().includes(q))
      || (inv.booking?.villaNumber && inv.booking.villaNumber.toLowerCase().includes(q))
      || (inv.guest?.phoneNumber && inv.guest.phoneNumber.includes(q));
  });
  renderInvoicesList(filtered);
}

async function viewInvoiceDetails(invoiceNumber) {
  try {
    const res = await fetch("/api/billing/invoices/" + encodeURIComponent(invoiceNumber));
    if (res.ok) {
      const inv = await res.json();
      renderInvoiceModal(inv);
    }
  } catch (err) {
    alert("Error fetching invoice: " + err);
  }
}

// Dashboard Analytics
async function updateDashboardData() {
  try {
    const res = await fetch("/api/billing/dashboard");
    if (res.ok) {
      const d = await res.json();
      document.getElementById("dashTotalInvoices").textContent = d.totalInvoices;
      document.getElementById("dashTotalRevenue").textContent = formatInr(d.totalRevenue);
      document.getElementById("dashTaxableRevenue").textContent = formatInr(d.totalTaxableRevenue);
      document.getElementById("dashTotalTax").textContent = formatInr(d.totalTax);
      document.getElementById("dashCgst").textContent = formatInr(d.totalCgst);
      document.getElementById("dashSgst").textContent = formatInr(d.totalSgst);
      document.getElementById("dashIgst").textContent = formatInr(d.totalIgst);
      document.getElementById("dashPending").textContent = formatInr(d.totalPendingDue);
    }
  } catch (err) {
    console.warn("Error updating dashboard:", err);
  }
}

// Tab Switching
function switchTab(tab) {
  document.getElementById("viewNewBill").style.display = tab === "newBill" ? "block" : "none";
  document.getElementById("viewInvoices").style.display = tab === "invoices" ? "block" : "none";
  document.getElementById("viewDashboard").style.display = tab === "dashboard" ? "block" : "none";

  document.getElementById("tabNewBillBtn").className = "tab-btn " + (tab === "newBill" ? "active" : "");
  document.getElementById("tabInvoicesBtn").className = "tab-btn " + (tab === "invoices" ? "active" : "");
  document.getElementById("tabDashboardBtn").className = "tab-btn " + (tab === "dashboard" ? "active" : "");

  if (tab === "invoices") {
    refreshInvoicesList();
  } else if (tab === "dashboard") {
    updateDashboardData();
  }
}

function resetFormToSample() {
  setupSampleBillingData();
  calculatePreview();
}
