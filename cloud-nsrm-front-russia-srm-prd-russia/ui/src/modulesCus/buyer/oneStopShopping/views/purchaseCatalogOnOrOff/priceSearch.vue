<template>
  <!-- 价格库编号 -->
  <srm-dialog
    :title="$t('bidMod.priceLibraryNo')"
    size="large"
    :destroy-on-close="true"
    :visible.sync="priceVisible"
    :close-on-click-modal="false"
    v-bind="$attrs"
    v-on="$listeners"
  >
    <FormWrapper
      :form-array="preArr"
      @getFormData="getQuerydata"
    />
    <TableView
      v-if="priceVisible"
      ref="tableRef"
      style="height: 250px;"
      row-key="priceLibraryId"
      :rowIndex="false"
      :radioOptions="{ enabled: true, labelKey: 'priceLibraryId'}"
      :table-header="tableHeader"
      :pre-query-data="queryParam"
      :auto-query="false"
      :url="url"
      :reserve-selection="true"
      :comActive="$attrs['changeTab']"
      @radio-change="radioChange"
    />
    <div slot="footer" class="dialog-footer">
      <el-button @click="priceVisible = false">
        {{ $t('common.cancel') }}
      </el-button>
      <el-button type="primary" @click="priceComfirm">
        {{ $t('common.confirm') }}
      </el-button>
    </div>
  </srm-dialog>
</template>

<script>
import FormWrapper from 'lib@/components/Table/FormWrapper'
import TableView from 'lib@/components/Table/TableView'
import { parseTime } from '@/utils'
export default {
  name: 'PriceSearch',
  components: {
    TableView,
    FormWrapper
  },
  props: {
    url: {
      type: String,
      default: 'api-sou/jcAgreement/getJcAgreementLinePageList'
    },
    priceVisible: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      userType: this.$store.getters.user.userType, // VENDOR | BUYER
      currentRow: {},
      queryParam: {},
      preArr: [
        {
          prop: 'supName',
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorName')
        },
        {
          prop: 'materialName',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialName')
        }
      ],
      tableHeader: [
        {
          prop: 'agreementCode',
          label: this.$t('bidMod.priceLibraryNo'), // 价格库编号
          minWidth: 140
        },
        {
          prop: 'supName',
          label: () => this.$t('common.vendorName'), // 供应商名称
          minWidth: 150
        },
        {
          prop: 'supCode',
          label: () => this.$t('common.vendorCode'), //  供应商编码
          minWidth: 150
        },
        {
          prop: 'materialName',
          label: () => this.$t('common.materialName'), // 物料名称
          minWidth: 150
        },
        {
          prop: 'materialCode',
          label: () => this.$t('common.materialCode'), // 物料编码
          minWidth: 150
        },
        {
          prop: 'extOrgNameList',
          label: () => this.$t('purchaseDemand.businessEntity'), // 业务实体
          minWidth: 150,
          formattor: (val, row) => {
            let buyOrgId = row.sccSouJcAgreementOrgList.map(item => item.buyOrgId)
            let buyOrgCode = row.sccSouJcAgreementOrgList.map(item => item.buyOrgCode)
            let buyOrgName = row.sccSouJcAgreementOrgList.map(item => item.buyOrgName)
            row.extOrgIdList = buyOrgId.join(',')
            row.extOrgCodeList = buyOrgCode.join(',')
            row.extOrgNameList = buyOrgName.join(',')
            return row.extOrgNameList
          }
        },
        {
          prop: 'extAreaName',
          label: '区域',
          minWidth: 150,
          formattor: (val, row) => {
            let extAreaId = row.areaList.map(item => item.dictItemId)
            let extAreaCode = row.areaList.map(item => item.dictItemCode)
            let extAreaName = row.areaList.map(item => item.dictItemName)
            row.extAreaId = extAreaId.join(',')
            row.extAreaCode = extAreaCode.join(',')
            row.extAreaName = extAreaName.join(',')
            return row.extAreaName
          }
        },
        {
          prop: 'priceTax',
          label: '未税单价',
          minWidth: 150
        },
        {
          prop: 'ratePrice',
          label: '含税单价',
          minWidth: 150
        },
        {
          prop: 'currencyType',
          label: () => this.$t('purchaseDemand.currency'), // 币种
          minWidth: 150,
          formattor: val => val ? this.$getDictLabel('currency', val) : ''
        },
        {
          prop: 'effectiveStartDate',
          label: '有效开始时间',
          minWidth: 150,
          formattor: val => val ? parseTime(val, '{y}-{m}-{d}') : ''
        },
        {
          prop: 'effectiveEndDate',
          label: '有效结束时间',
          minWidth: 150,
          formattor: val => val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      ]
    }
  },
  watch: {
    priceVisible (oldValue, newValue) {
      if (!newValue) {
        this.getQuerydata()
      }
    }
  },
  created () {
    if (this.userType === 'VENDOR') {
      // 供方去除供应商查询
      this.preArr.splice(0, 1)
    }
  },
  methods: {
    radioChange (select) {
      const { status, createdUserName, creationDate, areaList = [], sccSouJcAgreementOrgList = [], ...rest } = select
      this.currentRow = {
        priceLibraryNo: select.agreementCode, // 协议编号-价格库编号
        priceLibraryId: select.agreementId, // 协议ID-价格库ID
        extOrgIdList: select.extOrgIdList, // 业务实体
        extOrgCodeList: select.extOrgCodeList,
        extOrgNameList: select.extOrgNameList,
        extAreaId: select.extAreaId, // 区域
        extAreaCode: select.extAreaCode,
        extAreaName: select.extAreaName,
        vendorName: select.supName,
        vendorCode: select.supCode,
        vendorId: select.supId,
        materialCode: select.materialCode,
        materialName: select.materialName,
        extMaterialType: select.materialType, // 商品货源类型
        categoryId: select.goodsTypeId, // 品类名称-商品分类
        categoryCode: select.goodsTypeCode, // 品类名称-商品分类
        categoryName: select.goodsTypeName, // 品类名称-商品分类
        unit: select.unit,
        effectiveDate: parseTime(select.effectiveStartDate, '{y}-{m}-{d}'), // 价格开始日期
        expirationDate: parseTime(select.effectiveEndDate, '{y}-{m}-{d}'), // 价格结束日期
        extProtocolRowNo: select.materialLine, // 协议行号-物料行号
        extGoodsName: select.materialName, // 商品名称-物料名称
        notaxPrice: select.priceTax,
        taxPrice: select.ratePrice,
        extReferencePrice: select.referencePrice, // 参考价
        taxRate: select.taxRate, // 税率
        currencyCode: select.currencyType, // 币种
        orderQuantityMinimum: select.startNum, // 起订量
        deliveryCycle: select.leadTime, // 交货周期（自然日）
        extShelfLife: select.sellByDate, // 质保期（自然日）
        brand: select.brand, // 品牌
        specification: select.standards // 规格
      }
    },
    priceComfirm () {
      this.$emit('priceComfirm', this.currentRow)
    },
    getQuerydata (obj) {
      this.queryParam = obj || this.queryParam
      this.$nextTick(() => {
        this.$refs.tableRef.query()
      })
    }
  }
}
</script>

<style></style>
