<template>
  <el-container
    class="flex-container-notab the_vendor_dashboard_wrapper dashboard"
    direction="vertical"
  >
    <el-main>
      <el-row>
        <!-- 业务单据任务 -->
        <businessProcessTask />
      </el-row>
      <el-row
        :gutter="16"
      >
        <el-col
          :xs="24"
          :sm="24"
          :md="17"
          :lg="17"
          :xl="17"
        >
          <!-- 常用功能设置 -->
          <commonMenuSetting class="marginTop" />
          <!-- 友情链接 -->
          <!-- <friendlyLink class="marginTop" /> -->
        </el-col>
        <el-col
          :xs="24"
          :sm="24"
          :md="7"
          :lg="7"
          :xl="7"
        >
          <!-- 公告信息 -->
          <noticeInfo class="marginTop" />
          <!-- 满意度问卷调查 -->
          <question class="marginTop"/>
          <!-- 服务中心 -->
          <serviceCenter class="marginTop" />
        </el-col>
      </el-row>
      <!-- 版本信息 -->
      <el-row>
        <el-col :span="24">
          <Version />
        </el-col>
      </el-row>
    </el-main>
    <!-- 契约锁提醒 -->
    <srm-dialog
      :title="$t('cusEntry.vendorMod.lockTipTitle')"
      :visible.sync="showLock"
      :close-on-click-modal="false"
      size="middle"
    >
      <p>{{ $t('cusEntry.vendorMod.lockTipContent') }}</p>
      <div slot="footer">
        <el-button
          type="primary"
          @click="showLock = false"
        >
          {{ $t('common.cancel') }}
        </el-button>
        <el-button
          type="primary"
          @click="sendVerificationCode"
        >
          {{ $t('cusEntry.common.goToRealName') }}
        </el-button>
      </div>
    </srm-dialog>
    <!-- 证件到期提醒 -->
    <srm-dialog
      :title="$t('cusEntry.vendorMod.expiredDocumentsTitle')"
      :visible.sync="showExpireDocument"
      :close-on-click-modal="false"
      size="middle"
    >
      <p>{{ $t('cusEntry.vendorMod.yourCompany') }}{{ showExpireDocumentContent }}{{ $t('cusEntry.vendorMod.expiredDocumentsContent') }}</p>
      <div slot="footer">
        <el-button type="primary" @click="showExpireDocument = false">
          {{ $t('common.cancel') }}
        </el-button>
        <el-button type="primary" @click="goToInfoChange">
          {{ $t('cusEntry.common.goToChange') }}
        </el-button>
      </div>
    </srm-dialog>
    <!-- 廉洁考试提示框 -->
    <div v-if="tipShowFlag" class="tip-box">
      <div class="tip-title">
        <span>
          {{ $t('cusEntry.vendorMod.integrityTitle') }}
        </span>
      </div>
      <div class="tip-con">
        {{ $t('cusEntry.vendorMod.integrityIndexTipText') }}
      </div>
      <div class="tip-footer">
        <el-button type="primary" @click="handleCancle">
          {{ $t('cusEntry.vendorMod.buttonCancel') }}
        </el-button>
        <el-button type="primary" @click="handleGoExam">
          {{ $t('cusEntry.vendorMod.integrityTitle') }}
        </el-button>
      </div>
    </div>
  </el-container>
</template>

<script>
import { mapState } from 'vuex'
import commonMenuSetting from '@/views/dashboard/components/commonMenuSetting'
import businessProcessTask from '@/views/dashboard/components/businessProcessTask'
import noticeInfo from '@/views/dashboard/components/noticeInfo'
import serviceCenter from '@/views/dashboard/components/serviceCenter'
import question from '@/views/dashboard/components/question'
import friendlyLink from '@/views/dashboard/components/friendlyLink'
import Version from '@/views/dashboard/components/version'
import '@/views/dashboard/components/style.scss'
import { getDictItemList } from '@/api/common'
import { isCheckExam } from '../api'
import { adaptDictData } from '@/utils'
export default {
  name: 'DashboardVendor',
  components: { commonMenuSetting, businessProcessTask, noticeInfo, serviceCenter, friendlyLink, Version, question },
  data () {
    return {
      showLock: false,
      showExpireDocument: false,
      showExpireDocumentContent: '',
      tipShowFlag: false
    }
  },
  created () {
    const userInfo = this.$store.getters.userInfo
    const companyId = userInfo.companyId
    if (companyId) {
      this.queryLockStatus(companyId)
      this.getExpiredDocuments()
    }
    this.handleCheckExam()
    this.handleCloseAuto()
  },
  methods: {
    /* 发送短信验证 */
    sendVerificationCode () {
      this.showLock = false
      const id = this.$store.getters.userInfo.companyId
      if (id) {
        this.$http({
          url: 'api-pj/external/ContractLock/sendCompanyAuthNotify',
          method: 'POST',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          },
          data: {
            companyId: id
          },
          loading: true
        }).then(res => {
          this.$alert(this.$t('cusEntry.tipMessage.sendCodeMessage'), this.$t('cusEntry.common.tip'), {
            confirmButtonText: this.$t('common.close'),
            callback: action => {
              console.log(333)
            }
          })
        })
      }
    },
    /* 跳转到信息变更 */
    goToInfoChange () {
      this.showExpireDocument = false
      this.showLock = false
      this.$router.push({
        name: 'supplierInfoChange'
      })
    },
    /* 查询契约锁状态 */
    queryLockStatus (id) {
      this.$http({
        url: `/api-pj/external/ContractLock/getComnpanyAuthStatus?companyId=${id}`,
        method: 'POST'
      }).then(res => {
        if (res.data) {
          if (['UNSUBMIT', 'REJECTED'].includes(res.data)) {
            this.showLock = true
          }
        }
      })
    },
    /* 实名验证跳转 */
    goToRealName () {
      window.open(import.meta.env.VUE_APP_REALNAME_URL)
    },
    /* 查询证件到期提醒 */
    getExpiredDocuments () {
      this.$http({
        url: '/api-sup/pj/info/vendorInformation/listManagementAttachPageByDTO',
        method: 'POST',
        data: {
          pageNum: 1,
          pageSize: 15
        }
      }).then(res => {
        if (res.data && res.data?.list?.length) {
          const filterList = [...new Set(res.data.list)]
          const codeList = [
            { dictCode: 'ExpiredCertificateType' },
            { dictCode: 'CERTIFICATE_TYPE' }
          ]
          getDictItemList(codeList).then(res => {
            const [
              ExpiredCertificateType,
              CERTIFICATE_TYPE
            ] = res.data
            const labelList = filterList.map(item => {
              if (item.formType === 'MANAGEMENT_ATTACH') {
                return adaptDictData(CERTIFICATE_TYPE.CERTIFICATE_TYPE).find(itm => itm.value === item.authNum)?.label
              } else {
                return adaptDictData(ExpiredCertificateType.ExpiredCertificateType).find(itm => itm.value === item.formType)?.label
              }
            })
            this.showExpireDocumentContent = labelList.join('、')
            this.showExpireDocument = true
          })
        }
      })
    },
    // 校验是否需要廉洁考试
    handleCheckExam () {
      isCheckExam().then(res => {
        if (res.code + '' === '0') {
          this.tipShowFlag = res.data?.isExam === 'N'
        }
      })
    },
    // 廉洁考试--点击取消按钮
    handleCancle () {
      this.tipShowFlag = false
    },
    // 一分钟廉洁考试提示框自动关闭
    handleCloseAuto () {
      setTimeout(() => {
        this.tipShowFlag = false
      }, 60000)
    },
    // 去廉洁考试
    handleGoExam () {
      this.$http({
        url: 'api-pj/sun-honesty/externalSso',
        method: 'POST',
        loading: true
      }).then(res => {
        if (res.code + '' === '0') {
          window.open(res.data)
          this.tipShowFlag = false
        }
      })
    }
  }
}
</script>

<style lang="scss">
.marginTop{
  margin-top: 16px;
}
.the_vendor_dashboard_wrapper {
  min-height: 550px;
  .the_body > .el-col {
    height: 100%;
    padding: 0px;
    margin-bottom: 12px;
  }
}
.tip-box {
  position: fixed;
  z-index: 99;
  right: 0;
  bottom: 0;
  width: 400px;
  background: #fff;
  border-radius: 2px;
  box-shadow: 2px 2px 6px rgba(0,0,0,0.2);
  .tip-title {
    height: 48px;
    line-height: 48px;
    font-size: 16px;
    color: #161C24;
    border-bottom: 1px solid #DCDDDE;
    span {
      padding-left:20px;
    }
  }
  .tip-footer {
    height: 48px;
    display: flex;
    align-items: center;
    border-top: 1px solid #DCDDDE;
    padding: 0 16px;
    justify-content: flex-end;
  }
  .tip-con {
    height: 100px;
    padding: 20px 24px;
    font-size: 14px;
    color: #161C24;
  }
}
</style>
