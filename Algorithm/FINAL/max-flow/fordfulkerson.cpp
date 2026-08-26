#include <bits/stdc++.h>
using namespace std;

class FordFulkerson {
private:
    int n;
    vector<vector<int>> capacity;
    vector<vector<int>> aList;

    int dfs(int u, int sink, int flow, vector<bool>& visited)
    {
        if (u == sink)
            return flow;

        visited[u] = true;

        for (int v : aList[u])
        {
            if (!visited[v] && capacity[u][v] > 0)
            {
                int pushed = dfs(v, sink,min(flow, capacity[u][v]),visited);

                if (pushed > 0)
                {
                    // Update residual graph
                    capacity[u][v] -= pushed;
                    capacity[v][u] += pushed;

                    return pushed;
                }
            }
        }

        return 0;
    }

public:
    FordFulkerson(int n) {
        this->n = n;
        capacity.assign(n, vector<int>(n, 0));
        aList.resize(n);
    }

    void addEdge(int u, int v, int cap) {
        capacity[u][v] += cap;

        aList[u].push_back(v);
        aList[v].push_back(u); // Reverse edge for residual graph
    }

    int maxFlow(int source, int sink)
    {
        int totalFlow = 0;

        while (true)
        {
            vector<bool> visited(n, false);

            int flow = dfs(source, sink,INT_MAX,visited);

            // No augmenting path remains
            if (flow == 0)
                break;

            totalFlow += flow;
        }

        return totalFlow;
    }
};

int main() {
    int n = 6;

    FordFulkerson graph(n);

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

    int source = 0;
    int sink = 5;

    cout << "Maximum Flow = "
         << graph.maxFlow(source, sink)
         << '\n';

    return 0;
}