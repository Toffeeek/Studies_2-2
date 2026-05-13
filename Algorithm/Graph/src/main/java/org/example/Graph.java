package org.example;

import org.w3c.dom.ls.LSOutput;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Graph
{
    private int V;
    private int E;
    private ArrayList<ArrayList<Pair<Integer, Integer>>> adjacencyList;

    private void dfsRecursive(int curr, ArrayList<Boolean> visited, ArrayList<Integer> path)
    {
        if(visited.get(curr))
            return;

        visited.set(curr, true);
        path.add(curr);
        for(var ver : adjacencyList.get(curr))
        {
            if(!visited.get(ver.first))
            {
                dfsRecursive(ver.first, visited, path);
            }
        }
    }

    public Graph(ArrayList<ArrayList<Pair<Integer, Integer>>> adjacencyList)
    {
        this.adjacencyList = adjacencyList;
        this.V = adjacencyList.size();
        this.E = 0;
        for(var ver : adjacencyList)
        {
            this.E += ver.size();
        }
    }

    public ArrayList<Integer> DFS(int src)
    {
        ArrayList<Integer> path = new ArrayList<>();
        ArrayList<Boolean> visited = new ArrayList<>();
        for(int i = 0; i < V; i++)
        {
            visited.add(false);
        }

        dfsRecursive(src, visited, path);

        for(int i = 0; i < V; i++)
        {
            if(i == src)
                continue;

            dfsRecursive(i, visited, path);
        }

        return path;
    }

    public ArrayList<Integer> BFS(int src)
    {
        Queue<Integer> q = new ArrayDeque<>();
        ArrayList<Integer> path = new ArrayList<>();
        ArrayList<Boolean> visited = new ArrayList<>();
        for(int i = 0; i < V; i++)
        {
            visited.add(false);
        }

        q.add(src);
        visited.set(src, true);
        path.add(src);

        while(!q.isEmpty())
        {
            int curr = q.poll();

            for(var ver : adjacencyList.get(curr))
            {
                if(!visited.get(ver.first))
                {
                    visited.set(ver.first, true);
                    q.add(ver.first);
                    path.add(ver.first);
                }
            }
        }

        for(int i = 0; i < V; i++)
        {
            if(i == src || visited.get(i))
                continue;

            q.add(i);
            visited.set(i, true);
            path.add(i);

            while(!q.isEmpty())
            {
                int curr = q.poll();

                for(var ver : adjacencyList.get(curr))
                {
                    if(!visited.get(ver.first))
                    {
                        visited.set(ver.first, true);
                        q.add(ver.first);
                        path.add(ver.first);
                    }
                }
            }
        }

        return path;
    }

    public void printAdjacencyList()
    {
        for(var ver : adjacencyList)
        {
            for(var edge : ver)
            {
                System.out.print(edge + ", ");
            }
            System.out.println();
        }
    }
}
