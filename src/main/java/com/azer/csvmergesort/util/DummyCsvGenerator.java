package com.azer.csvmergesort.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class DummyCsvGenerator {

    public static void main(String[] args) {
        String[] headers = {"ID", "Name", "Score","x"};
        int numberOfRows = 100_000;

        String filePath = "./upload/dummy.csv";

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.append(String.join(",", headers)).append("\n");

            Random random = new Random();
            for (int i = 1; i <= numberOfRows; i++) {
                writer.append(String.valueOf(i)).append(",");
                writer.append("Name").append(String.valueOf(random.nextInt(10000))).append(",");
                writer.append(String.format("%.2f", random.nextDouble() * 100)).append("\n");
            }

            System.out.println("Fichier généré : " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
