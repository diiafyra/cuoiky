// /js/home/likes.js
const HEART_SELECTOR = '.heart-btn';

function setLiked(btn, liked){
  btn.setAttribute('aria-pressed', liked ? 'true' : 'false');
  btn.classList.toggle('is-liked', !!liked);
}

async function fetchStatuses(buttons){
  const items = buttons.map(btn => ({
    externalId: btn.dataset.externalId,
    source: btn.dataset.source
  }));

  if(items.length === 0) return;

  try{
    const res = await fetch('/api/likes/status', {
      method: 'POST',
      headers: {'Content-Type':'application/json'},
      credentials: 'include',
      body: JSON.stringify({items})
    });
    if(!res.ok) return;
    const data = await res.json();
    (data.results || []).forEach(r => {
      const b = document.querySelector(`${HEART_SELECTOR}[data-external-id="${CSS.escape(r.externalId)}"][data-source="${CSS.escape(r.source)}"]`);
      if (b) setLiked(b, r.liked);
    });
  }catch(e){ /* ignore */ }
}

async function toggleLike(btn){
  const payload = {
    externalId: btn.dataset.externalId,
    source: btn.dataset.source,
    title: btn.dataset.title || null,
    description: null,
    externalUrl: btn.dataset.externalUrl || null,
    coverUrl: btn.dataset.coverUrl || null,
    ownerName: btn.dataset.ownerName || null,
    totalTracks: btn.dataset.totalTracks ? parseInt(btn.dataset.totalTracks,10) : null,
    snapshotId: btn.dataset.snapshotId || null
  };
  btn.disabled = true;
  try{
    const res = await fetch('/api/likes/toggle', {
      method: 'POST',
      headers: {'Content-Type':'application/json'},
      credentials: 'include',
      body: JSON.stringify(payload)
    });
    if(res.ok){
      const data = await res.json();
      setLiked(btn, !!data.liked);
    }
  }catch(e){ /* ignore */ }
  finally { btn.disabled = false; }
}

(function initLikes(){
  const btnNodes = Array.from(document.querySelectorAll(HEART_SELECTOR));
  if (btnNodes.length === 0) return; // guest hoặc không có card

  // trạng thái ban đầu
  fetchStatuses(btnNodes);

  // click handler
  btnNodes.forEach(btn => {
    btn.addEventListener('click', (e)=>{
      e.stopPropagation();
      toggleLike(btn);
    });
  });
})();
