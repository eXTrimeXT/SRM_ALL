<template>
  <div>
    <div class="risk-info red">
      当前供应商存在{{ riskCount }}处风险
    </div>
    <BaseTable
      stripe
      index
      :data="riskData"
      :columns="riskColumns"
      :empty-text="$t('components.noData')"
      border
      :cell-class-name="setCellClass"
    />
    <div class="abnormal-info">
      异常详情
    </div>
    <BaseTable
      stripe
      index
      :data="abnormalData"
      :columns="abnormalColumns"
      :empty-text="$t('components.noData')"
      border
    />
  </div>
</template>
<script>
import BaseTable from 'lib@/components/BaseTable'
import { transformMQL } from 'lib@/utils/util'
import recommendHttp from 'modcb@/supplierRecommend/api'
import caHttp from 'modcb@/caManagement/views/calibrationApply/api'

export default {
  components: {
    BaseTable
  },
  data () {
    return {
      riskCount: 0,
      riskColumns: [
        {
          attrs: {
            label: '序号',
            type: 'index',
            width: 60,
            fixed: 'left'
          }
        },
        {
          attrs: {
            prop: 'vendorCode',
            label: '供应商编码',
            minWidth: 120,
            fixed: 'left',
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'vendorName',
            label: '推荐供应商',
            minWidth: 150,
            fixed: 'left',
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'timeLimitFlag',
            label: '时间受限',
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'extIsMainPoint',
            label: '重点关注',
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'groupBlacklistFlag',
            label: '是否集团黑名单',
            minWidth: 120,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'positionLimitFlag',
            label: '是否单位受限',
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'categoryLimitFlag',
            label: '是否品类受限',
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'keySupervisionFlag',
            label: '是否重点监督',
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'extIsDishonesty',
            label: '是否失信',
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'extIsBizAnomaly',
            label: '是否经营异常',
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'contackRepeatFlag',
            label: '联系人是否重复',
            minWidth: 120,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'telRepeatFlag',
            label: '联系人电话是否重复',
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'emailRepeatFlag',
            label: '联系人邮箱是否重复',
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'holderRepeatFlag',
            label: '股东是否重复',
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'legalRepeatFlag',
            label: '法人是否重复',
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'relBlacklistRepeatFlag',
            label: '关联关系供应商是否黑名单',
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'mainPeopleRepeatFlag',
            label: '主要人员是否重复',
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        }
      ],
      riskData: [
      ],
      abnormalColumns: [
        {
          attrs: {
            type: 'index',
            label: '序号',
            width: 60
          }
        },
        {
          attrs: {
            prop: 'type',
            label: '异常类型',
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'description',
            label: '异常说明',
            showOverflowTooltip: true
          }
        }
      ],
      abnormalData: [],
      caId: null
    }
  },
  async created () {
    this.caId = this.$attrs.params.caId
    this.projectId = this.$attrs.params.projectId
    if (this.caId) {
      await this.getCaDetail()
    } else if (this.projectId) {
      await this.getSupplierDetail()
    }
  },
  methods: {
    setCellClass ({ row, column, rowIndex, columnIndex }) {
      if (row[column.property] === 'Y') return 'red'
    },
    async getCaDetail () {
      let transformParams = transformMQL.save('Ca', [this.caId], 'read',
        {
          '*': {},
          'caOrders': {
            '*': {}
          },
          'caSuppliers': {
            '*': {}
          },
          'caSelectionResults': {
            '*': {}
          },
          'sceneFiles': {
            '*': {}
          }
        }
      )
      const response = await caHttp.read(transformParams)
      if (response.data.length) {
        const { applicantNo, caSuppliers } = response.data[0]
        this.getFormDetail(applicantNo, caSuppliers)
      }
    },
    async getSupplierDetail () {
      let transformParams = transformMQL.save('RecommvendorProject', [this.projectId], 'read',
        {
          '*': {},
          'recommvendorList': {
            '*': {}
          },
          'recommvendorProjectExtend': {
            '*': {}
          }
        }
      )
      const response = await recommendHttp.read(transformParams)
      if (response.data.length) {
        const { recommvendorList, applicantNo } = response.data[0]

        this.getFormDetail(applicantNo, recommvendorList)
      }
    },
    async getFormDetail (applicantNo, caSuppliers) {
      let recommvendorList = caSuppliers.filter(item => item.vendorId).map(item => ({
        vendorId: item.vendorId
      }))
      let transformParams = transformMQL.save('Recommvendor', {
        applicantNo,
        recommvendorList
      }, 'queryRisk')
      const response = await recommendHttp.queryRisk(transformParams)
      if (response.data.records.length) {
        const { riskItemList = [], vendorRiskList = [] } = response.data.records[0].vendorRisk
        this.riskData = vendorRiskList || []
        this.abnormalData = riskItemList || []
        if (this.riskData.length) {
          let count = 0
          for (let item of this.riskData) {
            for (let key in item) {
              if (item[key] === 'Y') {
                count++
              }
            }
          }
          this.riskCount = count
        }
      }
    }
  }
}
</script>
<style style="scss" scoped>
.red {
  color:red;
}
.risk-info {
  margin-bottom: 10px;
}
.abnormal-info {
  font-size: 14px;
  font-weight: bold;
  margin: 10px 0;
}
</style>
<style style="scss">
.red .cell {
  color:red;
}
</style>
