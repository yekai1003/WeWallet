/* 缩短字符串，显示两头 */
const formatShort = (src,pn,an) => {
  let ps = src.substr(0,pn)
  let as = src.substr(an)
  return ps+'...'+as
}
module.exports = {
  formatShort: formatShort
}
