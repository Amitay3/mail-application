
import socket

BUFFER_SIZE = 4096

class TCPClient:
    def __init__(self, ip, port):
        self.server_ip = ip
        self.server_port = port
        # Create a TCP socket (IPv4 + TCP)
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    def connect(self):
        # Connect the socket to the server
        self.sock.connect((self.server_ip, self.server_port))

    def send_command(self, command):
        # Define behavior for input without newline
        if not command.endswith('\n'):
            command += '\n'
        # Send the command encoded in UTF-8
        self.sock.sendall(command.encode('utf-8'))

    def receive_response(self):
        # Recieve server response and return it as a string
        data = self.sock.recv(BUFFER_SIZE)
        return data.decode('utf-8')

    def close(self):
        self.sock.close()
