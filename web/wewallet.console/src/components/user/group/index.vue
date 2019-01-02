<template>
  <section>
    <el-button size="mini" type="primary" @click="add">新用户组</el-button>

    <el-table :data="groupList" border style="width: 100%">
      <el-table-column type="index" label="序号"/>
      <el-table-column label="ID" width="80" prop="id"/>
      <el-table-column label="名称" width="200" prop="groupName"/>

      <el-table-column label="描述" prop="description"/>
      <el-table-column label="权限">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="resourcesEditBtn(scope.row)">编辑</el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140">
        <template slot-scope="scope">
          <el-button size="mini" type="primary" @click="edit(scope.$index, scope.row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="remove(scope.$index, scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </section>

  <!--<section>-->
  <!--<div id="user-group-management">-->
  <!--<section style="margin-bottom: 20px;text-align: right;">-->
  <!--<el-button @click="add">-->
  <!--<icon name="plus"></icon>-->
  <!--</el-button>-->
  <!--</section>-->

  <!--<section>-->
  <!--<UserGroupList v-bind:userGroupList="userGroupList" v-on:triggerEdit="edit" v-on:cancel="resetData" v-on:resourcesEdit="resourcesEdit" />-->
  <!--</section>-->

  <!--<el-dialog title="添加新公司" :visible.sync="addUserGroupDialog">-->
  <!--<CreateUserGroup v-bind:userGroup="newUserGroup" v-on:cancel="resetData"></CreateUserGroup>-->
  <!--</el-dialog>-->

  <!--<el-dialog title="编辑公司" :visible.sync="editUserGroupDialog">-->
  <!--<EditUserGroup v-bind:userGroup="editUserGroup" v-on:cancel="resetData"></EditUserGroup>-->
  <!--</el-dialog>-->

  <!--<el-dialog title="编辑权限" :visible.sync="groupResources" v-if="groupResources">-->
  <!--<group-resources :group="editUserGroup"></group-resources>-->
  <!--</el-dialog>-->
  <!--</div>-->
  <!--</section>-->
</template>

<script>
import groupResources from './groupResources'
import UserGroupList from './UserGroupsList.vue'
import CreateUserGroup from './CreateUserGroup.vue'
import EditUserGroup from './EditUserGroup.vue'
import errHandler from '@/assets/errHandler.js'
import Vue from 'vue'// 后续添加属性，需要vue联动
import { mapActions } from 'vuex'

export default {
  name: 'user-group-management',
  components: {
    UserGroupList, CreateUserGroup, EditUserGroup, groupResources
  },
  data () {
    return {
      groupList: null,

      addUserGroupDialog: false,
      editUserGroupDialog: false,
      userGroupList: [],
      newUserGroup: {},
      editUserGroup: {},
      groupResources: false
    }
  },
  created () {
    this.requestUserGroups()
    this.getGroups()
  },
  methods: {
    ...mapActions({
      fetch: 'fetch'
    }),
    getGroups () {
      this.fetch({name: 'getUserGroupList'})
        .then(res => {
          console.log(res)
          this.groupList = res
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    resetData () {
      this.addUserGroupDialog = false
      this.editUserGroupDialog = false
      this.newUserGroup = {}
      this.editUserGroup = {}
      this.requestUserGroups()
    },
    requestUserGroups () {
      this.$store.dispatch({
        type: 'fetch',
        name: 'getUserGroupList'
      }).then(res => {
        if (res.errCode) {
          this.$message({
            message: res.errMsg,
            type: 'error'
          })
        } else {
          this.userGroupList = res
        }
      }).catch(error => {
        this.$message({
          message: error,
          type: 'error'
        })
      })
    },
    add () {
      this.newUserGroup = {
        groupName: '',
        description: ''
      }
      this.addUserGroupDialog = true
    },
    edit (row) {
      if (!row.clients) { Vue.set(row, 'clients', []) }
      if (!row.apis) { Vue.set(row, 'apis', []) }
      console.log(row)
      this.editUserGroup = row
      this.editUserGroupDialog = true
    },
    resourcesEdit (e) {
      console.log('编辑权限的事件————————', e)
      this.editUserGroup = e
      this.groupResources = true
    }
  }

}
</script>
