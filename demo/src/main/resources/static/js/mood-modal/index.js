// ===== Modal open/close & preset =====
(() => {
  const modal = document.getElementById('mood-modal');
  if (!modal) return;

  const dialog = modal.querySelector('.mood-dialog');
  const openers = document.querySelectorAll('[data-open-mood]');
  const closers = modal.querySelectorAll('[data-close-mood]');
  let lastFocus = null;

  function open(preset){
    lastFocus = document.activeElement;
    modal.setAttribute('aria-hidden','false');
    document.body.classList.add('no-scroll');
    if (preset) modal.dispatchEvent(new CustomEvent('mood:preset',{detail:{preset}}));
    setTimeout(()=>dialog.focus(),0);
  }
  function close(){
    modal.setAttribute('aria-hidden','true');
    document.body.classList.remove('no-scroll');
    if (lastFocus && lastFocus.focus) lastFocus.focus();
  }

  openers.forEach(btn=>{
    btn.addEventListener('click', ()=>{
      open(btn.getAttribute('data-preset') || null);
    });
  });
  closers.forEach(btn=>btn.addEventListener('click', close));
  modal.addEventListener('click', (e)=>{ if (e.target.classList.contains('mood-backdrop')) close(); });
  window.addEventListener('keydown', (e)=>{ if (e.key==='Escape' && modal.getAttribute('aria-hidden')==='false') close(); });

  // auto-close khi widget submit
  modal.addEventListener('mood:submit', close);

  // ===== Widget logic (code cũ, scope trong modal) =====
  const textarea = modal.querySelector('textarea.textMood');
  const btnDelete = modal.querySelector('#btnDelete');
  const quarters  = modal.querySelectorAll('.quarter');
  const btnSend   = modal.querySelector('#btnSend');
  const canvas    = modal.querySelector('#seedlingCanvas');
  const ctx       = canvas.getContext('2d');

  // Hiển thị sắc nét trên màn hình retina
  function resizeCanvas(){
    const dpr = window.devicePixelRatio || 1;
    const rect = canvas.getBoundingClientRect();
    canvas.width  = Math.max(1, Math.floor(rect.width  * dpr));
    canvas.height = Math.max(1, Math.floor(rect.height * dpr));
    ctx.setTransform(dpr,0,0,dpr,0,0);
  }
  new ResizeObserver(resizeCanvas).observe(canvas);

  const clickCount = { happy:0, calm:0, sad:0, energetic:0 };

  function draw(x, y, size, color, angle = 0, clockwise = true) {
    ctx.save(); ctx.translate(x, y); ctx.rotate(clockwise ? angle : -angle);
    ctx.scale(size / 100, size / 100); ctx.fillStyle = color; ctx.strokeStyle = color; ctx.lineWidth = 3;

    const outerRadius = 40, innerRadius = outerRadius * 0.5, spikes = 5;
    let rot = (Math.PI / 2) * 3, step = Math.PI / spikes;

    ctx.beginPath(); ctx.moveTo(0, -outerRadius);
    for (let i=0;i<spikes;i++){
      let x1 = Math.cos(rot) * outerRadius, y1 = Math.sin(rot) * outerRadius; ctx.lineTo(x1,y1); rot += step;
      let x2 = Math.cos(rot) * innerRadius, y2 = Math.sin(rot) * innerRadius; ctx.lineTo(x2,y2); rot += step;
    }
    ctx.lineTo(0, -outerRadius); ctx.closePath(); ctx.fill(); ctx.stroke(); ctx.restore();
  }

  const colorMap = { happy:'#a04a3a', calm:'#6f7f7f', sad:'#243456', energetic:'#e7c8d0' };

  // Preset khi mở nhanh
  modal.addEventListener('mood:preset', (e)=>{
    const k = e.detail?.preset;
    if (!k || !colorMap[k]) return;
    for (let i=0;i<3;i++) clickOnce(k);
    textarea.focus();
  });

  function clickOnce(key){
    const color = colorMap[key] || '#fff';
    if (clickCount.hasOwnProperty(key)) clickCount[key]++;
    const margin = 20;
    const x = Math.random() * (canvas.clientWidth  - 2*margin) + margin;
    const y = Math.random() * (canvas.clientHeight - 2*margin) + margin;
    const size = Math.random() * 40 + 30;
    const angle = Math.random() * 2 * Math.PI;
    const clockwise = Math.random() > 0.5;
    draw(x, y, size, color, angle, clockwise);
  }

  quarters.forEach(q => q.addEventListener('click', ()=>{
    textarea.focus();
    const key = (q.dataset.feeling || q.classList[1] || '').toLowerCase();
    clickOnce(key);
  }));

  btnDelete.addEventListener('click', ()=>{
    textarea.value=''; textarea.focus();
    ctx.clearRect(0,0,canvas.width,canvas.height);
  });

  btnSend.addEventListener('click', ()=>{
    const message = textarea.value.trim();
    let stats = 'Thống kê số lần nhấn từng nút:\n';
    for (const key in clickCount) stats += `${key}: ${clickCount[key]} lần\n`;

    // Giữ behavior cũ
    alert(`Nội dung đã gửi:\n${message}\n\n${stats}`);

    // Emit event ra ngoài (Home sẽ lắng nghe để gọi API)
    const image = canvas.toDataURL('image/png', 0.92);
    modal.dispatchEvent(new CustomEvent('mood:submit', {
      bubbles:true, detail:{ text: message, clickCount:{...clickCount}, image }
    }));

    // Reset
    textarea.value=''; textarea.focus();
    ctx.clearRect(0,0,canvas.width,canvas.height);
    for (const key in clickCount) clickCount[key]=0;
  });
})();
