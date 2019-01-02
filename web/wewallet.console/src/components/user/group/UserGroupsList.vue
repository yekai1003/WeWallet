<template>
  <el-row>

    <el-table :data="userGroupList" border style="width: 100%">

      <el-table-column label="ID" width="80">
        <template slot-scope="scope">
          <span style="margin-left: 10px">{{ scope.row.id }}</span>
        </template>
      </el-table-column>

      <el-table-column :label="tableHead.groupName" width="200">
        <template slot-scope="scope">
          <span style="margin-left: 10px">{{ scope.row.groupName }}</span>
        </template>
      </el-table-column>

      <el-table-column :label="tableHead.description">
        <template slot-scope="scope">
          {{ scope.row.description }}
        </template>
      </el-table-column>
      <el-table-column label="权限">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="resourcesEditBtn(scope.row)">编辑</el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140">
        <template slot-scope="scope">

          <el-button size="small" @click="edit(scope.$index, scope.row)">
            <template>
              <icon name="pencil-alt"></icon>
            </template>
          </el-button>

          <el-button size="small" @click="remove(scope.$index, scope.row)">
            <template>
              <icon name="ban" style="color:red"></icon>
            </template>
          </el-button>

        </template>
      </el-table-column>

    </el-table>

  </el-row>
</template>

<script>
export default {
  props: ['userGroupList'],
  data () {
    return {
      tableHead: {
        groupName: '组名称',
        description: '描述'
      }
    }
  },
  methods: {
    edit (i, row) {
      this.$emit('triggerEdit', row)
    },
    resourcesEditBtn (row) {
      this.$emit('resourcesEdit', row)
    },
    remove (i, row) {
      this.$confirm('确定删除数据?', '提醒', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.requestDelete(row.id)
      }).catch(error => {

      })
    },
    requestDelete (id) {
      this.$store.dispatch({
        type: 'fetch',
        name: 'deleteUserGroup',
        data: {groupId: id}
      }).then(res => {
        if (res.errCode) {
          this.$message({
            message: res.errMsg,
            type: 'error'
          })
        } else {
          this.$message({
            message: '删除成功',
            type: 'success'
          })
          this.cancel()
        }
      }).catch(error => {
        this.$message({
          message: '连不上服务器',
          type: 'error'
        })
      })
    },
    cancel () {
      this.$emit('cancel')
    }
  }
}
</script>
