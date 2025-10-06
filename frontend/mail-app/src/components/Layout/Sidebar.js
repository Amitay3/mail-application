import '../../App.css';
import React, { useState, useRef, useEffect } from 'react';
import LabelDropdown from '../Label/LabelDropdown';
import { useParams } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';


function Sidebar({ onComposeClick, onPlusClick, onInboxClick, onSentClick, onDraftsClick, onSpamClick, onLabelClick, refreshKey, onLabelRefresh, darkMode, type }) {
  const { labelId } = useParams();
  const [labels, setLabels] = useState([]);
  const hasFetched = useRef(false);
  const navigate = useNavigate();

  useEffect(() => {
    fetchLabels();
  }, [refreshKey]);

  // Function to fetch all labels from backend API
  const fetchLabels = async () => {
    try {
      const token = localStorage.getItem('jwt');
      const response = await fetch('http://localhost:8080/api/labels', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        const errText = await response.text();
        throw new Error(`Failed to fetch labels: ${errText}`);
      }

      const data = await response.json();
      setLabels(data.reverse());
      hasFetched.current = true;
    } catch (error) {
      alert(error.message);
    }
  };


  // In case labels have not been fetched yet, fetch them immediately
  if (!hasFetched.current) {
    fetchLabels();
  }

  // Called when a label is deleted:
  // Navigate back to inbox view, then refresh label list after a short delay
  const handleLabelDelete = () => {
    navigate('/inbox');
    setTimeout(() => {
      onLabelRefresh?.();
    }, 100);
  }

  return (
    <div className="col-3 sidebar-menu" >
      {/* Compose button */}
      <button type="button" className="btn btn-primary newmail-btn mb-3" onClick={onComposeClick}>
        <img src="/icons8-pencil-40.png" alt="Compose Icon" style={{ width: '20px', height: '20px', marginRight: '8px', verticalAlign: 'middle' }} />
        Compose
      </button>
      <div className="list-group">
        {/* Inbox link */}
          <a href="#" className={`sidebar-item ${type === 'inbox' ? 'active' : ''}`} onClick={onInboxClick}>
          <img
            src={darkMode ? "/icons8-inbox-30-gray.png" : "/icons8-inbox-30.png"}
            alt="Inbox Icon"
            style={{ width: '20px', height: '20px', marginRight: '8px' }}
          />
          Inbox
        </a>

        {/* Sent link */}
          <a href="#" className={`sidebar-item ${type === 'sent' ? 'active' : ''}`} onClick={onSentClick}>
          <img
            src={darkMode ? "/icons8-sent-30 -gray.png" : "/icons8-sent-30.png"}
            alt="Sent Icon"
            style={{ width: '20px', height: '20px', marginRight: '8px' }}
          />
          Sent
        </a>

        {/* Drafts link */}
          <a href="#" className={`sidebar-item ${type === 'drafts' ? 'active' : ''}`} onClick={onDraftsClick}>
          <img
            src={darkMode ? "/icons8-draft-30-gray.png" : "/icons8-draft-30 (1).png"}
            alt="Drafts Icon"
            style={{ width: '20px', height: '20px', marginRight: '8px' }}
          />
          Drafts
        </a>

        {/* Spam link */}
          <a href="#" className={`sidebar-item ${type === 'spam' ? 'active' : ''}`} onClick={onSpamClick}>
          <div className="d-flex align-items-center">
            <img
              src={darkMode ? "/icons8-spam-30-gray.png" : "/icons8-spam-30 (3).png"}
              alt="Spam Icon"
              style={{ width: '20px', height: '20px', marginRight: '5px' }}
            />
            <span>Spam</span>
          </div>
        </a>

        {/* Labels header with add (+) button */}
        <div className="list-group-item d-flex justify-content-between align-items-center">
          <div className="d-flex align-items-center">
            <img
              src={darkMode ? "/icons8-labels-30-gray.png" : "/icons8-labels-35.png"}
              alt="Labels Icon"
              style={{ width: '20px', height: '20px', marginRight: '8px' }}
            />
            <span style={{ fontWeight: 'bold', fontSize: '1.25rem' }}>Labels</span>
          </div>

          <button onClick={onPlusClick} className='plus-btn'>
            <img src={darkMode ? "/icons8-plus-24-gray.png" : "/icons8-plus-24.png"} alt="Plus Icon" style={{ width: '20px', height: '20px' }} />
          </button>
        </div>

        {/* Render each label with a clickable link and dropdown menu */}
        {labels.map((label, i) => (
          <a
            key={i}
            href="#"
            className={`sidebar-item d-flex justify-content-between align-items-center ${type === 'label' && label.id === labelId ? 'active' : ''
              }`}
            onClick={(e) => {
              e.preventDefault();
              onLabelClick?.(label.id);
            }}
          >
            <div className="d-flex align-items-center">
              <img
                src={darkMode ? "/icons8-label-30 (3)-gray.png" : "/icons8-label-30.png"}
                alt="Label Icon"
                style={{ width: '20px', height: '20px', marginRight: '8px' }}
              />
              {label.labelName}
            </div>
            {/* Label dropdown for edit/delete actions */}
            <LabelDropdown label={label} onLabelUpdate={onLabelRefresh} onLabelDelete={handleLabelDelete} darkMode={darkMode} />
          </a>
        ))}
      </div>
    </div>
  );
}


export default Sidebar;
