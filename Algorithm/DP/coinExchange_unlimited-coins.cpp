//
// Created by tawfiq on 7/1/26.
//
#include <bits/stdc++.h>
using namespace std;
using ll = long long;

// bottom up soln
// start from the bottom and store each value in the dp table to be used for
// subsequent calculations
ll  change_limited_bottomup(const int requiredSum, const vector<int>& coins)
{
    int n = coins.size();
    vector<vector<ll>> dp(n + 1, vector<ll>(requiredSum + 1));
    for (int col = 0; col <= requiredSum; col++)
    {
        dp[0][col] = 0;
    }
    dp[0][0] = 1;

    for (int row = 1; row < n + 1; row++)
    {
        int coin = coins[row - 1];
        for (int col = 0; col < requiredSum + 1; col++)
        {
            if (coin > col)
            {
                dp[row][col] = dp[row-1][col];
            }
            else
            {
                dp[row][col] = dp[row-1][col] + dp[row][col - coin];
            }
        }
    }

    return dp.back()[requiredSum];
}

// top down sol
// we start from the top and recursively break
// down the problem into smaller subproblems
vector<vector<ll>> dp;

ll  change_rec(int n, const int sum, const vector<int>& coins)
{
    // base case: sum completed
    if (sum == 0)
        return 1;

    // no more coins left to consider
    if (n == 0)
        return 0;

    int coin = coins[n-1];
    if (coin > sum)
    {
        return dp[n][sum] = change_rec(n-1, sum, coins);
    }

    // choice 1: do not use the current coin
    ll notUse = change_rec(n-1, sum, coins);

    // choice 2: use the current coin
    ll use = change_rec(n, sum - coin, coins);

    return dp[n][sum] = notUse + use;
}

ll change_topDown(const int requiredSum, const vector<int>& coins)
{
    int n = coins.size();
    dp.assign(n + 1, vector<ll>(requiredSum + 1, -1));
    return change_rec(n, requiredSum, coins);
}

int main()
{
    int requiredSum = 100;
    vector<int> coins = {1, 3, 5};

    cout << "BOTTOM UP SOLN: " << change_limited_bottomup(requiredSum, coins) << endl;
    cout << "TOP DOWN SOLN : " << change_topDown(requiredSum, coins) << endl;


    return 0;
}