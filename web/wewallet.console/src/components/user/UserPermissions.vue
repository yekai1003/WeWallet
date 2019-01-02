<template>
  <div>
    <!--<el-row>-->
    <!--<h3>权限包</h3>-->
    <!--<el-select v-model="selectedGroup" placeholder="选择权限包">-->
    <!--<el-option v-for="item in powerGroups" :key="item.id" :label="item.title" :value="item.id"></el-option>-->
    <!--</el-select>-->
    <!--<el-button type="primary" @click="usePermissionGroup">使用</el-button>-->
    <!--</el-row>-->
    <el-row>
      <h3>客户端权限</h3>
      <el-checkbox-group v-model="user.userResource.clients">
        <el-checkbox style="width: 25%;margin: 5px 0;" v-for="client in clients" :label="client.id" :key="client.id"
                     @change="updateClientsPermission($event,client)">{{client.clientName}}
        </el-checkbox>
      </el-checkbox-group>
      <h3>接口权限</h3>
      <selApi :user="user"></selApi>
    </el-row>
    <el-row>
      <el-col :span="24" class="footer">
        <!--<el-button type="primary" @click="updateUserPermissions">提交</el-button>-->
      </el-col>
    </el-row>
  </div>
</template>

<script>
import {mapActions} from 'vuex'
import selApi from './selApi'

export default {
  name: 'edit-user-permission',
  props: ['user', 'powerGroups'],
  components: {selApi},
  computed: {
    // clients:function(){
    //   console.log(this.$store)
    //   console.log(this.$store.getters.clients)
    //   return this.$store.getters.clients;
    // },
  },
  data () {
    return {
      clients: [],
      selectedGroup: '',
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
    this.getCurrMember(this.user.id)
  },
  methods: {
    ...mapActions({
      fetch: 'fetch'
    }),
    getCurrMember (mid) {
      this.fetch({name: 'getMember', data: {mid}})
        .then(res => {
          console.log(res)
          this.currMember = res
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    init (member) {
      if (!member.userResource) {
        console.log(this.user)
        member.userResource = {
          api: [],
          clients: [],
          companies: []
        }
      }
      this.getClientList()
    },
    getClientList () {
      this.fetch({name: 'getClientList'})
        .then(res => {
          this.clients = res
          console.log(res)
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    usePermissionGroup () {

    },
    updateClientsPermission (e, c) {
      console.log(e, c)
      let value
      if (e) value = 1
      else value = 0
      this.fetch({
        name: 'setUserClient',
        data: {
          cId: c.id,
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
