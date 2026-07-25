//
// Created by tawfiq on 7/21/26.
//
#include <bits/stdc++.h>
using namespace std;
using vll = vector<long long>;
using vll2 = vector<vector<long long>>;
using vstr = vector<string>;

auto minWhitespace(vstr& words, int capacity)
{
    int n = words.size();
    vll2 dp(n+1, vll(capacity+1, 0));

    for (int row = 1; row <= n; row++)
    {
        int wordSize = words[row-1].size();
        for (int col = 1; col <= capacity; col++)
        {
            if (wordSize > col)
            {
                dp[row][col] = dp[row-1][col];
            }
            else
            {
                dp[row][col] = max(dp[row-1][col], dp[row-1][col - wordSize] + wordSize);
            }
        }
    }
    return capacity - dp.back().back();
}

int main()
{
    int capacity = 10;
    vstr words = {"there", "is", "nothing", "like", "home"};
    cout << minWhitespace(words, capacity);


}

