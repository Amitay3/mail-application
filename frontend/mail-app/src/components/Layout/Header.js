import { useNavigate } from 'react-router-dom';
import { useState, useRef, useEffect } from 'react';
import '../../App.css';

// Header component
function Header({ user, onBack, darkMode, toggleDarkMode }) {
  // State for showing/hiding the profile modal
  const [showProfile, setShowProfile] = useState(false);
  const navigate = useNavigate();

  // Ref to detect clicks outside the profile modal
  const modalRef = useRef();

  // Open and close profile modal handlers
  const handleProfileOpen = () => setShowProfile(true);
  const handleProfileClose = () => setShowProfile(false);

  // Handle click outside the profile modal to close it
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (modalRef.current && !modalRef.current.contains(e.target)) {
        handleProfileClose();
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  // Clears local storage and redirects to login on logout
  const handleLogout = () => {
    localStorage.removeItem('jwt');
    localStorage.removeItem('user');
    navigate('/');
  };

  return (
    <>
      {/* Banner with back button and app logo */}
      <div className="gmail-banner">
        <button className="home-btn" onClick={onBack}>
          <img className="gmail-logo" src="/ABA.webp" alt="Gmail Logo" />
          <span className="gmail-text"> Mail</span>
        </button>
      </div>

      {/* Dark mode toggle switch */}
      <div className="d-flex justify-content-end align-items-center position-absolute top-0" style={{ right: '90px', marginTop: '30px' }}>
        <div className="form-check form-switch">
          <input
            className="form-check-input"
            type="checkbox"
            id="darkModeToggle"
            checked={darkMode}
            onChange={toggleDarkMode}
          />
          <label className="form-check-label" htmlFor="darkModeToggle">
            Dark Mode
          </label>
        </div>
      </div>

      {/* Profile picture button to open modal */}
      <button className="logoff-btn" onClick={handleProfileOpen}>
        <img
          className="profile-pic"
          src={user.image === 'default' ? '/default-pic.jpg' : user.image}
          alt="Profile"
        />
      </button>

      {/* Profile modal */}
      {showProfile && (
        <div className="profile-modal-backdrop">
          <div className="profile-modal-window" ref={modalRef}>
            <div className="profile-email">{user.mailAddress}</div>
            <img
              className="profile-pic-modal"
              src={user.image === 'default' ? '/default-pic.jpg' : user.image}
              alt="Profile"
            />
            <div className="profile-greeting">Hi, {user.userName}!</div>
            <button className="signout-btn" style={{ border: 'none' }} onClick={handleLogout}>
              <img
                src={darkMode ? "icons8-sign-out-30-gray.png" : "/icons8-sign-out-30.png"}
                alt="Sign Out Icon"
                style={{ width: '20px', height: '20px', marginRight: '8px' }}
              />
              Sign Out
            </button>
          </div>
        </div>
      )}
    </>
  );
}

export default Header;
