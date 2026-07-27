<template>
  <el-container class="flex-container wrapper" direction="vertical">
    <el-main>
      <el-collapse v-model="activeDims">
        <!--基础信息-->
        <el-collapse-item :title="$t('meeting.baseInfo')" name="1">
          <el-form
            ref="meetModelForm"
            :model="meetModelFormData"
            :rules="meetModelFormRules"
            :disabled="pageFlag.isView"
          >
            <SrmRow>
              <SrmCol :init-col="4">
                <!-- 上会类型 -->
                <el-form-item prop="topicType" :label="$t('meeting.topicType')">
                  <DictSelect
                    v-model="meetModelFormData.topicType"
                    code="MEET_TYPE"
                  />
                </el-form-item>
              </SrmCol>

              <SrmCol :init-col="4">
                <!-- 会议模板编号 -->
                <el-form-item prop="modelCode" :label="$t('meeting.topicModelCode')">
                  <el-input v-model="meetModelFormData.modelCode" disabled />
                </el-form-item>
              </SrmCol>

              <SrmCol :init-col="4">
                <!-- 会议模板名称 -->
                <el-form-item prop="modelName" :label="$t('meeting.topicModelName')">
                  <el-input v-model="meetModelFormData.modelName" />
                </el-form-item>
              </SrmCol>

              <SrmCol :init-col="4">
                <!-- 状态 -->
                <el-form-item prop="status" :label="$t('common.status')">
                  <DictSelect
                    v-model="meetModelFormData.status"
                    code="MEET_MODEL_STATUS"
                    disabled
                  />
                </el-form-item>
              </SrmCol>

              <SrmCol :init-col="4">
                <!-- 创建人 -->
                <el-form-item :label="$t('common.creator')">
                  <el-input v-model="meetModelFormData.createdFullName" disabled />
                </el-form-item>
              </SrmCol>

              <SrmCol :init-col="4">
                <!-- 创建时间 -->
                <el-form-item :label="$t('common.creationTime')">
                  <el-input v-model="meetModelFormData.creationDate" disabled />
                </el-form-item>
              </SrmCol>

              <SrmCol :init-col="4">
                <!-- 更新人 -->
                <el-form-item :label="$t('common.lastUpdatedFullName')">
                  <el-input v-model="meetModelFormData.lastUpdatedFullName" disabled />
                </el-form-item>
              </SrmCol>

              <SrmCol :init-col="4">
                <!-- 更新时间 -->
                <el-form-item :label="$t('common.lastUpdateDate')">
                  <el-input v-model="meetModelFormData.lastUpdateDate" disabled />
                </el-form-item>
              </SrmCol>
            </SrmRow>
          </el-form>
        </el-collapse-item>

        <!-- 议题成员 -->
        <el-collapse-item :title="$t('meeting.issueMember')" name="2">
          <el-button
            type="primary"
            :disabled="pageFlag.isView"
            @click="addMemberRow"
          >
            {{ $t('common.add') }}
          </el-button>

          <el-table
            :data="memberData"
            border
            stripe
            class="mt-10"
          >
            <!--姓名-->
            <el-table-column prop="fullName" :label="$t('meeting.fullName')">
              <template v-slot="scope">
                <QuickSearch
                  :show-input="scope.row.fullName"
                  :disabled="pageFlag.isView"
                  show-key="username"
                  :scope-data="scope.row"
                  auto-query
                  name="scc_rbac_user_display"
                  @close-quicksearch="setUserInfo"
                />
              </template>
            </el-table-column>

            <!--部门-->
            <el-table-column prop="deptName" :label="$t('meeting.deptName')">
              <template v-slot="scope">
                <el-input v-model="scope.row.deptName" disabled />
              </template>
            </el-table-column>

            <!--手机号-->
            <el-table-column prop="mobileNo" :label="$t('meeting.mobileNo')">
              <template v-slot="scope">
                <el-input v-model="scope.row.mobileNo" disabled />
              </template>
            </el-table-column>

            <!--邮箱-->
            <el-table-column prop="email" :label="$t('common.email')">
              <template v-slot="scope">
                <el-input v-model="scope.row.email" disabled />
              </template>
            </el-table-column>

            <el-table-column :label="$t('common.operation')">
              <template v-slot="{ $index }">
                <!--删除-->
                <el-button
                  type="text"
                  :disabled="pageFlag.isView"
                  @click="deleteMemberRow($index)"
                >
                  {{ $t('common.delete') }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>

        <!-- 材料模板 -->
        <el-collapse-item :title="$t('meeting.materialTemplateTitle')" name="3">
          <FileDynamic
            ref="sceneAttachment"
            v-model="fileData"
            scene-module-code="SCENE_SOU_CAR_MEET_MODEL_ATTACHMENT"
            :business-id="modelId"
            :editable="!pageFlag.isView"
            :need-init="false"
          />
        </el-collapse-item>
      </el-collapse>
    </el-main>

    <CToolbar>
      <template slot="right">
        <!--取消-->
        <el-button type="ghost" @click="backTab">
          {{ $t("common.cancel") }}
        </el-button>
        <!--创建模板 / 暂存-->
        <el-button
          v-if="!pageFlag.isView"
          type="primary"
          @click="submit"
        >
          {{ pageFlag.isAdd ? $t("meeting.createTemplate") : $t("common.staging") }}
        </el-button>
      </template>
    </CToolbar>
  </el-container>
</template>

<script>
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import FileDynamic from 'lib@/components/c-file-management/file-dynamic'
import { meetModelApi } from 'modb@/meetManagement/api'
export default {
  name: 'MeetModelDetail',

  components: {
    CToolbar,
    QuickSearch,
    FileDynamic
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      activeDims: ['1', '2', '3'],
      meetModelFormData: {
        topicType: '',
        modelCode: '',
        modelName: '',
        status: '',
        createdFullName: '',
        creationDate: '',
        lastUpdatedFullName: '',
        lastUpdateDate: ''
      },
      meetModelFormRules: {
        topicType: [{ required: true, message: this.$t('common.pleaseSelect') }],
        modelName: [{ required: true, message: this.$t('common.pleaseInput') }]
      },
      fileData: [],
      memberData: [],
      attrsParams: this.$attrs.params || {},
      modelId: ''
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
      this.modelId = this.attrsParams.row.modelId
      this.getFormDetail()
    }
  },

  mounted () {
    this.$nextTick(() => {
      if (this.pageFlag.isAdd) {
        this.$refs.sceneAttachment.loadFileInfo()
      }
    })
  },

  methods: {
    /* 查询详情 */
    async getFormDetail () {
      if (!this.modelId) {
        return
      }

      const response = await meetModelApi.getModelInfo({
        modelId: this.modelId
      })

      if (response) {
        const {
          meetModelMemberList = [],
          ...rest
        } = response.data || {}
        this.meetModelFormData = Object.keys(this.meetModelFormData).reduce((pre, key) => {
          return {
            ...pre,
            [key]: rest[key] || ''
          }
        }, {})
        this.memberData = meetModelMemberList.concat()
        this.$nextTick(() => {
          // 更新附件表格
          this.$refs.sceneAttachment.loadFileInfo()
        })
      }
    },

    /* 冗余用户信息 */
    setUserInfo (val, row) {
      row.fullNameId = val.userId
      row.fullName = val.nickname
      row.deptName = val.department
      row.mobileNo = val.phone
      row.email = val.email
    },

    /* 新增成员行 */
    addMemberRow () {
      this.memberData.push({
        fullName: '',
        deptName: '',
        mobileNo: '',
        email: ''
      })
    },

    /* 删除成员行 */
    deleteMemberRow ($index) {
      this.memberData.splice($index, 1)
    },

    /* 保存 */
    async submit () {
      const valid = await this.$refs.meetModelForm.validate().catch(() => this.__focus_error__())

      if (!valid) {
        return
      }

      let paramsData = {
        ...this.meetModelFormData,
        meetModelMemberList: this.memberData,
        modelFiles: this.fileData
      }

      if (!this.pageFlag.isAdd) {
        paramsData = {
          ...paramsData,
          modelId: this.modelId
        }
      }

      const response = await meetModelApi.submit(paramsData)

      if (response) {
        this.$message.success(this.$t('common.successSubmit'))
        if (this.pageFlag.isAdd) {
          this.backTab()
        }
      }
    },

    /* 返回 */
    backTab () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('MeetModelList.getQueryData')
    }
  }
}
</script>

<style lang="scss" scoped>
.mt-10 {
  margin-top: 10px;
}
.wrapper {
  padding-bottom: 40px;
}
</style>
