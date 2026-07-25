//
// Created by tawfiq on 7/22/26.
//
#include <bits/stdc++.h>
using namespace std;
using ll = long long;
using vll = vector<ll>;
using vint = vector<int>;

ll calculatePenalty(int S, int C, int E)
{
    cout << "total ram required: " << S << "\n";
    cout << "C - S: " << C - S << endl;

    if (C-S > E)
    {
        return 5 * (C - S - E);
    }
    return 0;
}
ll totalCurrentRevenue(const vll& rev, int currRevIdx)
{
    ll r = 0;
    for (int i = 0; i <= currRevIdx; i++)
    {
        r += rev[i];
    }
    return r;
}

auto maxProfit(const vll& ram, const vll& rev, int C, int E)
{
    int N = ram.size();
    vector<vector<pair<ll, ll>>> dp(N+1, vector<pair<ll, ll>>(C+1, {0,0}));

    for (int row = 1; row <= N; row++)
    {
        ll currRam = ram[row-1];
        ll currRev = rev[row-1];
        for (int col = 1; col <= C; col++)
        {
            if (col < currRam)
            {
                dp[row][col].first = dp[row-1][col].first;
                dp[row][col].second = dp[row-1][col].second;
            }
            else
            {
                // cout << "using " << currRam << " and prev ones\n";
                ll currPenalty = calculatePenalty(col, C, E);
                cout << "pen: " << currPenalty << endl;

                if (dp[row-1][col].first - dp[row-1][col].second > dp[row-1][col - currRam].first + currRev - currPenalty)
                {
                    dp[row][col].first = dp[row-1][col].first;
                    dp[row][col].second = dp[row-1][col].second;
                }
                else
                {
                    dp[row][col].first = dp[row-1][col-currRam].first + currRev;
                    dp[row][col].second = currPenalty;
                }

            }
        }
    }

    // for (int row = 0; row < dp.size(); row++)
    // {
    //     for (int col = 0; col <= C; col++)
    //     {
    //         cout << dp[row][col].first << " ";
    //     }
    //     cout << endl;
    // }

    // ll maxProfit = LLONG_MIN;
    // for (int row = 0; row < dp.size(); row++)
    // {
    //     for (int col = 0; col <= C; col++)
    //     {
    //         maxProfit = max(maxProfit, dp[row][col].first - dp[row][col].second);
    //     }
    // }
    // return maxProfit;

    // cout << dp.back().back().first << " " << dp.back().back().second << endl;
    return dp.back().back().first - dp.back().back().second;
}

int main()
{
    int C, N, E; cin >> C >> N >> E;

    vll ram(N), rev(N);

    for (int i = 0; i < N; i++)
    {
        ll r; cin >> r;
        ram[i] = r;
    }
    for (int i = 0; i < N; i++)
    {
        ll r; cin >> r;
        rev[i] = r;
    }
    cout << maxProfit(ram, rev, C, E);


    return 0;
}
