import React from 'react';
import FormInput from './FormInput';
import FileInput from './FileInput';
// Component for the registration form
function RegisterForm({ userName, setUserName, password, setPassword, verifiedPassword, setVerifiedPassword, mailAddress, setMailAddress, setImg, onSubmit }) {
  const [showPassTooltip, setShowPassTooltip] = React.useState(false);
  const [showMailTooltip, setShowMailTooltip] = React.useState(false);


  return (
    <form onSubmit={onSubmit}>
      <FormInput
        type="text"
        id="user"
        placeholder="Your name"
        value={userName}
        onChange={(e) => setUserName(e.target.value)}
      />
      {/* Password input with info button to the left */}
        <div className="position-relative me-2">
          <button
            type="button"
            className="btn btn-light p-1 border-0"
            onMouseEnter={() => setShowPassTooltip(true)}
            onMouseLeave={() => setShowPassTooltip(false)}
            style={{backgroundColor: 'inherit'}}
          >
            <img src="/icons8-info-48.png" alt="info" style={{ width: 20, height: 20 }} />
          </button>

          {showPassTooltip && (
            <div
              className="position-absolute bg-light border p-2 rounded shadow"
              style={{
                top: '100%',
                left: 0,
                whiteSpace: 'nowrap',
                zIndex: 10,
                fontSize: '0.875rem',
              }}
            >
              Password must:
              <ul className="mb-0 ps-3">
                <li>Be at least 8 characters</li>
                <li>Contain a number</li>
                <li>Contain a capital letter</li>
              </ul>
            </div>
          )}
        </div>

        <div className="flex-grow-1">
          <FormInput
            type="password"
            id="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>

      <FormInput
        type="password"
        id="verifiedPassword"
        placeholder="Verify password"
        value={verifiedPassword}
        onChange={(e) => setVerifiedPassword(e.target.value)}
      />

      {/* Mail input with info button to the left */}
        <div className="position-relative me-2">
          <button
            type="button"
            className="btn btn-light p-1 border-0"
            onMouseEnter={() => setShowMailTooltip(true)}
            onMouseLeave={() => setShowMailTooltip(false)}
            style={{backgroundColor: 'inherit'}}
          >
            <img src="/icons8-info-48.png" alt="info" style={{ width: 20, height: 20 }} />
          </button>

          {showMailTooltip && (
            <div
              className="position-absolute bg-light border p-2 rounded shadow"
              style={{
                top: '100%',
                left: 0,
                whiteSpace: 'nowrap',
                zIndex: 10,
                fontSize: '0.875rem',
              }}
            >
            Mail address must end with @abamail.com
            </div>
          )}
        </div>
      <FormInput
        type="email"
        id="email"
        placeholder="Email address"
        value={mailAddress}
        onChange={(e) => setMailAddress(e.target.value)}
      />
      <FileInput id="image" onChange={(e) => setImg(e.target.files[0])} />

      <div className="d-flex gap-4">
        <button type="submit" className="btn btn-primary next-btn">Submit</button>
      </div>
    </form>
  );
}

export default RegisterForm;
