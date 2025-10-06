import React, { useState } from 'react';
import showToast from '../../utils/toast';

/**
 * Component for creating a new label.
 *
 * Props:
 * - onCancel: function to call when cancel is clicked
 * - onSend: function to call after successful label creation
 * - refreshKey: not used here, but may be for triggering parent re-fetch
 */
function CreateLabel({ onCancel, onSend,  refreshKey}) {
    const [label, setLabel] = useState('');

    // Calls backend to add a new label
    async function handleSend(e) {
      e.preventDefault();

      const token = localStorage.getItem('jwt');

      try {
        const response = await fetch('http://localhost:8080/api/labels', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify({
            labelName: label
          })
        });

        if (!response.ok) {
          const errorText = await response.json();
          throw new Error(errorText.error || 'Failed to send mail');
        }

        showToast('Label created!');
        onSend?.();

      } catch (error) {
        alert(error.message);
      }
    }

    // Handles cancel button click
    async function handleCancel(e) {
        onCancel?.();
    }


    return (
    <div className="container mt-4 force-light-inputs" style={{ maxWidth: '800px' }}>
      <h2 style={{color: 'black'}}>New Label</h2>
      <form onSubmit={handleSend}>
        <div className="mb-3">
          <label className="form-label"></label>
          <input
            type="label"
            id="label"
            className="form-control"
            placeholder="Label name"
            value={label}
            setLabel={setLabel}
            onChange={e => setLabel(e.target.value)}
            required
            style={{ borderRadius: '20px', height: '50px' }}
          />
        </div>
        <div className="d-flex gap-3">
          {/* Buttons for canceling or creating the changes */}
          <button type="submit" className="btn btn-primary px-4" style={{border: 'none'}}>Create</button>
          <button type="button" className="btn btn-secondary px-4" onClick={handleCancel}>Cancel</button>
        </div>
      </form>
    </div>
  );
}

export default CreateLabel;