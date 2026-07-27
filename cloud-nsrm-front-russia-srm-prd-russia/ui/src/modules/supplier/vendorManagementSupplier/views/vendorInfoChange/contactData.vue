// 只有供应商变更才用这个业务组件,这个是联系人信息模块
<template>
  <div class="contactData">
    <div class="left_div">
      <p
        v-if="curOpt !== 'view'"
        class="sub_header"
      >
        <el-button
          v-if="!disabledBol"
          type="primary"
          class="detail-pbtn"
          @click="addContactData"
        >
          {{ $t('common.new') }}
        </el-button>
      </p>
      <el-table
        :data="contactDataS"
        style="width: 100%"
        border
        max-height="250px"
      >
        <el-table-column
          align="center"
          type="index"
          width="50"
        />
        <!-- 姓名 -->
        <el-table-column
          align="center"
          prop="contactName"
          :label="$t('vendorMod.nickname')"
          width="130"
          :show-overflow-tooltip="true"
        >
          <template slot="header">
            <i class="toRequired">*</i>{{ $t('vendorMod.nickname') }}
          </template>
          <template slot-scope="scope">
            <el-input
              v-if="scope.row.opType === 'add' || scope.row.opType === 'update'"
              v-model="scope.row.contactName"
              :disabled="disabledBol"
              :class="contactData[scope.$index]?(contactData[scope.$index].contactName!=scope.row.contactName?'redColorFont':null):'redColorFont'"
              @change="f1(contactData)"
            />
            <span v-else>{{ scope.row.contactName }}</span>
          </template>
        </el-table-column>
        <!-- 性别 -->
        <el-table-column
          align="center"
          prop="ceeaGender"
          :label="$t('vendorMod.sex')"
          width="100"
          :show-overflow-tooltip="true"
        >
          <template slot-scope="scope">
            <DictSelect
              v-model="scope.row.ceeaGender"
              :class="contactData[scope.$index]?(contactData[scope.$index].ceeaGender!=scope.row.ceeaGender?'redColorFont':null):'redColorFont'"
              code="GENDER"
              :disabled="disabledBol"
            />
          </template>
        </el-table-column>
        <!-- 部门 -->
        <el-table-column
          align="center"
          prop="ceeaDeptName"
          :label="$t('vendorMod.department')"
          min-width="100"
          :show-overflow-tooltip="true"
        >
          <template slot-scope="scope">
            <el-input
              v-if="scope.row.opType === 'add' || scope.row.opType === 'update'"
              v-model="scope.row.ceeaDeptName"
              :class="contactData[scope.$index]?(contactData[scope.$index].ceeaDeptName!=scope.row.ceeaDeptName?'redColorFont':null):'redColorFont'"
              :disabled="disabledBol"
            />
            <span v-else>{{ scope.row.ceeaDeptName }}</span>
          </template>
        </el-table-column>
        <!-- 职位 -->
        <el-table-column
          align="center"
          prop="position"
          :label="$t('dataConfMod.position')"
          min-width="100"
          :show-overflow-tooltip="true"
        >
          <template slot-scope="scope">
            <el-input
              v-if="scope.row.opType === 'add' || scope.row.opType === 'update'"
              v-model="scope.row.position"
              :class="contactData[scope.$index]?(contactData[scope.$index].position!=scope.row.position?'redColorFont':null):'redColorFont'"
              :disabled="disabledBol"
            />
            <span v-else>{{ scope.row.position }}</span>
          </template>
        </el-table-column>
        <!-- 联系方式 -->
        <el-table-column
          align="center"
          prop="ceeaContactMethod"
          :label="$t('vendorMod.contactMethod')"
          min-width="100"
          :show-overflow-tooltip="true"
        >
          <template slot-scope="scope">
            <el-input
              v-if="scope.row.opType === 'add' || scope.row.opType === 'update'"
              v-model="scope.row.ceeaContactMethod"
              :class="contactData[scope.$index]?(contactData[scope.$index].ceeaContactMethod!=scope.row.ceeaContactMethod?'redColorFont':null):'redColorFont'"
              :disabled="disabledBol"
            />
            <span v-else>{{ scope.row.ceeaContactMethod }}</span>
          </template>
        </el-table-column>
        <!-- 邮箱 -->
        <el-table-column
          align="center"
          prop="email"
          :label="$t('vendorMod.email')"
          min-width="180"
          :show-overflow-tooltip="true"
        >
          <template slot-scope="scope">
            <el-input
              v-if="scope.row.opType === 'add' || scope.row.opType === 'update'"
              v-model="scope.row.email"
              :class="contactData[scope.$index]?(contactData[scope.$index].email!=scope.row.email?'redColorFont':null):'redColorFont'"
              :disabled="disabledBol"
            />
            <span v-else>{{ scope.row.email }}</span>
          </template>
        </el-table-column>
        <!-- 默认联系人 -->
        <el-table-column
          align="center"
          prop="ceeaDefaultContact"
          :label="$t('dataConfMod.isDefault')"
          min-width="100"
          :show-overflow-tooltip="true"
        >
          <template slot-scope="scope">
            <el-checkbox
              v-model="scope.row.ceeaDefaultContact"
              true-label="Y"
              false-label="N"
              :disabled="disabledBol"
            />
          </template>
        </el-table-column>
        <!-- 备注 -->
        <el-table-column
          align="center"
          prop="ceeaComments"
          :label="$t('dataConfMod.remark')"
          min-width="150"
          :show-overflow-tooltip="true"
        >
          <template slot-scope="scope">
            <el-input
              v-if="scope.row.opType === 'add' || scope.row.opType === 'update'"
              v-model="scope.row.ceeaComments"
              :disabled="disabledBol"
            />
            <span v-else>{{ scope.row.ceeaComments }}</span>
          </template>
        </el-table-column>
        <!-- 拓展字段 [[-->
        <template v-if="contactDimFieldContexts.length > 0">
          <el-table-column
            v-for="col in contactDimFieldContexts"
            :key="col.fieldId"
            :prop="col.fieldCode"
            :label="col.languageCode ? $t(col.languageCode) : col.fieldName"
            min-width="140px"
          >
            <template slot-scope="scope">
              <el-input
                v-if="scope.row.opType === 'add' || scope.row.opType === 'update'"
                v-model="scope.row[col.fieldCode]"
                :disabled="disabledBol"
              />
              <span v-else>{{ scope.row[col.fieldCode] }}</span>
            </template>
          </el-table-column>
        </template>
        <!-- 拓展字段 ]]]-->
        <el-table-column
          fixed="right"
          :label="$t('common.operation')"
          width="120"
        >
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.opType === 'delete' || scope.row.opType === ''"
              :disabled="disabledBol"
              type="text"
              @click="contactRowHandel(scope.$index, scope.row, 'update')"
            >
              {{ $t('common.edit') }}
            </el-button>
            <el-button
              v-if="scope.row.opType === 'delete' || scope.row.opType === 'update'"
              :disabled="disabledBol"
              type="text"
              @click="contactRowHandel(scope.$index, scope.row, 'cancel')"
            >
              {{ $t('common.cancel') }}
            </el-button>
            <el-button
              v-if="
                scope.row.opType === 'add' ||
                  scope.row.opType === 'update' ||
                  scope.row.opType === ''
              "
              type="text"
              :disabled="disabledBol"
              @click="contactDel(scope.$index, scope.row, 'delete')"
            >
              {{ $t('common.delete') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script>

export default {
  name: 'CompanyInfo',
  components: {

  },
  model: {
    prop: 'data',
    event: 'change'
  },
  props: {
    contactDataS: {
      type: Array,
      default () {
        return []
      }
    },
    contactData: {
      type: Array,
      default () {
        return []
      }
    },
    disabledBol: {
      type: Boolean,
      default () {
        return false
      }
    },
    contactDimFieldContexts: {
      type: Array,
      default () {
        return []
      }
    }
  },
  data () {
    return {

    }
  },
  inject: ['addContactData', 'contactRowHandel', 'contactDel'],
  computed: {

  },
  watch: {

  },
  mounted () {

  },
  methods: {
    f1 (scope) {
      console.log(scope)
    }
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
