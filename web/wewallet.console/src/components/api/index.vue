<template>
  <section v-loading="loading">
    <div id="top">
      <el-button circle icon="el-icon-caret-top" @click="toTop" class="top">
      </el-button>
    </div>
    <el-row>
      <el-tabs v-model="apiPage" @tab-click="selApiPage">
        <el-tab-pane label="所有API" name="allApi">
          <div id="api-manager">
            <!--Show api list-->
            <el-row v-if="!currentOption">
              <ApiList v-on:triggerAddApi="addApi" v-on:triggerEditApi="editApi"></ApiList>
            </el-row>
            <!--Create api form-->
            <el-row v-if="currentOption==='addApi'">
              <NewApi
                v-on:triggerSubmitNewApi="submitNewApi"
                v-on:triggerReset="resetComponentData"
              ></NewApi>
            </el-row>
            <!--Edit api form-->
            <el-row v-if="currentOption==='editApi'">
              <EditApi
                v-bind:api="editingApi"
                v-on:triggerSubmitEditApi="submitEditedApi"
                v-on:triggerReset="resetComponentData"
              ></EditApi>
            </el-row>
          </div>
        </el-tab-pane>
        <el-tab-pane :label="unit.label" :name="unit.name" v-for="unit in units" :key="unit.name" v-loading="loading">
          <newList :currApiList="currApiList" :currTab="unit.name" v-if="apiPage === unit.name"></newList>
        </el-tab-pane>
      </el-tabs>
    </el-row>
  </section>
</template>

<script>
import {mapGetters, mapActions, mapMutations} from 'vuex'
import Vue from 'vue'// 后续添加属性，需要vue联动
import errHandler from '@/assets/errHandler'
import newList from './newList'
import ApiList from './ApiList'
import NewApi from './NewApi'
import EditApi from './EditApi'
export default {
  components: {
    ApiList, NewApi, EditApi, newList
  },
  name: 'api-manager',
  data () {
    return {
      totalPage: 0,
      loading: false,
      units: [
        {name: 'user', label: '用户'},
        {name: 'api', label: 'API'},
        {name: 'authorize', label: '授权'},
        {name: 'power', label: '权限管理'},
        {name: 'client', label: '客户端管理'},
        {name: 'usergroup', label: '用户组'},
        {name: 'core', label: '钱包核心'}
      ],
      currApiList: [],
      apiPage: 'allApi',
      newApi: {
        uri: '',
        description: '',
        requirements: [],
        needAuthorization: true,
        isEnable: true
      },
      activeTab: 'api',
      editingApi: {
        uri: '',
        description: '',
        requirements: [],
        needAuthorization: false,
        isEnable: false
      },
      currentOption: undefined,
      requirementsOptions: [
        {value: 'user_id', label: 'user_id'},
        {value: 'token', label: 'token'},
        {value: 'clientid', label: 'clientid'}
      ]
    }
  },
  created () {
    // this.requestApiList()
    this.loading = true
    this.getApi()
  },
  computed: {
    apiList: function () {
      return this.$store.getters.apiList
    }
  },
  watch: {
    apiPage (nv) {}
  },
  methods: {
    ...mapActions({
      fetch: 'fetch'
    }),
    getApi (pageNo) {
      this.fetch({name: 'getApiList', data: {page: pageNo || 0}})
        .then(res => {
          console.log(res)
          this.getApiList(res.totalElements)
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    getApiList (size) {
      this.fetch({name: 'getApiList', data: {size}})
        .then(res => {
          console.log(res)
          // this.apiList=res.content
          this.loading = false
          this.$store.commit({
            type: 'setApiList',
            apiList: res.content
          })
          console.log('index—————获取api列表—————————————')
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    toTop () {
      console.log(document)
      console.log(document.documentElement)
      document.body.scrollTop = 0
      document.documentElement.scrollTop = 0
    },
    selApiPage (tab, event) {
      console.log(tab.name)
      if (tab.name === 'allApi') return
      this.getCurrApiList(tab.name)
    },
    getCurrApiList (n) {
      this.currApiList = []
      this.apiList.forEach(r => {
        let str = r.uri
        let i = str.indexOf('/', 1)
        let ns = str.substring(1, i)
        if (n === ns) {
          this.currApiList.push(r)
        }
      })
    },
    requestApiList (pageNo) {
      this.$store.dispatch({
        type: 'fetch',
        name: 'getApiList',
        data: {page: pageNo || 0},
        method: 'post'
      }).then(res => {
        console.log('————获取api列表————————', res)
        if (res.errCode) {
          this.$message({
            message: res.errMsg,
            type: 'error'
          })
        } else {
          this.$store.commit({
            type: 'setApiList',
            apiList: res.content
          })
          this.totalPage = res.totalPages
        }
      }).catch(error => {
        this.$message({
          message: '连不上服务器',
          type: 'error'
        })
      })
    },
    // Adding new api
    addApi () {
      this.currentOption = 'addApi'
    },
    submitNewApi (form) {
      console.log('——————————————————看一下！', form)
      if (this.checkForm(form)) {
        // Submit api
        const request = {
          uri: form.uri,
          description: form.description,
          requirements: form.requirements,
          needAuthorization: form.needAuthorization,
          isPublic: form.isPublic,
          isEnable: form.isEnable
        }
        this.$store.dispatch({
          type: 'fetch',
          name: 'createApi',
          data: {
            ...request
          },
          method: 'post'
        }).then(res => {
          if (res.errCode) {
            this.$message({
              message: res.errMsg,
              type: 'error'
            })
          } else {
            this.$message({
              message: '提交成功',
              type: 'success'
            })
            // this.getApi()
            this.resetComponentData()
            this.flashServer()
          }
        }).catch(error => {
          this.$message({
            message: error,
            type: 'error'
          })
          this.resetComponentData()
        })
      }
    },
    flashServer () {
      console.log(this.$store)
      this.$store.dispatch({
        type: 'fetch',
        name: 'flashServer'
      })
    },
    // Editing api
    editApi (api) {
      if (!api.requirements) api.requirements = []
      this.editingApi = JSON.parse(JSON.stringify(api))
      this.currentOption = 'editApi'
    },
    submitEditedApi (form) {
      console.log(form)
      if (this.checkForm(form)) {
        // Submit api
        let request
        this.apiList.map(api => {
          if (api.id === form.id) {
            request = this.requestParser(api, form)
          }
        })
        this.$store.dispatch({
          type: 'fetch',
          name: 'updateApi',
          data: {...request},
          method: 'post'
        }).then(res => {
          if (res.errCode) {
            this.$message({message: res.errMsg, type: 'error'})
          } else {
            this.$message({message: '提交成功', type: 'success'})
            this.flashServer()
            this.requestApiList()
            this.resetComponentData()
          }
        }).catch(error => {
          this.$message({message: error, type: 'error'})
          this.resetComponentData()
        })
      }
    },
    // Utilities
    checkForm (form) {
      if (!form.uri) {
        this.$message({
          message: '接口地址不能为空',
          type: 'error'
        })
        return false
      }
      if (!form.description) {
        this.$message({
          message: '接口描述不能为空',
          type: 'error'
        })
        return false
      }
      return true
    },
    requestParser (original, edited) {
      let data = {}
      data.id = original.id
      if (original.uri !== edited.uri) data.uri = edited.uri
      if (original.description !== edited.description) data.description = edited.description
      if (original.requirements !== edited.requirements) data.requirements = edited.requirements
      if (original.needAuthorization !== edited.needAuthorization) data.needAuthorization = edited.needAuthorization
      if (original.isPublic !== edited.isPublic) data.isPublic = edited.isPublic
      if (original.isEnable !== edited.isEnable) data.isEnable = edited.isEnable
      return data
    },
    resetComponentData () {
      this.editingApi = {
        uri: '',
        description: '',
        requirements: [],
        needAuthorization: false,
        isPublic: false,
        isEnable: false
      }
      this.newApi = {
        uri: '',
        description: '',
        requirements: [],
        needAuthorization: true,
        isPublic: false,
        isEnable: true
      }
      this.currentOption = undefined
      this.getApi()
    }
  }
}
</script>

<style lang="scss">
  #api-manager{
    .button-container{
      margin-top: 15px;
      float: right;
    }
  }
  #top{
    position: fixed;
    right: 20px;
    bottom: 50px;
    width: 50px;
    height: 50px;
    z-index: 1000;
    opacity:0.7
  }
  #top:hover{
    opacity:1
  }

</style>
