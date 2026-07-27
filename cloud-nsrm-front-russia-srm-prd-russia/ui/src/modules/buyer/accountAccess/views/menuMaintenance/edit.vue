<template>
  <el-container
    direction="vertical"
  >
    <el-main>
      <div class="form-container">
        <el-form
          ref="form"
          :model="form"
          class="form-menuContainer"
          :rules="rules"
          label-width="70px"
        >
          <el-row :gutter="20">
            <el-col :span="18">
              <!-- 菜单编码 -->
              <el-form-item
                :label="$t('dataConfMod.menuCode')"
                prop="permissionCode"
              >
                <el-input
                  v-model="form.permissionCode"
                  :disabled="isEdit && curOpt!='copy'"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row
            :gutter="20"
            class="menuNames"
          >
            <el-col :span="18">
              <!-- 菜单名称 -->
              <el-form-item
                :label="$t('dataConfMod.menuName')"
                prop="menuNames"
              >
                <el-input
                  v-model="menuNames[0].permissionName"
                  :placeholder="$t('dataConfMod.menuName')"
                  class="input-with-select"
                >
                  <el-select
                    slot="prepend"
                    v-model="menuNames[0].language"
                    :placeholder="$t('common.language')"
                    style="width: 100px;"
                  >
                    <el-option
                      v-for="item in langList"
                      :key="item.language"
                      :label="item.languageName"
                      :value="item.language"
                    />
                  </el-select>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <i class="el-icon-circle-plus-outline buttonAddAndReduce" @click="addItem()" />
            </el-col>
          </el-row>
          <template v-for="(menuItem, index) in menuNames">
            <!-- 第二个开始渲染 -->
            <el-row
              v-if="index > 0"
              :key="'menuItem_'+index"
              :gutter="20"
              class="menuNames"
            >
              <el-col :span="18">
                <el-form-item
                  :key="index"
                  prop="menuNames"
                >
                  <el-input
                    v-model="menuItem.permissionName"
                    :placeholder="$t('dataConfMod.menuName')"
                    class="input-with-select"
                  >
                    <el-select
                      slot="prepend"
                      v-model="menuItem.language"
                      :placeholder="$t('common.language')"
                      style="width: 100px;"
                    >
                      <el-option
                        v-for="item in langList"
                        :key="item.language"
                        :label="item.languageName"
                        :value="item.language"
                      />
                    </el-select>
                    <!-- <el-button
                      slot="append"
                      style="margin:0;padding:7px 37px"
                      @click.prevent="removeName(item, index)"
                    >
                      {{ $t("common.delete") }}
                    </el-button> -->
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="4">
                <i class="el-icon-circle-plus-outline buttonAddAndReduce" @click="addItem()" />
                <i class="el-icon-remove-outline buttonAddAndReduce" @click="removeName(menuItem, index)" />
              </el-col>
            </el-row>
          </template>

          <el-row :gutter="32" type="flex">
            <el-col>
              <!-- 父菜单 -->
              <el-form-item
                :label="$t('dataConfMod.parentMenu')"
                prop="parentPermissionName"
              >
                <el-input
                  v-model="form.parentPermissionName"
                  suffix-icon="el-icon-search"
                  @focus="showParentMenu"
                />
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 功能名称 -->
              <el-form-item
                :label="$t('dataConfMod.functionName')"
                prop="functionName"
              >
                <el-input
                  v-model="form.functionName"
                  suffix-icon="el-icon-search"
                  @focus="showFunctionList"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="32" type="flex">
            <el-col>
              <!-- 生效日期 -->
              <el-form-item
                :label="$t('dataConfMod.startDate')"
                prop="startDate"
              >
                <el-date-picker
                  v-model="form.startDate"
                  value-format="timestamp"
                  type="date"
                  :placeholder="$t('common.pleaseSelectDate')"
                />
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 失效日期 -->
              <el-form-item
                :label="$t('dataConfMod.endDate')"
                prop="endDate"
              >
                <el-date-picker
                  v-model="form.endDate"
                  value-format="timestamp"
                  type="date"
                  :placeholder="$t('common.pleaseSelectDate')"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="32" type="flex">
            <el-col>
              <!-- 菜单顺序 -->
              <el-form-item
                :label="$t('dataConfMod.menuSort')"
                prop="sort"
              >
                <el-input
                  v-model="form.sort"
                  type="number"
                />
              </el-form-item>
            </el-col>
            <el-col>
              <!-- icon路径 -->
              <el-form-item
                :label="$t('dataConfMod.iconPath')"
                prop="iconPath"
              >
                <el-input
                  v-model="form.iconPath"
                  disabled
                >
                  <el-button
                    slot="prepend"
                    icon="el-icon-search"
                    @click="showIcons"
                  />
                  <el-button
                    slot="append"
                    icon="el-icon-close"
                    @click="clearIcon"
                  />
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="32" type="flex">
            <el-col>
              <!-- 组织管控维度 -->
              <el-form-item
                :label="$t('dataConfMod.orgControlDim')"
                label-width="93px"
                prop="orgControlDim"
              >
                <el-select
                  v-model="form.orgControlDim"
                  clearable
                >
                  <el-option
                    v-for="organizationItem in organizationList"
                    :key="organizationItem.organizationTypeCode"
                    :label="organizationItem.organizationTypeName"
                    :value="organizationItem.organizationTypeCode"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 启用品类分工 -->
              <el-form-item
                :label="$t('dataConfMod.enableCategoryDivision')"
                label-width="93px"
                prop="enableCategoryDivision"
              >
                <DictSelect
                  v-model="form.enableCategoryDivision"
                  code="YES_OR_NO"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="32" type="flex">
            <el-col>
              <!-- 启用附件管理 -->
              <el-form-item
                :label="$t('dataConfMod.enableAttachManage')"
                label-width="93px"
                prop="enableAttachManage"
              >
                <DictSelect
                  v-model="form.enableAttachManage"
                  code="YES_OR_NO"
                />
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 启用业务状态控制 -->
              <el-form-item
                :label="$t('dataConfMod.enableBusiStateControl')"
                label-width="117px"
                prop="enableBusiStateControl"
              >
                <DictSelect
                  v-model="form.enableBusiStateControl"
                  code="YES_OR_NO"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <ParentMenu
          :id="form.parentPermissionId || parentMenuId"
          :current-perission-id="currentPerissionId"
          :visible="parentMenuVisible"
          @on-ok="onOkParentMenu"
          @on-cancle="onCancleParentMenu"
        />
        <FunctionList
          :id="form.functionId || functionNameCode"
          :visible="functionListVisible"
          @on-ok="onOkFunctionList"
          @on-cancle="onCancleFunctionList"
        />
        <Icons
          :id="form.iconPath"
          :visible="iconModalVisible"
          @close="closeIconModal"
          @clickHandle="chooseIconHandle"
        />
      </div>
      <CToolbar>
        <template #right>
          <el-button
            type="primary"
            :loading="saveLoading"
            @click="save"
          >
            {{ $t('common.affirm') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import CToolbar from 'lib@/components/c-toolbar'
import ParentMenu from './parentMenu'
import FunctionList from './functionList'
import Icons from './icons'
import { menuApi, accessApi } from 'modb@/accountAccess/api'
export default {
  name: 'MenuMaintenanceEdit',
  components: {
    CToolbar,
    ParentMenu,
    FunctionList,
    Icons
  },
  data () {
    const languageNameCkeck = (rule, value, callback) => {
      const menuNames = this.menuNames
      if (!menuNames.length) {
        callback(new Error(this.$t('dataConfMod.msgMenuName'))) // '请填写菜单名称'
      } else if (
        menuNames.filter(i => !i.language || !i.permissionName).length
      ) {
        callback(new Error(this.$t('dataConfMod.msgMenuName'))) // '请填写菜单名称'
      } else {
        callback()
      }
    }
    return {
      functionNameCode: null,
      functionListVisible: false,
      iconModalVisible: false,
      parentMenuVisible: false,
      currentPerissionId: null,
      parentMenuId: null,
      organizationList: [],
      langList: [],
      menuNames: [
        { language: this.$store.getters.language, permissionName: '' }
      ],
      isEdit: false,
      form: {
        permissionCode: '',
        permissionName: '',
        parentPermissionName: '',
        functionName: '',
        startDate: null,
        endDate: null,
        sort: '',
        iconPath: '',
        functionId: '',
        parentPermissionId: ''
      },
      rules: {
        permissionCode: [
          { required: true, message: this.$t('dataConfMod.msgMenuCode') }
        ], // "请输入菜单编码"
        permissionName: [
          { required: true, message: this.$t('dataConfMod.msgMenuName') }
        ], // "请输入菜单名称"
        startDate: [
          { required: true, message: this.$t('dataConfMod.msgStartDate') }
        ], // "请输入生效日期"
        menuNames: [
          { validator: languageNameCkeck, trigger: ['blur', 'change'] }
        ]
      },
      curOpt: 'add',
      saveLoading: false
    }
  },
  computed: {},
  watch: {},
  created () {
    const { flag } = this.$attrs.params
    this.curOpt = flag
    if (flag === 'edit') {
      this.getMenuInfo('edit')
      this.isEdit = true
    } else if (flag === 'copy') {
      this.getMenuInfo('copy')
      this.isEdit = true
    } else { // 新增
      this.$set(this.form, 'startDate', new Date().getTime())
      this.functionNameCode = null
      this.parentMenuId = null
      this.menuNames = []
      this.menuNames.push({
        language: this.$store.getters.language,
        permissionName: ''
      })
    }
    this.$nextTick(() => {
      this.getLanguage()
      this.getOrganizationList()
      // 是否
    })
  },
  mounted () {},
  methods: {
    save () {
      this.saveLoading = true
      this.$refs.form.validate((val) => {
        if (!val) {
          this.$message.warning(this.$t('common.pleasefinishRequired'))
          this.saveLoading = false
          return false
        }
        const params = {
          menu: {
            ...this.form,
            functionId: this.functionNameCode,
            parentPermissionId: this.parentMenuId
          },
          permissionLanguages: this.menuNames
        }
        if (params.menu.startDate) { params.menu.startDate = new Date(params.menu.startDate).getTime() }
        if (params.menu.endDate) {
          params.menu.endDate = new Date(params.menu.endDate).getTime()
        } else {
          params.menu.endDate = ''
        }
        // 编辑 modify | 新增 复制 add
        const path = this.curOpt == 'edit' ? 'modify' : 'add'
        menuApi.permissionModify(params, path).then(data => {
          this.$message({
            message: data.message,
            type: 'success'
          })
          this.saveLoading = false
          this.back()
        }).catch(err => {
          console.log(err)
          this.saveLoading = false
        })
      })
    },
    back () {
      this.$emit('tab-remove', this.$attrs.tabName)
    },
    closeIconModal () {
      this.iconModalVisible = false
    },
    chooseIconHandle (e) {
      const iconPath = e.target.dataset.class
      this.form.iconPath = iconPath
    },
    onOkFunctionList (value) {
      this.functionListVisible = false
      if (!value[0]) {
        this.functionNameCode = ''
        this.form.functionName = ''
        return
      }
      const { functionId, functionName } = value[0]
      this.functionNameCode = functionId
      this.form.functionName = functionName
    },
    onCancleFunctionList () {
      this.functionListVisible = false
    },
    onCancleParentMenu () {
      this.parentMenuVisible = false
    },
    onOkParentMenu (value) {
      this.parentMenuVisible = false
      if (!value[0]) {
        this.parentMenuId = ''
        this.form.parentPermissionName = ''
        return
      }
      const { permissionName, permissionId } = value[0]
      this.parentMenuId = permissionId
      this.form.parentPermissionName = permissionName
    },
    async getMenuInfo (type) {
      const { row } = this.$attrs.params
      const { data = {} } = await menuApi.getPermission({ id: row.permissionId })
      const { menu, permissionLanguages = [] } = data
      this.functionNameCode = menu.functionId // 用于功能选择回选
      this.parentMenuId = menu.parentPermissionId // 父菜单ID
      this.currentPerissionId = menu.permissionId
      if (type == 'copy') {
        this.menuNames = permissionLanguages.map(i => ({
          permissionName: i.permissionName,
          language: i.language
        }))
        this.form = {
          parentPermissionId: menu.parentPermissionId,
          parentPermissionName: menu.parentPermissionName,
          permission: menu.permission,
          permissionCode: menu.permissionCode,
          permissionName: menu.permissionName,
          permissionType: menu.permissionType,
          functionAddress: menu.functionAddress,
          functionCode: menu.functionCode,
          functionDesc: menu.functionDesc,
          functionIcon: menu.functionIcon,
          functionId: menu.functionId,
          functionName: menu.functionName,
          iconPath: menu.iconPath,
          enableAttachManage: menu.enableAttachManage,
          enableBusiStateControl: menu.enableBusiStateControl,
          enableCategoryDivision: menu.enableCategoryDivision,
          enableWorkFlow: menu.enableWorkFlow,
          orgControlDim: menu.orgControlDim,
          sort: menu.sort,
          startDate: menu.startDate ? new Date(menu.startDate) : null,
          endDate: menu.endDate ? new Date(menu.endDate) : null
        }

        console.log(this.form)
      } else {
        this.menuNames = permissionLanguages
        this.form = {
          ...menu,
          startDate: menu.startDate ? new Date(menu.startDate) : null,
          endDate: menu.endDate ? new Date(menu.endDate) : null
        }
      }
    },
    clearIcon () {
      this.form.iconPath = ''
    },
    showIcons () {
      this.iconModalVisible = true
    },
    showFunctionList () {
      this.functionListVisible = true
    },
    showParentMenu () {
      this.parentMenuVisible = true
    },
    removeName (item, index) {
      this.menuNames.splice(index, 1)
    },
    addItem () {
      this.menuNames.push({
        language: '',
        permissionName: ''
      })
    },
    getOrganizationList () {
      accessApi.getOrganizationList().then(data => {
        this.organizationList = data.data
      })
        .catch(err => console.log(err))
    },
    getLanguage () {
      // 获取语言列表
      accessApi.getLanguageList().then(data => {
        this.langList = data.data
      })
        .catch(err => {
          console.log(err)
        })
    }
  }
}
</script>
<style scoped lang="scss">
.form-container{
  // padding: 15px;
  height: 100%;
  overflow: auto;
}
.buttonAddAndReduce{
  color: #96999C;
  font-size: 18px;
  line-height: 28px;
  margin: 0 5px;
  cursor: pointer;
}
</style>
