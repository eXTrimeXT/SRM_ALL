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
            <!-- 企业信息  -->
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
                    <el-input v-if="isReadOnly" v-model="sourcingForm.orgName" disabled />
                    <template v-else>
                      <OrganizationSelector
                        ref="organizationSelector"
                        v-model="sourcingForm.orgId"
                        :parent-id="-1"
                        node-type="OU"
                        :placeholder="$t('common.pleaseSelect')"
                        @select="selectHandler"
                      />
                    </template>
                  </el-form-item>
                </srm-col>
                <!-- 库存组织 -->
                <srm-col>
                  <el-form-item
                    :label="$t('sourcingBuyer.organizationName')"
                    :label-width="formLabelWidth"
                    prop="organizationId"
                  >
                    <el-input v-if="isReadOnly" v-model="sourcingForm.organizationName" disabled />
                    <OrganizationSelector
                      v-else
                      ref="organizationSelector2"
                      v-model="sourcingForm.organizationId"
                      :parent-id="sourcingForm.orgId"
                      node-type="INV"
                      :placeholder="$t('common.pleaseSelect')"
                      @select="selectHandler2"
                    />
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
                    <CCategorySelect
                      v-model="sourcingForm.categoryName"
                      :scope="sourcingForm"
                      :placeholder="$t('common.pleaseSelect')"
                      show-key="categoryName"
                      :disabled="isReadOnly"
                      @select="comfirmSelect"
                    />
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
                    <DictSelect
                      v-model="sourcingForm.tradeSiteName"
                      :code="sourcingForm.organizationId"
                      :custom-select-type="sourcingForm.organizationId ? 'RECEIVE_ADDRESS' : ''"
                      :disabled="isReadOnly"
                      @change-value="(val, element) => changeSiteInfo(sourcingForm, element)"
                    />
                  </el-form-item>
                </srm-col>

                <srm-col>
                  <!-- 寻源方式 -->
                  <el-form-item
                    :label="$t('sourcingBuyer.sourcingType')"
                    :label-width="formLabelWidth"
                    prop="type"
                  >
                    <dict-select
                      v-model="sourcingForm.type"
                      code="REQ_HEAD_TYPE"
                      :disabled="isReadOnly"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 创建人 -->
                  <el-form-item
                    :label="$t('sourcingBuyer.createdFullName')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="sourcingForm.createdFullName" disabled />
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
                    <dict-select v-model="sourcingForm.status" code="REQ_HEAD_STATUS" disabled />
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
              <!--  允许对部分物料报名  -->
              <el-form-item label="">
                <el-checkbox
                  v-model="sourcingForm.isPartItem"
                  true-label="Y"
                  false-label="N"
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
                  <!-- 批量导入 -->
                  <el-button v-show="!showRealImport" type="primary" @click="beforeUpload">
                    {{ $t('common.batchImport') }}
                  </el-button>
                  <MImport
                    v-show="showRealImport"
                    ref="import"
                    style="display: inline-block;"
                    :title="$t('common.batchImport')"
                    upLoadUrl="/api-inq/inq/reqhead/importSouReqItems"
                    :extraData="categoryExtraData"
                    @downloadTemplate="categoryDownloadTemplate"
                    @handleSuccess="categoryHandleSuccess"
                  />
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
                  prop="isNoCodeItem"
                  :label="$t('sourcingBuyer.isMaterialSourcing')"
                  width="120"
                >
                  <template slot-scope="{row,$index}">
                    <el-form-item>
                      <el-checkbox
                        v-model="row.isNoCodeItem"
                        true-label="Y"
                        false-label="N"
                        :disabled="isReadOnly"
                        @change="isNoCodeItemHandler(row.isNoCodeItem, $index)"
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
                >
                  <template slot-scope="{row}">
                    <el-form-item v-if="row.isNoCodeItem === 'N'">
                      <QuickSearch
                        :show-input="row.materialCode"
                        show-key="materialCode"
                        :scope-data="row"
                        :preQueryData="preQueryData"
                        name="scc_base_material_item"
                        :disabled="isReadOnly"
                        @close-quicksearch="getItemObj"
                      />
                    </el-form-item>
                  </template>
                </el-table-column>
                <!-- 物料名称 -->
                <el-table-column
                  align="center"
                  prop="materialName"
                  :label="$t('sourcingBuyer.categoryName')"
                  min-width="130"
                  :render-header="_addStarToColumn"
                >
                  <template slot-scope="{row}">
                    <el-form-item>
                      <el-input
                        v-model="row.materialName"
                        :disabled="isReadOnly || row.isNoCodeItem === 'N'"
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
                  :render-header="_addStarToColumn"
                >
                  <template slot-scope="{row,$index}">
                    <el-form-item>
                      <dict-select
                        v-model="row.unit"
                        code="unit"
                        filterable
                        :disabled="isReadOnly || row.isNoCodeItem === 'N'"
                        @focus="selectFocus($index)"
                        @change-value="unitHandler"
                      />
                    </el-form-item>
                  </template>
                </el-table-column>
                <!-- 预计数量 -->
                <el-table-column
                  align="center"
                  prop="quantity"
                  :label="$t('sourcingBuyer.quantity')"
                  width="90"
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
              <FileDynamic
                ref="sceneAttachment"
                v-model="sourcingForm.fileUploads"
                scene-module-code="SCENE_SOU_REQ_ATTACHMENT"
                :business-id="businessId"
                :editable="!isReadOnly"
              />
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
                      <dict-select
                        v-model="manageModel"
                        class="ml10"
                        code="MANAGE_MODEL"
                        multiple
                        :disabled="isReadOnly"
                      />
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
                  min-width="120"
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
                  min-width="120"
                >
                  <template slot-scope="{row}">
                    <el-form-item>
                      <el-input v-model="row.remark" :disabled="isReadOnly" />
                    </el-form-item>
                  </template>
                </el-table-column>
                <!-- 附件 -->
                <el-table-column
                  align="center"
                  prop="fileName"
                  :label="$t('sourcingBuyer.attach')"
                  min-width="120"
                >
                  <template slot-scope="{row,$index}">
                    <SrmCommonFile
                      :default-file="{
                        fileId: row.fileuploadId,
                        fileName: row.fileName
                      }"
                      :extra-data="fileInfo"
                      :readonly="!!isReadOnly"
                      @on-change="value => applyAttachUploadSuccess(value, $index)"
                    />
                  </template>
                </el-table-column>
                <el-table-column :label="$t('common.operation')" width="60">
                  <template slot-scope="{$index}">
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
              <OriginInviteSuppliers
                ref="inviteSuppliers"
                business-type="SOURCING"
                :materialData="materialData"
                :inviteSuppliersData="sourcingForm.reqApplyList"
                :showSuppliersPermission="false"
                :baseInfo="inviteSuppliersBaseInfo"
                :isReadonly="isReadOnly || !sourcingForm.orgId || !sourcingForm.categoryId"
              />
            </el-collapse-item>

            <!-- 采购方联系方式 -->
            <el-collapse-item :title="$t('sourcingBuyer.contactInfo')" name="8">
              <OriginContactInfo
                ref="contactInfo"
                businessType="SOURCING"
                :setDefault="true"
                :infoData="sourcingForm"
                :readOnly="isReadOnly"
              />
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </div>
      <CToolbar>
        <template slot="right">
          <el-button v-if="!$route.query.id" @click="backOne">
            {{ isReadOnly ? $t("common.close") : $t("common.cancel") }}
          </el-button>
          <!-- 暂存 -->
          <el-button v-if="!isReadOnly" type="primary" @click="saveOne">
            {{ $t("common.staging") }}
          </el-button>
          <el-button v-if="!isReadOnly" type="primary" @click="submitOne">
            {{ $t('common.publish') }}
          </el-button>
          <el-button
            v-if="$route.query.id && $store.getters.userType === 'VENDOR'"
            type="primary"
            @click="signUp"
          >
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
import CCategorySelect from 'lib@/components/c-category-select'
import OriginContactInfo from 'lib@/composition/origin/contactInfo'
import OriginInviteSuppliers from 'lib@/composition/origin/inviteSuppliers'
import OrganizationSelector from 'lib@/components/organization-selector'
import { tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import MImport from 'lib@/components/import'
import { downloadFileLinkByPost, downloadFileLink } from 'lib@/utils/file'
import { deepClone } from '@/utils'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import { cannotLessCurrentTime } from '@/library/mixins/datePickerOptions'
import { getRegion } from '@/api/common'
import sourcingApplicationSupplierDetail from 'mods@/sourcingSupplier/views/sourcingApplicationSupplier/sourcingApplicationDetail'
import { sourcing } from 'modb@/sourcing/api'

export default {
  name: 'SourcingApplicationDetail',
  components: {
    MainHeader,
    CToolbar,
    CCategorySelect,
    MImport,
    QuickSearch,
    OrganizationSelector,
    OriginInviteSuppliers,
    OriginContactInfo,
    FileDynamic
  },
  mixins: [tabTodoMixin, cannotLessCurrentTime],
  data () {
    return {
      activeDims: ['1', '2', '3', '4', '5', '6', '7', '8'],
      sourcingForm: {
        // 企业信息
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
        tradeSiteName: '',
        tradeSiteId: '',
        type: '',
        createdId: '',
        createdFullName: '',
        creationDate: '',
        status: 'DRAFT',
        description: '',

        // 需求信息
        isPartItem: 'N',
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
        tradeSiteName: [
          { required: true, message: this.$t('sourcingBuyer.tradingPlaceIsRequired') }
        ], // 交货地址
        type: [{ required: true, message: this.$t('sourcingBuyer.sourcingTypeIsRequired') }] // 寻源方式
      },
      formLabelWidth: '120px',
      reqItemsListRowIndex: 0, // 需求信息当前行下标
      businessId: null, // 需求信息附件businessId
      fileInfo: {
        fileModular: 'sourcingBuyer',
        fileFunction: 'sourcingApplication',
        fileType: 'images'
      },
      provinceList: [],
      reqHeadId: null,
      showRealImport: false,
      inviteSuppliersBaseInfo: {
         // 邀请供应控制
         excludeBlackVendors: 'Y',
        excludeNoCurrentOrgVendors: 'N',
        excludeOrgQuitVendors: 'N',
        excludeOrgCategoryStatus: ''
      }
    }
  },
  computed: {
    isReadOnly () {
      let showType = null
      if (this.$attrs.params && this.$attrs.params.showType) {
        showType = this.$attrs.params.showType
      }
      let id = this.$route.query.id || null
      return showType === 'readOnly' || id
    },
    materialData () {
      let data = deepClone(this.sourcingForm.reqItemsList)
      data.forEach(item => {
        item.orgOuId = this.sourcingForm.orgId
        item.categoryId = this.sourcingForm.categoryId
      })
      return data
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
    },
    preQueryData () {
      return { 't.CATEGORY_ID': this.sourcingForm.categoryId }
    },
    categoryExtraData () {
      return {
        fileType: 'excel',
        fileModular: 'sourcing',
        fileFunction: 'sourcingApplicationBuyer',
        reqHeadId: this.reqHeadId
      }
    },
    categoryImportDisabled () {
      return !(
        this.sourcingForm.orgName &&
        this.sourcingForm.organizationName &&
        this.sourcingForm.souReqTitile &&
        this.sourcingForm.categoryName &&
        this.sourcingForm.type
      )
    }
  },
  created () {
    this.getProvinceList()
    if (this.$route.query.id) {
      this.getFormDetail(this.$route.query.id)
    } else {
      const { flag, row } = this.$attrs.params
      if (flag === 'add') {
        this.$nextTick(() => {
          this.$refs.sceneAttachment.loadFileInfo()
        })
      } else {
        this.getFormDetail(row.reqHeadId)
      }
    }
  },
  methods: {
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
      this.sourcingForm.tradeSiteName = element.siteName
      this.sourcingForm.tradeSiteId = element.siteId
    },

    // 新增需求信息
    addRequirementInfo () {
      if (this.sourcingForm.categoryName) {
        this.sourcingForm.reqItemsList.push({
          isNoCodeItem: 'N',
          materialCode: null,
          materialId: null,
          materialName: '',
          unit: '',
          unitName: '',
          quantity: null,
          remark: ''
        })
      } else {
        this.$message.error('请先选择物料品类')
      }
    },
    async beforeUpload () {
      if (this.sourcingForm.type === 'INVITE') {
        this.sourcingForm.reqApplyList = this.$refs.inviteSuppliers.getSuppliersPermissionData()
      }
      let data = { ...this.sourcingForm, isTempSave: true }
      data.fileUploads = this.sourcingForm.fileUploads.filter(item => !!item.fileName)
      let res = await sourcing.tempSaveOrPublishSouReq(data)
      if (res.data && res.data.reqHeadId) {
        this.$message.success('暂存成功')
        this.__setTabTodo('sourcingApplicationList.getQuerydata')
        this.getFormDetail(res.data.reqHeadId, 'staging')
        this.showRealImport = true
        this.$refs.import.importDialogVisible = true
        setTimeout(() => {
          this.showRealImport = false
        }, 0)
      }
    },
    categoryDownloadTemplate () {
      downloadFileLink(
        `/api-inq/inq/reqhead/exportSouReqItemTemplate/${this.sourcingForm.reqHeadId}`,
        '物料导入模板下载.xlsx',
      ).catch(err => {
        this.$message.error(err.message)
      })
    },
    categoryHandleSuccess () {
      this.getFormDetail(this.sourcingForm.reqHeadId, 'import')
    },

    // 无料号寻源
    isNoCodeItemHandler (value, index) {
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
      let selectedData = this.sourcingForm.reqItemsList
      if (val) {
        let hasRow = selectedData.find(i => (i.materialId == val.materialId))
        if (hasRow) {
          this.$message.warning('这个物料已经存在！')
          scope.materialCode = ''
          return false
        } else {
          scope.materialId = val.materialId || ''
          scope.materialCode = val.materialCode || ''
          scope.materialName = val.materialName || ''
          scope.unit = val.unit || ''
          scope.unitName = val.unitName || ''
        }
      } else {
        scope.materialId = ''
        scope.materialCode = ''
        scope.materialName = ''
        scope.unit = ''
        scope.unitName = ''
      }
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
    applyAttachUploadSuccess ({ file }, index) {
      const { fileId = '', fileName = '' } = file || {}
      this.sourcingForm.attachTemplateList[index].fileuploadId = fileId
      this.sourcingForm.attachTemplateList[index].fileName = fileName
    },
    applyAttachRemoveHandle (row) {
      row.fileuploadId = ''
      row.fileName = ''
    },
    // 删除需求信息附件
    deleteApplyAttach (index, row) {
      this.sourcingForm.attachTemplateList.splice(index, 1)
    },

    async getFormDetail (reqHeadId, from = null) {
      let res = await sourcing.getFormDetail({ id: reqHeadId })
      if (res.data) {
        if (from === 'staging') {
          this.reqHeadId = res.data.reqHeadId
          this.sourcingForm.reqHeadId = res.data.reqHeadId
          this.sourcingForm.reqHeadNo = res.data.reqHeadNo
          this.sourcingForm.createdFullName = res.data.createdFullName
          this.sourcingForm.creationDate = res.data.creationDate
        } else if (from === 'import') {
          this.sourcingForm.reqItemsList = res.data.reqItemsList
        } else {
          this.sourcingForm = res.data
          this.businessId = this.sourcingForm.reqHeadId
          this.$refs.sceneAttachment.loadFileInfo()
        }
      }
      /** 只读状态需要移除表单校验结果 */
      if(this.isReadOnly){
        this.$nextTick(() => {
          this.$refs.sourcingForm.clearValidate()
        })
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
      this.submitFetch(true)
    },
    submitOne () {
      this.$refs.sourcingForm.validate(async valid => {
        if (valid) {
          if (this.sourcingForm.reqItemsList.length === 0) {
            // 定位到需求信息栏位
            return this.__jump_error__('itemInfo', null, this.$t('sourcingBuyer.reqItemsListIsRequired'))
          }
          for (const i of this.sourcingForm.reqItemsList) {
            if (!i.materialName) {
            // 定位到需求信息栏位
            return this.__jump_error__('itemInfo', null, this.$t('sourcingBuyer.materialNameIsRequired1'))
            }
            if (!i.unit) {
            // 定位到需求信息栏位
            return this.__jump_error__('itemInfo', null, this.$t('sourcingBuyer.unitIsRequired1'))
            }
            if (i.quantity <= 0) {
            // 定位到需求信息栏位
            return this.__jump_error__('itemInfo', null, this.$t('sourcingBuyer.quantityIsRequired1'))
            }
          }

          let expirationTime = new Date(this.sourcingForm.expirationTime).getTime()
          let tradingTime = new Date(this.sourcingForm.tradingTime).getTime()
          let now = new Date().getTime()
          if (expirationTime < now) {
            return this.__jump_error__('corporateInfo', null, '报名截止时间不能小于当前时间')
          }
          if (tradingTime < now) {
            return this.__jump_error__('corporateInfo', null, '需求交付时间不能小于当前时间')
          }

          this.sourcingForm.status = 'PUBLISHED' // 发布的接口需要status为'PUBLISHED'
          this.submitFetch()
        } else {
          return this.__jump_error__('corporateInfo')
        }
      })
    },
    async submitFetch (isTempSave = false) {
      if (this.sourcingForm.type === 'INVITE') {
        this.sourcingForm.reqApplyList = this.$refs.inviteSuppliers.getSuppliersPermissionData()
      }
      let data = { ...this.sourcingForm, isTempSave }
      data.fileUploads = this.sourcingForm.fileUploads.filter(item => !!item.fileName)
      let res = await sourcing.tempSaveOrPublishSouReq(data)
      if (res.data) {
        this.$message.success(this.$t('common.success'))
        if (isTempSave) {
          this.__setTabTodo('sourcingApplicationList.getQuerydata')
          this.getFormDetail(res.data.reqHeadId, 'staging')
        } else {
          this.backOne()
        }
      }
    },
    async signUp () {
      let res = await sourcing.reqApplyListPage({
        pageNum: 1,
        pageSize: 15,
        reqHeadNo: this.$route.query.reqHeadNo,
        __page: 1,
        __pagesize: 15
      })
      if (res.data) {
        if (res.data.list.length) {
          let row = res.data.list[0]
          this.$emit('tab-add', {
            component: sourcingApplicationSupplierDetail,
            params: {
              flag: 'add',
              row: row,
              tabName: 'sourcingApplicationSupplierDetail'
            },
            title: this.$t('sourcingBuyer.signUp'),
            name: 'sourcingApplicationSupplierDetail'
          })
        } else {
          this.$message.error('您暂无权限报名！')
        }
      }
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
    :deep(.el-input__inner)  {
      padding-left: 40px;
    }
  }

  :deep(.el-select__tags) {
    overflow-x: auto;
    flex-wrap: nowrap;
  }

  :deep(.el-tag--mini){
    height: 16px;
    line-height: 16px;
  }

  .ml10 {
    margin-left: 10px;
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
