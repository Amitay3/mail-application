#include "UrlFileSaver.h"
#include <fstream>
#include <stdexcept>

// Class to save URLs to a file
// This class implements the ISaver interface for saving URLs to a file.
UrlFileSaver::UrlFileSaver(const std::string& filepath) {
    m_filepath = filepath;
}
// Save a URL to the file
void UrlFileSaver::save(const std::string& url) {
    std::ofstream out(m_filepath, std::ios::app);
    if (!out) {
        throw std::runtime_error("Failed to open file: " + m_filepath);
    }
    out << url << '\n';
    out.close();
}