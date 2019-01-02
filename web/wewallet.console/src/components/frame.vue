<template>
  <el-container>
    <el-header height="50px" class="main-header animated" :class="{'aside-show':!sidebar.collapsed,'aside-hidden':sidebar.collapsed}">
      <a href="/" class="logo">
        <span><i class="fa fa-diamond"></i><b>System Platform</b></span>
      </a>
      <nav>
        <a href="#" class="sidebar-toggle" @click.stop.prevent="switchShowSide"></a>
        <div class="navbar-custom-menu">
          <!-- 消息 -->
          <el-dropdown class="msg-menu">
            <el-badge :value="msgCount">
              <i class="fa fa-envelope-o"></i>
            </el-badge>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item v-for="(item,index) in msgList" :key="index">
                <router-link :to="{path:'/sys/message',query:{id:item.id}}">
                  {{index + 1}}.{{item.title}}
                </router-link>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <!-- 头像 -->
          <el-dropdown v-if="userInfo">
            <div class="el-dropdown-link">
              <img :src='customerInfo ? customerInfo.avatarUrl:"/static/img/defaultAvatar.png"'
                   style="width: 25px;height: 25px;border-radius: 50%; vertical-align: middle;"
                   alt="U"/>
              {{userInfo.name}}
            </div>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item>
                <div>
                  <div class="header-pic">
                    <img :src='customerInfo ? customerInfo.avatarUrl:"/static/img/defaultAvatar.png"' class="img-circle" alt="User Image">
                    <p>{{userInfo.name}}</p>
                  </div>
                  <div class="pull-left">
                    <router-link :to="{ path: '/resetPwd' }">
                      <el-button size="small" type="default">修改密码</el-button>
                    </router-link>
                  </div>
                  <div class="pull-right">
                    <el-button size="small" type="default" @click="logout">退出</el-button>
                  </div>
                </div>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </nav>
    </el-header>
    <el-container>
      <el-aside :width="asideWidth" class="main-aside" style="padding-top:5px;">
        <el-scrollbar style="height:100%;" wrapClass="content-scrollbar">
          <el-menu style="border-right:0;" :default-active="activeMenu" router :collapse="sidebar.collapsed" :unique-opened="true" :collapse-transition="true">
            <sub-menu v-for="menu in menuList" v-if="!menu.hidden" :key="menu.href" :menu="menu"></sub-menu>
          </el-menu>
        </el-scrollbar>
      </el-aside>
      <el-container>
        <el-main>
          <el-breadcrumb separator="/" style="margin-bottom:20px;">
            <el-breadcrumb-item v-for="child in currentMenus" :key="child.href" :to="{ path: child.href }">{{child.name}}</el-breadcrumb-item>
          </el-breadcrumb>
          <transition mode="out-in" enter-active-class="fadeIn" leave-active-class="fadeOut" appear>
            <router-view></router-view>
          </transition>
        </el-main>
        <el-footer class="main-footer">
          Copyright &copy; 2018-2019 <a href="http://www.crzycn.com">时瑞健康产业有限公司</a>. All rights reserved.
        </el-footer>
      </el-container>
    </el-container>
  </el-container>
</template>

<script>
import {mapGetters, mapActions, mapMutations} from 'vuex'
import types from '@/store/mutation-types'
import SubMenu from './layout/SubMenu'

export default {
  components: { SubMenu },
  name: 'frame',
  data () {
    return {
      msgList: [{ title: '第一条消息' }, { title: '第二条消息' }],
      activeMenu: null
    }
  },
  watch: {
    sidebarCollapsed (nv, ov) {
      // console.log(nv,ov)
      if (nv) { this.activeMenu = this.$route.fullPath }
    }
  },
  computed: {
    asideWidth () {
      if (!this.sidebar.collapsed) { return '230px' } else { return '50px' }
    },
    msgCount () {
      return this.msgList.length
    },
    ...mapGetters({
      sidebar: 'sidebar',
      userInfo: 'userInfo',
      customerInfo: 'customerInfo',
      currentMenus: 'currentMenus',
      menuList: 'menuList'
    }),
    sidebarCollapsed () {
      return this.sidebar.collapsed
    }
  },
  mounted () {
    // 加载用户信息(每次刷新都需要获取的)
    this.getUser()
    // 加载菜单
    this.loadMenu()
    // 设置当前页面 第一加载的时候 菜单还没有，所以设置下
    // console.log(this.$route)
    this.activeMenu = this.$route.fullPath
    this.setCurrMenu(this.$route)
  },
  methods: {
    ...mapMutations({
      toggleSidebar: types.TOGGLE_SIDEBAR,
      setUserInfo: types.SET_USER_INFO,
      setCustomerInfo: types.SET_CUSTOMER_INFO,
      disconnect: 'disconnect'
    }),
    ...mapActions({
      fetch: 'fetch',
      setCurrMenu: 'changeCurrentMenu',
      loadMenu: 'loadMenuList'
    }),
    switchShowSide () {
      this.toggleSidebar()
    },
    logout () {
      this.disconnect({callback: () => {
        this.$router.push({ path: '/login?redirect=' + this.$route.path })
      }})
    },
    getUser () {
      this.fetch({name: 'getSelfInfo', method: 'post'})
        .then(res => {
          // Store the user on the store
          if (res.errCode) {
            this.$message({
              message: res.errMsg,
              type: 'error'
            })
          } else {
            this.setUserInfo(res)
          }
        }).catch(error => {
          console.log(error)
          this.$message({
            message: '获取用户错误',
            type: 'error'
          })
        })
      // 获取客户信息
      // this.getCurrInfo() ——————————————————————————————————————————————————————————————不知道下一个方法是干啥的？
    },
    getCurrInfo () {
      this.fetch({
        name: 'getCurrInfo',
        method: 'get'
      }).then(res => {
        if (res.errCode) {
          this.$message({
            message: res.errMsg,
            type: 'error'
          })
        } else {
          this.setCustomerInfo(res)
        }
      }).catch(err => {
        console.log(err)
        this.$message({
          message: '获取客户错误',
          type: 'error'
        })
      })
    }
  }
}
</script>

<style lang="scss">
  .main-header {
    padding: 0;
    box-shadow: 0 2px 3px hsla(0, 0%, 7%, .1), 0 0 0 1px hsla(0, 0%, 7%, .1);
    .logo {
      -webkit-transition: width 0.3s ease-in-out;
      -o-transition: width 0.3s ease-in-out;
      transition: width 0.3s ease-in-out;
      float: left;
      margin-top: 6px;
      font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
      display: block;
      font-size: 20px;
      font-weight: 300;
      span {
        display: block;
        height: 38px;
        line-height: 38px;
        overflow: hidden;
        i {
          padding: 0 15px
        }
      }
    }
    &.aside-hidden {
      .logo {
        width: 50px;
        b {
          display: none
        }
      }
    }
    &.aside-show {
      .logo {
        width: 230px;
      }
    }
    .sidebar-toggle {
      float: left;
      background-color: transparent;
      background-image: none;
      padding: 15px 15px;
      font-family: fontAwesome;
      line-height: 20px;
      &:before {
        content: "\F03B";
      }
    }
    .navbar-custom-menu {
      float: right;
      .msg-menu {
        cursor: pointer;
        margin: 15px 20px 0 0;
        .fa {
          font-size: 18px;
        }
      }
      .el-dropdown-link {
        cursor: pointer;
        margin: 0 20px 0 0;
        min-width: 50px;
        text-align: center;
      }
    }
  }
  .el-dropdown-menu .header-pic {
    text-align: center;
    .img-circle {
      vertical-align: middle;
      height: 90px;
      width: 90px;
      border-color: transparent;
      border-color: hsla(0, 0%, 100%, .2);
    }
    p {
      font-size: 15px;
      color: #666666;
    }
  }

  .el-dropdown-menu .pull-left {
    padding-right: 10px;
    display: inline-block;
    float: left;
  }

  .el-dropdown-menu .pull-right {
    padding-left: 10px;
    float: right;
    display: inline-block;
  }

  .animated {
    animation-duration: .2s;
  }

  .main-aside {
    height: calc(100vh - 50px);
    border-right: 1px solid #e6e6e6;
    -webkit-transition: -webkit-transform 0.3s ease-in-out, width 0.3s ease-in-out;
    -moz-transition: -moz-transform 0.3s ease-in-out, width 0.3s ease-in-out;
    -o-transition: -o-transform 0.3s ease-in-out, width 0.3s ease-in-out;
    transition: transform 0.3s ease-in-out, width 0.3s ease-in-out;
  }

  .main-footer {
    transition: transform 0.3s ease-in-out, margin 0.3s ease-in-out;
    text-align: center;
    padding: 5px 15px;
    color: #444;
    width: 100%;
    a {
      color: cadetblue;
      &:hover {
        color: #666;
      }
    }
  }
  .el-menu--vertical {
    .el-menu--popup {
      .el-menu-item {
        height:45px;
        line-height:45px;
      }
    }
  }
  /* 这样可以把 横向的滚动条去掉，不知道为什么会有横向滚动条*/
  .content-scrollbar{
    height: calc(100vh - 43px);
  }
</style>
