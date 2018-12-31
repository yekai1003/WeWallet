<template>
    <div class="login">

        <v-tabs
                centered
                color="blue"
                dark
                icons-and-text
        >
            <v-tabs-slider color="yellow"></v-tabs-slider>

            <v-tab href="#tab-1">
                登录
                <v-icon>account_box</v-icon>
            </v-tab>

            <v-tab href="#tab-2">
                注册
                <v-icon>add_to_queue</v-icon>
            </v-tab>

            <v-tab-item
                    :id="'tab-1'"
                    :key="1"
            >
                <form>
                    <v-text-field
                            v-model="newUser.login_username"
                            :error-messages="loginUserNameErrors"
                            label="userName"
                            required
                            @input="$v.newUser.login_username.$touch()"
                            @blur="$v.newUser.login_username.$touch()"
                    ></v-text-field>
                    <v-text-field
                            v-model="newUser.login_password"
                            :error-messages="loginPasswordErrors"
                            label="password"
                            type="password"
                            required
                            @input="$v.newUser.login_password.$touch()"
                            @blur="$v.newUser.login_password.$touch()"
                    ></v-text-field>

                    <v-btn @click="loginSubmit">登录</v-btn>
                    <v-btn @click="clear">清空</v-btn>
                </form>
            </v-tab-item>

            <v-tab-item
                    :id="'tab-2'"
                    :key="2"
            >
                <form>
                    <v-text-field
                            v-model="newUser.regist_username"
                            :error-messages="registUserNameErrors"
                            label="userName"
                            required
                            @input="$v.newUser.regist_username.$touch()"
                            @blur="$v.newUser.regist_username.$touch()"
                    >
                    </v-text-field>
                    <v-text-field
                            v-model="newUser.regist_password"
                            :error-messages="registPasswordErrors"
                            label="password"
                            type="password"
                            required
                            @input="$v.newUser.regist_password.$touch()"
                            @blur="$v.newUser.regist_password.$touch()"
                    ></v-text-field>
                    <v-text-field
                            v-model="newUser.regist_confirmpassword"
                            :error-messages="registConfirmPasswordErrors"
                            label="confirmPassword"
                            type="password"
                            required
                            @input="$v.newUser.regist_confirmpassword.$touch()"
                            @blur="$v.newUser.regist_confirmpassword.$touch()"
                    ></v-text-field>

                    <v-btn @click="registSubmit">注册</v-btn>
                    <v-btn @click="clear">清空</v-btn>
                </form>
            </v-tab-item>
        </v-tabs>

    </div>
</template>

<script>

    import { required, maxLength, minLength, sameAs } from 'vuelidate/lib/validators'

    import axios from 'axios'

    export default {
        name: "Login",
        data: ()=>({
            newUser: {
                login_username: '1',
                login_password: '2',
                regist_username: '3',
                regist_password: '4',
                regist_confirmpassword: '5'
            }
        }),
        validations: {
            newUser: {
                username: {
                    required,
                    maxLength: maxLength(10)
                },
                password: {
                    required,
                    minLength: minLength(8)
                },
                confirmpassword: {
                    required,
                    sameAsPassword: sameAs('password')
                }
            }
        },

        computed: {
            loginUserNameErrors(){
                let errors = []

                if (!this.$v.newUser.username.$dirty) return errors
                !this.$v.newUser.username.maxLength && errors.push('Name can not be more than 10 characters long')
                !this.$v.newUser.username.required && errors.push('Name is required.')
                return errors
            },
            loginPasswordErrors(){
                let errors = []
                if (!this.$v.newUser.password.$dirty) return errors
                !this.$v.newUser.password.minLength && errors.push('Password must be at least 8 characters long')
                !this.$v.newUser.password.required && errors.push('Password is required.')
                return errors
            },


            registUserNameErrors(){
                let errors = []
                if (!this.$v.newUser.username.$dirty) return errors
                !this.$v.newUser.username.maxLength && errors.push('Name can not be more than 10 characters long')
                !this.$v.newUser.username.required && errors.push('Name is required.')
                return errors
            },
            registPasswordErrors(){
                let errors = []
                if (!this.$v.newUser.password.$dirty) return errors
                !this.$v.newUser.password.minLength && errors.push('Password must be at least 8 characters long')
                !this.$v.newUser.password.required && errors.push('Password is required.')
                return errors
            },
            registConfirmPasswordErrors(){
                let errors = []
                if (!this.$v.newUser.password.$dirty) return errors
                !this.$v.newUser.confirmpassword.sameAsPassword && errors.push('ConfirmPassword must be same as Password')
                return errors
            }

        },


        methods: {
            loginSubmit() {
                console.log(this.newUser.username);
                console.log("a",axios);
                this.$v.newUser.$touch()
                axios({
                    /*url: "/api/v1/bpi/currentprice.json",
                    method: 'get',*/
                    method: 'post',
                    data: {
                        userName: this.newUser.login_username,
                        password: this.newUser.login_password
                    }
                }).then(res=>{
                    //console.log(res.data.bpi);
                    console.log(res);
                }).catch(err=>{
                    console.log(err);
                })
            },
            registSubmit(){
                this.$v.newUser.$touch()
                axios({
                    method: 'post',
                    data: {
                        userName: this.newUser.regist_username,
                        password: this.newUser.regist_password
                    }
                }).then(res=>{
                    console.log(res);
                }).catch(err=>{
                    console.log(err);
                })
            },
            clear() {
                this.$v.$reset()
                this.newUser.username = ''
                this.newUser.password = ''
                this.newUser.confirmpassword = ''
            }
        }
    }
</script>

<style lang="scss">

    .v-toolbar__content {
        height: 0!important;
    }

    .v-tabs__item--active {
        border-bottom: 4px solid teal;
    }
    .v-window {
        width: 80%!important;
        //height: 300px!important;
        margin: 3rem auto;
        padding: 0.1rem 0 .1rem;
    }
    .v-window__container {
        //height: 300px!important;
    }

    form {
        width: 60%!important;
        margin: 4rem auto!important;
        button {
            margin-top: 2rem!important;

        }
        .v-input {
            margin-bottom: 1rem;
        }
        input {
            padding: 1.2rem 0!important;
        }
        .v-messages__message {
            color: #ff0729;
            font-size: 1rem;
        }
        .v-label--active {
            color: #2196F3!important;
            font-weight: bold;
            font-size: 1.5rem;
        }
    }

</style>