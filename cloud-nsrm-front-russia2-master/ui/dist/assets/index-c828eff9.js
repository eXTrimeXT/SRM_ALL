import{N as NavTabs}from"./index-9a7f2446.js";import{h as http,ak as defineComponent,al as usePageHelper,am as useAttrs,aq as defineSchemas,ad as expression,ae as i18nExpression,ar as RenderEngine,bN as markRaw,n as normalizeComponent,aD as requiredValidatorSegment,ah as generateXindexInOrder,af as yearMonthDaySelectorSegment,bD as changeFieldVisibleByDeps}from"./index-6b6051d8.js";import{T as Tinymce}from"./index-2d535dac.js";import{F as FileDynamic}from"./file-dynamic-25a093c4.js";const souQuestionApi={getSouProjectList:data=>http({url:"/api-sou/vendor/qa/souOrder/page",method:"POST",data,loading:!1})},_sfc_main$4=defineComponent({__name:"questionDetail",setup(__props){const{emitTabRemove,emitTabAdd,t,app}=usePageHelper();let attrs=useAttrs();const questionDetailSchema=defineSchemas({state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{souProjectList:[],answerContentEditor:null}},SouQuestion:{type:"void","x-component":"FormContainer","x-decorator":"QueryEngine","x-query-engine":{service:"sou",type:"SouQuestion",actions:{read:{immediate:!0,ready:expression(`() => {
            $form.readPretty = $readOnly
            let id = $attrs.params.row.questionId
            $form.values.questionId = id
            return !!id
          }`),transformRequest:expression(`(data,headers) => {
            data.payload = [$form.values.questionId]
          }`),onSuccess:expression(`(res) => {
            console.log('res::',res)
            const value = res.data[0]
            $form.setValues({
              ...value
            })
            $form.query('sceneFiles').take(field => {
              field.componentProps.componentInstance.reLoadFileInfo()
            })
          }`)},save:{cascadeDeletion:!0}}},properties:{collapse:{type:"void","x-component":"Collapse","x-component-props":{defaultOpenPanelCount:1},properties:{receiptInfo:{type:"void","x-query-engine-skip":!0,"x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.receiptInfo")},properties:{orderInfo:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:4,columnGap:32,rowGap:0},properties:{questionId:{type:"string","x-hidden":!0},questionNum:{type:"string",title:"{{$t('bidMod.questionNum')}}","x-decorator":"FormItem","x-component-props":{disabled:!0}},questionTitle:{type:"string",title:"{{$t('bidMod.questionTitle')}}",required:!0,"x-decorator":"FormItem"},questionStatus:{type:"string",title:i18nExpression("bidMod.questionStatus"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"QUERY_STATUS",disabled:!0}},projectId:{type:"string",title:"{{$t('bidMod.bidingName')}}",required:!0,"x-decorator":"FormItem","x-component":"Select","x-hidden":"{{ $buyer() }}","x-component-props":{filterable:!0,remote:!0,clearable:!0,"automatic-dropdown":!0,placeholder:"{{$t('bidMod.msgKeyword')}}","@change":expression(`(val) => {
                        if (val) {
                          const souProjectList = $form.query('state').get('data').souProjectList
                          const project = souProjectList.find(item => item.value === val)
                          const { label = '', no = '' } = project || {}
                          $form.values.souName = label
                          $form.values.souNo = no
                        }else {
                          $form.values.souName = ''
                          $form.values.souNo = ''
                        }
                      }`),"remote-method":expression(`(val) => {
                        let parma = {
                          souType: $attrs.params.souType,
                          souName: val,
                          pageSize: 999,
                          pageNum: 1
                        }
                        souQuestionApi.getSouProjectList(parma).then(res => {
                          let attr = res.data.list || []
                          let optionData = []
                          attr.forEach(item => {
                            const objs = {
                              id: (item.projectId).toString(),
                              no: item.souNo,
                              value: (item.projectId).toString(),
                              label: item.souName
                            }
                            optionData.push(objs)
                          })
                          $form.query('state').get('data').souProjectList = optionData
                          $self.dataSource = optionData
                        })
                      }`)},"x-reactions":[expression(`(field) => {
                        let parma = {
                          souType: $attrs.params.souType,
                          pageSize: 999,
                          pageNum: 1
                        }
                        souQuestionApi.getSouProjectList(parma).then(res => {
                          let attr = res.data.list || []
                          let optionData = []
                          attr.forEach(item => {
                            const objs = {
                              id: (item.projectId).toString(),
                              no: item.souNo,
                              value: (item.projectId).toString(),
                              label: item.souName
                            }
                            optionData.push(objs)
                          })
                          $form.query('state').get('data').souProjectList = optionData
                          $self.dataSource = optionData
                        })
                      }`)]},souName:{type:"string",title:i18nExpression("bidMod.bidingName"),"x-hidden":"{{ $vendor() }}","x-decorator":"FormItem","x-component-props":{disabled:!0}},souNo:{type:"string",title:i18nExpression("bidMod.bidingNum"),"x-decorator":"FormItem","x-component-props":{disabled:!0}}}}}},questionContentDetail:{type:"void","x-query-engine-skip":!0,"x-component":"CollapseItem","x-component-props":{title:i18nExpression("bidMod.questionContent")},properties:{questionContent:{type:"string","x-component":"Tinymce","x-component-props":{id:"questionTinymce",height:460,"@setup":expression(`(editorInstance) => {
                    this.answerContentEditor = editorInstance
                    if ($attrs.params.flag == 'view') {
                      editorInstance.setMode('readonly')
                    } else {
                      editorInstance.setMode('design')
                    }
                    $self.setData({
                      editorInstance: $markRaw(editorInstance)
                    })
                  }`)}}}},relevantAttachment:{type:"void","x-query-engine-skip":!0,"x-component":"CollapseItem","x-component-props":{title:i18nExpression("bidMod.attachment")},properties:{sceneFiles:{type:"array","x-query-engine-relation":"sceneFiles:*","x-component":"FileDynamic","x-component-props":{primaryKey:"sceneFileId",cascadeDeletion:!0,"scene-module-code":"SCENE_SOU_QUESTION_FILE_ATTACHMENT","business-id":expression("$form.query('.questionId').get('value')"),editable:expression("$attrs.params.flag != 'view'"),"need-init":!1}}}}}}},items:{type:"object",properties:{goBack:{type:"void","x-content":expression("$t($readOnly ? 'common.backTo' : 'components.common.cancel')"),"x-component":"Button","x-component-props":{type:"default","@click":expression(`async (values) => {
              if($readOnly){
                emitTabRemove($attrs.tabName)
                return
              }
              app.$confirm($t('outsource.goBackConfirm'), $t('common.tips'), {
                confirmButtonText: $t('common.confirm'),
                cancelButtonText: $t('common.cancel'),
                type: 'warning'
              }).then(() => {
                $bus.$emit('questionList')
                emitTabRemove($attrs.tabName)
              }).catch(() => {
              });
            }`)}},save:{type:"void","x-content":i18nExpression("common.staging"),"x-component":"Button","x-hidden":"{{ $buyer()}}","x-component-props":{type:"default",plain:"plain","@submit":expression(`async (values) => {
              $queryEngine.request.save({
                ...values,
                projectId: Number(values.projectId),
                souType: $attrs.params.souType,
                questionStatus: values.questionStatus || 'DRAFT',
              }).then(() => {
                $message.success($t('common.successSave'))
                $bus.$emit('questionList')
                emitTabRemove($attrs.tabName)
              })
            }`)}},publish:{type:"void","x-content":i18nExpression("common.publish"),"x-component":"Button","x-hidden":"{{ $buyer()}}","x-component-props":{"@submit":expression(`async (values) => {
              $queryEngine.request.save({
                ...values,
                projectId: Number(values.projectId),
                souType: $attrs.params.souType,
                questionStatus: 'SUBMITTED'
              },{
                customizeAction:'submit'
              }).then(() => {
                $message.success($t('common.successSave'))
                $bus.$emit('questionList')
                emitTabRemove($attrs.tabName)
              })
            }`)}}}}}});return{__sfc:!0,emitTabRemove,emitTabAdd,t,app,attrs,questionDetailSchema,scope:{souQuestionApi,emitTabRemove,app,$markRaw:markRaw},components:{Tinymce,FileDynamic},RenderEngine}}});var _sfc_render$4=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{pageAttrs:_vm.$attrs,schema:_setup.questionDetailSchema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$4=[],__component__$4=normalizeComponent(_sfc_main$4,_sfc_render$4,_sfc_staticRenderFns$4,!1,null,null,null,null);const QuestionDetail=__component__$4.exports,_sfc_main$3=defineComponent({__name:"answerDetail",setup(__props){const{emitTabRemove,emitTabAdd,t,app}=usePageHelper();let attrs=useAttrs();const questionDetailSchema=defineSchemas({state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{souProjectList:[],questionContentEditor:null}},SouQuestion:{type:"void","x-component":"FormContainer","x-decorator":"QueryEngine","x-query-engine":{service:"sou",type:"SouAnswer",actions:{read:{immediate:!0,ready:expression(`() => {
            $form.readPretty = $readOnly
            let id = $attrs.params.row.answerId
            $form.values.answerId = id
            if ($attrs.params.flag === 'clarifyFormQuestion'){
              $form.values.projectId = $attrs.params.row.projectId
              $form.values.souNo = $attrs.params.row.souNo
              $form.values.souName = $attrs.params.row.souName
              $form.values.questionId = $attrs.params.row.questionId
              $form.values.questionNum = $attrs.params.row.questionNum
            }
            return !!id
          }`),transformRequest:expression(`(data,headers) => {
            data.payload = [$form.values.answerId]
          }`),onSuccess:expression(`(res) => {
            console.log('res::',res)
            const value = res.data[0]
            $form.setValues({
              ...value
            })
            $form.query('sceneFiles').take(field => {
              field.componentProps.componentInstance.reLoadFileInfo()
            })
          }`)},save:{cascadeDeletion:!0}}},properties:{collapse:{type:"void","x-component":"Collapse","x-component-props":{defaultOpenPanelCount:1},properties:{receiptInfo:{type:"void","x-query-engine-skip":!0,"x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.receiptInfo")},properties:{orderInfo:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:4,columnGap:32,rowGap:0},properties:{answerId:{type:"string","x-hidden":!0},answerStatus:{type:"string","x-hidden":!0},answerNum:{type:"string",title:i18nExpression("bidMod.answerNum"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},answerTitle:{type:"string",title:i18nExpression("bidMod.answerTitle"),required:!0,"x-decorator":"FormItem"},questionNum:{type:"string",title:i18nExpression("bidMod.questionNum"),"x-hidden":!0},souName:{type:"string",title:i18nExpression("bidMod.bidingName"),"x-decorator":"FormItem","x-component":"QuickSearchWrapper","x-component-props":{readPretty:"{{$form.readPretty}}",showKey:"souName",propKey:"souNo",preQueryData:'{{{ "t.sou_type": $attrs.params.souType }}}',name:"scc_sou_project",disabled:'{{ $vendor() || $attrs.params.flag == "clarifyFormQuestion" }}',"@close-quicksearch":expression(`(value) => {
                        if (value) {
                          $form.values.projectId = value ? value.projectId : ''
                          $form.values.souNo = value ? value.souNo : ''
                          $form.values.souName = value ? value.souName : ''
                        } else {
                          $form.values.projectId = ''
                          $form.values.souName = ''
                          $form.values.souNo = ''
                        }
                      }`),...requiredValidatorSegment}},projectId:{type:"string",title:i18nExpression("bidMod.bidingName"),"x-hidden":!0,"x-decorator":"FormItem","x-component-props":{disabled:!0}},souNo:{type:"string",title:i18nExpression("bidMod.bidingNum"),"x-decorator":"FormItem","x-component-props":{disabled:!0}}}}}},answerContentDetail:{type:"void","x-query-engine-skip":!0,"x-component":"CollapseItem","x-component-props":{title:i18nExpression("bidMod.questionContent")},properties:{answerContent:{type:"string","x-component":"Tinymce","x-component-props":{id:"answerTinymce",height:460,"@setup":expression(`(editorInstance) => {
                    this.questionContentEditor = editorInstance
                    if ($attrs.params.flag == 'view') {
                      editorInstance.setMode('readonly')
                    } else {
                      editorInstance.setMode('design')
                    }
                    $self.setData({
                      editorInstance: $markRaw(editorInstance)
                    })
                  }`)}}}},relevantAttachment:{type:"void","x-query-engine-skip":!0,"x-component":"CollapseItem","x-component-props":{title:i18nExpression("bidMod.attachment")},properties:{sceneFiles:{type:"array","x-query-engine-relation":"sceneFiles:*","x-component":"FileDynamic","x-component-props":{primaryKey:"sceneFileId",cascadeDeletion:!0,"scene-module-code":"SCENE_SOU_QUESTION_FILE_ATTACHMENT","business-id":expression("$form.query('.answerId').get('value')"),editable:expression("$attrs.params.flag != 'view'"),"need-init":!1}}}}}}},items:{type:"object",properties:{goBack:{type:"void","x-content":expression("$t($readOnly ? 'common.backTo' : 'components.common.cancel')"),"x-component":"Button","x-component-props":{type:"default","@click":expression(`async (values) => {
              if($readOnly){
                emitTabRemove($attrs.tabName)
                return
              }
              app.$confirm($t('outsource.goBackConfirm'), $t('common.tips'), {
                confirmButtonText: $t('common.confirm'),
                cancelButtonText: $t('common.cancel'),
                type: 'warning'
              }).then(() => {
                $bus.$emit('answerList')
                emitTabRemove($attrs.tabName)
              }).catch((err) => {
                console.log(err)
              });
            }`)}},save:{type:"void","x-content":i18nExpression("common.staging"),"x-component":"Button","x-hidden":"{{ $vendor()}}","x-component-props":{type:"default",plain:"plain","@submit":expression(`async (values) => {
              $queryEngine.request.save({
                ...values,
                projectId: Number(values.projectId),
                souType: $attrs.params.souType,
                answerStatus: values.answerStatus || 'DRAFT',
              }).then(() => {
                $message.success($t('common.successSave'))
                $bus.$emit('answerList')
                emitTabRemove($attrs.tabName)
              })
            }`)}},publish:{type:"void","x-content":i18nExpression("common.publish"),"x-component":"Button","x-hidden":"{{ $vendor()}}","x-component-props":{"@submit":expression(`async (values) => {
              $queryEngine.request.save({
                ...values,
                projectId: Number(values.projectId),
                souType: $attrs.params.souType,
                answerStatus: 'SUBMITTED'
              },{
                customizeAction:'publish'
              }).then(() => {
                $message.success($t('common.successSave'))
                $bus.$emit('answerList')
                emitTabRemove($attrs.tabName)
              })
            }`)}}}}}});return{__sfc:!0,emitTabRemove,emitTabAdd,t,app,attrs,questionDetailSchema,scope:{souQuestionApi,emitTabRemove,app,$markRaw:markRaw},components:{Tinymce,FileDynamic},RenderEngine}}});var _sfc_render$3=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"answerDetail",pageAttrs:_vm.$attrs,schema:_setup.questionDetailSchema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$3=[],__component__$3=normalizeComponent(_sfc_main$3,_sfc_render$3,_sfc_staticRenderFns$3,!1,null,null,null,null);const AnswerDetail=__component__$3.exports,_sfc_main$2=defineComponent({__name:"questionList",setup(__props){const{emitTabAdd,t}=usePageHelper();let attrs=useAttrs();const $qEdit=(row,flag)=>{let name,title;flag==="add"?(name="questionDetail",title=i18nExpression("bidMod.addChallenge")):["edit"].includes(flag)?(name="questionDetail"+row.questionNum||row.questionId,title=row.questionNum):(name="questionDetail"+row.questionNum,title=row.questionNum);let tab={component:QuestionDetail,params:{row,flag,tabName:name,souType:attrs.params.souType},title,name};emitTabAdd(tab)},$aEdit=(row,flag)=>{let name,title;flag==="add"?(name="answerDetail",title=i18nExpression("bidMod.addClarification")):flag==="clarifyFormQuestion"?(name="answerDetail"+row.questionNum,title=row.questionNum+"-"+i18nExpression("bidMod.addClarification")):flag==="edit"?(name="answerDetail"+row.answerNum||row.answerId,title=row.answerNum):(name="answerDetail"+row.answerNum,title=row.answerNum);let tab={component:AnswerDetail,params:{row,flag,tabName:name,souType:attrs.params.souType},title,name};emitTabAdd(tab)},schema=defineSchemas({state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{}},question:{type:"void","x-decorator":"QueryEngine","x-decorator-props":{style:"height:auto"},"x-component":"el-container","x-component-props":{class:"flex-container the_dictionary_wrapper",direction:"vertical"},"x-query-engine":{service:"sou",type:"SouQuestion",actions:{paginationQuery:{immediate:!0,transformRequest:expression(`(data, headers) => {
            data.query = {
              '*': {}
            }
            let filter =  data.payload.filter || {}
            data.payload = {
              "page": {
                sort: "creationDate desc"
              },
              "filter": {
                ...filter,
                'souType': {eq: $attrs.params.souType }
              }
            }
            return data
          }`)}}},properties:{query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine","x-component-props":{"@query":expression(`(val) => {
            $queryEngine.state.paginationManagement.refresh()
          }`)},properties:generateXindexInOrder({souNo:{type:"string",title:"{{$t('bidMod.bidingNum')}}","x-query-engine-query-operator":"contains"},souName:{type:"string",title:"{{$t('bidMod.bidingName')}}","x-query-engine-query-operator":"contains"},questionTitle:{type:"string",title:"{{$t('bidMod.questionTitle')}}","x-query-engine-query-operator":"contains"},questionNum:{type:"string",title:"{{$t('bidMod.questionNum')}}","x-query-engine-query-operator":"contains"},questionStatus:{type:"string",title:"{{$t('bidMod.questionStatus')}}","x-component":"DictSelect","x-component-props":{code:"QUERY_STATUS"}},answerTitle:{type:"string",title:"{{$t('bidMod.answerTitle')}}","x-query-engine-query-operator":"contains"},answerNum:{type:"string",title:"{{$t('bidMod.answerNum')}}","x-query-engine-query-operator":"contains"},answerStatus:{type:"string",title:"{{$t('bidMod.answerStatus')}}","x-component":"DictSelect","x-component-props":{code:"CLARIFIED_STATUS"}}})},SouQuestion:{type:"void",properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"questionList","@listener":expression(`() => {
                $queryEngine.state.paginationManagement.refresh()
              }`)}},toolbarQuestion:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},properties:{add:{type:"void",title:"{{$t('common.add')}}","x-component":"RButton","x-hidden":"{{ $buyer() }}","x-component-props":{type:"primary","@click":'{{() => $qEdit({}, "add")}}'}}}},tableQuestion:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",style:"flex: 1",preColumns:"seq",openCustomTable:!0},properties:generateXindexInOrder({questionId:{type:"string","x-hidden":!0,"x-query-engine-primary-key":!0},creationDate:{type:"string","x-hidden":!0,"x-query-engine-primary-key":!0},lastUpdateDate:{type:"string","x-query-engine-sort":"desc","x-hidden":!0,"x-query-engine-primary-key":!0},questionNum:{type:"string","x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({ row }) => {
                    let optFlag = row.questionStatus == 'DRAFT' ? 'edit' : 'view'
                    $qEdit(row, optFlag)
                  }`)},"x-render-table-column":{title:"{{$t('bidMod.questionNum')}}",minWidth:150,customRender:!0}},questionTitle:{type:"string",title:"{{$t('bidMod.questionTitle')}}","x-render-table-column":{minWidth:150}},questionStatus:{type:"string",title:"{{$t('bidMod.questionStatus')}}","x-component":"DictSelect","x-component-props":{code:"QUERY_STATUS"},"x-render-table-column":{width:110}},souNo:{type:"string",title:"{{$t('bidMod.bidingNum')}}","x-render-table-column":{minWidth:150}},souName:{type:"string",title:"{{$t('bidMod.bidingName')}}","x-render-table-column":{minWidth:150}},submitTime:{title:"{{$t('bidMod.qa.submitTimeBuyer')}}",...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                    parseTime(row.submitTime, '{y}-{m}-{d}')
                  }`)},"x-render-table-column":{width:100}},vendorName:{type:"string",title:"{{$t('bidMod.qa.qaSource')}}","x-render-table-column":{minWidth:150}},rejectReason:{type:"string",title:"{{$t('bidMod.rejectReason')}}","x-render-table-column":{minWidth:150}},createdFullName:{type:"string",title:"{{$t('common.creator')}}","x-render-table-column":{minWidth:100}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:204,fixed:"right"},"x-component":"RenderTableButtonList",properties:{edit:{type:"void",title:"{{$t('common.edit')}}","x-reactions":changeFieldVisibleByDeps([".questionStatus"],"($vendor() && ['DRAFT'].includes($deps[0]))"),"x-component-props":{"@click":expression('({ row }) => $qEdit(row, "edit")')}},delete:{type:"void",title:"{{$t('common.delete')}}","x-reactions":changeFieldVisibleByDeps([".questionStatus"],"($vendor() && ['DRAFT'].includes($deps[0]))"),"x-component-props":{popconfirm:{title:i18nExpression("common.confirmDeleteRow")},"@click":expression(`({ row }) => {
                        let obj = {
                          questionId: row.questionId,
                          souType: $attrs.params.souType
                        }
                        $queryEngine.request.baseRequest({
                          type: "SouQuestion",
                          action: "delete",
                          service: 'sou',
                          loading: true,
                          payload: [{
                            ...obj
                          }],
                          query: { "*": {}}
                        }).then((res) => {
                          $queryEngine.state.paginationManagement.refresh()
                        }).catch((err) => {
                          $message.error(err.message)
                        })
                      }`)}},withdraw:{type:"void",title:"{{$t('bidMod.withdraw')}}","x-reactions":changeFieldVisibleByDeps([".questionStatus"],"($vendor() && ['SUBMITTED'].includes($deps[0]))"),"x-component-props":{"@click":expression(`({ row }) => {
                        let obj = {
                          questionId: row.questionId,
                          souType: $attrs.params.souType
                        }
                        $queryEngine.request.baseRequest({
                          type: "SouQuestion",
                          action: "withDraw",
                          service: 'sou',
                          loading: true,
                          payload: [{
                            ...obj
                          }],
                          query: { "*": {}}
                        }).then((res) => {
                          $queryEngine.state.paginationManagement.refresh()
                        }).catch((err) => {
                          $message.error(err.message)
                        })
                      }`)}},clarification:{type:"void",title:"{{$t('bidMod.clarification')}}","x-reactions":changeFieldVisibleByDeps([".questionStatus"],"($buyer() && !['CLARIFIED'].includes($deps[0]))"),"x-component-props":{"@click":expression('({ row }) => $aEdit(row, "clarifyFormQuestion")')}},toRefuse:{type:"void",title:"{{$t('common.toRefuse')}}","x-reactions":changeFieldVisibleByDeps([".questionStatus"],"($buyer() && ['SUBMITTED'].includes($deps[0]))"),"x-component-props":{"@click":expression(`({ row }) => {
                        $prompt($t('bidMod.msgRejectReason'), $t('common.toRefuse'), {
                          confirmButtonText: $t('common.confirm'),
                          cancelButtonText: $t('common.cancel'),
                          inputValidator: (value) => !(!value || value.length > 200),
                          inputErrorMessage: $t('bidMod.biddingManagementBuyer.rejectReason')
                        }).then(({ value }) => {
                          let obj = {
                            questionId: row.questionId,
                            souType: $attrs.params.souType,
                            rejectReason: value
                          }
                          $queryEngine.request.baseRequest({
                            type: "SouQuestion",
                            action: "reject",
                            service: 'sou',
                            loading: true,
                            payload: [{
                              ...obj
                            }],
                            query: { "*": {}}
                          }).then((res) => {
                            $queryEngine.state.paginationManagement.refresh()
                          }).catch((err) => {
                            $message.error(err.message)
                          })
                        }).then(() => {
                          $message.success($t('bidMod.successRefuse'))
                        }).catch((err) => {
                          console.error('approvalRefuse', err)
                        })
                      }`)}}}}})}}}}}});return{__sfc:!0,emitTabAdd,t,attrs,$qEdit,$aEdit,schema,scope:{souQuestionApi,$qEdit,$aEdit,$markRaw:markRaw},components:{},RenderEngine}}});var _sfc_render$2=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"questionList",pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$2=[],__component__$2=normalizeComponent(_sfc_main$2,_sfc_render$2,_sfc_staticRenderFns$2,!1,null,null,null,null);const QuestionList=__component__$2.exports,_sfc_main$1=defineComponent({__name:"answerList",setup(__props){const{emitTabAdd,t}=usePageHelper();let attrs=useAttrs();const $qEdit=(row,flag)=>{let name,title;flag==="add"?(name="questionDetail",title=i18nExpression("bidMod.addChallenge")):["edit"].includes(flag)?(name="questionDetail"+row.questionNum||row.questionId,title=row.questionNum):(name="questionDetail"+row.questionNum,title=row.questionNum);let tab={component:QuestionDetail,params:{row,flag,tabName:name,souType:attrs.params.souType},title,name};emitTabAdd(tab)},$aEdit=(row,flag)=>{let name,title;flag==="add"?(name="answerDetail",title=i18nExpression("bidMod.addClarification")):["edit"].includes(flag)?(name="answerDetail"+row.answerNum||row.answerId,title=row.answerNum):(name="answerDetail"+row.answerNum,title=row.answerNum);let tab={component:AnswerDetail,params:{row,flag,tabName:name,souType:attrs.params.souType},title,name};emitTabAdd(tab)},schema=defineSchemas({state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{}},answer:{type:"void","x-decorator":"QueryEngine","x-decorator-props":{style:"height:auto"},"x-component":"el-container","x-component-props":{class:"flex-container the_dictionary_wrapper",direction:"vertical"},"x-query-engine":{service:"sou",type:"SouAnswer",actions:{paginationQuery:{immediate:!0,transformRequest:expression(`(data, headers) => {
            data.query = {
              '*': {}
            }
            let filter =  data.payload.filter || {}
            data.payload = {
              "page": {
                sort: "creationDate desc"
              },
              "filter": {
                ...filter,
                'souType': {eq: $attrs.params.souType }
              }
            }
            return data
          }`)}}},properties:{query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine","x-component-props":{"@query":expression(`(val) => {
            $queryEngine.state.paginationManagement.refresh()
          }`)},properties:generateXindexInOrder({souNo:{type:"string",title:"{{$t('bidMod.bidingNum')}}","x-query-engine-query-operator":"contains"},souName:{type:"string",title:"{{$t('bidMod.bidingName')}}","x-query-engine-query-operator":"contains"},questionTitle:{type:"string",title:"{{$t('bidMod.questionTitle')}}","x-query-engine-query-operator":"contains"},questionNum:{type:"string",title:"{{$t('bidMod.questionNum')}}","x-query-engine-query-operator":"contains"},questionStatus:{type:"string",title:"{{$t('bidMod.questionStatus')}}","x-component":"DictSelect","x-component-props":{code:"QUERY_STATUS"}},answerTitle:{type:"string",title:"{{$t('bidMod.answerTitle')}}","x-query-engine-query-operator":"contains"},answerNum:{type:"string",title:"{{$t('bidMod.answerNum')}}","x-query-engine-query-operator":"contains"},answerStatus:{type:"string",title:"{{$t('bidMod.answerStatus')}}","x-component":"DictSelect","x-component-props":{code:"CLARIFIED_STATUS"}}})},SouAnswer:{type:"void",properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"answerList","@listener":expression(`() => {
                $queryEngine.state.paginationManagement.refresh()
              }`)}},toolbarAnswer:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},properties:{add:{type:"void",title:"{{$t('common.add')}}","x-component":"RButton","x-hidden":"{{ $vendor() }}","x-component-props":{type:"primary","@click":'{{() => $aEdit({}, "add")}}'}}}},tableAnswer:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",style:"flex: 1",preColumns:"seq",openCustomTable:!0},properties:generateXindexInOrder({answerId:{type:"string","x-hidden":!0,"x-query-engine-primary-key":!0},creationDate:{type:"string","x-hidden":!0,"x-query-engine-primary-key":!0},lastUpdateDate:{type:"string","x-query-engine-sort":"desc","x-hidden":!0,"x-query-engine-primary-key":!0},answerNum:{type:"string","x-component":"TableButton","x-component-props":{type:"text","@click":expression('({ row }) => $aEdit(row, "view")')},"x-render-table-column":{title:"{{$t('bidMod.answerNum')}}",minWidth:150,customRender:!0}},answerTitle:{type:"string",title:"{{$t('bidMod.answerTitle')}}","x-render-table-column":{minWidth:150}},answerStatus:{type:"string",title:"{{$t('bidMod.answerStatus')}}","x-component":"DictSelect","x-component-props":{code:"CLARIFIED_STATUS"},"x-render-table-column":{width:110}},questionId:{type:"string","x-hidden":!0},questionNum:{type:"string","x-component":"TableButton","x-component-props":{type:"text","@click":expression('({ row }) => $qEdit(row, "view")')},"x-render-table-column":{title:"{{$t('bidMod.questionNum')}}",minWidth:150,customRender:!0}},souNo:{type:"string",title:"{{$t('bidMod.bidingNum')}}","x-render-table-column":{minWidth:150}},souName:{type:"string",title:"{{$t('bidMod.bidingName')}}","x-render-table-column":{minWidth:150}},submitTime:{title:"{{$t('bidMod.qa.submitTimeBuyer')}}",...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                    parseTime(row.submitTime, '{y}-{m}-{d}')
                  }`)},"x-render-table-column":{width:100}},createdFullName:{type:"string",title:"{{$t('common.creator')}}","x-render-table-column":{minWidth:100}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:204,fixed:"right"},"x-component":"RenderTableButtonList",properties:{edit:{type:"void",title:"{{$t('common.edit')}}","x-reactions":changeFieldVisibleByDeps([".answerStatus"],"($buyer() && ['DRAFT'].includes($deps[0]))"),"x-component-props":{"@click":expression('({ row }) => $aEdit(row, "edit")')}},delete:{type:"void",title:"{{$t('common.delete')}}","x-reactions":changeFieldVisibleByDeps([".answerStatus"],"($buyer() && ['DRAFT'].includes($deps[0]))"),"x-component-props":{popconfirm:{title:i18nExpression("common.confirmDeleteRow")},"@click":expression(`({ row }) => {
                        let obj = {
                          answerId: row.answerId,
                          souType: $attrs.params.souType
                        }
                        $queryEngine.request.baseRequest({
                          type: "SouAnswer",
                          action: "delete",
                          service: 'sou',
                          loading: true,
                          payload: [{
                            ...obj
                          }],
                          query: { "*": {}}
                        }).then((res) => {
                          $queryEngine.state.paginationManagement.refresh()
                        }).catch((err) => {
                          $message.error(err.message)
                        })
                      }`)}},publish:{type:"void",title:"{{$t('common.publish')}}","x-reactions":changeFieldVisibleByDeps([".answerStatus"],"($buyer() && ['DRAFT'].includes($deps[0]))"),"x-component-props":{"@click":expression(`({ row }) => {
                        let obj = {
                          answerId: row.answerId,
                          souType: $attrs.params.souType
                        }
                        $queryEngine.request.baseRequest({
                          type: "SouAnswer",
                          action: "publish",
                          service: 'sou',
                          loading: true,
                          payload: [{
                            ...obj
                          }],
                          query: { "*": {}}
                        }).then((res) => {
                          $queryEngine.state.paginationManagement.refresh()
                        }).catch((err) => {
                          $message.error(err.message)
                        })
                      }`)}},withdraw:{type:"void",title:"{{$t('bidMod.withdraw')}}","x-reactions":changeFieldVisibleByDeps([".answerStatus"],"($buyer() && ['ISSUED'].includes($deps[0]))"),"x-component-props":{"@click":expression(`({ row }) => {
                        let obj = {
                          answerId: row.answerId,
                          souType: $attrs.params.souType
                        }
                        $queryEngine.request.baseRequest({
                          type: "SouAnswer",
                          action: "withdraw",
                          service: 'sou',
                          loading: true,
                          payload: [{
                            ...obj
                          }],
                          query: { "*": {}}
                        }).then((res) => {
                          $queryEngine.state.paginationManagement.refresh()
                        }).catch((err) => {
                          $message.error(err.message)
                        })
                      }`)}},acceptClarify:{type:"void",title:"{{$t('bidMod.acceptClarify')}}","x-reactions":changeFieldVisibleByDeps([".answerStatus"],"($vendor() && ['ISSUED'].includes($deps[0]))"),"x-component-props":{"@click":expression(`({ row }) => {
                        let obj = {
                          answerId: row.answerId,
                          souType: $attrs.params.souType
                        }
                        $queryEngine.request.baseRequest({
                          type: "SouAnswer",
                          action: "vendorAccept",
                          service: 'sou',
                          loading: true,
                          payload: [{
                            ...obj
                          }],
                          query: { "*": {}}
                        }).then((res) => {
                          $queryEngine.state.paginationManagement.refresh()
                        }).catch((err) => {
                          $message.error(err.message)
                        })
                      }`)}}}}})}}}}}});return{__sfc:!0,emitTabAdd,t,attrs,$qEdit,$aEdit,schema,scope:{souQuestionApi,$qEdit,$aEdit,$markRaw:markRaw},components:{},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"answerList",pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const AnswerList=__component__$1.exports,_sfc_main={name:"QaList",components:{NavTabs},props:{souType:{type:String,required:!0},quickSearchCode:{type:String,default:""}},data(){return{historyTabName:"QuestionList",activeTab:"QuestionList",tabs:[{title:this.$t("bidMod.challengeList"),name:"QuestionList",component:QuestionList,closable:!1,params:{souType:this.souType,quickSearchCode:this.quickSearchCode}},{title:this.$t("bidMod.clarificationList"),name:"AnswerList",component:AnswerList,closable:!1,params:{souType:this.souType,quickSearchCode:this.quickSearchCode}}]}},watch:{activeTab(_newVal,oldVal){["QuestionList","AnswerList"].includes(oldVal)&&(this.historyTabName=oldVal)}},methods:{tabChange(tab){this.activeTab=tab,this.tabs=this.$refs.tabs.tabs},tabRemove({activeTab}){["listEngine","AnswerList"].includes(activeTab)&&(this.$refs.tabs.activeTab=this.historyTabName)}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab},on:{"tab-change":_vm.tabChange,"tab-remove":_vm.tabRemove}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const Qa=__component__.exports;export{Qa as Q};
