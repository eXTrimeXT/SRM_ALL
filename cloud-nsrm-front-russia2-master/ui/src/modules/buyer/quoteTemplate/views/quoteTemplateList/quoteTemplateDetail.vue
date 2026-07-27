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

        <!--详细信息-->
        <el-collapse-item :title="$t('quoteTemplate.detailInfo')" name="detailInfo">
          <DetailInfo
            ref="detailInfo"
            :readonly="pageFlag.isView"
            :temp-line-list="tempLineList"
          />
        </el-collapse-item>
      </el-collapse>

      <CToolbar>
        <template slot="right">
          <!--取消-->
          <el-button @click="backTab">
            {{ $t('common.cancel') }}
          </el-button>

          <!--模板预览-->
          <el-button @click="tempPreview">
            {{ $t('quoteTemplate.tempPreview') }}
          </el-button>

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
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>

<script>
import { quoteBuyerHttp } from 'modb@/quoteTemplate/api'
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import BaseInfo from './quoteTemplateDetail/baseInfo.vue'
import DetailInfo from './quoteTemplateDetail/detailInfo.vue'
import templatePreview from './quoteTemplateDetail/templatePreview.vue'

export default {
  name: 'QuoteTemplateDetail',

  components: {
    CToolbar,
    BaseInfo,
    DetailInfo
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      tempId: '',
      detailFormData: {
        tempId: '',
        tempNo: '',
        tempName: '',
        tempStatus: '',
        creationDate: '',
        createdUserName: ''
      },
      activeCollapseItem: ['baseInfo', 'detailInfo'],
      tempLineList: [],
      templatePreviewDialogVisible: true
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
      this.tempId = this.$attrs.params.row.tempId
      this.getFormDetail()
    }
  },

  methods: {
    /* 查询单据详情 */
    async getFormDetail () {
      if (!this.tempId) {
        return
      }

      const response = await quoteBuyerHttp.template.getDetail(this.tempId)
      if (response && response.data) {
        const { temp = {}, tempLineList = [] } = response.data

        const formData = {}
        for (const key in this.detailFormData) {
          if (temp[key] || temp[key] === 0) {
            formData[key] = temp[key]
          }
        }

        this.detailFormData = formData
        this.tempLineList = tempLineList
      }
    },

    /* 提交 / 保存 */
    async saveOrSubmit (type) {
      return new Promise(async resolve => {
        // 校验表单
        const validateBaseInfoResult = await this.$refs.baseInfo.validateForm()
        if (!validateBaseInfoResult) {
          resolve(false)
          return
        }

        // 校验属性列表并拿到数据
        const validateDetailInfoResult = await this.$refs.detailInfo.validateForm()
        if (!validateDetailInfoResult.status) {
          resolve(false)
          return
        }

        let submitData = {
          // 属性信息
          temp: this.detailFormData,
          // 报价属性
          tempLineList: validateDetailInfoResult.data
        }

        const response = await quoteBuyerHttp.template[type.toLowerCase()](submitData)
        if (response) {
          if (type === 'SAVE') {
            this.$message.success(this.$t('common.successSave'))
            this.detailFormData.tempId = response.data
            // 查询
            await this.getFormDetail()
            resolve(true)
          }
          if (type === 'SUBMIT') {
            this.$message.success(this.$t('common.successSubmit'))
            this.backTab('refresh')
            resolve(true)
          }
        }
      })
    },

    /* 模板预览 */
    async tempPreview () {
      // 需要以下步骤 暂存 => 查模板详情 title带上模板名称 name带上模板编码
      if (!this.pageFlag.isView) {
        // 查看状态无需保存
        const result = await this.saveOrSubmit('SAVE')
        if (!result) {
          return
        }
      }

      this.$emit('tab-add', {
        component: templatePreview,
        params: {
          flag: 'edit',
          row: this.detailFormData,
          tabName: `templatePreview${this.detailFormData.tempNo}`
        },
        title: this.detailFormData.tempNo + ' ' + this.$t('quoteTemplate.tempPreview'),
        name: `templatePreview${this.detailFormData.tempNo}`
      })
    },

    /* 返回 */
    backTab (type) {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      if (type === 'refresh') {
        this.__setTabTodo('QuoteTemplateList.getQueryData')
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
