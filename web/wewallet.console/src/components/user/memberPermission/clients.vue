<template>
  <section>
    <el-checkbox-group v-model="member.userResource.clients" v-if="clients">
      <el-checkbox style="width: 25%;margin: 5px 0;" v-for="client in clients" :label="client.id" :key="client.id" @change="updateClientsPermission($event,client)">{{client.clientName}}</el-checkbox>
    </el-checkbox-group>
  </section>
</template>

<script>
import Vue from 'vue'// 后续添加属性，需要vue联动
import errHandler from '@/assets/errHandler'
import {mapActions} from 'vuex'
export default {
  name: 'clients',
  props: ['member'],
  data () {
    return {
      clients: null
    }
  },
  created () {
    if (this.member.userResource.clients === undefined) {
      Vue.set(this.member.userResource, 'clients', [])
    }
    this.getClientList()
  },
  methods: {
    ...mapActions({
      fetch: 'fetch'
    }),
    getClientList () {
      this.fetch({name: 'getClientList'})
        .then(res => {
          console.log(res)
          this.clients = res
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    updateClientsPermission (e, c) {
      console.log(e, c)
      let value
      if (e) value = 1
      else value = 0
      this.fetch({name: 'setUserClient',
        data: {
          cId: c.id,
          type: value,
          userId: this.member.id
        }})
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
