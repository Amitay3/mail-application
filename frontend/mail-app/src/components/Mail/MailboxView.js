import React, { useState, useRef, useEffect } from 'react';
import { Routes, Route, useNavigate, useLocation } from 'react-router-dom';
import ComposeMail from './Compose/Compose';
import Sidebar from '../Layout/Sidebar';
import MailList from './MailList';
import SearchBar from '../Layout/SearchBar';
import Header from '../Layout/Header';
import MailView from './MailView';
import CreateLabel from '../Label/CreateLabel';

function MailboxView() {
  // Get the current logged-in user info from localStorage
  const user = JSON.parse(localStorage.getItem('user'));
  
  // State variables for UI control and data
  const [showCompose, setShowCompose] = useState(false);
  const [selectedMail, setSelectedMail] = useState(null);
  const [refresheKey, setRefreshKey] = useState(0);
  const [showLabel, setShowLabel] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [draftToEdit, setDraftToEdit] = useState(null);
  const [darkMode, setDarkMode] = useState(false);

  const navigate = useNavigate();

  const handleComposeOpen = () => setShowCompose(true);
  const handleComposeClose = () => setShowCompose(false);
  const handleLabelOpen = () => setShowLabel(true);
  const handleLabelClose = () => setShowLabel(false);
  const handleInboxOpen = () => navigate('/inbox');
  const handleSentOpen = () => navigate('/inbox/sent');
  const handleDraftsOpen = () => navigate('/inbox/drafts');
  const handleSpamOpen = () => navigate('/inbox/spam');
  const handleMailLabel = (labelId) => navigate(`/inbox/labels/${labelId}`);
  const toggleDarkMode = () => setDarkMode(d => !d);

  const composeRef = useRef(null);

  const location = useLocation();

  // Determine current mailbox view based on URL path
  const currentType = (() => {
    const path = location.pathname;
    if (path === '/inbox') return 'inbox';
    if (path === '/inbox/sent') return 'sent';
    if (path === '/inbox/drafts') return 'drafts';
    if (path === '/inbox/spam') return 'spam';
    if (path.startsWith('/inbox/labels')) return 'label';
    if (path.startsWith('/inbox/search')) return 'search';
    return '';
  })();

  // Close compose modal when clicking outside of it
  useEffect(() => {
    const handleOutsideClick = (e) => {
      if (showCompose && composeRef.current && !composeRef.current.contains(e.target)) {
        handleComposeClose();
      }
    };

    document.addEventListener('mousedown', handleOutsideClick);
    return () => {
      document.removeEventListener('mousedown', handleOutsideClick);
    };
  }, [showCompose]);

  // Toggle dark mode CSS classes on body and html elements
  useEffect(() => {
    if (darkMode) {
      document.body.classList.add('dark-mode');
      document.documentElement.classList.add('dark-mode');
    } else {
      document.body.classList.remove('dark-mode');
      document.documentElement.classList.remove('dark-mode');
    }
  }, [darkMode]);

  // Clear selected mail when the route changes
  useEffect(() => {
  setSelectedMail(null);
  }, [location.pathname]);


  // Handle search, setting query and navigating to search results
  const handleSearch = (query) => {
    setSearchQuery(query);
    navigate('/inbox/search');
  };

  // After sending mail, close compose and refresh mailbox
  const handleSendMail = () => {
    setShowCompose(false);
    setRefreshKey(v => v + 1)
  };

  // When a mail is clicked in the list, set it as selected to view
  const handleMailClick = (mail) => {
    setSelectedMail(mail);
  };

  // Back button handler to return from mail view to list view
  const handleBackToList = () => {
    setSelectedMail(null);
  };

  // After creating a label, close label modal and refresh labels
  const handleCreateLabel = () => {
    setShowLabel(false);
    setRefreshKey(v => v + 1);
  };

  // When clicking on a draft mail, open compose modal to edit draft
  const handleDraftMailClick = (mail) => {
    setSelectedMail(null);
    setDraftToEdit(mail);
    setShowCompose(true);
    setRefreshKey(v => v + 1)
  };

  // Refresh mailbox data on mail updates (like delete, move)
  const handleMailUpdate = () => setRefreshKey((v) => v + 1);

  return (
    <div className={`app-wrapper ${darkMode ? 'dark-mode' : ''}`}>
      {/* Header component with back button, user info, and dark mode toggle */}
      <Header user={user} onBack={selectedMail ? handleBackToList : null} darkMode={darkMode} toggleDarkMode={toggleDarkMode} />
      {/* Main content container */}
      <div className="container-fluid" style={darkMode ? {backgroundColor: '#0e0e0e'} : {backgroundColor:'inherit'}}>
        {/* Search bar */}
        <SearchBar onSearchClick={handleSearch} darkMode={darkMode} />
        <div className="row" style={darkMode ? {backgroundColor: '#0e0e0e'} : {color:'inherit'}}>
           {/* Sidebar with mailbox navigation */}
          <Sidebar
            onComposeClick={handleComposeOpen}
            onPlusClick={handleLabelOpen}
            onInboxClick={handleInboxOpen}
            onSentClick={handleSentOpen}
            onDraftsClick={handleDraftsOpen}
            onSpamClick={handleSpamOpen}
            onLabelClick={handleMailLabel}
            refreshKey={refresheKey}
            onLabelRefresh={() => setRefreshKey(k => k + 1)}
            darkMode={darkMode}
            type={currentType}
          />
          {/* Main mail list or mail view area */}
          <div className="col-9 position-relative" style={darkMode ? { backgroundColor: '#0e0e0e' } : { backgroundColor: 'inherit' }}>
            {!selectedMail ? (
              <Routes>
                <Route path="/" element={<MailList onMailClick={handleMailClick} refreshKey={refresheKey} type="inbox" query={searchQuery} onMailUpdate={handleMailUpdate} darkMode={darkMode} />} />
                <Route path="sent" element={<MailList onMailClick={handleMailClick} refreshKey={refresheKey} type="sent" query={searchQuery} onMailUpdate={handleMailUpdate} darkMode={darkMode} />} />
                <Route path="drafts" element={<MailList onMailClick={handleDraftMailClick} refreshKey={refresheKey} type="drafts" query={searchQuery} onMailUpdate={handleMailUpdate} darkMode={darkMode} />} />
                <Route path="spam" element={<MailList onMailClick={handleMailClick} refreshKey={refresheKey} type="spam" query={searchQuery} onMailUpdate={handleMailUpdate} darkMode={darkMode} />} />
                <Route path="labels/:labelId" element={<MailList onMailClick={handleMailClick} refreshKey={refresheKey} type="label" query={searchQuery} onMailUpdate={handleMailUpdate} darkMode={darkMode} />} />
                <Route path="search" element={<MailList onMailClick={handleMailClick} refreshKey={refresheKey} type="search" query={searchQuery} onMailUpdate={handleMailUpdate} darkMode={darkMode} />} />
              </Routes>
            ) : (
              <div className="position-absolute top-0 start-0 w-100" style={{ zIndex: 1050 }}><MailView
                mail={selectedMail}
                onBack={handleBackToList}
                darkMode={darkMode}
                type={currentType}
                onMailUpdate={handleMailUpdate}
                />
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Compose modal */}
      {showCompose && (
        <div className="compose-modal-backdrop">
          <div className="compose-modal-window" ref={composeRef}>
            <div className="compose-header" style={{ color: 'black' }}>
              <h5 className="mb-0">New Message</h5>
            </div>
            <ComposeMail
              onCancel={() => {
                setDraftToEdit(null);
                handleComposeClose();
                setRefreshKey(k => k + 1);
              }}
              onSend={handleSendMail}
              draft={draftToEdit}
              darkMode={darkMode}
            />
          </div>
        </div>
      )}

      {/* Label modal */}
      {showLabel && (
        <div className="label-modal-backdrop">
          <div className="label-modal-window">
            <CreateLabel onCancel={handleLabelClose} onSend={handleCreateLabel} refreshKey={refresheKey} darkMode={darkMode} />
          </div>
        </div>
      )}
    </div>
  );
}

export default MailboxView;
