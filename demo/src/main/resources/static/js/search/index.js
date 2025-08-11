// public/js/search/index.js
const qEl = document.getElementById('q');
const clearQ = document.getElementById('clearQ');
const resultsEl = document.getElementById('results');
const noResultsEl = document.getElementById('noResults');
const pickedListEl = document.getElementById('pickedList');
const pickedCountEl = document.getElementById('pickedCount');
const plNameEl = document.getElementById('plName');
const plDescEl = document.getElementById('plDesc');
const plPublicEl = document.getElementById('plPublic');
const createForm = document.getElementById('createForm');
const btnCreate = document.getElementById('btnCreate');
const btnCreateCount = document.getElementById('btnCreateCount');

let picked = [];        // [{id, name, artist_names, albumImg, uri}]
let lastKeyword = '';
let searching = false;

const readTokens = () => {
  // nếu có lưu ở localStorage/cookie thì lấy
  const accessToken = localStorage.getItem('spotify_access') || undefined;
  const refreshToken = localStorage.getItem('spotify_refresh') || undefined;
  return { accessToken, refreshToken };
};

const debounce = (fn, ms=350) => {
  let t; return (...args)=>{ clearTimeout(t); t=setTimeout(()=>fn(...args), ms); };
};

const renderResults = (tracks=[]) => {
  resultsEl.innerHTML = '';
  if (!tracks || tracks.length === 0) {
    noResultsEl.style.display = 'block';
    return;
  }
  noResultsEl.style.display = 'none';

  tracks.forEach(t => {
    const already = picked.some(p => p.id === t.id);
    const div = document.createElement('div');
    div.className = 'result-item';
    div.innerHTML = `
      <div class="thumb"><img src="${t.album?.image_url || '/img/cover-fallback.png'}" alt="" width="56" height="56"/></div>
      <div class="meta">
        <h4>${t.name}</h4>
        <small>${t.artist_names || ''}</small>
      </div>
      <div class="actions">
        <button ${already ? 'disabled' : ''} title="Thêm">+</button>
      </div>
    `;
    div.querySelector('button')?.addEventListener('click', ()=>{
      addPicked(t);
    });
    resultsEl.appendChild(div);
  });
};

const refreshPickedUI = () => {
  pickedListEl.innerHTML = '';
  picked.forEach((t, idx) => {
    const row = document.createElement('div');
    row.className = 'picked-row';
    row.draggable = true;
    row.dataset.id = t.id;
    row.innerHTML = `
      <div class="index">${idx+1}</div>
      <div class="thumb"><img src="${t.albumImg}" alt="" width="44" height="44"/></div>
      <div class="meta">
        <h5>${t.name}</h5>
        <small>${t.artist_names || ''}</small>
      </div>
      <button class="remove" title="Gỡ">✕</button>
    `;
    row.querySelector('.remove').addEventListener('click', ()=>{
      picked = picked.filter(x => x.id !== t.id);
      updateState();
    });

    // drag sort
    row.addEventListener('dragstart', e=>{
      e.dataTransfer.setData('text/plain', t.id);
      row.classList.add('dragging');
    });
    row.addEventListener('dragend', ()=> row.classList.remove('dragging'));
    row.addEventListener('dragover', e=>{
      e.preventDefault();
      const draggingId = e.dataTransfer.getData('text/plain');
      if (!draggingId || draggingId === t.id) return;
      const from = picked.findIndex(x=>x.id===draggingId);
      const to = picked.findIndex(x=>x.id===t.id);
      if (from >=0 && to >=0) {
        const item = picked.splice(from,1)[0];
        picked.splice(to,0,item);
        refreshPickedUI();
      }
    });

    pickedListEl.appendChild(row);
  });

  pickedCountEl.textContent = picked.length;
  btnCreateCount.textContent = picked.length;
  btnCreate.disabled = picked.length === 0;
};

const updateState = () => {
  refreshPickedUI();
  // cập nhật nút ở results (disable nếu đã chọn)
  Array.from(resultsEl.querySelectorAll('.result-item')).forEach(el=>{
    const name = el.querySelector('h4')?.textContent?.trim();
    const artist = el.querySelector('small')?.textContent?.trim();
    const btn = el.querySelector('button');
    if (!btn) return;
    const pickedMatch = picked.find(p => p.name === name && p.artist_names === artist);
    btn.disabled = !!pickedMatch;
  });
};

const addPicked = (t) => {
  if (picked.some(p => p.id === t.id)) return;
  picked.push({
    id: t.id,
    name: t.name,
    artist_names: t.artist_names || (t.artists?.map(a=>a.name).join(', ') || ''),
    albumImg: t.album?.image_url || '/img/cover-fallback.png',
    uri: `spotify:track:${t.id}`
  });
  updateState();
};

const doSearch = async (kw) => {
  if (!kw || kw.trim().length === 0) {
    renderResults([]); return;
  }
  searching = true;
  lastKeyword = kw;
  const body = { keyword: kw, ...readTokens() };
  const resp = await fetch('/api/no-store/search-tracks', {
    method: 'POST', headers: { 'Content-Type':'application/json' },
    body: JSON.stringify(body)
  }).then(r=>r.json()).catch(()=>null);

  searching = false;
  if (!resp || !resp.success) { renderResults([]); return; }
  renderResults(resp.tracks || []);
  updateState();
};

qEl.addEventListener('input', debounce(e => doSearch(e.target.value), 350));
clearQ.addEventListener('click', ()=>{
  qEl.value = '';
  renderResults([]);
  updateState();
  qEl.focus();
});

// Create playlist
createForm.addEventListener('submit', async (e)=>{
  e.preventDefault();
  if (picked.length === 0) return;

  const name = plNameEl.value.trim() || `Moodsic Playlist ${new Date().toLocaleDateString()}`;
  const description = plDescEl.value.trim() || 'Custom Playlist (NoStore)';
  const trackUris = picked.map(t => t.uri);
  const isPublic = !!plPublicEl.checked;

  btnCreate.disabled = true;
  btnCreate.textContent = 'Đang tạo...';

  const body = { name, trackUris, description, isPublic, ...readTokens() };

  const resp = await fetch('/api/no-store/create-playlists', {
    method: 'POST',
    headers: { 'Content-Type':'application/json' },
    body: JSON.stringify(body)
  }).then(r=>r.json()).catch(()=>null);

  if (resp && resp.success) {
    alert('Tạo playlist thành công!');
    // điều hướng sang trang Playlists của bạn
    window.location.href = '/me/playlists';
  } else {
    alert('Tạo playlist thất bại. Vui lòng thử lại.');
    btnCreate.disabled = false;
    btnCreate.innerHTML = `➕ Tạo Playlist (<span id="btnCreateCount">${picked.length}</span> bài)`;
  }
});

// khởi tạo
updateState();
