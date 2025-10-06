#ifndef BITARRAYLOADER_H
#define BITARRAYLOADER_H

#include "ILoader.h"
#include <string>
#include <vector>

// Class header to load a bit array from a file
class BitArrayLoader : public ILoader {

public:
    BitArrayLoader(const std::string& filePath);
    void load() override;
    virtual const std::vector<bool>& getBitArray() const override;

private:
    std::string m_filePath;
    std::vector<bool> m_bit_array;
};

#endif