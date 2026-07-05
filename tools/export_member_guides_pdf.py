from __future__ import annotations

import argparse
import html
import re
from pathlib import Path

from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER, TA_LEFT
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import mm
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.platypus import (
    BaseDocTemplate,
    Frame,
    HRFlowable,
    KeepTogether,
    PageBreak,
    PageTemplate,
    Paragraph,
    Preformatted,
    Spacer,
    Table,
    TableStyle,
)
from reportlab.platypus.tableofcontents import TableOfContents


PAGE_WIDTH, PAGE_HEIGHT = A4
NAVY = colors.HexColor("#17324D")
TEAL = colors.HexColor("#087E8B")
PALE_TEAL = colors.HexColor("#E8F5F5")
INK = colors.HexColor("#1B2633")
MUTED = colors.HexColor("#5C6B78")
LINE = colors.HexColor("#D9E2E8")
PAPER = colors.HexColor("#FAFCFD")
CODE_BG = colors.HexColor("#F2F6F8")


def register_fonts() -> None:
    fonts = Path("C:/Windows/Fonts")
    candidates = {
        "GuideSans": fonts / "arial.ttf",
        "GuideSans-Bold": fonts / "arialbd.ttf",
        "GuideSans-Italic": fonts / "ariali.ttf",
        "GuideMono": fonts / "consola.ttf",
        "GuideMono-Bold": fonts / "consolab.ttf",
    }
    for name, path in candidates.items():
        if path.exists():
            pdfmetrics.registerFont(TTFont(name, str(path)))


def clean_text(value: str) -> str:
    replacements = {
        "\u2011": "-",
        "\u2012": "-",
        "\u2013": "-",
        "\u2014": "-",
        "\u2192": "->",
        "\u2190": "<-",
        "\u00a0": " ",
        "\u2018": "'",
        "\u2019": "'",
        "\u201c": '"',
        "\u201d": '"',
    }
    for old, new in replacements.items():
        value = value.replace(old, new)
    return value


def inline_markup(value: str) -> str:
    value = clean_text(value.strip())
    placeholders: list[str] = []

    def protect_code(match: re.Match[str]) -> str:
        token = f"@@CODE{len(placeholders)}@@"
        placeholders.append(
            f'<font name="GuideMono" color="#27566B">{html.escape(match.group(1))}</font>'
        )
        return token

    value = re.sub(r"`([^`]+)`", protect_code, value)
    value = html.escape(value)
    value = re.sub(r"\*\*([^*]+)\*\*", r"<b>\1</b>", value)
    value = re.sub(r"(?<!\*)\*([^*]+)\*(?!\*)", r"<i>\1</i>", value)
    for index, replacement in enumerate(placeholders):
        value = value.replace(html.escape(f"@@CODE{index}@@"), replacement)
    return value


def styles() -> dict[str, ParagraphStyle]:
    base = getSampleStyleSheet()
    return {
        "title": ParagraphStyle(
            "GuideTitle",
            parent=base["Title"],
            fontName="GuideSans-Bold",
            fontSize=27,
            leading=33,
            textColor=colors.white,
            alignment=TA_LEFT,
            spaceAfter=8 * mm,
        ),
        "subtitle": ParagraphStyle(
            "GuideSubtitle",
            parent=base["BodyText"],
            fontName="GuideSans",
            fontSize=12,
            leading=18,
            textColor=colors.HexColor("#D8EEF2"),
            alignment=TA_LEFT,
        ),
        "chapter": ParagraphStyle(
            "Chapter",
            parent=base["Heading1"],
            fontName="GuideSans-Bold",
            fontSize=20,
            leading=25,
            textColor=NAVY,
            spaceAfter=5 * mm,
            keepWithNext=True,
        ),
        "h2": ParagraphStyle(
            "H2",
            parent=base["Heading2"],
            fontName="GuideSans-Bold",
            fontSize=13.5,
            leading=17,
            textColor=TEAL,
            spaceBefore=4 * mm,
            spaceAfter=2 * mm,
            keepWithNext=True,
        ),
        "h3": ParagraphStyle(
            "H3",
            parent=base["Heading3"],
            fontName="GuideSans-Bold",
            fontSize=11.5,
            leading=15,
            textColor=NAVY,
            spaceBefore=3 * mm,
            spaceAfter=1.5 * mm,
            keepWithNext=True,
        ),
        "body": ParagraphStyle(
            "Body",
            parent=base["BodyText"],
            fontName="GuideSans",
            fontSize=9.4,
            leading=13.6,
            textColor=INK,
            spaceAfter=1.8 * mm,
            allowWidows=0,
            allowOrphans=0,
        ),
        "bullet": ParagraphStyle(
            "Bullet",
            parent=base["BodyText"],
            fontName="GuideSans",
            fontSize=9.2,
            leading=13.2,
            textColor=INK,
            leftIndent=6 * mm,
            firstLineIndent=-3.5 * mm,
            bulletIndent=1.5 * mm,
            spaceAfter=1.1 * mm,
        ),
        "number": ParagraphStyle(
            "Number",
            parent=base["BodyText"],
            fontName="GuideSans",
            fontSize=9.15,
            leading=13.1,
            textColor=INK,
            leftIndent=8 * mm,
            firstLineIndent=-7 * mm,
            spaceAfter=1.6 * mm,
        ),
        "quote": ParagraphStyle(
            "Quote",
            parent=base["BodyText"],
            fontName="GuideSans-Italic",
            fontSize=9.3,
            leading=13.5,
            textColor=MUTED,
            leftIndent=7 * mm,
            rightIndent=5 * mm,
            borderColor=TEAL,
            borderWidth=0,
            borderPadding=(2 * mm, 3 * mm, 2 * mm, 4 * mm),
            backColor=PALE_TEAL,
            spaceAfter=3 * mm,
        ),
        "code": ParagraphStyle(
            "Code",
            parent=base["Code"],
            fontName="GuideMono",
            fontSize=7.1,
            leading=9.2,
            textColor=colors.HexColor("#183B4A"),
            leftIndent=4 * mm,
            rightIndent=4 * mm,
            borderPadding=3 * mm,
            backColor=CODE_BG,
            borderColor=LINE,
            borderWidth=0.5,
            borderRadius=2,
            spaceBefore=1 * mm,
            spaceAfter=3 * mm,
        ),
        "toc_title": ParagraphStyle(
            "TOCTitle",
            parent=base["Heading1"],
            fontName="GuideSans-Bold",
            fontSize=22,
            leading=27,
            textColor=NAVY,
            spaceAfter=7 * mm,
        ),
        "toc1": ParagraphStyle(
            "TOC1",
            fontName="GuideSans-Bold",
            fontSize=10.5,
            leading=16,
            leftIndent=0,
            firstLineIndent=0,
            textColor=NAVY,
            spaceBefore=1.5 * mm,
        ),
        "toc2": ParagraphStyle(
            "TOC2",
            fontName="GuideSans",
            fontSize=8.5,
            leading=12,
            leftIndent=7 * mm,
            firstLineIndent=0,
            textColor=MUTED,
        ),
        "table_header": ParagraphStyle(
            "TableHeader",
            parent=base["BodyText"],
            fontName="GuideSans-Bold",
            fontSize=8,
            leading=10,
            textColor=colors.white,
        ),
        "table_cell": ParagraphStyle(
            "TableCell",
            parent=base["BodyText"],
            fontName="GuideSans",
            fontSize=7.7,
            leading=10.2,
            textColor=INK,
        ),
        "footer": ParagraphStyle(
            "Footer",
            parent=base["BodyText"],
            fontName="GuideSans",
            fontSize=7,
            textColor=MUTED,
        ),
    }


class HandbookDocTemplate(BaseDocTemplate):
    def __init__(self, filename: str, style_map: dict[str, ParagraphStyle]):
        super().__init__(
            filename,
            pagesize=A4,
            rightMargin=18 * mm,
            leftMargin=18 * mm,
            topMargin=20 * mm,
            bottomMargin=18 * mm,
            title="Per-Member Technical Guides, Viva Banks, and Revision Sheets",
            author="Mercato E-Commerce Project Team",
            subject="Technical presentation handbook",
        )
        self.style_map = style_map
        frame = Frame(
            self.leftMargin,
            self.bottomMargin,
            self.width,
            self.height,
            id="content",
            leftPadding=0,
            rightPadding=0,
            topPadding=0,
            bottomPadding=0,
        )
        self.addPageTemplates(PageTemplate(id="main", frames=[frame], onPage=self.draw_page))

    def draw_page(self, canvas, doc) -> None:
        page = canvas.getPageNumber()
        canvas.saveState()
        if page == 1:
            canvas.setFillColor(NAVY)
            canvas.rect(0, 0, PAGE_WIDTH, PAGE_HEIGHT, stroke=0, fill=1)
            canvas.setFillColor(TEAL)
            canvas.rect(0, PAGE_HEIGHT - 9 * mm, PAGE_WIDTH, 9 * mm, stroke=0, fill=1)
            canvas.setFillColor(colors.HexColor("#0E6570"))
            canvas.circle(PAGE_WIDTH - 18 * mm, 24 * mm, 32 * mm, stroke=0, fill=1)
            canvas.setFillColor(colors.HexColor("#164A62"))
            canvas.circle(PAGE_WIDTH - 4 * mm, 2 * mm, 42 * mm, stroke=0, fill=1)
        else:
            canvas.setStrokeColor(LINE)
            canvas.setLineWidth(0.5)
            canvas.line(doc.leftMargin, PAGE_HEIGHT - 12 * mm, PAGE_WIDTH - doc.rightMargin, PAGE_HEIGHT - 12 * mm)
            canvas.setFont("GuideSans-Bold", 7.2)
            canvas.setFillColor(NAVY)
            canvas.drawString(doc.leftMargin, PAGE_HEIGHT - 9.5 * mm, "MERCATO - TECHNICAL PRESENTATION HANDBOOK")
            canvas.setFont("GuideSans", 7.2)
            canvas.setFillColor(MUTED)
            canvas.drawRightString(PAGE_WIDTH - doc.rightMargin, 9 * mm, f"Page {page}")
        canvas.restoreState()

    def afterFlowable(self, flowable) -> None:
        if isinstance(flowable, Paragraph):
            level = getattr(flowable, "toc_level", None)
            if level is not None:
                text = flowable.getPlainText()
                key = f"heading-{self.seq.nextf('heading')}"
                self.canv.bookmarkPage(key)
                self.canv.addOutlineEntry(text, key, level=level, closed=False)
                self.notify("TOCEntry", (level, text, self.page, key))


def parse_table(lines: list[str], style_map: dict[str, ParagraphStyle], available_width: float):
    rows: list[list[str]] = []
    for line in lines:
        cells = [cell.strip() for cell in line.strip().strip("|").split("|")]
        if cells and all(re.fullmatch(r":?-{3,}:?", cell) for cell in cells):
            continue
        rows.append(cells)
    if not rows:
        return Spacer(1, 1)
    column_count = max(len(row) for row in rows)
    for row in rows:
        row.extend([""] * (column_count - len(row)))
    formatted = []
    for row_index, row in enumerate(rows):
        style = style_map["table_header"] if row_index == 0 else style_map["table_cell"]
        formatted.append([Paragraph(inline_markup(cell), style) for cell in row])
    if column_count == 3:
        widths = [available_width * 0.20, available_width * 0.42, available_width * 0.38]
    elif column_count == 2:
        widths = [available_width * 0.32, available_width * 0.68]
    else:
        widths = [available_width / column_count] * column_count
    table = Table(formatted, colWidths=widths, repeatRows=1, hAlign="LEFT")
    table.setStyle(
        TableStyle(
            [
                ("BACKGROUND", (0, 0), (-1, 0), NAVY),
                ("VALIGN", (0, 0), (-1, -1), "TOP"),
                ("GRID", (0, 0), (-1, -1), 0.35, LINE),
                ("ROWBACKGROUNDS", (0, 1), (-1, -1), [colors.white, PAPER]),
                ("LEFTPADDING", (0, 0), (-1, -1), 2.5 * mm),
                ("RIGHTPADDING", (0, 0), (-1, -1), 2.5 * mm),
                ("TOPPADDING", (0, 0), (-1, -1), 2 * mm),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 2 * mm),
            ]
        )
    )
    return table


def build_story(markdown_text: str, style_map: dict[str, ParagraphStyle], doc_width: float):
    lines = markdown_text.splitlines()
    story = []

    # Designed title page; skip the markdown H1 when parsing body.
    story.extend(
        [
            Spacer(1, 46 * mm),
            Paragraph("PER-MEMBER", style_map["subtitle"]),
            Spacer(1, 2 * mm),
            Paragraph(
                "Technical Guides,<br/>Viva Banks, and<br/>Revision Sheets",
                style_map["title"],
            ),
            HRFlowable(width="44%", thickness=2, color=TEAL, spaceBefore=1 * mm, spaceAfter=7 * mm),
            Paragraph(
                "Mercato E-Commerce Full-Stack Project<br/>Repository-specific presentation handbook",
                style_map["subtitle"],
            ),
            Spacer(1, 52 * mm),
            Paragraph(
                "Eight presenters | Seven functional modules | 240 viva questions",
                ParagraphStyle(
                    "TitleMeta",
                    parent=style_map["subtitle"],
                    fontName="GuideSans-Bold",
                    fontSize=9.5,
                    textColor=colors.white,
                ),
            ),
            PageBreak(),
        ]
    )

    toc = TableOfContents()
    toc.levelStyles = [style_map["toc1"], style_map["toc2"]]
    story.extend([Paragraph("Contents", style_map["toc_title"]), toc, PageBreak()])

    i = 0
    first_h1_skipped = False
    chapter_seen = False
    paragraph_buffer: list[str] = []

    def flush_paragraph() -> None:
        if paragraph_buffer:
            text = " ".join(part.strip() for part in paragraph_buffer).strip()
            if text:
                story.append(Paragraph(inline_markup(text), style_map["body"]))
            paragraph_buffer.clear()

    while i < len(lines):
        raw = lines[i]
        stripped = raw.strip()

        if stripped.startswith("```"):
            flush_paragraph()
            language = stripped[3:].strip()
            code_lines = []
            i += 1
            while i < len(lines) and not lines[i].strip().startswith("```"):
                code_lines.append(clean_text(lines[i]))
                i += 1
            label = "FLOW / CODE" if language == "mermaid" else (language.upper() if language else "CODE")
            story.append(Paragraph(label, style_map["h3"]))
            story.append(Preformatted("\n".join(code_lines), style_map["code"], maxLineLength=105))
            i += 1
            continue

        if stripped.startswith("|"):
            flush_paragraph()
            table_lines = []
            while i < len(lines) and lines[i].strip().startswith("|"):
                table_lines.append(lines[i])
                i += 1
            story.append(parse_table(table_lines, style_map, doc_width))
            story.append(Spacer(1, 3 * mm))
            continue

        if not stripped:
            flush_paragraph()
            i += 1
            continue

        if stripped == "---":
            flush_paragraph()
            story.append(HRFlowable(width="100%", thickness=0.7, color=LINE, spaceBefore=3 * mm, spaceAfter=3 * mm))
            i += 1
            continue

        heading = re.match(r"^(#{1,3})\s+(.+)$", stripped)
        if heading:
            flush_paragraph()
            level = len(heading.group(1))
            title = clean_text(heading.group(2))
            if level == 1 and not first_h1_skipped:
                first_h1_skipped = True
                i += 1
                continue
            if level == 1:
                if chapter_seen:
                    story.append(PageBreak())
                chapter_seen = True
                p = Paragraph(inline_markup(title), style_map["chapter"])
                p.toc_level = 0
                story.extend(
                    [
                        Paragraph("MEMBER MODULE", style_map["h3"]),
                        p,
                        HRFlowable(width="100%", thickness=1.4, color=TEAL, spaceAfter=4 * mm),
                    ]
                )
            elif level == 2:
                p = Paragraph(inline_markup(title), style_map["h2"])
                # The shared baseline appears before the first member chapter, so
                # it must be a top-level outline entry. Member subsections are
                # second-level entries beneath their chapter.
                p.toc_level = 1 if chapter_seen else 0
                story.append(p)
            else:
                story.append(Paragraph(inline_markup(title), style_map["h3"]))
            i += 1
            continue

        if stripped.startswith(">"):
            flush_paragraph()
            quote = stripped.lstrip("> ")
            story.append(Paragraph(inline_markup(quote), style_map["quote"]))
            i += 1
            continue

        numbered = re.match(r"^(\d+)\.\s+(.+)$", stripped)
        if numbered:
            flush_paragraph()
            number, content = numbered.groups()
            story.append(Paragraph(f"<b>{number}.</b> {inline_markup(content)}", style_map["number"]))
            i += 1
            continue

        bullet = re.match(r"^-\s+(.+)$", stripped)
        if bullet:
            flush_paragraph()
            content = bullet.group(1)
            if content.startswith("[ ]"):
                content = "[ ] " + content[3:].strip()
            story.append(Paragraph(inline_markup(content), style_map["bullet"], bulletText="-"))
            i += 1
            continue

        paragraph_buffer.append(stripped)
        i += 1

    flush_paragraph()
    return story


def export_pdf(source: Path, output: Path) -> None:
    register_fonts()
    style_map = styles()
    output.parent.mkdir(parents=True, exist_ok=True)
    markdown_text = source.read_text(encoding="utf-8")
    document = HandbookDocTemplate(str(output), style_map)
    story = build_story(markdown_text, style_map, document.width)
    document.multiBuild(story)


def main() -> None:
    parser = argparse.ArgumentParser(description="Export the member technical guide Markdown to PDF.")
    parser.add_argument("source", type=Path)
    parser.add_argument("output", type=Path)
    args = parser.parse_args()
    export_pdf(args.source.resolve(), args.output.resolve())


if __name__ == "__main__":
    main()
