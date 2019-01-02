<template>
  <div class="api-list">
    <el-row style="margin-bottom: 10px;">
      <el-col :span="4">
        <el-input v-model="filter" size="mini" @keydown.native.prevent.enter="filterList"></el-input>
      </el-col>
      <el-col :span="2" :offset="18" style="text-align: right">
        <el-button @click.native="addApi">
          <icon name="plus"></icon>
        </el-button>
      </el-col>
    </el-row>
    <el-row>
      <el-table :data="apiList" border>
        <el-table-column label="ID" width="80">
          <template slot-scope="scope">
            <span style="margin-left: 10px">{{ scope.row.id }}</span>
          </template>
        </el-table-column>
        <el-table-column label="接口地址">
          <template slot-scope="scope">
            {{ scope.row.uri }}
          </template>
        </el-table-column>
        <el-table-column label="接口描述">
          <template slot-scope="scope">
            {{ scope.row.description }}
          </template>
        </el-table-column>
        <el-table-column label="需要Token" width="120">
          <template slot-scope="scope">
            <p style="text-align: center">
              <icon name="check" v-if="scope.row.needAuthorization == 1" style="color: darkgreen"></icon>
              <icon name="ban" v-else style="color: red;"></icon>
            </p>
          </template>
        </el-table-column>

        <el-table-column label="是否公用" width="100">
          <template slot-scope="scope">
            <p style="text-align: center">
              <icon name="check" v-if="scope.row.isPublic == 1" style="color: darkgreen"></icon>
              <icon name="ban" v-else style="color: red;"></icon>
            </p>
          </template>
        </el-table-column>

        <el-table-column label="是否可用" width="100">
          <template slot-scope="scope">
            <p style="text-align: center">
              <icon name="check" v-if="scope.row.isEnable == 1" style="color: darkgreen"></icon>
              <icon name="ban" v-else style="color: red;"></icon>
            </p>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="70">
          <template slot-scope="scope">
            <el-button size="small" @click="editApi(scope.$index, scope.row)">
              <template>
                <icon name="pencil-alt"></icon>
              </template>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination layout="prev, pager, next" v-show="totalPage>0" style="margin-top:20px;"
                     :page-count="totalPage"
                     @current-change="goPage">
      </el-pagination>
    </el-row>

  </div>

</template>

<script>
import {mapActions} from 'vuex'

export default {
  computed: {
    // apiList(){
    //   console.log(this.$store)
    // },
  },
  data () {
    return {
      filter: '',
      list: [],
      apiList: null,
      totalPage: 0
    }
  },
  watch: {
    filter (nv, ov) {
      console.log('有没有搜索的关键词————————————————', nv)
      if (nv === '') this.getApi()
    }
  },
  created () {
    this.getApi()
  },
  methods: {
    ...mapActions({
      fetch: 'fetch'
    }),
    goPage (val) {
      this.getApi(val - 1)
    },
    getApi (pageNo) {
      this.fetch({name: 'getApiList', data: {page: pageNo || 0}})
        .then(res => {
          console.log(res)
          this.apiList = res.content
          this.totalPage = res.totalPages
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    filterList () {
      let array = this.$store.getters.apiList
      console.log('————————', array)
      const result = array.filter(api => {
        if (api.uri.indexOf(this.filter) !== -1) return api
        if (api.description.indexOf(this.filter) !== -1) return api
        if (api.id == this.filter) return api
      })
      this.apiList = result
      console.log(this.apiList)
    },
    editApi (index, row) {
      this.$emit('triggerEditApi', row)
    },
    addApi () {
      this.$emit('triggerAddApi')
    }
  }
}
</script>
