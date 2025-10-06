#ifndef SOCKETMANAGER_H
#define SOCKETMANAGER_H

#include <iostream>
#include <sys/socket.h>
#include <stdio.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <string.h>

class SocketManager {
public:
    SocketManager(int port, const char* ip);
    int acceptClient();
    int getServerSocket();
    ~SocketManager();

private:
    int m_port;
    int m_server_socket;
};
#endif
