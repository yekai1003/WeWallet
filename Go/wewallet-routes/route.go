package routes

import (
	//"crypto/sha256"
	//"errors"
	"fmt"
	"net/http"

	//"os"
	//"strconv"

	"go-wewallet/dbs"
	"go-wewallet/eths"
	"go-wewallet/utils"

	"go-wewallet/configs"

	"github.com/gorilla/sessions"
	"github.com/labstack/echo"
	"github.com/labstack/echo-contrib/session"
)

func PingHandler(c echo.Context) error {

	return c.String(http.StatusOK, "pong")
}

//注册功能
func Register(c echo.Context) error {
	//1. 响应数据结构初始化
	var resp utils.Resp
	resp.Errno = utils.RECODE_OK
	defer utils.ResponseData(c, &resp)

	//2. 解析数据
	account := &dbs.Account{}
	if err := c.Bind(account); err != nil {
		fmt.Println(account)
		resp.Errno = utils.RECODE_PARAMERR
		return err
	}
	//3. 操作geth创建账户
	address, err := eths.NewAcc(account.IdentityID, configs.Config.Eth.Connstr)
	if err != nil {
		resp.Errno = utils.RECODE_IPCERR
		return err
	}
	//4. 操作mysql-插入数据
	//sql:insert into account(email,username,identity_id,address) values('1','2','3','4')
	sql := fmt.Sprintf("insert into account(email,username,identity_id,address) values('%s','%s','%s','%s')",
		account.Email,
		account.UserName,
		account.IdentityID,
		address,
	)
	fmt.Println(sql)
	_, err = dbs.Create(sql)
	if err != nil {
		fmt.Println("failed to insert account", err)
		resp.Errno = utils.RECODE_DBERR
		return err
	}
	//5. session处理
	sess, _ := session.Get("session", c)
	sess.Options = &sessions.Options{
		Path:     "/",
		MaxAge:   86400 * 7,
		HttpOnly: true,
	}
	sess.Values["address"] = address
	sess.Save(c.Request(), c.Response())

	//助记词、私钥提供
	return nil
}

//登陆功能
func Login(c echo.Context) error {
	//1. 响应数据结构初始化
	var resp utils.Resp
	resp.Errno = utils.RECODE_OK
	defer utils.ResponseData(c, &resp)

	//2. 解析数据
	account := &dbs.Account{}
	if err := c.Bind(account); err != nil {
		fmt.Println(account)
		resp.Errno = utils.RECODE_PARAMERR
		return err
	}

	//3. 操作mysql-查询数据
	//sql:select * from account where username ='yekai' and identity_id='yekai';
	sql := fmt.Sprintf("select * from account where username ='%s' and identity_id='%s'",
		account.UserName,
		account.IdentityID,
	)
	fmt.Println(sql)
	m, n, err := dbs.DBQuery(sql)
	if err != nil || n <= 0 {
		fmt.Println("failed to query account", err)
		resp.Errno = utils.RECODE_DBERR
		return err
	}
	//m 是一个[]map[string]string
	rows := m[0]
	//4. session处理
	sess, _ := session.Get("session", c)
	sess.Options = &sessions.Options{
		Path:     "/",
		MaxAge:   86400 * 7,
		HttpOnly: true,
	}
	sess.Values["address"] = rows["address"]
	sess.Save(c.Request(), c.Response())
	return nil
}

//找回密码
func Retrieve(c echo.Context) error {
	//1. 响应数据结构初始化
	var resp utils.Resp
	resp.Errno = utils.RECODE_OK
	defer utils.ResponseData(c, &resp)
	//查询数据库
	
	
	
	
		
}

//session获取
func GetSession(c echo.Context) error {
	var resp utils.Resp
	resp.Errno = utils.RECODE_OK
	defer utils.ResponseData(c, &resp)
	//处理session
	sess, err := session.Get("session", c)
	if err != nil {
		fmt.Println("failed to get session")
		resp.Errno = utils.RECODE_SESSIONERR
		return err
	}
	address := sess.Values["address"]
	if address == nil {
		fmt.Println("failed to get session,address is nil")
		resp.Errno = utils.RECODE_SESSIONERR
		return err
	}
	return nil
}
