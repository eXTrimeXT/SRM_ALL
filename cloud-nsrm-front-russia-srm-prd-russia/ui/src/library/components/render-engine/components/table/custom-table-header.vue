<script setup lang="ts">
import Sortable from 'sortablejs'
import { watch, nextTick, ref } from 'vue-demi'
import { useExpressionScope } from '@meicloud/render-engine'
import { Button as ElButton, Dialog as ElDialog, Input as ElInput, Checkbox as ElCheckbox, Switch as ElSwitch } from '@meicloud/element-ui'
import { useSetupContext } from '../../../composables/useSetupContext'
import { useRenderEngineConfig } from '../../context'
import drag from '@/assets/table/drag.svg'

const props = withDefaults(
  defineProps<{ columns: any[], tableKey: string }>(),
  {
    columns: () => []
  }
)

const emits = defineEmits<{
  (e: 'change', v: any[]): void
}>()

const dragIcon = drag

const [loading, loadingToggle] = useToggle()

const { baseRequest, configRef } = useRenderEngineConfig()
const dynamicTableConfigId = ref('')

const commonRequestParams = () => ({
  formKey: configRef.value.schemaKey,
  tableKey: props.tableKey
})

const handleRequest = (config = {}) => {
  loadingToggle(true)

  // TODO 内置
  return baseRequest({
    service: 'cm',
    type: 'DynamicTableConfig',
    ...config
  }).catch((err) => {
    console.error(err.message)
  }).finally(() => {
    loadingToggle(false)
  })
}

const getCustomTableHeaderConfig = () => {
  handleRequest({
    action: 'query',
    payload: {
      filter: commonRequestParams()
    }
  }).then(res => {
    if (!res?.records) {
      return
    }

    dynamicTableConfigId.value = res.records[0]
    const resTableConfig = res.ref.DynamicTableConfig ? res.ref.DynamicTableConfig[dynamicTableConfigId.value].tableConfig : '[]'

    const columns = JSON.parse(resTableConfig)

    emits('change', columns.map(col => ({ ...col,
      fixed: col.fixed ?? (
        // 兼容旧数据
        col.lockLeft === 'Y'
          ? 'left'
          : col.lockRight === 'Y'
            ? 'right'
            : null
      ),
      visible: col.visible ?? col['x-visible'] ?? true,
      lockLeft: null,
      lockRight: null
    })))
  })
}

getCustomTableHeaderConfig()

const { currentInstance } = useSetupContext()

const scopeRef = useExpressionScope()
const [visible, visibleToggle] = useToggle()

const innerColumns = ref([])

const handleCreateRowSortable = () => {
  nextTick(() => {
    const xTable = currentInstance.$refs.table

    Sortable.create(xTable.$el.querySelector('.body--wrapper>.vxe-table--body tbody'), {
      handle: '.drag-block',
      animation: 180,
      delay: 1,
      onEnd: ({ newIndex, oldIndex }) => {
        const curRow = innerColumns.value.splice(oldIndex, 1)[0]
        innerColumns.value.splice(newIndex, 0, curRow)

        innerColumns.value = innerColumns.value.map((col, idx) => ({ ...col, 'x-index': idx + 1 }))
      }
    })
  })
}

const initInnerColumnsData = (isReset?: boolean) => {
  innerColumns.value = props.columns.map(col => ({
    ...col.columnProps
  }))
}

watch(() => props.columns, () => {
  initInnerColumnsData()
}, { immediate: true })

watch(visible, (bool) => {
  if (bool && !currentInstance.$refs.table) {
    handleCreateRowSortable()
  }
})

const close = () => {
  visibleToggle(false)
}

const handleSubmit = useDebounceFn(() => {
  const tableConfig = innerColumns.value.map(({ _X_ROW_KEY, customRender, ...last }) => last)

  handleRequest({
    action: 'save',
    payload: [{
      ...commonRequestParams(),
      id: dynamicTableConfigId.value,
      tableConfig: JSON.stringify(tableConfig)
    }]
  }).then(() => {
    emits('change', tableConfig)
    close()
  })
}, 216)

const reset = () => {
  initInnerColumnsData(true)

  nextTick(() => {
    handleSubmit()
  })
}

const switchFixed = (row, fixed) => {
  row.fixed = fixed
}
</script>

<template>
  <div class="custom-table-header">
    <ElButton
      class="custom-table-header__btn"
      icon="iconfont iconiconchilun"
      type="text"
      @click="visibleToggle(true)"
    >
      {{ scopeRef.$t('customTable.tableTitle') }}
    </ElButton>

    <ElDialog
      :visible="visible"
      :title="scopeRef.$t('customTable.tableTitle')"
      showClose
      append-to-body
      class="custom-table-dialog"
      @close="close"
    >
      <!-- <div class="tips">
        {{ scopeRef.$t('customTable.tableTip') }}
      </div> -->

      <vxe-table
        ref="table"
        class="custom-table"
        border
        stripe
        :row-config="{ useKey: true }"
        max-height="400px"
        :data="innerColumns"
      >
        <vxe-column align="left" width="36px">
          <template #default="{row}">
            <div v-if="row.field !== 'operation'" class="drag-block">
              <img :src="dragIcon" height="16" width="16">
            </div>
          </template>
        </vxe-column>

        <vxe-column
          width="100px"
          :title="scopeRef.$t('customTable.showCol')"
          align="center"
          type="checkbox"
        >
          <template #header>
            {{ scopeRef.$t('customTable.showCol') }}
          </template>
          <template #checkbox="{ row }">
            <ElCheckbox v-model="row.visible" :disabled="row.field === 'operation'" />
          </template>
        </vxe-column>
        <vxe-column :title="scopeRef.$t('customTable.colName')" field="title" align="left" />
        <!-- <vxe-column align="left" :title="scopeRef.$t('customTable.propName')" field="field" /> -->
        <vxe-column :title="scopeRef.$t('customTable.colWidth')" align="left">
          <template #default="{ row, column }">
            <ElInput v-model="row.width" :disabled="row.field === 'operation'" />
          </template>
        </vxe-column>
        <vxe-column :title="scopeRef.$t('customTable.fixedLeft')" align="left">
          <template #default="{ row }">
            <ElSwitch
              :value="row.fixed"
              active-value="left"
              :inactive-value="null"
              :disabled="row.field === 'operation'"
              @change="bool => switchFixed(row, bool)"
            />
          </template>
        </vxe-column>
        <vxe-column :title="scopeRef.$t('customTable.fixedRight')" align="left">
          <template #default="{ row }">
            <ElSwitch
              :value="row.fixed"
              active-value="right"
              :inactive-value="null"
              :disabled="row.field === 'operation'"
              @change="bool => switchFixed(row, bool)"
            />
          </template>
        </vxe-column>
      </vxe-table>

      <template #footer>
        <div class="custom-table-footer">
          <ElButton :loading="loading" @click="close">
            {{ scopeRef.$t('common.cancel') }}
          </ElButton>
          <ElButton :loading="loading" @click="reset">
            {{ scopeRef.$t('common.reset') }}
          </ElButton>
          <ElButton type="primary" :loading="loading" @click="handleSubmit">
            {{ scopeRef.$t('common.confirm') }}
          </ElButton>
        </div>
      </template>
    </ElDialog>
  </div>
</template>

<style lang="scss">
.custom-table-header {
  position: absolute;
  right: 0px;
  top: -38px;

  .custom-table-header__btn {
    color: #161C24;
    padding: 4px 0;
    .iconiconchilun{
      font-size: 12px;
    }
  }
}
</style>
