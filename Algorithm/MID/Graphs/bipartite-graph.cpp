#include <bits/stdc++.h>
using namespace std;

enum Team
{
    BLUE,
    RED,
    UNDEFINED,
    IMPOSSIBLE
};

auto assignTeams(const vector<vector<int>>& adjList)
{
    int V = adjList.size();
    
    vector<bool> visited(V, false);
    vector<Team> teams(V, UNDEFINED);
    
    for(int i = 0; i < V; i++)
    {
    
        int src = i;

        if(visited[src])    
            continue;

        queue<int> q;
        q.push(src);
        teams[src] = BLUE;
        visited[src] = true;
        
        while(!q.empty())
        {
            int currVer = q.front();
            Team currTeam = teams[currVer];
            q.pop();
        
            for(int nextVer : adjList[currVer])
            {
                Team nextTeam = teams[nextVer];
        
                if(nextTeam == currTeam)
                    return vector<Team> {IMPOSSIBLE};
        
                if(!visited[nextVer])
                {
                    if(currTeam == BLUE)
                        teams[nextVer] = RED;
                    else    
                        teams[nextVer] = BLUE;
        
                    q.push(nextVer);
                    visited[nextVer] = true;
                }
            }
        }
    
    
    }
    
    // for(auto team : teams)
    // {
    //     if(team == IMPOSSIBLE)
    //         cout << "IMPOSSIBLE ";
    //     else if(team == RED)
    //         cout << "RED ";
    //     else if(team == BLUE)
    //         cout << "BLUE ";
    //     else
    //         cout << "UNDEFINED ";
    // }
    // cout << endl;

    return teams;
    
}

int main()
{
    freopen("input.txt", "r", stdin);

    int T;
    cin >> T; 

    while(T--)
    {
        int V, E;
        cin >> V >> E;
        vector<vector<int>> adjList(V);
        for(int i = 0; i < E; i++)
        {
            int v1, v2;
            cin >> v1 >> v2;
            adjList[v1 - 1].push_back(v2 - 1);
            adjList[v2 - 1].push_back(v1 - 1);
        }

        auto teams = assignTeams(adjList);
  
        for(auto team : teams)
        {
            if(team == IMPOSSIBLE)
                cout << "-1";
            else if(team == BLUE || team == UNDEFINED)
                cout << "1 ";
            else
                cout << "2 ";
        }
        cout << endl;
    }



    return 0;
}