#include "Server.h"
#include "LogicHandler.h"
#include "SocketManager.h"
#include "BloomFilter.h"
#include <iostream>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <string.h>


// Constructor
Server::Server(int port, const char* ip, LogicHandler logic_handler)
    : socket_manager(port, ip), logic_handler(logic_handler), thread_pool(4) {
}
// Destructor
Server::~Server() {
}
// Run the server to accept client connections and handle requests
void Server::run() {
    while (true) {
        int client_socket = socket_manager.acceptClient();
        if (client_socket < 0) {
            std::cerr << "Failed to accept client.\n";
            continue;
        }

        // Pass work to the thread pool
        thread_pool.addTask([this, client_socket]() {
            handleClient(client_socket);
        });
    }
}
// Handle client requests
void Server::handleClient(int client_socket) {
    char buffer[4096];
    
    while (true) {
        // Read data from the client
        ssize_t bytes_read = recv(client_socket, buffer, sizeof(buffer) - 1, 0);
        if (bytes_read <= 0) break;

        buffer[bytes_read] = '\0';
        std::string request(buffer);
        // Process the request using the logic handler
        std::string response;
        {
            std::lock_guard<std::mutex> lock(logic_mutex);
            response = logic_handler.HandleLine(request);
        }
        // Send the response back to the client
        ssize_t sent_bytes = send(client_socket, response.c_str(), response.size(), 0);
        if (sent_bytes < 0) break;
    }

    close(client_socket);
}

