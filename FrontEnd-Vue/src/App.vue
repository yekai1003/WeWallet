
<template>

    <div id="app">

        <v-card height="200px" class="footernav">
            <v-bottom-nav
                    :active.sync="bottomNav"
                    :color="color"
                    :value="true"
                    absolute
                    shift
            >

                <router-link to="/">
                <v-btn dark>
                    <span>{{home}}</span>
                    <v-icon>home</v-icon>
                </v-btn>
                </router-link>


                <router-link to="/login">
                <v-btn dark>
                    <span>{{login}}</span>
                    <v-icon>loyalty</v-icon>
                </v-btn>
                </router-link>

                <router-link to="/about">
                <v-btn dark>
                    <span>{{about}}</span>
                    <v-icon>opacity</v-icon>
                </v-btn>
                </router-link>

                <router-link to="/my">
                <v-btn dark>
                    <span>{{my}}</span>
                    <v-icon>face</v-icon>
                </v-btn>
                </router-link>
            </v-bottom-nav>
        </v-card>
        <router-view/>
    </div>
</template>

<style lang="scss">

    #app {
        font-family: 'Avenir', Helvetica, Arial, sans-serif;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
        text-align: center;
        color: #2c3e50;
    }

    html {
        min-height: 500px;
    }

    .footernav {
        position: fixed!important;
        bottom: 0!important;
        width: 100%;
        z-index: 5;
        height: 60px!important;
    }

    a {
        text-decoration: none;
    }

</style>

<script>


    let home = 'Home',
        login = 'Login',
        about = 'About',
        my = "My";

    export default {
        data(){
            return {
                home,
                login,
                about,
                my,
                bottomNav: 0
            }
        },
        computed: {
            color () {
                switch (this.bottomNav) {
                    case 0: return 'blue-grey'
                    case 1: return 'teal'
                    case 2: return 'brown'
                    case 3: return 'indigo'
                }
            }
        },
        methods:{
            clickActive(e){
                console.log(e.target);
                console.log(1223);
            }
        }
    }


</script>
