<template>
  <el-container>
    <el-main class="companyNature">
      <!-- 步骤条 -->
      <el-steps
        class="comInfosteps"
        :active="stepsActive"
        finish-status="success"
        :align-center="true"
      >
        <!-- 申请账号 -->
        <el-step :title="$t('vendorMod.applyUser')" />
        <!-- 选择企业性质 -->
        <el-step :title="$t('vendorMod.selectCompanyType')" />
        <!-- 填写企业信息 -->
        <el-step :title="$t('vendorMod.finishCompanyInfo')" />
        <!-- 提交成功 -->
        <el-step :title="$t('common.successSubmit')" />
      </el-steps>

      <el-form
        ref="relModel"
        :model="relModel.relform"
        :rules="rules"
        class="rel-form-select"
      >
        <srm-row class="boxs-row">
          <srm-col :initCol="1">
            <!-- 境内外关系 -->
            <el-form-item
              :label="$t('vendorMod.overseasRelation')"
              prop="overseasRelation"
            >
              <DictSelect
                v-model="relModel.relform.overseasRelation"
                code="RELATION"
                @change="overseasChangeHandle"
              />
            </el-form-item>
          </srm-col>
          <srm-col
            v-if="allParam.curRel === 'INSIDE'"
            :initCol="1"
          >
            <!-- 企业性质 -->
            <el-form-item
              :label="$t('vendorMod.companyType')"
              prop="companyType"
            >
              <DictSelect
                v-model="relModel.relform.companyType"
                code="COMPANY_NATURE"
                @change="companyTypeChangeHandle"
              />
            </el-form-item>
          </srm-col>
        </srm-row>
      </el-form>

      <CToolbar>
        <template slot="right">
          <!--返回-->
          <el-button
            @click="backTo('stepOne')"
          >
            {{ $t('common.backTo') }}
          </el-button>
          <!-- 下一步 -->
          <el-button
            type="primary"
            @click="backToMain()"
          >
            {{ $t('common.nextOne') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import OrganizationSelector from 'lib@/components/organization-selector'
import { adaptDictData } from '@/utils'
import { getDictItemList } from '@/api/common'
import CToolbar from 'lib@/components/c-toolbar'

export default {
  components: {
    CToolbar,
    OrganizationSelector
  },
  props: {
    relModel: {
      type: Object,
      default: () => {}
    },
    allParam: {
      type: Object,
      default: () => {}
    }
  },
  data () {
    return {
      stepsActive: 1,
      curRel: '',
      companyId: '',
      orgIdList: [], // 组织列表
      rules: {
        overseasRelation: [
          {
            required: true,
            message: this.$t('vendorMod.msgOverseasRelation')
          }
        ], // '请选择境内外关系'
        companyType: [
          { required: true, message: this.$t('vendorMod.msgCompanyType') }
        ], // '请选择企业性质'
        orgId: [
          { required: true, message: this.$t('dataConfMod.msgPleaseSelectOrg') }
        ]
      }
    }
  },
  computed: {

  },
  created () {
    this.getListData()
    this.$bus.$on('getorgIdList', () => {
      this.getorgIdList()
    })
  },
  updated () {},
  methods: {
    // 选择公司的类型
    companyTypeChangeHandle (val) {
      this.allParam.companyInfo.companyType = val
      this.curType = val // 当前公司属性
    },
    // 跳到第三步主编辑界面
    backToMain () {
      this.$refs.relModel.validate((valid) => {
        if (valid) {
          this.$emit('backData', this.allParam)
          this.backTo('main')
        } else {
          return false
        }
      })
    },
    // 返回上一页
    backTo (where) {
      this.$emit('goToWhere', where)
    },
    overseasChangeHandle (val) {
      this.allParam.companyInfo.overseasRelation = val
      this.allParam.curRel = val // 当前海内外关系
      if (val === 'INSIDE') {
        // 境内
        this.allParam.companyInfo.companyCountry = 'CN'
      }
      // this.switchBaseRules()
    },
    // 业务实体改变时
    selectChangeHandler (val) {
      // debugger
      let node = this.orgIdList.find(v => v.organizationId == val) || {}
      this.relModel.relform.orgId = node.organizationId
      this.relModel.relform.orgCode = node.organizationCode
      this.relModel.relform.orgName = node.organizationName
      if (node) {
        this.allParam.orgInfos = [
          {
            orgId: node.organizationId,
            orgCode: node.organizationCode,
            orgName: node.organizationName
          }
        ]
      }
    },
    getListData () {
      let dictParamsArr = [
        { dictCode: 'RELATION' }, // 境内外关系
        { dictCode: 'COMPANY_NATURE' }, // 企业性质
        { dictCode: 'OU_ROOT_ID' }
      ]
      getDictItemList(dictParamsArr).then(async res => {
        const [
          RELATION,
          COMPANY_NATURE,
          OU_ROOT_ID
        ] = res.data
        this.relations = adaptDictData(RELATION.RELATION, 'dict')
        this.natureList = adaptDictData(COMPANY_NATURE.COMPANY_NATURE, 'dict')

        let orgIdList = []
        let getorgIdListData = await this.getorgIdList()

        if (getorgIdListData) {
          orgIdList = getorgIdListData.data || []
        }
        let getOuRootID = adaptDictData(OU_ROOT_ID.OU_ROOT_ID, 'dict')
        if (getOuRootID && getOuRootID.length > 0) {
          getOuRootID.map(item => {
            orgIdList.push({
              organizationId: Number(item.value),
              organizationName: item.label
            })
          })
        }
        this.orgIdList = orgIdList
      })
    },
    getorgIdList () {
      return this.$http({
        url:
          '/api-base/organization/organization/listOrganizationByOrgCode',
        method: 'POST',
        data: {
          organizationTypeCode: 'ou',
          parentOrganizationId: 7546243049783296
        },
        loading: true
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.companyNature{
  position: relative;
}
.boxs-row{
  width: 50%;
  position: absolute;
  top: 40%;
  left: 50%;
  transform: translate(-50%,-50%);
}
</style>
