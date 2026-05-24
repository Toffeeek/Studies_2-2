#include <bits/stdc++.h>
using namespace std;

const long long INF = 2e18;

class Graph
{
    int V;
    vector<vector<pair<int, long long>>> adjList;

public:
    Graph(const vector<vector<pair<int,long long>>>& adjList) : adjList(adjList)
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
        vector<long long> cost(V, INF);
        priority_queue
        <
            pair<long long, int>,
            vector<pair<long long, int>>,
            greater<pair<long long, int>>
        > pq;

        cost[src] = 0;
        pq.push({0, src});
        while (!pq.empty())
        {
            int currVer = pq.top().second;
            long long currCost = pq.top().first;
            pq.pop();

            if (visited[currVer])
                continue;

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

        if (cost[dest] == INF)
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
            pair<long long, vector<int>>,
            vector<pair<long long, vector<int>>>,
            greater<pair<long long, vector<int>>>
        > pq;

        pq.push({0, {src}});
        int foundPaths = 0;
        while (!pq.empty())
        {
            long long currCost = pq.top().first;
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
    auto kCosts(int src, int dest, int k)
    {
        vector<priority_queue<long long>> best(V);
        priority_queue
        <
            pair<long long, int>,
            vector<pair<long long, int>>,
            greater<pair<long long, int>>
        > pq;

        best[src].push(0);
        pq.push({0, src});

        while (!pq.empty())
        {
            auto [currCost, currVer] = pq.top();
            pq.pop();

            for (auto [nextVer, nextCost] : adjList[currVer])
            {
                long long newCost = currCost + nextCost;

                if (best[nextVer].size() < k)
                {
                    best[nextVer].push(newCost);
                    pq.push({newCost, nextVer});
                }
                else if (best[nextVer].top() > newCost)
                {
                    best[nextVer].pop();
                    best[nextVer].push(newCost);
                    pq.push({newCost, nextVer});
                }
            }
        }

        vector<long long> kCosts;
        while (!best[dest].empty())
        {
            kCosts.push_back(best[dest].top());
            best[dest].pop();
        }
        reverse(kCosts.begin(), kCosts.end());
        return kCosts;
    }
    auto bellmanford(int src, int dest)
    {
        vector<long long> cost(V, INF);
        vector<int> parent(V, -1);
        cost[src] = 0;

        for (int i = 0; i < V - 1; i++)
        {
            for (int currVer = 0; currVer < V; currVer++)
            {
                for (auto [nextVer, nextCost] : adjList[currVer])
                {
                    if (cost[currVer] != INF && cost[nextVer] > cost[currVer] + nextCost)
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
                if (cost[currVer] != INF && cost[nextVer] > cost[currVer] + nextCost)
                {
                    return vector<int> {-1};
                }
            }
        }

        if (cost[dest] == INF)
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
    auto floydWarshall()
    {
        vector<vector<long long>> cost(V, vector<long long>(V, INF));
        vector<vector<int>> nextVertex(V, vector<int>(V, -1));

        for (int i = 0; i < V; i++)
        {
            cost[i][i] = 0;
            nextVertex[i][i] = i;
        }

        for (int i = 0; i < V; i++)
        {
            for (auto [nextVer, nextCost] : adjList[i])
            {
                cost[i][nextVer] = nextCost;
                nextVertex[i][nextVer] = nextVer;
            }
        }

        for (int k = 0; k < V; k++)
        {
            for (int i = 0; i < V; i++)
            {
                for (int j = 0; j < V; j++)
                {
                    if (cost[k][j] != INF && cost[i][k] && cost[i][j] > cost[k][j] + cost[i][k])
                    {
                        cost[i][j] = cost[k][j] + cost[i][k];
                        nextVertex[i][j] = nextVertex[i][k];
                    }
                }
            }
        }
        return pair(cost, nextVertex);
    }
    auto floydWarshallPath(int src, int dest, const vector<vector<int>> &nextVertex)
    {
        vector<int> path;
        int currVer = src;
        while (currVer != dest)
        {
            path.push_back(currVer);
            currVer = nextVertex[src][currVer];
        }
        return path;
    }
};

int main()
{
    vector<vector<pair<int, long long>>> adjList(5);
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

    cout << "Costs of 3 best paths from 0 to 2: ";
    auto cost_k3 = g.kCosts(`0, 2, 3);
    for (long long c : cost_k3)
    {
        cout << c << " ";
    }
    cout << endl;

    cout << "Bellman-Ford from 0 to 4: ";
    auto path_bellmanford = g.bellmanford(0, 4);
    for (auto ver : path_bellmanford)
    {
        cout << ver << " -> ";
    }
    cout << "\b\b\b" << endl;

    cout << "Floyd Warshall:\n";
    auto [cost, nextVer] = g.floydWarshall();
    auto fwPath_23 = g.floydWarshallPath(2, 3, nextVer);
    for (auto ver : fwPath_23)
    {
        cout << ver << " -> ";
    }
    cout << "\b\b\b" << endl;


    return 0;
}