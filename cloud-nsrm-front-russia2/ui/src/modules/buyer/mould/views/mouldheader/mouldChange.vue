<template>
  <el-container class="mouldScrapEdit" direction="vertical">
    <el-main>
      <CWorkflowMulti
        ref="workflowMulti"
        v-model="activeTabName"
        :fun-params="workflowParamsInfo"
        :button-config-info="buttonConfigInfo"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @click-handler="type => save(type)"
        @submit-direct="type => save(type)"
        @confirm="(type, comment) => save(type, comment)"
        @close-tab="back"
      >
        <div class="form-container">
          <el-form ref="form" :model="form" :rules="rules">
            <el-collapse v-model="activeDims" class="tab-form-style">
              <!--基本信息-->
              <el-collapse-item :title="$t('vendorMod.basicInformation')" name="1">
                <srm-row>
                  <srm-col :initCol="4">
                    <el-form-item prop="mouldFlowCode" :label="$t('flowMod.documentNo')">
                      <el-input v-model="form.mouldFlowCode" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item
                      prop="flowBusinessType"
                      :label="$t('components.flownode.processValue')"
                    >
                      <el-input v-model="form.flowBusinessType" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item prop="createdBy" :label="$t('purchaseDemand.createdBy1')">
                      <el-input v-model="form.createdBy" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item prop="creationDate" :label="$t('common.creationTime')">
                      <el-date-picker
                        v-model="form.creationDate"
                        type="date"
                        :format="$formatDatePicker"
                        value-format="yyyy-MM-dd"
                        disabled
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.mouldShiftTime')" prop="scrapDate">
                      <el-date-picker
                        v-model="form.changeDate"
                        type="date"
                        :format="$formatDatePicker"
                        value-format="yyyy-MM-dd"
                        :placeholder="$t('vendorMod.datePicker')"
                        :disabled="readOnly"
                      />
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-collapse-item>

              <!--转移信息-->
              <el-collapse-item
                ref="transinformation"
                :title="$t('mould.transinformation')"
                name="2"
              >
                <el-container class="flex-container" style="height: 300px;">
                  <el-main>
                    <div style="padding: 12px 0;">
                      <el-button
                        class="detail-pbtn"
                        type="primary"
                        :disabled="readOnly"
                        @click="addLine"
                      >
                        {{ $t('common.new') }}
                      </el-button>
                    </div>
                    <BaseTable
                      ref="table"
                      :columns="columns"
                      :data-source="dataSource"
                      :initialize="false"
                      row-key="mouldHeaderId"
                      border
                      @asyncGetRealDataSource="asyncGetRealDataSource"
                    >
                      <template #changeEntity="{ scope }">
                        <DictSelect v-model="scope.row.changeEntity" code="STORAGE_ENTITY" :disabled="readOnly" />
                      </template>
                      <template #changeSupplierCode="{ scope }">
                        <QuickSearch
                          :show-input="scope.row.changeSupplierCode"
                          show-key="changeSupplierCode"
                          :scope-data="scope"
                          :disabled="readOnly || scope.row.changeEntity === '1'"
                          name="scc_sup_company_info"
                          @close-quicksearch="value => getSupplyObj(value,scope)"
                        />
                      </template>
                      <template #changeSupplierName="{ scope }">
                        <el-input v-model="scope.row.changeSupplierName" disabled />
                      </template>
                      <template #changeSupplierResponerName="{ scope }">
                        <QuickSearch
                          :show-input="scope.row.changeSupplierResponerName"
                          show-key="changeSupplierResponerName"
                          :scope-data="scope"
                          name="scc_rbac_user_vendor_display"
                          :disabled="readOnly"
                          @close-quicksearch="value => getSupplierUserObj(value,scope)"
                        />
                      </template>
                      <template #changeStorageAddress="{ scope }">
                        <el-input v-model="scope.row.changeStorageAddress" :disabled="readOnly" />
                      </template>
                      <template #mouldCode="{ scope }">
                        <el-input v-model="scope.row.mouldCode" disabled />
                      </template>
                      <template #mouldName="{ scope }">
                        <QuickSearch
                          :show-input="scope.row.mouldName"
                          show-key="mouldName"
                          :scope-data="scope"
                          name="scc_sc_mould_header"
                          :disabled="readOnly || !scope.row.mouldEditableFlag"
                          @close-quicksearch="value => getMouldName(value,scope)"
                        />
                      </template>
                      <template #orgName="{ scope }">
                        <el-input v-model="scope.row.orgName" disabled />
                      </template>
                      <template #itemNumber="{ scope }">
                        <el-input v-model="scope.row.itemNumber" disabled />
                      </template>
                      <template #itemDescZhs="{ scope }">
                        <el-input v-model="scope.row.itemDescZhs" disabled />
                      </template>
                      <template #mouldTypeCode="{ scope }">
                        <DictSelect v-model="scope.row.mouldTypeCode" code="MOULD_TYPE" disabled />
                      </template>
                      <template #mouldStatusCode="{ scope }">
                        <DictSelect
                          v-model="scope.row.mouldStatusCode"
                          code="MOULD_STATUS"
                          disabled
                        />
                      </template>
                      <template #storageEntity="{ scope }">
                        <DictSelect
                          v-model="scope.row.storageEntity"
                          code="STORAGE_ENTITY"
                          disabled
                        />
                      </template>
                      <template #supplierCode="{ scope }">
                        <el-input v-model="scope.row.supplierCode" disabled />
                      </template>
                      <template #usedPercent="{ scope }">
                        <el-input v-model="scope.row.usedPercent" disabled />
                      </template>
                      <template #sharedPercent="{ scope }">
                        <el-input v-model="scope.row.sharedPercent" disabled />
                      </template>
                    </BaseTable>
                  </el-main>
                </el-container>
              </el-collapse-item>

              <!--说明-->
              <el-collapse-item :title="$t('vendorMod.operationMemo')" name="3">
                <srm-row>
                  <srm-col :initCol="1">
                    <el-form-item prop="explanation">
                      <el-input v-model="form.explanation" type="textarea" :disabled="readOnly" />
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-collapse-item>

              <el-collapse-item :title="$t('dataConfMod.attachment')" name="4">
                <div class="btn_line" style="margin-bottom: 10px;">
                  <el-button
                    type="primary"
                    class="detail-pbtn"
                    :disabled="readOnly"
                    @click="addFile"
                  >
                    {{ $t("common.add") }}
                  </el-button>
                </div>
                <el-table :data="attachmentList" style="width: 100%" border max-height="200">
                  <el-table-column
                    align="center"
                    type="index"
                    :label="$t('purSettlementMod.tabindex')"
                    width="50"
                  />
                  <el-table-column align="center" prop="attachName" :label="$t('bidMod.fileName')">
                    <template slot-scope="scope">
                      <SrmCommonFile
                        :extra-data="fileInfo"
                        :default-file="{
                          fileId: scope.row.fileuploadId,
                          fileName: scope.row.fileSourceName
                        }"
                        :readonly="false"
                        @on-change="({file}) => HandleUploadSuccess(file,scope.row)"
                      />
                    </template>
                  </el-table-column>

                  <el-table-column :label="$t('common.operation')" width="100">
                    <template slot-scope="scope">
                      <el-button
                        v-if="!readOnly"
                        type="text"
                        @click="deleteOneContent3(scope.$index, scope.row)"
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
      </CWorkflowMulti>
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoMixin } from '@/utils/mixins'
import { numberToPercent, percentToNumber } from '@/library/utils/number'
import { EDITABLE_KEY } from '@/library/components/BaseTable/utils'
import { downloadFileLink } from 'lib@/utils/file'
import BaseTable from 'lib@/components/BaseTable/baseTable'
import QuickSearch from '@/library/components/QuickSearch'
import WorkflowCommon from '@/library/mixins/workflow-common'
import { mouldheader } from 'modb@/mould/api'
export default {
  name: 'MouldChange',
  components: {
    BaseTable,
    QuickSearch
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      activeDims: ['1', '2', '3', '4'],
      fileInfo: {
        fileModular: 'workFlow',
        fileFunction: 'workflowReport',
        fileType: 'images'
      },
      attachmentList: [],
      realDataSource: [], // 明细表数据
      orgList: [], // 业务实体
      dataSource: [],
      columns: [],
      disabledButton: false,
      form: {
        changeDate: null,
        scrapDate: null,
        mouldHeaderId: null,
        mouldFlowLogId: null,
        flowBusinessType: this.$t('mould.transfer'),
        approveStatus: 'DRAFT',
        explanation: null,
        createdBy: null,
        creationDate: null
      },
      rules: {},
      readOnly: false
    }
  },
  computed: {
    viewUpdateButton () {
      return (
        !this.readOnly &&
        this.form.approveStatus !== 'APPROVED'
      )
    },
    disabledUpdateButton () {
      return (
        this.form.approveStatus === 'SUBMITTED' ||
        this.form.approveStatus === 'APPROVING'
      )
    },
    workflowBusinessId () {
      return this.form
        ? this.form.mouldFlowLogId
        : null
    },
    workflowTabDisabled () {
      return this.form.approveStatus === 'DRAFT'
    }
  },
  watch: {
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
    }
  },
  created () {
    this.buttonConfigInfo.save.view = this.viewUpdateButton
    this.buttonConfigInfo.submit.view = this.viewUpdateButton
    this.buttonConfigInfo.cancel.view = !this.readOnly
    this.buttonConfigInfo.close.view = this.readOnly
  },
  mounted () {
    const _this = this
    this.columns =
      [
        {
          attrs: {
            prop: 'mouldHeaderId',
            label: this.$t('mould.mouldHeaderId')
          },
          slot: 'mouldHeaderId',
          hidden: true
        },
        {
          attrs: {
            prop: 'changeEntity',
            label: this.$t('mould.changeEntity'),
            formatter: value => _this.$getDictLabel('STORAGE_ENTITY', value)
          },
          slot: 'changeEntity',
          rules: { required: true, message: this.$t('priceModel.priceModel.requiredFlag') }
        },
        {
          attrs: {
            prop: 'changeSupplierCode',
            label: this.$t('mould.changeSupplierCode')
          },
          slot: 'changeSupplierCode'
        },
        {
          attrs: {
            prop: 'changeSupplierName',
            label: this.$t('mould.changeSupplierName')
          },
          slot: 'changeSupplierName'
        },
        {
          attrs: {
            prop: 'changeSupplierResponerName',
            label: this.$t('mould.changeSupplierResponerName')
          },
          slot: 'changeSupplierResponerName'
        },
        {
          attrs: {
            prop: 'changeStorageAddress',
            label: this.$t('mould.changeStorageAddress')
          },
          slot: 'changeStorageAddress',
          rules: { required: true, message: this.$t('dataConfMod.required') }
        },
        {
          attrs: {
            prop: 'mouldCode',
            label: this.$t('mould.mouldNumber')
          },
          slot: 'mouldCode'
        },
        {
          attrs: {
            prop: 'mouldName',
            label: this.$t('mould.mouldName')
          },
          slot: 'mouldName'
        },
        {
          attrs: {
            prop: 'orgName',
            label: this.$t('oneStopShopping.businessEntity')
          },
          slot: 'orgName'
        },
        {
          attrs: {
            prop: 'itemNumber',
            label: this.$t('supplierCapacityReport.materialCode')
          },
          slot: 'itemNumber'
        },
        {
          attrs: {
            prop: 'itemDescZhs',
            label: this.$t('supplierCapacityReport.materialName')
          },
          slot: 'itemDescZhs'
        },
        {
          attrs: {
            prop: 'mouldTypeCode',
            label: this.$t('mould.mouldType'),
            formatter: value => _this.$getDictLabel('MOULD_TYPE', value)
          },
          slot: 'mouldTypeCode'
        },
        {
          attrs: {
            prop: 'mouldStatusCode',
            label: this.$t('mould.mouldStatus'),
            formatter: value => _this.$getDictLabel('MOULD_STATUS', value)
          },
          slot: 'mouldStatusCode'
        },
        {
          attrs: {
            prop: 'storageEntity',
            label: this.$t('mould.storageEntity'),
            formatter: value => _this.$getDictLabel('STORAGE_ENTITY', value)
          },
          slot: 'storageEntity'
        },
        {
          attrs: {
            prop: 'supplierCode',
            label: this.$t('supplierCapacityReport.vendorCode')
          },
          slot: 'supplierCode'
        },
        {
          attrs: {
            prop: 'usedPercent',
            label: this.$t('mould.usedPercent')
            // formatter: value => numberToPercent(value)
          },
          slot: 'usedPercent'
        },
        {
          attrs: {
            prop: 'sharedPercent',
            label: this.$t('mould.sharedPercent')
            // formatter: value => numberToPercent(value)
          },
          slot: 'sharedPercent'
        },
        {
          attrs: {
            prop: 'operation',
            label: this.$t('components.headers.operation'),
            width: 100,
            fixed: 'right'
          },
          operations: [
            {
              event: 'deleteItem',
              name: this.$t('common.delete'),
              func: this.deleteItem,
              show: () => !this.readOnly
            }
          ]
        }
      ]
    const { flag, dataList, mouldFlowLogId } = this.$attrs.params
    this.readOnly = flag === 'view'
    if (flag === 'change' || flag === 'view') {
      this.getDetail(dataList, mouldFlowLogId)
    }
  },
  methods: {
    getMouldName (val, scope) {
      this.$set(this.$refs.table.form.dataSource[scope.$index], 'mouldHeaderId', val.mouldHeaderId)
      this.$set(this.$refs.table.form.dataSource[scope.$index], 'mouldCode', val.mouldCode)
      this.$set(this.$refs.table.form.dataSource[scope.$index], 'mouldName', val.mouldName)
      this.$set(this.$refs.table.form.dataSource[scope.$index], 'orgName', val.orgName)
      this.$set(this.$refs.table.form.dataSource[scope.$index], 'itemNumber', val.itemNumber)
      this.$set(this.$refs.table.form.dataSource[scope.$index], 'itemDescZhs', val.itemDescZhs)
      this.$set(this.$refs.table.form.dataSource[scope.$index], 'mouldTypeCode', val.mouldTypeCode)
      this.$set(this.$refs.table.form.dataSource[scope.$index], 'mouldStatusCode', val.mouldStatusCode)
      this.$set(this.$refs.table.form.dataSource[scope.$index], 'storageEntity', val.storageEntity)
      this.$set(this.$refs.table.form.dataSource[scope.$index], 'supplierCode', val.supplierCode)
      this.$set(this.$refs.table.form.dataSource[scope.$index], 'usedPercent', val.usedPercent)
      this.$set(this.$refs.table.form.dataSource[scope.$index], 'sharedPercent', val.sharedPercent)
    },
    getSupplierUserObj (val, scope) {
      this.$set(this.$refs.table.form.dataSource[scope.$index], 'changeSupplierResponerId', val.userId)
      this.$set(this.$refs.table.form.dataSource[scope.$index], 'changeSupplierResponerName', val.username)
      this.$set(this.$refs.table.form.dataSource[scope.$index], 'changeSupplierResponerEmail', val.email)
    },
    getSupplyObj (val, scope) {
      // base-table 设值
      this.$set(this.$refs.table.form.dataSource[scope.$index], 'changeSupplierId', val.companyId)
      this.$set(this.$refs.table.form.dataSource[scope.$index], 'changeSupplierCode', val.companyCode)
      this.$set(this.$refs.table.form.dataSource[scope.$index], 'changeSupplierName', val.companyName)
    },
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'MOULDCHANGE'
    },
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    back () {
      if (this.$attrs.params.flag === 'change') {
        this.$emit('tab-remove', 'mouldChange')
      } else {
        this.$emit('tab-remove', this.$attrs.params.tabName)
      }
      this.__setTabTodo('mouldheaderList.getQuerydata')
    },
    addFile () {
      this.attachmentList.push({
        fileuploadId: null,
        fileSourceName: '',
        fileFunction: 'mouldAttachment' // 文件所属功能
      })
    },
    async getDetail (dataList, flowId) {
      if (flowId) {
        await mouldheader.getChangeInfoByFlowId(flowId).then(res => {
          const { mouldChangeLineList, mouldAttachList, ...rest } = res.data
          this.form = rest
          this.dataSource = mouldChangeLineList
          this.attachmentList = mouldAttachList
          this.dataSource.forEach(item => {
            item[EDITABLE_KEY] = true
            item.usedPercent = numberToPercent(item.usedPercent)
            item.sharedPercent = numberToPercent(item.sharedPercent)
          })
        })
      } else {
        dataList.forEach(item => {
          item[EDITABLE_KEY] = true
          item.usedPercent = numberToPercent(item.usedPercent)
          item.sharedPercent = numberToPercent(item.sharedPercent)
        })
        this.dataSource = dataList
      }
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-sup/sup/mouldheader/exportMouldLineExcelTemplate',
        this.$t('mould.importTemplate')
      ).catch(() => {
        this.$message.error(this.$t('purchaseDemand.downloadFail'))
      })
    },
    handleSuccess () {
      this.getDetail()
    },
    async save (type) {
      this.disabledButton = true
      setTimeout(() => {
        this.disabledButton = false
      }, 1000)
      if (type === 'SUBMIT') {
        var confirmSelectValue = await this.$confirm(
          this.$t('mould.goToFlow'),
          this.$t('common.tips'),
          {
            confirmButtonText: this.$t('common.confirm'),
            cancelButtonText: this.$t('common.cancel'),
            type: 'warning'
          }
        )
        if (confirmSelectValue !== 'confirm') {
          // 非确认则返回
          return
        }
      }
      this.saveFunction(type)
    },
    saveFunction (type) {
      this.$refs.form.validate(result => {
        this.$refs.table.validate(res => {
          if (result && res) {
            this.realDataSource.forEach(item => {
              item.usedPercent = percentToNumber(item.usedPercent)
              item.sharedPercent = percentToNumber(item.sharedPercent)
            })
            const data = {
              ...this.form,
              mouldChangeLineList: this.realDataSource,
              mouldAttachList: this.attachmentList
            }
            if (this.form.mouldFlowLogId) {
              mouldheader.updateChangeMoulds(data).then(async res => {
                this.$message({
                  type: 'success',
                  message: res.message
                })
                await this.getDetail(null, res.data)
                await this.handlerAfter(type)
                // this.cancelBill();
              })
            } else {
              mouldheader.saveChangeMoulds(data).then(async res => {
                this.$message({
                  type: 'success',
                  message: res.message
                })
                await this.getDetail(null, res.data)
                await this.handlerAfter(type)
                // this.cancelBill();
              })
            }
          } else {
            this.__focus_error__()
          }
        })
      })
    },
    asyncGetRealDataSource (data) {
      this.realDataSource = data
    },
    addLine () {
      this.$refs.table.add({ mouldEditableFlag: true })
    },
    deleteItem (scope, data) {
      data.splice(scope.$index, 1)
    },
    cancelBill () {
      this.$emit('tab-remove', 'mouldChangeEdit')
      this.__setTabTodo('mouldheaderList.getQuerydata')
    },
    // 上传附件成功
    HandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.fileuploadId = fileId.toString()
      row.fileSourceName = fileName
    },
    deleteOneContent3 (index, row) {
      if (row.fileuploadId) {
        mouldheader.fileuploadDelete({ id: row.fileuploadId }).then(res => {
          this.attachmentList.splice(index, 1)
        })
      }
    }
  }
}
</script>

<style scoped lang="scss">
.mouldScrapEdit {
  height: 100%;
  padding-bottom: 50px;

  :deep(.table-wrapper) {
    padding-left: 0;
    padding-right: 0;
  }

  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }

  .el-table .el-date-editor {
    width: 135px;
  }

  .base-form {
    padding: 15px 30px 0;
  }

  .toRequired {
    color: #ff4949;
    padding-right: 2px;
  }

  .edit_cond {
    color: #23adf4;
    cursor: pointer;
  }
}
</style>
