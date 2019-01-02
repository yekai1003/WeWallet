<template>
  <div>
    <div v-for="n in icons" :key="n" style="display: block;width:260px;height:60px;float:left;">
      <span>{{n}}</span>
      <icon :name="n"></icon>
    </div>
  </div>
</template>

<script>
import Icon from 'vue-awesome'

export default {
  name: 'icon-index',
  data () {
    return {
      icons: []
    }
  },
  mounted () {
    // console.log(Icon.icons)

    for (let name in Icon.icons) {
      this.icons.push(name)
    }
  }
}
</script>

<style scoped>
  .fa-icon{
    width:30px;
    height:30px;
  }
</style>
