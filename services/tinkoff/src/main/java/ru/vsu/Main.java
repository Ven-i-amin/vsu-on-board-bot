package ru.vsu;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            int[] param = Arrays.stream(br.readLine().split(" ")).mapToInt(Integer::parseInt).toArray();

            Node[] nodes = new Node[param[0] + 1];

            for (int i = 1; i <= param[0]; i++) {
                nodes[i] = new Node(i);
            }

            for (int i = 0; i < param[1]; i++) {
                int[] edge = Arrays.stream(br.readLine().split(" ")).mapToInt(Integer::parseInt).toArray();

                nodes[edge[0]].neighbors.add(nodes[edge[1]]);
                nodes[edge[1]].neighbors.add(nodes[edge[0]]);
            }

            solution(nodes);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    private static void solution(Node[] nodes) {
        int answer = Integer.MAX_VALUE;

        for (int i = 1; i < nodes.length; i++) {
            Queue<Node> queue = new LinkedList<>();
            int[] dist = new int[nodes.length];
            int[] parent = new int[nodes.length];

            Arrays.fill(dist, -1);
            Arrays.fill(parent, -1);

            queue.add(nodes[i]);
            dist[i] = 0;

            while (!queue.isEmpty()) {
                Node node = queue.remove();

                for (Node neighbor : node.neighbors) {
                    if (dist[neighbor.value] == -1) {
                        dist[neighbor.value] = dist[node.value] + 1;
                        parent[neighbor.value] = node.value;
                        queue.add(neighbor);
                    } else if (parent[node.value] != neighbor.value) {
                        answer = Math.min(answer, dist[node.value] + dist[neighbor.value] + 1);
                    }
                }
            }
        }

        if (answer == Integer.MAX_VALUE) {
            System.out.println(-1);
        } else {
            System.out.println(answer);
        }
    }

    private static class Node {
        public Node(int value) {
            this.value = value;
        }

        public List<Node> neighbors = new ArrayList<>();
        public int value;
    }
}
