<template>
  <main id="editingUser" v-if="currMember.id">
    <el-row>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="账户信息" name="profile" class="profile-tab">
          <UserProfile v-bind:member="currMember" v-if="activeTab === 'profile'"></UserProfile>
        </el-tab-pane>
        <el-tab-pane label="订单管理" name="order" class="profile-tab">
          放订单列表，仅限于查看
        </el-tab-pane>
        <el-tab-pane label="权限" name="permission-tab">
          <member-permission :mid="currMember.id" v-if="activeTab === 'permission-tab'"></member-permission>
          <!--<UserPermissions v-bind:user="currMember" v-bind:powerGroups="powerGroups" v-if="activeTab === 'permission-tab'"></UserPermissions>-->
        </el-tab-pane>
      </el-tabs>
    </el-row>
  </main>
</template>
<script>
import memberPermission from '../memberPermission'
import {mapGetters, mapActions} from 'vuex'
import errHandler from '@/assets/errHandler'
import UserProfile from './memberBase.vue'
import UserPermissions from '../UserPermissions.vue'
export default {
  name: 'user-edit',
  components: {UserProfile, UserPermissions, memberPermission},
  data () {
    return {
      activeTab: 'profile',
      powerGroups: [],
      userGroups: [],
      currMember: {}
    }
  },
  computed: {
    id: function () {
      return this.$route.params.id
    },
    ...mapGetters({
      user: 'userInfo'
    })
  },
  watch: {
  },
  created () {
    console.log(this.id)
    this.requestClients()
    this.requestApiList()
    this.requestUserGroups()
    this.requestCurrMember()
  },
  methods: {
    ...mapActions({fetch: 'fetch'}),
    requestUserGroups () {
      this.fetch({name: 'getUserGroupList'})
        .then(res => {
          console.log('获取用户组————————————————', res)
          this.userGroups = res
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    requestApiList () {
      this.fetch({name: 'getApiList', data: {uid: this.id}})
        .then(res => {
          console.log('getApiList————————————————', res)
          this.$store.commit({type: 'setApiList', apiList: res})
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    requestPowerGroups () {
      this.fetch({name: 'getPowerGroups', data: {uid: this.id}})
        .then(res => {
          console.log('获取PowerGroups————————————————', res)
          this.powerGroups = res
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    requestCurrMember () {
      this.fetch({name: 'getMember', data: {mid: this.id}})
        .then(res => {
          console.log('获取Member————————————————', res)
          if (!res.group) res.group = {}
          this.currMember = res
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    requestClients () {
      this.fetch({name: 'getClientList', data: {uid: this.id}})
        .then(res => {
          console.log('获取客户端————————————————', res)
          this.$store.commit({type: 'setClients', clients: res})
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    }
  }
}
</script>
