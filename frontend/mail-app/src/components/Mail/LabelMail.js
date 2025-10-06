import showToast from '../../utils/toast';

// Component to label a mail a specific label
async function LabelMail(mailId) {
    e.stopPropagation();

    const token = localStorage.getItem('jwt');

    try {
        const response = await fetch(`http://localhost:8080/api/labels/${labelId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
        });

        if (!response.ok) {
            const errorText = await response.json();
            throw new Error(errorText.error || 'Failed to delete label');
        }

        showToast('Label deleted');

        window.location.reload();
    } catch (error) {
        alert(error.message);
    }
}
