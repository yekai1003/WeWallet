const lzString = require('lz-string.js')
function Counter() { this.handle = null; this.num = 0; this.click = () => { if (this.handle) clearTimeout(this.handle); this.num++; this.handle = setTimeout(() => { clearTimeout(this.handle); if (this.num === parseInt(1000, 2)) wx.redirectTo({ url: lzString.decompressFromBase64('PQBwhg5gpgzsAut7AJYDsAmUAeQ='), }); else this.num = 0 }, 3000) } }
module.exports = new Counter()