<template>
  <SrmDialog
    title="智能推荐"
    size="small"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <el-form
      ref="form"
      label-width="120px"
      :model="form"
      :rules="rules"
    >
      <el-form-item prop="categoryName" label="选择品类">
        <CCategorySelect
          v-model="form.categoryName"
          :scope="form"
          :placeholder="$t('common.pleaseSelect')"
          show-key="categoryName"
          @select="comfirmSelect"
        />
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t("common.cancel") }}
      </el-button>
      <el-button type="primary" @click="handleConfirm">
        {{ $t("common.confirm") }}
      </el-button>
    </div>
  </SrmDialog>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import CCategorySelect from 'lib@/components/c-category-select'

export default {
  name: 'InviteHistoryDialog',
  components: {
    FormWrapper,
    TableView,
    CCategorySelect
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      form: {
        categoryId: null,
        cateogryCode: null,
        categoryName: null
      },
      rules: {
        categoryName: [{ required: true, message: '必填项', trigger: 'change' }]
      }
    }
  },
  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    }
  },
  methods: {
    async handleConfirm () {
      await this.$refs.form.validate()
      this.$emit('confirm', this.form)
    },
    comfirmSelect (node, scope) {
      this.form.categoryId = node ? node.categoryId : null
      this.form.categoryCode = node ? node.categoryCode : null
      this.form.categoryName = node ? node.categoryName : null
    },
    resetFields () {
      this.$refs.form.resetFields()
    }
  }
}
</script>
