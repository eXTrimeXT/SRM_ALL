<template>
  <el-container class="flex-container the_vendorPurchaseOrderList_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :form-array="preArr" form-label-width="120px" @getFormData="getQuerydata" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <!-- 新增 -->
          <!-- <AuthorityButton
            code="sup:purchaseCatalogOnOrOffSupplier:add"
            type="primary"
            @click="addData"
          >
            {{ $t('orderMod.buyerOrderSynergy.add') }}
          </AuthorityButton> -->
          <CUploadFile
            title="批量上传图片"
            :cus-data="fileInfo"
            :fileList="fileList"
            :showFileList="false"
            @upload-success="(file,fileList,uploadNum) => mulUploadSuccess(file,fileList,uploadNum)"
          />
        </template>
      </MainHeader>
      <TableView
        ref="tableRef"
        row-key="priceLibraryId"
        :bigData="true"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :auto-query="false"
        :adeptMeiQl="true"
        :cell-style="cellStyle"
        :open-custom-table="true"
        :reserve-selection="true"
        :comActive="$attrs['changeTab']"
        url="/api-sup-ce/api-ql/CatalogOnShelvesVendor/query"
      />
    </el-main>
  </el-container>
</template>
<script>
import { parseTime } from '@/utils'
import CUploadFile from 'modcb@/oneStopShopping/views/purchaseCatalogOnOrOff/c-upload-file'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import TableView from 'lib@/components/Table/TableView'
import purchaseCatalogOnOrOffDetail from './purchaseCatalogOnOrOffDetail'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'PurchaseCatalogOnOrOffList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    CUploadFile
  },
  data () {
    return {
      queryParam: {},
      fileAttachList: [],
      fileList: [],
      fileInfo: {
        uploadType: 'DEF', // 固定参数
        sourceType: 'WEB_APP', // 固定参数
        fileModular: 'CatalogOnShelvesVendor', // 文件所属模块 -》基础模块
        fileFunction: 'CatalogOnShelvesVendor', // 文件所属功能
        fileType: 'images' // 文件所属类型file（所有）、images
      },
      preArr: [
        {
          prop: 'extOrgIdList',
          label: () => this.$t('oneStopShopping.businessEntity')
          // type: 'OUorganizationSelector'
        },
        {
          prop: 'materialCode',
          label: () => this.$t('common.materialCode')
          // type: 'quicksearch',
          // showKey: 'materialCode',
          // propKey: 'materialId',
          // name: 'scc_base_material_item'
        },
        {
          prop: 'categoryId',
          label: () => this.$t('common.categoryName'), // '品类名称'
          type: 'catSelect',
          propKey: 'categoryId',
          showKey: 'categoryName'
        }
      ],
      tableHeader: [
        {
          prop: 'materialCode',
          label: () => this.$t('common.materialCode'), // 物料编码
          width: 150
        },
        {
          prop: 'materialName',
          label: () => this.$t('common.materialName'), // 物料名称
          width: 150
        },
        {
          prop: 'taxPrice',
          label: () => this.$t('purchaseDemand.taxPrice'), // 含税单价
          width: 150
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
          width: 120,
          formattor (val) {
            return val ? parseTime(val, '{y}-{m}-{d}') : ''
          }
        },
        {
          prop: 'expirationDate',
          label: '价格结束日期',
          width: 120,
          formattor (val) {
            return val ? parseTime(val, '{y}-{m}-{d}') : ''
          }
        },
        {
          prop: 'createdFullName',
          label: () => this.$t('orderMod.buyerOrderSynergy.createdBy'),
          width: 100
        },
        {
          prop: 'creationDate',
          label: () => this.$t('orderMod.buyerOrderSynergy.creationDate'),
          width: 100,
          formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
        },
        {
          prop: 'offShelvesBy',
          label: () => this.$t('dataConfMod.remover'), // 下架人
          width: 100
        },
        {
          prop: 'offShelvesReason',
          label: () => this.$t('dataConfMod.offShelvesReason'), // 下架原因
          width: 100
        },
        {
          prop: 'offShelvesDate',
          label: () => this.$t('dataConfMod.removeDate'), // 下架时间
          width: 100,
          formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
        },
        {
          prop: 'farFromOffShelvesDate',
          label: '距离下架时间（天）',
          minWidth: 150,
          formattor: (val, row) => {
            let endTimestamp = Date.parse(row.offShelvesDate) // 下架日期转换为毫秒数
            let nowTimestamp = Date.parse(new Date()) // 当前日期转换为毫秒数
            row.farFromOffShelvesDate = Math.ceil((endTimestamp - nowTimestamp) / (1000 * 3600 * 24)) // 转换为天数 向上取整
            return row.offShelvesDate ? row.farFromOffShelvesDate : null
          }
        },
        {
          prop: 'status',
          label: '商品状态',
          width: 100,
          btnStyle: 'text',
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
          prop: 'extOrgNameList',
          label: () => this.$t('purchaseDemand.businessEntity'), // 业务实体
          width: 150
        },
        {
          prop: 'extGoodsCode',
          label: '商品编码',
          minWidth: 150
        },
        {
          prop: 'categoryName',
          label: () => this.$t('dataConfMod.category'), // 品类
          width: 150
        },
        {
          prop: 'priceLibraryNo',
          label: this.$t('bidMod.priceLibraryNo'), // 价格库编号
          width: 150
        },
        {
          prop: 'operation',
          label: () => this.$t('common.operation'),
          width: 120,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            // {
            //   callback: row => this.manageOne(row, 'edit'),
            //   code: 'sup:purchaseCatalogOnOrOffSupplier:manage',
            //   // 待提交、审核驳回
            //   show: row =>
            //     ['TO_BE_SUBMIT', 'REJECTED'].includes(row.status),
            //   formattor: () => this.$t('contractMod.manage')
            // },
            {
              // 只可查看：待上架，已定时待上架
              callback: row => this.manageOne(row, 'view'),
              code: 'sup:purchaseCatalogOnOrOffSupplier:view',
              show: row => ['TO_BE_ON_SHELVES', 'SCHEDULED_SHELVES'].includes(row.status),
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
      if (row.farFromOffShelvesDate < 30 && column.property == 'farFromOffShelvesDate') {
        return {
          'font-weight': 'bold',
          'color': '#FF4A4D'
        }
      }
    },
    getQuerydata (obj) {
      let params = obj || {}
      this.queryParam = transformMQL.listPageData({
        type: 'CatalogOnShelvesVendor',
        action: 'query',
        params
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
        title: this.$t('route.purchaseCatalogOnOrOff'),
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
      // 所有文件上传成功后调用批量导入接口
      if (this.fileAttachList.length == uploadNum) {
        const saveData = transformMQL.save('CatalogOnShelvesAttachVendor', this.fileAttachList, 'batchImportPic')
        // 每次上传完图片数组后清空数据
        this.fileAttachList = []
        this.$http({
          url: '/api-sup-ce/api-ql/CatalogOnShelvesAttachVendor/batchImportPic',
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
