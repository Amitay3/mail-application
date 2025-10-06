export default function showToast(message) {
  const toast = document.createElement('div');
  toast.className = 'my-toast';

  // Theme handling
  if (document.body.classList.contains('dark-mode')) {
    toast.classList.add('dark');
  } else {
    toast.classList.add('light');
  }

  // Create
  const text = document.createElement('span');
  text.innerText = message;
  toast.appendChild(text);

  // Close button
  const btn = document.createElement('button');
  btn.className = 'my-toast-close';
  btn.innerText = '×';
  btn.onclick = () => {
    toast.classList.remove('show');
    setTimeout(() => toast.remove(), 300);
  };
  toast.appendChild(btn);

  // Append to body and show
  document.body.appendChild(toast);
  requestAnimationFrame(() => toast.classList.add('show'));

  // Automatically remove after 5 seconds
  setTimeout(() => {
    toast.classList.remove('show');
    setTimeout(() => toast.remove(), 300);
  }, 5000);
}
