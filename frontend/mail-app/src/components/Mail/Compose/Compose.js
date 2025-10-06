import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import showToast from '../../../utils/toast';

// Component for composing a mail
function ComposeMail({ onCancel, onSend, draft }) {
    const [to, setTo] = useState('');
    const [subject, setSubject] = useState('');
    const [body, setBody] = useState('');
    const navigate = useNavigate();

    // If the mail was already a draft, prefill its fields
    useEffect(() => {
      if (draft) {
        setTo(draft.recipient || '');
        setSubject(draft.subject || '');
        setBody(draft.content || '');
      }
    }, [draft]);

    // Function to send a POST request to backend, creating a mail
    async function handleSend(e) {
      e.preventDefault(); 

      const token = localStorage.getItem('jwt');

      try {
        let url = 'http://localhost:8080/api/mails';
        let method = '';
        if (draft) {
          url += `/${draft.id}`;          
          method = 'PATCH';
        } else {
          method = 'POST';
        }

        const response = await fetch(url, {
          method: method,
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify({
            recipient: to,
            subject: subject,
            content: body,
            isDraft: false
          })
        });

        if (!response.ok) {
          const errorText = await response.json();
          throw new Error(errorText.error || 'Failed to send mail');
        }

        showToast('Sent successfully');
        onSend?.();
        navigate('/inbox');

      } catch (error) {
        alert(error.message);
      }
    }

  // Function to handle when a user cancels a mail
  async function handleCancel(e) {
    e.preventDefault();

    const token = localStorage.getItem('jwt');
    try {
        let url = 'http://localhost:8080/api/mails';
        let method = '';
        if (draft) {
          url += `/${draft.id}`;
          method = 'PATCH';
        } else {
          method = 'POST';
        }

        const response = await fetch(url, {
          method: method,
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify({
            recipient: to,
            subject: subject,
            content: body,
            isDraft: true
          })
        });

        if (!response.ok) {
          const errorText = await response.json();
          throw new Error(errorText.error || 'Failed to send mail');
        }
        onCancel?.();
        showToast('Saved as draft');
      } catch (error) {
        if (error.message !== "Draft not created") {
          alert(error.message);
        }
      }
    onCancel?.();
  }
    
    return (
    <div className="container mt-4 force-light-inputs" style={{ maxWidth: '800px' }}>
      <form onSubmit={handleSend}>
        <div className="mb-3">
          {/* Recipient input */}
          <input
            type="email"
            id="toEmail"
            className="form-control"
            placeholder="example@abamail.com"
            value={to}
            onChange={e => setTo(e.target.value)}
            required
              style={{
                border: 'none',
                borderBottom: '1px solid #ccc',
                borderRadius: '0',
                backgroundColor: 'white',
                boxShadow: 'none'
              }} />
        </div>
        <div className="mb-3">
          {/* Subject input */}
          <input
            type="text"
            id="subject"
            className="form-control"
            placeholder="Subject"
            value={subject}
            onChange={e => setSubject(e.target.value)}
              style={{
                border: 'none',
                borderBottom: '1px solid #ccc',
                borderRadius: '0',
                backgroundColor: 'white',
                boxShadow: 'none'
              }} />
        </div>
        <div className="mb-3">
          {/* Body textarea */}
          <textarea
            id="body"
            className="form-control"
            rows={8}
            placeholder="Write your message here..."
            value={body}
            onChange={e => setBody(e.target.value)}
              style={{
                border: 'none',
                outline: 'none',
                boxShadow: 'none',
              }} />
        </div>
        {/* Form action buttons */}
        <div className="compose-footer">
          <button type="submit" className="btn btn-primary px-4" style={{border: 'none'}}>Send</button>
          <button type="button" className="btn btn-secondary px-4" onClick={handleCancel}>Cancel</button>
        </div>
      </form>
    </div>
  );
}

export default ComposeMail;