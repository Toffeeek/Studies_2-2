//
// Created by tawfiq on 7/1/26.
//
#include <bits/stdc++.h>
using namespace std;

using ll = long long;
using v_int = vector<int>;
using v2_int = vector<vector<int>>;

int maxValue(const v_int& weights,const v_int& values, int capacity)
{
    int n = weights.size();

    v2_int dp(n + 1, v_int(capacity + 1, 0));

    for (int row = 1; row <= n; row++)
    {
        int weight = weights[row - 1];
        int value = values[row - 1];

        for (int col = 1; col <= capacity; col++)
        {
            if (weight > col)
            {
                dp[row][col] = dp[row - 1][col];
            }
            else
            {
                dp[row][col] = max(
                    dp[row - 1][col],
                    dp[row-1][col - weight] + value
                );
            }
        }
    }

    return dp.back().back();
}

int main()
{
    v_int weights = {3, 2, 5 ,4};
    v_int values =  {4, 3, 6, 5};
    int capacity = 5;

    cout << maxValue(weights, values, capacity);







    return 0;
}