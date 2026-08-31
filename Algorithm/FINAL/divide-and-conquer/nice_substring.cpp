
#include <iostream>
#include <string>
#include <unordered_set>
#include <cctype>
using namespace std;

string longestNiceSubstring(const string& text)
{
    // A nice substring must contain at least two characters.
    if (text.size() < 2) {
        return "";
    }

    // unordered_set<char> characters;
    //
    // for (char ch : text) {
    //     characters.insert(ch);
    // }

    for (int i = 0; i < text.size(); i++)
    {
        char current = text[i];

        char oppositeCase;

        if (islower(current))
        {
            oppositeCase = toupper(static_cast<unsigned char>(current));
        }
        else
        {
            oppositeCase = tolower(static_cast<unsigned char>(current));
        }

        // The current character does not have
        // its opposite case in this substring.
        if (text.find(oppositeCase) == string::npos)
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
    string text = "YazaAabyBY";

    cout << "Longest nice substring: "
         << longestNiceSubstring(text)
         << '\n';

    return 0;
}