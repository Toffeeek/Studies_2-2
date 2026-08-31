#include <bits/stdc++.h>
using namespace std;

struct Point {
    long long x, y;
};

// --------------------------------------------------
// Orientation
//
//  1  = clockwise
// -1  = counter-clockwise
//  0  = collinear
// --------------------------------------------------

int orientation(Point a, Point b, Point c) {

    long long value =
        (b.y - a.y) * (c.x - b.x)
        - (c.y - b.y) * (b.x - a.x);

    if (value == 0)
        return 0;

    return (value > 0) ? 1 : -1;
}


vector<Point> bruteHull(vector<Point> points)
{

    int n = points.size();

    if (n <= 2)
        return points;

    set<pair<long long,long long>> hullPoints;

    for (int i = 0; i < n; i++) {

        for (int j = i + 1; j < n; j++) {

            long long a =
                points[i].y - points[j].y;

            long long b =
                points[j].x - points[i].x;

            long long c =
                points[i].x * points[j].y
                - points[i].y * points[j].x;

            int positive = 0;
            int negative = 0;

            for (int k = 0; k < n; k++) {

                long long value =
                    a * points[k].x
                    + b * points[k].y
                    + c;

                if (value >= 0)
                    positive++;

                if (value <= 0)
                    negative++;
            }

            if (positive == n ||
                negative == n) {

                hullPoints.insert({
                    points[i].x,
                    points[i].y
                });

                hullPoints.insert({
                    points[j].x,
                    points[j].y
                });
            }
        }
    }


    vector<Point> hull;

    for (auto p : hullPoints)
        hull.push_back({p.first, p.second});


    double cx = 0;
    double cy = 0;

    for (Point p : hull) {
        cx += p.x;
        cy += p.y;
    }

    cx /= hull.size();
    cy /= hull.size();


    sort(hull.begin(), hull.end(),
         [&](Point a, Point b) {

             double angleA =
                 atan2(a.y - cy, a.x - cx);

             double angleB =
                 atan2(b.y - cy, b.x - cx);

             return angleA < angleB;
         });


    return hull;
}


// --------------------------------------------------
// Orientation
//
//  1  = clockwise
// -1  = counter-clockwise
//  0  = collinear
// --------------------------------------------------
pair<int, int> getUpperTangent(const vector<Point>& A, const vector<Point>& B, int rightA, int leftB)
{
    int n1 = A.size();
    int n2 = B.size();

    int upperA = rightA;
    int upperB = leftB;

    bool done = false;

    while (!done)
    {
        done = true;

        while (orientation(B[upperB],A[upperA],A[(upperA + 1) % n1]) >= 0)
        {
            upperA = (upperA + 1) % n1;
        }

        while (orientation(A[upperA],B[upperB],B[(upperB - 1 + n2) % n2]) <= 0)
        {
            upperB = (upperB - 1 + n2) % n2;
            done = false;
        }
    }

    return {upperA, upperB};
}

// --------------------------------------------------
// Orientation
//
//  1  = clockwise
// -1  = counter-clockwise
//  0  = collinear
// --------------------------------------------------
pair<int, int> getLowerTangent(const vector<Point>& A, const vector<Point>& B, const int rightA, const int leftB)
{
    int n1 = A.size();
    int n2 = B.size();

    int lowerA = rightA;
    int lowerB = leftB;

    bool done = false;

    while (!done)
    {
        done = true;

        while (orientation(A[lowerA],B[lowerB],B[(lowerB + 1) % n2]) >= 0)
        {
            lowerB = (lowerB + 1) % n2;
        }

        while (orientation(B[lowerB],A[lowerA],A[(lowerA - 1 + n1) % n1]) <= 0)
        {
            lowerA = (lowerA - 1 + n1) % n1;
            done = false;
        }
    }

    return {lowerA, lowerB};
}

vector<Point> mergeHulls(const vector<Point> &A, const vector<Point> &B)
{

    int n1 = A.size();
    int n2 = B.size();

    int rightA = 0;

    for (int i = 1; i < n1; i++)
    {
        if (A[i].x > A[rightA].x)
            rightA = i;
    }

    int leftB = 0;

    for (int i = 1; i < n2; i++)
    {
        if (B[i].x < B[leftB].x)
            leftB = i;
    }

    auto [upperA, upperB] = getUpperTangent(A, B, rightA, leftB);
    auto [lowerA, lowerB] = getLowerTangent(A, B, rightA, leftB);

    // Merge the two hulls
    vector<Point> result;

    int index = upperA;

    result.push_back(A[index]);

    while (index != lowerA)
    {

        index =
            (index + 1) % n1;

        result.push_back(A[index]);
    }

    index = lowerB;

    result.push_back(B[index]);

    while (index != upperB)
    {
        index = (index + 1) % n2;
        result.push_back(B[index]);
    }

    return result;
}


vector<Point> divideHull(vector<Point> points)
{

    int n = points.size();

    if (n <= 5)
        return bruteHull(points);

    int mid = n / 2;

    vector<Point> left(points.begin(), points.begin() + mid);
    vector<Point> right(points.begin() + mid, points.end());

    vector<Point> leftHull = divideHull(left);
    vector<Point> rightHull = divideHull(right);

    return mergeHulls(leftHull,rightHull);
}


vector<Point> convexHull(vector<Point> points) {

    sort(points.begin(), points.end(),
         [](Point a, Point b) {

             if (a.x != b.x)
                 return a.x < b.x;

             return a.y < b.y;
         });


    return divideHull(points);
}


int main() {

    vector<Point> points = {

        {0, 0},
        {1, 2},
        {2, 1},
        {3, 4},
        {4, 0},
        {2, 3},
        {5, 2},
        {6, 1}
    };


    vector<Point> hull =
        convexHull(points);


    cout << "Convex Hull:\n";

    for (Point p : hull) {

        cout << "("
             << p.x << ", "
             << p.y << ")\n";
    }
}