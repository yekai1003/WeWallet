<template>
  <header class="main-header animated" :class="{closeLogo:sidebar.collapsed,mobileLogo:device.isMobile}">
    <a href="#" class="logo">
      <span class="logo-lg"><i class="fa fa-diamond"></i>&nbsp; <b>System Admin</b></span>
    </a>
    <nav class="navbar">
      <a href="#" class="sidebar-toggle" data-toggle="offcanvas" role="button"
         @click.stop.prevent="toggleMenu(!sidebar.collapsed,device.isMobile)">
        <span class="sr-only">Toggle navigation</span>
      </a>
      <div class="navbar-custom-menu">
        <!-- 消息 -->
        <el-dropdown class="navbar-dropdown">
          <div class="el-dropdown-link" style="height: auto;line-height: inherit">
            <el-badge :value="count" class="item">
              <i class="fa fa-envelope-o"></i>
            </el-badge>
          </div>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item>
              <ul class="message-list">
                <li v-for="(item,index) in list" :key="index"><!-- start message -->
                  <router-link :to="{path:'/sys/message',query:{id:item.id}}">
                    <p>{{index + 1}}. {{item.title}}</p>
                  </router-link>
                </li>
              </ul>
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <!-- 头像 -->
        <el-dropdown v-if="userInfo" class="navbar-dropdown">
          <div class="el-dropdown-link">
            <img :src='userInfo.headImgUrl' style="width: 25px;height: 25px;border-radius: 50%; vertical-align: middle;"
                 alt="U" />
            {{userInfo.name}}
          </div>
          <el-dropdown-menu style="padding: 0px" slot="dropdown">
            <el-dropdown-item>
              <div>
                <div class="header-pic">
                  <img :src='userInfo.avatarUrl' class="img-circle" alt="User Image" >
                  <p>{{userInfo.name}}</p>
                </div>
                <div class="pull-left">
                  <router-link :to="{ path: '/resetPwd' }">
                    <el-button type="default">修改密码</el-button>
                  </router-link>
                </div>
                <div class="pull-right">
                  <el-button type="default" @click="logout">退出</el-button>
                </div>
              </div>
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </nav>
  </header>
</template>

<script>
import {mapGetters, mapActions, mapMutations} from 'vuex'
import types from '../../store/mutation-types'

export default {
  name: 'i-header',
  data () {
    return {
      showMessageBox: false,
      showProfileBox: false,
      list: [],
      count: 4,
      show: true
    }
  },
  computed: mapGetters({
    sidebar: 'sidebar',
    userInfo: 'userInfo',
    device: 'device'
  }),
  created () {
    // let item = window.sessionStorage.getItem("user-info");
    // if (!!item) {
    //   this.setUserInfo(JSON.parse(item));
    // }
    let tmpMsgList = [{'id': '310000201511210936', 'type': 1, 'code': 102, 'timeLine': '1975051514', 'message': '元派目想铁难而应才院色种离。义两作期复铁节被制等头设。此能论装受我电局代算素业用我。', 'title': '除口方增常处装公会深此面', 'createTime': '1192729032451', 'senderName': 'Smith', 'senderPic': 'http://dummyimage.com/100x100/d9f279/757575.png&text=S'}, {'id': '460000201611256678', 'type': 0, 'code': 108, 'timeLine': '1972022907', 'message': '门可太长消题四义人素分天习。', 'title': '车主精论状第算整京联科出报', 'createTime': '1019549821001', 'senderName': 'Anderson', 'senderPic': 'http://dummyimage.com/100x100/e779f2/757575.png&text=A'}, {'id': '21000019780808275X', 'type': 1, 'code': 105, 'timeLine': '1970031803', 'message': '指给西着林为计着布同细认产。', 'title': '正白要外高情总儿才必响', 'createTime': '503250769103', 'senderName': 'Clark', 'senderPic': 'http://dummyimage.com/100x100/79f2c4/757575.png&text=C'}, {'id': '540000197505267710', 'type': 1, 'code': 106, 'timeLine': '2010020800', 'message': '变没装调标矿劳土类角九至由。特引南素都则次采特分义管装设青工率新。海者快决角定标门段越美流东厂体。', 'title': '马争有战采圆还使层极口队理任目器万干', 'createTime': '1282562311030', 'senderName': 'Anderson', 'senderPic': 'http://dummyimage.com/100x100/f2a179/757575.png&text=A'}, {'id': '120000201607082136', 'type': 0, 'code': 110, 'timeLine': '1995042315', 'message': '议价进则族外代水白深白离系。质体们劳确作水今领议圆个中处每容江。', 'title': '看变易精最领得都', 'createTime': '557022187618', 'senderName': 'Martinez', 'senderPic': 'http://dummyimage.com/100x100/7d79f2/757575.png&text=M'}, {'id': '310000199103172213', 'type': 1, 'code': 107, 'timeLine': '1993081322', 'message': '和油得离接装小都空看来响资。部算调法土图毛重知参时图改用化龙观地。', 'title': '史运美很且应部想器发此精', 'createTime': '331067892810', 'senderName': 'Hernandez', 'senderPic': 'http://dummyimage.com/100x100/97f279/757575.png&text=H'}, {'id': '310000201408208918', 'type': 0, 'code': 107, 'timeLine': '1996071523', 'message': '影将度更调图界实交接外他度龙习。米战走加系区日委也总图日。', 'title': '高按我发史社就高水复周', 'createTime': '1115948146226', 'senderName': 'Robinson', 'senderPic': 'http://dummyimage.com/100x100/f279bb/757575.png&text=R'}, {'id': '500000200504263440', 'type': 0, 'code': 101, 'timeLine': '1972032318', 'message': '都八在取音特业划克天造求照般。', 'title': '整家水山又知整新便分现物精划放干', 'createTime': '1241409165029', 'senderName': 'Clark', 'senderPic': 'http://dummyimage.com/100x100/79def2/757575.png&text=C'}, {'id': '320000198908103896', 'type': 1, 'code': 105, 'timeLine': '2009010906', 'message': '当式名厂采物且计料三公切点应更民体。内反温干华点方上等或划消但名反到准。', 'title': '况别联东红后还由列级任法向', 'createTime': '138005376045', 'senderName': 'Walker', 'senderPic': 'http://dummyimage.com/100x100/f2e279/757575.png&text=W'}, {'id': '330000199709134661', 'type': 0, 'code': 102, 'timeLine': '2007031214', 'message': '都种形一必采海想元的调次位府团系。社界个向但所历亲器它争计积相所为记。适心称备叫近理或种使水算党须今划文。', 'title': '酸约况实改火间子直后约质影反', 'createTime': '434811164417', 'senderName': 'Thompson', 'senderPic': 'http://dummyimage.com/100x100/bf79f2/757575.png&text=T'}]
    this.list = tmpMsgList
    this.count = this.list.length
  },
  methods: {
    ...mapMutations({
      toggleSidebar: types.TOGGLE_SIDEBAR,
      toggleSidebarShow: types.TOGGLE_SIDEBAR_SHOW,
      setUserInfo: types.SET_USER_INFO,
      disconnect: 'disconnect'
    }),
    toggleMenu (collapsed, isMobile) {
      if (isMobile) {
        this.toggleSidebarShow()
      } else {
        this.toggleSidebar()
      }
    },
    logout () {
      this.disconnect(() => {
        this.$router.push({ path: '/login?redicrect=' + this.$route.path })
      })
    },
    toggleMessage () {
      this.showMessageBox = !this.showMessageBox
    },
    toggleProfile () {
      this.showProfileBox = !this.showProfileBox
    },
    autoHide (evt) {
      if (!this.$el.querySelector('li.messages-menu').contains(evt.target)) {
        this.showMessageBox = false
      }
      if (!this.$el.querySelector('li.user-menu').contains(evt.target)) {
        this.showProfileBox = false
      }
    }
  }
}
</script>

<style lang="scss">
  .animated {
    animation-duration: .2s;
  }

  .main-header a {
    text-decoration: none;
    color: #48576a;
  }

  .main-header {
    position: fixed;
    min-width: 100%;
    box-shadow: 0 2px 3px hsla(0, 0%, 7%, .1), 0 0 0 1px hsla(0, 0%, 7%, .1);
    z-index: 1999;
    animation-name: slideInDown;
    animation-fill-mode: both;
    color: #48576a;
  }

  .main-header .navbar .sidebar-toggle {
    float: left;
    background-color: transparent;
    background-image: none;
    padding: 15px 15px;
    font-family: fontAwesome;
    line-height: 20px;
  }

  .main-header .navbar .sidebar-toggle:before {
    content: "\f03b";
  }

  .main-header {
    background-color: #ffffff;
  }

  .main-header .logo {
    -webkit-transition: width 0.3s ease-in-out;
    -o-transition: width 0.3s ease-in-out;
    transition: width 0.3s ease-in-out;
    display: block;
    float: left;
    height: 50px;
    font-size: 20px;
    line-height: 50px;
    text-align: center;
    width: 230px;
    font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
    padding: 0 8px;
    font-weight: 300;
    overflow: hidden;
    text-decoration: none;
  }

  .main-header .logo .logo-lg {
    display: block;
    height: 38px;
    line-height: 38px;
    margin-top: 6px;
  }

  .main-header .navbar {
    -webkit-transition: margin-left 0.3s ease-in-out;
    -o-transition: margin-left 0.3s ease-in-out;
    transition: margin-left 0.3s ease-in-out;
    margin-bottom: 0;
    margin-left: 230px;
    border: none;
    min-height: 50px;
    border-radius: 0;
    background-color: #ffffff;
  }

  .layout-top-nav .main-header .navbar {
    margin-left: 0;
  }

  body.hold-transition .main-header .navbar,
  body.hold-transition .main-header .logo {
    -webkit-transition: none;
    -o-transition: none;
    transition: none;
  }

  .main-header .navbar .sidebar-toggle {
    display: block;
  }

  .main-header .navbar .sidebar-toggle:hover {
    background: #f9f9f9;
  }

  .main-header .logo {
    border-bottom: 0 solid transparent;
  }

  @media (max-width: 800px) {

    .main-header .logo {
      display: none;
    }

    .main-header .navbar {
      margin: 0;
    }

    .main-header .logo, .main-header .navbar {
      width: 100%;
      float: none;
    }

  }

  .main-header.closeLogo .navbar {
    margin-left: 44px;
  }

  .main-header.closeLogo .logo {
    width: 44px;
    padding: 0 8px;
  }

  .main-header.closeLogo .logo .logo-lg b {
    display: none;
  }

  .main-header.closeLogo .sidebar-toggle {
    background: #f9f9f9;
  }

  .main-header.closeLogo .navbar .sidebar-toggle:before {
    content: "\f03c";
  }

  .main-header.mobileLogo .navbar .sidebar-toggle:before {
    content: "\f03a";
  }

  .navbar-custom-menu {
    float: right;
  }

  .navbar-custom-menu .el-dropdown-link {
    cursor: pointer;
    height: 50px;
    padding: 13px 5px;
    min-width: 50px;
    text-align: center;
  }

  .navbar-custom-menu .el-dropdown-link img {
    background-color: #108ee9;
  }

  .navbar-custom-menu .el-dropdown-link:hover {
    background: #f9f9f9;
  }

  .message-list {
    list-style: none;
    padding: 0 10px;
  }

  .message-list li {
    list-style: none;
    height: 25px;
    line-height: 25px;
  }

  .message-list li a {
    text-decoration: none;
    color: #666666;
  }

  .message-list li:hover {
    background-color: #f9f9f9;
  }

  .el-dropdown-menu .header-pic {
    text-align: center;
    padding: 20px;
  }

  .el-dropdown-menu .header-pic img {
    vertical-align: middle;
    height: 90px;
    width: 90px;
    border: 3px solid;
    border-color: transparent;
    border-color: hsla(0, 0%, 100%, .2);
  }

  .el-dropdown-menu .header-pic p {
    font-size: 1.5rem;
    color: #666666;
  }

  .el-dropdown-menu .pull-left {
    padding: 10px;
    display: inline-block;
    float: left;
  }

  .el-dropdown-menu .pull-right {
    padding: 10px;
    float: right;
    display: inline-block;
  }
</style>
