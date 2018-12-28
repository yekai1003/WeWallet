var express = require('express'),
    formidable = require('formidable'),
    fs = require('fs'),
    router = express.Router(),
    image = require("imageinfo");
function readFileList(path, filesList) {
    fs.readdirSync(path).forEach(function (itm, index) {
        var stat = fs.statSync(path + itm);
        if (stat.isDirectory()) {
            readFileList(path + itm + "/", filesList)
        } else {
            filesList.push(path+itm);
        }
    })
}
router.get('/getFileList',function(req,res){
//获取文件夹下的所有文件
    var filesList = [];
    readFileList(req.query.path, filesList);
    res.send(filesList);
});
router.get('/getImageFiles',function(req,res){
    //获取文件夹下的所有图片imageList.push("img/"+item);
    var imageList = [],ms = req.query.path;
    fs.readdirSync(ms).forEach((item) => {
        image(fs.readFileSync(ms + item)).mimeType && (imageList.push("img/emoji/"+item))
    });
    res.send(imageList);
});

router.post('/files',function (req, res) {
    var form = new formidable.IncomingForm();
    form.encoding = 'utf-8';
    form.uploadDir = "public/up/";
    form.keepExtensions = true;
    form.maxFieldsSize = 2097152;//文件小于2M,服务器还不够大
    //form.maxFields = 1000;  设置所以文件的大小总和console.log(files.fileName.path);
    form.parse(req, function (err, fields, files) {
        if(files.fileName&&files.fileName.path){
            res.send({ "picName": files.fileName.path});
        }else{res.send({ "error":" bug "});}
    });
});
module.exports=router;