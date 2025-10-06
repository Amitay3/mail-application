import React from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
import './App.css';

import MailboxView from './components/Mail/MailboxView';
import Login from './components/Auth/Login';
import Register from './components/Register/Register';
import ProtectedRoute from './components/Auth/ProtectedRoute';

import { BrowserRouter, Routes, Route } from 'react-router-dom';

function App() {
  
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/inbox/*" element={
          <ProtectedRoute>
            <MailboxView />
          </ProtectedRoute>
        } />
      </Routes>
    </BrowserRouter>
  );
}

export default App;

