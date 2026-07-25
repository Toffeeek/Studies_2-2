//
// Created by tawfiq on 7/21/26.
//
#include <bits/stdc++.h>
using namespace std;
using vint = vector<int>;

void rsort(vint& arr, int exp)
{
    vint count(10, 0);
    for (int& val: arr)
    {
        int dig = (val / exp) % 10;
        count[dig]++;
    }
    for (int i = 1; i < 10; i++)
    {
        count[i] += count[i-1];
    }

    vint output(arr.size());
    for (int i = arr.size() - 1; i >= 0; i--)
    {
        int dig = (arr[i] / exp) % 10;
        output[--count[dig]] = arr[i];
    }
    arr.swap(output);
}

auto radixSort(vint& arr)
{
    const int min = *min_element(arr.begin(), arr.end());
    if (min < 0)
    {
        for (int& num: arr)
        {
            num += abs(min);
        }
    }

    const int max = *max_element(arr.begin(), arr.end());
    for (int exp = 1; max / exp > 0; exp *= 10)
    {
        rsort(arr, exp);
    }
    if (min < 0)
    {
        for (int& num: arr)
        {
            num -= abs(min);
        }
    }
}


int main()
{
    vint arr{15, 150, 1500, 20, 2, 3, 3000, 4, 1400, 1};
    radixSort(arr);
    for (int num : arr)
    {
        cout << num << " ";
    }


}