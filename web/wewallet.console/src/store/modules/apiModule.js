export default  {
  state: {
    apiList:[],
  },
  getters: {
    apiList: (state) => {
      return state.apiList;
    },
  },
  mutations: {
    setApiList: (state, payload) => {
      state.apiList = payload.apiList;
    },

  }
};
