package org.example;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Pair <F, S>
{
    public F first;
    public S second;

    public String toString()
    {
        return "{" + first + ", " + second + "}";
    }
}
