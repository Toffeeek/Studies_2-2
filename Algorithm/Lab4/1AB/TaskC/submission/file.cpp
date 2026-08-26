#include <bits/stdc++.h>
using namespace std;


using ll = long long;
const ll INF = LLONG_MAX;
using vint = vector<int>;
using vll = vector<ll>;
using vlist = vector<vector<pair<int, ll>>>;

auto dijkstra(const vlist& aList, const vector<ll>& trains)
{
    int V = aList.size();
    
    priority_queue
    <
        pair<ll, int>,
        vector<pair<ll, int>>,
        greater<pair<ll, int>>
    > pq;

    vector<bool> visited(V, false);
    pq.push({0, 0});
    
    vll cost(V, INF);
    vector<bool> usesTrain(V, false);
    for(int i = 0; i < V; i++)
    {
        if(trains[i] != INF)
        {
            cost[i] = trains[i];
            usesTrain[i] = true;
            pq.push({trains[i], i});
        }
    }
    auto totalTrains = count(usesTrain.begin(), usesTrain.end(), true);
    cost[0] = 0;




    while(!pq.empty())
    {
        auto [currCost, currVer] = pq.top();
        pq.pop();
        if(visited[currVer])
        {
            continue;
        }
        visited[currVer] = true;

        for(auto [nextVer, nextCost] : aList[currVer])
        {
            if(cost[nextVer] > currCost + nextCost)
            {
                cost[nextVer] = currCost + nextCost;
                pq.push({cost[nextVer], nextVer});

                if(usesTrain[nextVer])
                {
                    usesTrain[nextVer] = false;
                }
            }
            if(cost[nextVer] == currCost + nextCost && usesTrain[nextVer])
            {
                usesTrain[nextVer] = false;
            }
        }
    }

    auto usefulTrains = count(usesTrain.begin(), usesTrain.end(), true);


    return totalTrains - usefulTrains;
}



int main()
{
    // cout << "RUNNING\n";
    // freopen("../input/input1.txt", "r", stdin);
    int V, E, T;
    cin >> V >> E >> T;

    vlist adjList(V);
    
    for(int i = 0; i < E; i++)
    {
        int v1, v2; ll cost;
        cin >> v1 >> v2 >> cost;
        adjList[v1-1].push_back({v2-1, cost});
        adjList[v2-1].push_back({v1-1, cost});
    }

    vector<ll> trains(V, INF);

    int duplicates = 0;
    for(int i = 0; i < T; i++)
    {
        int v; ll cost;
        cin >> v >> cost;

        if (trains[v-1] != INF)
            duplicates++;

        trains[v-1] = min(trains[v-1], cost);
    }

    // cout << "INPUT DONE\n";

    // auto cost = dijkstra(adjList, trains);
    // cout << unnecessaryTrains(cost, trains);
    cout << dijkstra(adjList, trains) + duplicates;


}