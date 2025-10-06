import showToast from '../../utils/toast';

// Component for deleting a mail, calling backend to do so
async function DeleteMail(mailId, navigate) {
      const token = localStorage.getItem('jwt');

      try {
        const response = await fetch(`http://localhost:8080/api/mails/${mailId}`, {
          method: 'DELETE',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          }
        });

        if (!response.ok) {
          const errorText = await response.json();
          throw new Error(errorText.error || 'Failed to send mail');
        }

        showToast('Mail deleted');
      } catch (error) {
        alert(error.message);
    }
}

export default DeleteMail;