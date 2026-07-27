<template>
  <div
    id="sysMessage"
    class="sysMsgInner"
  >
    <div class="sysMsgListLeft">
      <div class="imSearch">
        <div
          id="sysMsg"
          class="searchBar"
        >
          <div class="searchInner">
            <input
              type="text"
              name="searchMsg"
              class="imSearchInput"
            >
            <span class="imsearchBtn" />
          </div>
        </div>
        <div
          id="msgList"
          class="sysMsgList"
        >
          <ul id="sysMsgUl">
            <li
              v-for="msgT in msgTypeArr"
              :key="msgT.id"
              @click="noteTypeHandel(msgT.id)"
            >
              <div>
                <div class="profileImg">
                  <img src="./img/defaultProfile.jpg">
                </div>
                <div class="imUuser">
                  <span class="userNameText">{{ msgT.noteType }}</span>
                  <span class="lastTime">{{ $parseTime(msgT.lastTime) }}</span>
                </div>
                <div class="imCompany">
                  {{ msgT.notetitle }}
                  <span
                    v-if="msgT.unreadMsg>0"
                    class="unRead"
                  >{{ msgT.unreadMsg }}</span>
                </div>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>
    <div class="sysMsgShowContent">
      <div class="sysMsgShowInner">
        <div class="msgTitleBar">
          <span>{{ $t('chatMod.viewMessage') }}</span>
        </div>
        <div
          id="msgContent"
          class="showMsg"
        >
          <div id="msgContentViewPort">
            <div
              v-for="item in msgContentArr"
              :key="item.id"
              class="msgSection"
            >
              <div class="notePbTime">
                {{ $parseTime(item.pbTime) }}
              </div>
              <div class="noteDetail">
                <div class="notesTitle">
                  {{ item.title }}
                </div>
                <el-collapse-transition>
                  <!-- v-show=""  点击查看显示详情的效果后面优化-->
                  <div class="notesContent">
                    {{ item.content }}
                  </div>
                </el-collapse-transition>
                <div class="notesReadMore">
                  {{ $t('orderMod.viewDetail') }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>

export default {
  name: 'SysMsgMod',
  props: {
    isSearch: { // 是否显示搜索框
      type: Boolean,
      default: true
    },
    imModule: {// 当前模式
      type: String,
      default: 'chartModule'
    }
  },
  data () {
    return {
      userSearchKey: '', // 搜索用户绑定的变量
      msgTypeArr: [// 左边消息类型列表
        // {
        //   id:'12434sd',
        //   noteType:'sys',
        //   notetitle:'费大幅度发',
        //   unreadMsg:2,
        //   lastTime:'12:30',
        // }
      ],
      msgContentArr: [// 消息类型对应的消息详情列表
        // {
        //   id : 11,
        //   title : '敬请期待',
        //   pbTime : '2019-06-25 00:00',
        //   content : '建设中，敬请期待',
        //   autor : '协作云系统'
        // }
      ]
    }
  },
  created () {

  },
  methods: {
    // 左边消息类型点击 获取公告列表
    noteTypeHandel (id) {

    }
  }
}
</script>
<style>

</style>
