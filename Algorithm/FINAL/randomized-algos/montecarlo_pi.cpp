//
// Created by tawfiq on 8/2/26.
//
#include <bits/stdc++.h>
using namespace std;
using ll = long long;

constexpr int RADIUS = 1;
constexpr int SQUARE_SIDE = 2;

void printTries(int i)
{
    int digits = 0;
    int i_copy = i;
    while (i_copy > 0)
    {
        i_copy /= 10;
        digits++;
    }
    cout << "tries: " << i;
    for (int j= 0; j < digits; j++)
    {
        cout << "\b";
    }
    // cout << "\b\b\b\b\b\b\b\b";
    cout << endl;
}


int main()
{
    srand(static_cast<unsigned int>(time(nullptr)));

    ll dotsInsideCircle = 0;
    ll totalTries = 1e8;

    for (ll i = 0; i < totalTries; i++)
    {
        // printTries(i);
        cout << i << endl;
        float randX = (rand() % 20000 - 10000) / 10000.0f;
        float randY = (rand() % 20000 - 10000) / 10000.0f;

        if (randX * randX + randY * randY <= RADIUS * RADIUS)
        {
            dotsInsideCircle++;
        }
    }

    float pi = 4 * (static_cast<float>(dotsInsideCircle) / static_cast<float>(totalTries));
    cout << "PI: " << pi;

}