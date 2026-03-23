package ru.vsu;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ThreeThree {
    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            int i = Integer.parseInt(br.readLine());

            String[] s = new String[i];

            for (int j = 0; j < i; j++) {
                s[j] = br.readLine();
            }

            for (String s1 : s) {
                solution(s1);
            }
        } catch (Exception e) {

        }
    }

    private static void solution(String str) {
        ArrayList<Integer> list = new ArrayList<>(List.of(0));

        int max = Integer.MIN_VALUE;

        for (int i = 0; i < str.length(); i++) {
            int listIndex = list.size() - 1;
            if (str.charAt(i) == '1') {
                list.set(listIndex, list.get(listIndex) + 1);
            } else {
                if (list.get(listIndex) != 0) {
                    max = Math.max(list.get(listIndex), max);
                    list.add(0);
                }
            }
        }

        if (max == Integer.MIN_VALUE) {
            max = list.get(0);

            if (max == str.length()) {
                System.out.println((long) max * max);
                return;
            }
        }

        if (str.charAt(0) == '1' && str.charAt(str.length() - 1) == '1') {
            max = Math.max(list.get(0) + list.get(list.size() - 1), max);
        }

        max++;
        System.out.println((long) (Math.ceil(max / 2.0) * Math.floor(max / 2.0)));
    }

}
