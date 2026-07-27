<template>
  <el-container class="flex-container the_vendorPurchaseOrderList_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :form-array="preArr" form-label-width="120px" @getFormData="getQuerydata" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <!-- 新增 -->
          <AuthorityButton
            code="pur:purchaseCatalogOnOrOff:add"
            type="primary"
            @click="addData"
          >
            {{ $t('orderMod.buyerOrderSynergy.add') }}
          </AuthorityButton>
          <!-- 批量操作 -->
          <el-dropdown style="margin-left: 10px" @command="batchOperation">
            <el-button type="primary">
              批量操作<i class="el-icon-arrow-down el-icon--right" />
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="batchOn">
                批量上架
              </el-dropdown-item>
              <el-dropdown-item command="timeOn">
                定时上架
              </el-dropdown-item>
              <el-dropdown-item command="timeOff">
                取消定时上架
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <CUploadFile
            ref="cUploadFile"
            style="margin-left: 10px;"
            title="批量上传图片"
            :limit="100"
            :cus-data="fileInfo"
            :fileList="fileList"
            :showFileList="false"
            @upload-success="(file,fileList,uploadNum) => mulUploadSuccess(file,fileList,uploadNum)"
          />
        </template>
      </MainHeader>
      <TableView
        ref="tableRef"
        row-key="catalogOnShelvesId"
        :bigData="true"
        :checkbox="true"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :auto-query="false"
        :adeptMeiQl="true"
        :open-custom-table="true"
        :cell-style="cellStyle"
        :reserve-selection="true"
        :check-change="checkChange"
        :comActive="$attrs['changeTab']"
        url="/api-sup-ce/api-ql/CatalogOnShelves/query"
      />
    </el-main>
  </el-container>
</template>
<script>
import { parseTime } from '@/utils'
import CUploadFile from './c-upload-file'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import TableView from 'lib@/components/Table/TableView'
import purchaseCatalogOnOrOffDetail from './purchaseCatalogOnOrOffDetail'
import { transformMQL } from 'lib@/utils/util'
import { getCountDown, calcDate } from 'lib@/utils/date-format'
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'

export default {
  name: 'PurchaseCatalogOnOrOffList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    CUploadFile
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      queryParam: {},
      selection: [],
      fileAttachList: [],
      fileList: [],
      fileInfo: {
        uploadType: 'DEF', // 固定参数
        sourceType: 'WEB_APP', // 固定参数
        fileModular: 'catalogOnShelves', // 文件所属模块 -》基础模块
        fileFunction: 'catalogOnShelves', // 文件所属功能
        fileType: 'images' // 文件所属类型file（所有）、images
      },
      preArr: [
        {
          prop: 'extOrgIdList',
          label: () => this.$t('oneStopShopping.businessEntity'),
          type: 'OUorganizationSelector'
        },
        {
          prop: 'materialCode',
          label: '物料编码'
        },
        {
          prop: 'materialName',
          label: '物料名称'
        },
        // {
        //   prop: 'materialCode',
        //   label: () => this.$t('common.materialCode'),
        //   type: 'quicksearch',
        //   showKey: 'materialCode',
        //   propKey: 'materialCode',
        //   name: 'scc_base_material_item_contract'
        // },
        {
          prop: 'vendorCode',
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorCode')
        },
        {
          prop: 'vendorName',
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorName')
        },
        {
          prop: 'specification',
          label: '规格'
        },
        {
          prop: 'categoryName',
          label: () => this.$t('common.categoryName'),
          type: 'catSelect',
          showKey: 'categoryName'
        },
        {
          prop: 'status',
          label: '商品状态',
          type: 'dict',
          code: 'CATALOG_ON_SHELVES_STATUS'
        },
        {
          prop: 'extPriceLibraryStatus',
          label: '协议状态',
          type: 'dict',
          code: 'AGREEMENT_STATUS'
        },
        {
          prop: 'brand',
          label: '品牌'
        },
        {
          prop: 'createdFullName',
          label: '创建人'
        },
        {
          prop: 'creationDate',
          label: '创建日期',
          type: 'daterange'
        }
      ],
      tableHeader: [
        {
          prop: 'materialCode',
          label: () => this.$t('common.materialCode'), // 物料编码
          minWidth: 150
        },
        {
          prop: 'materialName',
          label: () => this.$t('common.materialName'), // 物料名称
          minWidth: 150
        },
        {
          prop: 'taxPrice',
          label: () => this.$t('purchaseDemand.taxPrice'), // 含税单价
          minWidth: 150
        },
        {
          prop: 'extMaterialType',
          label: '商品货源类型',
          dataType: 'dict',
          code: 'EXT_MATERIAL_TYPE',
          minWidth: 150
        },
        {
          prop: 'vendorCode',
          label: () => this.$t('common.vendorCode'), //  供应商编码
          minWidth: 150
        },
        {
          prop: 'vendorName',
          label: () => this.$t('common.vendorName'), // 供应商名称
          minWidth: 150
        },
        {
          prop: 'effectiveDate',
          label: '价格开始日期',
          minWidth: 120,
          formattor (val) {
            return val ? parseTime(val, '{y}-{m}-{d}') : ''
          }
        },
        {
          prop: 'expirationDate',
          label: '价格结束日期',
          minWidth: 120,
          formattor (val) {
            return val ? parseTime(val, '{y}-{m}-{d}') : ''
          }
        },
        {
          prop: 'createdFullName',
          label: () => this.$t('orderMod.buyerOrderSynergy.createdBy'),
          minWidth: 100
        },
        {
          prop: 'creationDate',
          label: () => this.$t('orderMod.buyerOrderSynergy.creationDate'),
          minWidth: 100,
          formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
        },
        {
          prop: 'offShelvesBy',
          label: () => this.$t('dataConfMod.remover'), // 下架人
          minWidth: 100
        },
        {
          prop: 'offShelvesReason',
          label: () => this.$t('dataConfMod.offShelvesReason'), // 下架原因
          minWidth: 100
        },
        {
          prop: 'offShelvesDate',
          label: () => this.$t('dataConfMod.removeDate'), // 下架时间
          minWidth: 100,
          formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
        },
        {
          prop: 'farFromOffShelvesDate',
          label: '距离下架时间',
          minWidth: 150,
          formattor: (val, row) => {
            return row.offShelvesDate ? getCountDown(row.offShelvesDate) : null
            // let endTimestamp = Date.parse(row.offShelvesDate) // 下架日期转换为毫秒数
            // let nowTimestamp = Date.parse(new Date()) // 当前日期转换为毫秒数
            // row.farFromOffShelvesDate = Math.ceil((endTimestamp - nowTimestamp) / (1000 * 3600 * 24)) // 转换为天数 向上取整
            // return row.offShelvesDate ? row.farFromOffShelvesDate : null
          }
        },
        {
          prop: 'status',
          label: '商品状态',
          minWidth: 100,
          dataType: 'dict',
          code: 'CATALOG_ON_SHELVES_STATUS'
        },
        {
          prop: 'extPriceLibraryStatus',
          label: '协议状态',
          minWidth: 100,
          dataType: 'dict',
          code: 'AGREEMENT_STATUS'
        },
        {
          prop: 'extAgreementType',
          label: () => this.$t('cusEntry.purchaseCatalogOnOrOff.extAgreementType'),
          minWidth: 100
        },
        {
          prop: 'extOrgNameList',
          label: () => this.$t('purchaseDemand.businessEntity'), // 业务实体
          minWidth: 150
        },
        {
          prop: 'extGoodsCode',
          label: '商品编码',
          minWidth: 150
        },
        {
          prop: 'categoryName',
          label: () => this.$t('dataConfMod.category'), // 品类
          minWidth: 150
        },
        {
          prop: 'priceLibraryNo',
          label: this.$t('bidMod.priceLibraryNo'), // 价格库编号
          minWidth: 150
        },
        {
          prop: 'operation',
          label: () => this.$t('common.operation'),
          width: 120,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            {
              callback: row => this.manageOne(row, 'edit'),
              code: 'pur:purchaseCatalogOnOrOff:manage',
              formattor: () => this.$t('contractMod.manage')
            },
            {
              callback: row => this.manageOne(row, 'view'),
              code: 'pur:purchaseCatalogOnOrOff:view',
              formattor: () => this.$t('common.view')
            }
          ]
        }
      ]
    }
  },
  mounted () {
    this.getQuerydata()
  },
  methods: {
    // 设置距离下架时间为红色 (不满一个月)
    cellStyle ({ row, column, rowIndex, columnIndex }) {
      const now = new Date().getTime()
      const countDown = new Date(row.offShelvesDate).getTime()
      if (now === countDown && column.property == 'farFromOffShelvesDate') {
        return {
          // 'font-weight': 'bold',
          'color': '#FF4A4D'
        }
      }

      const timeObj = calcDate(now, countDown)
      const day = timeObj.days > 0 ? timeObj.days : 0
      const hours = timeObj.hours > 0 ? timeObj.hours : 0
      if ((day < 30 || (day == 30 && hours == 0)) && column.property == 'farFromOffShelvesDate') {
        return {
          // 'font-weight': 'bold',
          'color': '#FF4A4D'
        }
      }
    },
    checkChange (val) {
      this.selection = val
    },
    getQuerydata (obj) {
      let params = obj || {}
      this.queryParam = transformMQL.listPageData({
        type: 'CatalogOnShelves',
        action: 'query',
        params,
        filterOperator: {
          creationDate: 'between',
          status: 'eq'
        }
      })
      this.$nextTick(() => {
        this.$refs.tableRef.query()
      })
    },
    addData () {
      this.$emit('tab-add', {
        component: purchaseCatalogOnOrOffDetail,
        params: {
          flag: 'add',
          tabName: 'purchaseCatalogOnOrOffDetail'
        },
        title: `${this.$t('route.purchaseCatalogOnOrOff')}-新增`,
        name: 'purchaseCatalogOnOrOffDetail'
      })
    },
    manageOne (row, type) {
      this.$emit('tab-add', {
        component: purchaseCatalogOnOrOffDetail,
        params: {
          flag: type,
          row,
          tabName: 'purchaseCatalogOnOrOffDetail' + row.priceLibraryNo
        },
        title: row.priceLibraryNo,
        name: 'purchaseCatalogOnOrOffDetail' + row.priceLibraryNo
      })
    },
    async batchOperation (type) {
      if (this.selection.length == 0) {
        this.$message.warning('请先勾选商品')
        return
      }

      let tipMsg = null
      let callback = null
      if (type == 'timeOn') {
        tipMsg = '是否确认定时上架？'
        callback = this.timeOnBill
      } else if (type == 'timeOff') {
        tipMsg = '是否确认取消定时上架？'
        callback = this.timeOffBill
      } else if (type == 'batchOn') {
        tipMsg = '是否确认批量上架？'
        callback = this.onBill
      }

      const confirmResult = await this.$confirm(tipMsg, {
        confirmButtonText: this.$t('common.affirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => {})
      if (confirmResult !== 'confirm') return
      callback()
    },
    // 上架
    onBill () {
      let saveData = transformMQL.save('CatalogOnShelves', this.selection, 'batchOnShelves')
      this.$http({
        url: '/api-sup-ce/api-ql/CatalogOnShelves/batchOnShelves',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(() => {
        this.$message.success('上架成功！')
        this.getQuerydata()
      })
    },
    // 定时上架
    timeOnBill () {
      const saveData = transformMQL.save('CatalogOnShelves', this.selection, 'batchScheduledShelves')
      this.$http({
        url: '/api-sup-ce/api-ql/CatalogOnShelves/batchScheduledShelves',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(() => {
        this.$message.success('定时上架成功！')
        this.getQuerydata()
      })
    },
    // 取消定时上架
    timeOffBill () {
      const saveData = transformMQL.save('CatalogOnShelves', this.selection, 'batchUntimedShelves')
      this.$http({
        url: '/api-sup-ce/api-ql/CatalogOnShelves/batchUntimedShelves',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(() => {
        this.$message.success('取消定时上架成功！')
        this.getQuerydata()
      })
    },
    // 多个文件上传成功
    mulUploadSuccess (file, fileList, uploadNum) {
      const { id, name } = file
      // 改变fileList会报错，故使用一个新的数组来保存每次上传的图片信息
      this.fileAttachList.push({
        ...file,
        attachId: id,
        attachName: name,
        fileuploadId: id,
        fileSourceName: name
      })
      console.log(this.fileAttachList.length, uploadNum)
      // 所有文件上传成功后调用批量导入接口
      if (this.fileAttachList.length == uploadNum) {
        const saveData = transformMQL.save('CatalogOnShelvesAttach', this.fileAttachList, 'batchImportPic')
        // 每次上传完图片数组后清空数据
        this.fileAttachList = []
        this.$refs.cUploadFile.handleClearFile()
        this.$http({
          url: '/api-sup-ce/api-ql/CatalogOnShelvesAttach/batchImportPic',
          method: 'POST',
          data: saveData,
          loading: true
        }).then(() => {
          this.$message.success('导入图片成功！')
          this.getQuerydata()
        })
      }
    }
  }
}
</script>

<style scoped lang="scss"></style>
