// Avatar dropdown (giữ nguyên)
(() => {
  const btn = document.getElementById('profileBtn');
  const menu = document.getElementById('profileMenu');
  if (!btn || !menu) return;
  const toggle = (open) => {
    const next = open ?? (menu.getAttribute('aria-hidden') !== 'false');
    menu.setAttribute('aria-hidden', next ? 'false' : 'true');
    btn.setAttribute('aria-expanded', next ? 'true' : 'false');
  };
  btn.addEventListener('click', (e)=>{ e.stopPropagation(); toggle(); });
  document.addEventListener('click', ()=> toggle(false));
  menu.addEventListener('click', (e)=> e.stopPropagation());
})();

// Burger / Drawer
(() => {
  const btn = document.getElementById('burgerBtn');
  const drawer = document.getElementById('navDrawer');
  const overlay = document.getElementById('drawerOverlay');
  if (!btn || !drawer) return;

  const open = () => {
    drawer.hidden = false;
    drawer.classList.add('is-open');
    btn.classList.add('is-open');
    btn.setAttribute('aria-expanded','true');
    document.body.classList.add('no-scroll');
  };
  const close = () => {
    drawer.classList.remove('is-open');
    btn.classList.remove('is-open');
    btn.setAttribute('aria-expanded','false');
    document.body.classList.remove('no-scroll');
    setTimeout(() => { if (!drawer.classList.contains('is-open')) drawer.hidden = true; }, 240);
  };

  btn.addEventListener('click', ()=> drawer.classList.contains('is-open') ? close() : open());
  if (overlay) overlay.addEventListener('click', close);
  window.addEventListener('keydown', (e)=>{ if (e.key === 'Escape') close(); });

  // đóng khi bấm link/nút trong drawer
  drawer.querySelectorAll('a,[data-open-mood],button').forEach(el=> el.addEventListener('click', close));

  // logout mobile
  const btnLogoutMobile = document.getElementById('btnLogoutMobile');
  if (btnLogoutMobile) btnLogoutMobile.addEventListener('click', ()=>{
    const btnLogout = document.getElementById('btnLogout');
    if (btnLogout) btnLogout.click();
  });
})();
