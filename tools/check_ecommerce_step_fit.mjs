import { FileBlob, SpreadsheetFile } from "@oai/artifact-tool";

const inputPath = "outputs/ecommerce-qa-test-cases/Ecommerce_QA_Test_Cases_expanded_steps_fit.xlsx";
const input = await FileBlob.load(inputPath);
const workbook = await SpreadsheetFile.importXlsx(input);

const sheetInfo = await workbook.inspect({ kind: "sheet", include: "id,name", maxChars: 4000 });
const rows = [];

for (const line of sheetInfo.ndjson.trim().split("\n")) {
  const item = JSON.parse(line);
  if (item.kind !== "sheet") continue;
  const sheet = workbook.worksheets.getItem(item.name);
  const values = sheet.getUsedRange().values;
  for (const row of values) {
    if (!/^TC-/i.test(String(row[0] || ""))) continue;
    const text = String(row[4] || "");
    const wrappedLines = text
      .split("\n")
      .reduce((total, stepLine) => total + Math.max(1, Math.ceil(stepLine.length / 95)), 0);
    rows.push({
      sheet: item.name,
      id: row[0],
      chars: text.length,
      stepLines: text.split("\n").length,
      estimatedWrappedLines: wrappedLines,
      estimatedRowHeight: Math.min(409, Math.max(120, wrappedLines * 17 + 18)),
    });
  }
}

rows.sort((a, b) => b.estimatedWrappedLines - a.estimatedWrappedLines);
console.log(JSON.stringify(rows.slice(0, 10), null, 2));
