import React, { useState } from 'react';
import showToast from '../../utils/toast';


function DeleteUrl({ onCancel, onSend }) {
  const [url, setUrl] = useState('');
  const token = localStorage.getItem('jwt');

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch(`http://localhost:8080/api/blacklist/delete-by-name/${encodeURIComponent(url)}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        }
      });

      if (!response.ok) {
        const errorText = await response.json();
        throw new Error(errorText.error || 'Failed to delete url.');
      }

      showToast('Link removed from future blacklisting');
      onSend();
    } catch (err) {
      alert(err.message);
    }
  };

  return (
    <div className="modal-content p-4">
      <h5>Remove a link from future blacklisting</h5>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          value={url}
          onChange={(e) => setUrl(e.target.value)}
          required
          className="form-control my-2"
          placeholder="Enter URL to remove"
        />
        <div className="d-flex justify-content-end">
          <button type="button" className="btn btn-secondary me-2" onClick={onCancel}>Cancel</button>
          <button type="submit" className="btn btn-primary">Remove</button>
        </div>
      </form>
    </div>
  );
}

export default DeleteUrl;
