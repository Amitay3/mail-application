import React, { useState } from 'react';
import EditLabel from '../Label/EditLabel';
import showToast from '../../utils/toast';

// Component for the menu that drops down when clicking on the side of a label
function LabelDropdown({ label, onLabelUpdate, onLabelDelete, darkMode }) {
  const [showEditLabel, setShowEditLabel] = useState(false);

  const handleEditLabelOpen = () => setShowEditLabel(true);
  const handleEditLabelClose = () => setShowEditLabel(false);

  const handleLabelEdited = () => {
    setShowEditLabel(false);
    onLabelUpdate();
  };
  // Calls backend to delete a specific label
  const handleDeleteLabel = async (e) => {
    e.stopPropagation();
    const token = localStorage.getItem('jwt');

    try {
      const response = await fetch(`http://localhost:8080/api/labels/${label.id}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        const errorText = await response.json();
        throw new Error(errorText.error || 'Failed to delete label');
      }

      showToast('Label deleted');
      onLabelDelete();
    } catch (error) {
      alert(error.message);
    }
  };

  return (
    <div className="dropdown" onClick={(e) => e.stopPropagation()}>
      <button className="btn btn-secondary dropdown-toggle plus-btn" type="button" id={`dropdown-${label.id}`} data-bs-toggle="dropdown" aria-expanded="false" onClick={(e) => e.stopPropagation()}>
        <img src={darkMode ? "/icons8-menu-30-gray.png" : "/icons8-menu-30.png"} alt="Options" style={{ width: '20px', height: '20px' }} />
      </button>

      <ul className="dropdown-menu text-centered" aria-labelledby={`dropdown-${label.id}`} style={{ width: '300px' }}>
        <li>
          <button className="dropdown-item d-flex align-items-center" onClick={handleDeleteLabel}>
            <img src="/icons8-delete-30.png" alt="Delete Icon" style={{ width: '20px', height: '20px', marginRight: '10px' }} />
            Delete label
          </button>
        </li>
        <li>
          <button className="dropdown-item d-flex align-items-center" onClick={handleEditLabelOpen}>
            <img src="/icons8-label-30 (1).png" alt="Label Icon" style={{ width: '20px', height: '20px', marginRight: '10px' }} />
            Edit label
          </button>
        </li>
      </ul>

      {showEditLabel && (
        <div className="label-modal-backdrop">
          <div className="label-modal-window">
            <EditLabel
              label={label}
              onCancel={handleEditLabelClose}
              onSend={handleLabelEdited}
            />
          </div>
        </div>
      )}
    </div>
  );
}

export default LabelDropdown;
