<template>
  <srm-dialog
    v-if="dialogVisible"
    :visible.sync="dialogVisible"
    :title="$t('bidMod.common.payBill')"
    size="middle"
    append-to-body
    :close-on-click-modal="false"
  >
    <div class="dialog-wrap">
      <!--报价截止倒计时-->
      <div>
        <DynamicCutoffTime
          :label="$t('bidMod.common.payDateLeft')"
          :deadline-time="bondPayInfoData.bondEndTime"
          @isDeadline="value => isDeadline = value"
        />
      </div>

      <div class="bond-info-wrap">
        <srm-row>
          <!--保证金金额（万元）-->
          <srm-col :init-col="2" class="info-col">
            <span>{{ $t('bidMod.bondAmount') }}: </span>{{ bondPayInfoData.bondAmount }}
          </srm-col>
          <!--保证金提交方式-->
          <srm-col :init-col="2" class="info-col">
            <span>{{ $t('bidMod.bondMethod') }}: </span>{{ $getDictLabel('BID_BOND_SUBMISSION', bondPayInfoData.bondMethod) }}
          </srm-col>
          <!--保证金缴纳账号-->
          <srm-col :init-col="2" class="info-col">
            <span>{{ $t('bidMod.bankAccountNum') }}: </span>{{ bondPayInfoData.bankAccountNum }}
          </srm-col>
          <!--账户名称-->
          <srm-col :init-col="2" class="info-col">
            <span>{{ $t('vendorMod.bankAccountName') }}: </span>{{ bondPayInfoData.bankAccountName }}
          </srm-col>
          <!--开户支行-->
          <srm-col :init-col="2" class="info-col">
            <span>{{ $t('bidMod.bankBranchName') }}: </span>{{ bondPayInfoData.bankBranchName }}
          </srm-col>
          <!--其他说明-->
          <srm-col :init-col="1" class="info-col">
            <span>{{ $t('bidMod.bondDesc') }}: </span>{{ bondPayInfoData.bondDesc }}
          </srm-col>
        </srm-row>
      </div>

      <el-form
        ref="bondPayForm"
        :model="bondPayFormData"
        :rules="bondPayFormRules"
        inline
        label-position="right"
        label-width="80px"
        :disabled="readonly || isDeadline"
      >
        <!-- 缴纳时间 -->
        <el-form-item
          :label="$t('bidMod.common.payDateOrigin')"
          prop="payDate"
          style="margin-bottom: 20px"
        >
          <el-date-picker
            v-model="bondPayFormData.payDate"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            :picker-options="cannotLargerCurrentTimeOptions"
          />
        </el-form-item>

        <!--上传附件-->
        <SrmCommonFile
          type="form-item"
          :default-file="{ fileId: bondPayFormData.payDocId, fileName: bondPayFormData.payFileName }"
          :form-item-options="{
            label: '缴纳证明',
            prop: 'payDocId',
            nameProp: 'payFileName'
          }"
          :readonly="readonly || isDeadline"
          @on-change="bondPayFilesChange"
        />
      </el-form>
    </div>

    <div
      slot="footer"
      class="dialog-footer"
    >
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>
      <!--提交-->
      <el-button
        v-if="!readonly && !isDeadline"
        type="primary"
        @click="submitBond"
      >
        {{ $t('common.submit') }}
      </el-button>
    </div>
  </srm-dialog>
</template>

<script>
/**
 * 供应商保证金缴纳
 */
import { validatorBusinessType } from 'lib@/composition/origin/composition'
import { getBondPayApiParams } from './utils'
import { cannotLargerCurrentTime } from 'lib@/mixins/datePickerOptions'
import DynamicCutoffTime from 'lib@/components/dynamic-cutoff-time'
import { transformMQL } from 'lib@/utils/util'
import { compHttp } from './api'
import { transform } from 'lodash'

export default {
  name: 'BondPayDialog',

  components: { DynamicCutoffTime },

  mixins: [cannotLargerCurrentTime],

  props: {
    // 业务类型
    businessType: {
      type: String,
      required: false,
      validator: value => validatorBusinessType(value)
    },
    visible: {
      type: Boolean,
      default: false
    },
    // 单据基础信息 { id, idKey }
    baseInfo: {
      type: Object,
      required: true,
      default: () => {
        return {
          id: '',
          idKey: ''
        }
      }
    },
    // 只读
    readonly: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      bondPayFormData: {
        payDate: '',
        payDocId: '',
        payFileName: '',
        vendorBondId: null
      },
      bondPayFormRules: {
        payDate: [
          { required: true, message: this.$t('bidMod.common.payDateMsg1') },
          {
            validator: (rule, value, callback) => {
              if (value) {
                const startDate = new Date()
                const endDate = new Date(value)
                if (startDate.getTime() < endDate.getTime()) {
                  callback(new Error(this.$t('bidMod.common.payDateMsg2')))
                }
              }
              callback()
            },
            trigger: ['change', 'blur']
          }
        ],
        payDocId: [{ required: true, message: this.$t('bidMod.common.payDocMsg') }]
      },
      bondPayInfoData: {},
      isDeadline: false
    }
  },

  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    }
  },

  created () {
    this.getBondData()
  },

  methods: {
    /* 获取保证金缴纳数据 */
    async getBondData () {
      let transformParams = transformMQL.save('AuctSouProjectForVendor', {
        filter: {
          projectId: {
            eq: this.baseInfo.id
          }
        },
        page: {
          pageNum: 1,
          pageSize: 1000
        }
      }, 'listVendorBonds')

      const response = await compHttp.listBonds(
        transformParams
      )

      if (response.data.records.length) {
        let result = response.data.records[0]
        this.bondPayInfoData = result.souProject.auctSouProject
        this.bondPayFormData = {
          payDate: result.payDate || '',
          payDocId: result.payDocId || '',
          payFileName: result.payFileName || '',
          vendorBondId: result.vendorBondId || null
        }
      }
    },

    /* 文件上传改变 */
    bondPayFilesChange ({ file }) {
      if (file) {
        this.bondPayFormData.payDocId = file.fileId.toString()
        this.bondPayFormData.payFileName = file.fileName
      } else {
        this.bondPayFormData.payDocId = ''
        this.bondPayFormData.payFileName = ''
      }
    },

    /* 提交缴纳保证金 */
    async submitBond () {
      let transformParams = transformMQL.save('AuctSouProjectForVendor', [{
        ...this.bondPayFormData,
        projectId: this.baseInfo.id,
        vendorId: this.$store.state.user.userInfo.companyId,
        tempSave: false
      }], 'submitBond')

      const response = await compHttp.submitBond(transformParams)

      if (response) {
        this.$message.success(this.$t('common.successSubmit'))
        this.$emit('success')
        this.dialogVisible = false
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.bond-info-wrap {
  .el-row {
    margin-bottom: 11px;
    .info-col {
      font-size: 14px;
      position: relative;
      line-height: 22px;
      min-height: 22px;
      text-overflow: ellipsis;
      white-space: nowrap;
      overflow: hidden;
    }
  }
}
</style>
