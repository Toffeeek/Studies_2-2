#include <iostream>
#include <vector>
#include <climits>
using namespace std;

struct Edge
{
    int u, v, w;
};

void bellmanFord(int V, int E, vector<Edge> &edges, int source)
{
    vector<int> dist(V, INT_MAX);

    dist[source] = 0;

    // Relax all edges V - 1 times
    for (int i = 0; i < V - 1; i++)
    {
        for (int j = 0; j < E; j++)
        {
            int u = edges[j].u;
            int v = edges[j].v;
            int w = edges[j].w;

            if (dist[u] != INT_MAX && dist[u] + w < dist[v])
            {
                dist[v] = dist[u] + w;
            }
        }
    }

    // Check for negative weight cycle
    for (int j = 0; j < E; j++)
    {
        int u = edges[j].u;
        int v = edges[j].v;
        int w = edges[j].w;

        if (dist[u] != INT_MAX && dist[u] + w < dist[v])
        {
            cout << "Graph contains a negative weight cycle\n";
            return;
        }
    }

    // Print shortest distances
    cout << "Vertex\tDistance from Source\n";
    for (int i = 0; i < V; i++)
    {
        cout << i << "\t";

        if (dist[i] == INT_MAX)
            cout << "INF\n";
        else
            cout << dist[i] << "\n";
    }
}

int main()
{
    int V = 5;
    int E = 8;

    vector<Edge> edges =
    {
        {0, 1, -1},
        {0, 2, 4},
        {1, 2, 3},
        {1, 3, 2},
        {1, 4, 2},
        {3, 2, 5},
        {3, 1, 1},
        {4, 3, -3}
    };

    int source = 0;

    bellmanFord(V, E, edges, source);

    return 0;
}