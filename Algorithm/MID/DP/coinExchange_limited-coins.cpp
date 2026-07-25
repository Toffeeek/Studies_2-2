//
// Created by tawfiq on 7/1/26.
//
#include <bits/stdc++.h>
using namespace std;
using ll = long long;

// bottom up soln
// start from the bottom and store each value in the dp table to be used for
// subsequent calculations
ll  change_limited_bottomup(const int requiredSum, const vector<int>& coins, const vector<int>& supply)
{
    int n = coins.size();
    vector<vector<ll>> dp(n+1, vector<ll>(requiredSum+1, 0));

    dp[0][0] = 1;

    for (int row = 1; row < n + 1; row++)
    {
        int coin = coins[row - 1];
        int available = supply[row - 1];
        for (int col = 0; col < requiredSum + 1; col++)
        {
            for (int k = 0; k <= available; k++)
            {
                int usedValue = k * coin;

                if (usedValue > col)
                    break;

                dp[row][col] += dp[row - 1][col - usedValue];
            }
        }
    }
    return dp[n][requiredSum];
}


int main()
{
    int requiredSum = 100;
    vector<int> coins = {1, 3, 5};
    vector<int> supply = {7, 5, 2};

    cout << "BOTTOM UP SOLN: " << change_limited_bottomup(requiredSum, coins, supply) << endl;



    return 0;
}