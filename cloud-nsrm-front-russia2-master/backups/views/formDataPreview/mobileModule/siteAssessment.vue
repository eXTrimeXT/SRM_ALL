<template>
  <div class="model">
    <el-collapse
      v-model="activeNames"
      class="modelA"
      @change="handleChange"
    >
      <el-collapse-item
        title="供应商评审"
        name="1"
      >
        <!-- <el-row>
          <el-col :span="12"
            ><div class="bg-purple">供应商评审类型：</div></el-col
          >
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ this.requirementHead.assessmentType }}

            </div></el-col
          >
        </el-row> -->
        <el-row>
          <el-col :span="6">
            <div class="bg-purple">
              供应商名称：
            </div>
          </el-col>
          <el-col
            :span="18"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.vendorName }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="8"
          >
            <div class="bg-purple">
              供应商评审单号
            </div>
          </el-col>
          <el-col
            :span="16"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.siteFormNumber }}
            </div>
          </el-col>
        </el-row>
        <!-- <el-row>
          <el-col :span="12"
            ><div class="bg-purple">资质审查单号：</div></el-col
          >
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ requirementHead.reviewFormNumber }}
            </div></el-col
          >
        </el-row>
        <el-row>
          <el-col :span="12"><div class="bg-purple">审批状态:</div></el-col>
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{$getDictLabel("APPROVE_STATUS_TYPE",requirementHead.approveStatus)  }}
             </div
          ></el-col>
        </el-row> -->
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              创建人:
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.createdBy }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              部门:
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaDeptName }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="4"
          >
            <div class="bg-purple">
              认证说明:
            </div>
          </el-col>
          <el-col
            :span="20"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.siteFormExplain }}
            </div>
          </el-col>
        </el-row>
        <!-- <el-row>
          <el-col :span="12"><div class="bg-purple">创建时间:</div></el-col>
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ this.requirementHead.creationDate }}
            </div></el-col
          >
        </el-row> -->
      </el-collapse-item>
      <!-- <el-collapse-item title="认证基本信息" name="2">
        <el-row>
          <el-col :span="12"><div class="bg-purple">认证地址(省):</div></el-col>
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ this.requirementHead.siteProvince }}

              </div
          ></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><div class="bg-purple">认证地址(市):</div></el-col>
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ this.requirementHead.siteCity }}

              </div
          ></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"
            ><div class="bg-purple">认证地址(详细地址):</div></el-col
          >
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ this.requirementHead.siteAdress }}
            </div></el-col
          >
        </el-row>
      </el-collapse-item> -->
      <el-collapse-item
        title="业务实体"
        name="3"
        class="model_LineList"
      >
        <template v-if="tableData.length > 10">
          <div class="LineList">
            <el-pagination
              :page-size="pagesize"
              layout="total,prev, next"
              :total="tableData.length"
              @current-change="current_change"
            />
          </div>
          <div class="card">
            <!-- categoryData.slice((currentPage-1)*pagesize,currentPage*pagesize) -->
            <el-table
              :data="
                tableData.slice(
                  (currentPage - 1) * pagesize,
                  currentPage * pagesize
                )
              "
              style="width: 100%; margin-top: 6px;"
            >
              <el-table-column
                prop="index"
                label="序号"
                width="100"
              />
              <el-table-column
                prop="orgName"
                label="引入组织"
                style="width: 40%"
              />
            </el-table>
          </div>
        </template>
        <div
          v-else
          class="card"
        >
          <el-table
            :data="tableData"
            style="width: 100%"
          >
            <el-table-column
              prop="index"
              label="序号"
              width="100"
            />
            <el-table-column
              prop="orgName"
              label="引入组织"
              style="width: 40%"
            />
            <!-- <el-table-column prop="buName" label="事业部" style="width: 40%">
            </el-table-column> -->
          </el-table>
        </div>
      </el-collapse-item>
      <el-collapse-item
        title="采购品类"
        name="4"
        class="model_LineList"
      >
        <template v-if="catData.length > 10">
          <div class="LineList">
            <el-pagination
              :page-size="pagesizeA"
              layout="total,prev, next"
              :total="catData.length"
              @current-change="current_changeA"
            />
          </div>
          <div class="card">
            <!-- categoryData.slice((currentPage-1)*pagesize,currentPage*pagesize) -->
            <el-table
              :data="
                catData.slice(
                  (currentPageA - 1) * pagesizeA,
                  currentPageA * pagesizeA
                )
              "
              style="width: 100%; margin-top: 6px;"
            >
              <el-table-column
                type="index"
                label="序号"
                width="100"
              />
              <el-table-column
                prop="categoryName"
                label="采购品类"
                min-width="200"
              />
            </el-table>
          </div>
        </template>
        <div
          v-else
          class="card"
        >
          <el-table
            :data="catData"
            style="width: 100%"
          >
            <el-table-column
              type="index"
              label="序号"
              width="100"
            />
            <el-table-column
              prop="categoryName"
              label="采购品类"
              min-width="200"
            />
          </el-table>
        </div>
      </el-collapse-item>
      <el-collapse-item
        title="认证结果"
        name="5"
        class="model_LineList"
      >
        <div class="LineList">
          <el-pagination
            :page-size="pagesizeB"
            layout="total,prev, next"
            :total="allParams.length"
            @current-change="current_changeB"
          />
        </div>
        <div
          v-for="(item, i) in allParams.slice((currentPageB - 1) * pagesizeB,currentPageB * pagesizeB)"
          :key="item.i"
          class="card"
        >
          <template>
            <el-row>
              <el-col :span="12">
                <div class="bg-purple">
                  评审人员：
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{ item.ceeaReviewPeople }}
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <div class="bg-purple">
                  评审环节：
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{ $getDictLabel("CEEA_REVIEW_LINK",item.ceeaReviewLink) }}
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <div class="bg-purple">
                  评审日期：
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{ item.ceeaReviewDate }}
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <div class="bg-purple">
                  认证结果：
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{ $getDictLabel("CEEA_AUTH_RESULT",item.ceeaAuthResult) }}
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="4">
                <div class="bg-purple">
                  附件：
                </div>
              </el-col>
              <el-col
                :span="20"
              >
                <div class="bg-purple-light">
                  <c-download-link
                    :id="item.fileuploadId"
                    :name="item.fileSourceName"
                    ellipsis
                    class="download-link-item"
                  />
                </div>
              </el-col>
            </el-row>
          </template>

          <!-- <el-table
          :data="allParams.slice(
                  (currentPageB - 1) * pagesizeB,
                  currentPageB * pagesizeB
                )"
          style="width: 100%"
          border
          max-height="250px"
        >
          <el-table-column align="center" type="index" width="50" />
          <el-table-column
            align="center"
            prop="fileSourceName"
            label="附件"
            min-width="150"
          >
            <template slot-scope="scope">
              <c-download-link
                :id="scope.row.fileuploadId"
                :name="scope.row.fileSourceName"
                ellipsis
                class="download-link-item"
              />
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="ceeaReviewLink"
            label="评审环节"
            min-width="150"
          >
            <template slot-scope="scope">
              {{$getDictLabel("CEEA_REVIEW_LINK",scope.row.ceeaReviewLink)}}

            </template>
          </el-table-column>

          <el-table-column
            align="center"
            prop="ceeaReviewPeople"
            label="评审人员"
            min-width="150"
          >

          </el-table-column>

          <el-table-column
            align="center"
            prop="ceeaVendorAssessor"
            label="供方陪审人员"
            min-width="150"
          >

          </el-table-column>

          <el-table-column
            align="center"
            prop="ceeaReviewDate"
            label="评审日期"
            width="160"
          >

          </el-table-column>

          <el-table-column
            align="center"
            prop="score"
            :label="$t('vendorMod.score')"
            min-width="150"
          >

          </el-table-column>

          <el-table-column
            align="center"
            prop="ceeaAuthResult"
            label="认证结果"
            width="150"
          >
            <template slot-scope="scope">
              {{$getDictLabel("CEEA_AUTH_RESULT",scope.row.ceeaAuthResult)}}

            </template>
          </el-table-column>
        </el-table>  -->
        </div>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>
<script>
import CUploadFile from '@/library/components/c-upload-file'
import CDownloadLink from 'lib@/components/c-download-link'
import { adaptDictData, parseTime } from '@/utils'

export default {
  components: {
    CUploadFile,
    CDownloadLink
  },
  data () {
    return {
      currentPage: 1, // 采购品类初始页
      currentPageA: 1, // 资质审查初始页
      currentPageB: 1, // 认证结果初始页
      pagesize: 10, //    每页的数据
      pagesizeB: 1,
      pagesizeA: 10,
      cityList: [], // 市
      provinceList: [], // 省
      assessmentTypeList: [],
      approveStatus: [],
      ceeaAuthResult: [], // 认证结果
      ceeaReviewLink: [], // 评审环节
      allParams: [],
      catData: [],
      tableData: [],
      dialogVisible: false,
      buObj: {},
      // 文件上传配置信息
      fileInfo: {
        uploadType: 'FASTDFS', // 固定参数
        sourceType: 'WEB_APP', // 固定参数
        fileModular: 'sup', // 文件所属模块 -》基础模块
        fileFunction: 'vendorBiddingManagement', // 文件所属功能
        fileType: 'images' // 文件所属类型
      },
      activeNames: ['1', '2', '3', '4', '5'],
      requirementHead: {},
      requirementAttaches: [],
      requirementLineList: [],
      // index:0,
      num: 0,
      fullscreen: false,
      n: 0,
      isFullscreen: true,
      browserKernel: '',
      canFullScreen: false,
      isFullScreen: false
    }
  },
watch: {
		// assessmentTypeList(){
		// 	this.$nextTick(()=>{
    //     //此时就可以获取到在created赋值后的assessmentTypeList了

		// 	})
		// }
	},

  created () {
    // this.fatchDictData();
    this.getFormDetail(this.$attrs.params.siteFormId)
    this.getSiteFormDetail(this.$attrs.params.siteFormId)
  },
  mounted () {

  },
  methods: {
    // fatchDictData() {
    //   // 批量查询字典
    //   let dictParamsArr = [
    //     { dictCode: "CEEA_ASSESSMENT_TYPE" }, //
    //     { dictCode: "APPROVE_STATUS_TYPE" }, //
    //     { dictCode: "CEEA_AUTH_RESULT" }, // 认证结果
    //     { dictCode: "CEEA_REVIEW_LINK" }, // 评审环节
    //   ];
    //   getDictItemList(dictParamsArr).then((res) => {
    //     const [
    //       CEEA_ASSESSMENT_TYPE,
    //       APPROVE_STATUS_TYPE,
    //       CEEA_AUTH_RESULT,
    //       CEEA_REVIEW_LINK,
    //     ] = res.data;
    //     this.assessmentTypeList = adaptDictData(
    //       CEEA_ASSESSMENT_TYPE.CEEA_ASSESSMENT_TYPE,
    //       "dict"
    //     );
    //     console.log("assessmentTypeList111", this.assessmentTypeList);
    //     this.approveStatus = adaptDictData(
    //       APPROVE_STATUS_TYPE.APPROVE_STATUS_TYPE,
    //       "dict"
    //     );
    //     this.ceeaAuthResult = adaptDictData(
    //       CEEA_AUTH_RESULT.CEEA_AUTH_RESULT,
    //       "dict"
    //     );
    //     this.ceeaReviewLink = adaptDictData(
    //       CEEA_REVIEW_LINK.CEEA_REVIEW_LINK,
    //       "dict"
    //     );
    //   });
    //   getRegion({ queryType: "province" }).then((res) => {
    //     if (res.data) {
    //       this.provinceList = this.adaptProvinceCity(res.data, "province");

    //     }
    //   });
    //   getDictItem("DIVISION").then((res) => {
    //     this.BUList = adaptDictData(res.data, "dict");
    //     this.buObj = {};
    //     for (let i of this.BUList) {
    //       this.buObj[i.value] = i.label;
    //     }
    //   });
    //   // console.log("assessmentTypeList",this.assessmentTypeList)

    //   // console.log(
    //   //       "assessmentType",
    //   //       this.$getLabelByValue(
    //   //         this.assessmentTypeList,
    //   //         "ACCESS_ASSESSMENT"
    //   //       )
    //   //     );
    // },
    // //加载城市
    // getCityData(provinceId) {
    //   let parame = { queryType: "city", parentId: provinceId };
    //   getRegion(parame).then((res) => {
    //     if (res.data) {
    //       this.cityList = this.adaptProvinceCity(res.data, "city");
    //      let siteCity=  this.cityList.find(v=> v.value ===this.requirementHead.siteCity)
    //      this.requirementHead.siteCity = siteCity.label
    //     }
    //   });
    // },

    // adaptProvinceCity(data, type) {
    //   let arr = [];
    //   if (data && data.length > 0) {
    //     if (type === "province") {
    //       // 省
    //       data.forEach((element) => {
    //         arr.push({
    //           id: element.provinceId,
    //           value: element.provinceId.toString(),
    //           label: element.province,
    //         });
    //       });
    //     } else if (type === "city") {
    //       // 市
    //       data.forEach((element) => {
    //         arr.push({
    //           id: element.cityId,
    //           value: element.cityId.toString(),
    //           label: element.city,
    //         });
    //       });
    //     }
    //   }
    //   return arr;
    // },
    // 移除
    outerHandleRemove (fileuploadId) {},
    handleScriptProgress (percent) {},

    getformattor (val) {
      return this.$getLabelByValue(this.assessmentTypeList, val)
    },

    // 认证结果
    getSiteFormDetail (siteFormId) {
      if (!siteFormId) return
      this.$http({
        url: '/api-sup/review/siteForm/getSiteFormDTO',
        method: 'GET',
        params: { siteFormId },
        loading: true
      })
        .then((res) => {
          if (res) {
                let arrParmas = []
          arrParmas = res.data.siteFormRecords
          // arrParmas.forEach((item) => {
            // item.ceeaReviewLink =  getLabel("CEEA_REVIEW_LINK",item.ceeaReviewLink)
            //  item.ceeaAuthResult =  getLabel("CEEA_AUTH_RESULT",item.ceeaAuthResult)
          // });
          console.log('arrParmas', arrParmas)
          this.allParams = arrParmas
          }
        })
        .catch((err) => {
          console.log(err)
        })
    },

    // 采购品类
    getPurchase (reviewFormId) {
      this.$http({
        url:
          '/api-sup/review/reviewForm/listOrgAndCategoryByReviewId',
        method: 'GET',
        params: { reviewFormId },
        loading: true
      })
        .then((res) => {
          if (res.data) {
            this.catData = res.data.orgCategorys
          }
        })
        .catch((err) => {
          console.log(err)
        })
    },
    getDetail (siteFormId) {
      return this.$http({
        url: '/api-sup/review/siteForm/getSiteFormDTO',
        method: 'GET',
        params: { siteFormId },
        loading: true
      })
    },
    // 获取创建人、部门
       getDeptName (data) {
      if (data) {
        return this.$http({
          url: '/api-rbac/rbac-anon/getUserInfoByAccount',
          method: 'GET',
          params: { account: data }
        })
      }
    },
    // 供应商评审
    async getFormDetail (siteFormId) {
          try {
          let { data } = await this.getDetail(siteFormId)
          console.log('provinceList', this.provinceList)
          let requirementHead = {}
          this.getPurchase(data.siteForm.reviewFormId)// this.$attrs.params.reviewFormId

          let getDept = data.siteForm.createdBy || ''
          let getDeptName = await this.getDeptName(getDept)

          console.log('[getDeptName]', getDeptName)
          requirementHead = data.siteForm
          if (getDeptName) {
                requirementHead.createdBy = getDeptName.data.nickName
            requirementHead.ceeaDeptName = getDeptName.data.department
          }
          this.requirementHead = requirementHead

          // console.log("this.assessmentTypeList", this.assessmentTypeList);
          // this.assessmentTypeList.forEach((item) => {
          //   if (item.value == requirementHead.assessmentType) {
          //     // console.log(item.label, 222);
          //     requirementHead.assessmentType = item.label;
          //   }
          // });
          // // console.log("siteProvince", requirementHead.assessmentType);

          //  let siteProvince= this.provinceList.find(v=> {
          //    return v.value ==requirementHead.siteProvince
          //  } )
          //  requirementHead.siteProvince = siteProvince.label
          //  this.requirementHead = requirementHead;
          //   this.getCityData(siteProvince.value)
          //  console.log("cityList",this.cityList)
          // this.getCityData(requirementHead.siteProvince);

          this.orgJournals = data.orgJournals
          let num = 1
          let arrBuName = []
          this.orgJournals.forEach((item) => {
            let obj = {}
            obj.index = num++
            obj.orgName = item.orgName
            this.$http({
              url: '/api-base/organization/organization/getBuByOrgId',
              method: 'GET',
              params: { organizationId: item.orgId },
              loading: true
            }).then((res) => {
              obj.buName = res.data.organizationName
            })

            arrBuName.push(obj)
          })
          this.tableData = arrBuName
          } catch (e) {
            console.log(e)
          }
    },
    current_change: function (currentPage) {
      console.log(currentPage)
      this.currentPage = currentPage
      // debugger
    },
      current_changeA: function (currentPageA) {
      console.log(currentPageA)
      this.currentPageA = currentPageA
      // debugger
    },
    current_changeB: function (currentPageB) {
      this.currentPageB = currentPageB
      // debugger
    },
    handleChange (val) {
      console.log(val)
    }
  }
}
</script>
<style scoped lang="scss">
.model {
  width: 100%;
  .bg-purple {
    text-align: left;
  }
  .bg-purple-light {
    text-align: right;
    min-width: 100px;
  }
  .bg {
    text-align: left;
    line-height: 32px;
  }

  .model_LineList {
    width: 100%;

    .LineList {
      border-bottom: 1px solid #e6ebf5;
      background-color: #f4f5f7;

      margin-top: -10px;
      margin-left: -10px;
      margin-right: -10px;

      span {
        margin-right: 4px;
      }
      .document {
        color: aqua;
        text-align: right;
        float: right;
      }
    }
    .card {
      width: 100%;
      // min-height: 200px;
    }
  }
  // .el-collapse-item__wrap{
  //   padding: 0px;
  // }
}
</style>
