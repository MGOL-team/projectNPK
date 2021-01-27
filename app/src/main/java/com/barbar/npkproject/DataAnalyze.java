package com.barbar.npkproject;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataAnalyze {

    public String getResult (List<String> list) {
        if (list.size() <= 2) {
            return "NaN";
        }

        List<Double> notes = new ArrayList<>();
        List<Double> priorities = new ArrayList<>();
        List<Double> times = new ArrayList<>();

        try {
            for (String data : list) {
                String[] strings = data.split(" ");
                notes.add(Double.parseDouble(strings[0]));
                priorities.add(Double.parseDouble(strings[1]));
                times.add(Double.parseDouble(strings[2]) / 86_400_000.0);
            }
        } catch (Exception e) {
            return "-0";
        }

        double[] rating = new double[notes.size()];
        for (int i = 0;i < rating.length;i++) {
            rating[i] = notes.get(i);
        }

        rating[0] = (notes.get(0) * 2 + notes.get(1)) / 3.0;
        for (int i = 1;i < rating.length - 1;i++) {
            rating[i] = (notes.get(i - 1) + notes.get(i) * 2 + notes.get(i + 1)) / 4.0;
        }
        rating[rating.length - 1] = (notes.get(notes.size() - 1) * 2 + notes.get(notes.size() - 2)) / 3.0;

        double currentTime = new Date().getTime() / 86_400_000.0;
        double[] influence = new double[notes.size()];
        for (int i = 0;i < influence.length;i++) {
            influence[i] = sigmoid(currentTime - times.get(i)) * Math.sqrt(priorities.get(i));
        }

        double sum_of_influences = 0;
        for (int i = 0;i < influence.length;i++) {
            sum_of_influences += influence[i];
        }

        double answer = 0;

        for (int i = 0;i < rating.length;i++) {
            answer += rating[i] * (influence[i] / sum_of_influences);
        }

        String result = String.valueOf(answer);
        if (result.length() > 4 && result.contains(".")) {
            result = result.substring(0, 5);
        }
        return result;
    }

    private static double sigmoid (double x) {
        return 1 / (1 + Math.pow(Math.E, -(0.2 * x + 5) ) );
    }
}
