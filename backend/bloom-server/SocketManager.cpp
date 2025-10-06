#include <string>     
#include <vector>  
#include "SocketManager.h"
#include <arpa/inet.h>

using namespace std;
// Constructor
SocketManager::SocketManager(int port, const char* ip) {
    m_server_socket = socket(AF_INET, SOCK_STREAM, 0);
    if (m_server_socket < 0) {
        perror("error creating socket");
    }
    // Struct sockaddr_in is used to specify an endpoint address for the socket
    struct sockaddr_in sin;
    memset(&sin, 0, sizeof(sin));
    sin.sin_family = AF_INET;
    sin.sin_addr.s_addr = inet_addr(ip);
    sin.sin_port = htons(port);
    // Bind the socket to the specified IP address and port
    if (bind(m_server_socket, (struct sockaddr*)&sin, sizeof(sin)) < 0) {
        perror("error binding socket");
    }
    // Listen for incoming connections on the socket
    if (listen(m_server_socket, 5) < 0) {
        perror("error listening to a socket");
    }
}
// Accept a client connection
int SocketManager::acceptClient() {
    sockaddr_in clientAddr{};
    socklen_t len = sizeof(clientAddr);
    return accept(m_server_socket, (struct sockaddr*)&clientAddr, &len);
}
// Get the server socket file descriptor
int SocketManager::getServerSocket() {
    return m_server_socket;
}
// Destructor
SocketManager::~SocketManager() {
    if (m_server_socket > 0) {
        close(m_server_socket);
    }
}

