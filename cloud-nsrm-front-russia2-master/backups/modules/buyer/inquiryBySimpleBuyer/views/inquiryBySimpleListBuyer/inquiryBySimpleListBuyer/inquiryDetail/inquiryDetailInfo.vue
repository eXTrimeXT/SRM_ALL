<template>
  <div class="inquiry-detail-info">
    <el-form
      ref="form"
      :model="headerData"
      label-width="120px"
      label-position="top"
      class="form-incontainer"
      :disabled="readOnly"
      :rules="rules"
    >
      <el-collapse
        v-model="activeDims"
        class="tab-form-style"
      >
        <!--c 询价信息 1-->
        <el-collapse-item
          :title="$t('bidMod.inquiryInfo')"
          name="1"
        >
          <srm-row>
            <srm-col :init-col="3">
              <!--询价单号-->
              <el-form-item :label="$t('bidMod.inquiryNo')">
                <el-input
                  v-model="headerData.inquiryNo"
                  disabled
                />
              </el-form-item>
            </srm-col>

            <srm-col :init-col="3">
              <!--询价标题-->
              <el-form-item
                :label="$t('bidMod.inquiryTitle')"
                prop="inquiryTitle"
              >
                <el-input
                  v-model="headerData.inquiryTitle"
                  maxlength="80"
                  show-word-limit
                />
              </el-form-item>
            </srm-col>

            <srm-col :init-col="3">
              <!--报价方式-->
              <el-form-item
                :label="$t('bidMod.quoteRule')"
                prop="quoteRule"
              >
                <dict-select
                  v-model="headerData.quoteRule"
                  clearable
                  :disabled="quoteRuleDisabled"
                  code="RFQ_QUOTE_TYPE"
                />
              </el-form-item>
            </srm-col>

            <srm-col :init-col="3">
              <!--预计报价开始时间-->
              <el-form-item
                :label="$t('bidMod.beginQuote')"
                prop="beginQuote"
              >
                <el-date-picker
                  v-model="headerData.beginQuote"
                  type="datetime"
                  value-format="yyyy-MM-dd HH:mm:ss"
                  :picker-options="pickerOptions"
                />
              </el-form-item>
            </srm-col>

            <srm-col :init-col="3">
              <!--报价结束时间-->
              <el-form-item
                :label="$t('bidMod.deadline')"
                prop="deadline"
              >
                <el-date-picker
                  v-model="headerData.deadline"
                  type="datetime"
                  value-format="yyyy-MM-dd HH:mm:ss"
                  :picker-options="deadlinePickerOptions"
                />
              </el-form-item>
            </srm-col>

            <srm-col :init-col="3">
              <!--询价类型-->
              <el-form-item
                :label="$t('bidMod.inquiryType')"
                prop="inquiryType"
              >
                <dict-select
                  v-model="headerData.inquiryType"
                  code="RFQ_TYPE"
                  clearable
                />
              </el-form-item>
            </srm-col>

            <srm-col :init-col="3">
              <!--邀标类型-->
              <el-form-item
                label="邀标类型"
                prop="publishScope"
              >
                <dict-select
                  v-model="headerData.publishScope"
                  code="BID_SCOPE"
                  clearable
                />
              </el-form-item>
            </srm-col>

            <srm-col :init-col="3">
              <!--发起人-->
              <el-form-item :label="$t('bidMod.createdBy')">
                <el-input
                  v-model="headerData.createdUserName"
                  disabled
                />
              </el-form-item>
            </srm-col>

            <srm-col :init-col="3">
              <!--创建时间-->
              <el-form-item :label="$t('bidMod.creationDate')">
                <el-date-picker
                  v-model="headerData.creationDate"
                  type="date"
                  disabled
                />
              </el-form-item>
            </srm-col>

            <srm-col :init-col="3">
              <!--单据状态-->
              <el-form-item :label="$t('bidMod.billstatus')">
                <dict-select
                  v-model="headerData.status"
                  disabled
                  code="RFQ_STATUS"
                />
              </el-form-item>
            </srm-col>
          </srm-row>

          <srm-row>
            <srm-col :init-col="1">
              <!--备注-->
              <el-form-item :label="$t('bidMod.remark')">
                <el-input
                  v-model="headerData.remark"
                  type="textarea"
                  :rows="2"
                  maxlength="500"
                  show-word-limit
                />
              </el-form-item>
            </srm-col>
          </srm-row>
        </el-collapse-item>

        <!--c 查看附件 2-->
        <el-collapse-item
          :title="$t('bidMod.fileList')"
          name="2"
        >
          <div class="the_file_part">
            <div class="left_div">
              <p style="margin-bottom: 10px">
                <span>{{ $t("bidMod.innerFileList") }}</span>
                <el-button
                  type="primary"
                  @click="addOne"
                >
                  {{ $t("common.new") }}
                </el-button>
              </p>
              <el-table
                :data="innerFilesData"
                style="width: 100%"
                border
                height="133px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  width="40"
                />

                <!--上传附件-->
                <SrmCommonFile
                  type="table-column"
                  :table-column-options="{
                    label: $t('bidMod.fileName'),
                    prop: 'fileRelationId',
                    nameProp: 'fileName'
                  }"
                  :readonly="readOnly"
                  @on-change="innerFilesChange"
                />

                <el-table-column
                  align="center"
                  prop="remark"
                  :label="$t('bidMod.remark')"
                  min-width="120"
                >
                  <template v-slot="scope">
                    <el-input
                      v-model="scope.row.remark"
                      maxlength="150"
                    />
                  </template>
                </el-table-column>

                <el-table-column
                  align="center"
                  prop="operation"
                  :label="$t('bidMod.operation')"
                  width="60"
                >
                  <template v-slot="scope">
                    <el-button
                      type="primary"
                      icon="el-icon-delete"
                      class="el-button-icon"
                      @click="deleteInnerRow(scope.$index, scope.row)"
                    />
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <div class="right_div">
              <p style="margin-bottom: 10px">
                <span>{{ $t("bidMod.outterFileList") }}</span>
                <el-button
                  type="primary"
                  @click="addOne2"
                >
                  {{ $t("common.new") }}
                </el-button>
              </p>
              <el-table
                :data="outerFilesData"
                style="width: 100%"
                border
                height="133px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  width="40"
                />

                <!--附件名称-->
                <SrmCommonFile
                  type="table-column"
                  :table-column-options="{
                    label: $t('bidMod.fileName'),
                    prop: 'fileRelationId',
                    nameProp: 'fileName',
                    minWidth: '130'
                  }"
                  :readonly="readOnly"
                  @on-change="outerFileChange"
                />

                <el-table-column
                  align="center"
                  prop="remark"
                  :label="$t('bidMod.remark')"
                  min-width="120"
                >
                  <template v-slot="scope">
                    <el-input
                      v-model="scope.row.remark"
                      maxlength="150"
                    />
                  </template>
                </el-table-column>

                <el-table-column
                  align="center"
                  prop="operation"
                  :label="$t('bidMod.operation')"
                  width="60"
                >
                  <template v-slot="scope">
                    <el-button
                      type="primary"
                      icon="el-icon-delete"
                      class="el-button-icon"
                      @click="deleteOuterRow(scope.$index, scope.row)"
                    />
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </el-collapse-item>

        <!--c 商务信息 3-->
        <el-collapse-item
          :title="$t('bidMod.businessInfo')"
          name="3"
        >
          <OriginQuoteCurrency
            ref="quoteCurrency"
            :base-info.sync="headerData"
            :detail-data="currencyList"
            business-type="INQUIRY"
            :readonly="readOnly"
          />
        </el-collapse-item>

        <!--c 联系方式 4-->
        <el-collapse-item :title="$t('bidMod.contactInfo')" name="4">
          <OriginContactInfo
            business-type="INQUIRY"
            :info-data.sync="headerData"
            :set-default="pageFlag === 'add'"
          />
        </el-collapse-item>

        <!--c 投标控制 5-->
        <el-collapse-item
          :title="$t('bidMod.bidingControl')"
          name="5"
        >
          <srm-row>
            <!--允许供应商撤回报价-->
            <srm-col :init-col="3">
              <el-checkbox
                v-model="headerData.allowWithdrawBiding"
                true-label="Y"
                false-label="N"
              >
                {{ $t("bidMod.withdrawBiding1") }}
              </el-checkbox>
            </srm-col>

            <!--允许供应商只对部分商品（组合）报价-->
            <srm-col :init-col="3">
              <el-checkbox
                v-model="headerData.allowPartBiding"
                true-label="Y"
                false-label="N"
              >
                {{ $t("bidMod.partPrice") }}
              </el-checkbox>
            </srm-col>

            <!--密封报价-->
            <srm-col :init-col="3">
              <el-checkbox
                v-model="headerData.needEncryptPrice"
                true-label="Y"
                false-label="N"
              >
                密封报价
              </el-checkbox>
            </srm-col>
          </srm-row>
        </el-collapse-item>

        <!--c 推荐供应商控制 6-->
        <el-collapse-item
          title="推荐供应商控制"
          name="6"
        >
          <srm-row>
            <!--排除黑名单供应商-->
            <srm-col :init-col="3">
              <el-checkbox
                v-model="headerData.excludeBlackVendors"
                true-label="Y"
                false-label="N"
              >
                排除黑名单供应商
              </el-checkbox>
            </srm-col>

            <!--排除非本业务实体供应商-->
            <srm-col :init-col="3">
              <el-checkbox
                v-model="headerData.excludeNoCurrentOrgVendors"
                true-label="Y"
                false-label="N"
              >
                排除非本业务实体供应商
              </el-checkbox>
            </srm-col>

            <!--排除业务实体退出/冻结供应商-->
            <srm-col :init-col="3">
              <el-checkbox
                v-model="headerData.excludeOrgQuitVendors"
                true-label="Y"
                false-label="N"
              >
                排除业务实体退出/冻结供应商
              </el-checkbox>
            </srm-col>
          </srm-row>

          <srm-row style="margin-top: 15px">
            <!--排除 XX 状态品类供应商-->
            <srm-col :init-col="2">
              <el-checkbox
                :value="categoryStatusCheck"
                true-label="Y"
                false-label="N"
                disabled
              >
                排除
                <dict-select
                  v-model="excludeOrgCategoryStatus"
                  code="CATEGORY_STATUS"
                  multiple
                  style="width: 250px"
                />
                状态品类供应商
              </el-checkbox>
            </srm-col>
          </srm-row>
        </el-collapse-item>
      </el-collapse>
    </el-form>
  </div>
</template>

<script>
/**
 * 询价信息
 */
import { isEmail, isMobile } from 'lib@/utils/validate'
import OriginQuoteCurrency from 'lib@/composition/origin/quoteCurrency'
import OriginContactInfo from 'lib@/composition/origin/contactInfo'

export default {
  name: 'InquiryDetailInfo',

  components: {
    OriginQuoteCurrency,
    OriginContactInfo
  },

  props: {
    header: Object,
    innerFiles: [Array, Object],
    outerFiles: [Array, Object],
    pickerOptions: Object,
    currencyList: [Array, Object],
    readOnly: {
      type: Boolean,
      required: true
    },
    pageFlag: {
      type: [String, Object],
      required: true
    }
  },

  data () {
    return {
      rules: {
        inquiryTitle: [{ required: true, message: '请输入询价标题' }],
        organizationId: [{ required: true, message: '请选择采购组织' }],
        quoteRule: [{ required: true, message: '请选择报价方式' }],
        beginQuote: [
          { required: true, message: '请选择预计报价开始时间' },
          {
            validator: (_rule, value, callback) => {
              if (value) {
                const [valueDate, diffDate] = [
                  this.$dayjs(value).unix(),
                  this.$dayjs(this.headerData.deadline).unix()
                ]
                if (valueDate >= diffDate) {
                  callback(new Error('报价开始时间需要小于报价结束时间'))
                }
              }
              callback()
            },
            trigger: ['change', 'blur']
          }
        ],
        deadline: [
          { required: true, message: '请选择报价结束时间' },
          {
            validator: (_rule, value, callback) => {
              if (value) {
                const [nowDate, diffDate, valueDate] = [
                  this.$dayjs().unix(),
                  this.$dayjs(this.headerData.beginQuote).unix(),
                  this.$dayjs(value).unix()
                ]
                if (valueDate < nowDate) {
                  callback(new Error('报价结束时间需要大于当前时间'))
                }
                if (valueDate < diffDate) {
                  callback(new Error('报价结束时间需要大于报价开始时间'))
                }
              }
              callback()
            },
            trigger: ['change', 'blur']
          }
        ],
        publishScope: [{ required: true, message: '请选择邀标类型' }],
        inquiryType: [{ required: true, message: '请选择询价类型' }],
        currency: [{ required: true, message: '请选择结算币种' }],
        exchangeRateType: [{ required: true, message: '请选择汇率类型' }],
        currencyExchangeDate: [{ required: true, message: '请选择币种转换日期' }],
        priceNum: [{ required: true, message: '请输入报价最多保留' }],
        linkman: [{ required: true, message: '请输入姓名' }],
        email: [
          { required: true, message: '请输入邮箱' },
          {
            validator: (rule, value, callback) => {
              if (!value) {
                callback(new Error(this.$t('bidMod.bidMsgList[24]')))
              } else if (!isEmail(value)) {
                callback(new Error(this.$t('bidMod.bidMsgList[25]')))
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ],
        tel: [
          {
            validator: (rule, value, callback) => {
              if (!value) {
                callback()
              } else if (!isMobile(value)) {
                callback(new Error('手机格式不合法'))
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ]
      },
      activeDims: ['1', '2', '3', '4', '5', '6'],
      bankRowIndex: null,
      selectValue: [],
      deadlinePickerOptions: {
        disabledDate: (time) => {
          const [nowDate, startDate, valueDate] = [
            this.$dayjs().hour(0).minute(0).second(0).unix(),
            this.$dayjs(this.headerData.beginQuote).hour(0).minute(0).second(0).unix(),
            this.$dayjs(time).unix()
          ]
          return (valueDate < startDate) || (valueDate < nowDate)
        }
      }
    }
  },

  computed: {
    headerData: {
      get: function () {
        return this.header
      },
      set: function (val) {
        this.$emit('update:header', val)
      }
    },

    innerFilesData: {
      get: function () {
        return this.innerFiles
      },
      set: function (val) {
        this.$emit('update:innerFiles', val)
      }
    },

    outerFilesData: {
      get: function () {
        return this.outerFiles
      },
      set: function (val) {
        this.$emit('update:outerFiles', val)
      }
    },

    // currencyListData: {
    //   get: function () {
    //     return this.currencyList
    //   },
    //   set: function (val) {
    //     this.$emit('update:currencyList', val)
    //   }
    // },

    // 是否禁止选择报价方式
    quoteRuleDisabled () {
      return this.headerData.inquiryRule === 'COMPREHENSIVE_SCORING_METHOD'
    },

    excludeOrgCategoryStatus: {
      get: function () {
        const str = this.headerData.excludeOrgCategoryStatus || ''
        return str ? str.split(',') : []
      },
      set: function (val) {
        this.headerData.excludeOrgCategoryStatus = val.toString()
      }
    },

    // 排除状态的供应勾选
    categoryStatusCheck () {
      return this.excludeOrgCategoryStatus.length > 0
    }
  },

  methods: {
    /* 新增内部查看附件 */
    addOne () {
      this.innerFilesData.push({
        inquiryId: '',
        fileRelationId: '',
        fileName: '',
        type: 'INNER',
        remark: ''
      })
    },

    /* 文件变更 */
    outerFileChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.outerFilesData[$index].fileRelationId = fileId
      this.outerFilesData[$index].fileName = fileName
    },

    /* 内部查看文件变更 */
    innerFilesChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.innerFilesData[$index].fileRelationId = fileId
      this.innerFilesData[$index].fileName = fileName
    },

    /* 删除外部附件 */
    outerHandleAttachmentRemove (row) {
      row.fileRelationId = ''
      row.fileName = ''
    },

    /* 删除内部附件行 */
    deleteInnerRow (index) {
      this.innerFilesData.splice(index, 1)
    },

    /* 删除外部附件行 */
    deleteOuterRow (index) {
      this.outerFilesData.splice(index, 1)
    },

    addOne2 () {
      this.outerFilesData.push({
        inquiryId: '',
        fileRelationId: '',
        fileName: '',
        type: 'OUTER',
        remark: ''
      })
    },

    /* 清除表单校验信息 父组件调用 */
    clearFormValidate () {
      this.$refs.form.clearValidate()
    },

    /* 返回当前数据 父组件外部调用 */
    getParamsData () {
      return {
        currencyList: this.$refs.quoteCurrency.getParamsData()
      }
    },

    /* 校验 */
    validateForm () {
      return new Promise(resolve => {
        this.$refs.form.validate(async valid => {
          if (valid) {
            let resolveStatus = true
            const currencyList = this.$refs.quoteCurrency.getParamsData()
            for (const i of currencyList) {
              if (!i.priceTax) {
                this.$message.warning('外币清单存在行没有配置汇率')
                resolveStatus = false
                return
              }
            }
            resolve(resolveStatus)
          } else {
            this.__focus_error__()
            resolve(false)
          }
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.inquiry-detail-info {
  .the_file_part {
    .left_div,
    .right_div {
      width: 50%;
      padding: 3px;
      float: left;
      > p {
        margin: 0;
        span {
          padding-right: 11px;
        }
      }
    }
  }
}
</style>
