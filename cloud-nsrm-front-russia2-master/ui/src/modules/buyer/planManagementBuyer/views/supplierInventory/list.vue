<template>
  <el-container
    class="flex-container supplierinventory_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <!-- <AuthorityButton type="primary" @click="addHandle">{{ $t('common.add') }}</AuthorityButton> -->

          <MImport
            code="sup:inventory:import"
            :title="$t('common.import')"
            up-load-url="/api-sup-ce/sup/inventory/importExcel"
            :extra-data="extraData"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
          <!-- 供方库存明细.xlsx -->
          <ExportDirect
            type=""
            exprot-url="/api-sup-ce/sup/inventory/exportExcel"
            :filter-params="filterParams"
            requst-type="POST"
            :btn-text="$t('common.export')"
            :filename="$t('supplierInventory.exportFileName')"
            type="default"
          />
          <!--自定义导出暂时用不上 <ExportExcel
            pageUrl="/api-sup-ce/sup/inventory/listPage"
            :filterParams="filterParams"
            :tableHeader="tableHeader"
            :dictCodes="dictCodes"
            timeout="1000000"
            exportMode="front"
          /> -->
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :source="supplierInventoryApi.list"
        :row-dblclick="rowDblclick"
        :comActive="$attrs['changeTab']"
        @afterQuery="afterQuery"
      >
        <template #inventoryNumber="{ scope }">
          <el-input
            v-if="scope.row.edit && curRole === 'VENDOR'"
            v-model="scope.row.inventoryNumber"
          />
          <span v-else>{{ scope.row.inventoryNumber }}</span>
        </template>
        <template #inAndOutNumber="{ scope }">
          <el-input
            v-if="scope.row.edit && curRole === 'VENDOR'"
            v-model="scope.row.inAndOutNumber"
          />
          <span v-else>{{ scope.row.inAndOutNumber }}</span>
        </template>
      </TableView>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MImport from 'lib@/components/import'
import { downloadFileLinkByPost } from 'lib@/utils/file'
import ExportExcel from 'lib@/components/export-excel'
import ExportDirect from 'lib@/components/export-direct'
import { adaptDictData } from '@/utils'
import { getDictItem } from '@/api/common'
import { supplierInventory } from 'modb@/planManagementBuyer/api/index'
import { supplierInventoryApi } from 'modb@/planManagementBuyer/api/inventory'

export default {
  name: 'SupplierinventoryList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    MImport,
    ExportExcel,
    ExportDirect
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      supplierInventoryApi: supplierInventoryApi,
      curRole: this.$store.getters.userType, // VENDOR BUYER
      tableData: [],
      name: 'supplierinventoryList',
      tableName: 'supplierinventoryTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      extraData: {
        fileModular: 'base',
        fileFunction: 'supplierinventory',
        fileType: 'excel'
      },
      dictCodes: {},
      filterParams: {},
      tableHeader: [],
      filterConfig: [],
      queryParam: {},
      serviceStatus: []
    }
  },
  created () {
    this.fatchDictData() // 字典
    let _this = this
    this.tableHeader = [
      {
        prop: 'itemCode',
        label: this.$t('common.materialCode'), // 物料编码
        width: 100
      },
      {
        prop: 'itemDesc',
        label: this.$t('common.materialName'), // 物料名称
        width: 100
      },
      {
        prop: 'categoryName',
        label: this.$t('supplierInventory.categoryName'), // 品类名称(物料小类)
        width: 160
      },
      {
        prop: 'categoryCode',
        label: this.$t('supplierInventory.categoryCode'), // 品类编码(物料小类)
        width: 160
      },
      {
        label: this.$t('supplierInventory.serviceStatus'), // 供方品类状态
        prop: 'serviceStatus',
        width: 130,
        formattor (val) {
          return _this.$getDictLabelByValue(_this.serviceStatus, val)
        }
      },
      {
        prop: 'vendorCode',
        label: this.$t('supplierRating.vendorCode'), // 供应商编码
        width: 120
      },
      {
        prop: 'vendorName',
        label: this.$t('supplierRating.supplierName'), // 供应商名称
        width: 120
      },
      {
        prop: 'ceeaOrgName',
        label: this.$t('supplierRating.entity'), // 业务实体"
        width: 100
      },
      {
        prop: 'invOrgName',
        label: this.$t('bom.invOrgName'), // 库存组织"
        width: 100
      },
      {
        prop: 'minStockInventory',
        label: () => _this.$t('common.supplierMiniStock'), // 供方最低备货库存
        width: 150,
        showType: 'input',
        editable: row => {
          return row.edit && this.curRole === 'BUYER'
        },
        callback: row => this.checkRowDataAmount(row, 'minStockInventory')
      },
      {
        prop: 'lessMinStockInventory',
        label: () => this.$t('common.isLowerStock'), // '是否低于最低备货库存',
        width: 150,
        formattor: val => (!val ? '' : val === 'Y' ? this.$t('common.yes') : this.$t('common.no'))
      },
      {
        prop: 'inventoryNumber',
        label: this.$t('supplierInventory.inventoryNumber'), // 供方库存现有数量
        width: 180,
        showType: 'slot',
        slot: 'inventoryNumber',
        desc: this.$t('supplierRating.calculationLogic')
      },
      {
        prop: 'inAndOutNumber',
        label: this.$t('supplierInventory.inAndOutNumber'), // 出入库数量
        width: 130,
        showType: 'slot',
        slot: 'inAndOutNumber',
        desc: this.$t('supplierRating.inAndOutNumberDesc')
      },
      {
        prop: 'unit',
        label: this.$t('materialPrice.unit'), // 单位
        width: 100
      },
      {
        prop: 'createdBy',
        label: this.$t('supplierRating.creator'), // 创建人
        width: 100
      },
      {
        prop: 'creationDate',
        label: this.$t('supplierRating.creationDate'), // 创建时间
        width: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'lastUpdatedBy',
        label: this.$t('common.updatePeople'), // 更新人
        width: 100
      },
      {
        prop: 'lastUpdateDate',
        label: this.$t('common.updateTime'), // 更新时间
        width: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'operation',
        label: this.$t('common.operation'),
        showType: 'buttons',
        btnStyle: 'text',
        fixed: 'right',
        width: 130,
        buttons: [
          {
            callback: row => this.editHandle(row),
            // code: 'sup:inventory:edit',
            show: row => row.edit === false,
            formattor: () => {
              return this.$t('common.edit')
            }
          },
          {
            callback: row => this.unEditHandle(row),
            // code: 'sup:inventory:edit',
            show: row => row.edit === true,
            formattor: () => {
              return this.$t('supplierInventory.unEdit')
            }
          },
          {
            callback: row => this.saveHandle(row),
            // code: 'sup:inventory:save',
            show: row => row.edit === true,
            formattor: () => {
              return this.$t('common.save')
            }
          }
        ]
      }
    ]
    _this.filterConfig = [
      {
        prop: 'itemCode',
        label: () => this.$t('common.materialCode'), // 物料编码,
        type: 'quicksearch',
        showKey: 'materialCode',
        name: 'scc_base_material_item'
      },
      {
        prop: 'vendorName',
        label: () => this.$t('common.vendorName'), // 供应商名称
        type: 'quicksearch',
        showKey: 'companyName',
        name: 'scc_sup_company_info_display_buyer'
      },
      {
        prop: 'ceeaOrgId',
        label: () => this.$t('contractMod.buId'),
        type: 'OUorganizationSelector',
        multiple: false
      },
      {
        prop: 'updateStarTime',
        label: () => this.$t('supplierInventory.updateStarTime'),
        type: 'datetime'
      },
      {
        prop: 'updateEndTime',
        label: () => this.$t('supplierInventory.updateEndTime'),
        type: 'datetime'
      },
      {
        prop: 'lessMinStockInventory',
        label: () => this.$t('common.isLowerStock'), // '是否低于最低备货库存',
        type: 'select',
        options: [
          { label: this.$t('common.yes'), value: 'Y' }, // 是
          { label: this.$t('common.no'), value: 'N' } // 否
        ]
      }
    ]
    if (this.$store.getters.userType === 'VENDOR') {
      this.filterConfig = this.filterConfig.filter(item => item.prop !== 'vendorName')
    }
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    // 校验数值数据
    checkRowDataAmount (row, key) {
      let n = Number(row[key])
      if (isNaN(n)) {
        // this.$message.warning(this.$t('common.errorNumber'))
        row[key] = ''
      }
      if (n < 0) {
        // this.$message.warning(this.$t('purchaseDemand.lessThan0Tips'))
        row[key] = 0
      }
    },
    fatchDictData () {
      // 品类服务状态
      getDictItem('CATEGORY_STATUS').then(res => {
        this.serviceStatus = adaptDictData(res.data, 'dict')
        console.log(this.serviceStatus)
      })
    },
    handleSuccess () {
      this.getQuerydata()
    },
    downloadTemplate () {
      downloadFileLinkByPost(
        '/api-sup-ce/sup/inventory/exportExcelTemplate',
        '导入模板.xlsx'
      ).catch(() => {
        this.$message.error(this.$t('purchaseDemand.downloadFail'))
      })
    },

    syncFilterParams (values) {
      this.filterParams = values
    },
    getQuerydata (params) {
      this.queryParam = params || this.queryParam
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 新增与删除功能禁用
    // deleteHandle(row) {
    //   this.$confirm(this.$t("common.confirmDelete"), {
    //     confirmButtonText: this.$t("common.confirm"),
    //     cancelButtonText: this.$t("common.cancel"),
    //     type: "warning"
    //   })
    //     .then(() => {
    //       supplierInventoryApi.delete(row.supplierInventoryId).then(res => {
    //         this.$message.success(res.message);
    //         this.getQuerydata();
    //       });
    //     })
    //     .catch(() => {});
    // },

    //   addHandle(row) {
    //       this.mode = 'add';
    //       const tab = {
    //           component: supplierinventoryEdit,
    //           params: {
    //               row,
    //               flag: this.mode
    //           },
    //           title: "供方库存管理新增",
    //           name: "supplierinventoryEdit"
    //       };
    //       this.$emit("tab-add", tab);
    //   },
    editHandle (row) {
      // this.mode = "edit";
      // const tab = {
      //   component: supplierinventoryEdit,
      //   params: {
      //     row,
      //     flag: this.mode,
      //   },
      //   title: "物料编码"+row.itemCode+"编辑",
      //   name: "supplierinventoryEdit" + row.supplierInventoryId,
      // };
      // this.$emit("tab-add", tab);
      this.$set(row, 'edit', true)
    },
    unEditHandle (row) {
      this.$set(row, 'edit', false)
    },
    checkStoceNumber (row) {
      // 该正则数字且大于等于0
      let numReg = /^(?!(0[0-9]{0,}$))[0-9]{1,}[.]{0,}[0-9]{0,}$/
      let numRe = new RegExp(numReg)
      let r = true

      if (this.curRole === 'VENDOR') {
        if (!row.inventoryNumber) {
          this.$message({
            type: 'warning',
            message: this.$t('supplierRating.quantityAvailableStock'),
            duration: 10000,
            showClose: true
          })
          return (r = false)
        }
        if (!numRe.test(row.inventoryNumber) && row.inventoryNumber !== '') {
          this.$message({
            type: 'warning',
            message: this.$t('supplierRating.correctQuantityStock'),
            duration: 10000,
            showClose: true
          })
          return (r = false)
        }
        if (Number(row.inventoryNumber) + Number(row.inAndOutNumber) < 0) {
          this.$message({
            type: 'warning',
            message: this.$t('supplierRating.quantityLessWarehouse'),
            duration: 10000,
            showClose: true
          })
          return (r = false)
        }
      } else {
        if (row.minStockInventory !== '' && !numRe.test(row.minStockInventory)) {
          this.$message({
            type: 'warning',
            message: this.$t('supplierRating.correctSupplierMinimumStock'),
            duration: 10000,
            showClose: true
          })
          return (r = false)
        }
      }
      return r
    },
    async saveHandle (row) {
      // 库存校验
      if (!this.checkStoceNumber(row)) return

      let resData = ''
      if (this.curRole === 'VENDOR') {
        resData = await supplierInventory.update(row)
      } else {
        resData = await supplierInventory.updateSupplierInventoryByBuyer(row)
      }

      if (resData) {
        this.$message({
          type: 'success',
          message: resData.message
        })
      }

      this.getQuerydata()
      this.$set(row, 'edit', false)
    },
    rowDblclick (row, event, column) {
      // debugger
      this.$set(row, 'edit', true)
      // row.edit = true;
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    afterQuery (data) {
      let resList = data.map(i => ({
        ...i,
        edit: false
      }))
      this.$refs[this.gridId].tableData = resList
      // debugger
    },
    // 上传附件成功
    handleUploadSuccess (file, row, key) {
      const { id, name } = file
      row[key] = id.toString()
    },
    // 删除文件
    handleAttachmentRemove (row, key) {
      row[key] = ''
    }
  }
}
</script>
