//
// Created by tawfiq on 7/26/26.
//
#include <iostream>
#include <vector>
#include <algorithm>
#include <queue>
using namespace std;

struct Lecture {
    int start;
    int end;
    char name;
};

struct Room
{
    int endTime;
    int roomNumber;

    // Makes priority_queue behave like a min-heap
    bool operator > (const Room& other) const
    {
        return this->endTime > other.endTime;
    }
};

bool sortLectures(const Lecture& a, const Lecture& b)
{
    if (a.start == b.start)
    {
        return a.end < b.end;
    }
    return a.start < b.start;
}


int main() {
    int n;
    cout << "Enter number of lectures: ";
    cin >> n;

    vector<Lecture> lectures(n);

    cout << "Enter lecture name, start time and end time:\n";

    for (int i = 0; i < n; i++)
    {
        cin >> lectures[i].name
            >> lectures[i].start
            >> lectures[i].end;
    }

    // Sort lectures according to starting time
    sort(lectures.begin(), lectures.end(), sortLectures);

    // Stores rooms according to earliest finishing lecture
    priority_queue<Room, vector<Room>, greater<Room>> availableRooms;

    int roomCount = 0;

    // Stores the lectures assigned to each room
    vector<vector<Lecture>> schedule(n);

    for (const Lecture& lecture : lectures)
    {

        // Reuse the earliest available room
        if (!availableRooms.empty() &&
            availableRooms.top().endTime <= lecture.start)
        {
            Room room = availableRooms.top();
            availableRooms.pop();

            schedule[room.roomNumber].push_back(lecture);

            // Update the room's ending time
            room.endTime = lecture.end;
            availableRooms.push(room);
        }
        else
        {
            // No room is free, so create a new room
            schedule[roomCount].push_back(lecture);
            availableRooms.push({lecture.end,roomCount++});
        }
    }

    cout << "\nMinimum number of rooms required: "
         << roomCount << "\n";

    for (int room = 0; room < roomCount; room++) {
        cout << "\nRoom " << room << ":\n";

        for (const Lecture& lecture : schedule[room]) {
            cout << lecture.name
                 << " (" << lecture.start
                 << ", " << lecture.end << ")\n";
        }
    }

    return 0;
}