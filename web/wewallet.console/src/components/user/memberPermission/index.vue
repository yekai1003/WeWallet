<template>
  <section>
    <el-row v-if="currMember">
      <h3>客户端权限</h3>
      <clients :member="currMember"></clients>
      <h3>接口权限</h3>
      <el-main style="height: 500px">
        <apis :member="currMember"></apis>
      </el-main>
    </el-row>
  </section>
</template>

<script>
import clients from './clients'
import apis from './apis'
import Vue from 'vue'// 后续添加属性，需要vue联动
import errHandler from '@/assets/errHandler'
import {mapActions} from 'vuex'
export default {
  name: 'index',
  components: {clients, apis},
  props: ['mid'],
  data () {
    return {
      currMember: null
    }
  },
  watch: {
    currMember (nv, ov) {
      if (nv) {
        this.init(nv)
      }
    }
  },
  created () {
    this.getMember(this.mid)
  },
  methods: {
    ...mapActions({
      fetch: 'fetch'
    }),
    getMember (mid) {
      this.fetch({name: 'getMember', data: {mid}})
        .then(res => {
          console.log(res)
          if (res.userResource === undefined) {
            let tmp = {apis: [], clients: [], companies: []}
            Vue.set(res, 'userResource', tmp)
          }
          this.currMember = res
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    init () {}
  }
}
</script>

<style scoped>

</style>
