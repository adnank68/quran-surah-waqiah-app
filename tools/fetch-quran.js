const fs = require('fs');

const EDITIONS = {
  uthmani: 'quran-uthmani',
  ansarian: 'fa.ansarian',
  ghomshei: 'fa.ghomshei',
  gharaati: 'fa.gharaati',
  makarem: 'fa.makarem',
};

const BISMILLAH = 'بِسْمِ ٱللَّهِ ٱلرَّحْمَـٰنِ ٱلرَّحِيمِ';

async function get(edition) {
  const r = await fetch(`https://api.alquran.cloud/v1/surah/56/${edition}`);
  const j = await r.json();
  if (j.code !== 200) throw new Error(edition + ' -> ' + j.status);
  return j.data;
}

(async () => {
  const out = {};
  for (const [key, ed] of Object.entries(EDITIONS)) {
    const d = await get(ed);
    out[key] = d.ayahs.map(a => a.text);
    console.log(key, 'count=', d.ayahs.length, 'name=', d.englishName);
  }

  // آیه‌ی اول در نسخه‌ی عثمانی «بسم الله» را چسبیده دارد؛ جدایش می‌کنیم
  console.log('\n--- ayah1 raw uthmani ---');
  console.log(JSON.stringify(out.uthmani[0]));
  console.log('--- ayah1 ansarian ---');
  console.log(JSON.stringify(out.ansarian[0]));
  console.log('--- ayah96 uthmani ---');
  console.log(JSON.stringify(out.uthmani[95]));

  fs.writeFileSync('raw.json', JSON.stringify(out, null, 2), 'utf8');
  console.log('\nwrote raw.json');
})();
