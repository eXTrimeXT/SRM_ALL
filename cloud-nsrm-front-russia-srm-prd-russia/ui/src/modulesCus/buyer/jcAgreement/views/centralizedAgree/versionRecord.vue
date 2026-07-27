<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <el-collapse v-model="colValue">
        <el-collapse-item title="版本记录" name="1">
          <BaseTable
            stripe
            index
            :data="tableData"
            :columns="tableColumns"
            :empty-text="$t('components.noData')"
            border
          >
            <template #operate="scope">
              <el-button type="text" @click="viewDetail(scope.row)">
                查看
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
          label: '序号',
          type: 'index',
          width: 100
        }
      },
      {
        attrs: {
          prop: 'changeVersion',
          label: '版本号'
        }
      },
      {
        attrs: {
          prop: 'operate',
          label: '操作'
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
      const tab = {
        component: RecordDetail,
        params: {
          row: {
            agreementId: this.agreementId,
            version: row.changeVersion
          },
          tabName: `变更记录${this.agreementId}${row.changeVersion}`
        },
        title: '变更记录' + row.changeVersion,
        name: `变更记录${this.agreementId}${row.changeVersion}`
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
