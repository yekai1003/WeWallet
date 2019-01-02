<template>
  <el-table :data="clientList" border style="width: 100%">
    <el-table-column label="ID" width="70">
      <template slot-scope="scope">
        <span style="margin-left: 10px">{{ scope.row.id }}</span>
      </template>
    </el-table-column>
    <el-table-column label="客户端名称" width="180">
      <template slot-scope="scope">
        {{ scope.row.clientName }}
      </template>
    </el-table-column>
    <el-table-column label="公开" width="70">
      <template slot-scope="scope">
        <div style="text-align: center">
          <icon name="check" v-if="scope.row.isPublic" style="color: darkgreen"></icon>
          <icon name="ban" v-else style="color: red;"></icon>
        </div>
      </template>
    </el-table-column>
    <el-table-column label="可用" width="70">
      <template slot-scope="scope">
        <div style="text-align: center">
          <icon name="check" v-if="scope.row.isEnable" style="color: darkgreen"></icon>
          <icon name="ban" v-else style="color: red;"></icon>
        </div>
      </template>
    </el-table-column>
    <el-table-column label="操作" width="70">
      <template slot-scope="scope">
        <el-button size="mini" @click="edit(scope.$index, scope.row)">
          <template>
            <icon name="pencil-alt"></icon>
          </template>
        </el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script>
export default {
  props: ['clientList'],
  methods: {
    edit (i, row) {
      this.$emit('edit', row)
    }
  }
}
</script>
