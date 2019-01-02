export default {
  mobilePhone (number) {
    const validation = number.search(/^((13[4-9])|(15[012789])|147|178|(18[23478]))[0-9]{8}$/)
    if (validation == 0) return true
    else return false
  }
}
