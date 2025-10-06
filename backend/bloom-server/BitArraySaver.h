#ifndef BITARRAYSAVER_H
#define BITARRAYSAVER_H

#include "ISaver.h"
#include <fstream>
#include <stdexcept>
#include <vector>
// Class header to save a bit array to a file
class BitArraySaver : public ISaver<std::vector<bool>> {
public:
    BitArraySaver(const std::string& filepath);
    void save(const std::vector<bool>& bits) override;
    
private:
    std::string m_filepath;
};
#endif