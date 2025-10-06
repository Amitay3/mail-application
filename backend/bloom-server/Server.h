#ifndef SERVER_H
#define SERVER_H

#include <string>
#include <vector>
#include "BloomFilter.h"
#include "SocketManager.h"
#include "LogicHandler.h"
#include "ThreadPool.h"
#include <mutex>

class Server {
public:
    Server(int port, const char* ip, LogicHandler logic_handler);
    void run();
    ~Server();

private:
    SocketManager socket_manager;
    LogicHandler logic_handler;
    ThreadPool thread_pool;
    std::mutex logic_mutex;
    std::string m_ip;

    void handleClient(int client_socket);
};
#endif