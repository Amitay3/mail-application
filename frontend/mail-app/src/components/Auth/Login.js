import '../../App.css';
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import LoginForm from './LoginForm';
import showToast from '../../utils/toast';


// Main login page component
function Login() {
  // State for login fields
  const [mailAddress, setMailAddress] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  /**
   * Handles login by sending credentials to the backend.
   * If successul, stores the token and user info, and navigates to inbox.
   */
  async function handleLogin(mailAddress, password) {
    try {
      const response = await fetch('http://localhost:8080/api/tokens', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ mailAddress, password })
      });

      if (!response.ok) {
        throw new Error('Invalid credentials');
      }

      const data = await response.json();

      // Store token and user info in localStorage
      localStorage.setItem('jwt', data.token);
      localStorage.setItem('user', JSON.stringify(data.user));

      showToast('WELCOME'); 
      navigate('/inbox');   
    } catch (error) {
      alert(error.message); 
    }
  }

  return (
    <div className="d-flex justify-content-center align-items-center vh-100 bg-gray text-white">
      {/* Login card container */}
      <div className="card text-black rounded-4 p-4 shadow"
           style={{ width: '1000px', backgroundColor: 'white', height: '350px' }}>
        {/* Logo */}
        <img className="gmail-logo" src="/Gmail.webp" alt="Gmail Logo" />

        {/* Form layout: text on the left, fields on the right */}
        <div className="row">
          <div className="col-md-5 d-flex flex-column justify-content-center">
            <h1 className="mb-1">Sign in</h1>
            <h5 className="text-black">Use your ABA account</h5>
          </div>

          <div className="col-md-7">
            {/* Login form component */}
            <LoginForm
              mailAddress={mailAddress}
              setMailAddress={setMailAddress}
              password={password}
              setPassword={setPassword}
              onSubmit={handleLogin}
            />
          </div>
        </div>
      </div>
    </div>
  );
}

export default Login;
