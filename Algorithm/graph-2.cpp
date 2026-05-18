#include <bits/stdc++.h>
using namespace std;

class Graph
{
    int V;
    vector<vector<pair<int, int>>> adjList;

public:
    Graph(vector<vector<pair<int, int>>> adjList) : adjList(adjList)
    {
        this->V = adjList.size();
    }
    auto bfs(int src, int dest)
    {
        vector<bool> visited(V, false);
        vector<int> parent(V, -1);
        queue<int> q;

        visited[src] = true;
        q.push(src);

        while (!q.empty())
        {
            int currVer = q.front();
            q.pop();


            for (auto [nextVer, nextPrice] : adjList[currVer])
            {
                if (!visited[nextVer])
                {
                    visited[nextVer] = true;
                    parent[nextVer] = currVer;
                    q.push(nextVer);
                }
            }
        }

        if (!visited[dest])
            return vector<int> {-1};

        vector<int> path;
        int currVer = dest;
        while (currVer != -1)
        {
            path.push_back(currVer);
            currVer = parent[currVer];
        }

        reverse(path.begin(), path.end());
        return path;
    }
    auto dijkstra(int src, int dest)
    {
        vector<bool> visited(V, false);
        vector<int> parent(V, -1);
        vector<int> cost(V, INT_MAX);
        priority_queue
        <
            pair<int, int>,
            vector<pair<int, int>>,
            greater<pair<int, int>>
        > pq;

        cost[src] = 0;
        pq.push({0, src});
        while (!pq.empty())
        {
            int currVer = pq.top().second;
            int currCost = pq.top().first;
            pq.pop();

            visited[currVer] = true;

            for (auto [nextVer, nextCost] : adjList[currVer])
            {
                if (!visited[nextVer] && cost[nextVer] > currCost + nextCost)
                {
                    cost[nextVer] = currCost + nextCost;
                    parent[nextVer] = currVer;
                    pq.push({cost[nextVer], nextVer});
                }
            }
        }

        if (cost[dest] == INT_MAX)
        {
            return vector<int> {-1};
        }

        vector<int> path;
        int currVer = dest;
        while (currVer != -1)
        {
            path.push_back(currVer);
            currVer = parent[currVer];
        }

        reverse(path.begin(), path.end());
        return path;
    }
    auto kthPath(int src, int dest, int k)
    {
        priority_queue
        <
            pair<int, vector<int>>,
            vector<pair<int, vector<int>>>,
            greater<pair<int, vector<int>>>
        > pq;

        pq.push({0, {src}});
        int foundPaths = 0;
        while (!pq.empty())
        {
            int currCost = pq.top().first;
            auto currPath = pq.top().second;
            int  currVer = currPath.back();
            pq.pop();

            if (currVer == dest)
            {
                foundPaths++;
                if (foundPaths == k)
                {
                    return currPath;
                }
            }

            for (auto [nextVer, nextCost] : adjList[currVer])
            {
                if (find(currPath.begin(), currPath.end(), nextVer) != currPath.end())
                {
                    continue;
                }

                vector<int> newPath(currPath);
                newPath.push_back(nextVer);
                pq.push({currCost + nextCost, newPath});
            }
        }

        return vector<int> {-1};
    }
    auto kthCost(int src, int dest, int k)
    {
        auto path = kthPath(src, dest, k);
        if (path.size() == 1 && path[0] == -1)
        {
            return pair<bool, int> {false, -1};
        }

        int cost = 0;
        int currVerIdx = 0;

        while (path[currVerIdx] != dest)
        {
            for (auto [nextVer, nextCost] : adjList[path[currVerIdx]])
            {
                if (nextVer == path[currVerIdx + 1])
                {
                    cost += nextCost;
                    currVerIdx++;
                    break;
                }
            }
        }
        return pair<bool, int> {true, cost};
    }
    auto bellmanford(int src, int dest)
    {
        vector<int> cost(V, INT_MAX);
        vector<int> parent(V, -1);
        cost[src] = 0;

        for (int i = 0; i < V - 1; i++)
        {
            for (int currVer = 0; currVer < V; currVer++)
            {
                for (auto [nextVer, nextCost] : adjList[currVer])
                {
                    if (cost[currVer] != INT_MAX && cost[nextVer] > cost[currVer] + nextCost)
                    {
                        parent[nextVer] = currVer;
                        cost[nextVer] = cost[currVer] + nextCost;
                    }
                }
            }
        }

        for (int currVer = 0; currVer < V; currVer++)
        {
            for (auto [nextVer, nextCost] : adjList[currVer])
            {
                if (cost[currVer] != INT_MAX && cost[nextVer] > cost[currVer] + nextCost)
                {
                    return vector<int> {-1};
                }
            }
        }

        if (cost[dest] == INT_MAX)
        {
            return vector<int> {-1};
        }

        vector<int> path;
        int currVer = dest;
        while (currVer != -1)
        {
            path.push_back(currVer);
            currVer = parent[currVer];
        }
        reverse(path.begin(), path.end());
        return path;
    }
};

int main()
{
    // cout << "Running\n";
    vector<vector<pair<int, int>>> adjList(5);
    adjList[0].push_back({1, -1});
    adjList[0].push_back({2, 4});
    adjList[1].push_back({2, 3});
    adjList[1].push_back({3, 2});
    adjList[1].push_back({4, 2});
    adjList[3].push_back({2, 5});
    adjList[3].push_back({1, 1});
    adjList[4].push_back({3, -3});

    Graph g(adjList);
    cout << "BFS from 0 to 4: ";
    auto path_bfs = g.bfs(0, 4);
    for (auto ver : path_bfs)
    {
        cout << ver << " -> ";
    }
    cout << "\b\b\b" << endl;

    cout << "Dijkstra from 0 to 2: ";
    auto path_dijkstra = g.dijkstra(0, 2);
    for (auto ver : path_dijkstra)
    {
        cout << ver << " -> ";
    }
    cout << "\b\b\b" << endl;

    cout << "3rd best path from 0 to 2: ";
    auto path_3rd = g.kthPath(0, 2, 3);
    for (auto ver : path_3rd)
    {
        cout << ver << " -> ";
    }
    cout << "\b\b\b" << endl;

    cout << "Cost of 3rd best path from 0 to 2: ";
    auto cost_3rd = g.kthCost(0, 2, 3);
    if (cost_3rd.first == true)
        cout << cost_3rd.second << endl;
    else
        cout << "No such path exists\n";

    cout << "Bellman-Ford from 0 to 4: ";
    auto path_bellmanford = g.bellmanford(0, 4);
    for (auto ver : path_bellmanford)
    {
        cout << ver << " -> ";
    }
    cout << "\b\b\b" << endl;


    return 0;
}