#include <iostream>
#include <vector>
#include <queue>
#include <utility>
#include <algorithm>

using namespace std;

class Graph
{
private:
    int V;
    int E;
    vector<vector<pair<int, int>>> adjacencyList;

    void dfsRecursive(int curr, vector<bool> &visited, vector<int> &path)
    {
        if (visited[curr])
            return;

        visited[curr] = true;
        path.push_back(curr);

        for (auto edge : adjacencyList[curr])
        {
            int next = edge.first;

            if (!visited[next])
            {
                dfsRecursive(next, visited, path);
            }
        }
    }



public:
    Graph(vector<vector<pair<int, int>>> adjacencyList)
    {
        this->adjacencyList = adjacencyList;
        this->V = adjacencyList.size();
        this->E = 0;

        for (auto ver : adjacencyList)
        {
            this->E += ver.size();
        }
    }

    vector<int> DFS(int src)
    {
        vector<int> path;
        vector<bool> visited(V, false);

        dfsRecursive(src, visited, path);

        for (int i = 0; i < V; i++)
        {
            if (i == src)
                continue;

            dfsRecursive(i, visited, path);
        }

        return path;
    }

    vector<int> BFS(int src)
    {
        queue<int> q;
        vector<int> path;
        vector<bool> visited(V, false);

        q.push(src);
        visited[src] = true;
        path.push_back(src);

        while (!q.empty())
        {
            int curr = q.front();
            q.pop();

            for (auto edge : adjacencyList[curr])
            {
                int next = edge.first;

                if (!visited[next])
                {
                    visited[next] = true;
                    q.push(next);
                    path.push_back(next);
                }
            }
        }

        for (int i = 0; i < V; i++)
        {
            if (i == src || visited[i])
                continue;

            q.push(i);
            visited[i] = true;
            path.push_back(i);

            while (!q.empty())
            {
                int curr = q.front();
                q.pop();

                for (auto edge : adjacencyList[curr])
                {
                    int next = edge.first;

                    if (!visited[next])
                    {
                        visited[next] = true;
                        q.push(next);
                        path.push_back(next);
                    }
                }
            }
        }

        return path;
    }

    vector<int> dijkstra(int src, int dest)
    {
        const int INF = 1e9;

        vector<int> dist(V, INF);
        vector<int> parent(V, -1);
        vector<bool> visited(V, false);

        // pair = {distance, vertex}
        priority_queue
        <
            pair<int, int>,
            vector<pair<int, int>>,
            greater<pair<int, int>>
        >
            pq;

        dist[src] = 0;
        pq.push({0, src});

        while (!pq.empty())
        {
            int currDist = pq.top().first;
            int curr = pq.top().second;
            pq.pop();

            if (visited[curr])
                continue;

            visited[curr] = true;

            if (curr == dest)
                break;

            for (auto edge : adjacencyList[curr])
            {
                int next = edge.first;
                int weight = edge.second;

                if (!visited[next] && currDist + weight < dist[next])
                {
                    dist[next] = currDist + weight;
                    parent[next] = curr;
                    pq.push({dist[next], next});
                }
            }
        }

        vector<int> path;

        if (dist[dest] == INF)
        {
            return path; // no path found
        }

        int curr = dest;

        while (curr != -1)
        {
            path.push_back(curr);
            curr = parent[curr];
        }

        reverse(path.begin(), path.end());

        cout << "Shortest distance: " << dist[dest] << endl;

        return path;
    }

    int kthCost(int src, int dest, int k)
    {
        // vector<int> path = kthPath(src, dest, k);
        //
        // if(path.size() == 1 && path[0] == -1)
        //     return -1;
        //
        // int totalCost = 0;
        //
        // for each consecutive pair path[i] -> path[i + 1]:
        //     find that edge in adjacencyList
        //     add its weight to totalCost
        //
        // return totalCost;
    }
    vector<int> kthPath(int src, int dest, int k) const
    {
        // Each item: {totalCost, pathSoFar}
        priority_queue
        <
            pair<int, vector<int>>,
            vector<pair<int, vector<int>>>,
            greater<pair<int, vector<int>>>
        > pq;

        pq.push({0, {src}});

        int foundPaths = 0;

        while(!pq.empty())
        {
            int currCost = pq.top().first;
            vector<int> currPath = pq.top().second;
            pq.pop();

            int currVer = currPath.back();

            if(currVer == dest)
            {
                foundPaths++;

                if(foundPaths == k)
                {
                    cout << k << "-th shortest path cost = " << currCost << endl;
                    return currPath;
                }
            }

            for(auto [nextVer, nextCost] : adjList[currVer])
            {
                bool alreadyInPath = false;

                for(int v : currPath)
                {
                    if(v == nextVer)
                    {
                        alreadyInPath = true;
                        break;
                    }
                }

                if(alreadyInPath)
                    continue;

                vector<int> newPath = currPath;
                newPath.push_back(nextVer);

                pq.push({currCost + nextCost, newPath});
            }
        }

        return vector<int>{-1};
    }

    void printAdjacencyList()
    {
        for (auto ver : adjacencyList)
        {
            for (auto edge : ver)
            {
                cout << "(" << edge.first << ", " << edge.second << "), ";
            }
            cout << endl;
        }
    }
};

int main()
{
    vector<vector<pair<int, int>>> graph =
        {
            {{1, 1}, {2, 4}},
            {{2, 2}, {4, 8}},
            {{3, 4}, {5, 1}, {6, 1}},
            {{4, 3}},
            {},
            {},
            {{3, 2}}};

    return 0;
}