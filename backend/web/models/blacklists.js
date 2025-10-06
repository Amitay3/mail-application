const net = require('net');
const UrlChecker = require('../middleware/urlChecker');
const { generateId } = require('../middleware/generateId');

const blacklistedUrls = [];
let blacklistCounter = 0;

// Adds a URL to the blacklist if the server confirms it can be added
async function addUrlToBlacklist(url) {
    // Check with external server if URL can be added (POST)
    const result = await UrlChecker.checkUrlWithServer("POST", url.toLowerCase());
    if (result) {
        const urlToAdd = {
            id: generateId(),
            url: url.toLowerCase()
        };
        blacklistedUrls.push(urlToAdd);
        blacklistCounter++;
        return urlToAdd;
    } else {
        // Server rejected the URL, return null to indicate failure
        return null;
    }
}

// Deletes a URL from the blacklist by its unique ID
async function deleteUrlFromBlacklist(urlId) {
    // Find the index of the URL with the given ID
    const index = blacklistedUrls.findIndex(u => u.id === urlId);
    if (index === -1) {
        // URL ID not found in blacklist
        return -1;
    }

    const url = blacklistedUrls[index].url;
    // Request external server to delete the URL (DELETE)
    const result = await UrlChecker.checkUrlWithServer("DELETE", url.toLowerCase());
    if (result) {
        // Successfully deleted on server, remove locally
        blacklistedUrls.splice(index, 1);
        blacklistCounter--;
        return true;
    } else {
        // Server failed to delete the URL
        return -2;
    }
}
// Deletes a URL from the blacklist by its name (case-insensitive)
// Returns true if deleted, -1 if not found, -2 if server deletion failed
async function deleteUrlFromBlacklistByName(rawUrl) {
    const url = rawUrl.toLowerCase();
    const index = blacklistedUrls.findIndex(u => u.url === url);

    if (index !== -1) {
        // Local entry found — request remote deletion
        const result = await UrlChecker.checkUrlWithServer("DELETE", url);
        if (result) {
            blacklistedUrls.splice(index, 1);
            blacklistCounter = Math.max(0, (blacklistCounter || 0) - 1);
            return true;
        } else {
            return -2;
        }
    }

    // Local entry not found, attempt remote delete
    const remoteResult = await UrlChecker.checkUrlWithServer("DELETE", url);
    if (remoteResult) {
        // Remote deleted
        return true;
    } else {
        return -1;
    }
}

module.exports = { deleteUrlFromBlacklist, addUrlToBlacklist, deleteUrlFromBlacklistByName };
