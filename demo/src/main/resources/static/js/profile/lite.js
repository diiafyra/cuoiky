// đọc avatar từ cookie
function getCookie(name){
  const raw = document.cookie.split('; ').find(r => r.startsWith(name+'='));
  if (!raw) return '';
  try { return decodeURIComponent(raw.split('=')[1]); } catch { return ''; }
}
function hydrateAvatar(){
  const url = getCookie('avatarUrl');
  const img = document.querySelector('.js-avatar');
  if (img && url) img.src = url;
}
document.addEventListener('DOMContentLoaded', hydrateAvatar);

// helpers
const $ = (s) => document.querySelector(s);
const toList = (v) => v.split(',').map(x=>x.trim()).filter(Boolean);
const fromList = (arr) => (Array.isArray(arr) && arr.length) ? arr.join(', ') : '';

const vibeHidden = $('#vibe');
const chips = document.querySelectorAll('.chips--vibe [data-vibe]');
chips.forEach(btn=>{
  btn.addEventListener('click', ()=>{
    chips.forEach(b=>b.classList.remove('active'));
    btn.classList.add('active');
    vibeHidden.value = btn.dataset.vibe;
  });
});

const fields = {
  genres:  $('#genres'),
  artists: $('#artists'),
  langs:   $('#langs'),
};

const saveState = $('#saveState');
const form = $('#mpLiteForm');

async function loadLite(){
  try {
    const res = await fetch('/api/me/music-profile-lite');
    if (!res.ok) throw new Error('unauthorized');
    const p = await res.json();

    // vibe
    if (p.vibe) {
      vibeHidden.value = p.vibe;
      const btn = document.querySelector(`[data-vibe="${p.vibe}"]`);
      if (btn) btn.classList.add('active');
    }

    fields.genres.value  = fromList(p.favoriteGenres);
    fields.artists.value = fromList(p.favoriteArtists);
    fields.langs.value   = fromList(p.languages);
  } catch (e) {
    console.warn('[profile-lite] load error:', e);
  }
}

form.addEventListener('submit', async (e)=>{
  e.preventDefault();
  const payload = {
    vibe: vibeHidden.value || 'CHILL',
    favoriteGenres:  toList(fields.genres.value),
    favoriteArtists: toList(fields.artists.value),
    languages:       toList(fields.langs.value)
  };
  saveState.textContent = 'Đang lưu...';
  try {
    const res = await fetch('/api/me/music-profile-lite', {
      method: 'PUT',
      headers: { 'Content-Type':'application/json' },
      body: JSON.stringify(payload)
    });
    if (!res.ok) throw new Error('save failed');
    saveState.textContent = 'Đã lưu!';
    setTimeout(()=> saveState.textContent = '', 1800);
  } catch (e) {
    saveState.textContent = 'Lưu thất bại, thử lại.';
  }
});

loadLite();
