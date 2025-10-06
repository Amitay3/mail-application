import React, { useEffect, useState } from 'react';
import showToast from '../../utils/toast';

// Component for labeling a specific mail 
function LabelMailDropdown({ mailId, darkMode, onLabelSelect }) {
  const [labels, setLabels] = useState([]);
  const [mailLabelIds, setMailLabelIds] = useState([]);

  const token = localStorage.getItem('jwt');

  // Fetch all labels from the backend
  const fetchAllLabels = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/labels', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });
      if (!response.ok) throw new Error('Failed to fetch all labels');
      const data = await response.json();
      setLabels(data.reverse());
    } catch (err) {
      console.error(err.message);
    }
  };

  // Fetch the labels assigned to a specific mail
  const fetchLabelsForMail = async () => {
    try {
      const response = await fetch(`http://localhost:8080/api/labels/mail/${mailId}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });
      if (!response.ok) throw new Error('Failed to fetch mail labels');
      const data = await response.json();
      const ids = data.map(label => label.id);
      setMailLabelIds(ids);
    } catch (err) {
      console.error(err.message);
    }
  };

  // Toggle a label on or off for this mail
  const toggleLabel = async (labelId) => {
    try {
      const isLabeled = mailLabelIds.includes(labelId);

      const url = isLabeled
        ? `http://localhost:8080/api/labels/mail/${mailId}/${labelId}`
        : `http://localhost:8080/api/labels/mail`;

      const method = isLabeled ? 'DELETE' : 'POST';
      const headers = {
        'Authorization': `Bearer ${token}`,
        ...(method === 'POST' && { 'Content-Type': 'application/json' }),
      };

      const fetchOptions = method === 'POST'
        ? {
          method,
          headers,
          body: JSON.stringify({ mailId, labelId }),
        }
        : {
          method,
          headers,
        };

      const response = await fetch(url, fetchOptions);
      if (!response.ok) throw new Error(`Failed to ${isLabeled ? 'remove' : 'add'} label`);

      // Update UI state
      setMailLabelIds(prev =>
        isLabeled ? prev.filter(id => id !== labelId) : [...prev, labelId]
      );
      showToast(`Label ${isLabeled ? 'removed' : 'added'}`);
    } catch (err) {
      console.error(err.message);
    }
  };

  // Fetch data when mailId changes
  useEffect(() => {
    fetchAllLabels();
    fetchLabelsForMail();
  }, [mailId]);


  return (
    <>
      {labels.map((label) => {
        const isSelected = mailLabelIds.includes(label.id);
        return (
          <button
            key={label.id}
            className="dropdown-item d-flex align-items-center"
            onClick={(e) => {
              e.preventDefault();
              e.stopPropagation();
              toggleLabel(label.id);
            }}
          >
            <div className="d-flex align-items-center">
              <img
                src={
                  isSelected
                    ? "/icons8-checked-box.png"
                    : "/icons8-unchecked-box.png"
                }
                alt="Checkbox"
                style={{ width: '20px', height: '20px', marginRight: '8px' }}
              />
              {label.labelName}
            </div>
          </button>
        );
      })}
    </>
  );
}

export default LabelMailDropdown;
