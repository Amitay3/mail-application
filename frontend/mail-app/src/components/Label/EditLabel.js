import React, { useState, useEffect } from 'react';
import showToast from '../../utils/toast';

// Component for editing an existing label
function EditLabel({ label, onCancel, onSend }) {
  let nameLabel = label.labelName;
  const [labelName, setLabelName] = useState('');
  const token = localStorage.getItem('jwt');

  // Pre-fill the input with the current label name when the component mounts or label prop changes
  useEffect(() => {
    if (label?.labelName) {
      setLabelName(label.labelName);
    }
  }, [label]);

  // Handle form submission to send a PATCH request to update the label
  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch(`http://localhost:8080/api/labels/${label.id}`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({ labelName })
      });

      if (!response.ok) {
        const errorText = await response.json();
        throw new Error(errorText.error || 'Failed to edit label');
      }

      showToast('Label updated');
      onSend();
    } catch (err) {
      alert(err.message);
    }
  };

  return (
    <div className="container mt-4 force-light-inputs">
      <h5 style={{color: 'black'}}>Edit Label</h5>
      <form onSubmit={handleSubmit}>
        {/* Input field for label name */}
        <input
          type="text"
          value={labelName}
          onChange={(e) => setLabelName(e.target.value)}
          placeholder={nameLabel}
          required
          className="form-control my-2"
        />
        <div className="d-flex justify-content-end">
          {/* Buttons for canceling or saving the changes */}
          <button type="button" className="btn btn-secondary me-2" style={{border: 'none'}} onClick={onCancel}>Cancel</button>
          <button type="submit" className="btn btn-primary"style={{border: 'none'}}>Save</button>
        </div>
      </form>
    </div>
  );
}

export default EditLabel;
