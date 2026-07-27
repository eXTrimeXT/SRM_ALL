// 只有供应商变更才用这个业务组件,这个是供应商地址信息模块
<template>
  <div class="vendorSiteInfos">
    <p class="sub_header">
      <el-button
        v-if="!disabledBol"
        type="primary"
        class="detail-pbtn"
        @click="addSiteInfo"
      >
        {{ $t('common.new') }}
      </el-button>
    </p>
    <el-table
      ref="siteInfoChangesTable"
      :data="
        siteInfoChanges.slice(
          (currentPage - 1) * pageSizeApprovalBiddingItemLis,
          currentPage * pageSizeApprovalBiddingItemLis
        )
      "
      style="width: 100%"
      max-height="250px"
      border
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
      />
      <!-- 业务实体 -->
      <el-table-column
        align="center"
        prop="orgId"
        width="150"
        :show-overflow-tooltip="true"
      >
        <template slot="header">
          <i class="toRequired">*</i>{{ $t('dataConfMod.orgId') }}
        </template>
        <template slot-scope="scope">
          <OrganizationSelector
            ref="organizationSelector2"
            v-model="scope.row.orgId"
            :disabled="disabledBol"
            :parent-id="-1"
            :class="siteInfoChangesYS[scope.$index]?(siteInfoChangesYS[scope.$index].orgId!=scope.row.orgId?'redColorFont':null):'redColorFont'"
            node-type="OU"
            :placeholder="$t('common.pleaseSelect')"
            :scope="scope.row"
            @select="selectHandler"
          />
        </template>
      </el-table-column>
      <!-- 地点名称 -->
      <el-table-column
        align="center"
        prop="vendorSiteCode"
        min-width="150"
        :show-overflow-tooltip="true"
      >
        <template slot="header">
          <i class="toRequired">*</i>{{ $t('vendorMod.siteName') }}
        </template>
        <template slot-scope="scope">
          <DictSelect
            v-model="scope.row.vendorSiteCode"
            code="VENDOR_SITE_CODE"
            :class="siteInfoChangesYS[scope.$index]?(siteInfoChangesYS[scope.$index].vendorSiteCode!=scope.row.vendorSiteCode?'redColorFont':null):'redColorFont'"
            :disabled="disabledBol"
          />
        </template>
      </el-table-column>
      <!-- 国家 -->
      <el-table-column
        align="center"
        prop="country"
        min-width="150"
        :show-overflow-tooltip="true"
      >
        <template slot="header">
          <i class="toRequired">*</i>{{ $t('components.address.country') }}
        </template>
        <template slot-scope="scope">
          <DictSelect
            v-model="scope.row.country"
            code="country"
            filterable
            :class="siteInfoChangesYS[scope.$index]?(siteInfoChangesYS[scope.$index].country!=scope.row.country?'redColorFont':null):'redColorFont'"
            :disabled="disabledBol"
            @change="getCountry(scope.row)"
          />
        </template>
      </el-table-column>
      <!-- 地区 -->
      <el-table-column
        align="center"
        prop="province"
        :label="$t('components.address.area')"
        min-width="150"
        :show-overflow-tooltip="true"
      >
        <template slot-scope="scope">
          <el-select
            v-model="scope.row.province"
            filterable
            :disabled="scope.row.country !== 'CN' || curOpt == 'view' || disabledBol"
            :class="siteInfoChangesYS[scope.$index]?(siteInfoChangesYS[scope.$index].province!=scope.row.province?'redColorFont':null):'redColorFont'"
            @change="provinceChangeHandle2(scope.row)"
          >
            <el-option
              v-for="item in provinceList"
              :key="item.id"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </template>
      </el-table-column>
      <!-- 城市 -->
      <el-table-column
        align="center"
        prop="city"
        :label="$t('components.address.city')"
        min-width="150"
        :show-overflow-tooltip="true"
      >
        <template slot-scope="scope">
          <el-select
            v-model="scope.row.city"
            filterable
            :class="siteInfoChangesYS[scope.$index]?(siteInfoChangesYS[scope.$index].city!=scope.row.city?'redColorFont':null):'redColorFont'"
            :disabled="scope.row.country !== 'CN' || curOpt == 'view' || disabledBol"
          >
            <el-option
              v-for="item in cityList"
              :key="item.id"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </template>
      </el-table-column>
      <!-- 详细地址 -->
      <el-table-column
        align="center"
        prop="addressDetail"
        width="150"
        :show-overflow-tooltip="true"
      >
        <template slot="header">
          <i class="toRequired">*</i>{{ $t('components.address.detailAddress') }}
        </template>
        <template slot-scope="scope">
          <el-input
            v-model="scope.row.addressDetail"
            :class="siteInfoChangesYS[scope.$index]?(siteInfoChangesYS[scope.$index].addressDetail!=scope.row.addressDetail?'redColorFont':null):'redColorFont'"
            :disabled="disabledBol"
          />
        </template>
      </el-table-column>
      <!-- 邮政编码 -->
      <el-table-column
        align="center"
        prop="postCode"
        :label="$t('components.address.postalCode')"
        width="120"
        :show-overflow-tooltip="true"
      >
        <template slot-scope="scope">
          <el-input
            v-model="scope.row.postCode"
            v-input-format="{ type: 'number' }"
            :class="siteInfoChangesYS[scope.$index]?(siteInfoChangesYS[scope.$index].postCode!=scope.row.postCode?'redColorFont':null):'redColorFont'"
            :disabled="disabledBol"
            @change="setRowAmount(scope.row)"
          />
        </template>
      </el-table-column>
      <!-- 地址备注 -->
      <el-table-column
        align="center"
        prop="siteComment"
        :label="$t('components.address.remark')"
        min-width="150"
        :show-overflow-tooltip="true"
      >
        <template slot-scope="scope">
          <el-input
            v-model="scope.row.siteComment"
            :class="siteInfoChangesYS[scope.$index]?(siteInfoChangesYS[scope.$index].siteComment!=scope.row.siteComment?'redColorFont':null):'redColorFont'"
            :disabled="disabledBol"
          />
        </template>
      </el-table-column>
      <!-- 启用 -->
      <el-table-column
        align="center"
        prop="enabledFlag"
        :label="$t('common.enable')"
        width="100"
        :show-overflow-tooltip="true"
      >
        <template slot-scope="scope">
          <el-checkbox
            v-model="scope.row.enabledFlag"
            :disabled="disabledBol"
            :class="siteInfoChangesYS[scope.$index]?(siteInfoChangesYS[scope.$index].enabledFlag!=scope.row.enabledFlag?'redColorFont':null):'redColorFont'"
            true-label="Y"
            false-label="N"
          />
        </template>
      </el-table-column>
      <!-- 操作 -->
      <el-table-column
        fixed="right"
        :label="$t('common.operation')"
        width="60"
      >
        <template slot-scope="scope">
          <el-button
            type="text"
            :disabled="disabledBol"
            @click="vendorDel(scope.$index, scope.row)"
          >
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <div style="width: 100%; margin-top: 15px">
      <el-pagination
        align="center"
        :current-page="currentPage"
        :page-sizes="[5, 10]"
        :page-size="pageSizeApprovalBiddingItemLis"
        layout="total, sizes, prev, pager, next, jumper"
        :total="siteInfoChanges.length"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>
<script>
import OrganizationSelector from 'lib@/components/organization-selector'

export default {
  name: 'VendorSiteInfos',
  components: {
    OrganizationSelector
  },
  model: {
    prop: 'data',
    event: 'change'
  },
  props: {
    curOpt: {
      type: String,
      default () {
        return 'add'
      }
    },
    disabledBol: {
      type: Boolean,
      default () {
        return []
      }
    },
    pageSizeApprovalBiddingItemLis: {
      type: Number,
      required: true
    },
    siteInfoChanges: {
      type: Array,
      default () {
        return []
      }
    },
    siteInfoChangesY: {
      type: Array,
      default () {
        return []
      }
    },
    currentPage: {
      type: Number,
      default () {
        return []
      }
    },
    provinceList: {
      type: Array,
      default () {
        return []
      }
    },
    cityList: {
      type: Array,
      default () {
        return []
      }
    }
  },
  data () {
    return {
      siteInfoChangesYS: []
    }
  },
  computed: {

  },
  watch: {
    // 监听保存提交 按钮变更状态，如果自定义按钮则无需添加
    siteInfoChangesY () {
      this.siteInfoChangesYS = this.siteInfoChangesY.slice(
        (this.currentPage - 1) * this.pageSizeApprovalBiddingItemLis,
        this.currentPage * this.pageSizeApprovalBiddingItemLis
      )
    }
  },
  inject: ['addSiteInfo', 'selectHandler', 'getCountry', 'provinceChangeHandle2', 'setRowAmount', 'vendorDel', 'handleSizeChange', 'handleCurrentChange'],
  mounted () {

  },
  methods: {

  }
}
</script>

<style scope>
.formClassAll form{
  padding-left: 18px
}
.changeTitle{
  background-color: #F6F6F6;
  font-size: 14px ;
  color: #393E45 ;
  overflow: hidden;
  line-height: 40px;
  margin-bottom:20px;
  font-weight: 400;
}
.changeTitle i{
  width: 4px;
  height: 18px;
  background-color: #0077FF;
  margin: 11px 10px 11px 16px;
  display: block;
  float: left;
}
</style>
