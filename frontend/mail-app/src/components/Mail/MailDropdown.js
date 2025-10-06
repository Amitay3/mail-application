import React, { useState } from 'react';
import DeleteMail from '../Mail/DeleteMail';
import ReportMail from './ReportMail';
import RemoveFromSpam from './RemoveFromSpam'; 
import LabelMailDropdown from '../Label/LabelMailDropdown';
import showToast from '../../utils/toast';

// Component for displaying a dropdown menu for mail actions
function MailDropdown({ mail, emailId, onMailUpdate, onClose, type, darkMode }) {
  const [showLabelDropdown, setShowLabelDropdown] = useState(false);

    const handleLabelSelect = async (labelId) => {
    try {
      const token = localStorage.getItem('jwt');

      const response = await fetch('http://localhost:8080/api/labels/mail', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({
          mailId: emailId,
          labelId: labelId,
        }),
      });

      if (!response.ok) {
        const errorText = await response.json();
        throw new Error(errorText.error || 'Failed to label mail');
      }

      showToast('Mail labeled successfully');
      onMailUpdate();
      setShowLabelDropdown(false);
    } catch (error) {
      console.error('Error labeling mail:', error.message);
    }
  };

  async function handleDeleteMail() {
    await DeleteMail(emailId);
    if (onClose) onClose();
    onMailUpdate();
  }

  async function handleReportMail() {
    await ReportMail(emailId);
    if (onClose) onClose();
    onMailUpdate();
  }

  async function handleRemoveFromSpam() {
    await RemoveFromSpam(emailId); 
    if (onClose) onClose();
    onMailUpdate();
  }
  // Render the dropdown menu with options
  return (
    <div className="dropdown" onClick={(e) => e.stopPropagation()}>
      <button
        className="btn btn-secondary dropdown-toggle plus-btn"
        type="button"
        id={`dropdown-${emailId}`}
        data-bs-toggle="dropdown"
        aria-expanded="false"
        onClick={(e) => e.stopPropagation()}
      >
        <img
          src={darkMode ? "/icons8-menu-30-gray.png" : "/icons8-menu-30.png"}
          alt="Options"
          style={{ width: '20px', height: '20px' }}
        />
      </button>

      <ul className="dropdown-menu text-centered" aria-labelledby={`dropdown-${emailId}`} style={{ width: '300px' }}>
      {/* Label mail */}
      {!mail.isDraft && (type !== 'spam') && (
        <>
          <li>
            <button
              className="dropdown-item d-flex align-items-center"
              onClick={e => { e.stopPropagation(); setShowLabelDropdown(p => !p); }}
            >
              Label as…
            </button>
          </li>
          {showLabelDropdown && (
            <li>
              <div style={{ border: '1px solid #ccc', padding: '5px' }}>
                <LabelMailDropdown
                  mailId={emailId}
                  darkMode={darkMode}
                  onLabelSelect={handleLabelSelect}
                />
              </div>
            </li>
          )}
        </>
      )}
      {/* Report spam*/}
        {!mail.isDraft && (type === 'inbox' || type === 'search' || type === 'label' || type === 'sent') && (
          <li>
            <button
              className="dropdown-item d-flex align-items-center"
              onClick={(e) => {
                e.stopPropagation();
                handleReportMail();
              }}
            >
              <img src="/icons8-spam-30 (3).png" alt="Report Icon" style={{ width: '20px', height: '20px', marginRight: '10px' }} />
              Report spam
            </button>
          </li>
        )}
        {/* Remove from spam */}
        {type === 'spam' && (
          <li>
            <button
              className="dropdown-item d-flex align-items-center"
              onClick={(e) => {
                e.stopPropagation();
                handleRemoveFromSpam();
              }}
            >
              <img src="/icons8-inbox-30.png" alt="Inbox Icon" style={{ width: '20px', height: '20px', marginRight: '10px' }} />
              Remove from spam
            </button>
          </li>
        )}
        {/* Delete mail */}
        <li>
          <button
            className="dropdown-item d-flex align-items-center"
            onClick={(e) => {
              e.stopPropagation();
              handleDeleteMail();
            }}
          >
            <img src="/icons8-delete-30.png" alt="Delete Icon" style={{ width: '20px', height: '20px', marginRight: '10px' }} />
            {mail.isDraft ? 'Delete draft' : 'Delete mail'}
          </button>
        </li>
      </ul>
    </div>
  );
}

export default MailDropdown;
