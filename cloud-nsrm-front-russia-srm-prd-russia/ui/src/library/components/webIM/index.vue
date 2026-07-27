<template>
  <div
    id="srmIM"
    v-drag-div="{dragHandle: '.imChatModule'}"
  >
    <div class="imChatModule">
      <div class="mypPofile">
        <img
          v-if="pofileImg"
          src="./img/defaultProfile.jpg"
        >
        <span v-else>
          {{ nicknameCurrent }}
        </span>
      </div>
      <div class="moduleList">
        <!-- 模式：聊天、系统信息、消息提示 -->
        <ul id="moduleUl">
          <li
            v-for="(m,index) in moduleArr"
            :id="m.mClass"
            :key="m.mId"
            :class="[m.mClass,{'currentModule': !(index-menuIndex)}]"
            @click="moduleClickHandel(m.mId,index)"
          />
        </ul>
      </div>
    </div>
    <div class="chatWap">
      <!-- 关闭按钮 -->
      <div class="imOpation">
        <span
          id="imCloseBtn"
          @click="imCloseHandel"
        />
      </div>
      <div
        id="moduleTabCont"
        class="moduleTab"
      >
        <!-- 畅聊模式 curModule==='chartModule' -->
        <chatMod
          v-if="curModule==='chartModule'"
          ref="chatMod"
          im-module="chartModule"
        />
        <!-- 查看系统信息 curModule==='sysMsgModule'-->
        <sysMsgMod
          v-if="curModule==='sysMsgModule'"
          ref="sysMsgMod"
          im-module="sysMsgModule"
        />
        <!-- 其他模式 -->
      </div>
    </div>
  </div>
</template>
<script>
import chatMod from './chatMod'
import sysMsgMod from './sysMsgMod'
import { sysPrefix } from '@/config/ipConfig'

export default {
  name: 'SrmIM',
  components: { chatMod, sysMsgMod },
  props: {
    chatType: { // 聊天模式
      type: String,
      default: 'common' // common | buyer | vendor
    },
    businessId: { // 业务单据ID
      type: [String, Number],
      default: ''
    },
    tenderNo: { // 业务单据号
      type: String,
      default: ''
    },
    title: { // 标题
      type: String,
      default: ''
    },
    userList: { // 用户列表
      type: Array,
      default: () => []
    },
    visible: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      socket: '',
      socketUrl: '',
      menuIndex: 0, // 默认第一个
      seachFlowDivVisible: false, // 显示用户查询结果div
      searchUser: false,
      userSearchKey: '', // 搜索用户绑定的变量
      curModule: 'chartModule', // 当前模式
      moduleArr: [// 左边菜单的模式(聊天、系统信息、消息提示)
        {
          mClass: 'commonChat',
          mId: 'chartModule'
        }
        // {
        //   mClass : 'sysMsg',
        //   mId : 'sysMsgModule'
        // }
				//, { mClass:'todoTip', mId :'todoTipModule' }
			],
      userSearchArr: [], // 查询用户下拉数据
      hisMsgArr: [], // 历史聊天记录

      userIdCurrent: 0,
      nickNameCurrent: '',
      // websocket
      websocketMainObj: null,
      billType: null,
      billId: null,
      errorCountWebsocket: 0,
      websocketUrl: 0,
      pofileImg: ''// 用户头像
    }
  },

  created () {
    const userInfo = this.$store.getters.userInfo
    this.userIdCurrent = this.$store.getters.user.userId
    this.nicknameCurrent = userInfo.nickname || userInfo.userName

    // 初始化Websocket
    this.initWebsocket()
  },
  mounted () {
    document.body.appendChild(this.$el)
  },
  methods: {
    // 初始化Websocket
		initWebsocket () {
      // 第一次进来就启动定时器用于健康检测
      let _this = this
			if (this.websocketMainObj == null) {
        setInterval(function () {
          _this.checkWebsocket()
        }, 1000 * 3)
      }
			var clientId = Math.ceil(Math.random() * 1000)
			// host+port
      var domain = window.location.host
      if (domain.indexOf('localhost') > -1) {
        domain = '10.16.87.99'// 测试用，vue的负载均衡不支持websocket，localhost:9005,10.16.87.99,0.16.87.99:9005
      }
      var url = '/websocket/chat/' + clientId + '/' + this.userIdCurrent
      let websocket
			if ('WebSocket' in window) {
        let prefix = sysPrefix()
        if (window.location.protocol == 'https:') {
          this.websocketUrl = 'wss://' + domain + prefix + url
        } else {
          this.websocketUrl = 'ws://' + domain + prefix + url
        }
        websocket = new WebSocket(this.websocketUrl)
        this.websocketMainObj = websocket
        // 监听socket连接
        websocket.onopen = this.websocketOpen
        // 监听socket错误信息
        websocket.onerror = this.websocketError
        // 监听socket消息
        websocket.onmessage = this.websocketMessage
        // 监听socket关闭
        websocket.onclose = this.websocketClose
			} else {
        this.$message.error('您的浏览器不支持Websocket')
      }
		},
    // 监听连接
    websocketOpen () {
      console.log('Websocket - ' + this.websocketUrl + ' - OnOpen')
    },
    // 监听连接错误
    websocketError () {
      console.log('Websocket - ' + this.websocketUrl + ' - OnError')
    },
    // 关闭
    websocketClose () {
      console.log('Websocket - ' + this.websocketUrl + ' - OnClose')
    },
    // 监听拿到信息
    websocketMessage (msg) {
      console.log(msg.data)
      var jsonData = JSON.parse(msg.data)
      // 上线下线
      if (jsonData.dataType == 'OnOffline') {
        this.$refs.chatMod.updateOnlineByWebsocket(jsonData.createdId, jsonData.data)
      }
      // 聊天消息
      if (jsonData.dataType == 'Chatdata') {
        this.$refs.chatMod.createChatdataByWebsocket(jsonData.data)
      }
      // 消息已读
      if (jsonData.dataType == 'Readed') {
        this.$refs.chatMod.updateReadByWebsocket(jsonData.data)
      }
      // 聊天群组
      if (jsonData.dataType == 'Chatgroup') {
        this.$refs.chatMod.createChatgroupByWebsocket(jsonData.data)
      }
    },
    // websocket健康检查
    checkWebsocket () {
			// 0 ：对应常量CONNECTING (numeric value 0)
			// 1 ：对应常量OPEN (numeric value 1)
			// 2 ：对应常量CLOSING (numeric value 2)
			// 3 : 对应常量CLOSED (numeric value 3)
			if (this.websocketMainObj != null && this.websocketMainObj.readyState == 3) {
        this.errorCountWebsocket = this.errorCountWebsocket + 1
        // 最多重试10次
        if (this.errorCountWebsocket < 10) {
          console.log('Websocket - checkWebsocket - try - initWebsocket')
          this.initWebsocket()
        }
			}
    },
    // 关闭按钮
    imCloseHandel () {
      // // 关闭websocket
      if (this.websocketMainObj) {
        this.websocketMainObj.close()
        this.websocketMainObj = null
      }
      this.$emit('update:visible', false)
      this.$emit('closeIm', false)
    },
    // 切换左边聊天模式
    moduleClickHandel (mid, index) {
      this.curModule = mid
      this.menuIndex = index
      if (this.curModule === 'chartModule') { // 畅聊模式
        // getleftUserData();// 获取左边用户菜单
      } else if (this.curModule === 'sysMsgModule') { // 查看系统信息
        // getSysMsg();// 获取消息列表
      }
    }
  }
}
</script>

<style>
  #srmIM {
    position: absolute;
    right: 20px;
    top: 50px;
    z-index: 2000;
    width: 1060px;
    height: 492px;
    /* border: 1px solid rgba(203, 207, 204, 0.9); */
    background: #f3f4f5;
    font-size: 14px;
    /* box-shadow: 0px 0px 1px rgba(230, 230, 230, 1); */
    box-shadow: 0px 0px 2px rgb(165 160 160);
    /* display:none; */
  }
  /*聊天模式*/
  .imChatModule {
    width: 60px;
    background: #499df2;
    height: 100%;
    position: absolute;
    top: 0;
    left: 0;
    z-index: 3;
    padding: 10px 0;
    box-sizing: border-box;
  }

  .mypPofile {
    width: 40px;
    height: 40px;
    margin: 0 auto;
    border-radius: 4px;
    overflow: hidden;
    position: relative;
  }
  .mypPofile span{
    vertical-align: middle;
    width: 100%;
    height: 40px;
    line-height: 20px;
    display: inline-block;
    background: rgb(255 255 255 / 90%);
    font-size: 12px;
    text-align: center;
    padding-top: 10px;
    color: #75b9fd;
  }
  .mypPofile img {
    width: 100%;
  }

  .moduleList {
    padding-top: 20px;
    text-align: center;
    color: #fff;
  }

  .moduleList ul {
    list-style: none;
    padding-left: 0;
    margin: 0;
  }

  .moduleList ul li {
    height: 32px;
    padding: 15px 0;
    background-position: center center;
    background-repeat: no-repeat;
    background-size: 30px 30px;
    opacity: 0.7;
    box-sizing: content-box;
    position: relative;
    cursor: pointer;
  }

  .hasMsg:after {
    content: " ";
    position: absolute;
    width: 8px;
    height: 8px;
    border-radius: 5px;
    background: #F25555;
    top: 14px;
    right: 11px;
  }

  .moduleList .commonChat {
    background-image: url('./img/chat.png');
  }

  .moduleList .todoTip {
    background-image: url('./img/todo.png');
  }

  .moduleList .sysMsg {
    background-image: url('./img/sysmsg.png');
  }

  .moduleList ul li.currentModule {
    opacity: 1;
  }

  .chatWap {
    padding-left: 60px;
    position: relative;
  }

  .chatWapInner {
    position: relative;
  }

  /*左边联系人列表【*/
  .leftSectionIM {
    width: 230px;
    height: 100%;
    background: #fff;
    position: absolute;
    z-index: 2;
    left: 0;
    top: 0;
    border-right: 1px solid #CBCFD6;
  }

  .imSearch {
    height: 60px;
  }

  .imSearch .searchBar {
    padding: 15px 20px 15px 10px;
    height: 60px;
    box-sizing: border-box;
    position: relative
  }

  .imSearch .searchInner {
    position: relative
  }

  .imSearch .searchBar .imSearchInput {
    height: 30px;
    border: none;
    outline: none;
    border-radius: 2px;
    background: #f2f2f2;
    padding: 5px 10px;
    box-sizing: border-box;
    font-size: 14px;
    width: 100%;
    padding-right: 35px;
    color: #333;
  }

  .imSearch .searchBar .imsearchBtn {
    position: absolute;
    right: 5px;
    top: 2px;
    height: 25px;
    width: 25px;
    background-image: url('./img/searchBtn.png');
    background-position: center center;
    background-repeat: no-repeat;
    background-size: 14px 14px;
  }

  .imSearch .searchBar .searchDataList {
    position: absolute;
    background: #fff;
    top: 52px;
    left: 10px;
    width: 240px;
    height: 350px;
    border: 1px solid #DFE3E6;
    box-shadow: 0 0 2px #d2d2d2;
    border-radius: 2px;
    z-index: 1000;
  }

  .imSearch .searchBar .searchDataList .searchTit {
    padding: 5px 15px;
    background: #F5f5f5;
    font-size: 14px;
    line-height: 22px;
  }

  .imUserDiv {
    padding-top: 5px;
    padding-left: 12px;
  }

  .leftSectionIM .imUserList {
    height: 425px;
    overflow-y: hidden;
    overflow-y: auto;
    position: relative;
  }

  .searchDataUl {
    height: 314px;
    overflow: hidden;
    overflow-y: auto;
  }

  .imUserList ul,.searchDataUl,.sysMsgList ul {
    list-style: none;
    padding-left: 0;
    margin: 0;
  }

  .imUserList ul li,.searchDataUl li,.sysMsgList ul li {
    cursor: pointer;
    height: 60px;
    box-sizing: border-box;
    padding: 6px 8px;
  }
  .imUserList ul li:hover,.searchDataUl li:hover,.sysMsgList ul li:hover{
    background: #e9edef;
  }

  .imUserList ul li.current,.searchDataUl li.current,.sysMsgList ul li.current
    {
    background: #e9edef;
  }

  .imUserList ul li .profileImg,.searchDataUl li .profileImg,.sysMsgList ul li .profileImg
    {
    height: 46px;
    width: 50px;
    border-radius: 4px;
    background: #75b9fd;
    float: left;
    margin-right: 10px;
    text-align: center;
    line-height: 20px;
    color: #fff;
    overflow: hidden;
    box-shadow: 0px 0px 2px #e2e0e0;
  }
  .imUserList ul li .profileImg{
    background: #fff;
  }

  .imUserList ul li .profileImg img,.searchDataUl li .profileImg img,.sysMsgList ul li .profileImg img
    {
    width: 40px;
  }

  .imUserList ul li .imUuser,
  .searchDataUl li .imUuser,
  .sysMsgList ul li .imUuser
    {
    height: 22px;
    color: #333;
    padding-left: 56px;
    padding-right: 32px;
    position: relative;
  }

  .imUserList ul li .imUuser span,
  .searchDataUl li .imUuser span,
  .sysMsgList ul li .imUuser span
    {
    display: inline-block;
  }
  .imUserList ul li .imUuser span.userNameText,
  .searchDataUl li .imUuser span.userNameText{
    display: block;
    overflow: hidden;
    text-overflow: ellipsis;
    height: 22px;
    word-break: break-all;
    white-space: nowrap;
  }
  .imUserList ul li .imUuser span.lastTime,
  .searchDataUl li .imUuser span.lastTime{
    position: absolute;
    right: 0;
    top: 0;
  }

  .imUserList ul li .imUuser .lineState,
  .searchDataUl .imUuser .lineState,
  .sysMsgList ul li .imUuser .lineState
    {
    font-size: 12px;
    padding: 0 5px;
  }

  .imUserList ul li .imUuser .lineState.online,
  .searchDataUl li .imUuser .lineState.online,
  .sysMsgList ul li .imUuser .lineState.online
    {
    color: #666;
  }

  .imUserList ul li .imUuser .lineState.unOnline,.searchDataUl li .imUuser .lineState.unOnline,.sysMsgList ul li .imUuser .lineState.unOnline
    {
    color: #999;
  }

  .imUserList ul li .imUuser .lastTime,.searchDataUl li .imUuser .lastTime,.sysMsgList ul li .imUuser .lastTime
    {
    float: right;
    font-size: 12px;
    color: #999;
  }

  .imUserList ul li .imCompany .unRead,.searchDataUl li .imCompany .unRead,.sysMsgList ul li .imCompany .unRead
    {
    float: right;
    background: #f77373;
    border-radius: 10px;
    color: #fff;
    padding: 0 5px;
    height: 18px;
    line-height: 18px;
    font-size: 12px;
  }

  .imUserList ul li .imCompany,.searchDataUl li .imCompany,.sysMsgList ul li .imCompany
    {
    font-size: 12px;
    height: 18px;
    line-height: 18px;
    color: #999;
    overflow: hidden;
    text-overflow: ellipsis;
    word-break: keep-all;
    word-spacing: initial;
    white-space: pre;
  }

  /*左边联系人列表】*/
  /*右边聊天框【*/
  .chatSectionIm {
    height: 490px;
    position: relative;
    padding-left: 230px;
  }

  #chartDailog {
    height: 490px;
    position: relative
  }

  .chatSectionIm .toChartUserInfo {
    height: 68px;
    width: 100%;
    border-bottom: 1px solid #DFE3E6;
    position: absolute;
    top: 0;
    left: 0;
  }
  .chatSectionIm .toChartUserInfo .groupChatBtn{
    position: absolute;
    bottom: 8px;
    right: 5px;
    text-align: right;
    height: 24px;
  }
  .chatSectionIm .toChartUserInfo .groupChatBtn .el-button {
    padding: 0;
    min-width: 30px !important;
    font-size: 20px;
    vertical-align: bottom;
    color: #585858;
    margin-left: 2px;
  }
  .chatSectionIm .toChartUserInfo .chartUser {
    padding: 12px 30px 10px 15px;
  }

  .chatSectionIm .toChartUserInfo .chartUser .chartUuser {
    color: #333;
    font-size: 16px;
    height: 26px;
    line-height: 26px;
  }

  .chatSectionIm .toChartUserInfo .chartUser .chartCompany {
    font-size: 12px;
    color: #999;
  }

  .imOpation {
    position: absolute;
    right: 0px;
    top: 0px;
    padding: 0px;
    width: 40px;
    height: 30px;
    text-align: right;
    z-index: 10;
  }

  .imOpation #imCloseBtn {
    display: inline-block;
    width: 30px;
    height: 30px;
    background-repeat: no-repeat;
    background-size: 15px 15px;
    background-position: center center;
    background-image: url('./img/closeBtn.png');
    transition: all ease 300ms;
    cursor: pointer;
  }

  .imOpation #imCloseBtn:hover {
    background-image: url('./img/closeBtnHover.png');
    background-color: #ea9393;
  }

  .chatSectionIm .chartContent {
    padding-top: 72px;
    padding-bottom: 84px;
    height: 490px;
    box-sizing: border-box;
  }
  .chatSectionIm .chartContent.groupChatDiv{
    width: 75%;
  }
  .chatSectionIm .groupChatUser{
    width: 25%;
    height: 424px;
    box-sizing: border-box;
    position: absolute;
    right: 0;
    top: 68px;
    border-left: 1px solid #DFE3E6;
    overflow: hidden;
    overflow-y: auto;
    /* padding: 5px; */
  }
  .chatSectionIm .groupChatUser .groupChatUserTit{
    font-size: 14px;
    height: 26px;
    line-height: 26px;
    padding: 0px 5px;
  }
  .chatSectionIm .groupChatUser .groupChatUserTit span{
    float: right;
    cursor: pointer;
  }
  .chatSectionIm .groupChatUser ul{
    padding: 0;
    margin: 0;
  }
  .chatSectionIm .groupChatUser ul li{
    list-style: none;
    padding: 4px 10px;
    position: relative;
  }
  .chatSectionIm .groupChatUser ul li:hover{
    background: #e2e6e8;
  }
  .chatSectionIm .groupChatUser ul li .removeFromGroup {
    position: absolute;
    height: 100%;
    width: 30px;
    background: #bfbaba;
    display: none;
    top: 0;
    right: 0;
    font-size: 12px;
    text-align: center;
  }
  .chatSectionIm .groupChatUser ul li:hover .removeFromGroup{
    display: block;
    cursor: pointer;
    line-height: 48px;
  }
  .chatSectionIm .groupChatUser ul li .groupChatUserName .el-checkbox__label{
    color: #333;
  }
  .chatSectionIm .groupChatUser ul li .groupChatUserName .onlineStatus{
    font-size: 12px;
    color: #888;
  }
  .chatSectionIm .groupChatUser ul li .groupChatUserCompanyName{
    padding-left: 20px;
    line-height: 18px;
    overflow: hidden;
    text-overflow: ellipsis;
    height: 20px;
    word-break: break-all;
    white-space: nowrap;
    color: #888;
    font-size: 12px;
  }

  .chatSectionIm .chartContent .imMessageList {
    padding: 0 20px;
    height: 100%;
    overflow: hidden;
    overflow-y: auto;
  }

  .imMessageList .msgComm {
    padding: 16px 0px;
    display: -webkit-flex;
    display: flex;
    display: inline-flex;
    width: 100%;
  }

  .imMessageList .msgComm.receiveText {
    flex-direction: row;
  }

  .imMessageList .msgComm.sendText {
    flex-direction: row-reverse;
  }

  .imMessageList .msgComm .reciveMsg {
    /* padding: 10px 15px; */
    padding: 5px 15px;
    border-radius: 10px;
    background: #fff;
    margin: 0 12px;
    line-height: 20px;
    max-width: 320px;
  }

  .imMessageList .msgComm.receiveText .reciveMsg {
    border-top-left-radius: 2px;
  }

  .imMessageList .msgComm.sendText .reciveMsg {
    border-top-right-radius: 2px;
    /* background: #75b9fd;
    color: #fff; */
    background: #cae2fb;
    color: #000;
  }
  .imMessageList .msgComm .reciveMsg .hisMsg {
    border-top: 1.5px dashed rgba(123,122,122,0.3);
    padding: 3px 0px;
    font-size: 12px;
    color: #777;
  }
  .imMessageList .msgComm .reciveMsg .firstMsg {
    padding: 3px 0px 5px;
    border-top:none;
  }

  .imMessageList .msgComm .msgProfileImg {
    float: left;
    text-align: center;
    color: #fff;
    position: relative;
    top: -8px;
    max-width: 150px;

  }
  .imMessageList .msgComm .msgProfileImg .userName{
    display: block;
    height: 40px;
    width: 40px;
    line-height: 38px;
    background: #75b9fd;
    border-radius: 4px;
    box-sizing: border-box;
    text-align: center;
    position: relative;
    top: -5px;
  }
  .imMessageList .msgComm.receiveText .msgProfileImg .userCompany{
    text-align: left;
  }
  .imMessageList .msgComm.sendText .msgProfileImg .userCompany{
    text-align: right;
  }
  .imMessageList .msgComm .msgProfileImg span.userCompany{
    position: absolute;
    top: -10px;
    width: 400px;
    background: none;
    color: #888;
    font-size: 12px;
  }

  .imMessageList .msgComm.receiveText .msgProfileImg span.userCompany{
    left: 55px;
  }
  .imMessageList .msgComm.sendText .msgProfileImg span.userCompany{
    right: 55px;
  }

  .imMessageList .msgComm .msgState,
  .imMessageList .msgComm .msgReply,
  .imMessageList .msgComm .msgWithdrow {
    align-self: center;
    font-size: 12px;
    color: #999;
    /* width: 30px; */
    padding: 0 0 0 5px;
  }

  .imMessageList .msgComm .msgReply {
    cursor: pointer;
    color: #666;
  }

  .imMessageList .msgComm .msgWithdrow {
    cursor: pointer;
  }

  .imMessageList .timeDivision {
    padding: 15px 0 20px;
    text-align: center;
    font-size: 12px;
    color: #666;
  }

  /*发送消息框*/
  .chatSectionIm .sendMessage {
    height: 82px;
    width: 100%;
    border-top: 1px solid #CBCFD6;
    position: absolute;
    bottom: 0;
    left: 0;
  }
  .chatSectionIm .sendMessage.groupChatDiv{
    width: 75%;
  }

  .chatSectionIm .sendMessage .sendText {
    height: 78px;
    padding: 2px;
    box-sizing: border-box;
    padding-right: 90px;
  }

  .chatSectionIm .sendMessage .sendText #sendTextarea {
    height: 100%;
    width: 100%;
    border: none;
    outline: none;
    background: #f1f3f5;
    padding: 10px;
    box-sizing: border-box;
    resize: none;
    color: #333;
    overflow: hidden;
  }

  .chatSectionIm .sendMessage .sendInfo {
    height: 30px;
    text-align: right;
    position: absolute;
    top: 28px;
    right: 10px;
  }

  .chatSectionIm .sendMessage .sendInfo .sendTip {
    font-size: 12px;
    color: #999;
    padding-top: 5px;
  }

  .chatSectionIm .sendMessage .sendInfo .toSendBtn {
    height: 28px;
    line-height: 28px;
    font-size: 14px;
    text-align: center;
    min-width: 68px;
    outline: none;
    border: none;
    border-radius: 2px;
    background: #75b9fd;
    color: #fff;
    cursor: pointer;
  }

  /*右边聊天框】 */

  /*系统信息模式*/
  .sysMsgInner {
    position: relative;
  }

  .sysMsgInner .sysMsgListLeft {
    width: 230px;
    height: 100%;
    position: absolute;
    top: 0;
    left: 0;
    background: #fff;
    border-right: 1px solid #CBCFD6;
    z-index: 3;
  }

  .sysMsgListLeft .sysMsgList {
    height: 425px;
    overflow: hidden;
  }

  .sysMsgInner .sysMsgShowContent {
    padding-left: 230px;
    height: 490px;
  }

  .sysMsgShowInner {
    height: 100%;
  }

  .msgTitleBar {
    padding: 15px;
    height: 60px;
    box-sizing: border-box;
    line-height: 30px;
    border-bottom: 1px solid #CBCFD6;
  }

  .showMsg {
    height: 420px;
    overflow: hidden;
    padding: 0px 50px;
    box-sizing: border-box;
  }

  .msgSection .notePbTime {
    text-align: center;
    line-height: 30px;
    color: #666;
    font-size: 12px;
    padding-top: 10px;
  }

  .msgSection .noteDetail {
    background: #fff;
    border-radius: 8px;
    padding: 15px 20px;
  }

  .msgSection .noteDetail .notesTitle {
    line-height: 36px;
    padding-bottom: 5px;
    font-size: 18px;
  }

  .msgSection .noteDetail .notesContent {
    line-height: 24px;
    color: #666;
    /* display: none; */
    padding: 10px 0 15px;
  }

  .msgSection .noteDetail .notesReadMore {
    border-top: 1px solid #CBCFD6;
    text-align: right;
    padding-top: 10px;
    cursor: pointer;
    color: #999;
  }

  .vendor_label {
    max-width: 130px;
    float: left;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  /* 群聊选择框 */
  .groupChatSelector{
    position: absolute;
    top: -1px;
    left: 60px;
    width: 600px;
    height: 420px;
    border: 1px solid rgba(203, 207, 204, 0.9);
    background: #f3f4f5;
    box-shadow: 0px 0px 1px rgba(230, 230, 230, 1);
    z-index: 3;
  }
  .addChatMembers{
    padding: 5px;
  }
  .addChatMembers .nameSpan{
    font-size: 12px;
  }
  .addChatMembers >.el-button{
    width: 30px !important;
    padding: 0;
    height: 30px;
    min-width: 30px;
    border-radius: 6px;
    border-style: dashed;
    background: #f3f4f5;
    border-color: #c3c3c3;
    margin-right: 10px;
  }
  .addChatMembers:hover{
    cursor: pointer;
    background: #e2e6e8;
  }
  .userItems{
    font-size: 12px !important;
  }
  .userItems .el-col span {
    background: #75b9fd;
    transform: scale(0.9);
    display: block;
    border-radius: 4px;
    padding: 1px 0;
  }
  .replyMsg{
    padding-left: 10px;
  }
  .replyMsg .megInfo {
    padding: 2px 5px;
    background: #fff;
    border-radius: 3px;
    height: 22px;
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
    line-height: 18px;
    word-wrap: normal;
    word-break: keep-all;
    overflow: hidden;
    text-overflow: ellipsis;
    vertical-align: top;
    font-size: 12px;
    white-space: nowrap;
  }
  .clearReplyMeg {
    padding: 0 !important;
    height: 22px !important;
    min-width: 22px !important;
  }
  .withdrowInfo{
    font-size: 12px;
    text-align: center;
    color: #999;
    padding: 10px 0;
  }
</style>
