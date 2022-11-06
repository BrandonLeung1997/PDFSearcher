package com.fyp.searcher.model;

public class Coordinate {
    int page;
    double x1;
    double y1;
    double x2;
    double y2;

    public Coordinate(int page, double x1, double y1, double x2, double y2) {
        this.page = page;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public int getPage() {
        return page;
    }

    public double getX1() {
        return x1;
    }

    public double getY1() {
        return y1;
    }

    public double getX2() {
        return x2;
    }

    public double getY2() {
        return y2;
    }
}
