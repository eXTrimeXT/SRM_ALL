<!-- 附件管理（新） -->
<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="formArray"
        @getFormData="getFormData"
        @synchronous-value="storeFormData"
      >
        <template #sceneCode="{ scope }">
          <dict-select
            v-model="formData.sceneCode"
            :dict-class="dictClass"
            code="ATTACHMENT_TEMPLATE_SCENE"
            clearable
            @change="val => dictChangeFilter(val, formData,'sceneCode')"
          />
        </template>
        <template #sceneModuleCode="{ scope }">
          <dict-select
            v-model="formData.sceneModuleCode"
            :dict-class="dictClass"
            :code="formData.sceneCode"
            clearable
            @change="val => dictChangeFilter(val, formData,'sceneModuleCode')"
          />
        </template>
        <template #attachmentType="{ scope }">
          <dict-select
            v-model="formData.attachmentType"
            :dict-class="dictClass"
            :code="formData.sceneModuleCode"
            clearable
            @change="val => dictChangeFilter(val, formData,'fileType')"
          />
        </template>
      </FormWrapper>
      <EasyTable
        ref="table"
        :selection="true"
        :methods="methods"
        :columns="columns"
        row-key="sceneTemplateId"
        table-name="scc_base_scene_file"
        :query-params.sync="queryParams"
        @row-dblclick="rowDbClick"
        @selection-change="selectionChange"
      >
        <template #sceneCode="{ scope }">
          <dict-select
            v-model="scope.row.sceneCode"
            :dict-class="dictClass"
            code="ATTACHMENT_TEMPLATE_SCENE"
            :disabled="!scope.row[ADD_KEY]"
            @change="val => dictChange(val, scope.row,'sceneCode')"
          />
        </template>
        <template #sceneModuleCode="{ scope }">
          <dict-select
            v-model="scope.row.sceneModuleCode"
            :dict-class="dictClass"
            :code="scope.row.sceneCode"
            :disabled="!scope.row[ADD_KEY]"
            @change="val => dictChange(val, scope.row,'sceneModuleCode')"
          />
        </template>
        <template #attachmentType="{ scope }">
          <dict-select
            v-model="scope.row.attachmentType"
            :dict-class="dictClass"
            :code="scope.row.sceneModuleCode"
            :disabled="!scope.row[ADD_KEY]"
          />
        </template>
        <template #attachmentName="{ scope }">
          <el-input v-model="scope.row.attachmentName" :disabled="!scope.row[ADD_KEY]" />
        </template>
        <template #attachmentSourceName="{ scope }">
          <SrmCommonFile
            :extra-data="fileInfo"
            :default-file="{
              fileId: scope.row.templateFileId,
              fileName: scope.row.attachmentSourceName
            }"
            :readonly="!scope.row[ADD_KEY]"
            @on-change="({file}) => handleUploadSuccess(file,scope)"
          />
        </template>
        <template #required="{ scope }">
          <el-checkbox
            v-model="scope.row.required"
            true-label="Y"
            false-label="N"
            :disabled="!scope.row[ADD_KEY]"
          />
        </template>
        <template #enabled="{ scope }">
          <el-checkbox
            v-model="scope.row.enabled"
            true-label="Y"
            false-label="N"
            :disabled="!scope.row[ADD_KEY]"
          />
        </template>
        <template #columnControl="{ scope }">
          <AuthorityButton
            v-if="scope.row.hasColumnConfig"
            type="text"
            code="sceneFileManagement:colSetting"
            @click="columnControl(scope)"
          >
            {{ $t('dataConfMod.columnSetting') }}
          </AuthorityButton>
        </template>
        <template #btns>
          <!-- 新增 -->
          <AuthorityButton
            code="sceneFileManagement:add"
            type="primary"
            @click="add"
          >
            {{ $t('common.add') }}
          </AuthorityButton>
          <!-- 提交 -->
          <AuthorityButton
            code="sceneFileManagement:submit"
            type="primary"
            plain
            @click="submit"
          >
            {{ $t("common.submit") }}
          </AuthorityButton>
        </template>
      </EasyTable>
    </el-main>
  </el-container>
</template>

<script>
import EasyTable from 'lib@/components/BaseTable/EasyTable'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { createDictClass } from '@/library/utils/dict/dict-utils'
import { sceneTemplateApi } from 'modb@/basicSetting/api/basicSetting'
import { ADD_KEY, EDITABLE_KEY, UPDATE_KEY } from '@/library/components/BaseTable/utils'

export default {
  name: 'SceneFileManagement',
  components: { EasyTable, FormWrapper },
  data () {
    return {
      ADD_KEY: ADD_KEY,
      dictClass: createDictClass({
        ATTACHMENT_TEMPLATE_SCENE: []
      }),
      queryParams: {},
      selectList: [],
      fileInfo: {
        fileModular: 'base', // 文件所属模块 -》基础模块
        fileFunction: 'SceneFileManagement', // 文件所属功能
        fileType: 'images' // 文件所属类型
      },
      formData: {},
      formArray: [
        {
          prop: 'sceneCode',
          label: () => this.$t('dataConfMod.processSence'), // 流程场景
          type: 'slot',
          slot: 'sceneCode'
        },
        {
          prop: 'sceneModuleCode',
          label: () => this.$t('dataConfMod.functionName'), // 功能名称
          type: 'slot',
          slot: 'sceneModuleCode'
        },
        {
          prop: 'attachmentType',
          label: () => this.$t('dataConfMod.attachmentType'), // 附件类型
          type: 'slot',
          slot: 'attachmentType'
        },
        {
          prop: 'attachmentName',
          label: () => this.$t('dataConfMod.attachmentName') // 附件名称
        }
      ],
      methods: {
        listPage: async (params) => {
          const res = await sceneTemplateApi.listPage(params)
          const dictCodes = res.data.list.reduce((last, item) => {
            last.push(item.sceneModuleCode)
            last.push(item.sceneCode)
            return last
          }, [])
          for (let i = 0; i < res.data.list.length; i++) {
            const item = res.data.list[i]
            item[UPDATE_KEY] = false
            item[ADD_KEY] = false
            item[EDITABLE_KEY] = true
          }
          const arr = [...new Set(dictCodes)].filter((i) => !!i)
          this.dictClass.loadDictionary(arr)
          return res
        }
      },
      columns: [
        {
          attrs: {
            label: () => this.$t('dataConfMod.processSence'), // 流程场景
            prop: 'sceneCode',
            minWidth: '120px',
            formatter: (value) => this.dictClass.getDictLabel('ATTACHMENT_TEMPLATE_SCENE', value)
          },
          slot: 'sceneCode'
        },
        {
          attrs: {
            label: () => this.$t('dataConfMod.functionName'), // 功能名称
            prop: 'sceneModuleCode',
            minWidth: '120px',
            formatter: (value, row) => this.dictClass.getDictLabel(row.sceneCode, value)
          },
          slot: 'sceneModuleCode'
        },
        {
          attrs: {
            label: () => this.$t('dataConfMod.attachmentType'), // 附件类型
            prop: 'attachmentType',
            minWidth: '120px',
            formatter: (value, row) => this.dictClass.getDictLabel(row.sceneModuleCode, value)
          },
          slot: 'attachmentType'
        },
        {
          attrs: {
            label: () => this.$t('dataConfMod.attachmentName'), // 附件名称
            prop: 'attachmentName',
            minWidth: '180px'
          },
          slot: 'attachmentName'
        },
        {
          attrs: {
            label: () => this.$t('dataConfMod.attachTemplate'), // 附件模板
            prop: 'attachmentSourceName',
            minWidth: '150px'
          },
          slot: 'attachmentSourceName'
        },
        {
          attrs: {
            label: () => this.$t('dataConfMod.isRequested'), // 是否必填
            prop: 'required',
            formatter: (value) => (value === 'Y' ? this.$t('common.yes') : value === 'N' ? this.$t('common.no') : '')
          },
          slot: 'required'
        },
        {
          attrs: {
            label: () => this.$t('dataConfMod.enabledUse'), // 是否启用
            prop: 'enabled',
            formatter: (value) => (value === 'Y' ? this.$t('common.yes') : value === 'N' ? this.$t('common.no') : '')
          },
          slot: 'enabled'
        },
        {
          attrs: {
            label: () => this.$t('common.updateTime'), // 更新时间
            prop: 'lastUpdateDate'
          }
        },
        {
          attrs: {
            label: () => this.$t('dataConfMod.lastUpdatedBy'), // 更新人
            prop: 'lastUpdatedFullName'
          }
        },
        {
          attrs: {
            label: () => this.$t('dataConfMod.columnControl'), // 列控制
            prop: 'columnControl'
          },
          slot: 'columnControl'
        },
        {
          attrs: {
            prop: 'operation',
            label: () => this.$t('common.operation'), // 操作
            width: 150,
            fixed: 'right'
          },
          operations: [
            {
              event: 'deleteItem',
              name: this.$t('common.delete'),
              func: this.deleteItem,
              code: 'sceneFileManagement:delete'
            },
            {
              event: 'editItem',
              name: this.$t('common.edit'),
              func: this.editItem,
              code: 'sceneFileManagement:edit'
            }
          ]
        }
      ]
    }
  },
  mounted () {
    this.dictClass.loadDictionary('ATTACHMENT_TEMPLATE_SCENE')
  },
  methods: {
    selectionChange (selections) {
      this.selectList = selections
    },
    submit () {
      if (this.selectList.length === 0) {
        this.$message.error(this.$t('dataConfMod.selectSubmitList'))
        return
      }
      const rows = this.selectList
      sceneTemplateApi.batchSaveOrUpdate(rows).then((res) => {
        this.$message.success(res.message)
        this.$refs.table.search(this.formData)
        this.$refs.table.clearSelection()
      })
    },
    handleUploadSuccess (file, scope) {
      const { fileId = '', fileName = '' } = file || {}
      scope.row.templateFileId = fileId.toString()
      scope.row.attachmentSourceName = fileName
    },
    // 删除文件
    handleAttachmentRemove (row) {
      row.templateFileId = ''
      row.attachmentSourceName = ''
    },
    async dictChangeFilter (value, data, type) {
      await this.dictClass.loadDictionary(value)
      if (type === 'sceneCode') {
        this.$set(data, 'sceneCode', value)
        this.$set(data, 'sceneModuleCode', '')
        this.$set(data, 'attachmentType', '')
      }
      if (type === 'sceneModuleCode') {
        this.$set(data, 'sceneModuleCode', value)
        this.$set(data, 'attachmentType', '')
      }
      if (type === 'fileType') {
        this.$set(data, 'attachmentType', value)
      }
      this.$forceUpdate()
    },
    dictChange (value, data, type) {
      if (type === 'sceneCode') {
        data.sceneModuleCode = ''
        data.attachmentType = ''
      }
      if (type === 'sceneModuleCode') {
        data.attachmentType = ''
      }
      this.dictClass.loadDictionary(value)
      this.$forceUpdate()
    },
    storeFormData (data) {
      this.formData = data
    },
    getFormData (params) {
      const { pageSize, pageNum } = this.queryParams
      const queries = { pageSize, pageNum, ...params }
      this.$refs.table.search(queries)
    },
    rowDbClick (row) {
      row[ADD_KEY] = true
    },
    editItem ({ row }) {
      row[ADD_KEY] = true
    },
    columnControl (scope) {
      this.$router.push({
        name: 'formPageScene',
        params: {
          from: 'fromFun',
          funName: 'sceneFileManagement',
          pageCode: scope.row.sceneModuleCode
        }
      })
    },
    deleteItem ({ row, $index }, data) {
      if (!row.sceneTemplateId) {
        return data.splice($index, 1)
      }
      this.$confirm(this.$t('common.delRow'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          sceneTemplateApi.delete(row.sceneTemplateId).then((res) => {
            this.$message.success(res.message)
            this.$refs.table.search(this.formData)
            this.$refs.table.clearSelection()
          })
        })
    },
    add () {
      this.$refs.table.add({
        sceneModuleCode: '',
        sceneCode: '',
        attachmentName: '',
        attachmentType: '',
        templateFileId: '',
        attachmentSourceName: '',
        required: 'Y',
        enabled: 'Y'
      })
    }
  }
}
</script>
<style scoped lang="scss">
</style>
