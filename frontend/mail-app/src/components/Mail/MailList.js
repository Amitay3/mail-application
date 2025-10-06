import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';

import 'bootstrap/dist/js/bootstrap.bundle.min.js';
import MailItem from './MailItem';

function MailList({ onMailClick, refreshKey, type, query, onMailUpdate, darkMode }) {
  const [emails, setEmails] = useState([]);
  const [loading, setLoading] = useState(false);
  const [hasFetched, setHasFetched] = useState(false);
  const { labelId } = useParams();

  useEffect(() => {
    const fetchUserMails = async (showLoading = true) => {
      try {
        setHasFetched(false);
        if (showLoading) setLoading(true); 
        const token = localStorage.getItem('jwt');
        let url = 'http://localhost:8080/api/mails';

        switch (type) {
          case 'inbox': url += '/inbox'; break;
          case 'sent': url += '/sent'; break;
          case 'drafts': url += '/drafts'; break;
          case 'spam': url += '/spam'; break;
          case 'label': url = `http://localhost:8080/api/labels/folder/${labelId}`; break;
          case 'search': url += `/search/${encodeURIComponent(query)}`; break;
          default: url += ''; break;
        }

        const response = await fetch(url, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          }
        });

        if (!response.ok) {
          const errText = await response.text();
          throw new Error(`Failed to fetch mails: ${errText}`);
        }

        const data = await response.json();
        setEmails(data);
        setHasFetched(true);
      } catch (error) {
        alert(error.message);
      } finally {
        if (showLoading) setLoading(false);
      }
    };

    fetchUserMails();

    const intervalId = setInterval(() => {
      if (type === 'inbox') {
        fetchUserMails(false);
      }
    }, 10000);

    return () => clearInterval(intervalId);
  }, [type, refreshKey, query, labelId]);

  return (
    <div className="col-9 mails-content">
      <div className="mails-container">
        <div className="list-group mt-2">
          {loading ? (
            <div className={`text-center mt-3 ${darkMode ? 'text-light' : 'text-muted'}`}>
              Loading...
            </div>
          ) : (emails.length === 0 && hasFetched) ? (
            <div className={`text-center mt-3 ${darkMode ? 'text-light' : 'text-muted'}`}>
              {{ 
                inbox: 'You have no new mails.',
                sent: 'There are no sent mails.',
                drafts: 'There are no drafts.',
                search: 'No messages matched your search.',
                label: 'There are no mails with this label.'
              }[type] || 'No mails found.'}
            </div>
          ) : (
            emails.map((email) => (
              <MailItem
                key={email.id}
                email={email}
                onMailClick={onMailClick}
                onMailUpdate={onMailUpdate}
                type={type}
                darkMode={darkMode}
              />
            ))
          )}
        </div>
      </div>
    </div>
  );
}

export default MailList;
