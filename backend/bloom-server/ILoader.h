#ifndef ILOADER_H
#define ILOADER_H

#include <vector>
#include <string>
// Interface for loading data
class ILoader {
public:
    virtual ~ILoader() = default;
    virtual void load() = 0;
    virtual const std::vector<std::string>& getUrls() const {
        static std::vector<std::string> empty;  
        return empty;
    }

    virtual const std::vector<bool>& getBitArray() const {
        static std::vector<bool> empty; 
        return empty;
    }
};

#endif