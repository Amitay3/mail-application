import '../../App.css';
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import RegisterForm from './RegisterForm';
import showToast from '../../utils/toast';
// Main registration page component
function Register() {
  const [userName, setUserName] = useState('');
  const [password, setPassword] = useState('');
  const [verifiedPassword, setVerifiedPassword] = useState('');
  const [mailAddress, setMailAddress] = useState('');
  const [img, setImg] = useState('');
  const navigate = useNavigate();

  async function handleRegister(userName, password, verifiedPassword, mailAddress, imageFile) {
    const formData = new FormData();
    formData.append('userName', userName);
    formData.append('password', password);
    formData.append('verifiedPassword', verifiedPassword);
    formData.append('mailAddress', mailAddress);
    formData.append('image', imageFile);

    try {
      const response = await fetch('http://localhost:8080/api/users', {
        method: 'POST',
        body: formData,
      });

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.error || 'Registration failed');
      }

      showToast('User registered successfully!');
      navigate('/');
    } catch (error) {
      alert(error.message);
    }
  }
  // Render the registration form with fields and submit handler
  return (
    <div className="d-flex justify-content-center align-items-center vh-100 bg-gray text-white">
      <div className="card text-black rounded-4 p-4 shadow" style={{ width: '1000px', backgroundColor: 'white', height: '550px' }}>
        <img className="gmail-logo" src="/Gmail.webp" alt="Gmail Logo" />
        <div className="row">
          <div className="col-md-5 d-flex flex-column justify-content-center">
            <h1 className="mb-1">Create an account</h1>
            <h5 className="text-black">Enter your credentials</h5>
          </div>
          <div className="col-md-7">
            <RegisterForm
              userName={userName}
              setUserName={setUserName}
              password={password}
              setPassword={setPassword}
              verifiedPassword={verifiedPassword}
              setVerifiedPassword={setVerifiedPassword}
              mailAddress={mailAddress}
              setMailAddress={setMailAddress}
              img={img}
              setImg={setImg}
              onSubmit={(e) => {
                e.preventDefault();
                handleRegister(userName, password, verifiedPassword, mailAddress, img);
              }}
            />
          </div>
        </div>
      </div>
    </div>
  );
}

export default Register;
