import React, { useState } from 'react';

function FileInput({ id, onChange }) {
  const [fileName, setFileName] = useState('No file chosen');

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    setFileName(file ? file.name : 'No file chosen');
    onChange(e);
  };

  return (
    // "Choose profile picture" button to upload an image
    <div className="mb-3 text-start d-flex align-items-center gap-3">
      <label htmlFor={id} className="btn btn-secondary m-0">
        Choose profile picture
      </label>
      <span>{fileName}</span>
      <input
        type="file"
        name="image"
        id={id}
        accept="image/*"
        onChange={handleFileChange}
        style={{ display: 'none' }}
      />
    </div>
  );
}

export default FileInput;