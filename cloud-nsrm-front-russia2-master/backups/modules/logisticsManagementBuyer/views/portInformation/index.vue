<template>
  <el-container
    class="flex-container-notab the_contractPaymentTypeList_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="preArr"
        :init-active="true"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <!--新增-->
          <el-button
            type="primary"
            @click="addOne"
          >
            {{ $t("common.add") }}
          </el-button>

          <!--保存-->
          <el-button
            type="primary"
            @click="batchSave"
          >
            {{ $t("common.save") }}
          </el-button>

          <!--删除-->
          <el-button
            type="primary"
            @click="batchDelete"
          >
            {{ $t("common.delete") }}
          </el-button>

          <!--生效-->
          <el-button
            type="primary"
            @click="doBatchEffect"
          >
            {{ $t("common.active") }}
          </el-button>

          <!--失效-->
          <el-button
            type="primary"
            @click="doBatchIneffect"
          >
            {{ $t("common.inactive") }}
          </el-button>
        </template>
      </main-header>

      <table-view
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="15"
        :pre-query-data="queryParam"
        open-custom-table
        checkbox
        :check-change="handleSelectionChange"
        url="/api-pd/logistics/port/listPageByParam"
      >
        <template #countryCode="props">
          <el-input
            v-model="props.scope.row.countryCode"
            :disabled="!(props.scope.row.editable || props.scope.row.status === 'DRAFT')"
          />
        </template>
        <template #countryNameZhs="props">
          <el-input
            v-model="props.scope.row.countryNameZhs"
            :disabled="!(props.scope.row.editable || props.scope.row.status === 'DRAFT')"
          />
        </template>
        <template #countryNameEn="props">
          <el-input
            v-model="props.scope.row.countryNameEn"
            :disabled="!(props.scope.row.editable || props.scope.row.status === 'DRAFT')"
          />
        </template>
        <template #portCode="props">
          <el-input
            v-model="props.scope.row.portCode"
            :disabled="!(props.scope.row.editable || props.scope.row.status === 'DRAFT')"
          />
        </template>
        <template #portNameZhs="props">
          <el-input
            v-model="props.scope.row.portNameZhs"
            :disabled="!(props.scope.row.editable || props.scope.row.status === 'DRAFT')"
          />
        </template>
        <template #portNameEn="props">
          <el-input
            v-model="props.scope.row.portNameEn"
            :disabled="!(props.scope.row.editable || props.scope.row.status === 'DRAFT')"
          />
        </template>
        <template #portType="props">
          <DictSelect
            v-model="props.scope.row.portType"
            code="PORT_TYPE"
            :disabled="!(props.scope.row.editable || props.scope.row.status === 'DRAFT')"
          />
        </template>
      </table-view>
    </el-main>
  </el-container>
</template>

<script>
import { parseTime } from '@/utils'
import MImport from 'lib@/components/import'
import ExportExcel from 'lib@/components/export-excel'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'

export default {
  name: 'PortInformation',
  components: {
    TableView,
    MainHeader,
    ExportExcel,
    FormWrapper,
    QuickSearch,
    MImport,
    OrganizationSelector
  },
  provide () {
    return { context: this }
  },
  data () {
    return {
      gridId: 'list',
      selectList: [],
      tableHeader: [],
      tableData: [],
      preArr: [
        //  国家中文名称
        { prop: 'countryNameZhs', label: this.$t('logisticsMod.countryNameZhs') },
        // 国家英文名称
        { prop: 'countryNameEn', label: this.$t('logisticsMod.countryNameEn') },
        // 港口类型
        { prop: 'portType', label: this.$t('logisticsMod.portType'), type: 'dict', code: 'PORT_TYPE' },
        // 港口英文名称
        { prop: 'portNameEn', label: this.$t('logisticsMod.portNameEn') },
        // 港口中文名称
        { prop: 'portNameZhs', label: this.$t('logisticsMod.portNameZhs') },
        // 港口代码
        { prop: 'portCode', label: this.$t('logisticsMod.portCode') },
        {
          prop: 'status',
          label: this.$t('common.status'),
          type: 'dict',
          code: 'LOGISTICS_STATUS'
        }
      ],
      queryParam: {}
    }
  },
  created () {
    this.tableHeader = [
      {
        prop: 'countryCode',
        label: this.$t('logisticsMod.countryCode'), // 国家编号
        width: 120,
        showType: 'slot',
        slot: 'countryCode'
      },
      {
        prop: 'countryNameZhs',
        label: this.$t('logisticsMod.countryNameZhs'), // 国家中文名
        minWidth: 150,
        showType: 'slot',
        slot: 'countryNameZhs'
      },
      {
        prop: 'countryNameEn',
        label: this.$t('logisticsMod.countryNameEn'), // 国家英文名
        minWidth: 150,
        showType: 'slot',
        slot: 'countryNameEn'
      },
      {
        prop: 'portCode',
        label: this.$t('logisticsMod.portCode'), // 港口代码
        width: 120,
        showType: 'slot',
        slot: 'portCode'
      },
      {
        prop: 'portNameZhs',
        label: this.$t('logisticsMod.portNameZhs'), // 港口中文名称
        minWidth: 150,
        showType: 'slot',
        slot: 'portNameZhs'
      },
      {
        prop: 'portNameEn',
        label: this.$t('logisticsMod.portNameEn'), // 港口英文名称
        minWidth: 150,
        showType: 'slot',
        slot: 'portNameEn'
      },
      {
        prop: 'portType',
        label: this.$t('logisticsMod.portType'), // 港口类型
        width: 150,
        showType: 'slot',
        slot: 'portType'
      },
      {
        prop: 'status',
        label: this.$t('common.status'),
        width: 120,
        dataType: 'dict',
        code: 'LOGISTICS_STATUS'
      },
      {
        prop: 'createdUserName', // createdBy
        label: this.$t('common.creator'),
        width: 100
      },
      {
        prop: 'creationDate',
        label: this.$t('common.creationTime'),
        width: 100,
        formattor: val => val ? parseTime(val, '{y}-{m}-{d}') : ''
      },
      {
        prop: 'lastUpdatedUserName', // lastUpdatedBy
        label: this.$t('common.updatePeople'),
        width: 100
      },
      {
        prop: 'lastUpdateDate',
        label: this.$t('common.updateTime'),
        width: 100,
        formattor: val => val ? parseTime(val, '{y}-{m}-{d}') : ''
      },
      {
        prop: 'operation',
        label: this.$t('common.operation'),
        width: 100,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          // 生效
          {
            // 状态： 拟定、失效 && 非新增
            show: row => ['DRAFT', 'INEFFECTIVE'].includes(row.status) && row.portId,
            callback: row => this.doEffect(row),
            formattor: () => this.$t('common.active')
          },
          // 失效
          {
            // 状态：生效
            show: row => row.status === 'EFFECTIVE',
            callback: row => this.doIneffect(row),
            formattor: () => this.$t('common.inactive')
          },
          // 删除
          {
            // 状态：拟定
            show: row => row.status === 'DRAFT',
            callback: row => this.deleteOne(row),
            formattor: () => this.$t('common.delete')
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (v) {
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },

    handleSelectionChange (val) {
      this.selectList = val
    },

    addOne () {
      this.$refs[this.gridId].addOneEditableColumn({ status: 'DRAFT' })
      // 勾选
      this.$nextTick(() => {
        const tableData = this.$refs[this.gridId].getTableData()
        this.$refs[this.gridId].$refs.tableGrid.toggleRowSelection(tableData[0], true)
      })
    },

    delOne (index) {
      this.$refs[this.gridId].deleteRow(index)
    },

    batchSave () {
      if (this.selectList.length === 0) {
        return this.$message.error(this.$t('contractMod.msgSelData')) // 请选择数据!
      }
      this.$http({
        url: '/api-pd/logistics/port/savePorts',
        method: 'POST',
        data: this.selectList.filter(v => v.status === 'DRAFT'),
        loading: true
      }).then(() => {
        this.$message.success(this.$t('common.successSave'))
        this.getQuerydata()
      })
    },

    batchDelete () {
      if (this.selectList.length === 0) {
        return this.$message.error(this.$t('contractMod.msgSelData')) // 请选择数据!
      }
      if (this.selectList.some(v => v.status !== 'DRAFT')) {
        return this.$message.error(this.$t('logisticsMod.msgNotDelete')) // 状态不是拟定的不能删除!
      }
      for (let row of this.selectList) {
        if (!row.portId) {
          let index = this.$refs[this.gridId].tableData.indexOf(row)
          this.delOne(index)
        }
      }
      let idArr = this.selectList.map(v => v.portId).filter(v => !!v)
      if (idArr.length === 0) return
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/api-pd/logistics/port/deleteByIds',
          method: 'POST',
          data: idArr,
          loading: true
        }).then(() => {
          this.$message.success(this.$t('common.success'))
          this.getQuerydata()
        })
      })
    },

    doBatchEffect () {
      if (this.selectList.length === 0) {
        return this.$message.error(this.$t('contractMod.msgSelData'))
      }
      if (this.selectList.some(v => v.status !== 'DRAFT' && v.status !== 'INEFFECTIVE')) {
        // 请选择拟定或失效状态的数据!
        return this.$message.error(this.$t('logisticsMod.msgSelDraftOrInAData'))
      }
      this.$http({
        url: '/api-pd/logistics/port/effectivePorts',
        method: 'POST',
        data: this.selectList.map(v => v.portId).filter(v => !!v),
        loading: true
      }).then(() => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },

    doBatchIneffect () {
      if (this.selectList.length === 0) {
        return this.$message.error(this.$t('contractMod.msgSelData'))
      }
      if (this.selectList.some(v => v.status !== 'EFFECTIVE')) {
        return this.$message.error(this.$t('logisticsMod.msgSelActiveData')) // 请选择拟定或失效状态的数据!
      }
      this.$http({
        url: '/api-pd/logistics/port/inEffectivePorts',
        method: 'POST',
        data: this.selectList.map(v => v.portId).filter(v => !!v),
        loading: true
      }).then(() => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },

    doEffect (row) {
      this.$http({
        url: '/api-pd/logistics/port/effectivePort',
        method: 'GET',
        params: { portId: row.portId },
        loading: true
      }).then(() => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },

    doIneffect (row) {
      this.$http({
        url: '/api-pd/logistics/port/inEffectivePort',
        method: 'GET',
        params: { portId: row.portId },
        loading: true
      }).then(() => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },

    syncFilterParams (values) {
      this.queryParam = values
    },

    deleteOne (row) {
      if (!row.portId) {
        let index = this.$refs[this.gridId].tableData.indexOf(row)
        this.delOne(index)
        return
      }
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/api-pd/logistics/port/deleteById',
          method: 'GET',
          params: { portId: row.portId },
          loading: true
        }).then(() => {
          this.$message.success(this.$t('common.successDelete'))
          this.getQuerydata()
        })
      })
    }
  }
}
</script>
