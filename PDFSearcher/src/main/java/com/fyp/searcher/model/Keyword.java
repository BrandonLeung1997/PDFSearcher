package com.fyp.searcher.model;

import java.util.ArrayList;

public class Keyword {
    String keyword;
    String operator;
    ArrayList<String> pos;

    public Keyword(String keyword, String operator, ArrayList<String> pos) {
        this.keyword = keyword;
        this.operator = operator;
        this.pos = pos;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getOperator() {
        return operator;
    }

    public ArrayList<String> getPos() {
        return pos;
    }
}
