//
// Created by tawfiq on 8/30/26.
//
#include <bits/stdc++.h>
using namespace std;
using vint = vector<int>;

int kadane(const vint& arr)
{
    if (arr.empty())
        return 0;
    // if (arr.size() == 1)
    //     return arr[0];

    int localMax = arr[0];
    int globalMax = arr[0];

    for (int i = 1; i < arr.size(); i++)
    {
        localMax = max(arr[i], localMax + arr[i]);
        if (localMax > globalMax)
        {
            globalMax = localMax;
        }
    }
    return globalMax;
}


int main()
{
    vint arr = {1, -4, 2, -5, 1, 4, 3, -6, 2, 1, 3, 10};
    cout << kadane(arr);

    return 0;
}