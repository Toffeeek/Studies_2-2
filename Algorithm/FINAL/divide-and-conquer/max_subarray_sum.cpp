//
// Created by tawfiq on 7/13/26.
//
#include <iostream>
#include <vector>
#include <climits>
#include <algorithm>
using namespace std;

// Finds the maximum sum of a subarray crossing the middle.
long long maximumCrossingSum(const vector<int>& nums, int left, int middle, int right)
{
    long long sum = 0;
    long long maximumLeftSum = LLONG_MIN;

    // Start from the middle and move left.
    for (int i = middle; i >= left; i--) {
        sum += nums[i];
        maximumLeftSum = max(maximumLeftSum, sum);
    }

    sum = 0;
    long long maximumRightSum = LLONG_MIN;

    // Start after the middle and move right.
    for (int i = middle + 1; i <= right; i++) {
        sum += nums[i];
        maximumRightSum = max(maximumRightSum, sum);
    }

    return maximumLeftSum + maximumRightSum;
}

long long maximumSubarraySum(const vector<int>& nums, int left, int right)
{
    // Base case: only one element.
    if (left == right)
    {
        return nums[left];
    }

    int middle = left + (right - left) / 2;

    long long leftMaximum =
        maximumSubarraySum(nums, left, middle);

    long long rightMaximum =
        maximumSubarraySum(nums, middle + 1, right);

    long long crossingMaximum =
        maximumCrossingSum(nums, left, middle, right);

    return max({leftMaximum, rightMaximum, crossingMaximum});
}

int main() {
    vector<int> nums = {
        -2, 1, -3, 4, -1, 2, 1, -5, 4
    };

    if (nums.empty()) {
        cout << "The array is empty.\n";
        return 0;
    }

    long long answer =
        maximumSubarraySum(nums, 0, nums.size() - 1);

    cout << "Maximum subarray sum: " << answer << '\n';

    return 0;
}