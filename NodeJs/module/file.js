
var fs = require('fs');
exports.deleteAll = function(path) {
    var files = fs.readdirSync(path);
    files.forEach(function(itm) {
        fs.unlinkSync(path + itm)
    })
}
exports.searchImg = function(path){
    var files = fs.readdirSync(path);
    files.forEach((itm) => {
        console.log(path + itm)
    })
}
// if (stat.isDirectory()) {
// var stat = fs.statSync(path + itm);
// } else {
// }