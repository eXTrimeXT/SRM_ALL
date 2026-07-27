<template>
  <el-form
    ref="tableForm"
    :model="value"
  >
    <el-row :gutter="32">
      <el-col :span="6">
        <el-form-item
          prop="pageCode"
          :label="$t('dataConfMod.pageCode')"
        >
          <el-input v-model="value.pageCode" />
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item
          prop="pageName"
          :label="$t('dataConfMod.pageName')"
        >
          <el-input v-model="value.pageName" />
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item
          prop="pageModel"
          :label="$t('dataConfMod.pageModel')"
        >
          <DictSelect
            v-model="value.pageModel"
            code="FORM_PAGE_MODEL"
          />
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item
          prop="pageDescribe"
          :label="$t('dataConfMod.pageDescribe')"
        >
          <el-input v-model="value.pageDescribe" />
        </el-form-item>
      </el-col>
      <el-col :span="6">
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
      <el-col :span="6">
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
    </el-row>
  </el-form>
</template>
<script>
export default {
  name: 'FormPageEdit',
  props: {
    visible: {
      type: Boolean,
      default: () => {
        return false
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
          tableName: null
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
    }
  }
}
</script>
