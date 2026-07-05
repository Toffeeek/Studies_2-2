#include <bits/stdc++.h>
using namespace std;

const int INF = 1e9;

int minCoins2D(int requiredSum, const vector<int>& coins)
{
    int n = coins.size();

    vector<vector<int>> dp(n + 1, vector<int>(requiredSum + 1, INF));

    // Base case: 0 coins needed to make sum 0
    for (int i = 0; i <= n; i++)
    {
        dp[i][0] = 0;
    }

    for (int row = 1; row <= n; row++)
    {
        int coin = coins[row - 1];

        for (int sum = 1; sum <= requiredSum; sum++)
        {
            if (coin > sum)
            {
                // Cannot use current coin
                dp[row][sum] = dp[row - 1][sum];
            }
            else
            {
                // Minimum of excluding or including current coin
                dp[row][sum] = min(dp[row - 1][sum], 1 + dp[row][sum - coin]);
                // 1 + dp[row][sum - coin] because just dp[row][sum - coin] gives the
                // number of coins needed to make 'sum - coin', so we add it
                // by 1 for it to give the number of coins needed to make 'sum'

            }
        }
    }

    if (dp[n][requiredSum] == INF)
        return -1;

    return dp[n][requiredSum];
}

int main()
{
    int requiredSum = 10;
    vector<int> coins = {1, 3, 5};

    cout << minCoins2D(requiredSum, coins) << endl;

    return 0;
}