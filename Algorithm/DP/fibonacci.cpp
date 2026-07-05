//
// Created by tawfiq on 6/30/26.
//
#include <bits/stdc++.h>
using namespace std;

// top down approach
// breaking down the higher value into recursive calls of smaller values
vector<int> memo_topdown = {0, 1};
auto fib_topdown(const int n)
{
    if (n < 0)
        return -1;

    if (n < memo_topdown.size())
    {
        return memo_topdown[n];
    }
    else
    {
        memo_topdown.push_back(fib_topdown(n-1) + fib_topdown(n-2));
        return memo_topdown[n];
    }
}

// bottom up approach
// iteratively calculates upto memo[n] from zero
vector<int> memo_bottomup = {0, 1};
auto fib_bottomup(int n)
{
    if (n < 0)
        return -1;
    else if (n < memo_bottomup.size())
        return memo_bottomup[n];


    for (int i = memo_bottomup.size(); i <= n; i++)
    {
        memo_bottomup.push_back(memo_bottomup[i-1] + memo_bottomup[i-2]);
    }
    return memo_bottomup[n];
}

int main()
{
    cout << "TOP DOWN\n";
    cout << "FIB 2: " << fib_topdown(2) << endl;
    cout << "FIB 3: " << fib_topdown(3) << endl;
    cout << "FIB 5: " << fib_topdown(5) << endl;
    cout << "FIB 8: " << fib_topdown(8) << endl;

    cout << "BOTTOM UP\n";
    cout << "FIB 2: " << fib_bottomup(2) << endl;
    cout << "FIB 3: " << fib_bottomup(3) << endl;
    cout << "FIB 5: " << fib_bottomup(5) << endl;
    cout << "FIB 8: " << fib_bottomup(8) << endl;


    return 0;
}