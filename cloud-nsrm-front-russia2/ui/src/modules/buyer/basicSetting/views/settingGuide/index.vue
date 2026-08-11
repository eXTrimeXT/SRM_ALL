<template>
  <el-container
    class="flex-container-notab normal-container the_settingGuide_wrapper"
    direction="vertical"
  >
    <el-main style="border: 0;">
      <div class="the_header">
        <div class="right">
          <!-- 配置导引 -->
          <span class="the_first">{{ $t('dataConfMod.settingGuide.title')[0] }}</span><br>
          <!-- 引导用户设置系统 -->
          <span class="the_second">{{ $t('dataConfMod.settingGuide.title')[1] }}</span>
        </div>
      </div>

      <!-- 引导配置项 -->
      <div class="the_steps_wrapper">
        <div class="clearfix guide-content">
          <div class="clearfix common-sec forwardDir finishStatus">
            <div class="icon-div">
              <!-- 平台注册 -->
              <span class="icon-div-text">{{ $t('dataConfMod.settingGuide.step1')[0] }}</span>
              <span class="statusText">{{ $t('dataConfMod.settingGuide.stepStatus')[0] }}</span>
              <div class="status-div first-icon">
                <em class="line" />
                <div class="statuIcon el-icon-success" />
              </div>
            </div>
            <div class="content-div">
              <div class="pross-steps">
                <!-- 已完成 -->
                <a class="btn btn-link text-info finishBtn" rel="noopener">
                  <em class="icon-flag el-icon-s-flag"  />
                  <!-- 注册 -->
                  <span>{{ $t('dataConfMod.settingGuide.step1')[1] }}</span>
                </a>
              </div>
              <div class="steps-des">
                <!-- 1、注册成为平台的用户,认证企业平台用户 -->
                <p>{{ $t('dataConfMod.settingGuide.step1')[2] }}</p>
              </div>
            </div>
          </div>
          <div :class="['clearfix common-sec reverseDir', computedStatus.orgStatus]">
            <div class="icon-div">
              <!-- 组织设置 -->
              <span class="icon-div-text">{{ $t('dataConfMod.settingGuide.step2')[0] }}</span>
              <span class="statusText">
                {{ computedStatus.orgStatusText }}
              </span>
              <div class="status-div">
                <em class="line" />
                <!-- 已完成 -->
                <div v-if="computedStatus.orgStatus === 'finishStatus'" class="statuIcon el-icon-success" />
                <!-- 待进行 -->
                <div v-else class="statuIcon todo">2</div>
              </div>
            </div>
            <div class="content-div">
              <div class="pross-steps">
                <a
                  :class="[
                    'btn btn-link text-info',
                    { finishBtn: confData.orgConfig === 'Y' }
                  ]"
                  rel="noopener"
                  @click="toRoute('organizationSetting')"
                >
                  <em class="icon-flag el-icon-s-flag"  />
                  <!-- 组织设置 -->
                  <span>{{ $t('dataConfMod.settingGuide.step2')[0] }}</span>
                </a>
              </div>
              <div class="steps-des">
                <!-- 1、创建组织，根据公司和采购管理现状创建组织，可包含集团、事业部、业务实体、库存组织等； -->
                <p>{{ $t('dataConfMod.settingGuide.step2')[1] }}</p>
                <!-- 2、设置组织层级，根据公司和采购管理层级，设置组织间的层级关系； -->
                <p>{{ $t('dataConfMod.settingGuide.step2')[2] }}</p>
                <!-- 3、编辑组织，对于已变更或者即将变更的组织名称和组织层级关系，可以进行修改。 -->
                <p>{{ $t('dataConfMod.settingGuide.step2')[3] }}</p>
              </div>
            </div>
          </div>
          <div :class="['clearfix common-sec forwardDir', computedStatus.purchaseStatus]">
            <div class="icon-div">
              <!-- 采购设置 -->
              <span class="icon-div-text">{{ $t('dataConfMod.settingGuide.step3')[0] }}</span>
              <span class="statusText">
                {{ computedStatus.purchaseStatusText }}
              </span>
              <div class="status-div">
                <em class="line" />
                <div v-if="computedStatus.purchaseStatus === 'finishStatus'" class="statuIcon el-icon-success" />
                <div v-else-if="computedStatus.purchaseStatus === 'todo'" class="statuIcon todo">3</div>
                <div v-else class="statuIcon currentStatus">3</div>
              </div>
            </div>
            <div class="content-div">
              <div class="pross-steps">
                <!-- 管理层级设置 -->
                <!-- <a
                  :class="[
                    'btn btn-link text-info',
                    { finishBtn: confData.levelConfig === 'Y' }
                  ]"
                  rel="noopener"
                  @click="toRoute('manageLevelSetting')"
                >
                  <em class="icon-flag el-icon-s-flag"  />
                  <span>{{ $t('dataConfMod.settingGuide.step3')[1] }}</span>
                </a> -->
                <a
                  :class="[
                    'btn btn-link text-info',
                    { finishBtn: confData.currencyConfig === 'Y' }
                  ]"
                  rel="noopener"
                  @click="toRoute('purchaseBaseSetting', { tabName: 'currency' })"
                >
                  <em class="icon-flag el-icon-s-flag" :class="{'not-finished': confData.currencyConfig === 'N'}"  />
                  <!-- 币种 -->
                  <span>{{ $t('dataConfMod.settingGuide.step3')[2] }}</span>
                </a>
                <a
                  :class="[
                    'btn btn-link text-info',
                    { finishBtn: confData.unitConfig === 'Y' }
                  ]"
                  rel="noopener"
                  @click="toRoute('purchaseBaseSetting', { tabName: 'unit' })"
                >
                  <em class="icon-flag el-icon-s-flag" :class="{'not-finished': confData.unitConfig === 'N'}" />
                  <!-- 单位 -->
                  <span>{{ $t('dataConfMod.settingGuide.step3')[3] }}</span>
                </a>
                <a
                  :class="[
                    'btn btn-link text-info',
                    { finishBtn: confData.taxConfig === 'Y' }
                  ]"
                  rel="noopener"
                  @click="toRoute('purchaseBaseSetting', { tabName: 'rate' })"
                >
                  <em class="icon-flag el-icon-s-flag" :class="{'not-finished': confData.taxConfig === 'N'}"  />
                  <!-- 税率 -->
                  <span>{{ $t('dataConfMod.settingGuide.step3')[4] }}</span>
                </a>
              </div>

              <div class="steps-des">
                <!-- 1、设置供应商采购类别管理层级和管理供应商层级 -->
                <!-- <p>{{ $t('dataConfMod.settingGuide.step3')[5] }}</p> -->
                <!-- 2、启用币种，根据企业实际业务所用的币种进行启用 -->
                <p>1、{{ $t('dataConfMod.settingGuide.step3')[6] }}</p>
                <!-- 3、启用单位，根据企业实际业务所用的单位进行启用 -->
                <p>2、{{ $t('dataConfMod.settingGuide.step3')[7] }}</p>
                <!-- 4、启用税率，根据企业实际业务所用的税率进行启用 -->
                <p>3、{{ $t('dataConfMod.settingGuide.step3')[8] }}</p>
              </div>
            </div>
          </div>

          <div :class="['clearfix common-sec reverseDir', computedStatus.vendorAccessStatus]">
            <div class="icon-div">
              <!-- 供应商管理设置 -->
              <span class="icon-div-text">{{ $t('dataConfMod.settingGuide.step4')[0] }}</span>
              <span class="statusText">
                {{ computedStatus.vendorAccessStatusText }}
              </span>
              <div class="status-div">
                <em class="line" />
                <div v-if="computedStatus.vendorAccessStatus === 'finishStatus'" class="statuIcon el-icon-success" />
                <div v-else-if="computedStatus.vendorAccessStatus === 'todo'" class="statuIcon todo">4</div>
                <div v-else class="statuIcon currentStatus">4</div>
              </div>
            </div>
            <div class="content-div">
              <div class="pross-steps">
                <a
                  :class="[
                    'btn btn-link text-info',
                    { finishBtn: confData.flowConfig === 'Y' }
                  ]"
                  rel="noopener"
                  @click="toRoute('accessFlowSetting')"
                >
                  <em class="icon-flag el-icon-s-flag"  :class="{'not-finished': confData.flowConfig === 'N'}" />
                  <!-- 准入流程配置 -->
                  <span>{{ $t('dataConfMod.settingGuide.step4')[1] }}</span>
                </a>
                <a
                  :class="[
                    'btn btn-link text-info',
                    { finishBtn: confData.vendorFieldConfig === 'Y' }
                  ]"
                  rel="noopener"
                  @click="toRoute('vendorAttributeSetting')"
                >
                  <em class="icon-flag el-icon-s-flag"  :class="{'not-finished': confData.vendorFieldConfig === 'N'}" />
                  <!-- 供应商属性配置 -->
                  <span>{{ $t('dataConfMod.settingGuide.step4')[3] }}</span>
                </a>
              </div>

              <div class="steps-des">
                <!-- 3、配置供应商准入流程 -->
                <p>{{ $t('dataConfMod.settingGuide.step4')[6] }}</p>
                <!-- 1、配置供应商属性，进行供应商属性精细化管理 -->
                <p>{{ $t('dataConfMod.settingGuide.step4')[4] }}</p>
              </div>
            </div>
          </div>

          <div :class="['clearfix common-sec forwardDir', computedStatus.initDataStatus]">
            <div class="icon-div">
              <!-- 数据初始化 -->
              <span class="icon-div-text">{{ $t('dataConfMod.settingGuide.step5')[0] }}</span>
              <span class="statusText">
                {{ computedStatus.initDataStatusText }}
              </span>
              <div class="status-div">
                <em class="line" />
                <div v-if="computedStatus.initDataStatus === 'finishStatus'" class="statuIcon el-icon-success" />
                <div v-else-if="computedStatus.initDataStatus === 'todo'" class="statuIcon todo">5</div>
                <div v-else class="statuIcon currentStatus">5</div>
              </div>
            </div>

            <div class="content-div">
              <div class="pross-steps">
                <a
                  :class="[
                    'btn btn-link text-info',
                    { finishBtn: confData.purchaseConfig === 'Y' }
                  ]"
                  rel="noopener"
                  @click="toRoute('purchaseCategoryMaintenance')"
                >
                  <em class="icon-flag el-icon-s-flag" :class="{'not-finished': confData.purchaseConfig === 'N'}" />
                  <!-- 导入采购分类 -->
                  <span>{{ $t('dataConfMod.settingGuide.step5')[1] }}</span>
                </a>
                <a
                  :class="[
                    'btn btn-link text-info',
                    { finishBtn: confData.materialConfig === 'Y' }
                  ]"
                  rel="noopener"
                  @click="toRoute('materialMaintenance')"
                >
                  <em class="icon-flag el-icon-s-flag" :class="{'not-finished': confData.materialConfig === 'N'}"  />
                  <!-- 导入物料 -->
                  <span>{{ $t('dataConfMod.settingGuide.step5')[2] }}</span>
                </a>
                <a
                  :class="[
                    'btn btn-link text-info',
                    { finishBtn: confData.vendorConfig === 'Y' }
                  ]"
                  rel="noopener"
                  @click="toRoute('vendorGreenChannel')"
                >
                  <em class="icon-flag el-icon-s-flag" :class="{'not-finished': confData.vendorConfig === 'N'}"  />
                  <!-- 导入供应商 -->
                  <span>{{ $t('dataConfMod.settingGuide.step5')[3] }}</span>
                </a>
                <a
                  :class="[
                    'btn btn-link text-info',
                    { finishBtn: confData.childConfig === 'Y' }
                  ]"
                  rel="noopener"
                  @click="toRoute('accountManagement')"
                >
                  <em class="icon-flag el-icon-s-flag" :class="{'not-finished': confData.childConfig === 'N'}" />
                  <!-- 子账号维护 -->
                  <span>{{ $t('dataConfMod.settingGuide.step5')[4] }}</span>
                </a>
              </div>
              <div class="steps-des">
                <!-- 1、采购分类数据初始化，导入企业管理的采购分类 -->
                <p>{{ $t('dataConfMod.settingGuide.step5')[5] }}</p>
                <!-- 2、物料数据初始化，导入企业采购管理的物料 -->
                <p>{{ $t('dataConfMod.settingGuide.step5')[6] }}</p>
                <!-- 3、供应商数据初始化，导入与企业进行交易的供应商，包含组织和品类分类关系 -->
                <p>{{ $t('dataConfMod.settingGuide.step5')[7] }}</p>
                <!-- 4、创建子账号，为子账号分配角色权限和组织权限 -->
                <p>{{ $t('dataConfMod.settingGuide.step5')[8] }}</p>
              </div>
            </div>
          </div>

          <div :class="['clearfix common-sec reverseDir', computedStatus.otherStatus]">
            <div class="icon-div">
              <!-- 其他 -->
              <span class="icon-div-text">{{ $t('dataConfMod.settingGuide.step6')[0] }}</span>
              <span class="statusText">
                {{ computedStatus.otherStatusText }}
              </span>
              <div class="status-div last-icon">
                <em class="line" />
                <div v-if="computedStatus.otherStatus === 'finishStatus'" class="statuIcon el-icon-success" />
                <div v-else-if="computedStatus.otherStatus === 'todo'" class="statuIcon todo">6</div>
                <div v-else class="statuIcon currentStatus">6</div>
              </div>
            </div>
            <div class="content-div">
              <div class="pross-steps">
                <a
                  :class="[
                    'btn btn-link text-info',
                    { finishBtn: confData.uploadConfig === 'Y' }
                  ]"
                  rel="noopener"
                  @click="toRoute('sceneFileManagement')"
                >
                  <em class="icon-flag el-icon-s-flag" :class="{'not-finished': confData.uploadConfig === 'N'}"  />
                  <!-- 场景附件管理 -->
                  <span>{{ $t('dataConfMod.settingGuide.step6')[1] }}</span>
                </a>
<!--                <a-->
<!--                  :class="[-->
<!--                    'btn btn-link text-info',-->
<!--                    { finishBtn: confData.flowTemplateConfig === 'Y' }-->
<!--                  ]"-->
<!--                  rel="noopener"-->
<!--                  @click="toRoute('flowTemplate')"-->
<!--                >-->
<!--                  <em class="icon-flag el-icon-s-flag"  />-->
<!--                  &lt;!&ndash; 流程模板配置 &ndash;&gt;-->
<!--                  <span>{{ $t('dataConfMod.settingGuide.step6')[2] }}</span>-->
<!--                </a>-->
                <a
                  :class="[
                    'btn btn-link text-info',
                    { finishBtn: confData.dvConfig === 'Y' }
                  ]"
                  rel="noopener"
                  @click="toRoute('categoryAssignRule')"
                >
                  <em class="icon-flag el-icon-s-flag" :class="{'not-finished': confData.dvConfig === 'N'}"  />
                  <!-- 品类分工规则 -->
                  <span>{{ $t('dataConfMod.settingGuide.step6')[3] }}</span>
                </a>
              </div>

              <div class="steps-des">
                <!-- 1、根据不同业务流程，配置不同的附件要求 -->
                <p>{{ $t('dataConfMod.settingGuide.step6')[4] }}</p>
                <!-- 2、根据不同业务流程，配置不同的工作流 -->
<!--                <p>{{ $t('dataConfMod.settingGuide.step6')[5] }}</p>-->
                <!-- 3、根据不同的业务范围和细化分工，配置品类分工 -->
                <p>{{ $t('dataConfMod.settingGuide.step6')[6] }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-main>
  </el-container>
</template>
<script>
import flag from '@/assets/guide/flag.svg'
import orgSetting from '@/assets/guide/org-setting.svg'
import purchaseSetting from '@/assets/guide/purchase-setting.svg'
import initData from '@/assets/guide/init-data.svg'
import supplierManSetting from '@/assets/guide/supplier-man-setting.svg'
import platformRegistered from '@/assets/guide/platform-registered.svg'
import other from '@/assets/guide/other.svg'
import { settingGuide } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'SettingGuide',
  data () {
    return {
      flag: flag,
      confData: {
        childConfig: '', // 子账号导入
        currencyConfig: '', // 币种配置
        unitConfig: '', // 单位配置
        taxConfig: '', // 税率配置
        flowConfig: '', // 流程配置
        levelConfig: '', // 层级配置
        materialConfig: '', // 物料维护
        orgConfig: '', // 组织设置
        purchaseConfig: '', // 采购设置
        vendorConfig: '', // 供应商导入
        // stateControlConfig: '', // 业务状态控制
        vendorFieldConfig: '', // 供应商属性配置
        uploadConfig: '', // 附件管理
        dvConfig: '', // 品类分工
        flowTemplateConfig: '' // 流程模板
      }, // 配置数据
      icon: {
        // 图标
        platformRegistered: platformRegistered,
        orgSetting: orgSetting,
        purchaseSetting: purchaseSetting,
        supplierManSetting: supplierManSetting,
        initData: initData,
        other: other
      }
    }
  },
  computed: {
    computedStatus () {
      let statusObj = {}
      // 组织设置
      if (this.confData.orgConfig === 'Y') {
        statusObj.orgStatus = 'finishStatus'
        statusObj.orgStatusText = this.$t('dataConfMod.settingGuide.stepStatus')[0]// "已完成"
      } else {
        statusObj.orgStatus = 'todo'
        statusObj.orgStatusText = this.$t('dataConfMod.settingGuide.stepStatus')[1]// "待进行"
      }
      // 采购设置 unitConfig taxConfig this.confData.levelConfig === 'Y' &&
      if (this.confData.currencyConfig === 'Y' &&
          this.confData.unitConfig === 'Y' &&
          this.confData.taxConfig === 'Y'
      ) {
        statusObj.purchaseStatus = 'finishStatus'
        statusObj.purchaseStatusText = this.$t('dataConfMod.settingGuide.stepStatus')[0]// "已完成"
      } else if ( // this.confData.levelConfig === 'N' &&
        this.confData.currencyConfig === 'N' &&
        this.confData.unitConfig === 'N' &&
        this.confData.taxConfig === 'N'
      ) {
        statusObj.purchaseStatus = 'todo'
        statusObj.purchaseStatusText = this.$t('dataConfMod.settingGuide.stepStatus')[1]// "待进行"
      } else if ( // this.confData.levelConfig === 'N' ||
        this.confData.currencyConfig === 'N' ||
        this.confData.unitConfig === 'N' ||
        this.confData.taxConfig === 'N'
      ) {
        statusObj.purchaseStatus = 'currentStatus'
        statusObj.purchaseStatusText = this.$t('dataConfMod.settingGuide.stepStatus')[2]// "进行中"
      } else {
        statusObj.purchaseStatus = 'todo'
        statusObj.purchaseStatusText = this.$t('dataConfMod.settingGuide.stepStatus')[1]// "待进行"
      }
      // 供应商管理设置
      if (this.confData.flowConfig === 'Y') {
        // 组织设置
        statusObj.vendorAccessStatus = 'finishStatus'
        statusObj.vendorAccessStatusText = this.$t('dataConfMod.settingGuide.stepStatus')[0]// "已完成"
      } else {
        statusObj.vendorAccessStatus = 'todo'
        statusObj.vendorAccessStatusText = this.$t('dataConfMod.settingGuide.stepStatus')[1]// "待进行"
      }

      // 数据初始化
      if (
        this.confData.materialConfig === 'Y' &&
        this.confData.childConfig === 'Y' &&
        this.confData.purchaseConfig === 'Y' &&
        this.confData.vendorConfig === 'Y'
      ) {
        statusObj.initDataStatus = 'finishStatus'
        statusObj.initDataStatusText = this.$t('dataConfMod.settingGuide.stepStatus')[0]// "已完成"
      } else if (
        this.confData.materialConfig === 'N' &&
        this.confData.childConfig === 'N' &&
        this.confData.purchaseConfig === 'N' &&
        this.confData.vendorConfig === 'N'
      ) {
        statusObj.initDataStatus = 'todo'
        statusObj.initDataStatusText = this.$t('dataConfMod.settingGuide.stepStatus')[1]// "待进行"
      } else if (
        this.confData.materialConfig === 'N' ||
        this.confData.childConfig === 'N' ||
        this.confData.purchaseConfig === 'N' ||
        this.confData.vendorConfig === 'N'
      ) {
        statusObj.initDataStatus = 'currentStatus'
        statusObj.initDataStatusText = this.$t('dataConfMod.settingGuide.stepStatus')[2]// "进行中"
      } else {
        statusObj.initDataStatus = 'todo'
        statusObj.initDataStatusText = this.$t('dataConfMod.settingGuide.stepStatus')[1]// "待进行"
      }
      // 其他
      if (
        this.confData.uploadConfig === 'Y' &&
        this.confData.dvConfig === 'Y'
      ) {
        statusObj.otherStatus = 'finishStatus'
        statusObj.otherStatusText = this.$t('dataConfMod.settingGuide.stepStatus')[0]// "已完成"
      } else if (
        this.confData.uploadConfig === 'N' &&
        this.confData.dvConfig === 'N'
      ) {
        statusObj.otherStatus = 'todo'
        statusObj.otherStatusText = this.$t('dataConfMod.settingGuide.stepStatus')[1]// "待进行"
      } else if (
        this.confData.uploadConfig === 'N' ||
        this.confData.dvConfig === 'N'
      ) {
        statusObj.otherStatus = 'currentStatus'
        statusObj.otherStatusText = this.$t('dataConfMod.settingGuide.stepStatus')[2]// "进行中"
      } else {
        statusObj.otherStatus = 'todo'
        statusObj.otherStatusText = this.$t('dataConfMod.settingGuide.stepStatus')[1]// "待进行"
      }
      return statusObj
    }
  },
  created () {
    this.fatchConfig() // 查询配置
  },
  activated () {
    this.fatchConfig() // 查询配置
  },
  methods: {
    toRoute (name, query) {
      if (!name) return
      if (query) {
        this.$router.push({ name: name, params: query })
      } else {
        this.$router.push({ name: name })
      }
    },
    fatchConfig () {
      settingGuide.getConfigGuide().then(res => {
        if (res) {
          let confData = res.data
          this.confData.orgConfig = confData.orgConfig // 组织设置
          this.confData.levelConfig = confData.levelConfig // 层级配置
          this.confData.currencyConfig = confData.currencyConfig // 币种配置
          this.confData.unitConfig = confData.unitConfig // 单位
          this.confData.taxConfig = confData.taxConfig // 税率
          this.confData.flowConfig = confData.flowConfig // 流程配置
          this.confData.materialConfig = confData.materialConfig // 物料维护
          this.confData.childConfig = confData.childConfig // 子账号导入
          this.confData.purchaseConfig = confData.purchaseConfig // 采购分类
          this.confData.vendorConfig = confData.vendorConfig // 供应商导入
          this.confData.vendorFieldConfig = confData.vendorFieldConfig // 供应商属性配置
          this.confData.uploadConfig = confData.uploadConfig // 附件管理
          this.confData.dvConfig = confData.dvConfig // 品类分工
          this.confData.flowTemplateConfig = confData.flowTemplateConfig // 流程模板
          this.guideToConf(this.confData) // 判断跳转
        }
      })
    },
    // 判断配置项并跳转
    guideToConf (data) {
      let conf = data
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
          // 0 "您还未完成基础配置，是否马上开始配置？已完成"
          // 1 "个任务，共计"
          // 2 "个任务"
          // 3 "欢迎使用SRM云！"
          this.$confirm(
            this.$t('dataConfMod.settingGuide.guideToConf')[0] +
              finish + this.$t('dataConfMod.settingGuide.guideToConf')[1] +
              totle + this.$t('dataConfMod.settingGuide.guideToConf')[2],
            this.$t('dataConfMod.settingGuide.guideToConf')[3],
            {
              confirmButtonText: this.$t('dataConfMod.settingGuide.confirm'), // "开始配置"
              cancelButtonText: this.$t('dataConfMod.settingGuide.cancel'), // "稍后配置"
              type: 'warning'
            }
          )
            .then(() => {
              this.goToFun(conf)
            })
        }
      }
    },
    goToFun (data) {
      if (data.orgConfig === 'N') {
        this.$router.push({ name: 'organizationSetting' })
        return false
      }
      if (data.levelConfig === 'N') {
        this.$router.push({ name: 'manageLevelSetting' })
        return false
      }
      if (data.currencyConfig === 'N') {
        this.$router.push({
          name: 'purchaseBaseSetting',
          params: { tabName: 'currency' }
        })
        return false
      }
      if (data.unitConfig === 'N') {
        this.$router.push({
          name: 'purchaseBaseSetting',
          params: { tabName: 'unit' }
        })
        return false
      }
      if (data.taxConfig === 'N') {
        this.$router.push({
          name: 'purchaseBaseSetting',
          params: { tabName: 'rate' }
        })
        return false
      }
      if (data.flowConfig === 'N') {
        this.$router.push({ name: 'accessFlowSetting' })
        return false
      }
      if (data.materialConfig === 'N') {
        this.$router.push({ name: 'materialMaintenance' })
        return false
      }
      if (data.childConfig === 'N') {
        this.$router.push({ name: 'usersAccess' })
        return false
      }
      if (data.purchaseConfig === 'N') {
        this.$router.push({ name: 'purchaseCategoryMaintenance' })
        return false
      }
      if (data.vendorConfig === 'N') {
        this.$router.push({ name: 'vendorGreenChannel' })
        return false
      }
    }
  }
}
</script>
<style scoped lang="scss">
.the_settingGuide_wrapper {
  min-width: 800px;
  padding:0 !important;
  .the_header {
    border-bottom: 1px solid #E8E9EA;
    padding-left: 16px;
    .right {
      display: flex;
      align-items: center;
      height: 48px;
      .the_first {
        font-size: 16px;
        font-weight: bold;
        color: #161C24;
        height: 24px;
        line-height: 24px;
      }
      .the_second {
        font-size: 12px;
        color: #73777C;
        margin-left: 8px;
        height: 20px;
        line-height: 20px;
      }
    }
  }
  .the_steps_wrapper {
    min-height: 450px;
  }
}
.guide-content {
  position: relative;
  margin: 0 auto;
}
.common-sec {
  min-height: 100px;
  position: relative;
  z-index: 4;
  display: -webkit-flex;
  display: flex;
  display: inline-flex;
  flex-direction: row;
  width: 100%;
  &.forwardDir {
    background: #ffffff;
  }
  &.reverseDir {
    background: #FAFBFB;
  }
  .icon-div {
    width: 150px;
    position: relative;
    margin-right: 22px;
    padding: 16px 0;
    span {
      display: block;
      text-align: right;
      margin-right: 36px;
      &.icon-div-text {
        font-size: 14px;
        color: #161C24;
        line-height: 24px;
        font-weight: bold;
      }
      &.statusText {
        font-size: 12px;
        color: #73777C;
        line-height: 20px;
      }
    }
    .status-div {
      position: absolute;
      top: 0;
      right: 0;
      width: 20px;
      height: 100%;
      .line {
        position: absolute;
        width: 1px;
        height: 100%;
        content: " ";
        right: 9px;
        top: 0px;
        z-index: 5;
        border-right: 1px dashed #DCDDDE;
      }
      &.first-icon {
        .line {
          top: 20px;
        }
      }
      &.last-icon {
        .line {
          height: 20px;
        }
      }
    }
    .statuIcon {
      position: absolute;
      width: 20px;
      height: 20px;
      font-size: 20px;
      right: 0;
      top: 17px;
      z-index: 6;
      color: #52c718;
      background: #fff;
      border-radius: 50%;
      text-align: center;
      line-height: 20px;
      &.todo {
        background: #C5C6C8;
        color: #fff;
        font-size: 12px;
      }
      &.currentStatus {
        background: #0077FF;
        color: #fff;
        font-size: 12px;
      }
    }
    &::after {
      position: absolute;
      content: " ";
    }
  }
  .content-div {
    padding: 16px 0;
    flex: 1;
    .pross-steps {
      .icon-flag {
        color:#0077FF;
        margin-right: 6px;
        &.not-finished {
          color: #C5C6C8;
        }
      }
      .btn-link {
        color: #0077FF;
        padding-right: 90px;
        position: relative;
        font-size: 14px;
        line-height: 24px;
        &::after {
          position: absolute;
          content: " ";
          width: 80px;
          font-size: 0px;
          height: 1px;
          background: #E8E9EA;
          right: 3px;
          top: 10px;
        }
        &:last-child {
          &::after {
            display: none;
          }
        }
      }
    }
    .steps-des {
      p {
        margin: 0;
        font-size: 12px;
        color: #73777C;
        line-height: 20px;
        letter-spacing: 0;
      }
    }
  }
}
</style>
