import dateFormat from 'date-fns/format'
import zhLocale from 'date-fns/locale/de'

export default{
  format (date) {
    return dateFormat(date, 'YYYY-MM-DD', {locale: zhLocale})
  },
  formatDatetime (dateTime) {
    return dateFormat(dateTime, 'YYYY-MM-DD HH:mm:ss', {locale: zhLocale})
  }
}
