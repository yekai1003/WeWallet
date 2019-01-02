<template>
  <main>
    <section style="margin-bottom: 20px;text-align: right">
      <el-button @click="addClient">
        <icon name="plus"></icon>
      </el-button>
    </section>
    <section style="margin-top: 20px;">
      <ListClient
        v-bind:clientList="clientList"
        v-on:edit="editClient"
      ></ListClient>
    </section>
    <el-dialog title="添加客户端" :visible.sync="createClientDialog">
      <CreateClient
        v-bind:apps="appList"
        v-bind:client="newClient"
        v-on:cancel="resetData"
      ></CreateClient>
    </el-dialog>
    <el-dialog title="编辑客户端" :visible.sync="editClientDialog">
      <EditClient
        v-bind:apps="appList"
        v-bind:client="editingClient"
        v-on:cancel="resetData"
      ></EditClient>
    </el-dialog>
  </main>
</template>

<script>
import Vue from 'vue'
import ListClient from './ListClient.vue'
import CreateClient from './CreateClient.vue'
import EditClient from './EditClient.vue'

export default {
  name: 'client-platform-manager',
  components: {
    ListClient, CreateClient, EditClient
  },
  computed: {},
  data () {
    return {
      clientList: [],
      createClientDialog: false,
      editClientDialog: false,
      newClient: {},
      editingClient: {},
      appList: []
    }
  },
  methods: {
    resetData () {
      this.requestClients()
      this.createClientDialog = false
      this.editClientDialog = false
      this.editingClient = {}
      this.newClient = {}
    },
    requestClients () {
      this.$store.dispatch({
        type: 'fetch',
        name: 'getClientList'
      }).then(res => {
        if (res.errCode) {
          this.$message({
            message: res.errMsg,
            type: 'error'
          })
        } else {
          res.map(client => {
            if (client.app) {
              if (client.app.id) {
                client.app = client.app.id
              }
            }
          })
          this.clientList = res
        }
      }).catch(error => {
        this.$message({message: '连不上服务器', type: 'error'})
      })
    },
    requestApp () {
      this.$store.dispatch({
        type: 'fetch',
        name: 'getAppList'
      }).then(res => {
        if (!res.errCode) {
          this.appList = res
        } else {
          console.log(res)
          this.$message({type: 'error', message: '获取app错误'})
        }
      }).catch(error => {
        this.$message({type: 'error', message: '获取不到app'+error})
      })
    },
    addClient () {
      this.newClient = {
        clientName: '',
        isPublic: true,
        isEnable: true,
        clientTypeId: null,
        tableData: []
      }
      this.createClientDialog = true
    },
    editClient (row) {
      console.log(row)
      let tableData = []
      if (row.clientData) {
        for (let key in row.clientData) {
          tableData.push({key: key, value: row.clientData[key], edit: false})
        }
      }
      this.editClientDialog = true
      this.editingClient = {
        id: row.id,
        clientName: row.clientName,
        isPublic: row.isPublic,
        isEnable: row.isEnable,
        clientTypeId: row.clientType ? row.clientType.id : -1,
        tableData
      }
    }
  },
  created () {
    this.requestClients()
  }
}
</script>
