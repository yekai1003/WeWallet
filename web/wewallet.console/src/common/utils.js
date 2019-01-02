import pathToRegexp from 'path-to-regexp'

export const getSessionKey = (key, defaultValue) => {
  const item = window.sessionStorage.getItem(key)
  if (item == undefined && defaultValue != undefined) {
    return defaultValue
  }
  return item
}

export const getBaseUrl = (url) => {
  var reg = /^((\w+):\/\/([^\/:]*)(?::(\d+))?)(.*)/
  reg.exec(url)
  return RegExp.$1
}

export const keyMirror = (obj) => {
  let key
  let mirrored = {}
  if (obj && typeof obj === 'object') {
    for (key in obj) {
      if ({}.hasOwnProperty.call(obj, key)) {
        mirrored[ key ] = key
      }
    }
  }
  return mirrored
}

/**
 * 数组格式转树状结构
 * @param   {array}     array
 * @param   {String}    id
 * @param   {String}    pid
 * @param   {String}    children
 * @return  {Array}
 */
export const arrayToTree = (array, id = 'id', pid = 'pid', children = 'children') => {
  let data = array.map(item => ({ ...item }))
  let result = []
  let hash = {}
  data.forEach((item, index) => {
    hash[ data[ index ][ id ] ] = data[ index ]
  })

  data.forEach((item) => {
    let hashVP = hash[ item[ pid ] ]
    if (hashVP) {
      !hashVP[ children ] && (hashVP[ children ] = [])
      hashVP[ children ].push(item)
    } else {
      result.push(item)
    }
  })
  return result
}

export function getCurrentMenu (location, arrayMenu) {
  if (arrayMenu) {
    let current = []
    for (let i = 0; i < arrayMenu.length; i++) {
      const e = arrayMenu[ i ]
      const child = getCurrentMenu(location, e.children)
      if (!!child && child.length > 0) {
        child.push({ ...e, children: null })
        return child
      }
      if (e.href && pathToRegexp(e.href).exec(location)) {
        current.push({ ...e, children: null })
        return current
      }
    }
    return current
  }
  return null
}
export function deepClone (obj) {
  var _tmp, result
  _tmp = JSON.stringify(obj)
  result = JSON.parse(_tmp)
  return result
}

export function generateMenuFromMenuData (menus) {
  let rets = []
  menus.forEach(menu => {
    let item = {
      href: menu.href,
      name: menu.name,
      icon: menu.icon
    }
    if (menu.hidden) { item.hidden = menu.hidden }
    if (menu.children) { item.children = generateMenuFromMenuData(menu.children) }
    rets.push(item)
  })
  return rets
}

/**
 * 从菜单生成路由信息
 * @param menus
 * @returns {Array}
 */
export function generateRoutesFromMenu (menus) {
  let rets = []
  menus.forEach((menu) => {
    let item = {
      path: menu.href
    }
    item.component = menu.component
    if (menu.children) {
      // children
      item.children = generateRoutesFromMenu(menu.children)
    }
    // 如果有parentComponent
    if (menu.parentComponent) {
      let pItem = {path: '', component: menu.parentComponent, children: []}
      pItem.children.push(item)
      rets.push(pItem)
    } else { rets.push(item) }
  })
  return rets
}
