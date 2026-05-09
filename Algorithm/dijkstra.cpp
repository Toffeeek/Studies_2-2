#include <iostream>
#include <vector>
#include <queue>
#include <utility>
#include <unordered_map>
#include <climits>
#include <algorithm>
using namespace std;

class Graph
{
    int V;
    vector<vector<pair<int, int>>> adjList;
    
    vector<int> bfs(int src, vector<bool>& visited)
    {
        vector<int> path;
        queue<int> q; 
        visited[src] = true;
        q.push(src);
        path.push_back(src);

        cout << "Visited " << src << endl;

        while(!q.empty())
        {
            int curr = q.front();
            q.pop();
            for(auto v : adjList[curr])
            {
                if(!visited[v.first])
                {
                    visited[v.first] = true;
                    q.push(v.first);
                    path.push_back(v.first);

                    // cout << "Visited " << v.first << endl;
                }
            }
        }
        return path;
    } 


public:
    Graph(vector<vector<pair<int, int>>> adjList) : V(adjList.size())
    {
        cout << "Running " << endl;
        this->adjList = adjList;
    }
    // Uses Dijkstra's alogrithm
    void findShortestPath(int src, int dest)
    {
        vector<int> dist(V, INT_MAX);
        vector<int> parent(V, -1);

        priority_queue
        <
            pair<int, int>,             // whats stored within the heap
            vector<pair<int, int>>,     // container for the heap
            greater<pair<int, int>>     // creates a min heap
        > pq;

        dist[src] = 0;

        // {cost, vertex} -> the min heap is created based on the first value of the pair which is cost
        // it looks for the second value of the parir (vertex) only if the first value is equal;
        pq.push({0, src}); 
    

        while(!pq.empty())
        {
            int currCost = pq.top().first;
            int currVertex = pq.top().second;
            pq.pop();

            if(currCost > dist[currVertex])
            {
                continue;
            }

            for(auto edge : adjList[currVertex])
            {
                int nextVertex = edge.first;
                int weight = edge.second;

                if(dist[nextVertex] > currCost + weight)
                {
                    dist[nextVertex] = currCost + weight;
                    parent[nextVertex] = currVertex;
                    pq.push({dist[nextVertex], nextVertex});
                }
            }
        }

        if(dist[dest] == INT_MAX)
        {
            cout << "No path from " << src << " to " << dest << endl;
            return;
        }

        vector<int> path;

        for(int v = dest; v != -1; v = parent[v])
        {
            path.push_back(v);
        }

        reverse(path.begin(), path.end());

        cout << "Shortest path from " << src << " to " << dest << ": ";

        for(int v : path)
        {
            cout << v << " ";
        }

        cout << "\nCost: " << dist[dest] << endl;
    }
    vector<int> bfsSearch(int src)
    {
        vector<bool> visited(V, false);
        auto bfsPath = bfs(src, visited);
        
        for(int i = 0; i < V; i++)
        {
            if(!visited[i])
            {
                auto morePath = bfs(i, visited);
                bfsPath.insert(bfsPath.end(), morePath.begin(), morePath.end());
            }
        }
        return bfsPath;
    }
};


int main()
{
    vector<vector<pair<int, int>>> graph 
    {
        {{1,2}, {2,8}},
        {{4,7}, {2,3}},  
        {{3,1}, {4,4}},
        {{4,2}},
        {}
    };

    Graph g(graph);
    auto bfsPath = g.bfsSearch(0);

    cout << "Printing bfs path: ";
    for(int v : bfsPath)
    {
        cout << v << " ";
    }
    cout << endl;

    g.findShortestPath(0,4);





    return 0;
}