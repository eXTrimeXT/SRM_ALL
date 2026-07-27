<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <el-collapse v-model="activeCollapseItem">
        <!--基础信息-->
        <el-collapse-item :title="$t('quoteTemplate.baseInfo')" name="baseInfo">
          <BaseInfo
            ref="baseInfo"
            :form-data.sync="detailFormData"
            :readonly="pageFlag.isView"
          />
        </el-collapse-item>

        <!--属性字段-->
        <el-collapse-item :title="$t('quoteTemplate.attrField')" name="attrField">
          <AttrField
            ref="attrField"
            style="margin-top: 10px"
            :page-flag="pageFlag"
            :readonly="pageFlag.isView"
            :field-list="fieldList"
            @attr-field-update="attrFieldUpdate"
          />
        </el-collapse-item>

        <!--公式定义-->
        <el-collapse-item :title="$t('quoteTemplate.formula')" name="formula">
          <Formula
            ref="formula"
            style="margin-top: 10px"
            :readonly="pageFlag.isView"
            :attr-field-data="attrFieldData"
            :formula-list="formulaList"
          />
        </el-collapse-item>
      </el-collapse>

      <CToolbar>
        <template slot="right">
          <template v-if="!pageFlag.isView">
            <!--保存-->
            <el-button type="primary" @click="saveOrSubmit('SAVE')">
              {{ $t('common.staging') }}
            </el-button>

            <!--提交-->
            <el-button type="primary" @click="saveOrSubmit('SUBMIT')">
              {{ $t('common.submit') }}
            </el-button>
          </template>

          <!--返回-->
          <el-button @click="backTab('refresh')">
            {{ $t('bidMod.backTo') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>

<script>
import { quoteBuyerHttp } from 'modb@/quoteTemplate/api'
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import BaseInfo from './quoteAttrDetail/baseInfo.vue'
import AttrField from './quoteAttrDetail/attrField.vue'
import Formula from './quoteAttrDetail/formula.vue'

export default {
  name: 'QuoteAttrDetail',

  components: {
    CToolbar,
    BaseInfo,
    AttrField,
    Formula
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      attrId: '',
      detailFormData: {
        attrId: '',
        attrNo: '',
        attrName: '',
        attrStatus: '',
        creationDate: ''
      },
      activeCollapseItem: ['baseInfo', 'attrField', 'formula'],
      activeTab: 'attr',
      attrFieldData: [],
      fieldList: [],
      formulaList: []
    }
  },

  computed: {
    pageFlag () {
      // 新增、编辑、只读
      // flag: ['add', 'edit', 'view']
      const flag = this.$attrs.params.flag
      return {
        isAdd: flag === 'add',
        isEdit: flag === 'edit',
        isView: flag === 'view'
      }
    }
  },

  created () {
    if (!this.pageFlag.isAdd) {
      // 编辑 查看 审批
      this.attrId = this.$attrs.params.row.attrId
      this.getFormDetail()
    }
  },

  methods: {
    /* 查询单据详情 */
    async getFormDetail () {
      if (!this.attrId) {
        return
      }

      const response = await quoteBuyerHttp.attr.getDetail(this.attrId)
      if (response && response.data) {
        const {
          attr,
          fieldList = [],
          formulaList = []
        } = response.data

        const formData = {}
        for (const key in this.detailFormData) {
          if (attr[key] || attr[key] === 0) {
            formData[key] = attr[key]
          }
        }

        this.detailFormData = formData
        this.fieldList = fieldList
        this.formulaList = formulaList
      }
    },

    /* 要素字段更新 */
    attrFieldUpdate (val) {
      this.attrFieldData = val
    },

    /* 提交 / 保存 */
    async saveOrSubmit (type) {
      // 校验表单
      const validateBaseInfoResult = await this.$refs.baseInfo.validateForm()
      if (!validateBaseInfoResult) {
        return
      }

      // 校验属性字段并拿到数据
      const validateAttrFieldResult = await this.$refs.attrField.validateForm()
      if (!validateAttrFieldResult.status) {
        return
      }

      // 校验公式定义并拿到数据
      const validateFormulaResult = await this.$refs.formula.validateForm()
      if (!validateFormulaResult.status) {
        return
      }

      let submitData = {
        // 属性信息
        attr: this.detailFormData,
        // 字段定义
        fieldList: validateAttrFieldResult.data,
        // 公式定义
        formulaList: validateFormulaResult.data,
        tempSave: type === 'SAVE'
      }

      const response = await quoteBuyerHttp.attr.edit(submitData)
      if (response) {
        if (type === 'SAVE') {
          this.$message.success(this.$t('common.successSave'))
          this.detailFormData.attrId = response.data
          // 查询
          await this.getFormDetail()
        }
        if (type === 'SUBMIT') {
          this.$message.success(this.$t('common.successSubmit'))
          this.backTab('refresh')
        }
      }
    },

    /* 返回 */
    backTab (type) {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      if (type === 'refresh') {
        this.__setTabTodo('QuoteAttrList.getQueryData')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.flex-container {
  padding-bottom: 50px;
}
</style>
