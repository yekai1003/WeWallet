
export default {
  addDays (mc, d) {
    // //取毫秒数
    // let mc = srcDate.getTime()
    // console.log(mc)
    // 算增量
    let amount = 1000 * 60 * 60 * 24 * d
    let newDate = new Date()
    newDate.setTime(mc + amount)
    return newDate
  },
  getDay (srcDate) {
    let day = srcDate.getDay()
    if (day === 0) {
      return '周日'
    } else if (day === 1) {
      return '周一'
    } else if (day === 2) {
      return '周二'
    } else if (day === 3) {
      return '周三'
    } else if (day === 4) {
      return '周四'
    } else if (day === 5) {
      return '周五'
    } else if (day === 6) {
      return '周六'
    }
  },
  parseDate (dateString) {
    if (dateString) {
      var date = new Date(dateString.replace(/-/, '/'))
      return date
    }
  },
  parseDatetime (dateString) {
    if (dateString) {
      var arr1 = dateString.split(' ')
      var sdate = arr1[0].split('-')
      var date = new Date(sdate[0], sdate[1] - 1, sdate[2])
      return date
    }
  },
  getMonthDays (y, m) {
    return [31, null, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][m] || (this.isLeapYear(y) ? 29 : 28)
  },
  isLeapYear (y) {
    return (y % 400 == 0) || (y % 4 == 0 && y % 100 != 0)
  },
  getWeekNumber (now) {
    var year = now.getFullYear()
    var month = now.getMonth()
    var days = now.getDate()
    // 那一天是那一年中的第多少天
    for (var i = 0; i < month; i++) {
      days += this.getMonthDays(year, i)
    }
    // 那一年第一天是星期几
    var yearFirstDay = new Date(year, 0, 1).getDay() || 7
    var week = null
    if (yearFirstDay == 1) {
      week = Math.ceil(days / 7)
    } else {
      days -= (7 - yearFirstDay + 1)
      week = Math.ceil(days / 7)
    }
    return week
  },
  format (srcDate, fmt) {
    var o = {
      'M+': srcDate.getMonth() + 1,
      'd+': srcDate.getDate(),
      'H+': srcDate.getHours(),
      'm+': srcDate.getMinutes(),
      's+': srcDate.getSeconds(),
      'S+': srcDate.getMilliseconds()
    }

    // 因位date.getFullYear()出来的结果是number类型的,所以为了让结果变成字符串型，下面有两种方法：

    if (/(y+)/.test(fmt)) {
      // 第一种：利用字符串连接符“+”给date.getFullYear()+""，加一个空字符串便可以将number类型转换成字符串。

      fmt = fmt.replace(RegExp.$1, (srcDate.getFullYear() + '').substr(4 - RegExp.$1.length))
    }
    for (var k in o) {
      if (new RegExp('(' + k + ')').test(fmt)) {
        // 第二种：使用String()类型进行强制数据类型转换String(date.getFullYear())，这种更容易理解。

        fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (('00' + o[k]).substr(String(o[k]).length)))
      }
    }
    return fmt
  }
}
