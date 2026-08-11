import{N as NavTabs}from"./index-9a7f2446.js";import{ae as i18nExpression,ad as expression,aD as requiredValidatorSegment,af as yearMonthDaySelectorSegment,ak as defineComponent,al as usePageHelper,aq as defineSchemas,ah as generateXindexInOrder,b$ as dataTimeSelectorSegment,ca as buttonListItemVisibleByPermission,bS as exportExcelSegment,c0 as queryFieldValueExpression,ai as editTableFormItemValid,ar as RenderEngine,cm as bus,br as getImgSrc,C as getFileUrl,cE as transformColumns,n as normalizeComponent}from"./index-6b6051d8.js";import{s as shoppingCartApi}from"./index-f68159c8.js";const sumDialog={type:"void",title:i18nExpression("oneStopShopping.setSummaryAndNoticeUser"),"x-component":"RDialog","x-component-props":{"close-on-click-modal":!1,destroyOnClose:!0,size:"small",footerButtonList:expression(`(_, { cancelButton,okButton }) => {
      return [
        cancelButton,
        okButton,
      ]
        
      }`),beforeClose:expression(`(done, type) => {
        if ( type === 'ok') {
          $confirmSum($form,$queryEngine,done)
        } else {
          done()
          }
        }
      `)},properties:{sumForm:{type:"object","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:1,columnGap:32,rowGap:0},properties:{summaryNickname:{type:"string",title:"{{$t('oneStopShopping.summaryUser')}}","x-decorator":"FormItem","x-component":"QuickSearchWrapper","x-component-props":{showKey:"username",propKey:"username",name:"scc_rbac_user_display","@close-quicksearch":expression(`(val, scope) => {
              $values.sumForm.summaryUserId = val ? val.userId : ''
              $values.sumForm.summaryEmpNo = val ? val.username : ''
              $values.sumForm.summaryNickname = val ? val.nickname : ''
            }`)}},deptLeaderUserNickname:{type:"string",title:"{{$t('cusEntry.inq.departmentLeader')}}","x-decorator":"FormItem","x-component":"QuickSearchWrapper","x-component-props":{showKey:"nickname",name:"scc_rbac_user_display","@close-quicksearch":expression(`val => {
              const {
                userId,
                nickname
              } = val || {}
              $values.sumForm.deptLeaderUserId = userId || ''
              $values.sumForm.deptLeaderUserNickname = nickname || ''
            }`)}}}}}},rejectDialog={type:"void",title:i18nExpression("oneStopShopping.inputRejectReason"),"x-component":"RDialog","x-component-props":{"close-on-click-modal":!1,destroyOnClose:!0,size:"small",footerButtonList:expression(`(_, { cancelButton,okButton }) => {
      return [
        cancelButton,
        okButton,
      ]
        
      }`),beforeClose:expression(`(done, type) => {
        if ( type === 'ok') {
          $rejectSubmit($form,$queryEngine,done)
        } else {
          done()
          }
        }
      `)},properties:{rejectForm:{type:"object","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:1,columnGap:32,rowGap:0},properties:{returnReason:{type:"string","x-decorator":"FormItem",title:"{{$t('purchaseDemand.rejectReason')}}","x-component-props":{disabled:expression("$form.readPretty")},...requiredValidatorSegment}}}}},batchDialog={type:"void",title:i18nExpression("vendorMod.batchMaintain"),"x-component":"RDialog","x-component-props":{"close-on-click-modal":!1,destroyOnClose:!0,size:"small",footerButtonList:expression(`(_, { cancelButton,okButton }) => {
      return [
        cancelButton,
        okButton,
      ]
        
      }`),beforeClose:expression(`(done, type) => {
        if ( type === 'ok') {
          $batchSubmit($form,$queryEngine,done)
        } else {
          done()
          }
        }
      `)},properties:{batchUpdateForm:{type:"object","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:1,columnGap:32,rowGap:0},properties:{requirementDate:{"x-decorator":"FormItem",title:"{{$t('purchaseDemand.requirementDate')}}",...yearMonthDaySelectorSegment}}}}},_sfc_main$1=defineComponent({__name:"list-engine",setup(__props){const{emitTabAdd,t:$t,app}=usePageHelper(),$ceeaSetSummaryAndNoticeUser=($form,$queryEngine)=>{let selects=$form.query("table").take().componentProps.componentInstance.getCheckboxRecords();if(selects.find(v=>v.status!=="DRAFT"))return app.$message.warning($t("oneStopShopping.createProjectMsg11"));if(selects.length<1)return app.$message.warning($t("oneStopShopping.createProjectMsg12"));$form.query("sumDialog").take().setComponentProps({visible:!0})},$batchMaintenance=($form,$queryEngine)=>{let selects=$form.query("table").take().componentProps.componentInstance.getCheckboxRecords();if(selects.find(v=>v.status!=="DRAFT"))return app.$message.warning($t("cusEntry.tipMessage.unDraftListMsg"));if(selects.length<1)return app.$message.warning($t("oneStopShopping.createProjectMsg14"));$form.query("batchDialog").take().setComponentProps({visible:!0})},$createProject=($form,$queryEngine)=>{let selects=$form.query("table").take().componentProps.componentInstance.getCheckboxRecords();if(selects.find(v=>v.status!=="APPROVED"))return app.$message.warning($t("cusEntry.tipMessage.unApprovalMsg"));const userId=app.$store.getters.userInfo.userId;if(selects.findIndex(item=>item.summaryUserId!==userId)>-1)return app.$message.warning($t("cusEntry.tipMessage.loginerNoSummaryUser")),!1;const ids=selects.map(item=>item.shopCartId);$queryEngine.request.baseRequest({loading:!0,action:"createRequirements",type:"ShopCart",payload:ids,query:{"*":{}}}).then(res=>{res.data&&(res.data.length>0?(app.$message.success($t("common.success")),bus.$emit("ShopCart"),app.$confirm($t("oneStopShopping.createProjectMsg4"),$t("components.approvalHead.tips.tip"),{confirmButtonText:$t("common.confirm"),cancelButtonText:$t("components.common.cancel"),type:"warning"}).then(()=>{app.$router.push({name:"purchaseApplication",params:{autoQuery:!0}})}).catch(err=>{})):(app.$message.warning($t("oneStopShopping.createProjectMsg5")),bus.$emit("ShopCart")))}).catch(()=>{})},$submitGet=($form,$queryEngine)=>{let selects=$form.query("table").take().componentProps.componentInstance.getCheckboxRecords();if(selects.length===0)return app.$message.warning($t("cusEntry.tipMessage.selectRowsMsg")),!1;if(selects.findIndex(item=>!["DRAFT","WITHDRAW","REJECTED"].includes(item.status))>-1)return app.$message.warning($t("cusEntry.tipMessage.selectRowStatusNotUnsubmit")),!1;let validTag=!0,tipMessage="";if(selects.some(item=>{if(!item.deptLeaderUserId||!item.requirementDate||!item.requirementNum||!item.materialName||!item.extDepartmentcode||!item.extReferencePrice||!item.currencyCode||!item.unit||!item.extUseTo||!item.extUserPhone||!item.extAddressName||!item.extReceiver)return validTag=!1,tipMessage=$t("cusEntry.tipMessage.materialRowRequired",{materialCode:item.materialCode}),!0;if(Number(item.requirementNum)<Number(item.extOrderQuantityMinimum))return validTag=!1,tipMessage=$t("cusEntry.tipMessage.extOrderQuantityMinimumMsg",{materialCode:item.materialCode}),!0}),!validTag)return app.$message.warning(tipMessage),!1;let shopCarts=[...selects];$queryEngine.request.baseRequest({loading:!0,action:"submit",type:"ShopCart",payload:shopCarts,query:{"*":{}}}).then(res=>{app.$message.success($t("common.success")),bus.$emit("ShopCart")}).catch(()=>{})},$approval=($form,$queryEngine)=>{const selects=$form.query("table").take().componentProps.componentInstance.getCheckboxRecords();if(selects.length===0)return app.$message.warning($t("cusEntry.tipMessage.selectRowsMsg")),!1;if(selects.findIndex(item=>item.status!=="SUBMITTED")>-1)return app.$message.warning($t("cusEntry.tipMessage.onlySelectApprovalRows")),!1;const userId=app.$store.getters.userInfo.userId;if(selects.findIndex(item=>item.deptLeaderUserId!==userId)>-1)return app.$message.warning($t("cusEntry.tipMessage.loginerNoLeader")),!1;$queryEngine.request.baseRequest({type:"ShopCart",action:"approved",lang:"zh-cn",payload:selects,query:{"*":{}}}).then(res=>{app.$message.success($t("common.success")),bus.$emit("ShopCart")})},$keep=($form,$queryEngine)=>{let selects=$form.query("table").take().componentProps.componentInstance.getCheckboxRecords();if(!selects.length)return app.$message.warning($t("cusEntry.tipMessage.selectRowsMsg"));const saveData=selects.filter(item=>["DRAFT","WITHDRAW","REJECTED"].includes(item.status));$queryEngine.request.baseRequest({loading:!0,action:"add",type:"ShopCart",payload:saveData,query:{"*":{}}}).then(res=>{app.$message.success($t("common.success")),bus.$emit("ShopCart")}).catch(()=>{})},$closeByIds=($form,$queryEngine)=>{let selects=$form.query("table").take().componentProps.componentInstance.getCheckboxRecords();if(selects.length===0)return app.$message.warning($t("cusEntry.tipMessage.selectRowsMsg")),!1;if(selects.find(v=>v.status!=="APPROVED"))return app.$message.warning($t("cusEntry.tipMessage.unApprovalMsg"));const userId=app.$store.getters.userInfo.userId;if(selects.findIndex(item=>item.summaryUserId!==userId)>-1)return app.$message.warning($t("cusEntry.tipMessage.loginerNoSummaryUser")),!1;app.$prompt("",$t("cusEntry.tipMessage.closeTip"),{confirmButtonText:$t("common.confirm"),cancelButtonText:$t("common.cancel"),inputType:"textarea",inputValidator:value=>value?!0:$t("cusEntry.tipMessage.closeReason")}).then(({value})=>{$queryEngine.request.baseRequest({loading:!0,action:"close",type:"ShopCart",payload:selects.map(item=>({...item,extCloseComment:value})),query:{"*":{}}}).then(res=>{app.$message.success($t("common.success")),bus.$emit("ShopCart")})})},$deleteByIds=($form,$queryEngine)=>{let selects=$form.query("table").take().componentProps.componentInstance.getCheckboxRecords(),delIds=!0;selects.forEach(item=>{["DRAFT","WITHDRAW","REJECTED"].includes(item.status)||(delIds=!1)});let ids=[];if(selects.forEach(elm=>{ids.push(elm.shopCartId)}),!delIds||ids.length===0)return app.$message.warning($t("oneStopShopping.createProjectMsg11"));const username=app.$store.getters.userInfo.username;if(selects.findIndex(item=>item.createdBy!==username)>-1)return app.$message.warning($t("cusEntry.tipMessage.loginerNoCreatedBy")),!1;$queryEngine.request.baseRequest({loading:!0,action:"delete",type:"ShopCart",payload:ids,query:{"*":{}}}).then(res=>{app.$message.success($t("common.success")),bus.$emit("ShopCart")}).catch(()=>{})},$submitOut=($form,$queryEngine)=>{let userId=app.$store.getters.userInfo.userId,selects=$form.query("table").take().componentProps.componentInstance.getCheckboxRecords(),currentRowsCheck=selects.find(v=>v.status!=="SUBMITTED"),userIdCheck=selects.find(v=>v.summaryUserId!==userId);if(currentRowsCheck)return app.$message.warning($t("oneStopShopping.submitOutMsg1"));if(userIdCheck)return app.$message.warning($t("oneStopShopping.submitOutMsg2"));if(selects.length<1)return app.$message.warning($t("components.userSelection.selectData"));$form.query("rejectDialog").take().setComponentProps({visible:!0})},$rejectSubmit=($form,$queryEngine,done)=>{$form.query("rejectForm").take().submit(values=>{let selects=$form.query("table").take().componentProps.componentInstance.getCheckboxRecords(),ids=[];selects.forEach(item=>{ids.push(item.shopCartId)});let submitData={ids,returnReason:values.returnReason};$queryEngine.request.baseRequest({loading:!0,action:"withdraw",type:"ShopCart",payload:[submitData],query:{"*":{}}}).then(res=>{app.$message.success($t("common.success")),bus.$emit("ShopCart"),done()}).catch(()=>{})})},$batchSubmit=($form,$queryEngine,done)=>{$form.query("batchUpdateForm").take().submit(values=>{const submitData=$form.query("table").take().componentProps.componentInstance.getCheckboxRecords().map(item=>({...item,...values}));$queryEngine.request.baseRequest({loading:!0,action:"add",type:"ShopCart",payload:submitData,query:{"*":{}}}).then(res=>{app.$message.success($t("common.success")),bus.$emit("ShopCart"),done()}).catch(()=>{})})},$confirmSum=($form,$queryEngine,done)=>{$form.query("sumForm").take().submit(values=>{const submitData=$form.query("table").take().componentProps.componentInstance.getCheckboxRecords().map(item=>({...item,...values}));$queryEngine.request.baseRequest({loading:!0,action:"add",type:"ShopCart",payload:submitData,query:{"*":{}}}).then(res=>{app.$message.success($t("common.success")),bus.$emit("ShopCart"),done()}).catch(()=>{})})},$detailOne=(type,row)=>{},$readOne=row=>{},$editOne=row=>{},$delete=($queryEngine,row,$message)=>{$queryEngine.request.delete([row.tagTemplateRelationId]).then(res=>{$message.success($t("common.successDelete")),$queryEngine.state.paginationManagement.refresh()})},smallImgSlot=($form,row)=>{const data=$form.query("ShopCart").get("data");return{functional:!0,render:h=>h("div",{attrs:{class:"table-img"}},[h("div",{attrs:{class:"logo-small"},style:{padding:row.extShelvesAttachId?0:"0 4px",display:data.listShowType==="list"?"block":"none"}},[h("img",{attrs:{width:"100%",src:row.imgUrl,alt:row.materialName}})])])}},formatImgUrl=(id,$form)=>id?getImgSrc(id):getFileUrl($form.query("ShopCart").get("data").defaultLogo),adaptData=(data,$form)=>data.map(item=>{let imgUrl=formatImgUrl(item.extShelvesAttachId,$form);return{...item,imgUrl}}),schema=defineSchemas({state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{}},ShopCart:{type:"void","x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container shop-cart",direction:"vertical"},"x-query-engine":{service:"sup-ce",actions:{paginationQuery:{immediate:!0,ready:expression(`async () => {
            /* 获取业务实体和默认部门 */
            const userId = app.$store.getters.userInfo.userId
            const respone = await shoppingCartApi.getUserOrgAndDept(userId)
            if (respone) {
              const {
                ouOrganization,
                departmentOrganization
              } = respone.data
              $form.query('ShopCart').get('data').defaultOrgInfo = ouOrganization
              $form.query('ShopCart').get('data').defaultDeptInfo = departmentOrganization
              /* 获取业务实体下的部门 */
              if (ouOrganization?.organizationId) {
                const params = {
                  parentId: ouOrganization?.organizationId
                }
                shoppingCartApi.getDept(params).then(res => {
                  if (res.data) {
                    $form.query('ShopCart').get('data').deptList = res.data.map(item => ({
                      label: item.organizationName,
                      value: item.organizationCode,
                      id: item.organizationId
                    })) || []
                  }
                })
              }
              /* 获取默认收货地址 */
              if (departmentOrganization?.organizationId) {
                shoppingCartApi.getDeptAddress(departmentOrganization?.organizationId).then(res => {
                  if (res.data) {
                    /* 获取默认收货地址&收货人*/
                    $form.query('ShopCart').get('data').defaultAddress= res.data.find(item => item.isDefault === 'Y') || {}
                  }
                })
              }
            }
            return true
          }`),transformRequest:expression(`(data, headers) => {
            const {
              filter
            } = data.payload
            if (!filter) {
              data.payload.filter = {
                status: {
                  ne: 'CLOSE'
                }
              }
            } else if (!filter.status) {
              data.payload.filter.status = {
                ne: 'CLOSE'
              }
            }
            data.query['*'] = {}
            return data
          }`),onSuccess:expression(`async (res) => {
            const materialIds = res.data.map(item => item.materialId)
            const resData = await getMaterialLang(materialIds)
            const newData = res.data.map(item => {
              const data = resData.find(it => it.materialId === item.materialId)
              return {
                ...item,
                materialNameShow: data?.materialName,
                specificationShow: data?.extMaterialModel
              }
            })
            let list = adaptData(newData, $form)
            setTimeout(() => {
              $form.values.table = list
            })
          }`)}}},"x-data":{listShowType:"list",smallLogo:"images/gwn.png",bigLogo:"images/gwn.png",defaultLogo:"images/gwn.png",defaultOrgInfo:{},defaultDeptInfo:{},defaultAddress:{},deptList:[],addressList:[]},properties:{sumDialog:{...sumDialog},rejectDialog:{...rejectDialog},batchDialog:{...batchDialog},bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"ShopCart","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({materialId:{type:"string",title:"{{$t('common.materialCode')}}","x-component":"QuickSearchWrapper","x-component-props":{name:"scc_base_material_item_contract",showKey:"materialCode",propKey:"materialId"}},status:{type:"string",title:"{{$t('common.status')}}","x-component":"DictSelect","x-component-props":{code:"SHOP_CART_STATUS"},"x-reactions":{effects:["onFieldInit"],fulfill:{state:{value:expression("app.$route?.params?.from === 'fromFun' ? app.$route?.params?.row.configCode === 'NPM_SHOP_CART_WAIT_APPROVE' ? 'SUBMITTED' : 'APPROVED' : ''")},run:"$initDept($form)"}}},orgId:{type:"string",title:"{{$t('quota.org')}}","x-component":"OrganizationSelector","x-component-props":{"parent-id":-1,"node-type":"OU","select-type":"input",placeholder:"{{$t('common.pleaseSelect')}}",multiple:!0,"@select":expression(`(node) => {
                  if (!$form.values.query.organizationIds) return
                  $form.values.query.organizationIds = null
              }`)},"x-query-engine-query-operator":"in"},categoryName:{type:"string",title:"{{$t('common.category')}}","x-component":"CCategorySelect","x-component-props":{showKey:"categoryName"}},summaryNickname:{type:"string",title:"{{$t('oneStopShopping.summaryUser')}}","x-query-engine-query-operator":"contains"},createdFullName:{type:"string",title:"{{$t('common.creator')}}","x-query-engine-query-operator":"contains"},creationDate:{title:"{{$t('common.creationTime')}}",...dataTimeSelectorSegment,"x-query-engine-query-operator":"between"},extDepartment:{type:"string",title:i18nExpression("cusEntry.sup.extDepartment"),"x-query-engine-query-operator":"contains"}})},toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary","@click":expression(`() => {
                /* 获取业务实体和部门 */
                const state = $form.query('ShopCart').get('data')
                const {
                  organizationName,
                  organizationCode,
                  organizationId
                } = state.defaultOrgInfo || {}
                const {
                  organizationName: extDepartment,
                  organizationId: extCeeaDeptid,
                  organizationCode: extDepartmentcode
                } = state.defaultDeptInfo || {}
                const {
                  siteId,
                  siteName,
                  siteDesc,
                  receiver,
                  addressRegion,
                  receiverPhone
                } = state.defaultAddress || {}
                $self.query('.table').take(field => {
                  field.componentProps.componentInstance.addRow('unshift', {
                    status: 'DRAFT',
                    extIsGoods: 'N',
                    summaryNickname: app.$store.getters.userInfo.nickname,
                    summaryUserId: app.$store.getters.userInfo.userId,
                    extDepartment,
                    extCeeaDeptid,
                    extDepartmentcode,
                    imgUrl: formatImgUrl(null, $form),
                    orgName: organizationName,
                    orgCode: organizationCode,
                    orgId: organizationId,
                    extAddressId: siteId,
                    extAddressName: siteName,
                    extAreaCode: addressRegion,
                    extAddress: siteDesc,
                    extReceiver: receiver,
                    extReceiverContact:receiverPhone,
                    currencyCode: 'RMB',
                    extAttachId:null,
                    extAttachName:null
                  })
                })
              }`),...buttonListItemVisibleByPermission("pr:shoppingCart:add")}},importExcel:{type:"void","x-component":"ImportExcel","x-component-props":{title:i18nExpression("cusEntry.inq.importShopCart"),type:"primary",extraData:{fileModular:"sup",fileFunction:"shopCartImportExcel",fileType:"excel"},upLoadUrl:"/api-sup-ce/shopCart/ext/importShopCartExcel",downloadTemplateOptions:{downloadUrl:"/api-sup-ce/shopCart/ext/importShopCartModelDownload",fileName:expression("$t('cusEntry.inq.shopCartImportTemplateXLXS')")},class:"export-excel","@handleSuccess":expression(`() => {
                $queryEngine.state.paginationManagement.refresh()
              }`)}},delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{type:"primary","@click":expression(`() => {
                $deleteByIds($form, $queryEngine)
              }`),...buttonListItemVisibleByPermission("pr:shoppingCart:ceeaDeleteByIds")}},ceeaSetSummaryAndNoticeUser:{type:"void",title:"{{$t('oneStopShopping.setSummaryAndNoticeUser')}}","x-component-props":{type:"primary","@click":expression(`() => {
                $ceeaSetSummaryAndNoticeUser($form, $queryEngine)
              }`),...buttonListItemVisibleByPermission("pr:shoppingCart:ceeaSetSummaryAndNoticeUser")}},batchMaintenance:{type:"void",title:"{{$t('vendorMod.batchMaintain')}}","x-component":"RButton","x-component-props":{type:"primary","@click":expression(`() => {
                $batchMaintenance($form, $queryEngine)
              }`),...buttonListItemVisibleByPermission("pr:shoppingCart:Batch_maintenance")}},keep:{type:"void",title:"{{$t('common.save')}}","x-component-props":{type:"primary","@click":expression(`() => {
                $keep($form, $queryEngine)
              }`),...buttonListItemVisibleByPermission("pr:shoppingCart:save")}},submitSummaryUser:{type:"void",title:i18nExpression("common.submit"),"x-component-props":{type:"primary",...buttonListItemVisibleByPermission("pr:shoppingCart:submitSummaryUser"),"@click":expression(`() => {
                $submitGet($form, $queryEngine)
              }`)}},recall:{type:"void",title:i18nExpression("common.recall"),"x-component-props":{type:"primary","@click":expression(`() => {
                $recall($form, $queryEngine)
              }`),...buttonListItemVisibleByPermission("pr:shoppingCart:recall")}},exportExcel:{type:"void","x-component":"ExportExcel","x-component-props":{type:"default",pageUrl:"/api-sup-ce/api-ql/ShopCart/query",...exportExcelSegment,meiqlKey:"ShopCart",filterParams:queryFieldValueExpression("query"),dictCodes:{status:"SHOP_CART_STATUS",purchaseType:"PURCHASE_TYPE"},class:"export-excel"},"x-reactions":expression(`(field) => {
              $form.query('ShopCart.table').take(fields => {
                let columns = fields?.data?.columns ?? []
                field.componentProps.tableHeader = transformColumns(columns,[{
                  targetFiled: 'materialNameShow',
                  field: 'materialName',
                  title: $t('materialMainData.materialDesc')
                },
                {
                  targetFiled: 'specificationShow',
                  field: 'specification',
                  title: $t('common.specification')
                }])
             })
            }`)},createProject:{type:"void",title:"{{$t('oneStopShopping.createProject')}}","x-component-props":{type:"default","@click":expression(`() => {
                $createProject($form, $queryEngine)
              }`),...buttonListItemVisibleByPermission("pr:shoppingCart:createProjectDetail")}},close:{type:"void",title:"{{$t('common.close')}}","x-component-props":{type:"default","@click":expression(`() => {
                $closeByIds($form, $queryEngine)
              }`),...buttonListItemVisibleByPermission("pr:shoppingCart:close")}},approval:{type:"void",title:i18nExpression("cusEntry.common.flowApproval"),"x-component-props":{type:"primary",...buttonListItemVisibleByPermission("pr:shoppingCart:approval"),"@click":expression(`() => {
                $approval($form, $queryEngine)
              }`)}},refuse:{type:"void",title:i18nExpression("common.toRefuse"),"x-component-props":{type:"primary","@click":expression(`() => {
                $refuse($form, $queryEngine)
              }`),...buttonListItemVisibleByPermission("pr:shoppingCart:refuse")}}}},table:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",preColumns:"seq, checkbox",openCustomTable:!0,editMode:!0,"scroll-y":{gt:10,oSize:10},"@current-change":expression(`({ row, rowIndex }) => {
            if (!row.canEdit) {
              row.canEdit = true
              let otherList = $form.query('table').take().value.filter((item, index) => index !== rowIndex)
              otherList.forEach(item => {
                item.canEdit = false
              })
            }
          }`)},properties:generateXindexInOrder({shopCartId:{type:"number","x-hidden":!0,"x-query-engine-primary-key":!0},status:{type:"string","x-component":"DictSelect","x-component-props":{code:"SHOP_CART_STATUS"},"x-read-pretty":!0,"x-render-table-column":{title:"{{$t('common.status')}}",minWidth:100}},imgSmall:{type:"object","x-query-engine-skip":!0,"x-content":"{{smallImgSlot($form, $table.getRowByIndex($self.index))}}","x-read-pretty":!1,"x-render-table-column":{minWidth:90,sortable:!1,"show-overflow":!1,title:i18nExpression("cusEntry.sup.img")}},requirementHeadNum:{type:"string","x-render-table-column":{title:"{{$t('purchaseDemand.requirementHeadNum')}}",minWidth:120,static:!0},"x-read-pretty":!0},summaryUserId:{type:"number","x-hidden":!0},summaryNickname:{type:"string","x-render-table-column":{title:"{{$t('oneStopShopping.summaryUser')}}",minWidth:120},"x-component":"QuickSearchWrapper","x-component-props":{showKey:"nickname",name:"scc_rbac_user_display","@close-quicksearch":expression(`val => {
                const {
                  department,
                  nickname,
                  userId,
                  username
                } = val || {}
                let row = $table.getRowByIndex($self.index)
                row.summaryNickname = nickname || ''
                row.summaryUserId = userId || ''
              }`)},"x-reactions":expression(`() => {
              const row = $table.getRowByIndex($self.index)
              const {
                status,
                extIsGoods,
                canEdit
              } = row || {}
              $self.readPretty = !(['WITHDRAW', 'DRAFT', 'REJECTED'].includes(status) && canEdit)
            }`),...editTableFormItemValid},deptLeaderUserId:{type:"number","x-hidden":!0},deptLeaderUserNickname:{type:"string","x-render-table-column":{title:"{{$t('cusEntry.inq.departmentLeader')}}",minWidth:120},"x-component":"QuickSearchWrapper","x-component-props":{showKey:"nickname",name:"scc_rbac_user_display","@close-quicksearch":expression(`val => {
                const {
                  userId,
                  nickname
                } = val || {}
                let row = $table.getRowByIndex($self.index)
                row.deptLeaderUserId = userId || ''
                row.deptLeaderUserNickname = nickname || ''
              }`)},"x-reactions":expression(`() => {
              const row = $table.getRowByIndex($self.index)
              const {
                status,
                extIsGoods,
                canEdit
              } = row || {}
              $self.readPretty = !(['WITHDRAW', 'DRAFT', 'REJECTED'].includes(status) && canEdit)
            }`),...editTableFormItemValid},requirementDate:{"x-render-table-column":{title:"{{$t('purchaseDemand.requirementDate')}}",minWidth:150},"x-reactions":expression(`() => {
              const row = $table.getRowByIndex($self.index)
              const {
                status,
                extIsGoods,
                canEdit
              } = row || {}
              $self.readPretty = !(['WITHDRAW', 'DRAFT', 'REJECTED'].includes(status) && canEdit)
            }`),...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.requirementDate, '{y}-{m}-{d}')
              }`),style:{width:"120px"},"picker-options":expression("$cannotLessCurrentTimeOptions")},...editTableFormItemValid},materialCode:{type:"string","x-render-table-column":{title:"{{$t('common.materialCode')}}",minWidth:120},"x-component":"QuickSearchWrapper","x-component-props":{showKey:"materialCode",name:"scc_base_material_item_contract","@close-quicksearch":expression(`async val => {
                const {
                  materialName,
                  materialCode,
                  materialId,
                  extMaterialModel,
                  categoryName,
                  categoryCode,
                  categoryId,
                  unit,
                  unitName
                } = val || {}
                let row = $table.getRowByIndex($self.index)
                row.materialName = materialName || ''
                row.materialCode = materialCode || ''
                row.materialId = materialId || null
                row.categoryName = categoryName || ''
                row.categoryId = categoryId || null
                row.categoryCode = categoryCode || ''
                row.specification = extMaterialModel || ''
                row.unitName = unitName || ''
                row.unit = unit || ''
                if (val) {
                  const resData = await getMaterialLang([materialId])
                  row.materialNameShow = resData[0]?.materialName
                  row.specificationShow = resData[0]?.extMaterialModel
                  
                  app.$http({
                    url: 'api-sup-ce/shopCart/ext/extAdd',
                    method: 'POST',
                    data: {
                      materialCode,
                      orgId: row.orgId,
                      extAreaCode: row.extAreaCode
                    }
                  }).then(res => {
                    if (res.data) {
                      const {
                        extReferencePrice,
                        orderQuantityMinimum
                      } = res.data
                      row.extReferencePrice = extReferencePrice
                      row.orderQuantityMinimum = orderQuantityMinimum
                      row.extIsGoods = 'Y'
                    } else {
                      row.extIsGoods = 'N'
                    }
                  })
                } else {
                  row.materialNameShow = null
                  row.specificationShow = null
                }
              }`)},"x-reactions":expression(`() => {
              const row = $table.getRowByIndex($self.index)
              const {
                status,
                extIsGoods,
                canEdit
              } = row || {}
              $self.readPretty = !(['WITHDRAW', 'DRAFT', 'REJECTED'].includes(status) && canEdit) || extIsGoods === 'Y'
            }`),...editTableFormItemValid},materialName:{type:"string","x-hidden":!0},materialNameShow:{type:"string","x-render-table-column":{title:"{{$t('materialMainData.materialDesc')}}",minWidth:120,static:!0},"x-read-pretty":!0,...editTableFormItemValid},extOrderQuantityMinimum:{type:"string","x-render-table-column":{title:i18nExpression("cusEntry.sup.extOrderQuantityMinimum"),minWidth:120,static:!0},"x-read-pretty":!0},categoryName:{type:"string","x-render-table-column":{title:"{{$t('common.category')}}",minWidth:120},"x-read-pretty":!0,...editTableFormItemValid},specification:{type:"string","x-hidden":!0},specificationShow:{type:"string","x-render-table-column":{title:"{{$t('common.specification')}}",minWidth:120,static:!0},"x-read-pretty":!0},unit:{type:"string","x-render-table-column":{title:"{{$t('dataConfMod.unit')}}",minWidth:120},"x-component":"DictSelect","x-component-props":{code:"unit"},"x-read-pretty":!0,...editTableFormItemValid},requirementNum:{type:"number","x-render-table-column":{title:"{{$t('purchaseDemand.requirementQuantity')}}",minWidth:120},"x-component-props":{min:0,"@change":expression(`(value) => {
                  setTimeout(() => {
                    const ifPrecision = value?.toString().includes('.')
                    if (ifPrecision) {
                      /* 获取小数点 */
                      const [integer, precision] = value?.toString().split('.')
                      $self.value = Number(integer + '.' +  precision.toString().slice(0, 4))
                    }
                  })
              }`)},"x-reactions":expression(`() => {
              const row = $table.getRowByIndex($self.index)
              const {
                status,
                extIsGoods,
                canEdit
              } = row || {}
              $self.readPretty = !(['WITHDRAW', 'DRAFT', 'REJECTED'].includes(status) && canEdit)
            }`),...editTableFormItemValid},orgName:{type:"string","x-render-table-column":{title:"{{$t('purchaseDemand.businessEntity')}}",minWidth:120,static:!0},"x-read-pretty":!0},extUserPhone:{type:"string","x-render-table-column":{title:i18nExpression("cusEntry.sup.extUserPhone"),minWidth:140},"x-reactions":expression(`() => {
              const row = $table.getRowByIndex($self.index)
              const {
                status,
                extIsGoods,
                canEdit
              } = row || {}
              $self.readPretty = !(['WITHDRAW', 'DRAFT', 'REJECTED'].includes(status) && canEdit)
            }`),...editTableFormItemValid},extDepartment:{type:"string","x-hidden":!0},extDepartmentcode:{type:"string","x-render-table-column":{title:i18nExpression("cusEntry.sup.extDepartment"),minWidth:120},"x-component":"Select","x-component-props":{"@change":expression(`field => {
                let state = $form.query('ShopCart').get('data')
                let row = $table.getRowByIndex($self.index)
                let deptItem = state.deptList.find(item=> item.value == field)
                row.extCeeaDeptid = deptItem?.id
                row.extDepartment = deptItem?.label
                if (field) {
                  if (row.extIsGoods === 'N') {
                    shoppingCartApi.getDeptAddress(deptItem.id).then(res => {
                      if (res.data) {
                        state.addressList = res.data.map(item =>({
                          receiver: item.receiver,
                          phone: item.receiverPhone,
                          id: item.siteId,
                          label: item.siteName,
                          code: item.siteDesc,
                          value: item.siteId,
                          areaCode: item.addressRegion
                        }))
                        /* 获取默认收货地址&收货人*/
                        const defaultItem = res.data.find(item => item.isDefault === 'Y')
                        if (defaultItem) {
                          row.extAddressName = defaultItem.siteName
                          row.extAddressId = defaultItem.siteId
                          row.extAddress = defaultItem.siteDesc
                          row.extReceiver = defaultItem.receiver
                          row.extReceiverContact = defaultItem.phone
                          row.extAreaCode = defaultItem.addressRegion
                        }
                      }
                    })
                  }
                } else {
                  row.extReceiver = ''
                  row.extAddressName = ''
                  row.extAddressId = ''
                  row.extAddress = ''
                  row.extAreaCode = ''
                  row.extReceiverContact = ''
                  state.addressList = []
                }
              }`)},"x-reactions":expression(`() => {
              const row = $table.getRowByIndex($self.index)
              const {
                status,
                extIsGoods,
                canEdit
              } = row || {}
              $self.readPretty = !(['WITHDRAW', 'DRAFT', 'REJECTED'].includes(status) && canEdit)
            }`),enum:expression("$form.query('ShopCart').get('data').deptList"),...editTableFormItemValid},extAddressName:{type:"string","x-render-table-column":{title:i18nExpression("cusEntry.sup.extAddress"),minWidth:120},"x-reactions":expression(`() => {
              const row = $table.getRowByIndex($self.index)
              const {
                status,
                extIsGoods,
                canEdit
              } = row || {}
              $self.readPretty = !(['WITHDRAW', 'DRAFT', 'REJECTED'].includes(status) && canEdit)
            }`),"x-component":"Select","x-component-props":{"@visible-change":expression(`value => {
                let state = $form.query('ShopCart').get('data')
                let row = $table.getRowByIndex($self.index)
                if (value && row.extDepartmentcode) {
                  let deptItem = state.deptList.find(item=> item.value == row.extDepartmentcode)
                  if (deptItem) {
                    shoppingCartApi.getDeptAddress(deptItem.id).then(res => {
                      if (res.data) {
                        state.addressList = res.data.map(item =>({
                          receiver: item.receiver,
                          phone: item.receiverPhone,
                          id: item.siteId,
                          label: item.siteName,
                          code: item.siteDesc,
                          value: item.siteId,
                          areaCode: item.addressRegion
                        }))
                      }
                    })
                  } else {
                    row.extReceiver = ''
                    row.extAddressName = ''
                    row.extAddressId = ''
                    row.extAreaCode = ''
                    row.extAddress = ''
                    row.extReceiverContact = ''
                  }
                }
              }`),"@change":expression(`field => {
                let row = $table.getRowByIndex($self.index)
                let state = $form.query('ShopCart').get('data')
                const addressItem = state.addressList.find(item => item.id === field)
                row.extAddressName = addressItem.label
                row.extAddressId = addressItem.id
                row.extAddressCode = addressItem.code
                row.extReceiver = addressItem.receiver
                row.extAreaCode = addressItem.areaCode
                row.extReceiverContact = addressItem.phone
              }`)},enum:expression("$form.query('ShopCart').get('data').addressList"),...editTableFormItemValid},extReceiver:{type:"string","x-render-table-column":{title:i18nExpression("cusEntry.sup.extReceiver"),minWidth:120,static:!0},"x-read-pretty":!0,...editTableFormItemValid},extAreaCode:{type:"string","x-render-table-column":{title:i18nExpression("cusEntry.sup.area"),minWidth:120},"x-component":"DictSelect","x-component-props":{code:"REGION"},"x-read-pretty":!0},extReferencePrice:{type:"number","x-render-table-column":{title:i18nExpression("cusEntry.sup.extReferencePrice"),minWidth:120},"x-component-props":{min:0},"x-reactions":expression(`() => {
              const row = $table.getRowByIndex($self.index)
              const {
                status,
                extIsGoods,
                canEdit
              } = row || {}
              $self.readPretty = !(['WITHDRAW', 'DRAFT', 'REJECTED'].includes(status) && canEdit) || extIsGoods === 'Y'
            }`),...editTableFormItemValid},extIsGoods:{type:"string","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-render-table-column":{title:i18nExpression("cusEntry.sup.extIsGoods"),minWidth:120},"x-read-pretty":!0},extRejectReason:{type:"string","x-render-table-column":{title:i18nExpression("cusEntry.sup.extRejectReason"),minWidth:120,static:!0},"x-read-pretty":!0},extUseTo:{type:"string","x-render-table-column":{title:"{{$t('cusEntry.sup.extUseTo')}}",width:160},"x-reactions":expression(`() => {
              const row = $table.getRowByIndex($self.index)
              const {
                status,
                extIsGoods,
                canEdit
              } = row || {}
              $self.readPretty = !(['WITHDRAW', 'DRAFT', 'REJECTED'].includes(status) && canEdit)
            }`),...requiredValidatorSegment},brand:{type:"string","x-render-table-column":{title:"{{$t('dataConfMod.band')}}",width:160},"x-reactions":expression(`() => {
              const row = $table.getRowByIndex($self.index)
              const {
                status,
                extIsGoods,
                canEdit
              } = row || {}
              $self.readPretty = !(['WITHDRAW', 'DRAFT', 'REJECTED'].includes(status) && canEdit)
            }`)},extBuyTypeComment:{type:"string","x-render-table-column":{title:"{{$t('bidMod.remark')}}",width:160},"x-reactions":expression(`() => {
              const row = $table.getRowByIndex($self.index)
              const {
                status,
                extIsGoods,
                canEdit
              } = row || {}
              $self.readPretty = !(['WITHDRAW', 'DRAFT', 'REJECTED'].includes(status) && canEdit)
            }`)},extAttachName:{type:"string",title:i18nExpression("dataConfMod.attachment"),"x-component":"SrmCommonFile","x-component-props":{defaultFile:{fileId:"{{$table.getRowByIndex($self.index).extAttachId}}",fileName:"{{$self.value}}"},"@on-change":expression(`({file}) => {
                let row = $table.getRowByIndex($self.index)
                const { fileId = null, fileName = null } = file || {}
                row.extAttachId = fileId
                row.extAttachName = fileName
                $self.value = fileName          
              }`)},"x-render-table-column":{minWidth:150},"x-reactions":expression(`() => {
              const row = $table.getRowByIndex($self.index)
              const {
                status,
                extIsGoods,
                canEdit
              } = row || {}
              $self.readPretty = !(['WITHDRAW', 'DRAFT', 'REJECTED'].includes(status) && canEdit)
            }`)},createdUserName:{type:"string","x-render-table-column":{title:"{{$t('common.creator')}}",width:120,static:!0},"x-read-pretty":!0},createdFullName:{type:"string","x-hidden":!0},creationDate:{title:"{{ $t('common.creationTime') }}",...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)},"x-render-table-column":{width:150,static:!0},"x-read-pretty":!0},extCloseComment:{type:"string","x-render-table-column":{title:i18nExpression("cusEntry.sup.extCloseReason"),minWidth:120,static:!0},"x-read-pretty":!0},lastUpdateDate:{type:"string","x-query-engine-sort":"desc","x-hidden":!0,"x-query-engine-primary-key":!0}})}}}}),$recall=($form,$queryEngine)=>{const selects=$form.query("table").take().componentProps.componentInstance.getCheckboxRecords();if(selects.length===0)return app.$message.warning($t("cusEntry.tipMessage.selectRowsMsg")),!1;if(selects.findIndex(item=>item.status!=="SUBMITTED")>-1)return app.$message.warning($t("cusEntry.tipMessage.onlySelectRecallRows")),!1;const username=app.$store.getters.userInfo.username;if(selects.findIndex(item=>item.createdBy!==username)>-1)return app.$message.warning($t("cusEntry.tipMessage.loginerNoCreatedBy")),!1;$queryEngine.request.baseRequest({loading:!0,action:"withdraw",type:"ShopCart",payload:selects,query:{"*":{}}}).then(res=>{app.$message.success($t("common.success")),bus.$emit("ShopCart")})},$refuse=($form,$queryEngine)=>{const selects=$form.query("table").take().componentProps.componentInstance.getCheckboxRecords();if(selects.length===0)return app.$message.warning($t("cusEntry.tipMessage.selectRowsMsg")),!1;if(selects.findIndex(item=>item.status!=="SUBMITTED")>-1)return app.$message.warning($t("cusEntry.tipMessage.onlySelectRefuseRows")),!1;const userId=app.$store.getters.userInfo.userId;if(selects.findIndex(item=>item.deptLeaderUserId!==userId)>-1)return app.$message.warning($t("cusEntry.tipMessage.loginerNoLeader")),!1;app.$prompt("",$t("cusEntry.tipMessage.refuseTip"),{confirmButtonText:$t("common.confirm"),cancelButtonText:$t("common.cancel"),inputType:"textarea",inputValidator:value=>value?!0:$t("cusEntry.tipMessage.rejectReason")}).then(({value})=>{$queryEngine.request.baseRequest({loading:!0,action:"reject",type:"ShopCart",payload:selects.map(item=>({...item,extRejectReason:value})),query:{"*":{}}}).then(res=>{app.$message.success($t("common.success")),bus.$emit("ShopCart")})})},$cannotLessCurrentTimeOptions={disabledDate:time=>{const nowDate=new Date;return nowDate.setHours(0),nowDate.setMinutes(0),nowDate.setSeconds(0),nowDate.setMilliseconds(0),time.getTime()<nowDate.getTime()}},$initDept=async $form=>{const userId=app.$store.getters.userInfo.userId,respone=await shoppingCartApi.getUserOrgAndDept(userId);if(respone){const{ouOrganization,departmentOrganization}=respone.data;if($form.query("ShopCart").get("data").defaultOrgInfo=ouOrganization,$form.query("ShopCart").get("data").defaultDeptInfo=departmentOrganization,ouOrganization?.organizationId){const params={parentId:ouOrganization?.organizationId};shoppingCartApi.getDept(params).then(res=>{res.data&&($form.query("ShopCart").get("data").deptList=res.data.map(item=>({label:item.organizationName,value:item.organizationCode,id:item.organizationId}))||[])})}departmentOrganization?.organizationId&&shoppingCartApi.getDeptAddress(departmentOrganization?.organizationId).then(res=>{res.data&&($form.query("ShopCart").get("data").defaultAddress=res.data.find(item=>item.isDefault==="Y")||{})})}},getMaterialLang=async(materialIds=[])=>materialIds.length===0?[]:(await app.$http({url:"/api-base/material/materialItem/ext/multilingual",method:"POST",data:{materialIds,language:app.$i18n.locale},loading:!0})).data;return{__sfc:!0,emitTabAdd,$t,app,$ceeaSetSummaryAndNoticeUser,$batchMaintenance,$createProject,$submitGet,$approval,$keep,$closeByIds,$deleteByIds,$submitOut,$rejectSubmit,$batchSubmit,$confirmSum,$detailOne,$readOne,$editOne,$delete,smallImgSlot,formatImgUrl,adaptData,schema,$recall,$refuse,$cannotLessCurrentTimeOptions,$initDept,getMaterialLang,components:{},scope:{$t,transformColumns,$delete,$ceeaSetSummaryAndNoticeUser,$confirmSum,$submitGet,$submitOut,$rejectSubmit,$keep,$deleteByIds,$closeByIds,$batchMaintenance,$batchSubmit,$createProject,smallImgSlot,formatImgUrl,getImgSrc,adaptData,getFileUrl,app,$approval,shoppingCartApi,$recall,$refuse,$cannotLessCurrentTimeOptions,getMaterialLang,$initDept},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{pageAttrs:_vm.$attrs,scope:_setup.scope,components:_setup.components,schema:_setup.schema,schemaKey:"ShopCartList"}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const shoppingCartList=__component__$1.exports,_sfc_main={name:"ShoppingCartList",components:{NavTabs},data(){return{activeTab:"shoppingCartList",tabs:[{title:this.$t("route.shoppingCart"),name:"shoppingCartList",component:shoppingCartList,closable:!1}]}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
