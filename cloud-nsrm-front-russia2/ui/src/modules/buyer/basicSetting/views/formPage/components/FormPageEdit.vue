<template>
  <el-form
    ref="pageForm"
    :model="value"
  >
    <el-row :gutter="32">
      <el-col :span="6">
        <el-form-item
          prop="pageCode"
          :label="$t('dataConfMod.pageCode')"
        >
          <el-input v-if="pageModel === 'REPORT'" v-model="value.pageCode" />
          <DictSelect
            v-else-if="pageModel === 'SCENE_ATTACHMENT'"
            v-model="value.pageCode"
            code="ATTACHMENT_TEMPLATE_SCENE"
            custom-select-type="SECOND_DICT"
            @change-value="pageCodeChange"
          />
          <el-input v-else v-model="value.pageCode" />
        </el-form-item>
      </el-col>
      <el-col v-if="pageModel !== 'SCENE_ATTACHMENT'" :span="6">
        <el-form-item
          prop="pageName"
          :label="$t('dataConfMod.pageName')"
        >
          <el-input v-model="value.pageName" :disabled="pageModel === 'SCENE_ATTACHMENT'" />
        </el-form-item>
      </el-col>
      <el-col v-if="pageModel === 'COMMON'" :span="6">
        <el-form-item
          prop="pageModel"
          :label="$t('dataConfMod.pageModel')"
        >
          <DictSelect
            v-model="value.pageModel"
            code="FORM_PAGE_MODEL"
            disabled
          />
        </el-form-item>
      </el-col>
      <el-col v-if="pageModel === 'COMMON'" :span="6">
        <el-form-item
          prop="pageDescribe"
          :label="$t('dataConfMod.pageDescribe')"
        >
          <el-input v-model="value.pageDescribe" />
        </el-form-item>
      </el-col>
      <el-col v-if="pageModel === 'COMMON' || pageModel === 'REPORT'" :span="6">
        <el-form-item
          prop="bizModule"
          :label="$t('dataConfMod.bizModule')"
        >
          <DictSelect
            v-model="value.bizModule"
            code="MODULE_DIVISION"
          />
        </el-form-item>
      </el-col>
      <el-col v-if="pageModel === 'COMMON' || pageModel === 'REPORT'" :span="6">
        <el-form-item
          prop="tableName"
          :label="$t('dataConfMod.tableName')"
        >
          <DictSelect
            v-model="value.tableName"
            :code="value.bizModule"
            custom-select-type="MODULE_TABLE_NAME"
            @change="changeTableName"
          />
        </el-form-item>
      </el-col>
      <el-col v-if="pageModel === 'SCENE_ATTACHMENT'" :span="6">
        <el-form-item
          prop="needCheck"
          :label="$t('dataConfMod.needCheck')"
        >
          <el-input v-model="value.needCheck" />
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>

<script>
export default {
  name: 'FormPageEdit',
  components: {
  },
  props: {
    visible: {
      type: Boolean,
      default: () => {
        return false
      }
    },
    pageModel: {
      type: String,
      default: () => {
        return 'COMMON'
      }
    },
    value: {
      type: Object,
      default: () => {
        return {
          pageCode: null,
          pageName: null,
          pageModel: null,
          pageDescribe: null,
          bizModule: null,
          tableName: null,
          needCheck: null
        }
      }
    }
  },
  methods: {
    changeTableName () {
      this.$emit('changeTableName', this.value.tableName)
    },
    async validate () {
      const res = await this.$refs.pageForm.validate()
      return res
    },
    pageCodeChange (val, dictItem) {
      this.value.pageName = dictItem.label
    }
  }
}
</script>
