import React from 'react';
import MailDropdown from './MailDropdown';

// Function to determine if the mail was created more than 24 hours ago
// Displays the time accorsdingly
function formatMailTimestamp(timestamp) {
  const now = new Date();
  const mailTime = new Date(timestamp);
  const diffInMs = now - mailTime;
  const diffInHours = diffInMs / (1000 * 60 * 60);

  if (diffInHours > 24) {
    // Show date only
    return mailTime.toLocaleDateString([], {
      month: 'short',
      day: 'numeric',
      year: 'numeric'
    });
  } else {
    // Show time only
    return mailTime.toLocaleTimeString([], {
      hour: '2-digit',
      minute: '2-digit',
      hour12: false
    });
  }
}


function MailItem({ email, onMailClick, onMailUpdate, type, darkMode }) {
  return (
    <div
      className="list-group-item list-group-item-action d-flex justify-content-between align-items-center"
      onClick={() => onMailClick(email)}
      style={{cursor: 'pointer' }}
    >
      <div>
        <span className="email-sender fw-bold">
          {type === 'sent'
            ? `To: ${email.recipient}`
            : email.sender
          }
        </span>
        <span className="email-subject fw-bold ms-4">{email.subject}</span>
      </div>

      {/*Group timestamp and dropdown together*/}
          <div className="d-flex align-items-center gap-2">
              <small style={darkMode ? { color: 'white' } : { color: 'gray' }}>
                  {formatMailTimestamp(email.timestamp)}
              </small>  
              <MailDropdown mail={email} emailId={email.id} onMailUpdate={onMailUpdate} type={type} darkMode={darkMode} />
          </div>
    </div>
  );
}

export default MailItem;
