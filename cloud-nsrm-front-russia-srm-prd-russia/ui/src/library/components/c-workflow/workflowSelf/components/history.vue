<template>
  <div>
    <el-steps :active="curStatus" :align-center="true" finish-status="success">
      <el-step :title="$t('vendorMod.finishCompanyInfo')" description="这是一段很长很长很长的描述性文字" />
      <el-step :title="$t('common.successSubmit')" description="这是一段很长很长很长的描述性文字" />
      <el-step :title="$t('vendorMod.approvalSuccess')" description="这是一段很长很长很长的描述性文字" />
    </el-steps>
    <vxe-table
      ref="metadataListTable"
      border
      show-overflow="tooltip"
      keep-source
      stripe
      align="left"
      min-height="500"
      :data="metadataList"
    >
      <template #empty>
        <div style="color: #96999c;">
          <img style="margin-top: 16px" :src="emptyIcon" alt="">
          <p>{{ $t('components.common.noMoreData') }}</p>
        </div>
      </template>
      <!--序号-->
      <vxe-column :title="$t('common.sort')" type="seq" width="60" />
      <!--节点-->
      <vxe-column
        field="fdNodeName"
        :title="$t('components.processTable.headers.fdNodeName')"
        min-width="120"
      >
        <template #default="{ row }">
          {{ row.columnName }}
        </template>
      </vxe-column>
      <!--处理人-->
      <vxe-column
        field="operator"
        :title="$t('vendorMod.operator')"
        min-width="120"
      >
        <template #default="{ row }">
          {{ row.columnName }}
        </template>
      </vxe-column>
      <!--操作-->
      <vxe-column
        field="operation"
        :title="$t('common.operation')"
        min-width="120"
      >
        <template #default="{ row }">
          {{ row.columnName }}
        </template>
      </vxe-column>
      <!--处理意见-->
      <vxe-column
        field="auditNote"
        :title="$t('flowMod.auditNote')"
        min-width="120"
      >
        <template #default="{ row }">
          {{ row.columnName }}
        </template>
      </vxe-column>
      <!--附件-->
      <vxe-column
        field="attachment"
        :title="$t('bidMod.attachment')"
        min-width="120"
      >
        <template #default="{ row }">
          {{ row.columnName }}
        </template>
      </vxe-column>
      <!--操作时间-->
      <vxe-column
        field="operationTime"
        :title="$t('common.operationTime')"
        min-width="120"
      >
        <template #default="{ row }">
          {{ row.columnName }}
        </template>
      </vxe-column>
    </vxe-table>
  </div>
</template>

<script>

import emptyIcon from '@/assets/table/empty.svg'

export default {
  name: 'History',
  components: {
  },
  props: {
    funParams: {
      type: Object,
      default: function () {
        return {}
      }
    }
  },
  data () {
    return {
    }
  },
  watch: {
  },
  created () {

  },
  methods: {
  }
}
</script>
