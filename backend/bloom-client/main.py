
import sys
from tcp_client import TCPClient
from user_interface import read_user_command, print_server_response

def main():
    # Check for correct number of command-line arguments
    if len(sys.argv) != 3:
        sys.exit(1)  
    server_ip = sys.argv[1]
    server_port = int(sys.argv[2])

    # Create a TCP client and connect to the server
    client = TCPClient(server_ip, server_port)
    client.connect()

    try:
        # Infinite loop to read user commands and send to server
        while True:
            # Read from user, send to server, and print response
            command = read_user_command()
            client.send_command(command)
            response = client.receive_response()
            print_server_response(response) 
    finally:
        client.close()
        
if __name__ == "__main__":
    main()

