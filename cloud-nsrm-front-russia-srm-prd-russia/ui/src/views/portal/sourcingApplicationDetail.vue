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
            <!-- 企业信息 -->
            <el-collapse-item
              ref="corporateInfo"
              :title="$t('sourcingBuyer.corporateInformation')"
              name="1"
            >
              <srm-row>
                <!-- 业务实体 -->
                <srm-col>
                  <el-form-item
                    :label="$t('sourcingBuyer.orgName')"
                    :label-width="formLabelWidth"
                    prop="orgId"
                  >
                    <el-input v-model="sourcingForm.orgName" disabled />
                  </el-form-item>
                </srm-col>
                <!-- 库存组织 -->
                <srm-col>
                  <el-form-item
                    :label="$t('sourcingBuyer.organizationName')"
                    :label-width="formLabelWidth"
                    prop="organizationId"
                  >
                    <el-input v-model="sourcingForm.organizationName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 需求部门 -->
                  <el-form-item
                    :label="$t('sourcingBuyer.reqDepartment')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="sourcingForm.reqDepartment" :disabled="isReadOnly" />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>

            <!-- 招募内容 -->
            <el-collapse-item :title="$t('sourcingBuyer.recruitmentContent')" name="2">
              <srm-row>
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
                    prop="souReqTitile"
                  >
                    <el-input v-model="sourcingForm.souReqTitile" :disabled="isReadOnly" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 物料品类 -->
                  <el-form-item
                    :label="$t('sourcingBuyer.categoryType')"
                    :label-width="formLabelWidth"
                    prop="categoryName"
                  >
                    <el-input v-model="sourcingForm.categoryName" :disabled="isReadOnly" />
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
                      value-format="yyyy-MM-dd HH:mm:ss"
                      :placeholder="$t('common.selectDate')"
                      :picker-options="cannotLessCurrentTimeOptions"
                      :disabled="isReadOnly"
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
                      value-format="yyyy-MM-dd HH:mm:ss"
                      :placeholder="$t('common.selectDate')"
                      :picker-options="cannotLessCurrentTimeOptions"
                      :disabled="isReadOnly"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 交货地址 -->
                  <el-form-item
                    :label="$t('sourcingBuyer.tradingPlace')"
                    :label-width="formLabelWidth"
                    prop="tradeSiteName"
                  >
                    <el-input v-model="sourcingForm.tradeSiteName" :disabled="isReadOnly" />
                  </el-form-item>
                </srm-col>

                <srm-col>
                  <!-- 寻源方式 -->
                  <el-form-item
                    :label="$t('sourcingBuyer.sourcingType')"
                    :label-width="formLabelWidth"
                    prop="type"
                  >
                    <el-input value="寻源需求" :disabled="isReadOnly" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 创建人 -->
                  <el-form-item
                    :label="$t('sourcingBuyer.createdFullName')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="sourcingForm.createdUserName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 创建时间 -->
                  <el-form-item
                    :label="$t('sourcingBuyer.creationDate')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="sourcingForm.creationDate" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 单据状态 -->
                  <el-form-item
                    :label="$t('sourcingBuyer.sourcingStatus')"
                    :label-width="formLabelWidth"
                  >
                    <el-input value="已发布" :disabled="isReadOnly" />
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
                      type="textarea"
                      :rows="2"
                      :disabled="isReadOnly"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>

            <!-- 需求信息 -->
            <el-collapse-item ref="itemInfo" :title="$t('sourcingBuyer.requirementInfo')" name="3">
              <el-form-item label="">
                <el-checkbox
                  v-model="sourcingForm.isAllItems"
                  true-label="N"
                  false-label="Y"
                  :disabled="isReadOnly"
                >
                  {{ $t('sourcingBuyer.isAllItems') }}
                </el-checkbox>
              </el-form-item>
              <MainHeader v-if="!isReadOnly" :l-span="22" :r-span="2">
                <template slot="left">
                  <!-- 新增 -->
                  <el-button type="primary" @click="addRequirementInfo">
                    {{ $t('common.add') }}
                  </el-button>
                  <!-- excel导入 -->
                  <!-- <m-import
                    ref="import"
                    style="display: inline-block; margin: 0 10px;"
                    :title="requirementImportModal.title"
                    :up-load-url="requirementImportModal.upLoadUrl"
                    :extra-data="extraData"
                    @beforeUpload="beforeUpload"
                    @downloadTemplate="downloadTemplate"
                    @handleSuccess="handleSuccess"
                  /> -->
                </template>
              </MainHeader>
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
                <!-- 无料号寻源 -->
                <el-table-column
                  align="center"
                  prop="isMaterialSourcing"
                  :label="$t('sourcingBuyer.isMaterialSourcing')"
                  width="120"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="{row,$index}">
                    <el-form-item>
                      <el-checkbox
                        v-model="row.isMaterialSourcing"
                        true-label="Y"
                        false-label="N"
                        :disabled="isReadOnly"
                        @change="isMaterialSourcingHandler(row.isMaterialSourcing,$index)"
                      />
                    </el-form-item>
                  </template>
                </el-table-column>
                <!-- 物料编码 -->
                <el-table-column
                  align="center"
                  prop="materialCode"
                  :label="$t('sourcingBuyer.materialCode')"
                  width="150"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="{row}">
<!--                    <el-form-item v-if="row.isMaterialSourcing === 'N'">-->
                      <el-input v-model="row.materialCode" :disabled="isReadOnly" />
<!--                    </el-form-item>-->
                  </template>
                </el-table-column>
                <!-- 物料名称 -->
                <el-table-column
                  align="center"
                  prop="materialName"
                  :label="$t('sourcingBuyer.categoryName')"
                  min-width="130"
                  :show-overflow-tooltip="true"
                  :render-header="_addStarToColumn"
                >
                  <template slot-scope="{row}">
                    <el-form-item>
                      <el-input
                        v-model="row.materialName"
                        :disabled="isReadOnly || row.isMaterialSourcing === 'N'"
                      />
                    </el-form-item>
                  </template>
                </el-table-column>
                <!-- 单位 -->
                <el-table-column
                  align="center"
                  prop="unit"
                  :label="$t('sourcingBuyer.unit')"
                  width="150"
                  :show-overflow-tooltip="true"
                  :render-header="_addStarToColumn"
                >
                  <template slot-scope="{row,$index}">
                    <el-form-item>
                      <el-input v-model="row.unit" :disabled="isReadOnly" />
                    </el-form-item>
                  </template>
                </el-table-column>
                <!-- 预计数量 -->
                <el-table-column
                  align="center"
                  prop="quantity"
                  :label="$t('sourcingBuyer.quantity')"
                  width="90"
                  :show-overflow-tooltip="true"
                  :render-header="_addStarToColumn"
                >
                  <template slot-scope="{row}">
                    <el-form-item>
                      <el-input-number
                        v-model="row.quantity"
                        :controls="false"
                        :min="0"
                        class="input-number-precision"
                        :disabled="isReadOnly"
                      />
                    </el-form-item>
                  </template>
                </el-table-column>

                <!-- 备注 -->
                <el-table-column
                  align="center"
                  prop="remark"
                  :label="$t('sourcingBuyer.remark')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="{row}">
                    <el-form-item>
                      <el-input v-model="row.remark" :disabled="isReadOnly" />
                    </el-form-item>
                  </template>
                </el-table-column>

                <!-- 删除 -->
                <el-table-column :label="$t('common.operation')" width="60" fixed="right">
                  <template slot-scope="{row,$index}">
                    <el-button
                      type="text"
                      :disabled="isReadOnly"
                      @click="deleteRequirementInfo($index, row)"
                    >
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>

            <!-- 需求信息附件 -->
            <el-collapse-item :title="$t('sourcingBuyer.requirementInfoAttach')" name="4">
              <el-table
                :data="sourcingForm.fileUploads"
                style="width: 100%"
                border
                max-height="250px"
              >
                <el-table-column align="center" type="index" width="50" />
                <el-table-column
                  align="center"
                  prop="fileName"
                  :label="$t('vendorMod.attachmentName')"
                  min-width="150"
                />
                <el-table-column
                  align="center"
                  prop="remark"
                  :label="$t('bidMod.fileRemark')"
                  min-width="150"
                />
              </el-table>
            </el-collapse-item>

            <!-- 对供应商要求 -->
            <el-collapse-item :title="$t('sourcingBuyer.applyInfo')" name="5">
              <srm-row>
                <srm-col>
                  <!-- 行业 -->
                  <el-form-item :label="$t('sourcingBuyer.industry')" :label-width="formLabelWidth">
                    <div class="form-item-line">
                      <el-checkbox
                        v-model="sourcingForm.isIndustry"
                        true-label="Y"
                        false-label="N"
                        :disabled="isReadOnly"
                      />
                      <el-input
                        v-model="sourcingForm.industry"
                        class="ml10"
                        :disabled="isReadOnly"
                      />
                    </div>
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 经营模式 -->
                  <el-form-item
                    :label="$t('sourcingBuyer.manageModel')"
                    :label-width="formLabelWidth"
                  >
                    <div class="form-item-line">
                      <el-checkbox
                        v-model="sourcingForm.isManageModel"
                        true-label="Y"
                        false-label="N"
                        :disabled="isReadOnly"
                      />
                      <el-input v-model="manageModel" class="ml10" :disabled="isReadOnly" />
                    </div>
                  </el-form-item>
                </srm-col>

                <srm-col>
                  <!-- 注册资本（万元） -->
                  <el-form-item
                    :label="$t('sourcingBuyer.registCapital')"
                    :label-width="formLabelWidth"
                  >
                    <div class="form-item-line">
                      <el-checkbox
                        v-model="sourcingForm.isRegistCapital"
                        true-label="Y"
                        false-label="N"
                        :disabled="isReadOnly"
                      />
                      <el-input
                        v-model="sourcingForm.registCapital"
                        class="ml10 input-prefix"
                        type="number"
                        :disabled="isReadOnly"
                      >
                        <span slot="prefix">至少：</span>
                      </el-input>
                    </div>
                  </el-form-item>
                </srm-col>

                <srm-col>
                  <!-- 企业所在地 -->
                  <el-form-item
                    :label="$t('sourcingBuyer.postalAddress')"
                    :label-width="formLabelWidth"
                  >
                    <div class="form-item-line">
                      <el-checkbox
                        v-model="sourcingForm.isPostalAddress"
                        true-label="Y"
                        false-label="N"
                        :disabled="isReadOnly"
                      />
                      <el-input
                        v-model="sourcingForm.postalAddress"
                        class="ml10"
                        :disabled="isReadOnly"
                      />
                    </div>
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 需具有代理资质的品牌 -->
                  <el-form-item
                    :label="$t('sourcingBuyer.agentQualifiedBrand')"
                    :label-width="formLabelWidth"
                  >
                    <div class="form-item-line">
                      <el-checkbox
                        v-model="sourcingForm.isAgentQualifiedBrand"
                        true-label="Y"
                        false-label="N"
                        :disabled="isReadOnly"
                      />
                      <el-input
                        v-model="sourcingForm.agentQualifiedBrand"
                        class="ml10"
                        :disabled="isReadOnly"
                      />
                    </div>
                  </el-form-item>
                </srm-col>

                <srm-col>
                  <!-- 需供货区域 -->
                  <el-form-item
                    :label="$t('sourcingBuyer.supplyArea')"
                    :label-width="formLabelWidth"
                  >
                    <div class="form-item-line">
                      <el-checkbox
                        v-model="sourcingForm.isSupplyArea"
                        true-label="Y"
                        false-label="N"
                        :disabled="isReadOnly"
                      />
                      <el-select
                        v-model="supplyArea"
                        filterable
                        multiple
                        :disabled="isReadOnly"
                        class="ml10"
                        popper-class="province-select-block"
                      >
                        <el-option
                          v-for="item in provinceList"
                          :key="item.provinceId"
                          :label="item.province"
                          :value="item.province"
                        />
                      </el-select>
                    </div>
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 公司成立年限（年） -->
                  <el-form-item
                    :label="$t('sourcingBuyer.companyCreationYear')"
                    :label-width="formLabelWidth"
                  >
                    <div class="form-item-line">
                      <el-checkbox
                        v-model="sourcingForm.isCompanyCreationYear"
                        true-label="Y"
                        false-label="N"
                        :disabled="isReadOnly"
                      />
                      <el-input
                        v-model="sourcingForm.companyCreationYear"
                        class="ml10 input-prefix"
                        type="number"
                        :disabled="isReadOnly"
                      >
                        <span slot="prefix">至少：</span>
                      </el-input>
                    </div>
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>

            <!-- 供应商附件要求 -->
            <el-collapse-item :title="$t('sourcingBuyer.applyAttach')" name="6">
              <MainHeader v-if="!isReadOnly" :l-span="22" :r-span="2">
                <template slot="left">
                  <el-button type="primary" class="detail-pbtn" @click="addApplyAttaches">
                    {{ $t('common.add') }}
                  </el-button>
                </template>
              </MainHeader>
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
                  min-width="150"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="{row}">
                    <el-form-item>
                      <el-input v-model="row.attachmentName" :disabled="isReadOnly" />
                    </el-form-item>
                  </template>
                </el-table-column>
                <!-- 必填 -->
                <el-table-column
                  align="center"
                  prop="required"
                  :label="$t('sourcingBuyer.required')"
                  width="120"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="{row}">
                    <el-form-item>
                      <el-checkbox
                        v-model="row.required"
                        true-label="Y"
                        false-label="N"
                        :disabled="isReadOnly"
                      />
                    </el-form-item>
                  </template>
                </el-table-column>
                <!-- 要求说明 -->
                <el-table-column
                  align="center"
                  prop="remark"
                  :label="$t('sourcingBuyer.remark1')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="{row}">
                    <el-form-item>
                      <el-input v-model="row.remark" :disabled="isReadOnly" />
                    </el-form-item>
                  </template>
                </el-table-column>
                <!-- 附件 -->
                <el-table-column align="center" prop="fileName" :label="$t('sourcingBuyer.attach')">
                  <template slot-scope="scope">
                    <span>{{scope.row.fileName}}</span>
                    <em class="el-icon-download operation-icon" style="margin-left: 10px;cursor: pointer" @click="downloadF(scope.row.fileuploadId,scope.row.fileName)"></em>
<!--                    <SrmCommonFile-->
<!--                      :extra-data="fileInfo"-->
<!--                      :default-file="{-->
<!--                        fileId: scope.row.fileuploadId,-->
<!--                        fileName: scope.row.fileName-->
<!--                      }"-->
<!--                      :filePreview="false"-->
<!--                      :readonly="true"-->
<!--                      @on-change="({file}) => applyAttachUploadSuccess(file,scope.row)"-->
<!--                    />-->
                  </template>
                </el-table-column>
                <el-table-column :label="$t('common.operation')" width="60">
                  <template slot-scope="{row,$index}">
                    <el-button
                      type="text"
                      :disabled="isReadOnly"
                      @click="deleteApplyAttach($index, row)"
                    >
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>

            <!-- 邀请供应商列表 -->
            <el-collapse-item
              v-if="sourcingForm.type === 'INVITE'"
              :title="$t('sourcingBuyer.inviteApplyList')"
              name="7"
            >
              <origin-invite-suppliers
                ref="inviteSuppliers"
                business-type="SOURCING"
                :materialData="materialData"
                :inviteSuppliersData="sourcingForm.reqApplyList"
                :showSuppliersPermission="false"
                :isReadonly="isReadOnly"
              />
            </el-collapse-item>

            <!-- 采购方联系方式 -->
            <el-collapse-item
              :title="$t('sourcingBuyer.contactInfo')"
              name="8"
              style="margin-bottom:56px"
            >
              <OriginContactInfo
                ref="contactInfo"
                business-type="SOURCING"
                :setDefault="true"
                :infoData="sourcingForm"
                :readOnly="isReadOnly"
              />
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </div>
      <CToolbar v-if="!userInfo.nickname">
        <template slot="right">
          <el-button type="primary" @click="viewSignUp">
            {{ $t('sourcingBuyer.signUp') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import MainHeader from 'lib@/components/Table/MainHeader'
import CToolbar from 'lib@/components/c-toolbar'
import OriginContactInfo from 'lib@/composition/origin/contactInfo'
import _pick from 'lodash/pick'
import { tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import { deepClone } from '@/utils'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import { cannotLessCurrentTime } from '@/library/mixins/datePickerOptions'
import { getRegion } from '@/api/common'
import * as path from '@/utils/path'
import { mapGetters, mapState } from 'vuex'

export default {
  name: 'SourcingApplicationDetail',
  components: {
    MainHeader,
    CToolbar,
    MImport,
    QuickSearch,
    OriginContactInfo,
    FileDynamic
  },
  mixins: [tabTodoMixin, cannotLessCurrentTime],
  data () {
    return {
      activeDims: ['1', '2', '3', '4', '5', '6', '7', '8'],
      sourcingForm: {
        // 企业信息
        tradeSiteName: '',
        orgName: ' ',
        orgCode: '',
        orgId: '',
        organizationName: '',
        organizationCode: '',
        organizationId: '',
        reqDepartment: '',

        // 招募内容
        reqHeadNo: '',
        souReqTitile: '',
        categoryName: '',
        categoryCode: '',
        categoryId: '',
        expirationTime: '',
        tradingTime: '',
        tradingPlace: '',
        type: '',
        createdId: '',
        createdUserName: '',
        creationDate: '',
        status: 'DRAFT',
        description: '',

        // 需求信息
        isAllItems: 'Y',
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

        // 供应商附件要求
        attachTemplateList: [],

        // 邀请供应商列表
        reqApplyList: [],

        // 采购方联系方式
        contactName: '',
        phone: '',
        email: ''
      },
      rules: {
        orgId: [{ required: true, message: this.$t('sourcingBuyer.orgIdIsRequired') }], // 业务实体
        organizationId: [
          { required: true, message: this.$t('sourcingBuyer.organizationIdIsRequired') }
        ], // 库存组织
        reqHeadNo: [{ required: true, message: this.$t('sourcingBuyer.reqHeadNoIsRequired') }], // 寻源单号
        souReqTitile: [
          { required: true, message: this.$t('sourcingBuyer.souReqTitileIsRequired') }
        ], // 寻源标题
        categoryName: [
          { required: true, message: this.$t('sourcingBuyer.categoryNameIsRequired') }
        ], // 物料品类
        expirationTime: [
          { required: true, message: this.$t('sourcingBuyer.expirationTimeIsRequired') }
        ], // 报名截止时间
        tradingTime: [{ required: true, message: this.$t('sourcingBuyer.tradingTimeIsRequired') }], // 需求交付时间
        tradingPlace: [
          { required: true, message: this.$t('sourcingBuyer.tradingPlaceIsRequired') }
        ], // 交货地址
        type: [{ required: true, message: this.$t('sourcingBuyer.sourcingTypeIsRequired') }] // 寻源方式
      },
      formLabelWidth: '120px',
      reqItemsListRowIndex: 0, // 需求信息当前行下标
      businessId: null, // 需求信息附件businessId
      applyAttachesRowIndex: 0, // 供应商附件当前行下标
      fileInfo: {
        fileModular: 'sourcingBuyer',
        fileFunction: 'sourcingApplication',
        fileType: 'images'
      },
      provinceList: []
    }
  },
  computed: {
    ...mapState({
      isPC: state => state.settings.isPC,
      device: state => state.app.device,
      visitedViews: state => state.tagsView.visitedViews,
      routes: state => state.permission.routes,
      entrance: state => state.user.entrance
    }),
    ...mapGetters(['userInfo', 'language', 'languageList', 'sidebar']),
    userId () {
      return this.$store.getters.userId
    },
    userType () {
      return this.$store.getters.userType
    },
    showSignUp () {
      // TODO:勿删，后续需要用到
      // return this.$attrs.params.showType === 'showSignUp' && (!this.$store.getters.userId  || (this.$store.getters.userId && this.$store.getters.userType === 'VENDOR'))
      return false
    },
    isReadOnly () {
      return true
    },
    materialData () {
      return deepClone(this.sourcingForm.reqItemsList)
    },
    manageModel: {
      get: function () {
        const str = this.sourcingForm.manageModel || ''
        return str ? str.split(',') : []
      },
      set: function (val) {
        this.sourcingForm.manageModel = val.toString()
      }
    },
    supplyArea: {
      get: function () {
        const str = this.sourcingForm.supplyArea || ''
        return str ? str.split(',') : []
      },
      set: function (val) {
        this.sourcingForm.supplyArea = val.toString()
      }
    }
  },
  created () {
    // this.getProvinceList()
    console.log(this.$route.query.id)
    const id = this.$route.query.id

    this.getFormDetail(id)
  },
  methods: {
    downloadF (fileuploadId, fileName) {
      const id = this.$route.query.id
      downloadFileLink(
        `/api-inq/inq-anon/reqhead/getReqHead/vendorReqFile?reqHeadId=${id}&docId=${fileuploadId}`,
        fileName
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
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
    // 业务实体
    selectHandler (node, value, scope) {
      this.sourcingForm.orgId = node ? node.organizationId : null
      this.sourcingForm.orgCode = node ? node.organizationCode : null
      this.sourcingForm.orgName = node ? node.organizationName : null
      this.sourcingForm.organizationId = null
      this.sourcingForm.organizationCode = null
      this.sourcingForm.organizationName = null
    },
    // 库存组件
    selectHandler2 (node, value, scope) {
      this.sourcingForm.organizationId = node ? node.organizationId : null
      this.sourcingForm.organizationCode = node ? node.organizationCode : null
      this.sourcingForm.organizationName = node ? node.organizationName : null
    },

    // 确认选中的品类
    comfirmSelect (node, scope) {
      this.sourcingForm.categoryId = node ? node.categoryId : null
      this.sourcingForm.categoryCode = node ? node.categoryCode : ''
      this.sourcingForm.categoryName = node ? node.categoryName : ''
    },
    // 收货地点选择
    changeSiteInfo (row, { element }) {
      this.sourcingForm.tradingPlace = element.siteName
    },

    // 新增需求信息
    addRequirementInfo () {
      this.sourcingForm.reqItemsList.push({
        isMaterialSourcing: 'N',
        materialCode: null,
        materialId: null,
        materialName: '',
        unit: '',
        unitName: '',
        quantity: null,
        remark: ''
      })
    },
    // 无料号寻源
    isMaterialSourcingHandler (value, index) {
      if (value === 'Y') {
        this.sourcingForm.reqItemsList[index].materialId = ''
        this.sourcingForm.reqItemsList[index].materialCode = ''
      } else {
        this.sourcingForm.reqItemsList[index].materialName = ''
        this.sourcingForm.reqItemsList[index].unit = ''
        this.sourcingForm.reqItemsList[index].unitName = ''
      }
    },
    // 物料编码
    getItemObj (val, scope) {
      scope.materialId = val ? val.materialId : ''
      scope.materialCode = val ? val.materialCode : ''
      scope.materialName = val ? val.materialName : ''
      scope.unit = val ? val.unit : ''
      scope.unitName = val ? val.unitName : ''
    },
    selectFocus (index) {
      this.reqItemsListRowIndex = index
    },
    // 单位事件切换选择
    unitHandler (value, dictItem) {
      this.sourcingForm.reqItemsList[this.reqItemsListRowIndex].unitName = dictItem.label
    },
    // 删除需求信息
    deleteRequirementInfo (index, row) {
      this.sourcingForm.reqItemsList.splice(index, 1)
    },
    // beforeUpload () {
    //   const extraData = this.extraData
    //   const header = _pick(this.sourcingForm, [
    //     'categoryCode',
    //     'categoryId',
    //     'categoryName',
    //     'ceeaAssetType',
    //     'ceeaProjectUserNickname',
    //     'ceeaDepartmentName',
    //     'ceeaPurchaseType',
    //     'orgId',
    //     'orgName',
    //     'orgCode',
    //     'organizationCode',
    //     'organizationId',
    //     'organizationName',
    //     'requirementHeadId',
    //     'requirementHeadNum'
    //   ])
    //   this.extraData = { ...header, ...extraData }
    // },
    // handleSuccess ({ data }) {
    //   // 导入成功就刷新界面
    //   if (data.status === 'Y') {
    //     data.data.forEach(row => {
    //       this.sourcingForm.reqItemsList.push(row)
    //     })
    //   }
    // },
    // downloadTemplate () {
    //   downloadFileLink(
    //     '/api-sup-ce/pr/requirementLine/importMaterialItemModelDownload',
    //     this.$t('purchaseDemand.importMaterialItemModelDownload')
    //   ).catch(() => {
    //     this.$message.error(this.$t('purchaseDemand.downloadFail'))
    //   })
    // },

    // requirementAttachUploadSuccess (file) {
    //   const { id, name, createdBy, creationDate } = file
    //   this.sourcingForm.fileUploads[this.requirementAttachesRowIndex].fileuploadId = id.toString()
    //   this.sourcingForm.fileUploads[this.requirementAttachesRowIndex].fileName = name
    //   this.sourcingForm.fileUploads[this.requirementAttachesRowIndex].createdBy = createdBy
    //   this.sourcingForm.fileUploads[this.requirementAttachesRowIndex].creationDate = creationDate
    // },
    // // 移除
    // requirementAttachRemove (fileuploadId) { },
    // requirementAttachProgress (percent) { },
    // requirementAttachButtonClick (index) {
    //   this.requirementAttachesRowIndex = index
    // },
    // requirementAttachRemoveHandle (row) {
    //   row.fileuploadId = ''
    //   row.fileName = ''
    // },

    // 添加供应商附件
    addApplyAttaches () {
      this.sourcingForm.attachTemplateList.push({
        attachmentName: '',
        required: 'N',
        remark: '',
        fileuploadId: null,
        fileName: ''
      })
    },
    applyAttachUploadSuccess (file, row) {
      const { fileId = '', fileName = '', createdBy = '', creationDate = '' } = file || {}
      row.fileuploadId = fileId.toString()
      row.fileName = fileName
      row.createdBy = createdBy
      row.creationDate = creationDate
    },
    // 删除需求信息附件
    deleteApplyAttach (index, row) {
      this.sourcingForm.fileUploads.splice(index, 1)
    },

    async getFormDetail (reqHeadId) {
      var res = await this.$http({
        url: '/api-inq/inq-anon/reqhead/get',
        method: 'GET',
        params: { id: reqHeadId },
        loading: true
      })
      if (res.data) {
        this.sourcingForm = res.data
        this.businessId = this.sourcingForm.reqHeadId
      }
    },
    backOne () {
      if (this.$attrs.params.flag === 'add') {
        this.$emit('tab-remove', 'sourcingApplicationDetail')
      } else {
        this.$emit('tab-remove', this.$attrs.params.tabName)
      }
      this.__setTabTodo('sourcingApplicationList.getQuerydata')
    },
    saveOne () {
      this.submitFetch()
    },
    submitOne () {
      this.$refs.sourcingForm.validate(async valid => {
        if (valid) {
          if (this.sourcingForm.reqItemsList.length === 0) {
            return this.__jump_error__(
              'itemInfo',
              null,
              this.$t('sourcingBuyer.reqItemsListIsRequired'), // 定位到需求信息栏位
            )
          }
          for (const i of this.sourcingForm.reqItemsList) {
            if (!i.materialName) {
              return this.__jump_error__(
                'itemInfo',
                null,
                this.$t('sourcingBuyer.materialNameIsRequired1'), // 定位到需求信息栏位
              )
            }
            if (!i.unit) {
              return this.__jump_error__(
                'itemInfo',
                null,
                this.$t('sourcingBuyer.unitIsRequired1'), // 定位到需求信息栏位
              )
            }
            if (i.quantity <= 0) {
              return this.__jump_error__(
                'itemInfo',
                null,
                this.$t('sourcingBuyer.quantityIsRequired1'), // 定位到需求信息栏位
              )
            }
          }
          this.sourcingForm.status = 'PUBLISHED'
          this.submitFetch()
        } else {
          this.__jump_error__('corporateInfo', null, null) // 定位到企业信息栏位
          return false
        }
      })
    },
    submitFetch () {
      if (this.sourcingForm.type === 'INVITE') {
        this.sourcingForm.reqApplyList = this.$refs.inviteSuppliers.getSuppliersPermissionData()
      }
      this.$http({
        url: '/api-inq/inq/reqhead/addOrUpdate',
        method: 'POST',
        data: this.sourcingForm,
        loading: true
      })
        .then(async data => {
          this.$message.success(this.$t('common.success'))
          this.backOne()
        })
        .catch(err => {
          console.log(err)
        })
    },
    viewSignUp () {
      this.$confirm('报名前需先登录系统，若无账号需先注册', '需要先登录系统', {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$router.push({ path: path.resolve('/login') })
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.the-sourcingApplicationDetail-detail {
  .input-number-precision {
    width: 100%;
    :deep(.el-input__inner) {
      text-align: left;
      padding-left: 8px;
    }
  }
  .form-item-line {
    display: flex;
    align-items: center;
  }

  .input-prefix {
    :deep(.el-input__inner) {
      padding-left: 40px;
    }
    :deep(.el-input__prefix) {
      top: 8px;
    }
  }

  :deep(.el-select__tags) {
    overflow-x: auto;
    flex-wrap: nowrap;
  }
  :deep(.el-tag) {
    height: 16px;
    line-height: 16px;
  }
  .ml10 {
    margin-left: 10px;
  }
  :deep(.el-table td.el-table__cell div ){
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
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
