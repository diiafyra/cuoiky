// /js/playlists/index.js
// Đảm bảo nút open-spotify hoạt động trên trang này
document.addEventListener('click', function (e) {
  const btn = e.target.closest('.open-spotify');
  if (!btn) return;
  e.stopPropagation();
  const url = btn.getAttribute('data-url');
  if (url) window.open(url, '_blank', 'noopener');
}, { passive: true });

// likes.js đã lo phần tim (toggle + status). Ở trang này
// section "Đã thích" đã set sẵn aria-pressed=true cho nút.
