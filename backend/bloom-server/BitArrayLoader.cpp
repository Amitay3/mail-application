#include <fstream>
#include <stdexcept>
#include "BitArrayLoader.h"

BitArrayLoader::BitArrayLoader(const std::string& filePath){
   m_filePath=filePath;
}
// Class to load a bit array from a file
void BitArrayLoader::load() {
    std::ifstream file(m_filePath);
    if (!file.is_open()) {
        throw std::runtime_error("Failed to open file: " + m_filePath);
    }
    // Read bits as chars from the file 
    char bitChar;
    m_bit_array.clear();
    while (file >> bitChar) {
        if (bitChar == '0' || bitChar == '1') {
            m_bit_array.push_back(bitChar == '1');
        } else {
            throw std::runtime_error("Invalid bit value in file: " + m_filePath);
        }
    }
   file.close();
}

const std::vector<bool>& BitArrayLoader::getBitArray() const {
    return m_bit_array;
}