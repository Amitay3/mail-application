import React from 'react';
import MailDropdown from './MailDropdown';
/* MailView component displays the details of a selected email */
function MailView({ mail, onBack, onMailUpdate, type, darkMode }) {
  return (
    <div className="col-9 mails-content">
        <div className='mails-container mail-view-container'>
            <div className="p-4">
                 {/*DROPDOWN at top‑right of the open mail*/}
                 <div
                   className="position-absolute top-0 end-0 m-3"
                   onClick={e => e.stopPropagation()}
                 >
                  <MailDropdown
                     emailId={mail.id}
                     mail={mail}
                     onMailUpdate={onMailUpdate}
                     type={type}
                     darkMode={darkMode}
                     onClose={onBack}
                   />
                 </div>
 
                <button className="back-btn" onClick={onBack}>
                  <img
                    src={darkMode ? "/icons8-back-30-gray.png" : "/icons8-back-30.png"}
                    alt="Spam Icon"
                    style={{ width: '20px', height: '20px', marginRight: '5px' }}
                  />
                </button>
                <h4>{mail.subject}</h4>
                <p><strong>From:</strong> {mail.sender}</p>
                <p><strong>To:</strong> {mail.recipient}</p>
                <p style={{ whiteSpace: 'pre-wrap' }}>{mail.content}</p>
                <div className='text-end text-muted'>
                  <small style={darkMode ? {color:'white'} : {color: 'gray'}}>
                    {new Date(mail.timestamp).toLocaleString([], {
                      month: 'short',
                      day: 'numeric',
                      hour: '2-digit',
                      minute: '2-digit',
                      hour12: false
                    })}
                  </small> 
                </div>
            </div>
        </div>
    </div>
  );
}

export default MailView;