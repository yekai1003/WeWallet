import login from '@/components/login'
import frame from '@/components/frame'
import Dashboard from '@/components/index'
import ClientPlatformManagement from '@/components/client'
import UsersManager from '@/components/user'
import memberEdit from '@/components/user/member/memberEdit'
import UserGroupManagement from '@/components/user/group/home'
import ApiManagement from '@/components/api'
import iconView from '@/components/icons/index'

const menuList = [
  {
    name: '登陆',
    href: '/login',
    hidden: true,
    component: login
  },
  {
    name: '首页',
    href: '/index',
    icon: 'regular/bookmark',
    parentComponent: frame,
    component: Dashboard
  },
  {
    name: '系统管理',
    href: '/system',
    icon: 'cogs',
    component: frame,
    children: [
      {
        name: '接口管理',
        href: '/system/api-management',
        icon: 'cog',
        component: ApiManagement
      }
    ]
  },
  {
    name: '账户管理',
    href: '/user',
    icon: 'users',
    component: frame,
    children: [
      {
        name: '用户管理',
        href: '/user/user-management',
        icon: 'user',
        component: UsersManager
      },
      {
        name: '用户编辑',
        href: '/user/member-edit/:id',
        icon: 'user',
        hidden: true,
        component: memberEdit
      },
      {
        name: '组管理',
        href: '/user/group-management',
        icon: 'users',
        component: UserGroupManagement
      }
    ]
  },
  {
    name: '客户端管理',
    href: '/client',
    icon: 'desktop',
    component: frame,
    children: [
      {
        name: '客户端编辑',
        href: '/client/client-management',
        icon: 'desktop',
        component: ClientPlatformManagement
      }
    ]
  },
  {
    name: '查看所有图标',
    href: '/icons',
    icon: 'regular/star',
    parentComponent: frame,
    component: iconView,
  }
]
export default menuList
