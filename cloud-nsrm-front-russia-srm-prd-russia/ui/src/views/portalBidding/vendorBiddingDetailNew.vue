<template>
  <el-container class="the_vendorBiddingDetail_wrapper" direction="vertical">
    <el-main>
      <AppHeader pageType="gateway" />
      <img src="../login/img/bulletinBoard.jpg" style="width:100%" alt="">
      <div class="main">
        <div class="title">
          {{ form.title }}
        </div>
        <div class="sign">
          <el-button type="primary" @click="signUp">
            {{ $t('sourcingBuyer.signUp') }}
          </el-button>
        </div>
        <span>附件：</span>
        <span v-if="fileList.length === 0">无</span>
        <el-button
          v-for="item in fileList"
          :key="item.fileId"
          type="text"
          @click="downloadFile(item)"
        >
          {{ item.fileName }}
        </el-button>
        <div class="content" v-html="form.content" />
      </div>
      <!-- <CToolbar>
        <template slot="right">
          <el-button type="primary" @click="signUp">
            {{ $t('sourcingBuyer.signUp') }}
          </el-button>
        </template>
      </CToolbar> -->
    </el-main>

    <!-- 报名登记弹窗 -->
    <SignUpDialog
      ref="signUpDialog"
      :visible.sync="signUpDialogVisible"
      @confirm="signUpDialogConfirm"
    />
  </el-container>
</template>

<script>
import CToolbar from 'lib@/components/c-toolbar'
import { mapGetters } from 'vuex'
import Header from '@/layout/components/Header/index.vue'
import ProjectInformation from './vendorBiddingDetail/projectInformation'
import ProjectRequirement from './vendorBiddingDetail/projectRequirement'
import SignUpDialog from './signUpDialog'
import { getToken, setRedirectUrl } from '@/utils/auth'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'BiddingProjectDetail',

  components: {
    AppHeader: Header,
    CToolbar,
    SignUpDialog
  },

  data () {
    return {
      recruitId: null,
      form: {
        title: '',
        content: ''
      },
      fileList: [],
      signUpDialogVisible: false
    }
  },

  computed: {

  },

  created () {
    this.recruitId = this.$route.query.id
    if (this.recruitId) {
      this.getFormDetail()
    }
  },

  methods: {
    // 文件下载
    downloadFile (row) {
      let newUrl = `${window.location.origin}/cloud-srm/api-pj/external/file/download?fileSourceName=${row.fileName}&fileuploadId=${row.fileId}`
      window.open(newUrl, '_self')
    },
    /* 查询详情 */
    async getFormDetail () {
      let transformParams = transformMQL.save('Recruit', [{
        recruitId: this.recruitId
      }], 'visitDetail', {
        '*': {},
        'contentList': {
          '*': {}
        },
        'fileList': { '*': {} }
      })
      const response = await this.$http({
        url: '/api-sou/api-ql/Recruit/visitDetail',
        method: 'POST',
        data: transformParams,
        loading: true
      })
      console.log('response', response)
      if (response.data.length) {
        this.form.title = response.data[0].title
        this.form.content = response.data[0].contentList[0].content
        this.fileList = response.data[0].fileList
      }
    },
    signUp () {
      this.signUpDialogVisible = true
    },
    async signUpDialogConfirm  (data) {
      let transformParams = transformMQL.save('RecruitVendor', [{
        ...data,
        recruitId: this.recruitId
      }], 'visitEnroll')
      const response = await this.$http({
        url: '/api-sou/api-ql/RecruitVendor/visitEnroll',
        method: 'POST',
        data: transformParams,
        loading: true
      })
      if (response) {
        this.signUpDialogVisible = false
        this.$message.success('报名成功')
      }
    }
  }
}
</script>

<style scoped lang="scss">
.main {
  width: 70%;
  max-width: 1200px;
  margin: 15px auto 0;
  .content {
    font-size: 14px;
  }
}

.title {
  text-align: center;
  font-weight: bold;
  font-size: 18px;
}
.sign {
  text-align: right;
  margin: 10px 0;
}
</style>
