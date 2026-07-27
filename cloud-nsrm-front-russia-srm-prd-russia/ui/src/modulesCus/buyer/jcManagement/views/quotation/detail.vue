<template>
  <el-container class="flex-container the-inquiryOrdersDetail-detail" direction="vertical">
    <el-main style="padding: 10px 10px 20px 10px">
      <div class="stepDiv">
        <el-steps
          :active="activeNum"
          :align-center="true"
          finish-status="success"
        >
          <el-step :title="'待报价'" />
          <el-step :title="'已报价'" />
          <el-step :title="'报价截止'" />
        </el-steps>
      </div>
      <div class="timeBox">
        <DynamicCutoffTime
          label="距离本轮报价截止还剩余:"
          :deadline-time="headerData?.orderEndTime || ''"
        />
      </div>
      <el-form ref="relForm" :model="form" :rules="formRules" style="padding-bottom:40px">
        <el-collapse v-model="activeList" class="tab-form-style">
          <!-- ****************************************** 报价信息 ************************************** -->
          <el-collapse-item name="1" :title="'报价信息'">
            <el-row :gutter="32">
              <el-col :span="6">
                <el-form-item :label="'询价单号'">
                  <el-input v-model="headerData.souNo" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item :label="'预计报价开始时间'">
                  <el-input v-model="headerData.orderStartTime" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item :label="'报价结束时间'">
                  <el-input v-model="headerData.orderEndTime" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item :label="'当前轮次'">
                  <el-input v-model="headerData.currentRound" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item :label="'报价单号'">
                  <el-input v-model="headerData.orderNo" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item :label="'询价方'">
                  <el-input v-model="headerData.createdFullName" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item :label="'价格有效期截止日期'">
                  <el-input v-model="headerData.priceDeadlines" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item :label="'备注'">
                  <el-input v-model="headerData.remark" type="textarea" disabled />
                </el-form-item>
              </el-col>
            </el-row>
          </el-collapse-item>
          <el-collapse-item name="2" :title="'采购员信息'">
            <el-row :gutter="32">
              <el-col :span="6">
                <el-form-item :label="'姓名'">
                  <el-input v-model="headerData.createdUserName" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item :label="'手机号码'">
                  <el-input v-model="headerData.tel" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item :label="'电子邮箱'">
                  <el-input v-model="headerData.email" disabled />
                </el-form-item>
              </el-col>
            </el-row>
          </el-collapse-item>
          <el-collapse-item name="3" :title="'报价人信息（报价有效期已签订合同日期开始）'">
            <el-row :gutter="32">
              <el-col :span="6">
                <el-form-item :label="'报价人'" prop="orderByNickname">
                  <el-input v-model="form.orderByNickname" :disabled="isReadOnly" />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item :label="'报价人电话'" prop="orderPhone">
                  <el-input v-model="form.orderPhone" :disabled="isReadOnly" />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item :label="'报价人邮箱'" prop="orderEmail">
                  <el-input v-model="form.orderEmail" :disabled="isReadOnly" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-collapse-item>
          <el-collapse-item name="4" :title="'物料信息（温馨提示：增值税普通发票税率填写0（未税单价需按照含税单价填写），增值税专用发票税率根据票面实际填写。）'">
            <itemInfo
              ref="itemInfo"
              :item-list="orderItemList"
              :projectId="projectId"
              :round="round"
              :isReadOnly="isReadOnly"
              @refresh="getDetail"
            />
          </el-collapse-item>
        </el-collapse>
      </el-form>
      <CToolbar>
        <template slot="right">
          <!--b 返回-->
          <el-button @click="back">
            {{ $t('common.backTo') }}
          </el-button>
          <!--b 保存-->
          <el-button
            v-if="!isReadOnly"
            @click="saveBill('SAVE')"
          >
            {{ $t('common.save') }}
          </el-button>
          <!--b 提交-->
          <el-button v-if="!isReadOnly" type="primary" @click="saveBill('SUBMIT')">
            {{ $t('common.submit') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>

<script>
import BaseForm from 'lib@/components/BaseForm'
import CToolbar from 'lib@/components/c-toolbar'
import pictureCard from 'lib@/composition/oneStopShopping/pictureCard'
import { tabTodoMixin } from '@/utils/mixins'
import { parseTime } from '@/utils'
import { transformMQL } from 'lib@/utils/util'
import { validEmail, validatePhone } from '@/utils/validate'
import quotationApi from 'modcb@/jcManagement/api'
import DynamicCutoffTime from 'lib@/components/dynamic-cutoff-time'
import { downloadFileLink } from 'lib@/utils/file'
import itemInfo from './itemInfo'
import { BUSINESS_TYPE_ENUM, SOU_ORDER_STATUS_ENUM } from 'lib@/composition/origin/enum'
export default {
  name: 'JcQuotationDetail',
  components: {
    BaseForm,
    pictureCard,
    CToolbar,
    DynamicCutoffTime,
    itemInfo
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      curStatus: 0,
      activeList: ['1', '2', '3', '4'],
      form: {
        orderByNickname: '',
        orderPhone: '',
        orderEmail: '',
        orderItemList: []
      },
      formRules: {
        orderByNickname: [
          { required: true, message: '请填写报价人' }
        ],
        orderPhone: [
          { required: true, message: '请填写报价电话' }
        ],
        orderEmail: [
          { required: true, message: '请填写报价邮箱' }
        ]
      },
      headerData: {},
      orderItemList: [],
      round: 0,
      projectId: '',
      orderStatus: '',
      tabFlag: ''
    }
  },
  computed: {
    // 进度条状态
    activeNum () {
      let active = 0
      // 已定价 定价中
      if (['PRICING', 'PRICE_END'].includes(this.headerData.extProjectStatus)) {
        active = 2
      } else if (this.orderStatus === SOU_ORDER_STATUS_ENUM.SUBMISSION) {
        // 已报价
        active = 1
      }
      return active
    },
    isReadOnly () {
      // 已作废，已报价
      return this.tabFlag === 'view' ||
        (this.tabFlag === 'edit' && [SOU_ORDER_STATUS_ENUM.CANCEL, SOU_ORDER_STATUS_ENUM.SUBMISSION].includes(this.orderStatus))
    }
  },
  created () {
    this.tabFlag = this.$attrs.params.flag
    this.getDetail()
  },
  methods: {
    async getDetail () {
      let userInfo = this.$store.getters.userInfo
      const { flag, projectId } = this.$attrs.params
      this.projectId = projectId
      const response = await quotationApi.quotation.getOrderDetail(projectId, userInfo.companyId)
      if (response && response.data) {
        const {
          initInfo,
          itemList,
          order
        } = response.data
        const {
          projectInfo
        } = initInfo
        this.form.orderItemList = itemList || []
        this.orderItemList = itemList || []
        this.headerData = { ...projectInfo }
        this.round = this.headerData.currentRound || 0
        if (order) {
          this.form.orderByNickname = order.orderByNickname
          this.form.orderPhone = order.orderPhone
          this.form.orderEmail = order.orderEmail
          this.orderStatus = order.orderStatus
          this.headerData.orderNo = order.orderNo
        }
        this.$nextTick(() => {
          this.$refs.relForm.clearValidate()
        })
      }
    },
    async saveBill (type) {
      let flag = null
      let paramData = null
      if (type == 'SAVE') {
        flag = true
      } else {
        flag = false
        let validResult = true
        this.$refs.relForm.validate(status => {
          if (!status) {
            validResult = false
          }
        })
        if (!validResult) {
          this.$message.warning(this.$t('cusEntry.tipMessage.required'))
          return false
        }
      }
      paramData = await this.arrangeInquiryOrdersData(flag)
      if (paramData) {
        let response = await quotationApi.quotation.editOrder(paramData)
        if (response) {
          flag ? this.$message.success(this.$t('common.successSave')) : this.$message.success(this.$t('common.successSubmit'))
          this.back()
        }
      }
    },
    async arrangeInquiryOrdersData (tempSave) {
      let userInfo = this.$store.getters.userInfo
      let params = {
        ...this.form,
        projectId: this.projectId,
        // 报价信息
        orderItemList: await this.$refs.itemInfo.validateForm(true),
        // 提交还是暂存
        isTempSave: tempSave,
        tempSave: tempSave,
        orderNoGenerateCode: 'EXT_SEQ_SOU_PURINQ_ORDER_NO',
        vendorId: userInfo.companyId
        // submitById: userInfo.userId,
        // submitBy: userInfo.username,
        // submitFullName: userInfo.nickname,
        // submitByIp: userInfo.userId
      }
      if (!params.orderItemList) {
        // 校验不通过
        return null
      }
      return params
    },
    back () {
      let { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
      this.__setTabTodo('JcQuotationList.getQuerydata')
    }
  }
}
</script>

<style scoped lang="scss">
.off-cursor {
  cursor: pointer;
}
.search-po {
  float: right;
}
:deep(.el-input__clear) {
  font-size: 12px;
  width: 12px;
}
:deep(.el-input__suffix) {
  height: 28px;
  padding: 0 4px;
  color: #96999c;
  line-height: 28px;
  margin: 1px 0;
  &:hover {
    color: #0077ff;
    border-color: #96999c;
    background-color: #f6f6f6;
  }
}
.timeBox{
    display: flex;
    justify-content: flex-start;
    align-items: center;
    font-size: 18px;
    font-weight: 600;
    margin-bottom: 12px;
    >.timeNum{
        color: red;
    }
}
.tip{
    font-size: 12px;
    color:red;
    display: inline-block;
    margin: 8px 0;
}
</style>
