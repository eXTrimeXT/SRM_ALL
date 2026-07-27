<template>
  <div class="flex-container wrapper">
    <div class="header">
      <div class="header-title">
        {{ form.projectName }}
      </div>
      <div class="header-content">
        <div class="header-info">
          <div class="header-row">
            <div class="header-item">
              寻源单号：<span>{{ form.reqHeadNo }}</span>
            </div>
            <div class="header-item">
              报名联系人：<span>{{ form.contactName }}</span>
            </div>
          </div>
          <div class="header-row">
            <div class="header-item">
              发布时间：<span>{{ form.releaseDate }}</span>
            </div>
            <div class="header-item">
              报名联系电话：<span>{{ form.phone }}</span>
            </div>
          </div>
          <div class="header-row">
            <div class="header-item">
              公示截止时间：<span>{{ form.publicEndTime }}</span>
            </div>
            <div class="header-item">
              办公电话：<span>{{ form.officePhone }}</span>
            </div>
          </div>
          <div class="header-row">
            <div class="header-item">
              项目所在地：<span>{{ form.projectAddress }}</span>
            </div>
            <div class="header-item">
              品类：<span>{{ form.categoryName }}</span>
            </div>
          </div>
          <div v-if="fileUploads.length" class="header-row">
            附件：
            <div v-for="(item,index) in fileUploads" :key="index" class="file-item">
              <SrmCommonFile :default-file="{fileId:item.fileuploadId,fileName:item.fileName}" readonly />
            </div>
          </div>
        </div>
        <!--        <div class="header-operater">-->
        <!--          <div class="dead-time">-->
        <!--            <DynamicCutoffTime :label="$t('bidMod.competitionLts.signUpEndTime')" :deadline-time="form.publicEndTime" />-->
        <!--          </div>-->
        <!--          <template v-if="showButtons">-->
        <!--            <div class="btn">-->
        <!--              <el-button type="primary" @click="viewSignUp">-->
        <!--                立即报名-->
        <!--              </el-button>-->
        <!--            </div>-->

        <!--            <div class="btn">-->
        <!--              <el-button type="primary" @click="viewTech">-->
        <!--                技术澄清-->
        <!--              </el-button>-->
        <!--            </div>-->
        <!--          </template>-->
        <!--        </div>-->
      </div>
    </div>
    <div class="content">
      <div class="content-box">
        <el-divider class="sub-title" content-position="left">
          寻源内容
        </el-divider>
        <div class="content-title">
          项目概况与招标范围
        </div>
        <div class="content-info" v-html="form.projectScope?.replace(/\n/g,'</br>')" />
        <div class="content-desc">
          <!-- <div class="desc">
            品类：
            <span>{{ form.categoryName }}</span>
          </div> -->
          <!-- <div class="requireQuantity">
            规模数量：
            <span>{{ form.requireQuantity }}</span>
          </div> -->
        </div>
      </div>
      <div class="content-box">
        <el-divider class="sub-title" content-position="left">
          投标主体报名要求
        </el-divider>
        <div class="content-item">
          <div class="content-title">
            一、技术要求
          </div>
          <div class="content-info" v-html="form.technicalReq?.replace(/\n/g,'</br>')" />
        </div>
        <div class="content-item">
          <div class="content-title">
            二、投标主体资质要求
          </div>
          <div class="content-info" v-html="form.vendorQualReq?.replace(/\n/g,'</br>')" />
        </div>
        <div class="content-item">
          <div class="content-title">
            三、投标主体业绩要求
          </div>
          <div class="content-info" v-html="form.performanceReq?.replace(/\n/g,'</br>')" />
        </div>
        <div class="content-item">
          <div class="content-title">
            四、报名方式
          </div>
          <div class="content-info">
            <div v-html="pjSourcePubconfig.signType" />
          </div>
        </div>
        <!-- 无意向金时不展示 -->
        <div v-if="form.isNeedDeposit === 'Y'" class="content-item">
          <div class="content-title">
            五、意向金
          </div>
          <div class="content-info">
            投标意向金：{{ form.depositAmount }}（大写：人民币{{ amountToChinese(form.depositAmount) }}）
          </div>
          <div class="remind red">
            <div v-html="pjSourcePubconfig.earnestDescr" />
          </div>
          <!-- <div class="common-tips">
            <p>注意：</p>
            <p>①原则上以公司交款为准，即公对公账户；注册成功后汇款至以上账户；</p>
            <p>②交款行为仅在此账户有效力，我司不存在其他任何代收账户；</p>
            <p>③请不要相信任何个人或公司的交款要求，以防上当受骗！</p>
          </div> -->
        </div>
        <div class="content-item">
          <el-divider class="sub-title" content-position="left">
            发布媒体
          </el-divider>
          <div class="content-info">
            <div v-html="pjSourcePubconfig.pubMedium" />
          </div>
        </div>
        <div class="content-item">
          <el-divider class="sub-title" content-position="left">
            争议解决
          </el-divider>
          <div class="content-info">
            <div v-html="pjSourcePubconfig.disputeReso" />
          </div>
        </div>
        <div class="content-item">
          <el-divider class="sub-title" content-position="left">
            集团举报渠道
          </el-divider>
          <div class="content-info">
            <div v-html="pjSourcePubconfig.groupTipOff" />
          </div>
        </div>
      </div>
      <div class="logo">
        <img :src="require('./img/logo.png')">
      </div>
      <!--      <VendorLoginDialog-->
      <!--        v-if="vendorLoginVisible"-->
      <!--        :visible.sync="vendorLoginVisible"-->
      <!--        :projectInfo="projectInfo"-->
      <!--      />-->
    </div>
  </div>
</template>
<script>
import DynamicCutoffTime from 'lib@/components/dynamic-cutoff-time'
import { transformMQL } from 'lib@/utils/util'
import { mapGetters } from 'vuex'
import * as path from '@/utils/path'
import { getToken, setRedirectUrl } from '@/utils/auth'
// import VendorLoginDialog from './vendorLoginDialog'

export default {
  components: {
    // VendorLoginDialog,
    DynamicCutoffTime
  },
  data () {
    return {
      form: {
        publicEndTime: null,
        reqHeadId: null,
        reqHeadNo: null
      },
      vendorLoginVisible: false,
      projectInfo: {},
      pjSourcePubconfig: {},
      fileUploads: []
    }
  },
  computed: {
    ...mapGetters(['userInfo']),
    showButtons () { // 公示截至时间需小于当前时间才显示 报名、澄清
      if (this.form.publicEndTime) {
        return new Date(this.form.publicEndTime).getTime() > new Date().getTime()
      }
      return true
    }
  },
  created () {
    this.reqHeadId = this.$attrs.params.row.reqHeadId
    this.getFormDetail()
    console.log('reqHeadId', this.reqHeadId)
  },
  methods: {
    async getFormDetail () {
      let transformParams = transformMQL.save('SouReqHead', [this.reqHeadId], 'souReqgetById',
        {
          '*': {},
          'souReqApplyList': {
            '*': {}
          },
          'fileUploads': {
            '*': {}
          }
        }
      )
      const response = await this.$http({
        url: '/api-sou/api-ql/SouReqHead/souReqgetById',
        method: 'POST',
        data: transformParams,
        loading: true
      })
      if (response && response.data.length) {
        const { fileUploads, pjSourcePubconfig, souReqApplyList, ...rest } = response.data[0]
        this.form = rest
        this.fileUploads = fileUploads
        this.pjSourcePubconfig = pjSourcePubconfig
      }
    },

    amountToChinese (n) {
      var fraction = ['角', '分']
      var digit = ['零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖']
      var unit = [['元', '万', '亿'], ['', '拾', '佰', '仟']]
      var head = n < 0 ? '欠' : ''
      n = Math.abs(n)

      var s = ''

      for (var i = 0; i < fraction.length; i++) {
        s += (digit[Math.floor(n * 10 * Math.pow(10, i)) % 10] + fraction[i]).replace(/零./, '')
      }
      s = s || '整'
      n = Math.floor(n)

      for (var i = 0; i < unit[0].length && n > 0; i++) {
        var p = ''
        for (var j = 0; j < unit[1].length && n > 0; j++) {
          p = digit[n % 10] + unit[1][j] + p
          n = Math.floor(n / 10)
        }
        s = p.replace(/(零.)*零$/, '').replace(/^$/, '零') + unit[0][i] + s
      }
      return head + s.replace(/(零.)*零元/, '元').replace(/(零.)+/g, '零').replace(/^整$/, '零元整')
    },

    goToLogin (type) {
      this.$confirm('报名前需先登录系统，若无账号需先注册', '需要先登录系统', {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        let href = window.location.href.split('#')[1]
        setRedirectUrl(decodeURI(href))
        this.$router.push({ path: path.resolve('/login') })
      })
    },
    viewSignUp  () {
      if (this.userInfo && this.userInfo.userId) {
        // 跳转到报名详情页
        this.$router.push({
          name: 'sourcingCooperation',
          params: {
            from: 'portal',
            row: {
              id: this.reqHeadId,
              formNo: this.form.reqHeadNo
            }
          }
        })
      } else {
        // this.goToLogin()
        this.projectInfo = {
          flag: 'sign',
          id: this.reqHeadId,
          formNo: this.form.reqHeadNo
        }
        this.vendorLoginVisible = true
      }
    },
    viewTech () {
      if (this.userInfo && this.userInfo.userId) {
        // 跳转到质疑澄清页面
        this.$router.push({
          name: 'biddingQas',
          params: {
            from: 'portal', // 来源路由name
            flag: 'bid',
            row: {
              souName: this.form.projectName,
              projectId: this.form.projectId || this.form.requirementHeadId,
              extType: 'REQ'
            }
          }
        })
      } else {
        // this.goToLogin()
        this.projectInfo = {
          flag: 'bid',
          souName: this.form.projectName,
          projectId: this.form.projectId || this.form.requirementHeadId
        }
        this.vendorLoginVisible = true
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.wrapper {
  overflow: auto;
  height: 100%;
  padding: 15px 10%;
}
.header {
  .header-title {
    text-align: center;
    font-weight: bold;
    font-size: 18px;
  }
  .header-content {
    display: flex;
    justify-content: start;
    margin-top: 20px;
    .header-info {
      font-size: 14px;
      flex:2;
      .header-row {
        display:flex;
        align-items: center;
        margin-top: 10px;
        .header-item {
          flex:1;
          span {
          }
        }
      }
    }
    .header-operater {
      flex:1;
      font-size: 14px;
      .dead-time {

      }
      .btn {
        margin-top: 10px;
        padding-left: 30px;
      }
    }
  }
}
.content-box {
  font-size: 14px;
  .content-title {
    font-weight: bold;
    margin-top: 20px;
  }
  .content-info {
    line-height: 24px;
    margin: 10px 0;
  }
  .content-item {
    margin: 30px 0;
  }
  .content-desc {
    display: flex;
    align-items: center;
    >div {
      flex: 1;
    }
  }
}
.logo {
  img {
    width: 200px;
  }
}
.content-box {
  margin-top: 40px;
}
</style>
<style>
.sub-title .el-divider__text {
  font-size: 18px !important;
  color: #1a1a1a;
  font-weight: 800;
}
.content-box p {
  line-height: 24px;
}
</style>
