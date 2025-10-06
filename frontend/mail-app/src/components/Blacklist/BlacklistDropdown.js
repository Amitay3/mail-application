import { useState } from "react";
import AddUrl from "./AddUrl";
import DeleteUrl from "./DeleteUrl";
// Component for displaying a dropdown menu for blacklisting options
function BlacklistDropdown({ onUrlAdd, onUrlDelete }) {
    const [showUrl, setShowUrl] = useState(false);
    const [showDeleteUrl, setShowDeleteUrl] = useState(false);

    const handleUrlOpen = () => setShowUrl(true);
    const handleUrlClose = () => setShowUrl(false);
    const handleDeleteUrlOpen = () => setShowDeleteUrl(true);
    const handleDeleteUrlClose = () => setShowDeleteUrl(false);

    const handleAddedUrl = () => {
        setShowUrl(false);
        onUrlAdd();
    }
    // Function to handle opening the URL addition modal
    return (
    <div className="dropdown" onClick={(e) => e.stopPropagation()}>
      <button className="btn btn-secondary dropdown-toggle plus-btn" type="button" data-bs-toggle="dropdown" aria-expanded="false" onClick={(e) => e.stopPropagation()}>
        <img src="/icons8-menu-30.png" alt="Options" style={{ width: '20px', height: '20px' }} />
      </button>

      <ul className="dropdown-menu text-centered"  style={{ width: '300px' }}>
        <li>
          <button className="dropdown-item d-flex align-items-center" onClick={handleUrlOpen}>
            <img src="/icons8-spam-30 (3).png" alt="Label Icon" style={{ width: '20px', height: '20px', marginRight: '10px' }} />
            Report malicious url
          </button>
        </li>
        <li>
          <button className="dropdown-item d-flex align-items-center" onClick={handleDeleteUrlOpen}>
            <img src="/icons8-delete-30.png" alt="Delete Icon" style={{ width: '20px', height: '20px', marginRight: '10px' }} />
            Remove url from future blacklisting
          </button>
        </li>
      </ul>

      {showUrl && (
        <div className="label-modal-backdrop">
          <div className="label-modal-window">
            <AddUrl onSend={handleAddedUrl} onCancel={handleUrlClose}/>
          </div>
        </div>
      )}

      {showDeleteUrl && (
        <div className="label-modal-backdrop">
          <div className="label-modal-window">
            <DeleteUrl
              onCancel={handleDeleteUrlClose}
                onSend={() => {
                    setShowDeleteUrl(false);
                }}            
            />            
          </div>
        </div>
      )}
    </div>
    );
}

export default BlacklistDropdown;
