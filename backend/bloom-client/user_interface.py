# Can be used in the future for more complex user interfaces
def read_user_command():
    # Read line from the user
    return input()

def print_server_response(response):
    # Print the server response exactly as it was received
    # The end='' is to avoid printing extra newlines
    print(response, end='')