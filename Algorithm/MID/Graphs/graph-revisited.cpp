//
// Created by tawfiq on 6/18/26.
//
#include <bits/stdc++.h>
using namespace std;
using ll = long long;
constexpr ll INF = LONG_LONG_MAX;

class DSU
{
    int n;
    vector<int> repArr;
    vector<int> rank;

public:
    DSU(int n) : n(n), repArr(n), rank(n, 1)
    {
        for (int i = 0; i < n; i++)
        {
            repArr[i] = i;
        }
    }
    int find(int a)
    {
        if (repArr[a] == a)
        {
            return a;
        }
        else
        {
            return repArr[a] = find(repArr[a]);
        }
    }
    void unite(int a, int b)
    {
        a = find(a);
        b = find(b);

        if (a == b)
            return;

        if (rank[a] > rank[b])
        {
            repArr[b] = a;
        }
        else if (rank[a] < rank[b])
        {
            repArr[a] = b;
        }
        else
        {
            repArr[b] = a;
            rank[a]++;
        }
    }
    auto getArray()
    {
        return repArr;
    }
};

class Graph
{
    int V;
    vector<vector<pair<int, ll>>> aList;

    // Floyd Warshall Data
    vector<vector<ll>> dist;
    vector<vector<int>> next;

public:
    Graph(const vector<vector<pair<int, ll>>>& aList) : V(aList.size()), aList(aList)
    {

    }

    // Path Finding Algorithms
    auto bellmanFord(int src, int dest)
    {
        vector<ll> cost(V, INF);
        vector<int> parent(V, -1);
        cost[src] = 0;

        for (int it = 0; it < V - 1; it++)
        {
            for (int currVer = 0; currVer < V; currVer++)
            {
                for (auto [nextVer, nextCost] : aList[currVer])
                {
                    if (cost[currVer] != INF && cost[currVer] + nextCost < cost[nextVer])
                    {
                        cost[nextVer] = cost[currVer] + nextCost;
                        parent[nextVer] = currVer;
                    }
                }
            }
        }

        for (int currVer = 0; currVer < V; currVer++)
        {
            for (auto [nextVer, nextCost] : aList[currVer])
            {
                if (cost[currVer] != INF && cost[currVer] + nextCost < cost[nextVer])
                {
                    cout << "NEGATIVE CYCLE EXISTS, CANNOT SOLVE\n";
                    return pair<ll, vector<int>> {-1, {-1}};
                }
            }
        }

        if (cost[dest] == INF)
        {
            cout << "DESTINATION UNREACHABLE FROM SOURCE\n";
            return pair<ll, vector<int>> {INF, {-1}};
        }

        vector<int> path;
        int currVer = dest;
        while (currVer != -1)
        {
            path.push_back(currVer);
            currVer = parent[currVer];
        }
        reverse(path.begin(), path.end());
        return pair<ll, vector<int>> {cost[dest], path};
    }
    auto floydWarshall()
    {

        dist.assign(V, vector<ll>(V, INF));
        next.assign(V, vector<int>(V, -1));

        for (int currVer = 0; currVer < V; currVer++)
        {
            for (auto [nextVer, nextCost] : aList[currVer])
            {
                dist[currVer][nextVer] = nextCost,
                next[currVer][nextVer] = nextVer;
            }
        }

        for (int i = 0; i < V; i++)
        {
            dist[i][i] = 0;
            next[i][i] = i;
        }

        for (int k = 0; k < V; k++)
        {
            for (int i = 0; i < V; i++)
            {
                for (int j = 0; j < V; j++)
                {
                    if (dist[k][j] != INF && dist[i][k] != INF &&
                        dist[i][j] > dist[k][j] + dist[i][k])
                    {
                        dist[i][j] = dist[k][j] + dist[i][k];
                        next[i][j] = next[i][k];
                    }
                }
            }
        }
    }
    auto floydWarshallPath(int src, int dest)
    {
        if (next[src][dest] == -1)
        {
            cout << "DESTINATION UNREACHABLE FROM SOURCE\n";
            return vector<int>{-1};
        }

        vector<int> path;
        int curr = src;
        while (curr != dest)
        {
            path.push_back(curr);
            curr = next[curr][dest];
        }
        path.push_back(dest);
        return path;
    }
    ll floydWarshallCost(int src, int dest)
    {
        return dist[src][dest];
    }

    // MST Algorithms
    auto prim()
    {
        vector<bool> visited(V, false);
        vector<int> parent(V, -1);
        vector<ll> cost(V, INF);
        priority_queue
        <
            pair<ll, int>,
            vector<pair<ll, int>>,
            greater<pair<ll, int>>
        > pq;

        cost[0] = 0;
        pq.push({0, 0});

        while (!pq.empty())
        {
            auto [currCost, currVer] = pq.top();
            pq.pop();

            if (visited[currVer])
                continue;

            visited[currVer] = true;
            for (auto [nextVer, nextCost] : aList[currVer])
            {
                if (!visited[nextVer] && cost[nextVer] > nextCost)
                {
                    cost[nextVer] = nextCost;
                    pq.push({nextCost, nextVer});
                    parent[nextVer] = currVer;
                }
            }
        }
        return parent;
    }
    static auto sortEdges(const pair<ll, pair<int, int>>& e1, const pair<ll, pair<int, int>>& e2)
    {
        return e1.first < e2.first;
    }
    auto kruskal()
    {
        vector<pair<ll, pair<int, int>>> eList;
        for (int currVer = 0; currVer < V; currVer++)
        {
            for (auto [nextVer, nextCost] : aList[currVer])
            {
                eList.push_back({nextCost, {currVer, nextVer}});
            }
        }

        sort(eList.begin(), eList.end(), sortEdges);

        vector<pair<int, int>> mstEdges;
        ll mstCost = 0;
        DSU dsu(V);
        for (auto [cost, edge] : eList)
        {
            auto [v1, v2] = edge;
            if (dsu.find(v1) != dsu.find(v2))
            {
                mstCost += cost;
                mstEdges.push_back({v1, v2});
                dsu.unite(v1, v2);
            }
        }

        if (mstEdges.size() != V - 1)
        {
            cout << "DISCONNECTED GRAPH, MST DOES NOT EXIST\n";
            return pair<ll, vector<pair<int, int>>> {-1, {}};
        }

        return pair<ll, vector<pair<int, int>>> {mstCost, mstEdges};
    }

};

int main()
{
    constexpr int V = 5;

    vector<vector<pair<int, ll>>> adjList(V);
    adjList[0].push_back({1, -1});
    adjList[0].push_back({2, 4});
    adjList[1].push_back({2, 3});
    adjList[1].push_back({3, 2});
    adjList[1].push_back({4, 2});
    adjList[3].push_back({2, 5});
    adjList[3].push_back({1, 1});
    adjList[4].push_back({3, -3});





    return 0;
}

