package org.example;

import java.util.ArrayList;
import java.util.List;


public class Main
{
    public static void main(String[] args)
    {

        ArrayList<ArrayList<Pair<Integer, Integer>>> graph = new ArrayList<>();

        graph.add(new ArrayList<>(List.of(
                new Pair<>(1, 1),
                new Pair<>(2, 4)
        )));

        graph.add(new ArrayList<>(List.of(
                new Pair<>(2, 2),
                new Pair<>(4, 8)
        )));

        graph.add(new ArrayList<>(List.of(
                new Pair<>(3, 4),
                new Pair<>(5, 1),
                new Pair<>(6, 1)
        )));

        graph.add(new ArrayList<>(List.of(
                new Pair<>(4, 3)
        )));

        graph.add(new ArrayList<>());

        graph.add(new ArrayList<>());

        graph.add(new ArrayList<>(List.of(
                new Pair<>(3, 2)
        )));

//        System.out.println(graph);
        Graph g = new Graph(graph);
        g.printAdjacencyList();

        System.out.print("DFS: ");
        var DFSpath = new ArrayList<>(g.DFS(0));
        System.out.println(DFSpath);

        System.out.print("BFS: ");
        var BFSpath = new ArrayList<>(g.BFS(0));
        System.out.println(BFSpath);


    }
}
