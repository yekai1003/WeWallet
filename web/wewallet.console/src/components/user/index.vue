<template>
  <section>
    <el-tabs value="first">
      <el-tab-pane label="账户管理" name="first">
        <member></member>
      </el-tab-pane>
    </el-tabs>
  </section>
</template>
<script>
import member from './member'
import {mapGetters} from 'vuex'
export default {
  name: 'user-manager',
  components: {member},
  computed: {
    ...mapGetters({
      user: 'userInfo'
    })
  },
  data () {
    return {
    }
  },
  methods: {
  },
  created () {

  }
}
</script>
