#ifndef BLOOMFILTER_H
#define BLOOMFILTER_H

#include <vector>
#include <string>
#include <functional>
#include "StdHash.h"
#include "IHashFunction.h"
// Class header for BloomFilter
class BloomFilter {
private:
    std::vector<bool> m_bit_array;
    int m_array_size;       
    std::string m_bit_array_file;
    std::string m_blacklist_file;
    std::vector<int> m_hash_counts;
    std::vector<std::string> m_blacklist;
    
public:
    // Constructor
    BloomFilter(int array_size, const std::vector<int>& hash_counts);
    bool isInRealBlacklist(const std::string& url);
    void printBlackList();
    // Getters
    int getArraySize() const;
    const std::vector<int>& getHashCounts() const;
    size_t hashMultiple(const StdHash& hasher, const std::string& input, int times) const;

    // Setters
    void setArraySize(int array_size);
    void setHashCounts(const std::vector<int>& hash_counts); 

    // Insert an element into the Bloom filter
    void insert(const std::string& item);
    // Function to hash an input multiple times
    bool mightContain(const std::string& url) const;

    int deleteUrl(const std::string& url);
    void removeUrlFromBlackListFile(const std::string& blacklist_file, const std::string& targetUrl);

};
#endif // BLOOMFILTER_H
