<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <el-collapse v-model="colValue">
        <!-- 版本记录 -->
        <el-collapse-item :title="$t('cusEntry.supplement20250121.versionRecord')" name="1">
          <BaseTable
            stripe
            index
            :data="tableData"
            :columns="tableColumns"
            :empty-text="$t('components.noData')"
            border
          >
            <template #operate="scope">
              <!-- 查看 -->
              <el-button type="text" @click="viewDetail(scope.row)">
                {{ $t('common.view') }}
              </el-button>
            </template>
          </BaseTable>
        </el-collapse-item>
      </el-collapse>
    </el-main>
  </el-container>
</template>
<script>
import BaseTable from 'lib@/components/BaseTable'
import { centralHttp } from 'modcb@/jcAgreement/api'
import RecordDetail from './recordDetail'

export default {
  name: 'RecordDetail',
  components: {
    BaseTable,
    RecordDetail
  },
  data () {
    return {
      colValue: ['1', '2'],
      tableData: [],
      tableColumns: [],
      agreementId: null
    }
  },
  mounted () {
    this.tableColumns = [
      {
        attrs: {
          label: this.$t('common.sort'), //'序号',
          type: 'index',
          width: 100
        }
      },
      {
        attrs: {
          prop: 'changeVersion',
          label: this.$t('dataConfMod.version'), // '版本号'
        }
      },
      {
        attrs: {
          prop: 'operate',
          label: this.$t('common.operation'), // '操作'
        },
        slot: 'operate'
      }
    ]
    const { agreementId } = this.$attrs.params.row
    this.agreementId = agreementId
    if (agreementId) {
      this.getFormDetail(agreementId)
    }
  },
  methods: {
    viewDetail (row) {
      // 变更记录
      let tabName = `${this.$t('vendorMod.changeRecord')}${this.agreementId}${row.changeVersion}`
      const tab = {
        component: RecordDetail,
        params: {
          row: {
            agreementId: this.agreementId,
            version: row.changeVersion
          },
          tabName: tabName
        },
        title: this.$t('vendorMod.changeRecord') + row.changeVersion,
        name: tabName
      }
      this.$emit('tab-add', tab)
    },
    async getFormDetail (agreementId) {
      const response = await centralHttp.getChangeJcAgreementList({
        agreementId
      })
      if (response) {
        this.tableData = response.data || []
      }
    }
  }
}
</script>
