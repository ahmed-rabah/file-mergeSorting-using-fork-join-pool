package com.azer.csvmergesort.service;

import com.azer.csvmergesort.model.CsvRow;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.ArrayList;

public class MergeSortTask extends RecursiveTask<List<CsvRow>> {
    private final List<CsvRow> data;

    public MergeSortTask(List<CsvRow> data) {
        this.data = data;
    }

    @Override
    protected List<CsvRow> compute() {
        if (data.size() <= 1) return data;

        int mid = data.size() / 2;
        MergeSortTask leftTask = new MergeSortTask(data.subList(0, mid));
        MergeSortTask rightTask = new MergeSortTask(data.subList(mid, data.size()));

        leftTask.fork();
        List<CsvRow> right = rightTask.compute();
        List<CsvRow> left = leftTask.join();

        return merge(left, right);
    }

    private List<CsvRow> merge(List<CsvRow> left, List<CsvRow> right) {
        List<CsvRow> result = new ArrayList<>();
        int i = 0, j = 0;

        while (i < left.size() && j < right.size()) {
            if (left.get(i).compareTo(right.get(j)) <= 0) {
                result.add(left.get(i++));
            } else {
                result.add(right.get(j++));
            }
        }
        result.addAll(left.subList(i, left.size()));
        result.addAll(right.subList(j, right.size()));
        return result;
    }
}
