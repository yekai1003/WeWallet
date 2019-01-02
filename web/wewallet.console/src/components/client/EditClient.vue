<template>
  <div v-if="client" style="margin:0 40px 0 0px">
    <el-form ref="form" label-width="120px" size="mini">
      <el-form-item label="名称">
        <el-input v-model="client.clientName" size="small"></el-input>
      </el-form-item>

      <el-form-item label="类型">
        <el-select v-model="client.clientTypeId" size="small" placeholder="选择平台">
          <el-option
            v-for="ct in clientTypes"
            :key="ct.id"
            :label="ct.id+'、'+ct.name"
            :value="ct.id"
          ></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="公开">
        <el-switch active-text="" inactive-text="" v-model="client.isPublic"></el-switch>
      </el-form-item>

      <el-form-item label="可用">
        <el-switch active-text="" inactive-text="" v-model="client.isEnable"></el-switch>
      </el-form-item>
      <el-form-item label="属性？" >

        <el-table :data="client.tableData" style="width: 100%">
          <el-table-column type="index" width="30" >
        </el-table-column>
        <el-table-column label="关键词" width="180">
          <template slot-scope="scope">
            <el-input v-show="scope.row.edit" size="small" v-model="scope.row.key"></el-input>
            <span  v-show="!scope.row.edit">{{scope.row.key}}</span>
          </template>
        </el-table-column>
        <el-table-column label="值" width="100">
          <template slot-scope="scope">
            <el-input  v-show="scope.row.edit" size="small" v-model="scope.row.value"></el-input>
            <span  v-show="!scope.row.edit">{{scope.row.value}}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="scope">
            <el-button :type="scope.row.edit?'success':'primary'" @click='confirm(scope)' size="small" icon="edit">{{scope.row.edit?'完成':'编辑'}}</el-button>
            <el-button @click='del(scope)' size="small" >DEL</el-button>
          </template>
        </el-table-column>
      </el-table>

        <el-button @click="addClientAttributes" size="small">
          <icon name="plus"></icon>
        </el-button>
      </el-form-item>
      <el-form-item>
        <el-button @click="cancel">取消</el-button>
        <el-button type="primary" @click="submit">提交</el-button>
      </el-form-item>
    </el-form>
  </div>

</template>

<script>
import {mapActions} from 'vuex'
import ElFormItem from '../../../node_modules/element-ui/packages/form/src/form-item.vue'
export default {
  components: {ElFormItem},
  name: 'edit-client-form',
  props: ['client', 'app'],
  data () {
    return {
      clientTypes: []
    }
  },
  created () {
    console.log(this.client)
    this.fetch({name: 'getClientTypeList'})
      .then(res => {
        if (res.errCode) {
          console.log(res)
          this.$message({type: 'error', message: res.errMsg})
        } else {
          console.log(res)
          this.clientTypes = res
        }
      })
      .catch(err => {
        console.log(err)
        this.$message({type: 'error', message: '服务器错误'})
      })
      // 还需获取属性值列表
  },
  methods: {
    ...mapActions({
      fetch: 'fetch'
    }),
    del (e) {
      console.log(e)
      this.client.tableData.splice(e.$index, 1)
    },
    confirm (e) {
      console.log(e)
      e.row.edit = !e.row.edit
      console.log(e)
    },
    addClientAttributes () {
      let tb = {key: '', value: '', edit: true}
      console.log(this.client)
      this.client.tableData.push(tb)
      // 初始化操作按键row.edit = false
    },
    validateForm (form) {
      let result
      if (!form.clientName) result = '客户端名称'
      if (result) {
        this.$message({
          message: result + '不能为空',
          type: 'error'
        })
        return false
      } else {
        return true
      }
    },
    submit () {
      console.log(this.client)
      if (this.validateForm(this.client)) {
        let clientData = {}
        if (this.client.tableData && this.client.tableData.length > 0)// 将tableData内的key,value组装成对象，
        {
          this.client.tableData.forEach((td) => {
            clientData[td.key] = td.value
          })
        }
        this.$store.dispatch({
          type: 'fetch',
          name: 'updateClient',
          data: {
            ...this.client,
            clientData: JSON.stringify(clientData)// 以字符串形式提交并告知参数名
          }
        }).then(res => {
          if (res.errCode) {
            this.$message({
              message: res.errMsg,
              type: 'error'
            })
          } else {
            console.log(res)
            this.client = res
            this.$message({
              message: '编辑成功',
              type: 'success'
            })
            this.$emit('cancel')
          }
        }).catch(error => {
          this.$message({
            message: '连不上服务器',
            type: 'error'
          })
        })
      }
    },
    cancel () {
      this.$emit('cancel')
    }
  }
}
</script>
