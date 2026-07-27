<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <el-collapse v-model="colValue">
        <el-collapse-item title="基本信息" name="1">
          <BaseTable
            stripe
            index
            :data="tableData1"
            :columns="tableColumns1"
            :empty-text="$t('components.noData')"
            border
            max-height="250px"
          />
        </el-collapse-item>
        <el-collapse-item title="协议信息" name="2">
          <BaseTable
            stripe
            index
            :data="tableData2"
            :columns="tableColumns2"
            :empty-text="$t('components.noData')"
            border
            max-height="250px"
          />
        </el-collapse-item>
      </el-collapse>
    </el-main>
  </el-container>
</template>
<script>
import BaseTable from 'lib@/components/BaseTable'
import { centralHttp } from 'modcb@/jcAgreement/api'

export default {
  name: 'RecordDetail',
  components: {
    BaseTable
  },
  data () {
    return {
      colValue: ['1', '2'],
      tableData1: [],
      tableColumns1: [],
      tableData2: [],
      tableColumns2: []
    }
  },
  mounted () {
    this.tableColumns1 = [
      {
        attrs: {
          label: '序号',
          type: 'index',
          width: 100
        }
      },
      {
        attrs: {
          prop: 'fieldName',
          label: '字段名称'
        }
      },
      {
        attrs: {
          prop: 'newValue',
          label: '变更值'
        }
      }
    ]
    this.tableColumns2 = [
      {
        attrs: {
          label: '序号',
          type: 'index',
          width: 100
        }
      },
      {
        attrs: {
          prop: 'materialCode',
          label: '物料编码'
        }
      },
      {
        attrs: {
          prop: 'materialName',
          label: '物料名称'
        }
      },
      {
        attrs: {
          prop: 'fieldName',
          label: '变更字段'
        }
      },
      {
        attrs: {
          prop: 'newValue',
          label: '变更值'
        }
      }
    ]
    const { agreementId, version } = this.$attrs.params.row
    if (agreementId && version) {
      this.getFormDetail(agreementId, version)
    }
  },
  methods: {
    async getFormDetail (agreementId, version) {
      const response = await centralHttp.getChangeJcAgreementInfo({
        agreementId,
        version
      })
      if (response && response.data) {
        const { xy = [], xyInfo = [] } = response.data || {}
        this.tableData1 = xy
        this.tableData2 = xyInfo
      }
    }
  }
}
</script>
