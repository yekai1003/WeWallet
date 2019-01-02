<template>
  <section>
    <el-collapse v-model="activeName" accordion>
      <el-collapse-item title="客户端" name="client">
        <el-checkbox-group v-if="activeName === 'client'" v-model="group.clients">
          <div v-for="client in clients" :key="client.id" style="float:left;margin:0 20px">
            <el-checkbox :label="client.id"  @change="setUserGroupClientEvent($event,client)">{{client.clientName}}</el-checkbox>
          </div>
        </el-checkbox-group>
      </el-collapse-item>
      <el-collapse-item title="api" name="api">
        <el-main style="height: 500px">
          <apis :group="group" v-if="activeName === 'api'"/>
        </el-main>
        <!--<el-row style="border:0px solid red;">-->
          <!--<el-checkbox-group v-if="activeName === 'api'" v-model="group.apis">-->
            <!--<el-row v-for="api in apis" :key="api.id">-->
              <!--<el-checkbox :label="api.id"  @change="setUserGroupApiEvent($event,api)">{{api.description}}</el-checkbox>-->
            <!--</el-row>-->
          <!--</el-checkbox-group>-->
        <!--</el-row>-->
        <!--<el-button size="mini" type="text" @click="allCheckApis(true)">全选</el-button>-->
        <!--<el-button size="mini" type="text" @click="allCheckApis(false)">全不选</el-button>-->
        <!--<el-pagination v-show="totalPage>0" style="margin-top:20px;" small-->
                       <!--layout="prev, pager, next"-->
                       <!--:page-count="totalPage"-->
                       <!--@current-change="goPage">-->
        <!--</el-pagination>-->
      </el-collapse-item>
    </el-collapse>
  </section>
</template>

<script>
import apis from '../memberPermission/apis'
import Vue from 'vue'// 后续添加属性，需要vue联动
import errHandler from '@/assets/errHandler'
import {mapGetters, mapActions, mapMutations} from 'vuex'
export default {
  name: 'groupResources',
  components: {apis},
  props: ['group'],
  data () {
    return {
      totalPage: 0,
      companies: null,
      apis: null,
      clients: null,
      activeName: 'client'
    }
  },
  created () {
    console.log('感觉这里有问题————————', this.group)
    this.init()
    this.getClientList()
  },
  methods: {
    ...mapActions({
      fetch: 'fetch'
    }),
    init () {
      if (this.group.apis === undefined) {
        Vue.set(this.group, 'apis', [])
      }
      if (this.group.clients === undefined) {
        Vue.set(this.group, 'clients', [])
      }
    },
    goPage (val) {
      this.getApiList(val - 1)
    },
    allCheckApis (type) {
      let array = this.group.apis
      // let tmp=[]
      this.apis.forEach(api => {
        let index = array.findIndex(a => a === api.id)
        if (index < 0 && type) {
          array.push(api.id)
          this.setUserGroupApiEvent(true, api)
        }
        if (index >= 0 && !type) {
          array.splice(index, 1)
          this.setUserGroupApiEvent(false, api)
        }
      })
    },
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
    getClientList () {
      this.fetch({name: 'getClientList'})
        .then(res => {
          this.clients = res
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    setUserGroupCompanyEvent (e, c) {
      console.log(e, c)
      let data = {
        type: e ? 1 : 0,
        companyId: c.id,
        groupId: this.group.id
      }
      console.log(data)
      this.setUserGroupCompany(data)
    },
    setUserGroupCompany (data) {
      this.fetch({name: 'setUserGroupCompany', data})
        .then(res => {
          console.log(res)
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    setUserGroupClientEvent (e, c) {
      console.log(e, c)
      let data = {
        type: e ? 1 : 0,
        clientId: c.id,
        groupId: this.group.id
      }
      console.log(data)
      this.setUserGroupClient(data)
    },
    setUserGroupClient (data) {
      this.fetch({name: 'setUserGroupClient', data})
        .then(res => {
          console.log(res)
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    setUserGroupApiEvent (e, a) {
      let data = {
        type: e ? 1 : 0,
        apiId: a.id,
        groupId: this.group.id
      }
      this.setUserGroupApi(data)
    },
    setUserGroupApi (data) {
      this.fetch({name: 'setUserGroupApi', data})
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

</style>
