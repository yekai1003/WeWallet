var express = require('express');
var path = require('path');
var app = express();
//自己编写的文件(router)
var user=require("./router/user");
var file=require("./router/file");
app.use(express.static(path.join(__dirname, 'public')));
app.use("/user",user);
app.use("/file",file);
app.use(function (req, res, next) {
    var err = new Error('Not Found');
    err.status = 404;
    next(err);
});
if (app.get('env') === 'development') {
    app.use(function (err, req, res, next) {
        res.status(err.status || 500);
        res.send({
            message: err.message,
            error: err
        });
    });
}
app.use(function (err, req, res, next) {
    res.status(err.status || 500);
    res.send({
        message: err.message,
        error: err
    });
});
//普通变量暴露exports.msg = msg;
module.exports = app;