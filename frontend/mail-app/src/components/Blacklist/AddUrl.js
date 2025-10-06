import React, { useState, useEffect } from 'react';
import showToast from '../../utils/toast';

// Component for adding a URL to the blacklist
function AddUrl({ onCancel, onSend }) {
  const [url, setUrl] = useState('');
  const token = localStorage.getItem('jwt');

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch(`http://localhost:8080/api/blacklist`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({ url })
      });

      if (!response.ok) {
        const errorText = await response.json();
        throw new Error(errorText.error || 'Failed to report url.');
      }

      showToast('Link reported succefully');
      onSend();
    } catch (err) {
      alert(err.message);
    }
  };

  return (
    <div className="modal-content p-4">
      <h5>Report a malicious link</h5>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          value={url}
          onChange={(e) => setUrl(e.target.value)}
          required
          className="form-control my-2"
        />
        <div className="d-flex justify-content-end">
          <button type="button" className="btn btn-secondary me-2" onClick={onCancel}>Cancel</button>
          <button type="submit" className="btn btn-primary">Report</button>
        </div>
      </form>
    </div>
  );
}

export default AddUrl;
