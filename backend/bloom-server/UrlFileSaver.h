#ifndef URLFILESAVER_H
#define URLFILESAVER_H

#include "ISaver.h"
#include <fstream>
#include <stdexcept>
// Class header to save URLs to a file
class UrlFileSaver : public ISaver<std::string> {
public:
    void save(const std::string& url) override;
    UrlFileSaver(const std::string& filepath);

private:
    std::string m_filepath;
};
#endif