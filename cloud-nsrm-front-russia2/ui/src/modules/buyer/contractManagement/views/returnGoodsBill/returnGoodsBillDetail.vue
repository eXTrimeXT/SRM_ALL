<template>
  <el-container
    class="flex-container the-returnGoodsBillDetail-detail"
    direction="vertical"
  >
    <el-main>
      <div class="form-container2">
        <el-form
          ref="form"
          :model="form"
          label-width="80px"
          label-position="top"
          class="form-incontainer"
          :rules="rules"
        >
          <p>{{ $t('returnGoodsBill.key1') }}</p>
          <el-collapse
            v-model="activeDims"
            class="tab-form-style"
          >
            <el-collapse-item
              :title="$t('purSettlementMod.basicInfo')"
              name="1"
            >
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('orderMod.buyerOrderSynergy.returnOrderNumber')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="form.site"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('orderMod.buyerOrderSynergy.contractNo')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="form.tel" />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('orderMod.buyerOrderSynergy.vendorName')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="form.vendorName"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('common.vendorCode')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="form.vendorCode"
                      disabled
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('route.vendorPurchaseOrder')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="form.purchaseNum"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('orderMod.buyerOrderSynergy.deliveryNumber')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="form.deliveryNum" />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('returnGoodsBill.key2')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="form.paidAmount"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('returnGoodsBill.key3')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="form.totalAmount"
                      disabled
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('returnGoodsBill.key4')"
                    :label-width="formLabelWidth"
                  >
                    <el-select
                      v-model="form.currency"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('returnGoodsBill.key5')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="form.returnAmount"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.returnNum')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="form.returnQuantity"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('common.approvalStatus')"
                    :label-width="formLabelWidth"
                  >
                    <el-select
                      v-model="form.auditStatus"
                      disabled
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('dataConfMod.createdBy')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="form.createdBy"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('common.creationTime')"
                    :label-width="formLabelWidth"
                    prop="creationDate"
                  >
                    <el-date-picker
                      v-model="form.stopTime"
                      type="date"
                      :format="$formatDatePicker"
                      disabled
                      :placeholder="$t('purchaseDemand.datePicker')"
                    />
                  </el-form-item>
                </el-col>
                <el-col><p /></el-col>
                <el-col><p /></el-col>
              </el-row>
            </el-collapse-item>
            <el-collapse-item
              :title="$t('purSettlementMod.returnDetail')"
              name="2"
            >
              <p>
                <el-button
                  type="primary"
                  @click="addOneContent"
                >
                  {{ $t('common.add') }}
                </el-button>
                <!-- <el-button type="primary" @click="deleteOneContent">{{$t('common.delete')}}</el-button> -->
                <el-button
                  type="primary"
                  @click="readOneContent"
                >
                  {{ $t('returnGoodsBill.key6') }}
                </el-button>
                <el-button
                  type="primary"
                  @click="copyOneContent"
                >
                  {{ $t('returnGoodsBill.key7') }}
                </el-button>
              </p>
              <el-table
                :data="receptionList"
                style="width: 100%"
                border
                max-height="250px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('contractMod.order')"
                  width="60"
                />
                <el-table-column
                  align="center"
                  prop="prLine"
                  :label="$t('returnGoodsBill.key8')"
                  width="120"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="prNo"
                  :label="$t('returnGoodsBill.key9')"
                  width="120"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="itemCode"
                  :label="$t('mould.itemNumber')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="itemName"
                  :label="$t('purSettlementMod.materialId')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="specification"
                  :label="$t('bidMod.specification')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="quantity"
                  :label="$t('returnGoodsBill.key10')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="quantity"
                  :label="$t('orderMod.buyerOrderSynergy.taxPrice')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="inspectionQuantity"
                  :label="$t('purSettlementMod.returnNum')"
                  width="100"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.inspectionQuantity"
                      type="number"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="notaxAmount"
                  :label="$t('accountMod.refundBeforeTax')"
                  width="120"
                />
                <el-table-column
                  align="center"
                  prop="taxAmount"
                  :label="$t('accountMod.refundAfterTax')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="unit"
                  :label="$t('dataConfMod.settingGuide.step3.3')"
                  width="60"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="receptionComments"
                  :label="$t('bidMod.appraisRemark')"
                  width="150"
                  :show-overflow-tooltip="true"
                />
              </el-table>
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </div>
      <c-toolbar>
        <template slot="right">
          <el-button
            type="primary"
            @click="saveBill"
          >
            {{ $t('common.save') }}
          </el-button>
          <el-button
            type="primary"
            @click="submitBill"
          >
            {{ $t('common.submit') }}
          </el-button>
          <el-button
            type="primary"
            @click="approvalBill"
          >
            {{ $t('accountMod.review') }}
          </el-button>
        </template>
      </c-toolbar>
    </el-main>
  </el-container>
</template>
<script>
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import { parseTime } from '@/utils'

export default {
  name: 'ReturnGoodsBillDetail',
  components: {
    MainHeader,
    CToolbar
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      form: {
        projId: '',
        projCode: '',
        templateName: '',
        templateType: '',
        stopTime: '',
        publishRange: '',
        status: 'DRAFT',
        resume: '',
        currency: '',
        taxRate: '',
        showPriceEnabled: 'N',
        remark: '',
        fileuploadId: '',
        fileName: ''
      },
      receptionList: [],
      activeDims: ['1', '2', '3', '4'],
      rules: {
        templateName: [{ required: true, message: '请输入项目名称' }],
        templateType: [{ required: true, message: '请选择项目类型' }],
        stopTime: [{ required: true, message: '请选择截止时间' }],
        publishRange: [{ required: true, message: '请选择发布范围' }]
      },
      isDisabled: this.$attrs.params.flag == 'edit',
      formLabelWidth: '120px',
      isModify: false
    }
  },
  created () {
    if (this.$attrs.params.flag == 'edit') {
      this.getFormDetail()
    }
  },
  methods: {
    getFormDetail () {
      this.$http({
        url:
          '/api-bid/techDiscuss/techDiscussReply/techDiscussReplyInfo',
        method: 'POST',
        data: this.$attrs.params.row,
        loading: true
      })
        .then(data => {
          this.form = data.data
        })
        .catch(err => {
          console.log(err)
        })
    },
    deleteOneContent (index, row) {
      this.receptionList.splice(index, 1)
    },
    backBill () {
      if (this.$attrs.params.flag == 'edit') {
        this.$emit(
          'tab-remove',
          'returnGoodsBillDetail' + this.$attrs.params.row.templateName
        )
      } else {
        this.$emit('tab-remove', 'returnGoodsBillDetail')
      }
      this.__setTabTodo('technicalCommunicationList.getQuerydata')
    },
    addOneContent () {
      this.receptionList.push({})
    },
    readOneContent () {},
    copyOneContent () {},
    submitBill () {},
    approvalBill () {},
    saveBill () {
      this.$emit('tab-remove', 'returnGoodsBillDetail')
    },
    reset () {
      // 重置所有过滤条件
      for (let i in this.form) {
        this.form[i] = ''
      }
    }
  }
}
</script>
<style scoped lang="scss">
.the-returnGoodsBillDetail-detail {
  .form-container2{padding: 5px;}
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
}
</style>
