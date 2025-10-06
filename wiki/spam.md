# Spam and Blacklist Filtering System

Our app includes a **blacklist filtering system** to protect against malicious URLs.  

### How It Works

1. **Marking an Email as Spam**  
   - When you move an email to your **Spam** folder, every URL in that email (from the subject or body) is automatically **added to the blacklist**.  

2. **Automatic Filtering**  
   - Any future email containing a blacklisted URL will be automatically moved to your **Spam** folder.  

3. **Removing from Spam**  
   - If you remove an email from the **Spam** folder, all URLs in that email are **removed from the blacklist**, so future emails containing those URLs will no longer be filtered automatically.
