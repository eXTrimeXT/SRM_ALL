<template>
  <el-container
    class="flex-container the-shoppingCartDetail-detail"
    direction="vertical"
  >
    <el-main>
      <div class="form-container2">
        <el-form
          ref="quota"
          :disabled="isReadOnly"
          :model="quota"
          label-width="80px"
          label-position="top"
          class="form-incontainer"
          :rules="rules"
        >
          <el-collapse
            v-model="activeDims"
            class="tab-form-style"
          >
            <el-collapse-item
              :title="$t('oneStopShopping.applyNumber')"
              name="1"
            >
              <el-row>
                <el-col :span="6">
                  <el-form-item :label="$t('purchaseDemand.purchaseType')">
                    <el-input
                      v-model="quota.quotaCode"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('purchaseDemand.applyDate')">
                    <el-input v-model="quota.quotaName" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('purchaseDemand.applyStatus')">
                    <DictSelect
                      v-model="quota.quotaStatus"
                      code="currency"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('purchaseDemand.businessEntity')">
                    <el-input
                      v-model="quota.createdUserName"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('purchaseDemand.invOrg')">
                    <el-input
                      v-model="quota.createdBy"
                      disabled
                    />
                  </el-form-item>
                </el-col>

                <el-col :span="6">
                  <el-form-item :label="$t('purchaseDemand.ceeaDepartment')">
                    <el-input v-model="quota.remark" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('purchaseDemand.applicant')">
                    <el-input v-model="quota.remark" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('purchaseDemand.materialCate')">
                    <el-input v-model="quota.remark" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('purchaseDemand.projectId')">
                    <el-input v-model="quota.remark" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('purchaseDemand.projectName')">
                    <el-input v-model="quota.remark" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('purchaseDemand.projectManager')">
                    <el-input v-model="quota.remark" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('purchaseDemand.ceeaProjectApprovalNum')">
                    <el-input v-model="quota.remark" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('oneStopShopping.assetClass')">
                    <el-input v-model="quota.remark" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('oneStopShopping.ifHeadquarters')">
                    <el-input v-model="quota.remark" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('purchaseDemand.ceeaTotalBudget')">
                    <el-input v-model="quota.remark" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('dataConfMod.businessType')">
                    <el-input v-model="quota.remark" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('dataConfMod.businessLittleType')">
                    <el-input v-model="quota.remark" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="" />
                </el-col>
                <el-col :span="12">
                  <el-form-item :label="$t('common.remark')">
                    <el-input
                      v-model="quota.remark"
                      type="textarea"
                      :rows="2"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item :label="$t('vendorMod.loggerComment')">
                    <el-input
                      v-model="quota.remark"
                      type="textarea"
                      :rows="2"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item :label="$t('purchaseDemand.ceeaUrgencyExplain')">
                    <el-input
                      v-model="quota.remark"
                      type="textarea"
                      :rows="2"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-collapse-item>
            <el-collapse-item
              :title="$t('purchaseDemand.itemInfo')"
              name="2"
            >
              <el-table
                :data="quotaSourceDTOList"
                style="width: 100%"
                border
                max-height="251px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('purSettlementMod.tabindex')"
                  width="50"
                />
                <el-table-column
                  align="center"
                  prop="ceeaSourceNo"
                  :label="$t('quota.ceeaSourceNo')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="sourceType"
                  :label="$t('quota.sourceType')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="orgName"
                  :label="$t('quota.org')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="categoryName"
                  :label="$t('quota.subcategory')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="itemCode"
                  :label="$t('quota.itemCode')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="itemName"
                  :label="$t('quota.itemName')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="isNeedTotal"
                  :label="$t('quota.isNeedTotal')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="vendorCode"
                  :label="$t('common.vendorCode')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="vendorName"
                  :label="$t('common.vendorName')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="taxPrice"
                  :label="$t('quota.taxPrice')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="startTime"
                  :label="$t('quota.priceStartTime')"
                  width="100"
                  :show-overflow-tooltip="true"
                  :formatter="(row, column, cellValue) => $parseTime(cellValue)"
                />
                <el-table-column
                  align="center"
                  prop="endTime"
                  :label="$t('quota.priceEndTime')"
                  width="100"
                  :show-overflow-tooltip="true"
                  :formatter="(row, column, cellValue) => $parseTime(cellValue)"
                />
                <el-table-column
                  align="center"
                  prop="quotaProportion"
                  :label="$t('quota.quotaProportion') + '%'"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="targetProportion"
                  :label="$t('quota.targetProportion') + '%'"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="amountDifference"
                  :label="$t('quota.amountDifference')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="currency"
                  :label="$t('quota.currency')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
              </el-table>
            </el-collapse-item>
            <el-collapse-item
              :title="$t('quota.fileInfo')"
              name="4"
            >
              <p style="margin:10px 0">
                <el-button
                  type="primary"
                  @click="addFileuploads"
                >
                  {{ $t('common.add') }}
                </el-button>
              </p>
              <el-table
                :data="quotaFile"
                style="width: 100%"
                border
                max-height="251px"
              >
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
                      @on-change="({file}) => outerHandleUploadSuccess(file,scope.row)"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="createdUserName"
                  :label="$t('quota.uploadBy')"
                  width="150"
                />
                <el-table-column
                  align="center"
                  prop="creationDate"
                  :label="$t('quota.uploadDate')"
                  width="150"
                  :formatter="(row, column, cellValue) => $parseTime(cellValue)"
                />
                <el-table-column
                  align="center"
                  prop="comment"
                  :label="$t('common.remark')"
                  min-width="200"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.comment" />
                  </template>
                </el-table-column>
                <el-table-column
                  :label="$t('common.operation')"
                  width="60"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="delInvoiceTaxControls(scope.$index, scope.row)"
                    >
                      {{ $t("common.delete") }}
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
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import MainHeader from 'lib@/components/Table/MainHeader'
import CPagination from 'lib@/components/c-pagination'
import OrganizationSelector from 'lib@/components/organization-selector'

export default {
  name: 'ShoppingCartDetail',
  components: {
    MainHeader,
    CToolbar,
    QuickSearch,
    CPagination,
    OrganizationSelector
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'vendorBiddingManagement',
        fileType: 'images'
      },
      queryTotal: -1,
      viewSize: 10,
      viewIndex: 1,
      quota: {
        quotaId: null,
        quotaCode: null,
        quotaName: null,
        ceeaSourceNo: null,
        createdBy: null,
        createdUserName: '',
        creationDate: null,
        adjustType: null,
        status: 'DRAFT',
        remark: null
      },
      dialogFormVisible: false,
      quotaSourceDTOList: [],
      itemList7: [],
      quotaFile: [],
      activeDims: ['1', '2', '3', '4'],
      rules: {
        vendorCode: [{ required: true, message: this.$t('quota.vendorTips') }], // 请选择供应商
        orgId: [{ required: true, message: this.$t('quota.orgIdTips') }], // 请选择业务实体
        businessType: [{ required: true, message: this.$t('quota.businessTypeTips') }], // 请选择业务类型
        costType: [{ required: true, message: this.$t('quota.costTypeTips') }], // 请选择成本类型
        ifPaperAttach: [{ required: true, message: this.$t('quota.ifPaperAttachTips') }], // 请选择是否纸质附件
        applyDeptName: [{ required: true, message: this.$t('quota.applyDeptNameTips') }]// 请填写申请部门
      },
      isReadOnly: this.$attrs.params.flag == 'isReadOnly',
      formLabelWidth: '120px',
      isModify: false
    }
  },
  created () {
    if (this.$attrs.params.flag == 'add') {
      if (this.$store.getters.userInfo) {
        this.quota.applyUserNickname = this.$store.getters.userInfo.nickname
        this.quota.applyDeptName = this.$store.getters.userInfo.department
      }
    }
    if (
      this.$attrs.params.flag == 'edit' ||
      this.$attrs.params.flag == 'isReadOnly'
    ) {
      this.getFormDetail()
    }
  },
  methods: {
    getFormDetail () {
      this.$http({
        url: '/api-inq/inquiry/quota/getquota',
        method: 'GET',
        params: { id: this.$attrs.params.row.quotaId },
        loading: true
      })
        .then((data) => {
          if (data && data.data) {
            this.quota = data.data.quota
            this.quotaFile = data.data.quotaFile
            this.quotaSourceDTOList = data.data.quotaSourceDTOList
          }
        })
        .catch((err) => {
          console.log(err)
        })
    },
    getCategoryObj (val, scope) {
      scope.sourceType = val ? val.sourceType : ''
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
              ceeaSourceNo: val.sourceNo, //
              currency: v.currency,
              endTime: v.endTime,
              isNeedTotal: v.isNeedTotal,
              itemCode: v.itemCode,
              itemName: v.itemName,
              needNum: v.needNum,
              orgId: v.orgId,
              orgName: v.orgName,
              quotaId: v.quotaId,
              quotaProportion: v.quotaProportion,
              sourceType: val.sourceType, //
              startTime: v.startTime,
              targetProportion: v.targetProportion,
              taxPrice: v.taxPrice,
              unit: v.unit,
              vendorName: v.vendorName
            })
          })
          /* for(let i of this.quotaSourceDTOList) {
            i['ceeaSourceNo'] = val.sourceNo;
            i['sourceType'] = val.sourceType;
          } */
        })
        .catch((err) => {
          console.log(err)
        })
    },
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
    },
    submitBill () {
      this.$refs.quota.validate((valid) => {
        if (valid) {
          this.$http({
            url: '/api-sup-ce/ps/quota/submit',
            method: 'POST',
            data: {
              quota: this.quota,
              quotaSourceDTOList: this.quotaSourceDTOList,
              quotaFile: this.quotaFile
            },
            loading: true
          })
            .then((data) => {
              this.$message({
                message: this.$t('common.success'),
                type: 'success'
              })
              if (this.$attrs.params.flag == 'edit') {
                this.$emit(
                  'tab-remove',
                  'shoppingCartDetail' + this.$attrs.params.row.quotaCode
                )
              } else {
                this.$emit('tab-remove', 'shoppingCartDetail')
              }
              this.__setTabTodo('shoppingCartList.getQuerydata')
            })
            .catch((err) => {
              console.log(err)
            })
        } else {
          return false
        }
      })
    },
    saveBill () {
      const params = {
        quota: this.quota,
        quotaSourceDTOList: this.quotaSourceDTOList,
        quotaFile: this.quotaFile
      }

      this.$refs.quota.validate((valid) => {
        if (valid) {
          this.$http({
            url: '/api-inq/inquiry/quota/quotaAdd',
            method: 'POST',
            data: params,
            loading: true
          })
            .then((data) => {
              this.$message({
                message: this.$t('common.success'),
                type: 'success'
              })
              if (this.$attrs.params.flag == 'edit') {
                this.$emit(
                  'tab-remove',
                  'shoppingCartDetail' + this.$attrs.params.row.quotaCode
                )
              } else {
                this.$emit('tab-remove', 'shoppingCartDetail')
              }
              this.__setTabTodo('shoppingCartList.getQuerydata')
            })
            .catch((err) => {
              console.log(err)
            })
        } else {
          return false
        }
      })
    },
    outerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.fileuploadId = fileId.toString()
      row.fileSourceName = fileName
    },
    handleDelClick (index, row) {
      this.quotaFile.splice(index, 1)
    },
    // 新增file
    addFileuploads () {
      this.quotaFile.push({
        fileuploadId: null,
        fileSourceName: null,
        comment: null,
        fileModular: 'inq',
        fileFunction: 'quotaFile'
      })
    }
  }
}
</script>
<style scoped lang="scss">
.the-shoppingCartDetail-detail {
  .form-container2 {
    padding: 5px;
  }
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
  .btn_line {
    margin: 0;
  }
}
</style>
