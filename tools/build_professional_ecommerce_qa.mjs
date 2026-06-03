import fs from "node:fs/promises";
import { SpreadsheetFile, Workbook } from "@oai/artifact-tool";

const csvPath = "C:/Users/Acer/IdeaProjects/Ecommerce-fullstack/ecommerce_test_cases.csv";
const outputDir = "C:/Users/Acer/IdeaProjects/Ecommerce-fullstack/outputs/ecommerce_qa_redesign";
const outputPath = `${outputDir}/Ecommerce_QA_Test_Cases_Professional.xlsx`;

const modules = [
  "Homepage",
  "Authentication",
  "Product Listing",
  "Product Details",
  "Cart & Checkout",
  "User Profile",
  "Order Management",
  "Original Test Cases",
];

const prefixes = {
  Homepage: "HOME",
  Authentication: "AUTH",
  "Product Listing": "PLP",
  "Product Details": "PDP",
  "Cart & Checkout": "CHK",
  "User Profile": "PROF",
  "Order Management": "ORD",
  "Original Test Cases": "SRC",
};

const columns = [
  "Test Case ID",
  "Module",
  "Test Scenario",
  "Preconditions",
  "Test Steps",
  "Test Data",
  "Expected Result",
  "Actual Result",
  "Status",
  "Priority",
  "Remarks",
];

const colors = {
  navy: "#17324D",
  blue: "#1F4E79",
  teal: "#0F766E",
  paleBlue: "#EAF2F8",
  surface: "#F4F7FA",
  white: "#FFFFFF",
  text: "#1F2937",
  muted: "#64748B",
  border: "#D6DEE6",
  red: "#FEE2E2",
  redText: "#991B1B",
  orange: "#FFEDD5",
  orangeText: "#92400E",
  green: "#DCFCE7",
  greenText: "#166534",
  purple: "#EDE9FE",
  purpleText: "#5B21B6",
};

function parseCsv(text) {
  const rows = [];
  let row = [];
  let value = "";
  let inQuotes = false;
  for (let i = 0; i < text.length; i += 1) {
    const char = text[i];
    const next = text[i + 1];
    if (char === '"' && inQuotes && next === '"') {
      value += '"';
      i += 1;
    } else if (char === '"') {
      inQuotes = !inQuotes;
    } else if (char === "," && !inQuotes) {
      row.push(value);
      value = "";
    } else if ((char === "\n" || char === "\r") && !inQuotes) {
      if (char === "\r" && next === "\n") i += 1;
      row.push(value);
      if (row.some((cell) => cell.trim() !== "")) rows.push(row);
      row = [];
      value = "";
    } else {
      value += char;
    }
  }
  if (value || row.length) {
    row.push(value);
    rows.push(row);
  }
  return rows;
}

function clean(value) {
  return String(value ?? "").replace(/\s+/g, " ").trim();
}

function moduleFor(sourceModule, name, objective) {
  const source = clean(sourceModule).toLowerCase();
  const text = `${name} ${objective}`.toLowerCase();
  if (source.includes("authentication")) return "Authentication";
  if (source.includes("user profile")) return "User Profile";
  if (source.includes("home page")) return "Homepage";
  if (text.includes("cart") || text.includes("checkout") || text.includes("payment") || text.includes("coupon")) return "Cart & Checkout";
  if (text.includes("order") || text.includes("invoice") || text.includes("tracking") || text.includes("history")) return "Order Management";
  if (text.includes("profile") || text.includes("address") || text.includes("wishlist")) return "User Profile";
  if (text.includes("product detail") || text.includes("review") || text.includes("rating")) return "Product Details";
  if (text.includes("listing") || text.includes("filter") || text.includes("sort") || text.includes("search") || text.includes("category")) return "Product Listing";
  if (text.includes("home") || text.includes("banner") || text.includes("navigation") || text.includes("menu")) return "Homepage";
  return "Authentication";
}

function priorityFor(name, objective, expected) {
  const text = `${name} ${objective} ${expected}`.toLowerCase();
  if (text.includes("payment") || text.includes("checkout") || text.includes("login") || text.includes("password") || text.includes("security") || text.includes("session") || text.includes("registration")) return "High";
  if (text.includes("cart") || text.includes("order") || text.includes("search") || text.includes("filter") || text.includes("price")) return "Medium";
  return "Low";
}

function statusFor(actual) {
  const text = clean(actual).toLowerCase();
  if (text === "pass" || text === "passed") return "Pass";
  if (text === "fail" || text === "failed") return "Failed";
  if (text.includes("block")) return "Blocked";
  return text ? "Pending" : "Pending";
}

function testDataFor(name, objective) {
  const text = `${name} ${objective}`.toLowerCase();
  if (text.includes("invalid email")) return "Email: abc";
  if (text.includes("duplicate")) return "Email: existing.user@example.com";
  if (text.includes("short password")) return "Password: 12345";
  if (text.includes("login")) return "Email: qa.user@example.com; Password: Valid@123";
  if (text.includes("register") || text.includes("signup")) return "Name: QA User; Email: unique.qa@example.com; Password: Valid@123";
  if (text.includes("search")) return "Keyword: wireless headphones";
  if (text.includes("cart")) return "Product: Test SKU 1001; Qty: 2";
  if (text.includes("checkout") || text.includes("payment")) return "Card: 4111 1111 1111 1111; Exp: 12/30; CVV: 123";
  return "Standard QA test data";
}

const requestedCases = [
  ["Homepage", "Homepage UI validation", "Guest user opens the ecommerce site", "1. Launch the application. 2. Verify logo, banner, featured categories, product cards, and footer alignment.", "Desktop and mobile viewport", "Homepage loads with clean layout, no broken images, and consistent branding.", "As expected", "Pass", "Medium", "Professional UI smoke test."],
  ["Homepage", "Navigation menu validation", "Guest user is on homepage", "1. Click each navigation item. 2. Verify destination and active state.", "Home, Products, Cart, Login, Profile", "Every navigation item routes correctly without broken links.", "As expected", "Pass", "High", "Requested sample."],
  ["Product Listing", "Search functionality with valid keyword", "Products exist for the keyword", "1. Enter keyword. 2. Submit search. 3. Verify result relevance and count.", "Keyword: headphones", "Relevant products are displayed and result count updates.", "As expected", "Pass", "High", "Requested sample."],
  ["Product Listing", "Search functionality with no results", "No product matches the keyword", "1. Enter uncommon keyword. 2. Submit search.", "Keyword: zzz-no-product", "Clear no-result message appears with recovery action.", "", "Pending", "Medium", "Empty-state coverage."],
  ["Authentication", "Login validation with valid credentials", "Registered user exists", "1. Open login page. 2. Enter valid email/password. 3. Click Login.", "qa.user@example.com / Valid@123", "User logs in and is redirected to the expected page.", "As expected", "Pass", "High", "Requested sample."],
  ["Authentication", "Login validation with invalid credentials", "Registered user exists", "1. Open login page. 2. Enter valid email and wrong password. 3. Click Login.", "qa.user@example.com / Wrong@123", "System shows validation error and does not create a session.", "As expected", "Pass", "High", "Negative auth flow."],
  ["Authentication", "Signup validation with valid data", "Guest user is on signup page", "1. Enter name, email, password, confirm password. 2. Submit.", "QA User / unique.qa@example.com / Valid@123", "Account is created successfully.", "As expected", "Pass", "High", "Requested sample."],
  ["Authentication", "Signup validation with duplicate email", "Email already exists", "1. Enter existing email. 2. Submit signup form.", "existing.user@example.com", "Duplicate email message appears and account is not created.", "As expected", "Pass", "High", "Requested sample."],
  ["Authentication", "Forgot password flow", "Registered email exists", "1. Open forgot password. 2. Enter email. 3. Submit request.", "qa.user@example.com", "Reset confirmation is shown or reset email is sent.", "", "Pending", "High", "Requested sample."],
  ["Authentication", "Logout validation", "User is logged in", "1. Click Logout. 2. Try to open a protected page.", "Protected page: Profile", "Session is cleared and protected page redirects to login.", "As expected", "Pass", "High", "Authentication module."],
  ["Authentication", "Session timeout validation", "User is logged in and idle timeout is configured", "1. Stay idle until timeout. 2. Perform protected action.", "Configured idle threshold", "User is logged out or asked to re-authenticate.", "", "Pending", "High", "Requested sample."],
  ["Product Details", "Product details page validation", "Product exists", "1. Open product details. 2. Verify title, image, price, stock, reviews, and CTA.", "SKU: QA-1001", "All product information is accurate and complete.", "As expected", "Pass", "High", "PDP coverage."],
  ["Product Details", "Quantity boundary validation", "Product is in stock", "1. Increase quantity. 2. Try zero, negative, and above-stock quantity.", "Stock: 5", "Quantity stays within allowed boundaries.", "", "Pending", "High", "Inventory guardrail."],
  ["Cart & Checkout", "Add product to cart", "Product is available", "1. Click Add to Cart. 2. Open cart.", "SKU: QA-1001; Qty: 1", "Cart shows correct product, price, and quantity.", "As expected", "Pass", "High", "Requested cart operation."],
  ["Cart & Checkout", "Update cart quantity", "Cart has one item", "1. Increase quantity. 2. Decrease quantity. 3. Verify totals.", "Qty: 1 to 3 to 2", "Line total and grand total recalculate correctly.", "As expected", "Pass", "High", "Requested cart operation."],
  ["Cart & Checkout", "Remove item from cart", "Cart has one item", "1. Click Remove. 2. Confirm item is removed.", "SKU: QA-1001", "Cart item is removed and totals update.", "As expected", "Pass", "High", "Requested cart operation."],
  ["Cart & Checkout", "Checkout shipping address validation", "Cart has products", "1. Proceed to checkout. 2. Enter shipping address. 3. Continue.", "221B QA Street, Test City", "Shipping form validates and saves address.", "", "Pending", "High", "Requested checkout flow."],
  ["Cart & Checkout", "Payment flow with valid card", "User is on payment step", "1. Enter card details. 2. Place order.", "4111 1111 1111 1111 / 12-30 / 123", "Payment succeeds and order confirmation appears.", "", "Pending", "High", "Requested payment flow."],
  ["Cart & Checkout", "Payment failure handling", "User is on payment step", "1. Enter declined card. 2. Submit payment.", "4000 0000 0000 0002", "Payment error appears and paid order is not created.", "", "Pending", "High", "Critical negative flow."],
  ["User Profile", "Profile details validation", "User is logged in", "1. Open profile page. 2. Verify name, email, phone, and address.", "qa.user@example.com", "Profile details are displayed accurately.", "As expected", "Pass", "Medium", "Profile module."],
  ["Order Management", "Order confirmation validation", "Checkout completed successfully", "1. Open confirmation page. 2. Verify order ID, amount, product list, and address.", "Latest order", "Order confirmation details match checkout details.", "", "Pending", "High", "Order module."],
  ["Order Management", "Order history validation", "User has previous orders", "1. Open order history. 2. Verify status, amount, date, and details link.", "qa.user@example.com", "Order history displays accurate order records.", "As expected", "Pass", "Medium", "Order module."],
];

function sourceCasesFromCsv(rows) {
  return rows.slice(1).filter((row) => row.length >= 8).map((row) => {
    const [sourceModule, name, objective, preconditions, steps, expected, actual, comments] = row;
    const module = moduleFor(sourceModule, name, objective);
    const status = statusFor(actual);
    return {
      module,
      scenario: clean(name),
      preconditions: clean(preconditions) || "None",
      steps: clean(steps),
      data: testDataFor(name, objective),
      expected: clean(expected),
      actual: status === "Pass" ? "As expected" : "",
      status,
      priority: priorityFor(name, objective, expected),
      remarks: `Original Excel: ${clean(comments)}`,
    };
  });
}

function dedupe(cases) {
  const seen = new Set();
  return cases.filter((testCase) => {
    const key = `${testCase.module}|${testCase.scenario}`.toLowerCase();
    if (seen.has(key)) return false;
    seen.add(key);
    return true;
  });
}

function withIds(cases) {
  const counters = Object.fromEntries(modules.map((module) => [module, 0]));
  return cases.map((testCase) => {
    counters[testCase.module] += 1;
    return {
      ...testCase,
      id: `TC-${prefixes[testCase.module]}-${String(counters[testCase.module]).padStart(3, "0")}`,
    };
  });
}

function rowFor(testCase) {
  return [
    testCase.id,
    testCase.module,
    testCase.scenario,
    testCase.preconditions,
    testCase.steps,
    testCase.data,
    testCase.expected,
    testCase.actual,
    testCase.status,
    testCase.priority,
    testCase.remarks,
  ];
}

function style(range, format) {
  range.format = format;
}

function setWidths(sheet, widths) {
  widths.forEach((width, index) => {
    const col = String.fromCharCode(65 + index);
    sheet.getRange(`${col}1:${col}1`).format.columnWidth = width;
  });
}

function applyStatusPriorityFormats(sheet, startRow, count) {
  for (let i = 0; i < count; i += 1) {
    const row = startRow + i;
    const statusCell = sheet.getRange(`I${row}`);
    const priorityCell = sheet.getRange(`J${row}`);
    const status = clean(statusCell.values[0][0]);
    const priority = clean(priorityCell.values[0][0]);
    if (status === "Pass") style(statusCell, { fill: colors.green, font: { color: colors.greenText, bold: true }, horizontalAlignment: "center" });
    if (status === "Failed") style(statusCell, { fill: colors.red, font: { color: colors.redText, bold: true }, horizontalAlignment: "center" });
    if (status === "Pending" || status === "Not Run") style(statusCell, { fill: colors.orange, font: { color: colors.orangeText, bold: true }, horizontalAlignment: "center" });
    if (status === "Blocked") style(statusCell, { fill: colors.purple, font: { color: colors.purpleText, bold: true }, horizontalAlignment: "center" });
    if (priority === "High") style(priorityCell, { fill: colors.red, font: { color: colors.redText, bold: true }, horizontalAlignment: "center" });
    if (priority === "Medium") style(priorityCell, { fill: colors.orange, font: { color: colors.orangeText, bold: true }, horizontalAlignment: "center" });
    if (priority === "Low") style(priorityCell, { fill: colors.green, font: { color: colors.greenText, bold: true }, horizontalAlignment: "center" });
  }
}

function createModuleSheet(workbook, module, cases) {
  const sheet = workbook.worksheets.add(module);
  sheet.showGridLines = false;
  sheet.getRange("A1:K1").merge();
  sheet.getRange("A1").values = [[`Ecommerce QA Test Cases - ${module}`]];
  style(sheet.getRange("A1:K1"), { fill: colors.navy, font: { color: colors.white, bold: true, size: 16 }, horizontalAlignment: "center", verticalAlignment: "center" });
  sheet.getRange("A1:K1").format.rowHeightPx = 36;

  sheet.getRange("A2:K3").values = [
    ["Project", "Ecommerce Fullstack QA", "", "Module", module, "", "Prepared For", "Interview / Portfolio / Real QA Usage", "", "Updated", new Date()],
    ["Format", "QA Industry Standard", "", "Execution", "Manual + Automation Ready", "", "Legend", "Priority and status are color-coded", "", "Owner", "QA Team"],
  ];
  style(sheet.getRange("A2:K3"), { fill: colors.paleBlue, font: { color: colors.text, size: 10 }, verticalAlignment: "center" });
  sheet.getRange("A2:A3").format.font = { bold: true, color: colors.blue };
  sheet.getRange("D2:D3").format.font = { bold: true, color: colors.blue };
  sheet.getRange("G2:G3").format.font = { bold: true, color: colors.blue };
  sheet.getRange("J2:J3").format.font = { bold: true, color: colors.blue };
  sheet.getRange("K2").setNumberFormat("yyyy-mm-dd");

  sheet.getRange("A5:K5").values = [columns];
  style(sheet.getRange("A5:K5"), { fill: colors.blue, font: { color: colors.white, bold: true, size: 10 }, horizontalAlignment: "center", verticalAlignment: "center", wrapText: true });
  sheet.getRange("A5:K5").format.rowHeightPx = 32;

  const data = cases.map(rowFor);
  if (data.length) {
    sheet.getRangeByIndexes(5, 0, data.length, columns.length).values = data;
    style(sheet.getRangeByIndexes(5, 0, data.length, columns.length), { font: { color: colors.text, size: 10 }, verticalAlignment: "top", wrapText: true });
    sheet.getRangeByIndexes(5, 0, data.length, columns.length).format.rowHeightPx = 58;
    sheet.getRangeByIndexes(5, 0, data.length, 2).format.horizontalAlignment = "center";
    sheet.getRangeByIndexes(5, 8, data.length, 2).format.horizontalAlignment = "center";
  }

  const endRow = Math.max(6, data.length + 5);
  const table = sheet.tables.add(`A5:K${endRow}`, true, `${prefixes[module]}_TestCases`);
  table.style = "TableStyleMedium2";
  table.showFilterButton = true;
  table.showBandedRows = true;
  sheet.getRange("I6:I205").dataValidation = { rule: { type: "list", values: ["Pass", "Failed", "Pending", "Blocked", "Not Run"] } };
  sheet.getRange("J6:J205").dataValidation = { rule: { type: "list", values: ["High", "Medium", "Low"] } };
  applyStatusPriorityFormats(sheet, 6, data.length);
  setWidths(sheet, [14, 18, 34, 30, 44, 28, 40, 24, 13, 12, 34]);
  sheet.freezePanes.freezeRows(5);
}

function sq(name) {
  return `'${name.replaceAll("'", "''")}'`;
}

function createDashboard(workbook) {
  const sheet = workbook.worksheets.getItem("Test Summary Dashboard");
  const dashboardModules = modules.filter((module) => module !== "Original Test Cases");
  sheet.showGridLines = false;
  sheet.getRange("A1:K1").merge();
  sheet.getRange("A1").values = [["Ecommerce QA Test Summary Dashboard"]];
  style(sheet.getRange("A1:K1"), { fill: colors.navy, font: { color: colors.white, bold: true, size: 18 }, horizontalAlignment: "center", verticalAlignment: "center" });
  sheet.getRange("A1:K1").format.rowHeightPx = 42;
  sheet.getRange("A2:K2").merge();
  sheet.getRange("A2").values = [["Professional QA workbook with all original Excel test cases preserved, plus module-wise coverage, filters, priority/status highlighting, bug tracking, and execution charts."]];
  style(sheet.getRange("A2:K2"), { fill: colors.surface, font: { color: colors.muted, italic: true, size: 10 }, horizontalAlignment: "center" });

  const total = `=SUM(${dashboardModules.map((m) => `COUNTA(${sq(m)}!$A$6:$A$205)`).join(",")})`;
  const countStatus = (status) => `=SUM(${dashboardModules.map((m) => `COUNTIF(${sq(m)}!$I$6:$I$205,"${status}")`).join(",")})`;
  sheet.getRange("A4:F5").values = [
    ["Total Test Cases", "Passed Cases", "Failed Cases", "Pending Cases", "Blocked Cases", "Pass Percentage"],
    ["", "", "", "", "", ""],
  ];
  sheet.getRange("A5").formulas = [[total]];
  sheet.getRange("B5").formulas = [[countStatus("Pass")]];
  sheet.getRange("C5").formulas = [[countStatus("Failed")]];
  sheet.getRange("D5").formulas = [[`=SUM(${modules.map((m) => `COUNTIF(${sq(m)}!$I$6:$I$205,"Pending")+COUNTIF(${sq(m)}!$I$6:$I$205,"Not Run")`).join(",")})`]];
  sheet.getRange("E5").formulas = [[countStatus("Blocked")]];
  sheet.getRange("F5").formulas = [["=IF(A5=0,0,B5/A5)"]];
  sheet.getRange("F5").setNumberFormat("0.0%");
  style(sheet.getRange("A4:F4"), { fill: colors.blue, font: { color: colors.white, bold: true }, horizontalAlignment: "center" });
  style(sheet.getRange("A5:F5"), { fill: colors.white, font: { color: colors.navy, bold: true, size: 18 }, horizontalAlignment: "center", verticalAlignment: "center" });
  sheet.getRange("A4:F5").format.rowHeightPx = 32;

  sheet.getRange("A8:D8").values = [["Module", "Total Cases", "Passed", "Open / Not Passed"]];
  style(sheet.getRange("A8:D8"), { fill: colors.teal, font: { color: colors.white, bold: true }, horizontalAlignment: "center" });
  sheet.getRangeByIndexes(8, 0, dashboardModules.length, 1).values = dashboardModules.map((module) => [module]);
  sheet.getRangeByIndexes(8, 1, dashboardModules.length, 3).formulas = dashboardModules.map((module, index) => [
    `=COUNTA(${sq(module)}!$A$6:$A$205)`,
    `=COUNTIF(${sq(module)}!$I$6:$I$205,"Pass")`,
    `=B${9 + index}-C${9 + index}`,
  ]);
  sheet.tables.add(`A8:D${8 + dashboardModules.length}`, true, "ModuleExecutionSummary").style = "TableStyleMedium4";

  sheet.getRange("F8:G13").values = [["Status", "Count"], ["Pass", ""], ["Failed", ""], ["Pending", ""], ["Blocked", ""], ["Not Run", ""]];
  sheet.getRange("G9").formulas = [[countStatus("Pass")]];
  sheet.getRange("G10").formulas = [[countStatus("Failed")]];
  sheet.getRange("G11").formulas = [[countStatus("Pending")]];
  sheet.getRange("G12").formulas = [[countStatus("Blocked")]];
  sheet.getRange("G13").formulas = [[countStatus("Not Run")]];
  style(sheet.getRange("F8:G8"), { fill: colors.blue, font: { color: colors.white, bold: true }, horizontalAlignment: "center" });

  sheet.getRange("F16:G19").values = [["Priority", "Count"], ["High", ""], ["Medium", ""], ["Low", ""]];
  sheet.getRange("G17").formulas = [[`=SUM(${dashboardModules.map((m) => `COUNTIF(${sq(m)}!$J$6:$J$205,"High")`).join(",")})`]];
  sheet.getRange("G18").formulas = [[`=SUM(${dashboardModules.map((m) => `COUNTIF(${sq(m)}!$J$6:$J$205,"Medium")`).join(",")})`]];
  sheet.getRange("G19").formulas = [[`=SUM(${dashboardModules.map((m) => `COUNTIF(${sq(m)}!$J$6:$J$205,"Low")`).join(",")})`]];
  style(sheet.getRange("F16:G16"), { fill: colors.teal, font: { color: colors.white, bold: true }, horizontalAlignment: "center" });

  sheet.getRange("A22:K22").merge();
  sheet.getRange("A22").values = [["Workbook Navigation"]];
  style(sheet.getRange("A22:K22"), { fill: colors.paleBlue, font: { color: colors.navy, bold: true, size: 12 }, horizontalAlignment: "center" });
  sheet.getRange("A23:B31").values = [
    ["Homepage", "Homepage UI, navigation, responsiveness, and entry-point checks"],
    ["Authentication", "Login, signup, forgot password, logout, and session timeout"],
    ["Product Listing", "Catalog, search, filters, sorting, pagination, and no-result states"],
    ["Product Details", "PDP information, image gallery, quantity, stock, and add-to-cart"],
    ["Cart & Checkout", "Cart operations, checkout, shipping, payment, and totals"],
    ["User Profile", "Profile display/edit, password change, and address management"],
    ["Order Management", "Confirmation, order history, details, cancellation, and invoice"],
    ["Original Test Cases", "Exact imported test cases from your original Excel/CSV source"],
    ["Bug Report", "Defect log with severity, priority, reproduction steps, and status"],
  ];
  style(sheet.getRange("A23:B31"), { fill: colors.white, font: { color: colors.text, size: 10 }, wrapText: true });

  const pie = sheet.charts.add("pie", sheet.getRange("F8:G13"));
  pie.title = "Execution Status Distribution";
  pie.hasLegend = true;
  pie.setPosition("I4", "K15");
  const bar = sheet.charts.add("bar", sheet.getRange("F8:G13"));
  bar.title = "Execution Status Count";
  bar.hasLegend = false;
  bar.yAxis = { numberFormatCode: "0" };
  bar.setPosition("I17", "K30");
  setWidths(sheet, [15, 14, 14, 17, 15, 14, 12, 4, 28, 20, 20]);
  sheet.freezePanes.freezeRows(4);
}

function createOriginalCasesSheet(workbook, csvRows) {
  const sheet = workbook.worksheets.add("Original Test Cases");
  sheet.showGridLines = false;
  const sourceHeaders = [
    "Original ID",
    "Source Module/Category",
    "Test Case Name",
    "Objective",
    "Prerequisites",
    "Test Steps",
    "Expected Result",
    "Actual Result",
    "Comments",
  ];
  const rows = csvRows.slice(1).filter((row) => row.length >= 8).map((row, index) => [
    `SRC-${String(index + 1).padStart(3, "0")}`,
    ...row.slice(0, 8).map(clean),
  ]);

  sheet.getRange("A1:I1").merge();
  sheet.getRange("A1").values = [["Original Test Cases From Provided Excel"]];
  style(sheet.getRange("A1:I1"), { fill: colors.navy, font: { color: colors.white, bold: true, size: 16 }, horizontalAlignment: "center", verticalAlignment: "center" });
  sheet.getRange("A1:I1").format.rowHeightPx = 36;
  sheet.getRange("A3:I3").values = [sourceHeaders];
  style(sheet.getRange("A3:I3"), { fill: colors.blue, font: { color: colors.white, bold: true }, horizontalAlignment: "center", wrapText: true });
  sheet.getRangeByIndexes(3, 0, rows.length, sourceHeaders.length).values = rows;
  style(sheet.getRangeByIndexes(3, 0, rows.length, sourceHeaders.length), { fill: colors.white, font: { color: colors.text, size: 10 }, wrapText: true, verticalAlignment: "top" });
  const table = sheet.tables.add(`A3:I${3 + rows.length}`, true, "OriginalExcelTestCases");
  table.style = "TableStyleMedium2";
  table.showFilterButton = true;
  table.showBandedRows = true;
  setWidths(sheet, [12, 34, 34, 42, 28, 48, 42, 16, 44]);
  sheet.getRangeByIndexes(3, 0, rows.length, sourceHeaders.length).format.rowHeightPx = 52;
  sheet.freezePanes.freezeRows(3);
}

function createBugReport(workbook) {
  const sheet = workbook.worksheets.add("Bug Report");
  sheet.showGridLines = false;
  const headers = ["Bug ID", "Module", "Linked Test Case ID", "Severity", "Priority", "Bug Summary", "Steps to Reproduce", "Expected Result", "Actual Result", "Status", "Remarks / Owner"];
  const rows = [
    ["BUG-ECOM-001", "Authentication", "TC-AUTH-002", "Major", "High", "Invalid login message needs clearer UX", "1. Open Login. 2. Enter wrong password. 3. Submit.", "Clear validation message with no session created.", "Generic error message appears.", "Open", "Owner: QA / Dev"],
    ["BUG-ECOM-002", "Cart & Checkout", "TC-CHK-006", "Critical", "High", "Grand total does not refresh after coupon removal", "1. Apply coupon. 2. Remove coupon. 3. Review total.", "Grand total recalculates immediately.", "Discount remains until refresh.", "Open", "Regression required after fix."],
    ["BUG-ECOM-003", "Homepage", "TC-HOME-001", "Minor", "Medium", "Mobile banner spacing needs polish", "1. Open homepage at mobile width. 2. Inspect hero section.", "Text and CTA remain readable.", "CTA sits too close to banner edge.", "In Progress", "UI polish."],
  ];
  sheet.getRange("A1:K1").merge();
  sheet.getRange("A1").values = [["Ecommerce QA Bug Report"]];
  style(sheet.getRange("A1:K1"), { fill: colors.navy, font: { color: colors.white, bold: true, size: 16 }, horizontalAlignment: "center" });
  sheet.getRange("A3:K3").values = [headers];
  style(sheet.getRange("A3:K3"), { fill: colors.blue, font: { color: colors.white, bold: true }, horizontalAlignment: "center", wrapText: true });
  sheet.getRangeByIndexes(3, 0, rows.length, headers.length).values = rows;
  style(sheet.getRangeByIndexes(3, 0, rows.length, headers.length), { fill: colors.white, font: { color: colors.text, size: 10 }, wrapText: true, verticalAlignment: "top" });
  const table = sheet.tables.add(`A3:K${3 + rows.length}`, true, "BugReportLog");
  table.style = "TableStyleMedium2";
  table.showFilterButton = true;
  table.showBandedRows = true;
  sheet.getRange("D4:D205").dataValidation = { rule: { type: "list", values: ["Critical", "Major", "Minor", "Trivial"] } };
  sheet.getRange("E4:E205").dataValidation = { rule: { type: "list", values: ["High", "Medium", "Low"] } };
  sheet.getRange("J4:J205").dataValidation = { rule: { type: "list", values: ["New", "Open", "In Progress", "Fixed", "Retest", "Closed", "Deferred"] } };
  setWidths(sheet, [14, 18, 18, 13, 13, 34, 44, 37, 37, 15, 25]);
  sheet.freezePanes.freezeRows(3);
}

const csvRows = parseCsv(await fs.readFile(csvPath, "utf8"));
const existingCases = sourceCasesFromCsv(csvRows);
const generatedCases = requestedCases.map(([module, scenario, preconditions, steps, data, expected, actual, status, priority, remarks]) => ({
  module, scenario, preconditions, steps, data, expected, actual, status, priority, remarks,
}));
const moduleTabs = modules.filter((module) => module !== "Original Test Cases");
const allCases = withIds(dedupe([...generatedCases, ...existingCases]).filter((testCase) => moduleTabs.includes(testCase.module)));
const casesByModule = Object.fromEntries(moduleTabs.map((module) => [module, allCases.filter((testCase) => testCase.module === module)]));

const workbook = Workbook.create();
workbook.worksheets.add("Test Summary Dashboard");
for (const module of moduleTabs) createModuleSheet(workbook, module, casesByModule[module]);
createOriginalCasesSheet(workbook, csvRows);
createBugReport(workbook);
createDashboard(workbook);

const dashboard = await workbook.inspect({
  kind: "table",
  range: "Test Summary Dashboard!A1:K30",
  include: "values,formulas",
  tableMaxRows: 32,
  tableMaxCols: 11,
  maxChars: 9000,
});
console.log(dashboard.ndjson);

const errors = await workbook.inspect({
  kind: "match",
  searchTerm: "#REF!|#DIV/0!|#VALUE!|#NAME\\?|#N/A",
  options: { useRegex: true, maxResults: 300 },
  maxChars: 3000,
});
console.log(errors.ndjson);

await fs.mkdir(outputDir, { recursive: true });
const xlsx = await SpreadsheetFile.exportXlsx(workbook);
await xlsx.save(outputPath);
console.log(outputPath);
