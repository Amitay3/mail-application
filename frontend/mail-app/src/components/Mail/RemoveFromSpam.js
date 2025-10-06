import showToast from '../../utils/toast';
/* Removing a mail from the spam folder */
async function RemoveFromSpam(mailId) {
    try {
        const token = localStorage.getItem('jwt');
        let url = `http://localhost:8080/api/mails/spam/${mailId}`;

        const response = await fetch(url, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            const errText = await response.text();
            throw new Error(`Failed to report spam: ${errText}`);
        }
        showToast('Mail removed from spam');
    } catch (error) {
        alert(error.message);
    }
}

export default RemoveFromSpam;
    