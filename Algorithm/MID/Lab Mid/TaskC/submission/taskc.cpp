//
// Created by tawfiq on 7/22/26.
//
#include <bits/stdc++.h>
using namespace std;
using vint = vector<int>;

int main()
{
    int N;
    cin >> N;
    N = 2*N;
    vint arr;
    for (int i = 0; i < N; i++)
    {
        int x;
        cin >> x;
        arr.push_back(x);
    }
    // cout << "check1";
    sort(arr.begin(), arr.end());
    // cout << "check2";

    int total = 0;
    for (int i = 0; i < N; i += 2)
    {
        total += arr[i+1] - arr[i];
    }
    cout << total;

    return 0;
}