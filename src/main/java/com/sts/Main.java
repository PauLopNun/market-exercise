package com.sts;

import com.sts.shared.model.CSVReader;

public class Main {
    public static void main(String[] args) {
        CSVReader csvReader = new CSVReader();
        csvReader.getData("./data/", ",");
    }
}
