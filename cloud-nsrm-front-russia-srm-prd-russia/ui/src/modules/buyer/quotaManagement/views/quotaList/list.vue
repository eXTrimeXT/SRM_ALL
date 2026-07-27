<template>
  <el-container class="flex-container quotaList_wrapper" direction="vertical">
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <!-- 新增 -->
          <AuthorityButton type="primary" @click="addHandle">
            {{ $t('common.add') }}
          </AuthorityButton>
          <!-- 导入 -->
          <MImport
            type="default"
            :title="$t('common.import')"
            up-load-url="/api-sup/sup/quotadetail/importExcel"
            :extra-data="extraData"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
          <!-- 配额计算 -->
          <AuthorityButton @click="quotaCalculation">
            {{ $t('quota.quotaCal') }}
          </AuthorityButton>
          <!-- 生产配额清单 -->
          <AuthorityButton @click="quotaList">
            {{ $t('quota.generatingQuotaList') }}
          </AuthorityButton>
          <!-- 导出 -->
          <ExportExcel
            type="default"
            page-url="/api-sup/sup/quotadetail/listPage"
            :filter-params="queryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            :title="$t('common.export')"
            export-mode="front"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :table-data="tableData"
        :page-size="pageSize"
        :check-change="handleCurrentChange"
        :checkbox="true"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        :transform-data="transformData"
        rowKey="quotaDetailId"
        :source="quotaDetailApi.list"
      />
    </el-main>

    <!-- 新增弹框 -->
    <srm-dialog :title="dialogTitle" size="large" :visible.sync="visible">
      <div class="quotadetailEdit">
        <div style="padding-bottom: 10px">
          <el-button class="detail-pbtn" type="primary" @click="OsMaterialRequisitionAttachAddLine">
            {{ $t('common.add') }}
          </el-button>
        </div>
        <el-table :data="requirementAttaches" style="width: 100%" border max-height="250px">
          <el-table-column
            align="center"
            type="index"
            :label="$t('purSettlementMod.tabindex')"
            width="50"
          />
          <!-- 库存组织 -->
          <el-table-column
            align="center"
            prop="orgId"
            :label="$t('purchaseDemand.fullPathId')"
          >
            <template slot-scope="scope">
              <OrganizationSelector
                ref="organizationSelector2"
                v-model="scope.row.orgId"
                node-type="INV"
                :placeholder="$t('common.pleaseSelect')"
                @select="getVendorObj($event,scope.row)"
              />
            </template>
          </el-table-column>
          <!-- 品类 -->
          <el-table-column align="center" prop="categoryId" :label="$t('common.category')">
            <template slot-scope="scope">
              {{ scope.row.categoryName }}
            </template>
          </el-table-column>
          <!-- 物料编码 -->
          <el-table-column align="center" prop="itemId" :label="$t('common.materialCode')">
            <template slot-scope="scope">
              <QuickSearch
                :show-input="scope.row.itemCode"
                show-key="itemId"
                :scope-data="scope.row"
                name="scc_base_material_item"
                @close-quicksearch="getVendorObj"
              />
            </template>
          </el-table-column>
          <!-- 物料名称 -->
          <el-table-column
            align="center"
            prop="itemName"
            :label="$t('quota.itemName')"
            :show-overflow-tooltip="true"
          />
          <!-- 预计月用量 -->
          <el-table-column
            align="center"
            prop="quantityPerMonth"
            :label="$t('common.estimatedMonthlyConsumption')"
          >
            <template slot-scope="scope">
              <el-input v-model="scope.row.quantityPerMonth" />
            </template>
          </el-table-column>
          <!-- 删除 -->
          <el-table-column :label="$t('common.operation')" width="60">
            <template slot-scope="scope">
              <el-button type="text" @click="handleDelClick(scope.$index, scope.row)">
                {{
                  $t('common.delete')
                }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <template #footer class="dialog-footer">
        <el-button type="primary" @click="confirm">
          {{ $t('common.confirm') }}
        </el-button>
        <el-button @click="cancel">
          {{ $t('common.cancel') }}
        </el-button>
      </template>
    </srm-dialog>
  </el-container>
</template>
<script>
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import ExportExcel from 'lib@/components/export-excel'
import OrganizationSelector from 'lib@/components/organization-selector'
import { quotaDetailApi } from 'modb@/quotaManagement/api/quotaApi'

export default {
  name: 'QuotadetailList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch,
    MImport,
    ExportExcel,
    OrganizationSelector
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      quotaDetailApi: quotaDetailApi,
      tableData: [],
      requirementAttaches: [
        {
          orgId: null, // 组织ID
          orgCode: '', // 组织Code
          orgName: '', // 组织名称
          itemId: '', // 物料ID
          itemCode: '', // 物料Code
          itemName: '', // 物料名称
          categoryId: '', // 品类ID
          categoryCode: '', // 品类code
          categoryName: '', // 品类名称
          quantityPerMonth: ''
        }
      ],
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      visible: false,
      mode: 'add',
      dialogTitle: this.$t('vendorMod.detail'),
      form: {
        quotaDetailId: '配额ID',
        quotaFlowId: '配置流程ID',
        quotaGroupId: '配置分组ID',
        orgId: '组织ID',
        orgCode: '组织Code',
        orgName: '组织名称',
        itemId: '物料ID',
        itemCode: '物料Code',
        itemName: '物料名称',
        categoryId: '品类ID',
        categoryCode: '品类code',
        categoryName: '品类名称',
        unit: '单位',
        unitName: '单位名称',
        startDate: '生效日期',
        endDate: '失效日期',
        companyId: '供应商ID',
        companyCode: '供应商编码',
        companyName: '供应商名称',
        presetPercent: '预设比例',
        quotaCeilLimitPercent: '上限比例',
        treatyPercent: '协议比例',
        mouldPercent: '模具比例',
        suggestQuotaPercent: '建议配额',
        quotaPercent: '制定配额',
        actualQuotaPercent: '执行比例',
        quotaOffset: '偏差比例',
        quantityPerMonth: '预计月用量',
        flowCode: '单据编码',
        approveStatus: '审批状态',
        remark: '备注',
        createdId: '创建人ID',
        createdBy: '创建人',
        creationDate: '创建时间',
        createdByIp: '创建人IP',
        lastUpdatedId: '最后更新人ID',
        lastUpdatedBy: '最后更新人',
        lastUpdateDate: '最后更新时间',
        lastUpdatedByIp: '最后更新人IP',
        tenantId: '租户',
        version: '版本号'
      },
      extraData: {
        fileModular: 'base',
        fileFunction: 'quotadetail',
        fileType: 'excel'
      },
      dictCodes: {},
      filterParams: {},
      tableHeader: [
        // 采购组织
        {
          prop: 'orgName',
          label: this.$t('purchaseDemand.fullPathId'),
          width: 100
        },
        // 物料编码
        {
          prop: 'itemCode',
          label: this.$t('supplierCapacityReport.materialCode'),
          width: 100
        },
        // 物料名称
        {
          prop: 'itemName',
          label: this.$t('supplierCapacityReport.materialName'),
          width: 100
        },
        // 品类
        {
          prop: 'categoryName',
          label: this.$t('materialMainData.categoryName'),
          width: 100
        },
        // 单位
        {
          prop: 'unitName',
          label: this.$t('materialMainData.unit'),
          width: 100
        },
        // 生效日期
        {
          prop: 'startDate',
          label: this.$t('purchaseDemand.startDate'),
          showType: 'date',
          formatter: (val) => (val || null),
          editable: (row) => row.editable,
          callback: function (row) {
            const groupId = row.quotaGroupId
            this.$refs[this.gridId].tableData.forEach((item) => {
              if (item.quotaGroupId === groupId) {
                item.startDate = row.startDate
              }
            })
          }.bind(this),
          width: 100
        },
        // 失效日期
        {
          prop: 'endDate',
          label: this.$t('purchaseDemand.endDate'),
          showType: 'date',
          formatter: (val) => (val || null),
          editable: (row) => row.editable,
          callback: function (row) {
            const groupId = row.quotaGroupId
            this.$refs[this.gridId].tableData.forEach((item) => {
              if (item.quotaGroupId === groupId) {
                item.endDate = row.endDate
              }
            })
          }.bind(this),
          width: 100
        },
        // 供应商名称
        {
          prop: 'companyName',
          label: this.$t('supplierCapacityReport.vendorName'),
          width: 150
        },
        // 预设比例
        {
          prop: 'presetPercent',
          label: this.$t('quota.presetProportion'),
          showType: 'input',
          editable: (row) => row.editable,
          width: 120
        },
        // 上限比例
        {
          prop: 'quotaCeilLimitPercent',
          label: this.$t('quota.limitProportion'),
          showType: 'input',
          editable: (row) => row.editable,
          width: 120
        },
        // 协议比例
        {
          prop: 'treatyPercent',
          label: this.$t('quota.treatyPercentRatio'),
          showType: 'input',
          editable: (row) => row.editable,
          width: 120
        },
        // 模具比例
        {
          prop: 'mouldPercent',
          label: this.$t('quota.mouldProportion'),
          showType: 'input',
          editable: (row) => row.editable,
          width: 120
        },
        // 建议配额
        {
          prop: 'suggestQuotaPercent',
          label: this.$t('quota.suggestQuota'),
          showType: 'input',
          editable: (row) => row.editable,
          width: 120
        },
        // 制定配额
        {
          prop: 'quotaPercent',
          label: this.$t('quota.setQuotas'),
          showType: 'input',
          editable: (row) => row.editable,
          width: 120
        },
        // {
        //   prop: "actualQuotaPercent",
        //   label: "执行比例",
        //   formattor: val => {
        //     val = val * 100 + "%";
        //     return val;
        //   },
        //   width: 100
        // },
        // {
        //   prop: "quotaOffset",
        //   label: "偏差比例",
        //   formattor: val => {
        //     val = val * 100 + "%";
        //     return val;
        //   },
        //   width: 100
        // },
        // 预计月用量
        {
          prop: 'quantityPerMonth',
          label: this.$t('common.estimatedMonthlyConsumption'),
          showType: 'input',
          editable: (row) => row.editable,
          width: 120
        },
        // 单据编码
        {
          prop: 'flowCode',
          label: this.$t('dataConfMod.sequenceCode'),
          width: 100
        },
        // 审批状态
        {
          prop: 'approveStatus', // 'APPROVAL','TOBEAPPROVED'  'DRAFT','CALCED','REJECTED'
          label: this.$t('purchaseDemand.auditStatus'),
          formattor: (val) => {
            switch (val) {
              case 'DRAFT':
                val = '拟定'
                break
              case 'CALCING':
                val = '计算中'
                break
              case 'CALCED':
                val = '已计算'
                break
              case 'TOBEAPPROVED':
                val = '待审批'
                break
              case 'APPROVAL':
                val = '已通过'
                break
              case 'REJECTED':
                val = '已驳回'
                break
              default:
                val = ''
                break
            }
            return val
          },
          width: 100
        },
        // 是否生效
        {
          prop: 'enableFlag',
          label: this.$t('purchaseDemand.enable'),
          width: 100
        },
        // 创建人
        {
          prop: 'createdBy',
          label: this.$t('supplierCapacityReport.createdBy'),
          width: 100
        },
        // 创建时间
        {
          prop: 'creationDate',
          label: this.$t('elementDefinition.creationDate'),
          width: 100
        },
        // 操作
        {
          prop: 'operation',
          label: this.$t('components.headers.operation'),
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 130,
          buttons: [
            {
              callback: (row, socpe) => this.edit(row, socpe),
              show: (row) =>
                !row.editable && ['DRAFT', 'CALCED', 'REJECTED'].includes(row.approveStatus),
              formattor: () => {
                return this.$t('common.edit')
              }
            },
            {
              callback: (row, socpe) => this.save(row, socpe),
              show: (row) => row.editable,
              formattor: () => {
                return this.$t('common.save')
              }
            },
            {
              callback: (row, socpe) => this.cancelEdit(row, socpe),
              show: (row) => row.editable, // 取消
              formattor: () => {
                return this.$t('common.cancel')
              }
            },
            {
              callback: (row) => this.deleteHandle(row),
              show: (row) =>
                !row.editable && ['DRAFT', 'CALCED', 'REJECTED'].includes(row.approveStatus),
              formattor: () => {
                return this.$t('common.delete')
              }
            }
          ]
        }
        // 审批状态=拟定、已计算、已驳回  --可编辑可删除，可配额计算、可生成配额清单
        // 审批状态=已通过、待审批  --不可编辑不可删除，不可配额计算，不可生成配额清单
      ],
      queryForm: [],
      queryParam: {}
    }
  },
  mounted () {
    this.queryForm = [
      // 库存组织
      {
        label: () => this.$t('dataConfMod.organizationId'),
        type: 'INVorganizationSelector',
        propKey: 'orgId',
        prop: 'orgId'
      },
      // 物料编码
      {
        label: () => this.$t('common.materialCode'),
        type: 'quicksearch',
        showKey: 'materialCode',
        propKey: 'materialCode',
        prop: 'itemCode',
        name: 'scc_base_material_item'
      },
      // 品类
      {
        label: () => this.$t('common.category'),
        type: 'catSelect',
        showKey: 'categoryId',
        prop: 'categoryId'
      },
      // 供应商名称
      {
        label: () => this.$t('common.vendorName'),
        type: 'quicksearch',
        showKey: 'companyName',
        propKey: 'companyId',
        prop: 'companyId',
        name: 'scc_sup_company_info'
      },
      // 创建人
      {
        label: () => this.$t('common.creator'),
        prop: 'createdBy'
      },
      // 创建时间
      {
        label: () => this.$t('common.creationTime'),
        type: 'daterange',
        prop: 'queryCreationDate'
      },
      // 生效时间
      {
        label: () => this.$t('common.effectTime'),
        type: 'daterange',
        prop: 'queryStartDate'
      }
      // {
      //   label: () => this.$t("contractMod.isValid"),
      //   type: "dict",
      //   code: 'YES_OR_NO',
      //   prop: "isValid"
      // }
    ]
    this.getQuerydata()
  },
  methods: {
    // 在新增弹框中删除某行
    handleDelClick (indexs, row) {
      this.requirementAttaches.splice(indexs, 1)
    },
    // 点击生成配额清单
    quotaList () {
      let attr = []
      const currentRows = this.currentRows
      currentRows.forEach((element) => {
        // 'APPROVAL','TOBEAPPROVED' 已通过、待审批  --不可编辑不可删除，不可配额计算，不可生成配额清单
        if (element.approveStatus !== 'APPROVAL' && element.approveStatus !== 'TOBEAPPROVED') {
          attr.push(element.quotaGroupId)
        }
      })
      if (attr.length == 0) {
        this.$message.error('已通过、待审批状态单据不可编辑不可删除，不可配额计算，不可生成配额清单')
        return false
      }
      quotaDetailApi.createQuotaFlow(attr).then((res) => {
        if (res.code == '0') {
          this.$message.success(res.message)
          this.$router.push({ name: 'quotaFlow' })
        } else {
          this.$message.error(res.message)
        }
      })
    },
    // 点击配额计算
    quotaCalculation () {
      let attr = []
      const currentRows = this.currentRows
      currentRows.forEach((element) => {
        // 'APPROVAL','TOBEAPPROVED' 已通过、待审批  --不可编辑不可删除，不可配额计算，不可生成配额清单
        if (element.approveStatus !== 'APPROVAL' && element.approveStatus !== 'TOBEAPPROVED') {
          attr.push(element.quotaGroupId)
        }
      })
      quotaDetailApi.calcQuota(attr).then((res) => {
        if (res.code == '0') {
          this.$message.success(res.message)
          this.getQuerydata()
        } else {
          this.$message.error(res.message)
        }
      })
    },
    // 点击保存
    save (row, scope) {
      const groupId = row.quotaGroupId
      let rowList = []
      let rowBol = 0
      this.$refs[this.gridId].tableData.forEach((item) => {
        if (item.quotaGroupId === groupId) {
          rowList.push(item)
        }
      })
      rowList.forEach((item) => {
        if (!(item.suggestQuotaPercent >= 0 && item.suggestQuotaPercent % 5 == 0)) {
          rowBol = 1
        }

        if (!(item.presetPercent >= 0 && item.presetPercent % 5 == 0)) {
          rowBol = 1
        }

        if (!(item.quotaPercent >= 0 && item.quotaPercent % 5 == 0)) {
          rowBol = 1
        }
      })
      if (rowBol == 0) {
        quotaDetailApi.batchUpdate(rowList).then((res) => {
          if (res.code == '0') {
            this.$message.success(res.message)
            this.getQuerydata()
          } else {
            this.$message.error(res.message)
            this.getQuerydata()
          }
        })
      } else {
        this.$message.error(this.$t('quota.suggestThatQuota'))
      }
    },
    transformData (data) {
      const list = data.data.list
      data.data.list = list.map((item) => ({ ...item, editable: false }))
      return data
    },
    // 点击编辑
    edit (row, scope) {
      scope.row.editable = true
      // 同一批的全部可编辑
      const groupId = scope.row.quotaGroupId
      this.$refs[this.gridId].tableData.forEach((item) => {
        if (item.quotaGroupId === groupId) {
          item.editable = true
        }
      })
    },
    cancelEdit (row, scope) {
      scope.row.editable = false
      const groupId = scope.row.quotaGroupId
      this.$refs[this.gridId].tableData.forEach((item) => {
        if (item.quotaGroupId === groupId) {
          item.editable = false
        }
      })
      this.getQuerydata()
    },
    OsMaterialRequisitionAttachAddLine () {
      this.requirementAttaches.push({
        orgId: null,
        categoryId: '',
        itemId: '',
        itemCode: '',
        itemName: '',
        quantityPerMonth: ''
      })
    },
    getVendorObj (val, scope) {
      console.log(val)
      if (val.organizationId) {
        scope.orgId = val.organizationId
        scope.orgCode = val ? val.organizationCode : ''
        scope.orgName = val ? val.organizationName : ''
      }
      if (val.materialCode) {
        scope.categoryId = val ? val.categoryId : ''
        scope.categoryName = val ? val.categoryName : ''
        scope.itemId = val ? val.materialId : ''
        scope.itemCode = val ? val.materialCode : ''
        scope.itemName = val ? val.materialName : ''
      }

      console.log(this.requirementAttaches)
    },
    handleSuccess () {
      this.getQuerydata()
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-sup/sup/quotadetail/importExcelTemplate',
        '导入模板.xlsx'
      ).catch(() => {
        this.$message.error(this.$t('purchaseDemand.downloadFail'))
      })
    },
    cancel () {
      this.visible = false
    },
    confirm () {
      this.$api.inq.quotadetail.add(this.requirementAttaches).then((res) => {
        this.$message(this.$t('components.approvalHead.tips.approvalCompletion'))
        this.visible = false
        this.getQuerydata()
      })
    },

    syncFilterParams (values) {
      this.filterParams = values
    },
    getQuerydata (params) {
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    deleteHandle (row) {
      const _row = row
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          const quotaDetailId = _row.quotaDetailId
          this.$api.inq.quotadetail.delete(quotaDetailId).then((datas) => {
            this.$message({
              message: this.$t('components.approvalHead.tips.approvalCompletion'),
              type: 'success'
            })
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    addHandle (row) {
      for (let i in this.form) {
        this.form[i] = ''
      }
      this.dialogTitle = this.$t('quota.quotaListAdded')
      this.visible = true
      this.mode = 'add'
    },
    handleCurrentChange (val) {
      this.currentRows = val
    }
  }
}
</script>
