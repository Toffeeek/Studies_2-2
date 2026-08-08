//
// Created by tawfiq on 7/30/26.
//
#include <bits/stdc++.h>
using namespace std;
using vint = vector<int>;
using pii = pair<int, int>;

void printUM(const unordered_map<int, int>& um)
{
    cout << "UM: \n";
    for (const auto& entry : um)
    {
        cout << entry.first << ":" << entry.second << endl;
    }
}

class DSU
{
    unordered_map<int, int> um;

public:
    int dsu_find(int a)
    {
        if (um.find(a) == um.end())
        {
            return -1;
        }
        else
        {
            if (um[a] != a)
            {
                return um[a] = dsu_find(um[a]);
            }
            else
            {
                return a;
            }
        }
    }
    bool dsu_union(int a)
    {
        if (a == 0)
            return false;

        if (um.find(a) == um.end())
        {
            um[a] = a;
            // cout << "*put a job in slot " << a << endl;
            // printUM(um);
            return true;
        }
        else
        {
            int rep = dsu_find(um[a]);
            // printUM(um);

            for (int i = rep - 1; i > 0; i--)
            {
                if (um.find(i) == um.end())
                {
                    um[i] = i;
                    // // cout << "put a job in slot " << i << endl;
                    um[rep] = i;
                    // printUM(um);
                    return true;
                }
                else
                {
                    i = dsu_find(i);
                }
            }
            return false;
        }
    }
};

bool sortJobs(const pii& a, const pii& b)
{
    return a.first > b.first;
}

auto jobSchedule(vector<pii>& jobs)
{
    sort(jobs.begin(), jobs.end(), sortJobs);
    DSU dsu;

    long long total = 0;
    for (int i = 0; i < jobs.size(); i++)
    {
        auto [profit, deadline] = jobs[i];
        if (dsu.dsu_union(deadline) == true)
        {
            total += profit;
        }
    }
    return total;

}

int main()
{
    int T; cin >> T;
    for (int t = 0; t < T; t++)
    {
        int N; cin >> N;
        vector<pii> jobs(N);
        for (int i = 0; i < N; i++)
        {
            int d; cin >> d;
            jobs[i] = {-1, d};
        }
        for (int i = 0; i < N; i++)
        {
            int p; cin >> p;
            jobs[i].first = p;
        }
        auto profit = jobSchedule(jobs);

        cout << profit << endl;
    }
    
    return 0;
}