#include <iostream>
#include <vector>
#include <algorithm>
using namespace std;

struct Job {
    int id;
    int deadline;
    int profit;
};

// Sort jobs by decreasing profit
bool compareJobs(const Job& a, const Job& b) {
    return a.profit > b.profit;
}

int main() {
    int n;

    cout << "Enter number of jobs: ";
    cin >> n;

    vector<Job> jobs(n);

    cout << "Enter job ID, zero-based deadline and profit:\n";

    for (int i = 0; i < n; i++) {
        cin >> jobs[i].id
            >> jobs[i].deadline
            >> jobs[i].profit;
    }

    // Process higher-profit jobs first
    sort(jobs.begin(), jobs.end(), compareJobs);

    int maxDeadline = 0;

    for (const Job& job : jobs) {
        maxDeadline = max(maxDeadline, job.deadline);
    }

    vector<int> slot(maxDeadline + 1, -1);

    int totalProfit = 0;
    int selectedJobs = 0;

    for (const Job& job : jobs)
    {
        // Search from the job's deadline backward
        for (int time = job.deadline; time >= 0; time--)
        {

            if (slot[time] == -1)
            {
                slot[time] = job.id;
                totalProfit += job.profit;
                selectedJobs++;
                break;
            }
        }
    }

    cout << "\nSelected jobs in execution order:\n";

    for (int time = 0; time <= maxDeadline; time++)
    {
        if (slot[time] != -1)
        {
            cout << slot[time] << " ";
        }
    }

    cout << "\n\nTime slot schedule:\n";

    for (int time = 0; time <= maxDeadline; time++)
    {
        cout << "Slot " << time << ": ";

        if (slot[time] == -1)
        {
            cout << "Empty";
        }
        else
        {
            cout << "Job " << slot[time];
        }

        cout << '\n';`
    }

    cout << "\nNumber of selected jobs: " << selectedJobs;
    cout << "\nMaximum total profit: " << totalProfit << '\n';

    return 0;
}