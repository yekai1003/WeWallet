package eths

import (
	"fmt"
	"math/big"

	"go-wewallet/configs"
	"go-wewallet/utils"

	"os"

	"github.com/ethereum/go-ethereum/accounts/abi/bind"
	"github.com/ethereum/go-ethereum/common"
	"github.com/ethereum/go-ethereum/ethclient"
	"github.com/ethereum/go-ethereum/rpc"
)

//创建一个账户
func NewAcc(pass, connstr string) (string, error) {
	//连接到geth
	client, err := rpc.Dial(connstr)
	if err != nil {
		fmt.Println("failed to connect to geth", err)
		return "", err
	}

	//创建账户
	var account string
	err = client.Call(&account, "personal_newAccount", pass)
	if err != nil {
		fmt.Println("failed to create acc", err)
		return "", err
	}
	fmt.Println("acc==", account)
	return account, nil
}

//erc20转账 from:buyer,to:发起拍卖人(mysql)
func EthTransfer20(from, pass, seller string, price int64) error {
	///1. dial
	cli, err := ethclient.Dial(configs.Config.Eth.Connstr)
	if err != nil {
		fmt.Println("failed to conn to geth", err)
		return err
	}
	//2. 入口
	instance, err := NewPxc(common.HexToAddress(configs.Config.Eth.PxcAddr), cli)
	if err != nil {
		fmt.Println("failed to cNewCallabi", err)
		return err
	}
	//3. 设置签名 -- 需要owner的keystore文件
	//需要获得文件名字
	fileName, err := utils.GetFileName(string([]rune(from)[2:]), configs.Config.Eth.Keydir)
	if err != nil {
		fmt.Println("failed to GetFileName", err)
		return err
	}

	file, err := os.Open(configs.Config.Eth.Keydir + "/" + fileName)
	if err != nil {
		fmt.Println("failed to open file ", err)
		return err
	}
	auth, err := bind.NewTransactor(file, pass)
	if err != nil {
		fmt.Println("failed to NewTransactor  ", err)
		return err
	}
	//4. 调用
	//Transfer(opts *bind.TransactOpts, _to common.Address, _value *big.Int)
	_, err = instance.Transfer(auth, common.HexToAddress(seller), big.NewInt(price))
	if err != nil {
		fmt.Println("failed to Transfer20  ", err)
		return err
	}
	return nil
}
