import { FileBlob, SpreadsheetFile } from "@oai/artifact-tool";

const outputPath = "C:/Users/Acer/IdeaProjects/Ecommerce-fullstack/outputs/ecommerce_qa_redesign/Ecommerce_QA_Test_Cases_Professional.xlsx";
const workbook = await SpreadsheetFile.importXlsx(await FileBlob.load(outputPath));

const sheets = await workbook.inspect({
  kind: "sheet,table,drawing",
  tableMaxRows: 3,
  tableMaxCols: 4,
  maxChars: 7000,
});
console.log(sheets.ndjson);

const errors = await workbook.inspect({
  kind: "match",
  searchTerm: "#REF!|#DIV/0!|#VALUE!|#NAME\\?|#N/A",
  options: { useRegex: true, maxResults: 300 },
  maxChars: 3000,
});
console.log(errors.ndjson);
