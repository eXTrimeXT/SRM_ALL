<template>
  <div class="bond-management">
    <p>
      <el-button @click="getBondsData">
        {{ $t('common.refresh') }}
      </el-button>
    </p>
    <el-table
      :data="bondList"
      style="width: 100%"
      border
      height="400px"
      highlight-current-row
    >
      <el-table-column
        type="index"
        :label="$t('common.sort')"
        width="50"
      />

      <!--供应商编码-->
      <el-table-column
        prop="vendorCode"
        :label="$t('bidMod.vendorCode')"
        width="120"
        show-overflow-tooltip
      />

      <!--供应商名称-->
      <el-table-column
        prop="vendorName"
        :label="$t('bidMod.vendorName')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--联系人-->
      <el-table-column
        prop="linkManName"
        :label="$t('vendorMod.contactPerson')"
        min-width="100"
        show-overflow-tooltip
      />

      <!--电话-->
      <el-table-column
        prop="phone"
        :label="$t('common.phone')"
        min-width="100"
        show-overflow-tooltip
      />

      <!--邮箱-->
      <el-table-column
        prop="email"
        :label="$t('bidMod.email2')"
        min-width="120"
        show-overflow-tooltip
      />

      <!--保证金缴纳时间-->
      <el-table-column
        prop="payDate"
        :label="$t('bidMod.common.payDate')"
        width="150"
        show-overflow-tooltip
      />

      <!--缴纳证明-->
      <SrmCommonFile
        type="table-column"
        :table-column-options="{
          label: '缴纳证明',
          prop: 'payDocId',
          nameProp: 'payFileName'
        }"
        readonly
      />

      <!--是否缴纳保证金-->
      <el-table-column
        :label="$t('bidMod.ifDeposit')"
        width="130"
      >
        <template v-slot="{ row }">
          <el-switch
            v-model="row.hasPay"
            active-value="Y"
            inactive-value="N"
            @change="bondHasPayChange(row)"
          />
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
/**
 * 采购商 - 保证金管理 - 重构的
 */
import { validatorBusinessType } from 'lib@/composition/origin/composition'
import { getBondManagementApiParams } from './utils'
import { transformMQL } from 'lib@/utils/util'
import { compHttp } from './api'

export default {
  name: 'BondManagement',

  props: {
    // 业务类型
    businessType: {
      type: String,
      required: true,
      validator: value => validatorBusinessType(value)
    },
    // 单据基础信息 { id, idKey }
    baseInfo: {
      type: Object,
      required: true,
      default: () => {
        return {
          id: '',
          idKey: ''
        }
      }
    },
    isActiveMenu: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      bondList: []
    }
  },

  watch: {
    isActiveMenu: {
      handler (nVal) {
        if (nVal) {
          this.getBondsData()
        }
      },
      immediate: true
    }
  },

  methods: {
    /* 获取保证金缴纳数据 */
    async getBondsData () {
      let transformParams = transformMQL.save('AuctSouProjectForBuyer', {
        filter: {
          projectId: {
            eq: this.baseInfo.id
          }
        },
        page: {
          pageNum: 1,
          pageSize: 1000
        }
      }, 'listVendorBonds')

      const response = await compHttp.listVendorBonds(transformParams)
      if (response) {
        this.bondList = (response.data.records || []).concat().map(item => ({
          ...item,
          hasPay: item.bondStatus === 'PASS' ? 'Y' : 'N'
        }))
      }
    },

    /* 是否缴纳保证金切换 */
    async bondHasPayChange (row) {
      console.log('hasPay', row.hasPay)
      let transformParams = transformMQL.save('AuctSouProjectForBuyer', [{
        projectId: this.baseInfo.id,
        vendorIds: [row.vendorId],
        toConfirm: !!(row.hasPay === 'Y')
      }], 'confirmBonds')

      const response = await compHttp.confirmBonds(transformParams)

      if (response) {
        this.$message.success(this.$t('common.successUpdate'))
        this.$emit('success', row.hasPay)
      }
    }
  }
}
</script>
