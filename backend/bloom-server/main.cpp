#include <string>
#include <vector>
#include "BloomFilter.h"
#include <iostream>
#include <algorithm>
#include <sstream>
#include <regex>
#include <fstream>
#include <iostream> 
#include "Server.h"
#include "LogicHandler.h"

using namespace std;

// Main function to run the Bloom filter program
int main(int argc, char** argv) {
    BloomFilter* filter = nullptr;
    vector<int> hash_counts;
    // Check that there are at least 4 arguemnts given as input. Ip, port, BloomFilter size and at least one hash function
    if (argc < 5) {
        exit(EXIT_FAILURE);
    }
    // Initialize hash count vector
    for (int i = 4; i < argc; i++) {
        hash_counts.push_back(stoi(argv[i]));
    }

    // Initialize Bloom filter
    int array_size = stoi(argv[3]);
    filter = new BloomFilter(array_size, hash_counts);

    // Initialize LogicHandler
    LogicHandler logic_handler(filter);
    const char* ip = argv[1];
    int port = stoi(argv[2]);

    // Initialize the server and the start an infinite loop
    Server server(port, ip, logic_handler);
    server.run();
}