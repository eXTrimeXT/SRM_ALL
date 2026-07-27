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
              <!-- 寻源单号： -->
              {{ $t('cusEntry.bidMod.reqHeadNo') }}<span>{{ form.reqHeadNo }}</span>
            </div>
            <div class="header-item">
              <!-- 报名联系人： -->
              {{ $t('cusEntry.bidMod.contactName') }}<span>{{ form.contactName }}</span>
            </div>
          </div>
          <div class="header-row">
            <div class="header-item">
              <!-- 发布时间： -->
              {{ $t('cusEntry.bidMod.releaseDate') }}<span>{{ $parseTime(form.releaseDate) }}</span>
            </div>
            <div class="header-item">
              <!-- 报名联系电话： -->
              {{ $t('cusEntry.bidMod.contactPhone') }}<span>{{ form.phone }}</span>
            </div>
          </div>
          <div class="header-row">
            <div class="header-item">
              <!-- 公示截止时间： -->
              {{ $t('cusEntry.bidMod.publicEndTime') }}<span>{{ $parseTime(form.publicEndTime) }}</span>
            </div>
            <div class="header-item">
              <!-- 办公电话： -->
              {{ $t('cusEntry.bidMod.officePhone') }}<span>{{ form.officePhone }}</span>
            </div>
          </div>
          <div class="header-row">
            <div class="header-item">
              <!-- 项目所在地： -->
              {{ $t('cusEntry.bidMod.projectAddress') }}<span>{{ form.projectAddress }}</span>
            </div>
            <div class="header-item">
              <!-- 品类： -->
              {{ $t('cusEntry.bidMod.categoryName') }}<span>{{ form.categoryName }}</span>
            </div>
          </div>
          <div v-if="fileUploads.length" class="header-row">
            <!-- 附件： -->
            {{ $t('cusEntry.bidMod.fileUpload') }}
            <div v-for="(item,index) in fileUploads" :key="index" class="file-item">
              <SrmCommonFile :default-file="{fileId:item.fileuploadId,fileName:item.fileName}" readonly />
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="content">
      <div class="content-box">
        <el-divider class="sub-title" content-position="left">
          <!-- 寻源内容 -->
          {{ $t('cusEntry.bidMod.searchContent') }}
        </el-divider>
        <div class="content-title">
          <!-- 项目概况与招标范围 -->
          {{ $t('cusEntry.bidMod.bidScope') }}
        </div>
        <div class="content-info">
          {{ form.projectScope }}
        </div>
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
          <!-- 投标主体报名要求 -->
          {{ $t('cusEntry.bidMod.registRequirements') }}
        </el-divider>
        <div class="content-item">
          <div class="content-title">
            <!-- 一、技术要求 -->
            {{ $t('cusEntry.bidMod.techRequirements') }}
          </div>
          <div class="content-info">
            {{ form.technicalReq }}
          </div>
        </div>
        <div class="content-item">
          <div class="content-title">
            <!-- 二、投标主体资质要求 -->
            {{ $t('cusEntry.bidMod.qualifyRequirements') }}
          </div>
          <div class="content-info">
            {{ form.vendorQualReq }}
          </div>
        </div>
        <div class="content-item">
          <div class="content-title">
            <!-- 三、投标主体业绩要求 -->
            {{ $t('cusEntry.bidMod.performanceRequirements') }}
          </div>
          <div class="content-info">
            {{ form.performanceReq }}
          </div>
        </div>
        <div class="content-item">
          <div class="content-title">
            <!-- 四、报名方式 -->
            {{ $t('cusEntry.bidMod.registMethod') }}
          </div>
          <div class="content-info">
            <p>{{ $t('cusEntry.supplement20250205.registMethodTip1') }}</p>
            <p>{{ $t('cusEntry.supplement20250205.registMethodTip2') }}</p>
            <p>{{ $t('cusEntry.supplement20250205.registMethodTip3') }}</p>
            <p>{{ $t('cusEntry.supplement20250205.registMethodTip4') }}</p>
          </div>
        </div>
        <!-- 无意向金时不展示 -->
        <!-- <div v-if="form.isNeedDeposit === 'Y'" class="content-item">
          <div class="content-title">
            五、意向金
          </div>
          <div class="content-info">
            投标意向金：{{ form.depositAmount }}（大写：人民币{{ amountToChinese(form.depositAmount) }}）
          </div>
          <div class="remind red">
            <div v-html="pjSourcePubconfig.earnestDescr" />
          </div>
        </div> -->
        <!-- <div class="content-item">
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
        </div> -->
        <!-- 投诉渠道/反馈服务 -->
        <div class="content-item">
          <el-divider class="sub-title" content-position="left">
            {{ $t('cusEntry.biddingSettings.complaints') }}
          </el-divider>
          <div class="content-info">
            <div v-html="pjSourcePubconfig.groupTipOff" />
          </div>
        </div>
      </div>
      <!-- <div class="logo">
        <img :src="require('./img/logo.png')">
      </div> -->
    </div>
  </div>
</template>
<script>
import DynamicCutoffTime from 'lib@/components/dynamic-cutoff-time'
import { transformMQL } from 'lib@/utils/util'
import { mapGetters } from 'vuex'
import * as path from '@/utils/path'
import { getToken, setRedirectUrl } from '@/utils/auth'

export default {
  components: {
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

    // amountToChinese (n) {
    //   var fraction = ['角', '分']
    //   var digit = ['零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖']
    //   var unit = [['元', '万', '亿'], ['', '拾', '佰', '仟']]
    //   var head = n < 0 ? '欠' : ''
    //   n = Math.abs(n)

    //   var s = ''

    //   for (var i = 0; i < fraction.length; i++) {
    //     s += (digit[Math.floor(n * 10 * Math.pow(10, i)) % 10] + fraction[i]).replace(/零./, '')
    //   }
    //   s = s || '整'
    //   n = Math.floor(n)

    //   for (var i = 0; i < unit[0].length && n > 0; i++) {
    //     var p = ''
    //     for (var j = 0; j < unit[1].length && n > 0; j++) {
    //       p = digit[n % 10] + unit[1][j] + p
    //       n = Math.floor(n / 10)
    //     }
    //     s = p.replace(/(零.)*零$/, '').replace(/^$/, '零') + unit[0][i] + s
    //   }
    //   return head + s.replace(/(零.)*零元/, '元').replace(/(零.)+/g, '零').replace(/^整$/, '零元整')
    // },

    goToLogin (type) {
      this.$confirm(this.$t('dashboard.signUpTips'), this.$t('dashboard.needLogin'), {
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
