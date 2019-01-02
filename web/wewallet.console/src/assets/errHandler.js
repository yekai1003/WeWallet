
export default {
  handle (page, err) {
    if (err.errCode) {
      page.$message({
        message: err.errMsg,
        type: 'error'
      })
    } else {
      page.$message({
        message: '服务器异常',
        type: 'error'
      })
    }
  }
}
