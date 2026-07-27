<template>
  <div class="model">
    <el-collapse
      v-model="activeNames"
      class="modelA"
      @change="handleChange"
    >
      <el-collapse-item
        title="基本信息"
        name="1"
      >
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              业务实体：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaOrgName }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              供应商名称：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.vendorName }}
            </div>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              验收日期：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.acceptDate }}
            </div>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              验收单号：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.acceptNumber }}
            </div>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              资产类别：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaAsset }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              验收申请单号：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaApplictionCode }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              采购订单：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.orderNum }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              验收状态：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.acceptStatus }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            v-if="requirementHead.ceeaAssetType=='SERVICE'"
            :span="12"
          >
            <div class="bg-purple">
              服务区域：
            </div>
          </el-col>
          <el-col
            v-if="requirementHead.ceeaAssetType=='SERVICE'"
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaServiceZone }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            v-if="requirementHead.ceeaAssetType=='SERVICE'"
            :span="12"
          >
            <div class="bg-purple">
              服务日期从：
            </div>
          </el-col>
          <el-col
            v-if="requirementHead.ceeaAssetType=='SERVICE'"
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaServiceDateStart }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            v-if="requirementHead.ceeaAssetType=='SERVICE'"
            :span="12"
          >
            <div class="bg-purple">
              服务日期至：
            </div>
          </el-col>
          <el-col
            v-if="requirementHead.ceeaAssetType=='SERVICE'"
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaServiceDateEnd }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            v-if="requirementHead.ceeaAssetType!=='SERVICE'"
            :span="12"
          >
            <div class="bg-purple">
              资产名称：
            </div>
          </el-col>
          <el-col
            v-if="requirementHead.ceeaAssetType!=='SERVICE'"
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaAssetName }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            v-if="requirementHead.ceeaAssetType!=='SERVICE'"
            :span="12"
          >
            <div class="bg-purple">
              到货日期：
            </div>
          </el-col>
          <el-col
            v-if="requirementHead.ceeaAssetType!=='SERVICE'"
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaAcceptDate }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            v-if="requirementHead.ceeaAssetType!=='SERVICE'"
            :span="12"
          >
            <div class="bg-purple">
              规格型号：
            </div>
          </el-col>
          <el-col
            v-if="requirementHead.ceeaAssetType!=='SERVICE'"
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaSpecificationsModels }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            v-if="requirementHead.ceeaAssetType!=='SERVICE'"
            :span="12"
          >
            <div class="bg-purple">
              存放地点：
            </div>
          </el-col>
          <el-col
            v-if="requirementHead.ceeaAssetType!=='SERVICE'"
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaStoreCode }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            v-if="requirementHead.ceeaAssetType!=='SERVICE'"
            :span="12"
          >
            <div class="bg-purple">
              资产数量：
            </div>
          </el-col>
          <el-col
            v-if="requirementHead.ceeaAssetType!=='SERVICE'"
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaAssetNumber }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            v-if="requirementHead.ceeaAssetType!=='SERVICE'"
            :span="12"
          >
            <div class="bg-purple">
              立项编号：
            </div>
          </el-col>
          <el-col
            v-if="requirementHead.ceeaAssetType!=='SERVICE'"
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaProjectNumber }}
            </div>
          </el-col>
        </el-row>

        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              使用部门：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaUserDepartment }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              使用人：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaUserName }}
            </div>
          </el-col>
        </el-row>
        <!-- <el-row>
          <el-col :span="12"
            ><div class="bg-purple">随附技术文件：</div></el-col
          >
          <el-col :span="12"
            ><div class="bg-purple-light">
              <el-checkbox v-model="requirementHead.ceeaTechnicalDocuments" true-label="1" false-label="0"/>

            </div></el-col
          >
        </el-row> -->
        <el-row>
          <el-col
            v-if="requirementHead.ceeaAssetType!=='SERVICE'"
            :span="12"
          >
            <div class="bg-purple">
              附带工具设备：
            </div>
          </el-col>
          <el-col
            v-if="requirementHead.ceeaAssetType!=='SERVICE'"
            :span="12"
          >
            <div class="bg-purple-light">
              <el-checkbox
                v-model="requirementHead.ceeaToolEquipment"
                true-label="1"
                false-label="0"
              />
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            v-if="requirementHead.ceeaAssetType!=='SERVICE'"
            :span="12"
          >
            <div class="bg-purple">
              不动产大于500万：
            </div>
          </el-col>
          <el-col
            v-if="requirementHead.ceeaAssetType!=='SERVICE'"
            :span="12"
          >
            <div class="bg-purple-light">
              <el-checkbox
                v-model="requirementHead.ceeaAssetQualification"
                true-label="1"
                false-label="0"
              />
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              总数量：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.totalNum }}
            </div>
          </el-col>
        </el-row>

        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              总金额：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.totalAmount }}
            </div>
          </el-col>
        </el-row>

        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              其他杂费：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaTotalQuantity }}
            </div>
          </el-col>
        </el-row>

        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              总价值：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.totalValue }}
            </div>
          </el-col>
        </el-row>

        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              备注：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.remark }}
            </div>
          </el-col>
        </el-row>

        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              起草人意见：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaDraftsmanOpinion }}
            </div>
          </el-col>
        </el-row>
      </el-collapse-item>

      <el-collapse-item
        title="验收明细"
        name="2"
        class="model_LineList"
      >
        <template v-if="requirementLineList.length >10">
          <div class="LineList">
            <el-pagination
              :page-size="pagesize"
              layout="total,prev, next"
              :total="requirementLineList.length"
              @current-change="current_change"
            />
          </div>
          <div class="card">
            <el-table
              :data="
                requirementLineList.slice(
                  (currentPage - 1) * pagesize,
                  currentPage * pagesize
                )
              "
              style="width: 100%; margin-top: 6px;"
            >
              <el-table-column
                type="index"
                label="序号"
              />
              <el-table-column
                prop="orderNumber"
                label="采购订单号"
              />
              <el-table-column
                prop="orderLineNumber"
                label="订单行号"
              />
              <el-table-column
                prop="ceeaOrganizationName"
                label="库存组织"
              />
              <el-table-column
                prop="categoryName"
                label="物料小类"
              />
              <el-table-column
                prop="materialCode"
                label="物料编码"
              />
              <el-table-column
                prop="materialName"
                label="物料描述"
              />
              <el-table-column
                prop="ceeaUnit"
                label="单位"
              />
              <el-table-column
                prop="waitAcceptQuantity"
                label="可验收数量"
              />
              <el-table-column
                prop="acceptQuantity"
                label="本次验收数量"
              />
              <el-table-column
                prop="ceeaDamageQuantity"
                label="缺陷数量"
              />
              <el-table-column
                prop="ceeaWeight"
                label="重量"
              />
              <el-table-column
                prop="ceeaBulk"
                label="体积"
              />
              <el-table-column
                prop="ceeaProductionDate"
                label="出厂日期"
              />
              <el-table-column
                prop="ceeaProductionNum"
                label="出厂编号"
              />
              <el-table-column
                prop="orderNum"
                label="订单数量"
              />
              <el-table-column
                prop="ceeaTaxRate"
                label="税率"
              />
              <el-table-column
                prop="ceeaUnitTaxPrice"
                label="含税单价"
              />
            </el-table>
          </div>
        </template>
        <div
          v-else
          class="card"
        >
          <el-table
            :data="
              requirementLineList.slice(
                (currentPage - 1) * pagesize,
                currentPage * pagesize
              )
            "
            style="width: 100%; margin-top: 6px;"
          >
            <el-table-column
              type="index"
              label="序号"
            />
            <el-table-column
              prop="orderNumber"
              label="采购订单号"
            />
            <el-table-column
              prop="orderLineNumber"
              label="订单行号"
            />
            <el-table-column
              prop="ceeaOrganizationName"
              label="库存组织"
            />
            <el-table-column
              prop="categoryName"
              label="物料小类"
            />
            <el-table-column
              prop="materialCode"
              label="物料编码"
            />
            <el-table-column
              prop="materialName"
              label="物料描述"
            />
            <el-table-column
              prop="ceeaUnit"
              label="单位"
            />
            <el-table-column
              prop="waitAcceptQuantity"
              label="可验收数量"
            />
            <el-table-column
              prop="acceptQuantity"
              label="本次验收数量"
            />
            <el-table-column
              prop="ceeaDamageQuantity"
              label="缺陷数量"
            />
            <el-table-column
              prop="ceeaWeight"
              label="重量"
            />
            <el-table-column
              prop="ceeaBulk"
              label="体积"
            />
            <el-table-column
              prop="ceeaProductionDate"
              label="出厂日期"
            />
            <el-table-column
              prop="ceeaProductionNum"
              label="出厂编号"
            />
            <el-table-column
              prop="orderNum"
              label="订单数量"
            />
            <el-table-column
              prop="ceeaTaxRate"
              label="税率"
            />
            <el-table-column
              prop="ceeaUnitTaxPrice"
              label="含税单价"
            />
          </el-table>
        </div>
        <!-- <div class="LineList">
          <el-row>
            <el-col :span="12">
              <div>
                <span>共{{ this.requirementLineList.length }}条</span>
              <span :data-radius="requirementLineList.length" @click="pre($event,2)">上一条</span>
                <span :data-radius="requirementLineList.length"  @click="next($event,2)">下一条</span>
              </div></el-col
            >
          </el-row>
        </div>
        <div class="card">
          <el-carousel :autoplay="false" ref="carousel" heigth="20px">
            <el-carousel-item
              v-for="(item, index) in this.requirementLineList"
              :key="index"
              name="index"
            >
              <el-row>
                <el-col :span="12"><div class="bg-purple">采购订单号:</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.orderNumber }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">订单行号:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.orderLineNumber }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">库存组织:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.ceeaOrganizationName }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">物料小类:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.categoryName }}
                  </div></el-col
                >
              </el-row>
               <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">物料编码:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.materialCode }}
                  </div></el-col
                >
              </el-row>
               <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">物料描述:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.materialName }}
                  </div></el-col
                >
              </el-row>
               <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">单位:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.ceeaUnit }}
                  </div></el-col
                >
              </el-row>
               <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">可验收数量:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.waitAcceptQuantity }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">本次验收数量:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.acceptQuantity }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">缺陷数量:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.ceeaDamageQuantity }}
                  </div></el-col
                >
              </el-row>
               <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">重量:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.ceeaWeight }}
                  </div></el-col
                >
              </el-row>
               <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">体积:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.ceeaBulk }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">出厂日期:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.ceeaProductionDate }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">出厂编号:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.ceeaProductionNum }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">立项流水号:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.ceeaProjectApprovalNum }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">未税单价:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.ceeaUntaxedPrice }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">订单数量:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.orderNum }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">税率:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.ceeaTaxRate }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">含税单价:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.ceeaUnitTaxPrice }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">采购申请单号:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.ceeaRequirementHeadNum }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">申请行号:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.ceeaRowNum }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">合同编号:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.ceeaContractNo }}
                  </div></el-col
                >
              </el-row>
               <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">验收申请单号:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.ceeaAcceptApplicationNum }}
                  </div></el-col
                >
              </el-row>
            </el-carousel-item>
          </el-carousel>
        </div> -->
      </el-collapse-item>

      <el-collapse-item
        title="随附技术文件"
        name="3"
      >
        <template v-if="requirementAttaches.length > 10">
          <div class="LineList">
            <el-pagination
              :page-size="pagesizeA"
              layout="total,prev, next"
              :total="requirementAttaches.length"
              @current-change="current_changeA"
            />
          </div>
          <div class="card">
            <el-table
              :data="
                requirementAttaches.slice(
                  (currentPageA - 1) * pagesizeA,
                  currentPageA * pagesizeA
                )
              "
              style="width: 100%; margin-top: 6px;"
            >
              <el-table-column
                type="index"
                label="序号"
              />
              <el-table-column
                prop="orgName"
                label="附件名称"
              >
                <template slot-scope="scope">
                  <c-download-link
                    :id="scope.row.fileuploadId"
                    :name="scope.row.fileFullname"
                    ellipsis
                    class="download-link-item"
                  />
                </template>
              </el-table-column>
              <el-table-column
                prop="createdBy"
                label="上传人"
              />
              <el-table-column
                prop="creationDate"
                label="上传时间"
              />
              <el-table-column
                prop="targetNum"
                label="备注"
              />
              <el-table-column
                prop="comments"
                label="备注"
              />
            </el-table>
          </div>
        </template>
        <div
          v-else
          class="card"
        >
          <el-table
            :data="
              requirementAttaches.slice(
                (currentPageA - 1) * pagesizeA,
                currentPageA * pagesizeA
              )
            "
            style="width: 100%; margin-top: 6px;"
          >
            <el-table-column
              type="index"
              label="序号"
            />
            <el-table-column
              prop="orgName"
              label="附件名称"
              width="100"
            >
              <template slot-scope="scope">
                <c-download-link
                  :id="scope.row.fileuploadId"
                  :name="scope.row.fileFullname"
                  ellipsis
                  class="download-link-item"
                />
              </template>
            </el-table-column>
            <el-table-column
              prop="createdBy"
              label="上传人"
            />
            <el-table-column
              prop="creationDate"
              label="上传时间"
            />
            <el-table-column
              prop="targetNum"
              label="备注"
            />
            <el-table-column
              prop="targetDesc"
              label="物料描述"
              width="200"
            />
            <el-table-column
              prop="quantity"
              label="预计数量"
            />
            <el-table-column
              prop="comments"
              label="备注"
            />
          </el-table>
        </div>
        <!-- <div class="LineList">
          <el-row>
            <el-col :span="12">
              <div>
                <span>共{{ this.requirementAttaches.length }}条</span>
               <span :data-radius="requirementAttaches.length" @click="pre($event,3)">上一条</span>
                <span :data-radius="requirementAttaches.length"  @click="next($event,3)">下一条</span>
              </div></el-col
            >
          </el-row>
        </div>
        <div class="card">
          <el-carousel :autoplay="false" ref="carousel3" heigth="20px">
            <el-carousel-item
              v-for="(item, index) in this.requirementAttaches"
              :key="index"
              name="index"
            >
              <el-row>
                <el-col :span="12"><div class="bg-purple">附件名称:</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">

              <c-download-link
                :id="item.fileuploadId"
                :name="item.fileFullname"
                ellipsis
                class="download-link-item"
              />

                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">上传人:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.createdBy }}
                  </div></el-col
                >
              </el-row>
               <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">上传时间:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.creationDate }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">备注:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.comments }}
                  </div></el-col
                >
              </el-row>
            </el-carousel-item>
          </el-carousel>
        </div> -->
      </el-collapse-item>
      <el-collapse-item
        title="附件信息"
        name="4"
      >
        <template v-if="assetFile.length > 10">
          <div class="LineList">
            <el-pagination
              :page-size="pagesizeB"
              layout="total,prev, next"
              :total="assetFile.length"
              @current-change="current_changeA"
            />
          </div>
          <div class="card">
            <el-table
              :data="
                assetFile.slice(
                  (currentPageB - 1) * pagesizeB,
                  currentPageB * pagesizeB
                )
              "
              style="width: 100%; margin-top: 6px;"
            >
              <el-table-column
                type="index"
                label="序号"
              />
              <el-table-column
                prop="orgName"
                label="附件名称"
              >
                <template slot-scope="scope">
                  <c-download-link
                    :id="scope.row.fileuploadId"
                    :name="scope.row.fileFullname"
                    ellipsis
                    class="download-link-item"
                  />
                </template>
              </el-table-column>
              <el-table-column
                prop="createdBy"
                label="上传人"
              />
              <el-table-column
                prop="creationDate"
                label="上传时间"
              />
              <el-table-column
                prop="comments"
                label="备注"
              />
            </el-table>
          </div>
        </template>
        <div
          v-else
          class="card"
        >
          <el-table
            :data="
              assetFile.slice(
                (currentPageB - 1) * pagesizeB,
                currentPageB * pagesizeB
              )
            "
            style="width: 100%; margin-top: 6px;"
          >
            <el-table-column
              type="index"
              label="序号"
            />
            <el-table-column
              prop="orgName"
              label="附件名称"
            >
              <template slot-scope="scope">
                <c-download-link
                  :id="scope.row.fileuploadId"
                  :name="scope.row.fileFullname"
                  ellipsis
                  class="download-link-item"
                />
              </template>
            </el-table-column>
            <el-table-column
              prop="createdBy"
              label="上传人"
            />
            <el-table-column
              prop="creationDate"
              label="上传时间"
            />
            <el-table-column
              prop="comments"
              label="备注"
            />
          </el-table>
        </div>

        <!-- <div class="LineList">
          <el-row>
            <el-col :span="12">
              <div>
                <span>共{{ this.assetFile.length }}条</span>
                <span :data-radius="assetFile.length" @click="pre($event,4)">上一条</span>
                <span :data-radius="assetFile.length"  @click="next($event,4)">下一条</span>
              </div></el-col
            >
          </el-row>
        </div>
        <div class="card">
          <el-carousel :autoplay="false" ref="carousel4" heigth="20px">
            <el-carousel-item
              v-for="(item, index) in this.assetFile"
              :key="index"
              name="index"
            >
              <el-row>
                <el-col :span="12"><div class="bg-purple">附件名称:</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">

              <c-download-link
                :id="item.fileuploadId"
                :name="item.fileFullname"
                ellipsis
                class="download-link-item"
              />

                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">上传人:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.createdBy }}
                  </div></el-col
                >
              </el-row>
               <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">上传时间:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.creationDate }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">备注:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.comments }}
                  </div></el-col
                >
              </el-row>
            </el-carousel-item>
          </el-carousel>
        </div> -->
      </el-collapse-item>
    </el-collapse>
  </div>
</template>
<script>
import CUploadFile from '@/library/components/c-upload-file'
import CDownloadLink from 'lib@/components/c-download-link'

import { parseTime, adaptDictData } from '@/utils'

import {
  getDictItemList
} from '@/api/common'
export default {
  components: {
    CUploadFile,
    CDownloadLink

  },
  data () {
    return {
       currentPage: 1, // 采购品类初始页
       pagesize: 10, //    每页的数据
       currentPageA: 1, // 采购品类初始页
       pagesizeA: 10, //    每页的数据
        currentPageB: 1, // 采购品类初始页
       pagesizeB: 10, //    每页的数据
      assetFile: [],
      fileuploads: [],
      displayCatData: [], // 扩展品类
      dataAtt: [],
      fileRefresh: false,
      companyId: null,
      filesChangeData: [], // 附件变更数据

      requirementAccess: [], // 账户信息
      approvalFileList: [], // 审批附件信息
      orderFileList: [], // 订单附件
      requirementAffix: [], // 附件
      currencyList: [], // 币种
      dialog: false,
      dialogVisible: false,
      // 文件上传配置信息
      fileInfo: {
        uploadType: 'FASTDFS', // 固定参数
        sourceType: 'WEB_APP', // 固定参数
        fileModular: 'sup', // 文件所属模块 -》基础模块
        fileFunction: 'vendorBiddingManagement', // 文件所属功能
        fileType: 'images' // 文件所属类型
      },
      activeNames: ['1', '2', '3', '4'],
      requirementHead: {

      },
      acceptDetails: [],
      requirementAttaches: [],
      requirementLineList: [],
      currencyListA: [],
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
    currencyList () {
      this.$nextTick(() => {
        // 此时就可以获取到在created赋值后的assessmentTypeList了
        this.getFormDetail(this.$attrs.params.row.acceptOrderId)
      })
    }
  },
  created () {
    this.fatchDictData()

    console.log('this.$attrs.params', this.$attrs.params)
    // this.getFormDetail(this.$attrs.params.row.acceptOrderId);
    // 检查浏览器是否支持全屏
    // this.canFullScreen =
    //   document.fullscreenEnabled ||
    //   document.webkitFullscreenEnabled ||
    //   document.mozFullScreenEnabled ||
    //   document.msFullscreenEnabled;
    // if (document.webkitFullscreenEnabled) {
    //   this.browserKernel = "webkit";
    // } else if (document.mozFullScreenEnabled) {
    //   this.browserKernel = "gecko";
    // } else if (document.msFullscreenEnabled) {
    //   this.browserKernel = "trident";
    // } else if (document.fullscreenEnabled) {
    //   this.browserKernel = "others";
    // }
    // if (this.canFullScreen) {
    //   this.addFullScreenListener();
    // }
  },

  mounted () {

  },
  methods: {
    fatchDictData () {
      // 批量查询字典
      let dictParamsArr = [

         { dictCode: 'AQL_ASSET_TYPE' },
         { dictCode: 'ACCEPTANCE_STATUS' }
        // AQL_ASSET_TYPE 资产类别 ceeaAssetType
        // ACCEPTANCE_STATUS 验收状态 acceptStatus
      ]
      getDictItemList(dictParamsArr).then((res) => {
        const [AQL_ASSET_TYPE, ACCEPTANCE_STATUS] = res.data
        this.currencyList = adaptDictData(
          AQL_ASSET_TYPE.AQL_ASSET_TYPE,

          'dict'
        )
        this.currencyListA = adaptDictData(
          ACCEPTANCE_STATUS.ACCEPTANCE_STATUS,
           'dict'
        )
      })
    },
    outerButtonClick (index) {
      this.bankRowIndex = index
    },
    outerHandleUploadSuccess (file) {
      const { id, name, createdBy, creationDate } = file
      this.requirementAttaches[this.bankRowIndex].fileuploadId = id.toString()
      this.requirementAttaches[this.bankRowIndex].attachName = name
      this.requirementAttaches[this.bankRowIndex].createdBy = createdBy
      this.requirementAttaches[this.bankRowIndex].creationDate = creationDate
    },
     current_change: function (currentPage) {
      console.log(currentPage)
      this.currentPage = currentPage
      // debugger
    },
    current_changeA: function (currentPage) {
      console.log(currentPage)
      this.currentPageA = currentPage
      // debugger
    },
     current_changeB: function (currentPage) {
      console.log(currentPage)
      this.currentPageB = currentPage
      // debugger
    },

    // 移除
    outerHandleRemove (fileuploadId) {},
    handleScriptProgress (percent) {},

    getFormDetail (acceptOrderId) {
      this.$http({
        url: '/api-cm/accept/acceptOrder/getAcceptDTO',
        method: 'GET',
        params: { acceptOrderId },
        loading: true
      })
        .then((res) => {
          if (res) {
            console.log('All', res)
            let requirementHead = res.data.acceptOrder
            requirementHead.totalNum = 0
            requirementHead.totalAmount = 0
            this.acceptDetails = res.data.acceptDetails
              this.acceptDetails.map(v => {
              requirementHead.totalNum += v.acceptQuantity || 0
              requirementHead.totalAmount += v.acceptQuantity * v.ceeaUnitTaxPrice || 0
            })

            this.currencyList.forEach((item) => {
              if (item.value == requirementHead.ceeaAssetType) {
                requirementHead.ceeaAsset = item.label
              }
            })

               this.currencyListA.forEach((item) => {
              if (item.value == requirementHead.acceptStatus) {
                requirementHead.acceptStatus = item.label
              }
            })
             this.requirementHead = requirementHead

            this.requirementLineList = res.data.acceptDetails
            this.requirementAttaches = res.data.techFile
            this.assetFile = res.data.assetFile
            // let displayCatDataArr = this.requirementLineList.map(
            //   (v) => v.categoryName
            // );
            // displayCatDataArr = Array.from(new Set(displayCatDataArr));
            // this.displayCatData = [];
            // for (let i = 0; i < displayCatDataArr.length; i++) {
            //   this.displayCatData.push({
            //     categoryName: displayCatDataArr[i],
            //   });
            // }

            // this.fileuploads = res.data.fileuploads;
          }
        })
        .catch((err) => {
          console.log(err)
        })
    },
      pre (event, number) {
      // console.log(" this.requirementAttaches.length",event.target.dataset.radius)
      this.num += 1
      if (number == 2) {
          if (this.num > event.target.dataset.radius - 1) {
        this.num = 0
            this.$refs.carousel2.setActiveItem(this.num)
        } else {
        this.$refs.carousel2.setActiveItem(this.num)
        }
      } else if (number == 3) {
            if (this.num > event.target.dataset.radius - 1) {
        this.num = 0
            this.$refs.carousel3.setActiveItem(this.num)
        } else {
        this.$refs.carousel3.setActiveItem(this.num)
        }
      } else if (number == 4) {
            if (this.num > event.target.dataset.radius - 1) {
        this.num = 0
            this.$refs.carousel4.setActiveItem(this.num)
        } else {
        this.$refs.carousel4.setActiveItem(this.num)
        }
      }
    },
    next (event, number) {
      // console.log(" this.requirementAttaches.length1",event.target.dataset.radius)
      this.num -= 1
      if (number == 2) {
          if (this.num < 0) {
          this.num = event.target.dataset.radius - 1
          this.$refs.carousel2.setActiveItem(this.num)
        } else {
          this.$refs.carousel2.setActiveItem(this.num)
        }
      } else if (number == 3) {
            if (this.num < 0) {
        this.num = event.target.dataset.radius - 1
            this.$refs.carousel3.setActiveItem(this.num)
        } else {
        this.$refs.carousel3.setActiveItem(this.num)
        }
      } else if (number == 4) {
            if (this.num < 0) {
        this.num = event.target.dataset.radius - 1
            this.$refs.carousel4.setActiveItem(this.num)
        } else {
        this.$refs.carousel4.setActiveItem(this.num)
        }
      }
    },
    // 设置全屏
    // 全屏设置
    fullTable () {
      console.log('1111')
      if (this.canFullScreen) {
        if (this.isFullScreen) {
          // 关闭全屏
          this.exitFullScreen()
          this.isFullScreen = false
        } else {
          // 打开全屏
          console.log('2222')
          this.Full(document.getElementsByClassName('card')[0])
          this.isFullScreen = true
        }
      } else {
        this.$message.warning({
          content: '当前浏览器暂不支持全屏模式，请切换浏览器后重新尝试！',
          duration: 3
        })
      }
    },
    Full (element) {
      // 判断各种浏览器，找到正确的方法
      console.log('3333')
      const requestMethod =
        element.requestFullScreen || // W3C
        element.webkitRequestFullScreen || // Chrome, safari
        element.mozRequestFullScreen || // FireFox
        element.msRequestFullscreen // IE11
      if (requestMethod) {
        console.log('4444')
        requestMethod.call(element)
      }
    },
    hideDialog () {
      this.dialog = true
    },
    exitFullScreen () {
      var exitMethod =
        document.exitFullscreen || // W3C
        document.mozCancelFullScreen || // FireFox
        document.webkitExitFullscreen || // Chrome等
        document.msExitFullscreen // IE11
      if (exitMethod) {
        exitMethod.call(document)
      }
    },
    addFullScreenListener () {
      const self = this
      document.onkeydown = function (e) {
        if (e && e.keyCode === 122) {
          // 捕捉F11键盘动作
          e.preventDefault() // 阻止F11默认动作
          self.toggleFullScreen()
        }
      }
      // 监听不同浏览器的全屏事件，并件执行相应的代码
      switch (self.browserKernel) {
        case 'webkit':
          document.onwebkitfullscreenchange = function () {
            if (document.webkitIsFullScreen) {
              self.isFullScreen = true
            } else {
              self.isFullScreen = false
            }
          }
          break
        case 'gecko':
          document.onmozfullscreenchange = function () {
            if (document.mozFullScreen) {
              self.isFullScreen = true
            } else {
              self.isFullScreen = false
            }
          }
          break
        case 'trident':
          document.onmsfullscreenchange = function () {
            if (document.msFullscreenElement) {
              self.isFullScreen = true
            } else {
              self.isFullScreen = false
            }
          }
          break
        case 'others':
          document.onfullscreenchange = function () {
            if (document.fullscreen) {
              self.isFullScreen = true
            } else {
              self.isFullScreen = false
            }
          }
          break
        default:
          break
      }
    },

    getFullCreeen () {
      this.n++
      this.n % 2 == 0
        ? this.outFullCreeen(document)
        : this.inFullCreeen(document.getElementsByClassName('card')[0])
    },
    inFullCreeen (element) {
      console.log(
        'ocument.getElementsByClassName(\'.card\')[0]',
        document.getElementsByClassName('card')[0]
      )
      let el = document.getElementsByClassName('card')[0]
      let rfs =
        el.requestFullScreen ||
        el.webkitRequestFullScreen ||
        el.mozRequestFullScreen ||
        el.msRequestFullScreen
      if (typeof rfs !== 'undefined' && rfs) {
        rfs.call(el)
      } else if (typeof window.ActiveXObject !== 'undefined') {
        let wscript = new ActiveXObject('WScript.Shell')
        if (wscript != null) {
          wscript.SendKeys('{F11}')
        }
      }
    },
    outFullCreeen (element) {
      let el = element
      let cfs =
        el.cancelFullScreen ||
        el.webkitCancelFullScreen ||
        el.mozCancelFullScreen ||
        el.exitFullScreen
      if (typeof cfs !== 'undefined' && cfs) {
        cfs.call(el)
      } else if (typeof window.ActiveXObject !== 'undefined') {
        let wscript = new ActiveXObject('WScript.Shell')
        if (wscript != null) {
          wscript.SendKeys('{F11}')
        }
      }
    },

    handleChange (val) {
      console.log(val)
    }
  }
}
</script>
<style scoped lang="scss">
.banner,
.banner .block,
.banner >>> .el-carousel .el-carousel--horizontal,
.block >>> .el-carousel__container {
  height: 100% !important;
  overflow: hidden;
}

.popContainer {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  img {
    width: 100%;
    height: 100%;
    object-fit: contain;
  }
}

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
    }
  }
  // .el-collapse-item__wrap{
  //   padding: 0px;
  // }
}
</style>
