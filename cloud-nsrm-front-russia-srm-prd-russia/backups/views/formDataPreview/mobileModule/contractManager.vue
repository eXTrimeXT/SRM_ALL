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
              合同名称：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.contractName }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              合同类型：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.contractClass }}
            </div>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              模板名称：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.modelHeadId }}
            </div>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              合同编号：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.contractCode }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              原合同编号：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.contractCodeFirst }}
            </div>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              是否标准合同：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.enable=="Y"? "是":"否" }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              合同有效期从：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.effectiveDateFrom }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              合同有效期至：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.effectiveDateTo }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="6"
          >
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
            :span="12"
          >
            <div class="bg-purple">
              是否框架协议
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.isFrameworkAgreement=="Y"? "是":"否" }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              合同级别：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.contractLevel }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              送审业务实体：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.buName }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              人民币：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.currencyName }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              是否总部：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.isHeadquarters=="Y"? "是":"否" }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              操作类型：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.contractType }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              状态：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.contractStatus }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              合同总金额（含税）
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.includeTaxAmount }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              创建人：
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
              {{ this.requirementHead.contractRemark }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              起草人意见
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.drafterOpinion }}
            </div>
          </el-col>
        </el-row>
      </el-collapse-item>

      <el-collapse-item
        title="物料明细"
        name="2"
        class="model_LineList"
      >
        <template v-if="requirementLineList.length > 10">
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
                prop="sourceNumber"
                label="来源单号:"
              />
              <el-table-column
                prop="ceeaOuNumber"
                label="OU组编号:"
              />
              <el-table-column
                prop="ceeaOuName"
                label="OU组名称:"
              />
              <el-table-column
                prop="buName"
                label="业务实体:"
              />
              <el-table-column
                prop="invName"
                label="库存组织:"
              />
              <el-table-column
                prop="materialCode"
                label="物料编码:"
              />
              <el-table-column
                prop="materialName"
                label="物料名称:"
                width="200"
                align="center"
              />
              <el-table-column
                prop="categoryName"
                label="采购分类:"
              />
              <el-table-column
                prop="taxedPrice"
                label="含税单价:"
              />
              <el-table-column
                prop="contractQuantity"
                label="数量:"
              />
              <el-table-column
                prop="amount"
                label="含税金额:"
              />
              <el-table-column
                prop="unitName"
                label="单价:"
              />
            </el-table>
          </div>
        </template>
        <div
          v-else
          class="card"
        >
          <el-table :data="requirementLineList">
            <el-table-column
              type="index"
              label="序号"
            />
            <el-table-column
              prop="sourceNumber"
              label="来源单号:"
            />
            <el-table-column
              prop="ceeaOuNumber"
              label="OU组编号:"
            />
            <el-table-column
              prop="ceeaOuName"
              label="OU组名称:"
            />
            <el-table-column
              prop="buName"
              label="业务实体:"
            />
            <el-table-column
              prop="invName"
              label="库存组织:"
            />
            <el-table-column
              prop="materialCode"
              label="物料编码:"
            />
            <el-table-column
              prop="materialName"
              label="物料名称:"
              width="200"
              align="center"
            />
            <el-table-column
              prop="categoryName"
              label="采购分类:"
            />
            <el-table-column
              prop="taxedPrice"
              label="含税单价:"
            />
            <el-table-column
              prop="contractQuantity"
              label="数量:"
            />
            <el-table-column
              prop="amount"
              label="含税金额:"
            />
            <el-table-column
              prop="unitName"
              label="单价:"
            />
          </el-table>
          <!-- <el-carousel arrow="never" :autoplay="false" ref="carousel2" heigth="20px">
            <el-carousel-item
              v-for="(item, index) in this.requirementLineList"
              :key="index"
              name="index"
            >
              <el-row>
                <el-col :span="12"><div class="bg-purple">来源单号:</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.sourceNumber }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">OU组编号:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.ceeaOuNumber }}
                  </div></el-col
                >
              </el-row>
               <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">OU组名称:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.ceeaOuName }}
                  </div></el-col
                >
              </el-row>
               <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">业务实体:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.buName }}
                  </div></el-col
                >
              </el-row>
               <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">库存组织:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.invName }}
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
                  ><div class="bg-purple">物料名称:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.materialName }}
                  </div></el-col
                >
              </el-row>
               <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">采购分类:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.categoryName }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">含税单价:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.taxedPrice }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">数量:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.contractQuantity }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">含税金额:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.amount }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">单价:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.unitName }}
                  </div></el-col
                >
              </el-row>
            </el-carousel-item>
          </el-carousel> -->
        </div>
      </el-collapse-item>
      <el-collapse-item
        title="付款计划"
        name="3"
        class="model_LineList"
      >
        <template v-if="pay_plan.length > 10">
          <div class="LineList">
            <el-pagination
              :page-size="pagesizeA"
              layout="total,prev, next"
              :total="pay_plan.length"
              @current-change="current_changeA"
            />
          </div>
          <div class="card">
            <el-table
              :data="
                pay_plan.slice(
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
                prop="paymentPeriod"
                label="付款期数:"
              />
              <el-table-column
                prop="paymentStage"
                label="付款阶段:"
              />
              <el-table-column
                prop="payExplain"
                label="付款条件:"
              />
              <el-table-column
                prop="dateNum"
                label="天数:"
              />
              <el-table-column
                prop="paymentRatio"
                label="付款比例:"
              />
              <el-table-column
                prop="stagePaymentAmount"
                label="阶段付款金额:"
              />
              <el-table-column
                prop="payMethod"
                label="计划付款信息:"
              />
              <el-table-column
                prop="payMethod"
                label="付款方式:"
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
              pay_plan.slice(
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
              prop="paymentPeriod"
              label="付款期数:"
            />
            <el-table-column
              prop="paymentStage"
              label="付款阶段:"
            />
            <el-table-column
              prop="payExplain"
              label="付款条件:"
            />
            <el-table-column
              prop="dateNum"
              label="天数:"
            />
            <el-table-column
              prop="paymentRatio"
              label="付款比例:"
            />
            <el-table-column
              prop="stagePaymentAmount"
              label="阶段付款金额:"
            />
            <el-table-column
              prop="payMethod"
              label="计划付款信息:"
            />
            <el-table-column
              prop="payMethod"
              label="付款方式:"
            />
          </el-table>
        </div>
        <!-- <div class="LineList">
          <el-row>
            <el-col :span="12">
              <div>
                <span>共{{ this.pay_plan.length }}条</span>
                <span :data-radius="pay_plan.length" @click="pre($event,3)">上一条</span>
                <span :data-radius="pay_plan.length"  @click="next($event,3)">下一条</span>
              </div></el-col
            >
          </el-row>
        </div> -->
        <!-- <div class="card">
          <el-carousel arrow="never" :autoplay="false" ref="carousel3" heigth="200px">
            <el-carousel-item
              v-for="(item, index) in this.pay_plan"
              :key="index"
              name="index"
            >
              <el-row>
                <el-col :span="12"><div class="bg-purple">付款期数:</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.paymentPeriod }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">付款阶段:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.paymentStage }}
                  </div></el-col
                >
             </el-row>
            <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">付款条件:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.payExplain }}
                  </div></el-col
                >
             </el-row>
             <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">天数:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.dateNum }}
                  </div></el-col
                >
             </el-row>
             <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">付款比例:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.paymentRatio }}
                  </div></el-col
                >
             </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">阶段付款金额:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.stagePaymentAmount }}
                  </div></el-col
                >
             </el-row>
             <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">计划付款信息:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.plannedPaymentDate }}
                  </div></el-col
                >
             </el-row>
             <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">付款方式:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.payMethod }}
                  </div></el-col
                >
             </el-row>
            </el-carousel-item>
          </el-carousel>
        </div> -->
      </el-collapse-item>
      <el-collapse-item
        title="合作伙伴"
        name="4"
        class="model_LineList"
      >
        <template v-if="requirementAttaches.length > 10">
          <div class="LineList">
            <el-pagination
              :page-size="pagesizeB"
              layout="total,prev, next"
              :total="requirementAttaches.length"
              @current-change="current_changeB"
            />
          </div>
          <div class="card">
            <el-table
              :data="
                requirementAttaches.slice(
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
                prop="partnerType"
                label="伙伴类型:"
              />
              <el-table-column
                prop="partnerName"
                label="伙伴名称:"
              />
              <el-table-column
                prop="contactName"
                label="代表人:"
              />
              <el-table-column
                prop="phone"
                label="联系电话:"
              />
              <el-table-column
                prop="address"
                label="地址信息:"
              />
              <el-table-column
                prop="bankName"
                label="开户银行:"
              />
              <el-table-column
                prop="bankAccount"
                label="开户账号:"
              />
              <el-table-column
                prop="postCode"
                label="邮编:"
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
              prop="partnerType"
              label="伙伴类型:"
            />
            <el-table-column
              prop="partnerName"
              label="伙伴名称:"
            />
            <el-table-column
              prop="contactName"
              label="代表人:"
            />
            <el-table-column
              prop="phone"
              label="联系电话:"
            />
            <el-table-column
              prop="address"
              label="地址信息:"
            />
            <el-table-column
              prop="bankName"
              label="开户银行:"
            />
            <el-table-column
              prop="bankAccount"
              label="开户账号:"
            />
            <el-table-column
              prop="postCode"
              label="邮编:"
            />
          </el-table>
        </div>
        <!-- <div class="LineList">
          <el-row>
            <el-col :span="12">
              <div>
                <span>共{{ this.requirementAttaches.length }}条</span>
                <span :data-radius="requirementAttaches.length" @click="pre($event,4)">上一条</span>
                <span :data-radius="requirementAttaches.length"  @click="next($event,4)">下一条</span>
              </div></el-col
            >
          </el-row>
        </div>
        <div class="card">
          <el-carousel :autoplay="false" ref="carousel4" heigth="20px">
            <el-carousel-item
              v-for="(item, index) in this.requirementAttaches"
              :key="index"
              name="index"
            >
              <el-row>
                <el-col :span="12"><div class="bg-purple">伙伴类型:</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.partnerType }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">伙伴名称:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.partnerName }}
                  </div></el-col
                >
             </el-row>
            <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">代表人:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.contactName }}
                  </div></el-col
                >
             </el-row>
             <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">联系电话:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.phone }}
                  </div></el-col
                >
             </el-row>
             <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">地址信息:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.address }}
                  </div></el-col
                >
             </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">开户银行:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.bankName }}
                  </div></el-col
                >
             </el-row>
             <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">开户账号:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.bankAccount }}
                  </div></el-col
                >
             </el-row>
             <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">邮编:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.postCode }}
                  </div></el-col
                >
             </el-row>
            </el-carousel-item>
          </el-carousel>
        </div> -->
      </el-collapse-item>

      <el-collapse-item
        title="附件"
        name="5"
      >
        <el-table
          :data="fileuploads"
          style="width: 100%"
          border
          max-height="250px"
        >
          <el-table-column
            align="center"
            type="index"
            label="序号"
            width="50"
          />
          <el-table-column
            align="center"
            prop="fileFullname"
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
        </el-table>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>
<script>
import CUploadFile from '@/library/components/c-upload-file'
import CDownloadLink from 'lib@/components/c-download-link'
import http from '@/utils/http'
import { adaptDictData } from '@/utils'

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
      currentPageB: 1, // 采购品类初始页
       pagesizeB: 10, //    每页的数据
      currentPageA: 1, // 采购品类初始页
       pagesizeA: 10, //    每页的数据
       currentPage: 1, // 采购品类初始页
       pagesize: 10, //    每页的数据
      payExplainData: [], // 存储payExplainHttp返回来的数据
      pay_plan: [],
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
      currencyList: [],
      currencyListA: [],
      currencyListB: [],
      currencyListC: [],
      currencyListSTAGE: [], // 到货款
      currencyListPERIOD: [], // 付款账期
      currencyListMODE: [], // 支付方式
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
      activeNames: ['1', '2', '3', '4', '5', '6'],
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
    currencyList () {
      this.$nextTick(() => {
        // 此时就可以获取到在created赋值后的assessmentTypeList了

        this.getFormDetail(this.$attrs.params.rowId)
      })
    }
  },
  created () {
    this.fatchDictData()
    http({
        url: '/api-cm/template/payType/getActivationPaymentTerms',
        method: 'GET'
      }).then(res => {
        this.payExplainData = res.data.map(i => ({
          id: i.payTypeId,
          label: i.payExplain,
          value: i.payTypeId
        }))
      })
    // console.log("this.$attrs.params", this.$attrs.params);
    //  this.getFormDetail(this.$attrs.params.rowId );
    // this.getFormDetail(this.$attrs.params.row.contractHeadId);
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
  if (this.$attrs.params) {
        const { data } = this.$api.cm.contract.getInfoById(
        this.$attrs.params.rowId,
        this.$attrs.params.sourceId
      )
  }
  },
  methods: {
    fatchDictData () {
      // 批量查询字典
      let dictParamsArr = [
        { dictCode: 'ELEM_CONTRACT_TYPE' },
        { dictCode: 'CONTARCT_LEVEL' },
        { dictCode: 'CONTRACT_TYPE' },
        { dictCode: 'CONTRACT_STATUS' },
        { dictCode: 'PAYMENT_STAGE' },
        { dictCode: 'PAYMENT_PERIOD' },
        { dictCode: 'PAYMENT_MODE' }

        // PAYMENT_STAGE 到货款
         // PAYMENT_PERIOD 付款账期
        // PAYMENT_MODE 支付方式
        // payExplain 付款条件

      ]

      getDictItemList(dictParamsArr).then((res) => {
        const [ELEM_CONTRACT_TYPE, CONTARCT_LEVEL, CONTRACT_TYPE, CONTRACT_STATUS, PAYMENT_STAGE, PAYMENT_PERIOD, PAYMENT_MODE] = res.data
        this.currencyList = adaptDictData(
          ELEM_CONTRACT_TYPE.ELEM_CONTRACT_TYPE, // 合同类型
          'dict'
        )
        this.currencyListA = adaptDictData(
          CONTARCT_LEVEL.CONTARCT_LEVEL, // 合同级别
          'dict'
        )
        this.currencyListB = adaptDictData(
          CONTRACT_TYPE.CONTRACT_TYPE, // 操作类型
          'dict'
        )
        this.currencyListC = adaptDictData(
          CONTRACT_STATUS.CONTRACT_STATUS, // 状态
          'dict'
        )
        this.currencyListSTAGE = adaptDictData(
          PAYMENT_STAGE.PAYMENT_STAGE, // 到货款
          'dict'
        )
        this.currencyListPERIOD = adaptDictData(
          PAYMENT_PERIOD.PAYMENT_PERIOD, // 付款账期
          'dict'
        )
        this.currencyListMODE = adaptDictData(
          PAYMENT_MODE.PAYMENT_MODE, // 支付方式
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

    async getFormDetail (contractHeadId, sourceId) {
      this.$http({
        url: '/api-cm/contract/contractHead/getContractDTOSecond',

        method: 'GET',
        params: { contractHeadId, sourceId },
        loading: true
      })
        .then((res) => {
          if (res) {
            console.log('res', res)
            let obj = res.data.contractHead
            this.$api.cm.modelListByType(obj.contractClass)
             .then((res) => {
                console.log('contractClass', res)
                res.data.forEach(item => {
                  if (item.modelHeadId == obj.modelHeadId) {
                    obj.modelHeadId = item.modelName // 模板名称
                  }
                })
              })
            if (this.$attrs.params.contractType == 'MIAN_CONTRACT_ALTER' || this.$attrs.params.contractType == 'SUPPLEMENTAL_AGREEMENT') {
              obj.contractCodeFirst = obj.contractCode
              obj.contractCode = ''
              obj.contractType = this.$attrs.params.contractType // 变更、补充的操作类型
              obj.contractStatus = 'DRAFT'// 变更、补充的状态
            }

            this.currencyList.forEach((item) => {
              if (item.value == obj.contractClass) {
                obj.contractClass = item.label // 合同类型
              }
            })
             this.currencyListA.forEach(item => {
                if (item.value == obj.contractLevel) {
                 obj.contractLevel = item.label// 合同级别
               }
             })

              this.currencyListB.forEach(item => {
                if (item.value == obj.contractType) {
                 obj.contractType = item.label// 操作类型
               }
             })
              this.currencyListC.forEach(item => {
                if (item.value == obj.contractStatus) {
                 obj.contractStatus = item.label// 状态
               }
             })

             obj.includeTaxAmount = obj.includeTaxAmount.toFixed(2)
            this.requirementHead = obj
            this.requirementLineList = res.data.contractMaterials
            this.requirementAttaches = res.data.contractPartners

            let obj_pay = res.data.payPlans
            obj_pay.forEach(elm => {
            this.currencyListSTAGE.forEach((item) => {
              if (item.value == elm.paymentStage) {
                elm.paymentStage = item.label // 到货款
              }
            })
             this.currencyListPERIOD.forEach((item) => {
              if (item.value == elm.dateNum) {
                elm.dateNum = item.label // 付款账期
              }
            })
            this.currencyListMODE.forEach((item) => {
              if (item.value == elm.payMethod) {
                elm.payMethod = item.label // 付款方式
              }
            })
            this.payExplainData.forEach(item => {
              if (item.value == elm.payExplain) {
                elm.payExplain = item.label// 付款条件
              }
            })
            })

            this.pay_plan = obj_pay
            this.fileuploads = res.data.annexes
          }
        })
        .catch((err) => {
          console.log(err)
        })
    },
    next (event, number) {
      console.log(' this.requirementAttaches.length', event.target.dataset.radius, 'number', number)
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
    pre (event, number) {
      console.log(' this.requirementAttaches.length', event.target.dataset.radius, 'number', number)
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
      // let a =val.find(v=>v =="3")
      // if(a){
      //   this.$refs.carousel2.setActiveItem(0);
      // }
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
