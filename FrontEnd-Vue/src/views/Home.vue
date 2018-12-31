<template>
    <div class="home">
        <img alt="Vue logo" src="../assets/logo.png">
        <!--<HelloWorld msg="Welcome222 to Your Vue.js App"/>-->

        <h3 class="blue-grey darken-2 white--text display-2">This is Home Page</h3>
    </div>
</template>

<script>
    // @ is an alias to /src
    import HelloWorld from '@/components/HelloWorld.vue'

    export default {
        name: 'home',
        components: {
            HelloWorld
        }
    }
</script>
