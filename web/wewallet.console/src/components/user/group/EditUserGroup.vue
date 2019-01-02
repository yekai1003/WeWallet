<template>
  <div style="margin:0 60px 0 20px">
    <el-form ref="form" label-width="60px" size="mini">

      <el-form-item label="组名：">
        <el-input v-model="userGroup.groupName"></el-input>
      </el-form-item>
      <el-form-item label="描述：">
        <el-input v-model="userGroup.description"></el-input>
      </el-form-item>
      <!--我要在这里放一个 公司、api、客户端的选项卡-->

      <el-form-item label="权限：">
        <el-tabs v-model="currTab" @tab-click="handleClick">
          <el-tab-pane label="客户端" name="second">
            <el-checkbox-group v-if="clients" v-model="userGroup.clients">
              <div v-for="client in clients" :key="client.id" style="float:left;margin:0 20px">
                <el-checkbox :label="client.id" @change="updateGroupClient($event,client)">{{client.clientName}}
                </el-checkbox>
              </div>
            </el-checkbox-group>
            <div style="clear:both"></div>
          </el-tab-pane>

          <el-tab-pane label="API" name="third">
            <el-checkbox-group v-if="apis" v-model="userGroup.apis">
              <div v-for="api in apis" :key="api.id" style="float:left;margin:0 20px">
                <el-checkbox :label="api.id" @change="updateGroupApi($event,api)">{{api.description}}</el-checkbox>
              </div>
            </el-checkbox-group>
          </el-tab-pane>
        </el-tabs>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit">提交</el-button>
        <el-button @click="cancel" style="margin-left:40px;">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
import {mapActions} from 'vuex'
import errHandler from '@/assets/errHandler.js'
import ElCheckboxGroup from '../../../../node_modules/element-ui/packages/checkbox/src/checkbox-group.vue'
import ElCheckbox from '../../../../node_modules/element-ui/packages/checkbox/src/checkbox.vue'
import ElFormItem from '../../../../node_modules/element-ui/packages/form/src/form-item.vue'

export default {
  components: {
    ElFormItem,
    ElCheckbox,
    ElCheckboxGroup
  },
  data () {
    return {
      apiList: null,
      companies: null,
      currTab: 'second',
      totalPage: 0
    }
  },
  props: ['userGroup'],
  computed: {},
  created () {
    // 加载客户端，api，用户组现有权限

    // 2、加载所有api
    this.getApiList()
    // 3、加载所有客户端
    this.fetch({name: 'getClientList'}).then(res => {
      if (res.errCode) {
        this.$message({type: 'error', message: res.errMsg})
      } else {
        // 处理下
        console.log(res)
        this.clients = res
      }
    }).catch(err => {
      console.log(err)
      this.$message({type: 'error', message: '服务器错误'})
    })
  },
  methods: {
    ...mapActions({
      fetch: 'fetch'
    }),
    getApiList (pageNo) {
      this.fetch({name: 'getApiList', data: {page: pageNo || 0}})
        .then(res => {
          console.log('获取api——————————', res)
          this.apis = res.content
          this.totalPage = res.totalPages
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    handleClick (tab, event) {
      if (tab.label === '公司') { // 调用/core/usergroup/setUserCompany 获取公司列表 多选

      } else if (tab.label === '客户端') { // 调用/core/usergroup/setUserClient 获取客户端列表 多选

      } else if (tab.label === 'API') { // 调用/core/usergroup/setUserApi 获取API列表 多选

      }
    },
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
    updateGroupCompany (event, company) {
      console.log(event)
      let value
      if (event) value = 1
      else value = 0
      console.log(value)
      this.$store.dispatch({
        type: 'fetch',
        name: 'setUserGroupCompany',
        method: 'post',
        data: {
          companyId: company.id,
          type: value,
          groupId: this.userGroup.id
        }
      }).then(res => {
        if (res.errCode) {
          this.$message({
            message: res.errMsg,
            type: 'error'
          })
        } else {
          console.log(res)
        }
      }).catch(error => {
        this.$message({
          message: error,
          type: 'error'
        })
      })
    },
    updateGroupClient (event, client) {
      console.log(event)
      let value
      if (event) value = 1
      else value = 0
      console.log(value)
      this.$store.dispatch({
        type: 'fetch',
        name: 'setUserGroupClient',
        method: 'post',
        data: {
          clientId: client.id,
          type: value,
          groupId: this.userGroup.id
        }
      }).then(res => {
        if (res.errCode) {
          this.$message({
            message: res.errMsg,
            type: 'error'
          })
        } else {
          console.log(res)
        }
      }).catch(error => {
        this.$message({
          message: error,
          type: 'error'
        })
      })
    },
    updateGroupApi (event, api) {
      console.log(event)
      let value
      if (event) value = 1
      else value = 0
      console.log(value)
      this.$store.dispatch({
        type: 'fetch',
        name: 'setUserGroupApi',
        method: 'post',
        data: {
          apiId: api.id,
          type: value,
          groupId: this.userGroup.id
        }
      }).then(res => {
        if (res.errCode) {
          this.$message({
            message: res.errMsg,
            type: 'error'
          })
        } else {
          console.log(res)
        }
      }).catch(error => {
        this.$message({
          message: error,
          type: 'error'
        })
      })
    },
    submit () {
      console.log(this.userGroup)
      if (this.validateForm(this.userGroup)) {
        this.$store.dispatch({
          type: 'fetch',
          name: 'updateUserGroup',
          data: {
            id: this.userGroup.id,
            groupName: this.userGroup.groupName,
            description: this.userGroup.description
          }
        }).then(res => {
          if (res.errCode) {
            console.log(res)
            this.$message({
              message: res.errMsg,
              type: 'error'
            })
          } else {
            this.$message({
              message: '更新成功',
              type: 'success'
            })
            this.cancel()
          }
        }).catch(error => {
          this.$message({
            message: '连不上服务器',
            type: 'error'+error
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
