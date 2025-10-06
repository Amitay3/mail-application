import React from 'react';

// Component for a specific field in the login form
function LoginField({ type, id, placeholder, value, onChange }) {
  return (
    <div className="mb-3 text-start">
      <label htmlFor={id} className="form-label"></label>
      <input
        type={type}
        className="form-control"
        id={id}
        placeholder={placeholder}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        required
        style={{ borderRadius: '20px', height: '50px' }}
      />
    </div>
  );
}

export default LoginField;
