import showToast from '../../utils/toast';

async function ReportMail(mailId) {
    try {
        const token = localStorage.getItem('jwt');
        let url = `http://localhost:8080/api/mails/spam/${mailId}`;

        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            const errText = await response.text();
            throw new Error(`Failed to report spam: ${errText}`);
        }
        showToast('Mail reported as spam');

    } catch (error) {
        alert(error.message);
    }
}

export default ReportMail;
    