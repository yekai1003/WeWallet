<template>
  <section>
    <el-form :rules="rules" ref="memberForm" :model="memberForm" label-width="180px" size="mini">
      <el-form-item label="账户名：" prop="username">
        <el-input size="mini" v-model="memberForm.username" style="width: 200px"></el-input>
      </el-form-item>
      <el-form-item label="用户组：" prop="groupId">
        <el-select v-model="memberForm.groupId">
          <el-option v-for="group in groupList" :key="group.id" :value="group.id" :label="group.groupName"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="登录名：" prop="loginname">
        <el-input size="mini" v-model="memberForm.loginname" style="width: 200px"></el-input>
      </el-form-item>
      <el-form-item label="密码：" prop="loginpwd">
        <el-input size="mini" v-model="memberForm.loginpwd" style="width: 200px"></el-input>
      </el-form-item>
      <el-form-item label="手机号：" prop="phone">
        <el-input size="mini" v-model="memberForm.phone" style="width: 200px"></el-input>
      </el-form-item>
      <el-form-item label=" ">
        <el-button type="primary" @click="createMemberBtn('memberForm')" size="mini">立即创建</el-button>
        <el-button @click="resetForm('memberForm')" size="mini">重置</el-button>
      </el-form-item>
    </el-form>
  </section>
</template>

<script>
import errHandler from '@/assets/errHandler'
import {mapActions} from 'vuex'
export default {
  name: 'createMemberDialog',
  props: ['member'],
  data () {
    return {
      groupList: null,
      memberForm: {
        username: '', groupId: '', loginname: '', loginpwd: '', phone: ''
      },
      rules: {
        username: [
          {required: true, message: '请输入账户名', trigger: 'blur'},
          {min: 1, max: 10, message: '长度在 2 到 5 个字符', trigger: 'blur'}
        ],
        loginname: [
          {required: true, message: '请输入登录名', trigger: 'blur'}
        ],
        loginpwd: [
          {required: true, message: '请输入密码', trigger: 'blur'}
        ],
        groupId: [
          { required: true, message: '请选择用户组', trigger: 'change' }
        ],
        phone: [
          {required: false, message: '请输入手机号', trigger: 'blur'},
          {min: 11, max: 11, message: '长度为11', trigger: 'blur'}
        ]
      }
    }
  },
  created () {
    console.log(this.member)
    this.initForm()
    this.getUserGroupList()
  },
  methods: {
    ...mapActions({
      fetch: 'fetch'
    }),
    initForm () {
      if (this.member) {
        this.memberForm = {
          username: this.member.username, groupId: this.member.group.id, loginname: this.member.loginname, loginpwd: '', phone: ''
        }
      }
    },
    resetForm (memberForm) {
      this.$refs[memberForm].resetFields()
    },
    getUserGroupList () {
      this.fetch({name: 'getUserGroupList'})
        .then((res) => {
          console.log('用户组——————', res)
          this.groupList = res
        })
        .catch((err) => {
          console.log(err)
          this.$message({type: 'error', message: '服务器错误'})
        })
    },
    createMemberBtn (memberForm) {
      this.$refs[memberForm].validate((valid) => {
        if (valid) {
          // 这里做创建的事儿
          console.log(this.memberForm)
          this.createMember()
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    createMember () {
      this.fetch({name: 'createMember', data: {...this.memberForm}})
        .then(res => {
          // 创建成功，关掉对话框
          console.log(res)
          this.$message({type: 'sucess', message: '创建成功'})
          this.$emit('cancel', res)
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    }
  }
}
</script>

<style scoped>

</style>
