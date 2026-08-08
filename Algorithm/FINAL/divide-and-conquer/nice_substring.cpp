//
// Created by tawfiq on 7/13/26.
//
#include <iostream>
#include <string>
#include <unordered_set>
#include <cctype>
using namespace std;

string longestNiceSubstring(const string& text) {
    // A nice substring must contain at least two characters.
    if (text.size() < 2) {
        return "";
    }

    unordered_set<char> characters;

    for (char ch : text) {
        characters.insert(ch);
    }

    for (int i = 0; i < static_cast<int>(text.size()); i++)
    {
        char current = text[i];

        char oppositeCase;

        if (islower(static_cast<unsigned char>(current)))
        {
            oppositeCase = static_cast<char>(toupper(static_cast<unsigned char>(current)));
        }
        else
        {
            oppositeCase = static_cast<char>(tolower(static_cast<unsigned char>(current)));
        }

        // The current character does not have
        // its opposite case in this substring.
        if (!characters.count(oppositeCase))
        {
            string leftAnswer =
                longestNiceSubstring(text.substr(0, i));

            string rightAnswer =
                longestNiceSubstring(text.substr(i + 1));

            // Return the left one when both lengths are equal.
            if (leftAnswer.size() >= rightAnswer.size())
            {
                return leftAnswer;
            }

            return rightAnswer;
        }
    }

    // Every character has both uppercase and lowercase forms.
    return text;
}

int main() {
    string text = "YazaAay";

    cout << "Longest nice substring: "
         << longestNiceSubstring(text)
         << '\n';

    return 0;
}