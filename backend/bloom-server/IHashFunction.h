#ifndef IHASHFUNCTION_H
#define IHASHFUNCTION_H

#include <string>
// Interface for hash functions
class IHashFunction {
    public:
        // hash function that has to be implemented in every class that inherits this interface.
        virtual size_t hash(const std::string& input) const = 0;
        // Destructor
        virtual ~IHashFunction() = default;
    };
#endif