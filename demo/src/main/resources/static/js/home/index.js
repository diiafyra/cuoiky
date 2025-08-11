// Explore: chỉ hiện 2 hàng, bấm "Xem thêm" để mở rộng
(() => {
  const grid = document.getElementById('exploreGrid');
  const moreBtn = document.getElementById('exploreMore');
  if (!grid || !moreBtn) return;

  const layout = () => {
    const styles = getComputedStyle(grid);
    let cols = styles.gridTemplateColumns.split(' ').length;
    if (styles.gridTemplateColumns === 'none') cols = 1;

    const maxRows = 2;
    const maxItems = cols * maxRows;

    const items = Array.from(grid.children);
    let hidden = 0;
    const showAll = grid.classList.contains('show-all');

    items.forEach((el, i) => {
      const hide = !showAll && i >= maxItems;
      el.classList.toggle('is-hidden', hide);
      if (hide) hidden++;
    });

    if (items.length > maxItems || showAll) {
      moreBtn.style.display = 'inline-flex';
      moreBtn.textContent = showAll ? 'Thu gọn' : 'Xem thêm';
    } else {
      moreBtn.style.display = 'none';
    }
  };

  moreBtn.addEventListener('click', () => {
    grid.classList.toggle('show-all');
    layout();
  });

  let rid = null;
  window.addEventListener('resize', () => {
    cancelAnimationFrame(rid);
    rid = requestAnimationFrame(layout);
  });

  layout();
})();
