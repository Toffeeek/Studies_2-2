//
// Created by tawfiq on 7/20/26.
//
#include <bits/stdc++.h>
using namespace std;

struct Task
{
    int startTime;
    int endTime;
    int idx;
    Task(int s, int e, int i) : startTime(s), endTime(e), idx(i)
    {

    }
};

bool sortTasks(const Task& a, const Task& b)
{
    if (a.endTime == b.endTime)
    {
        return a.idx < b.idx;
    }

    return a.endTime < b.endTime;
}

auto taskScheduler(const vector<Task>& tasks)
{
    vector<Task> sortedTasks(tasks);
    sort(sortedTasks.begin(), sortedTasks.end(), sortTasks);

    int currTime = 0;
    vector<int> selectedIndices;
    for (auto task : sortedTasks)
    {
        if (task.startTime >= currTime)
        {
            selectedIndices.push_back(task.idx);
            currTime = task.endTime;
        }
    }

    return selectedIndices;
}

int main()
{
    // int N;
    // cin >> N;
    // vector<Task> tasks;
    // for (int i = 0; i < N; i++)
    // {
    //     int s, e;
    //     cin >> s >> e;
    //     tasks.emplace_back(s, e, i);
    // }

    vector<Task> tasks
    {
        Task(900, 1030, 0),
        Task(930, 1100, 1),
        Task(1000, 1130, 2),
        Task(1030, 1200, 3),
        Task(1100, 1230, 4),
        Task(1200, 1300, 5),
        Task(1230, 1400, 6),
        Task(1300, 1430, 7),
        Task(1400, 1500, 8),
        Task(1430, 1600, 9)
    };

    auto selectedIndices = taskScheduler(tasks);

    for (int i : selectedIndices)
    {
        cout << "Task " << i << ": " << tasks[i].startTime << "-" << tasks[i].endTime << endl;
    }




    return 0;
}