//
// Created by tawfiq on 8/2/26.
//
#include <bits/stdc++.h>
using namespace std;
using ll = long long;

constexpr float WIN_PROBABILITY = 0.9;
constexpr float LOSS_PROBABILITY = 1 - WIN_PROBABILITY;
constexpr ll TOTAL_TRIES = 1e5;

int main()
{
    bool lostPrevGame = false;
    srand(time(nullptr));


    ll totalRoundsSurvived = 0;
    for (ll i = 0; i < TOTAL_TRIES; i++)
    {
        bool lostPreviousRound = false;
        ll roundSurvived = 0;
        while (true)
        {
            roundSurvived++;
            float randFloat = (rand() % 10000) / 10000.0f;
            if (randFloat < WIN_PROBABILITY)
            {
                lostPreviousRound = false;
            }
            else
            {
                if (lostPreviousRound)
                {
                    break;
                }
                else
                {
                    lostPreviousRound = true;
                }
            }
        }

        totalRoundsSurvived += roundSurvived;
    }

    cout << "Expected Rounds Played: "
         << static_cast<double>(totalRoundsSurvived) / TOTAL_TRIES;


}