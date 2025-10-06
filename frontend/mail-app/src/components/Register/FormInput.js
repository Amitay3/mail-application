import React from 'react';

function FormInput({ type, id, placeholder, value, onChange }) {
  return (
    <div className="mb-3 text-start">
      <input
        type={type}
        className="form-control"
        id={id}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        required
        style={{ borderRadius: '20px', height: '50px' }}
      />
    </div>
  );
}

export default FormInput;
