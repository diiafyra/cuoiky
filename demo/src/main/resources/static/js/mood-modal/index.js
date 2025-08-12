// ===== Modal open/close & preset =====
(() => {
  const modal = document.getElementById("mood-modal");
  if (!modal) return;

  const dialog = modal.querySelector(".mood-dialog");
  const openers = document.querySelectorAll("[data-open-mood]");
  const closers = modal.querySelectorAll("[data-close-mood]");
  let lastFocus = null;

  function open(preset) {
    lastFocus = document.activeElement;
    modal.setAttribute("aria-hidden", "false");
    document.body.classList.add("no-scroll");
    if (preset)
      modal.dispatchEvent(
        new CustomEvent("mood:preset", { detail: { preset } })
      );
    setTimeout(() => dialog.focus(), 0);
  }
  function close() {
    modal.setAttribute("aria-hidden", "true");
    document.body.classList.remove("no-scroll");
    if (lastFocus && lastFocus.focus) lastFocus.focus();
  }

  openers.forEach((btn) =>
    btn.addEventListener("click", () =>
      open(btn.getAttribute("data-preset") || null)
    )
  );
  closers.forEach((btn) => btn.addEventListener("click", close));
  modal.addEventListener("click", (e) => {
    if (e.target.classList.contains("mood-backdrop")) close();
  });
  window.addEventListener("keydown", (e) => {
    if (e.key === "Escape" && modal.getAttribute("aria-hidden") === "false")
      close();
  });

  // auto-close khi widget submit
  modal.addEventListener("mood:submit", close);

  // ===== Widget logic =====
  const textarea = modal.querySelector("textarea.textMood");
  const btnDelete = modal.querySelector("#btnDelete");
  const quarters = modal.querySelectorAll(".quarter");
  const btnSend = modal.querySelector("#btnSend");
  const canvas = modal.querySelector("#seedlingCanvas");
  const ctx = canvas.getContext("2d");

  // Retina
  function resizeCanvas() {
    const dpr = window.devicePixelRatio || 1;
    const rect = canvas.getBoundingClientRect();
    canvas.width = Math.max(1, Math.floor(rect.width * dpr));
    canvas.height = Math.max(1, Math.floor(rect.height * dpr));
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
  }
  new ResizeObserver(resizeCanvas).observe(canvas);

  const clickCount = { happy: 0, calm: 0, sad: 0, energetic: 0 };

  function draw(x, y, size, color, angle = 0, clockwise = true) {
    ctx.save();
    ctx.translate(x, y);
    ctx.rotate(clockwise ? angle : -angle);
    ctx.scale(size / 100, size / 100);
    ctx.fillStyle = color;
    ctx.strokeStyle = color;
    ctx.lineWidth = 3;
    const outerRadius = 40,
      innerRadius = outerRadius * 0.5,
      spikes = 5;
    let rot = (Math.PI / 2) * 3,
      step = Math.PI / spikes;
    ctx.beginPath();
    ctx.moveTo(0, -outerRadius);
    for (let i = 0; i < spikes; i++) {
      let x1 = Math.cos(rot) * outerRadius,
        y1 = Math.sin(rot) * outerRadius;
      ctx.lineTo(x1, y1);
      rot += step;
      let x2 = Math.cos(rot) * innerRadius,
        y2 = Math.sin(rot) * innerRadius;
      ctx.lineTo(x2, y2);
      rot += step;
    }
    ctx.lineTo(0, -outerRadius);
    ctx.closePath();
    ctx.fill();
    ctx.stroke();
    ctx.restore();
  }

  const colorMap = {
    happy: "#a04a3a",
    calm: "#6f7f7f",
    sad: "#243456",
    energetic: "#e7c8d0",
  };

  // Preset khi mở nhanh
  modal.addEventListener("mood:preset", (e) => {
    const k = e.detail?.preset;
    if (!k || !colorMap[k]) return;
    for (let i = 0; i < 3; i++) clickOnce(k);
    textarea.focus();
  });

  function clickOnce(key) {
    const color = colorMap[key] || "#fff";
    if (clickCount.hasOwnProperty(key)) clickCount[key]++;
    const margin = 20;
    const x = Math.random() * (canvas.clientWidth - 2 * margin) + margin;
    const y = Math.random() * (canvas.clientHeight - 2 * margin) + margin;
    const size = Math.random() * 40 + 30;
    const angle = Math.random() * 2 * Math.PI;
    const clockwise = Math.random() > 0.5;
    draw(x, y, size, color, angle, clockwise);
  }

  quarters.forEach((q) =>
    q.addEventListener("click", () => {
      textarea.focus();
      const key = (q.dataset.feeling || q.classList[1] || "").toLowerCase();
      clickOnce(key);
    })
  );

  btnDelete.addEventListener("click", () => {
    textarea.value = "";
    textarea.focus();
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    for (const k in clickCount) clickCount[k] = 0;
  });

  // ==== NEW: tính circumplex ====
  const safeRatio = (a, b) => {
    const A = +a || 0,
      B = +b || 0;
    const den = A + B;
    return den ? (A - B) / den : 0;
  };
  const calcCircumplex = (cc) => ({
    valence: safeRatio(cc?.happy, cc?.sad),
    arousal: safeRatio(cc?.energetic, cc?.calm),
  });

  // Ghép ảnh: canvas + moodText
  async function composeImageBlob(canvas, moodText) {
    const w = Math.max(1, Math.floor(canvas.clientWidth));
    const h = Math.max(1, Math.floor(canvas.clientHeight));
    const pad = 16;
    const text = (moodText || "").trim();
    const textBlock = text ? 80 : 0;

    const off = document.createElement("canvas");
    off.width = w;
    off.height = h + textBlock;
    const o = off.getContext("2d");

    const g = o.createLinearGradient(0, 0, 0, off.height);
    g.addColorStop(0, "#203940");
    g.addColorStop(1, "#16262b");
    o.fillStyle = g;
    o.fillRect(0, 0, off.width, off.height);

    o.drawImage(canvas, 0, 0, w, h);

    if (text) {
      o.fillStyle = "rgba(255,255,255,0.95)";
      o.font = "16px system-ui, -apple-system, Segoe UI, Roboto, Arial";
      o.textBaseline = "top";
      const maxW = w - pad * 2;
      const words = text.split(/\s+/);
      let line = "",
        y = h + pad;
      for (const wd of words) {
        const test = line ? line + " " + wd : wd;
        if (o.measureText(test).width > maxW) {
          o.fillText(line, pad, y);
          y += 22;
          line = wd;
        } else line = test;
      }
      if (line) o.fillText(line, pad, y);
    }

    return await new Promise((res) => off.toBlob(res, "image/png", 0.92));
  }

  // ==== Submit: gửi multipart (FormData) ====
  btnSend.addEventListener("click", async () => {
    try {
      const moodText = (textarea.value || "").trim();
      const circumplex = calcCircumplex(clickCount);
      const blob = await composeImageBlob(canvas, moodText);

      // emit cho ai cần
      modal.dispatchEvent(
        new CustomEvent("mood:submit", {
          bubbles: true,
          detail: { moodText, clickCount: { ...clickCount }, circumplex },
        })
      );

      const fd = new FormData();
      fd.append("moodText", moodText);
      fd.append("circumplex", JSON.stringify(circumplex)); // JSON string
      if (blob) fd.append("image", blob, "mood.png"); // file part

      const res = await fetch("/api/recommendations/mood", {
        method: "POST",
        body: fd,
      });
      const data = await res.json();
      if (!data?.id) throw new Error("No id returned");

      // reset nhỏ
      textarea.value = "";
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      for (const k in clickCount) clickCount[k] = 0;

      // chuyển sang trang kết quả theo logId
      window.location.href = `/recommend/${data.id}`;
    } catch (err) {
      console.error("[recommendation]", err);
      alert("Có lỗi khi gợi ý. Vui lòng thử lại.");
    }
  });
})();
