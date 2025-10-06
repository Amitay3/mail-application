import React from 'react';
import LoginField from './LoginField';
import { useNavigate } from 'react-router-dom';

// Renders the login form with email and password fields, and handles navigation and form submission.
function LoginForm({ mailAddress, setMailAddress, password, setPassword, onSubmit }) {
  const navigate = useNavigate();

  return (
    <form>
      {/* Email input field */}
      <LoginField
        type="email"
        id="username"
        placeholder="Mail address"
        value={mailAddress}
        onChange={setMailAddress}
      />

      {/* Password input field */}
      <LoginField
        type="password"
        id="password"
        placeholder="Password"
        value={password}
        onChange={setPassword}
      />

      {/* Button group: login + create account */}
      <div className="d-flex gap-2">
        {/* Submit button: calls onSubmit with entered credentials */}
        <button
          type="submit"
          className="btn btn-primary next-btn"
          onClick={(e) => {
            e.preventDefault(); 
            onSubmit(mailAddress, password); 
          }}
        >
          Sign-in
        </button>

        {/* Navigate to register page */}
        <button
          type="button"
          className="btn btn-outline-primary create-account-btn"
          onClick={() => navigate('/register')}
        >
          Create account
        </button>
      </div>
    </form>
  );
}

export default LoginForm;
