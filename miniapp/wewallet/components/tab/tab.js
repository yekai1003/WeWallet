// components/tab.js
Component({
  relations: {
    './tab-item':{
      type: 'child',// 关联的目标节点应为子节点
      linked: target=>{
        // 每次有tab-item被插入时执行，target是该节点实例对象，触发在该节点attached生命周期之后
      },
      linkChanged:target=>{
        // 每次有tab-item被移动后执行，target是该节点实例对象，触发在该节点moved生命周期之后
      },
      unlinked: target =>{
        // 每次有tab-item被移除时执行，target是该节点实例对象，触发在该节点detached生命周期之后
      }
    }
  },
  /**
   * 组件的属性列表
   */
  properties: {

  },

  /**
   * 组件的初始数据
   */
  data: {
    items: null,
    tabs:null,
    currentTab: 0,
    animationData:{},
    hAnimationData:{},
    status:1, //1大，2小
    tabDatas:[],
    hData:null,
    hOffSet:0,
    hTotalWidth:0,
    hTouchData: { start: null, end: null},
    cTouchData: { start: null, end: null}
  },

  ready: function () {
    //console.log('---ready called----')
    this._getAllItem()
    //显示第一个选项
    if (this.data.items.length > 0)
      this.data.items[0].setData({
        isHidden:false
      })
    //创建动画   内容框动画
    this.animation = wx.createAnimation({
      duration: 400,
      timingFunction: 'ease'
    })
    //创建动画   选项卡动画
    this.hAnimation = wx.createAnimation({
      duration: 400,
      timingFunction: 'ease'
    })
    //获取控件size
    let query = wx.createSelectorQuery().in(this)
    query.select('.swiper-tab').boundingClientRect(rect => {
     // console.log('------rect:', rect)
      this.data.hData=rect
    })
    for (let i = 0; i < this.data.tabs.length; i++) {
      query.select('#item-' + i).boundingClientRect(rect=>{
        //console.log(rect)
        this.data.hTotalWidth += rect.width
        this.data.tabDatas.push(rect)
      })
    }
    query.exec()//统一获取！
  },
  /**
   * 组件的方法列表
   */
  methods: {
    animationIn(item) {
      item.setData({
        isHidden: false
      })
      setTimeout(() => {
        this.animation.scale(1).step()
        this.setData({
          animationData: this.animation.export(),
          status: 1
        })
      }, 500)
    },
    animationOut(item){
      this.animation.scale(0.2).step()
      this.setData({
        animationData: this.animation.export(),
        status: 2
      })
      item.setData({
        isHidden: true,
      })
    },
    animationTab(current){
      //当选项卡 n+1 后宽度如果超过 h 宽度，左移一个，如果没有 1了就不移动
      let width = 0
      let x = current
      if(x+1 <= this.data.tabDatas.length-1)
        x+=1
      for(let i=0;i<=x;i++)
        width+= this.data.tabDatas[i].width
      if ((width + this.data.hOffSet)>=this.data.hData.width)//向后移
        this.data.hOffSet = this.data.hData.width - width
      else if(width < this.data.hData.width)
        this.data.hOffSet = 0 //这个算法还是有问题，需要改进
      this.moveTabH()
    },
    moveTabH() {
      this.hAnimation.translateX(this.data.hOffSet).step()
      this.setData({
        hAnimationData: this.hAnimation.export()
      })
    },
    //点击切换
    clickTab: function (e) {
      if (this.data.currentTab === e.target.dataset.current) {
        return false;
      } else {
        this.changeTab(e.target.dataset.current)
      }
    },
    changeTab: function (index) {
      //选项卡移动
      this.animationTab(index)
      //设置隐藏
      this.animationOut(this.data.items[this.data.currentTab])
      //设置当前值
      this.setData({
        currentTab: index
      })
      //设置显示
      this.animationIn(this.data.items[this.data.currentTab])
    },
    hMoveStart(e){
      // console.log(e)
      this.data.hTouchData.start=e.changedTouches[0]
    },
    hMoveEnd(e) {
      this.data.hTouchData.end = e.changedTouches[0]
      //X向 偏移计算
      let offset = this.data.hTouchData.end.clientX - this.data.hTouchData.start.clientX
      if (Math.abs(offset) < 10) return//移动太少，可能不是移动，是点击
      if((this.data.hOffSet+offset) > 0) this.data.hOffSet=0
      else if ((this.data.hOffSet + offset) < (this.data.hData.width - this.data.hTotalWidth)){
        if(this.data.hData.width>this.data.hTotalWidth) this.data.hOffSet=0
        else this.data.hOffSet = this.data.hData.width - this.data.hTotalWidth
      }else
        this.data.hOffSet+=offset
      this.moveTabH()
    },
    cMoveStart(e) {
      // console.log(e)
      this.data.cTouchData.start = e.changedTouches[0]
    },
    cMoveEnd(e) {
      // console.log(e)
      this.data.cTouchData.end = e.changedTouches[0]
      //X向 偏移计算
      let offset = this.data.cTouchData.end.clientX - this.data.cTouchData.start.clientX
      if (Math.abs(offset) < 10) return//移动太少，可能不是移动，是点击
      if (offset < 0) {//向右移动一格
        if (this.data.currentTab === this.data.items.length - 1) return
        this.changeTab(this.data.currentTab+1)
      } else {
        if (this.data.currentTab === 0) return
        this.changeTab(this.data.currentTab-1)
      }
    },
    test:function(e){
      this._getAllItem()
    },
    _getAllItem: function () {
      // 使用getRelationNodes可以获得nodes数组，包含所有已关联的tab-item，且是有序的
      let nodes = this.getRelationNodes('./tab-item')
      //console.log(nodes)
      let tmpTabs =[]
      nodes.forEach(item=>{
        tmpTabs.push(item.data.header)
      })
      this.setData({
        items:nodes,
        tabs:tmpTabs
      })
    }
  }
})
