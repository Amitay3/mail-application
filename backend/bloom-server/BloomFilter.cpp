#include "BloomFilter.h"
#include "StdHash.h"
#include "IHashFunction.h"
#include <functional> 
#include <string>     
#include <vector>     
#include <iostream> 
#include "BitArrayLoader.h"
#include "UrlFileLoader.h"
#include "UrlFileSaver.h"
#include "BitArraySaver.h"
#include <fstream>
#include <algorithm>


using namespace std;

// Constructor
BloomFilter::BloomFilter(int array_size, const std::vector<int>& hash_counts)
{
    m_array_size = array_size;
    m_hash_counts = hash_counts;
    // Initialize bit array and blacklist file paths
    m_bit_array_file = "data/bit_array.txt"; 
    m_blacklist_file = "data/blacklist.txt";

    // Ensure files exist
    std::ofstream bit_array_out(m_bit_array_file, std::ios::app); 
    if (!bit_array_out) {
        std::cerr << "Error: Could not create or open " << m_bit_array_file << std::endl;
        return;
    }
    std::ofstream blacklist_out(m_blacklist_file, std::ios::app); 
    if (!blacklist_out) {
        std::cerr << "Error: Could not create or open " << m_blacklist_file << std::endl;
        return;
    }
    
    std::ifstream file(m_bit_array_file, std::ios::ate);
    // Check if file is empty
    if (file.tellg() == 0) {
        m_bit_array.resize(array_size,0);
        BitArraySaver saver(m_bit_array_file);
        saver.save(m_bit_array);
    }
    else {
        // Load bit array
        BitArrayLoader bit_array_loader(m_bit_array_file);
        bit_array_loader.load();
        m_bit_array = bit_array_loader.getBitArray();
    }

    // Initialize blacklist 
    UrlFileLoader file_loader(m_blacklist_file);
    file_loader.load();
    m_blacklist = file_loader.getUrls();
}


// Getters
int BloomFilter::getArraySize() const {
    return m_array_size;
}
const std::vector<int>& BloomFilter::getHashCounts() const {
    return m_hash_counts;
}

// Setters
void BloomFilter::setArraySize(int array_size) {
    m_array_size = array_size;
    m_bit_array.resize(array_size, 0);
}
void BloomFilter::setHashCounts(const std::vector<int>& hash_counts) {
    m_hash_counts = hash_counts;
}

// Insert an element into the Bloom filter
void BloomFilter::insert(const std::string& url) {
    UrlFileSaver url_saver(m_blacklist_file);
    BitArraySaver array_saver(m_bit_array_file);
    // Hash the URL multiple times and set the corresponding bits in the bit array
    for (int count : m_hash_counts) {
        StdHash hasher;
        size_t on_bit = hashMultiple(hasher, url, count) % m_array_size;
        m_bit_array[on_bit] = true;
    }
    
    // Update blacklist file and bit array
    url_saver.save(url);
    m_blacklist.push_back(url);
    array_saver.save(m_bit_array);
}

// Function to hash an input multiple times
size_t BloomFilter::hashMultiple(const StdHash& hasher, const std::string& input, int times) const {
    unsigned int hash_val = 0;
    std::string temp_url = input;
    
    for (int i = 0; i <times; ++i) {
        hash_val = hasher.hash(temp_url);
        temp_url = std::to_string(hash_val);
    }
    return hash_val;
}

// Function to check if a url might be blacklisted
bool BloomFilter::mightContain(const std::string& url) const {
    StdHash hasher;
    for (int count : m_hash_counts) {
        size_t on_bit = hashMultiple(hasher, url, count) % m_array_size;
        if (!m_bit_array[on_bit]) {
            return false;
        }
    }
    return true;
}
// Function to check if a URL is in the real blacklist
bool BloomFilter::isInRealBlacklist(const std::string& url) {
    for (std::string bl_url : m_blacklist) {
        if (bl_url == url) {
            return true;
        }
    }
    return false;
}

// Function to delete a url from the blacklist file and from the blacklist itself
int BloomFilter::deleteUrl(const std::string& url) {
    if (isInRealBlacklist(url)) {
        // Find where the url is in the string
        auto it = std::find(m_blacklist.begin(), m_blacklist.end(), url);
        m_blacklist.erase(it);
        removeUrlFromBlackListFile(m_blacklist_file, url);
        return 1;
    }
    return 0;
}

// Function to remove a url from the blacklist file
void BloomFilter::removeUrlFromBlackListFile(const std::string& blacklist_file, const std::string& targetUrl) {
    std::ifstream inputFile(blacklist_file);
    std::vector<std::string> lines;
    std::string line;

    // Read all lines
    while (std::getline(inputFile, line)) {
        if (line != targetUrl) {
            lines.push_back(line);
        }
    }
    inputFile.close();

    // Overwrite the file and write back the lines (except the target)
    std::ofstream outputFile(blacklist_file, std::ios::trunc);
    for (const std::string& i : lines) {
        outputFile << i << '\n';
    }
    outputFile.close();
}

// Function to print the blacklist
void BloomFilter::printBlackList() {
    for (const auto& s : m_blacklist) {
        std::cout << s <<"\n";
    }
}