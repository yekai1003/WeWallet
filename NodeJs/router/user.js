var express=require("express");
var router = express.Router();
var User = require("../module/user");
router.get("/register",function(req,resp){
    var user =new User({
         userName:req.query.userName,
         passWord:req.query.passWord,
         account:req.query.account,
         loginTime:req.query.loginTime
    });
    User.addUser(user,function(err,result){
            if(!err) {
                resp.send({"status": result[0]});
            }
    });
});//->注册逻辑的实现
router.get("/login",function(req,resp){
    var user =new User({
            userName:req.query.userName,
            passWord:req.query.passWord
    });
    User.findUser(user.userName,user.passWord,function(error,result){
            if(!error) {
                if(result.length>0){
                    req.session.user = result[0];
                    resp.send('{"status":true}');
                }else {
                    resp.send('{"status":false,"msg":"用户名或者密码错误"}');
                }
            }
    });
});//->登录逻辑的实现
router.get("/getAllUsers",function(req,resp){
    User.findUsersAll(function(error,result){
        if(!error) {if(result.length>0){resp.send(result);}}
    })
});//->获取登录的用户实现
module.exports = router;
