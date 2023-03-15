package com.gmail.morozowau;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<String> firstVariety = new ArrayList<>();
        List<String> secondVariety = new ArrayList<>();
        try (BufferedReader input = new BufferedReader(new FileReader("src/main/resources/input.txt"));
             BufferedWriter output = new BufferedWriter(new FileWriter("src/main/resources/output.txt"))) {
            // Читаем данные
            int firstVarietyLength = Integer.parseInt(input.readLine());
            for (int i = 0; i < firstVarietyLength; i++) {
                firstVariety.add(input.readLine());
            }

            int secondVarietyLength = Integer.parseInt(input.readLine());
            for (int i = 0; i < secondVarietyLength; i++) {
                secondVariety.add(input.readLine());
            }
            // Записываем результат
            for (String pair : getPairs(secondVariety, firstVariety)) {
                output.write(pair + "\n");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static List<String> getPairs(List<String> firstVariety, List<String> secondVariety) {
        if (firstVariety.size() == 0 && secondVariety.size() == 0) {
            throw new IllegalArgumentException("Нет данных для сравнения");
        }

        List<String> longVariety = new ArrayList<>(secondVariety);
        List<String> shortVariety = new ArrayList<>(firstVariety);
        List<List<Double>> mappingMatrix = new ArrayList<>();

        // Составляем матрицу совпадений
        for (int i = 0; i < longVariety.size(); i++) {
            ArrayList<Double> shortElements = new ArrayList<>();
            mappingMatrix.add(shortElements);
            for (int j = 0; j < shortVariety.size(); j++) {
                shortElements.add(findSimilarity(longVariety.get(i), shortVariety.get(j)));
            }
        }
        return findBestInMatrix(mappingMatrix, longVariety, shortVariety);
    }

    // Получаем совпадения из матрицы
    private static List<String> findBestInMatrix(List<List<Double>> mappingMatrix, List<String> longVariety, List<String> shortVariety) {
        List<String> result = new ArrayList<>();
        System.out.println(longVariety);
        System.out.println(shortVariety);
        System.out.println(mappingMatrix);

        for (int i = 0; i < mappingMatrix.size(); i++) {
            //  Находим максимум в строке
            double lineMax = mappingMatrix.get(i).stream().max(Double::compareTo).orElseThrow();
            int index = mappingMatrix.get(i).indexOf(lineMax);
            // Находим максимум в столбце
            double columnMax = -1d;
            for (List<Double> matrix : mappingMatrix) {
                columnMax = Double.max(matrix.get(index), columnMax);
            }
            // Записываем результат и меняем конфигурацию
            if (lineMax == columnMax) {
                result.add(longVariety.get(i) + ":" + shortVariety.get(index));
                longVariety.remove(i);
                shortVariety.remove(index);
                mappingMatrix.remove(i);
                for (List<Double> matrix : mappingMatrix) {
                    matrix.remove(index);
                }
                i--;
            } else {
                result.add(longVariety.get(i) + ":?");
            }

        }
        for (String s : shortVariety) {
            result.add(s + ":?");
        }
        return result;
    }


    // Расчет относительного коэффициента Жаккара
    private static double findSimilarity(String left, String right) {
        Set<String> intersectionSet = new HashSet<>();
        Set<String> unionSet = new HashSet<>();
        boolean unionFilled = false;
        int leftLength = left.length();
        int rightLength = right.length();
        if (leftLength == 0 || rightLength == 0) {
            return 0d;
        }

        for (int leftIndex = 0; leftIndex < leftLength; leftIndex++) {
            unionSet.add(String.valueOf(left.charAt(leftIndex)));
            for (int rightIndex = 0; rightIndex < rightLength; rightIndex++) {
                if (!unionFilled) {
                    unionSet.add(String.valueOf(right.charAt(rightIndex)));
                }
                if (left.charAt(leftIndex) == right.charAt(rightIndex)) {
                    intersectionSet.add(String.valueOf(left.charAt(leftIndex)));
                }
            }
            unionFilled = true;
        }
        return (double) intersectionSet.size() / (double) unionSet.size();
    }
}
