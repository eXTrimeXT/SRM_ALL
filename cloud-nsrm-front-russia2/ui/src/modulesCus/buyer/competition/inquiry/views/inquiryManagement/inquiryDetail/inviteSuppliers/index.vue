<template>
  <div class="invite-suppliers">
    <div
      v-if="$slots.toolbar || !isReadonly"
      class="invite-suppliers-operation"
    >
      <!--额外内容插槽-->
      <slot name="toolbar" />

      <template v-if="!isReadonly">
        <!--智能推荐-->
        <el-button
          v-if="showRecommendVendor"
          type="primary"
          @click="getRecommendVendor"
        >
          {{ $t("bidMod.smartRecommond") }}
        </el-button>

        <!--新增供应商-->
        <QuickSearch
          show-button
          :btn-title="$t('vendorMod.addVendor')"
          :multi-select="multiSelect"
          show-key="companyCode"
          name="scc_sup_company_info2"
          class="select-company-search"
          @close-quicksearch="addOneSuppliers"
        />
        <!-- 历史最低价供应商 -->
        <el-button
          v-if="!nextRound"
          type="primary"
          @click="openRecommendVendorDialog('historyLowPrice')"
        >
          {{ $t('cusEntry.common.historyLowPriceSupplier') }}
        </el-button>
        <!-- 删除供应商查看 -->
        <el-button
          v-if="!nextRound"
          type="primary"
          @click="deleteSupplierRecord"
        >
          {{ $t('cusEntry.common.deleteSupplierRecord') }}
        </el-button>
      </template>

      <slot name="buttons" />
    </div>

    <el-form
      :model="inviteSuppliersForm"
      :disabled="isReadonly"
      style="height:100%"
    >
      <el-table
        ref="inviteSuppliersTable"
        :data="inviteSuppliersList"
        style="width: 100%;"
        border
        height="250px"
      >
        <el-table-column
          type="index"
          :label="$t('common.sort')"
          width="50"
        />

        <!--供应商编码-->
        <el-table-column
          prop="vendorCode"
          :label="$t('bidMod.vendorCode')"
          width="150"
          show-overflow-tooltip
        >
          <!-- <template v-slot="scope">
            <QuickSearch
              ref="companyQuickSearch"
              :show-input="scope.row.vendorCode"
              show-key="companyCode"
              :scope-data="scope.row"
              name="scc_sup_company_info"
              :disabled="true"
              @close-quicksearch="setVendorObj"
            />
          </template> -->
        </el-table-column>

        <!--供应商名称-->
        <el-table-column
          prop="vendorName"
          :label="$t('bidMod.vendorName')"
          min-width="150"
          show-overflow-tooltip
        />

        <!--联系人-->
        <el-table-column
          :prop="keyMap.linkManName"
          :label="$t('bidMod.linkMan')"
          width="150"
          show-overflow-tooltip
        >
          <template v-slot="scope">
            <QuickSearch
              :pre-query-data="{ 't.COMPANY_ID': scope.row.vendorId }"
              :show-input="scope.row[keyMap.linkManName]"
              show-key="contactName"
              allow-input
              :scope-data="scope.row"
              :table-index="scope.$index"
              name="scc_sup_contact_info"
              @close-quicksearch="setContactObj"
            />
          </template>
        </el-table-column>

        <!--电话-->
        <el-table-column
          :prop="keyMap.phone"
          :label="$t('bidMod.phone')"
          width="150"
          show-overflow-tooltip
        >
          <template v-slot="scope">
            <el-input v-model="scope.row[keyMap.phone]" />
          </template>
        </el-table-column>

        <!--邮箱-->
        <el-table-column
          prop="email"
          :label="$t('bidMod.email2')"
          width="180"
          show-overflow-tooltip
        >
          <template v-slot="scope">
            <el-input v-model="scope.row.email" />
          </template>
        </el-table-column>
        <!-- 来源 -->
        <el-table-column
          v-if="!nextRound"
          prop="sourceFromType"
          :label="$t('cusEntry.inq.dataSource')"
          width="180"
          show-overflow-tooltip
          :formatter="row => {
            return row.sourceFromType ? $getDictLabel('EXT_SOU_INQ_VENDOR_SOURCE_FROM', row.sourceFromType) : ''
          }"
        />
        <!-- 是否新增供应商 -->
        <el-table-column
          v-if="nextRound"
          prop="newVendorTag"
          :label="$t('cusEntry.inq.isNewVendor')"
          width="100"
          :formatter="row => {
            return row.newVendorTag ? $getDictLabel('YES_OR_NO', row.newVendorTag) : ''
          }"
        />
        <el-table-column
          v-if="!isReadonly"
          fixed="right"
          :label="$t('bidMod.operation')"
          width="100"
        >
          <template v-slot="scope">
            <!--删除-->
            <el-button
              type="text"
              @click="deleteSuppliersItem(scope.$index, scope.row)"
            >
              {{ $t("common.delete") }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!--报价权限列表-->
      <template v-if="showSuppliersPermission && inviteSuppliersList.length">
        <p>{{ $t('bidMod.offerPermissions') }}</p>
        <BaseTable
          ref="suppliersPermissionTable"
          stripe
          :data="suppliersPermissionData"
          :columns="suppliersPermissionTableColumns"
          :empty-text="$t('components.noData')"
          border
          height="250px"
          style="width: 100%"
        >
          <template #checkbox="{ column, row }">
            <el-checkbox
              v-model="row[column.property]"
              true-label="Y"
              false-label="N"
              :disabled="isReadonly"
            />
          </template>
        </BaseTable>
      </template>
    </el-form>

    <!-- 推荐供应商 -->
    <RecommendVendorDialog
      v-if="recommendVendorDialogVisible"
      :visible.sync="recommendVendorDialogVisible"
      :base-info="baseInfo"
      :add-type="addType"
      :vendors-data="inviteSuppliersList"
      :item-list="materialData"
      @saveRecommendVendor="saveRecommendVendor"
    />
    <!-- 查看删除供应商 -->
    <SupplierDeleteRecordDialog
      :visible.sync="supplierDeleteVisible"
      :projectId="baseInfo.projectId"
    />
  </div>
</template>

<script>
/**
 * 邀请供应商
 */
import { BUSINESS_TYPE, targetNumReveal } from 'lib@/composition/origin/composition'
import { mappingPropByBusinessTypeAndKey } from './utils'
import QuickSearch from 'lib@/components/QuickSearch'
import RecommendVendorDialog from './recommendVendorDialog'
import BaseTable from 'lib@/components/BaseTable'
import { getContactInfoByCompanyId } from 'modb@/vendorManagementBuyer/api/supApi'
import SupplierDeleteRecordDialog from './supplier-delete-record-dialog'
import { inqBuyerHttp } from 'modcb@/inquiry/api'
export default {
  name: 'InviteSuppliers',

  components: {
    QuickSearch,
    RecommendVendorDialog,
    BaseTable,
    SupplierDeleteRecordDialog
  },

  // 供应商列表旧数据，以及物料数据由外部传进来，不同的字段在当前做映射处理
  props: {
    // 业务类型
    businessType: {
      type: String,
      required: true,
      validator: value => BUSINESS_TYPE.includes(value)
    },
    // 是否只读
    isReadonly: {
      type: Boolean,
      required: false
    },
    // 供应商数据
    inviteSuppliersData: {
      type: [Array, Object],
      default: () => []
    },
    // 物料数据
    materialData: {
      type: [Array, Object],
      default: () => []
    },
    // 单据基础信息
    baseInfo: {
      type: Object,
      default: () => { /* nothing */ }
    },
    // 是否需要物料权限判断
    showSuppliersPermission: {
      type: Boolean,
      default: false
    },
    // 是否需要智能推荐供应商
    showRecommendVendor: {
      type: Boolean,
      default: true
    },
    /* 是否下一轮引用 */
    nextRound: {
      type: Boolean,
      default: false
    },
    // 是否支持多选
    multiSelect: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      supplierDeleteVisible: false,
      addType: '',
      inviteSuppliersForm: {},
      inviteSuppliersList: [],
      suppliersPermissionData: [],
      recommendVendorDialogVisible: false
    }
  },

  computed: {
    // key map 计算一次缓存下来
    keyMap () {
      const mappingProp = key => {
        return mappingPropByBusinessTypeAndKey(this.businessType, key)
      }
      return {
        targetId: mappingProp('targetId'),
        targetNum: mappingProp('targetNum'),
        targetDesc: mappingProp('targetDesc'),
        quantity: mappingProp('quantity'),
        requirementLineId: mappingProp('requirementLineId'),
        linkManName: mappingProp('linkManName'),
        authList: mappingProp('authList'),
        phone: mappingProp('phone'),
        quoteForbid: mappingProp('quoteForbid')
      }
    },

    // 报价权限表格列表配置
    suppliersPermissionTableColumns () {
      if (!this.showSuppliersPermission) {
        return []
      }

      const columns = [
        // 序号
        {
          attrs: {
            label: () => this.$t('common.sort'),
            type: 'index',
            minWidth: '50'
          }
        },
        // 物料编码
        {
          attrs: {
            minWidth: '150',
            label: () => this.$t('bidMod.itemCode'),
            prop: this.keyMap.targetNum,
            formatter: (row, column, cellValue) => targetNumReveal(cellValue)
          }
        },
        // 物料名称
        {
          attrs: {
            minWidth: '150',
            label: () => this.$t('bidMod.itemDesc'),
            prop: this.keyMap.targetDesc
          }
        },
        // 物料分类
        {
          attrs: {
            minWidth: '150',
            label: () => this.$t('bidMod.categoryName'),
            prop: 'categoryName'
          }
        },
        // 单位
        {
          attrs: {
            minWidth: '90',
            label: () => this.$t('bidMod.unit'),
            prop: 'unit',
            formatter: (row, column, cellValue) => this.$getDictLabel('unit', cellValue)
          }
        },
        // 预计数量
        {
          attrs: {
            minWidth: '100',
            label: () => this.$t('bidMod.demandQuantity'),
            prop: this.keyMap.quantity
          }
        }
      ]

      // 过滤存在ID的供应商
      const filterList = this.inviteSuppliersList.filter(item => item.vendorId)

      if (filterList.length) {
        const colWidth = 100
        const vendorColumn = {
          attrs: {
            minWidth: (filterList.length * colWidth).toString(),
            label: () => this.$t('bidMod.isquoteForbid')
          },
          children: filterList.map(item => {
            return {
              attrs: {
                minWidth: colWidth,
                label: item.vendorName || '',
                prop: item.vendorId.toString()
              },
              slot: 'checkbox'
            }
          })
        }
        if (vendorColumn.children.length) {
          // 可用供应商列表
          columns.push(vendorColumn)
        }
      }
      return columns
    }
  },

  watch: {
    inviteSuppliersData: {
      handler (val) {
        if (val && Array.isArray(val)) {
          this.inviteSuppliersList = val.concat([])
          if (this.materialData && this.materialData.length > 0) {
            this.arrangeInitSuppliersPermissionData()
          }
        }
      },
      deep: true,
      immediate: true
    },
    materialData: {
      handler (val) {
        if (val && Array.isArray(val) && val.length > 0) {
          this.arrangeInitSuppliersPermissionData()
        }
      },
      deep: true,
      immediate: true
    }
  },

  methods: {
    /* 获取智能推荐供应商 */
    getRecommendVendor () {
      if (this.materialData.length === 0) {
        this.$message.warning(this.$t('bidMod.inpItemInfo'))
        return false
      }
      inqBuyerHttp.init.getRecommendVendor(this.baseInfo.projectId).then(res => {
        if (res.data) {
          const vendorList = (res.data || []).map(item => {
            // 重构数据结构，只取需要的字段
            return {
              vendorId: item.companyId,
              vendorCode: item.companyCode,
              vendorName: item.companyName,
              contactName: item.contactName,
              ceeaContactMethod: item.ceeaContactMethod,
              email: item.email,
              availableCategoryIds: item.availableCategoryIds || [],
              availableOrgIds: item.availableOrgIds || []
            }
          })
          this.saveRecommendVendor(vendorList)
        }
      })
    },
    /* 删除供应商查看 */
    deleteSupplierRecord () {
      this.supplierDeleteVisible = true
    },
    /* 历史最低价供应商 */
    historyLowPriceSupplierHandle () {},
    /* 初始化编排权限表格数据 */
    arrangeInitSuppliersPermissionData () {
      if (!this.showSuppliersPermission) {
        return false
      }

      this.suppliersPermissionData = this.materialData.map(item => {
        // 权限编排
        let permission = {}

        this.inviteSuppliersData.forEach(vendorItem => {
          const auth = (vendorItem[this.keyMap.authList] || []).find(authItem => authItem[this.keyMap.requirementLineId] === item[this.keyMap.requirementLineId])
          permission[vendorItem.vendorId] = (auth || {})[this.keyMap.quoteForbid] || 'N'
        })

        return {
          [this.keyMap.targetId]: item[this.keyMap.targetId],
          [this.keyMap.targetNum]: item[this.keyMap.targetNum],
          [this.keyMap.targetDesc]: item[this.keyMap.targetDesc],
          categoryId: item.categoryId,
          categoryCode: item.categoryCode,
          categoryName: item.categoryName,
          orgOuId: item.orgOuId,
          orgOuName: item.orgOuName,
          unit: item.unit,
          souItemId: item.souItemId,
          [this.keyMap.requirementLineId]: item[this.keyMap.requirementLineId],
          [this.keyMap.quantity]: item[this.keyMap.quantity],
          ...permission
        }
      })
    },

    /* 新增一个供应商 */
    async addOneSuppliers (val) {
      if (this.inviteSuppliersList.find(item => val.companyId === item.vendorId)) {
        // 供应商已存在，请勿重复添加
        this.$message.warning(`${val ? val.companyName || '' : ''} ${this.$t('bidMod.common.vendorRepeatMsg')}`)
        return
      }

      const vendor = {
        sourceFromType: 'HAND_MAKE',
        vendorId: val.companyId,
        vendorCode: val.companyCode,
        vendorName: val.companyName,
        [this.keyMap.linkManName]: '',
        [this.keyMap.phone]: '',
        email: '',
        newVendorTag: this.nextRound ? 'Y' : 'N'
      }

      const response = await getContactInfoByCompanyId(val.companyId)
      if (Array.isArray((response || {}).data) && (response || {}).data.length > 0) {
        vendor[this.keyMap.linkManName] = response.data[0].contactName
        vendor[this.keyMap.phone] = response.data[0].ceeaContactMethod
        vendor.email = response.data[0].email
      }

      this.inviteSuppliersList.push(vendor)
      this.$bus.$emit('saveInquirySuppliers')
      this.$nextTick(() => {
        this.doLayoutSuppliersPermission()
      })
    },

    /* 选择供应商，冗余数据 */
    async setVendorObj (val, scope) {
      const {
        companyId = '',
        companyCode = '',
        companyName = ''
      } = val || {}

      if (this.inviteSuppliersList.find(item => companyId && companyId === item.vendorId)) {
        // 供应商已存在，请勿重复添加
        this.$message.warning(`${companyName} ${this.$t('bidMod.common.vendorRepeatMsg')}`)
        scope.vendorId = ''
        scope.vendorCode = ''
        scope.vendorName = ''
        scope[this.keyMap.linkManName] = ''
        scope[this.keyMap.phone] = ''
        scope.email = ''
        return
      }

      scope.vendorId = companyId
      scope.vendorCode = companyCode
      scope.vendorName = companyName
      if (!val) {
        scope[this.keyMap.linkManName] = ''
        scope[this.keyMap.phone] = ''
        scope.email = ''
      } else {
        const response = await getContactInfoByCompanyId(companyId)
        if (Array.isArray((response || {}).data) && (response || {}).data.length > 0) {
          scope[this.keyMap.linkManName] = response.data[0].contactName
          scope[this.keyMap.phone] = response.data[0].ceeaContactMethod
          scope.email = response.data[0].email
        }
      }
      this.$nextTick(() => {
        this.doLayoutSuppliersPermission()
      })
    },

    /* 选择联系人，冗余数据 */
    setContactObj (val, row, index) {
      if (typeof val === 'string') {
        row[this.keyMap.linkManName] = val || ''
        row[this.keyMap.phone] = ''
        row.email = ''
        this.inviteSuppliersList.splice(index, 1, row)
        return
      }
      const {
        contactName = '',
        mobileNumber = '',
        ceeaContactMethod = '',
        email = ''
      } = val || {}
      row[this.keyMap.linkManName] = contactName
      row[this.keyMap.phone] = mobileNumber || ceeaContactMethod || ''
      row.email = email
      this.inviteSuppliersList.splice(index, 1, row)
    },

    /* 删除供应商行 */
    async deleteSuppliersItem (index, row) {
      const {
        souVendorId
      } = row
      if (row.sourceFromType === 'AUTO_RECOMMEND') {
        this.$prompt(this.$t('cusEntry.tipMessage.deleteReason'), this.$t('cusEntry.tipMessage.tip'), {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          inputErrorMessage: this.$t('cusEntry.tipMessage.rejectReason'),
          inputValidator: (value) => {
            if (value) {
              return true
            }
            return false
          }
        }).then(async ({ value }) => {
          await inqBuyerHttp.init.deleteSupplier({ souVendorId, delReason: value })
          this.$message.success(this.$t('common.successDelete'))
          this.inviteSuppliersList.splice(index, 1)
        })
      } else {
        await inqBuyerHttp.init.deleteSupplier({ souVendorId })
        this.$message.success(this.$t('common.successDelete'))
        this.inviteSuppliersList.splice(index, 1)
      }
      this.$nextTick(() => {
        this.doLayoutSuppliersPermission()
      })
    },
    /* 打开智能推荐供应商弹窗 */
    openRecommendVendorDialog (type) {
      if (this.materialData.length === 0) {
        this.$message.warning(this.$t('bidMod.inpItemInfo'))
        return
      }
      this.addType = type
      this.recommendVendorDialogVisible = true
    },

    // 新增供应商
    async addVendorList (vendorList) {
      let { data } = await this.$http({
        url: '/api-sup/partner/api/v1/supplier/page',
        method: 'POST',
        data: {
          'pageNum': 1,
          'pageSize': 1000,
          'param': {
            'queryTypes': ['CONTACT'],
            'supplierCodes': [...this.inviteSuppliersList, ...vendorList].map(vendor => vendor.vendorCode)
          }
        }
      })

      const vendorMap = new Map()
      for (let item of data.list) {
        let vendor = item.contactInfos[0] || {}
        vendorMap.set(vendor.supplierId, {
          [this.keyMap.linkManName]: vendor.contactName || '',
          [this.keyMap.phone]: vendor.ceeaContactMethod || '',
          email: vendor.email || ''
        })
      }

      this.inviteSuppliersList = [...this.inviteSuppliersList, ...vendorList.map(item => {
        return {
          sourceFromType: 'AUTO_RECOMMEND',
          ...item,
          // 冗余空值，否则会导致表格输入框组件无法绑定bind
          ...vendorMap.get(item.vendorId)
        }
      })]
    },

    /* 保存智能推荐供应商 */
    async saveRecommendVendor (vendorList = []) {
      await this.addVendorList(vendorList)
      /* 保存供应商数据 */
      if (this.inviteSuppliersList.length) {
        this.$bus.$emit('saveInquirySuppliers')
      }
      if (!this.showSuppliersPermission) {
        return
      }

      // 根据新增的供应商，编排权限数据
      // this.suppliersPermissionData = this.suppliersPermissionData.map(item => {
      //   let suggest = {}
      //   vendorList.forEach(vendorItem => {
      //     if (
      //       !(vendorItem.availableCategoryIds.includes(item.categoryId) &&
      //         vendorItem.availableOrgIds.includes(item.orgOuId))
      //     ) {
      //       // 判断当前物料中，供应商的品类list 以及 实体list中，需要同时存在，如不符合，则建议禁止报价，打勾
      //       suggest = {
      //         ...suggest,
      //         [vendorItem.vendorId]: 'Y'
      //       }
      //     }
      //   })
      //   return {
      //     ...item,
      //     ...suggest
      //   }
      // })

      this.$nextTick(() => {
        this.doLayoutSuppliersPermission()
      })
    },

    /* doLayout */
    doLayoutSuppliersPermission () {
      if (!this.showSuppliersPermission) {
        return false
      }

      if (this.inviteSuppliersList.length > 0 && ((this.$refs.suppliersPermissionTable || {}).$children || [{}])[0]) {
        this.$refs.suppliersPermissionTable.$children[0].doLayout()
      }
    },

    /* 返回当前数据 父组件外部调用 */
    getSuppliersPermissionData () {
      return this.inviteSuppliersList.map(item => {
        let resultItem = {
          ...item
        }

        if (this.showSuppliersPermission) {
          resultItem = {
            ...resultItem,
            [this.keyMap.authList]: this.suppliersPermissionData.map(permissionItem => {
              // 删除冗余的数据
              delete permissionItem.availableCategoryIds
              delete permissionItem.availableOrgIds
              return {
                ...permissionItem,
                // 默认 N
                [this.keyMap.quoteForbid]: permissionItem[item.vendorId] || 'N'
              }
            })
          }
        }

        return resultItem
      })
    },
    /* 重新更新供应商表格布局 */
    renderInviteSuppliersTable () {
      this.$nextTick(() => {
        this.$refs.inviteSuppliersTable.doLayout()
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.invite-suppliers {
  .invite-suppliers-operation {
    margin-bottom: 10px;
  }
  .select-company-search {
    display: inline-block;
    vertical-align: top;
    margin: 0 10px;
  }
}
</style>
