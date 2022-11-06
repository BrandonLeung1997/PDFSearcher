package com.fyp.searcher.model;

public class Collocation {
    String basisword, collocation, relation;
    String[] example;
    int id;

    public String getBasisword() {
        return basisword;
    }

    public String getCollocation() {
        return collocation;
    }

    public String getRelation() {
        return relation;
    }

    public String[] getExample() {
        return example;
    }

    public int getId() {
        return id;
    }

    public Collocation(String basisword, String collocation, String relation, String[] example, int id) {
        this.basisword = basisword;
        this.collocation = collocation;
        this.relation = relation;
        this.example = example;
        this.id = id;
    }
}
