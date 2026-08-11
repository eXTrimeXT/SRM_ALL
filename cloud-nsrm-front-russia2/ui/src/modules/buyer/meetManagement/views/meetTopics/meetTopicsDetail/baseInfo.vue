<template>
  <el-form
    ref="form"
    :rules="rules"
    :model="form"
    :disabled="readonly"
  >
    <SrmRow>
      <!--议题编号-->
      <SrmCol :init-col="4">
        <el-form-item prop="topicCode" :label="$t('meeting.topicCode')">
          <el-input v-model="form.topicCode" disabled />
        </el-form-item>
      </SrmCol>

      <!--议题名称-->
      <SrmCol :init-col="4">
        <el-form-item prop="topicName" :label="$t('meeting.topicName')">
          <el-input v-model="form.topicName" :placeholder="$t('common.pleaseInput')" />
        </el-form-item>
      </SrmCol>

      <!--上会类型-->
      <SrmCol :init-col="4">
        <el-form-item prop="topicType" :label="$t('meeting.topicType')">
          <DictSelect
            v-model="form.topicType"
            code="MEET_TYPE"
            :disabled="urlParams.flag === 'add' || topicTypeDisabled || fromInquiry || form.relationBillCode"
            @change="topicTypeChange"
          />
        </el-form-item>
      </SrmCol>

      <!--议题模板名称-->
      <SrmCol :init-col="4">
        <el-form-item prop="modelName" :label="$t('meeting.topicModelName')">
          <QuickSearch
            :show-input="form.modelName"
            :disabled="readonly"
            show-key="modelName"
            :scope-data="form"
            auto-query
            name="lts_scc_meet_model"
            :pre-query-data="{'t.TOPIC_TYPE':form.topicType}"
            @close-quicksearch="getMeetObj"
          />
        </el-form-item>
      </SrmCol>

      <!--模块-->
      <SrmCol :init-col="4">
        <el-form-item :label="$t('meeting.categoryId')" prop="categoryId">
          <el-select
            v-model="form.categoryId"
            :disabled="readonly"
            @change="categorySelect"
          >
            <el-option v-for="(item,index) in categoryList" :key="item.value + index" :value="item.value" :label="item.label" />
          </el-select>
        </el-form-item>
      </SrmCol>

      <!--库存组织-->
      <SrmCol :init-col="4">
        <el-form-item :label="$t('common.invOrg')" prop="invId">
          <OrganizationSelector
            ref="orgSelector"
            v-model="form.invId"
            :placeholder="$t('common.pleaseSelect')"
            :parent-id="-1"
            node-type="INV"
            @select="invSelectHandler"
          />
        </el-form-item>
      </SrmCol>

      <!--关联单据号-->
      <SrmCol :init-col="4">
        <el-form-item prop="relationBillCode" :label="$t('meeting.relationBillCode')">
          <el-button
            :class="{'btn':true,'gray': form.topicType === 'OTHER' }"
            @click="goToRelation"
          >
            {{ form.relationBillCode }}
          </el-button>
        </el-form-item>
      </SrmCol>

      <!--议题状态-->
      <SrmCol :init-col="4">
        <el-form-item prop="status" :label="$t('meeting.topicStatus')">
          <DictSelect
            v-model="form.status"
            code="MEET_SUBJECT_STATUS"
            disabled
          />
        </el-form-item>
      </SrmCol>

      <!--创建人-->
      <SrmCol :init-col="4">
        <el-form-item :label="$t('common.creator')">
          <el-input v-model="form.createdFullName" disabled />
        </el-form-item>
      </SrmCol>

      <!--创建时间-->
      <SrmCol :init-col="4">
        <el-form-item :label="$t('common.creationTime')">
          <el-date-picker
            v-model="form.creationDate"
            :format="$formatDatePickerTime"
            value-format="yyyy-MM-dd HH:mm:ss"
            disabled
          />
        </el-form-item>
      </SrmCol>

      <!--更新人-->
      <SrmCol :init-col="4">
        <el-form-item :label="$t('common.lastUpdatedFullName')">
          <el-input v-model="form.lastUpdatedFullName" disabled />
        </el-form-item>
      </SrmCol>

      <!--更新日期-->
      <SrmCol :init-col="4">
        <el-form-item :label="$t('common.lastUpdateDate')">
          <el-date-picker
            v-model="form.lastUpdateDate"
            :format="$formatDatePickerTime"
            value-format="yyyy-MM-dd HH:mm:ss"
            disabled
          />
        </el-form-item>
      </SrmCol>

      <!--待决议内容-->
      <SrmCol :init-col="1">
        <el-form-item prop="awaitResolution" :label="$t('meeting.awaitResolution')">
          <el-input v-model="form.awaitResolution" type="textarea" :rows="2" />
        </el-form-item>
      </SrmCol>

      <!--会议纪要-->
      <SrmCol :init-col="1">
        <el-form-item prop="meetingMinutes" :label="$t('meeting.meetingMinutes')">
          <el-input v-model="form.meetingMinutes" type="textarea" :rows="2" disabled />
        </el-form-item>
      </SrmCol>

      <!--议题结论-->
      <SrmCol :init-col="4">
        <el-form-item prop="topicConclusion" :label="$t('meeting.topicConclusion')">
          <DictSelect
            v-model="form.topicConclusion"
            code="MEET_RESOLUTION"
            disabled
          />
        </el-form-item>
      </SrmCol>

      <!--升级等级-->
      <SrmCol v-if="form.topicConclusion === 'UPGRADE'" :init-col="4">
        <el-form-item prop="upgrade" :label="$t('meeting.upgrade')">
          <DictSelect
            v-model="form.upgrade"
            code="FIXED_POINT_MEETING_TYPE"
            disabled
          />
        </el-form-item>
      </SrmCol>
    </SrmRow>
  </el-form>
</template>

<script>
/**
 * 基础信息
 */
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'

export default {
  name: 'MeetTopicsDetailBaseInfo',

  components: {
    QuickSearch,
    OrganizationSelector
  },

  props: {
    readonly: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      form: {
        topicId: '',
        topicCode: '',
        topicName: '',
        topicType: '',
        modelCode: '',
        modelId: '',
        modelName: '',
        invId: '',
        invCode: '',
        invName: '',
        relationBillCode: '',
        relationBillId: '',
        status: '',
        createdFullName: '',
        creationDate: '',
        lastUpdatedFullName: '',
        lastUpdateDate: '',
        awaitResolution: '',
        meetingMinutes: '',
        topicConclusion: '',
        upgrade: '',
        categoryId: '',
        categoryFullName: '',
        categoryCode: ''
      },
      rules: {
        topicName: [{ required: true, message: this.$t('common.pleaseInput') }],
        awaitResolution: [{ required: true, message: this.$t('common.pleaseInput') }],
        categoryId: [{ required: true, message: this.$t('common.pleaseSelect') }]
      },
      topicTypeDisabled: false,
      modelId: ''
    }
  },

  methods: {
    topicTypeChange () {
      this.form.modelName = ''
    },

    async getMeetObj (val, scope) {
      this.modelId = this.form.modelId
      let attrs = ['modelId', 'modelCode', 'modelName', 'topicType']
      for (let key of attrs) {
        scope[key] = val ? val[key] : null
      }
      if (!val || !val.modelId) return
      this.getModelFormDetail(val.modelId)
    },

    getModelFormDetail (modelId) {
      this.getModelForm(modelId).then(res => {
        let { meetModelMemberList, meetModelMaterialList } = res.data || {}
        this.memberData = this.memberData.filter(item => item.modelId !== this.modelId)
        let hasIds = []
        for (let item of this.memberData) {
          item.fullNameId && hasIds.push(item.fullNameId)
        }
        for (let item of meetModelMemberList) {
          if (!hasIds.includes(item.fullNameId)) {
            this.memberData.push(item)
          }
        }
        this.memberData.forEach(item => {
          item.enableMeetingInvitee = true
          item.enableMinutesSender = true
        })
        this.fileData = meetModelMaterialList
      })
    }
  }
}
</script>
