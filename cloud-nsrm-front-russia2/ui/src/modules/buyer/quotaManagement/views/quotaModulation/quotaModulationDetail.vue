<template>
  <el-container class="flex-container the-quotaModulationDetail-detail" direction="vertical">
    <el-main>
      <div class="form-container2">
        <el-form
          ref="quotaAdjust"
          :disabled="isReadOnly"
          :model="quotaAdjust"
          label-width="80px"
          label-position="top"
          class="form-fill-style"
        >
          <el-collapse v-model="activeDims" class="tab-form-style">
            <!-- 配额配置详情 -->
            <el-collapse-item :title="$t('quota.quotaConfigDetail')" name="1">
              <srm-row>
                <srm-col>
                  <!-- 单据编号 -->
                  <el-form-item :label="$t('quota.orderNumber')" :label-width="formLabelWidth">
                    <el-input v-model="quotaAdjust.quotaAdjustCode" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 单据标题 -->
                  <el-form-item
                    :label="$t('quota.orderTitle')"
                    :label-width="formLabelWidth"
                    prop="quotaAdjustName"
                  >
                    <el-input v-model="quotaAdjust.quotaAdjustName" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 寻源单号 -->
                  <el-form-item :label="$t('quota.ceeaSourceNo')" :label-width="formLabelWidth">
                    <QuickSearch
                      :show-input="quotaAdjust.ceeaSourceNo"
                      show-key="ceeaSourceNo"
                      :scope-data="quotaAdjust"
                      name="scc_price_library_display"
                      @close-quicksearch="getCategoryObj"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 创建人 -->
                  <el-form-item :label="$t('quota.createdBy')" :label-width="formLabelWidth">
                    <el-input v-model="quotaAdjust.createdUserName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 创建日期 -->
                  <el-form-item :label="$t('quota.createdDate')" :label-width="formLabelWidth">
                    <el-date-picker
                      v-model="quotaAdjust.creationDate"
                      :format="$formatDatePickerTime"
                      value-format="yyyy-MM-dd HH:mm:ss"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 调整类型 -->
                  <el-form-item :label="$t('quota.adjustType')" :label-width="formLabelWidth">
                    <DictSelect v-model="quotaAdjust.adjustType" code="ADJUSTMENT_TYPE" />
                  </el-form-item>
                </srm-col>

                <srm-col>
                  <!-- 状态 -->
                  <el-form-item :label="$t('quota.status')" :label-width="formLabelWidth">
                    <DictSelect v-model="quotaAdjust.status" code="ADJUST_STATUS" disabled />
                  </el-form-item>
                </srm-col>

                <srm-col :init-col="1">
                  <!-- 备注 -->
                  <el-form-item :label="$t('quota.remark')" :label-width="formLabelWidth">
                    <el-input v-model="quotaAdjust.remark" type="textarea" :rows="2" />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>

            <!-- 中标行 -->
            <el-collapse-item :title="$t('quota.quotaSourceDTO')" name="2">
              <el-table :data="quotaSourceDTOList" style="width: 100%" border max-height="251px">
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('purSettlementMod.tabindex')"
                  width="65"
                />
                <!-- 寻源单号 -->
                <el-table-column
                  align="center"
                  prop="ceeaSourceNo"
                  :label="$t('quota.ceeaSourceNo')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                />
                <!-- 寻源类型 -->
                <el-table-column
                  align="center"
                  prop="sourceType"
                  :label="$t('quota.sourceType')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 业务实体 -->
                <el-table-column
                  align="center"
                  prop="orgName"
                  :label="$t('quota.org')"
                  width="150"
                  :show-overflow-tooltip="true"
                />
                <!-- 小类 -->
                <el-table-column
                  align="center"
                  prop="categoryName"
                  :label="$t('quota.subcategory')"
                  width="150"
                  :show-overflow-tooltip="true"
                />
                <!-- 物料编码 -->
                <el-table-column
                  align="center"
                  prop="itemCode"
                  :label="$t('quota.itemCode')"
                  width="120"
                  :show-overflow-tooltip="true"
                />
                <!-- 物料名称 -->
                <el-table-column
                  align="center"
                  prop="itemName"
                  :label="$t('quota.itemName')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                />
                <!-- 需求数量 -->
                <el-table-column
                  align="center"
                  prop="isNeedTotal"
                  :label="$t('quota.isNeedTotal')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 供应商编码 -->
                <el-table-column
                  align="center"
                  prop="vendorCode"
                  :label="$t('quota.vendorCode')"
                  width="120"
                  :show-overflow-tooltip="true"
                />
                <!-- 供应商名称 -->
                <el-table-column
                  align="center"
                  prop="vendorName"
                  :label="$t('quota.vendorName')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                />
                <!-- 含税单价 -->
                <el-table-column
                  align="center"
                  prop="taxPrice"
                  :label="$t('quota.taxPrice')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 价格有效期自 -->
                <el-table-column
                  align="center"
                  prop="startTime"
                  :label="$t('quota.priceStartTime')"
                  width="100"
                  :show-overflow-tooltip="true"
                  :formatter="(row, column, cellValue) => $parseTime(cellValue)"
                />
                <!-- 价格有效期至 -->
                <el-table-column
                  align="center"
                  prop="endTime"
                  :label="$t('quota.priceEndTime')"
                  width="100"
                  :show-overflow-tooltip="true"
                  :formatter="(row, column, cellValue) => $parseTime(cellValue)"
                />
                <!-- 实际比例% -->
                <el-table-column
                  align="center"
                  prop="quotaProportion"
                  :label="$t('quota.quotaProportion') + '%'"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 目标比例% -->
                <el-table-column
                  align="center"
                  prop="targetProportion"
                  :label="$t('quota.targetProportion') + '%'"
                  width="100"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.targetProportion"
                      v-input-format="{ type: 'float' }"
                      @change="setRowAmount(scope.row)"
                    />
                  </template>
                </el-table-column>
                <!-- 金额差异 -->
                <el-table-column
                  align="center"
                  prop="amountDifference"
                  :label="$t('quota.amountDifference')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 币种 -->
                <el-table-column
                  align="center"
                  prop="currency"
                  :label="$t('quota.currency')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
              </el-table>
            </el-collapse-item>
            <!-- 附件信息 -->
            <el-collapse-item :title="$t('quota.fileInfo')" name="4">
              <p style="margin: 0 0 10px 0">
                <el-button type="primary" class="detail-pbtn" @click="addFileuploads">
                  {{
                    $t('common.add')
                  }}
                </el-button>
              </p>
              <el-table :data="quotaAdjustFile" style="width: 100%" border max-height="251px">
                <!-- 附件上传 -->
                <el-table-column
                  align="center"
                  prop="fileSourceName"
                  :label="$t('quota.fileupload')"
                  width="250"
                >
                  <template slot-scope="scope">
                    <SrmCommonFile
                      :extra-data="fileInfo"
                      :default-file="{
                        fileId: scope.row.fileuploadId,
                        fileName: scope.row.fileSourceName
                      }"
                      :readonly="false"
                      @on-change="({ file }) => outerHandleUploadSuccess(file, scope.row)"
                    />
                  </template>
                </el-table-column>
                <!-- 上传人 -->
                <el-table-column
                  align="center"
                  prop="createdUserName"
                  :label="$t('quota.uploadBy')"
                  width="150"
                />
                <!-- 上传时间 -->
                <el-table-column
                  align="center"
                  prop="creationDate"
                  :label="$t('quota.uploadDate')"
                  width="150"
                  :formatter="(row, column, cellValue) => $parseTime(cellValue)"
                />
                <!-- 备注 -->
                <el-table-column
                  align="center"
                  prop="comment"
                  :label="$t('quota.remark')"
                  min-width="200"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.comment" />
                  </template>
                </el-table-column>
                <el-table-column :label="$t('common.operation')" width="60">
                  <template slot-scope="scope">
                    <el-button type="text" @click="delInvoiceTaxControls(scope.$index, scope.row)">
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </div>
      <CToolbar v-if="!isReadOnly">
        <template slot="right">
          <el-button type="primary" @click="saveBill">
            {{ $t('common.submit') }}
          </el-button>
          <!--<el-button type="primary"  @click="submitBill">{{$t('common.submit')}}</el-button>-->
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>

<script>
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'

export default {
  name: 'QuotaModulationDetail',
  components: {
    CToolbar,
    QuickSearch
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      activeDims: ['1', '2', '3', '4'],
      quotaAdjust: { // 配额配置详情
        quotaAdjustId: null,
        quotaAdjustCode: null,
        quotaAdjustName: null,
        ceeaSourceNo: null,
        createdUserName: null,
        creationDate: null,
        adjustType: null,
        status: 'DRAFT',
        remark: null
      },
      quotaSourceDTOList: [], // 中标行
      quotaAdjustFile: [], // 附件信息
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'quotaModulation',
        fileType: 'images'
      },
      isReadOnly: this.$attrs.params.flag == 'isReadOnly',
      formLabelWidth: '120px'
    }
  },
  created () {
    if (this.$attrs.params.flag == 'add') {
      if (this.$store.getters.userInfo) {
        this.quotaAdjust.applyUserNickname = this.$store.getters.userInfo.nickname
        this.quotaAdjust.applyDeptName = this.$store.getters.userInfo.department
      }
    }
    if (this.$attrs.params.flag == 'edit' || this.$attrs.params.flag == 'isReadOnly') {
      this.getFormDetail()
    }
  },
  methods: {
    getFormDetail () {
      this.$http({
        url: '/api-inq/inquiry/quotaAdjust/getQuotaAdjust',
        method: 'GET',
        params: { id: this.$attrs.params.row.quotaAdjustId },
        loading: true
      })
        .then((data) => {
          if (data && data.data) {
            this.quotaAdjust = data.data.quotaAdjust // 配额配置详情
            this.quotaSourceDTOList = data.data.quotaSourceDTOList // 中标行
            this.quotaAdjustFile = data.data.quotaAdjustFile // 附件信息
          }
        })
        .catch((err) => {
          console.log(err)
        })
    },

    getCategoryObj (val, scope) {
      scope.ceeaSourceNo = val ? val.sourceNo : ''
      this.$http({
        url: '/api-inq/price/approval/getApprovalDetails',
        method: 'GET',
        params: {
          ceeaSourceNo: val.sourceNo
        },
        loading: true
      })
        .then((data) => {
          data.data.approvalBiddingItemList.map((v) => {
            this.quotaSourceDTOList.push({
              amountDifference: v.amountDifference,
              approvalBiddingItemId: v.approvalBiddingItemId,
              categoryCode: v.categoryCode,
              categoryId: v.categoryId,
              categoryName: v.categoryName,
              ceeaSourceNo: val.sourceNo,
              currency: v.currency,
              endTime: v.endTime,
              isNeedTotal: v.isNeedTotal,
              itemCode: v.itemCode,
              itemName: v.itemName,
              needNum: v.needNum,
              orgId: v.orgId,
              orgName: v.orgName,
              quotaAdjustId: v.quotaAdjustId,
              quotaProportion: v.quotaProportion,
              startTime: v.startTime,
              targetProportion: v.targetProportion,
              taxPrice: v.taxPrice,
              unit: v.unit,
              vendorName: v.vendorName
            })
          })
        })
    },

    setRowAmount (row) {
      row.amountDifference = Math.abs(row.quotaProportion - row.targetProportion) * row.taxPrice
    },

    // 新增附件
    addFileuploads () {
      this.quotaAdjustFile.push({
        fileuploadId: null,
        fileSourceName: null,
        comment: null,
        fileModular: 'inq',
        fileFunction: 'quotaAdjustFile'
      })
    },
    outerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.fileuploadId = fileId.toString()
      row.fileSourceName = fileName
    },

    submitBill () {
      this.billFetch('/api-sup-ce/ps/quotaAdjust/submit')
    },
    saveBill () {
      this.billFetch('/api-inq/inquiry/quotaAdjust/quotaAdjustAdd')
    },
    billFetch (url) {
      this.$http({
        url,
        method: 'POST',
        data: {
          quotaAdjust: this.quotaAdjust,
          quotaSourceDTOList: this.quotaSourceDTOList,
          quotaAdjustFile: this.quotaAdjustFile
        },
        loading: true
      })
        .then(data => {
          this.$message({
            message: this.$t('common.successSave'),
            type: 'success'
          })
          if (this.$attrs.params.flag == 'edit') {
            this.$emit(
              'tab-remove',
              'quotaModulationDetail' + this.$attrs.params.row.quotaAdjustCode
            )
          } else {
            this.$emit('tab-remove', 'quotaModulationDetail')
          }
          this.__setTabTodo('QuotaModulationList.getQueryData')
        })
        .catch((err) => {
          console.log(err)
        })
    }
  }
}
</script>
<style scoped lang="scss">
.the-quotaModulationDetail-detail {
  .form-container2 {
    padding: 5px;
  }

  .el-table .el-date-editor {
    width: 135px;
  }
}
</style>
