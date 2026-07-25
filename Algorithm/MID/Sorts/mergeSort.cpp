//
// Created by tawfiq on 7/21/26.
//
#include <bits/stdc++.h>
using namespace std;

using vint = vector<int>;

void merge(vint& arr, int left, int mid, int right)
{
    int n1 = mid - left + 1;
    int n2 = right - mid;

    vint leftArr(n1);
    vint rightArr(n2);

    for (int i = 0; i < n1; i++)
    {
        leftArr[i] = arr[left + i];
    }

    for (int i = 0; i < n2; i++)
    {
        rightArr[i] = arr[mid + 1 + i];
    }

    int i = 0;
    int j = 0;
    int k = left;

    while (i < n1 && j < n2)
    {
        if (leftArr[i] <= rightArr[j])
        {
            arr[k] = leftArr[i];
            i++;
        }
        else
        {
            arr[k] = rightArr[j];
            j++;
        }

        k++;
    }

    while (i < n1)
    {
        arr[k] = leftArr[i];
        i++;
        k++;
    }

    while (j < n2)
    {
        arr[k] = rightArr[j];
        j++;
        k++;
    }
}

void mergeSort(vint& arr, int left, int right)
{
    if (left >= right)
    {
        return;
    }

    int mid = left + (right - left) / 2;

    mergeSort(arr, left, mid);
    mergeSort(arr, mid + 1, right);

    merge(arr, left, mid, right);
}

int main()
{
    vint arr{15, 150, 1500, 20, 2, 3, 3000, 4, 1400, 1};

    mergeSort(arr, 0, static_cast<int>(arr.size()) - 1);

    for (int num : arr)
    {
        cout << num << " ";
    }

    return 0;
}