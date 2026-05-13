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
    auto findShortestPath(int src, int dest)
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
        while (curr != -1)
        {
            path.push_back(curr);

            if (curr == src)
                break;

            curr = parent[curr];
        }
        
        reverse(path.begin(), path.end());
        return path;
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
    auto path = g.findShortestPath(0, 4);

    for(int v : path)
    {
        cout << v << " ";
    }



    return 0;
}