<template>
  <section>
    <el-row>

      <el-row style="display: flex;align-items: center;">
        <el-col :span="6">
          <el-input size="mini" placeholder="请输入关键字"  clearable v-model="searchKey" @keydown.native.prevent.enter="searchBtn" ></el-input>
        </el-col>
        <el-col :span="4">
          <i class="el-icon-search" @click="searchBtn" style="margin-left:10px" ></i>
        </el-col>
      </el-row>
      <el-table :data="currApiList" border v-loading="loading">
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
          <template slot-scope="scope" >
            <el-button size="small" @click="editApi(scope.$index, scope.row)">
              <template>
                <icon name="pencil-alt"></icon>
              </template>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-show="totalPage>0" style="margin-top:20px;" layout="prev, pager, next" :page-count="totalPage" @current-change="goPage">
      </el-pagination>
    </el-row>
  </section>
</template>

<script>
  import {mapGetters, mapActions, mapMutations} from 'vuex'
  import Vue from 'vue'//后续添加属性，需要vue联动
  import errHandler from '@/assets/errHandler'
  export default {
    name: "newList",
    props: ['currApiList','currTab'],
    data(){
      return {
        searchKey:null,
        loading:false,
        apiList:null,
        totalPage:0
      }
    },
    created(){
      // this.loading=true
      console.log(this.currTab)
      // this.searchKey=this.currTab
      // this.getApiList(0,'/'+this.searchKey+'/')
    },
    methods: {
      ...mapActions({
        fetch: 'fetch'
      }),
      searchBtn(){
        this.getApiList(0,this.searchKey)
      },
      goPage(val){
        this.loading=true
        // this.getApiList(val-1,'/'+this.searchKey+'/')
      },
      // getApiList(pageNo,key){
      //   this.fetch({name:"getApiList",data:{page:pageNo?pageNo:0,key:key?key:''}})
      //     .then(res=>{
      //       console.log(res)
      //       this.apiList=res.content
      //       this.totalPage=res.totalPages
      //       this.loading=false
      //     })
      //     .catch(err=>{
      //       console.log(err)
      //       errHandler.handle(this,err)
      //     })
      // }
    }
  }
</script>

<style scoped>

</style>
