<template>
  <el-container class="flex-container toolinginfo_list_wrapper" direction="vertical">
    <el-main>
      <div class="form-container" style="overflow-y:scroll">
        <el-form ref="form" :model="form" :rules="rules" :disabled="false">
          <srm-row :gutter="32">
            <srm-col :init-col="4">
              <!-- 车型编码 -->
              <el-form-item :label="$t('problemManagement.motorcycleTypeCode')" prop="modelCode">
                <el-input
                  v-model="form.modelCode"
                  :disabled="isEdit||noEdit||extraData.outputConfirmId !==''"
                />
              </el-form-item>
            </srm-col>
            <srm-col :init-col="4">
              <!-- 车型名称 -->
              <el-form-item :label="$t('problemManagement.motorcycleTypeName')">
                <el-input
                  v-model="form.modelName"
                  :disabled="isEdit||noEdit||extraData.outputConfirmId !==''"
                />
              </el-form-item>
            </srm-col>
            <srm-col :init-col="4">
              <!-- 状态 -->
              <el-form-item :label="$t('components.stratProcess.headers.docStatusValue')">
                <dict-select v-model="form.status" code="OUTPUT_CONFIRM_STATUS" disabled />
              </el-form-item>
            </srm-col>
            <srm-col :init-col="4">
              <!-- 认可清单编号 -->
              <el-form-item :label="$t('productionPrepare.outputConfirmCode')">
                <el-input v-model="form.outputConfirmId" :disabled="true" />
              </el-form-item>
            </srm-col>
            <srm-col :init-col="4">
              <!-- 创建人 -->
              <el-form-item :label="$t('common.creator')">
                <el-input v-model="form.createdBy" :disabled="true" />
              </el-form-item>
            </srm-col>
            <srm-col :init-col="4">
              <!-- 创建时间 -->
              <el-form-item :label="$t('common.creationTime')">
                <el-date-picker
                  v-model="form.creationDate"
                  :format="$formatDatePickerTime"
                  value-format="yyyy-MM-dd HH:mm:ss"
                  disabled
                />
              </el-form-item>
            </srm-col>
          </srm-row>
          <srm-row>
            <srm-col :init-col="1">
              <!-- 简述 -->
              <!-- 请输入内容 -->
              <el-form-item :label="$t('vendorMod.relegation.sketch')">
                <el-input
                  v-model="form.comments"
                  type="textarea"
                  :rows="3"
                  maxlength="500"
                  :placeholder="$t('common.pleaseTypeContents')"
                  show-word-limit
                  :disabled="noEdit"
                />
              </el-form-item>
            </srm-col>
          </srm-row>
        </el-form>
        <MainHeader :l-span="22" :r-span="2">
          <template slot="left">
            <!-- 暂存单据后可导入 -->
            <el-tooltip :content="$t('productionPrepare.productionApprovalTip1')" :disabled="!!extraData.outputConfirmId">
              <!-- 导入零件 -->
              <MImport
                v-if="!noEdit"
                style="display: inline-block;"
                :title="$t('productionPrepare.productionApprovalTitle1')"
                up-load-url="/api-ppap/outputConfirm/importExcel"
                :extra-data="extraData"
                :disabled="!extraData.outputConfirmId"
                @downloadTemplate="downloadItemTemplate"
                @handleSuccess="handleSuccess"
              />
            </el-tooltip>

            <ExportExcel
              :custom-title="$t('common.export')"
              page-url="/api-ppap/outputConfirm/getInventoryById"
              :filter-params="queryParam"
              :table-header="tableHeader"
              export-mode="front"
              type="ghost"
              :dict-codes="dictCodes"
            />
            <AuthorityButton v-if="!noEdit" type="ghost" @click="getResult">
              <!-- 获取认可结果 -->
              {{ $t("productionPrepare.productionApprovalTitle2") }}
            </AuthorityButton>
          </template>
        </MainHeader>
        <TableView
          ref="list"
          :table-height="tableHeight"
          :table-header="tableHeader"
          :page-size="pageSize"
          :pre-query-data="queryParam"
          :open-custom-table="true"
          :post-query-data="postQueryData"
          :checkbox="false"
          url="/api-ppap/outputConfirm/getInventoryById"
        />
        <div style="height:50px" />
        <CToolbar>
          <template #right>
            <el-button @click="cancelBill">
              <!-- 取消 -->
              {{ $t("components.common.cancel") }}
            </el-button>
            <el-button v-if="!noEdit" type="primary" @click="tempSave">
              <!-- 暂存 -->
              {{ $t("common.staging") }}
            </el-button>
            <el-button v-if="!noEdit" type="primary" size="mini" @click="sendGIO">
              <!-- 提交审批 -->
              {{ $t("bidMod.submitApproval") }}
            </el-button>
          </template>
        </CToolbar>
      </div>
    </el-main>
  </el-container>
</template>
<script>
import http from '@/utils/axios/http'
import { tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import CToolbar from 'lib@/components/c-toolbar'
import MImport from 'lib@/components/import'
import QuickSearch from 'lib@/components/QuickSearch'
import { downloadFileLinkByPost } from 'lib@/utils/file'
import ExportExcel from 'lib@/components/export-excel'
import { productionApproval } from 'modb@/productionPrepare/api'

export default {
  name: 'ProductionApprovalEdit',
  components: {
    TableView,
    MainHeader,
    CToolbar,
    MImport,
    QuickSearch,
    ExportExcel
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      form: {
        modelCode: '',
        modelName: '',
        status: 'DART',
        outputConfirmId: '',
        createdBy: '',
        creationDate: '',
        comments: ''
      },
      rules: {
        modelCode: [{ required: true, message: this.$t('productionPrepare.enterVehicleTypeCode') }]  // '请输入车型编码'
      },
      tableHeight: '400px',
      dictCodes: {

      },
      isEdit: false,
      pageSize: 15,
      tableHeader: [
        {
          prop: 'toolingCode',
          label: this.$t('productionPrepare.toolingCode'),  // '零件编号'
          minWidth: 210
        },
        {
          prop: 'toolingName',
          label: this.$t('marketBudget.partName'),  // '零件名称'
          minWidth: 210
        },
        {
          prop: 'vendorCode',
          label: this.$t('common.vendorCode'),  // '供应商编码'
          minWidth: 210
        },
        {
          prop: 'vendorName',
          label: this.$t('common.companyName'),  // '供应商名称'
          minWidth: 210
        },
        {
          prop: 'purchaseArea',
          label: this.$t('productionPrepare.purchaseArea'),  // '采购领域认可结果'
          minWidth: 210
        },
        {
          prop: 'qualityArea',
          label: this.$t('productionPrepare.qualityArea'),  // '质量领域认可结果'
          minWidth: 210
        },
        {
          prop: 'logisticsArea',
          label: this.$t('productionPrepare.logisticsArea'),  // '物流领域认可结果'
          minWidth: 210
        },
        {
          prop: 'comprehensiveArea',
          label: this.$t('productionPrepare.comprehensiveArea'), // '综合结果'
          minWidth: 210
        },
        {
          prop: 'remarks',
          label: this.$t('components.eio.headers.remark'),  //  '备注'
          minWidth: 210
        },
        {
          label: this.$t('components.headers.operation'),  // '操作'
          minWidth: 100,
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          buttons: [
            {
              formattor: () => this.$t('components.common.delete'),  // '删除'
              callback: row => { this.deleteHandle(row) },
              show: row => !this.noEdit
            }

          ]
        }
      ],
      queryParam: {
        outputConfirmId: ''
      },
      postQueryData: {},
      extraData: {
        fileModular: 'suplier',
        fileFunction: 'accountAccess',
        fileType: 'excel',
        outputConfirmId: ''
      },
      noEdit: false
    }
  },
  mounted () {
    this.$nextTick(() => {
      if (this.$attrs.params.flag === 'edit') {
        this.form = this.$attrs.params.form
        this.getDetail(this.form.outputConfirmId)
        this.isEdit = true
        this.extraData.outputConfirmId = this.form.outputConfirmId
      } else if (this.$attrs.params.flag === 'read') {
        this.noEdit = true
        this.form = this.$attrs.params.form
        this.extraData.outputConfirmId = this.form.outputConfirmId
        this.getDetail(this.$attrs.params.id)
      }
    })
  },
  methods: {
    getDetail (outputConfirmId) {
      const params = {
        outputConfirmId
      }
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },
    downloadItemTemplate () {
      downloadFileLinkByPost(
        '/api-ppap/outputConfirm/importModelDownload',
        // '量产认可零件导入模板.xlsx'
        this.$t('cusEntry.supplement20250211.massProductionApprovalPartsTemplate')
      ).catch(() => {
        // '下载失败'
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    handleSuccess (res) {
      // '导入成功'
      this.$message.success(this.$t('components.eio.importSuccess'))
      this.getDetail(this.extraData.outputConfirmId)
    },
    getResult () {
      if (this.$refs.list.tableData.length === 0) {
        // '请先导入零件信息'
        this.$message.warning(this.$t('productionPrepare.productionApprovalTip5'))
        return false
      }
      const params = {
        outputConfirmId: this.form.outputConfirmId
      }
      http({
        url: '/api-ppap/outputConfirm/getResult',
        method: 'POST',
        loading: true,
        data: params
      }).then(res => {
        console.log(res)
        if (res.code === '0') {
          this.getDetail(this.extraData.outputConfirmId)
        }
      })
    },
    deleteHandle (row) {
      let data = {
        outputConfirmDetailId: row.outputConfirmDetailId
      }
      productionApproval.deletePartDetail(data).then(res => {
        if (res.code === '0') {
          // '删除成功'
          this.$message.success(this.$t('common.successDelete'))
          this.getDetail(this.form.outputConfirmId)
        }
      })
    },
    tempSave () {
      this.$refs.form.validate(async valid => {
        if (valid) {
          const params = {
            ...this.form
          }
          http({
            url: '/api-ppap/outputConfirm/tempSave',
            method: 'POST',
            loading: true,
            data: params
          }).then(res => {
            if (res.code === '0') {
              this.extraData.outputConfirmId = res.data.data.outputConfirmId
              this.form = res.data.data
              this.getDetail(this.extraData.outputConfirmId)
              // '操作成功'
              this.$message.success(this.$t('components.approvalHead.tips.approvalCompletion'))
            } else {
              this.$message.error(res.message)
            }
          })
        }
      })
    },
    async sendGIO () {
      if (this.$refs.list.tableData.length === 0) {
        // '请先导入零件信息'
        this.$message.warning(this.$t('productionPrepare.productionApprovalTip5'))
        return false
      }
      let ngArr = this.$refs.list.tableData.filter(item => {
        return item.purchaseArea === '' || item.qualityArea === '' || item.logisticsArea === ''
      })
      if (ngArr.length > 0) {
        let msg = ngArr.map(item => {
          return item.toolingCode
        }).join(',')
        let pFn1 = function () {
          return new Promise((resolve) => {
            // this.$confirm(`零件${msg}认可结果未填充完整，是否确定提交GIO审批`, '提示', {
            this.$confirm(`${this.$t('productionPrepare.tooling')}${msg}${this.$t('cusEntry.supplement20250211.recognizeResultNotFilledCompletelySubmitGIOApproval')}`, this.$t('components.approvalHead.tips.tip'), {
              confirmButtonText: this.$t('common.confirm'),
              cancelButtonText: this.$t('components.common.cancel'),
              type: 'warning'
            }).then(() => {
              resolve(true)
            }).catch(() => {
              resolve(false)
            })
          })
        }.bind(this)

        let go = await pFn1()
        if (!go) {
          return false
        }
      }
      const params = {
        ...this.form,
        outputDetailList: this.$refs.list.tableData
      }

      http({
        url: '/api-ppap/outputConfirm/tempSave',
        method: 'POST',
        loading: true,
        data: params
      }).then(res => {
        this.$refs.list.tableData = res.data.data.outputDetailList|| []
        this.$nextTick(() => {
          http({
            url: '/api-ppap/outputConfirm/sendGIO',
            method: 'POST',
            loading: true,
            data: params
          }).then(res => {
            if (res.code === '0') {
              this.$message.success(res.message)
              this.cancelBill()
            }
          })
        })
      })
    },
    cancelBill () {
      let { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
      this.__setTabTodo('productionApprovalList.reloadData')
    }
  }
}
</script>
