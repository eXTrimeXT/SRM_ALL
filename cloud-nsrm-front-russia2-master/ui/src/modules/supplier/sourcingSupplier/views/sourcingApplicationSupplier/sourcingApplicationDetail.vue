<template>
  <el-container class="flex-container the-sourcingApplicationDetail-detail" direction="vertical">
    <el-main>
      <div class="form-container2">
        <el-form
          ref="sourcingForm"
          :model="sourcingForm"
          label-width="80px"
          label-position="top"
          :rules="rules"
        >
          <el-collapse v-model="activeDims" class="tab-form-style">
            <!-- 基本信息 -->
            <el-collapse-item :title="$t('sourcingBuyer.baseInfo')" name="1">
              <srm-row>
                <!-- 业务实体 -->
                <srm-col>
                  <el-form-item :label="$t('sourcingBuyer.orgName')" :label-width="formLabelWidth">
                    <el-input v-model="sourcingForm.orgName" disabled />
                  </el-form-item>
                </srm-col>
                <!-- 库存组织 -->
                <srm-col>
                  <el-form-item
                    :label="$t('sourcingBuyer.organizationName')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="sourcingForm.organizationName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 寻源单号 -->
                  <el-form-item
                    :label="$t('sourcingBuyer.reqHeadNo')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="sourcingForm.reqHeadNo" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 寻源标题 -->
                  <el-form-item
                    :label="$t('sourcingBuyer.souReqTitile1')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="sourcingForm.souReqTitile" disabled />
                  </el-form-item>
                </srm-col>

                <srm-col>
                  <!-- 物料品类 -->
                  <el-form-item
                    :label="$t('sourcingBuyer.categoryType')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="sourcingForm.categoryName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 交货地址 -->
                  <el-form-item
                    :label="$t('sourcingBuyer.tradingPlace')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="sourcingForm.tradeSiteName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 报名截止时间 -->
                  <el-form-item
                    :label="$t('sourcingBuyer.expirationTime1')"
                    :label-width="formLabelWidth"
                    prop="expirationTime"
                  >
                    <el-date-picker
                      v-model="sourcingForm.expirationTime"
                      type="datetime"
                      disabled
                      :format="$formatDatePickerTime"
                      value-format="yyyy-MM-dd HH:mm:ss"
                      :placeholder="$t('sourcingBuyer.timePlaceholder')"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 需求交付时间 -->
                  <el-form-item
                    :label="$t('sourcingBuyer.tradingTime')"
                    :label-width="formLabelWidth"
                    prop="tradingTime"
                  >
                    <el-date-picker
                      v-model="sourcingForm.tradingTime"
                      type="datetime"
                      disabled
                      :format="$formatDatePickerTime"
                      value-format="yyyy-MM-dd HH:mm:ss"
                      :placeholder="$t('sourcingBuyer.timePlaceholder')"
                    />
                  </el-form-item>
                </srm-col>

                <srm-col :initCol="1">
                  <!-- 详情说明 -->
                  <el-form-item
                    :label="$t('sourcingBuyer.description')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="sourcingForm.description"
                      disabled
                      type="textarea"
                      :rows="2"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>

            <!-- 需求信息 -->
            <el-collapse-item ref="itemInfo" :title="$t('sourcingBuyer.requirementInfo')" name="2">
              <el-table
                ref="reqItemsList"
                :data="sourcingForm.reqItemsList"
                style="width: 100%"
                border
                :row-height="38"
                max-height="390px"
                highlight-current-row
              >
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('common.sort')"
                  width="50"
                />
                <!-- 物料编码 -->
                <el-table-column
                  align="center"
                  prop="materialCode"
                  :label="$t('sourcingBuyer.materialCode')"
                  width="120"
                  :show-overflow-tooltip="true"
                />
                <!-- 物料名称 -->
                <el-table-column
                  align="center"
                  prop="materialName"
                  :label="$t('sourcingBuyer.categoryName')"
                  width="120"
                  :show-overflow-tooltip="true"
                />
                <!-- 单位 -->
                <el-table-column
                  align="center"
                  prop="unit"
                  :label="$t('sourcingBuyer.unit')"
                  width="120"
                  :show-overflow-tooltip="true"
                />
                <!-- 预计数量 -->
                <el-table-column
                  align="center"
                  prop="quantity"
                  :label="$t('sourcingBuyer.quantity')"
                  width="120"
                  :show-overflow-tooltip="true"
                />
                <!-- 备注 -->
                <el-table-column
                  align="center"
                  prop="remark"
                  :label="$t('sourcingBuyer.remark')"
                  width="120"
                  :show-overflow-tooltip="true"
                />
                <!-- 供应商规格描述 -->
                <el-table-column
                  align="center"
                  prop="materialSpecDes"
                  :label="$t('sourcingBuyer.materialSpecDes')"
                  :show-overflow-tooltip="true"
                  :render-header="_addStarToColumn"
                >
                  <template slot-scope="{row}">
                    <el-form-item>
                      <el-input v-model="row.materialSpecDes" :disabled="isReadOnly" />
                    </el-form-item>
                  </template>
                </el-table-column>

                <!-- 供货周期（自然天） -->
                <el-table-column
                  align="center"
                  prop="leadTime"
                  :label="$t('sourcingBuyer.leadTime')"
                  width="120"
                  :show-overflow-tooltip="true"
                  :render-header="_addStarToColumn"
                >
                  <template slot-scope="{row}">
                    <el-form-item>
                      <el-input
                        v-model="row.leadTime"
                        type="number"
                        :disabled="isReadOnly"
                        @keydown.native="inputLimit"
                      />
                    </el-form-item>
                  </template>
                </el-table-column>

                <!-- 质保期（月） -->
                <el-table-column
                  align="center"
                  prop="shelfLife"
                  :label="$t('sourcingBuyer.shelfLife')"
                  width="120"
                  :show-overflow-tooltip="true"
                  :render-header="_addStarToColumn"
                >
                  <template slot-scope="{row}">
                    <template>
                      <el-form-item>
                        <el-input
                          v-model="row.shelfLife"
                          type="number"
                          :disabled="isReadOnly"
                          @keydown.native="inputLimit"
                        />
                      </el-form-item>
                    </template>
                  </template>
                </el-table-column>

                <!-- 制造年限（年） -->
                <el-table-column
                  align="center"
                  prop="makeLife"
                  :label="$t('sourcingBuyer.makeLife')"
                  width="120"
                  :show-overflow-tooltip="true"
                  :render-header="_addStarToColumn"
                >
                  <template slot-scope="{row}">
                    <el-form-item>
                      <el-input
                        v-model="row.makeLife"
                        type="number"
                        :disabled="isReadOnly"
                        @keydown.native="inputLimit"
                      />
                    </el-form-item>
                  </template>
                </el-table-column>
                <!-- 主要客户 -->
                <el-table-column
                  align="center"
                  prop="mainCustomer"
                  :label="$t('sourcingBuyer.mainCustomer')"
                  width="120"
                  :show-overflow-tooltip="true"
                  :render-header="_addStarToColumn"
                >
                  <template slot-scope="{row}">
                    <template>
                      <el-form-item>
                        <el-input v-model="row.mainCustomer" :disabled="isReadOnly" />
                      </el-form-item>
                    </template>
                  </template>
                </el-table-column>
                <!-- 使用方向 -->
                <el-table-column
                  align="center"
                  prop="userDirection"
                  :label="$t('sourcingBuyer.userDirection')"
                  width="120"
                  :show-overflow-tooltip="true"
                  :render-header="_addStarToColumn"
                >
                  <template slot-scope="{row}">
                    <template>
                      <el-form-item>
                        <el-input v-model="row.userDirection" :disabled="isReadOnly" />
                      </el-form-item>
                    </template>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>

            <!-- 需求信息附件 -->
            <el-collapse-item :title="$t('sourcingBuyer.requirementInfoAttach')" name="3">
              <FileDynamic
                ref="sceneAttachment"
                v-model="sourcingForm.fileUploads"
                scene-module-code="SCENE_SOU_REQ_ATTACHMENT"
                :business-id="businessId"
                :editable="false"
              />
            </el-collapse-item>

            <!-- 对供应商要求 -->
            <el-collapse-item ref="applyInfo" :title="$t('sourcingBuyer.applyInfo')" name="4">
              <srm-row>
                <srm-col>
                  <!-- 行业 -->
                  <el-form-item
                    :label="$t('sourcingBuyer.industry')"
                    :label-width="formLabelWidth"
                    prop="applyIndustry"
                    :rules="sourcingForm.isIndustry ==='Y' ? rules.applyIndustry : [{ required:false}]"
                  >
                    <el-input v-model="sourcingForm.applyIndustry" :disabled="isReadOnly" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 经营模式 -->
                  <el-form-item
                    :label="$t('sourcingBuyer.applyManageModel')"
                    :label-width="formLabelWidth"
                    prop="applyManageModel"
                    :rules="sourcingForm.isManageModel ==='Y' ? rules.applyManageModel : [{ required:false}]"
                  >
                    <dict-select
                      v-model="sourcingForm.applyManageModel"
                      code="MANAGE_MODEL"
                      :disabled="isReadOnly"
                    />
                  </el-form-item>
                </srm-col>

                <srm-col>
                  <!-- 注册资本（万元） -->
                  <el-form-item
                    :label="$t('sourcingBuyer.registCapital')"
                    :label-width="formLabelWidth"
                    prop="applyRegistCapital"
                    :rules="sourcingForm.isRegistCapital ==='Y' ? rules.applyRegistCapital : [{ required:false}]"
                  >
                    <el-input
                      v-model="sourcingForm.applyRegistCapital"
                      type="number"
                      :disabled="isReadOnly"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 企业所在地 -->
                  <el-form-item
                    :label="$t('sourcingBuyer.postalAddress')"
                    :label-width="formLabelWidth"
                    prop="applyPostalAddress"
                    :rules="sourcingForm.isPostalAddress ==='Y' ? rules.applyPostalAddress : [{ required:false}]"
                  >
                    <el-input v-model="sourcingForm.applyPostalAddress" :disabled="isReadOnly" />
                  </el-form-item>
                </srm-col>

                <srm-col>
                  <!-- 具有代理资质的品牌 -->
                  <el-form-item
                    :label="$t('sourcingBuyer.applyAgentQualifiedBrand')"
                    :label-width="formLabelWidth"
                    prop="applyAgentQualifiedBrand"
                    :rules="sourcingForm.isAgentQualifiedBrand ==='Y' ? rules.applyAgentQualifiedBrand : [{ required:false}]"
                  >
                    <el-input
                      v-model="sourcingForm.applyAgentQualifiedBrand"
                      :disabled="isReadOnly"
                    />
                  </el-form-item>
                </srm-col>

                <srm-col>
                  <!-- 供货区域 -->
                  <el-form-item
                    :label="$t('sourcingBuyer.applySupplyArea')"
                    :label-width="formLabelWidth"
                    prop="applySupplyArea"
                    :rules="sourcingForm.isSupplyArea ==='Y' ? rules.applySupplyArea : [{ required:false}]"
                  >
                    <el-select
                      v-model="supplyArea"
                      filterable
                      multiple
                      :disabled="isReadOnly"
                      popper-class="province-select-block"
                    >
                      <el-option
                        v-for="item in provinceList"
                        :key="item.provinceId"
                        :label="item.province"
                        :value="item.province"
                      />
                    </el-select>
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 公司成立年限（年） -->
                  <el-form-item
                    :label="$t('sourcingBuyer.companyCreationYear')"
                    :label-width="formLabelWidth"
                    :rules="sourcingForm.isCompanyCreationYear ==='Y' ? rules.applyCompanyCreationYear : [{ required:false}]"
                  >
                    <el-input
                      v-model="sourcingForm.applyCompanyCreationYear"
                      type="number"
                      :disabled="isReadOnly"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>

            <!-- 供应商附件要求 -->
            <el-collapse-item ref="applyAttach" :title="$t('sourcingBuyer.applyAttach')" name="5">
              <el-table
                :data="sourcingForm.attachTemplateList"
                style="width: 100%"
                border
                max-height="250px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('common.sort')"
                  width="50"
                />
                <!-- 附件名称 -->
                <el-table-column
                  align="center"
                  prop="attachmentName"
                  :label="$t('sourcingBuyer.attachmentName')"
                  width="120"
                  :show-overflow-tooltip="true"
                />
                <!-- 必填 -->
                <el-table-column
                  align="center"
                  prop="required"
                  :label="$t('sourcingBuyer.required')"
                  width="120"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="{row}">
                    {{ row.required === 'Y' ? $t('common.yes'):$t('common.no') }}
                  </template>
                </el-table-column>
                <!-- 要求说明 -->
                <el-table-column
                  align="center"
                  prop="remark"
                  :label="$t('sourcingBuyer.remark1')"
                  :show-overflow-tooltip="true"
                />
                <!-- 参考附件 -->
                <el-table-column
                  align="center"
                  prop="fileName"
                  :label="$t('sourcingBuyer.fileName')"
                >
                  <template slot-scope="{row}">
                    <SrmCommonFile
                      :default-file="{
                        fileId: row.fileuploadId,
                        fileName: row.fileName
                      }"
                      readonly
                    />
                  </template>
                </el-table-column>
                <!-- 附件 -->
                <el-table-column
                  align="center"
                  prop="applyFileName"
                  :label="$t('sourcingBuyer.attach')"
                >
                  <template slot-scope="{row,$index}">
                    <SrmCommonFile
                      :default-file="{
                        fileId: row.applyFileuploadId,
                        fileName: row.applyFileName
                      }"
                      :extra-data="fileInfo"
                      :readonly="!!isReadOnly"
                      @on-change="value => applyAttachUploadSuccess(value, $index)"
                    />
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>

            <!-- 采购方联系方式 -->
            <el-collapse-item :title="$t('sourcingBuyer.contactInfo')" name="6">
              <OriginContactInfo
                ref="contactInfo"
                business-type="SOURCING"
                :infoData="sourcingForm"
                readOnly
              />
            </el-collapse-item>

            <!-- 供应商联系方式 -->
            <el-collapse-item ref="supplierInfo" :title="$t('sourcingBuyer.supplierInfo')" name="7">
              <srm-row>
                <!-- 姓名 -->
                <srm-col>
                  <el-form-item
                    :label="$t('sourcingBuyer.name')"
                    :label-width="formLabelWidth"
                    prop="applyContactName"
                  >
                    <el-input v-model="sourcingForm.applyContactName" :disabled="isReadOnly" />
                  </el-form-item>
                </srm-col>
                <!-- 手机号码 -->
                <srm-col>
                  <el-form-item
                    :label="$t('sourcingBuyer.phone')"
                    :label-width="formLabelWidth"
                    prop="applyPhone"
                  >
                    <el-input v-model="sourcingForm.applyPhone" :disabled="isReadOnly" />
                  </el-form-item>
                </srm-col>
                <!-- 邮箱 -->
                <srm-col>
                  <el-form-item
                    :label="$t('sourcingBuyer.email')"
                    :label-width="formLabelWidth"
                    prop="applyEmail"
                  >
                    <el-input v-model="sourcingForm.applyEmail" :disabled="isReadOnly" />
                  </el-form-item>
                </srm-col>

                <srm-col :initCol="1">
                  <!-- 备注 -->
                  <el-form-item :label="$t('sourcingBuyer.remark')" :label-width="formLabelWidth">
                    <el-input
                      v-model="sourcingForm.applyRemark"
                      type="textarea"
                      :rows="2"
                      :disabled="isReadOnly"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </div>
      <CToolbar>
        <template slot="right">
          <el-button @click="backOne">
            {{ isReadOnly ? $t("common.close") : $t("common.cancel") }}
          </el-button>
          <el-button v-if="!isReadOnly" type="primary" @click="refuseOne">
            {{ $t('sourcingBuyer.refuse') }}
          </el-button>
          <!-- 暂存 -->
          <el-button v-if="!isReadOnly" type="primary" @click="saveOne">
            {{ $t("common.staging") }}
          </el-button>
          <el-button v-if="!isReadOnly" type="primary" @click="submitOne">
            {{ $t('sourcingBuyer.submitSignUp') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import CToolbar from 'lib@/components/c-toolbar'
import OriginContactInfo from 'lib@/composition/origin/contactInfo'
import { tabTodoMixin } from '@/utils/mixins'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import { getRegion } from '@/api/common'

export default {
  name: 'SourcingApplicationDetail',
  components: {
    CToolbar,
    OriginContactInfo,
    FileDynamic
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      activeDims: ['1', '2', '3', '4', '5', '6', '7'],
      sourcingForm: {
        // 基本信息
        orgName: ' ',
        orgCode: '',
        orgId: '',
        organizationName: '',
        organizationCode: '',
        organizationId: '',
        reqHeadNo: '',
        souReqTitile: '',
        categoryName: '',
        categoryCode: '',
        categoryId: '',
        tradeSiteName: '',
        expirationTime: '',
        tradingTime: '',
        description: '',

        // 需求信息
        isAllItems: '', //  Y 不允许对部分物料报名 N 允许对部分物料报名
        reqItemsList: [],

        // 需求信息附件
        fileUploads: [],

        // 对供应商要求
        isIndustry: 'N',
        industry: '',
        isManageModel: 'N',
        manageModel: '',
        isPostalAddress: 'N',
        postalAddress: '',
        isAgentQualifiedBrand: 'N',
        agentQualifiedBrand: '',
        isCompanyCreationYear: 'N',
        companyCreationYear: '',
        isSupplyArea: 'N',
        supplyArea: '',
        isRegistCapital: 'N',
        registCapital: '',
        applyIndustry: '',
        applyManageModel: '',
        applyRegistCapital: '',
        applyPostalAddress: '',
        applyAgentQualifiedBrand: '',
        applySupplyArea: '',
        applyCompanyCreationYear: '',

        // 供应商附件要求
        attachTemplateList: [],

        // 邀请供应商列表
        reqApplyList: [],

        // 采购方联系方式
        contactName: '',
        phone: '',
        email: '',

        // 供应商联系方式
        applyContactName: '',
        applyPhone: '',
        applyEmail: '',
        applyRemark: ''
      },
      rules: {
        applyIndustry: [
          { required: true, message: this.$t('sourcingBuyer.applyIndustryIsRequired') }
        ], // 行业
        applyManageModel: [
          { required: true, message: this.$t('sourcingBuyer.applyManageModelIsRequired') }
        ], // 经营模式
        applyRegistCapital: [
          { required: true, message: this.$t('sourcingBuyer.applyRegistCapitalIsRequired') }
        ], // 注册资本
        applyPostalAddress: [
          { required: true, message: this.$t('sourcingBuyer.applyPostalAddressIsRequired') }
        ], // 企业所在地
        applyAgentQualifiedBrand: [
          { required: true, message: this.$t('sourcingBuyer.applyAgentQualifiedBrandIsRequired') }
        ], // 具有代理资质的品牌
        applySupplyArea: [
          { required: true, message: this.$t('sourcingBuyer.applySupplyAreaIsRequired') }
        ], // 供货区域
        applyCompanyCreationYear: [
          { required: true, message: this.$t('sourcingBuyer.applyCompanyCreationYearIsRequired') }
        ], // 公司成立年限
        applyContactName: [
          { required: true, message: this.$t('sourcingBuyer.applyContactNameIsRequired') }
        ], // 姓名
        applyPhone: [{ required: true, message: this.$t('sourcingBuyer.applyPhoneIsRequired') }], // 手机号码
        applyEmail: [{ required: true, message: this.$t('sourcingBuyer.applyEmailIsRequired') }] // 邮箱
      },
      formLabelWidth: '120px',
      businessId: null,

      fileInfo: {
        fileModular: 'sourcingSupplier',
        fileFunction: 'sourcingApplicationDetail',
        fileType: 'images'
      },
      provinceList: []
    }
  },
  computed: {
    isReadOnly () {
      return this.$attrs.params.showType === 'readOnly'
    },
    supplyArea: {
      get: function () {
        const str = this.sourcingForm.applySupplyArea || ''
        return str ? str.split(',') : []
      },
      set: function (val) {
        this.sourcingForm.applySupplyArea = val.toString()
      }
    }
  },
  watch: {},
  created () {
    this.getProvinceList()
    this.getFormDetail()
  },
  methods: {
    inputLimit (e) {
      let key = e.key
      if (key.toLowerCase() === 'e') {
        e.returnValue = false
        return false
      }
      return true
    },
    getProvinceList () {
      getRegion({ queryType: 'province' }).then(res => {
        if (res.data) {
          res.data.push({
            area: '',
            areaId: null,
            city: '',
            cityId: null,
            province: this.$t('sourcingBuyer.nationwide'),
            provinceId: 0,
            rangeId: null,
            rangeName: '',
            town: '',
            townId: ''
          })
          this.provinceList = res.data
        }
      })
    },
    applyAttachUploadSuccess ({ file }, index) {
      const { fileId = '', fileName = '' } = file || {}
      this.sourcingForm.attachTemplateList[index].applyFileuploadId = fileId
      this.sourcingForm.attachTemplateList[index].applyFileName = fileName
    },
    applyAttachRemoveHandle (row) {
      row.applyFileuploadId = ''
      row.applyFileName = ''
    },

    async getFormDetail () {
      let { reqHeadId, vendorId } = this.$attrs.params.row
      if (!vendorId) {
        vendorId = this.$store.getters.userInfo.companyId
      }
      let res = await this.$api.inq.sourcing.getFormDetail({ id: reqHeadId })
      if (res.data) {
        let resp = await this.$api.inq.sourcing.getDetail({ reqHeadId, vendorId })
        if (resp.data) {
          res.data = this.signUpDataHandle(res.data, resp.data)
          Object.assign(this.sourcingForm, res.data)
          this.businessId = this.sourcingForm.reqHeadId
          this.$refs.sceneAttachment.loadFileInfo()
        }
      }
    },
    signUpDataHandle (sourcingData, signUpdata) {
      sourcingData.applyIndustry = signUpdata.industry || ''
      sourcingData.applyManageModel = signUpdata.manageModel || ''
      sourcingData.applyRegistCapital = signUpdata.registCapital || ''
      sourcingData.applyPostalAddress = signUpdata.postalAddress || ''
      sourcingData.applyAgentQualifiedBrand = signUpdata.agentQualifiedBrand || ''
      sourcingData.applySupplyArea = signUpdata.supplyArea || ''
      sourcingData.applyCompanyCreationYear = signUpdata.companyCreationYear || ''

      // 供应商联系方式
      sourcingData.applyContactName = signUpdata.contactName || ''
      sourcingData.applyPhone = signUpdata.phone || ''
      sourcingData.applyEmail = signUpdata.email || ''
      sourcingData.applyRemark = signUpdata.remark || ''

      if (sourcingData.reqItemsList.length) {
        sourcingData.reqItemsList.forEach(item => {
          let obj = signUpdata.reqApplyItemsList.filter(i => i.reqItemsId === item.reqItemsId)
          if (obj.length) {
            item.materialSpecDes = obj[0].materialSpecDes
            item.leadTime = obj[0].leadTime
            item.shelfLife = obj[0].shelfLife
            item.makeLife = obj[0].makeLife
            item.mainCustomer = obj[0].mainCustomer
            item.userDirection = obj[0].userDirection
          } else {
            item.materialSpecDes = ''
            item.leadTime = ''
            item.shelfLife = ''
            item.makeLife = ''
            item.mainCustomer = ''
            item.userDirection = ''
          }
        })
      }

      if (sourcingData.attachTemplateList.length) {
        sourcingData.attachTemplateList.forEach(item => {
          let obj = signUpdata.attachList.filter(i => i.attachTemplateId === item.attachTemplateId)
          if (obj.length) {
            item.applyFileName = obj[0].fileName
            item.applyFileuploadId = obj[0].fileuploadId
          } else {
            item.applyFileName = ''
          }
        })
      }
      return sourcingData
    },
    backOne () {
      if (this.$route.query.id) {
        this.$emit('tab-remove', 'sourcingApplicationSupplierDetail')
      } else {
        if (this.$attrs.params.flag === 'add') {
          this.$emit('tab-remove', 'sourcingApplicationDetail')
          this.__setTabTodo('sourcingApplicationList.getQuerydata')
        } else if (this.$attrs.params.flag === 'supplierView') {
          this.$emit('tab-remove', this.$attrs.params.tabName)
          this.__setTabTodo('sourcingApplicationList.getQuerydata')
        } else {
          this.$emit('tab-remove', this.$attrs.params.tabName)
        }
      }
    },
    refuseOne () {
      this.$prompt(this.$t('sourcingBuyer.refuseSignUp'), this.$t('sourcingBuyer.refuseReason'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        inputValidator: value => !!value,
        inputErrorMessage: this.$t('sourcingBuyer.refuseReasonIsRequired')
      }).then(async ({ value }) => {
        await this.$api.inq.sourcing.refuseSignUp({
          refuseReason: value,
          reqHeadId: this.$attrs.params.row.reqHeadId
        })
        this.$message.success(this.$t('sourcingBuyer.refused'))
        this.backOne()
      })
    },
    async submitOne () {
      this.$refs.sourcingForm.validate(async valid => {
        if (valid) {
          for (const i of this.sourcingForm.attachTemplateList) {
            if (i.required === 'Y') {
              if (!i.applyFileName) {
                return this.__jump_error__(
                  'applyAttach',
                  null,
                  this.$t('sourcingBuyer.applyAttachIsRequired'),
                )
              }
            }
          }
          this.handleData(false)
        } else {
          this.__jump_error__('applyInfo', null, null)
        }
      })
    },
    saveOne () {
      this.handleData(true)
    },
    handleData (isTempSave) {
      // 需求信息
      let reqApplyItemsList = this.sourcingForm.reqItemsList.map(item => {
        return {
          reqItemsId: item.reqItemsId,
          materialSpecDes: item.materialSpecDes,
          leadTime: item.leadTime,
          shelfLife: item.shelfLife,
          makeLife: item.makeLife,
          mainCustomer: item.mainCustomer,
          userDirection: item.userDirection
        }
      })
      // 供应商附件
      let attachList = this.sourcingForm.attachTemplateList.map(item => {
        return {
          attachTemplateId: item.attachTemplateId,
          fileuploadId: item.applyFileuploadId,
          fileName: item.applyFileName,
          createdBy: item.applyCreatedBy,
          creationDate: item.applyCreationDate
        }
      })
      // 供应商信息
      let userInfo = this.$store.getters.userInfo
      let params = {
        reqHeadId: this.sourcingForm.reqHeadId,
        applyStatus: 'SIGN',
        vendorId: userInfo.companyId,
        vendorCode: userInfo.companyCode,
        vendorName: userInfo.companyName,
        // 对供应商要求
        industry: this.sourcingForm.applyIndustry,
        manageModel: this.sourcingForm.applyManageModel,
        registCapital: this.sourcingForm.applyRegistCapital,
        postalAddress: this.sourcingForm.applyPostalAddress,
        agentQualifiedBrand: this.sourcingForm.applyAgentQualifiedBrand,
        supplyArea: this.sourcingForm.applySupplyArea,
        companyCreationYear: this.sourcingForm.applyCompanyCreationYear,

        // 供应商联系方式
        contactName: this.sourcingForm.applyContactName,
        phone: this.sourcingForm.applyPhone,
        email: this.sourcingForm.applyEmail,
        remark: this.sourcingForm.applyRemark,

        reqApplyItemsList,
        attachList,

        isTempSave // true-暂存 false-提交报名
      }
      this.submitFetch(params)
    },
    async submitFetch (data) {
      await this.$api.inq.sourcing.tempSaveOrSubmit(data)
      this.$message.success(this.$t('common.success'))
      if (!data.isTempSave) {
        this.backOne()
      }
    }
  }
}
</script>
<style scoped lang="scss">
.the-sourcingApplicationDetail-detail {
  :deep(.el-table td.el-table__cell div) {
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
  }

  :deep(.el-select__tags) {
    overflow-x: auto;
    flex-wrap: nowrap;
  }
  :deep(.el-tag) {
    height: 16px;
    line-height: 16px;
  }
}
</style>

<style lang="scss">
.province-select-block {
  ul.el-select-dropdown__list {
    display: flex;
    flex-wrap: wrap;
    width: 300px;
  }
}
</style>
