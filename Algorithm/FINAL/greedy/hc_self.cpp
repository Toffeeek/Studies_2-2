//
// Created by tawfiq on 7/27/26.
//
#include <bits/stdc++.h>
using namespace std;

struct Node {
    int freq;
    char ch;

    Node* left;
    Node* right;

    Node(int freq, char ch) : freq(freq), ch(ch)
    {
        left = nullptr;
        right = nullptr;
    }
    Node(Node* left, Node* right, int freq) : freq(freq), left(left), right(right)
    {
        this->ch = '\0';
    }
};

struct compareNodes
{
    bool operator ()(const Node* a, const Node* b) const
    {
        return a->freq > b->freq;
    }
};


Node* buildHuffmanTree(const unordered_map<char, int>& frequencies)
{
    priority_queue
    <
        Node*,
        vector<Node*>,
        compareNodes
    > pq;

    for (auto [ch, freq] : frequencies)
    {
        pq.push(new Node(freq, ch));
    }

    while (pq.size() > 1)
    {
        Node* left = pq.top();
        pq.pop();
        Node* right = pq.top();
        pq.pop();

        pq.push(new Node(left, right, left->freq + right->freq));
    }

    return pq.top();
}

void generateHuffmanCodes(const Node* currNode, const string& code, unordered_map<char, string>& huffmanCodes)
{
    if (currNode == nullptr)
    {
        return;
    }

    if (currNode->left == nullptr && currNode->right == nullptr)
    {
        if (code.empty())
        {
            huffmanCodes[currNode->ch] = "0";
        }
        else
        {
            huffmanCodes[currNode->ch] = code;
        }

        return;
    }

    generateHuffmanCodes(currNode->left, code + "0", huffmanCodes);
    generateHuffmanCodes(currNode->right, code + "1", huffmanCodes);
}

string encodeText(const string& text, const unordered_map<char, string>& huffmanCodes)
{
    string code = "";

    for (char ch : text)
    {
        code += huffmanCodes.at(ch);
    }
    return code;
}

auto calculateFrequencies(const string& text)
{
    unordered_map<char, int> freq;
    for (char ch : text)
    {
        freq[ch]++;
    }
    return freq;
}

int main()
{
    string text = "in the jungle the mighty jungle the lion sleeps tonight";
    auto frequencies = calculateFrequencies(text);

    // cout << "generated freqs\n";

    auto huffmanTree = buildHuffmanTree(frequencies);

    unordered_map<char, string> huffmanCodes;
    generateHuffmanCodes(huffmanTree, "", huffmanCodes);

    cout << encodeText(text, huffmanCodes);


    return 0;
}