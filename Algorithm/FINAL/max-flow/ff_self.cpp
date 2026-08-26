#include <bits/stdc++.h>
using namespace std;
using vint = vector<int>;

class FordFulkerson
{
    int N;
    vector<vint> aList;
    vector<vint> capacityMatrix;

    int dfs(int currVer, int source, int sink, int flow, vector<bool>& visited)
    {
        if (currVer == sink)
            return flow;

        visited[currVer] = true;

        for (int nextVer: aList[currVer])
        {
            if (!visited[nextVer] && capacityMatrix[currVer][nextVer] != 0)
            {
                int pushed = dfs(nextVer, source, sink, min(flow, capacityMatrix[currVer][nextVer]), visited);

                if (pushed > 0)
                {
                    capacityMatrix[currVer][nextVer] -= pushed;
                    capacityMatrix[nextVer][currVer] += pushed;
                    return pushed;
                }
            }
        }

        return 0;
    }

public:
    FordFulkerson(int N) : N(N)
    {
        aList.resize(N);
        capacityMatrix.resize(N, vector(N, 0));
    }
    int maxFlow(int source, int sink)
    {
        int totalFlow = 0;

        while (true)
        {
            vector<bool> visited(N, false);
            int flow = dfs(source, source, sink, INT_MAX, visited);

            if (flow > 0)
            {
                totalFlow += flow;
            }
            else
            {
                break;
            }
        }

        return totalFlow;

    }
    void addEdge(int u, int v, int flow)
    {
        aList[u].push_back(v);
        aList[v].push_back(u);
        capacityMatrix[u][v] += flow;
    }


};


int main()
{

    FordFulkerson graph(6);

    // u, v, capacity
    graph.addEdge(0, 1, 16);
    graph.addEdge(0, 2, 13);
    graph.addEdge(1, 2, 10);
    graph.addEdge(2, 1, 4);
    graph.addEdge(1, 3, 12);
    graph.addEdge(3, 2, 9);
    graph.addEdge(2, 4, 14);
    graph.addEdge(4, 3, 7);
    graph.addEdge(3, 5, 20);
    graph.addEdge(4, 5, 4);
    cout << graph.maxFlow(0, 5);

    return 0;
}