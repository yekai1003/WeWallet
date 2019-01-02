import authentication from './authentication'
import user from './user'
import api from './api'
import client from './client'
import power from './power'
import userGroups from './user-groups'

export default {
  ...power,
  ...client,
  ...authentication,
  ...user,
  ...api,
  ...userGroups
}
