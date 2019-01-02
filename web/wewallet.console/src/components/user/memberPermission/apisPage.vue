<template>
  <section>
    <el-row style="border: 0px solid red">
      <el-row v-for="d in currApis" :key="d.id" style="margin-bottom: 5px;line-height:30px" >
        <el-checkbox-group v-model="selApis" style="border: 0px solid green">
          <el-checkbox :label="d.id"  @change="checkeBtn($event,d)">
            <el-row >
              <el-row >
                {{d.id}}-{{d.label}}
              </el-row>
              <el-row >
                {{cut(d.description)}}
              </el-row>
            </el-row>
          </el-checkbox>
        </el-checkbox-group>
      </el-row>
    </el-row>
    <el-pagination style="margin-top: 10px;border:0px solid red"
                   layout="prev, pager, next"
                   :total="totalPage" @current-change="goPage">
    </el-pagination>
  </section>
</template>

<script>
import { mapActions } from 'vuex'
export default {
  name: 'apisPage',
  props: ['apis', 'checkApis'],
  data () {
    return {
      totalPage: 0,
      currApis: [],
      selApis: []
    }
  },
  watch: {
    checkApis (nv, ov) {
      if (nv) {
        nv.forEach(n => {
          this.selApis.push(n)
        })
      }
    }
  },
  created () {
    this.totalPage = this.apis.length
    this.getCurrApis(0)
    this.checkApis.forEach(n => {
      this.selApis.push(n)
    })
  },
  methods: {
    ...mapActions({
      fetch: 'fetch'
    }),
    cut (e) {
      let tmpProps = e.split('，')
      return tmpProps[0]
    },
    getCurrApis (page) {
      this.currApis = this.apis.slice(page * 10, page * 10 + 10)
    },
    goPage (val) {
      console.log(val)
      this.getCurrApis(val - 1)
    },
    checkeBtn ($event, api) {
      console.log('到这么？？？')
      this.$emit('checkeBtn', {$event, api})
    }
  }
}
</script>

<style scoped>

</style>
