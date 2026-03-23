package ru.vsu;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Third {
    private static final long MOD = 1_000_000_007L;

    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            int[] param = Arrays.stream(br.readLine().split(" ")).mapToInt(Integer::parseInt).toArray();

            solution(param[0], param[1]);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    private static void solution(int n, int k) {
        int max = n == 1 ? 1 : 2 * n - 2;

        if (k > max) {
            System.out.println(0);
            return;
        }

        List<Integer> black = new ArrayList<>();
        List<Integer> white = new ArrayList<>();

        for (int i = 2; i <= 2 * n; i++) {
            int len = Math.min(i - 1, 2 * n + 1 - i);

            if (i % 2 == 0) {
                black.add(len);
            } else {
                white.add(len);
            }
        }

        Collections.sort(black);
        Collections.sort(white);

        long[] blackWays = getWays(black, k);
        long[] whiteWays = getWays(white, k);

        long answer = 0;

        for (int i = 0; i <= k; i++) {
            answer = (answer + blackWays[i] * whiteWays[k - i]) % MOD;
        }

        System.out.println(answer);
    }

    private static long[] getWays(List<Integer> list, int k) {
        long[] dp = new long[k + 1];
        dp[0] = 1;

        for (Integer len : list) {
            long[] next = dp.clone();

            for (int i = 1; i <= k; i++) {
                int count = len - (i - 1);

                if (count > 0) {
                    next[i] = (next[i] + dp[i - 1] * count) % MOD;
                }
            }

            dp = next;
        }

        return dp;
    }
}
