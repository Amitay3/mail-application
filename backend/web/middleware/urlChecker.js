const net = require('net');

// Extract URLs from the given email content using regex
function checkEmailForUrls(emailContent) {
    const urlRegex = /(https?:\/\/[^\s/$.?#].[^\s]*)|(www\.[^\s/$.?#].[^\s]*)|([a-zA-Z0-9-]+\.[a-zA-Z]{2,}(?:\.[a-zA-Z]{2,})?(?:\/[^\s]*)?)/g;

    const urls = emailContent.match(urlRegex);
    if (!urls) {
        return [];
    }
    return urls;
}

// Checks if the given URL is blacklisted by communicating with a local server
function checkUrlWithServer(method, url) {
    url = url.toLowerCase();
    return new Promise((resolve, reject) => {
        // Create TCP connection to the local URL checking server
        console.log("connecting");
        const client = net.createConnection({host: process.env.BLOOM_HOST || 'bloom-server', port: process.env.BLOOM_PORT || 5588 }, () => {
            client.write(`${method} ${url}\n`);
        });

        let responseData =  '';

        // Collect data from server response
        client.on('data', (data) => {
            responseData += data.toString();
            console.log(responseData);

            // When the response contains a newline, process it
            if (responseData.includes('\n')) {
                responseData = responseData.trim();
                client.end(); // Close connection

                // Resolve true if response indicates URL is blacklisted or created/deleted successfully
                if (responseData.includes('true true')
                    || responseData.includes('201 Created')
                    || responseData.includes('204 No Content')) {
                    resolve(true);
                } else {
                    resolve(false);
                }
            }
        });

        // Handle connection end (no action needed here)
        client.on('end', () => {});

        // Handle errors during connection or communication
        client.on('error', (err) => {
            reject(new Error('Error connecting to server' + err.message));
            client.destroy();
        });
    });
}

module.exports = { checkEmailForUrls, checkUrlWithServer };
