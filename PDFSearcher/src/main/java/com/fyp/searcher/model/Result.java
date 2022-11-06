package com.fyp.searcher.model;

import java.util.ArrayList;

public class Result {
    String path;
    ArrayList<Coordinate> list_coordinate;

    public Result(String path, ArrayList<Coordinate> list_coordinate) {
        this.path = path;
        this.list_coordinate = list_coordinate;
    }

    public String getPath() {
        return path;
    }

    public ArrayList<Coordinate> getList_coordinate(){
        return list_coordinate;
    }
}
