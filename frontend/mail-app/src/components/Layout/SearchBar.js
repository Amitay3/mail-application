import '../../App.css';
import React, { useState } from 'react';

// Search bar component
function SearchBar({ onSearchClick, darkMode }) {
  const [query, setQuery] = useState('');

  // Hnadles when a user clicks on search
  const handleSubmit = (e) => {
    e.preventDefault();
    onSearchClick(query);
  };
  // Render the search bar with input and button
  return (
    <div className="d-flex justify-content-center my-3" style={darkMode ? {backgroundColor: '#0e0e0e'} : {backgroundColor: 'inherit'}}>
      <form
        className="position-relative"
        style={{ width: '48%' }}
        onSubmit={(e) => {
          e.preventDefault();
          if (query.trim()) {
            handleSubmit(e);
          }
        }}

      >
        <button
          type="submit"
          className="search-btn"
        >
          <img
            src={darkMode ? "/icons8-search-30-gray.png" : "/icons8-search-35.png"}
            alt="Search Icon"
            className="search-icon"
          />
        </button>

        <input
          className="form-control ps-5 search-container"
          placeholder="Search mail"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          style={{ borderRadius: '20px' }}
        />
      </form>
    </div>
  );
}

export default SearchBar;
