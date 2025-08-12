// /js/recommend/show.js (debug build)
(function () {
  console.info("[recommend/show] loaded");

  const dataEl = document.getElementById("rec-items");
  const listEl = document.getElementById("recGrid");
  const emptyEl = document.getElementById("recEmpty");
  if (!dataEl) {
    console.warn("[recommend/show] #rec-items not found");
    return;
  }
  if (!listEl) {
    console.warn("[recommend/show] #recCarousel not found");
    return;
  }

  function readItems() {
    let raw = (dataEl.textContent || "").trim();
    console.groupCollapsed("[recommend/show] raw payload");
    console.log("length:", raw.length);
    console.log("head  :e", raw.slice(0, 220));
    console.groupEnd();

    if (!raw) return [];

    // 1) Unescape HTML entities if any
    raw = raw
      .replace(/&quot;/g, '"')
      .replace(/&amp;/g, "&")
      .replace(/&lt;/g, "<")
      .replace(/&gt;/g, ">");

    // 2) TH phổ biến: raw có dấu \" do bị stringify 2 lần
    //    Ví dụ: [{\"title\":\"...\"}]  ->  [{"title":"..."}]
    if (raw.includes('\\"')) {
      const fixed = raw.replace(/\\"/g, '"');
      try {
        const arr = JSON.parse(fixed);
        console.info(
          '[recommend/show] parsed after de-escape \\"',
          Array.isArray(arr) ? "Array OK" : typeof arr
        );
        if (Array.isArray(arr)) return arr;
      } catch (e) {
        console.warn("[recommend/show] fail parse after de-escape", e);
      }
    }

    // 3) Parse bình thường
    try {
      const first = JSON.parse(raw);
      console.info(
        "[recommend/show] first parse type:",
        Array.isArray(first) ? "Array" : typeof first
      );
      if (Array.isArray(first)) return first;

      // 4) Nếu ra string JSON -> parse lần 2
      if (typeof first === "string") {
        try {
          const second = JSON.parse(first);
          console.info(
            "[recommend/show] second parse type:",
            Array.isArray(second) ? "Array" : typeof second
          );
          return Array.isArray(second) ? second : [];
        } catch (e2) {
          console.warn("[recommend/show] second parse failed", e2);
          return [];
        }
      }
      return [];
    } catch (e) {
      console.warn("[recommend/show] first parse failed", e);
      return [];
    }
  }

  const items = readItems();
  console.log("[recommend/show] items length:", items.length);

  // Toggle empty state
  if (!items.length && emptyEl) emptyEl.style.display = "block";
  else if (emptyEl) emptyEl.style.display = "none";

  if (!items.length) return;

  // Render cards
  const frag = document.createDocumentFragment();
  items.forEach((p) => {
    const cover = p.coverUrl || p.imageUrl || "/img/cover-fallback.png";
    const title = p.title || p.name || "Playlist";
    const owner = p.ownerName || "";
    const total = p.totalTracks ?? null;
    const ext = p.externalUrl || "";
    const extId = p.externalId || p.id || "";
    const snap = p.snapshotId || "";

    const card = document.createElement("div");
    card.className = "card tile";
    card.innerHTML = `
      <img src="${cover}" alt="${escapeHtml(title)}">
      <div class="card__meta">
        <h3>${escapeHtml(title)}</h3>
        <small class="muted">${escapeHtml(owner)}</small>
        ${total != null ? `<small class="muted">${total} tracks</small>` : ``}
      </div>

      <button class="heart-btn"
              aria-pressed="false"
              aria-label="Thích playlist"
              title="Thích playlist"
              data-external-id="${extId}"
              data-title="${escapeHtml(title)}"
              data-external-url="${ext}"
              data-cover-url="${cover}"
              data-owner-name="${escapeHtml(owner)}"
              ${total != null ? `data-total-tracks="${total}"` : ``}
              data-snapshot-id="${snap}">
        <svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
          <path class="heart-outline" d="M12.1 8.64l-.1.1-.11-.11C10.14 6.87 7.1 7.3 5.6 9.28c-1.23 1.63-1.05 3.92.43 5.35L12 20l5.97-5.37c1.48-1.43 1.66-3.72.43-5.35-1.5-1.98-4.54-2.41-6.3-.64z"
                fill="none" stroke="currentColor" stroke-width="1.8" />
          <path class="heart-fill" d="M12 21l-1.45-1.32C5.4 14.36 2 11.28 2 7.5 2 5.01 4.01 3 6.5 3c1.74 0 3.41.81 4.5 2.09C12.59 3.81 14.26 3 16 3 18.49 3 20.5 5.01 20.5 7.5c0 3.78-3.4 6.86-8.05 12.18L12 21z"
                fill="currentColor"/>
        </svg>
      </button>

      <button class="open-spotify" data-url="${ext}" aria-label="Mở trên Spotify" title="Mở trên Spotify">
        <svg width="18" height="18" viewBox="0 0 24 24" aria-hidden="true">
          <path d="M7 7h10v10M17 7L7 17" fill="none" stroke="currentColor" stroke-width="2" />
        </svg>
      </button>
    `;
    frag.appendChild(card);
  });
  listEl.appendChild(frag);
  console.log("[recommend/show] appended cards count:", listEl.children.length);

  // Delegate open-spotify
  document.addEventListener(
    "click",
    function (e) {
      const btn = e.target.closest(".open-spotify");
      if (!btn) return;
      e.stopPropagation();
      const url = btn.getAttribute("data-url");
      if (url) window.open(url, "_blank", "noopener");
    },
    { passive: true }
  );

  function escapeHtml(str) {
    return String(str || "")
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#039;");
  }
})();
