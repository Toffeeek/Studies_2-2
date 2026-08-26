#include <iostream>
#include <queue>
#include <vector>
#include <unordered_map>
#include <string>
using namespace std;

struct Node {
    char character;
    int frequency;

    Node* left;
    Node* right;

    Node(char ch, int freq) {
        character = ch;
        frequency = freq;
        left = nullptr;
        right = nullptr;
    }

    Node(int freq, Node* leftChild, Node* rightChild) {
        character = '\0';   // Internal node
        frequency = freq;
        left = leftChild;
        right = rightChild;
    }
};

// Makes the priority queue a min-heap based on frequency
struct CompareNodes
{
    bool operator()(Node* a, Node* b) {
        return a->frequency > b->frequency;
    }
};

void generateCodes(Node* root, const string& code, unordered_map<char, string>& huffmanCodes)
{
    if (root == nullptr)
    {
        return;
    }

    // A leaf node represents an actual character
    if (root->left == nullptr && root->right == nullptr)
    {
        /*
            If there is only one unique character,
            assign it code "0".
        */
        if (code.empty())
        {
            huffmanCodes[root->character] = "0";
        } else
        {
            huffmanCodes[root->character] = code;
        }

        return;
    }

    // Left edge represents 0
    generateCodes(root->left, code + "0", huffmanCodes);

    // Right edge represents 1
    generateCodes(root->right, code + "1", huffmanCodes);
}

Node* buildHuffmanTree(const unordered_map<char, int>& frequencies) {
    priority_queue<Node*, vector<Node*>, CompareNodes> minHeap;

    // Create one leaf node for every unique character
    for (const auto& entry : frequencies)
    {
        char character = entry.first;
        int frequency = entry.second;

        minHeap.push(new Node(character, frequency));
    }

    /*
        Repeatedly remove the two nodes with the smallest
        frequencies and combine them.
    */
    while (minHeap.size() > 1)
    {
        Node* left = minHeap.top();
        minHeap.pop();

        Node* right = minHeap.top();
        minHeap.pop();

        int combinedFrequency =
            left->frequency + right->frequency;

        Node* parent =
            new Node(combinedFrequency, left, right);

        minHeap.push(parent);
    }

    return minHeap.top();
}

string encodeText(const string& text, const unordered_map<char, string>& huffmanCodes)
{
    string encodedText;

    for (char character : text) {
        encodedText += huffmanCodes.at(character);
    }

    return encodedText;
}

string decodeText(const string& encodedText, Node* root) {
    string decodedText;

    if (root == nullptr) {
        return decodedText;
    }

    // Special case: only one unique character
    if (root->left == nullptr && root->right == nullptr) {
        for (char bit : encodedText) {
            decodedText += root->character;
        }

        return decodedText;
    }

    Node* current = root;

    for (char bit : encodedText) {
        if (bit == '0') {
            current = current->left;
        } else {
            current = current->right;
        }

        // Reached a leaf node
        if (current->left == nullptr &&
            current->right == nullptr) {

            decodedText += current->character;
            current = root;
        }
    }

    return decodedText;
}

void deleteTree(Node* root)
{
    if (root == nullptr)
    {
        return;
    }

    deleteTree(root->left);
    deleteTree(root->right);

    delete root;
}

int main() {
    string text = "in the jungle the mighty jungle the lion sleeps tonight";

    // cout << "Enter text: ";
    // getline(cin, text);

    if (text.empty())
    {
        cout << "The input text is empty.\n";
        return 0;
    }

    unordered_map<char, int> frequencies;

    // Count character frequencies
    for (char character : text)
    {
        frequencies[character]++;
    }

    Node* root = buildHuffmanTree(frequencies);

    unordered_map<char, string> huffmanCodes;

    generateCodes(root, "", huffmanCodes);

    cout << "\nCharacter frequencies and Huffman codes:\n";

    for (const auto& entry : frequencies) {
        char character = entry.first;

        if (character == ' ') {
            cout << "[space]";
        } else {
            cout << character;
        }

        cout << " -> Frequency: " << entry.second
             << ", Code: " << huffmanCodes[character]
             << '\n';
    }

    string encodedText = encodeText(text, huffmanCodes);

    cout << "\nOriginal text:\n";
    cout << text << '\n';

    cout << "\nEncoded text:\n";
    cout << encodedText << '\n';

    cout << "\nDecoded text:\n";
    cout << decodeText(encodedText, root) << '\n';

    deleteTree(root);

    return 0;
}