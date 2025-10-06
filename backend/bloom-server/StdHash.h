#ifndef STDHASH_H
#define STDHASH_H

#include <string>
#include <functional>
#include "IHashFunction.h"
// Class header for standard hash function
class StdHash : public IHashFunction{
public:
    virtual size_t hash(const std::string& input) const override {
        std::hash<std::string> hasher;
        return hasher(input);
    }
};
#endif