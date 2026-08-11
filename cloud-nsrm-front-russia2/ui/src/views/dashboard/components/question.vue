<template>
    <div class="grid-content">
      <h3 class="grid-title">
        <!-- 用户声音 -->
        {{ $t('cusEntry.dashboard.userSound') }}
      </h3>
      <div class="content" style="cursor: pointer;" @click="goQuestion">
        <i class="el-icon-microphone" style="font-size: 16px;color:#0077ff;"></i>
        <!-- 用户反馈 -->
        {{ $t('cusEntry.dashboard.userFeedBack') }}
      </div>
    </div>
  </template>
  <script>
  import axios from 'axios'
  export default {
    name: 'Question',
    components: {},
    data () {
      return {
        userInfo: this.$store.getters.userInfo
      }
    },
    methods: {
        goQuestion () {
            axios.get('/question/opm/external/sys/select_call_info?sign=CBBCFUMWXC').then(res => {
                const url = res.data?.url
                let jumpUrl = ''
                if (this.userInfo.userType === 'VENDOR') {
                  jumpUrl = `${url}&platform=长城慧采云&userid=${this.userInfo.phone}(${this.userInfo.companyName})`
                } else {
                  jumpUrl = `${url}&platform=长城慧采云&userid=${this.userInfo.username}(${this.userInfo.nickname})`
                }
                if (url) {
                    window.open(jumpUrl)
                }
            })
        }
    }
  }
  </script>
  <style lang="scss" scoped>
  
  </style>
  