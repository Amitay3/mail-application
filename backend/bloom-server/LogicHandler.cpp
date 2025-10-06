#include <string>
#include <vector>
#include "BloomFilter.h"
#include "LogicHandler.h"
#include <sstream>
#include <regex>

using namespace std;
LogicHandler::LogicHandler(BloomFilter* bf) {
    m_bf = bf;
}

string LogicHandler::HandleLine(std::string line) {
    // Break the line into action and URL and handle accordingly
    std::istringstream iss(line);
    std::string action, url;
    iss >> action >> url;
    if (!isUrl(url)) {
        return "400 Bad Request\n";
    }else if (action == "POST") {
        return HandlePost(url);
    }else if (action == "GET") {
        return HandleGet(url);
    }else if (action == "DELETE") {
        return HandleDelete(url);
    }else {
        return "400 Bad Request\n";
    }
}

string LogicHandler::HandlePost(const string& url) {
    // Check if the URL is already in the Bloom filter and blacklist
    if (m_bf->mightContain(url) && m_bf->isInRealBlacklist(url)) {
        return "400 Bad Request\n";
    }
    else {
        // URL is not in the Bloom filter or blacklist, insert it
        m_bf->insert(url);
        return "201 Created\n";
    }
}
string LogicHandler::HandleGet(const string& url) {
    string response = "200 Ok\n\n";
    // Check if the URL is in the Bloom filter
    if (m_bf->mightContain(url)) {
        response.append("true ");
        if (m_bf->isInRealBlacklist(url)) {
            // True positive
            response.append("true\n");
        }
        else {
            // False positive
            response.append("false\n");
        }
    }
    else {
        // URL is not in the Bloom filter
        response.append("false\n");
    }
    return response;
}

string LogicHandler::HandleDelete(const string& url) {
    if (m_bf->deleteUrl(url) != 1) {
        return "404 Not Found\n";
    }
    else return "204 No Content\n";
}

// Function to check if a string is a valid URL according to a regex pattern
bool LogicHandler::isUrl(const std::string& url) {
    std::regex url_regex(
        R"(^(https?://)?(www\.)?([a-zA-Z0-9-]+\.)+[a-zA-Z0-9]{2,}(/.*)?$)",
        std::regex::icase
    );
    return std::regex_match(url, url_regex);
}

