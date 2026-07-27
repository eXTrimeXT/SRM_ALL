<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <!-- 查询条件 -->
      <FormWrapper
        ref="formRef"
        :form-array="preArr"
        :init-active="true"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />

      <!-- 按钮域 -->
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton v-if="userType === 'BUYER'" type="primary" @click="addOne">
            {{ $t('common.add') }}
          </AuthorityButton>

          <AuthorityButton v-if="userType === 'BUYER'" @click="batchDelete">
            {{ $t('common.delete') }}
          </AuthorityButton>

          <ExportExcel
            v-loading
            page-url="/api-pef/inspection-standard/listPage"
            :filter-params="queryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            :title="$t('common.export')"
            timeout="1000000"
            export-mode="front"
            type="default"
          />
        </template>
      </MainHeader>

      <!-- 列表 -->
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        checkbox
        :check-change="(x) => (selectRows = x)"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-pef/inspection-standard/listPage"
      >
        <!-- 检验标准附件 -->
        <template #standardFileSlot="{ scope }">
          <el-button type="text" @click="viewFile(scope.row)">
            {{ $t('bidMod.expertAttachment') }}
          </el-button>
        </template>
      </TableView>

      <srm-dialog
        :title="$t('bidMod.expertAttachment')"
        size="large"
        :visible.sync="dialogVisible"
        :close-on-click-modal="false"
        append-to-body
      >
        <FileDynamic
          ref="sceneAttachment"
          v-model="insStandardFiles"
          scene-module-code="SCENE_PERFORMANCE_INS_STANDARD_ATTACHMENT"
          :business-id="businessId"
          :needInit="false"
          :editable="false"
        />
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">
            {{ $t("common.cancel") }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import inspectionStandardDetail from './edit'
import ExportExcel from 'lib@/components/export-excel'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import { inspectionStandard } from 'modb@/qualitySynergy/api'

export default {
  name: 'InspectionStandardList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch,
    ExportExcel,
    FileDynamic
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      dialogVisible: false,
      insStandardFiles: [],
      dictCodes: {
        isLatest: 'DRAWING_IS_LATEST',
        status: 'INSPECTION_STANDARD_STATUS'
      },
      preList: [
        {
          // 物料编码
          prop: 'materialCode',
          label: this.$t('common.materialCode'),
          type: 'quicksearch',
          showKey: 'materialCode',
          propKey: 'materialCode',
          name: 'scc_base_material_item'
        },
        // 品类名称
        {
          prop: 'categoryName',
          label: this.$t('common.categoryName'),
          type: 'catSelect',
          showKey: 'categoryName'
        },
        // 标准编号
        {
          prop: 'inspectionStandardNo',
          label: this.$t('qualitySynergy.inspectionStandardNo')
        },

        // 业务实体
        {
          prop: 'orgId',
          label: this.$t('qualitySynergy.orgId'),
          type: 'OUorganizationSelector'
        },
        {
          // 标准状态
          prop: 'status',
          label: this.$t('qualitySynergy.paymentPlanStatus'),
          width: 180,
          type: 'dict',
          code: 'INSPECTION_STANDARD_STATUS'
        },
        {
          // 生效日期
          prop: 'startDateList',
          type: 'daterange',
          label: this.$t('qualitySynergy.startDate1')
        },
        {
          // 失效日期
          prop: 'endDateList',
          type: 'daterange',
          label: this.$t('qualitySynergy.endDate')
        }
      ],
      queryParam: {},
      pageSize: 15,
      gridId: 'list',
      tableHeader: [],
      tableData: [],
      selectRows: [],
      businessId: ''
    }
  },
  computed: {
    userType () {
      return this.$store.getters.userType
    },
    preArr () {
      if (this.userType === 'BUYER') {
        return [
          ...this.preList,
          // 供应商名称
          {
            prop: 'vendorId',
            label: this.$t('common.vendorName'),
            type: 'quicksearch',
            showKey: 'companyName',
            propKey: 'companyId',
            name: 'scc_sup_company_info'
          }
        ]
      } else {
        return this.preList
      }
    }
  },
  created () {
    // 列表定义
    let tableHeader = [
      {
        // 标准编号
        prop: 'inspectionStandardNo',
        label: this.$t('qualitySynergy.inspectionStandardNo'),
        width: 140,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.readOne(row)
      },
      {
        // 业务实体
        label: this.$t('qualitySynergy.orgId'),
        prop: 'orgName',
        width: 130
      },
      {
        // 物料编码
        prop: 'materialCode',
        label: this.$t('common.materialCode'),
        width: 140
      },
      {
        // 物料名称
        label: this.$t('qualitySynergy.materialName'),
        prop: 'materialName',
        width: 160
      },
      {
        // 检验标准版本
        label: this.$t('qualitySynergy.standardVersion'),
        prop: 'standardVersion',
        width: 130
      },
      {
        // 是否最新板
        label: this.$t('qualitySynergy.isLatest'),
        prop: 'isLatest',
        width: 120,
        dataType: 'dict',
        code: 'DRAWING_IS_LATEST'
      },
      {
        // 品类名称
        label: this.$t('qualitySynergy.categoryName'),
        prop: 'categoryFullName',
        width: 220
      },
      {
        // 标准状态
        label: this.$t('qualitySynergy.paymentPlanStatus'),
        prop: 'status',
        width: 120,
        dataType: 'dict',
        code: 'INSPECTION_STANDARD_STATUS'
      },
      {
        // 检验标准附件
        label: this.$t('qualitySynergy.standardFileSlot'),
        width: 160,
        showType: 'slot',
        slot: 'standardFileSlot'
      },
      {
        // 生效日期
        prop: 'startDate',
        label: this.$t('qualitySynergy.startDate1'),
        width: 160
      },
      {
        // 失效日期
        prop: 'endDate',
        label: this.$t('qualitySynergy.endDate2'),
        width: 160
      }
    ]
    if (this.userType === 'BUYER') {
      let obj1 = {
        // 供应商编码
        label: this.$t('common.vendorCode'),
        prop: 'vendorCode',
        width: 130
      }
      let obj2 = {
        // 供应商名称
        label: this.$t('common.vendorName'),
        prop: 'vendorName',
        width: 130
      }
      let obj3 = {
        // 操作
        prop: 'operation',
        label: this.$t('components.headers.operation'),
        showType: 'buttons',
        btnStyle: 'text',
        fixed: 'right',
        width: 130,
        buttons: [
          {
            // 编辑
            callback: row => this.editOne(row),
            show: row => row.status === 'DRAFT',
            formattor: () => this.$t('common.edit')
          },
          {
            // 删除
            callback: row => this.deleteOne(row),
            show: row => row.status === 'DRAFT',
            formattor: () => this.$t('common.delete')
          },
          {
            // 升级
            callback: row => this.upgradeOne(row),
            show: row => row.status === 'VALID',
            formattor: () => this.$t('qualitySynergy.upgrade')
          },
          // { // 编辑页的提交按钮=生效作用 因为列表页的生效按钮没对明细页必填项做校验 所以先注释 20230717--
          //   // 生效
          //   callback: row => this.takeEffect(row),
          //   show: row => row.status === 'DRAFT',
          //   formattor: () => this.$t('common.active')
          // },
          {
            // 失效
            callback: row => this.loseEffect(row),
            show: row => row.status === 'VALID',
            formattor: () => this.$t('common.inactive')
          }
        ]
      }
      tableHeader.splice(2, 0, obj1)
      tableHeader.splice(3, 0, obj2)
      tableHeader.push(obj3)
    }
    this.tableHeader = tableHeader
    this.getQuerydata()
  },
  methods: {
    // 列表查询
    getQuerydata (v) {
      // 生效日期
      if (v && v.startDateList) {
        v.startDateFrom = v.startDateList[0]
        v.startDateTo = v.startDateList[1]
      } else if (v && !v.startDateList) {
        delete v.startDateFrom
        delete v.startDateTo
      }
      // 失效日期处理
      if (v && v.endDateList) {
        v.endDateFrom = v.endDateList[0]
        v.endDateTo = v.endDateList[1]
      } else if (v && !v.endDateList) {
        delete v.endDateFrom
        delete v.endDateTo
      }

      this.queryParam = v
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    syncFilterParams (values) {
      this.queryParam = values
      this.queryParam = values
    },
    // 新增
    addOne () {
      this.$emit('tab-add', {
        component: inspectionStandardDetail,
        params: {
          flag: 'add',
          tabName: 'inspectionStandardDetail'
        },
        // 成品通用标准新增
        title: this.$t('qualitySynergy.inspectionStandardAdd'),
        name: 'inspectionStandardDetail'
      })
    },
    // 批量删除
    batchDelete () {
      if (this.selectRows.length <= 0) {
        this.$message.warning(this.$t('common.msgSelectDelData'))
        return
      }
      let idArr = []
      for (let i = 0; i < this.selectRows.length; i++) {
        let row = this.selectRows[i]
        if (!row.inspectionStandardId) {
          continue
        }
        if (row.status === 'DRAFT') {
          idArr.push(row.inspectionStandardId)
          continue
        }

        this.$message.warning(`第${i + 1}行数据非拟定状态，不能删除`)
        return
      }
      this.deleteFetch(idArr)
    },
    // 删除行
    deleteOne ({ inspectionStandardId }) {
      this.deleteFetch([+inspectionStandardId])
    },
    deleteFetch (inspectionStandardIds) {
      // 当前操将永久删除此数据，确认删除此数据
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(async () => {
          inspectionStandard.delete(inspectionStandardIds).then(res => {
            this.$message.success(this.$t('common.successDelete'))
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    readOne (row) {
      this.$emit('tab-add', {
        component: inspectionStandardDetail,
        params: {
          flag: 'view',
          row: row,
          tabName: 'inspectionStandardDetail' + row.inspectionStandardId
        },
        title: row.inspectionStandardNo,
        name: 'inspectionStandardDetail' + row.inspectionStandardId
      })
    },
    // 编辑行
    editOne (row) {
      this.$emit('tab-add', {
        component: inspectionStandardDetail,
        params: {
          flag: 'edit',
          row: row,
          tabName: 'inspectionStandardDetail' + row.inspectionStandardId
        },
        title: row.inspectionStandardNo,
        name: 'inspectionStandardDetail' + row.inspectionStandardId
      })
    },
    // 升级
    upgradeOne ({ inspectionStandardId }) {
      inspectionStandard.upgrade(inspectionStandardId).then(res => {
        this.$message.success(this.$t('qualitySynergy.upgradeSuccessfully'))
        this.getQuerydata()
      })
    },
    // 生效
    takeEffect ({ inspectionStandardId }) {
      inspectionStandard.takeEffect(inspectionStandardId).then(res => {
        this.$message.success(this.$t('common.effectiveSuccessfully'))
        this.getQuerydata()
      })
    },
    // 失效
    loseEffect ({ inspectionStandardId }) {
      inspectionStandard.loseEffect(inspectionStandardId).then(res => {
        this.$message.success(this.$t('common.failedSuccess'))
        this.getQuerydata()
      })
    },
    // 查看附件
    viewFile ({ inspectionStandardId }) {
      inspectionStandard.getInfo({ inspectionStandardId }).then(res => {
        this.dialogVisible = true
        this.insStandardFiles = res.data.insStandardFiles
        this.businessId = res.data.inspectionStandardId
        this.$nextTick(() => {
          this.$refs.sceneAttachment.loadFileInfo()
        })
      })
    }
  }
}
</script>
