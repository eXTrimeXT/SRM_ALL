<template>
  <el-container class="flex-container-notab the_announcements_wrapper" direction="vertical">
    <el-main class="main-the-body">
      <FormWrapper :form-array="preArr" form-label-width="120px" @getFormData="getQuerydata" />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <!-- 发布新公告 -->
          <AuthorityButton
            code="base:announcements:publishNewNotice"
            type="primary"
            @click="publishNewNotice"
          >
            {{ $t('dataConfMod.publishNewNotice') }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        url="/api-base/notice/notice/pj/listPage"
      />
    </el-main>
    <srm-dialog
      v-if="dialogVisible"
      :close-on-click-modal="false"
      :visible.sync="dialogVisible"
      :title="title"
      size="large"
      @close="cancel"
    >
      <el-form
        ref="form"
        :rules="rules"
        :model="form"
        label-width="100px"
        label-position="top"
        class="form-incontainer"
      >
        <srm-row>
          <srm-col :initCol="3">
            <!-- 公告分类 -->
            <el-form-item :label="$t('dataConfMod.noticeType')" prop="noticeType">
              <DictSelect v-model="form.noticeType" code="NOTICE_TYPE" />
            </el-form-item>
          </srm-col>
          <srm-col :initCol="3">
            <!-- 发布人 -->
            <el-form-item :label="$t('dataConfMod.publishPerson')" prop="publishBy">
              <el-input v-model="form.publishBy" disabled />
            </el-form-item>
          </srm-col>
          <srm-col :initCol="3">
            <!-- 发布日期 -->
            <el-form-item :label="$t('dataConfMod.publishTime')" prop="publishTime">
              <el-input v-model="form.publishTime" disabled />
            </el-form-item>
          </srm-col>
          <srm-col :initCol="3">
            <!-- 公告有效日期 -->
            <el-form-item :label="$t('cusEntry.dataConfMod.extValidityDate')" prop="extValidityDate">
              <el-date-picker
                v-model="form.extValidityDate"
                type="date"
                value-format="yyyy-MM-dd"
              />
            </el-form-item>
          </srm-col>
        </srm-row>
        <el-row>
          <!-- 公告对象 -->
          <el-form-item
            :label="$t('dataConfMod.noticeVendors')"
            prop="noticeVendors"
            :show-message="false"
            :rules="{
              required: form.noticeAll === 'YES' ? false : true
            }"
          >
            <el-row style="margin-bottom: 5px;">
              <!-- 所有供应商 -->
              <el-checkbox
                v-if="dialogVisible"
                v-model="form.noticeAll"
                true-label="YES"
                false-label="NO"
                @change="checkboxChangeHandle"
              >
                {{ $t('dataConfMod.allVendors') }}
              </el-checkbox>
              <QuickSearch
                style="display: inline-block;margin: 0 10px; vertical-align: middle;"
                :disabled="form.noticeAll === 'YES'"
                show-key="username"
                name="scc_sup_company_info_new"
                :multiSelect="true"
                :showButton="true"
                :btnTitle="$t('common.select')"
                @close-quicksearch="getPeople"
              />
              <el-button @click="selectClearHandle">
                {{ $t('common.clear') }}
              </el-button>
            </el-row>
            <el-row>
              <div
                class="notice-vendor"
                :style="{borderColor: triggerNotcieVeondor ? '#ff4949' : '#ddd'}"
              >
                <el-tag
                  v-for="(item, index) in form.noticeVendors"
                  ref="tagNotice"
                  :key="item.vendorId + item.vendorCode"
                  class="tag"
                  closable
                  @close="removeTag(index)"
                >
                  {{ item.vendorName }}
                </el-tag>
              </div>
              <span
                v-show="triggerNotcieVeondor"
                class="validator-notice"
              >{{ $t('dataConfMod.msgNoticeVendors') }}</span>
            </el-row>
          </el-form-item>
        </el-row>
        <el-row type="flex">
          <!-- 公告标题 -->
          <el-form-item style="width: 100%" :label="$t('dataConfMod.noticeTitle')" prop="title">
            <el-input v-model="form.title" />
          </el-form-item>
        </el-row>
        <el-row type="flex">
          <!-- 正文 -->
          <el-form-item :label="$t('dataConfMod.noticeDetail')" prop="detail">
            <Tinymce id="announcementsTinymce" v-model="form.detail" :height="460" />
          </el-form-item>
        </el-row>
        <el-row type="flex">
          <!-- 附件 -->
          <el-form-item :label="$t('dataConfMod.attachment')" prop="fileRelationId">
            <SrmCommonFile
              :extra-data="fileInfo"
              :default-file="{
                fileId: form.fileRelationId,
                fileName: form.fileName
              }"
              :readonly="false"
              @on-change="({file}) => handleUploadSuccess(file)"
            />
          </el-form-item>
        </el-row>
        <el-row type="flex">
          <!-- 置顶公告 -->
          <el-form-item :label="$t('dataConfMod.topNotice')" prop="isTop">
            <el-switch v-model="form.isTop" active-value="Y" inactive-value="N" />
          </el-form-item>
        </el-row>
      </el-form>
      <div slot="footer">
        <el-button :loading="stagingLoading" @click="submit">
          {{ $t('common.staging') }}
        </el-button>
        <el-button type="primary" :loading="publishLoading" @click="publish">
          {{ $t('common.publish') }}
        </el-button>
      </div>
    </srm-dialog>
  </el-container>
</template>

<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import Tinymce from '@/components/Tinymce'
import { noticeApi } from 'modcc@/userManage/api'

export default {
  name: 'Announcements',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch,
    Tinymce
  },
  data () {
    return {
      triggerNotcieVeondor: false,
      // 表格配置
      tableData: [],
      tableHeader: [],
      queryParam: {},
      pageSize: 15,
      gridId: 'list',
      // 搜索表单配置
      selectDictionary: {},
      preArr: [],
      // 弹窗
      dialogVisible: false,
      title: this.$t('dataConfMod.addNotice'), // "新增公告"
      fileInfo: {
        fileModular: 'userManage',
        fileFunction: 'announcements',
        fileType: 'images'
      },
      isModify: false,
      rules: {
        noticeType: [{ required: true, message: this.$t('dataConfMod.msgNoticeType') }], // "请填写公告类型"
        // noticeVendors: [
        //   { required: true }
        // ],
        title: [{ required: true, message: this.$t('dataConfMod.msgNoticeTitle') }], // "请填写公告标题"
        detail: [{ required: true, message: this.$t('dataConfMod.msgNoticeDetail') }]// "请填写正文"
      },
      form: {
        fileName: null,
        fileRelationId: null,
        isTop: null,
        noticeAll: null,
        detail: null,
        noticeType: null,
        title: null,
        noticeVendors: [],
        extValidityDate: '2099-12-31'
      },
      peopleDialogVisible: false,
      // 字典相关
      isTopOpts: [],
      metaData: [],
      stagingLoading: false,
      publishLoading: false
    }
  },
  created () {
    var _this = this
    this.preArr = [
      {
        prop: 'noticeType',
        label: () => this.$t('dataConfMod.noticeType'), // "公告分类"
        type: 'dict', // 字典类型
        code: 'NOTICE_TYPE'

      },
      {
        prop: 'noticeStatus',
        label: () => this.$t('dataConfMod.noticeStatus'), // "公告状态
        type: 'dict', // 字典类型
        code: 'NOTICE_STATUS'

      },
      {
        prop: 'publishBy',
        label: () => this.$t('dataConfMod.publishPerson')// "发布人"
      },
      {
        prop: 'title',
        label: () => this.$t('dataConfMod.noticeTitle')// "公告标题"
      },
      {
        prop: 'startPublisherTime',
        label: () => this.$t('dataConfMod.startPublisherTime'), // "起始发布日期"
        type: 'date'
      },
      {
        prop: 'endPublisherTime',
        label: () => this.$t('dataConfMod.endPublisherTime'), // "截止发布日期"
        type: 'date'
      }
    ]
    this.tableHeader = [
      {
        prop: 'noticeType',
        label: () => this.$t('dataConfMod.noticeType'), // "公告分类"
        dataType: 'dict',
        code: 'NOTICE_TYPE'
      },
      {
        prop: 'title',
        label: () => this.$t('dataConfMod.noticeTitle')// "公告标题"
      },
      {
        prop: 'noticeStatus',
        label: () => this.$t('dataConfMod.noticeStatus'), // "公告状态"
        dataType: 'dict', // 数据类型为字典
        code: 'NOTICE_STATUS' // 字典code
      },
      {
        prop: 'publishBy',
        label: () => this.$t('dataConfMod.publishPerson')// "发布人"
      },
      {
        prop: 'publishTime',
        label: () => this.$t('dataConfMod.publishTime')// "发布日期"
      },
      {
        prop: 'extValidityDate',
        label: () => this.$t('cusEntry.dataConfMod.expirationDate')// "失效日期"
      },
      {
        prop: 'lastUpdatedUserName', // lastUpdatedBy
        label: () => this.$t('common.updatePeople')// "更新人"
      },
      {
        prop: 'lastUpdateDate',
        label: () => this.$t('common.updateTime')// "最后更新时间"
      },
      {
        prop: 'attachmentId',
        label: () => this.$t('common.operation'), // "操作"
        showType: 'buttons',
        fixed: 'right',
        width: 100,
        buttons: [
          {
            callback: row => this.eidtItem(row),
            show: row => row.createdBy == this.$store.getters.userInfo.username,
            formattor (val) {
              return _this.$t('common.edit')// "编辑"
            }
          },
          {
            callback: row => {
              this.$confirm(
                this.$t('common.confirmDelete'),
                {
                  type: 'warning',
                  title: this.$t('common.tips'),
                  callback: action => {
                    if (action === 'confirm') this.deleteItem(row)
                  }
                }
              )
            },
            show: row => row.createdBy == this.$store.getters.userInfo.username && row.noticeStatus === 'UNPUBLISHED',
            formattor (val) {
              return _this.$t('common.delete')// "删除"
            }
          }
        ]
      }
    ]
  },
  mounted () {
    this.getQuerydata()
  },
  methods: {
    handleAttachmentRemove () {
      this.form.fileName = ''
      this.form.fileRelationId = null
    },
    checkboxChangeHandle (value) {
      // this.form.noticeAll = value ? 'YES' : 'NO'
      if (this.form.noticeVendors.length < 1 && this.form.noticeAll !== 'YES') {
        this.triggerNotcieVeondor = true
      } else {
        this.triggerNotcieVeondor = false
      }
    },
    removeTag (index) {
      this.form.noticeVendors.splice(index, 1)
      this.$forceUpdate()

      if (this.form.noticeVendors.length < 1 && this.form.noticeAll !== 'YES') {
        this.triggerNotcieVeondor = true
      } else {
        this.triggerNotcieVeondor = false
      }
    },
    getPeople (values = []) {
      if (!this.form.noticeVendors) this.form.noticeVendors = []
      if (this.form.noticeVendors.length > 0) {
        values.forEach(item => {
          const { companyName, companyId, companyCode } = item
          let hasSelectedIndex = this.form.noticeVendors.findIndex(i => i.vendorId == companyId)
          const obj = {
            vendorName: companyName,
            vendorCode: companyCode,
            vendorId: companyId
          }
          if (hasSelectedIndex < 0) {
            this.form.noticeVendors.push(obj)
          }
        })
      } else {
        this.form.noticeVendors = values.map(item => {
          const { companyName, companyId, companyCode } = item
          return { vendorName: companyName, vendorCode: companyCode, vendorId: companyId }
        })
        this.triggerNotcieVeondor = false
      }
      this.$forceUpdate()
    },

    selectClearHandle () {
      this.form.noticeAll = 'NO'
      this.form.noticeVendors = []
    },
    handleUploadSuccess (file) {
      const { fileId = '', fileName = '' } = file || {}
      this.form.fileName = fileName
      this.form.fileRelationId = fileId
    },
    handleRemove () {},
    handleScriptProgress () {},
    buttonClick () {},
    eidtItem ({ noticeId }) {
      noticeApi.getNoticeInfo({ noticeId }).then(res => {
        const { noticeVendors, ...rest } = res.data
        this.form = {
          ...rest,
          noticeVendors: noticeVendors || []
        }
        this.metaData = noticeVendors ? [].concat(noticeVendors) : []
        this.showDialog()
        this.isModify = true
        this.title = this.$t('dataConfMod.editNotice')// "编辑公告"
      })
    },
    deleteItem ({ noticeId }) {
      noticeApi.deleteNotice({ noticeId }).then(res => {
        this.$message({ type: 'success', message: res.message })
        this.getQuerydata()
      })
    },
    publishNewNotice () {
      this.isModify = false
      this.title = this.$t('dataConfMod.addNotice')// "新增公告"
      this.showDialog()
      this.form = { noticeAll: null, extValidityDate: '2099-12-31' }
    },
    getQuerydata (v) {
      if (v) this.queryParam = v
      // this.queryParam.isValidity = 'Y'
      this.$nextTick(() => this.$refs[this.gridId].query())
    },
    // 发布
    publish () {
        this.publishLoading = true
        this.$refs.form.validate((status, message) => {
        // 公告对象校验
        if ((!this.form.noticeVendors || this.form.noticeVendors.length < 1) && this.form.noticeAll !== 'YES') {
          this.triggerNotcieVeondor = true
          this.publishLoading = false
          return
        }
        const { noticeVendors, ...rest } = { ...this.form }
        const data = { noticeVendors, notice: { ...rest } }
        if (this.isModify) {
          const noticeVendorsId = this.form.noticeVendors.map(
            i => i.noticeVendorId
          )
          const metaDataId = this.metaData.map(i => i.noticeVendorId)
          const deleteNoticeVendorIds = metaDataId
            .filter(i => !noticeVendorsId.includes(i))
            .filter(i => !!i)
          data.deleteNoticeVendorIds = deleteNoticeVendorIds
        }
        this.$http({
          url: '/api-base/notice/notice/pj/publish',
          method: 'POST',
          data: data
        }).then(res => {
          this.$message({ type: 'success', message: res.message })
          this.publishLoading = false
          this.closeDialog()
          this.getQuerydata()
          this.form = { noticeAll: null }
        }).catch(() => {
          this.publishLoading = false
        })
      })
    },
    // 暂存
    submit () {
      this.stagingLoading = true
      this.$refs.form.validate((status, message) => {
        if ((!this.form.noticeVendors || this.form.noticeVendors.length < 1) && this.form.noticeAll !== 'YES') {
          this.triggerNotcieVeondor = true
          this.stagingLoading = false
          return
        }
        const { noticeVendors, ...rest } = { ...this.form }
        const data = {
          noticeVendors: noticeVendors || [],
          notice: { ...rest }
        }
        if (this.isModify) {
          const noticeVendorsId = this.form.noticeVendors.map(
            i => i.noticeVendorId
          )
          const metaDataId = this.metaData.map(i => i.noticeVendorId)
          const deleteNoticeVendorIds = metaDataId
            .filter(i => !noticeVendorsId.includes(i))
            .filter(i => !!i)
          this.$http({
            url: '/api-base/notice/notice/pj/modify',
            method: 'POST',
            data: { ...data, deleteNoticeVendorIds }
          }).then(res => {
          if (res.code === '1' && res.data == 500) {
            this.$message({ type: 'error', message: res.message })
            this.stagingLoading = false
            return false
          }
          this.$message({ type: 'success', message: res.message })
          this.stagingLoading = false
          this.closeDialog()
          this.getQuerydata()
          this.form = { noticeAll: null }
        }).catch(() => {
          this.stagingLoading = false
        })
        } else {
          this.$http({
            url: '/api-base/notice/notice/pj/add',
            method: 'POST',
            data: data
          }).then(res => {
            this.$message({ type: 'success', message: res.message })
            this.stagingLoading = false
            this.closeDialog()
            this.getQuerydata()
            this.form = { noticeAll: null }
          }).catch(() => {
            this.stagingLoading = false
          })
        }
      })
    },
    cancel () {
      this.form = { noticeAll: null }
      this.closeDialog()
    },
    closeDialog () {
      this.dialogVisible = false
    },
    showDialog () {
      this.dialogVisible = true
    }
  }
}
</script>

<style lang="scss" scoped>
.the_announcements_wrapper {
  .form-incontainer {
    height: 400px;
    overflow: auto;
  }
  .download-link-wrap {
    .download-link-item {
      color: #1890ff;
    }
    .close-icon {
      font-weight: bold;
      cursor: pointer;
    }
  }
  .notice-vendor {
    display: flex;
    flex-wrap: wrap;
    border: 1px solid #ddd;
    min-height: 30px;
    padding: 5px 5px 0 5px;
    align-items: center;
    position: relative;
    .tag {
      margin-right: 5px;
      margin-bottom: 5px;
    }
  }
  .validator-notice {
    color: #ff4949;
    font-size: 12px;
    position: absolute;
    bottom: -21px;
    right: 0;
  }
}
</style>
