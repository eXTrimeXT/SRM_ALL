<template>
  <el-container
    class="flex-container-notab the_buyer_dashboard_wrapper dashboard"
    direction="vertical"
  >
    <el-main>
      <!-- 常用功能设置 -->
      <el-row
        class="the_body"
        :gutter="16"
      >
        <el-col
          :xs="24"
          :sm="24"
          :md="18"
          :lg="18"
          :xl="18"
        >
          <commonMenuSetting />
          <!-- 采购商流程审批代办 -->
          <buyerProcessTask class="marginTop" />
        </el-col>
        <el-col
          :xs="24"
          :sm="24"
          :md="6"
          :lg="6"
          :xl="6"
        >
          <!-- 公告信息 -->
          <noticeInfo :class="{'mt-16':['device-xs','device-sm'].includes(device)}" />
          <!-- 懂招标 -->
          <understandBidding class="marginTop" />
          <!-- 云生态 -->
          <!-- <friendlyLink class="marginTop" /> -->
          <!-- 满意度问卷调查 -->
          <!-- <question class="marginTop"/> -->
          <!-- 服务中心 -->
          <serviceCenter class="marginTop" />
        </el-col>
      </el-row>
      <!-- <el-row>
        <el-col :span="24"> -->
      <!-- 图表 -->
      <!-- <chartImage class="marginTop" />
        </el-col>
      </el-row> -->
      <!-- 版本信息 -->
      <!-- <el-row>
        <el-col :span="24">
          <Version />
        </el-col>
      </el-row> -->
    </el-main>
  </el-container>
</template>

<script>
import { mapState, mapGetters } from 'vuex'
import commonMenuSetting from '@/views/dashboard/components/commonMenuSetting'
import buyerProcessTask from '@/views/dashboard/components/buyerProcessTask'
import noticeInfo from '@/views/dashboard/components/noticeInfo'
import understandBidding from '@/views/dashboard/components/understandBidding'
import serviceCenter from '@/views/dashboard/components/serviceCenter'
import question from '@/views/dashboard/components/question'
import friendlyLink from '@/views/dashboard/components/friendlyLink'
import chartImage from '@/views/dashboard/components/chartImage'
import Version from '@/views/dashboard/components/version'
import '@/views/dashboard/components/style.scss'
import { getConfigGuide } from '@/api/common'

export default {
  name: 'DashboardBuyer',
  components: {
    commonMenuSetting,
    buyerProcessTask,
    understandBidding,
    serviceCenter,
    noticeInfo,
    friendlyLink,
    chartImage,
    Version,
    question
  },
  data () {
    return {
    }
  },
  computed: {
    ...mapState({
      menus: state => state.user.userInfo.menus || [],
      menuIds: state => state.user.userInfo.menus.map(i => i.permissionId)
    }),
    ...mapGetters(['device'])
  },
  created () {
    // 主账号弹框提示未完成的配置任务
    if (
      this.$store.getters.userInfo.userType === 'BUYER' &&
      this.$store.getters.userInfo.mainType === 'Y'
    ) {
      this.fatchConfig() // 查询基础配置信息
    }
  },
  methods: {
    // 查询基础配置信息
    fatchConfig () {
      getConfigGuide().then(res => {
        if (res) {
          let resData = res.data
          let confData = {}
          confData.orgConfig = resData.orgConfig // 组织设置
          confData.levelConfig = resData.levelConfig // 层级配置
          confData.currencyConfig = resData.currencyConfig // 币种配置
          confData.unitConfig = resData.unitConfig // 单位配置
          confData.taxConfig = resData.taxConfig // 税率配置
          confData.flowConfig = resData.flowConfig // 流程配置
          confData.materialConfig = resData.materialConfig // 物料维护
          confData.childConfig = resData.childConfig // 子账号导入
          confData.purchaseConfig = resData.purchaseConfig // 采购分类
          confData.vendorConfig = resData.vendorConfig // 供应商导入
          confData.stateControlConfig = resData.stateControlConfig // 业务状态控制
          confData.vendorFieldConfig = resData.vendorFieldConfig // 供应商属性配置
          confData.uploadConfig = resData.uploadConfig // 附件管理
          confData.dvConfig = resData.dvConfig // 品类分工
          confData.flowTemplateConfig = resData.flowTemplateConfig // 流程模板

          let configTimeObj = {
            configGuideId: resData.configGuideId,
            reminderSum: resData.reminderSum
          }
          this.guideToConf(confData, configTimeObj) // 判断跳转
        }
      })
    },
    // 弹框次数
    configGuideTime (params) {
      this.$http({
        url: '/api-base/configGuide/modify',
        method: 'POST',
        data: { ...params }
      })
        .then(data => {})
        .catch(err => {
          console.log(err)
        })
    },

    // 判断配置项并跳转
    guideToConf (data, data2) {
      let conf = data
      let fonf1 = data2
      if (conf) {
        let totle = Object.keys(conf).length
        let finish = 0
        Object.keys(conf).forEach(key => {
          if (conf[key] === 'Y') {
            finish += 1
          }
        })
        if (
          conf.orgConfig === 'N' ||
          conf.levelConfig === 'N' ||
          conf.currencyConfig === 'N' ||
          conf.unitConfig === 'N' ||
          conf.taxConfig === 'N' ||
          conf.flowConfig === 'N' ||
          conf.materialConfig === 'N' ||
          conf.childConfig === 'N' ||
          conf.purchaseConfig === 'N' ||
          conf.vendorConfig === 'N'
        ) {
          if (finish < totle && fonf1.reminderSum < 4) {
            // 弹框只弹三次
            // 您还未完成基础配置，是否马上开始配置？已完成
            this.$confirm(
              this.$t('dashboard.msgNoFinish') +
                finish +
                this.$t('dashboard.msgTotal') +
                totle +
                this.$t('dashboard.msgTask'),
              this.$t('dashboard.msgWelcome'),
              {
                confirmButtonText: this.$t('dashboard.startSetting'), // 开始配置
                cancelButtonText: this.$t('dashboard.laterSetting'), // 稍后配置
                type: 'warning'
              }
            )
              .then(() => {
                this.goToFun(conf)
              })
              .catch(() => {
                console.log('cancel')
              })
            // 更新 reminderSum值[[
            let params = {
              configGuideId: fonf1.configGuideId,
              reminderSum: fonf1.reminderSum + 1
            }
            this.configGuideTime(params)
            // ]]
          }
        }
      }
    },
    // 跳转到设置的导引页面
    goToFun (data) {
      this.$router.push({ name: 'settingGuide' })
    }
  }
}
</script>

<style lang="scss">

.the_buyer_dashboard_wrapper {
  min-height: 550px;
  .marginTop{
    margin-top: 16px;
  }
  .the_header {
    padding: 0 0 16px;
  }
  .the_body > .el-col {
    height: 100%;
    padding: 0px;
  }
}
.mt-16 {
  margin-top: 16px;
}
</style>
