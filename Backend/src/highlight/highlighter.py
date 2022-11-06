import fitz
import os

from fitz import Point, Quad


def addHighlight(path, list_coordinates):
    doc = fitz.open(path)
    for coordinate in list_coordinates:
        page = doc.load_page(coordinate["page"])
        quads = [Quad(Point(coordinate["x1"], coordinate["y1"]),
                      Point(coordinate["x2"], coordinate["y1"]),
                      Point(coordinate["x1"], coordinate["y2"]),
                      Point(coordinate["x2"], coordinate["y2"]))]
        page.add_highlight_annot(quads)
    if not os.path.exists("display"):
        os.mkdir("display")
    doc.save(os.path.join("display/", "display_highlight.pdf"))


def addUnderline(path, list_coordinates):
    doc = fitz.open(path)
    for coordinate in list_coordinates:
        page = doc.load_page(coordinate["page"])
        quads = [Quad(Point(coordinate["x1"], coordinate["y1"]),
                      Point(coordinate["x2"], coordinate["y1"]),
                      Point(coordinate["x1"], coordinate["y2"]),
                      Point(coordinate["x2"], coordinate["y2"]))]
        page.add_underline_annot(quads)
    if not os.path.exists("display"):
        os.mkdir("display")
    doc.save(os.path.join("display/", "display.pdf"))


def highlight(path, list_coordinates):
    addUnderline(path, list_coordinates)
    addHighlight(path, list_coordinates)
