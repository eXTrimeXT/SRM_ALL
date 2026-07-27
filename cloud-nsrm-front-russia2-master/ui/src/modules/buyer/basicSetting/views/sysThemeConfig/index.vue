<template>
  <el-container
    class="flex-container-notab sysThemeConfig-page"
    direction="vertical"
  >
    <el-main style="padding-bottom: 40px;">
      <el-form
        ref="form"
        v-model="configForm"
        label-width="160px"
      >
        <el-collapse
          v-model="activeCollapse"
          class="tab-form-style"
        >
          <!-- 网站标题 -->
          <el-collapse-item
            :title="$t('themeConfig.webInfo')"
            name="1"
          >
            <el-row :gutter="20">
              <el-col :span="14">
                <el-form-item
                  prop="webName"
                  :label="$t('themeConfig.webName')"
                >
                  <el-input
                    v-if="configForm.webNameInfo.length>0"
                    v-model="configForm.webNameInfo[0].themeValue"
                    :placeholder="$t('themeConfig.webName')"
                    class="input-with-select"
                  >
                    <el-select
                      slot="prepend"
                      v-model="configForm.webNameInfo[0].language"
                      clearable
                      :placeholder="$t('common.language')"
                      style="width: 100px;"
                      @change="(val) => languageChange(val,configForm.webNameInfo[0])"
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
                <i class="el-icon-circle-plus-outline buttonAddAndReduce" @click="webNameAddItem()" />
              </el-col>
              <el-col :span="6" style="text-align: right;">
                <el-button
                  @click="showDemo('webName')"
                >
                  {{$t('themeConfig.demo')}}
                </el-button>
              </el-col>
            </el-row>
            <!-- 第二个开始渲染 -->
            <template v-for="(nameItem, index) in configForm.webNameInfo">
              <el-row
                v-if="index > 0"
                :key="'nameItem_'+index"
                :gutter="20"
                class="menuNames"
              >
                <el-col :span="14">
                  <el-form-item
                    :key="index"
                  >
                    <el-input
                      v-model="nameItem.themeValue"
                      :placeholder="$t('themeConfig.webName')"
                      class="input-with-select"
                    >
                      <el-select
                        slot="prepend"
                        v-model="nameItem.language"
                        clearable
                        :placeholder="$t('common.language')"
                        style="width: 100px;"
                        @change="(val) => languageChange(val,nameItem, index, configForm.webNameInfo)"
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
                  <i class="el-icon-circle-plus-outline buttonAddAndReduce" @click="webNameAddItem()" />
                  <i class="el-icon-remove-outline buttonAddAndReduce" @click="removeWebName(nameItem, index)" />
                </el-col>
              </el-row>
            </template>
            <!-- 系统描述 -->
            <el-row :gutter="20" class="marginTop">
              <el-col :span="14">
                <el-form-item prop="themeValue" :label="$t('themeConfig.webDes')">
                  <el-input
                    v-if="configForm.webDesInfo.length>0"
                    v-model="configForm.webDesInfo[0].themeValue"
                    :placeholder="$t('themeConfig.webDes')"
                    class="input-with-select"
                  >
                    <el-select
                      slot="prepend"
                      v-model="configForm.webDesInfo[0].language"
                      :placeholder="$t('common.language')"
                      style="width: 100px;"
                      @change="(val) => languageChange(val,configForm.webDesInfo[0])"
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
                <i class="el-icon-circle-plus-outline buttonAddAndReduce" @click="webDesAddItem()" />
              </el-col>
              <el-col :span="6" style="text-align: right;">
                <el-button
                  @click="showDemo('webDes')"
                >
                  {{$t('themeConfig.demo')}}
                </el-button>
              </el-col>
            </el-row>
            <!-- 第二个开始渲染 -->
            <template v-for="(desItem, index) in configForm.webDesInfo">
              <el-row
                v-if="index > 0"
                :key="'nameDes_'+index"
                :gutter="20"
                class="menuNames"
              >
                <el-col :span="14">
                  <el-form-item
                    :key="index"
                  >
                    <el-input
                      v-model="desItem.themeValue"
                      :placeholder="$t('themeConfig.webDes')"
                      class="input-with-select"
                    >
                      <el-select
                        slot="prepend"
                        v-model="desItem.language"
                        :placeholder="$t('common.language')"
                        style="width: 100px;"
                        @change="(val) => languageChange(val,desItem, index, configForm.webDesInfo)"
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
                  <i class="el-icon-circle-plus-outline buttonAddAndReduce" @click="webDesAddItem()" />
                  <i class="el-icon-remove-outline buttonAddAndReduce" @click="removeWebDes(desItem, index)" />
                </el-col>
              </el-row>
            </template>
            <!-- 网站标题 -->
            <el-row :gutter="20" class="marginTop">
              <el-col :span="14">
                <el-form-item :label="$t('themeConfig.webTitle')">
                  <el-input
                    v-if="configForm.webTitleInfo.length>0"
                    v-model="configForm.webTitleInfo[0].themeValue"
                    :placeholder="$t('themeConfig.webTitle')"
                    class="input-with-select"
                  >
                    <el-select
                      slot="prepend"
                      v-model="configForm.webTitleInfo[0].language"
                      :placeholder="$t('common.language')"
                      style="width: 100px;"
                      @change="(val) => languageChange(val,configForm.webTitleInfo[0])"
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
                <i class="el-icon-circle-plus-outline buttonAddAndReduce" @click="webTitleAddItem()" />
              </el-col>
              <el-col :span="6" style="text-align: right;">
                <el-button
                  @click="showDemo('webTitle')"
                >
                  {{$t('themeConfig.demo')}}
                </el-button>
              </el-col>
            </el-row>
            <!-- 第二个开始渲染 -->
            <template v-for="(titleItem, index) in configForm.webTitleInfo">
              <el-row
                v-if="index > 0"
                :key="'titleItem_'+index"
                :gutter="20"
                class="menuNames"
              >
                <el-col :span="14">
                  <el-form-item
                    :key="index"
                  >
                    <el-input
                      v-model="titleItem.themeValue"
                      :placeholder="$t('themeConfig.webTitle')"
                      class="input-with-select"
                    >
                      <el-select
                        slot="prepend"
                        v-model="titleItem.language"
                        :placeholder="$t('common.language')"
                        style="width: 100px;"
                        @change="(val) => languageChange(val,titleItem, index, configForm.webTitleInfo)"
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
                  <i class="el-icon-circle-plus-outline buttonAddAndReduce" @click="webTitleAddItem()" />
                  <i class="el-icon-remove-outline buttonAddAndReduce" @click="removeWebTitle(titleItem, index)" />
                </el-col>
              </el-row>
            </template>
            <!-- favicon 浏览器icon -->
            <el-row :gutter="20" class="marginTop">
              <el-col :span="18">
                <el-form-item prop="faviconFileId" :label="$t('themeConfig.favicon')">
                  <div>
                    <srm-common-file
                      :default-file="{
                        fileId: configForm.faviconInfo.fileId,
                        fileName: configForm.faviconInfo.fileName
                      }"
                      :limit="1"
                      multiplePicture
                      :fileSizeTipWithMb="false"
                      list-type="picture-card"
                      :pictureStyleOptions="{
                        width: '100px',
                        height: '100px',
                      }"
                      :validate-options="{
                        accept: ['.ico'],
                        size: 16
                      }"
                      @on-change="faviconHandleUploadSuccess"
                    />
                    <div>{{ $t('themeConfig.faviconTip')}}</div>
                  </div>
                </el-form-item>
              </el-col>
              <el-col :span="6" style="text-align: right;">
                <el-button
                  @click="showDemo('favicon')"
                >
                  {{$t('themeConfig.demo')}}
                </el-button>
              </el-col>
            </el-row>
          </el-collapse-item>
          <!-- logo设置 -->
          <el-collapse-item
            :title="$t('themeConfig.logoInfo')"
            name="2"
          >
            <!-- 系统主logo -->
            <el-row :gutter="20">
              <el-col :span="18">
                <!-- 尺寸 260*48，单个图片不超过300KB，jpeg、jpg或png格式 -->
                <!-- 系统主logo -->
                <el-form-item :label="$t('themeConfig.mainLogo')">
                  <div>
                    <srm-common-file
                      :default-file="{
                        fileId: configForm.mainLogoInfo.fileId,
                        fileName: configForm.mainLogoInfo.fileName
                      }"
                      :limit="1"
                      multiplePicture
                      :fileSizeTipWithMb="false"
                      list-type="picture-card"
                      :pictureStyleOptions="{
                        width: '100px',
                        height: '100px'
                      }"
                      :validate-options="{
                        accept: ['.png', '.svg'],
                        size: 300
                      }"
                      @on-change="mainLogoHandleUploadSuccess"
                    />
                    <div>{{ $t('themeConfig.mainLogoTip')}}</div>
                  </div>
                </el-form-item>
              </el-col>
              <el-col :span="6" style="text-align: right;">
                <el-button
                  @click="showDemo('mainLogo')"
                >
                  {{$t('themeConfig.demo')}}
                </el-button>
              </el-col>
            </el-row>
            <!-- 次级logo -->
            <el-row :gutter="20" class="marginTop">
              <el-col :span="18">
                <el-form-item :label="$t('themeConfig.subLogo')">
                  <div>
                    <srm-common-file
                      :default-file="{
                        fileId: configForm.subLogoInfo.fileId,
                        fileName: configForm.subLogoInfo.fileName
                      }"
                      :limit="1"
                      multiplePicture
                      :fileSizeTipWithMb="false"
                      list-type="picture-card"
                      :pictureStyleOptions="{
                        width: '100px',
                        height: '100px'
                      }"
                      :validate-options="{
                        accept: ['.png', '.svg'],
                        size: 300
                      }"
                      @on-change="subLogoHandleUploadSuccess"
                    />
                    <div>{{ $t('themeConfig.subLogoTip')}}</div>
                  </div>
                </el-form-item>
              </el-col>
              <el-col :span="6" style="text-align: right;">
                <el-button
                  @click="showDemo('subLogo')"
                >
                  {{$t('themeConfig.demo')}}
                </el-button>
              </el-col>
            </el-row>
            <!-- 菜单主logo -->
            <el-row :gutter="20" class="marginTop">
              <el-col :span="18">
                <el-form-item :label="$t('themeConfig.menuMainLogo')">
                  <div>
                    <srm-common-file
                      :default-file="{
                        fileId: configForm.menuMainLogoInfo.fileId,
                        fileName: configForm.menuMainLogoInfo.fileName
                      }"
                      :limit="1"
                      multiplePicture
                      :fileSizeTipWithMb="false"
                      list-type="picture-card"
                      :pictureStyleOptions="{
                        width: '100px',
                        height: '100px'
                      }"
                      :validate-options="{
                        accept: ['.png', '.svg'],
                        size: 1024
                      }"
                      @on-change="menuMainLogoHandleUploadSuccess"
                    />
                    <div>{{ $t('cusEntry.themeConfig.menuMainLogoTip') }}</div>
                  </div>
                </el-form-item>
              </el-col>
              <el-col :span="6" style="text-align: right;">
                <el-button
                  @click="showDemo('menuMainLogo')"
                >
                  {{$t('themeConfig.demo')}}
                </el-button>
              </el-col>
            </el-row>
            <!-- 菜单收起logo -->
            <el-row :gutter="20" class="marginTop">
              <el-col :span="18">
                <el-form-item :label="$t('themeConfig.menuSubLogo')">
                  <div>
                    <srm-common-file
                      :default-file="{
                        fileId: configForm.menuSubLogoInfo.fileId,
                        fileName: configForm.menuSubLogoInfo.fileName
                      }"
                      :limit="1"
                      multiplePicture
                      :fileSizeTipWithMb="false"
                      list-type="picture-card"
                      :pictureStyleOptions="{
                        width: '100px',
                        height: '100px'
                      }"
                      :validate-options="{
                        accept: ['.png', '.svg'],
                        size: 100
                      }"
                      @on-change="menuSubLogoHandleUploadSuccess"
                    />
                    <div>{{ $t('themeConfig.menuSubLogoTip')}}</div>
                  </div>
                </el-form-item>
              </el-col>
              <el-col :span="6" style="text-align: right;">
                <el-button
                  @click="showDemo('menuSubLogo')"
                >
                  {{$t('themeConfig.demo')}}
                </el-button>
              </el-col>
            </el-row>
          </el-collapse-item>
          <!-- banner设置 -->
          <el-collapse-item
            :title="$t('themeConfig.loginBannerInfo')"
            name="4"
          >
            <el-row>
              <el-col :span="18">
                <el-form-item :label="$t('themeConfig.loginBanner')">
                  <div>
                    <srm-common-file
                      :file-list="configForm.loginBannerInfo"
                      :limit="5"
                      list-type="picture-card"
                      multiplePicture
                      :pictureStyleOptions="{
                        width: '100px',
                        height: '100px',
                      }"
                      :validate-options="{
                        accept: ['.png', '.jpg', '.jpeg'],
                        size: 10240
                      }"
                      @on-change="bannerHandleUploadSuccess"
                    />
                    <div>{{ $t('cusEntry.themeConfig.loginBannerTip') }}</div>
                  </div>
                </el-form-item>
              </el-col>
              <el-col :span="6" style="text-align: right;">
                <el-button
                  @click="showDemo('loginBanner')"
                >
                  {{$t('themeConfig.demo')}}
                </el-button>
              </el-col>
            </el-row>
          </el-collapse-item>
        </el-collapse>
      </el-form>
      <CToolbar>
        <template #right>
          <el-button
            type="primary"
            @click="saveConfigFn"
          >
            {{ $t("common.save") }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
    <srm-dialog
      v-if="dialogVisible"
      :visible.sync="dialogVisible"
      :title="$t('common.preview')"
      size="large"
      :close-on-click-modal="false"
      @close="dialogVisible = false"
    >
      <img :src="imgSrc" alt="" style="width:100%;"/>
      <div slot="footer">
        <el-button
          type="primary"
          @click="dialogVisible = false"
        >
          {{ $t("common.confirm") }}
        </el-button>
      </div>
    </srm-dialog>
  </el-container>
</template>

<script>
import { downloadFileLink } from 'lib@/utils/file'
import CToolbar from 'lib@/components/c-toolbar'
import { accessApi } from 'modb@/accountAccess/api'
import webName from './img/webName.png'
import webDes from './img/webDes.png'
import webTitle from './img/webTitle.png'
import favicon from './img/favicon.png'
import mainLogo from './img/mainLogo.png'
import subLogo from './img/subLogo.png'
import menuMainLogo from './img/menuMainLogo.png'
import menuSubLogo from './img/menuSubLogo.png'
import loginBanner from './img/loginBanner.png'

export default {
  name: 'SysThemeConfig1',

  components: {
    CToolbar
  },
  // themeType 类型如下
  // webName 系统名称
  // webTitle 网站标题
  // favicon 浏览器tab icon
  // mainLogo 门户logo
  // subLogo  次级logo
  // menuMainLogo  菜单主logo
  // menuSubLogo  菜单收起
  // loginBanner  登录页banner
  data () {
    return {
      activeCollapse: ['1', '2', '3', '4'],
      dialogVisible: false,
      demoImgSrc: {
        webName: webName,
        webDes: webDes,
        webTitle: webTitle,
        favicon: favicon,
        mainLogo: mainLogo,
        subLogo: subLogo,
        menuMainLogo: menuMainLogo,
        menuSubLogo: menuSubLogo,
        loginBanner: loginBanner
      },
      imgSrc: '',
      langList: [],
      webNameInfoInit: [
        {
          language: 'zh_CN',
          languageName: this.$t('common.zh'),  // '中文'
          themeType: 'webName',
          themeValue: ''
        }
      ],
      webDesInfoInit: [
        {
          language: 'zh_CN',
          languageName: this.$t('common.zh'),  // '中文'
          themeType: 'webDes',
          themeValue: ''
        }
      ],
      webTitleInfoInit: [
        {
          language: 'zh_CN',
          languageName: this.$t('common.zh'),  // '中文'
          themeType: 'webTitle',
          themeValue: ''
        }
      ],
      configForm: {
        // 系统名称  长城慧采云管理平台
        webNameInfo: [],
        // 系统描述
        webDesInfo: [],
        // 系统标题  长城慧采云
        webTitleInfo: [],
        // 网站标题icon
        faviconInfo: {
          themeType: 'favicon',
          fileId: '',
          fileName: '',
          no: '1',
          language: 'zh_CN',
          languageName: this.$t('common.zh')  // '中文'
        },
        // logo 信息
        mainLogoInfo: {
          themeType: 'mainLogo',
          fileId: '',
          fileName: '',
          no: '1',
          language: 'zh_CN',
          languageName: this.$t('common.zh')  // '中文'
        },
        subLogoInfo: {
          themeType: 'subLogo',
          fileId: '',
          fileName: '',
          no: '1',
          language: 'zh_CN',
          languageName: this.$t('common.zh')  // '中文'
        },
        menuMainLogoInfo: {
          themeType: 'menuMainLogo',
          fileId: '',
          fileName: '',
          no: '1',
          language: 'zh_CN',
          languageName: this.$t('common.zh')  // '中文'
        },
        menuSubLogoInfo: {
          themeType: 'menuSubLogo',
          fileId: '',
          fileName: '',
          no: '1',
          language: 'zh_CN',
          languageName: this.$t('common.zh')  // '中文'
        },
        // banner 信息
        loginBannerInfo: [
          // {
          //   themeType: 'loginBanner',
          //   fileId: '',
          //   fileName: '',
          //   no: '1',
          //   language: 'zh_CN',
          //   languageName: '中文'
          // }
        ]
      }
    }
  },
  async created () {
    this.getLanguage() // 查询语言
    await this.getConfigFn() // 查询配置
  },
  methods: {
    // 保存数据
    saveConfigFn () {
      let webNameInfo = this.configForm.webNameInfo
      let webDesInfo = this.configForm.webDesInfo
      let webTitleInfo = this.configForm.webTitleInfo
      let themeDTOs = []
      let themeFileDTOs = []
      webNameInfo.forEach(i => {
        if (i.language && i.themeValue) {
          themeDTOs.push(i)
        }
      })

      webDesInfo.forEach(j => {
        if (j.language && j.themeValue) {
          themeDTOs.push(j)
        }
      })

      webTitleInfo.forEach(k => {
        if (k.language && k.themeValue) {
          themeDTOs.push(k)
        }
      })

      // 附件
      // faviconInfo
      if (this.configForm.faviconInfo.fileId) {
        themeFileDTOs.push(this.configForm.faviconInfo)
      }
      // mainLogoInfo
      if (this.configForm.mainLogoInfo.fileId) {
        themeFileDTOs.push(this.configForm.mainLogoInfo)
      }
      // subLogoInfo
      if (this.configForm.subLogoInfo.fileId) {
        themeFileDTOs.push(this.configForm.subLogoInfo)
      }
      // menuMainLogoInfo
      if (this.configForm.menuMainLogoInfo.fileId) {
        themeFileDTOs.push(this.configForm.menuMainLogoInfo)
      }
      // menuSubLogoInfo
      if (this.configForm.menuSubLogoInfo.fileId) {
        themeFileDTOs.push(this.configForm.menuSubLogoInfo)
      }
      let saveData = {
        themeDTOs: [],
        themeFileDTOs: []
      }
      saveData.themeDTOs = themeDTOs
      saveData.themeFileDTOs = [
        ...themeFileDTOs,
        ...this.configForm.loginBannerInfo
      ]
      console.log(saveData)
      this.$http({
        url: '/api-base/systemTheme/save',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.$message({
          type: 'success',
          message: this.$t('common.successSave') // '保存成功'
        })
        this.getConfigFn()
      })
    },
    getConfigFn () {
      this.$http({
        url: '/api-base/systemTheme/get',
        method: 'GET',
        loading: true
      }).then(res => {
        const {
          webName = [], webDes = [], webTitle = [],
          favicon = [], mainLogo = [], subLogo = [],
          menuMainLogo = [], menuSubLogo = [], loginBanner = []
        } = res.data
        this.configForm.webNameInfo = webName.length > 0 ? webName : this.webNameInfoInit
        this.configForm.webDesInfo = webDes.length > 0 ? webDes : this.webDesInfoInit
        this.configForm.webTitleInfo = webTitle.length > 0 ? webTitle : this.webTitleInfoInit
        this.configForm.faviconInfo = favicon.length > 0 ? favicon?.[0] : this.configForm.faviconInfo
        this.configForm.mainLogoInfo = mainLogo.length > 0 ? mainLogo?.[0] : this.configForm.mainLogoInfo
        this.configForm.subLogoInfo = subLogo.length > 0 ? subLogo?.[0] : this.configForm.subLogoInfo
        this.configForm.menuMainLogoInfo = menuMainLogo.length > 0 ? menuMainLogo?.[0] : this.configForm.menuMainLogoInfo
        this.configForm.menuSubLogoInfo = menuSubLogo.length > 0 ? menuSubLogo?.[0] : this.configForm.menuSubLogoInfo
        this.configForm.loginBannerInfo = loginBanner
      })
    },
    getLanguage () {
      // 获取语言列表
      accessApi.getLanguageList().then(data => {
        this.langList = data.data
      }).catch(err => {
        console.log(err)
      })
    },
    // 系统名称
    webNameAddItem () {
      this.configForm.webNameInfo.push({
        language: '',
        languageName: '',
        themeType: 'webName',
        themeValue: ''
      })
    },
    // 删除系统名称
    removeWebName (item, index) {
      this.configForm.webNameInfo.splice(index, 1)
    },
    webDesAddItem () {
      this.configForm.webDesInfo.push({
        language: '',
        languageName: '',
        themeType: 'webDes',
        themeValue: ''
      })
    },
    removeWebDes (item, index) {
      this.configForm.webDesInfo.splice(index, 1)
    },
    // 网站标题
    webTitleAddItem () {
      this.configForm.webTitleInfo.push({
        language: '',
        languageName: '',
        themeType: 'webTitle',
        themeValue: ''
      })
    },
    // 删除网站标题
    removeWebTitle (item, index) {
      this.configForm.webTitleInfo.splice(index, 1)
    },

    // favicon
    faviconHandleUploadSuccess (file) {
      const { fileId = '', fileName = '' } = file.file || {}
      this.configForm.faviconInfo.fileId = fileId.toString()
      this.configForm.faviconInfo.fileName = fileName
    },
    // mainLogo
    mainLogoHandleUploadSuccess (file) {
      const { fileId = '', fileName = '' } = file.file || {}
      this.configForm.mainLogoInfo.fileId = fileId.toString()
      this.configForm.mainLogoInfo.fileName = fileName
    },
    // subLogo
    subLogoHandleUploadSuccess (file) {
      const { fileId = '', fileName = '' } = file.file || {}
      this.configForm.subLogoInfo.fileId = fileId.toString()
      this.configForm.subLogoInfo.fileName = fileName
    },
    // menuMainLogo
    menuMainLogoHandleUploadSuccess (file) {
      const { fileId = '', fileName = '' } = file.file || {}
      this.configForm.menuMainLogoInfo.fileId = fileId.toString()
      this.configForm.menuMainLogoInfo.fileName = fileName
    },
    // menuSubLogo
    menuSubLogoHandleUploadSuccess (file) {
      const { fileId = '', fileName = '' } = file.file || {}
      this.configForm.menuSubLogoInfo.fileId = fileId.toString()
      this.configForm.menuSubLogoInfo.fileName = fileName
    },
    // loginBanner 类型
    bannerHandleUploadSuccess ({ fileList = [] }) {
      this.configForm.loginBannerInfo = fileList.map((i, index) => ({
        themeType: 'loginBanner',
        fileId: i.fileId,
        fileName: i.fileName,
        no: index,
        language: 'zh_CN',
        languageName: this.$t('common.zh')  // '中文'
      }))
    },
    languageChange (val, data, index, arr) {
      let row = this.langList.find(i => (i.language == val))
      // 第二个开始判断重复语言配置
      let hasItemArr = arr.filter(v => v.language == val)
      if (hasItemArr.length > 1) {
        this.$message({
          type: 'error',
          message: this.$t('common.langRepeatTip') // '不能设置重复的语言'
        })
        data.language = ''
        data.languageName = ''
      } else {
        if (row) {
          data.languageName = row.languageName
        } else {
          data.languageName = ''
        }
      }
    },
    // 查看示例
    showDemo (type) {
      this.imgSrc = this.demoImgSrc[type]
      this.dialogVisible = true
    }
  }
}
</script>

<style lang="scss">
.sysThemeConfig-page{
  .buttonAddAndReduce{
    color: #96999C;
    font-size: 18px;
    line-height: 28px;
    margin: 0 5px;
    cursor: pointer;
  }
  .input-with-select{
    .el-input-group__prepend{
      background-color: #fff;
    }
  }
  .marginTop{
    margin-top: 16px;
  }
}
</style>
