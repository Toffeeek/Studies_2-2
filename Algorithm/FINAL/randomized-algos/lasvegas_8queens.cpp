//
// Created by tawfiq on 8/2/26.
//
#include <bits/stdc++.h>
using namespace std;
using ll = long long;

constexpr ll TOTAL_TRIES = 1e5;

bool checkUp(const vector<vector<bool>>& board, int currRow, int currCol)
{
    for (int row = currRow - 1; row >= 0; row--)
    {
        if (board[row][currCol] == true)
        {
            return false;
        }
    }
    return true;
}

bool checkLeftDiagonal(const vector<vector<bool>>& board, int currRow, int currCol)
{
    for (int col = currCol - 1; col >= 0 && currRow > 0; col--)
    {
        if (board[--currRow][col] == true)
        {
            return false;
        }
    }
    return true;
}

bool checkRightDiagonal(const vector<vector<bool>>& board, int currRow, int currCol)
{
    for (int col = currCol + 1; col < 8 && currRow > 0; col++)
    {
        if (board[--currRow][col] == true)
        {
            return false;
        }
    }
    return true;
}

vector<int> getValidColumns(const vector<vector<bool>>& board, int currRow)
{
    vector<int> validCols;
    for (int col = 0; col < 8; col++)
    {
        if (checkUp(board, currRow, col) && checkLeftDiagonal(board, currRow, col) && checkRightDiagonal(board, currRow, col))
        {
            validCols.push_back(col);
        }
    }
    return validCols;
}

int getRandomCol(const vector<int>& validCols)
{
    int randIdx = rand() % validCols.size();
    return validCols[randIdx];
}

void printBoard(const vector<vector<bool>>& board)
{
    cout << "┌";
    for (int col = 0; col < 8; col++)
    {
        cout << "───";
        cout << (col == 7 ? "┐" : "┬");
    }
    cout << '\n';

    for (int row = 0; row < 8; row++)
    {
        cout << "│";

        for (int col = 0; col < 8; col++)
        {
            if (board[row][col])
                cout << " Q ";
            else
                cout << "   ";

            cout << "│";
        }

        cout << '\n';

        if (row == 7)
        {
            cout << "└";

            for (int col = 0; col < 8; col++)
            {
                cout << "───";
                cout << (col == 7 ? "┘" : "┴");
            }
        }
        else
        {
            cout << "├";

            for (int col = 0; col < 8; col++)
            {
                cout << "───";
                cout << (col == 7 ? "┤" : "┼");
            }
        }

        cout << '\n';
    }
}


int main()
{
    srand(time(nullptr));


    for (ll t = 0; t < TOTAL_TRIES; t++)
    {
        bool valid = false;
        vector<vector<bool>> board(8, vector(8, false));
        for (int row = 0; row < 8; row++)
        {
            auto validCols = getValidColumns(board, row);

            if (validCols.empty())
            {
                break;
            }

            int randomCol = getRandomCol(validCols);
            board[row][randomCol] = true;
            if (row == 7)
                valid = true;
        }

        if (valid)
        {
            cout << "Valid board found on attempt " << t << endl;
            printBoard(board);
            break;
        }
    }

    return 0;
}