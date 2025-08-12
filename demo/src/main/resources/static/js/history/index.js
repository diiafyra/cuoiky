// history/index.js
document.addEventListener('click', (e) => {
  const a = e.target.closest('a.h-card');
  if (!a) return;
  // nếu bấm giữa/ctrl/cmd thì để browser xử lý mặc định
  if (e.metaKey || e.ctrlKey || e.which === 2) return;
  // fallback đảm bảo navigation
  e.preventDefault();
  window.location.href = a.getAttribute('href');
}, { passive: false });
