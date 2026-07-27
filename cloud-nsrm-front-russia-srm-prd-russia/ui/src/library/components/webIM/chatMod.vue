<template>
  <div
    id="chat"
    class="chatWapInner"
  >
    <!-- 用户列表带搜索框 -->
    <div
      v-if="searchUser"
      class="leftSectionIM"
    >
      <!-- 用户搜索 和弹框显示-->
      <div class="imSearch">
        <div class="searchBar">
          <div class="searchInner">
            <el-input
              v-model="userSearchKey"
              placeholder="搜索群组名称、单据号"
              suffix-icon="el-icon-search"
              @change="userSearchHandel"
            />
          </div>
          <div
            v-if="seachFlowDivVisible"
            class="searchDataList"
          >
            <div class="searchTit">
              群组名称、单据号
            </div>
            <ul
              id="imSearchUserUl"
              class="searchDataUl"
            >
              <li
                v-for="chatgroup in userSearchArr"
                :key="chatgroup.userName"
                @click="selectUserToChatHandel(chatgroup)"
              >
                <div>
                  <div class="profileImg">
                    <el-row class="userItems">
                      <el-col
                        v-for="user in chatgroup.groupNameArr"
                        :key="user"
                        :span="12"
                      >
                        <span>{{ user }}</span>
                      </el-col>
                    </el-row>
                  </div>
                  <div class="imUuser">
                    <span class="userNameText">{{ chatgroup.groupName }}</span>
                    <span class="lineState online" />
                    <span class="lastTime">{{ chatgroup.lastChattime }}</span>
                  </div>
                  <div class="imCompany">
                    {{ chatgroup.lastChatdata }}<span
                      v-if="chatgroup.unreadCount>0"
                      class="unRead"
                    >{{ chatgroup.unreadCount }}</span>
                  </div>
                </div>
              </li>
            </ul>
          </div>
        </div>
      </div>
      <!-- 所有用户列表chatgroupId  -->
      <div
        id="imListUser"
        class="imUserList"
      >
        <ul id="imUserUl">
          <li
            v-for="(chatgroup,$index) in chatgroupArr"
            :id="chatgroup.chatgroupId"
            :key="chatgroup.chatgroupId"
            :class="{'current': chatgroup.chatgroupId == chatgroupCurrent.chatgroupId}"
            @click="clickChatgroupHandle(chatgroup, $index)"
          >
            <div>
              <div class="profileImg">
                <!-- {{chatgroup.groupName}} -->
                <el-row class="userItems">
                  <el-col
                    v-for="user in chatgroup.groupNameArr"
                    :key="user"
                    :span="12"
                  >
                    <span>{{ user }}</span>
                  </el-col>
                </el-row>
              </div>
              <div class="imUuser">
                <span class="userNameText">{{ chatgroup.groupName }}</span>
                <span class="lineState online" />
                <span class="lastTime">{{ chatgroup.lastChattime }}</span>
              </div>
              <div class="imCompany">
                {{ chatgroup.lastChatdata }}<span
                  v-if="chatgroup.unreadCount>0"
                  class="unRead"
                >{{ chatgroup.unreadCount }}</span>
              </div>
            </div>
          </li>
        </ul>
      </div>
    </div>
    <!-- 用户列表不带搜索框 -->
    <div
      v-else
      class="leftSectionIM"
    >
      <div class="imSearch">
        <div class="searchBar">
          <input
            id="cbk_user"
            type="checkbox"
          >
        </div>
      </div>
      <div
        id="imListUser"
        class="imUserList"
      >
        <ul id="imUserUl">
          <li
            id="1"
            class="current"
          >
            <div>
              <div class="profileImg">
                <img src="./img/defaultProfile.jpg">
              </div>
              <div class="imUuser">
                <span class="userNameText">美小美2</span>
                <span class="lineState online">(在线)</span>
                <span class="lastTime">20:01</span>
              </div>
              <div class="imCompany">
                美的-美云智数111<span class="unRead">5</span>
              </div>
            </div>
          </li>
        </ul>
      </div>
    </div>
    <!-- 聊天框 -->
    <div class="chatSectionIm">
      <div id="chartDailog">
        <!-- 当前窗口聊天用户信息 -->
        <div class="toChartUserInfo">
          <div class="chartUser">
            <div class="chartUuser">
              {{ chatgroupCurrent.groupName }}
            </div>
            <div class="chartCompany">
              <span class="unRead" />
            </div>
          </div>
          <!-- type="primary"  -->
          <div class="groupChatBtn">
            <el-tooltip
              class="item"
              effect="dark"
              content="创建聊天"
              placement="bottom"
            >
              <el-button
                v-if="isGroupChat && userType==='BUYER'"
                icon="el-icon-folder-add"
                type="text"
                @click="toGroupChatHandel('newGroup')"
              />
            </el-tooltip>
            <!--
            <el-tooltip class="item" effect="dark" content="个人信息" placement="bottom">
              <el-button
                icon="el-icon-user"
                type="text"
              ></el-button>
            </el-tooltip>-->
          </div>
          <!-- <el-button
            v-if="isGroupChat"
            class="detail-pbtn groupChatBtn"
            icon="el-icon-circle-plus-outline"
            @click="toGroupChatHandel"
          >群聊</el-button> -->
        </div>
        <!-- 历史聊天消息记录 -->
        <div :class="['chartContent',{'groupChatDiv': isGroupChat}]">
          <div
            ref="chatMsgList"
            class="imMessageList"
          >
            <div id="dailogViewPort">
              <div
                v-for="chatdata in chatdataArr"
                :key="chatdata.chatdataId"
              >
                <div
                  v-if="chatdata.creationDateCenter"
                  class="timeDivision"
                >
                  {{ chatdata.creationDateCenter }}
                </div>
                <!-- 发送人撤回 -->
                <div
                  v-if="chatdata.showType==='withdrow'"
                  class="withdrowInfo"
                >
                  <div v-if="chatdata.createdId === userIdCurrent">
                    <span>您撤回了一条消息</span>
                    <!-- 暂时不做，撤回的消息不再保留
                    <el-button type="text" @click="reEdit" v-if="withdrowDataInfo.chatData">重新编辑</el-button>
                    -->
                  </div>
                  <div v-else>
                    {{ chatdata.createdUserBy }}撤回了一条消息
                  </div>
                </div>
                <div
                  v-else
                  :class="['msgComm',chatdata.createdId===userIdCurrent? 'sendText':'receiveText']"
                >
                  <div class="msgProfileImg">
                    <!-- createdUserBy -->
                    <span class="userName">{{ chatdata.formatUserName }}</span>
                    <span class="userCompany">{{ chatdata.createdCompanyBy?chatdata.createdCompanyBy:chatdata.createdUserBy }}</span>
                  </div>
                  <div class="reciveMsg">
                    <!-- showType=reply -->
                    <div class="firstMsg">
                      {{ chatdata.chatData }}
                    </div>
                    <div
                      v-if="chatdata.showType==='reply'"
                      class="hisMsg"
                    >
                      {{ chatdata.chatdataSourceData }}
                    </div>
                  </div>
                  <!-- 回复或者已读-->
                  <div
                    :class="chatdata.createdId === userIdCurrent ? 'msgState':'msgReply'"
                    @click="replyHandel(chatdata)"
                  >
                    {{ chatdata.createdId===userIdCurrent? chatdata.readStatusName:chatdata.replyStatusName }}
                  </div>
                  <!-- 撤回-->
                  <div
                    :class="chatdata.createdId === userIdCurrent ? 'msgWithdrow':'msgReply'"
                    @click="withdrowHandel(chatdata)"
                  >
                    {{ chatdata.createdId===userIdCurrent? chatdata.withdrowStatusName:'' }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- 群聊显示勾选的用户 -->
        <div
          v-if="isGroupChat"
          class="groupChatUser"
        >
          <div
            v-if="userType==='BUYER'"
            class="addChatMembers"
            @click="toGroupChatHandel('addMembers')"
          >
            <el-button icon="el-icon-plus" />
            <span class="nameSpan">添加成员</span>
          </div>
          <!-- 采购商可以看见所有供应商 -->
          <div v-if="userType==='BUYER'">
            <div class="groupChatUserTit">
              供应商({{ chatgroupMembersCountVendor }})  <span
                v-if="chatgroupMembersCountVendor>0"
                @click="vendorSelectAll"
              >全选</span>
            </div>
            <ul>
              <li
                v-for="(guser,index) in chatgroupMembersVendor"
                :key="guser.chatgroupMemberId"
              >
                <div class="groupChatUserName">
                  <el-checkbox v-model="guser.selectedFlag">
                    {{ guser.userName }}
                  </el-checkbox>
                  <span
                    v-if="guser.onlineStatus!=='Y'"
                    class="onlineStatus"
                  >[离线]</span>
                </div>
                <div class="groupChatUserCompanyName">
                  {{ guser.companyName }}
                </div>
                <span
                  class="removeFromGroup"
                  @click="removeUserFromGroup(guser,index,'vendor')"
                >移除</span>
              </li>
            </ul>
          </div>
          <div>
            <div class="groupChatUserTit">
              采购商({{ chatgroupMembersCountBuyer }}) <span
                v-if="chatgroupMembersCountBuyer>0"
                @click="buyerSelectAll"
              >全选</span>
            </div>
            <ul>
              <li
                v-for="(guser,index) in chatgroupMembersBuyer"
                :key="guser.chatgroupMemberId"
              >
                <div class="groupChatUserName">
                  <el-checkbox v-model="guser.selectedFlag">
                    {{ guser.userName }}
                  </el-checkbox>
                  <span
                    v-if="guser.onlineStatus!=='Y'"
                    class="onlineStatus"
                  >[离线]</span>
                </div>
                <div class="groupChatUserCompanyName">
                  {{ guser.companyName }}
                </div>
                <span
                  class="removeFromGroup"
                  @click="removeUserFromGroup(guser,index,'buyer')"
                >移除</span>
              </li>
            </ul>
          </div>
        </div>
        <!-- 发送消息框 -->
        <div :class="['sendMessage',{'groupChatDiv': isGroupChat}]">
          <div class="sendText">
            <el-input
              id="sendTextarea"
              v-model="messageCurrent"
              type="textarea"
              :rows="2"
            />
            <div
              v-if="curRelyMesInfo.chatData"
              class="replyMsg"
            >
              <el-row :gutter="10">
                <el-col :span="20">
                  <div class="megInfo">
                    {{ curRelyMesInfo.chatData || '' }}
                  </div>
                </el-col>
                <el-col :span="4">
                  <el-button
                    class="clearReplyMeg"
                    icon="el-icon-close"
                    round
                    @click="clearReplyMeg"
                  />
                </el-col>
              </el-row>
            </div>
          </div>
          <div class="sendInfo">
            <button
              id="sendMsgBtn"
              class="toSendBtn"
              @click="sendMsgHandle"
            >
              发送
            </button>
            <!-- <button v-if="imModule != 'common'" style="margin-left:5px;" class="toSendBtn" id="sendFileBtn">文件</button> -->
          </div>
        </div>
      </div>
    </div>
    <!-- 群聊选择框 -->
    <div
      v-if="groupVisibale"
      class="groupChatSelector"
    >
      <groupChatSelector
        ref="groupChatSelector"
        :visible.sync="groupVisibale"
        @cancel="cancelGroupHandel"
        @confirm="confirmGroupHandel"
      />
    </div>
  </div>
</template>
<script>
import groupChatSelector from './groupChatSelector'
import { getChatgroup, querytChatgroupSelf, createChatdata, queryChatdataSelf, updateChatdataRead, removeChatgroupMember, withdrowChatdata } from '@/api/chat'
export default {
  name: 'ChatMod',
  components: { groupChatSelector },
  props: {
    isSearch: { // 是否显示搜索框
      type: Boolean,
      default: true
    },
    imModule: {// 当前模式
      type: String,
      default: 'chartModule'
    },
    isGroupChat: { // 是否需要开启群聊
      type: Boolean,
      default: true
    }
  },
  data () {
    return {
      groupVisibale: false, // 选择群聊框
      seachFlowDivVisible: false, // 显示用户查询结果div
      searchUser: true,
      userSearchKey: '', // 搜索用户绑定的变量
      userSearchArr: [],
      messageCurrent: '', // 发送的消息
      // 用于查询
      chatgroupQuery: {},
      // 当前选中的聊天项
      chatgroupCurrent: {},
      chatgroupMembersCount: 0, // 群聊用户数
      chatgroupMembers: [],
      chatgroupMembersCountBuyer: 0, // 采购商用户数
      chatgroupMembersBuyer: [],
      chatgroupMembersCountVendor: 0, // 供应商用户数
      chatgroupMembersVendor: [],
      // 聊天项列表
      chatgroupArr: [],
      // 聊天数据列表
      chatdataArr: [],
      // 当前用户
      userIdCurrent: 0,
      billType: null,
      billId: null,
      // 新增群newGroup，增加成员addMembers
      openModeSelector: '',
      userType: this.$store.getters.user.userType, // VENDOR | BUYER
      curRelyMesInfo: {}, // 当前回复的消息
      megType: '', // 消息回复类型
      hisMsgArr: [
        {
          userName: 'dsdsd',
          msgContent: '收到收到收到收到收到收到',
          type: 'ChatDirectionTypeReceive'
        },
        {
          userName: 'ttggg',
          msgContent: '发送发送发发发发发',
          type: 'ChatDirectionTypeSend'
        }
      ],
      withdrowDataInfo: {}// 撤回的消息
    }
  },
  created () {
    this.querytChatgroupSelfHandle()
    this.userIdCurrent = this.$store.getters.user.userId
  },
  methods: {
    // 获取最近聊天项，所有聊天都是以群组模式，单聊是两个人组成的群组
    querytChatgroupSelfHandle () {
      querytChatgroupSelf(this.chatgroupQuery).then(res => {
        if (res) {
          let chatgroupArrTemp = res.data || []
          let chatgroupArrNew = chatgroupArrTemp.map(row => {
            let groupName = row.groupName ? row.groupName.split(',') : []
            let groupNNew = groupName.map(elm => (elm.substr(-2, 2)))
            // console.log(groupNNew.slice(0,4))
            return {
              ...row,
              groupNameArr: groupNNew.slice(0, 4) // 截取前面4个显示
            }
          })
          // 格式化显示时间和最新消息
          for (let i = 0; i < chatgroupArrNew.length; i++) {
            let chatgroupObj = chatgroupArrNew[i]
            // 去掉@的显示，可以显示的更多信息
            let sTemp = ''
            let arrTemp = chatgroupObj.lastChatdata.split(' ')
            for (let j = 0; j < arrTemp.length; j++) {
              if (arrTemp[j].indexOf('@') == -1) {
                sTemp = sTemp + arrTemp[j]
              }
            }
            chatgroupObj.lastChatdata = sTemp
            chatgroupObj.lastChattime = this.dateFtt('hh:mm', new Date(chatgroupObj.lastCreationDate))
            chatgroupArrNew[i] = chatgroupObj
          }
          this.chatgroupArr = chatgroupArrNew
          // _this.$forceUpdate();
          // 不需要自动打开第一个
          // let firstRow = chatgroupArr[0]
          // this.clickChatgroupHandle(firstRow,0)
        }
      })
    },
    // 点击聊天项，加载聊天项的详细信息，群信息，成员信息，聊天消息
    clickChatgroupHandle (chatgroup, index) {
      // 先获取群信息，成员信息
      getChatgroup(chatgroup).then(res => {
        if (res) {
          // 设置当前聊天项
          this.chatgroupCurrent = res.data
          let chatgroupMembers = res.data.chatgroupMembers || []
          this.chatgroupMembersCount = chatgroupMembers.length
          let resChatgroupMembers = chatgroupMembers.map(i => ({
            ...i,
            selectedFlag: false
          }))
          this.chatgroupMembers = resChatgroupMembers
          // 采购商，供应商分开
          this.chatgroupMembersBuyer = resChatgroupMembers.filter(item => (
            item.userType === 'BUYER'
          ))
          this.chatgroupMembersCountBuyer = this.chatgroupMembersBuyer.length || 0
          this.chatgroupMembersVendor = resChatgroupMembers.filter(item => (
            item.userType !== 'BUYER'
          ))
          this.chatgroupMembersCountVendor = this.chatgroupMembersVendor.length || 0
        }
      })
      // 再获取聊天消息
      queryChatdataSelf(chatgroup).then(res => {
        if (res) {
          let chatdataArr = res.data || []
          let resArr = chatdataArr.map(o => {
            let formatUserName = o.createdUserBy ? (o.createdUserBy).substr(-2, 2) : ''
            let chatData = o.chatData
            // let replyArr = []
            // if(chatData && chatData.indexOf('###') > -1){
            //   replyArr = chatData.split('###')
            // }else{
            //   replyArr = [chatData]
            // }
            return {
              ...o,
              // replyArr: replyArr,
              formatUserName: formatUserName
            }
          })
          this.chatdataArr = resArr
          // 逐条标记为已读
          for (let i = 0; i < this.chatdataArr.length; i++) {
            let chatdataObj = this.chatdataArr[i]
            // 对方发的，而且未读的
            if (this.chatdataArr[i].createdId != this.userIdCurrent && this.chatdataArr[i].isReadSelf == 'N') {
              updateChatdataRead(this.chatdataArr[i]).then(res02 => {
                // 不报错就行
              })
            }
            // 对于供应商要去掉@的显示
            if (this.userType == 'VENDOR') {
              let sTemp = ''
              let arrTemp = chatdataObj.chatData.split(' ')
              for (let j = 0; j < arrTemp.length; j++) {
                if (arrTemp[j].indexOf('@') == -1) {
                  sTemp = sTemp + arrTemp[j]
                }
              }
              chatdataObj.chatData = sTemp
              this.chatdataArr[i] = chatdataObj
            }
          }
          // 滚动到信息底部
          this.$nextTick(() => {
            this.scrollBoxToButtom()
          })
        }
      })

      this.curRelyMesInfo = {}
      this.withdrowDataInfo = {}
    },
    // 点击发送消息按钮
    sendMsgHandle () {
      let _this = this
      if (this.messageCurrent == null || this.messageCurrent == '') {
        // 提示，请输入发送内容
        this.$message.error('请输入发送内容')
        return
      }

      let chatdata = {}
      chatdata.chatgroupId = this.chatgroupCurrent.chatgroupId
      chatdata.dataType = 'Message'
      // chatdata.fileuploadId =null;
      chatdata.chatdataStates = []

      // 先判断回复消息
      if (this.megType === 'reply') {
        // 不需要@，因为从回复可以知道发给谁
        // chatdata.chatDatia ="@"+this.curRelyMesInfo.createdUserBy+" "+chatdata.chatData;
        for (let i = 0; i < this.chatgroupCurrent.chatgroupMembers.length; i++) {
          let chatgroupMember = this.chatgroupCurrent.chatgroupMembers[i]
          if (chatgroupMember.userId == this.curRelyMesInfo.createdId) {
            let chatdataState = {}
            chatdataState.targetUserId = chatgroupMember.userId
            chatdataState.targetUserName = chatgroupMember.userName
            chatdataState.targetCompanyId = chatgroupMember.companyId
            chatdataState.targetCompanyName = chatgroupMember.companyName
            chatdata.chatdataStates[0] = chatdataState
          }
        }
        chatdata.chatdataSourceId = this.curRelyMesInfo.chatdataId
        chatdata.chatdataSourceData = this.curRelyMesInfo.chatData
        // 关联回复的消息，在正常的消息后用###分割，暂时不用
        // chatdata.chatData =this.messageCurrent +"###"+ this.curRelyMesInfo.chatData;
        chatdata.chatData = this.messageCurrent
      } else { // 主动发送消息
        // 先判断是否勾选了目标人
        let message = ''
        let atMessageBuyer = ''
        let atMessageVendor = ''
        let indexTemp = 0
        let chatgroupMembersBuyer = this.chatgroupMembersBuyer
        let chatgroupMembersVendor = this.chatgroupMembersVendor
        let selectionAll = []
        let countBuyer = 0
        let countVendor = 0
        chatgroupMembersBuyer.forEach(elm => {
          let chatgroupMember = elm
          // 排除自己的
          if (elm.selectedFlag == true && chatgroupMember.userId != this.userIdCurrent) {
            selectionAll.push(elm)
            countBuyer = countBuyer + 1

            let chatdataState = {}
            chatdataState.targetUserId = chatgroupMember.userId
            chatdataState.targetUserName = chatgroupMember.userName
            chatdataState.targetCompanyId = chatgroupMember.companyId
            chatdataState.targetCompanyName = chatgroupMember.companyName
            chatdata.chatdataStates[indexTemp] = chatdataState
            indexTemp = indexTemp + 1
            atMessageBuyer = atMessageBuyer + '@' + chatgroupMember.userName + ' '
          }
        })
        chatgroupMembersVendor.forEach(elm => {
          let chatgroupMember = elm
          if (elm.selectedFlag == true) {
            selectionAll.push(elm)
            countVendor = countVendor + 1

            let chatdataState = {}
            chatdataState.targetUserId = chatgroupMember.userId
            chatdataState.targetUserName = chatgroupMember.userName
            chatdataState.targetCompanyId = chatgroupMember.companyId
            chatdataState.targetCompanyName = chatgroupMember.companyName
            chatdata.chatdataStates[indexTemp] = chatdataState
            indexTemp = indexTemp + 1
            atMessageVendor = atMessageVendor + '@' + chatgroupMember.userName + ' '
          }
        })
        // for(let i = 0; i　< selectionAll.length; i++) {
        //   let chatgroupMember =selectionAll[i];
        //   if( chatgroupMember.userId==this.userIdCurrent ){
        //     continue;
        //   }
        //   let chatdataState ={};
        //   chatdataState.targetUserId =chatgroupMember.userId;
        //   chatdataState.targetUserName =chatgroupMember.userName;
        //   chatdataState.targetCompanyId =chatgroupMember.companyId;
        //   chatdataState.targetCompanyName =chatgroupMember.companyName;
        //   chatdata.chatdataStates[indexTemp] =chatdataState;
        //   indexTemp =indexTemp+1;
        //   atMessage =atMessage+"@"+chatgroupMember.userName+" ";
        // }

        message = this.messageCurrent
        // 补充@的信息，@供应商，@采购商，这样可以精简输出
        if (countBuyer > 0 && countBuyer == chatgroupMembersBuyer.length - 1) {
          message = '@采购商' + ' ' + this.messageCurrent
        } else if (countBuyer > 0) {
          message = atMessageBuyer + this.messageCurrent
        }

        if (countVendor > 0 && countVendor == chatgroupMembersVendor.length) {
          message = '@供应商' + ' ' + message
        } else if (countBuyer > 0) {
          message = atMessageVendor + message
        }

        // if( indexTemp==this.chatgroupCurrent.chatgroupMembers.length-1 ){
        //   message ="@所有人"+" "+this.messageCurrent;
        // }else if( indexTemp>0 ){
        //   message =atMessage+this.messageCurrent;
        // }

        // 取消@的方式，避免不必要的权限问题
        // if( chatdata.chatdataStates.length==0 ){
        //   //解析发送内容中的@xxx
        //   let atArr =this.messageCurrent.split(" ");
        //   let indexTemp =0;
        //   if( atArr!=null && atArr.length>0 ){
        //     for(let i = 0; i　< atArr.length; i++) {
        //       for(let j = 0; j　< this.chatgroupCurrent.chatgroupMembers.length; j++) {
        //         let chatgroupMember =this.chatgroupCurrent.chatgroupMembers[j];
        //         if( atArr[i]=="@"+chatgroupMember.userName ){
        //           //不能at自己
        //           if( chatgroupMember.userId==this.userIdCurrent ){
        //             this.$message.error("不能@自己");
        //             return;
        //           }
        //         }
        //         //atArr[i]=="@所有人" || ，暂时不支持@所有人，如果供应商使用，会有问题
        //         if( atArr[i]=="@"+chatgroupMember.userName ){
        //           let chatdataState ={};
        //           chatdataState.targetUserId =chatgroupMember.userId;
        //           chatdataState.targetUserName =chatgroupMember.userName;
        //           chatdataState.targetCompanyId =chatgroupMember.companyId;
        //           chatdataState.targetCompanyName =chatgroupMember.companyName;
        //           chatdata.chatdataStates[indexTemp] =chatdataState;
        //           indexTemp =indexTemp+1;
        //         }
        //       }
        //     }
        //   }
        //   message =this.messageCurrent;
        // }

        if (chatdata.chatdataStates.length == 0) {
          // 提示，请勾选接收人或者@接收人，暂时不支持@所有人，如果供应商使用，会有问题，请勾选接收人或者输入@接收人
          this.$message.error('请勾选消息接收成员')
          return
        }
        chatdata.chatData = message
      }

      createChatdata(chatdata).then(res => {
        if (res) {
          let resMsg = res.data
          let formatUserName = resMsg.createdUserBy ? (resMsg.createdUserBy).substr(-2, 2) : ''
          let chatData = resMsg.chatData
          // let replyArr = [] // 回复消息
          // if(chatData && chatData.indexOf('###') > -1){
          //   replyArr = chatData.split('###')
          // }else{
          //   replyArr = [chatData]
          // }
          let result = {
            ...resMsg,
            // replyArr: replyArr, // 回复消息
            formatUserName: formatUserName // 用户名
          }
          this.chatdataArr.push(result)
          // 清除输入框
          this.messageCurrent = ''
          if (this.megType === 'reply') { // 回复消息的时候清掉回复消息
            this.clearReplyMeg()
          }
          // 滚动到底部
          this.$nextTick(() => {
            this.scrollBoxToButtom()
          })
        }
      })
    },
    // 点击发起群聊或者添加成员
    toGroupChatHandel (type) {
      this.openModeSelector = type
      if (type === 'addMembers') {
        // 添加群成员
        debugger
        if (this.chatgroupCurrent == null || this.chatgroupCurrent.chatgroupId == null) {
          this.$message.error('请先选中聊天群组')
          return
        }
        this.groupVisibale = true
        this.$nextTick(() => {
          this.$refs.groupChatSelector.initParam(type, this.chatgroupCurrent)
        })
      } else {
        // 创建群 newGroup
        this.groupVisibale = true
        this.$nextTick(() => {
          this.$refs.groupChatSelector.initParam(type, {})
        })
      }
    },
    // 群聊确认选择用户，创建者创建，立刻更新
    confirmGroupHandel (data) {
      console.log(data)
      if (this.openModeSelector == 'newGroup') {
        // 头部增加
        this.chatgroupArr.unshift(data)
        // 触发点击选中
        this.clickChatgroupHandle(data, 0)
      } else {
        // 加到当前聊天组
        let chatgroupMembers = data.chatgroupMembers || []
        chatgroupMembers.forEach(obj => {
          let row = obj
          this.chatgroupCurrent.chatgroupMembers.push(row)
          if (row.userType === 'BUYER') {
            this.chatgroupMembersBuyer.push(row)
          } else {
            this.chatgroupMembersVendor.push(row)
          }
        })
        // this.clickChatgroupHandle(data, 0);
      }
    },
    // 取消
    cancelGroupHandel (data) {
      this.groupVisibale = data
    },
    // 更新当前聊天的成员在线离线状态
    updateOnlineByWebsocket (userIdOnline, onlineStatus) {
      // 检查当前聊天的成员更新状态
      if (this.chatgroupCurrent == null || this.chatgroupCurrent.chatgroupMembers == null) {
        return
      }
      for (let i = 0; i < this.chatgroupCurrent.chatgroupMembers.length; i++) {
        let chatgroupMember = this.chatgroupCurrent.chatgroupMembers[i]
        if (chatgroupMember.userId == userIdOnline) {
          chatgroupMember.onlineStatus = onlineStatus
        }
      }
    },
    // 更新当前聊天的已读状态
    updateReadByWebsocket (chatdataObj) {
      // 打开了对应的聊天才会实时更新
      if (this.chatgroupCurrent == null) {
        return
      }
      if (this.chatgroupCurrent.chatgroupId == chatdataObj.chatgroupId) {
        for (let i = 0; i < this.chatdataArr.length; i++) {
          // 自己发的，对方已读
          // if(this.chatdataArr[i].createdId==this.userIdCurrent && this.chatdataArr[i].chatdataId==chatdataObj.chatdataId){
          //   //到后端统计是否全部已读，是的话再更新状态，由于单靠前端，做不到统计剩余的人已读状态，要做统计的话，会有大量的状态数据
          // }
          // 后端已经判断后，只会发送到创建人，并且统计好状态
          if (this.chatdataArr[i].chatdataId == chatdataObj.chatdataId) {
            this.chatdataArr[i].readStatusName = chatdataObj.readStatusName
          }
        }
      }
    },
    // 显示其他人发送的消息内容
    createChatdataByWebsocket (chatdataObj) {
      let _this = this
      if (this.chatgroupCurrent == null) {
        return
      }
      // 自己创建的，不处理
      if (this.userIdCurrent == chatdataObj.createdId) {
        return
      }
      // 打开了对应的聊天才会实时更新
      if (this.chatgroupCurrent.chatgroupId == chatdataObj.chatgroupId) {
        // 先判断是否撤回
        if (chatdataObj.isWithdrow == 'Y') {
          // 更新界面
          let withdrowData = chatdataObj
          let chatdataId = withdrowData.chatdataId
          let oldIndex = _this.chatdataArr.findIndex(i => i.chatdataId == chatdataId)
          if (oldIndex > -1) { // 找到数据
            _this.chatdataArr[oldIndex].chatData = withdrowData.chatData // 撤回内容
            _this.chatdataArr[oldIndex].showType = withdrowData.showType // 消息状态设为已撤回
          }
          _this.$forceUpdate()
        } else {
          this.chatdataArr.push(chatdataObj)
          // 更新已读状态
          updateChatdataRead(chatdataObj).then(res => {
            // 不报错就行
          })
        }
      }

      // 找到对应的聊天群组，并且更新最新的消息，并且移到最顶部
      for (let i = 0; i < this.chatgroupArr.length; i++) {
        let chatgroupObj = this.chatgroupArr[i]
        if (chatgroupObj.chatgroupId == chatdataObj.chatgroupId) {
          // 去掉@的显示，可以显示的更多信息
          let sTemp = ''
          let arrTemp = chatdataObj.chatData.split(' ')
          for (let j = 0; j < arrTemp.length; j++) {
            if (arrTemp[j].indexOf('@') == -1) {
              sTemp = sTemp + arrTemp[j]
            }
          }
          chatgroupObj.lastChatdata = sTemp
          chatgroupObj.unreadCount = chatgroupObj.unreadCount + 1
          chatgroupObj.lastChattime = this.dateFtt('hh:mm', new Date(chatdataObj.creationDate))
          this.chatgroupArr[i] = chatgroupObj
        }
      }
    },
    dateFtt (fmt, date) {
			let o = {
				'M+': date.getMonth() + 1, // 月份
				'd+': date.getDate(), // 日
				'h+': date.getHours(), // 小时
				'm+': date.getMinutes(), // 分
				's+': date.getSeconds(), // 秒
				'q+': Math.floor((date.getMonth() + 3) / 3), // 季度
				'S': date.getMilliseconds() // 毫秒
			}
			if (/(y+)/.test(fmt)) { fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length)) }
			for (let k in o) {
        if (new RegExp('(' + k + ')').test(fmt)) { fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (('00' + o[k]).substr(('' + o[k]).length))) }
      }
			return fmt
    },
    // 显示其他人创建的群组
    createChatgroupByWebsocket (chatgroupObj) {
      // 自己创建的，不处理
      if (this.userIdCurrent == chatgroupObj.createdId) {
        return
      }
      // 头部增加
      this.chatgroupArr.unshift(chatgroupObj)
    },
   // 搜索用户，只用于搜索聊天标题
    userSearchHandel (val) {
      if (val) {
        this.userSearchArr = []
        // 在现有基础上过滤
        for (let i = 0; i < this.chatgroupArr.length; i++) {
          if (this.chatgroupArr[i].groupName.indexOf(val) > -1) {
            this.userSearchArr.push(this.chatgroupArr[i])
          }
        }
        if (this.userSearchArr.length > 0) {
          this.seachFlowDivVisible = true
        }
      }
    },
    // 添加用户到聊天窗口
    selectUserToChatHandel (row) {
      this.seachFlowDivVisible = false
      // 触发点击选中
      this.clickChatgroupHandle(row, 0)
    },
    // 把用户移除群聊
    removeUserFromGroup (userInfo, index, type) {
      // 判断，不能移除自己
      if (userInfo.userId == this.userIdCurrent) {
        this.$message.error('不能移除自己')
      } else {
        this.$confirm('确认移除该成员吗？', {
          confirmButtonText: this.$t('common.affirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        }).then(() => {
          removeChatgroupMember(userInfo).then(res => {
            if (res) {
              // 界面移除
              for (let i = 0; i < this.chatgroupCurrent.chatgroupMembers.length; i++) {
                if (userInfo.chatgroupMemberId == this.chatgroupCurrent.chatgroupMembers[i].chatgroupMemberId) {
                  this.chatgroupCurrent.chatgroupMembers.splice(i, 1)
                  break
                }
              }
              if (type === 'vendor') {
                this.chatgroupMembersVendor.splice(index, 1) // 删除供应商
              } else {
                this.chatgroupMembersBuyer.splice(index, 1) // 删除采购商
              }
              console.log(this.chatgroupMembersVendor)
              console.log(this.chatgroupMembersBuyer)
              console.log(this.chatgroupCurrent)
            }
          })
        }).catch(() => {})
      }
    },
    // 回复消息
    replyHandel (data) {
      // 因为该方法是公用的，需要判断是否是否自己的消息
      if (data.createdId === this.userIdCurrent) {
        return
      }
      this.megType = 'reply' // 发送的消息类型为回复
      this.curRelyMesInfo = data //
    },
    clearReplyMeg () {
      this.megType = ''
      this.curRelyMesInfo = {} // 清空
    },
    // 撤回
    withdrowHandel (data) {
      let _this = this
      // 确定是否撤回？
      this.$confirm('确定是否撤回吗？', {
          confirmButtonText: this.$t('common.affirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        }).then(() => {
          this.withdrowDataInfo = data // 保留当前撤回的数据 可能用于再次编辑
          withdrowChatdata(data).then(res => {
            if (res) {
              // 更新界面
              let withdrowData = res.data
              let chatdataId = withdrowData.chatdataId
              let oldIndex = _this.chatdataArr.findIndex(i => i.chatdataId == chatdataId)
              if (oldIndex > -1) { // 找到数据
                _this.chatdataArr[oldIndex].chatData = withdrowData.chatData // 撤回内容
                _this.chatdataArr[oldIndex].showType = withdrowData.showType // 消息状态设为已撤回
              }
              _this.$forceUpdate()
              console.log(_this.chatdataArr)
              // for(let i = 0; i　< _this.chatdataArr.length; i++) {
              //   let chatdata =_this.chatdataArr[i];
              //   if( res.chatdataId==chatdata.chatdataId ){
              //     chatdata.chatData =res.chatData;
              //     _this.chatdataArr[i] =chatdata;
              //   }
              // }
            }
          })
        }).catch(() => {})
    },
    // 采购商全选
    buyerSelectAll () {
      this.chatgroupMembersBuyer.forEach(elm => {
        elm.selectedFlag = true
      })
    },
    // 采购商全选
    vendorSelectAll () {
      this.chatgroupMembersVendor.forEach(elm => {
        elm.selectedFlag = true
      })
    },

    // 左边用户列表：数据返回希望是上最近聊天的放在前面
		getleftUserData (businessId) {
			// 获取通讯对象列表
			// 处理刷新引起的退出
      this.$http({
        url: '/common/chat/queryChattargetCurrentUserByBusiness.ac',
        method: 'GET',
        params: { businessId: businessId },
        loading: true
      })
        .then(res => {
          let userArr = res.data
					// if (userArr == null || userArr.length == 0) {}
        })
        .catch(err => {
          console.log(err)
        })
		},

    // 获取聊天历史记录
    getChatData (chattargetId, companyId) {
			if (chattargetId == null || chattargetId == '') {
				return
			}
      this.$http({
        url: '/common/chat/queryChatdataByBusiness.ac',
        method: 'POST',
        data: {
          businessId: chattargetId,
					companyId: companyId
        }
      })
      .then(res => {
        this.hisMsgArr = res.data // 历史信息
        // addChatdata(msgArr);
      })
      .catch(err => {
        console.log(err)
      })
		},
    // 获取换行\空格等格式一起发送
    getValueFamate (strValue) {
			return strValue.replace(/\r\n/g, '<br/>').replace(/\n/g, '<br/>')
					.replace(/\s/g, ' ')
		},
    // 添加消息到消息框
		addChatdata (msgArr) {
			let msgArrLength = msgArr.length
			// 后台是降序返回的
			for (let j = 0; j < msgArrLength; j++) {
				let iIndex = msgArrLength - j - 1
				let mId = msgArr[iIndex].mId // id
				let userName = msgArr[iIndex].userName // name
				let profileImgUrl = './img/defaultProfile.jpg' // 头像url
				let msgContent = ''
				let type = msgArr[iIndex].type // 信息类型 接收|发送
      }
		},
    // 重新编辑
    reEdit () {
      this.sendMessage = this.withdrowDataInfo.chatData
    },
    scrollBoxToButtom () {
      let div = this.$refs.chatMsgList
      div.scrollTop = div.scrollHeight
    }

  }
}
</script>
<style>
  .groupChatUserTit {
    color: #75b9fd;
    font-weight:bold;
  }

</style>
