//
// Created by tawfiq on 7/21/26.
//
#include <bits/stdc++.h>
using namespace std;

using vint = vector<int>;

int frogJump(const vint& heights)
{
    int n = heights.size();

    // dp[i] = minimum cost required to reach stone i
    vint dp(n, 0);

    // Cost to reach stone 0 is 0 because the frog starts there.
    dp[0] = 0;

    // Stone 1 can only be reached from stone 0.
    if (n > 1)
    {
        dp[1] = abs(heights[1] - heights[0]);
    }

    for (int i = 2; i < n; i++)
    {
        // Jump from stone i - 1 to stone i.
        int oneJump =
            dp[i - 1]
            + abs(heights[i] - heights[i - 1]);

        // Jump from stone i - 2 to stone i.
        int twoJumps =
            dp[i - 2]
            + abs(heights[i] - heights[i - 2]);

        dp[i] = min(oneJump, twoJumps);
    }

    return dp[n - 1];
}

int main()
{
    vint heights{10, 30, 40, 20};

    cout << frogJump(heights) << '\n';

    return 0;
}