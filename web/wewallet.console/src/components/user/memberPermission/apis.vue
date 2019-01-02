<template>
  <section>
    <el-row style="margin:20px">
      <el-collapse v-model="pageName" accordion>
        <el-collapse-item  :name="api.name" v-for="api in uiApi" :key="api.id" >
          <template slot="title">
            {{api.name}}
          </template>
          <el-row style="color: red;">* “全选”为整个项目卡，并非一个页面。</el-row>
          <el-row id="top">
            <i class="el-icon-upload2" @click="toTop">收起</i>
          </el-row>
          <el-row style="display: flex;flex-direction: row; flex-wrap:wrap;" v-if="pageName === api.name">
            <el-card v-for="(a,i) in api.children" :key="i" style="width:300px;margin: 10px;" v-loading="loading">
              <el-row slot="header">
                <span>{{a.label}}</span>
                <el-checkbox style="float: right; padding: 3px 0" v-model="a.checkAll" @change="checkAllBtn($event,a)" :indeterminate="a.checked">全选</el-checkbox>
              </el-row>
              <el-row v-if="a.children.length > 10">
                <apis-page :apis="a.children" :checkApis="checkApis" v-on:checkeBtn="checkeBtnEvent"></apis-page>
              </el-row>
              <el-row v-else>
                <div v-for="d in a.children" :key="d.id" style="height: 50px;line-height: 100px">
                  <el-checkbox-group v-model="checkApis" >
                    <el-checkbox :label="d.id"  @change="checkeBtn($event,d,a)">
                      <div>
                        <div>
                          {{d.label}}
                        </div>
                        <div>
                          {{cut(d.description)}}
                        </div>
                      </div>
                    </el-checkbox>
                  </el-checkbox-group>
                </div>
              </el-row>
            </el-card>
          </el-row>
        </el-collapse-item>
      </el-collapse>
    </el-row>
  </section>
</template>

<script>
import apisPage from './apisPage'
import Vue from 'vue'// 后续添加属性，需要vue联动
import errHandler from '@/assets/errHandler'
import {mapActions} from 'vuex'
export default {
  name: 'apis',
  props: ['member', 'group'],
  components: {apisPage},
  data () {
    return {
      pageName: '',
      loading: false,
      checkAll: false,
      checkApis: [],
      checkApi: [],
      isIndeterminate: true,
      checkAllApi: false,
      uiApi: [
        {label: 'user', name: '用户', children: []},
        {label: 'api', name: 'API', children: []},
        {label: 'authorize', name: '授权', children: []},
        {label: 'power', name: '权限管理', children: []},
        {label: 'client', name: '客户端管理', children: []},
        {label: 'usergroup', name: '用户组', children: []},
        {label: 'core', name: '钱包核心', children: []}
      ]
    }
  },
  watch: {
  },
  created () {
    console.log(this.group)
    if (this.group) {
      this.getUserGroup(this.group.id)
    }
    if (this.member) {
      this.initMember()
    }
    this.getApiList()
  },
  methods: {
    ...mapActions({
      fetch: 'fetch'
    }),
    toTop () {
      this.pageName = ''
    },
    getUserGroup (id) {
      this.fetch({name: 'getUserGroup', data: {id}})
        .then(res => {
          this.checkApis = res.apis
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    initMember () {
      if (this.member.userResource.apis === undefined) {
        Vue.set(this.member.userResource, 'apis', [])
      }
      this.getMember(this.member.id)
    },
    getMember (mid) {
      this.fetch({name: 'getMember', data: {mid}})
        .then(res => {
          this.checkApis = res.userResource.apis
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    getApiList (sizeNo) {
      this.fetch({name: 'getApiList', data: {size: sizeNo || null}})
        .then(res => {
          if (res.content.length < res.totalElements) {
            this.getApiList(res.totalElements)
            return
          }
          this.make(res.content)
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    checkAllBtn (event, apis) {
      let tmp = []
      apis.children.forEach(a => {
        let index = this.checkApis.findIndex(c => c === a.id)
        if (event && index < 0) {
          this.checkApis.push(a.id)
          tmp.push({type: 1, a: a})
          return
        }
        if (!event && index >= 0) {
          this.checkApis.splice(index, 1)
          tmp.push({type: 0, a: a})
        }
      })
      if (tmp.length > 0) {
        this.loading = true
        if (this.member) {
          this.updateMember(tmp)
        }
        if (this.group) {
          console.log('下面循环——————', tmp)
          this.updateGroup(tmp)
        }
      }
      apis.checked = false
    },
    updateGroup (array) {
      console.log('循环设置中——————')
      this.fetch({name: 'setUserGroupApi',
        data: {
          apiId: array[0].a.id,
          type: array[0].type,
          groupId: this.group.id
        }})
        .then(res => {
          console.log(res)
          array.splice(0, 1)
          if (array.length > 0) {
            this.updateGroup(array)
          } else {
            this.$message({type: 'success', message: '成功！'})
            this.loading = false
            this.getUserGroup(this.group.id)
          }
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    updateMember (array) {
      this.fetch({name: 'setUserApi',
        data: {
          apiId: array[0].a.id,
          type: array[0].type,
          userId: this.member.id
        }})
        .then(res => {
          console.log(res)
          array.splice(0, 1)
          if (array.length > 0) {
            this.updateMember(array)
          } else {
            this.$message({type: 'success', message: '成功！'})
            this.loading = false
            this.getMember(this.member.id)
          }
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    cut (e) {
      let tmpProps = e.split('，')
      return tmpProps[0]
    },
    make (res) {
      res.forEach(r => {
        if(r.isPublic) return
        let str = r.uri
        let tmpProps = str.split('/')
        if (tmpProps.length > 2) {
          let label = tmpProps[1]
          this.uiApi.forEach(a => {
            if (a.label === label) {
              let tmp = {label: tmpProps[2], children: []}
              tmp.children.push({label: str, children: [], id: r.id, description: r.description})
              a.children.push(tmp)
            }
          })
        }
      })
      this.uiApi.forEach(a => {
        a.children = this.filter(a.children)
      })
      let table = this.addChecked(this.uiApi)
      this.uiApi = table
    },
    addChecked (array) {
      array.forEach(a => {
        if (a.children) {
          Vue.set(a, 'checked', true)
          Vue.set(a, 'checkAll', false)
        }
        if (a.children.length <= 0) {
          return
        }
        this.addChecked(a.children)
      })
      return this.uiApi
    },
    filter (array) {
      let tmp = []
      array.forEach(a => {
        let index = tmp.findIndex(i => i.label === a.label)
        if (index < 0) {
          tmp.push(a)
        } else {
          tmp[index].children = tmp[index].children.concat(a.children)
        }
      })
      return tmp
    },
    handleCheckAllChange (a) { // 有选择的地方用
      console.log(a)
      // this.checkApis = val ? this.apiList : []
      this.isIndeterminate = false
    },
    checkeBtnEvent (e) {
      console.log(e)
      this.checkeBtn(e.$event, e.api)
    },
    checkeBtn ($event, api) { // 选中的预约数组
      console.log('开始————————', this.checkApis)
      console.log($event, api)
      let value
      if ($event) value = 1
      else value = 0
      if (this.member) {
        let data = {apiId: api.id, type: value, userId: this.member.id}
        this.setUserApi(data)
      }
      if (this.group) {
        let data = { type: value, apiId: api.id, groupId: this.group.id }
        this.setUserGroupApi(data)
      }
    },
    setUserApi (data) {
      this.fetch({name: 'setUserApi', data})
        .then(res => {
          console.log(res)
          this.getMember(this.member.id)
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    setUserGroupApi (data) {
      this.fetch({name: 'setUserGroupApi', data})
        .then(res => {
          console.log(res)
          this.getUserGroup(this.group.id)
          console.log('结束————————', this.checkApis)
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

  .waterfall{
    column-count: 3;
    column-gap: 0;
  }

  .item{
    box-sizing: border-box;
    break-inside: avoid;
    padding: 10px;
  }
  .item-content{
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 10px;
    height: auto;
    font-size: 20px;
    color: #686868;
    box-sizing: border-box;
    border: 1px solid #ccc;
  }
  #top{
    position: absolute;
    right: 5%;
    /*top:450px;*/
    bottom: 20%;
    width: 50px;
    height: 50px;
    z-index: 1000;
    opacity:0.7;
    font-size: 12px;
  }
  #top:hover{
    opacity:1
  }
</style>
