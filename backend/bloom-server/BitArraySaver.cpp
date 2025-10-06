#include "BitArraySaver.h"
#include <fstream>
#include <stdexcept>
#include <vector>

// Class to save a bit array to a file
BitArraySaver::BitArraySaver(const std::string& filepath) {
    m_filepath = filepath;
}
// Save the bit array to the file
void BitArraySaver::save(const std::vector<bool>& bits) {
    std::ofstream out(m_filepath);
    if (!out) {
        throw std::runtime_error("Failed to open file: " + m_filepath);
    }
    for(bool bit : bits) {
        out << bit;
    }
}