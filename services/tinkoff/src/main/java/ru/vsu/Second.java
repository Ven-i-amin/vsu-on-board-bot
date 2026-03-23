package ru.vsu;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Second {
    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            String s = br.readLine();

            solution(s);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    private static void solution(String s) {
        String first = "tbank";
        String second = "study";

        int n = s.length();
        int size = 5;
        int[] firstCost = new int[n - size + 1];
        int[] secondCost = new int[n - size + 1];

        for (int i = 0; i <= n - size; i++) {
            firstCost[i] = getCost(s, i, first);
            secondCost[i] = getCost(s, i, second);
        }

        int[] prefix = new int[n - size + 1];
        int[] suffix = new int[n - size + 1];

        prefix[0] = secondCost[0];
        for (int i = 1; i < prefix.length; i++) {
            prefix[i] = Math.min(prefix[i - 1], secondCost[i]);
        }

        suffix[suffix.length - 1] = secondCost[secondCost.length - 1];
        for (int i = suffix.length - 2; i >= 0; i--) {
            suffix[i] = Math.min(suffix[i + 1], secondCost[i]);
        }

        int answer = Integer.MAX_VALUE;

        for (int i = 0; i < firstCost.length; i++) {
            if (i - size >= 0) {
                answer = Math.min(answer, firstCost[i] + prefix[i - size]);
            }

            if (i + size < secondCost.length) {
                answer = Math.min(answer, firstCost[i] + suffix[i + size]);
            }
        }

        for (int i = 0; i < firstCost.length; i++) {
            for (int j = Math.max(0, i - size + 1); j <= Math.min(secondCost.length - 1, i + size - 1); j++) {
                int current = 0;
                boolean correct = true;

                for (int k = Math.min(i, j); k < Math.max(i, j) + size; k++) {
                    char c = 0;

                    if (k >= i && k < i + size) {
                        c = first.charAt(k - i);
                    }

                    if (k >= j && k < j + size) {
                        char secondChar = second.charAt(k - j);

                        if (c != 0 && c != secondChar) {
                            correct = false;
                            break;
                        }

                        c = secondChar;
                    }

                    if (s.charAt(k) != c) {
                        current++;
                    }
                }

                if (correct) {
                    answer = Math.min(answer, current);
                }
            }
        }

        System.out.println(answer);
    }

    private static int getCost(String s, int start, String target) {
        int count = 0;

        for (int i = 0; i < 5; i++) {
            if (s.charAt(start + i) != target.charAt(i)) {
                count++;
            }
        }

        return count;
    }
}
