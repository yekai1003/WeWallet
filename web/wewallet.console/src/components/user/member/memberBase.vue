<template>
  <section>
    <el-form label-width="100px">
      <el-form-item label="账户名">
        <el-input size="mini" v-if="formMember.edit" v-model="formMember.userName" style="width:190px"></el-input>
        <span v-else>{{formMember.userName}} </span>
      </el-form-item>
      <el-form-item  label="用户组">
        <el-select v-model="selGroup" size="mini" :disabled="!formMember.edit">
          <el-option v-for="group in groups" :key="group.id" :value="group.id" :label="group.groupName"></el-option>
        </el-select>
        <!--<span v-else>{{member.group.groupName}}</span>-->
      </el-form-item>
      <el-form-item label="电话号码">
        <el-input size="mini" v-if="formMember.edit" v-model="formMember.phone" style="width: 190px"></el-input>
        <span v-else>{{formMember.phone}} </span>
      </el-form-item>
      <el-form-item label="登录方式">
        <el-button type="text">管理</el-button>
      </el-form-item>
      <el-form-item label="">
        <el-button :type="formMember.edit?'success':'primary'" size="mini" @click="editBtn">{{formMember.edit?'保存':'编辑'}}</el-button>
        <el-button type="danger" size="mini" @click="cancelBtn" v-if="formMember.edit">取消</el-button>
      </el-form-item>
    </el-form>
  </section>
</template>

<script>
import Vue from 'vue'// 后续添加属性，需要vue联动
import errHandler from '@/assets/errHandler'
import {mapGetters, mapActions, mapMutations} from 'vuex'
import userGroup from '../UserGroup'
export default {
  name: 'edit-user-profile',
  components: {userGroup},
  props: ['member'],
  data () {
    return {
      selGroup: null,
      groups: null,
      formMember: {}
    }
  },
  created () {
    this.selGroup = this.member.group.id
    this.getGroups()
    this.formMember = {
      memberId: this.member.id,
      userName: this.member.userName,
      phone: this.member.phone
    }
  },
  methods: {
    ...mapActions({
      fetch: 'fetch'
    }),
    getGroups () {
      this.fetch({name: 'getUserGroupList'})
        .then(res => {
          console.log(res)
          this.groups = res
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    parseIsSex (value) {
      console.log(value)
      if (value == 1) return '男'
      if (value == 2) return '女'
    },
    parseIsMarried (value) {
      if (value) return '是'
      else return '否'
    },
    editBtn () {
      if (this.formMember.edit === undefined) {
        Vue.set(this.formMember, 'edit', true)// 后续添加属性，需要vue联动
        return
      }
      if (this.formMember.edit) {
        console.log('保存啦', this.formMember)
        this.updateMember(this.formMember)
      } else {
        this.formMember.edit = true
      }
    },
    cancelBtn () {
      if (this.formMember.edit === undefined) {
        Vue.set(this.formMember, 'edit', false)// 后续添加属性，需要vue联动
        return
      }
      this.formMember.edit = false
    },
    updateMember (data) {
      this.fetch({name: 'updateMember', data: {...data, groupId: this.selGroup}})
        .then(res => {
          console.log(res)
          data.edit = false
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    }
  }
}
</script>

<style lang="scss">
  #editingUser{
    .profile-tab{
      .profile-title{
        span{
          display: inline-block;
          vertical-align: baseline;
          height: 100%;
          line-height: 37px;
        }
        .role{
          font-size: 18px;
        }
      }
      .profile-img{
        width: 100%;
        /*border: 1px solid crimson;*/
        overflow: auto;
        display: flex;
        justify-content: center;
      }
      .img{
        height: 200px;
        width: 200px;
        box-shadow: black 0 0 5px 0;
        margin: 20px 0;
      };
      .profile-data{
        font-size: 16px;
        .label{
          display: inline-block;
          min-width: 120px;
          font-weight: bold;
          &:after{
            content:':'
          }
        }
        .value{
          display: inline-block;
        }
      }
    }
    .footer{
      margin-top: 25px;
    }
  }
</style>
