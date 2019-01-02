<template>
  <section>
    <el-row>
      <el-input style="width: 20%" size="mini" v-model="searchKey" placeholder="手机或者姓名" @keydown.native.prevent.enter="searchMemberBtn"></el-input>
      <i class="el-icon-search"  @click="searchMemberBtn"></i>
      <el-button type="primary" style="font-size: 12px;float: right;margin-right: 10px" size="mini" @click="addMemberBtn">新建账户</el-button>
    </el-row>
    <el-table v-if="memberList" :data="memberList">
      <el-table-column type="index" label="序号"/>
      <el-table-column prop="nickname" label="昵称"></el-table-column>
      <el-table-column prop="group.groupName" label="用户组"></el-table-column>
      <el-table-column prop="phone" label="联系方式"></el-table-column>
      <el-table-column prop="enable" label="当前状态">
        <template slot-scope="scope">{{scope.row.enable?'可用':'不可用'}}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180px">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="editMemberBtn(scope.row)">编辑</el-button>
          <el-button type="danger" size="mini" @click="disableMemberBtn(scope.row)" v-if="scope.row.enable">停用</el-button>
          <el-button type="success" size="mini" @click="disableMemberBtn(scope.row)" v-else>启用</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog :visible.sync="createMemberDialog">
      <create-member-dialog v-on:cancel="cancelDialog"></create-member-dialog>
    </el-dialog>
  </section>
</template>

<script>
import createMemberDialog from './createMemberDialog'
import Vue from 'vue'// 后续添加属性，需要vue联动
import errHandler from '@/assets/errHandler'
import {mapGetters, mapActions, mapMutations} from 'vuex'
export default {
  name: 'index',
  components: {createMemberDialog},
  data () {
    return {
      searchKey: null,
      memberList: null,
      createMemberDialog: false
    }
  },
  created () {
    // 加载账户列表
  },
  methods: {
    ...mapActions({
      fetch: 'fetch'
    }),
    cancelDialog (e) {
      this.createMemberDialog = false
      this.searchKey = e.username
      this.getMembers(e.username)
    },
    searchMemberBtn () {
      console.log(this.searchKey)
      this.getMembers(this.searchKey)
    },
    getMembers (keywords) {
      console.log(keywords)
      this.fetch({name: 'getMembers', data: {keywords}})
        .then((res) => {
          console.log('账户列表——————', res)
          this.memberList = res.content
        })
        .catch((err) => {
          console.log(err)
          this.$message({type: 'error', message: '服务器错误'})
        })
    },
    addMemberBtn () {
      this.createMemberDialog = true
    },
    editMemberBtn (row) {
      this.$router.push('/user/member-edit/' + row.id)
    },
    disableMemberBtn (row) {
      console.log(row)
      let event = row.enable ? '停用' : '启用'
      this.$confirm('确认' + event + '该账户？')
        .then(() => {
          this.fetch({name: 'enableMember', data: {memberId: row.id, enable: !row.enable}})
            .then(res => {
              this.getMembers(this.searchKey)
            })
            .catch(err => {
              console.log(err)
              errHandler.handle(this, err)
            })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
          this.loading = false
        })
    }
  }
}
</script>

<style scoped>

</style>
