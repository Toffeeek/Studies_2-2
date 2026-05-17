#include <bits/stdc++.h>
using namespace std;

class Graph
{
    int V;
    vector<vector<pair<int, int>>> adjList;

public:
    Graph(const vector<vector<pair<int, int>>>& adjList) : adjList(adjList)
    {
        this->V = adjList.size();
    }

    auto dijkstra(int src, int dest) const
    {
        vector<int> parent(V, -1);
        vector<int> cost(V, INT_MAX);
        vector<bool> visited(V, false);

        priority_queue
        <
            pair<int, int>,
            vector<pair<int, int>>,
            greater<pair<int, int>>
        > pq;

        cost[src] = 0;
        pq.push({0, src});

        while(!pq.empty())
        {
            int currVertex = pq.top().second;
            int currCost = pq.top().first;
            pq.pop();

            if(visited[currVertex])
                continue;
            else
                visited[currVertex] = true;

            for(auto p : adjList[currVertex])
            {
                int nextVer = p.first;
                int nextCost = p.second;

                if(!visited[nextVer] && cost[nextVer] > currCost + nextCost)
                {
                    cost[nextVer] = currCost + nextCost;
                    parent[nextVer] = currVertex;
                    pq.push({cost[nextVer], nextVer});
                }
            }
        }

        if(cost[dest] == INT_MAX)
            return vector<int> {-1};

        vector<int> path;
        int curr = dest;

        while(curr != -1)
        {
            path.push_back(curr);

            if(curr == src)
                break;

            curr = parent[curr];
        }

        reverse(path.begin(), path.end());
        return path;
    }
    auto bellmanFord(int src, int dest) const
    {
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
    auto kthPath(int src, int dest, int k) const
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
            pq.pop();
            int currVer = currPath.back();

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
    auto kthCost(int src, int dest, int k) const
    {
        auto path = kthPath(src, dest, k);

        if (path.size() == 1 and path[0] == -1)
            return pair<bool, int> {false, -1};

        int totalCost = 0;
        int currVerIdx = 0;
        int currVer = path[currVerIdx];

        while (currVer != dest)
        {
            int nextVerIdx = currVerIdx + 1;
            int nextVer = path[nextVerIdx];

            for (auto [_nextVer, nextCost] : adjList[currVer])
            {
                if (_nextVer == nextVer)
                {
                    totalCost += nextCost;
                    currVerIdx++;
                    currVer = path[currVerIdx];
                    break;
                }
            }
        }

        return pair<bool, int> {true, totalCost};
    }
};

int main()
{
    vector<vector<pair<int, int>>> adjList =
    {
        {{1, 1}, {2, 4}},
        {{2, 2}, {4, 8}},
        {{3, 4}, {5, 1}, {6, 1}},
        {{4, 3}},
        {},
        {},
        {{3, 2}}
    };

    Graph g(adjList);

    auto path = g.dijkstra(0, 4);

    cout << "Shortest path from 0 to 4: ";
    for(int v : path)
    {
        cout << v << " ";
    }
    cout << endl;

    int k = 4;

    auto kthPathResult = g.kthPath(0, 4, k);

    cout << k << "-th shortest path from 0 to 4: ";
    for(int v : kthPathResult)
    {
        cout << v << " ";
    }
    cout << endl;

    auto kthCostResult = g.kthCost(0, 4, k);

    if(kthCostResult.first)
    {
        cout << k << "-th shortest path cost: " << kthCostResult.second << endl;
    }
    else
    {
        cout << "No " << k << "-th path exists" << endl;
    }

    return 0;
}