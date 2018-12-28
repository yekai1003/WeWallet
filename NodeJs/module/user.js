var db = require("./db");
function User (user) {
      this.id = user.id;
      this.userName = user.userName;
      this.account = user.account;
      this.state = user.state;
      this.loginTime = user.loginTime;
      this.passWord = user.passWord;
      this.exitTime = user.exitTime;
}
//查找用户
User.findUser =function (userName,password,callback) {
var selectSql = "select * from account where userName =?  and password = ?";
      db.query(selectSql,[userName,password],function(error,result){
      if(error) {return callback(error);}
      callback(error,result);});
}
//注册功能addUser
User.addUser = function (user,callback) {
      var selectSql = "insert into user (state,password,account,loginTime) values (?,?,?)";
      db.query(selectSql,[1,user.password,user.account,user.loginTime],function(error,result){
                if(error) {
                       return callback(error);
                }
                callback(error,result);
      });
}
//查看登录的用户findUsersAll
User.findUsersAll =function (callback) {
var selectSql = "select * from account where state = 1";
        db.query(selectSql,[],function(error,result){
        if(error) {return callback(error);}
        callback(error,result);
    });
}
module.exports = User;