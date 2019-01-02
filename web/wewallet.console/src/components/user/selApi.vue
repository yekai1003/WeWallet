<template>
  <section>
    <el-row style="margin:20px">
      <el-collapse>
        <el-collapse-item :name="api.id" v-for="api in uiApi" :key="api.id">
          <template slot="title">
            {{api.name}}
          </template>
          <el-row style="display: flex;flex-direction: row; flex-wrap:wrap;">
            <el-card v-for="(a,i) in api.children" :key="i" style="width:300px;margin: 10px;" v-loading="loading">
              <el-row slot="header">
                <span>{{a.label}},{{a.checked}}</span>
                <el-checkbox style="float: right; padding: 3px 0" v-model="a.checkAll" @change="checkAllBtn($event,a)"
                             :indeterminate="a.checked">全选
                </el-checkbox>
              </el-row>
              <div v-for="(d,j) in a.children" :key="d.id" style="height: 50px;line-height: 100px">
                <el-checkbox-group v-model="checkApis">
                  <el-checkbox :label="d.id" @change="checkeBtn($event,d,a)">
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
            </el-card>
          </el-row>
        </el-collapse-item>
      </el-collapse>
    </el-row>
  </section>
</template>

<script>
import {mapActions} from 'vuex'
import Vue from 'vue'// 后续添加属性，需要vue联动
export default {
  name: 'selApi',
  props: ['user'],
  data () {
    return {
      loading: false,
      checkAll: false,
      checkApis: [],
      checkApi: [],
      isIndeterminate: true,
      checkAllApi: false,
      apiList: [],
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
  watch: {},
  computed: {},
  created () {
    this.checkApis = this.user.userResource.apis
    console.log(this.checkApis)
    this.getApiList(0)
  },
  methods: {
    ...mapActions({
      fetch: 'fetch'
    }),
    checkAllBtn (event, apis) {
      console.log(event)
      console.log(apis.checked)
      let tmp = []
      apis.children.forEach(a => {
        let index = this.checkApis.findIndex(c => c === a.id)
        if (event && index < 0) {
          console.log('全选，不在的加进去')
          this.checkApis.push(a.id)
          tmp.push({type: 1, a: a})
          return
        }
        if (!event && index >= 0) {
          console.log('全取消，把在的减掉')
          this.checkApis.splice(index, 1)
          tmp.push({type: 0, a: a})
        }
      })
      if (tmp.length > 0) {
        this.loading = true
        this.update(tmp)
      }
      apis.checked = false
    },
    update (array) {
      console.log('我要看type！！！！！！', array[0].type)
      this.fetch({
        name: 'setUserApi',
        data: {
          apiId: array[0].a.id,
          type: array[0].type,
          userId: this.user.id
        }
      })
        .then(res => {
          console.log(res)
          array.splice(0, 1)
          if (array.length > 0) {
            this.update(array)
          } else {
            this.$message({type: 'success', message: '成功！'})
            this.loading = false
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
    getApiList (pageNo) {
      let page = this
      this.fetch({name: 'getApiList', data: {page: pageNo}})
        .then(res => {
          console.log('只能得到一页的api', res)
          this.getAllApi(res.totalElements)
          // this.make(res.content)
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    getAllApi (size) {
      this.fetch({name: 'getApiList', data: {size}})
        .then(res => {
          console.log('all', res)
          this.make(res.content)
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    make (res) {
      res.forEach(r => {
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
      console.log()
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
    checkeBtn ($event, api) { // 选中的预约数组
      console.log($event, api)
      let value
      if ($event) value = 1
      else value = 0
      this.fetch({
        name: 'setUserApi',
        data: {
          apiId: api.id,
          type: value,
          userId: this.user.id
        }
      })
        .then(res => {
          console.log(res)
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
  .waterfall {
    column-count: 3;
    column-gap: 0;
  }

  .item {
    box-sizing: border-box;
    break-inside: avoid;
    padding: 10px;
  }

  .item-content {
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
</style>
