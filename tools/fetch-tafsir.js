const fs = require('fs');
const crypto = require('crypto');

function strip(html) {
  return html
    .replace(/<script[\s\S]*?<\/script>/gi, ' ')
    .replace(/<style[\s\S]*?<\/style>/gi, ' ')
    .replace(/<br\s*\/?>/gi, '\n')
    .replace(/<\/(p|div|li|h\d|tr)>/gi, '\n')
    .replace(/<[^>]+>/g, ' ')
    .replace(/&nbsp;/g, ' ')
    .replace(/&zwnj;/g, '‌')
    .replace(/&laquo;/g, '«').replace(/&raquo;/g, '»')
    .replace(/&amp;/g, '&').replace(/&quot;/g, '"').replace(/&#039;/g, "'")
    .replace(/&lt;/g, '<').replace(/&gt;/g, '>')
    .replace(/&#\d+;/g, m => String.fromCharCode(+m.slice(2, -1)))
    .replace(/[ \t]+/g, ' ')
    .split('\n').map(l => l.trim()).filter(Boolean).join('\n')
    .replace(/\n{3,}/g, '\n\n')
    .trim();
}

// «جلد 9 - صفحه 419» و ارجاعات پانویس عددی، بقایای صفحه‌بندی کتاب چاپی‌اند
function clean(text) {
  return text
    .split('\n')
    .filter(l => !/^جلد\s*\d+\s*-\s*صفحه\s*\d+$/.test(l))
    .filter(l => l !== 'تفسیر نور (محسن قرائتی)')
    .join('\n')
    .replace(/\n{3,}/g, '\n\n')
    .trim();
}

function extractTafsir(html) {
  const start = html.indexOf('<div class="tafasir-tab-content">');
  if (start === -1) return null;
  // خواندن تا بسته‌شدن متوازن همان div
  let i = html.indexOf('>', start) + 1;
  let depth = 1;
  const re = /<\/?div\b[^>]*>/gi;
  re.lastIndex = i;
  let m;
  while ((m = re.exec(html))) {
    depth += m[0].startsWith('</') ? -1 : 1;
    if (depth === 0) return html.slice(i, m.index);
  }
  return null;
}

const sleep = ms => new Promise(r => setTimeout(r, ms));

(async () => {
  const perAyah = {};
  const sections = new Map(); // hash -> {text, ayat:[]}

  for (let n = 1; n <= 96; n++) {
    const url = 'https://wiki.ahlolbait.com/' + encodeURIComponent(`آیه_${n}_سوره_واقعه`);
    let ok = false;
    for (let attempt = 0; attempt < 3 && !ok; attempt++) {
      try {
        const r = await fetch(url, { headers: { 'User-Agent': 'Mozilla/5.0 Chrome/125' } });
        if (!r.ok) { console.log(`ayah ${n}: HTTP ${r.status}`); await sleep(1500); continue; }
        const html = await r.text();
        const inner = extractTafsir(html);
        if (!inner) { console.log(`ayah ${n}: no tafsir div`); ok = true; break; }
        const text = clean(strip(inner));
        if (!text) { console.log(`ayah ${n}: empty`); ok = true; break; }
        const h = crypto.createHash('sha1').update(text).digest('hex').slice(0, 12);
        if (!sections.has(h)) sections.set(h, { text, ayat: [] });
        sections.get(h).ayat.push(n);
        perAyah[n] = h;
        ok = true;
      } catch (e) {
        console.log(`ayah ${n}: ERR ${e.message}`);
        await sleep(2000);
      }
    }
    if (n % 12 === 0) console.log(`... ${n}/96 (${sections.size} distinct sections so far)`);
    await sleep(350);
  }

  const out = {
    source: 'تفسیر نور، محسن قرائتی، مرکز فرهنگی درسهایی از قرآن، چاپ یازدهم ۱۳۸۳ — گردآوری از دانشنامه اسلامی (wiki.ahlolbait.com)',
    sections: [...sections.values()].map(s => ({ ayat: s.ayat, text: s.text })),
    perAyah,
  };
  out.sections.sort((a, b) => a.ayat[0] - b.ayat[0]);

  fs.writeFileSync('tafsir_noor.json', JSON.stringify(out, null, 1), 'utf8');
  console.log(`\ndone: ${out.sections.length} sections, ${Object.keys(perAyah).length}/96 ayat covered`);
  console.log('coverage gaps:', [...Array(96)].map((_, i) => i + 1).filter(n => !perAyah[n]).join(', ') || 'none');
})();
