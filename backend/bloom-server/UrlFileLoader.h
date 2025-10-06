#ifndef URLFILELOADER_H
#define URLFILELOADER_H

#include "ILoader.h"
#include <string>
#include <vector>
// Class header to load URLs from a file
class UrlFileLoader : public ILoader {
public:
UrlFileLoader(const std::string& filePath);

    void load() override;
    const std::vector<std::string>& getUrls() const override;

private:
    std::string m_filePath;
    std::vector<std::string> m_urls;
};

#endif