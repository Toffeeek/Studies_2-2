//
// Created by tawfiq on 7/21/26.
//
#include <bits/stdc++.h>
using namespace std;

using vint = vector<int>;

int unboundedKnapsack(const vint& weights,const vint& values,int capacity)
{
    int n = weights.size();

    vector<vector<int>> dp(n + 1, vector<int>(capacity + 1, 0));

    for (int i = 1; i <= n; i++)
    {
        for (int w = 1; w <= capacity; w++)
        {
            dp[i][w] = dp[i - 1][w];

            int weight = weights[i - 1];

            if (weight <= w)
            {
                dp[i][w] = max(
                    dp[i][w],
                    values[i - 1]
                    + dp[i][w - weight]
                );
            }
        }
    }

    return dp[n][capacity];
}

int main()
{
    vint weights{2, 3, 4};
    vint values{4, 5, 7};

    int capacity = 7;

    cout << "Maximum value: "
         << unboundedKnapsack(weights, values, capacity)
         << '\n';

    return 0;
}