<template>
  <div>
    <el-form ref="form" label-width="120px">
      <el-form-item label="组名称">
        <el-input v-model="userGroup.groupName"></el-input>
      </el-form-item>

      <el-form-item label="描述">
        <el-input v-model="userGroup.description"></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="cancel">取消</el-button>
      <el-button type="primary" @click="submit">提交</el-button>
    </span>
  </div>

</template>

<script>
export default {
  props: ['userGroup'],
  methods: {
    validateForm (form) {
      let result
      if (!form.groupName) result = '组名称'
      if (!form.description) result = '描述'
      if (result) {
        this.$message({
          message: result + '不能为空',
          type: 'error'
        })
        return false
      } else {
        return true
      }
    },
    submit () {
      if (this.validateForm(this.userGroup)) {
        this.$store.dispatch({
          type: 'fetch',
          name: 'createUserGroup',
          data: {
            ...this.userGroup
          }
        }).then(res => {
          if (res.errCode) {
            this.$message({
              message: res.errMsg,
              type: 'error'
            })
          } else {
            console.log(res)
            this.$message({
              message: '添加成功',
              type: 'success'
            })
            this.cancel()
          }
        }).catch(error => {
          this.$message({
            message: '连不上服务器',
            type: 'error'
          })
        })
      }
    },
    cancel () {
      this.$emit('cancel')
    }
  }
}
</script>

<style>
  .avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }

  .avatar-uploader .el-upload:hover {
    border-color: #20a0ff;
  }

  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    line-height: 178px;
    text-align: center;
  }

  .avatar {
    width: 178px;
    height: 178px;
    display: block;
  }
</style>
