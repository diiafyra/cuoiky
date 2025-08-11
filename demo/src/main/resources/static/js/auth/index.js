// ===== Firebase config =====
const firebaseConfig = {
  apiKey: "AIzaSyA22EmgTu3L4pbs1s34l5OjhD-qUxTbtuk",
  authDomain: "careful-century-453110-s8.firebaseapp.com",
  projectId: "careful-century-453110-s8",
  appId: "1:50093176860:web:3212750e1fa661080687d9"
};
firebase.initializeApp(firebaseConfig);
const auth = firebase.auth();

// ===== Cookie helpers =====
function setCookie(name, value, days, { secure = true, sameSite = 'Lax' } = {}) {
  const d = new Date(); d.setTime(d.getTime() + (days * 24 * 60 * 60 * 1000));
  let cookie = `${name}=${encodeURIComponent(value)}; expires=${d.toUTCString()}; path=/; SameSite=${sameSite}`;
  if (secure && location.protocol === 'https:') cookie += '; Secure';
  document.cookie = cookie;
}
function getCookie(name) {
  const raw = document.cookie.split('; ').find(r => r.startsWith(name + '='));
  if (!raw) return '';
  try { return decodeURIComponent(raw.split('=')[1]); } catch { return ''; }
}
function deleteCookie(name) {
  document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/`;
}

// ===== UI helpers =====
function setAll(selector, fn) {
  document.querySelectorAll(selector).forEach(el => fn(el));
}

function hydrateAvatarFromCookie() {
  const url = getCookie('avatarUrl') || '/asset/profile.png';
  setAll('.js-avatar', img => {
    img.src = url;
    // fallback nếu ảnh hỏng
    img.onerror = () => { img.onerror = null; img.src = '/asset/profile.png'; };
  });
}

function hydrateEmailFromCookie() {
  const email = getCookie('email');
  const el = document.querySelector('.js-email');
  if (el && email) el.textContent = email;
}
function applyAuthUI(user) {
  const guest = document.querySelector('.auth--guest');
  const signed = document.querySelector('.auth--user');
  if (guest && signed) {
    const isOn = !!user;
    guest.style.display = isOn ? 'none' : 'flex';
    signed.style.display = isOn ? 'flex' : 'none';
  }

  const name = user
    ? (user.displayName || (user.email ? user.email.split('@')[0] : 'User'))
    : '';

  setAll('.js-username', el => { if (name) el.textContent = name; });

  const avatarUrl = (user && user.photoURL) || getCookie('avatarUrl') || '/asset/profile.png';
  setAll('.js-avatar', img => {
    img.src = avatarUrl;
    img.onerror = () => { img.onerror = null; img.src = '/asset/profile.png'; };
  });

  hydrateEmailFromCookie();
}
// ===== Persist idToken + avatar + email vào cookie =====
async function writeIdTokenCookie(user) {
  if (!user) {
    deleteCookie('idToken'); deleteCookie('avatarUrl'); deleteCookie('email');
    return;
  }
  const idToken = await user.getIdToken(true);
  setCookie('idToken', idToken, 1); // 1 ngày
  if (user.photoURL) setCookie('avatarUrl', user.photoURL, 1);
  if (user.email)    setCookie('email', user.email, 1); // <-- THÊM EMAIL
}

// refresh mỗi ~50 phút
let refreshTimer = null;
function startTokenRefresh(user) {
  if (refreshTimer) clearInterval(refreshTimer);
  if (!user) return;
  refreshTimer = setInterval(() => writeIdTokenCookie(user), 50 * 60 * 1000);
}

// ===== Auth state listener =====
auth.onAuthStateChanged(async (user) => {
  await writeIdTokenCookie(user);
  startTokenRefresh(user);
  applyAuthUI(user);
});

// Hydrate ngay khi tải trang
document.addEventListener('DOMContentLoaded', () => {
  hydrateAvatarFromCookie();
  hydrateEmailFromCookie();
});

// (Logout buttons)
document.addEventListener('click', async (e) => {
  if (e.target?.id === 'btnLogout' || e.target?.id === 'btnLogoutMobile') {
    await auth.signOut();
    deleteCookie('idToken'); deleteCookie('avatarUrl'); deleteCookie('email');
    location.href = '/login';
  }
});

// ===== Public functions cho login pages =====
async function loginWithEmail() {
  try {
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;
    const cred = await auth.signInWithEmailAndPassword(email, password);
    const idToken = await cred.user.getIdToken();
    setCookie('idToken', idToken, 1);
    if (cred.user.photoURL) setCookie('avatarUrl', cred.user.photoURL, 1);
    if (cred.user.email)    setCookie('email', cred.user.email, 1);
    window.location.href = '/home';
  } catch (e) { alert('Login failed: ' + e.message); }
}
async function loginWithGoogle() {
  try {
    const provider = new firebase.auth.GoogleAuthProvider();
    const result = await auth.signInWithPopup(provider);
    const idToken = await result.user.getIdToken();
    setCookie('idToken', idToken, 1);
    if (result.user.photoURL) setCookie('avatarUrl', result.user.photoURL, 1);
    if (result.user.email)    setCookie('email', result.user.email, 1);
    window.location.href = '/home';
  } catch (e) { alert('Google login failed: ' + e.message); }
}
window.loginWithEmail = loginWithEmail;
window.loginWithGoogle = loginWithGoogle;

console.debug('idToken cookie (present?):', !!getCookie('idToken'));
