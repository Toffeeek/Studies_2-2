//
// Created by tawfiq on 7/1/26.
//
#include <bits/stdc++.h>
using namespace std;
using ll = long long;

ll change_kCoins(int requiredSum, const vector<int>& coins, int K)
{
    vector<vector<ll>> dp(requiredSum + 1, vector<ll>(K + 1, 0));

    // One way to make sum 0 using 0 coins: choose nothing
    dp[0][0] = 1;

    // Process coin types one by one
    for (int coin : coins)
    {
        for (int sum = coin; sum <= requiredSum; sum++)
        {
            for (int used = 1; used <= K; used++)
            {
                dp[sum][used] += dp[sum - coin][used - 1];
            }
        }
    }

    // At most K coins means exactly 0, 1, 2, ..., K coins are allowed
    ll answer = 0;

    for (int used = 0; used <= K; used++)
    {
        answer += dp[requiredSum][used];
    }

    return answer;
}

int main()
{
    int requiredSum = 4;
    int K = 2;

    vector<int> coins = {1, 2, 3};

    cout << change_kCoins(requiredSum, coins, K) << endl;

    return 0;
}