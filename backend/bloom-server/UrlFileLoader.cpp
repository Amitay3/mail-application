#include "UrlFileLoader.h"
#include <fstream>
#include <stdexcept>
// Class to load URLs from a file
// This class implements the ILoader interface for loading URLs from a file.
UrlFileLoader::UrlFileLoader(const std::string& filePath){
   m_filePath=filePath;
}
// Load the URLs from the file
void UrlFileLoader::load() {
    std::ifstream file(m_filePath);
    if (!file.is_open()) {
        throw std::runtime_error("Failed to open file: " + m_filePath);
    }

    std::string line;
    m_urls.clear();
    while (std::getline(file, line)) {
        if (!line.empty()) {
            m_urls.push_back(line);
        }
    }

    file.close();
}

const std::vector<std::string>& UrlFileLoader::getUrls() const {
    return m_urls;
}