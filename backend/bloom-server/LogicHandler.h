#ifndef LOGICHANDLER_H
#define LOGICHANDLER_H

#include <string>
#include <vector>
#include "BloomFilter.h"



class LogicHandler {
public:
    LogicHandler(BloomFilter* bf);
    std::string HandleLine(std::string line);
    bool isUrl(const std::string& url);

private:
    BloomFilter* m_bf;
    std::string line;
    std::string HandleGet(const std::string& url);
    std::string HandlePost(const std::string& url);
    std::string HandleDelete(const std::string& url);
};
#endif