<template>
  <section>
    <el-row>
      <el-col :span="10">
        <el-input placeholder="输入关键字" v-model="filterText" size="mini">
        </el-input>
      </el-col>
      <el-col :span="4" style="margin-left: 10px">
        <el-button @click="addTargetBtn" size="mini">新建一级标签</el-button>
      </el-col>
    </el-row>
    <el-row style="color: crimson;margin-top: 10px">*拖曳标签进行层级变化。</el-row>
    <el-row style="margin-top: 10px">
      <el-tree :data="targets" node-key="id" :props="defaultProps" default-expand-all :expand-on-click-node="false" ref="tree" :filter-node-method="filterNode"
               @node-drag-start="handleDragStart"
               @node-drag-end="handleDragEnd"
               @node-drop="handleDrop"
               draggable>
      <span class="custom-tree-node" slot-scope="{ node, data }">
        <span v-if="data.edit">
          <el-input v-model="data.name" size="mini" @keyup.enter.native="saveTarget(data)"></el-input>
        </span>
        <span v-else>{{node.level}}//{{ data.name }}</span>
        <span>
          <i v-if="node.level >=2" class="el-icon-arrow-up" @click="up(node,data)" style="margin-right:10px"></i>
          <i v-if="node.parent.childNodes.length >1" class="el-icon-arrow-down" @click="down(node,data)" style="margin-right:10px"></i>
          <el-button v-if="!data.edit" type="text" size="mini" @click="edit(node,data)">编辑</el-button>
          <el-button v-if="data.edit" type="text" size="mini" @click="cancel(node,data)">取消</el-button>
          <el-button type="text" size="mini" @click="append(data)">增加</el-button>
          <el-button type="text" size="mini" @click="remove(node, data)">删除</el-button>
        </span>
      </span>
      </el-tree>
    </el-row>
    <el-row>
      <el-dialog title="新建标签" :visible.sync="showAddTargetDialog">
        <el-form :inline="true" size="mini" @submit.native.prevent>
          <el-form-item label="输入标签名：" >
            <el-input v-model="newTarget" @keyup.enter.native="subTarget">
            </el-input>
          </el-form-item>
        </el-form>
        <el-row>
          <el-button @click="subTarget">提交</el-button>
        </el-row>
      </el-dialog>
    </el-row>
  </section>
</template>

<script>

import Vue from 'vue'// 后续添加属性，需要vue联动
import {mapGetters, mapActions, mapMutations} from 'vuex'
import errHandler from '@/assets/errHandler'
export default {
  name: 'targetManagement',
  data () {
    return {
      newTarget: '',
      filterText: '',
      showAddTargetDialog: false,
      targets: [],
      defaultProps: {
        children: 'childrens',
        label: 'name'
      }
    }
  },
  created () {
    // 加载已有标签列表
    this.getLabelList()
  },
  watch: {
    filterText (val) {
      console.log(val)
      this.$refs.tree.filter(val)
    }
  },
  computed: {
    ...mapGetters(['department'])
  },
  methods: {
    ...mapActions({
      fetch: 'fetch'
    }),
    up (node, data) {
      console.log(node)
      console.log(data)
      let tmp = node.parent
      let d = {parentId: tmp.parent.key, id: data.id}// update用
      let index = tmp.childNodes.findIndex(c => c.key === node.key)
      let p = null
      if (d.parentId === undefined) {
        d.parentId = 0
        p = tmp.parent.data
        // p.push({...d,name:data.name,childrens:data.childrens})
      } else {
        p = tmp.parent.data.childrens
        // p.push({...d,name:data.name,childrens:data.childrens})
      }
      let uidata = {...d, name: data.name, childrens: data.childrens}
      let ui = {p: p, data: uidata}
      let del = {p: tmp.childNodes, index: index}
      // ui.p.push(ui.data)
      // del.p.splice(del.index,1)
      this.updataTarget(d, ui, del)
    },
    down (node, data) {
      // 找到自己所在父级的index，+1后为要下移的父级index，获取父级id
      let tmp = node.parent.childNodes
      let index = tmp.findIndex(c => c.key === node.key)
      // parentId = tmp[index+1].key
      if (tmp[index + 1] === undefined) {
        this.$message({type: 'error', message: '无可下移标签！'})
        return
      }
      let next = tmp[index + 1]
      let d = {id: node.key, parentId: next.key}// 用户更新的
      let uidata = {parentId: data.id, name: data.name, childrens: data.childrens}// 用户更新界面的
      let p = next.data.childrens
      let ui = {p: p, data: uidata}
      let del = {p: tmp, index: index}
      // 在同级里把自己加进去
      // ui.p.push(ui.data)
      // //把自己删掉
      // del.p.splice(del.index,1)
      // console.log(this.targets)
      this.updataTarget(d, ui, del)
    },
    updataTarget (data, ui, del) {
      console.log(del)
      this.fetch({name: 'updateLabel', data: data})
        .then(res => {
          console.log(res)
          this.getLabelList()
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    filterNode (value, data) {
      if (!value) return true
      return data.name.indexOf(value) !== -1
    },
    getLabelList () {
      this.fetch({name: 'getLabelList'})
        .then(res => {
          console.log(res)
          this.targets = res
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    addTargetBtn () {
      this.showAddTargetDialog = true
    },
    append (data) {
      console.log(data)
      if (!data.childrens) {
        this.$set(data, 'childrens', [])
      }
      data.childrens.push({parentId: data.id, name: '默认文本', childrens: [], edit: true})
    },
    remove (node, data) {
      console.log(node)
      console.log(data)
      this.fetch({name: 'deleteFamily', data: {id: data.id}})
        .then(res => {
          console.log(res)
          let index = node.parent.childNodes.findIndex(c => c.key === data.id)
          node.parent.childNodes.splice(index, 1)
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    edit (node, data) {
      if (data.edit === undefined) {
        Vue.set(data, 'edit', true)// 后续添加属性，需要vue联动
      } else {
        data.edit = true
      }
    },
    cancel (node, data) {
      data.edit = false
    },
    subTarget () {
      console.log(this.newTarget)
      console.log(this.targets)
      this.fetch({name: 'createLabel', data: {name: this.newTarget}})
        .then(res => {
          console.log(res)
          this.targets.push(res)
          this.showAddTargetDialog = false
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    saveTarget (data) {
      console.log(data)
      if (data.id) {
        this.fetch({name: 'updateLabel', data: data})
          .then(res => {
            console.log(res)
            data.edit = false
          })
          .catch(err => {
            console.log(err)
            errHandler.handle(this, err)
          })
      } else {
        this.fetch({name: 'createLabel', data: data})
          .then(res => {
            data.id = res.id
            data.edit = false
          })
          .catch(err => {
            console.log(err)
            errHandler.handle(this, err)
          })
      }
    },
    handleDragStart (node, ev) {
      console.log('开始移')
      // console.log('drag start', node);
    },
    handleDragEnd (draggingNode, dropNode, dropType, ev) {
      console.log('可能为空')
      console.log('tree drag end: ', dropNode && dropNode.label, dropType)
    },
    handleDrop (draggingNode, dropNode, dropType, ev) {
      console.log(dropNode)
      // 组装参数，被拖曳的id，拖进的id
      let data = { id: draggingNode.key}
      if (dropType === 'inner') {
        data.parentId = dropNode.key
      } else {
        data.parentId = dropNode.parent.key
      }
      if (data.parentId === undefined) {
        data.parentId = 0
      }
      this.updataTarget(data)
    }
  }
}
</script>

<style scoped>
  .custom-tree-node {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    padding-right: 8px;
    line-height: 25px;
  }
</style>
