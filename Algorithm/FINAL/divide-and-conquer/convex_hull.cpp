//
// Created by tawfiq on 7/13/26.
//
#include <iostream>
#include <vector>
#include <set>
#include <cstdlib>
using namespace std;

struct Point {
    long long x;
    long long y;

    bool operator<(const Point& other) const {
        if (x != other.x) {
            return x < other.x;
        }

        return y < other.y;
    }
};

// Cross product of vectors AB and AP.
long long crossProduct(
    const Point& a,
    const Point& b,
    const Point& p
) {
    return (b.x - a.x) * (p.y - a.y)
         - (b.y - a.y) * (p.x - a.x);
}

// Returns the side of point P relative to line AB.
int findSide(
    const Point& a,
    const Point& b,
    const Point& p
) {
    long long value = crossProduct(a, b, p);

    if (value > 0) {
        return 1;
    }

    if (value < 0) {
        return -1;
    }

    return 0;
}

// Returns a value proportional to the distance
// between point P and line AB.
long long lineDistance(
    const Point& a,
    const Point& b,
    const Point& p
) {
    return llabs(crossProduct(a, b, p));
}

void buildHull(
    const vector<Point>& points,
    const Point& a,
    const Point& b,
    int side,
    set<Point>& hull
) {
    int farthestIndex = -1;
    long long maximumDistance = 0;

    for (int i = 0; i < static_cast<int>(points.size()); i++) {
        long long distance = lineDistance(a, b, points[i]);

        if (
            findSide(a, b, points[i]) == side &&
            distance > maximumDistance
        ) {
            farthestIndex = i;
            maximumDistance = distance;
        }
    }

    // No point exists on the required side.
    // A and B are hull points.
    if (farthestIndex == -1) {
        hull.insert(a);
        hull.insert(b);
        return;
    }

    Point farthestPoint = points[farthestIndex];

    // Recursively process the two new regions.
    buildHull(
        points,
        farthestPoint,
        a,
        -findSide(farthestPoint, a, b),
        hull
    );

    buildHull(
        points,
        farthestPoint,
        b,
        -findSide(farthestPoint, b, a),
        hull
    );
}

set<Point> quickHull(const vector<Point>& points) {
    set<Point> hull;

    if (points.size() < 3) {
        for (const Point& point : points) {
            hull.insert(point);
        }

        return hull;
    }

    int minimumXIndex = 0;
    int maximumXIndex = 0;

    for (int i = 1; i < static_cast<int>(points.size()); i++) {
        if (points[i].x < points[minimumXIndex].x) {
            minimumXIndex = i;
        }

        if (points[i].x > points[maximumXIndex].x) {
            maximumXIndex = i;
        }
    }

    Point leftmost = points[minimumXIndex];
    Point rightmost = points[maximumXIndex];

    // Find hull points above line leftmost-rightmost.
    buildHull(
        points,
        leftmost,
        rightmost,
        1,
        hull
    );

    // Find hull points below line leftmost-rightmost.
    buildHull(
        points,
        leftmost,
        rightmost,
        -1,
        hull
    );

    return hull;
}

int main() {
    vector<Point> points = {
        {0, 3},
        {2, 2},
        {1, 1},
        {2, 1},
        {3, 0},
        {0, 0},
        {3, 3}
    };

    set<Point> hull = quickHull(points);

    cout << "Points on the convex hull:\n";

    for (const Point& point : hull) {
        cout << "(" << point.x
             << ", " << point.y << ")\n";
    }

    return 0;
}