#include "ThreadPool.h"
#include <vector>
#include <thread>
#include <queue>
#include <mutex>
#include <condition_variable>
#include <functional>
#include <atomic>
// ThreadPool implementation
ThreadPool::ThreadPool(size_t numThreads) : stop(false) {
    for (size_t i = 0; i < numThreads; ++i) {
        workers.emplace_back(&ThreadPool::workerLoop, this);
    }
}
// Add a task to the thread pool
void ThreadPool::addTask(std::function<void()> task) {
    {
        std::lock_guard<std::mutex> lock(queueMutex);
        tasks.push(std::move(task));
    }
    condition.notify_one();
}
// Worker loop that processes tasks
void ThreadPool::workerLoop() {
    while (true) {
        std::function<void()> task;

        {
            // Wait for a task to be available or for the pool to stop
            std::unique_lock<std::mutex> lock(queueMutex);
            condition.wait(lock, [this] {
                return stop || !tasks.empty();
            });

            if (stop && tasks.empty()) return;
            // Get the next task
            task = std::move(tasks.front());
            tasks.pop();
        }
        // Execute the task outside the lock
        task(); 
    }
}
// Destructor to clean up the thread pool
ThreadPool::~ThreadPool() {
    {
        std::lock_guard<std::mutex> lock(queueMutex);
        stop = true;
    }
    condition.notify_all();
    for (std::thread &worker : workers) {
        if (worker.joinable()) {
            worker.join();
        }
    }
}
