#include <bits/stdc++.h>
using namespace std;

const int INF = 1e9;

// BFS finds an augmenting path
int bfs(int source, int sink,
        vector<vector<int>>& capacity,
        vector<vector<int>>& adj,
        vector<int>& parent)
{

    fill(parent.begin(), parent.end(), -1);
    parent[source] = -2;

    queue<pair<int, int>> q;
    q.push({source, INF});

    while (!q.empty()) {
        int u = q.front().first;
        int flow = q.front().second;
        q.pop();

        for (int v : adj[u]) {

            // If v is not visited and residual capacity exists
            if (parent[v] == -1 && capacity[u][v] > 0) {

                parent[v] = u;

                int newFlow = min(flow, capacity[u][v]);

                // Sink reached
                if (v == sink)
                    return newFlow;

                q.push({v, newFlow});
            }
        }
    }

    return 0;
}

// Edmonds-Karp algorithm
int maxFlow(int source, int sink,
            vector<vector<int>>& capacity,
            vector<vector<int>>& adj,
            int n) {

    int flow = 0;
    vector<int> parent(n);

    int newFlow;

    while ((newFlow = bfs(source, sink,
        capacity, adj, parent))) {

        flow += newFlow;

    int current = sink;

    // Update residual graph
    while (current != source) {

        int previous = parent[current];

        // Forward edge
        capacity[previous][current] -= newFlow;

        // Reverse edge
        capacity[current][previous] += newFlow;

        current = previous;
    }
        }

        return flow;
            }

            int main() {

                int n = 6;

                vector<vector<int>> capacity(n, vector<int>(n, 0));
                vector<vector<int>> adj(n);

                auto addEdge = [&](int u, int v, int cap) {

                    adj[u].push_back(v);
                    adj[v].push_back(u); // needed for residual edge

                    capacity[u][v] += cap;
                };

                // Example graph
                addEdge(0, 1, 16);
                addEdge(0, 2, 13);
                addEdge(1, 2, 10);
                addEdge(2, 1, 4);
                addEdge(1, 3, 12);
                addEdge(3, 2, 9);
                addEdge(2, 4, 14);
                addEdge(4, 3, 7);
                addEdge(3, 5, 20);
                addEdge(4, 5, 4);

                int source = 0;
                int sink = 5;

                cout << "Maximum Flow = "
                << maxFlow(source, sink, capacity, adj, n)
                << endl;

                return 0;
            }
