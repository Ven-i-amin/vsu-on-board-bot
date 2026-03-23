package ru.vsu;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        System.out.println(new Solution().makeLargestSpecial("110010111100001100101100101010101011011010100010"));
    }
}

/*
"110010111100001100101100101010101011011010100010"

111100001100101100101100101010101011011010100010

110010111100001100101100101010101011011010100010
 */

class Solution {
    public String makeLargestSpecial(String s) {
        int len = s.length() / 2;

        int minChangeIndex = Integer.MAX_VALUE;
        int maxOneIndex = Integer.MIN_VALUE;

        for (int i = len; i > 0; i--) {
            int oneIndex = s.indexOf("1".repeat(i), 2);

            if (oneIndex == -1) {
                continue;
            }

            int sum = 0;
            int changeIndex = Integer.MAX_VALUE;

            for (int j = oneIndex - 1; j >= 0; j--) {
                sum += s.charAt(j) == '1' ? 1 : -1;

                if (sum == 0) {
                    changeIndex = j;
                }
            }

            if (changeIndex < minChangeIndex) {
                minChangeIndex = changeIndex;
                maxOneIndex = oneIndex;
            }
        }

        if (maxOneIndex == Integer.MIN_VALUE) {
            return s;
        }

        int sum = 0;
        int endOneIndex = maxOneIndex;

        do {
            sum += s.charAt(endOneIndex) == '1' ? 1 : -1;
            endOneIndex++;
        } while (sum != 0);

        return s.substring(0, minChangeIndex)
                + s.substring(maxOneIndex, endOneIndex)
                + s.substring(minChangeIndex, maxOneIndex)
                + s.substring(endOneIndex);
    }
}