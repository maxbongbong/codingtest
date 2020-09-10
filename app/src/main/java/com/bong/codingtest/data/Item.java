package com.bong.codingtest.data;

import java.util.List;

public class Item {
    public int total_count;
    public boolean incomplete_results;
    public List<User> items;

    public int getTital_count() {
        return total_count;
    }

    public void setTital_count(int tital_count) {
        this.total_count = tital_count;
    }

    public boolean isIncomplete_results() {
        return incomplete_results;
    }

    public void setIncomplete_results(boolean incomplete_results) {
        this.incomplete_results = incomplete_results;
    }

    public List<User> getItems() {
        return items;
    }

    public void setItems(List<User> items) {
        this.items = items;
    }
}
