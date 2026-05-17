#include <bits/stdc++.h>
using namespace std;



auto bellmanFord(const vector<vector<pair<int, int>>>& adjList, int src, int dest)
{
    int V = adjList.size();
    vector<int> cost(V, INT_MAX);
    vector<int> parent(V, -1);

    cost[src] = 0;

    for(int i = 0; i < V - 1; i++)
    {
        for(int currVer = 0; currVer < V; currVer++)
        {
            for(const auto& nextVerPair : adjList[currVer])
            {
                int nextVer = nextVerPair.first;
                int nextCost = nextVerPair.second;

                if(cost[currVer] != INT_MAX && cost[nextVer] > cost[currVer] + nextCost)
                {
                    cost[nextVer] = cost[currVer] + nextCost;
                    parent[nextVer] = currVer;
                }
            }
        }
    }

    for(int currVer = 0; currVer < V; currVer++)
    {
        for(const auto& nextVerPair : adjList[currVer])
        {
            int nextVer = nextVerPair.first;
            int nextCost = nextVerPair.second;

            if(cost[currVer] != INT_MAX && cost[nextVer] > cost[currVer] + nextCost)
            {
                cout << "Graph has a negative cycle, cannot find the best path\n";
                return vector<int> {-1};
            }
        }
    }

    cout << "Costs: ";
    for(auto c : cost)
    {
        cout << c << " ";
    }
    cout << endl;

    if(cost[dest] == INT_MAX)
    {
        cout << "No path to the dest vertex exists\n";
        return vector<int> {-1};
    }

    vector<int> path;

    int currVer = dest;
    while(currVer != -1)
    {
        path.push_back(currVer);
        currVer = parent[currVer];
    }

    reverse(path.begin(), path.end());
    return path;
}

int main()
{
    vector<vector<pair<int, int>>> adjList(5);
    adjList[0].push_back({1, -1});
    adjList[0].push_back({2, 4});
    adjList[1].push_back({2, 3});
    adjList[1].push_back({3, 2});
    adjList[1].push_back({4, 2});
    adjList[3].push_back({2, 5});
    adjList[3].push_back({1, 1});
    adjList[4].push_back({3, -3});

    auto path = bellmanFord(adjList, 0, 2);

    cout << "Path from 0 to 2: ";
    for(auto v : path)
    {
        cout << v << " ";
    }
    cout << endl;




    return 0;
}