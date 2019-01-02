<template>
  <el-submenu v-if="!menu.hidden && menu.children && menu.children.length > 0" :index="menu.href">
    <template slot="title">
      <!--<i v-if="menu.icon" :class="menu.icon"></i>-->
      <icon v-if="menu.icon" :name="menu.icon"></icon>
      <span slot="title">{{menu.name}}</span>

    </template>
    <sub-menu v-for="sub in menu.children" :key="sub.href" :menu="sub"></sub-menu>
  </el-submenu>
  <el-menu-item v-else-if="!menu.hidden" :index="menu.href">
    <icon v-if="menu.icon" :name="menu.icon"></icon>
    <span slot="title">{{menu.name}}</span>
  </el-menu-item>
</template>

<script>
export default {
  name: 'sub-menu',
  props: ['menu']
}
</script>

<style scoped>

</style>
