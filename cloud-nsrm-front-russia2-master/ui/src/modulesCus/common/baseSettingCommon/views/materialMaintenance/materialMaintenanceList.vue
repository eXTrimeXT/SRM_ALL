<template>
  <el-container
    class="flex-container the_material_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        ref="formRef"
        :form-array="queryForm"
        :pre-form-obj="preFormObj"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <!-- 新增 -->
          <AuthorityButton
            type="primary"
            code="base:materialMaintenance:edit"
            @click="editTab('add')"
          >
            {{ $t('common.add') }}
          </AuthorityButton>
          <ExportExcel
            page-url="/api-base/material/materialItem/ext/listPageByCondition"
            :filter-params="exportParam"
            :table-header="tableHeaderExport"
            :dict-codes="dictCodes"
            timeout="1000000"
            :export-size="30000"
            export-mode="front"
            type="default"
          />
          <!-- 标记中国寻源 -->
          <AuthorityButton
            type="primary"
            :disabled="!currentRows.length"
            code="base:materialMaintenance:signZh"
            @click="signZhHandle('Y', currentRows)"
          >
            {{ $t('cusEntry.dataConfMod.signZh') }}
          </AuthorityButton>
          <!-- 取消标记 -->
          <AuthorityButton
            type="primary"
            :disabled="!currentRows.length"
            code="base:materialMaintenance:signZh"
            @click="signZhHandle('N', currentRows)"
          >
            {{ $t('cusEntry.dataConfMod.signRuCateCancel') }}
          </AuthorityButton>
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :openCustomTable="true"
        :checkbox="true"
        :check-change="handleCurrentChange"
        customTableKey="materialMaintenanceListBuyer"
        :comActive="$attrs['changeTab']"
        url="/api-base/material/materialItem/ext/listPageByCondition"
        @afterQuery="afterQuery"
      />
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import ExportExcel from 'lib@/components/export-excel'
import Detail from './materialMaintenanceDetail'
import cloneDeep from 'lodash/cloneDeep'

export default {
  name: 'MaterialMaintenanceList',
  components: {
    MainHeader,
    FormWrapper,
    ExportExcel,
    TableView
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      gridId: 'list',
      pageSize: 15,
      queryParam: {},
      queryForm: [
        {
          prop: 'extApplyNumber',
          label: this.$t('purchaseDemand.requirementHeadNum') // 申请编号
        },
        {
          prop: 'materialName',
          label: this.$t('common.materialName') // '物料名称'
        },
        {
          prop: 'unit',
          label: this.$t('dataConfMod.unit'), // 单位
          type: 'dict',
          code: 'unit'
        },
        {
          prop: 'extMaterialModel',
          label: this.$t('vendorMod.specification') // 规格型号
        },
        {
          prop: 'extMaterialNameChn',
          label: this.$t('common.materialName') + this.$t('cusEntry.dataConfMod.langSignZh') // 物料名称（中文）
        },
        {
          prop: 'extMaterialModelChn',
          label: this.$t('vendorMod.specification') + this.$t('cusEntry.dataConfMod.langSignZh') // 规格型号（中文）
        },
        {
          prop: 'materialCode',
          label: this.$t('common.materialCode') // '物料编码'
        },
        {
          prop: 'categoryName',
          label: this.$t('dataConfMod.category'), // '品类'
          type: 'catSelect',
          showKey: 'categoryName'
        },
        {
          prop: 'extMaterialOrderStatus',
          label: this.$t('bidMod.billstatus'), // 单据状态
          type: 'dict',
          code: 'MATERIAL_ADD_STATUS'
        },
        {
          prop: 'extApplicantName',
          label: this.$t('purchaseDemand.applicant') // 申请人
        },
        {
          prop: 'extApplyDateRange',
          label: this.$t('purchaseDemand.applyDate'), // 申请日期
          type: 'daterange'
        },
        {
          prop: 'profileUpdateDate',
          label: this.$t('cusEntry.supplement20250314.imageUpdateTime'), // 图片更新时间
          type: 'daterange'
        },
        {
          prop: 'profileUpdateBy',
          label: this.$t('cusEntry.supplement20250314.imageUpdater'), // 图片更新人
        },
        {
          prop: 'extIfSyncMdm',
          label: this.$t('cusEntry.dataConfMod.extIfSyncMdm'), // 是否同步MDM
          type: 'dict',
          code: 'YES_OR_NO'
        },
        {
          prop: 'extIfSyncSap',
          label: this.$t('cusEntry.dataConfMod.extIfSyncSap'), // 是否同步SAP
          type: 'dict',
          code: 'YES_OR_NO'
        },
        {
          prop: 'extIfSyncOnec',
          label: this.$t('cusEntry.dataConfMod.extIfSyncOnec'), // 是否同步1C
          type: 'dict',
          code: 'YES_OR_NO'
        },
        {
          prop: 'extIsHome',
          label: this.$t('cusEntry.dataConfMod.extIsHome'), // 是否中国寻源
          type: 'dict',
          code: 'YES_OR_NO'
        },
        {
          prop: 'extIfValid',
          label: this.$t('purchaseDemand.enable'), // 是否生效
          type: 'dict',
          code: 'YES_OR_NO'
        }
      ],
      dictCodes: {
        extMaterialOrderStatus: 'MATERIAL_ADD_STATUS',
        unit: 'unit',
        extIsHome: 'YES_OR_NO',
        extIfSyncSap: 'YES_OR_NO',
        extIfSyncMdm: 'YES_OR_NO',
        extIfSyncOnec: 'YES_OR_NO',
        extIfValid: 'YES_OR_NO'
      },
      preFormObj: {},
      currentRows: [],
      tableHeader: [],
      curUserId: this.$store.getters.userInfo.userId
    }
  },
  computed: {
    exportParam () {
      let param = cloneDeep(this.queryParam)
      param.isExport = 'N'
      return param
    },
    tableHeaderExport () {
      return this.tableHeader.filter(item => item.prop !== 'operation')
    }
  },
  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler () {
        if (
          this.$route.params.from === 'workCount' &&
          this.$route.params.funName === 'materialMaintenance'
        ) {
          // 供应商 工作台跳转
          this.queryParam.ceeaMaterialStatus = this.$route.params.ceeaMaterialStatus
          this.preFormObj = Object.assign(
            {},
            { extMaterialOrderStatus: this.$route.params.ceeaMaterialStatus }
          )
        }
      }
    }
  },
  created () {
    this.tableHeader = [
      {
        prop: 'extApplyNumber',
        label: this.$t('purchaseDemand.requirementHeadNum'), // 申请编号
        minWidth: 120,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.editTab('view', row),
        formattor: val => val || '--'
      },
      {
        prop: 'materialName',
        label: this.$t('common.materialName'), // 物料名称
        minWidth: 120
      },
      {
        prop: 'extMaterialNameChn',
        label: this.$t('common.materialName') + this.$t('cusEntry.dataConfMod.langSignZh'), // 物料名称（中文）
        minWidth: 140
      },
      {
        prop: 'extMaterialOrderStatus',
        label: this.$t('bidMod.billstatus'), // 单据状态
        minWidth: 100,
        dataType: 'dict',
        code: 'MATERIAL_ADD_STATUS'
      },
      {
        prop: 'materialCode',
        label: this.$t('common.materialCode'), // 物料编码
        minWidth: 100
      },
      {
        prop: 'extPassFlowDate',
        label: this.$t('purchaseDemand.extPassFlowDate'),
        minWidth: 140,
        dataType: 'dateTime'
      },
      {
        prop: 'unit',
        label: this.$t('dataConfMod.unit'), //  单位
        minWidth: 80,
        dataType: 'dict',
        code: 'unit'
      },
      {
        prop: 'extMaterialModel',
        label: this.$t('vendorMod.specification'), // 规格型号
        minWidth: 150
      },
      {
        prop: 'extMaterialModelChn',
        label: this.$t('vendorMod.specification') + this.$t('cusEntry.dataConfMod.langSignZh'), // 规格型号（中文）
        minWidth: 150
      },
      {
        prop: 'categoryName',
        label: this.$t('purchaseDemand.materialCateSub'), // 物料小类
        minWidth: 150
      },
      {
        prop: 'categoryFullName',
        label: this.$t('purchaseDemand.categoryFullName'), // 品类全称
        width: 150
      },
      {
        prop: 'extIsHome',
        label: this.$t('cusEntry.dataConfMod.extIsHome'), // 是否中国寻源
        minWidth: 120,
        dataType: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'extIfValid',
        label: this.$t('purchaseDemand.enable'), // 是否生效
        minWidth: 120,
        dataType: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'creationDate',
        label: this.$t('common.creationDate'), // 创建日期
        dataType: 'dateTime',
        width: 150
      },
      {
        prop: 'extApplicantName',
        label: this.$t('purchaseDemand.applicant'), // 申请人
        width: 120
      },
      {
        prop: 'extApplyDate',
        label: this.$t('purchaseDemand.applyDate'), // 申请日期
        width: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'profileUpdateDate',
        label: this.$t('cusEntry.supplement20250314.imageUpdateTime'), // 图片更新时间
        width: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'profileUpdateBy',
        label: this.$t('cusEntry.supplement20250314.imageUpdater'), // 图片更新人
        width: 120
      },
      {
        prop: 'extIfSyncMdm',
        label: this.$t('cusEntry.dataConfMod.extIfSyncMdm'), // 是否同步MDM
        minWidth: 120,
        dataType: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'extIfSyncSap',
        label: this.$t('cusEntry.dataConfMod.extIfSyncSap'), // 是否同步SAP
        minWidth: 120,
        dataType: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'extIfSyncOnec',
        label: this.$t('cusEntry.dataConfMod.extIfSyncOnec'), // 是否同步1C
        minWidth: 120,
        dataType: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'operation',
        label: this.$t('common.operation'),
        width: 180,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          {
            callback: row => this.editTab('edit', row),
            code: 'base:materialMaintenance:edit',
            show: row => ['DRAFT', 'WITHDRAW', 'REJECTED'].includes(row.extMaterialOrderStatus) && this.curUserId == row.createdId,
            formattor: () => this.$t('common.edit')
          },
          {
            callback: row => this.deleteHandle([row]),
            code: 'base:materialMaintenance:edit',
            show: row => ['DRAFT'].includes(row.extMaterialOrderStatus) && this.curUserId == row.createdId,
            formattor: () => this.$t('common.delete')
          },
          // 审批
          {
            callback: row => {
              let flag = row.isApprover == 'Y' ? 'approval' : 'enterFlow'
              this.editTab(flag, row)
            },
            code: 'base:materialMaintenance:approve',
            show: row => ['APPROVING'].includes(row.extMaterialOrderStatus) && (this.curUserId == row.createdId || row.isApprover == 'Y'),
            formattor: () => this.$t('common.approve')
          },
          {
            callback: row => {
              let type = row.extIsHome == 'Y' ? 'N' : 'Y'
              this.signZhHandle(type, [row])
            },
            code: 'base:materialMaintenance:signZh',
            formattor: (val, row) => {
              return row.extIsHome == 'Y' ? this.$t('cusEntry.dataConfMod.signRuCateCancel') : this.$t('cusEntry.dataConfMod.signZh')
            }
          },
          // 维护图片
          {
            callback: row => {
              // let type = row.extIsHome == 'Y' ? 'N' : 'Y'
              this.editTab('maintenanceImg', row)
            },
            code: 'base:materialMaintenance:maintenanceImg',
            formattor: () => this.$t('cusEntry.supplement20250314.maintainImage'),
          }
        ]
      }
    ]
  },
  mounted () {
    // 即将进行【导入物料】，您需要完成：1、导入企业管理的物料清单；2、维护物料对应的采购分类；
    const materialTip = localStorage.getItem('materialTip') || 'Y'
    if (materialTip === 'Y') {
      this.$confirm(this.$t('dataConfMod.materialMaintenanceAlert'), this.$t('common.tips'), {
        distinguishCancelAndClose: true,
        confirmButtonText: this.$t('common.start'),
        cancelButtonText: this.$t('common.toNotshowTip')
      }).then(() => {
        // 点击开始
      }).catch(() => {
        // 不再提示
        localStorage.setItem('materialTip', 'N')
      })
    }
    this.getQuerydata(this.preFormObj) //  查询数据
  },
  methods: {
    syncFilterParams (values) {
      this.getQuerydata(values, false)
    },
    getQuerydata (obj, isQuery = true) {
      const { extApplyDateRange, ...rest } = obj || this.queryParam
      const params = { ...rest }
      if (extApplyDateRange) {
        params.extApplyDateStart = extApplyDateRange[0]
        params.extApplyDateEnd = extApplyDateRange[1]
      }
      this.queryParam = { ...params }
      if (!isQuery) return
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
        this.currentRows = []
      })
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    afterQuery () {
      this.$refs[this.gridId].setTableData(async tableData => {
        const res = await this.$api.base.flowAPI.queryTodo()
        let queryTodoList = res.data || []
        tableData.forEach(tableItem => {
          let obj = queryTodoList.find(todoItem => tableItem.materialId + '' === todoItem.businessId + '')
          if (obj) {
            this.$set(tableItem, 'isApprover', 'Y')
          } else {
            this.$set(tableItem, 'isApprover', 'N')
          }
        })
      })
    },
    // 标记/取消标记中国寻源
    signZhHandle (type, rows) {
      this.$http({
        url: '/api-base/material/materialItem/ext/updateIfChinaSou',
        method: 'POST',
        data: {
          materialIds: rows.map(item => item.materialId),
          ifChinaSou: type
        }
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      }).catch(err => {
        console.log(err)
      })
    },
    // 新增物料
    editTab (type, row = {}) {
      let tabName = 'detail_' + type + row.materialId
      this.$emit('tab-add', {
        component: Detail,
        params: {
          flag: type,
          row,
          tabName: tabName
        },
        title: type == 'add' ? this.$t('dataConfMod.addMaterial') : row.extApplyNumber,
        name: tabName
      })
    },
    // 删除
    deleteHandle (rows) {
      this.$confirm(this.$t('common.ifDeleteData'), this.$t('common.tips'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/api-base/material/materialItem/ext/deleteMaterial',
          method: 'POST',
          data: rows.map(item => item.materialId)
        }).then(res => {
          this.$message.success(this.$t('common.success'))
          this.getQuerydata()
        }).catch(err => {
          console.log(err)
        })
      }).catch(() => { /* nothing */ })
    }
  }
}
</script>

<style scoped lang="scss">
.materialTableForm {
  position: absolute;
  top: 0;
  bottom: 0;
  height: 100%;
  width: 100%;
}
.download-link-wrap {
  .download-link-item {
    color: #1890ff;
    cursor: pointer;
  }
  .close-icon {
    font-weight: bold;
    cursor: pointer;
  }
}
.toRequired {
  color: #ff4949;
  padding-right: 2px;
}
.itemPic {
  max-height: 480px;
}
</style>
