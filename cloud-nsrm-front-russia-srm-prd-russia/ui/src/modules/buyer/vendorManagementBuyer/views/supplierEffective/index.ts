import { defineLowCodeDesignerPage } from 'lib@/components/render-engine/ide-mini-sdk'
import { accessProcessProcessing } from './accessProcessProcessing'

export default defineLowCodeDesignerPage(
  {
    "form": {
      "labelCol": 6,
      "wrapperCol": 24,
      "colon": false,
      "feedbackLayout": "loose",
      "size": "default",
      "layout": "horizontal",
      "tooltipLayout": "icon",
      "labelAlign": "right",
      "wrapperAlign": "left",
      "shallow": false,
      "bordered": true,
      "style": {
        "opacity": 1
      },
      "x-decorator-props": {
        "style": {
          "opacity": 1
        }
      },
      "x-designer-extend": {
        "events": []
      },
      "events": {},
      "x-validator": []
    },
    "schema": {
      "type": "void",
      "properties": {
        "effectForm": {
          "type": "void",
          "x-component": "QueryEngine",
          "x-query-engine": {
            "service": "api-sup",
            "actions": {
              "paginationQuery": {
                "autoFormatResult": true,
                "immediate": true
              }
            },
            "pagination": {
              "pageSize": 15
            }
          },
          "properties": {
            "formTab": {
              "type": "void",
              "x-component": "FormTab",
              "x-index": 0,
              "x-designable-id": "wcyne8a8yag",
              "x-component-props": {
                "type": "border-card",
                "style": {
                  "opacity": 1
                }
              },
              "name": "formTab",
              "x-decorator-props": {
                "style": {
                  "opacity": 1
                }
              },
              "x-designer-extend": {},
              "x-reactions": {
                "dependencies": [],
                "fulfill": {
                  "state": {
                    "componentProps": "{{{ \n  ...$self.componentProps,\n  class: 'tabs--list-page',\n}}}"
                  }
                }
              },
              "properties": {
                "tab_1": {
                  "type": "void",
                  "x-component": "FormTab.TabPane",
                  "x-component-props": {
                    "label": "供方生效单据",
                    "style": {
                      "opacity": 1
                    }
                  },
                  "x-index": 0,
                  "name": "tab_1",
                  "x-designable-id": "xgzr21unwqc",
                  "x-decorator-props": {
                    "style": {
                      "opacity": 1
                    }
                  },
                  "x-designer-extend": {},
                  "x-reactions": {
                    "dependencies": [],
                    "fulfill": {
                      "state": {
                        "componentProps": "{{{ \n  ...$self.componentProps,\n  style: { \n    display: 'flex',\n    flexDirection: 'column',\n  },\n  class: 'aaa'\n}}}"
                      }
                    }
                  },
                  "properties": {
                    "uggr4r09267": {
                      "type": "object",
                      "x-component": "QueryFormByQueryEngine",
                      "x-component-props": {
                        "minWidth": 100,
                        "minColumns": 0,
                        "maxColumns": 3,
                        "columnGap": 10,
                        "rowGap": 5,
                        "colWrap": true,
                        "labelWidth": 80,
                        "immediateQueryForm": false,
                        "colon": false,
                        "style": {
                          "opacity": 1
                        }
                      },
                      "x-decorator-props": {
                        "style": "padding: 20px 20px 0;"
                      },
                      "x-index": 0,
                      "name": "uggr4r09267",
                      "x-designable-id": "vua3fjws4zk",
                      "x-validator": [],
                      "x-designer-extend": {},
                      "properties": {
                        "effectFormNumber": {
                          "type": "string",
                          "title": "供方生效单号",
                          "x-decorator": "FormItem",
                          "x-decorator-props": {
                            "labelWidth": "82px",
                            "style": {
                              "opacity": 1
                            },
                            "feedbackLayout": "loose"
                          },
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupEffectForm",
                              "reference": null,
                              "modelId": "1668183594803449858",
                              "field": "effectFormNumber",
                              "originalField": "effectFormNumber"
                            },
                            "events": []
                          },
                          "x-component": "Input",
                          "x-index": 0,
                          "name": "effectFormNumber",
                          "x-designable-id": "joq26uwggrk",
                          "x-validator": [],
                          "x-component-props": {
                            "style": {
                              "opacity": 1
                            }
                          }
                        },
                        "quaReviewType": {
                          "title": "资质审查类型",
                          "x-decorator": "FormItem",
                          "x-component": "Select",
                          "name": "quaReviewType",
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupEffectForm",
                              "reference": null,
                              "modelId": "1668183594803449858",
                              "field": "quaReviewType",
                              "originalField": "quaReviewType"
                            },
                            "source": {
                              "sourceType": "MQL",
                              "action": "query",
                              "type": "base_dict_item_ide",
                              "query": {
                                "*": {}
                              },
                              "filter": {
                                "$and": {
                                  "dictId": {
                                    "eq": "6763956529790976"
                                  }
                                }
                              },
                              "service": "api-base",
                              "payload": {
                                "page": {
                                  "sort": "dictItemNo asc"
                                }
                              },
                              "enum": [],
                              "conditions": [
                                [
                                  {
                                    "dataName": "dictId",
                                    "comparison": "eq",
                                    "valueType": "fixed",
                                    "modelField": "6763956529790976",
                                    "modelId": ""
                                  }
                                ]
                              ]
                            },
                            "events": []
                          },
                          "x-validator": [],
                          "x-component-props": {
                            "size": "small",
                            "multiple-limit": 0,
                            "placeholder": "请选择",
                            "style": {
                              "opacity": 1
                            },
                            "fieldNames": {
                              "label": "dictItemName",
                              "value": "dictItemCode"
                            },
                            "@created": "{{() => {\n                    $queryEngine.request.baseRequest({\n                      type: 'base_dict_item_ide',\n                      action: 'query',\n                      payload: {\n                        filter: {\"$and\":{\"dictId\":{\"eq\":\"6763956529790976\"}}},\n                        page: {\"pageSize\":9999,\"pageNum\":1,\"sort\":\"dictItemNo asc\"},\n                      },\n                      query: {\"*\":{}},\n                      actionConfig: { service: 'api-base' },\n                      queryConfig: { staleTime: 300000 }\n                    }).then(res => {\n                      $self.dataSource = res.data || []\n                    })\n                  }}}"
                          },
                          "enum": [],
                          "x-decorator-props": {
                            "style": {
                              "opacity": 1
                            },
                            "feedbackLayout": "loose",
                            "labelWidth": "82px"
                          },
                          "x-designable-id": "0kf7l3hnst0",
                          "x-index": 1
                        },
                        "approveStatus": {
                          "title": "审批状态",
                          "x-decorator": "FormItem",
                          "x-component": "Select",
                          "name": "approveStatus",
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sup_effect_form_ide",
                              "reference": null,
                              "modelId": "1668183594803449858",
                              "field": "approveStatus",
                              "originalField": "approveStatus"
                            },
                            "source": {
                              "sourceType": "MQL",
                              "action": "query",
                              "type": "base_dict_item_ide",
                              "query": {
                                "*": {}
                              },
                              "filter": {
                                "$and": {
                                  "dictId": {
                                    "eq": "6805186166587392"
                                  }
                                }
                              },
                              "service": "api-base",
                              "payload": {
                                "page": {
                                  "sort": "dictItemNo asc"
                                }
                              },
                              "enum": [],
                              "conditions": [
                                [
                                  {
                                    "dataName": "dictId",
                                    "comparison": "eq",
                                    "valueType": "fixed",
                                    "modelField": "6805186166587392",
                                    "modelId": ""
                                  }
                                ]
                              ]
                            },
                            "events": []
                          },
                          "x-validator": [],
                          "x-component-props": {
                            "size": "small",
                            "multiple-limit": 0,
                            "placeholder": "请选择",
                            "style": {
                              "opacity": 1
                            },
                            "fieldNames": {
                              "label": "dictItemName",
                              "value": "dictItemCode"
                            },
                            "@created": "{{() => {\n                    $queryEngine.request.baseRequest({\n                      type: 'base_dict_item_ide',\n                      action: 'query',\n                      payload: {\n                        filter: {\"$and\":{\"dictId\":{\"eq\":\"6805186166587392\"}}},\n                        page: {\"pageSize\":9999,\"pageNum\":1,\"sort\":\"dictItemNo asc\"},\n                      },\n                      query: {\"*\":{}},\n                      actionConfig: { service: 'api-base' },\n                      queryConfig: { staleTime: 300000 }\n                    }).then(res => {\n                      $self.dataSource = res.data || []\n                    })\n                  }}}"
                          },
                          "enum": [],
                          "x-decorator-props": {
                            "style": {
                              "opacity": 1
                            },
                            "feedbackLayout": "loose"
                          },
                          "x-designable-id": "psz4pyk4se7",
                          "x-index": 2
                        },
                        "companyName": {
                          "title": "供应商名称",
                          "x-decorator": "FormItem",
                          "x-component": "Selector",
                          "name": "companyName",
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupEffectForm",
                              "reference": {
                                "modelName": "公司基本信息",
                                "referenceField": "companyId",
                                "modelId": "1668157712692989956",
                                "businessType": "sccSupCompanyInfo"
                              },
                              "modelId": "1668183594803449858",
                              "field": "companyName",
                              "originalField": "companyName"
                            },
                            "events": [],
                            "reference": {
                              "type": "PAGE",
                              "content": {
                                "pageId": "LLP1668195754958966786"
                              },
                              "backfill": {
                                "type": "form",
                                "tableTag": "",
                                "options": []
                              },
                              "extend": {
                                "functionCode": "LLP1668195754958966786"
                              },
                              "source": {
                                "sourceType": "MQL",
                                "action": "query",
                                "service": "api-sup",
                                "type": "sup_company_info_ide",
                                "query": {
                                  "*": {}
                                },
                                "filter": {
                                  "sort": ""
                                },
                                "payload": {}
                              }
                            }
                          },
                          "x-validator": [],
                          "x-component-props": {
                            "pickerOptions": {
                              "type": "dialog",
                              "pageTitle": "供应商查询"
                            },
                            "style": {
                              "opacity": 1
                            },
                            "fieldNames": {
                              "label": "companyName",
                              "value": "companyName"
                            },
                            "reference": {
                              "type": "PAGE",
                              "content": {
                                "pageId": "LLP1668195754958966786"
                              },
                              "backfill": {
                                "type": "form",
                                "tableTag": "",
                                "options": []
                              },
                              "extend": {
                                "functionCode": "LLP1668195754958966786"
                              },
                              "source": {
                                "sourceType": "MQL",
                                "action": "query",
                                "service": "api-sup",
                                "type": "sup_company_info_ide",
                                "query": {
                                  "*": {}
                                },
                                "filter": {
                                  "sort": ""
                                },
                                "payload": {}
                              }
                            },
                            "isAsyncDataSource": true
                          },
                          "x-decorator-props": {
                            "style": {
                              "opacity": 1
                            },
                            "feedbackLayout": "loose"
                          },
                          "x-designable-id": "buamxeloo5x",
                          "x-index": 3
                        },
                        "reviewFormId.createdBy": {
                          "type": "string",
                          "title": "创建人",
                          "x-decorator": "FormItem",
                          "x-component": "Input",
                          "x-query-engine-relation": true,
                          "x-query-engine-relation-strict": true,
                          "name": "reviewFormId.createdBy",
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sup_auth_review_form_ide",
                              "reference": null,
                              "modelId": "1668157711610859522",
                              "field": "reviewFormId.createdBy",
                              "originalField": "createdBy"
                            },
                            "events": []
                          },
                          "x-query-engine-query-operator": "contains",
                          "x-decorator-props": {
                            "style": {
                              "opacity": 1
                            },
                            "feedbackLayout": "loose"
                          },
                          "x-component-props": {
                            "style": {
                              "opacity": 1
                            }
                          },
                          "x-validator": [],
                          "x-designable-id": "p5vu5www47x",
                          "x-index": 4
                        },
                        "approveTime": {
                          "type": "string",
                          "title": "生效时间",
                          "x-decorator": "FormItem",
                          "x-component": "DatePicker",
                          "x-component-props": {
                            "type": "daterange",
                            "editable": true,
                            "clearable": true,
                            "size": "small",
                            "placeholder": "请选择",
                            "start-placeholder": "请选择开始日期",
                            "end-placeholder": "请选择结束日期",
                            "style": {
                              "opacity": 1
                            }
                          },
                          "name": "approveTime",
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sup_effect_form_ide",
                              "reference": null,
                              "modelId": "1668183594803449858",
                              "field": "approveTime",
                              "originalField": "approveTime"
                            },
                            "events": []
                          },
                          "x-validator": [],
                          "x-decorator-props": {
                            "style": {
                              "opacity": 1
                            },
                            "feedbackLayout": "loose"
                          },
                          "x-designable-id": "vbpb25540h6",
                          "x-index": 5,
                          "x-query-engine-query-operator": "between"
                        }
                      }
                    },
                    "tools": {
                      "type": "void",
                      "x-component": "RenderButtonList",
                      "x-component-props": {
                        "style": {
                          "margin": "0px 20px 16px 20px",
                          "opacity": 1
                        },
                        "max": 3,
                        "size": 12
                      },
                      "x-index": 1,
                      "name": "tools",
                      "x-designable-id": "vqdlceljld6",
                      "x-validator": [],
                      "properties": {
                        "addRow": {
                          "type": "void",
                          "title": "新增",
                          "x-component": "Button",
                          "x-component-props": {
                            "type": "primary",
                            "size": "small",
                            "plain": false,
                            "round": false,
                            "circle": false,
                            "style": {
                              "opacity": 1
                            },
                            "@click": "{{(...args) => {\n        return new $LogicEngineRuntime({\n          nodes: [{\"id\":\"eb20ba6d-6b21-47a4-b145-4840c496e4fb\",\"shape\":{\"name\":\"logic-start\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":431,\"y\":0},\"visible\":true,\"zIndex\":1},\"data\":{\"title\":\"开始\",\"value\":null}},{\"id\":\"d2c4645e-fd4c-49e6-9751-c82dd273e944\",\"shape\":{\"name\":\"logic-end\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":431,\"y\":518},\"visible\":true,\"zIndex\":2},\"data\":{\"title\":\"结束\",\"value\":null}},{\"id\":\"e4bb592e-9557-41d8-a8cc-df97a1467203\",\"shape\":{\"name\":\"logic-page-open\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":416,\"y\":241},\"visible\":true,\"zIndex\":3},\"data\":{\"title\":\"打开\",\"value\":{\"scope\":[{\"key\":\"refresh\",\"way\":\"=\",\"type\":\"function\",\"value\":\"function refresh() {\\r\\n  return (\\r\\n    function _refresh() {\\r\\n      if (!this.queryEngine) {\\r\\n        return\\r\\n      }\\r\\n\\r\\n      this.queryEngine.state.paginationManagement.refresh();\\r\\n    }\\r\\n  ).bind($root)\\r\\n}\"}],\"refresh\":true,\"pageId\":\"FORM\",\"pageStatus\":\"add\",\"openWith\":\"tab\",\"pageTitle\":\"新增\",\"tabsFieldDesignerId\":\"wcyne8a8yag\"}},\"executeFunction\":\"function logicNode(ctx) {\\n  $designable.query('wcyne8a8yag').take((field) => {\\n        const pageConfig = $designPages['FORM']\\n        const pageParams = {\\n  primaryKeyValue: $$safeGetScope('$table', $root)\\n    ? $queryEngineConfig.getPrimaryKeyValueByRecord(\\n      $$safeGetScope('$table', $root).getRowByIndex($self.index)\\n    )\\n    : undefined,\\n  \\n  \\\"refresh\\\":\\n    \\n      (function refresh() {\\r\\n  return (\\r\\n    function _refresh() {\\r\\n      if (!this.queryEngine) {\\r\\n        return\\r\\n      }\\r\\n\\r\\n      this.queryEngine.state.paginationManagement.refresh();\\r\\n    }\\r\\n  ).bind($root)\\r\\n})()\\n    ,\\n  \\n}\\n\\n        field.invoke('addTab', {\\n          label: '新增',\\n          name: \\n            $$safeGetScope('$generateUid') && $$safeGetScope('$generateUid')()\\n            ,\\n          closable: true,\\n          \\n          onClose: () => {\\n            $queryEngine.state.paginationManagement.refresh();\\n          },\\n          \\n          scope: {\\n      $pageParams: pageParams,\\n      $readOnly: false,\\n    },\\n          schema: pageConfig.schema\\n        })\\n      })\\n      \\n}\"},{\"id\":\"c8640cd0-d17c-4c16-be22-6b9c5c6f5a00\",\"shape\":{\"name\":\"edge\",\"zIndex\":4,\"source\":{\"cell\":\"eb20ba6d-6b21-47a4-b145-4840c496e4fb\",\"port\":\"bottom\"},\"target\":{\"cell\":\"e4bb592e-9557-41d8-a8cc-df97a1467203\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"cbb044e5-82b0-45fa-aa36-50fc97c76d2d\",\"shape\":{\"name\":\"edge\",\"zIndex\":5,\"source\":{\"cell\":\"e4bb592e-9557-41d8-a8cc-df97a1467203\",\"port\":\"bottom\"},\"target\":{\"cell\":\"d2c4645e-fd4c-49e6-9751-c82dd273e944\",\"port\":\"top\"}},\"data\":{\"value\":null}}],\n          payload: args,\n          scope: {\n            ...$root,\n            $designPages: $designPages,\n          }\n        }).run()\n      }}}"
                          },
                          "x-designer-extend": {
                            "events": [
                              {
                                "title": "新增",
                                "name": "click",
                                "type": "logic",
                                "args": [
                                  {
                                    "id": "eb20ba6d-6b21-47a4-b145-4840c496e4fb",
                                    "shape": {
                                      "name": "logic-start",
                                      "size": {
                                        "width": 55,
                                        "height": 55
                                      },
                                      "position": {
                                        "x": 431,
                                        "y": 0
                                      },
                                      "visible": true,
                                      "zIndex": 1
                                    },
                                    "data": {
                                      "title": "开始",
                                      "value": null
                                    }
                                  },
                                  {
                                    "id": "d2c4645e-fd4c-49e6-9751-c82dd273e944",
                                    "shape": {
                                      "name": "logic-end",
                                      "size": {
                                        "width": 55,
                                        "height": 55
                                      },
                                      "position": {
                                        "x": 431,
                                        "y": 518
                                      },
                                      "visible": true,
                                      "zIndex": 2
                                    },
                                    "data": {
                                      "title": "结束",
                                      "value": null
                                    }
                                  },
                                  {
                                    "id": "e4bb592e-9557-41d8-a8cc-df97a1467203",
                                    "shape": {
                                      "name": "logic-page-open",
                                      "size": {
                                        "width": 86,
                                        "height": 30
                                      },
                                      "position": {
                                        "x": 416,
                                        "y": 241
                                      },
                                      "visible": true,
                                      "zIndex": 3
                                    },
                                    "data": {
                                      "title": "打开",
                                      "value": {
                                        "scope": [
                                          {
                                            "key": "refresh",
                                            "way": "=",
                                            "type": "function",
                                            "value": "function refresh() {\r\n  return (\r\n    function _refresh() {\r\n      if (!this.queryEngine) {\r\n        return\r\n      }\r\n\r\n      this.queryEngine.state.paginationManagement.refresh();\r\n    }\r\n  ).bind($root)\r\n}"
                                          }
                                        ],
                                        "refresh": true,
                                        "pageId": "FORM",
                                        "pageStatus": "add",
                                        "openWith": "tab",
                                        "pageTitle": "新增",
                                        "tabsFieldDesignerId": "wcyne8a8yag"
                                      }
                                    },
                                    "executeFunction": "function logicNode(ctx) {\n  $designable.query('wcyne8a8yag').take((field) => {\n        const pageConfig = $designPages['FORM']\n        const pageParams = {\n  primaryKeyValue: $$safeGetScope('$table', $root)\n    ? $queryEngineConfig.getPrimaryKeyValueByRecord(\n      $$safeGetScope('$table', $root).getRowByIndex($self.index)\n    )\n    : undefined,\n  \n  \"refresh\":\n    \n      (function refresh() {\r\n  return (\r\n    function _refresh() {\r\n      if (!this.queryEngine) {\r\n        return\r\n      }\r\n\r\n      this.queryEngine.state.paginationManagement.refresh();\r\n    }\r\n  ).bind($root)\r\n})()\n    ,\n  \n}\n\n        field.invoke('addTab', {\n          label: '新增',\n          name: \n            $$safeGetScope('$generateUid') && $$safeGetScope('$generateUid')()\n            ,\n          closable: true,\n          \n          onClose: () => {\n            $queryEngine.state.paginationManagement.refresh();\n          },\n          \n          scope: {\n      $pageParams: pageParams,\n      $readOnly: false,\n    },\n          schema: pageConfig.schema\n        })\n      })\n      \n}"
                                  },
                                  {
                                    "id": "c8640cd0-d17c-4c16-be22-6b9c5c6f5a00",
                                    "shape": {
                                      "name": "edge",
                                      "zIndex": 4,
                                      "source": {
                                        "cell": "eb20ba6d-6b21-47a4-b145-4840c496e4fb",
                                        "port": "bottom"
                                      },
                                      "target": {
                                        "cell": "e4bb592e-9557-41d8-a8cc-df97a1467203",
                                        "port": "top"
                                      }
                                    },
                                    "data": {
                                      "value": null
                                    }
                                  },
                                  {
                                    "id": "cbb044e5-82b0-45fa-aa36-50fc97c76d2d",
                                    "shape": {
                                      "name": "edge",
                                      "zIndex": 5,
                                      "source": {
                                        "cell": "e4bb592e-9557-41d8-a8cc-df97a1467203",
                                        "port": "bottom"
                                      },
                                      "target": {
                                        "cell": "d2c4645e-fd4c-49e6-9751-c82dd273e944",
                                        "port": "top"
                                      }
                                    },
                                    "data": {
                                      "value": null
                                    }
                                  }
                                ]
                              }
                            ]
                          },
                          "x-index": 0,
                          "name": "addRow",
                          "x-designable-id": "ou18l8fg3o3",
                          "x-validator": [],
                          "x-decorator-props": {
                            "style": {
                              "opacity": 1
                            }
                          }
                        }
                      }
                    },
                    "table": {
                      "type": "array",
                      "x-decorator": "FormItem",
                      "x-component": "RenderTable",
                      "x-validator": [],
                      "x-decorator-props": {
                        "style": "padding: 0 20px;",
                        "feedbackLayout": "loose"
                      },
                      "x-designable-id": "g01w2gug8mg",
                      "x-component-props": {
                        "stripe": false,
                        "border": true,
                        "show-header": true,
                        "size": "small",
                        "style": "width:100%",
                        "preColumns": [
                          "seq"
                        ],
                        "openCustomTable": true,
                        "editMode": "multi-row",
                        "dblclickEditable": false,
                        "pagination": {
                          "pageSizes": [
                            15,
                            30,
                            60,
                            120,
                            300,
                            600,
                            1000,
                            1500
                          ],
                          "pageSize": 15
                        },
                        "showHeader": true
                      },
                      "x-designer-extend": {
                        "page": {
                          "pagination": true,
                          "pageSize": 15,
                          "pageSizes": "15,30,60,120,300,600,1000,1500"
                        },
                        "events": []
                      },
                      "x-index": 2,
                      "name": "table",
                      "x-reactions": {
                        "dependencies": [],
                        "fulfill": {
                          "state": {
                            "decoratorProps": "{{{ \n  ...$self.componentProps,\n  class: 'list-page--table-decorator'\n}}}"
                          }
                        }
                      },
                      "properties": {
                        "effectFormNumber": {
                          "type": "void",
                          "x-component": "RenderTable.Column",
                          "x-component-props": {
                            "title": "供方生效单号",
                            "sortable": true,
                            "resizable": true,
                            "visible": true,
                            "align": "left",
                            "headerAlign": "left",
                            "skipEditable": false,
                            "style": {
                              "opacity": 1
                            },
                            "id": "dkklzhv7jkk",
                            "isGroup": false,
                            "minWidth": 150
                          },
                          "x-index": 0,
                          "name": "effectFormNumber",
                          "x-designable-id": "dkklzhv7jkk",
                          "x-display": "visible",
                          "x-decorator-props": {
                            "style": {
                              "opacity": 1
                            }
                          },
                          "x-designer-extend": {},
                          "properties": {
                            "effectFormNumber": {
                              "type": "string",
                              "x-decorator": "FormItem",
                              "x-component": "Link",
                              "x-component-props": {
                                "href": "单号",
                                "type": "primary",
                                "underline": false,
                                "style": {
                                  "opacity": 1
                                },
                                "@click": "{{(...args) => {\n        return new $LogicEngineRuntime({\n          nodes: [{\"id\":\"45ff3834-976e-4288-9466-06759df3fcca\",\"shape\":{\"name\":\"logic-start\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":431,\"y\":0},\"visible\":true,\"zIndex\":1},\"data\":{\"title\":\"开始\",\"value\":null}},{\"id\":\"3101b64f-0243-4aac-80d6-74a33441d5be\",\"shape\":{\"name\":\"logic-end\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":431,\"y\":469.7},\"visible\":true,\"zIndex\":2},\"data\":{\"title\":\"结束\",\"value\":null}},{\"id\":\"046554e9-62ad-41ec-9b33-cb5e876d5ecb\",\"shape\":{\"name\":\"logic-page-open\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":416,\"y\":225},\"visible\":true,\"zIndex\":3},\"data\":{\"title\":\"打开\",\"value\":{\"scope\":[],\"refresh\":true,\"pageTitle\":\"{{$self.value}}\",\"pageId\":\"FORM\",\"pageStatus\":\"view\",\"openWith\":\"tab\",\"tabsFieldDesignerId\":\"wcyne8a8yag\"}},\"executeFunction\":\"function logicNode(ctx) {\\n  $designable.query('wcyne8a8yag').take((field) => {\\n        const pageConfig = $designPages['FORM']\\n        const pageParams = {\\n  primaryKeyValue: $$safeGetScope('$table', $root)\\n    ? $queryEngineConfig.getPrimaryKeyValueByRecord(\\n      $$safeGetScope('$table', $root).getRowByIndex($self.index)\\n    )\\n    : undefined,\\n  \\n}\\n\\n        field.invoke('addTab', {\\n          label: String($self.value),\\n          name: \\n            pageConfig.value\\n          ,\\n          closable: true,\\n          \\n          onClose: () => {\\n            $queryEngine.state.paginationManagement.refresh();\\n          },\\n          \\n          scope: {\\n      $pageParams: pageParams,\\n      $readOnly: true,\\n    },\\n          schema: pageConfig.schema\\n        })\\n      })\\n      \\n}\"},{\"id\":\"525a009c-ac71-46ca-a68f-34a6ae3d7901\",\"shape\":{\"name\":\"edge\",\"zIndex\":4,\"source\":{\"cell\":\"45ff3834-976e-4288-9466-06759df3fcca\",\"port\":\"bottom\"},\"target\":{\"cell\":\"046554e9-62ad-41ec-9b33-cb5e876d5ecb\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"2aa8c198-080f-4006-bc25-d74fa457505d\",\"shape\":{\"name\":\"edge\",\"zIndex\":5,\"source\":{\"cell\":\"046554e9-62ad-41ec-9b33-cb5e876d5ecb\",\"port\":\"bottom\"},\"target\":{\"cell\":\"3101b64f-0243-4aac-80d6-74a33441d5be\",\"port\":\"top\"}},\"data\":{\"value\":null}}],\n          payload: args,\n          scope: {\n            ...$root,\n            $designPages: $designPages,\n          }\n        }).run()\n      }}}"
                              },
                              "x-read-pretty": false,
                              "x-designer-extend": {
                                "events": [
                                  {
                                    "title": "点击详情",
                                    "name": "click",
                                    "type": "logic",
                                    "args": [
                                      {
                                        "id": "45ff3834-976e-4288-9466-06759df3fcca",
                                        "shape": {
                                          "name": "logic-start",
                                          "size": {
                                            "width": 55,
                                            "height": 55
                                          },
                                          "position": {
                                            "x": 431,
                                            "y": 0
                                          },
                                          "visible": true,
                                          "zIndex": 1
                                        },
                                        "data": {
                                          "title": "开始",
                                          "value": null
                                        }
                                      },
                                      {
                                        "id": "3101b64f-0243-4aac-80d6-74a33441d5be",
                                        "shape": {
                                          "name": "logic-end",
                                          "size": {
                                            "width": 55,
                                            "height": 55
                                          },
                                          "position": {
                                            "x": 431,
                                            "y": 469.7
                                          },
                                          "visible": true,
                                          "zIndex": 2
                                        },
                                        "data": {
                                          "title": "结束",
                                          "value": null
                                        }
                                      },
                                      {
                                        "id": "046554e9-62ad-41ec-9b33-cb5e876d5ecb",
                                        "shape": {
                                          "name": "logic-page-open",
                                          "size": {
                                            "width": 86,
                                            "height": 30
                                          },
                                          "position": {
                                            "x": 416,
                                            "y": 225
                                          },
                                          "visible": true,
                                          "zIndex": 3
                                        },
                                        "data": {
                                          "title": "打开",
                                          "value": {
                                            "scope": [],
                                            "refresh": true,
                                            "pageTitle": "{{$self.value}}",
                                            "pageId": "FORM",
                                            "pageStatus": "view",
                                            "openWith": "tab",
                                            "tabsFieldDesignerId": "wcyne8a8yag"
                                          }
                                        },
                                        "executeFunction": "function logicNode(ctx) {\n  $designable.query('wcyne8a8yag').take((field) => {\n        const pageConfig = $designPages['FORM']\n        const pageParams = {\n  primaryKeyValue: $$safeGetScope('$table', $root)\n    ? $queryEngineConfig.getPrimaryKeyValueByRecord(\n      $$safeGetScope('$table', $root).getRowByIndex($self.index)\n    )\n    : undefined,\n  \n}\n\n        field.invoke('addTab', {\n          label: String($self.value),\n          name: \n            pageConfig.value\n          ,\n          closable: true,\n          \n          onClose: () => {\n            $queryEngine.state.paginationManagement.refresh();\n          },\n          \n          scope: {\n      $pageParams: pageParams,\n      $readOnly: true,\n    },\n          schema: pageConfig.schema\n        })\n      })\n      \n}"
                                      },
                                      {
                                        "id": "525a009c-ac71-46ca-a68f-34a6ae3d7901",
                                        "shape": {
                                          "name": "edge",
                                          "zIndex": 4,
                                          "source": {
                                            "cell": "45ff3834-976e-4288-9466-06759df3fcca",
                                            "port": "bottom"
                                          },
                                          "target": {
                                            "cell": "046554e9-62ad-41ec-9b33-cb5e876d5ecb",
                                            "port": "top"
                                          }
                                        },
                                        "data": {
                                          "value": null
                                        }
                                      },
                                      {
                                        "id": "2aa8c198-080f-4006-bc25-d74fa457505d",
                                        "shape": {
                                          "name": "edge",
                                          "zIndex": 5,
                                          "source": {
                                            "cell": "046554e9-62ad-41ec-9b33-cb5e876d5ecb",
                                            "port": "bottom"
                                          },
                                          "target": {
                                            "cell": "3101b64f-0243-4aac-80d6-74a33441d5be",
                                            "port": "top"
                                          }
                                        },
                                        "data": {
                                          "value": null
                                        }
                                      }
                                    ]
                                  }
                                ]
                              },
                              "x-validator": [],
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                },
                                "feedbackLayout": "popover"
                              },
                              "name": "effectFormNumber",
                              "x-designable-id": "usrx1i3po21",
                              "x-index": 0
                            }
                          }
                        },
                        "companyName": {
                          "type": "void",
                          "x-component": "RenderTable.Column",
                          "x-component-props": {
                            "title": "供应商名称",
                            "sortable": true,
                            "resizable": true,
                            "visible": true,
                            "align": "left",
                            "headerAlign": "left",
                            "skipEditable": false,
                            "style": {
                              "opacity": 1
                            },
                            "id": "3qp29vsmx7b",
                            "isGroup": false,
                            "minWidth": 150
                          },
                          "x-index": 1,
                          "name": "companyName",
                          "x-designable-id": "3qp29vsmx7b",
                          "x-display": "visible",
                          "x-decorator-props": {
                            "style": {
                              "opacity": 1
                            }
                          },
                          "x-designer-extend": {},
                          "properties": {
                            "companyName": {
                              "type": "string",
                              "x-component": "Input",
                              "x-decorator": "FormItem",
                              "x-component-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {
                                "model": {
                                  "businessType": "sccSupEffectForm",
                                  "reference": null,
                                  "modelId": "1668183594803449858",
                                  "field": "companyName",
                                  "originalField": "companyName"
                                },
                                "events": []
                              },
                              "x-index": 0,
                              "name": "companyName",
                              "x-designable-id": "qxhz6jdry9l",
                              "x-validator": [],
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                },
                                "feedbackLayout": "popover"
                              },
                              "x-pattern": "readPretty"
                            }
                          }
                        },
                        "quaReviewType": {
                          "type": "void",
                          "x-component": "RenderTable.Column",
                          "x-component-props": {
                            "title": "资质审查类型",
                            "sortable": true,
                            "resizable": true,
                            "visible": true,
                            "align": "left",
                            "headerAlign": "left",
                            "skipEditable": false,
                            "style": {
                              "opacity": 1
                            },
                            "id": "21pqzk9p4f3",
                            "isGroup": false,
                            "minWidth": 150
                          },
                          "x-index": 2,
                          "name": "quaReviewType",
                          "x-designable-id": "21pqzk9p4f3",
                          "x-display": "visible",
                          "x-decorator-props": {
                            "style": {
                              "opacity": 1
                            }
                          },
                          "x-designer-extend": {},
                          "properties": {
                            "quaReviewType": {
                              "title": "",
                              "x-decorator": "FormItem",
                              "x-component": "Select",
                              "name": "quaReviewType",
                              "x-designer-extend": {
                                "model": {
                                  "businessType": "IdeSupEffectForm",
                                  "reference": null,
                                  "modelId": "1668183594803449858",
                                  "field": "quaReviewType",
                                  "originalField": "quaReviewType"
                                },
                                "source": {
                                  "sourceType": "MQL",
                                  "action": "query",
                                  "type": "base_dict_item_ide",
                                  "query": {
                                    "*": {}
                                  },
                                  "filter": {
                                    "$and": {
                                      "dictId": {
                                        "eq": "6763956529790976"
                                      }
                                    }
                                  },
                                  "service": "api-base",
                                  "payload": {
                                    "page": {
                                      "sort": "dictItemNo asc"
                                    }
                                  },
                                  "enum": [],
                                  "conditions": [
                                    [
                                      {
                                        "dataName": "dictId",
                                        "comparison": "eq",
                                        "valueType": "fixed",
                                        "modelField": "6763956529790976",
                                        "modelId": ""
                                      }
                                    ]
                                  ]
                                },
                                "events": []
                              },
                              "x-validator": [],
                              "x-component-props": {
                                "size": "small",
                                "multiple-limit": 0,
                                "placeholder": "请选择",
                                "style": {
                                  "opacity": 1
                                },
                                "fieldNames": {
                                  "label": "dictItemName",
                                  "value": "dictItemCode"
                                },
                                "@created": "{{() => {\n                    $queryEngine.request.baseRequest({\n                      type: 'base_dict_item_ide',\n                      action: 'query',\n                      payload: {\n                        filter: {\"$and\":{\"dictId\":{\"eq\":\"6763956529790976\"}}},\n                        page: {\"pageSize\":9999,\"pageNum\":1,\"sort\":\"dictItemNo asc\"},\n                      },\n                      query: {\"*\":{}},\n                      actionConfig: { service: 'api-base' },\n                      queryConfig: { staleTime: 300000 }\n                    }).then(res => {\n                      $self.dataSource = res.data || []\n                    })\n                  }}}"
                              },
                              "enum": [],
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                },
                                "feedbackLayout": "popover"
                              },
                              "x-designable-id": "n00j96e4jzk",
                              "x-index": 0,
                              "x-pattern": "readPretty"
                            }
                          }
                        },
                        "approveStatus": {
                          "type": "void",
                          "x-component": "RenderTable.Column",
                          "x-component-props": {
                            "title": "单据审批状态",
                            "sortable": true,
                            "resizable": true,
                            "visible": true,
                            "align": "left",
                            "headerAlign": "left",
                            "skipEditable": false,
                            "style": {
                              "opacity": 1
                            },
                            "id": "cad7qko1wgh",
                            "isGroup": false,
                            "minWidth": 150
                          },
                          "x-index": 3,
                          "name": "approveStatus",
                          "x-designable-id": "cad7qko1wgh",
                          "x-display": "visible",
                          "x-decorator-props": {
                            "style": {
                              "opacity": 1
                            }
                          },
                          "x-designer-extend": {},
                          "properties": {
                            "approveStatus": {
                              "title": "",
                              "x-decorator": "FormItem",
                              "x-component": "Select",
                              "name": "approveStatus",
                              "x-designer-extend": {
                                "model": {
                                  "businessType": "sup_effect_form_ide",
                                  "reference": null,
                                  "modelId": "1668183594803449858",
                                  "field": "approveStatus",
                                  "originalField": "approveStatus"
                                },
                                "source": {
                                  "sourceType": "MQL",
                                  "action": "query",
                                  "type": "base_dict_item_ide",
                                  "query": {
                                    "*": {}
                                  },
                                  "filter": {
                                    "$and": {
                                      "dictId": {
                                        "eq": "6805186166587392"
                                      }
                                    }
                                  },
                                  "service": "api-base",
                                  "payload": {
                                    "page": {
                                      "sort": "dictItemNo asc"
                                    }
                                  },
                                  "enum": [],
                                  "conditions": [
                                    [
                                      {
                                        "dataName": "dictId",
                                        "comparison": "eq",
                                        "valueType": "fixed",
                                        "modelField": "6805186166587392",
                                        "modelId": ""
                                      }
                                    ]
                                  ]
                                },
                                "events": []
                              },
                              "x-validator": [],
                              "x-component-props": {
                                "size": "small",
                                "multiple-limit": 0,
                                "placeholder": "请选择",
                                "style": {
                                  "opacity": 1
                                },
                                "fieldNames": {
                                  "label": "dictItemName",
                                  "value": "dictItemCode"
                                },
                                "@created": "{{() => {\n                    $queryEngine.request.baseRequest({\n                      type: 'base_dict_item_ide',\n                      action: 'query',\n                      payload: {\n                        filter: {\"$and\":{\"dictId\":{\"eq\":\"6805186166587392\"}}},\n                        page: {\"pageSize\":9999,\"pageNum\":1,\"sort\":\"dictItemNo asc\"},\n                      },\n                      query: {\"*\":{}},\n                      actionConfig: { service: 'api-base' },\n                      queryConfig: { staleTime: 300000 }\n                    }).then(res => {\n                      $self.dataSource = res.data || []\n                    })\n                  }}}"
                              },
                              "enum": [],
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                },
                                "feedbackLayout": "popover"
                              },
                              "x-designable-id": "mgx9d9nqw92",
                              "x-index": 0,
                              "x-pattern": "readPretty"
                            }
                          }
                        },
                        "ek3b0dvk78i": {
                          "type": "void",
                          "x-component": "RenderButtonList",
                          "x-decorator-props": {
                            "style": {
                              "opacity": 1
                            }
                          },
                          "x-designer-extend": {},
                          "x-component-props": {
                            "max": 3,
                            "size": 12,
                            "style": {
                              "opacity": 1
                            }
                          },
                          "x-validator": [],
                          "x-designable-id": "ek3b0dvk78i",
                          "x-index": 4
                        },
                        "reviewFinishDate": {
                          "type": "void",
                          "x-component": "RenderTable.Column",
                          "x-component-props": {
                            "title": "生效时间",
                            "sortable": true,
                            "resizable": true,
                            "id": "pxfomu24rps",
                            "visible": true,
                            "isGroup": false,
                            "align": "left",
                            "headerAlign": "left",
                            "skipEditable": false,
                            "style": {
                              "opacity": 1
                            },
                            "minWidth": 150
                          },
                          "x-index": 5,
                          "name": "reviewFinishDate",
                          "x-designable-id": "pxfomu24rps",
                          "x-display": "visible",
                          "x-decorator-props": {
                            "style": {
                              "opacity": 1
                            }
                          },
                          "x-designer-extend": {},
                          "properties": {
                            "reviewFinishDate": {
                              "type": "string",
                              "x-component": "DatePicker",
                              "x-decorator": "FormItem",
                              "x-component-props": {
                                "type": "datetime",
                                "editable": true,
                                "clearable": true,
                                "size": "small",
                                "placeholder": "请选择",
                                "start-placeholder": "",
                                "end-placeholder": "",
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {
                                "model": {
                                  "businessType": "sccSupEffectForm",
                                  "reference": null,
                                  "modelId": "1668183594803449858",
                                  "field": "reviewFinishDate",
                                  "originalField": "reviewFinishDate"
                                },
                                "events": []
                              },
                              "x-index": 0,
                              "name": "reviewFinishDate",
                              "x-designable-id": "lxx1tnxi6zt",
                              "x-validator": [],
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                },
                                "feedbackLayout": "popover"
                              },
                              "x-pattern": "readPretty",
                              "x-query-engine-sort": ""
                            }
                          }
                        },
                        "rr999r5zl9y": {
                          "type": "void",
                          "x-component": "RenderTable.Column",
                          "x-component-props": {
                            "title": "创建人",
                            "sortable": true,
                            "resizable": true,
                            "visible": true,
                            "align": "left",
                            "headerAlign": "left",
                            "skipEditable": false,
                            "style": {
                              "opacity": 1
                            },
                            "minWidth": 150
                          },
                          "x-decorator-props": {
                            "style": {
                              "opacity": 1
                            }
                          },
                          "x-designer-extend": {},
                          "x-designable-id": "rr999r5zl9y",
                          "x-index": 6,
                          "x-display": "visible",
                          "properties": {
                            "createdBy": {
                              "type": "string",
                              "title": "",
                              "x-decorator": "FormItem",
                              "x-component": "Input",
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                },
                                "feedbackLayout": "popover"
                              },
                              "x-designer-extend": {
                                "events": []
                              },
                              "x-component-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-validator": [],
                              "x-pattern": "readPretty",
                              "name": "createdBy",
                              "x-designable-id": "8t0gwd5r7t3",
                              "x-index": 0
                            }
                          }
                        },
                        "7rrj5o6s9cn": {
                          "type": "void",
                          "x-component": "RenderTable.Column",
                          "x-component-props": {
                            "title": "操作",
                            "sortable": true,
                            "resizable": true,
                            "visible": true,
                            "align": "left",
                            "headerAlign": "left",
                            "skipEditable": false,
                            "style": {
                              "opacity": 1
                            },
                            "fixed": "right",
                            "minWidth": 100,
                            "width": 150
                          },
                          "x-decorator-props": {
                            "style": {
                              "opacity": 1
                            }
                          },
                          "x-designer-extend": {},
                          "x-designable-id": "7rrj5o6s9cn",
                          "x-index": 7,
                          "x-display": "visible",
                          "properties": {
                            "ek3b0dvk78i": {
                              "type": "void",
                              "x-component": "RenderButtonList",
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {},
                              "x-component-props": {
                                "max": 4,
                                "size": 12,
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-validator": [],
                              "x-designable-id": "ek3b0dvk78i",
                              "x-index": 0,
                              "title": "",
                              "properties": {
                                "paxi66kyl75": {
                                  "type": "void",
                                  "title": "编辑",
                                  "x-component": "Button",
                                  "x-component-props": {
                                    "type": "text",
                                    "size": "small",
                                    "plain": false,
                                    "round": false,
                                    "circle": false,
                                    "style": {
                                      "opacity": 1
                                    },
                                    "text": "编辑",
                                    "@click": "{{(...args) => {\n        return new $LogicEngineRuntime({\n          nodes: [{\"id\":\"3d3f2ef7-0ac5-4d03-bb7a-909e8b2cd506\",\"shape\":{\"name\":\"logic-start\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":449.5,\"y\":0},\"visible\":true,\"zIndex\":1},\"data\":{\"title\":\"开始\",\"value\":null}},{\"id\":\"53aaa070-32a3-4fb9-b8bf-679773f36880\",\"shape\":{\"name\":\"logic-end\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":449.5,\"y\":518},\"visible\":true,\"zIndex\":2},\"data\":{\"title\":\"结束\",\"value\":null}},{\"id\":\"289fd540-80d8-45ca-80df-36ee2a277b89\",\"shape\":{\"name\":\"logic-page-open\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":434,\"y\":259},\"visible\":true,\"zIndex\":3},\"data\":{\"title\":\"打开\",\"value\":{\"scope\":[],\"refresh\":true,\"pageId\":\"FORM\",\"pageStatus\":\"edit\",\"openWith\":\"tab\",\"tabsFieldDesignerId\":\"wcyne8a8yag\",\"pageTitle\":\"{{$table.getRowByIndex($self.index).effectFormNumber}}\"}},\"executeFunction\":\"function logicNode(ctx) {\\n  $designable.query('wcyne8a8yag').take((field) => {\\n        const pageConfig = $designPages['FORM']\\n        const pageParams = {\\n  primaryKeyValue: $$safeGetScope('$table', $root)\\n    ? $queryEngineConfig.getPrimaryKeyValueByRecord(\\n      $$safeGetScope('$table', $root).getRowByIndex($self.index)\\n    )\\n    : undefined,\\n  \\n}\\n\\n        field.invoke('addTab', {\\n          label: String($table.getRowByIndex($self.index).effectFormNumber),\\n          name: \\n            pageConfig.value\\n          ,\\n          closable: true,\\n          \\n          onClose: () => {\\n            $queryEngine.state.paginationManagement.refresh();\\n          },\\n          \\n          scope: {\\n      $pageParams: pageParams,\\n      $readOnly: false,\\n    },\\n          schema: pageConfig.schema,\\n          form: pageConfig.form,\\n        })\\n      })\\n      \\n}\"},{\"id\":\"da00c296-d43a-4d0d-b280-ca3c9ced7ac2\",\"shape\":{\"name\":\"edge\",\"zIndex\":4,\"source\":{\"cell\":\"3d3f2ef7-0ac5-4d03-bb7a-909e8b2cd506\",\"port\":\"bottom\"},\"target\":{\"cell\":\"289fd540-80d8-45ca-80df-36ee2a277b89\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"4f5deb3a-c410-420c-a43a-ae377187fbf5\",\"shape\":{\"name\":\"edge\",\"zIndex\":5,\"source\":{\"cell\":\"289fd540-80d8-45ca-80df-36ee2a277b89\",\"port\":\"bottom\"},\"target\":{\"cell\":\"53aaa070-32a3-4fb9-b8bf-679773f36880\",\"port\":\"top\"}},\"data\":{\"value\":null}}],\n          payload: args,\n          scope: {\n            ...$root,\n            $designPages: $designPages,\n          }\n        }).run()\n      }}}"
                                  },
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-designer-extend": {
                                    "events": [
                                      {
                                        "title": "打开",
                                        "name": "click",
                                        "type": "logic",
                                        "args": [
                                          {
                                            "id": "3d3f2ef7-0ac5-4d03-bb7a-909e8b2cd506",
                                            "shape": {
                                              "name": "logic-start",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 449.5,
                                                "y": 0
                                              },
                                              "visible": true,
                                              "zIndex": 1
                                            },
                                            "data": {
                                              "title": "开始",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "53aaa070-32a3-4fb9-b8bf-679773f36880",
                                            "shape": {
                                              "name": "logic-end",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 449.5,
                                                "y": 518
                                              },
                                              "visible": true,
                                              "zIndex": 2
                                            },
                                            "data": {
                                              "title": "结束",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "289fd540-80d8-45ca-80df-36ee2a277b89",
                                            "shape": {
                                              "name": "logic-page-open",
                                              "size": {
                                                "width": 86,
                                                "height": 30
                                              },
                                              "position": {
                                                "x": 434,
                                                "y": 259
                                              },
                                              "visible": true,
                                              "zIndex": 3
                                            },
                                            "data": {
                                              "title": "打开",
                                              "value": {
                                                "scope": [],
                                                "refresh": true,
                                                "pageId": "FORM",
                                                "pageStatus": "edit",
                                                "openWith": "tab",
                                                "tabsFieldDesignerId": "wcyne8a8yag",
                                                "pageTitle": "{{$table.getRowByIndex($self.index).effectFormNumber}}"
                                              }
                                            },
                                            "executeFunction": "function logicNode(ctx) {\n  $designable.query('wcyne8a8yag').take((field) => {\n        const pageConfig = $designPages['FORM']\n        const pageParams = {\n  primaryKeyValue: $$safeGetScope('$table', $root)\n    ? $queryEngineConfig.getPrimaryKeyValueByRecord(\n      $$safeGetScope('$table', $root).getRowByIndex($self.index)\n    )\n    : undefined,\n  \n}\n\n        field.invoke('addTab', {\n          label: String($table.getRowByIndex($self.index).effectFormNumber),\n          name: \n            pageConfig.value\n          ,\n          closable: true,\n          \n          onClose: () => {\n            $queryEngine.state.paginationManagement.refresh();\n          },\n          \n          scope: {\n      $pageParams: pageParams,\n      $readOnly: false,\n    },\n          schema: pageConfig.schema,\n          form: pageConfig.form,\n        })\n      })\n      \n}"
                                          },
                                          {
                                            "id": "da00c296-d43a-4d0d-b280-ca3c9ced7ac2",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 4,
                                              "source": {
                                                "cell": "3d3f2ef7-0ac5-4d03-bb7a-909e8b2cd506",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "289fd540-80d8-45ca-80df-36ee2a277b89",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "4f5deb3a-c410-420c-a43a-ae377187fbf5",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 5,
                                              "source": {
                                                "cell": "289fd540-80d8-45ca-80df-36ee2a277b89",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "53aaa070-32a3-4fb9-b8bf-679773f36880",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          }
                                        ]
                                      }
                                    ]
                                  },
                                  "x-validator": [],
                                  "x-reactions": {
                                    "dependencies": [],
                                    "fulfill": {
                                      "state": {},
                                      "run": "$self.visible = [null, '', 'DRAFT', 'WITHDRAW', 'REJECTED'].includes(\r\n  $self.query('.approveStatus').get('value')\r\n)"
                                    }
                                  },
                                  "default": "{{$table.getRowByIndex($self.index).code}}",
                                  "x-designable-id": "paxi66kyl75",
                                  "x-index": 0
                                },
                                "1eqc2iqd26j": {
                                  "type": "void",
                                  "title": "删除",
                                  "x-component": "Button",
                                  "x-component-props": {
                                    "type": "text",
                                    "size": "small",
                                    "plain": false,
                                    "round": false,
                                    "circle": false,
                                    "style": {
                                      "opacity": 1
                                    },
                                    "text": "删除",
                                    "popconfirm": {
                                      "title": "是否确定删除当前行"
                                    },
                                    "@click": "{{(...args) => {\n        return new $LogicEngineRuntime({\n          nodes: [{\"id\":\"cbb267fe-e39e-4f1c-9e74-2998cd6264fc\",\"shape\":{\"name\":\"logic-start\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":371,\"y\":0},\"visible\":true,\"zIndex\":1},\"data\":{\"title\":\"开始\",\"value\":null}},{\"id\":\"9eed3984-6800-49b9-8843-22dcdb262eed\",\"shape\":{\"name\":\"logic-end\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":371,\"y\":436.09999999999997},\"visible\":true,\"zIndex\":2},\"data\":{\"title\":\"结束\",\"value\":null}},{\"id\":\"c791d6cf-668d-4830-9862-ce81a3c8ddd4\",\"shape\":{\"name\":\"logic-table-remove\",\"size\":{\"width\":70,\"height\":60},\"position\":{\"x\":364,\"y\":160},\"visible\":true,\"zIndex\":3},\"data\":{\"title\":\"表格删除\",\"value\":null},\"executeFunction\":\"function logicNode(ctx) {\\n  \\n        if(!$table || !$queryEngineConfig.getPrimaryKeyValueByRecord($table.getRowByIndex($self.index))) return false\\n        return $queryEngine.request.delete($queryEngineConfig.getPrimaryKeyValueByRecord($table.getRowByIndex($self.index)), {})\\n          .then(() => {\\n            $table.cancelEditRow($self.index)\\n            $queryEngine.state.paginationManagement.refresh()\\n          })\\n      \\n}\"},{\"id\":\"565b19b7-4438-4db8-a146-77d5f9eba599\",\"shape\":{\"name\":\"edge\",\"zIndex\":4,\"source\":{\"cell\":\"cbb267fe-e39e-4f1c-9e74-2998cd6264fc\",\"port\":\"bottom\"},\"target\":{\"cell\":\"c791d6cf-668d-4830-9862-ce81a3c8ddd4\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"6d250261-84d8-41c7-a588-ad8481f54d4c\",\"shape\":{\"name\":\"logic-message\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":132,\"y\":175},\"visible\":true,\"zIndex\":6},\"data\":{\"title\":\"消息提示\",\"value\":{\"type\":\"error\",\"message\":\"删除失败，请稍后再试\"}},\"executeFunction\":\"function logicNode(ctx) {\\n  $message({\\\"message\\\":\\\"删除失败，请稍后再试\\\",\\\"type\\\":\\\"error\\\"})\\n}\"},{\"id\":\"8caa94c1-97f2-4eb6-a25b-43e79329c5b4\",\"shape\":{\"name\":\"logic-message\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":559,\"y\":175},\"visible\":true,\"zIndex\":7},\"data\":{\"title\":\"消息提示\",\"value\":{\"type\":\"success\",\"message\":\"删除成功\"}},\"executeFunction\":\"function logicNode(ctx) {\\n  $message({\\\"message\\\":\\\"删除成功\\\",\\\"type\\\":\\\"success\\\"})\\n}\"},{\"id\":\"d31a5fb7-0c23-47b7-afc9-0bdf56b61f34\",\"shape\":{\"name\":\"edge\",\"zIndex\":8,\"source\":{\"cell\":\"c791d6cf-668d-4830-9862-ce81a3c8ddd4\",\"port\":\"left\"},\"target\":{\"cell\":\"6d250261-84d8-41c7-a588-ad8481f54d4c\",\"port\":\"right\"},\"labels\":[{\"attrs\":{\"label\":{\"text\":\"失败\"}}}]},\"data\":{\"value\":null}},{\"id\":\"42162af8-b179-49af-9576-65b5c23cb65b\",\"shape\":{\"name\":\"edge\",\"zIndex\":9,\"source\":{\"cell\":\"c791d6cf-668d-4830-9862-ce81a3c8ddd4\",\"port\":\"right\"},\"target\":{\"cell\":\"8caa94c1-97f2-4eb6-a25b-43e79329c5b4\",\"port\":\"left\"},\"labels\":[{\"attrs\":{\"label\":{\"text\":\"成功\"}}}]},\"data\":{\"value\":null}},{\"id\":\"6bdd71d5-e584-44d1-b7a3-b876796d9363\",\"shape\":{\"name\":\"edge\",\"zIndex\":10,\"source\":{\"cell\":\"8caa94c1-97f2-4eb6-a25b-43e79329c5b4\",\"port\":\"bottom\"},\"target\":{\"cell\":\"9eed3984-6800-49b9-8843-22dcdb262eed\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"a4d4a26e-2da1-4b78-83bb-1bcb820e23ef\",\"shape\":{\"name\":\"edge\",\"zIndex\":11,\"source\":{\"cell\":\"6d250261-84d8-41c7-a588-ad8481f54d4c\",\"port\":\"bottom\"},\"target\":{\"cell\":\"9eed3984-6800-49b9-8843-22dcdb262eed\",\"port\":\"top\"}},\"data\":{\"value\":null}}],\n          payload: args,\n          scope: {\n            ...$root,\n            $designPages: $designPages,\n          }\n        }).run()\n      }}}"
                                  },
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-designer-extend": {
                                    "events": [
                                      {
                                        "title": "删除行",
                                        "name": "click",
                                        "type": "logic",
                                        "args": [
                                          {
                                            "id": "cbb267fe-e39e-4f1c-9e74-2998cd6264fc",
                                            "shape": {
                                              "name": "logic-start",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 371,
                                                "y": 0
                                              },
                                              "visible": true,
                                              "zIndex": 1
                                            },
                                            "data": {
                                              "title": "开始",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "9eed3984-6800-49b9-8843-22dcdb262eed",
                                            "shape": {
                                              "name": "logic-end",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 371,
                                                "y": 436.09999999999997
                                              },
                                              "visible": true,
                                              "zIndex": 2
                                            },
                                            "data": {
                                              "title": "结束",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "c791d6cf-668d-4830-9862-ce81a3c8ddd4",
                                            "shape": {
                                              "name": "logic-table-remove",
                                              "size": {
                                                "width": 70,
                                                "height": 60
                                              },
                                              "position": {
                                                "x": 364,
                                                "y": 160
                                              },
                                              "visible": true,
                                              "zIndex": 3
                                            },
                                            "data": {
                                              "title": "表格删除",
                                              "value": null
                                            },
                                            "executeFunction": "function logicNode(ctx) {\n  \n        if(!$table || !$queryEngineConfig.getPrimaryKeyValueByRecord($table.getRowByIndex($self.index))) return false\n        return $queryEngine.request.delete($queryEngineConfig.getPrimaryKeyValueByRecord($table.getRowByIndex($self.index)), {})\n          .then(() => {\n            $table.cancelEditRow($self.index)\n            $queryEngine.state.paginationManagement.refresh()\n          })\n      \n}"
                                          },
                                          {
                                            "id": "565b19b7-4438-4db8-a146-77d5f9eba599",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 4,
                                              "source": {
                                                "cell": "cbb267fe-e39e-4f1c-9e74-2998cd6264fc",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "c791d6cf-668d-4830-9862-ce81a3c8ddd4",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "6d250261-84d8-41c7-a588-ad8481f54d4c",
                                            "shape": {
                                              "name": "logic-message",
                                              "size": {
                                                "width": 86,
                                                "height": 30
                                              },
                                              "position": {
                                                "x": 132,
                                                "y": 175
                                              },
                                              "visible": true,
                                              "zIndex": 6
                                            },
                                            "data": {
                                              "title": "消息提示",
                                              "value": {
                                                "type": "error",
                                                "message": "删除失败，请稍后再试"
                                              }
                                            },
                                            "executeFunction": "function logicNode(ctx) {\n  $message({\"message\":\"删除失败，请稍后再试\",\"type\":\"error\"})\n}"
                                          },
                                          {
                                            "id": "8caa94c1-97f2-4eb6-a25b-43e79329c5b4",
                                            "shape": {
                                              "name": "logic-message",
                                              "size": {
                                                "width": 86,
                                                "height": 30
                                              },
                                              "position": {
                                                "x": 559,
                                                "y": 175
                                              },
                                              "visible": true,
                                              "zIndex": 7
                                            },
                                            "data": {
                                              "title": "消息提示",
                                              "value": {
                                                "type": "success",
                                                "message": "删除成功"
                                              }
                                            },
                                            "executeFunction": "function logicNode(ctx) {\n  $message({\"message\":\"删除成功\",\"type\":\"success\"})\n}"
                                          },
                                          {
                                            "id": "d31a5fb7-0c23-47b7-afc9-0bdf56b61f34",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 8,
                                              "source": {
                                                "cell": "c791d6cf-668d-4830-9862-ce81a3c8ddd4",
                                                "port": "left"
                                              },
                                              "target": {
                                                "cell": "6d250261-84d8-41c7-a588-ad8481f54d4c",
                                                "port": "right"
                                              },
                                              "labels": [
                                                {
                                                  "attrs": {
                                                    "label": {
                                                      "text": "失败"
                                                    }
                                                  }
                                                }
                                              ]
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "42162af8-b179-49af-9576-65b5c23cb65b",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 9,
                                              "source": {
                                                "cell": "c791d6cf-668d-4830-9862-ce81a3c8ddd4",
                                                "port": "right"
                                              },
                                              "target": {
                                                "cell": "8caa94c1-97f2-4eb6-a25b-43e79329c5b4",
                                                "port": "left"
                                              },
                                              "labels": [
                                                {
                                                  "attrs": {
                                                    "label": {
                                                      "text": "成功"
                                                    }
                                                  }
                                                }
                                              ]
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "6bdd71d5-e584-44d1-b7a3-b876796d9363",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 10,
                                              "source": {
                                                "cell": "8caa94c1-97f2-4eb6-a25b-43e79329c5b4",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "9eed3984-6800-49b9-8843-22dcdb262eed",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "a4d4a26e-2da1-4b78-83bb-1bcb820e23ef",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 11,
                                              "source": {
                                                "cell": "6d250261-84d8-41c7-a588-ad8481f54d4c",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "9eed3984-6800-49b9-8843-22dcdb262eed",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          }
                                        ]
                                      }
                                    ]
                                  },
                                  "x-validator": [],
                                  "x-reactions": {
                                    "dependencies": [],
                                    "fulfill": {
                                      "state": {},
                                      "run": "$self.visible = [null, '', 'DRAFT'].includes(\r\n  $self.query('.approveStatus').get('value')\r\n)"
                                    }
                                  },
                                  "x-designable-id": "1eqc2iqd26j",
                                  "x-index": 1
                                },
                                "q934dg9nu8k": {
                                  "type": "void",
                                  "title": "审批",
                                  "x-component": "Button",
                                  "x-component-props": {
                                    "type": "text",
                                    "size": "small",
                                    "plain": false,
                                    "round": false,
                                    "circle": false,
                                    "style": {
                                      "opacity": 1
                                    },
                                    "@click": "{{(...args) => {\n        return new $LogicEngineRuntime({\n          nodes: [{\"id\":\"3d3f2ef7-0ac5-4d03-bb7a-909e8b2cd506\",\"shape\":{\"name\":\"logic-start\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":449.5,\"y\":0},\"visible\":true,\"zIndex\":1},\"data\":{\"title\":\"开始\",\"value\":null}},{\"id\":\"53aaa070-32a3-4fb9-b8bf-679773f36880\",\"shape\":{\"name\":\"logic-end\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":449.5,\"y\":518},\"visible\":true,\"zIndex\":2},\"data\":{\"title\":\"结束\",\"value\":null}},{\"id\":\"289fd540-80d8-45ca-80df-36ee2a277b89\",\"shape\":{\"name\":\"logic-page-open\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":434,\"y\":259},\"visible\":true,\"zIndex\":3},\"data\":{\"title\":\"打开\",\"value\":{\"scope\":[],\"refresh\":true,\"pageId\":\"FORM\",\"pageStatus\":\"view\",\"openWith\":\"tab\",\"tabsFieldDesignerId\":\"wcyne8a8yag\",\"pageTitle\":\"{{$table.getRowByIndex($self.index).effectFormNumber}}\"}},\"executeFunction\":\"function logicNode(ctx) {\\n  $designable.query('wcyne8a8yag').take((field) => {\\n        const pageConfig = $designPages['FORM']\\n        const pageParams = {\\n  primaryKeyValue: $$safeGetScope('$table', $root)\\n    ? $queryEngineConfig.getPrimaryKeyValueByRecord(\\n      $$safeGetScope('$table', $root).getRowByIndex($self.index)\\n    )\\n    : undefined,\\n  \\n}\\n\\n        field.invoke('addTab', {\\n          label: String($table.getRowByIndex($self.index).effectFormNumber),\\n          name: \\n            pageConfig.value\\n          ,\\n          closable: true,\\n          \\n          onClose: () => {\\n            $queryEngine.state.paginationManagement.refresh();\\n          },\\n          \\n          scope: {\\n      $pageParams: pageParams,\\n      $readOnly: true,\\n    },\\n          schema: pageConfig.schema,\\n          form: pageConfig.form,\\n        })\\n      })\\n      \\n}\"},{\"id\":\"da00c296-d43a-4d0d-b280-ca3c9ced7ac2\",\"shape\":{\"name\":\"edge\",\"zIndex\":4,\"source\":{\"cell\":\"3d3f2ef7-0ac5-4d03-bb7a-909e8b2cd506\",\"port\":\"bottom\"},\"target\":{\"cell\":\"289fd540-80d8-45ca-80df-36ee2a277b89\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"4f5deb3a-c410-420c-a43a-ae377187fbf5\",\"shape\":{\"name\":\"edge\",\"zIndex\":5,\"source\":{\"cell\":\"289fd540-80d8-45ca-80df-36ee2a277b89\",\"port\":\"bottom\"},\"target\":{\"cell\":\"53aaa070-32a3-4fb9-b8bf-679773f36880\",\"port\":\"top\"}},\"data\":{\"value\":null}}],\n          payload: args,\n          scope: {\n            ...$root,\n            $designPages: $designPages,\n          }\n        }).run()\n      }}}"
                                  },
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-designer-extend": {
                                    "events": [
                                      {
                                        "title": "打开",
                                        "name": "click",
                                        "type": "logic",
                                        "args": [
                                          {
                                            "id": "3d3f2ef7-0ac5-4d03-bb7a-909e8b2cd506",
                                            "shape": {
                                              "name": "logic-start",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 449.5,
                                                "y": 0
                                              },
                                              "visible": true,
                                              "zIndex": 1
                                            },
                                            "data": {
                                              "title": "开始",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "53aaa070-32a3-4fb9-b8bf-679773f36880",
                                            "shape": {
                                              "name": "logic-end",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 449.5,
                                                "y": 518
                                              },
                                              "visible": true,
                                              "zIndex": 2
                                            },
                                            "data": {
                                              "title": "结束",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "289fd540-80d8-45ca-80df-36ee2a277b89",
                                            "shape": {
                                              "name": "logic-page-open",
                                              "size": {
                                                "width": 86,
                                                "height": 30
                                              },
                                              "position": {
                                                "x": 434,
                                                "y": 259
                                              },
                                              "visible": true,
                                              "zIndex": 3
                                            },
                                            "data": {
                                              "title": "打开",
                                              "value": {
                                                "scope": [],
                                                "refresh": true,
                                                "pageId": "FORM",
                                                "pageStatus": "view",
                                                "openWith": "tab",
                                                "tabsFieldDesignerId": "wcyne8a8yag",
                                                "pageTitle": "{{$table.getRowByIndex($self.index).effectFormNumber}}"
                                              }
                                            },
                                            "executeFunction": "function logicNode(ctx) {\n  $designable.query('wcyne8a8yag').take((field) => {\n        const pageConfig = $designPages['FORM']\n        const pageParams = {\n  primaryKeyValue: $$safeGetScope('$table', $root)\n    ? $queryEngineConfig.getPrimaryKeyValueByRecord(\n      $$safeGetScope('$table', $root).getRowByIndex($self.index)\n    )\n    : undefined,\n  \n}\n\n        field.invoke('addTab', {\n          label: String($table.getRowByIndex($self.index).effectFormNumber),\n          name: \n            pageConfig.value\n          ,\n          closable: true,\n          \n          onClose: () => {\n            $queryEngine.state.paginationManagement.refresh();\n          },\n          \n          scope: {\n      $pageParams: pageParams,\n      $readOnly: true,\n    },\n          schema: pageConfig.schema,\n          form: pageConfig.form,\n        })\n      })\n      \n}"
                                          },
                                          {
                                            "id": "da00c296-d43a-4d0d-b280-ca3c9ced7ac2",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 4,
                                              "source": {
                                                "cell": "3d3f2ef7-0ac5-4d03-bb7a-909e8b2cd506",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "289fd540-80d8-45ca-80df-36ee2a277b89",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "4f5deb3a-c410-420c-a43a-ae377187fbf5",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 5,
                                              "source": {
                                                "cell": "289fd540-80d8-45ca-80df-36ee2a277b89",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "53aaa070-32a3-4fb9-b8bf-679773f36880",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          }
                                        ]
                                      }
                                    ]
                                  },
                                  "x-validator": [],
                                  "x-reactions": {
                                    "dependencies": [],
                                    "fulfill": {
                                      "state": {},
                                      "run": "$self.visible = [''].includes(\r\n  $self.query('.approveStatus').get('value')\r\n)"
                                    }
                                  },
                                  "x-designable-id": "q934dg9nu8k",
                                  "x-index": 2
                                },
                                "gnqsw8pndxw": {
                                  "type": "void",
                                  "title": "废弃",
                                  "x-component": "Button",
                                  "x-component-props": {
                                    "type": "text",
                                    "size": "small",
                                    "plain": false,
                                    "round": false,
                                    "circle": false,
                                    "style": {
                                      "opacity": 1
                                    },
                                    "@click": "{{(...args) => {\n        return new $LogicEngineRuntime({\n          nodes: [{\"id\":\"34f4771c-d2cd-44d9-89e1-12d3c619b8d6\",\"shape\":{\"name\":\"logic-start\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":508,\"y\":0},\"visible\":true,\"zIndex\":1},\"data\":{\"title\":\"开始\",\"value\":null}},{\"id\":\"ff068446-80a6-475c-bc8a-430e5c0292a3\",\"shape\":{\"name\":\"logic-end\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":508,\"y\":522.1999999999999},\"visible\":true,\"zIndex\":2},\"data\":{\"title\":\"结束\",\"value\":null}},{\"id\":\"e324a5b2-9b13-40e7-a315-40a5ff42aae0\",\"shape\":{\"name\":\"logic-page-open\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":493,\"y\":224},\"visible\":true,\"zIndex\":3},\"data\":{\"title\":\"打开\",\"value\":{\"scope\":[],\"refresh\":true,\"pageId\":\"FORM\",\"pageStatus\":\"view\",\"openWith\":\"tab\",\"tabsFieldDesignerId\":\"wcyne8a8yag\"}},\"executeFunction\":\"function logicNode(ctx) {\\n  $designable.query('wcyne8a8yag').take((field) => {\\n        const pageConfig = $designPages['FORM']\\n        const pageParams = {\\n  primaryKeyValue: $$safeGetScope('$table', $root)\\n    ? $queryEngineConfig.getPrimaryKeyValueByRecord(\\n      $$safeGetScope('$table', $root).getRowByIndex($self.index)\\n    )\\n    : undefined,\\n  \\n}\\n\\n        field.invoke('addTab', {\\n          label: pageConfig.label,\\n          name: \\n            pageConfig.value\\n          ,\\n          closable: true,\\n          \\n          onClose: () => {\\n            $queryEngine.state.paginationManagement.refresh();\\n          },\\n          \\n          scope: {\\n      $pageParams: pageParams,\\n      $readOnly: true,\\n    },\\n          schema: pageConfig.schema,\\n          form: pageConfig.form,\\n        })\\n      })\\n      \\n}\"},{\"id\":\"cb196f87-4dfd-413b-bed2-7a39afc0d6ac\",\"shape\":{\"name\":\"edge\",\"zIndex\":4,\"source\":{\"cell\":\"34f4771c-d2cd-44d9-89e1-12d3c619b8d6\",\"port\":\"bottom\"},\"target\":{\"cell\":\"e324a5b2-9b13-40e7-a315-40a5ff42aae0\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"50350182-a770-4ad5-bbe3-e041c9425803\",\"shape\":{\"name\":\"edge\",\"zIndex\":5,\"source\":{\"cell\":\"e324a5b2-9b13-40e7-a315-40a5ff42aae0\",\"port\":\"bottom\"},\"target\":{\"cell\":\"ff068446-80a6-475c-bc8a-430e5c0292a3\",\"port\":\"top\"}},\"data\":{\"value\":null}}],\n          payload: args,\n          scope: {\n            ...$root,\n            $designPages: $designPages,\n          }\n        }).run()\n      }}}"
                                  },
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-designer-extend": {
                                    "events": [
                                      {
                                        "title": "点击废弃",
                                        "name": "click",
                                        "type": "logic",
                                        "args": [
                                          {
                                            "id": "34f4771c-d2cd-44d9-89e1-12d3c619b8d6",
                                            "shape": {
                                              "name": "logic-start",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 508,
                                                "y": 0
                                              },
                                              "visible": true,
                                              "zIndex": 1
                                            },
                                            "data": {
                                              "title": "开始",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "ff068446-80a6-475c-bc8a-430e5c0292a3",
                                            "shape": {
                                              "name": "logic-end",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 508,
                                                "y": 522.1999999999999
                                              },
                                              "visible": true,
                                              "zIndex": 2
                                            },
                                            "data": {
                                              "title": "结束",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "e324a5b2-9b13-40e7-a315-40a5ff42aae0",
                                            "shape": {
                                              "name": "logic-page-open",
                                              "size": {
                                                "width": 86,
                                                "height": 30
                                              },
                                              "position": {
                                                "x": 493,
                                                "y": 224
                                              },
                                              "visible": true,
                                              "zIndex": 3
                                            },
                                            "data": {
                                              "title": "打开",
                                              "value": {
                                                "scope": [],
                                                "refresh": true,
                                                "pageId": "FORM",
                                                "pageStatus": "view",
                                                "openWith": "tab",
                                                "tabsFieldDesignerId": "wcyne8a8yag"
                                              }
                                            },
                                            "executeFunction": "function logicNode(ctx) {\n  $designable.query('wcyne8a8yag').take((field) => {\n        const pageConfig = $designPages['FORM']\n        const pageParams = {\n  primaryKeyValue: $$safeGetScope('$table', $root)\n    ? $queryEngineConfig.getPrimaryKeyValueByRecord(\n      $$safeGetScope('$table', $root).getRowByIndex($self.index)\n    )\n    : undefined,\n  \n}\n\n        field.invoke('addTab', {\n          label: pageConfig.label,\n          name: \n            pageConfig.value\n          ,\n          closable: true,\n          \n          onClose: () => {\n            $queryEngine.state.paginationManagement.refresh();\n          },\n          \n          scope: {\n      $pageParams: pageParams,\n      $readOnly: true,\n    },\n          schema: pageConfig.schema,\n          form: pageConfig.form,\n        })\n      })\n      \n}"
                                          },
                                          {
                                            "id": "cb196f87-4dfd-413b-bed2-7a39afc0d6ac",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 4,
                                              "source": {
                                                "cell": "34f4771c-d2cd-44d9-89e1-12d3c619b8d6",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "e324a5b2-9b13-40e7-a315-40a5ff42aae0",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "50350182-a770-4ad5-bbe3-e041c9425803",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 5,
                                              "source": {
                                                "cell": "e324a5b2-9b13-40e7-a315-40a5ff42aae0",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "ff068446-80a6-475c-bc8a-430e5c0292a3",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          }
                                        ]
                                      }
                                    ]
                                  },
                                  "x-validator": [],
                                  "x-reactions": {
                                    "dependencies": [],
                                    "fulfill": {
                                      "state": {},
                                      "run": "$self.visible = ['WITHDRAW', 'REJECTED'].includes(\r\n  $self.query('.approveStatus').get('value')\r\n)"
                                    }
                                  },
                                  "x-designable-id": "gnqsw8pndxw",
                                  "x-index": 3
                                }
                              }
                            }
                          }
                        },
                        "xdg5fipuqhq": {
                          "type": "void",
                          "x-component": "RenderTable.Column",
                          "x-component-props": {
                            "title": "创建日期",
                            "sortable": true,
                            "resizable": true,
                            "visible": true,
                            "align": "left",
                            "headerAlign": "left",
                            "skipEditable": false,
                            "style": {
                              "opacity": 1
                            },
                            "minWidth": 150
                          },
                          "x-decorator-props": {
                            "style": {
                              "opacity": 1
                            }
                          },
                          "x-designer-extend": {},
                          "x-designable-id": "xdg5fipuqhq",
                          "x-index": 8,
                          "x-display": "visible",
                          "properties": {
                            "creationDate": {
                              "type": "string",
                              "title": "",
                              "x-decorator": "FormItem",
                              "x-component": "Input",
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                },
                                "feedbackLayout": "popover"
                              },
                              "x-designer-extend": {
                                "events": []
                              },
                              "x-component-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-validator": [],
                              "x-pattern": "readPretty",
                              "name": "creationDate",
                              "x-query-engine-sort": "desc",
                              "x-designable-id": "eg5tjrf6d4a",
                              "x-index": 0
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      },
      "x-decorator": "div",
      "x-designable-id": "rvbwcjff9xt"
    },
    "scope": {
      "$designPages": {
        "LLP1668195754958966786": {
          "label": "供应商查询",
          "value": "LLP1668195754958966786",
          "schema": {
            "sup_company_info_ide": {
              "type": "void",
              "x-component": "QueryEngine",
              "x-query-engine": {
                "service": "api-sup",
                "actions": {
                  "paginationQuery": {
                    "autoFormatResult": true,
                    "immediate": true
                  }
                },
                "pagination": {
                  "pageSize": 15
                }
              },
              "properties": {
                "73uw83nv9a1": {
                  "type": "object",
                  "x-component": "QueryFormByQueryEngine",
                  "x-component-props": {
                    "minWidth": 100,
                    "minColumns": 0,
                    "maxColumns": 3,
                    "columnGap": 10,
                    "rowGap": 5,
                    "colWrap": true,
                    "labelWidth": 80,
                    "immediateQueryForm": false,
                    "colon": true,
                    "style": {
                      "opacity": 1
                    }
                  },
                  "x-decorator-props": {
                    "style": "padding: 20px 20px 0;"
                  },
                  "x-index": 0,
                  "name": "73uw83nv9a1",
                  "x-designable-id": "6qll59tx7rv",
                  "x-validator": [],
                  "properties": {
                    "companyCode": {
                      "type": "string",
                      "title": "供应商编码",
                      "x-decorator": "FormItem",
                      "x-decorator-props": {
                        "style": {
                          "opacity": 1
                        },
                        "labelWidth": 80,
                        "feedbackLayout": "loose"
                      },
                      "x-designer-extend": {
                        "model": {
                          "businessType": "sccSupCompanyInfo",
                          "reference": null,
                          "modelId": "1668157712692989956",
                          "field": "companyCode",
                          "originalField": "companyCode"
                        },
                        "events": []
                      },
                      "x-component": "Input",
                      "x-component-props": {
                        "style": {
                          "opacity": 1
                        }
                      },
                      "x-index": 0,
                      "name": "companyCode",
                      "x-designable-id": "yr2llx3vmkv",
                      "x-validator": "",
                      "x-query-engine-query-operator": "contains"
                    },
                    "companyName": {
                      "type": "string",
                      "title": "供应商名称",
                      "x-decorator": "FormItem",
                      "x-decorator-props": {
                        "style": {
                          "opacity": 1
                        },
                        "labelWidth": 80,
                        "feedbackLayout": "loose"
                      },
                      "x-designer-extend": {
                        "model": {
                          "businessType": "sccSupCompanyInfo",
                          "reference": null,
                          "modelId": "1668157712692989956",
                          "field": "companyName",
                          "originalField": "companyName"
                        },
                        "events": []
                      },
                      "x-component": "Input",
                      "x-component-props": {
                        "style": {
                          "opacity": 1
                        }
                      },
                      "x-index": 1,
                      "name": "companyName",
                      "x-designable-id": "he3svdeas7x",
                      "x-validator": "",
                      "x-query-engine-query-operator": "contains"
                    }
                  }
                },
                "table": {
                  "type": "array",
                  "x-decorator": "FormItem",
                  "x-component": "RenderTable",
                  "x-validator": [],
                  "x-decorator-props": {
                    "style": "padding: 0 20px;",
                    "feedbackLayout": "loose"
                  },
                  "x-designable-id": "fam44va7bgl",
                  "x-component-props": {
                    "stripe": false,
                    "border": true,
                    "showHeader": true,
                    "openCustomTable": false,
                    "dblclickEditable": false,
                    "preColumns": [
                      "radio",
                      "seq"
                    ],
                    "editMode": "multi-row",
                    "style": "width:100%",
                    "show-header": true,
                    "size": "small",
                    "height": "500px",
                    "pagination": {
                      "pageSizes": [
                        15,
                        30,
                        60,
                        120,
                        300,
                        600,
                        1000,
                        1500
                      ],
                      "pageSize": 15
                    }
                  },
                  "x-designer-extend": {
                    "page": {
                      "pagination": true,
                      "pageSize": 15,
                      "pageSizes": "15,30,60,120,300,600,1000,1500"
                    },
                    "events": []
                  },
                  "x-index": 1,
                  "name": "table",
                  "properties": {
                    "companyId": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "ID",
                        "sortable": true,
                        "resizable": true,
                        "id": "ucqhqq6zuxy",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 0,
                      "name": "companyId",
                      "x-designable-id": "ucqhqq6zuxy",
                      "x-display": "hidden",
                      "properties": {
                        "companyId": {
                          "type": "number",
                          "x-component": "InputNumber",
                          "x-decorator": "FormItem",
                          "x-component-props": {
                            "controls-position": "right",
                            "controls": true,
                            "placeholder": "请输入",
                            "style": {
                              "opacity": 1
                            }
                          },
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "companyId",
                              "originalField": "companyId"
                            }
                          },
                          "x-index": 0,
                          "name": "companyId",
                          "x-designable-id": "avsr8zulr0o"
                        }
                      }
                    },
                    "status": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "单据状态（状态：拟定，已提交，已审批）",
                        "sortable": true,
                        "resizable": true,
                        "id": "il55zwf8hev",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 1,
                      "name": "status",
                      "x-designable-id": "il55zwf8hev",
                      "x-display": "hidden",
                      "properties": {
                        "status": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "status",
                              "originalField": "status"
                            }
                          },
                          "x-index": 0,
                          "name": "status",
                          "x-designable-id": "x4vwzp99q3l"
                        }
                      }
                    },
                    "companyCode": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "title": "供应商编码",
                        "sortable": true,
                        "resizable": true,
                        "id": "oc0y7brsmzw",
                        "visible": true,
                        "isGroup": false,
                        "align": "left",
                        "headerAlign": "left",
                        "skipEditable": false,
                        "style": {
                          "opacity": 1
                        },
                        "minWidth": 150
                      },
                      "x-index": 2,
                      "name": "companyCode",
                      "x-designable-id": "oc0y7brsmzw",
                      "x-display": "visible",
                      "x-decorator-props": {
                        "style": {
                          "opacity": 1
                        }
                      },
                      "x-designer-extend": {},
                      "properties": {
                        "companyCode": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "companyCode",
                              "originalField": "companyCode"
                            }
                          },
                          "x-index": 0,
                          "name": "companyCode",
                          "x-designable-id": "x70lobdiq0s"
                        }
                      }
                    },
                    "erpVendorId": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "ERP供应商Id（隆基新增，NSrm推送Erp成功后，Erp回传的erpVendorId）",
                        "sortable": true,
                        "resizable": true,
                        "id": "k8htln6jdv6",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 3,
                      "name": "erpVendorId",
                      "x-designable-id": "k8htln6jdv6",
                      "x-display": "hidden",
                      "properties": {
                        "erpVendorId": {
                          "type": "number",
                          "x-component": "InputNumber",
                          "x-decorator": "FormItem",
                          "x-component-props": {
                            "controls-position": "right",
                            "controls": true,
                            "placeholder": "请输入",
                            "style": {
                              "opacity": 1
                            }
                          },
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "erpVendorId",
                              "originalField": "erpVendorId"
                            }
                          },
                          "x-index": 0,
                          "name": "erpVendorId",
                          "x-designable-id": "7pc02e8repz"
                        }
                      }
                    },
                    "erpVendorCode": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "ERP供应商Code（隆基新增，NSrm推送Erp成功后，Erp回传的erpVendorCode）",
                        "sortable": true,
                        "resizable": true,
                        "id": "iyee5yh7cjq",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 4,
                      "name": "erpVendorCode",
                      "x-designable-id": "iyee5yh7cjq",
                      "x-display": "hidden",
                      "properties": {
                        "erpVendorCode": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "erpVendorCode",
                              "originalField": "erpVendorCode"
                            }
                          },
                          "x-index": 0,
                          "name": "erpVendorCode",
                          "x-designable-id": "jrmb3qf3be5"
                        }
                      }
                    },
                    "companyName": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "title": "供应商名称",
                        "sortable": true,
                        "resizable": true,
                        "id": "340gu02iwnd",
                        "visible": true,
                        "isGroup": false,
                        "align": "left",
                        "headerAlign": "left",
                        "skipEditable": false,
                        "style": {
                          "opacity": 1
                        },
                        "minWidth": 150
                      },
                      "x-index": 5,
                      "name": "companyName",
                      "x-designable-id": "340gu02iwnd",
                      "x-display": "visible",
                      "x-decorator-props": {
                        "style": {
                          "opacity": 1
                        }
                      },
                      "x-designer-extend": {},
                      "properties": {
                        "companyName": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {
                            "style": {
                              "opacity": 1
                            }
                          },
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "companyName",
                              "originalField": "companyName"
                            },
                            "events": []
                          },
                          "x-index": 0,
                          "name": "companyName",
                          "x-designable-id": "7p2qf9nlli1",
                          "x-decorator-props": {
                            "style": {
                              "opacity": 1
                            },
                            "feedbackLayout": "popover"
                          },
                          "x-validator": []
                        }
                      }
                    },
                    "companyEnName": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "企业名称（英文）",
                        "sortable": true,
                        "resizable": true,
                        "id": "lbunr8iwnya",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 6,
                      "name": "companyEnName",
                      "x-designable-id": "lbunr8iwnya",
                      "x-display": "hidden",
                      "properties": {
                        "companyEnName": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "companyEnName",
                              "originalField": "companyEnName"
                            }
                          },
                          "x-index": 0,
                          "name": "companyEnName",
                          "x-designable-id": "l9gm3j22mir"
                        }
                      }
                    },
                    "overseasRelation": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "境外关系",
                        "sortable": true,
                        "resizable": true,
                        "id": "kdqvyzk48kk",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 7,
                      "name": "overseasRelation",
                      "x-designable-id": "kdqvyzk48kk",
                      "x-display": "hidden",
                      "properties": {
                        "overseasRelation": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "overseasRelation",
                              "originalField": "overseasRelation"
                            }
                          },
                          "x-index": 0,
                          "name": "overseasRelation",
                          "x-designable-id": "237xoxy58vp"
                        }
                      }
                    },
                    "overseasRelationName": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "境外关系名称",
                        "sortable": true,
                        "resizable": true,
                        "id": "4j8i1qiyvxa",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 8,
                      "name": "overseasRelationName",
                      "x-designable-id": "4j8i1qiyvxa",
                      "x-display": "hidden",
                      "properties": {
                        "overseasRelationName": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "overseasRelationName",
                              "originalField": "overseasRelationName"
                            }
                          },
                          "x-index": 0,
                          "name": "overseasRelationName",
                          "x-designable-id": "269qief613f"
                        }
                      }
                    },
                    "companyType": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "企业性质",
                        "sortable": true,
                        "resizable": true,
                        "id": "7vbt9etg9ld",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 9,
                      "name": "companyType",
                      "x-designable-id": "7vbt9etg9ld",
                      "x-display": "hidden",
                      "properties": {
                        "companyType": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "companyType",
                              "originalField": "companyType"
                            }
                          },
                          "x-index": 0,
                          "name": "companyType",
                          "x-designable-id": "295bgizsy1x"
                        }
                      }
                    },
                    "companyTypeName": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "企业性质名称",
                        "sortable": true,
                        "resizable": true,
                        "id": "cfyp6vini5k",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 10,
                      "name": "companyTypeName",
                      "x-designable-id": "cfyp6vini5k",
                      "x-display": "hidden",
                      "properties": {
                        "companyTypeName": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "companyTypeName",
                              "originalField": "companyTypeName"
                            }
                          },
                          "x-index": 0,
                          "name": "companyTypeName",
                          "x-designable-id": "bi4kgo1i3r9"
                        }
                      }
                    },
                    "companyShortName": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "企业简称",
                        "sortable": true,
                        "resizable": true,
                        "id": "0bn5kqcaszt",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 11,
                      "name": "companyShortName",
                      "x-designable-id": "0bn5kqcaszt",
                      "x-display": "hidden",
                      "properties": {
                        "companyShortName": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "companyShortName",
                              "originalField": "companyShortName"
                            }
                          },
                          "x-index": 0,
                          "name": "companyShortName",
                          "x-designable-id": "90wpdos76tb"
                        }
                      }
                    },
                    "lcCode": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "统一社会信用代码",
                        "sortable": true,
                        "resizable": true,
                        "id": "b96xsgq8lp5",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 12,
                      "name": "lcCode",
                      "x-designable-id": "b96xsgq8lp5",
                      "x-display": "hidden",
                      "properties": {
                        "lcCode": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "lcCode",
                              "originalField": "lcCode"
                            }
                          },
                          "x-index": 0,
                          "name": "lcCode",
                          "x-designable-id": "068cc25y05h"
                        }
                      }
                    },
                    "businessLicenseFileId": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "上传(三证合一)文件ID",
                        "sortable": true,
                        "resizable": true,
                        "id": "935ag8gsqwa",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 13,
                      "name": "businessLicenseFileId",
                      "x-designable-id": "935ag8gsqwa",
                      "x-display": "hidden",
                      "properties": {
                        "businessLicenseFileId": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "businessLicenseFileId",
                              "originalField": "businessLicenseFileId"
                            }
                          },
                          "x-index": 0,
                          "name": "businessLicenseFileId",
                          "x-designable-id": "ueshu9opiqn"
                        }
                      }
                    },
                    "businessLicense": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "上传三证合一照片名称",
                        "sortable": true,
                        "resizable": true,
                        "id": "8tqewmstx4h",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 14,
                      "name": "businessLicense",
                      "x-designable-id": "8tqewmstx4h",
                      "x-display": "hidden",
                      "properties": {
                        "businessLicense": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "businessLicense",
                              "originalField": "businessLicense"
                            }
                          },
                          "x-index": 0,
                          "name": "businessLicense",
                          "x-designable-id": "cq29imcp7bx"
                        }
                      }
                    },
                    "idNumber": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "身份证号码",
                        "sortable": true,
                        "resizable": true,
                        "id": "gf4zj3o8xa5",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 15,
                      "name": "idNumber",
                      "x-designable-id": "gf4zj3o8xa5",
                      "x-display": "hidden",
                      "properties": {
                        "idNumber": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "idNumber",
                              "originalField": "idNumber"
                            }
                          },
                          "x-index": 0,
                          "name": "idNumber",
                          "x-designable-id": "j0wgl0u89xz"
                        }
                      }
                    },
                    "dunsCode": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "DUNS编号",
                        "sortable": true,
                        "resizable": true,
                        "id": "3i6vuyb2r9k",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 16,
                      "name": "dunsCode",
                      "x-designable-id": "3i6vuyb2r9k",
                      "x-display": "hidden",
                      "properties": {
                        "dunsCode": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "dunsCode",
                              "originalField": "dunsCode"
                            }
                          },
                          "x-index": 0,
                          "name": "dunsCode",
                          "x-designable-id": "cfbj8m7rmd3"
                        }
                      }
                    },
                    "companyStatus": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "经营状态",
                        "sortable": true,
                        "resizable": true,
                        "id": "x9m9i09yn16",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 17,
                      "name": "companyStatus",
                      "x-designable-id": "x9m9i09yn16",
                      "x-display": "hidden",
                      "properties": {
                        "companyStatus": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "companyStatus",
                              "originalField": "companyStatus"
                            }
                          },
                          "x-index": 0,
                          "name": "companyStatus",
                          "x-designable-id": "6isdhsnvxxl"
                        }
                      }
                    },
                    "companyStatusName": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "经营状态名称",
                        "sortable": true,
                        "resizable": true,
                        "id": "f2e3ui5rn4j",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 18,
                      "name": "companyStatusName",
                      "x-designable-id": "f2e3ui5rn4j",
                      "x-display": "hidden",
                      "properties": {
                        "companyStatusName": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "companyStatusName",
                              "originalField": "companyStatusName"
                            }
                          },
                          "x-index": 0,
                          "name": "companyStatusName",
                          "x-designable-id": "gmh8tf9pjx0"
                        }
                      }
                    },
                    "legalPerson": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "法定代表人",
                        "sortable": true,
                        "resizable": true,
                        "id": "1bj1x4h1j3n",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 19,
                      "name": "legalPerson",
                      "x-designable-id": "1bj1x4h1j3n",
                      "x-display": "hidden",
                      "properties": {
                        "legalPerson": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "legalPerson",
                              "originalField": "legalPerson"
                            }
                          },
                          "x-index": 0,
                          "name": "legalPerson",
                          "x-designable-id": "f79sez1xtsw"
                        }
                      }
                    },
                    "registeredCapital": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "注册资金",
                        "sortable": true,
                        "resizable": true,
                        "id": "2w1b6a2l195",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 20,
                      "name": "registeredCapital",
                      "x-designable-id": "2w1b6a2l195",
                      "x-display": "hidden",
                      "properties": {
                        "registeredCapital": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "registeredCapital",
                              "originalField": "registeredCapital"
                            }
                          },
                          "x-index": 0,
                          "name": "registeredCapital",
                          "x-designable-id": "ohhmv9v5slf"
                        }
                      }
                    },
                    "companyCreationDate": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "企业成立日期",
                        "sortable": true,
                        "resizable": true,
                        "id": "kdh6jv5xonp",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 21,
                      "name": "companyCreationDate",
                      "x-designable-id": "kdh6jv5xonp",
                      "x-display": "hidden",
                      "properties": {
                        "companyCreationDate": {
                          "type": "string",
                          "x-component": "DatePicker",
                          "x-decorator": "FormItem",
                          "x-component-props": {
                            "editable": true,
                            "clearable": true,
                            "placeholder": "请选择"
                          },
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "companyCreationDate",
                              "originalField": "companyCreationDate"
                            }
                          },
                          "x-index": 0,
                          "name": "companyCreationDate",
                          "x-designable-id": "61c9bq7pbi2"
                        }
                      }
                    },
                    "businessStartDate": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "营业日期开始",
                        "sortable": true,
                        "resizable": true,
                        "id": "r8c3otg071j",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 22,
                      "name": "businessStartDate",
                      "x-designable-id": "r8c3otg071j",
                      "x-display": "hidden",
                      "properties": {
                        "businessStartDate": {
                          "type": "string",
                          "x-component": "DatePicker",
                          "x-decorator": "FormItem",
                          "x-component-props": {
                            "editable": true,
                            "clearable": true,
                            "placeholder": "请选择"
                          },
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "businessStartDate",
                              "originalField": "businessStartDate"
                            }
                          },
                          "x-index": 0,
                          "name": "businessStartDate",
                          "x-designable-id": "b5xlzz73e6e"
                        }
                      }
                    },
                    "businessEndDate": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "营业日期结束",
                        "sortable": true,
                        "resizable": true,
                        "id": "dcds4r9t2q3",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 23,
                      "name": "businessEndDate",
                      "x-designable-id": "dcds4r9t2q3",
                      "x-display": "hidden",
                      "properties": {
                        "businessEndDate": {
                          "type": "string",
                          "x-component": "DatePicker",
                          "x-decorator": "FormItem",
                          "x-component-props": {
                            "editable": true,
                            "clearable": true,
                            "placeholder": "请选择"
                          },
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "businessEndDate",
                              "originalField": "businessEndDate"
                            }
                          },
                          "x-index": 0,
                          "name": "businessEndDate",
                          "x-designable-id": "7h8l6rlvrjh"
                        }
                      }
                    },
                    "companyRegisteredDate": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "企业注册日期",
                        "sortable": true,
                        "resizable": true,
                        "id": "tzxmw9mnuy5",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 24,
                      "name": "companyRegisteredDate",
                      "x-designable-id": "tzxmw9mnuy5",
                      "x-display": "hidden",
                      "properties": {
                        "companyRegisteredDate": {
                          "type": "string",
                          "x-component": "DatePicker",
                          "x-decorator": "FormItem",
                          "x-component-props": {
                            "editable": true,
                            "clearable": true,
                            "placeholder": "请选择"
                          },
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "companyRegisteredDate",
                              "originalField": "companyRegisteredDate"
                            }
                          },
                          "x-index": 0,
                          "name": "companyRegisteredDate",
                          "x-designable-id": "oonbmv34kxs"
                        }
                      }
                    },
                    "companyCountry": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "营业地址（国家/地区）",
                        "sortable": true,
                        "resizable": true,
                        "id": "wgwnjao96yb",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 25,
                      "name": "companyCountry",
                      "x-designable-id": "wgwnjao96yb",
                      "x-display": "hidden",
                      "properties": {
                        "companyCountry": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "companyCountry",
                              "originalField": "companyCountry"
                            }
                          },
                          "x-index": 0,
                          "name": "companyCountry",
                          "x-designable-id": "znvx16wtvg4"
                        }
                      }
                    },
                    "companyProvince": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "营业地址（省份/州）",
                        "sortable": true,
                        "resizable": true,
                        "id": "c1myn87bryy",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 26,
                      "name": "companyProvince",
                      "x-designable-id": "c1myn87bryy",
                      "x-display": "hidden",
                      "properties": {
                        "companyProvince": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "companyProvince",
                              "originalField": "companyProvince"
                            }
                          },
                          "x-index": 0,
                          "name": "companyProvince",
                          "x-designable-id": "m9ohltxv3kh"
                        }
                      }
                    },
                    "companyCity": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "营业地址（城市）",
                        "sortable": true,
                        "resizable": true,
                        "id": "pdv16xfwofp",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 27,
                      "name": "companyCity",
                      "x-designable-id": "pdv16xfwofp",
                      "x-display": "hidden",
                      "properties": {
                        "companyCity": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "companyCity",
                              "originalField": "companyCity"
                            }
                          },
                          "x-index": 0,
                          "name": "companyCity",
                          "x-designable-id": "ps6utqvxl22"
                        }
                      }
                    },
                    "companyAddress": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "详细地址",
                        "sortable": true,
                        "resizable": true,
                        "id": "f6088ej102c",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 28,
                      "name": "companyAddress",
                      "x-designable-id": "f6088ej102c",
                      "x-display": "hidden",
                      "properties": {
                        "companyAddress": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "companyAddress",
                              "originalField": "companyAddress"
                            }
                          },
                          "x-index": 0,
                          "name": "companyAddress",
                          "x-designable-id": "i7dc23rdqmg"
                        }
                      }
                    },
                    "materialCategory": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "可供物料品类",
                        "sortable": true,
                        "resizable": true,
                        "id": "xmhxhrb7nqa",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 29,
                      "name": "materialCategory",
                      "x-designable-id": "xmhxhrb7nqa",
                      "x-display": "hidden",
                      "properties": {
                        "materialCategory": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "materialCategory",
                              "originalField": "materialCategory"
                            }
                          },
                          "x-index": 0,
                          "name": "materialCategory",
                          "x-designable-id": "wfjnfdxkfxo"
                        }
                      }
                    },
                    "businessScope": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "经营范围",
                        "sortable": true,
                        "resizable": true,
                        "id": "e2defhirxuf",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 30,
                      "name": "businessScope",
                      "x-designable-id": "e2defhirxuf",
                      "x-display": "hidden",
                      "properties": {
                        "businessScope": {
                          "type": "string",
                          "x-component": "Input.TextArea",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "businessScope",
                              "originalField": "businessScope"
                            }
                          },
                          "x-index": 0,
                          "name": "businessScope",
                          "x-designable-id": "orxddpidwoa"
                        }
                      }
                    },
                    "registrationAuthority": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "登记机关",
                        "sortable": true,
                        "resizable": true,
                        "id": "z3hr7vn9fsj",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 31,
                      "name": "registrationAuthority",
                      "x-designable-id": "z3hr7vn9fsj",
                      "x-display": "hidden",
                      "properties": {
                        "registrationAuthority": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "registrationAuthority",
                              "originalField": "registrationAuthority"
                            }
                          },
                          "x-index": 0,
                          "name": "registrationAuthority",
                          "x-designable-id": "5nt3fjnhr4t"
                        }
                      }
                    },
                    "registCurrency": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "注册资金币种",
                        "sortable": true,
                        "resizable": true,
                        "id": "3ahr04dy9r1",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 32,
                      "name": "registCurrency",
                      "x-designable-id": "3ahr04dy9r1",
                      "x-display": "hidden",
                      "properties": {
                        "registCurrency": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "registCurrency",
                              "originalField": "registCurrency"
                            }
                          },
                          "x-index": 0,
                          "name": "registCurrency",
                          "x-designable-id": "5i2g5d7fnle"
                        }
                      }
                    },
                    "registCurrencyName": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "注册资金币种名称",
                        "sortable": true,
                        "resizable": true,
                        "id": "hsl96w38l6t",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 33,
                      "name": "registCurrencyName",
                      "x-designable-id": "hsl96w38l6t",
                      "x-display": "hidden",
                      "properties": {
                        "registCurrencyName": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "registCurrencyName",
                              "originalField": "registCurrencyName"
                            }
                          },
                          "x-index": 0,
                          "name": "registCurrencyName",
                          "x-designable-id": "vio9x3296yl"
                        }
                      }
                    },
                    "isBacklist": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "是否黑名单",
                        "sortable": true,
                        "resizable": true,
                        "id": "qpih5ix7o8k",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 34,
                      "name": "isBacklist",
                      "x-designable-id": "qpih5ix7o8k",
                      "x-display": "hidden",
                      "properties": {
                        "isBacklist": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "isBacklist",
                              "originalField": "isBacklist"
                            }
                          },
                          "x-index": 0,
                          "name": "isBacklist",
                          "x-designable-id": "o06syb3o03c"
                        }
                      }
                    },
                    "vendorClassification": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "供应商分级（字典编码：SUPPLIER_CLASSIFICATION，STRATEGIC_SUPPLIER：战略供应商；PARTNER_SUPPLIERS：合作供应商）",
                        "sortable": true,
                        "resizable": true,
                        "id": "qgiz2lzot2q",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 35,
                      "name": "vendorClassification",
                      "x-designable-id": "qgiz2lzot2q",
                      "x-display": "hidden",
                      "properties": {
                        "vendorClassification": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "vendorClassification",
                              "originalField": "vendorClassification"
                            }
                          },
                          "x-index": 0,
                          "name": "vendorClassification",
                          "x-designable-id": "k9bczonoj65"
                        }
                      }
                    },
                    "ifLongPeriod": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "是否长期供应商（Y：是，N：否，默认否）",
                        "sortable": true,
                        "resizable": true,
                        "id": "09du827excc",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 36,
                      "name": "ifLongPeriod",
                      "x-designable-id": "09du827excc",
                      "x-display": "hidden",
                      "properties": {
                        "ifLongPeriod": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "ifLongPeriod",
                              "originalField": "ifLongPeriod"
                            }
                          },
                          "x-index": 0,
                          "name": "ifLongPeriod",
                          "x-designable-id": "pvhh0f3ctcr"
                        }
                      }
                    },
                    "backlistUpdatedBy": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "黑名单更新人",
                        "sortable": true,
                        "resizable": true,
                        "id": "aa2ie2rdymy",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 37,
                      "name": "backlistUpdatedBy",
                      "x-designable-id": "aa2ie2rdymy",
                      "x-display": "hidden",
                      "properties": {
                        "backlistUpdatedBy": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "backlistUpdatedBy",
                              "originalField": "backlistUpdatedBy"
                            }
                          },
                          "x-index": 0,
                          "name": "backlistUpdatedBy",
                          "x-designable-id": "zgayi356b79"
                        }
                      }
                    },
                    "backlistUpdatedDate": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "黑名单更新时间",
                        "sortable": true,
                        "resizable": true,
                        "id": "0uxmvol1p2o",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 38,
                      "name": "backlistUpdatedDate",
                      "x-designable-id": "0uxmvol1p2o",
                      "x-display": "hidden",
                      "properties": {
                        "backlistUpdatedDate": {
                          "type": "string",
                          "x-component": "DatePicker",
                          "x-decorator": "FormItem",
                          "x-component-props": {
                            "type": "datetime"
                          },
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "backlistUpdatedDate",
                              "originalField": "backlistUpdatedDate"
                            }
                          },
                          "x-index": 0,
                          "name": "backlistUpdatedDate",
                          "x-designable-id": "2yh56415ltc"
                        }
                      }
                    },
                    "dataSources": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "1、注册用户 2、绿色通道用户",
                        "sortable": true,
                        "resizable": true,
                        "id": "zrnqohke8ir",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 39,
                      "name": "dataSources",
                      "x-designable-id": "zrnqohke8ir",
                      "x-display": "hidden",
                      "properties": {
                        "dataSources": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "dataSources",
                              "originalField": "dataSources"
                            }
                          },
                          "x-index": 0,
                          "name": "dataSources",
                          "x-designable-id": "3ky5p513fm0"
                        }
                      }
                    },
                    "blackListEffectiveDate": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "黑名单生效日期",
                        "sortable": true,
                        "resizable": true,
                        "id": "17v3pf5clcm",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 40,
                      "name": "blackListEffectiveDate",
                      "x-designable-id": "17v3pf5clcm",
                      "x-display": "hidden",
                      "properties": {
                        "blackListEffectiveDate": {
                          "type": "string",
                          "x-component": "DatePicker",
                          "x-decorator": "FormItem",
                          "x-component-props": {
                            "type": "datetime"
                          },
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "blackListEffectiveDate",
                              "originalField": "blackListEffectiveDate"
                            }
                          },
                          "x-index": 0,
                          "name": "blackListEffectiveDate",
                          "x-designable-id": "g0ne395jgdf"
                        }
                      }
                    },
                    "approvingDate": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "核准日期",
                        "sortable": true,
                        "resizable": true,
                        "id": "bbwoe9khxdm",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 41,
                      "name": "approvingDate",
                      "x-designable-id": "bbwoe9khxdm",
                      "x-display": "hidden",
                      "properties": {
                        "approvingDate": {
                          "type": "string",
                          "x-component": "DatePicker",
                          "x-decorator": "FormItem",
                          "x-component-props": {
                            "editable": true,
                            "clearable": true,
                            "placeholder": "请选择"
                          },
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "approvingDate",
                              "originalField": "approvingDate"
                            }
                          },
                          "x-index": 0,
                          "name": "approvingDate",
                          "x-designable-id": "tychji5jqb7"
                        }
                      }
                    },
                    "approvalInfo": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "审批信息",
                        "sortable": true,
                        "resizable": true,
                        "id": "4aq02h4dpev",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 42,
                      "name": "approvalInfo",
                      "x-designable-id": "4aq02h4dpev",
                      "x-display": "hidden",
                      "properties": {
                        "approvalInfo": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "approvalInfo",
                              "originalField": "approvalInfo"
                            }
                          },
                          "x-index": 0,
                          "name": "approvalInfo",
                          "x-designable-id": "tfh7wg9lsfs"
                        }
                      }
                    },
                    "applicationNumber": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "注册申请号",
                        "sortable": true,
                        "resizable": true,
                        "id": "bpepqg461n2",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 43,
                      "name": "applicationNumber",
                      "x-designable-id": "bpepqg461n2",
                      "x-display": "hidden",
                      "properties": {
                        "applicationNumber": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "applicationNumber",
                              "originalField": "applicationNumber"
                            }
                          },
                          "x-index": 0,
                          "name": "applicationNumber",
                          "x-designable-id": "uq47n36mn72"
                        }
                      }
                    },
                    "applicationDate": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "注册申请日期",
                        "sortable": true,
                        "resizable": true,
                        "id": "iz1gu8esfi1",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 44,
                      "name": "applicationDate",
                      "x-designable-id": "iz1gu8esfi1",
                      "x-display": "hidden",
                      "properties": {
                        "applicationDate": {
                          "type": "string",
                          "x-component": "DatePicker",
                          "x-decorator": "FormItem",
                          "x-component-props": {
                            "type": "datetime"
                          },
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "applicationDate",
                              "originalField": "applicationDate"
                            }
                          },
                          "x-index": 0,
                          "name": "applicationDate",
                          "x-designable-id": "2dj8y2838ut"
                        }
                      }
                    },
                    "approvedDate": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "审批日期/准入日期",
                        "sortable": true,
                        "resizable": true,
                        "id": "to4c5b2fvlg",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 45,
                      "name": "approvedDate",
                      "x-designable-id": "to4c5b2fvlg",
                      "x-display": "hidden",
                      "properties": {
                        "approvedDate": {
                          "type": "string",
                          "x-component": "DatePicker",
                          "x-decorator": "FormItem",
                          "x-component-props": {
                            "editable": true,
                            "clearable": true,
                            "placeholder": "请选择"
                          },
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "approvedDate",
                              "originalField": "approvedDate"
                            }
                          },
                          "x-index": 0,
                          "name": "approvedDate",
                          "x-designable-id": "sk1j4x6vltu"
                        }
                      }
                    },
                    "approvedBy": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "审批人",
                        "sortable": true,
                        "resizable": true,
                        "id": "f3gzdwrgyh2",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 46,
                      "name": "approvedBy",
                      "x-designable-id": "f3gzdwrgyh2",
                      "x-display": "hidden",
                      "properties": {
                        "approvedBy": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "approvedBy",
                              "originalField": "approvedBy"
                            }
                          },
                          "x-index": 0,
                          "name": "approvedBy",
                          "x-designable-id": "1l8x14owk4e"
                        }
                      }
                    },
                    "approver": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "审批人账号",
                        "sortable": true,
                        "resizable": true,
                        "id": "fu34z8ivydp",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 47,
                      "name": "approver",
                      "x-designable-id": "fu34z8ivydp",
                      "x-display": "hidden",
                      "properties": {
                        "approver": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "approver",
                              "originalField": "approver"
                            }
                          },
                          "x-index": 0,
                          "name": "approver",
                          "x-designable-id": "56fgq3mnf2k"
                        }
                      }
                    },
                    "statusName": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "状态名字",
                        "sortable": true,
                        "resizable": true,
                        "id": "4tjleuyneuy",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 48,
                      "name": "statusName",
                      "x-designable-id": "4tjleuyneuy",
                      "x-display": "hidden",
                      "properties": {
                        "statusName": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "statusName",
                              "originalField": "statusName"
                            }
                          },
                          "x-index": 0,
                          "name": "statusName",
                          "x-designable-id": "a0yj81b7xqm"
                        }
                      }
                    },
                    "ceeaCompanyWebsite": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "公司网站",
                        "sortable": true,
                        "resizable": true,
                        "id": "bv4wd988qci",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 49,
                      "name": "ceeaCompanyWebsite",
                      "x-designable-id": "bv4wd988qci",
                      "x-display": "hidden",
                      "properties": {
                        "ceeaCompanyWebsite": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "ceeaCompanyWebsite",
                              "originalField": "ceeaCompanyWebsite"
                            }
                          },
                          "x-index": 0,
                          "name": "ceeaCompanyWebsite",
                          "x-designable-id": "y2i90rc2ghn"
                        }
                      }
                    },
                    "ceeaAgentBrand": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "代理品牌",
                        "sortable": true,
                        "resizable": true,
                        "id": "76j89odyqma",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 50,
                      "name": "ceeaAgentBrand",
                      "x-designable-id": "76j89odyqma",
                      "x-display": "hidden",
                      "properties": {
                        "ceeaAgentBrand": {
                          "type": "string",
                          "x-component": "Input.TextArea",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "ceeaAgentBrand",
                              "originalField": "ceeaAgentBrand"
                            }
                          },
                          "x-index": 0,
                          "name": "ceeaAgentBrand",
                          "x-designable-id": "5gw0bnudee1"
                        }
                      }
                    },
                    "ceeaListedTime": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "上市时间",
                        "sortable": true,
                        "resizable": true,
                        "id": "chhvyyixfy8",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 51,
                      "name": "ceeaListedTime",
                      "x-designable-id": "chhvyyixfy8",
                      "x-display": "hidden",
                      "properties": {
                        "ceeaListedTime": {
                          "type": "string",
                          "x-component": "DatePicker",
                          "x-decorator": "FormItem",
                          "x-component-props": {
                            "editable": true,
                            "clearable": true,
                            "placeholder": "请选择"
                          },
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "ceeaListedTime",
                              "originalField": "ceeaListedTime"
                            }
                          },
                          "x-index": 0,
                          "name": "ceeaListedTime",
                          "x-designable-id": "e623dtbh8eg"
                        }
                      }
                    },
                    "ceeaIfListed": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "是否上市",
                        "sortable": true,
                        "resizable": true,
                        "id": "3s0qvf8fvyf",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 52,
                      "name": "ceeaIfListed",
                      "x-designable-id": "3s0qvf8fvyf",
                      "x-display": "hidden",
                      "properties": {
                        "ceeaIfListed": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "ceeaIfListed",
                              "originalField": "ceeaIfListed"
                            }
                          },
                          "x-index": 0,
                          "name": "ceeaIfListed",
                          "x-designable-id": "f9fs6mwwrqy"
                        }
                      }
                    },
                    "ceeaHasParentCompany": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "是否有母公司",
                        "sortable": true,
                        "resizable": true,
                        "id": "3zn7hd7n1r4",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 53,
                      "name": "ceeaHasParentCompany",
                      "x-designable-id": "3zn7hd7n1r4",
                      "x-display": "hidden",
                      "properties": {
                        "ceeaHasParentCompany": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "ceeaHasParentCompany",
                              "originalField": "ceeaHasParentCompany"
                            }
                          },
                          "x-index": 0,
                          "name": "ceeaHasParentCompany",
                          "x-designable-id": "3b0h14myqut"
                        }
                      }
                    },
                    "ceeaParentCompanyName": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "母公司名称",
                        "sortable": true,
                        "resizable": true,
                        "id": "y8phxchb38x",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 54,
                      "name": "ceeaParentCompanyName",
                      "x-designable-id": "y8phxchb38x",
                      "x-display": "hidden",
                      "properties": {
                        "ceeaParentCompanyName": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "ceeaParentCompanyName",
                              "originalField": "ceeaParentCompanyName"
                            }
                          },
                          "x-index": 0,
                          "name": "ceeaParentCompanyName",
                          "x-designable-id": "4s56cbd9vv0"
                        }
                      }
                    },
                    "ceeaParentCompanyLcCode": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "母公司统一信用代码",
                        "sortable": true,
                        "resizable": true,
                        "id": "0udvxfwe91j",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 55,
                      "name": "ceeaParentCompanyLcCode",
                      "x-designable-id": "0udvxfwe91j",
                      "x-display": "hidden",
                      "properties": {
                        "ceeaParentCompanyLcCode": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "ceeaParentCompanyLcCode",
                              "originalField": "ceeaParentCompanyLcCode"
                            }
                          },
                          "x-index": 0,
                          "name": "ceeaParentCompanyLcCode",
                          "x-designable-id": "ahyna7y7dve"
                        }
                      }
                    },
                    "ceeaPlantArea": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "厂房面积",
                        "sortable": true,
                        "resizable": true,
                        "id": "x4an053uz73",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 56,
                      "name": "ceeaPlantArea",
                      "x-designable-id": "x4an053uz73",
                      "x-display": "hidden",
                      "properties": {
                        "ceeaPlantArea": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "ceeaPlantArea",
                              "originalField": "ceeaPlantArea"
                            }
                          },
                          "x-index": 0,
                          "name": "ceeaPlantArea",
                          "x-designable-id": "jr6bv8ews21"
                        }
                      }
                    },
                    "ceeaPlantType": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "厂房性质",
                        "sortable": true,
                        "resizable": true,
                        "id": "kdc57hr8e0r",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 57,
                      "name": "ceeaPlantType",
                      "x-designable-id": "kdc57hr8e0r",
                      "x-display": "hidden",
                      "properties": {
                        "ceeaPlantType": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "ceeaPlantType",
                              "originalField": "ceeaPlantType"
                            }
                          },
                          "x-index": 0,
                          "name": "ceeaPlantType",
                          "x-designable-id": "qhegsvxsyl1"
                        }
                      }
                    },
                    "ceeaMainCategoryName": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "主营品类名称",
                        "sortable": true,
                        "resizable": true,
                        "id": "73as80e2lvm",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 58,
                      "name": "ceeaMainCategoryName",
                      "x-designable-id": "73as80e2lvm",
                      "x-display": "hidden",
                      "properties": {
                        "ceeaMainCategoryName": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "ceeaMainCategoryName",
                              "originalField": "ceeaMainCategoryName"
                            }
                          },
                          "x-index": 0,
                          "name": "ceeaMainCategoryName",
                          "x-designable-id": "d8icuf3e1nv"
                        }
                      }
                    },
                    "ceeaMainCategoryCode": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "主营品类编码",
                        "sortable": true,
                        "resizable": true,
                        "id": "yy69yabgauo",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 59,
                      "name": "ceeaMainCategoryCode",
                      "x-designable-id": "yy69yabgauo",
                      "x-display": "hidden",
                      "properties": {
                        "ceeaMainCategoryCode": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "ceeaMainCategoryCode",
                              "originalField": "ceeaMainCategoryCode"
                            }
                          },
                          "x-index": 0,
                          "name": "ceeaMainCategoryCode",
                          "x-designable-id": "lv0dea9siy2"
                        }
                      }
                    },
                    "ceeaMainCategoryId": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "主营品类ID",
                        "sortable": true,
                        "resizable": true,
                        "id": "2e7krzydygj",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 60,
                      "name": "ceeaMainCategoryId",
                      "x-designable-id": "2e7krzydygj",
                      "x-display": "hidden",
                      "properties": {
                        "ceeaMainCategoryId": {
                          "type": "number",
                          "x-component": "InputNumber",
                          "x-decorator": "FormItem",
                          "x-component-props": {
                            "controls-position": "right",
                            "controls": true
                          },
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "ceeaMainCategoryId",
                              "originalField": "ceeaMainCategoryId"
                            }
                          },
                          "x-index": 0,
                          "name": "ceeaMainCategoryId",
                          "x-designable-id": "7wv1mkmgkvu"
                        }
                      }
                    },
                    "ceeaCompanyIntro": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "公司简介",
                        "sortable": true,
                        "resizable": true,
                        "id": "n8snsnr2j6t",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 61,
                      "name": "ceeaCompanyIntro",
                      "x-designable-id": "n8snsnr2j6t",
                      "x-display": "hidden",
                      "properties": {
                        "ceeaCompanyIntro": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "ceeaCompanyIntro",
                              "originalField": "ceeaCompanyIntro"
                            }
                          },
                          "x-index": 0,
                          "name": "ceeaCompanyIntro",
                          "x-designable-id": "j7qr8jdwhw8"
                        }
                      }
                    },
                    "ceeaIndustryType": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "行业类型",
                        "sortable": true,
                        "resizable": true,
                        "id": "26bi3jhhvik",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 62,
                      "name": "ceeaIndustryType",
                      "x-designable-id": "26bi3jhhvik",
                      "x-display": "hidden",
                      "properties": {
                        "ceeaIndustryType": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "ceeaIndustryType",
                              "originalField": "ceeaIndustryType"
                            }
                          },
                          "x-index": 0,
                          "name": "ceeaIndustryType",
                          "x-designable-id": "x8hoteswpb9"
                        }
                      }
                    },
                    "ceeaBusinessModel": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "商业模式",
                        "sortable": true,
                        "resizable": true,
                        "id": "rzsiu61xqbu",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 63,
                      "name": "ceeaBusinessModel",
                      "x-designable-id": "rzsiu61xqbu",
                      "x-display": "hidden",
                      "properties": {
                        "ceeaBusinessModel": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "ceeaBusinessModel",
                              "originalField": "ceeaBusinessModel"
                            }
                          },
                          "x-index": 0,
                          "name": "ceeaBusinessModel",
                          "x-designable-id": "2hygrg5t6bs"
                        }
                      }
                    },
                    "ceeaSupBusinessType": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "供应商业务类型",
                        "sortable": true,
                        "resizable": true,
                        "id": "6va7ypmdg20",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 64,
                      "name": "ceeaSupBusinessType",
                      "x-designable-id": "6va7ypmdg20",
                      "x-display": "hidden",
                      "properties": {
                        "ceeaSupBusinessType": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "ceeaSupBusinessType",
                              "originalField": "ceeaSupBusinessType"
                            }
                          },
                          "x-index": 0,
                          "name": "ceeaSupBusinessType",
                          "x-designable-id": "src9ichpauq"
                        }
                      }
                    },
                    "email": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "有效",
                        "sortable": true,
                        "resizable": true,
                        "id": "1w3f6kqws1t",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 65,
                      "name": "email",
                      "x-designable-id": "1w3f6kqws1t",
                      "x-display": "hidden",
                      "properties": {
                        "email": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "email",
                              "originalField": "email"
                            }
                          },
                          "x-index": 0,
                          "name": "email",
                          "x-designable-id": "zfcs5817f1r"
                        }
                      }
                    },
                    "nickname": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "昵称",
                        "sortable": true,
                        "resizable": true,
                        "id": "skvlyin8vp7",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 66,
                      "name": "nickname",
                      "x-designable-id": "skvlyin8vp7",
                      "x-display": "hidden",
                      "properties": {
                        "nickname": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "nickname",
                              "originalField": "nickname"
                            }
                          },
                          "x-index": 0,
                          "name": "nickname",
                          "x-designable-id": "w7rcltysosq"
                        }
                      }
                    },
                    "ifNewCompany": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "是否新供应商（Y：是，N：否，默认Y）",
                        "sortable": true,
                        "resizable": true,
                        "id": "1nzqx2fj7rw",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 67,
                      "name": "ifNewCompany",
                      "x-designable-id": "1nzqx2fj7rw",
                      "x-display": "hidden",
                      "properties": {
                        "ifNewCompany": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "ifNewCompany",
                              "originalField": "ifNewCompany"
                            }
                          },
                          "x-index": 0,
                          "name": "ifNewCompany",
                          "x-designable-id": "osjr93k6msq"
                        }
                      }
                    },
                    "companyLevel": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "是否A级",
                        "sortable": true,
                        "resizable": true,
                        "id": "funbshp8cf7",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 68,
                      "name": "companyLevel",
                      "x-designable-id": "funbshp8cf7",
                      "x-display": "hidden",
                      "properties": {
                        "companyLevel": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "companyLevel",
                              "originalField": "companyLevel"
                            }
                          },
                          "x-index": 0,
                          "name": "companyLevel",
                          "x-designable-id": "wr022513cvq"
                        }
                      }
                    },
                    "oldCompanyId": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "老SRM供应商ID",
                        "sortable": true,
                        "resizable": true,
                        "id": "bcoyi53q7yr",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 69,
                      "name": "oldCompanyId",
                      "x-designable-id": "bcoyi53q7yr",
                      "x-display": "hidden",
                      "properties": {
                        "oldCompanyId": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "oldCompanyId",
                              "originalField": "oldCompanyId"
                            }
                          },
                          "x-index": 0,
                          "name": "oldCompanyId",
                          "x-designable-id": "apqxcn2s4yj"
                        }
                      }
                    },
                    "ceeaDraftsmanOpinion": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "起草人意见",
                        "sortable": true,
                        "resizable": true,
                        "id": "4twdxisilob",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 70,
                      "name": "ceeaDraftsmanOpinion",
                      "x-designable-id": "4twdxisilob",
                      "x-display": "hidden",
                      "properties": {
                        "ceeaDraftsmanOpinion": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "ceeaDraftsmanOpinion",
                              "originalField": "ceeaDraftsmanOpinion"
                            }
                          },
                          "x-index": 0,
                          "name": "ceeaDraftsmanOpinion",
                          "x-designable-id": "esa3c725pvr"
                        }
                      }
                    },
                    "greenChannelReason": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "绿色通道引入供应商的原因详述",
                        "sortable": true,
                        "resizable": true,
                        "id": "mlq8rb61iq2",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 71,
                      "name": "greenChannelReason",
                      "x-designable-id": "mlq8rb61iq2",
                      "x-display": "hidden",
                      "properties": {
                        "greenChannelReason": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "greenChannelReason",
                              "originalField": "greenChannelReason"
                            }
                          },
                          "x-index": 0,
                          "name": "greenChannelReason",
                          "x-designable-id": "e51x4j7e6mo"
                        }
                      }
                    },
                    "isUseReminder": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "是否启用到期提醒",
                        "sortable": true,
                        "resizable": true,
                        "id": "amiv12x8647",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 72,
                      "name": "isUseReminder",
                      "x-designable-id": "amiv12x8647",
                      "x-display": "hidden",
                      "properties": {
                        "isUseReminder": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "isUseReminder",
                              "originalField": "isUseReminder"
                            }
                          },
                          "x-index": 0,
                          "name": "isUseReminder",
                          "x-designable-id": "mu3zck9uvh0"
                        }
                      }
                    },
                    "quitFlag": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "是否已退出(Y/N)",
                        "sortable": true,
                        "resizable": true,
                        "id": "upnfrvknc80",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 73,
                      "name": "quitFlag",
                      "x-designable-id": "upnfrvknc80",
                      "x-display": "hidden",
                      "properties": {
                        "quitFlag": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "quitFlag",
                              "originalField": "quitFlag"
                            }
                          },
                          "x-index": 0,
                          "name": "quitFlag",
                          "x-designable-id": "tv9azk45umd"
                        }
                      }
                    },
                    "supplierType": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "供应商类型--字典编码(SUPPLIER_TYPE)",
                        "sortable": true,
                        "resizable": true,
                        "id": "8xraccctilp",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 74,
                      "name": "supplierType",
                      "x-designable-id": "8xraccctilp",
                      "x-display": "hidden",
                      "properties": {
                        "supplierType": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "supplierType",
                              "originalField": "supplierType"
                            }
                          },
                          "x-index": 0,
                          "name": "supplierType",
                          "x-designable-id": "k3y6y6bn0cr"
                        }
                      }
                    },
                    "potentialFlag": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "是否潜在供应商(Y/N)",
                        "sortable": true,
                        "resizable": true,
                        "id": "28r8fjzuyqm",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 75,
                      "name": "potentialFlag",
                      "x-designable-id": "28r8fjzuyqm",
                      "x-display": "hidden",
                      "properties": {
                        "potentialFlag": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "potentialFlag",
                              "originalField": "potentialFlag"
                            }
                          },
                          "x-index": 0,
                          "name": "potentialFlag",
                          "x-designable-id": "0yfjh3zdhi0"
                        }
                      }
                    },
                    "flowRemark": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "审批备注",
                        "sortable": true,
                        "resizable": true,
                        "id": "n6azgwanr37",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 76,
                      "name": "flowRemark",
                      "x-designable-id": "n6azgwanr37",
                      "x-display": "hidden",
                      "properties": {
                        "flowRemark": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "flowRemark",
                              "originalField": "flowRemark"
                            }
                          },
                          "x-index": 0,
                          "name": "flowRemark",
                          "x-designable-id": "ckpjiirq1yb"
                        }
                      }
                    },
                    "forzenFlag": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "是否已冻结(Y/N)",
                        "sortable": true,
                        "resizable": true,
                        "id": "p4mtopjlzwt",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 77,
                      "name": "forzenFlag",
                      "x-designable-id": "p4mtopjlzwt",
                      "x-display": "hidden",
                      "properties": {
                        "forzenFlag": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "forzenFlag",
                              "originalField": "forzenFlag"
                            }
                          },
                          "x-index": 0,
                          "name": "forzenFlag",
                          "x-designable-id": "tzsiqznprjs"
                        }
                      }
                    },
                    "saleService": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "售后服务",
                        "sortable": true,
                        "resizable": true,
                        "id": "0fvp1lgdlw3",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 78,
                      "name": "saleService",
                      "x-designable-id": "0fvp1lgdlw3",
                      "x-display": "hidden",
                      "properties": {
                        "saleService": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "saleService",
                              "originalField": "saleService"
                            }
                          },
                          "x-index": 0,
                          "name": "saleService",
                          "x-designable-id": "i7u429micea"
                        }
                      }
                    },
                    "exportQualification": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "是否具有出口资质",
                        "sortable": true,
                        "resizable": true,
                        "id": "g76w2d848xv",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 79,
                      "name": "exportQualification",
                      "x-designable-id": "g76w2d848xv",
                      "x-display": "hidden",
                      "properties": {
                        "exportQualification": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "exportQualification",
                              "originalField": "exportQualification"
                            }
                          },
                          "x-index": 0,
                          "name": "exportQualification",
                          "x-designable-id": "bavahd4jhet"
                        }
                      }
                    },
                    "idCardFileId": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "身份证文件id",
                        "sortable": true,
                        "resizable": true,
                        "id": "wsvm70k0scp",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 80,
                      "name": "idCardFileId",
                      "x-designable-id": "wsvm70k0scp",
                      "x-display": "hidden",
                      "properties": {
                        "idCardFileId": {
                          "type": "number",
                          "x-component": "InputNumber",
                          "x-decorator": "FormItem",
                          "x-component-props": {
                            "controls-position": "right",
                            "controls": true
                          },
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "idCardFileId",
                              "originalField": "idCardFileId"
                            }
                          },
                          "x-index": 0,
                          "name": "idCardFileId",
                          "x-designable-id": "lck7cr8jurk"
                        }
                      }
                    },
                    "idCardFileName": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "width": 150,
                        "title": "身份证文件名",
                        "sortable": true,
                        "resizable": true,
                        "id": "csvfkhhxh0p",
                        "visible": false,
                        "isGroup": false
                      },
                      "x-index": 81,
                      "name": "idCardFileName",
                      "x-designable-id": "csvfkhhxh0p",
                      "x-display": "hidden",
                      "properties": {
                        "idCardFileName": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sccSupCompanyInfo",
                              "reference": null,
                              "modelId": "1668157712692989956",
                              "field": "idCardFileName",
                              "originalField": "idCardFileName"
                            }
                          },
                          "x-index": 0,
                          "name": "idCardFileName",
                          "x-designable-id": "b4x117f55td"
                        }
                      }
                    },
                    "operation": {
                      "type": "void",
                      "title": "操作",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "title": "操作",
                        "sortable": false,
                        "resizable": true,
                        "visible": false,
                        "align": "left",
                        "headerAlign": "left",
                        "skipEditable": false,
                        "style": {
                          "opacity": 1
                        },
                        "width": 248,
                        "fixed": "right",
                        "id": "lh7a1tty0oi",
                        "isGroup": false
                      },
                      "name": "operation",
                      "x-designable-id": "lh7a1tty0oi",
                      "x-index": 82,
                      "x-display": "hidden",
                      "x-decorator-props": {
                        "style": {
                          "opacity": 1
                        }
                      },
                      "x-designer-extend": {},
                      "properties": {
                        "operation": {
                          "type": "void",
                          "name": "operation",
                          "x-component": "RenderButtonList",
                          "x-component-props": {
                            "max": 4,
                            "size": 12,
                            "style": {
                              "opacity": 1
                            }
                          },
                          "x-index": 0,
                          "x-designable-id": "pewutez4ku4",
                          "x-decorator-props": {
                            "style": {
                              "opacity": 1
                            }
                          },
                          "x-designer-extend": {},
                          "x-validator": [],
                          "properties": {
                            "saveRow": {
                              "type": "void",
                              "title": "保存",
                              "x-visible": "{{ $table.getSelfRowEditable($self) }}",
                              "x-component": "Button",
                              "x-component-props": {
                                "type": "text",
                                "@click": "{{(...args) => {\n        return new $LogicEngineRuntime({\n          nodes: [{\"id\":\"db33f9c7-7d7e-402c-8a38-ab70a9c3a98a\",\"shape\":{\"name\":\"logic-start\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":344.5,\"y\":0},\"visible\":true,\"zIndex\":1},\"data\":{\"title\":\"开始\",\"value\":null}},{\"id\":\"7eeb1a1c-9b7e-468c-abea-fdcee1ff78f4\",\"shape\":{\"name\":\"logic-end\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":344.5,\"y\":518},\"visible\":true,\"zIndex\":2},\"data\":{\"title\":\"结束\",\"value\":null}},{\"id\":\"9472a1ef-3932-41a4-9d30-988d412ef756\",\"shape\":{\"name\":\"logic-row-save\",\"size\":{\"width\":70,\"height\":60},\"position\":{\"x\":337,\"y\":165},\"visible\":true,\"zIndex\":3},\"data\":{\"title\":\"表格保存行\",\"value\":{\"action\":\"save\"}},\"executeFunction\":\"function logicNode(ctx) {\\n  \\n\\t\\t\\t\\treturn $queryEngine.request.save($table.getRowByIndex($self.index), {\\\"actionConfig\\\":{\\\"action\\\":\\\"save\\\",\\\"type\\\":\\\"sccSupCompanyInfo\\\"}}).then(() => {\\n\\t\\t\\t\\t\\t\\t$table.cancelEditRow($self.index)\\n\\t\\t\\t\\t\\t\\t$queryEngine.state.paginationManagement.refresh()\\n\\t\\t\\t\\t})\\n\\t\\t\\t\\n}\"},{\"id\":\"e715e2e7-1597-4c10-9d46-8df21fd32a36\",\"shape\":{\"name\":\"edge\",\"zIndex\":4,\"source\":{\"cell\":\"db33f9c7-7d7e-402c-8a38-ab70a9c3a98a\",\"port\":\"bottom\"},\"target\":{\"cell\":\"9472a1ef-3932-41a4-9d30-988d412ef756\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"fa06779d-42b2-4d2e-9bac-a82be741e7e1\",\"shape\":{\"name\":\"logic-message\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":230,\"y\":290},\"visible\":true,\"zIndex\":5},\"data\":{\"title\":\"消息提示\",\"value\":{\"type\":\"error\",\"message\":\"操作异常\"}},\"executeFunction\":\"function logicNode(ctx) {\\n  $message({\\\"message\\\":\\\"操作异常\\\",\\\"type\\\":\\\"error\\\"})\\n}\"},{\"id\":\"fe4e4a78-fa81-483e-8679-4c7baf2b5773\",\"shape\":{\"name\":\"logic-message\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":426,\"y\":290},\"visible\":true,\"zIndex\":6},\"data\":{\"title\":\"消息提示\",\"value\":{\"type\":\"success\",\"message\":\"保存成功\"}},\"executeFunction\":\"function logicNode(ctx) {\\n  $message({\\\"message\\\":\\\"保存成功\\\",\\\"type\\\":\\\"success\\\"})\\n}\"},{\"id\":\"533e9d32-b88d-4bce-979d-22297d8f07b2\",\"shape\":{\"name\":\"edge\",\"zIndex\":7,\"source\":{\"cell\":\"9472a1ef-3932-41a4-9d30-988d412ef756\",\"port\":\"right\"},\"target\":{\"cell\":\"fe4e4a78-fa81-483e-8679-4c7baf2b5773\",\"port\":\"top\"},\"labels\":[{\"attrs\":{\"label\":{\"text\":\"成功\"}}}]},\"data\":{\"value\":null}},{\"id\":\"15f5a608-9276-476f-afaa-66d2e2bffc78\",\"shape\":{\"name\":\"edge\",\"zIndex\":8,\"source\":{\"cell\":\"9472a1ef-3932-41a4-9d30-988d412ef756\",\"port\":\"left\"},\"target\":{\"cell\":\"fa06779d-42b2-4d2e-9bac-a82be741e7e1\",\"port\":\"top\"},\"labels\":[{\"attrs\":{\"label\":{\"text\":\"失败\"}}}]},\"data\":{\"value\":null}},{\"id\":\"c4d9929f-da07-4b0e-a316-a7330edbeb1f\",\"shape\":{\"name\":\"edge\",\"zIndex\":9,\"source\":{\"cell\":\"fa06779d-42b2-4d2e-9bac-a82be741e7e1\",\"port\":\"bottom\"},\"target\":{\"cell\":\"7eeb1a1c-9b7e-468c-abea-fdcee1ff78f4\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"791c92b7-d549-49fc-818f-05362aee6c96\",\"shape\":{\"name\":\"edge\",\"zIndex\":10,\"source\":{\"cell\":\"fe4e4a78-fa81-483e-8679-4c7baf2b5773\",\"port\":\"bottom\"},\"target\":{\"cell\":\"7eeb1a1c-9b7e-468c-abea-fdcee1ff78f4\",\"port\":\"top\"}},\"data\":{\"value\":null}}],\n          payload: args,\n          scope: {\n            ...$root,\n            $designPages: $designPages,\n          }\n        }).run()\n      }}}"
                              },
                              "x-designer-extend": {
                                "events": [
                                  {
                                    "title": "保存",
                                    "name": "click",
                                    "type": "logic",
                                    "args": [
                                      {
                                        "id": "db33f9c7-7d7e-402c-8a38-ab70a9c3a98a",
                                        "shape": {
                                          "name": "logic-start",
                                          "size": {
                                            "width": 55,
                                            "height": 55
                                          },
                                          "position": {
                                            "x": 344.5,
                                            "y": 0
                                          },
                                          "visible": true,
                                          "zIndex": 1
                                        },
                                        "data": {
                                          "title": "开始",
                                          "value": null
                                        }
                                      },
                                      {
                                        "id": "7eeb1a1c-9b7e-468c-abea-fdcee1ff78f4",
                                        "shape": {
                                          "name": "logic-end",
                                          "size": {
                                            "width": 55,
                                            "height": 55
                                          },
                                          "position": {
                                            "x": 344.5,
                                            "y": 518
                                          },
                                          "visible": true,
                                          "zIndex": 2
                                        },
                                        "data": {
                                          "title": "结束",
                                          "value": null
                                        }
                                      },
                                      {
                                        "id": "9472a1ef-3932-41a4-9d30-988d412ef756",
                                        "shape": {
                                          "name": "logic-row-save",
                                          "size": {
                                            "width": 70,
                                            "height": 60
                                          },
                                          "position": {
                                            "x": 337,
                                            "y": 165
                                          },
                                          "visible": true,
                                          "zIndex": 3
                                        },
                                        "data": {
                                          "title": "表格保存行",
                                          "value": {
                                            "action": "save"
                                          }
                                        },
                                        "executeFunction": "function logicNode(ctx) {\n  \n\t\t\t\treturn $queryEngine.request.save($table.getRowByIndex($self.index), {\"actionConfig\":{\"action\":\"save\",\"type\":\"sccSupCompanyInfo\"}}).then(() => {\n\t\t\t\t\t\t$table.cancelEditRow($self.index)\n\t\t\t\t\t\t$queryEngine.state.paginationManagement.refresh()\n\t\t\t\t})\n\t\t\t\n}"
                                      },
                                      {
                                        "id": "e715e2e7-1597-4c10-9d46-8df21fd32a36",
                                        "shape": {
                                          "name": "edge",
                                          "zIndex": 4,
                                          "source": {
                                            "cell": "db33f9c7-7d7e-402c-8a38-ab70a9c3a98a",
                                            "port": "bottom"
                                          },
                                          "target": {
                                            "cell": "9472a1ef-3932-41a4-9d30-988d412ef756",
                                            "port": "top"
                                          }
                                        },
                                        "data": {
                                          "value": null
                                        }
                                      },
                                      {
                                        "id": "fa06779d-42b2-4d2e-9bac-a82be741e7e1",
                                        "shape": {
                                          "name": "logic-message",
                                          "size": {
                                            "width": 86,
                                            "height": 30
                                          },
                                          "position": {
                                            "x": 230,
                                            "y": 290
                                          },
                                          "visible": true,
                                          "zIndex": 5
                                        },
                                        "data": {
                                          "title": "消息提示",
                                          "value": {
                                            "type": "error",
                                            "message": "操作异常"
                                          }
                                        },
                                        "executeFunction": "function logicNode(ctx) {\n  $message({\"message\":\"操作异常\",\"type\":\"error\"})\n}"
                                      },
                                      {
                                        "id": "fe4e4a78-fa81-483e-8679-4c7baf2b5773",
                                        "shape": {
                                          "name": "logic-message",
                                          "size": {
                                            "width": 86,
                                            "height": 30
                                          },
                                          "position": {
                                            "x": 426,
                                            "y": 290
                                          },
                                          "visible": true,
                                          "zIndex": 6
                                        },
                                        "data": {
                                          "title": "消息提示",
                                          "value": {
                                            "type": "success",
                                            "message": "保存成功"
                                          }
                                        },
                                        "executeFunction": "function logicNode(ctx) {\n  $message({\"message\":\"保存成功\",\"type\":\"success\"})\n}"
                                      },
                                      {
                                        "id": "533e9d32-b88d-4bce-979d-22297d8f07b2",
                                        "shape": {
                                          "name": "edge",
                                          "zIndex": 7,
                                          "source": {
                                            "cell": "9472a1ef-3932-41a4-9d30-988d412ef756",
                                            "port": "right"
                                          },
                                          "target": {
                                            "cell": "fe4e4a78-fa81-483e-8679-4c7baf2b5773",
                                            "port": "top"
                                          },
                                          "labels": [
                                            {
                                              "attrs": {
                                                "label": {
                                                  "text": "成功"
                                                }
                                              }
                                            }
                                          ]
                                        },
                                        "data": {
                                          "value": null
                                        }
                                      },
                                      {
                                        "id": "15f5a608-9276-476f-afaa-66d2e2bffc78",
                                        "shape": {
                                          "name": "edge",
                                          "zIndex": 8,
                                          "source": {
                                            "cell": "9472a1ef-3932-41a4-9d30-988d412ef756",
                                            "port": "left"
                                          },
                                          "target": {
                                            "cell": "fa06779d-42b2-4d2e-9bac-a82be741e7e1",
                                            "port": "top"
                                          },
                                          "labels": [
                                            {
                                              "attrs": {
                                                "label": {
                                                  "text": "失败"
                                                }
                                              }
                                            }
                                          ]
                                        },
                                        "data": {
                                          "value": null
                                        }
                                      },
                                      {
                                        "id": "c4d9929f-da07-4b0e-a316-a7330edbeb1f",
                                        "shape": {
                                          "name": "edge",
                                          "zIndex": 9,
                                          "source": {
                                            "cell": "fa06779d-42b2-4d2e-9bac-a82be741e7e1",
                                            "port": "bottom"
                                          },
                                          "target": {
                                            "cell": "7eeb1a1c-9b7e-468c-abea-fdcee1ff78f4",
                                            "port": "top"
                                          }
                                        },
                                        "data": {
                                          "value": null
                                        }
                                      },
                                      {
                                        "id": "791c92b7-d549-49fc-818f-05362aee6c96",
                                        "shape": {
                                          "name": "edge",
                                          "zIndex": 10,
                                          "source": {
                                            "cell": "fe4e4a78-fa81-483e-8679-4c7baf2b5773",
                                            "port": "bottom"
                                          },
                                          "target": {
                                            "cell": "7eeb1a1c-9b7e-468c-abea-fdcee1ff78f4",
                                            "port": "top"
                                          }
                                        },
                                        "data": {
                                          "value": null
                                        }
                                      }
                                    ]
                                  }
                                ]
                              },
                              "name": "saveRow",
                              "x-designable-id": "wtk6skefr94",
                              "x-index": 0
                            },
                            "cancelEditRow": {
                              "type": "void",
                              "title": "取消",
                              "x-visible": "{{ $table.getSelfRowEditable($self) }}",
                              "x-component": "Button",
                              "x-component-props": {
                                "type": "text",
                                "@click": "{{(...args) => {\n        return new $LogicEngineRuntime({\n          nodes: [{\"id\":\"871c11e0-4816-4626-adc1-a2d8cdc93f1a\",\"shape\":{\"name\":\"logic-start\",\"size\":{\"width\":50,\"height\":50},\"position\":{\"x\":604,\"y\":0},\"visible\":true,\"zIndex\":1},\"data\":{\"title\":\"开始\",\"value\":null}},{\"id\":\"8565c8dc-d3d7-4ff7-91bb-0043b07a4ef2\",\"shape\":{\"name\":\"logic-end\",\"size\":{\"width\":50,\"height\":50},\"position\":{\"x\":604,\"y\":491.4},\"visible\":true,\"zIndex\":2},\"data\":{\"title\":\"结束\",\"value\":null}},{\"id\":\"286da096-0830-417f-aabf-72ee593e2575\",\"shape\":{\"name\":\"logic-table-row-cancel\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":586,\"y\":235},\"visible\":true,\"zIndex\":3},\"data\":{\"title\":\"取消行\",\"value\":null},\"executeFunction\":\"function logicNode(ctx) {\\n  \\n        if ($table) {\\n          return $table.cancelEditRow($self.index)\\n        }\\n        console.warn('该事件动作只能在表格行上触发')\\n      \\n}\"},{\"id\":\"4947c04f-832d-48a1-93b5-a326bb9aa38f\",\"shape\":{\"name\":\"edge\",\"zIndex\":4,\"source\":{\"cell\":\"871c11e0-4816-4626-adc1-a2d8cdc93f1a\",\"port\":\"bottom\"},\"target\":{\"cell\":\"286da096-0830-417f-aabf-72ee593e2575\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"013c0a93-a15a-4d9d-9505-d7c570dd4bea\",\"shape\":{\"name\":\"edge\",\"zIndex\":5,\"source\":{\"cell\":\"286da096-0830-417f-aabf-72ee593e2575\",\"port\":\"bottom\"},\"target\":{\"cell\":\"8565c8dc-d3d7-4ff7-91bb-0043b07a4ef2\",\"port\":\"top\"}},\"data\":{\"value\":null}}],\n          payload: args,\n          scope: {\n            ...$root,\n            $designPages: $designPages,\n          }\n        }).run()\n      }}}"
                              },
                              "x-designer-extend": {
                                "events": [
                                  {
                                    "title": "取消",
                                    "name": "click",
                                    "type": "logic",
                                    "args": [
                                      {
                                        "id": "871c11e0-4816-4626-adc1-a2d8cdc93f1a",
                                        "shape": {
                                          "name": "logic-start",
                                          "size": {
                                            "width": 50,
                                            "height": 50
                                          },
                                          "position": {
                                            "x": 604,
                                            "y": 0
                                          },
                                          "visible": true,
                                          "zIndex": 1
                                        },
                                        "data": {
                                          "title": "开始",
                                          "value": null
                                        }
                                      },
                                      {
                                        "id": "8565c8dc-d3d7-4ff7-91bb-0043b07a4ef2",
                                        "shape": {
                                          "name": "logic-end",
                                          "size": {
                                            "width": 50,
                                            "height": 50
                                          },
                                          "position": {
                                            "x": 604,
                                            "y": 491.4
                                          },
                                          "visible": true,
                                          "zIndex": 2
                                        },
                                        "data": {
                                          "title": "结束",
                                          "value": null
                                        }
                                      },
                                      {
                                        "id": "286da096-0830-417f-aabf-72ee593e2575",
                                        "shape": {
                                          "name": "logic-table-row-cancel",
                                          "size": {
                                            "width": 86,
                                            "height": 30
                                          },
                                          "position": {
                                            "x": 586,
                                            "y": 235
                                          },
                                          "visible": true,
                                          "zIndex": 3
                                        },
                                        "data": {
                                          "title": "取消行",
                                          "value": null
                                        },
                                        "executeFunction": "function logicNode(ctx) {\n  \n        if ($table) {\n          return $table.cancelEditRow($self.index)\n        }\n        console.warn('该事件动作只能在表格行上触发')\n      \n}"
                                      },
                                      {
                                        "id": "4947c04f-832d-48a1-93b5-a326bb9aa38f",
                                        "shape": {
                                          "name": "edge",
                                          "zIndex": 4,
                                          "source": {
                                            "cell": "871c11e0-4816-4626-adc1-a2d8cdc93f1a",
                                            "port": "bottom"
                                          },
                                          "target": {
                                            "cell": "286da096-0830-417f-aabf-72ee593e2575",
                                            "port": "top"
                                          }
                                        },
                                        "data": {
                                          "value": null
                                        }
                                      },
                                      {
                                        "id": "013c0a93-a15a-4d9d-9505-d7c570dd4bea",
                                        "shape": {
                                          "name": "edge",
                                          "zIndex": 5,
                                          "source": {
                                            "cell": "286da096-0830-417f-aabf-72ee593e2575",
                                            "port": "bottom"
                                          },
                                          "target": {
                                            "cell": "8565c8dc-d3d7-4ff7-91bb-0043b07a4ef2",
                                            "port": "top"
                                          }
                                        },
                                        "data": {
                                          "value": null
                                        }
                                      }
                                    ]
                                  }
                                ]
                              },
                              "name": "cancelEditRow",
                              "x-designable-id": "ozzori5gbpl",
                              "x-index": 1
                            },
                            "editItem": {
                              "type": "void",
                              "title": "编辑",
                              "x-visible": "{{ !$table.getSelfRowEditable($self) }}",
                              "x-component": "Button",
                              "x-component-props": {
                                "type": "text",
                                "@click": "{{(...args) => {\n        return new $LogicEngineRuntime({\n          nodes: [{\"id\":\"bd2fa5bf-4803-4ab1-bb0e-9e4feb98cbe7\",\"shape\":{\"name\":\"logic-start\",\"size\":{\"width\":50,\"height\":50},\"position\":{\"x\":604,\"y\":0},\"visible\":true,\"zIndex\":1},\"data\":{\"title\":\"开始\",\"value\":null}},{\"id\":\"ce97f867-fd0b-4dcf-861b-9a9b4fc2515d\",\"shape\":{\"name\":\"logic-end\",\"size\":{\"width\":50,\"height\":50},\"position\":{\"x\":604,\"y\":491.4},\"visible\":true,\"zIndex\":2},\"data\":{\"title\":\"结束\",\"value\":null}},{\"id\":\"5d0127c6-c26e-45e9-b390-828a803b472b\",\"shape\":{\"name\":\"logic-table-row-edit\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":586,\"y\":232},\"visible\":true,\"zIndex\":3},\"data\":{\"title\":\"编辑行\",\"value\":null},\"executeFunction\":\"function logicNode(ctx) {\\n  \\n        if ($table) {\\n          return $table.editRowByIndex($self.index)\\n        }\\n        console.warn('该事件动作只能在表格行上触发')\\n      \\n}\"},{\"id\":\"eb3c905e-08f5-49d2-b049-0340677a76a6\",\"shape\":{\"name\":\"edge\",\"zIndex\":4,\"source\":{\"cell\":\"bd2fa5bf-4803-4ab1-bb0e-9e4feb98cbe7\",\"port\":\"bottom\"},\"target\":{\"cell\":\"5d0127c6-c26e-45e9-b390-828a803b472b\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"e42607b2-be5a-43e7-b56a-2387bd0c57a4\",\"shape\":{\"name\":\"edge\",\"zIndex\":5,\"source\":{\"cell\":\"5d0127c6-c26e-45e9-b390-828a803b472b\",\"port\":\"bottom\"},\"target\":{\"cell\":\"ce97f867-fd0b-4dcf-861b-9a9b4fc2515d\",\"port\":\"top\"}},\"data\":{\"value\":null}}],\n          payload: args,\n          scope: {\n            ...$root,\n            $designPages: $designPages,\n          }\n        }).run()\n      }}}"
                              },
                              "x-designer-extend": {
                                "events": [
                                  {
                                    "title": "编辑",
                                    "name": "click",
                                    "type": "logic",
                                    "args": [
                                      {
                                        "id": "bd2fa5bf-4803-4ab1-bb0e-9e4feb98cbe7",
                                        "shape": {
                                          "name": "logic-start",
                                          "size": {
                                            "width": 50,
                                            "height": 50
                                          },
                                          "position": {
                                            "x": 604,
                                            "y": 0
                                          },
                                          "visible": true,
                                          "zIndex": 1
                                        },
                                        "data": {
                                          "title": "开始",
                                          "value": null
                                        }
                                      },
                                      {
                                        "id": "ce97f867-fd0b-4dcf-861b-9a9b4fc2515d",
                                        "shape": {
                                          "name": "logic-end",
                                          "size": {
                                            "width": 50,
                                            "height": 50
                                          },
                                          "position": {
                                            "x": 604,
                                            "y": 491.4
                                          },
                                          "visible": true,
                                          "zIndex": 2
                                        },
                                        "data": {
                                          "title": "结束",
                                          "value": null
                                        }
                                      },
                                      {
                                        "id": "5d0127c6-c26e-45e9-b390-828a803b472b",
                                        "shape": {
                                          "name": "logic-table-row-edit",
                                          "size": {
                                            "width": 86,
                                            "height": 30
                                          },
                                          "position": {
                                            "x": 586,
                                            "y": 232
                                          },
                                          "visible": true,
                                          "zIndex": 3
                                        },
                                        "data": {
                                          "title": "编辑行",
                                          "value": null
                                        },
                                        "executeFunction": "function logicNode(ctx) {\n  \n        if ($table) {\n          return $table.editRowByIndex($self.index)\n        }\n        console.warn('该事件动作只能在表格行上触发')\n      \n}"
                                      },
                                      {
                                        "id": "eb3c905e-08f5-49d2-b049-0340677a76a6",
                                        "shape": {
                                          "name": "edge",
                                          "zIndex": 4,
                                          "source": {
                                            "cell": "bd2fa5bf-4803-4ab1-bb0e-9e4feb98cbe7",
                                            "port": "bottom"
                                          },
                                          "target": {
                                            "cell": "5d0127c6-c26e-45e9-b390-828a803b472b",
                                            "port": "top"
                                          }
                                        },
                                        "data": {
                                          "value": null
                                        }
                                      },
                                      {
                                        "id": "e42607b2-be5a-43e7-b56a-2387bd0c57a4",
                                        "shape": {
                                          "name": "edge",
                                          "zIndex": 5,
                                          "source": {
                                            "cell": "5d0127c6-c26e-45e9-b390-828a803b472b",
                                            "port": "bottom"
                                          },
                                          "target": {
                                            "cell": "ce97f867-fd0b-4dcf-861b-9a9b4fc2515d",
                                            "port": "top"
                                          }
                                        },
                                        "data": {
                                          "value": null
                                        }
                                      }
                                    ]
                                  }
                                ]
                              },
                              "name": "editItem",
                              "x-designable-id": "7gmbrnk9t0z",
                              "x-index": 2
                            },
                            "delete": {
                              "type": "void",
                              "title": "删除",
                              "x-component": "Button",
                              "x-visible": "{{ !$table.getSelfRowEditable($self) }}",
                              "x-component-props": {
                                "type": "text",
                                "size": "small",
                                "plain": false,
                                "round": false,
                                "circle": false,
                                "style": {
                                  "opacity": 1
                                },
                                "popconfirm": {
                                  "title": "确定删除该行数据？"
                                },
                                "@click": "{{(...args) => {\n        return new $LogicEngineRuntime({\n          nodes: [{\"id\":\"e7750441-eef6-4724-9b7e-9e9e860ad34e\",\"shape\":{\"name\":\"logic-start\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":431,\"y\":0},\"visible\":true,\"zIndex\":1},\"data\":{\"title\":\"开始\",\"value\":null}},{\"id\":\"9693a56b-1aeb-4904-b55c-e63564274770\",\"shape\":{\"name\":\"logic-end\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":431,\"y\":518},\"visible\":true,\"zIndex\":2},\"data\":{\"title\":\"结束\",\"value\":null}},{\"id\":\"d20a9f3f-3cd1-4fe5-a5d8-c90063ce0597\",\"shape\":{\"name\":\"logic-table-remove\",\"size\":{\"width\":70,\"height\":60},\"position\":{\"x\":424,\"y\":142},\"visible\":true,\"zIndex\":3},\"data\":{\"title\":\"表格删除\",\"value\":{\"action\":\"delete\"}},\"executeFunction\":\"function logicNode(ctx) {\\n  \\n        const _table = $$safeGetScope('$table', $root)\\n        if (!_table) {\\n          return false\\n        }\\n\\n        const primaryKeyValue = $queryEngineConfig.getPrimaryKeyValueByRecord(_table.getRowByIndex($self.index))\\n        if (!primaryKeyValue) {\\n          return false\\n        }\\n        return $root.$queryEngine.request.delete(primaryKeyValue, {\\\"actionConfig\\\":{\\\"action\\\":\\\"delete\\\"}})\\n          .then(() => {\\n            _table.cancelEditRow($self.index)\\n            $root.$queryEngine.state.paginationManagement.refresh()\\n          })\\n      \\n}\"},{\"id\":\"85b586fa-3cd6-4b7c-aa73-1e759239717c\",\"shape\":{\"name\":\"edge\",\"zIndex\":4,\"source\":{\"cell\":\"e7750441-eef6-4724-9b7e-9e9e860ad34e\",\"port\":\"bottom\"},\"target\":{\"cell\":\"d20a9f3f-3cd1-4fe5-a5d8-c90063ce0597\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"904cf603-c4c8-4b46-b44d-9f2949384dea\",\"shape\":{\"name\":\"logic-message\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":338,\"y\":287},\"visible\":true,\"zIndex\":6},\"data\":{\"title\":\"消息提示\",\"value\":{\"type\":\"error\",\"message\":\"操作异常\"}},\"executeFunction\":\"function logicNode(ctx) {\\n  $message({\\\"message\\\":\\\"操作异常\\\",\\\"type\\\":\\\"error\\\"})\\n}\"},{\"id\":\"9ea518f4-5781-4e11-9539-28a380d11a09\",\"shape\":{\"name\":\"logic-message\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":494,\"y\":287},\"visible\":true,\"zIndex\":7},\"data\":{\"title\":\"消息提示\",\"value\":{\"type\":\"success\",\"message\":\"删除成功\"}},\"executeFunction\":\"function logicNode(ctx) {\\n  $message({\\\"message\\\":\\\"删除成功\\\",\\\"type\\\":\\\"success\\\"})\\n}\"},{\"id\":\"e5491499-e78e-448a-8d88-ecc3b92ff195\",\"shape\":{\"name\":\"edge\",\"zIndex\":8,\"source\":{\"cell\":\"d20a9f3f-3cd1-4fe5-a5d8-c90063ce0597\",\"port\":\"left\"},\"target\":{\"cell\":\"904cf603-c4c8-4b46-b44d-9f2949384dea\",\"port\":\"top\"},\"labels\":[{\"attrs\":{\"label\":{\"text\":\"失败\"}}}]},\"data\":{\"value\":null}},{\"id\":\"b5bc57a7-53db-4153-811d-8acba75c4725\",\"shape\":{\"name\":\"edge\",\"zIndex\":9,\"source\":{\"cell\":\"904cf603-c4c8-4b46-b44d-9f2949384dea\",\"port\":\"bottom\"},\"target\":{\"cell\":\"9693a56b-1aeb-4904-b55c-e63564274770\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"93872ea1-4032-4dce-b504-80c64511fd85\",\"shape\":{\"name\":\"edge\",\"zIndex\":10,\"source\":{\"cell\":\"d20a9f3f-3cd1-4fe5-a5d8-c90063ce0597\",\"port\":\"right\"},\"target\":{\"cell\":\"9ea518f4-5781-4e11-9539-28a380d11a09\",\"port\":\"top\"},\"labels\":[{\"attrs\":{\"label\":{\"text\":\"成功\"}}}]},\"data\":{\"value\":null}},{\"id\":\"2ef24174-9288-45a3-99b5-b99c5d447536\",\"shape\":{\"name\":\"edge\",\"zIndex\":11,\"source\":{\"cell\":\"9ea518f4-5781-4e11-9539-28a380d11a09\",\"port\":\"bottom\"},\"target\":{\"cell\":\"9693a56b-1aeb-4904-b55c-e63564274770\",\"port\":\"top\"}},\"data\":{\"value\":null}}],\n          payload: args,\n          scope: {\n            ...$root,\n            $designPages: $designPages,\n          }\n        }).run()\n      }}}"
                              },
                              "x-designer-extend": {
                                "events": [
                                  {
                                    "title": "删除",
                                    "name": "click",
                                    "type": "logic",
                                    "args": [
                                      {
                                        "id": "e7750441-eef6-4724-9b7e-9e9e860ad34e",
                                        "shape": {
                                          "name": "logic-start",
                                          "size": {
                                            "width": 55,
                                            "height": 55
                                          },
                                          "position": {
                                            "x": 431,
                                            "y": 0
                                          },
                                          "visible": true,
                                          "zIndex": 1
                                        },
                                        "data": {
                                          "title": "开始",
                                          "value": null
                                        }
                                      },
                                      {
                                        "id": "9693a56b-1aeb-4904-b55c-e63564274770",
                                        "shape": {
                                          "name": "logic-end",
                                          "size": {
                                            "width": 55,
                                            "height": 55
                                          },
                                          "position": {
                                            "x": 431,
                                            "y": 518
                                          },
                                          "visible": true,
                                          "zIndex": 2
                                        },
                                        "data": {
                                          "title": "结束",
                                          "value": null
                                        }
                                      },
                                      {
                                        "id": "d20a9f3f-3cd1-4fe5-a5d8-c90063ce0597",
                                        "shape": {
                                          "name": "logic-table-remove",
                                          "size": {
                                            "width": 70,
                                            "height": 60
                                          },
                                          "position": {
                                            "x": 424,
                                            "y": 142
                                          },
                                          "visible": true,
                                          "zIndex": 3
                                        },
                                        "data": {
                                          "title": "表格删除",
                                          "value": {
                                            "action": "delete"
                                          }
                                        },
                                        "executeFunction": "function logicNode(ctx) {\n  \n        const _table = $$safeGetScope('$table', $root)\n        if (!_table) {\n          return false\n        }\n\n        const primaryKeyValue = $queryEngineConfig.getPrimaryKeyValueByRecord(_table.getRowByIndex($self.index))\n        if (!primaryKeyValue) {\n          return false\n        }\n        return $root.$queryEngine.request.delete(primaryKeyValue, {\"actionConfig\":{\"action\":\"delete\"}})\n          .then(() => {\n            _table.cancelEditRow($self.index)\n            $root.$queryEngine.state.paginationManagement.refresh()\n          })\n      \n}"
                                      },
                                      {
                                        "id": "85b586fa-3cd6-4b7c-aa73-1e759239717c",
                                        "shape": {
                                          "name": "edge",
                                          "zIndex": 4,
                                          "source": {
                                            "cell": "e7750441-eef6-4724-9b7e-9e9e860ad34e",
                                            "port": "bottom"
                                          },
                                          "target": {
                                            "cell": "d20a9f3f-3cd1-4fe5-a5d8-c90063ce0597",
                                            "port": "top"
                                          }
                                        },
                                        "data": {
                                          "value": null
                                        }
                                      },
                                      {
                                        "id": "904cf603-c4c8-4b46-b44d-9f2949384dea",
                                        "shape": {
                                          "name": "logic-message",
                                          "size": {
                                            "width": 86,
                                            "height": 30
                                          },
                                          "position": {
                                            "x": 338,
                                            "y": 287
                                          },
                                          "visible": true,
                                          "zIndex": 6
                                        },
                                        "data": {
                                          "title": "消息提示",
                                          "value": {
                                            "type": "error",
                                            "message": "操作异常"
                                          }
                                        },
                                        "executeFunction": "function logicNode(ctx) {\n  $message({\"message\":\"操作异常\",\"type\":\"error\"})\n}"
                                      },
                                      {
                                        "id": "9ea518f4-5781-4e11-9539-28a380d11a09",
                                        "shape": {
                                          "name": "logic-message",
                                          "size": {
                                            "width": 86,
                                            "height": 30
                                          },
                                          "position": {
                                            "x": 494,
                                            "y": 287
                                          },
                                          "visible": true,
                                          "zIndex": 7
                                        },
                                        "data": {
                                          "title": "消息提示",
                                          "value": {
                                            "type": "success",
                                            "message": "删除成功"
                                          }
                                        },
                                        "executeFunction": "function logicNode(ctx) {\n  $message({\"message\":\"删除成功\",\"type\":\"success\"})\n}"
                                      },
                                      {
                                        "id": "e5491499-e78e-448a-8d88-ecc3b92ff195",
                                        "shape": {
                                          "name": "edge",
                                          "zIndex": 8,
                                          "source": {
                                            "cell": "d20a9f3f-3cd1-4fe5-a5d8-c90063ce0597",
                                            "port": "left"
                                          },
                                          "target": {
                                            "cell": "904cf603-c4c8-4b46-b44d-9f2949384dea",
                                            "port": "top"
                                          },
                                          "labels": [
                                            {
                                              "attrs": {
                                                "label": {
                                                  "text": "失败"
                                                }
                                              }
                                            }
                                          ]
                                        },
                                        "data": {
                                          "value": null
                                        }
                                      },
                                      {
                                        "id": "b5bc57a7-53db-4153-811d-8acba75c4725",
                                        "shape": {
                                          "name": "edge",
                                          "zIndex": 9,
                                          "source": {
                                            "cell": "904cf603-c4c8-4b46-b44d-9f2949384dea",
                                            "port": "bottom"
                                          },
                                          "target": {
                                            "cell": "9693a56b-1aeb-4904-b55c-e63564274770",
                                            "port": "top"
                                          }
                                        },
                                        "data": {
                                          "value": null
                                        }
                                      },
                                      {
                                        "id": "93872ea1-4032-4dce-b504-80c64511fd85",
                                        "shape": {
                                          "name": "edge",
                                          "zIndex": 10,
                                          "source": {
                                            "cell": "d20a9f3f-3cd1-4fe5-a5d8-c90063ce0597",
                                            "port": "right"
                                          },
                                          "target": {
                                            "cell": "9ea518f4-5781-4e11-9539-28a380d11a09",
                                            "port": "top"
                                          },
                                          "labels": [
                                            {
                                              "attrs": {
                                                "label": {
                                                  "text": "成功"
                                                }
                                              }
                                            }
                                          ]
                                        },
                                        "data": {
                                          "value": null
                                        }
                                      },
                                      {
                                        "id": "2ef24174-9288-45a3-99b5-b99c5d447536",
                                        "shape": {
                                          "name": "edge",
                                          "zIndex": 11,
                                          "source": {
                                            "cell": "9ea518f4-5781-4e11-9539-28a380d11a09",
                                            "port": "bottom"
                                          },
                                          "target": {
                                            "cell": "9693a56b-1aeb-4904-b55c-e63564274770",
                                            "port": "top"
                                          }
                                        },
                                        "data": {
                                          "value": null
                                        }
                                      }
                                    ]
                                  }
                                ]
                              },
                              "name": "delete",
                              "x-designable-id": "ykb4slkgh53",
                              "x-index": 3,
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-validator": []
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          },
          "form": {
            "labelCol": 6,
            "wrapperCol": 24,
            "colon": false,
            "feedbackLayout": "loose",
            "size": "default",
            "layout": "horizontal",
            "tooltipLayout": "icon",
            "labelAlign": "right",
            "wrapperAlign": "left",
            "shallow": false,
            "bordered": true,
            "style": {
              "opacity": 1
            },
            "x-decorator-props": {},
            "x-designer-extend": {
              "events": []
            },
            "events": {}
          }
        },
        "FORM": {
          "label": "表单",
          "value": "FORM",
          "schema": {
            "effectForm": {
              "type": "void",
              "x-component": "QueryEngine",
              "x-query-engine": {
                "service": "api-sup",
                "actions": {
                  "read": {
                    "autoFormatResult": true,
                    "immediate": true,
                    "ready": "{{() => { $form.readPretty = $$safeGetScope('$readOnly') \r\n return $$safeGetScope('$pageParams.primaryKeyValue') }}}",
                    "transformRequest": "{{\n                (data) => {\n                  return {\n                    ...data,\n                    'query': {\n                      \"fileRecordIdes\": { \"*\": {} },\n                      \"effectFormFinanceInfos\": { \"*\": {} },\n                      \"effectFormReqHeads\": { \"*\": {} },\n                      \"effectFormRelationForms\": { \"*\": {} },\n                      \"effectFormBankInfos\": { \"*\": {} },\n                      \"effectFormImportScenes\": { \"*\": {} },\n                      \"effectFormOrgCategorys\": { \"*\": {} },\n                      \"*\": {}\n                    },\n                    payload: [$$safeGetScope('$pageParams.primaryKeyValue')]\n                  }\n                }\n              }}",
                    "onSuccess": "{{(res) => {\r\n  if (![null, '', 'DRAFT'].includes(res.data[0].approveStatus)) {\r\n    $form.readPretty = true\r\n  }\r\n  \r\n  $form.setValues(res.data[0])\r\n\r\n  $queryEngine.request.baseRequest({\r\n    \"type\": \"effectForm\",\r\n    \"action\": \"getDetailByReviewForm\",\r\n    \"lang\": \"zh-cn\",\r\n    'actionConfig': { autoFormatResult: false },\r\n    \"payload\": [\r\n      $form.values.reviewFormId\r\n    ],\r\n    \"query\": {\r\n      \"*\": {}\r\n    }\r\n  }).then((res) => {\r\n    console.log(res, 'res')\r\n    const record = res.records[0]\r\n    if (record) {\r\n      $form.query('effectFormRelationForms').take(field => {\r\n        field.value = record.effectFormRelationForms || []\r\n      })\r\n      $form.query('effectFormOrgCategorys').take(field => {\r\n        field.value = record.effectFormOrgCategorys || []\r\n        field.value.forEach((item, index) => {\r\n          field.invoke('editRowByIndex', index)\r\n        })\r\n      })\r\n    }\r\n  })\r\n}}}"
                  },
                  "save": {
                    "transformRequest": "{{(data) => {\n                  return {\n                    ...data,\n                    'query': {\n                      \"*\": {},\n                      \"effectFormRelationForms\": { '*': {} },\n                      \"effectFormImportScenes\": { '*': {} },\n                      \"effectFormBankInfos\": { '*': {} },\n                      \"effectFormFinanceInfos\": { '*': {} },\n                      \"fileRecordIdes\": { '*': {} },\n                      \"effectFormReqHeads\": { '*': {} },\n                      \"effectFormOrgCategorys\": { '*': {} }\n                    }\n                  }\n                }}}",
                    "cascadeDeletion": true,
                    "autoFormatResult": true
                  }
                },
                "pagination": {
                  "pageSize": 15
                }
              },
              "properties": {
                "qenxcou4juw": {
                  "type": "void",
                  "x-component": "FormCollapse",
                  "x-component-props": {
                    "defaultOpenPanelCount": 9,
                    "accordion": false,
                    "style": {
                      "opacity": 1
                    }
                  },
                  "x-index": 0,
                  "name": "qenxcou4juw",
                  "x-designable-id": "e5qgltlu2of",
                  "x-decorator-props": {
                    "style": {
                      "opacity": 1
                    }
                  },
                  "x-designer-extend": {},
                  "properties": {
                    "4my2aszl25a": {
                      "type": "void",
                      "x-component": "FormCollapse.Item",
                      "x-component-props": {
                        "title": "供方生效",
                        "disabled": false,
                        "style": {
                          "opacity": 1
                        }
                      },
                      "x-index": 0,
                      "name": "4my2aszl25a",
                      "x-designable-id": "5yplnli84aw",
                      "x-decorator-props": {
                        "style": {
                          "opacity": 1
                        }
                      },
                      "x-designer-extend": {},
                      "properties": {
                        "lm49yg27mfn": {
                          "type": "void",
                          "x-component": "FormLayout",
                          "x-component-props": {
                            "colon": true,
                            "style": {
                              "opacity": 1
                            },
                            "layout": "vertical"
                          },
                          "name": "lm49yg27mfn",
                          "x-designable-id": "7oqu733njky",
                          "x-index": 0,
                          "x-decorator-props": {
                            "style": {
                              "opacity": 1
                            }
                          },
                          "x-designer-extend": {},
                          "properties": {
                            "19ni8nuyh47": {
                              "type": "void",
                              "x-component": "FormGrid",
                              "x-validator": [],
                              "x-component-props": {
                                "minWidth": 100,
                                "minColumns": 0,
                                "columnGap": 32,
                                "rowGap": 0,
                                "colWrap": true,
                                "maxColumns": 4,
                                "style": {
                                  "margin": "0px 0px 0px 0px",
                                  "padding": "0px 0px 0px 0px",
                                  "borderRadius": "0px 0px 0px 0px",
                                  "opacity": 1
                                }
                              },
                              "x-index": 0,
                              "name": "19ni8nuyh47",
                              "x-designable-id": "eughdufx3gp",
                              "properties": {
                                "col_effectFormNumber": {
                                  "type": "void",
                                  "x-component": "FormGrid.GridColumn",
                                  "x-index": 0,
                                  "name": "col_effectFormNumber",
                                  "x-designable-id": "5eh0a2ognd3",
                                  "x-component-props": {
                                    "gridSpan": 1,
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "properties": {
                                    "effectFormNumber": {
                                      "type": "string",
                                      "title": "供方生效单号",
                                      "x-decorator": "FormItem",
                                      "x-decorator-props": {
                                        "labelWidth": 80,
                                        "style": {
                                          "opacity": 1
                                        },
                                        "feedbackLayout": "loose"
                                      },
                                      "x-component": "Input",
                                      "x-designer-extend": {
                                        "model": {
                                          "businessType": "sccSupEffectForm",
                                          "reference": null,
                                          "modelId": "1668183594803449858",
                                          "field": "effectFormNumber",
                                          "originalField": "effectFormNumber"
                                        },
                                        "events": []
                                      },
                                      "x-validator": [],
                                      "x-index": 0,
                                      "name": "effectFormNumber",
                                      "x-designable-id": "1l4u7h0fvj2",
                                      "x-component-props": {
                                        "style": {
                                          "opacity": 1
                                        }
                                      },
                                      "x-pattern": "disabled"
                                    }
                                  }
                                },
                                "approveStatus": {
                                  "title": "审批状态",
                                  "x-decorator": "FormItem",
                                  "x-component": "Select",
                                  "name": "approveStatus",
                                  "x-designer-extend": {
                                    "model": {
                                      "businessType": "sup_effect_form_ide",
                                      "reference": null,
                                      "modelId": "1668183594803449858",
                                      "field": "approveStatus",
                                      "originalField": "approveStatus"
                                    },
                                    "source": {
                                      "sourceType": "MQL",
                                      "action": "query",
                                      "type": "base_dict_item_ide",
                                      "query": {
                                        "*": {}
                                      },
                                      "filter": {
                                        "$and": {
                                          "dictId": {
                                            "eq": "6805186166587392"
                                          }
                                        }
                                      },
                                      "service": "api-base",
                                      "payload": {
                                        "page": {
                                          "sort": "dictItemNo asc"
                                        }
                                      },
                                      "enum": [],
                                      "conditions": [
                                        [
                                          {
                                            "dataName": "dictId",
                                            "comparison": "eq",
                                            "valueType": "fixed",
                                            "modelField": "6805186166587392",
                                            "modelId": ""
                                          }
                                        ]
                                      ]
                                    },
                                    "events": []
                                  },
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "loose"
                                  },
                                  "x-component-props": {
                                    "size": "small",
                                    "multiple-limit": 0,
                                    "placeholder": "请选择",
                                    "style": {
                                      "opacity": 1
                                    },
                                    "fieldNames": {
                                      "label": "dictItemName",
                                      "value": "dictItemCode"
                                    },
                                    "@created": "{{() => {\n                    $queryEngine.request.baseRequest({\n                      type: 'base_dict_item_ide',\n                      action: 'query',\n                      payload: {\n                        filter: {\"$and\":{\"dictId\":{\"eq\":\"6805186166587392\"}}},\n                        page: {\"pageSize\":9999,\"pageNum\":1,\"sort\":\"dictItemNo asc\"},\n                      },\n                      query: {\"*\":{}},\n                      actionConfig: { service: 'api-base' },\n                      queryConfig: { staleTime: 300000 }\n                    }).then(res => {\n                      $self.dataSource = res.data || []\n                    })\n                  }}}"
                                  },
                                  "x-validator": [],
                                  "enum": [],
                                  "x-pattern": "disabled",
                                  "x-designable-id": "yaye1sx9eyd",
                                  "x-index": 1
                                },
                                "createdBy": {
                                  "type": "string",
                                  "title": "创建人",
                                  "x-decorator": "FormItem",
                                  "x-component": "Input",
                                  "name": "createdBy",
                                  "x-designer-extend": {
                                    "model": {
                                      "businessType": "sup_effect_form_ide",
                                      "reference": null,
                                      "modelId": "1668183594803449858",
                                      "field": "createdBy",
                                      "originalField": "createdBy"
                                    },
                                    "events": []
                                  },
                                  "x-validator": [],
                                  "x-component-props": {
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "loose"
                                  },
                                  "x-pattern": "disabled",
                                  "x-designable-id": "b8cej6yt7iz",
                                  "x-index": 2
                                },
                                "creationDate": {
                                  "type": "string",
                                  "title": "创建时间",
                                  "x-decorator": "FormItem",
                                  "x-component": "DatePicker",
                                  "x-component-props": {
                                    "type": "datetime",
                                    "editable": true,
                                    "clearable": true,
                                    "size": "small",
                                    "placeholder": "请选择",
                                    "start-placeholder": "",
                                    "end-placeholder": "",
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "name": "creationDate",
                                  "x-designer-extend": {
                                    "model": {
                                      "businessType": "sup_effect_form_ide",
                                      "reference": null,
                                      "modelId": "1668183594803449858",
                                      "field": "creationDate",
                                      "originalField": "creationDate"
                                    },
                                    "events": []
                                  },
                                  "x-validator": [],
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "loose"
                                  },
                                  "x-pattern": "disabled",
                                  "x-designable-id": "tjg439emqyp",
                                  "x-index": 3
                                },
                                "companyId": {
                                  "title": "供应商名称",
                                  "x-decorator": "FormItem",
                                  "x-component": "Selector",
                                  "name": "companyId",
                                  "x-designer-extend": {
                                    "model": {
                                      "businessType": "sup_effect_form_ide",
                                      "reference": {
                                        "modelName": "公司基本信息",
                                        "referenceField": "companyId",
                                        "modelId": "1668157712692989956",
                                        "businessType": "sup_company_info_ide"
                                      },
                                      "modelId": "1668183594803449858",
                                      "field": "companyId",
                                      "originalField": "companyId"
                                    },
                                    "events": [
                                      {
                                        "title": "冗余字段",
                                        "name": "select",
                                        "type": "logic",
                                        "args": [
                                          {
                                            "id": "a57e6682-324d-4107-bdea-fb58c89b6797",
                                            "shape": {
                                              "name": "logic-start",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 505,
                                                "y": 0
                                              },
                                              "visible": true,
                                              "zIndex": 1
                                            },
                                            "data": {
                                              "title": "开始",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "16abd0d4-64fe-43ea-a910-10173469d179",
                                            "shape": {
                                              "name": "logic-end",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 505,
                                                "y": 728.6999999999999
                                              },
                                              "visible": true,
                                              "zIndex": 2
                                            },
                                            "data": {
                                              "title": "结束",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "54b217fa-a3dc-4e0d-ae5c-a36aae2acf56",
                                            "shape": {
                                              "name": "logic-function",
                                              "size": {
                                                "width": 86,
                                                "height": 30
                                              },
                                              "position": {
                                                "x": 490,
                                                "y": 355
                                              },
                                              "visible": true,
                                              "zIndex": 3
                                            },
                                            "data": {
                                              "title": "自定义函数",
                                              "value": {
                                                "code": "function logicNode(ctx) {\n  if (ctx.payload?.[1]?.[0]) {\n    $form.setValues({\n      companyName: ctx.payload[1][0].companyName,\n      reviewFormNumber: null,\n      reviewFormId: null\n    })\n\n    // 清空引用场景\n    $form.query('effectFormImportScenes').take(field => {\n      for (let index = field.value.length; index >= 0; index -= 1) {\n        field.remove(index)\n      }\n    })\n\n    // 清空引用场景\n    $form.query('effectFormImportScenes').take(field => {\n      for (let index = field.value.length; index >= 0; index -= 1) {\n        field.remove(index)\n      }\n    })\n\n    // 清空引用场景\n    $form.query('effectFormImportScenes').take(field => {\n      for (let index = field.value.length; index >= 0; index -= 1) {\n        field.remove(index)\n      }\n    })\n\n    // 清空关联单据\n    $form.query('effectFormRelationForms').take(field => {\n      for (let index = field.value.length; index >= 0; index -= 1) {\n        field.remove(index)\n      }\n    })\n\n    // 清空寻源信息\n    $form.query('effectFormReqHeads').take(field => {\n      for (let index = field.value.length; index >= 0; index -= 1) {\n        field.remove(index)\n      }\n    })\n\n    // 清空财务信息\n    $form.query('effectFormFinanceInfos').take(field => {\n      for (let index = field.value.length; index >= 0; index -= 1) {\n        field.remove(index)\n      }\n    })\n\n    // 清空银行信息\n    $form.query('effectFormBankInfos').take(field => {\n      for (let index = field.value.length; index >= 0; index -= 1) {\n        field.remove(index)\n      }\n    })\n\n    // 清空引入组织和品类\n    $form.query('effectFormOrgCategorys').take(field => {\n      for (let index = field.value.length; index >= 0; index -= 1) {\n        field.remove(index)\n      }\n    })\n  }\n}"
                                              }
                                            },
                                            "executeFunction": "function logicNode(ctx) {\n  if (ctx.payload?.[1]?.[0]) {\n    $form.setValues({\n      companyName: ctx.payload[1][0].companyName,\n      reviewFormNumber: null,\n      reviewFormId: null\n    })\n\n    // 清空引用场景\n    $form.query('effectFormImportScenes').take(field => {\n      for (let index = field.value.length; index >= 0; index -= 1) {\n        field.remove(index)\n      }\n    })\n\n    // 清空引用场景\n    $form.query('effectFormImportScenes').take(field => {\n      for (let index = field.value.length; index >= 0; index -= 1) {\n        field.remove(index)\n      }\n    })\n\n    // 清空引用场景\n    $form.query('effectFormImportScenes').take(field => {\n      for (let index = field.value.length; index >= 0; index -= 1) {\n        field.remove(index)\n      }\n    })\n\n    // 清空关联单据\n    $form.query('effectFormRelationForms').take(field => {\n      for (let index = field.value.length; index >= 0; index -= 1) {\n        field.remove(index)\n      }\n    })\n\n    // 清空寻源信息\n    $form.query('effectFormReqHeads').take(field => {\n      for (let index = field.value.length; index >= 0; index -= 1) {\n        field.remove(index)\n      }\n    })\n\n    // 清空财务信息\n    $form.query('effectFormFinanceInfos').take(field => {\n      for (let index = field.value.length; index >= 0; index -= 1) {\n        field.remove(index)\n      }\n    })\n\n    // 清空银行信息\n    $form.query('effectFormBankInfos').take(field => {\n      for (let index = field.value.length; index >= 0; index -= 1) {\n        field.remove(index)\n      }\n    })\n\n    // 清空引入组织和品类\n    $form.query('effectFormOrgCategorys').take(field => {\n      for (let index = field.value.length; index >= 0; index -= 1) {\n        field.remove(index)\n      }\n    })\n  }\n}"
                                          },
                                          {
                                            "id": "346a0494-b40b-481a-b5a1-cceae03f41a1",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 4,
                                              "source": {
                                                "cell": "a57e6682-324d-4107-bdea-fb58c89b6797",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "54b217fa-a3dc-4e0d-ae5c-a36aae2acf56",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "d27a7cd8-0f16-4ce1-b745-b2a84d63a23a",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 5,
                                              "source": {
                                                "cell": "54b217fa-a3dc-4e0d-ae5c-a36aae2acf56",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "16abd0d4-64fe-43ea-a910-10173469d179",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          }
                                        ]
                                      }
                                    ],
                                    "reference": {
                                      "type": "PAGE",
                                      "content": {
                                        "pageId": "LLP1668195754958966786"
                                      },
                                      "backfill": {
                                        "type": "form",
                                        "tableTag": "",
                                        "options": [
                                          {
                                            "from": "companyCode",
                                            "to": {
                                              "address": "qenxcou4juw.4my2aszl25a.lm49yg27mfn.19ni8nuyh47.companyCode",
                                              "name": "companyCode",
                                              "title": "供应商编码",
                                              "root": {
                                                "_isJSONSchemaObject": true,
                                                "version": "2.0",
                                                "type": "string",
                                                "title": "供应商编码",
                                                "x-decorator": "FormItem",
                                                "x-component": "Input",
                                                "name": "companyCode",
                                                "x-designer-extend": {
                                                  "model": {
                                                    "businessType": "sup_effect_form_ide",
                                                    "reference": null,
                                                    "modelId": "1668183594803449858",
                                                    "field": "companyCode",
                                                    "originalField": "companyCode"
                                                  },
                                                  "events": []
                                                },
                                                "x-validator": [],
                                                "x-component-props": {
                                                  "style": {
                                                    "opacity": 1
                                                  }
                                                },
                                                "x-decorator-props": {
                                                  "style": {
                                                    "opacity": 1
                                                  },
                                                  "feedbackLayout": "loose"
                                                },
                                                "x-designable-id": "m1r0itbzmsn",
                                                "x-index": 4,
                                                "x-pattern": "disabled"
                                              },
                                              "disabled": false,
                                              "parent": {
                                                "path": "qenxcou4juw.4my2aszl25a.lm49yg27mfn.19ni8nuyh47"
                                              },
                                              "designableId": "m1r0itbzmsn",
                                              "children": []
                                            }
                                          },
                                          {
                                            "from": "supplierType",
                                            "to": {
                                              "address": "qenxcou4juw.4my2aszl25a.lm49yg27mfn.19ni8nuyh47.supplierType_copy",
                                              "name": "supplierType_copy",
                                              "title": "供应商类型",
                                              "root": {
                                                "_isJSONSchemaObject": true,
                                                "version": "2.0",
                                                "title": "供应商类型",
                                                "x-decorator": "FormItem",
                                                "x-component": "Select",
                                                "name": "supplierType_copy",
                                                "x-designer-extend": {
                                                  "model": {
                                                    "businessType": "sccSupEffectForm",
                                                    "reference": null,
                                                    "modelId": "1668183594803449858",
                                                    "field": "supplierType",
                                                    "originalField": "supplierType"
                                                  },
                                                  "source": {
                                                    "sourceType": "MQL",
                                                    "enum": [],
                                                    "action": "query",
                                                    "type": "sccBaseDictItem",
                                                    "query": {
                                                      "*": {}
                                                    },
                                                    "filter": {
                                                      "$and": {
                                                        "dictId": {
                                                          "eq": "310329876631680"
                                                        }
                                                      }
                                                    },
                                                    "payload": {
                                                      "page": {
                                                        "sort": "dictItemId asc"
                                                      }
                                                    }
                                                  },
                                                  "events": []
                                                },
                                                "x-validator": [],
                                                "x-component-props": {
                                                  "size": "small",
                                                  "multiple-limit": 0,
                                                  "placeholder": "请选择",
                                                  "style": {
                                                    "opacity": 1
                                                  },
                                                  "fieldNames": {
                                                    "label": "dictItemName",
                                                    "value": "dictItemId"
                                                  }
                                                },
                                                "enum": [],
                                                "x-decorator-props": {
                                                  "style": {
                                                    "opacity": 1
                                                  },
                                                  "feedbackLayout": "loose"
                                                },
                                                "x-designable-id": "581xj4kk9qn",
                                                "x-reactions": {
                                                  "effects": [
                                                    "onFieldMount"
                                                  ],
                                                  "fulfill": {
                                                    "run": "\n                    let args = ['query', 'paginationQuery'].includes('query') ?\n                      [{\"$and\":{\"dictId\":{\"eq\":\"310329876631680\"}}}, {\"pageSize\":9999,\"pageNum\":1,\"sort\":\"dictItemId asc\"}, { type: 'sccBaseDictItem', query: {\"*\":{}} }] :\n                      [{\"page\":{\"sort\":\"dictItemId asc\"}}, { type: 'sccBaseDictItem', filter: {\"$and\":{\"dictId\":{\"eq\":\"310329876631680\"}}}, query: {\"*\":{}} }]\n                    $queryEngine.request['query'](...args).then(res => {\n                      $self.dataSource = res.data || []\n                    })\n                  "
                                                  }
                                                },
                                                "x-index": 5,
                                                "x-pattern": "disabled"
                                              },
                                              "disabled": false,
                                              "parent": {
                                                "path": "qenxcou4juw.4my2aszl25a.lm49yg27mfn.19ni8nuyh47"
                                              },
                                              "designableId": "581xj4kk9qn",
                                              "children": []
                                            }
                                          }
                                        ]
                                      },
                                      "extend": {
                                        "functionCode": "LLP1668195754958966786"
                                      },
                                      "source": {
                                        "sourceType": "MQL",
                                        "action": "query",
                                        "service": "api-sup",
                                        "type": "sup_company_info_ide",
                                        "query": {
                                          "*": {}
                                        },
                                        "filter": {
                                          "sort": "",
                                          "$and": {
                                            "isBacklist": {
                                              "eq": "N"
                                            },
                                            "status": {
                                              "eq": "APPROVED"
                                            }
                                          }
                                        },
                                        "payload": {},
                                        "conditions": [
                                          [
                                            {
                                              "dataName": "isBacklist",
                                              "comparison": "eq",
                                              "valueType": "fixed",
                                              "modelField": "N",
                                              "modelId": ""
                                            },
                                            {
                                              "dataName": "status",
                                              "comparison": "eq",
                                              "valueType": "fixed",
                                              "modelField": "APPROVED",
                                              "modelId": ""
                                            }
                                          ]
                                        ]
                                      }
                                    }
                                  },
                                  "x-validator": [
                                    {
                                      "required": true
                                    }
                                  ],
                                  "x-component-props": {
                                    "pickerOptions": {
                                      "type": "dialog",
                                      "pageTitle": "供应商查询"
                                    },
                                    "style": {
                                      "opacity": 1
                                    },
                                    "fieldNames": {
                                      "label": "companyName",
                                      "value": "companyId"
                                    },
                                    "reference": {
                                      "type": "PAGE",
                                      "content": {
                                        "pageId": "LLP1668195754958966786"
                                      },
                                      "backfill": {
                                        "type": "form",
                                        "tableTag": "",
                                        "options": [
                                          {
                                            "from": "companyCode",
                                            "to": {
                                              "address": "qenxcou4juw.4my2aszl25a.lm49yg27mfn.19ni8nuyh47.companyCode",
                                              "name": "companyCode",
                                              "title": "供应商编码",
                                              "root": {
                                                "_isJSONSchemaObject": true,
                                                "version": "2.0",
                                                "type": "string",
                                                "title": "供应商编码",
                                                "x-decorator": "FormItem",
                                                "x-component": "Input",
                                                "name": "companyCode",
                                                "x-designer-extend": {
                                                  "model": {
                                                    "businessType": "sup_effect_form_ide",
                                                    "reference": null,
                                                    "modelId": "1668183594803449858",
                                                    "field": "companyCode",
                                                    "originalField": "companyCode"
                                                  },
                                                  "events": []
                                                },
                                                "x-validator": [],
                                                "x-component-props": {
                                                  "style": {
                                                    "opacity": 1
                                                  }
                                                },
                                                "x-decorator-props": {
                                                  "style": {
                                                    "opacity": 1
                                                  },
                                                  "feedbackLayout": "loose"
                                                },
                                                "x-designable-id": "m1r0itbzmsn",
                                                "x-index": 4,
                                                "x-pattern": "disabled"
                                              },
                                              "disabled": false,
                                              "parent": {
                                                "path": "qenxcou4juw.4my2aszl25a.lm49yg27mfn.19ni8nuyh47"
                                              },
                                              "designableId": "m1r0itbzmsn",
                                              "children": []
                                            }
                                          },
                                          {
                                            "from": "supplierType",
                                            "to": {
                                              "address": "qenxcou4juw.4my2aszl25a.lm49yg27mfn.19ni8nuyh47.supplierType_copy",
                                              "name": "supplierType_copy",
                                              "title": "供应商类型",
                                              "root": {
                                                "_isJSONSchemaObject": true,
                                                "version": "2.0",
                                                "title": "供应商类型",
                                                "x-decorator": "FormItem",
                                                "x-component": "Select",
                                                "name": "supplierType_copy",
                                                "x-designer-extend": {
                                                  "model": {
                                                    "businessType": "sccSupEffectForm",
                                                    "reference": null,
                                                    "modelId": "1668183594803449858",
                                                    "field": "supplierType",
                                                    "originalField": "supplierType"
                                                  },
                                                  "source": {
                                                    "sourceType": "MQL",
                                                    "enum": [],
                                                    "action": "query",
                                                    "type": "sccBaseDictItem",
                                                    "query": {
                                                      "*": {}
                                                    },
                                                    "filter": {
                                                      "$and": {
                                                        "dictId": {
                                                          "eq": "310329876631680"
                                                        }
                                                      }
                                                    },
                                                    "payload": {
                                                      "page": {
                                                        "sort": "dictItemId asc"
                                                      }
                                                    }
                                                  },
                                                  "events": []
                                                },
                                                "x-validator": [],
                                                "x-component-props": {
                                                  "size": "small",
                                                  "multiple-limit": 0,
                                                  "placeholder": "请选择",
                                                  "style": {
                                                    "opacity": 1
                                                  },
                                                  "fieldNames": {
                                                    "label": "dictItemName",
                                                    "value": "dictItemId"
                                                  }
                                                },
                                                "enum": [],
                                                "x-decorator-props": {
                                                  "style": {
                                                    "opacity": 1
                                                  },
                                                  "feedbackLayout": "loose"
                                                },
                                                "x-designable-id": "581xj4kk9qn",
                                                "x-reactions": {
                                                  "effects": [
                                                    "onFieldMount"
                                                  ],
                                                  "fulfill": {
                                                    "run": "\n                    let args = ['query', 'paginationQuery'].includes('query') ?\n                      [{\"$and\":{\"dictId\":{\"eq\":\"310329876631680\"}}}, {\"pageSize\":9999,\"pageNum\":1,\"sort\":\"dictItemId asc\"}, { type: 'sccBaseDictItem', query: {\"*\":{}} }] :\n                      [{\"page\":{\"sort\":\"dictItemId asc\"}}, { type: 'sccBaseDictItem', filter: {\"$and\":{\"dictId\":{\"eq\":\"310329876631680\"}}}, query: {\"*\":{}} }]\n                    $queryEngine.request['query'](...args).then(res => {\n                      $self.dataSource = res.data || []\n                    })\n                  "
                                                  }
                                                },
                                                "x-index": 5,
                                                "x-pattern": "disabled"
                                              },
                                              "disabled": false,
                                              "parent": {
                                                "path": "qenxcou4juw.4my2aszl25a.lm49yg27mfn.19ni8nuyh47"
                                              },
                                              "designableId": "581xj4kk9qn",
                                              "children": []
                                            }
                                          }
                                        ]
                                      },
                                      "extend": {
                                        "functionCode": "LLP1668195754958966786"
                                      },
                                      "source": {
                                        "sourceType": "MQL",
                                        "action": "query",
                                        "service": "api-sup",
                                        "type": "sup_company_info_ide",
                                        "query": {
                                          "*": {}
                                        },
                                        "filter": {
                                          "sort": "",
                                          "$and": {
                                            "isBacklist": {
                                              "eq": "N"
                                            },
                                            "status": {
                                              "eq": "APPROVED"
                                            }
                                          }
                                        },
                                        "payload": {},
                                        "conditions": [
                                          [
                                            {
                                              "dataName": "isBacklist",
                                              "comparison": "eq",
                                              "valueType": "fixed",
                                              "modelField": "N",
                                              "modelId": ""
                                            },
                                            {
                                              "dataName": "status",
                                              "comparison": "eq",
                                              "valueType": "fixed",
                                              "modelField": "APPROVED",
                                              "modelId": ""
                                            }
                                          ]
                                        ]
                                      }
                                    },
                                    "isAsyncDataSource": true,
                                    "@select": "{{(...args) => {\n        return new $LogicEngineRuntime({\n          nodes: [{\"id\":\"a57e6682-324d-4107-bdea-fb58c89b6797\",\"shape\":{\"name\":\"logic-start\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":505,\"y\":0},\"visible\":true,\"zIndex\":1},\"data\":{\"title\":\"开始\",\"value\":null}},{\"id\":\"16abd0d4-64fe-43ea-a910-10173469d179\",\"shape\":{\"name\":\"logic-end\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":505,\"y\":728.6999999999999},\"visible\":true,\"zIndex\":2},\"data\":{\"title\":\"结束\",\"value\":null}},{\"id\":\"54b217fa-a3dc-4e0d-ae5c-a36aae2acf56\",\"shape\":{\"name\":\"logic-function\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":490,\"y\":355},\"visible\":true,\"zIndex\":3},\"data\":{\"title\":\"自定义函数\",\"value\":{\"code\":\"function logicNode(ctx) {\\n  if (ctx.payload?.[1]?.[0]) {\\n    $form.setValues({\\n      companyName: ctx.payload[1][0].companyName,\\n      reviewFormNumber: null,\\n      reviewFormId: null\\n    })\\n\\n    // 清空引用场景\\n    $form.query('effectFormImportScenes').take(field => {\\n      for (let index = field.value.length; index >= 0; index -= 1) {\\n        field.remove(index)\\n      }\\n    })\\n\\n    // 清空引用场景\\n    $form.query('effectFormImportScenes').take(field => {\\n      for (let index = field.value.length; index >= 0; index -= 1) {\\n        field.remove(index)\\n      }\\n    })\\n\\n    // 清空引用场景\\n    $form.query('effectFormImportScenes').take(field => {\\n      for (let index = field.value.length; index >= 0; index -= 1) {\\n        field.remove(index)\\n      }\\n    })\\n\\n    // 清空关联单据\\n    $form.query('effectFormRelationForms').take(field => {\\n      for (let index = field.value.length; index >= 0; index -= 1) {\\n        field.remove(index)\\n      }\\n    })\\n\\n    // 清空寻源信息\\n    $form.query('effectFormReqHeads').take(field => {\\n      for (let index = field.value.length; index >= 0; index -= 1) {\\n        field.remove(index)\\n      }\\n    })\\n\\n    // 清空财务信息\\n    $form.query('effectFormFinanceInfos').take(field => {\\n      for (let index = field.value.length; index >= 0; index -= 1) {\\n        field.remove(index)\\n      }\\n    })\\n\\n    // 清空银行信息\\n    $form.query('effectFormBankInfos').take(field => {\\n      for (let index = field.value.length; index >= 0; index -= 1) {\\n        field.remove(index)\\n      }\\n    })\\n\\n    // 清空引入组织和品类\\n    $form.query('effectFormOrgCategorys').take(field => {\\n      for (let index = field.value.length; index >= 0; index -= 1) {\\n        field.remove(index)\\n      }\\n    })\\n  }\\n}\"}},\"executeFunction\":\"function logicNode(ctx) {\\n  if (ctx.payload?.[1]?.[0]) {\\n    $form.setValues({\\n      companyName: ctx.payload[1][0].companyName,\\n      reviewFormNumber: null,\\n      reviewFormId: null\\n    })\\n\\n    // 清空引用场景\\n    $form.query('effectFormImportScenes').take(field => {\\n      for (let index = field.value.length; index >= 0; index -= 1) {\\n        field.remove(index)\\n      }\\n    })\\n\\n    // 清空引用场景\\n    $form.query('effectFormImportScenes').take(field => {\\n      for (let index = field.value.length; index >= 0; index -= 1) {\\n        field.remove(index)\\n      }\\n    })\\n\\n    // 清空引用场景\\n    $form.query('effectFormImportScenes').take(field => {\\n      for (let index = field.value.length; index >= 0; index -= 1) {\\n        field.remove(index)\\n      }\\n    })\\n\\n    // 清空关联单据\\n    $form.query('effectFormRelationForms').take(field => {\\n      for (let index = field.value.length; index >= 0; index -= 1) {\\n        field.remove(index)\\n      }\\n    })\\n\\n    // 清空寻源信息\\n    $form.query('effectFormReqHeads').take(field => {\\n      for (let index = field.value.length; index >= 0; index -= 1) {\\n        field.remove(index)\\n      }\\n    })\\n\\n    // 清空财务信息\\n    $form.query('effectFormFinanceInfos').take(field => {\\n      for (let index = field.value.length; index >= 0; index -= 1) {\\n        field.remove(index)\\n      }\\n    })\\n\\n    // 清空银行信息\\n    $form.query('effectFormBankInfos').take(field => {\\n      for (let index = field.value.length; index >= 0; index -= 1) {\\n        field.remove(index)\\n      }\\n    })\\n\\n    // 清空引入组织和品类\\n    $form.query('effectFormOrgCategorys').take(field => {\\n      for (let index = field.value.length; index >= 0; index -= 1) {\\n        field.remove(index)\\n      }\\n    })\\n  }\\n}\"},{\"id\":\"346a0494-b40b-481a-b5a1-cceae03f41a1\",\"shape\":{\"name\":\"edge\",\"zIndex\":4,\"source\":{\"cell\":\"a57e6682-324d-4107-bdea-fb58c89b6797\",\"port\":\"bottom\"},\"target\":{\"cell\":\"54b217fa-a3dc-4e0d-ae5c-a36aae2acf56\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"d27a7cd8-0f16-4ce1-b745-b2a84d63a23a\",\"shape\":{\"name\":\"edge\",\"zIndex\":5,\"source\":{\"cell\":\"54b217fa-a3dc-4e0d-ae5c-a36aae2acf56\",\"port\":\"bottom\"},\"target\":{\"cell\":\"16abd0d4-64fe-43ea-a910-10173469d179\",\"port\":\"top\"}},\"data\":{\"value\":null}}],\n          payload: args,\n          scope: {\n            ...$root,\n            $designPages: $designPages,\n          }\n        }).run()\n      }}}"
                                  },
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1,
                                      "width": "auto"
                                    },
                                    "feedbackLayout": "loose"
                                  },
                                  "x-designable-id": "kxyqdgzlo03",
                                  "x-index": 4,
                                  "x-reactions": {
                                    "dependencies": [],
                                    "fulfill": {
                                      "state": {}
                                    }
                                  }
                                },
                                "companyCode": {
                                  "type": "string",
                                  "title": "供应商编码",
                                  "x-decorator": "FormItem",
                                  "x-component": "Input",
                                  "name": "companyCode",
                                  "x-designer-extend": {
                                    "model": {
                                      "businessType": "sup_effect_form_ide",
                                      "reference": null,
                                      "modelId": "1668183594803449858",
                                      "field": "companyCode",
                                      "originalField": "companyCode"
                                    },
                                    "events": []
                                  },
                                  "x-validator": [
                                    {
                                      "required": true
                                    }
                                  ],
                                  "x-component-props": {
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "loose"
                                  },
                                  "x-designable-id": "m1r0itbzmsn",
                                  "x-index": 5,
                                  "x-pattern": "disabled"
                                },
                                "supplierType": {
                                  "title": "供应商类型",
                                  "x-decorator": "FormItem",
                                  "x-component": "Select",
                                  "name": "supplierType",
                                  "x-designer-extend": {
                                    "model": {
                                      "businessType": "sccSupEffectForm",
                                      "reference": null,
                                      "modelId": "1668183594803449858",
                                      "field": "supplierType",
                                      "originalField": "supplierType"
                                    },
                                    "source": {
                                      "sourceType": "MQL",
                                      "enum": [],
                                      "action": "query",
                                      "type": "base_dict_item_ide",
                                      "query": {
                                        "*": {}
                                      },
                                      "filter": {
                                        "$and": {
                                          "dictId": {
                                            "eq": "310329876631680"
                                          }
                                        }
                                      },
                                      "payload": {
                                        "page": {
                                          "sort": "dictItemNo asc"
                                        }
                                      },
                                      "service": "api-base",
                                      "conditions": [
                                        [
                                          {
                                            "dataName": "dictId",
                                            "comparison": "eq",
                                            "valueType": "fixed",
                                            "modelField": "310329876631680",
                                            "modelId": ""
                                          }
                                        ]
                                      ]
                                    },
                                    "events": []
                                  },
                                  "x-validator": [],
                                  "x-component-props": {
                                    "size": "small",
                                    "multiple-limit": 0,
                                    "placeholder": "请选择",
                                    "style": {
                                      "opacity": 1
                                    },
                                    "fieldNames": {
                                      "label": "dictItemName",
                                      "value": "dictItemCode"
                                    },
                                    "@created": "{{() => {\n                    $queryEngine.request.baseRequest({\n                      type: 'base_dict_item_ide',\n                      action: 'query',\n                      payload: {\n                        filter: {\"$and\":{\"dictId\":{\"eq\":\"310329876631680\"}}},\n                        page: {\"pageSize\":9999,\"pageNum\":1,\"sort\":\"dictItemNo asc\"},\n                      },\n                      query: {\"*\":{}},\n                      actionConfig: { service: 'api-base' },\n                      queryConfig: { staleTime: 300000 }\n                    }).then(res => {\n                      $self.dataSource = res.data || []\n                    })\n                  }}}"
                                  },
                                  "enum": [],
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "loose"
                                  },
                                  "x-designable-id": "581xj4kk9qn",
                                  "x-index": 6,
                                  "x-pattern": "disabled"
                                },
                                "reviewFormId": {
                                  "title": "资质审查单号",
                                  "x-decorator": "FormItem",
                                  "x-component": "Selector",
                                  "name": "reviewFormId",
                                  "x-designer-extend": {
                                    "model": {
                                      "businessType": "sup_effect_form_ide",
                                      "reference": {
                                        "modelName": "资质审查单据",
                                        "referenceField": "reviewFormId",
                                        "modelId": "1668157711610859522",
                                        "businessType": "sup_auth_review_form_ide"
                                      },
                                      "modelId": "1668183594803449858",
                                      "field": "reviewFormId",
                                      "originalField": "reviewFormId"
                                    },
                                    "events": [
                                      {
                                        "title": "选中事件",
                                        "name": "select",
                                        "type": "logic",
                                        "args": [
                                          {
                                            "id": "420dd429-0645-4858-9bcb-0b606c78ad05",
                                            "shape": {
                                              "name": "logic-start",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 505,
                                                "y": 0
                                              },
                                              "visible": true,
                                              "zIndex": 1
                                            },
                                            "data": {
                                              "title": "开始",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "999cf7d4-faf7-4d21-9a1c-5595a27f7690",
                                            "shape": {
                                              "name": "logic-end",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 505,
                                                "y": 728.6999999999999
                                              },
                                              "visible": true,
                                              "zIndex": 2
                                            },
                                            "data": {
                                              "title": "结束",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "89066faa-64df-4105-bd90-a55732e332ae",
                                            "shape": {
                                              "name": "logic-function",
                                              "size": {
                                                "width": 86,
                                                "height": 30
                                              },
                                              "position": {
                                                "x": 489.5,
                                                "y": 216
                                              },
                                              "visible": true,
                                              "zIndex": 3
                                            },
                                            "data": {
                                              "title": "自定义函数",
                                              "value": {
                                                "code": "function logicNode(ctx) {\n  if (ctx.payload[1] && ctx.payload[1][0]) {\n    // 设置冗余参数\n    $form.setValues({\n      reviewFormId: {\n        reviewFormNumber: ctx.payload[1][0].reviewFormNumber\n      }\n    })\n  }\n\n  $queryEngine.request.baseRequest({\n    \"type\": \"effectForm\",\n    \"action\": \"getDetailByReviewForm\",\n    \"lang\": \"zh-cn\",\n    'actionConfig': { autoFormatResult: false },\n    \"payload\": [\n      ctx.payload[0]\n    ],\n    \"query\": {\n      \"*\": {}\n    }\n  }).then((res) => {\n    console.log(res, 'res')\n    const record = res.records[0]\n    if (record) {\n      $form.query('effectFormImportScenes').take(field => {\n        field.value = record.effectFormImportScenes || []\n      })\n      $form.query('effectFormRelationForms').take(field => {\n        field.value = record.effectFormRelationForms || []\n      })\n      $form.query('effectFormReqHeads').take(field => {\n        field.value = record.effectFormReqHeads || []\n      })\n      $form.query('effectFormFinanceInfos').take(field => {\n        field.value = record.effectFormFinanceInfos || []\n        record.effectFormFinanceInfos.forEach((item, index) => {\n          field.invoke('editRowByIndex', index)\n        })\n      })\n      $form.query('effectFormBankInfos').take(field => {\n        field.value = record.effectFormBankInfos || []\n        field.value.forEach((item, index) => {\n          field.invoke('editRowByIndex', index)\n        })\n      })\n      $form.query('effectFormOrgCategorys').take(field => {\n        field.value = record.effectFormOrgCategorys || []\n        field.value.forEach((item, index) => {\n          field.invoke('editRowByIndex', index)\n        })\n      })\n      record.fileRecordIdes.forEach(e => {\n        e.remark = 'Y'\n      })\n      $form.query('fileRecordIdes').take(field => {\n        field.value = record.fileRecordIdes || []\n      })\n      // 提取组织去重，并赋值到组织下拉框\n      // let org = [] // 组织的数组\n      // record.effectFormOrgCategorys.forEach(e => {\n      //   let bol = org.find(el => el.orgId === e.orgId) \n      //   if (!bol) {\n      //     org.push({\n      //       label: e.orgName,\n      //       value: e.orgId,\n      //       // orgId: e.orgId,\n      //       // orgName: e.orgName,\n      //       // orgCode: e.orgCode\n      //     })\n      //   }\n      // })\n\n      // $form.query('effectFormFinanceInfos.orgName').take(field => {\n      //   debugger\n      //   field.dataSource = org\n      // })\n      \n    }\n  })\n}"
                                              }
                                            },
                                            "executeFunction": "function logicNode(ctx) {\n  if (ctx.payload[1] && ctx.payload[1][0]) {\n    // 设置冗余参数\n    $form.setValues({\n      reviewFormId: {\n        reviewFormNumber: ctx.payload[1][0].reviewFormNumber\n      }\n    })\n  }\n\n  $queryEngine.request.baseRequest({\n    \"type\": \"effectForm\",\n    \"action\": \"getDetailByReviewForm\",\n    \"lang\": \"zh-cn\",\n    'actionConfig': { autoFormatResult: false },\n    \"payload\": [\n      ctx.payload[0]\n    ],\n    \"query\": {\n      \"*\": {}\n    }\n  }).then((res) => {\n    console.log(res, 'res')\n    const record = res.records[0]\n    if (record) {\n      $form.query('effectFormImportScenes').take(field => {\n        field.value = record.effectFormImportScenes || []\n      })\n      $form.query('effectFormRelationForms').take(field => {\n        field.value = record.effectFormRelationForms || []\n      })\n      $form.query('effectFormReqHeads').take(field => {\n        field.value = record.effectFormReqHeads || []\n      })\n      $form.query('effectFormFinanceInfos').take(field => {\n        field.value = record.effectFormFinanceInfos || []\n        record.effectFormFinanceInfos.forEach((item, index) => {\n          field.invoke('editRowByIndex', index)\n        })\n      })\n      $form.query('effectFormBankInfos').take(field => {\n        field.value = record.effectFormBankInfos || []\n        field.value.forEach((item, index) => {\n          field.invoke('editRowByIndex', index)\n        })\n      })\n      $form.query('effectFormOrgCategorys').take(field => {\n        field.value = record.effectFormOrgCategorys || []\n        field.value.forEach((item, index) => {\n          field.invoke('editRowByIndex', index)\n        })\n      })\n      record.fileRecordIdes.forEach(e => {\n        e.remark = 'Y'\n      })\n      $form.query('fileRecordIdes').take(field => {\n        field.value = record.fileRecordIdes || []\n      })\n      // 提取组织去重，并赋值到组织下拉框\n      // let org = [] // 组织的数组\n      // record.effectFormOrgCategorys.forEach(e => {\n      //   let bol = org.find(el => el.orgId === e.orgId) \n      //   if (!bol) {\n      //     org.push({\n      //       label: e.orgName,\n      //       value: e.orgId,\n      //       // orgId: e.orgId,\n      //       // orgName: e.orgName,\n      //       // orgCode: e.orgCode\n      //     })\n      //   }\n      // })\n\n      // $form.query('effectFormFinanceInfos.orgName').take(field => {\n      //   debugger\n      //   field.dataSource = org\n      // })\n      \n    }\n  })\n}"
                                          },
                                          {
                                            "id": "edc438f8-cdc7-4809-afa1-e8ab69e7b65a",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 4,
                                              "source": {
                                                "cell": "420dd429-0645-4858-9bcb-0b606c78ad05",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "89066faa-64df-4105-bd90-a55732e332ae",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "85d4ece4-397d-49da-88e5-4cab6a2320ae",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 5,
                                              "source": {
                                                "cell": "89066faa-64df-4105-bd90-a55732e332ae",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "999cf7d4-faf7-4d21-9a1c-5595a27f7690",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          }
                                        ]
                                      },
                                      {
                                        "title": "值改变事件",
                                        "name": "valueChange",
                                        "type": "logic",
                                        "args": [
                                          {
                                            "id": "099bb595-d6cc-4b2f-9de6-a269589877b6",
                                            "shape": {
                                              "name": "logic-start",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 508,
                                                "y": 0
                                              },
                                              "visible": true,
                                              "zIndex": 1
                                            },
                                            "data": {
                                              "title": "开始",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "8b60a2ad-cccc-49ba-9410-777be86299ea",
                                            "shape": {
                                              "name": "logic-end",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 508,
                                                "y": 522.1999999999999
                                              },
                                              "visible": true,
                                              "zIndex": 2
                                            },
                                            "data": {
                                              "title": "结束",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "9831203c-3c76-44a1-bcd9-bd842a731645",
                                            "shape": {
                                              "name": "logic-function",
                                              "size": {
                                                "width": 86,
                                                "height": 30
                                              },
                                              "position": {
                                                "x": 477,
                                                "y": 242
                                              },
                                              "visible": true,
                                              "zIndex": 3
                                            },
                                            "data": {
                                              "title": "自定义函数",
                                              "value": {
                                                "code": ""
                                              }
                                            },
                                            "executeFunction": ""
                                          },
                                          {
                                            "id": "89799cd7-6d6b-464a-9cab-3f3ee2fc93f8",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 4,
                                              "source": {
                                                "cell": "099bb595-d6cc-4b2f-9de6-a269589877b6",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "9831203c-3c76-44a1-bcd9-bd842a731645",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "c6dfc46e-1301-40ae-b4ec-416fc06e3ad2",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 5,
                                              "source": {
                                                "cell": "9831203c-3c76-44a1-bcd9-bd842a731645",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "8b60a2ad-cccc-49ba-9410-777be86299ea",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          }
                                        ]
                                      }
                                    ],
                                    "reference": {
                                      "type": "PAGE",
                                      "content": {
                                        "pageId": "LLP1671422650607554561"
                                      },
                                      "backfill": {
                                        "type": "form",
                                        "tableTag": "",
                                        "options": [
                                          {
                                            "from": "quaReviewType",
                                            "to": {
                                              "address": "qenxcou4juw.4my2aszl25a.lm49yg27mfn.19ni8nuyh47.quaReviewType",
                                              "name": "quaReviewType",
                                              "title": "资质审查类型",
                                              "root": {
                                                "_isJSONSchemaObject": true,
                                                "version": "2.0",
                                                "title": "资质审查类型",
                                                "x-decorator": "FormItem",
                                                "x-component": "Select",
                                                "name": "quaReviewType",
                                                "x-designer-extend": {
                                                  "model": {
                                                    "businessType": "sup_effect_form_ide",
                                                    "reference": null,
                                                    "modelId": "1668183594803449858",
                                                    "field": "quaReviewType",
                                                    "originalField": "quaReviewType"
                                                  },
                                                  "source": {
                                                    "sourceType": "MQL",
                                                    "enum": [],
                                                    "action": "query",
                                                    "type": "base_dict_ide",
                                                    "query": {
                                                      "*": {}
                                                    },
                                                    "filter": {
                                                      "$and": {
                                                        "dictId": {
                                                          "eq": "6763956529790976"
                                                        },
                                                        "dictCode": {
                                                          "eq": "QUA_REVIEW_TYPE"
                                                        }
                                                      }
                                                    },
                                                    "payload": {
                                                      "page": {
                                                        "sort": "dictId asc"
                                                      }
                                                    },
                                                    "service": "api-base"
                                                  },
                                                  "events": []
                                                },
                                                "x-validator": [],
                                                "x-component-props": {
                                                  "size": "small",
                                                  "multiple-limit": 0,
                                                  "placeholder": "请选择",
                                                  "style": {
                                                    "opacity": 1
                                                  },
                                                  "fieldNames": {
                                                    "label": "dictName",
                                                    "value": "dictId"
                                                  },
                                                  "@created": "{{() => {\n                    let args = ['query', 'paginationQuery'].includes('query') ?\n                    [{\"$and\":{\"dictId\":{\"eq\":\"6763956529790976\"},\"dictCode\":{\"eq\":\"QUA_REVIEW_TYPE\"}}}, {\"pageSize\":9999,\"pageNum\":1,\"sort\":\"dictId asc\"}, { type: 'base_dict_ide', query: {\"*\":{}}, service:\"api-base\" }] :\n                    [{\"page\":{\"sort\":\"dictId asc\"}}, { type: 'base_dict_ide', filter: {\"$and\":{\"dictId\":{\"eq\":\"6763956529790976\"},\"dictCode\":{\"eq\":\"QUA_REVIEW_TYPE\"}}}, query: {\"*\":{}}, service:\"api-base\" }]\n                    $queryEngine.request['query'](...args).then(res => {\n                      console.log('123', res.data)\n                      $self.dataSource = res.data || []\n                    })\n                  }}}"
                                                },
                                                "enum": [],
                                                "x-decorator-props": {
                                                  "style": {
                                                    "opacity": 1
                                                  },
                                                  "feedbackLayout": "loose"
                                                },
                                                "x-designable-id": "zned7h44578",
                                                "x-index": 8,
                                                "x-pattern": "disabled"
                                              },
                                              "disabled": false,
                                              "parent": {
                                                "path": "qenxcou4juw.4my2aszl25a.lm49yg27mfn.19ni8nuyh47"
                                              },
                                              "designableId": "zned7h44578",
                                              "children": []
                                            }
                                          }
                                        ]
                                      },
                                      "extend": {
                                        "functionCode": "LLP1671422650607554561"
                                      },
                                      "source": {
                                        "sourceType": "MQL",
                                        "action": "query",
                                        "service": "api-sup",
                                        "type": "sup_auth_review_form_ide",
                                        "query": {
                                          "*": {}
                                        },
                                        "filter": {
                                          "sort": "",
                                          "$and": {
                                            "vendorCode": {
                                              "eq": "${m1r0itbzmsn}"
                                            },
                                            "approveStatus": {
                                              "eq": "APPROVED"
                                            }
                                          }
                                        },
                                        "payload": {},
                                        "conditions": [
                                          [
                                            {
                                              "dataName": "vendorCode",
                                              "comparison": "eq",
                                              "valueType": "dynamic",
                                              "modelField": "m1r0itbzmsn",
                                              "modelId": "m1r0itbzmsn"
                                            },
                                            {
                                              "dataName": "approveStatus",
                                              "comparison": "eq",
                                              "valueType": "fixed",
                                              "modelField": "APPROVED",
                                              "modelId": ""
                                            }
                                          ]
                                        ]
                                      }
                                    }
                                  },
                                  "x-validator": [
                                    {
                                      "required": true
                                    }
                                  ],
                                  "x-component-props": {
                                    "pickerOptions": {
                                      "type": "dialog",
                                      "pageTitle": "资质审查单快查-V2",
                                      "title": "资质审查单"
                                    },
                                    "isAsyncDataSource": true,
                                    "style": {
                                      "opacity": 1
                                    },
                                    "fieldNames": {
                                      "label": "reviewFormNumber",
                                      "value": "reviewFormId"
                                    },
                                    "reference": {
                                      "type": "PAGE",
                                      "content": {
                                        "pageId": "LLP1671422650607554561"
                                      },
                                      "backfill": {
                                        "type": "form",
                                        "tableTag": "",
                                        "options": [
                                          {
                                            "from": "quaReviewType",
                                            "to": {
                                              "address": "qenxcou4juw.4my2aszl25a.lm49yg27mfn.19ni8nuyh47.quaReviewType",
                                              "name": "quaReviewType",
                                              "title": "资质审查类型",
                                              "root": {
                                                "_isJSONSchemaObject": true,
                                                "version": "2.0",
                                                "title": "资质审查类型",
                                                "x-decorator": "FormItem",
                                                "x-component": "Select",
                                                "name": "quaReviewType",
                                                "x-designer-extend": {
                                                  "model": {
                                                    "businessType": "sup_effect_form_ide",
                                                    "reference": null,
                                                    "modelId": "1668183594803449858",
                                                    "field": "quaReviewType",
                                                    "originalField": "quaReviewType"
                                                  },
                                                  "source": {
                                                    "sourceType": "MQL",
                                                    "enum": [],
                                                    "action": "query",
                                                    "type": "base_dict_ide",
                                                    "query": {
                                                      "*": {}
                                                    },
                                                    "filter": {
                                                      "$and": {
                                                        "dictId": {
                                                          "eq": "6763956529790976"
                                                        },
                                                        "dictCode": {
                                                          "eq": "QUA_REVIEW_TYPE"
                                                        }
                                                      }
                                                    },
                                                    "payload": {
                                                      "page": {
                                                        "sort": "dictId asc"
                                                      }
                                                    },
                                                    "service": "api-base"
                                                  },
                                                  "events": []
                                                },
                                                "x-validator": [],
                                                "x-component-props": {
                                                  "size": "small",
                                                  "multiple-limit": 0,
                                                  "placeholder": "请选择",
                                                  "style": {
                                                    "opacity": 1
                                                  },
                                                  "fieldNames": {
                                                    "label": "dictName",
                                                    "value": "dictId"
                                                  },
                                                  "@created": "{{() => {\n                    let args = ['query', 'paginationQuery'].includes('query') ?\n                    [{\"$and\":{\"dictId\":{\"eq\":\"6763956529790976\"},\"dictCode\":{\"eq\":\"QUA_REVIEW_TYPE\"}}}, {\"pageSize\":9999,\"pageNum\":1,\"sort\":\"dictId asc\"}, { type: 'base_dict_ide', query: {\"*\":{}}, service:\"api-base\" }] :\n                    [{\"page\":{\"sort\":\"dictId asc\"}}, { type: 'base_dict_ide', filter: {\"$and\":{\"dictId\":{\"eq\":\"6763956529790976\"},\"dictCode\":{\"eq\":\"QUA_REVIEW_TYPE\"}}}, query: {\"*\":{}}, service:\"api-base\" }]\n                    $queryEngine.request['query'](...args).then(res => {\n                      console.log('123', res.data)\n                      $self.dataSource = res.data || []\n                    })\n                  }}}"
                                                },
                                                "enum": [],
                                                "x-decorator-props": {
                                                  "style": {
                                                    "opacity": 1
                                                  },
                                                  "feedbackLayout": "loose"
                                                },
                                                "x-designable-id": "zned7h44578",
                                                "x-index": 8,
                                                "x-pattern": "disabled"
                                              },
                                              "disabled": false,
                                              "parent": {
                                                "path": "qenxcou4juw.4my2aszl25a.lm49yg27mfn.19ni8nuyh47"
                                              },
                                              "designableId": "zned7h44578",
                                              "children": []
                                            }
                                          }
                                        ]
                                      },
                                      "extend": {
                                        "functionCode": "LLP1671422650607554561"
                                      },
                                      "source": {
                                        "sourceType": "MQL",
                                        "action": "query",
                                        "service": "api-sup",
                                        "type": "sup_auth_review_form_ide",
                                        "query": {
                                          "*": {}
                                        },
                                        "filter": {
                                          "sort": "",
                                          "$and": {
                                            "vendorCode": {
                                              "eq": "${m1r0itbzmsn}"
                                            },
                                            "approveStatus": {
                                              "eq": "APPROVED"
                                            }
                                          }
                                        },
                                        "payload": {},
                                        "conditions": [
                                          [
                                            {
                                              "dataName": "vendorCode",
                                              "comparison": "eq",
                                              "valueType": "dynamic",
                                              "modelField": "m1r0itbzmsn",
                                              "modelId": "m1r0itbzmsn"
                                            },
                                            {
                                              "dataName": "approveStatus",
                                              "comparison": "eq",
                                              "valueType": "fixed",
                                              "modelField": "APPROVED",
                                              "modelId": ""
                                            }
                                          ]
                                        ]
                                      }
                                    },
                                    "@select": "{{(...args) => {\n        return new $LogicEngineRuntime({\n          nodes: [{\"id\":\"420dd429-0645-4858-9bcb-0b606c78ad05\",\"shape\":{\"name\":\"logic-start\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":505,\"y\":0},\"visible\":true,\"zIndex\":1},\"data\":{\"title\":\"开始\",\"value\":null}},{\"id\":\"999cf7d4-faf7-4d21-9a1c-5595a27f7690\",\"shape\":{\"name\":\"logic-end\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":505,\"y\":728.6999999999999},\"visible\":true,\"zIndex\":2},\"data\":{\"title\":\"结束\",\"value\":null}},{\"id\":\"89066faa-64df-4105-bd90-a55732e332ae\",\"shape\":{\"name\":\"logic-function\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":489.5,\"y\":216},\"visible\":true,\"zIndex\":3},\"data\":{\"title\":\"自定义函数\",\"value\":{\"code\":\"function logicNode(ctx) {\\n  if (ctx.payload[1] && ctx.payload[1][0]) {\\n    // 设置冗余参数\\n    $form.setValues({\\n      reviewFormId: {\\n        reviewFormNumber: ctx.payload[1][0].reviewFormNumber\\n      }\\n    })\\n  }\\n\\n  $queryEngine.request.baseRequest({\\n    \\\"type\\\": \\\"effectForm\\\",\\n    \\\"action\\\": \\\"getDetailByReviewForm\\\",\\n    \\\"lang\\\": \\\"zh-cn\\\",\\n    'actionConfig': { autoFormatResult: false },\\n    \\\"payload\\\": [\\n      ctx.payload[0]\\n    ],\\n    \\\"query\\\": {\\n      \\\"*\\\": {}\\n    }\\n  }).then((res) => {\\n    console.log(res, 'res')\\n    const record = res.records[0]\\n    if (record) {\\n      $form.query('effectFormImportScenes').take(field => {\\n        field.value = record.effectFormImportScenes || []\\n      })\\n      $form.query('effectFormRelationForms').take(field => {\\n        field.value = record.effectFormRelationForms || []\\n      })\\n      $form.query('effectFormReqHeads').take(field => {\\n        field.value = record.effectFormReqHeads || []\\n      })\\n      $form.query('effectFormFinanceInfos').take(field => {\\n        field.value = record.effectFormFinanceInfos || []\\n        record.effectFormFinanceInfos.forEach((item, index) => {\\n          field.invoke('editRowByIndex', index)\\n        })\\n      })\\n      $form.query('effectFormBankInfos').take(field => {\\n        field.value = record.effectFormBankInfos || []\\n        field.value.forEach((item, index) => {\\n          field.invoke('editRowByIndex', index)\\n        })\\n      })\\n      $form.query('effectFormOrgCategorys').take(field => {\\n        field.value = record.effectFormOrgCategorys || []\\n        field.value.forEach((item, index) => {\\n          field.invoke('editRowByIndex', index)\\n        })\\n      })\\n      record.fileRecordIdes.forEach(e => {\\n        e.remark = 'Y'\\n      })\\n      $form.query('fileRecordIdes').take(field => {\\n        field.value = record.fileRecordIdes || []\\n      })\\n      // 提取组织去重，并赋值到组织下拉框\\n      // let org = [] // 组织的数组\\n      // record.effectFormOrgCategorys.forEach(e => {\\n      //   let bol = org.find(el => el.orgId === e.orgId) \\n      //   if (!bol) {\\n      //     org.push({\\n      //       label: e.orgName,\\n      //       value: e.orgId,\\n      //       // orgId: e.orgId,\\n      //       // orgName: e.orgName,\\n      //       // orgCode: e.orgCode\\n      //     })\\n      //   }\\n      // })\\n\\n      // $form.query('effectFormFinanceInfos.orgName').take(field => {\\n      //   debugger\\n      //   field.dataSource = org\\n      // })\\n      \\n    }\\n  })\\n}\"}},\"executeFunction\":\"function logicNode(ctx) {\\n  if (ctx.payload[1] && ctx.payload[1][0]) {\\n    // 设置冗余参数\\n    $form.setValues({\\n      reviewFormId: {\\n        reviewFormNumber: ctx.payload[1][0].reviewFormNumber\\n      }\\n    })\\n  }\\n\\n  $queryEngine.request.baseRequest({\\n    \\\"type\\\": \\\"effectForm\\\",\\n    \\\"action\\\": \\\"getDetailByReviewForm\\\",\\n    \\\"lang\\\": \\\"zh-cn\\\",\\n    'actionConfig': { autoFormatResult: false },\\n    \\\"payload\\\": [\\n      ctx.payload[0]\\n    ],\\n    \\\"query\\\": {\\n      \\\"*\\\": {}\\n    }\\n  }).then((res) => {\\n    console.log(res, 'res')\\n    const record = res.records[0]\\n    if (record) {\\n      $form.query('effectFormImportScenes').take(field => {\\n        field.value = record.effectFormImportScenes || []\\n      })\\n      $form.query('effectFormRelationForms').take(field => {\\n        field.value = record.effectFormRelationForms || []\\n      })\\n      $form.query('effectFormReqHeads').take(field => {\\n        field.value = record.effectFormReqHeads || []\\n      })\\n      $form.query('effectFormFinanceInfos').take(field => {\\n        field.value = record.effectFormFinanceInfos || []\\n        record.effectFormFinanceInfos.forEach((item, index) => {\\n          field.invoke('editRowByIndex', index)\\n        })\\n      })\\n      $form.query('effectFormBankInfos').take(field => {\\n        field.value = record.effectFormBankInfos || []\\n        field.value.forEach((item, index) => {\\n          field.invoke('editRowByIndex', index)\\n        })\\n      })\\n      $form.query('effectFormOrgCategorys').take(field => {\\n        field.value = record.effectFormOrgCategorys || []\\n        field.value.forEach((item, index) => {\\n          field.invoke('editRowByIndex', index)\\n        })\\n      })\\n      record.fileRecordIdes.forEach(e => {\\n        e.remark = 'Y'\\n      })\\n      $form.query('fileRecordIdes').take(field => {\\n        field.value = record.fileRecordIdes || []\\n      })\\n      // 提取组织去重，并赋值到组织下拉框\\n      // let org = [] // 组织的数组\\n      // record.effectFormOrgCategorys.forEach(e => {\\n      //   let bol = org.find(el => el.orgId === e.orgId) \\n      //   if (!bol) {\\n      //     org.push({\\n      //       label: e.orgName,\\n      //       value: e.orgId,\\n      //       // orgId: e.orgId,\\n      //       // orgName: e.orgName,\\n      //       // orgCode: e.orgCode\\n      //     })\\n      //   }\\n      // })\\n\\n      // $form.query('effectFormFinanceInfos.orgName').take(field => {\\n      //   debugger\\n      //   field.dataSource = org\\n      // })\\n      \\n    }\\n  })\\n}\"},{\"id\":\"edc438f8-cdc7-4809-afa1-e8ab69e7b65a\",\"shape\":{\"name\":\"edge\",\"zIndex\":4,\"source\":{\"cell\":\"420dd429-0645-4858-9bcb-0b606c78ad05\",\"port\":\"bottom\"},\"target\":{\"cell\":\"89066faa-64df-4105-bd90-a55732e332ae\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"85d4ece4-397d-49da-88e5-4cab6a2320ae\",\"shape\":{\"name\":\"edge\",\"zIndex\":5,\"source\":{\"cell\":\"89066faa-64df-4105-bd90-a55732e332ae\",\"port\":\"bottom\"},\"target\":{\"cell\":\"999cf7d4-faf7-4d21-9a1c-5595a27f7690\",\"port\":\"top\"}},\"data\":{\"value\":null}}],\n          payload: args,\n          scope: {\n            ...$root,\n            $designPages: $designPages,\n          }\n        }).run()\n      }}}",
                                    "@valueChange": "{{(...args) => {\n        return new $LogicEngineRuntime({\n          nodes: [{\"id\":\"099bb595-d6cc-4b2f-9de6-a269589877b6\",\"shape\":{\"name\":\"logic-start\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":508,\"y\":0},\"visible\":true,\"zIndex\":1},\"data\":{\"title\":\"开始\",\"value\":null}},{\"id\":\"8b60a2ad-cccc-49ba-9410-777be86299ea\",\"shape\":{\"name\":\"logic-end\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":508,\"y\":522.1999999999999},\"visible\":true,\"zIndex\":2},\"data\":{\"title\":\"结束\",\"value\":null}},{\"id\":\"9831203c-3c76-44a1-bcd9-bd842a731645\",\"shape\":{\"name\":\"logic-function\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":477,\"y\":242},\"visible\":true,\"zIndex\":3},\"data\":{\"title\":\"自定义函数\",\"value\":{\"code\":\"\"}},\"executeFunction\":\"\"},{\"id\":\"89799cd7-6d6b-464a-9cab-3f3ee2fc93f8\",\"shape\":{\"name\":\"edge\",\"zIndex\":4,\"source\":{\"cell\":\"099bb595-d6cc-4b2f-9de6-a269589877b6\",\"port\":\"bottom\"},\"target\":{\"cell\":\"9831203c-3c76-44a1-bcd9-bd842a731645\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"c6dfc46e-1301-40ae-b4ec-416fc06e3ad2\",\"shape\":{\"name\":\"edge\",\"zIndex\":5,\"source\":{\"cell\":\"9831203c-3c76-44a1-bcd9-bd842a731645\",\"port\":\"bottom\"},\"target\":{\"cell\":\"8b60a2ad-cccc-49ba-9410-777be86299ea\",\"port\":\"top\"}},\"data\":{\"value\":null}}],\n          payload: args,\n          scope: {\n            ...$root,\n            $designPages: $designPages,\n          }\n        }).run()\n      }}}"
                                  },
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "loose"
                                  },
                                  "x-designable-id": "8c05tpegvc7",
                                  "x-index": 7
                                },
                                "quaReviewType": {
                                  "title": "资质审查类型",
                                  "x-decorator": "FormItem",
                                  "x-component": "Select",
                                  "name": "quaReviewType",
                                  "x-designer-extend": {
                                    "model": {
                                      "businessType": "sup_effect_form_ide",
                                      "reference": null,
                                      "modelId": "1668183594803449858",
                                      "field": "quaReviewType",
                                      "originalField": "quaReviewType"
                                    },
                                    "source": {
                                      "sourceType": "MQL",
                                      "action": "query",
                                      "type": "base_dict_item_ide",
                                      "query": {
                                        "*": {}
                                      },
                                      "filter": {
                                        "$and": {
                                          "dictId": {
                                            "eq": "6763956529790976"
                                          }
                                        }
                                      },
                                      "service": "api-base",
                                      "payload": {
                                        "page": {
                                          "sort": "dictItemNo asc"
                                        }
                                      },
                                      "enum": [],
                                      "conditions": [
                                        [
                                          {
                                            "dataName": "dictId",
                                            "comparison": "eq",
                                            "valueType": "fixed",
                                            "modelField": "6763956529790976",
                                            "modelId": ""
                                          }
                                        ]
                                      ]
                                    },
                                    "events": []
                                  },
                                  "x-validator": [],
                                  "x-component-props": {
                                    "size": "small",
                                    "multiple-limit": 0,
                                    "placeholder": "请选择",
                                    "style": {
                                      "opacity": 1
                                    },
                                    "fieldNames": {
                                      "label": "dictItemName",
                                      "value": "dictItemCode"
                                    },
                                    "@created": "{{() => {\n                    $queryEngine.request.baseRequest({\n                      type: 'base_dict_item_ide',\n                      action: 'query',\n                      payload: {\n                        filter: {\"$and\":{\"dictId\":{\"eq\":\"6763956529790976\"}}},\n                        page: {\"pageSize\":9999,\"pageNum\":1,\"sort\":\"dictItemNo asc\"},\n                      },\n                      query: {\"*\":{}},\n                      actionConfig: { service: 'api-base' },\n                      queryConfig: { staleTime: 300000 }\n                    }).then(res => {\n                      $self.dataSource = res.data || []\n                    })\n                  }}}"
                                  },
                                  "enum": [],
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "loose"
                                  },
                                  "x-designable-id": "zned7h44578",
                                  "x-index": 8,
                                  "x-pattern": "disabled"
                                },
                                "5y2w0oh56zz": {
                                  "type": "void",
                                  "x-component": "FormGrid.GridColumn",
                                  "x-component-props": {
                                    "gridSpan": 1,
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-designable-id": "5y2w0oh56zz",
                                  "x-index": 9,
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-designer-extend": {}
                                },
                                "z1ux4kiic9t": {
                                  "type": "void",
                                  "x-component": "FormGrid.GridColumn",
                                  "x-component-props": {
                                    "gridSpan": 1,
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-designable-id": "z1ux4kiic9t",
                                  "x-index": 10,
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-designer-extend": {}
                                },
                                "a734hr0bt15": {
                                  "type": "void",
                                  "x-component": "FormGrid.GridColumn",
                                  "x-component-props": {
                                    "gridSpan": 1,
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-designable-id": "a734hr0bt15",
                                  "x-index": 11
                                },
                                "importReminder": {
                                  "type": "string",
                                  "title": "引入提醒",
                                  "x-decorator": "FormItem",
                                  "x-component": "Input.TextArea",
                                  "x-validator": [],
                                  "x-component-props": {
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-designer-extend": {
                                    "events": []
                                  },
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "loose",
                                    "gridSpan": 4
                                  },
                                  "name": "importReminder",
                                  "x-designable-id": "o623k83xgie",
                                  "x-index": 12
                                }
                              }
                            }
                          }
                        },
                        "x-index": {
                          "name": "x-index",
                          "x-designable-id": "f10qneuf795",
                          "x-index": 1
                        }
                      }
                    },
                    "2j6txw3yd8q": {
                      "type": "void",
                      "x-component": "FormCollapse.Item",
                      "x-component-props": {
                        "title": "引入场景",
                        "disabled": false,
                        "style": {
                          "opacity": 1
                        }
                      },
                      "name": "2j6txw3yd8q",
                      "x-designable-id": "qrr5r3pkvdl",
                      "x-index": 1,
                      "x-decorator-props": {
                        "style": {
                          "opacity": 1
                        }
                      },
                      "x-designer-extend": {},
                      "properties": {
                        "effectFormImportScenes": {
                          "type": "array",
                          "x-decorator": "FormItem",
                          "x-component": "RenderTable",
                          "x-validator": [],
                          "x-decorator-props": {
                            "style": "padding: 0 20px;",
                            "feedbackLayout": "loose"
                          },
                          "x-designable-id": "xp2i2l230st",
                          "x-component-props": {
                            "stripe": false,
                            "border": true,
                            "show-header": true,
                            "size": "small",
                            "style": "width:100%",
                            "preColumns": [
                              "seq"
                            ],
                            "editMode": false,
                            "primaryKey": "id",
                            "cascadeDeletion": true,
                            "height": "300px",
                            "pagination": false,
                            "showHeader": true,
                            "openCustomTable": false,
                            "dblclickEditable": false
                          },
                          "x-designer-extend": {
                            "page": {
                              "pagination": false
                            },
                            "events": []
                          },
                          "x-index": 0,
                          "name": "effectFormImportScenes",
                          "properties": {
                            "7rxto23wdyy": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "title": "引入场景",
                                "sortable": true,
                                "resizable": true,
                                "visible": true,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                },
                                "minWidth": 150
                              },
                              "x-designable-id": "7rxto23wdyy",
                              "x-index": 0,
                              "x-display": "visible",
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {},
                              "properties": {
                                "importScene": {
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "Select",
                                  "x-validator": [],
                                  "x-component-props": {
                                    "size": "small",
                                    "multiple-limit": 0,
                                    "placeholder": "请选择",
                                    "style": {
                                      "opacity": 1
                                    },
                                    "fieldNames": {
                                      "label": "dictItemName",
                                      "value": "dictItemCode"
                                    },
                                    "@created": "{{() => {\n                    $queryEngine.request.baseRequest({\n                      type: 'base_dict_item_ide',\n                      action: 'query',\n                      payload: {\n                        filter: {\"$and\":{\"dictId\":{\"eq\":\"6763956529790976\"}}},\n                        page: {\"pageSize\":9999,\"pageNum\":1,\"sort\":\"dictItemNo asc\"},\n                      },\n                      query: {\"*\":{}},\n                      actionConfig: { service: 'api-base' },\n                      queryConfig: { staleTime: 300000 }\n                    }).then(res => {\n                      $self.dataSource = res.data || []\n                    })\n                  }}}"
                                  },
                                  "x-designer-extend": {
                                    "source": {
                                      "sourceType": "MQL",
                                      "enum": [],
                                      "action": "query",
                                      "type": "base_dict_item_ide",
                                      "query": {
                                        "*": {}
                                      },
                                      "filter": {
                                        "$and": {
                                          "dictId": {
                                            "eq": "6763956529790976"
                                          }
                                        }
                                      },
                                      "service": "api-base",
                                      "payload": {
                                        "page": {
                                          "sort": "dictItemNo asc"
                                        }
                                      },
                                      "conditions": [
                                        [
                                          {
                                            "dataName": "dictId",
                                            "comparison": "eq",
                                            "valueType": "fixed",
                                            "modelField": "6763956529790976",
                                            "modelId": ""
                                          }
                                        ]
                                      ]
                                    },
                                    "events": []
                                  },
                                  "enum": [],
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "name": "importScene",
                                  "x-pattern": "readPretty",
                                  "x-designable-id": "7wwpiawj7rr",
                                  "x-index": 0,
                                  "x-reactions": {
                                    "dependencies": [
                                      {
                                        "property": "value",
                                        "type": "any",
                                        "source": "qenxcou4juw.4my2aszl25a.lm49yg27mfn.19ni8nuyh47.quaReviewType",
                                        "name": "v_r2apqz7uta7"
                                      }
                                    ],
                                    "fulfill": {
                                      "state": {
                                        "value": "{{$deps.v_r2apqz7uta7}}"
                                      }
                                    }
                                  }
                                }
                              }
                            },
                            "m1txig5uhj0": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "title": "寻源",
                                "sortable": true,
                                "resizable": true,
                                "visible": true,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                },
                                "minWidth": 150
                              },
                              "x-designable-id": "m1txig5uhj0",
                              "x-index": 1,
                              "x-display": "visible",
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {},
                              "properties": {
                                "ifReqHead": {
                                  "type": "string",
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "Checkbox",
                                  "x-validator": [],
                                  "x-component-props": {
                                    "content": "",
                                    "trueLabel": "Y",
                                    "falseLabel": "N",
                                    "disabled": true,
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-designer-extend": {
                                    "events": []
                                  },
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "name": "ifReqHead",
                                  "x-designable-id": "7jtlnc4dth0",
                                  "x-index": 0,
                                  "x-pattern": "disabled",
                                  "x-query-engine-skip": false
                                }
                              }
                            },
                            "1uvqhwg5qku": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "title": "资质审查",
                                "sortable": true,
                                "resizable": true,
                                "visible": true,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                },
                                "minWidth": 150
                              },
                              "x-designable-id": "1uvqhwg5qku",
                              "x-index": 2,
                              "x-display": "visible",
                              "properties": {
                                "ifReview": {
                                  "type": "string",
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "Checkbox",
                                  "x-validator": [],
                                  "x-component-props": {
                                    "content": "",
                                    "trueLabel": "Y",
                                    "falseLabel": "N",
                                    "disabled": true,
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-designer-extend": {
                                    "events": []
                                  },
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "name": "ifReview",
                                  "x-designable-id": "sbpdaj6epic",
                                  "x-index": 0,
                                  "x-pattern": "disabled"
                                }
                              }
                            },
                            "e3fpd89nt9b": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "title": "现场评审",
                                "sortable": true,
                                "resizable": true,
                                "visible": true,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                },
                                "minWidth": 150
                              },
                              "x-designable-id": "e3fpd89nt9b",
                              "x-index": 3,
                              "x-display": "visible",
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {},
                              "properties": {
                                "ifSite": {
                                  "type": "string",
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "Checkbox",
                                  "x-validator": [],
                                  "x-component-props": {
                                    "content": "",
                                    "trueLabel": "Y",
                                    "falseLabel": "N",
                                    "disabled": true,
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-designer-extend": {
                                    "events": []
                                  },
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "name": "ifSite",
                                  "x-designable-id": "ukvywqp4ffp",
                                  "x-index": 0,
                                  "x-pattern": "disabled",
                                  "x-query-engine-skip": false
                                }
                              }
                            },
                            "b3h4n5w70hh": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "title": "样品确认",
                                "sortable": true,
                                "resizable": true,
                                "visible": true,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                },
                                "minWidth": 150
                              },
                              "x-designable-id": "b3h4n5w70hh",
                              "x-index": 4,
                              "x-display": "visible",
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {},
                              "properties": {
                                "ifSample": {
                                  "type": "string",
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "Checkbox",
                                  "x-validator": [],
                                  "x-component-props": {
                                    "content": "",
                                    "trueLabel": "Y",
                                    "falseLabel": "N",
                                    "disabled": true,
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-designer-extend": {
                                    "events": []
                                  },
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "name": "ifSample",
                                  "x-designable-id": "6bgr9kbcldm",
                                  "x-index": 0,
                                  "x-pattern": "disabled"
                                }
                              }
                            },
                            "vfc0dl0rtfd": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "title": "物料试用",
                                "sortable": true,
                                "resizable": true,
                                "visible": true,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                },
                                "minWidth": 150
                              },
                              "x-designable-id": "vfc0dl0rtfd",
                              "x-index": 5,
                              "x-display": "visible",
                              "properties": {
                                "ifMaterial": {
                                  "type": "string",
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "Checkbox",
                                  "x-validator": [],
                                  "x-component-props": {
                                    "content": "",
                                    "trueLabel": "Y",
                                    "falseLabel": "N",
                                    "disabled": true,
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-designer-extend": {
                                    "events": []
                                  },
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "name": "ifMaterial",
                                  "x-designable-id": "42ipudxn7sg",
                                  "x-index": 0,
                                  "x-pattern": "disabled"
                                }
                              }
                            },
                            "kxgy30e2yz8": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "title": "生效",
                                "sortable": true,
                                "resizable": true,
                                "visible": true,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                },
                                "minWidth": 150
                              },
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {},
                              "x-designable-id": "kxgy30e2yz8",
                              "x-index": 6,
                              "x-display": "visible",
                              "properties": {
                                "ifEffective": {
                                  "type": "string",
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "Checkbox",
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "x-designer-extend": {
                                    "events": []
                                  },
                                  "x-component-props": {
                                    "content": "",
                                    "trueLabel": "Y",
                                    "falseLabel": "N",
                                    "disabled": true,
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-validator": [],
                                  "name": "ifEffective",
                                  "x-pattern": "disabled",
                                  "x-designable-id": "61p7yo279vb",
                                  "x-index": 0
                                }
                              }
                            }
                          }
                        }
                      }
                    },
                    "47aimtc14ff": {
                      "type": "void",
                      "x-component": "FormCollapse.Item",
                      "x-component-props": {
                        "title": "关联单据",
                        "disabled": false,
                        "style": {
                          "opacity": 1
                        }
                      },
                      "name": "47aimtc14ff",
                      "x-designable-id": "6tgym38ssnt",
                      "x-index": 2,
                      "x-decorator-props": {
                        "style": {
                          "opacity": 1
                        }
                      },
                      "x-designer-extend": {},
                      "properties": {
                        "effectFormRelationForms": {
                          "type": "array",
                          "x-decorator": "FormItem",
                          "x-component": "RenderTable",
                          "x-validator": [],
                          "x-decorator-props": {
                            "style": "padding: 0 20px;",
                            "feedbackLayout": "loose"
                          },
                          "x-designable-id": "1lj97io1gwt",
                          "x-component-props": {
                            "stripe": false,
                            "border": true,
                            "show-header": true,
                            "size": "small",
                            "style": "width:100%",
                            "preColumns": [
                              "seq"
                            ],
                            "editMode": false,
                            "primaryKey": "id",
                            "cascadeDeletion": true,
                            "height": "300px",
                            "pagination": false,
                            "showHeader": true,
                            "openCustomTable": false,
                            "dblclickEditable": false
                          },
                          "x-designer-extend": {
                            "page": {
                              "pagination": false
                            },
                            "events": []
                          },
                          "x-index": 0,
                          "name": "effectFormRelationForms",
                          "properties": {
                            "formType": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "width": 150,
                                "title": "单据类型",
                                "sortable": true,
                                "resizable": true,
                                "id": "g9qa74okp31",
                                "visible": true,
                                "isGroup": false,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-index": 0,
                              "name": "formType",
                              "x-designable-id": "g9qa74okp31",
                              "x-display": "visible",
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {},
                              "properties": {
                                "formType": {
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "Select",
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "x-designer-extend": {
                                    "source": {
                                      "sourceType": "STATIC",
                                      "enum": [
                                        {
                                          "value": "RF",
                                          "label": "资质审查"
                                        },
                                        {
                                          "label": "样品确认",
                                          "value": "QS"
                                        }
                                      ],
                                      "action": "query",
                                      "type": "base_dict_item_ide",
                                      "query": {
                                        "*": {}
                                      },
                                      "filter": {
                                        "$and": {
                                          "dictId": {
                                            "eq": "362688107751552"
                                          }
                                        }
                                      },
                                      "service": "api-base",
                                      "payload": {
                                        "page": {
                                          "sort": "dictItemNo asc"
                                        }
                                      },
                                      "conditions": [
                                        [
                                          {
                                            "dataName": "dictId",
                                            "comparison": "eq",
                                            "valueType": "fixed",
                                            "modelField": "362688107751552",
                                            "modelId": ""
                                          }
                                        ]
                                      ]
                                    },
                                    "events": []
                                  },
                                  "x-component-props": {
                                    "size": "small",
                                    "multiple-limit": 0,
                                    "placeholder": "请选择",
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-validator": [],
                                  "enum": [
                                    {
                                      "value": "RF",
                                      "label": "资质审查"
                                    },
                                    {
                                      "label": "样品确认",
                                      "value": "QS"
                                    }
                                  ],
                                  "name": "formType",
                                  "x-designable-id": "7c3m7jrc697",
                                  "x-index": 0
                                }
                              }
                            },
                            "formCode": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "title": "单据号",
                                "sortable": true,
                                "resizable": true,
                                "visible": true,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                },
                                "id": "8ozdegx5n67",
                                "isGroup": false,
                                "minWidth": 150
                              },
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {},
                              "name": "formCode",
                              "x-designable-id": "8ozdegx5n67",
                              "x-index": 1,
                              "x-display": "visible",
                              "properties": {
                                "formCode": {
                                  "type": "string",
                                  "x-decorator": "FormItem",
                                  "x-component": "Link",
                                  "x-component-props": {
                                    "href": "链接URL",
                                    "type": "primary",
                                    "underline": true,
                                    "style": {
                                      "opacity": 1
                                    },
                                    "@click": "{{(...args) => {\n        return new $LogicEngineRuntime({\n          nodes: [{\"id\":\"78beda3f-9a5e-4442-9746-f057da51d335\",\"shape\":{\"name\":\"logic-start\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":505,\"y\":0},\"visible\":true,\"zIndex\":1},\"data\":{\"title\":\"开始\",\"value\":null}},{\"id\":\"1e1199d5-235c-4e72-a033-d600dcafe1c4\",\"shape\":{\"name\":\"logic-end\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":505,\"y\":728.6999999999999},\"visible\":true,\"zIndex\":2},\"data\":{\"title\":\"结束\",\"value\":null}},{\"id\":\"7de4ea4f-ac45-4e42-b0fc-99d0a6f9788b\",\"shape\":{\"name\":\"logic-function\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":489.5,\"y\":334},\"visible\":true,\"zIndex\":3},\"data\":{\"title\":\"自定义函数\",\"value\":{\"code\":\"function logicNode() {\\n  const row = $table.getRowByIndex($self.index)\\n  // const name = row.sampleNumber\\n\\n  const name = row.formCode\\n\\n\\n  if (row.formType == 'QS') { // 样品确认\\n    $tabs.addTab({\\n      name,\\n      label: name,\\n      closable: true,\\n      component: $SampleConfirmed,\\n      attrs: {\\n        params: {\\n          row,\\n          flag: 'view',\\n          sampleId: row.formId\\n        },\\n        closeTabPane: () => {\\n          $tabs.removeTab(name)\\n        },\\n      },\\n    })\\n  } else if (row.formType == 'RF') { // 资质审查\\n    $tabs.addTab({\\n      name,\\n      label: name,\\n      closable: true,\\n      component: $QuaOfReview,\\n      attrs: {\\n        params: {\\n          row: {\\n            reviewFormId: row.formId\\n          },\\n          flag: 'view'\\n        },\\n        closeTabPane: () => {\\n          $tabs.removeTab(name)\\n        },\\n      },\\n    })\\n  } else if (row.formType == 'SF') {\\n    $tabs.addTab({\\n      name,\\n      label: name,\\n      closable: true,\\n      component: $SiteAssessment,\\n      attrs: {\\n        params: {\\n          row: {\\n            siteFormId: row.formId\\n          },\\n          flag: 'view'\\n        },\\n        closeTabPane: () => {\\n          $tabs.removeTab(name)\\n        },\\n      },\\n    })\\n  } else if (row.formType == 'MT') {\\n    $tabs.addTab({\\n      name,\\n      label: name,\\n      closable: true,\\n      component: $MaterialTrial,\\n      attrs: {\\n        params: {\\n          materialTrialId: row.formId,\\n          flag: 'view'\\n        },\\n        closeTabPane: () => {\\n          $tabs.removeTab(name)\\n        },\\n      },\\n    })\\n  }\\n\\n  \\n}\"}},\"executeFunction\":\"function logicNode() {\\n  const row = $table.getRowByIndex($self.index)\\n  // const name = row.sampleNumber\\n\\n  const name = row.formCode\\n\\n\\n  if (row.formType == 'QS') { // 样品确认\\n    $tabs.addTab({\\n      name,\\n      label: name,\\n      closable: true,\\n      component: $SampleConfirmed,\\n      attrs: {\\n        params: {\\n          row,\\n          flag: 'view',\\n          sampleId: row.formId\\n        },\\n        closeTabPane: () => {\\n          $tabs.removeTab(name)\\n        },\\n      },\\n    })\\n  } else if (row.formType == 'RF') { // 资质审查\\n    $tabs.addTab({\\n      name,\\n      label: name,\\n      closable: true,\\n      component: $QuaOfReview,\\n      attrs: {\\n        params: {\\n          row: {\\n            reviewFormId: row.formId\\n          },\\n          flag: 'view'\\n        },\\n        closeTabPane: () => {\\n          $tabs.removeTab(name)\\n        },\\n      },\\n    })\\n  } else if (row.formType == 'SF') {\\n    $tabs.addTab({\\n      name,\\n      label: name,\\n      closable: true,\\n      component: $SiteAssessment,\\n      attrs: {\\n        params: {\\n          row: {\\n            siteFormId: row.formId\\n          },\\n          flag: 'view'\\n        },\\n        closeTabPane: () => {\\n          $tabs.removeTab(name)\\n        },\\n      },\\n    })\\n  } else if (row.formType == 'MT') {\\n    $tabs.addTab({\\n      name,\\n      label: name,\\n      closable: true,\\n      component: $MaterialTrial,\\n      attrs: {\\n        params: {\\n          materialTrialId: row.formId,\\n          flag: 'view'\\n        },\\n        closeTabPane: () => {\\n          $tabs.removeTab(name)\\n        },\\n      },\\n    })\\n  }\\n\\n  \\n}\"},{\"id\":\"9b53684e-ca1f-4d1d-98f2-827451622854\",\"shape\":{\"name\":\"edge\",\"zIndex\":4,\"source\":{\"cell\":\"78beda3f-9a5e-4442-9746-f057da51d335\",\"port\":\"bottom\"},\"target\":{\"cell\":\"7de4ea4f-ac45-4e42-b0fc-99d0a6f9788b\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"a7ff267e-c086-445a-acde-0a3f5b46ed54\",\"shape\":{\"name\":\"edge\",\"zIndex\":5,\"source\":{\"cell\":\"7de4ea4f-ac45-4e42-b0fc-99d0a6f9788b\",\"port\":\"bottom\"},\"target\":{\"cell\":\"1e1199d5-235c-4e72-a033-d600dcafe1c4\",\"port\":\"top\"}},\"data\":{\"value\":null}}],\n          payload: args,\n          scope: {\n            ...$root,\n            $designPages: $designPages,\n          }\n        }).run()\n      }}}"
                                  },
                                  "x-read-pretty": false,
                                  "x-designer-extend": {
                                    "events": [
                                      {
                                        "title": "打开tab详情",
                                        "name": "click",
                                        "type": "logic",
                                        "args": [
                                          {
                                            "id": "78beda3f-9a5e-4442-9746-f057da51d335",
                                            "shape": {
                                              "name": "logic-start",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 505,
                                                "y": 0
                                              },
                                              "visible": true,
                                              "zIndex": 1
                                            },
                                            "data": {
                                              "title": "开始",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "1e1199d5-235c-4e72-a033-d600dcafe1c4",
                                            "shape": {
                                              "name": "logic-end",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 505,
                                                "y": 728.6999999999999
                                              },
                                              "visible": true,
                                              "zIndex": 2
                                            },
                                            "data": {
                                              "title": "结束",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "7de4ea4f-ac45-4e42-b0fc-99d0a6f9788b",
                                            "shape": {
                                              "name": "logic-function",
                                              "size": {
                                                "width": 86,
                                                "height": 30
                                              },
                                              "position": {
                                                "x": 489.5,
                                                "y": 334
                                              },
                                              "visible": true,
                                              "zIndex": 3
                                            },
                                            "data": {
                                              "title": "自定义函数",
                                              "value": {
                                                "code": "function logicNode() {\n  const row = $table.getRowByIndex($self.index)\n  // const name = row.sampleNumber\n\n  const name = row.formCode\n\n\n  if (row.formType == 'QS') { // 样品确认\n    $tabs.addTab({\n      name,\n      label: name,\n      closable: true,\n      component: $SampleConfirmed,\n      attrs: {\n        params: {\n          row,\n          flag: 'view',\n          sampleId: row.formId\n        },\n        closeTabPane: () => {\n          $tabs.removeTab(name)\n        },\n      },\n    })\n  } else if (row.formType == 'RF') { // 资质审查\n    $tabs.addTab({\n      name,\n      label: name,\n      closable: true,\n      component: $QuaOfReview,\n      attrs: {\n        params: {\n          row: {\n            reviewFormId: row.formId\n          },\n          flag: 'view'\n        },\n        closeTabPane: () => {\n          $tabs.removeTab(name)\n        },\n      },\n    })\n  } else if (row.formType == 'SF') {\n    $tabs.addTab({\n      name,\n      label: name,\n      closable: true,\n      component: $SiteAssessment,\n      attrs: {\n        params: {\n          row: {\n            siteFormId: row.formId\n          },\n          flag: 'view'\n        },\n        closeTabPane: () => {\n          $tabs.removeTab(name)\n        },\n      },\n    })\n  } else if (row.formType == 'MT') {\n    $tabs.addTab({\n      name,\n      label: name,\n      closable: true,\n      component: $MaterialTrial,\n      attrs: {\n        params: {\n          materialTrialId: row.formId,\n          flag: 'view'\n        },\n        closeTabPane: () => {\n          $tabs.removeTab(name)\n        },\n      },\n    })\n  }\n\n  \n}"
                                              }
                                            },
                                            "executeFunction": "function logicNode() {\n  const row = $table.getRowByIndex($self.index)\n  // const name = row.sampleNumber\n\n  const name = row.formCode\n\n\n  if (row.formType == 'QS') { // 样品确认\n    $tabs.addTab({\n      name,\n      label: name,\n      closable: true,\n      component: $SampleConfirmed,\n      attrs: {\n        params: {\n          row,\n          flag: 'view',\n          sampleId: row.formId\n        },\n        closeTabPane: () => {\n          $tabs.removeTab(name)\n        },\n      },\n    })\n  } else if (row.formType == 'RF') { // 资质审查\n    $tabs.addTab({\n      name,\n      label: name,\n      closable: true,\n      component: $QuaOfReview,\n      attrs: {\n        params: {\n          row: {\n            reviewFormId: row.formId\n          },\n          flag: 'view'\n        },\n        closeTabPane: () => {\n          $tabs.removeTab(name)\n        },\n      },\n    })\n  } else if (row.formType == 'SF') {\n    $tabs.addTab({\n      name,\n      label: name,\n      closable: true,\n      component: $SiteAssessment,\n      attrs: {\n        params: {\n          row: {\n            siteFormId: row.formId\n          },\n          flag: 'view'\n        },\n        closeTabPane: () => {\n          $tabs.removeTab(name)\n        },\n      },\n    })\n  } else if (row.formType == 'MT') {\n    $tabs.addTab({\n      name,\n      label: name,\n      closable: true,\n      component: $MaterialTrial,\n      attrs: {\n        params: {\n          materialTrialId: row.formId,\n          flag: 'view'\n        },\n        closeTabPane: () => {\n          $tabs.removeTab(name)\n        },\n      },\n    })\n  }\n\n  \n}"
                                          },
                                          {
                                            "id": "9b53684e-ca1f-4d1d-98f2-827451622854",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 4,
                                              "source": {
                                                "cell": "78beda3f-9a5e-4442-9746-f057da51d335",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "7de4ea4f-ac45-4e42-b0fc-99d0a6f9788b",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "a7ff267e-c086-445a-acde-0a3f5b46ed54",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 5,
                                              "source": {
                                                "cell": "7de4ea4f-ac45-4e42-b0fc-99d0a6f9788b",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "1e1199d5-235c-4e72-a033-d600dcafe1c4",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          }
                                        ]
                                      }
                                    ]
                                  },
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "x-validator": [],
                                  "name": "formCode",
                                  "x-designable-id": "2512b24uxmm",
                                  "x-index": 0
                                }
                              }
                            },
                            "submitDate": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "width": 150,
                                "title": "提交时间",
                                "sortable": true,
                                "resizable": true,
                                "id": "v0kbn3td234",
                                "visible": true,
                                "isGroup": false,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-index": 2,
                              "name": "submitDate",
                              "x-designable-id": "v0kbn3td234",
                              "x-display": "visible",
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {},
                              "properties": {
                                "submitDate": {
                                  "type": "string",
                                  "x-component": "DatePicker",
                                  "x-decorator": "FormItem",
                                  "x-component-props": {
                                    "editable": true,
                                    "clearable": true,
                                    "placeholder": "请选择",
                                    "size": "small",
                                    "type": "date",
                                    "style": {
                                      "opacity": 1
                                    },
                                    "start-placeholder": "",
                                    "end-placeholder": ""
                                  },
                                  "x-designer-extend": {
                                    "model": {
                                      "businessType": "sccSupEffectFormRelationForm",
                                      "reference": null,
                                      "modelId": "1668183595088662530",
                                      "field": "submitDate",
                                      "originalField": "submitDate"
                                    },
                                    "events": []
                                  },
                                  "x-index": 0,
                                  "name": "submitDate",
                                  "x-designable-id": "z5vyhttv4no",
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "x-validator": []
                                }
                              }
                            },
                            "approvedDate": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "width": 150,
                                "title": "准入日期",
                                "sortable": true,
                                "resizable": true,
                                "id": "q4p009185ru",
                                "visible": true,
                                "isGroup": false,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-index": 3,
                              "name": "approvedDate",
                              "x-designable-id": "q4p009185ru",
                              "x-display": "visible",
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {},
                              "properties": {
                                "approvedDate": {
                                  "type": "string",
                                  "x-component": "DatePicker",
                                  "x-decorator": "FormItem",
                                  "x-component-props": {
                                    "editable": true,
                                    "clearable": true,
                                    "placeholder": "请选择",
                                    "size": "small",
                                    "type": "date",
                                    "style": {
                                      "opacity": 1
                                    },
                                    "start-placeholder": "",
                                    "end-placeholder": ""
                                  },
                                  "x-designer-extend": {
                                    "model": {
                                      "businessType": "sccSupEffectFormRelationForm",
                                      "reference": null,
                                      "modelId": "1668183595088662530",
                                      "field": "approvedDate",
                                      "originalField": "approvedDate"
                                    },
                                    "events": []
                                  },
                                  "x-index": 0,
                                  "name": "approvedDate",
                                  "x-designable-id": "ygwkqxgzc7w",
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "x-validator": []
                                }
                              }
                            },
                            "summary": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "width": 150,
                                "title": "结论",
                                "sortable": true,
                                "resizable": true,
                                "id": "66db3fce4d1",
                                "visible": true,
                                "isGroup": false,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-index": 4,
                              "name": "summary",
                              "x-designable-id": "66db3fce4d1",
                              "x-display": "visible",
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {},
                              "properties": {
                                "summary": {
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "Select",
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "x-designer-extend": {
                                    "source": {
                                      "sourceType": "MQL",
                                      "enum": [],
                                      "action": "query",
                                      "type": "base_dict_item_ide",
                                      "query": {
                                        "*": {}
                                      },
                                      "filter": {
                                        "$and": {
                                          "dictId": {
                                            "eq": "7676603397832704"
                                          }
                                        }
                                      },
                                      "service": "api-base",
                                      "payload": {
                                        "page": {
                                          "sort": "dictItemNo asc"
                                        }
                                      },
                                      "conditions": [
                                        [
                                          {
                                            "dataName": "dictId",
                                            "comparison": "eq",
                                            "valueType": "fixed",
                                            "modelField": "7676603397832704",
                                            "modelId": ""
                                          }
                                        ]
                                      ]
                                    },
                                    "events": []
                                  },
                                  "x-component-props": {
                                    "size": "small",
                                    "multiple-limit": 0,
                                    "placeholder": "请选择",
                                    "style": {
                                      "opacity": 1
                                    },
                                    "fieldNames": {
                                      "label": "dictItemName",
                                      "value": "dictItemCode"
                                    },
                                    "@created": "{{() => {\n                    $queryEngine.request.baseRequest({\n                      type: 'base_dict_item_ide',\n                      action: 'query',\n                      payload: {\n                        filter: {\"$and\":{\"dictId\":{\"eq\":\"7676603397832704\"}}},\n                        page: {\"pageSize\":9999,\"pageNum\":1,\"sort\":\"dictItemNo asc\"},\n                      },\n                      query: {\"*\":{}},\n                      actionConfig: { service: 'api-base' },\n                      queryConfig: { staleTime: 300000 }\n                    }).then(res => {\n                      $self.dataSource = res.data || []\n                    })\n                  }}}"
                                  },
                                  "x-validator": [],
                                  "enum": [],
                                  "x-pattern": "readPretty",
                                  "x-designable-id": "x4c94my6jhk",
                                  "x-index": 0,
                                  "name": "summary"
                                }
                              }
                            },
                            "ozkhkn1s1t1": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "title": "状态",
                                "id": "ozkhkn1s1t1",
                                "align": "left",
                                "visible": true,
                                "resizable": true,
                                "sortable": true,
                                "isGroup": false,
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                },
                                "minWidth": 150
                              },
                              "x-index": 5,
                              "x-designable-id": "ozkhkn1s1t1",
                              "x-display": "visible",
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {},
                              "properties": {
                                "approveStatus": {
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "Select",
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "x-designer-extend": {
                                    "source": {
                                      "sourceType": "MQL",
                                      "enum": [],
                                      "action": "query",
                                      "type": "base_dict_item_ide",
                                      "query": {
                                        "*": {}
                                      },
                                      "filter": {
                                        "$and": {
                                          "dictId": {
                                            "eq": " 24"
                                          }
                                        }
                                      },
                                      "service": "api-base",
                                      "payload": {
                                        "page": {
                                          "sort": "dictItemNo asc"
                                        }
                                      },
                                      "conditions": [
                                        [
                                          {
                                            "dataName": "dictId",
                                            "comparison": "eq",
                                            "valueType": "fixed",
                                            "modelField": " 24",
                                            "modelId": ""
                                          }
                                        ]
                                      ]
                                    },
                                    "events": []
                                  },
                                  "x-component-props": {
                                    "size": "small",
                                    "multiple-limit": 0,
                                    "placeholder": "请选择",
                                    "style": {
                                      "opacity": 1
                                    },
                                    "fieldNames": {
                                      "label": "dictItemName",
                                      "value": "dictItemCode"
                                    },
                                    "@created": "{{() => {\n                    $queryEngine.request.baseRequest({\n                      type: 'base_dict_item_ide',\n                      action: 'query',\n                      payload: {\n                        filter: {\"$and\":{\"dictId\":{\"eq\":\" 24\"}}},\n                        page: {\"pageSize\":9999,\"pageNum\":1,\"sort\":\"dictItemNo asc\"},\n                      },\n                      query: {\"*\":{}},\n                      actionConfig: { service: 'api-base' },\n                      queryConfig: { staleTime: 300000 }\n                    }).then(res => {\n                      $self.dataSource = res.data || []\n                    })\n                  }}}"
                                  },
                                  "x-validator": [],
                                  "enum": [],
                                  "x-pattern": "readPretty",
                                  "x-designable-id": "zl79lud5umj",
                                  "x-index": 0,
                                  "name": "approveStatus"
                                }
                              }
                            },
                            "submitBy": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "width": 150,
                                "title": "提交人",
                                "sortable": true,
                                "resizable": true,
                                "id": "n61ntxrnywa",
                                "visible": true,
                                "isGroup": false,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-index": 6,
                              "name": "submitBy",
                              "x-designable-id": "n61ntxrnywa",
                              "x-display": "visible",
                              "properties": {
                                "submitBy": {
                                  "type": "string",
                                  "x-component": "Input",
                                  "x-decorator": "FormItem",
                                  "x-component-props": {
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-designer-extend": {
                                    "model": {
                                      "businessType": "sccSupEffectFormRelationForm",
                                      "reference": null,
                                      "modelId": "1668183595088662530",
                                      "field": "submitBy",
                                      "originalField": "submitBy"
                                    },
                                    "events": []
                                  },
                                  "x-index": 0,
                                  "name": "submitBy",
                                  "x-designable-id": "bifx7vkw6qq",
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "x-validator": []
                                }
                              }
                            }
                          }
                        }
                      }
                    },
                    "ihi0zhfotl7": {
                      "type": "void",
                      "x-component": "FormCollapse.Item",
                      "x-component-props": {
                        "title": "寻源信息",
                        "disabled": false,
                        "style": {
                          "opacity": 1
                        }
                      },
                      "name": "ihi0zhfotl7",
                      "x-designable-id": "x33rn717t4j",
                      "x-index": 3,
                      "x-decorator-props": {
                        "style": {
                          "opacity": 1
                        }
                      },
                      "x-designer-extend": {},
                      "properties": {
                        "effectFormReqHeads": {
                          "type": "array",
                          "x-decorator": "FormItem",
                          "x-component": "RenderTable",
                          "x-validator": [],
                          "x-decorator-props": {
                            "style": "padding: 0 20px;",
                            "feedbackLayout": "loose"
                          },
                          "x-designable-id": "gvmk0y2hafb",
                          "x-component-props": {
                            "stripe": false,
                            "border": true,
                            "show-header": true,
                            "size": "small",
                            "style": "width:100%",
                            "preColumns": [
                              "seq"
                            ],
                            "editMode": false,
                            "primaryKey": "id",
                            "cascadeDeletion": true,
                            "height": "300px",
                            "pagination": false,
                            "showHeader": true,
                            "openCustomTable": false,
                            "dblclickEditable": false
                          },
                          "x-designer-extend": {
                            "page": {
                              "pagination": false
                            },
                            "events": []
                          },
                          "x-index": 0,
                          "name": "effectFormReqHeads",
                          "properties": {
                            "reqHeadNo": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "title": "寻源单号",
                                "sortable": true,
                                "resizable": true,
                                "visible": true,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                },
                                "minWidth": 150
                              },
                              "x-index": 0,
                              "name": "reqHeadNo",
                              "x-designable-id": "tmp5nxre0f0",
                              "x-display": "visible",
                              "properties": {
                                "reqHeadNo": {
                                  "type": "string",
                                  "x-component": "Input",
                                  "x-decorator": "FormItem",
                                  "x-component-props": {
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-designer-extend": {
                                    "model": {
                                      "businessType": "sccSupEffectFormReqHead",
                                      "reference": null,
                                      "modelId": "1668183594820227074",
                                      "field": "reqHeadNo",
                                      "originalField": "reqHeadNo"
                                    },
                                    "events": []
                                  },
                                  "x-index": 0,
                                  "name": "reqHeadNo",
                                  "x-designable-id": "4yfxl84995h",
                                  "x-validator": [],
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "x-pattern": "readPretty"
                                }
                              }
                            },
                            "souReqTitle": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "title": "寻源单标题",
                                "sortable": true,
                                "resizable": true,
                                "visible": true,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                },
                                "minWidth": 150
                              },
                              "x-index": 1,
                              "name": "souReqTitle",
                              "x-designable-id": "srtj5q8krl3",
                              "x-display": "visible",
                              "properties": {
                                "souReqTitle": {
                                  "type": "string",
                                  "x-component": "Input",
                                  "x-decorator": "FormItem",
                                  "x-component-props": {
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-designer-extend": {
                                    "model": {
                                      "businessType": "sccSupEffectFormReqHead",
                                      "reference": null,
                                      "modelId": "1668183594820227074",
                                      "field": "souReqTitle",
                                      "originalField": "souReqTitle"
                                    },
                                    "events": []
                                  },
                                  "x-index": 0,
                                  "name": "souReqTitle",
                                  "x-designable-id": "l0iare5w3am",
                                  "x-validator": [],
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "x-pattern": "readPretty"
                                }
                              }
                            },
                            "categoryName": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "title": "品类名称",
                                "sortable": true,
                                "resizable": true,
                                "visible": true,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                },
                                "minWidth": 150
                              },
                              "x-index": 2,
                              "name": "categoryName",
                              "x-designable-id": "q529rxp4qpt",
                              "x-display": "visible",
                              "properties": {
                                "categoryName": {
                                  "type": "string",
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "Input",
                                  "x-validator": [],
                                  "x-component-props": {
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-designer-extend": {
                                    "events": []
                                  },
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "name": "categoryName",
                                  "x-designable-id": "cdoj3hlrxgj",
                                  "x-index": 0,
                                  "x-pattern": "readPretty"
                                }
                              }
                            }
                          }
                        }
                      }
                    },
                    "li47dkh4nz8": {
                      "type": "void",
                      "x-component": "FormCollapse.Item",
                      "x-component-props": {
                        "title": "财务信息",
                        "disabled": false,
                        "style": {
                          "opacity": 1
                        }
                      },
                      "name": "li47dkh4nz8",
                      "x-designable-id": "hie3caf0wgk",
                      "x-index": 4,
                      "x-decorator-props": {
                        "style": {
                          "opacity": 1
                        }
                      },
                      "x-designer-extend": {},
                      "properties": {
                        "tools": {
                          "type": "void",
                          "x-component": "RenderButtonList",
                          "x-component-props": {
                            "style": {
                              "margin": "0px 20px 16px 20px",
                              "opacity": 1
                            },
                            "max": 3,
                            "size": 12
                          },
                          "x-index": 0,
                          "name": "tools",
                          "x-designable-id": "lc05rj1s4md",
                          "x-validator": [],
                          "properties": {
                            "add": {
                              "type": "void",
                              "x-component": "Button",
                              "title": "添加",
                              "x-visible": "{{!$$safeGetScope('$readOnly')}}",
                              "x-component-props": {
                                "type": "primary",
                                "size": "small",
                                "plain": false,
                                "round": false,
                                "circle": false,
                                "style": {
                                  "opacity": 1
                                },
                                "@click": "{{(...args) => {\n        return new $LogicEngineRuntime({\n          nodes: [{\"id\":\"5424ed73-caba-47e9-86ea-39762a61483f\",\"shape\":{\"name\":\"logic-start\",\"size\":{\"width\":50,\"height\":50},\"position\":{\"x\":323.5,\"y\":0},\"visible\":true,\"zIndex\":1},\"data\":{\"title\":\"开始\",\"value\":null}},{\"id\":\"1e2ec30f-25b7-4e16-8aa4-2d2b114dff75\",\"shape\":{\"name\":\"logic-end\",\"size\":{\"width\":50,\"height\":50},\"position\":{\"x\":323.5,\"y\":518},\"visible\":true,\"zIndex\":2},\"data\":{\"title\":\"结束\",\"value\":null}},{\"id\":\"b281e537-ccc7-4c94-83dd-d4f91819bff4\",\"shape\":{\"name\":\"logic-table-row-insert\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":306,\"y\":284},\"visible\":true,\"zIndex\":3},\"data\":{\"title\":\"新增行\",\"value\":{\"method\":\"unshift\",\"tableId\":\"mz1neiig37i\"}},\"executeFunction\":\"function logicNode(ctx) {\\n  \\n      $designable.query(\\\"mz1neiig37i\\\").take()?.invoke(\\\"addRow\\\", \\\"unshift\\\")\\n    \\n}\"},{\"id\":\"4db8c197-3704-408c-85ab-1b52772130b4\",\"shape\":{\"name\":\"edge\",\"zIndex\":4,\"source\":{\"cell\":\"5424ed73-caba-47e9-86ea-39762a61483f\",\"port\":\"bottom\"},\"target\":{\"cell\":\"b281e537-ccc7-4c94-83dd-d4f91819bff4\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"ed683930-2de7-459a-b566-783362190238\",\"shape\":{\"name\":\"edge\",\"zIndex\":5,\"source\":{\"cell\":\"b281e537-ccc7-4c94-83dd-d4f91819bff4\",\"port\":\"bottom\"},\"target\":{\"cell\":\"1e2ec30f-25b7-4e16-8aa4-2d2b114dff75\",\"port\":\"top\"}},\"data\":{\"value\":null}}],\n          payload: args,\n          scope: {\n            ...$root,\n            $designPages: $designPages,\n          }\n        }).run()\n      }}}"
                              },
                              "x-designer-extend": {
                                "events": [
                                  {
                                    "title": "新增",
                                    "name": "click",
                                    "type": "logic",
                                    "args": [
                                      {
                                        "id": "5424ed73-caba-47e9-86ea-39762a61483f",
                                        "shape": {
                                          "name": "logic-start",
                                          "size": {
                                            "width": 50,
                                            "height": 50
                                          },
                                          "position": {
                                            "x": 323.5,
                                            "y": 0
                                          },
                                          "visible": true,
                                          "zIndex": 1
                                        },
                                        "data": {
                                          "title": "开始",
                                          "value": null
                                        }
                                      },
                                      {
                                        "id": "1e2ec30f-25b7-4e16-8aa4-2d2b114dff75",
                                        "shape": {
                                          "name": "logic-end",
                                          "size": {
                                            "width": 50,
                                            "height": 50
                                          },
                                          "position": {
                                            "x": 323.5,
                                            "y": 518
                                          },
                                          "visible": true,
                                          "zIndex": 2
                                        },
                                        "data": {
                                          "title": "结束",
                                          "value": null
                                        }
                                      },
                                      {
                                        "id": "b281e537-ccc7-4c94-83dd-d4f91819bff4",
                                        "shape": {
                                          "name": "logic-table-row-insert",
                                          "size": {
                                            "width": 86,
                                            "height": 30
                                          },
                                          "position": {
                                            "x": 306,
                                            "y": 284
                                          },
                                          "visible": true,
                                          "zIndex": 3
                                        },
                                        "data": {
                                          "title": "新增行",
                                          "value": {
                                            "method": "unshift",
                                            "tableId": "mz1neiig37i"
                                          }
                                        },
                                        "executeFunction": "function logicNode(ctx) {\n  \n      $designable.query(\"mz1neiig37i\").take()?.invoke(\"addRow\", \"unshift\")\n    \n}"
                                      },
                                      {
                                        "id": "4db8c197-3704-408c-85ab-1b52772130b4",
                                        "shape": {
                                          "name": "edge",
                                          "zIndex": 4,
                                          "source": {
                                            "cell": "5424ed73-caba-47e9-86ea-39762a61483f",
                                            "port": "bottom"
                                          },
                                          "target": {
                                            "cell": "b281e537-ccc7-4c94-83dd-d4f91819bff4",
                                            "port": "top"
                                          }
                                        },
                                        "data": {
                                          "value": null
                                        }
                                      },
                                      {
                                        "id": "ed683930-2de7-459a-b566-783362190238",
                                        "shape": {
                                          "name": "edge",
                                          "zIndex": 5,
                                          "source": {
                                            "cell": "b281e537-ccc7-4c94-83dd-d4f91819bff4",
                                            "port": "bottom"
                                          },
                                          "target": {
                                            "cell": "1e2ec30f-25b7-4e16-8aa4-2d2b114dff75",
                                            "port": "top"
                                          }
                                        },
                                        "data": {
                                          "value": null
                                        }
                                      }
                                    ]
                                  }
                                ]
                              },
                              "x-index": 0,
                              "name": "add",
                              "x-designable-id": "1zglodioob6",
                              "x-validator": [],
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              }
                            }
                          }
                        },
                        "effectFormFinanceInfos": {
                          "type": "array",
                          "x-decorator": "FormItem",
                          "x-component": "RenderTable",
                          "x-validator": [],
                          "x-decorator-props": {
                            "style": "padding: 0 20px;",
                            "feedbackLayout": "loose"
                          },
                          "x-designable-id": "mz1neiig37i",
                          "x-component-props": {
                            "stripe": false,
                            "border": true,
                            "show-header": true,
                            "size": "small",
                            "style": "width:100%",
                            "preColumns": [
                              "seq"
                            ],
                            "editMode": true,
                            "primaryKey": "id",
                            "cascadeDeletion": true,
                            "height": "300px",
                            "pagination": false,
                            "showHeader": true,
                            "openCustomTable": false,
                            "dblclickEditable": true
                          },
                          "x-designer-extend": {
                            "page": {
                              "pagination": false
                            },
                            "events": []
                          },
                          "x-index": 1,
                          "name": "effectFormFinanceInfos",
                          "x-reactions": {
                            "dependencies": [],
                            "fulfill": {
                              "state": {
                                "componentProps": "{{{\r\n  ...$self.componentProps,\r\n  primaryKey: 'effectFormFinanceInfoId'\r\n}}}"
                              }
                            }
                          },
                          "properties": {
                            "orgName": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "title": "业务实体",
                                "sortable": true,
                                "resizable": true,
                                "id": "lxppj319b8m",
                                "skipEditable": false,
                                "align": "left",
                                "visible": true,
                                "isGroup": false,
                                "headerAlign": "left",
                                "style": {
                                  "opacity": 1
                                },
                                "minWidth": 150
                              },
                              "x-index": 0,
                              "name": "orgName",
                              "x-designable-id": "lxppj319b8m",
                              "x-display": "visible",
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {},
                              "properties": {
                                "orgId": {
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "Select",
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "x-designer-extend": {
                                    "source": {
                                      "sourceType": "STATIC",
                                      "enum": []
                                    },
                                    "events": []
                                  },
                                  "x-component-props": {
                                    "size": "small",
                                    "multiple-limit": 0,
                                    "placeholder": "请选择",
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-validator": [],
                                  "enum": [],
                                  "x-reactions": {
                                    "dependencies": [],
                                    "fulfill": {
                                      "state": {},
                                      "run": "const effectFormOrgCategorys = $form.query('effectFormOrgCategorys').get('value')\r\nconsole.log(effectFormOrgCategorys, 'effectFormOrgCategorys')\r\n// 提取组织去重，并赋值到组织下拉框\r\nlet org = [] // 组织的数组\r\nif (effectFormOrgCategorys) {\r\n  effectFormOrgCategorys.forEach(e => {\r\n    let bol = org.find(el => el.orgId === e.orgId)\r\n    if (!bol) {\r\n      org.push({\r\n        label: e.orgName,\r\n        value: e.orgId,\r\n        orgId: e.orgId,\r\n        orgName: e.orgName,\r\n        orgCode: e.orgCode\r\n      })\r\n    }\r\n  })\r\n  $self.dataSource = org\r\n}"
                                    }
                                  },
                                  "name": "orgId",
                                  "x-designable-id": "1pedsy89bsy",
                                  "x-index": 0
                                }
                              }
                            },
                            "factoryCode": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "title": "工厂编码",
                                "sortable": true,
                                "resizable": true,
                                "id": "tkfnu8xtxly",
                                "visible": true,
                                "isGroup": false,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                },
                                "minWidth": 150
                              },
                              "x-index": 1,
                              "name": "factoryCode",
                              "x-designable-id": "tkfnu8xtxly",
                              "x-display": "visible",
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {},
                              "properties": {
                                "factoryCode": {
                                  "type": "string",
                                  "x-component": "Input",
                                  "x-decorator": "FormItem",
                                  "x-component-props": {
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-designer-extend": {
                                    "model": {
                                      "businessType": "sccSupEffectFormFinanceInfo",
                                      "reference": null,
                                      "modelId": "1668183594811838466",
                                      "field": "factoryCode",
                                      "originalField": "factoryCode"
                                    },
                                    "events": []
                                  },
                                  "x-index": 0,
                                  "name": "factoryCode",
                                  "x-designable-id": "yka9p5ghbbc",
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "x-validator": [],
                                  "x-pattern": ""
                                }
                              }
                            },
                            "clearCurrency": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "title": "结算币种",
                                "sortable": true,
                                "resizable": true,
                                "id": "jy21rehr9gl",
                                "visible": true,
                                "isGroup": false,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                },
                                "minWidth": 150
                              },
                              "x-index": 2,
                              "name": "clearCurrency",
                              "x-designable-id": "jy21rehr9gl",
                              "x-display": "visible",
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {},
                              "properties": {
                                "clearCurrency": {
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "Select",
                                  "x-validator": [],
                                  "x-component-props": {
                                    "size": "small",
                                    "multiple-limit": 0,
                                    "placeholder": "请选择",
                                    "style": {
                                      "opacity": 1
                                    },
                                    "fieldNames": {
                                      "label": "currencyName",
                                      "value": "currencyCode"
                                    },
                                    "@change": "{{(...args) => {\n        return new $LogicEngineRuntime({\n          nodes: [{\"id\":\"1ee87cdb-5ca3-4a2f-ab09-4362386f3022\",\"shape\":{\"name\":\"logic-start\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":604,\"y\":0},\"visible\":true,\"zIndex\":1},\"data\":{\"title\":\"开始\",\"value\":null}},{\"id\":\"81dee554-f7cb-496a-8356-a05413e2d369\",\"shape\":{\"name\":\"logic-end\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":604,\"y\":505.4},\"visible\":true,\"zIndex\":2},\"data\":{\"title\":\"结束\",\"value\":null}},{\"id\":\"de1a32c2-7949-434a-aa91-36aa217f8b33\",\"shape\":{\"name\":\"logic-function\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":588.5,\"y\":198},\"visible\":true,\"zIndex\":3},\"data\":{\"title\":\"自定义函数\",\"value\":{\"code\":\"function logicNode(ctx) {\\n  console.log(\\\"ctx\\\")\\n  const item = $self.dataSource.find((i) => i.organizationId === ctx.payload[0])\\n  const row = $table.getRowByIndex($self.index)\\n  row.currencyCode = item ? item.currencyCode : \\\"\\\"\\n  row.currencyName = item ? item.currencyName : \\\"\\\"\\n  row.currencyId = item ? item.currencyId : \\\"\\\"\\n}\\n\"}},\"executeFunction\":\"function logicNode(ctx) {\\n  console.log(\\\"ctx\\\")\\n  const item = $self.dataSource.find((i) => i.organizationId === ctx.payload[0])\\n  const row = $table.getRowByIndex($self.index)\\n  row.currencyCode = item ? item.currencyCode : \\\"\\\"\\n  row.currencyName = item ? item.currencyName : \\\"\\\"\\n  row.currencyId = item ? item.currencyId : \\\"\\\"\\n}\\n\"},{\"id\":\"e97305f9-6238-40d8-92c2-8c17e0787e15\",\"shape\":{\"name\":\"edge\",\"zIndex\":4,\"source\":{\"cell\":\"1ee87cdb-5ca3-4a2f-ab09-4362386f3022\",\"port\":\"bottom\"},\"target\":{\"cell\":\"de1a32c2-7949-434a-aa91-36aa217f8b33\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"3b59863e-0315-4fcf-9b39-e8032ec108c3\",\"shape\":{\"name\":\"edge\",\"zIndex\":5,\"source\":{\"cell\":\"de1a32c2-7949-434a-aa91-36aa217f8b33\",\"port\":\"bottom\"},\"target\":{\"cell\":\"81dee554-f7cb-496a-8356-a05413e2d369\",\"port\":\"top\"}},\"data\":{\"value\":null}}],\n          payload: args,\n          scope: {\n            ...$root,\n            $designPages: $designPages,\n          }\n        }).run()\n      }}}",
                                    "@created": "{{() => {\n                    $queryEngine.request.baseRequest({\n                      type: 'base_PurchaseCurrency_ide',\n                      action: 'query',\n                      payload: {\n                        filter: {},\n                        page: {\"pageSize\":9999,\"pageNum\":1,\"sort\":\"currencyId asc\"},\n                      },\n                      query: {\"*\":{}},\n                      actionConfig: { service: 'api-base' },\n                      queryConfig: { staleTime: 300000 }\n                    }).then(res => {\n                      $self.dataSource = res.data || []\n                    })\n                  }}}"
                                  },
                                  "x-designer-extend": {
                                    "source": {
                                      "sourceType": "MQL",
                                      "action": "query",
                                      "type": "base_PurchaseCurrency_ide",
                                      "query": {
                                        "*": {}
                                      },
                                      "filter": {},
                                      "service": "api-base",
                                      "payload": {
                                        "page": {
                                          "sort": "currencyId asc"
                                        }
                                      },
                                      "enum": []
                                    },
                                    "events": [
                                      {
                                        "title": "点击事件",
                                        "name": "change",
                                        "type": "logic",
                                        "args": [
                                          {
                                            "id": "1ee87cdb-5ca3-4a2f-ab09-4362386f3022",
                                            "shape": {
                                              "name": "logic-start",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 604,
                                                "y": 0
                                              },
                                              "visible": true,
                                              "zIndex": 1
                                            },
                                            "data": {
                                              "title": "开始",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "81dee554-f7cb-496a-8356-a05413e2d369",
                                            "shape": {
                                              "name": "logic-end",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 604,
                                                "y": 505.4
                                              },
                                              "visible": true,
                                              "zIndex": 2
                                            },
                                            "data": {
                                              "title": "结束",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "de1a32c2-7949-434a-aa91-36aa217f8b33",
                                            "shape": {
                                              "name": "logic-function",
                                              "size": {
                                                "width": 86,
                                                "height": 30
                                              },
                                              "position": {
                                                "x": 588.5,
                                                "y": 198
                                              },
                                              "visible": true,
                                              "zIndex": 3
                                            },
                                            "data": {
                                              "title": "自定义函数",
                                              "value": {
                                                "code": "function logicNode(ctx) {\n  console.log(\"ctx\")\n  const item = $self.dataSource.find((i) => i.organizationId === ctx.payload[0])\n  const row = $table.getRowByIndex($self.index)\n  row.currencyCode = item ? item.currencyCode : \"\"\n  row.currencyName = item ? item.currencyName : \"\"\n  row.currencyId = item ? item.currencyId : \"\"\n}\n"
                                              }
                                            },
                                            "executeFunction": "function logicNode(ctx) {\n  console.log(\"ctx\")\n  const item = $self.dataSource.find((i) => i.organizationId === ctx.payload[0])\n  const row = $table.getRowByIndex($self.index)\n  row.currencyCode = item ? item.currencyCode : \"\"\n  row.currencyName = item ? item.currencyName : \"\"\n  row.currencyId = item ? item.currencyId : \"\"\n}\n"
                                          },
                                          {
                                            "id": "e97305f9-6238-40d8-92c2-8c17e0787e15",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 4,
                                              "source": {
                                                "cell": "1ee87cdb-5ca3-4a2f-ab09-4362386f3022",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "de1a32c2-7949-434a-aa91-36aa217f8b33",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "3b59863e-0315-4fcf-9b39-e8032ec108c3",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 5,
                                              "source": {
                                                "cell": "de1a32c2-7949-434a-aa91-36aa217f8b33",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "81dee554-f7cb-496a-8356-a05413e2d369",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          }
                                        ]
                                      }
                                    ]
                                  },
                                  "enum": [],
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "name": "clearCurrency",
                                  "x-designable-id": "dwl4pjlp8ke",
                                  "x-index": 0
                                }
                              }
                            },
                            "paymentMethod": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "title": "付款方式",
                                "sortable": true,
                                "resizable": true,
                                "id": "xfyg1rw5hl7",
                                "visible": true,
                                "isGroup": false,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                },
                                "minWidth": 150
                              },
                              "x-index": 3,
                              "name": "paymentMethod",
                              "x-designable-id": "xfyg1rw5hl7",
                              "x-display": "visible",
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {},
                              "properties": {
                                "paymentMethod": {
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "Select",
                                  "x-validator": [],
                                  "x-component-props": {
                                    "size": "small",
                                    "multiple-limit": 0,
                                    "placeholder": "请选择",
                                    "style": {
                                      "opacity": 1
                                    },
                                    "fieldNames": {
                                      "label": "dictItemName",
                                      "value": "dictItemCode"
                                    },
                                    "@created": "{{() => {\n                    $queryEngine.request.baseRequest({\n                      type: 'base_dict_item_ide',\n                      action: 'query',\n                      payload: {\n                        filter: {\"$and\":{\"dictId\":{\"eq\":\"6844037009113088\"}}},\n                        page: {\"pageSize\":9999,\"pageNum\":1,\"sort\":\"dictItemNo asc\"},\n                      },\n                      query: {\"*\":{}},\n                      actionConfig: { service: 'api-base' },\n                      queryConfig: { staleTime: 300000 }\n                    }).then(res => {\n                      $self.dataSource = res.data || []\n                    })\n                  }}}"
                                  },
                                  "x-designer-extend": {
                                    "source": {
                                      "sourceType": "MQL",
                                      "action": "query",
                                      "type": "base_dict_item_ide",
                                      "query": {
                                        "*": {}
                                      },
                                      "filter": {
                                        "$and": {
                                          "dictId": {
                                            "eq": "6844037009113088"
                                          }
                                        }
                                      },
                                      "service": "api-base",
                                      "payload": {
                                        "page": {
                                          "sort": "dictItemNo asc"
                                        }
                                      },
                                      "enum": [],
                                      "conditions": [
                                        [
                                          {
                                            "dataName": "dictId",
                                            "comparison": "eq",
                                            "valueType": "fixed",
                                            "modelField": "6844037009113088",
                                            "modelId": ""
                                          }
                                        ]
                                      ]
                                    },
                                    "events": []
                                  },
                                  "enum": [],
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "name": "paymentMethod",
                                  "x-designable-id": "4ah753u1dmn",
                                  "x-index": 0
                                }
                              }
                            },
                            "paymentTerms": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "title": "付款账期",
                                "sortable": true,
                                "resizable": true,
                                "id": "hs6d45t2kz5",
                                "skipEditable": false,
                                "align": "left",
                                "visible": true,
                                "isGroup": false,
                                "headerAlign": "left",
                                "style": {
                                  "opacity": 1
                                },
                                "minWidth": 150
                              },
                              "x-index": 4,
                              "name": "paymentTerms",
                              "x-designable-id": "hs6d45t2kz5",
                              "x-display": "visible",
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {},
                              "properties": {
                                "paymentTerms": {
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "Select",
                                  "x-validator": [],
                                  "x-component-props": {
                                    "size": "small",
                                    "multiple-limit": 0,
                                    "placeholder": "请选择",
                                    "style": {
                                      "opacity": 1
                                    },
                                    "fieldNames": {
                                      "label": "dictItemName",
                                      "value": "dictItemCode"
                                    },
                                    "@created": "{{() => {\n                    $queryEngine.request.baseRequest({\n                      type: 'base_dict_item_ide',\n                      action: 'query',\n                      payload: {\n                        filter: {\"$and\":{\"dictId\":{\"eq\":\"6844016601726976\"}}},\n                        page: {\"pageSize\":9999,\"pageNum\":1,\"sort\":\"dictItemNo asc\"},\n                      },\n                      query: {\"*\":{}},\n                      actionConfig: { service: 'api-base' },\n                      queryConfig: { staleTime: 300000 }\n                    }).then(res => {\n                      $self.dataSource = res.data || []\n                    })\n                  }}}"
                                  },
                                  "x-designer-extend": {
                                    "source": {
                                      "sourceType": "MQL",
                                      "action": "query",
                                      "type": "base_dict_item_ide",
                                      "query": {
                                        "*": {}
                                      },
                                      "filter": {
                                        "$and": {
                                          "dictId": {
                                            "eq": "6844016601726976"
                                          }
                                        }
                                      },
                                      "service": "api-base",
                                      "payload": {
                                        "page": {
                                          "sort": "dictItemNo asc"
                                        }
                                      },
                                      "enum": [],
                                      "conditions": [
                                        [
                                          {
                                            "dataName": "dictId",
                                            "comparison": "eq",
                                            "valueType": "fixed",
                                            "modelField": "6844016601726976",
                                            "modelId": ""
                                          }
                                        ]
                                      ]
                                    },
                                    "events": []
                                  },
                                  "enum": [],
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "name": "paymentTerms",
                                  "x-designable-id": "pnik7twf3op",
                                  "x-index": 0
                                }
                              }
                            },
                            "operation": {
                              "type": "void",
                              "title": "操作",
                              "x-visible": "{{!$$safeGetScope('$readOnly')}}",
                              "x-reactions": {
                                "fulfill": {
                                  "state": {
                                    "visible": "{{ !$$safeGetScope('$readOnly') }}"
                                  }
                                }
                              },
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "width": 248,
                                "fixed": "right",
                                "title": "操作",
                                "id": "5d61awhz3nm",
                                "visible": true,
                                "resizable": true,
                                "sortable": true,
                                "isGroup": false
                              },
                              "name": "operation",
                              "x-designable-id": "5d61awhz3nm",
                              "x-index": 5,
                              "x-display": "visible",
                              "properties": {
                                "operation": {
                                  "type": "void",
                                  "x-component": "RenderButtonList",
                                  "x-component-props": {
                                    "max": 3,
                                    "size": 12
                                  },
                                  "x-index": 0,
                                  "name": "operation",
                                  "x-designable-id": "8tjo813kpy2",
                                  "properties": {
                                    "delete": {
                                      "type": "void",
                                      "title": "删除",
                                      "x-component": "Button",
                                      "x-component-props": {
                                        "type": "text",
                                        "size": "small",
                                        "plain": false,
                                        "round": false,
                                        "circle": false,
                                        "style": {
                                          "opacity": 1
                                        },
                                        "@click": "{{(...args) => {\n        return new $LogicEngineRuntime({\n          nodes: [{\"id\":\"19a39e2d-2d1c-44f2-928b-69e8dac206c4\",\"shape\":{\"name\":\"logic-start\",\"size\":{\"width\":50,\"height\":50},\"position\":{\"x\":323.5,\"y\":0},\"visible\":true,\"zIndex\":1},\"data\":{\"title\":\"开始\",\"value\":null}},{\"id\":\"23f30d6b-a80d-4aa9-ac7e-e0649178a042\",\"shape\":{\"name\":\"logic-end\",\"size\":{\"width\":50,\"height\":50},\"position\":{\"x\":323.5,\"y\":518},\"visible\":true,\"zIndex\":2},\"data\":{\"title\":\"结束\",\"value\":null}},{\"id\":\"20ff9bfc-5d8a-4850-a49a-4b56bfa66988\",\"shape\":{\"name\":\"logic-table-row-delete\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":306,\"y\":252},\"visible\":true,\"zIndex\":3},\"data\":{\"title\":\"删除行\",\"value\":null},\"executeFunction\":\"function logicNode(ctx) {\\n  \\n        if ($table) {\\n          return $table.remove($self.index)\\n        }\\n        console.warn('该事件动作只能在表格行上触发')\\n      \\n}\"},{\"id\":\"5fef1e14-b588-4016-83b2-821f5d369766\",\"shape\":{\"name\":\"edge\",\"zIndex\":4,\"source\":{\"cell\":\"19a39e2d-2d1c-44f2-928b-69e8dac206c4\",\"port\":\"bottom\"},\"target\":{\"cell\":\"20ff9bfc-5d8a-4850-a49a-4b56bfa66988\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"e5ff63c7-9a4c-4670-bddd-67cdbd9d66e9\",\"shape\":{\"name\":\"edge\",\"zIndex\":5,\"source\":{\"cell\":\"20ff9bfc-5d8a-4850-a49a-4b56bfa66988\",\"port\":\"bottom\"},\"target\":{\"cell\":\"23f30d6b-a80d-4aa9-ac7e-e0649178a042\",\"port\":\"top\"}},\"data\":{\"value\":null}}],\n          payload: args,\n          scope: {\n            ...$root,\n            $designPages: $designPages,\n          }\n        }).run()\n      }}}"
                                      },
                                      "x-designer-extend": {
                                        "events": [
                                          {
                                            "title": "删除",
                                            "name": "click",
                                            "type": "logic",
                                            "args": [
                                              {
                                                "id": "19a39e2d-2d1c-44f2-928b-69e8dac206c4",
                                                "shape": {
                                                  "name": "logic-start",
                                                  "size": {
                                                    "width": 50,
                                                    "height": 50
                                                  },
                                                  "position": {
                                                    "x": 323.5,
                                                    "y": 0
                                                  },
                                                  "visible": true,
                                                  "zIndex": 1
                                                },
                                                "data": {
                                                  "title": "开始",
                                                  "value": null
                                                }
                                              },
                                              {
                                                "id": "23f30d6b-a80d-4aa9-ac7e-e0649178a042",
                                                "shape": {
                                                  "name": "logic-end",
                                                  "size": {
                                                    "width": 50,
                                                    "height": 50
                                                  },
                                                  "position": {
                                                    "x": 323.5,
                                                    "y": 518
                                                  },
                                                  "visible": true,
                                                  "zIndex": 2
                                                },
                                                "data": {
                                                  "title": "结束",
                                                  "value": null
                                                }
                                              },
                                              {
                                                "id": "20ff9bfc-5d8a-4850-a49a-4b56bfa66988",
                                                "shape": {
                                                  "name": "logic-table-row-delete",
                                                  "size": {
                                                    "width": 86,
                                                    "height": 30
                                                  },
                                                  "position": {
                                                    "x": 306,
                                                    "y": 252
                                                  },
                                                  "visible": true,
                                                  "zIndex": 3
                                                },
                                                "data": {
                                                  "title": "删除行",
                                                  "value": null
                                                },
                                                "executeFunction": "function logicNode(ctx) {\n  \n        if ($table) {\n          return $table.remove($self.index)\n        }\n        console.warn('该事件动作只能在表格行上触发')\n      \n}"
                                              },
                                              {
                                                "id": "5fef1e14-b588-4016-83b2-821f5d369766",
                                                "shape": {
                                                  "name": "edge",
                                                  "zIndex": 4,
                                                  "source": {
                                                    "cell": "19a39e2d-2d1c-44f2-928b-69e8dac206c4",
                                                    "port": "bottom"
                                                  },
                                                  "target": {
                                                    "cell": "20ff9bfc-5d8a-4850-a49a-4b56bfa66988",
                                                    "port": "top"
                                                  }
                                                },
                                                "data": {
                                                  "value": null
                                                }
                                              },
                                              {
                                                "id": "e5ff63c7-9a4c-4670-bddd-67cdbd9d66e9",
                                                "shape": {
                                                  "name": "edge",
                                                  "zIndex": 5,
                                                  "source": {
                                                    "cell": "20ff9bfc-5d8a-4850-a49a-4b56bfa66988",
                                                    "port": "bottom"
                                                  },
                                                  "target": {
                                                    "cell": "23f30d6b-a80d-4aa9-ac7e-e0649178a042",
                                                    "port": "top"
                                                  }
                                                },
                                                "data": {
                                                  "value": null
                                                }
                                              }
                                            ]
                                          }
                                        ]
                                      },
                                      "name": "delete",
                                      "x-designable-id": "x36c7yiq800",
                                      "x-index": 0,
                                      "x-decorator-props": {
                                        "style": {
                                          "opacity": 1
                                        }
                                      },
                                      "x-validator": []
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    },
                    "a29ceb1leim": {
                      "type": "void",
                      "x-component": "FormCollapse.Item",
                      "x-component-props": {
                        "title": "银行信息",
                        "disabled": false,
                        "style": {
                          "opacity": 1
                        }
                      },
                      "name": "a29ceb1leim",
                      "x-designable-id": "4dv6lwiti4a",
                      "x-index": 5,
                      "x-decorator-props": {
                        "style": {
                          "opacity": 1
                        }
                      },
                      "x-designer-extend": {},
                      "properties": {
                        "tools": {
                          "type": "void",
                          "x-component": "RenderButtonList",
                          "x-component-props": {
                            "style": {
                              "margin": "0px 20px 16px 20px"
                            }
                          },
                          "x-index": 0,
                          "name": "tools",
                          "x-designable-id": "1dobpwp4ktk",
                          "properties": {
                            "add": {
                              "type": "void",
                              "x-component": "Button",
                              "title": "新增",
                              "x-visible": "{{!$$safeGetScope('$readOnly')}}",
                              "x-component-props": {
                                "type": "primary",
                                "size": "small",
                                "plain": false,
                                "round": false,
                                "circle": false,
                                "style": {
                                  "opacity": 1
                                },
                                "@click": "{{(...args) => {\n        return new $LogicEngineRuntime({\n          nodes: [{\"id\":\"5424ed73-caba-47e9-86ea-39762a61483f\",\"shape\":{\"name\":\"logic-start\",\"size\":{\"width\":50,\"height\":50},\"position\":{\"x\":323.5,\"y\":0},\"visible\":true,\"zIndex\":1},\"data\":{\"title\":\"开始\",\"value\":null}},{\"id\":\"1e2ec30f-25b7-4e16-8aa4-2d2b114dff75\",\"shape\":{\"name\":\"logic-end\",\"size\":{\"width\":50,\"height\":50},\"position\":{\"x\":323.5,\"y\":518},\"visible\":true,\"zIndex\":2},\"data\":{\"title\":\"结束\",\"value\":null}},{\"id\":\"b281e537-ccc7-4c94-83dd-d4f91819bff4\",\"shape\":{\"name\":\"logic-table-row-insert\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":306,\"y\":284},\"visible\":true,\"zIndex\":3},\"data\":{\"title\":\"新增行\",\"value\":{\"method\":\"unshift\",\"tableId\":\"gyetcq0yv7f\"}},\"executeFunction\":\"function logicNode(ctx) {\\n  \\n      $designable.query(\\\"gyetcq0yv7f\\\").take()?.invoke(\\\"addRow\\\", \\\"unshift\\\")\\n    \\n}\"},{\"id\":\"4db8c197-3704-408c-85ab-1b52772130b4\",\"shape\":{\"name\":\"edge\",\"zIndex\":4,\"source\":{\"cell\":\"5424ed73-caba-47e9-86ea-39762a61483f\",\"port\":\"bottom\"},\"target\":{\"cell\":\"b281e537-ccc7-4c94-83dd-d4f91819bff4\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"ed683930-2de7-459a-b566-783362190238\",\"shape\":{\"name\":\"edge\",\"zIndex\":5,\"source\":{\"cell\":\"b281e537-ccc7-4c94-83dd-d4f91819bff4\",\"port\":\"bottom\"},\"target\":{\"cell\":\"1e2ec30f-25b7-4e16-8aa4-2d2b114dff75\",\"port\":\"top\"}},\"data\":{\"value\":null}}],\n          payload: args,\n          scope: {\n            ...$root,\n            $designPages: $designPages,\n          }\n        }).run()\n      }}}"
                              },
                              "x-designer-extend": {
                                "events": [
                                  {
                                    "title": "新增",
                                    "name": "click",
                                    "type": "logic",
                                    "args": [
                                      {
                                        "id": "5424ed73-caba-47e9-86ea-39762a61483f",
                                        "shape": {
                                          "name": "logic-start",
                                          "size": {
                                            "width": 50,
                                            "height": 50
                                          },
                                          "position": {
                                            "x": 323.5,
                                            "y": 0
                                          },
                                          "visible": true,
                                          "zIndex": 1
                                        },
                                        "data": {
                                          "title": "开始",
                                          "value": null
                                        }
                                      },
                                      {
                                        "id": "1e2ec30f-25b7-4e16-8aa4-2d2b114dff75",
                                        "shape": {
                                          "name": "logic-end",
                                          "size": {
                                            "width": 50,
                                            "height": 50
                                          },
                                          "position": {
                                            "x": 323.5,
                                            "y": 518
                                          },
                                          "visible": true,
                                          "zIndex": 2
                                        },
                                        "data": {
                                          "title": "结束",
                                          "value": null
                                        }
                                      },
                                      {
                                        "id": "b281e537-ccc7-4c94-83dd-d4f91819bff4",
                                        "shape": {
                                          "name": "logic-table-row-insert",
                                          "size": {
                                            "width": 86,
                                            "height": 30
                                          },
                                          "position": {
                                            "x": 306,
                                            "y": 284
                                          },
                                          "visible": true,
                                          "zIndex": 3
                                        },
                                        "data": {
                                          "title": "新增行",
                                          "value": {
                                            "method": "unshift",
                                            "tableId": "gyetcq0yv7f"
                                          }
                                        },
                                        "executeFunction": "function logicNode(ctx) {\n  \n      $designable.query(\"gyetcq0yv7f\").take()?.invoke(\"addRow\", \"unshift\")\n    \n}"
                                      },
                                      {
                                        "id": "4db8c197-3704-408c-85ab-1b52772130b4",
                                        "shape": {
                                          "name": "edge",
                                          "zIndex": 4,
                                          "source": {
                                            "cell": "5424ed73-caba-47e9-86ea-39762a61483f",
                                            "port": "bottom"
                                          },
                                          "target": {
                                            "cell": "b281e537-ccc7-4c94-83dd-d4f91819bff4",
                                            "port": "top"
                                          }
                                        },
                                        "data": {
                                          "value": null
                                        }
                                      },
                                      {
                                        "id": "ed683930-2de7-459a-b566-783362190238",
                                        "shape": {
                                          "name": "edge",
                                          "zIndex": 5,
                                          "source": {
                                            "cell": "b281e537-ccc7-4c94-83dd-d4f91819bff4",
                                            "port": "bottom"
                                          },
                                          "target": {
                                            "cell": "1e2ec30f-25b7-4e16-8aa4-2d2b114dff75",
                                            "port": "top"
                                          }
                                        },
                                        "data": {
                                          "value": null
                                        }
                                      }
                                    ]
                                  }
                                ]
                              },
                              "x-index": 0,
                              "name": "add",
                              "x-designable-id": "why49zdht2n",
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-validator": []
                            }
                          }
                        },
                        "effectFormBankInfos": {
                          "type": "array",
                          "x-decorator": "FormItem",
                          "x-component": "RenderTable",
                          "x-validator": [],
                          "x-decorator-props": {
                            "style": "padding: 0 20px;",
                            "feedbackLayout": "loose"
                          },
                          "x-designable-id": "gyetcq0yv7f",
                          "x-component-props": {
                            "stripe": false,
                            "border": true,
                            "show-header": true,
                            "size": "small",
                            "style": "width:100%",
                            "preColumns": [
                              "seq"
                            ],
                            "editMode": true,
                            "primaryKey": "id",
                            "cascadeDeletion": true,
                            "height": "300px",
                            "pagination": true,
                            "showHeader": true,
                            "openCustomTable": false,
                            "dblclickEditable": false
                          },
                          "x-designer-extend": {
                            "page": {
                              "pagination": true
                            },
                            "events": []
                          },
                          "x-index": 1,
                          "name": "effectFormBankInfos",
                          "x-reactions": {
                            "dependencies": [],
                            "fulfill": {
                              "state": {
                                "componentProps": "{{{\r\n  ...$self.componentProps,\r\n    primaryKey: 'effectFormBankInfoId'\r\n}}}"
                              }
                            }
                          },
                          "properties": {
                            "bankCode": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "width": 150,
                                "title": "银行代码",
                                "sortable": true,
                                "resizable": true,
                                "id": "zljahdnrre1",
                                "visible": true,
                                "isGroup": false,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-index": 0,
                              "name": "bankCode",
                              "x-designable-id": "zljahdnrre1",
                              "x-display": "visible",
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {},
                              "properties": {
                                "bankCode": {
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "Selector",
                                  "x-query-engine-relation": true,
                                  "x-query-engine-relation-strict": true,
                                  "name": "bankCode",
                                  "x-designer-extend": {
                                    "model": {
                                      "businessType": "effectFormBankInfo",
                                      "reference": {
                                        "modelName": "银行分行信息（隆基银行分行数据同步）",
                                        "referenceField": "bankNum",
                                        "modelId": "1670599593048961025",
                                        "businessType": "base_erpBranchBank_ide"
                                      },
                                      "modelId": "1668183595457761282",
                                      "field": "effectFormBankInfos.bankCode",
                                      "originalField": "bankCode"
                                    },
                                    "events": [],
                                    "reference": {
                                      "type": "PAGE",
                                      "content": {
                                        "pageId": "LLP1668564193078726658"
                                      },
                                      "backfill": {
                                        "type": "form",
                                        "tableTag": "",
                                        "options": [
                                          {
                                            "from": "bankName",
                                            "to": {
                                              "address": "qenxcou4juw.a29ceb1leim.effectFormBankInfos.bankName.bankName",
                                              "name": "bankName",
                                              "title": "输入框",
                                              "root": {
                                                "_isJSONSchemaObject": true,
                                                "version": "2.0",
                                                "type": "string",
                                                "x-component": "Input",
                                                "x-decorator": "FormItem",
                                                "x-component-props": {
                                                  "style": {
                                                    "opacity": 1
                                                  }
                                                },
                                                "x-designer-extend": {
                                                  "model": {
                                                    "businessType": "sccSupEffectFormBankInfo",
                                                    "reference": null,
                                                    "modelId": "1668183595457761282",
                                                    "field": "bankName",
                                                    "originalField": "bankName"
                                                  },
                                                  "events": []
                                                },
                                                "x-index": 0,
                                                "name": "bankName",
                                                "x-designable-id": "rsylzj1x6s0",
                                                "x-validator": [],
                                                "x-decorator-props": {
                                                  "style": {
                                                    "opacity": 1
                                                  },
                                                  "feedbackLayout": "popover"
                                                },
                                                "x-pattern": "disabled"
                                              },
                                              "disabled": false,
                                              "parent": {
                                                "type": "array",
                                                "path": "qenxcou4juw.a29ceb1leim.effectFormBankInfos.bankName"
                                              },
                                              "designableId": "rsylzj1x6s0",
                                              "children": []
                                            }
                                          },
                                          {
                                            "from": "branchBankNum",
                                            "to": {
                                              "address": "qenxcou4juw.a29ceb1leim.effectFormBankInfos.unionCode.unionCode",
                                              "name": "unionCode",
                                              "title": "输入框",
                                              "root": {
                                                "_isJSONSchemaObject": true,
                                                "version": "2.0",
                                                "type": "string",
                                                "x-component": "Input",
                                                "x-decorator": "FormItem",
                                                "x-component-props": {
                                                  "style": {
                                                    "opacity": 1
                                                  }
                                                },
                                                "x-designer-extend": {
                                                  "model": {
                                                    "businessType": "sccSupEffectFormBankInfo",
                                                    "reference": null,
                                                    "modelId": "1668183595457761282",
                                                    "field": "unionCode",
                                                    "originalField": "unionCode"
                                                  },
                                                  "events": []
                                                },
                                                "x-index": 0,
                                                "name": "unionCode",
                                                "x-designable-id": "31r6w9mhogo",
                                                "x-validator": [],
                                                "x-decorator-props": {
                                                  "style": {
                                                    "opacity": 1
                                                  },
                                                  "feedbackLayout": "popover"
                                                },
                                                "x-pattern": "readPretty"
                                              },
                                              "disabled": false,
                                              "parent": {
                                                "type": "array",
                                                "path": "qenxcou4juw.a29ceb1leim.effectFormBankInfos.unionCode"
                                              },
                                              "designableId": "31r6w9mhogo",
                                              "children": []
                                            }
                                          },
                                          {
                                            "from": "branchBankName",
                                            "to": {
                                              "address": "qenxcou4juw.a29ceb1leim.effectFormBankInfos.openingBank.openingBank",
                                              "name": "openingBank",
                                              "title": "输入框",
                                              "root": {
                                                "_isJSONSchemaObject": true,
                                                "version": "2.0",
                                                "type": "string",
                                                "title": "",
                                                "x-decorator": "FormItem",
                                                "x-component": "Input",
                                                "x-decorator-props": {
                                                  "style": {
                                                    "opacity": 1
                                                  },
                                                  "feedbackLayout": "popover"
                                                },
                                                "x-designer-extend": {
                                                  "events": []
                                                },
                                                "x-component-props": {
                                                  "style": {
                                                    "opacity": 1
                                                  }
                                                },
                                                "x-validator": [],
                                                "name": "openingBank",
                                                "x-pattern": "readOnly",
                                                "x-designable-id": "nlknx95sxk9",
                                                "x-index": 0
                                              },
                                              "disabled": false,
                                              "parent": {
                                                "type": "array",
                                                "path": "qenxcou4juw.a29ceb1leim.effectFormBankInfos.openingBank"
                                              },
                                              "designableId": "nlknx95sxk9",
                                              "children": []
                                            }
                                          }
                                        ]
                                      },
                                      "extend": {
                                        "functionCode": "LLP1668564193078726658"
                                      },
                                      "source": {
                                        "sourceType": "MQL",
                                        "action": "query",
                                        "service": "api-base",
                                        "type": "base_erpBranchBank_ide",
                                        "query": {
                                          "*": {}
                                        },
                                        "filter": {
                                          "sort": ""
                                        },
                                        "payload": {}
                                      }
                                    }
                                  },
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "x-component-props": {
                                    "pickerOptions": {
                                      "type": "dialog",
                                      "pageTitle": "银行快查"
                                    },
                                    "isAsyncDataSource": true,
                                    "style": {
                                      "opacity": 1
                                    },
                                    "fieldNames": {
                                      "label": "bankNum",
                                      "value": "bankNum"
                                    },
                                    "reference": {
                                      "type": "PAGE",
                                      "content": {
                                        "pageId": "LLP1668564193078726658"
                                      },
                                      "backfill": {
                                        "type": "form",
                                        "tableTag": "",
                                        "options": [
                                          {
                                            "from": "bankName",
                                            "to": {
                                              "address": "qenxcou4juw.a29ceb1leim.effectFormBankInfos.bankName.bankName",
                                              "name": "bankName",
                                              "title": "输入框",
                                              "root": {
                                                "_isJSONSchemaObject": true,
                                                "version": "2.0",
                                                "type": "string",
                                                "x-component": "Input",
                                                "x-decorator": "FormItem",
                                                "x-component-props": {
                                                  "style": {
                                                    "opacity": 1
                                                  }
                                                },
                                                "x-designer-extend": {
                                                  "model": {
                                                    "businessType": "sccSupEffectFormBankInfo",
                                                    "reference": null,
                                                    "modelId": "1668183595457761282",
                                                    "field": "bankName",
                                                    "originalField": "bankName"
                                                  },
                                                  "events": []
                                                },
                                                "x-index": 0,
                                                "name": "bankName",
                                                "x-designable-id": "rsylzj1x6s0",
                                                "x-validator": [],
                                                "x-decorator-props": {
                                                  "style": {
                                                    "opacity": 1
                                                  },
                                                  "feedbackLayout": "popover"
                                                },
                                                "x-pattern": "disabled"
                                              },
                                              "disabled": false,
                                              "parent": {
                                                "type": "array",
                                                "path": "qenxcou4juw.a29ceb1leim.effectFormBankInfos.bankName"
                                              },
                                              "designableId": "rsylzj1x6s0",
                                              "children": []
                                            }
                                          },
                                          {
                                            "from": "branchBankNum",
                                            "to": {
                                              "address": "qenxcou4juw.a29ceb1leim.effectFormBankInfos.unionCode.unionCode",
                                              "name": "unionCode",
                                              "title": "输入框",
                                              "root": {
                                                "_isJSONSchemaObject": true,
                                                "version": "2.0",
                                                "type": "string",
                                                "x-component": "Input",
                                                "x-decorator": "FormItem",
                                                "x-component-props": {
                                                  "style": {
                                                    "opacity": 1
                                                  }
                                                },
                                                "x-designer-extend": {
                                                  "model": {
                                                    "businessType": "sccSupEffectFormBankInfo",
                                                    "reference": null,
                                                    "modelId": "1668183595457761282",
                                                    "field": "unionCode",
                                                    "originalField": "unionCode"
                                                  },
                                                  "events": []
                                                },
                                                "x-index": 0,
                                                "name": "unionCode",
                                                "x-designable-id": "31r6w9mhogo",
                                                "x-validator": [],
                                                "x-decorator-props": {
                                                  "style": {
                                                    "opacity": 1
                                                  },
                                                  "feedbackLayout": "popover"
                                                },
                                                "x-pattern": "readPretty"
                                              },
                                              "disabled": false,
                                              "parent": {
                                                "type": "array",
                                                "path": "qenxcou4juw.a29ceb1leim.effectFormBankInfos.unionCode"
                                              },
                                              "designableId": "31r6w9mhogo",
                                              "children": []
                                            }
                                          },
                                          {
                                            "from": "branchBankName",
                                            "to": {
                                              "address": "qenxcou4juw.a29ceb1leim.effectFormBankInfos.openingBank.openingBank",
                                              "name": "openingBank",
                                              "title": "输入框",
                                              "root": {
                                                "_isJSONSchemaObject": true,
                                                "version": "2.0",
                                                "type": "string",
                                                "title": "",
                                                "x-decorator": "FormItem",
                                                "x-component": "Input",
                                                "x-decorator-props": {
                                                  "style": {
                                                    "opacity": 1
                                                  },
                                                  "feedbackLayout": "popover"
                                                },
                                                "x-designer-extend": {
                                                  "events": []
                                                },
                                                "x-component-props": {
                                                  "style": {
                                                    "opacity": 1
                                                  }
                                                },
                                                "x-validator": [],
                                                "name": "openingBank",
                                                "x-pattern": "readOnly",
                                                "x-designable-id": "nlknx95sxk9",
                                                "x-index": 0
                                              },
                                              "disabled": false,
                                              "parent": {
                                                "type": "array",
                                                "path": "qenxcou4juw.a29ceb1leim.effectFormBankInfos.openingBank"
                                              },
                                              "designableId": "nlknx95sxk9",
                                              "children": []
                                            }
                                          }
                                        ]
                                      },
                                      "extend": {
                                        "functionCode": "LLP1668564193078726658"
                                      },
                                      "source": {
                                        "sourceType": "MQL",
                                        "action": "query",
                                        "service": "api-base",
                                        "type": "base_erpBranchBank_ide",
                                        "query": {
                                          "*": {}
                                        },
                                        "filter": {
                                          "sort": ""
                                        },
                                        "payload": {}
                                      }
                                    }
                                  },
                                  "x-validator": [],
                                  "x-designable-id": "jleaizahsj2",
                                  "x-index": 0
                                }
                              }
                            },
                            "bankName": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "width": 150,
                                "title": "银行名称",
                                "sortable": true,
                                "resizable": true,
                                "id": "yabkblj272l",
                                "visible": true,
                                "isGroup": false,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-index": 1,
                              "name": "bankName",
                              "x-designable-id": "yabkblj272l",
                              "x-display": "visible",
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {},
                              "properties": {
                                "bankName": {
                                  "type": "string",
                                  "x-component": "Input",
                                  "x-decorator": "FormItem",
                                  "x-component-props": {
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-designer-extend": {
                                    "model": {
                                      "businessType": "sccSupEffectFormBankInfo",
                                      "reference": null,
                                      "modelId": "1668183595457761282",
                                      "field": "bankName",
                                      "originalField": "bankName"
                                    },
                                    "events": []
                                  },
                                  "x-index": 0,
                                  "name": "bankName",
                                  "x-designable-id": "rsylzj1x6s0",
                                  "x-validator": [],
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "x-pattern": "disabled"
                                }
                              }
                            },
                            "openingBank": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "width": 150,
                                "title": "开户行名称",
                                "sortable": true,
                                "resizable": true,
                                "id": "anszykdq27m",
                                "visible": true,
                                "isGroup": false
                              },
                              "x-index": 2,
                              "name": "openingBank",
                              "x-designable-id": "anszykdq27m",
                              "x-display": "visible",
                              "properties": {
                                "openingBank": {
                                  "type": "string",
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "Input",
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "x-designer-extend": {
                                    "events": []
                                  },
                                  "x-component-props": {
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-validator": [],
                                  "name": "openingBank",
                                  "x-pattern": "disabled",
                                  "x-designable-id": "nlknx95sxk9",
                                  "x-index": 0
                                }
                              }
                            },
                            "unionCode": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "width": 150,
                                "title": "分行编码",
                                "sortable": true,
                                "resizable": true,
                                "id": "nck3p1kwcvz",
                                "visible": true,
                                "isGroup": false
                              },
                              "x-index": 3,
                              "name": "unionCode",
                              "x-designable-id": "nck3p1kwcvz",
                              "x-display": "visible",
                              "properties": {
                                "unionCode": {
                                  "type": "string",
                                  "x-component": "Input",
                                  "x-decorator": "FormItem",
                                  "x-component-props": {
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-designer-extend": {
                                    "model": {
                                      "businessType": "sccSupEffectFormBankInfo",
                                      "reference": null,
                                      "modelId": "1668183595457761282",
                                      "field": "unionCode",
                                      "originalField": "unionCode"
                                    },
                                    "events": []
                                  },
                                  "x-index": 0,
                                  "name": "unionCode",
                                  "x-designable-id": "31r6w9mhogo",
                                  "x-validator": [],
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "x-pattern": "readPretty"
                                }
                              }
                            },
                            "vxu2q4tfapw": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "title": "账户名称",
                                "sortable": true,
                                "resizable": true,
                                "visible": true,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                },
                                "id": "vxu2q4tfapw",
                                "isGroup": false,
                                "width": 150
                              },
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {},
                              "x-index": 4,
                              "x-designable-id": "vxu2q4tfapw",
                              "x-display": "visible",
                              "properties": {
                                "bankAccountName": {
                                  "type": "string",
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "Input",
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "x-designer-extend": {
                                    "events": []
                                  },
                                  "x-component-props": {
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-validator": [],
                                  "name": "bankAccountName",
                                  "x-designable-id": "0j8io96ylbd",
                                  "x-index": 0
                                }
                              }
                            },
                            "bankAccount": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "width": 150,
                                "title": "银行账号",
                                "sortable": true,
                                "resizable": true,
                                "id": "sk8fla5v3qc",
                                "visible": true,
                                "isGroup": false
                              },
                              "x-index": 5,
                              "name": "bankAccount",
                              "x-designable-id": "sk8fla5v3qc",
                              "x-display": "visible",
                              "properties": {
                                "bankAccount": {
                                  "type": "string",
                                  "x-component": "Input",
                                  "x-decorator": "FormItem",
                                  "x-component-props": {
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-designer-extend": {
                                    "model": {
                                      "businessType": "sccSupEffectFormBankInfo",
                                      "reference": null,
                                      "modelId": "1668183595457761282",
                                      "field": "bankAccount",
                                      "originalField": "bankAccount"
                                    },
                                    "events": []
                                  },
                                  "x-index": 0,
                                  "name": "bankAccount",
                                  "x-designable-id": "pb1g2ch0y1v",
                                  "x-validator": [],
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  }
                                }
                              }
                            },
                            "currencyCode": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "width": 150,
                                "title": "币种",
                                "sortable": true,
                                "resizable": true,
                                "id": "rfcxh7eya7p",
                                "visible": true,
                                "isGroup": false
                              },
                              "x-index": 6,
                              "name": "currencyCode",
                              "x-designable-id": "rfcxh7eya7p",
                              "x-display": "visible",
                              "properties": {
                                "currencyCode": {
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "Select",
                                  "x-validator": [],
                                  "x-component-props": {
                                    "size": "small",
                                    "multiple-limit": 0,
                                    "placeholder": "请选择",
                                    "style": {
                                      "opacity": 1
                                    },
                                    "fieldNames": {
                                      "label": "currencyName",
                                      "value": "currencyCode"
                                    },
                                    "@created": "{{() => {\n                    $queryEngine.request.baseRequest({\n                      type: 'base_PurchaseCurrency_ide',\n                      action: 'query',\n                      payload: {\n                        filter: {},\n                        page: {\"pageSize\":9999,\"pageNum\":1,\"sort\":\"currencyId asc\"},\n                      },\n                      query: {\"*\":{}},\n                      actionConfig: { service: 'api-base' },\n                      queryConfig: { staleTime: 300000 }\n                    }).then(res => {\n                      $self.dataSource = res.data || []\n                    })\n                  }}}"
                                  },
                                  "x-designer-extend": {
                                    "source": {
                                      "sourceType": "MQL",
                                      "action": "query",
                                      "type": "base_PurchaseCurrency_ide",
                                      "query": {
                                        "*": {}
                                      },
                                      "filter": {},
                                      "service": "api-base",
                                      "payload": {
                                        "page": {
                                          "sort": "currencyId asc"
                                        }
                                      },
                                      "enum": []
                                    },
                                    "events": []
                                  },
                                  "enum": [],
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "name": "currencyCode",
                                  "x-designable-id": "5piv2sat4ex",
                                  "x-index": 0
                                }
                              }
                            },
                            "ceeaMainAccount": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "width": 150,
                                "title": "主账号",
                                "sortable": true,
                                "resizable": true,
                                "id": "70ekce9gzt2",
                                "visible": true,
                                "isGroup": false,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-index": 7,
                              "name": "ceeaMainAccount",
                              "x-designable-id": "70ekce9gzt2",
                              "x-display": "visible",
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {},
                              "properties": {
                                "ceeaMainAccount": {
                                  "type": "string",
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "Checkbox",
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "x-designer-extend": {
                                    "events": []
                                  },
                                  "x-component-props": {
                                    "content": "",
                                    "trueLabel": "Y",
                                    "falseLabel": "N",
                                    "disabled": false,
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-validator": [],
                                  "name": "ceeaMainAccount",
                                  "x-designable-id": "x8ld81ant7b",
                                  "x-index": 0
                                }
                              }
                            },
                            "ceeaEnabled": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "width": 150,
                                "title": "启用",
                                "sortable": true,
                                "resizable": true,
                                "id": "extej9lpjbt",
                                "visible": true,
                                "isGroup": false,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-index": 8,
                              "name": "ceeaEnabled",
                              "x-designable-id": "extej9lpjbt",
                              "x-display": "visible",
                              "properties": {
                                "ceeaEnabled": {
                                  "type": "string",
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "Checkbox",
                                  "x-validator": [],
                                  "x-component-props": {
                                    "content": "",
                                    "trueLabel": "Y",
                                    "falseLabel": "N",
                                    "disabled": false,
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-designer-extend": {
                                    "events": []
                                  },
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "name": "ceeaEnabled",
                                  "x-designable-id": "cwx23jf0s82",
                                  "x-index": 0
                                }
                              }
                            },
                            "operation": {
                              "type": "void",
                              "title": "操作",
                              "x-visible": "{{!$$safeGetScope('$readOnly')}}",
                              "x-reactions": {
                                "fulfill": {
                                  "state": {
                                    "visible": "{{ !$$safeGetScope('$readOnly') }}"
                                  }
                                }
                              },
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "width": 248,
                                "fixed": "right",
                                "title": "操作",
                                "id": "388phwjbvj3",
                                "visible": true,
                                "resizable": true,
                                "sortable": true,
                                "isGroup": false
                              },
                              "name": "operation",
                              "x-designable-id": "388phwjbvj3",
                              "x-index": 9,
                              "x-display": "visible",
                              "properties": {
                                "operation": {
                                  "type": "void",
                                  "x-component": "RenderButtonList",
                                  "x-component-props": {
                                    "max": 3,
                                    "size": 12
                                  },
                                  "x-index": 0,
                                  "name": "operation",
                                  "x-designable-id": "crjyktb9qu3",
                                  "properties": {
                                    "delete": {
                                      "type": "void",
                                      "title": "删除",
                                      "x-component": "Button",
                                      "x-component-props": {
                                        "type": "text",
                                        "size": "small",
                                        "plain": false,
                                        "round": false,
                                        "circle": false,
                                        "style": {
                                          "opacity": 1
                                        },
                                        "@click": "{{(...args) => {\n        return new $LogicEngineRuntime({\n          nodes: [{\"id\":\"19a39e2d-2d1c-44f2-928b-69e8dac206c4\",\"shape\":{\"name\":\"logic-start\",\"size\":{\"width\":50,\"height\":50},\"position\":{\"x\":323.5,\"y\":0},\"visible\":true,\"zIndex\":1},\"data\":{\"title\":\"开始\",\"value\":null}},{\"id\":\"23f30d6b-a80d-4aa9-ac7e-e0649178a042\",\"shape\":{\"name\":\"logic-end\",\"size\":{\"width\":50,\"height\":50},\"position\":{\"x\":323.5,\"y\":518},\"visible\":true,\"zIndex\":2},\"data\":{\"title\":\"结束\",\"value\":null}},{\"id\":\"20ff9bfc-5d8a-4850-a49a-4b56bfa66988\",\"shape\":{\"name\":\"logic-table-row-delete\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":306,\"y\":252},\"visible\":true,\"zIndex\":3},\"data\":{\"title\":\"删除行\",\"value\":null},\"executeFunction\":\"function logicNode(ctx) {\\n  \\n        if ($table) {\\n          return $table.remove($self.index)\\n        }\\n        console.warn('该事件动作只能在表格行上触发')\\n      \\n}\"},{\"id\":\"5fef1e14-b588-4016-83b2-821f5d369766\",\"shape\":{\"name\":\"edge\",\"zIndex\":4,\"source\":{\"cell\":\"19a39e2d-2d1c-44f2-928b-69e8dac206c4\",\"port\":\"bottom\"},\"target\":{\"cell\":\"20ff9bfc-5d8a-4850-a49a-4b56bfa66988\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"e5ff63c7-9a4c-4670-bddd-67cdbd9d66e9\",\"shape\":{\"name\":\"edge\",\"zIndex\":5,\"source\":{\"cell\":\"20ff9bfc-5d8a-4850-a49a-4b56bfa66988\",\"port\":\"bottom\"},\"target\":{\"cell\":\"23f30d6b-a80d-4aa9-ac7e-e0649178a042\",\"port\":\"top\"}},\"data\":{\"value\":null}}],\n          payload: args,\n          scope: {\n            ...$root,\n            $designPages: $designPages,\n          }\n        }).run()\n      }}}"
                                      },
                                      "x-designer-extend": {
                                        "events": [
                                          {
                                            "title": "删除",
                                            "name": "click",
                                            "type": "logic",
                                            "args": [
                                              {
                                                "id": "19a39e2d-2d1c-44f2-928b-69e8dac206c4",
                                                "shape": {
                                                  "name": "logic-start",
                                                  "size": {
                                                    "width": 50,
                                                    "height": 50
                                                  },
                                                  "position": {
                                                    "x": 323.5,
                                                    "y": 0
                                                  },
                                                  "visible": true,
                                                  "zIndex": 1
                                                },
                                                "data": {
                                                  "title": "开始",
                                                  "value": null
                                                }
                                              },
                                              {
                                                "id": "23f30d6b-a80d-4aa9-ac7e-e0649178a042",
                                                "shape": {
                                                  "name": "logic-end",
                                                  "size": {
                                                    "width": 50,
                                                    "height": 50
                                                  },
                                                  "position": {
                                                    "x": 323.5,
                                                    "y": 518
                                                  },
                                                  "visible": true,
                                                  "zIndex": 2
                                                },
                                                "data": {
                                                  "title": "结束",
                                                  "value": null
                                                }
                                              },
                                              {
                                                "id": "20ff9bfc-5d8a-4850-a49a-4b56bfa66988",
                                                "shape": {
                                                  "name": "logic-table-row-delete",
                                                  "size": {
                                                    "width": 86,
                                                    "height": 30
                                                  },
                                                  "position": {
                                                    "x": 306,
                                                    "y": 252
                                                  },
                                                  "visible": true,
                                                  "zIndex": 3
                                                },
                                                "data": {
                                                  "title": "删除行",
                                                  "value": null
                                                },
                                                "executeFunction": "function logicNode(ctx) {\n  \n        if ($table) {\n          return $table.remove($self.index)\n        }\n        console.warn('该事件动作只能在表格行上触发')\n      \n}"
                                              },
                                              {
                                                "id": "5fef1e14-b588-4016-83b2-821f5d369766",
                                                "shape": {
                                                  "name": "edge",
                                                  "zIndex": 4,
                                                  "source": {
                                                    "cell": "19a39e2d-2d1c-44f2-928b-69e8dac206c4",
                                                    "port": "bottom"
                                                  },
                                                  "target": {
                                                    "cell": "20ff9bfc-5d8a-4850-a49a-4b56bfa66988",
                                                    "port": "top"
                                                  }
                                                },
                                                "data": {
                                                  "value": null
                                                }
                                              },
                                              {
                                                "id": "e5ff63c7-9a4c-4670-bddd-67cdbd9d66e9",
                                                "shape": {
                                                  "name": "edge",
                                                  "zIndex": 5,
                                                  "source": {
                                                    "cell": "20ff9bfc-5d8a-4850-a49a-4b56bfa66988",
                                                    "port": "bottom"
                                                  },
                                                  "target": {
                                                    "cell": "23f30d6b-a80d-4aa9-ac7e-e0649178a042",
                                                    "port": "top"
                                                  }
                                                },
                                                "data": {
                                                  "value": null
                                                }
                                              }
                                            ]
                                          }
                                        ]
                                      },
                                      "name": "delete",
                                      "x-designable-id": "5ebcjftaeky",
                                      "x-index": 0,
                                      "x-decorator-props": {
                                        "style": {
                                          "opacity": 1
                                        }
                                      },
                                      "x-validator": []
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    },
                    "lr7q39pmmz4": {
                      "type": "void",
                      "x-component": "FormCollapse.Item",
                      "x-component-props": {
                        "title": "引入组织和品类",
                        "disabled": false,
                        "style": {
                          "opacity": 1
                        }
                      },
                      "name": "lr7q39pmmz4",
                      "x-designable-id": "a9a2jjfk5jy",
                      "x-index": 6,
                      "x-decorator-props": {
                        "style": {
                          "opacity": 1
                        }
                      },
                      "x-designer-extend": {},
                      "properties": {
                        "b01bjb4n16r": {
                          "type": "void",
                          "x-component": "FormGrid",
                          "x-component-props": {
                            "minWidth": 100,
                            "minColumns": 4,
                            "columnGap": 10,
                            "rowGap": 5,
                            "colWrap": true,
                            "style": {
                              "opacity": 1
                            },
                            "maxColumns": 4
                          },
                          "x-designable-id": "b01bjb4n16r",
                          "x-index": 0,
                          "properties": {
                            "vfzf97drm9l": {
                              "type": "void",
                              "x-component": "FormGrid.GridColumn",
                              "x-designable-id": "vfzf97drm9l",
                              "x-index": 0,
                              "x-component-props": {
                                "gridSpan": 1,
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {},
                              "properties": {
                                "d0p8k8bgx7d": {
                                  "title": "组织名称",
                                  "x-decorator": "FormItem",
                                  "x-component": "Select",
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "loose"
                                  },
                                  "x-designer-extend": {
                                    "source": {
                                      "sourceType": "STATIC",
                                      "enum": []
                                    },
                                    "events": [
                                      {
                                        "title": "选择后",
                                        "name": "change",
                                        "type": "logic",
                                        "args": [
                                          {
                                            "id": "ac50a348-2de2-489b-87bb-3a6e652ec665",
                                            "shape": {
                                              "name": "logic-start",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 380,
                                                "y": 0
                                              },
                                              "visible": true,
                                              "zIndex": 1
                                            },
                                            "data": {
                                              "title": "开始",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "66857794-fb11-4609-94b0-691682689276",
                                            "shape": {
                                              "name": "logic-end",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 380,
                                                "y": 374.5
                                              },
                                              "visible": true,
                                              "zIndex": 2
                                            },
                                            "data": {
                                              "title": "结束",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "fbf666cb-19fb-4c83-94a3-6dbd73c3256d",
                                            "shape": {
                                              "name": "logic-function",
                                              "size": {
                                                "width": 86,
                                                "height": 30
                                              },
                                              "position": {
                                                "x": 349,
                                                "y": 199
                                              },
                                              "visible": true,
                                              "zIndex": 3
                                            },
                                            "data": {
                                              "title": "自定义函数",
                                              "value": {
                                                "code": "function logicNode() {\n  const table = $designable.query('8gvu0iwifpj')\n  if (table) {\n    const vxeTableInstance = table.invoke('getVxeTableInstance')\n\n    vxeTableInstance.updateData()\n  }\n}"
                                              }
                                            },
                                            "executeFunction": "function logicNode() {\n  const table = $designable.query('8gvu0iwifpj')\n  if (table) {\n    const vxeTableInstance = table.invoke('getVxeTableInstance')\n\n    vxeTableInstance.updateData()\n  }\n}"
                                          },
                                          {
                                            "id": "453d3c87-eb4e-400d-8793-ebf837ec48e3",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 4,
                                              "source": {
                                                "cell": "ac50a348-2de2-489b-87bb-3a6e652ec665",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "fbf666cb-19fb-4c83-94a3-6dbd73c3256d",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "2731e80c-42b7-41b4-81c0-555b8db3add9",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 5,
                                              "source": {
                                                "cell": "fbf666cb-19fb-4c83-94a3-6dbd73c3256d",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "66857794-fb11-4609-94b0-691682689276",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          }
                                        ]
                                      }
                                    ]
                                  },
                                  "x-component-props": {
                                    "size": "small",
                                    "multiple-limit": 0,
                                    "placeholder": "请选择",
                                    "style": {
                                      "opacity": 1
                                    },
                                    "@change": "{{(...args) => {\n        return new $LogicEngineRuntime({\n          nodes: [{\"id\":\"ac50a348-2de2-489b-87bb-3a6e652ec665\",\"shape\":{\"name\":\"logic-start\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":380,\"y\":0},\"visible\":true,\"zIndex\":1},\"data\":{\"title\":\"开始\",\"value\":null}},{\"id\":\"66857794-fb11-4609-94b0-691682689276\",\"shape\":{\"name\":\"logic-end\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":380,\"y\":374.5},\"visible\":true,\"zIndex\":2},\"data\":{\"title\":\"结束\",\"value\":null}},{\"id\":\"fbf666cb-19fb-4c83-94a3-6dbd73c3256d\",\"shape\":{\"name\":\"logic-function\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":349,\"y\":199},\"visible\":true,\"zIndex\":3},\"data\":{\"title\":\"自定义函数\",\"value\":{\"code\":\"function logicNode() {\\n  const table = $designable.query('8gvu0iwifpj')\\n  if (table) {\\n    const vxeTableInstance = table.invoke('getVxeTableInstance')\\n\\n    vxeTableInstance.updateData()\\n  }\\n}\"}},\"executeFunction\":\"function logicNode() {\\n  const table = $designable.query('8gvu0iwifpj')\\n  if (table) {\\n    const vxeTableInstance = table.invoke('getVxeTableInstance')\\n\\n    vxeTableInstance.updateData()\\n  }\\n}\"},{\"id\":\"453d3c87-eb4e-400d-8793-ebf837ec48e3\",\"shape\":{\"name\":\"edge\",\"zIndex\":4,\"source\":{\"cell\":\"ac50a348-2de2-489b-87bb-3a6e652ec665\",\"port\":\"bottom\"},\"target\":{\"cell\":\"fbf666cb-19fb-4c83-94a3-6dbd73c3256d\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"2731e80c-42b7-41b4-81c0-555b8db3add9\",\"shape\":{\"name\":\"edge\",\"zIndex\":5,\"source\":{\"cell\":\"fbf666cb-19fb-4c83-94a3-6dbd73c3256d\",\"port\":\"bottom\"},\"target\":{\"cell\":\"66857794-fb11-4609-94b0-691682689276\",\"port\":\"top\"}},\"data\":{\"value\":null}}],\n          payload: args,\n          scope: {\n            ...$root,\n            $designPages: $designPages,\n          }\n        }).run()\n      }}}"
                                  },
                                  "x-validator": [],
                                  "enum": [],
                                  "description": "",
                                  "x-reactions": {
                                    "dependencies": [],
                                    "fulfill": {
                                      "state": {},
                                      "run": "\r\n\r\nconst effectFormOrgCategorys = $form.query('effectFormOrgCategorys').get('value')\r\n// 提取组织去重，并赋值到组织下拉框\r\nlet org = [] // 组织的数组\r\nif (effectFormOrgCategorys) {\r\n  effectFormOrgCategorys.forEach(e => {\r\n    let bol = org.find(el => el.orgId === e.orgId)\r\n    if (!bol) {\r\n      org.push({\r\n        label: e.orgName,\r\n        value: e.orgId,\r\n        orgId: e.orgId,\r\n        orgName: e.orgName,\r\n        orgCode: e.orgCode\r\n      })\r\n    }\r\n  })\r\n  $self.dataSource = org\r\n}"
                                    }
                                  },
                                  "x-designable-id": "d0p8k8bgx7d",
                                  "x-index": 0
                                }
                              }
                            },
                            "bvqkwy0030k": {
                              "type": "void",
                              "x-component": "FormGrid.GridColumn",
                              "x-designable-id": "bvqkwy0030k",
                              "x-index": 1,
                              "x-component-props": {
                                "gridSpan": 1,
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {},
                              "properties": {
                                "5kk6o8xpb4w": {
                                  "title": "品类名称",
                                  "x-decorator": "FormItem",
                                  "x-component": "Select",
                                  "x-validator": [],
                                  "x-component-props": {
                                    "size": "small",
                                    "multiple-limit": 0,
                                    "placeholder": "请选择",
                                    "style": {
                                      "opacity": 1
                                    },
                                    "@select": "{{(...args) => {\n        return new $LogicEngineRuntime({\n          nodes: [{\"id\":\"b67b2499-08e1-4b81-94a0-2facd58d9a65\",\"shape\":{\"name\":\"logic-start\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":505,\"y\":0},\"visible\":true,\"zIndex\":1},\"data\":{\"title\":\"开始\",\"value\":null}},{\"id\":\"daa353f6-27ac-4aab-a042-79bcb9acc5a6\",\"shape\":{\"name\":\"logic-end\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":505,\"y\":728.6999999999999},\"visible\":true,\"zIndex\":2},\"data\":{\"title\":\"结束\",\"value\":null}},{\"id\":\"ef06e8bb-a594-4288-ba9e-99a58d9af08f\",\"shape\":{\"name\":\"logic-function\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":490,\"y\":236},\"visible\":true,\"zIndex\":3},\"data\":{\"title\":\"自定义函数\",\"value\":{\"code\":\"function logicNode() {\\n  // $designable.query('8gvu0iwifpj').take(field => {\\n  //   const vxeTableInstance = field.invoke('getVxeTableInstance')\\n    \\n  //   const column = vxeTableInstance.getColumnByField('categoryName')\\n  //   if (!column.filters) {\\n  //     vxeTableInstance.setFilter(column, [\\n  //       { label: '', value: '', checked: true },\\n  //     ])\\n  //   }\\n\\n  //   vxeTableInstance.updateData()\\n  // })\\n  \\n}\"}},\"executeFunction\":\"function logicNode() {\\n  // $designable.query('8gvu0iwifpj').take(field => {\\n  //   const vxeTableInstance = field.invoke('getVxeTableInstance')\\n    \\n  //   const column = vxeTableInstance.getColumnByField('categoryName')\\n  //   if (!column.filters) {\\n  //     vxeTableInstance.setFilter(column, [\\n  //       { label: '', value: '', checked: true },\\n  //     ])\\n  //   }\\n\\n  //   vxeTableInstance.updateData()\\n  // })\\n  \\n}\"},{\"id\":\"b72dace7-aa6d-4fcb-b912-2cf1417373e7\",\"shape\":{\"name\":\"edge\",\"zIndex\":4,\"source\":{\"cell\":\"b67b2499-08e1-4b81-94a0-2facd58d9a65\",\"port\":\"bottom\"},\"target\":{\"cell\":\"ef06e8bb-a594-4288-ba9e-99a58d9af08f\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"56781730-bb83-4d7e-99f7-ccdb5a5a065b\",\"shape\":{\"name\":\"edge\",\"zIndex\":5,\"source\":{\"cell\":\"ef06e8bb-a594-4288-ba9e-99a58d9af08f\",\"port\":\"bottom\"},\"target\":{\"cell\":\"daa353f6-27ac-4aab-a042-79bcb9acc5a6\",\"port\":\"top\"}},\"data\":{\"value\":null}}],\n          payload: args,\n          scope: {\n            ...$root,\n            $designPages: $designPages,\n          }\n        }).run()\n      }}}",
                                    "@change": "{{(...args) => {\n        return new $LogicEngineRuntime({\n          nodes: [{\"id\":\"ff1d8313-6b5c-476b-8218-a9e78f484df6\",\"shape\":{\"name\":\"logic-start\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":431,\"y\":0},\"visible\":true,\"zIndex\":1},\"data\":{\"title\":\"开始\",\"value\":null}},{\"id\":\"e66718fa-1d0e-4415-b245-138db27073e2\",\"shape\":{\"name\":\"logic-end\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":431,\"y\":469.7},\"visible\":true,\"zIndex\":2},\"data\":{\"title\":\"结束\",\"value\":null}},{\"id\":\"f617d0f4-e2b6-423e-be43-b7ee5aeebdf3\",\"shape\":{\"name\":\"logic-function\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":416,\"y\":196},\"visible\":true,\"zIndex\":3},\"data\":{\"title\":\"自定义函数\",\"value\":{\"code\":\"function logicNode(ctx) {\\n  console.log('ctx')\\n  $form.query('.effectFormOrgCategorys').take()?.invoke('manualFilter', 'categoryName')\\n}\"}},\"executeFunction\":\"function logicNode(ctx) {\\n  console.log('ctx')\\n  $form.query('.effectFormOrgCategorys').take()?.invoke('manualFilter', 'categoryName')\\n}\"},{\"id\":\"909597bd-ca49-4b1a-a83e-22952e507690\",\"shape\":{\"name\":\"edge\",\"zIndex\":4,\"source\":{\"cell\":\"ff1d8313-6b5c-476b-8218-a9e78f484df6\",\"port\":\"bottom\"},\"target\":{\"cell\":\"f617d0f4-e2b6-423e-be43-b7ee5aeebdf3\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"741689e5-a959-4cc5-bc7a-f3e39b66ef70\",\"shape\":{\"name\":\"edge\",\"zIndex\":5,\"source\":{\"cell\":\"f617d0f4-e2b6-423e-be43-b7ee5aeebdf3\",\"port\":\"bottom\"},\"target\":{\"cell\":\"e66718fa-1d0e-4415-b245-138db27073e2\",\"port\":\"top\"}},\"data\":{\"value\":null}}],\n          payload: args,\n          scope: {\n            ...$root,\n            $designPages: $designPages,\n          }\n        }).run()\n      }}}"
                                  },
                                  "x-designer-extend": {
                                    "source": {
                                      "sourceType": "MQL",
                                      "enum": []
                                    },
                                    "events": [
                                      {
                                        "title": "触发表格过滤",
                                        "name": "select",
                                        "type": "logic",
                                        "args": [
                                          {
                                            "id": "b67b2499-08e1-4b81-94a0-2facd58d9a65",
                                            "shape": {
                                              "name": "logic-start",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 505,
                                                "y": 0
                                              },
                                              "visible": true,
                                              "zIndex": 1
                                            },
                                            "data": {
                                              "title": "开始",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "daa353f6-27ac-4aab-a042-79bcb9acc5a6",
                                            "shape": {
                                              "name": "logic-end",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 505,
                                                "y": 728.6999999999999
                                              },
                                              "visible": true,
                                              "zIndex": 2
                                            },
                                            "data": {
                                              "title": "结束",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "ef06e8bb-a594-4288-ba9e-99a58d9af08f",
                                            "shape": {
                                              "name": "logic-function",
                                              "size": {
                                                "width": 86,
                                                "height": 30
                                              },
                                              "position": {
                                                "x": 490,
                                                "y": 236
                                              },
                                              "visible": true,
                                              "zIndex": 3
                                            },
                                            "data": {
                                              "title": "自定义函数",
                                              "value": {
                                                "code": "function logicNode() {\n  // $designable.query('8gvu0iwifpj').take(field => {\n  //   const vxeTableInstance = field.invoke('getVxeTableInstance')\n    \n  //   const column = vxeTableInstance.getColumnByField('categoryName')\n  //   if (!column.filters) {\n  //     vxeTableInstance.setFilter(column, [\n  //       { label: '', value: '', checked: true },\n  //     ])\n  //   }\n\n  //   vxeTableInstance.updateData()\n  // })\n  \n}"
                                              }
                                            },
                                            "executeFunction": "function logicNode() {\n  // $designable.query('8gvu0iwifpj').take(field => {\n  //   const vxeTableInstance = field.invoke('getVxeTableInstance')\n    \n  //   const column = vxeTableInstance.getColumnByField('categoryName')\n  //   if (!column.filters) {\n  //     vxeTableInstance.setFilter(column, [\n  //       { label: '', value: '', checked: true },\n  //     ])\n  //   }\n\n  //   vxeTableInstance.updateData()\n  // })\n  \n}"
                                          },
                                          {
                                            "id": "b72dace7-aa6d-4fcb-b912-2cf1417373e7",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 4,
                                              "source": {
                                                "cell": "b67b2499-08e1-4b81-94a0-2facd58d9a65",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "ef06e8bb-a594-4288-ba9e-99a58d9af08f",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "56781730-bb83-4d7e-99f7-ccdb5a5a065b",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 5,
                                              "source": {
                                                "cell": "ef06e8bb-a594-4288-ba9e-99a58d9af08f",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "daa353f6-27ac-4aab-a042-79bcb9acc5a6",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          }
                                        ]
                                      },
                                      {
                                        "title": "值改变事件",
                                        "name": "change",
                                        "type": "logic",
                                        "args": [
                                          {
                                            "id": "ff1d8313-6b5c-476b-8218-a9e78f484df6",
                                            "shape": {
                                              "name": "logic-start",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 431,
                                                "y": 0
                                              },
                                              "visible": true,
                                              "zIndex": 1
                                            },
                                            "data": {
                                              "title": "开始",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "e66718fa-1d0e-4415-b245-138db27073e2",
                                            "shape": {
                                              "name": "logic-end",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 431,
                                                "y": 469.7
                                              },
                                              "visible": true,
                                              "zIndex": 2
                                            },
                                            "data": {
                                              "title": "结束",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "f617d0f4-e2b6-423e-be43-b7ee5aeebdf3",
                                            "shape": {
                                              "name": "logic-function",
                                              "size": {
                                                "width": 86,
                                                "height": 30
                                              },
                                              "position": {
                                                "x": 416,
                                                "y": 196
                                              },
                                              "visible": true,
                                              "zIndex": 3
                                            },
                                            "data": {
                                              "title": "自定义函数",
                                              "value": {
                                                "code": "function logicNode(ctx) {\n  console.log('ctx')\n  $form.query('.effectFormOrgCategorys').take()?.invoke('manualFilter', 'categoryName')\n}"
                                              }
                                            },
                                            "executeFunction": "function logicNode(ctx) {\n  console.log('ctx')\n  $form.query('.effectFormOrgCategorys').take()?.invoke('manualFilter', 'categoryName')\n}"
                                          },
                                          {
                                            "id": "909597bd-ca49-4b1a-a83e-22952e507690",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 4,
                                              "source": {
                                                "cell": "ff1d8313-6b5c-476b-8218-a9e78f484df6",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "f617d0f4-e2b6-423e-be43-b7ee5aeebdf3",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "741689e5-a959-4cc5-bc7a-f3e39b66ef70",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 5,
                                              "source": {
                                                "cell": "f617d0f4-e2b6-423e-be43-b7ee5aeebdf3",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "e66718fa-1d0e-4415-b245-138db27073e2",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          }
                                        ]
                                      }
                                    ]
                                  },
                                  "enum": [],
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "loose"
                                  },
                                  "x-designable-id": "5kk6o8xpb4w",
                                  "x-index": 0,
                                  "x-reactions": {
                                    "dependencies": [],
                                    "fulfill": {
                                      "state": {},
                                      "run": "const effectFormOrgCategorys = $form.query('effectFormOrgCategorys').get('value')\r\n// 提取组织去重，并赋值到组织下拉框\r\nlet org = [] // 组织的数组\r\nif (effectFormOrgCategorys) {\r\n  effectFormOrgCategorys.forEach(e => {\r\n    let bol = org.find(el => el.categoryId === e.categoryId)\r\n    if (!bol) {\r\n      org.push({\r\n        label: e.categoryName,\r\n        value: e.categoryId,\r\n      })\r\n    }\r\n  })\r\n  $self.dataSource = org\r\n}"
                                    }
                                  }
                                }
                              }
                            }
                          }
                        },
                        "effectFormOrgCategorys": {
                          "type": "array",
                          "x-decorator": "FormItem",
                          "x-component": "RenderTable",
                          "x-validator": [],
                          "x-decorator-props": {
                            "style": "padding: 0 20px;",
                            "feedbackLayout": "loose"
                          },
                          "x-designable-id": "8gvu0iwifpj",
                          "x-component-props": {
                            "stripe": false,
                            "border": true,
                            "show-header": true,
                            "size": "small",
                            "style": "width:100%",
                            "preColumns": [
                              "seq"
                            ],
                            "editMode": false,
                            "primaryKey": "id",
                            "cascadeDeletion": true,
                            "height": "300px",
                            "pagination": true,
                            "showHeader": true,
                            "openCustomTable": false,
                            "dblclickEditable": false
                          },
                          "x-designer-extend": {
                            "page": {
                              "pagination": true
                            },
                            "events": []
                          },
                          "x-index": 1,
                          "name": "effectFormOrgCategorys",
                          "x-reactions": {
                            "dependencies": [],
                            "fulfill": {
                              "state": {
                                "componentProps": "{{{\n  ...$self.componentProps,\n    filterConfig: {\n    showIcon: false,\n    filterMethod: (params) => { \n      const categoryId = $designable.query('5kk6o8xpb4w').get('value')\n      const orgId = $designable.query('d0p8k8bgx7d').get('value')\n\n      const row = $self.value[params.rowIndex]\n\n      return (categoryId && !orgId) || (!categoryId && orgId)\n        ? row.categoryId === categoryId || row.orgId === orgId\n        : row.categoryId === categoryId && row.orgId === orgId\n    }\n  }\n}}}"
                              }
                            }
                          },
                          "properties": {
                            "orgName": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "width": 150,
                                "title": "业务实体",
                                "sortable": true,
                                "resizable": true,
                                "id": "myzgmrmu7j4",
                                "visible": true,
                                "isGroup": false,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-index": 0,
                              "name": "orgName",
                              "x-designable-id": "myzgmrmu7j4",
                              "x-display": "visible",
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {},
                              "properties": {
                                "orgName": {
                                  "type": "string",
                                  "x-component": "Input",
                                  "x-decorator": "FormItem",
                                  "x-component-props": {
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-designer-extend": {
                                    "model": {
                                      "businessType": "sccSupEffectFormOrgCategory",
                                      "reference": null,
                                      "modelId": "1668183595776528388",
                                      "field": "orgName",
                                      "originalField": "orgName"
                                    },
                                    "events": []
                                  },
                                  "x-index": 0,
                                  "name": "orgName",
                                  "x-designable-id": "w05a2hdyf43",
                                  "x-validator": [],
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "x-pattern": "readPretty"
                                }
                              }
                            },
                            "categoryName": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "title": "品类",
                                "sortable": true,
                                "resizable": true,
                                "visible": true,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                },
                                "id": "jn4u8yfdqyo",
                                "isGroup": false,
                                "minWidth": 150,
                                "filterType": "radio"
                              },
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {},
                              "x-index": 1,
                              "x-designable-id": "jn4u8yfdqyo",
                              "x-display": "visible",
                              "name": "categoryName",
                              "properties": {
                                "categoryName": {
                                  "type": "string",
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "Input",
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "x-designer-extend": {
                                    "events": []
                                  },
                                  "x-component-props": {
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-validator": [],
                                  "x-pattern": "readPretty",
                                  "x-designable-id": "nadz4pr2ab4",
                                  "x-index": 0,
                                  "name": "categoryName"
                                }
                              }
                            },
                            "siteFormId": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "width": 150,
                                "title": "现场评审单ID",
                                "sortable": true,
                                "resizable": true,
                                "id": "qml4a0cbwts",
                                "visible": false,
                                "isGroup": false
                              },
                              "x-index": 2,
                              "name": "siteFormId",
                              "x-designable-id": "qml4a0cbwts",
                              "x-display": "hidden",
                              "properties": {
                                "siteFormId": {
                                  "type": "number",
                                  "x-component": "InputNumber",
                                  "x-decorator": "FormItem",
                                  "x-component-props": {
                                    "controls-position": "right",
                                    "controls": true
                                  },
                                  "x-designer-extend": {
                                    "model": {
                                      "businessType": "sccSupEffectFormOrgCategory",
                                      "reference": null,
                                      "modelId": "1668183595776528388",
                                      "field": "siteFormId",
                                      "originalField": "siteFormId"
                                    }
                                  },
                                  "x-index": 0,
                                  "name": "siteFormId",
                                  "x-designable-id": "cnkib5yedo3"
                                }
                              }
                            },
                            "siteFormNumber": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "width": 150,
                                "title": "现场评审单号",
                                "sortable": true,
                                "resizable": true,
                                "id": "egort5vtwbr",
                                "visible": true,
                                "isGroup": false,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-index": 3,
                              "name": "siteFormNumber",
                              "x-designable-id": "egort5vtwbr",
                              "x-display": "visible",
                              "properties": {
                                "siteFormNumber": {
                                  "type": "string",
                                  "x-decorator": "FormItem",
                                  "x-component": "Link",
                                  "x-component-props": {
                                    "href": "-",
                                    "type": "primary",
                                    "underline": true,
                                    "style": {
                                      "opacity": 1
                                    },
                                    "@click": "{{(...args) => {\n        return new $LogicEngineRuntime({\n          nodes: [{\"id\":\"0731bb7a-dcdd-4d38-bbdb-81e500aaf009\",\"shape\":{\"name\":\"logic-start\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":460,\"y\":0},\"visible\":true,\"zIndex\":1},\"data\":{\"title\":\"开始\",\"value\":null}},{\"id\":\"899733a0-4c41-4055-a0d2-af9ba5b55b90\",\"shape\":{\"name\":\"logic-end\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":460,\"y\":421.4},\"visible\":true,\"zIndex\":2},\"data\":{\"title\":\"结束\",\"value\":null}},{\"id\":\"4fe3e9b8-6f1a-4009-a8c8-24886a30ebd8\",\"shape\":{\"name\":\"logic-function\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":445,\"y\":189},\"visible\":true,\"zIndex\":3},\"data\":{\"title\":\"自定义函数\",\"value\":{\"code\":\"function logicNode(ctx) {\\n  console.log('ctx')\\n  const row = $table.getRowByIndex($self.index)\\n  $form.query('tabs').take((field) => {\\n    const name = row.sampleNumber\\n    field.invoke('addTab', {\\n      name,\\n      label: name,\\n      closable: true,\\n      component: sampleConfirmed,\\n      attrs: {\\n        // content: '我是动态传入的内容',\\n        params: {\\n          row,\\n          flag: 'view',\\n          sampleId: row.sampleId\\n        },\\n        closeTabPane: () => {\\n          field.invoke('removeTab', name)\\n        },\\n      },\\n      props: {\\n        readOnly: true,\\n      },\\n    })\\n  })\\n}\"}},\"executeFunction\":\"function logicNode(ctx) {\\n  console.log('ctx')\\n  const row = $table.getRowByIndex($self.index)\\n  $form.query('tabs').take((field) => {\\n    const name = row.sampleNumber\\n    field.invoke('addTab', {\\n      name,\\n      label: name,\\n      closable: true,\\n      component: sampleConfirmed,\\n      attrs: {\\n        // content: '我是动态传入的内容',\\n        params: {\\n          row,\\n          flag: 'view',\\n          sampleId: row.sampleId\\n        },\\n        closeTabPane: () => {\\n          field.invoke('removeTab', name)\\n        },\\n      },\\n      props: {\\n        readOnly: true,\\n      },\\n    })\\n  })\\n}\"},{\"id\":\"e787f980-6cc2-4c0b-ab64-4433cc43e34f\",\"shape\":{\"name\":\"edge\",\"zIndex\":4,\"source\":{\"cell\":\"0731bb7a-dcdd-4d38-bbdb-81e500aaf009\",\"port\":\"bottom\"},\"target\":{\"cell\":\"4fe3e9b8-6f1a-4009-a8c8-24886a30ebd8\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"b9b4bfa9-1add-4d04-bc97-ad15b63ee771\",\"shape\":{\"name\":\"edge\",\"zIndex\":5,\"source\":{\"cell\":\"4fe3e9b8-6f1a-4009-a8c8-24886a30ebd8\",\"port\":\"bottom\"},\"target\":{\"cell\":\"899733a0-4c41-4055-a0d2-af9ba5b55b90\",\"port\":\"top\"}},\"data\":{\"value\":null}}],\n          payload: args,\n          scope: {\n            ...$root,\n            $designPages: $designPages,\n          }\n        }).run()\n      }}}"
                                  },
                                  "x-read-pretty": false,
                                  "x-designer-extend": {
                                    "events": [
                                      {
                                        "title": "点击事件",
                                        "name": "click",
                                        "type": "logic",
                                        "args": [
                                          {
                                            "id": "0731bb7a-dcdd-4d38-bbdb-81e500aaf009",
                                            "shape": {
                                              "name": "logic-start",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 460,
                                                "y": 0
                                              },
                                              "visible": true,
                                              "zIndex": 1
                                            },
                                            "data": {
                                              "title": "开始",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "899733a0-4c41-4055-a0d2-af9ba5b55b90",
                                            "shape": {
                                              "name": "logic-end",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 460,
                                                "y": 421.4
                                              },
                                              "visible": true,
                                              "zIndex": 2
                                            },
                                            "data": {
                                              "title": "结束",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "4fe3e9b8-6f1a-4009-a8c8-24886a30ebd8",
                                            "shape": {
                                              "name": "logic-function",
                                              "size": {
                                                "width": 86,
                                                "height": 30
                                              },
                                              "position": {
                                                "x": 445,
                                                "y": 189
                                              },
                                              "visible": true,
                                              "zIndex": 3
                                            },
                                            "data": {
                                              "title": "自定义函数",
                                              "value": {
                                                "code": "function logicNode(ctx) {\n  console.log('ctx')\n  const row = $table.getRowByIndex($self.index)\n  $form.query('tabs').take((field) => {\n    const name = row.sampleNumber\n    field.invoke('addTab', {\n      name,\n      label: name,\n      closable: true,\n      component: sampleConfirmed,\n      attrs: {\n        // content: '我是动态传入的内容',\n        params: {\n          row,\n          flag: 'view',\n          sampleId: row.sampleId\n        },\n        closeTabPane: () => {\n          field.invoke('removeTab', name)\n        },\n      },\n      props: {\n        readOnly: true,\n      },\n    })\n  })\n}"
                                              }
                                            },
                                            "executeFunction": "function logicNode(ctx) {\n  console.log('ctx')\n  const row = $table.getRowByIndex($self.index)\n  $form.query('tabs').take((field) => {\n    const name = row.sampleNumber\n    field.invoke('addTab', {\n      name,\n      label: name,\n      closable: true,\n      component: sampleConfirmed,\n      attrs: {\n        // content: '我是动态传入的内容',\n        params: {\n          row,\n          flag: 'view',\n          sampleId: row.sampleId\n        },\n        closeTabPane: () => {\n          field.invoke('removeTab', name)\n        },\n      },\n      props: {\n        readOnly: true,\n      },\n    })\n  })\n}"
                                          },
                                          {
                                            "id": "e787f980-6cc2-4c0b-ab64-4433cc43e34f",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 4,
                                              "source": {
                                                "cell": "0731bb7a-dcdd-4d38-bbdb-81e500aaf009",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "4fe3e9b8-6f1a-4009-a8c8-24886a30ebd8",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "b9b4bfa9-1add-4d04-bc97-ad15b63ee771",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 5,
                                              "source": {
                                                "cell": "4fe3e9b8-6f1a-4009-a8c8-24886a30ebd8",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "899733a0-4c41-4055-a0d2-af9ba5b55b90",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          }
                                        ]
                                      }
                                    ]
                                  },
                                  "x-validator": [],
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "name": "siteFormNumber",
                                  "x-designable-id": "6wi90wnxzn8",
                                  "x-index": 0
                                }
                              }
                            },
                            "sampleId": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "width": 150,
                                "title": "样品确认单ID",
                                "sortable": true,
                                "resizable": true,
                                "id": "wg630spnzhx",
                                "visible": false,
                                "isGroup": false
                              },
                              "x-index": 4,
                              "name": "sampleId",
                              "x-designable-id": "wg630spnzhx",
                              "x-display": "hidden",
                              "properties": {
                                "sampleId": {
                                  "type": "number",
                                  "x-component": "InputNumber",
                                  "x-decorator": "FormItem",
                                  "x-component-props": {
                                    "controls-position": "right",
                                    "controls": true
                                  },
                                  "x-designer-extend": {
                                    "model": {
                                      "businessType": "sccSupEffectFormOrgCategory",
                                      "reference": null,
                                      "modelId": "1668183595776528388",
                                      "field": "sampleId",
                                      "originalField": "sampleId"
                                    }
                                  },
                                  "x-index": 0,
                                  "name": "sampleId",
                                  "x-designable-id": "3yjte6fnmuo"
                                }
                              }
                            },
                            "sampleNumber": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "width": 150,
                                "title": "样品确认单号",
                                "sortable": true,
                                "resizable": true,
                                "id": "9po36apjz6h",
                                "visible": true,
                                "isGroup": false,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-index": 5,
                              "name": "sampleNumber",
                              "x-designable-id": "9po36apjz6h",
                              "x-display": "visible",
                              "properties": {
                                "sampleNumber": {
                                  "type": "string",
                                  "x-decorator": "FormItem",
                                  "x-component": "Link",
                                  "x-component-props": {
                                    "href": "-",
                                    "type": "primary",
                                    "underline": true,
                                    "style": {
                                      "opacity": 1
                                    },
                                    "@click": "{{(...args) => {\n        return new $LogicEngineRuntime({\n          nodes: [{\"id\":\"0731bb7a-dcdd-4d38-bbdb-81e500aaf009\",\"shape\":{\"name\":\"logic-start\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":460,\"y\":0},\"visible\":true,\"zIndex\":1},\"data\":{\"title\":\"开始\",\"value\":null}},{\"id\":\"899733a0-4c41-4055-a0d2-af9ba5b55b90\",\"shape\":{\"name\":\"logic-end\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":460,\"y\":421.4},\"visible\":true,\"zIndex\":2},\"data\":{\"title\":\"结束\",\"value\":null}},{\"id\":\"4fe3e9b8-6f1a-4009-a8c8-24886a30ebd8\",\"shape\":{\"name\":\"logic-function\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":445,\"y\":189},\"visible\":true,\"zIndex\":3},\"data\":{\"title\":\"自定义函数\",\"value\":{\"code\":\"function logicNode(ctx) {\\n  console.log('ctx')\\n  const row = $table.getRowByIndex($self.index)\\n  $form.query('tabs').take((field) => {\\n    const name = '自定义组件'\\n    field.invoke('addTab', {\\n      name,\\n      label: row.sampleNumber,\\n      closable: true,\\n      component: '样品确认跳转',\\n      attrs: {\\n        content: '我是动态传入的内容',\\n        params: {\\n          row,\\n          flag: 'view',\\n          sampleId: row.sampleId\\n        },\\n        closeTabPane: () => {\\n          field.invoke('removeTab', name)\\n        },\\n      },\\n      props: {\\n        readOnly: true,\\n      },\\n    })\\n  })\\n}\"}},\"executeFunction\":\"function logicNode(ctx) {\\n  console.log('ctx')\\n  const row = $table.getRowByIndex($self.index)\\n  $form.query('tabs').take((field) => {\\n    const name = '自定义组件'\\n    field.invoke('addTab', {\\n      name,\\n      label: row.sampleNumber,\\n      closable: true,\\n      component: '样品确认跳转',\\n      attrs: {\\n        content: '我是动态传入的内容',\\n        params: {\\n          row,\\n          flag: 'view',\\n          sampleId: row.sampleId\\n        },\\n        closeTabPane: () => {\\n          field.invoke('removeTab', name)\\n        },\\n      },\\n      props: {\\n        readOnly: true,\\n      },\\n    })\\n  })\\n}\"},{\"id\":\"e787f980-6cc2-4c0b-ab64-4433cc43e34f\",\"shape\":{\"name\":\"edge\",\"zIndex\":4,\"source\":{\"cell\":\"0731bb7a-dcdd-4d38-bbdb-81e500aaf009\",\"port\":\"bottom\"},\"target\":{\"cell\":\"4fe3e9b8-6f1a-4009-a8c8-24886a30ebd8\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"b9b4bfa9-1add-4d04-bc97-ad15b63ee771\",\"shape\":{\"name\":\"edge\",\"zIndex\":5,\"source\":{\"cell\":\"4fe3e9b8-6f1a-4009-a8c8-24886a30ebd8\",\"port\":\"bottom\"},\"target\":{\"cell\":\"899733a0-4c41-4055-a0d2-af9ba5b55b90\",\"port\":\"top\"}},\"data\":{\"value\":null}}],\n          payload: args,\n          scope: {\n            ...$root,\n            $designPages: $designPages,\n          }\n        }).run()\n      }}}"
                                  },
                                  "x-read-pretty": false,
                                  "x-designer-extend": {
                                    "events": [
                                      {
                                        "title": "点击事件",
                                        "name": "click",
                                        "type": "logic",
                                        "args": [
                                          {
                                            "id": "0731bb7a-dcdd-4d38-bbdb-81e500aaf009",
                                            "shape": {
                                              "name": "logic-start",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 460,
                                                "y": 0
                                              },
                                              "visible": true,
                                              "zIndex": 1
                                            },
                                            "data": {
                                              "title": "开始",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "899733a0-4c41-4055-a0d2-af9ba5b55b90",
                                            "shape": {
                                              "name": "logic-end",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 460,
                                                "y": 421.4
                                              },
                                              "visible": true,
                                              "zIndex": 2
                                            },
                                            "data": {
                                              "title": "结束",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "4fe3e9b8-6f1a-4009-a8c8-24886a30ebd8",
                                            "shape": {
                                              "name": "logic-function",
                                              "size": {
                                                "width": 86,
                                                "height": 30
                                              },
                                              "position": {
                                                "x": 445,
                                                "y": 189
                                              },
                                              "visible": true,
                                              "zIndex": 3
                                            },
                                            "data": {
                                              "title": "自定义函数",
                                              "value": {
                                                "code": "function logicNode(ctx) {\n  console.log('ctx')\n  const row = $table.getRowByIndex($self.index)\n  $form.query('tabs').take((field) => {\n    const name = '自定义组件'\n    field.invoke('addTab', {\n      name,\n      label: row.sampleNumber,\n      closable: true,\n      component: '样品确认跳转',\n      attrs: {\n        content: '我是动态传入的内容',\n        params: {\n          row,\n          flag: 'view',\n          sampleId: row.sampleId\n        },\n        closeTabPane: () => {\n          field.invoke('removeTab', name)\n        },\n      },\n      props: {\n        readOnly: true,\n      },\n    })\n  })\n}"
                                              }
                                            },
                                            "executeFunction": "function logicNode(ctx) {\n  console.log('ctx')\n  const row = $table.getRowByIndex($self.index)\n  $form.query('tabs').take((field) => {\n    const name = '自定义组件'\n    field.invoke('addTab', {\n      name,\n      label: row.sampleNumber,\n      closable: true,\n      component: '样品确认跳转',\n      attrs: {\n        content: '我是动态传入的内容',\n        params: {\n          row,\n          flag: 'view',\n          sampleId: row.sampleId\n        },\n        closeTabPane: () => {\n          field.invoke('removeTab', name)\n        },\n      },\n      props: {\n        readOnly: true,\n      },\n    })\n  })\n}"
                                          },
                                          {
                                            "id": "e787f980-6cc2-4c0b-ab64-4433cc43e34f",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 4,
                                              "source": {
                                                "cell": "0731bb7a-dcdd-4d38-bbdb-81e500aaf009",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "4fe3e9b8-6f1a-4009-a8c8-24886a30ebd8",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "b9b4bfa9-1add-4d04-bc97-ad15b63ee771",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 5,
                                              "source": {
                                                "cell": "4fe3e9b8-6f1a-4009-a8c8-24886a30ebd8",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "899733a0-4c41-4055-a0d2-af9ba5b55b90",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          }
                                        ]
                                      }
                                    ]
                                  },
                                  "x-validator": [],
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "name": "sampleNumber",
                                  "x-designable-id": "bp9hcaguqym",
                                  "x-index": 0
                                }
                              }
                            },
                            "materialTrialId": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "width": 150,
                                "title": "物料试用单ID",
                                "sortable": true,
                                "resizable": true,
                                "id": "yinvz1pxwj3",
                                "visible": false,
                                "isGroup": false
                              },
                              "x-index": 6,
                              "name": "materialTrialId",
                              "x-designable-id": "yinvz1pxwj3",
                              "x-display": "hidden",
                              "properties": {
                                "materialTrialId": {
                                  "type": "number",
                                  "x-component": "InputNumber",
                                  "x-decorator": "FormItem",
                                  "x-component-props": {
                                    "controls-position": "right",
                                    "controls": true
                                  },
                                  "x-designer-extend": {
                                    "model": {
                                      "businessType": "sccSupEffectFormOrgCategory",
                                      "reference": null,
                                      "modelId": "1668183595776528388",
                                      "field": "materialTrialId",
                                      "originalField": "materialTrialId"
                                    }
                                  },
                                  "x-index": 0,
                                  "name": "materialTrialId",
                                  "x-designable-id": "3l7ukfbcwgn"
                                }
                              }
                            },
                            "materialTrialNumber": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "width": 150,
                                "title": "物料试用单单号",
                                "sortable": true,
                                "resizable": true,
                                "id": "aicw8wwczqy",
                                "visible": true,
                                "isGroup": false,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-index": 7,
                              "name": "materialTrialNumber",
                              "x-designable-id": "aicw8wwczqy",
                              "x-display": "visible",
                              "properties": {
                                "materialTrialNumber": {
                                  "type": "string",
                                  "x-decorator": "FormItem",
                                  "x-component": "Link",
                                  "x-component-props": {
                                    "href": "-",
                                    "type": "primary",
                                    "underline": true,
                                    "style": {
                                      "opacity": 1
                                    },
                                    "@click": "{{(...args) => {\n        return new $LogicEngineRuntime({\n          nodes: [{\"id\":\"8eb26476-e37b-4115-8552-5de1a5d1d9ce\",\"shape\":{\"name\":\"logic-start\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":460,\"y\":0},\"visible\":true,\"zIndex\":1},\"data\":{\"title\":\"开始\",\"value\":null}},{\"id\":\"94a918c8-5ced-4542-b7d6-7d64d9985aec\",\"shape\":{\"name\":\"logic-end\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":460,\"y\":421.4},\"visible\":true,\"zIndex\":2},\"data\":{\"title\":\"结束\",\"value\":null}},{\"id\":\"6eab6029-3526-4ad0-b387-c7e30e3b0c37\",\"shape\":{\"name\":\"logic-function\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":445,\"y\":170},\"visible\":true,\"zIndex\":3},\"data\":{\"title\":\"自定义函数\",\"value\":{\"code\":\"function logicNode(ctx) {\\n  console.log('ctx')\\n  const row = $table.getRowByIndex($self.index)\\n  $form.query('tabs').take((field) => {\\n    const name = '自定义组件'\\n    field.invoke('addTab', {\\n      name,\\n      label: row.trialNumber,\\n      closable: true,\\n      component: '物料试用跳转',\\n      attrs: {\\n        content: '我是动态传入的内容',\\n        params: {\\n          row,\\n          flag: 'view',\\n          materialTrialId: row.materialTrialId\\n        },\\n        closeTabPane: () => {\\n          field.invoke('removeTab', name)\\n        },\\n      },\\n      props: {\\n        readOnly: true,\\n      },\\n    })\\n  })\\n}\"}},\"executeFunction\":\"function logicNode(ctx) {\\n  console.log('ctx')\\n  const row = $table.getRowByIndex($self.index)\\n  $form.query('tabs').take((field) => {\\n    const name = '自定义组件'\\n    field.invoke('addTab', {\\n      name,\\n      label: row.trialNumber,\\n      closable: true,\\n      component: '物料试用跳转',\\n      attrs: {\\n        content: '我是动态传入的内容',\\n        params: {\\n          row,\\n          flag: 'view',\\n          materialTrialId: row.materialTrialId\\n        },\\n        closeTabPane: () => {\\n          field.invoke('removeTab', name)\\n        },\\n      },\\n      props: {\\n        readOnly: true,\\n      },\\n    })\\n  })\\n}\"},{\"id\":\"8aba159d-19f8-4dc1-9981-39dd138938cf\",\"shape\":{\"name\":\"edge\",\"zIndex\":4,\"source\":{\"cell\":\"8eb26476-e37b-4115-8552-5de1a5d1d9ce\",\"port\":\"bottom\"},\"target\":{\"cell\":\"6eab6029-3526-4ad0-b387-c7e30e3b0c37\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"fd50eb9b-c7f2-4a35-9954-ac006d8f3193\",\"shape\":{\"name\":\"edge\",\"zIndex\":5,\"source\":{\"cell\":\"6eab6029-3526-4ad0-b387-c7e30e3b0c37\",\"port\":\"bottom\"},\"target\":{\"cell\":\"94a918c8-5ced-4542-b7d6-7d64d9985aec\",\"port\":\"top\"}},\"data\":{\"value\":null}}],\n          payload: args,\n          scope: {\n            ...$root,\n            $designPages: $designPages,\n          }\n        }).run()\n      }}}"
                                  },
                                  "x-read-pretty": false,
                                  "x-designer-extend": {
                                    "events": [
                                      {
                                        "title": "物料试用点击",
                                        "name": "click",
                                        "type": "logic",
                                        "args": [
                                          {
                                            "id": "8eb26476-e37b-4115-8552-5de1a5d1d9ce",
                                            "shape": {
                                              "name": "logic-start",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 460,
                                                "y": 0
                                              },
                                              "visible": true,
                                              "zIndex": 1
                                            },
                                            "data": {
                                              "title": "开始",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "94a918c8-5ced-4542-b7d6-7d64d9985aec",
                                            "shape": {
                                              "name": "logic-end",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 460,
                                                "y": 421.4
                                              },
                                              "visible": true,
                                              "zIndex": 2
                                            },
                                            "data": {
                                              "title": "结束",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "6eab6029-3526-4ad0-b387-c7e30e3b0c37",
                                            "shape": {
                                              "name": "logic-function",
                                              "size": {
                                                "width": 86,
                                                "height": 30
                                              },
                                              "position": {
                                                "x": 445,
                                                "y": 170
                                              },
                                              "visible": true,
                                              "zIndex": 3
                                            },
                                            "data": {
                                              "title": "自定义函数",
                                              "value": {
                                                "code": "function logicNode(ctx) {\n  console.log('ctx')\n  const row = $table.getRowByIndex($self.index)\n  $form.query('tabs').take((field) => {\n    const name = '自定义组件'\n    field.invoke('addTab', {\n      name,\n      label: row.trialNumber,\n      closable: true,\n      component: '物料试用跳转',\n      attrs: {\n        content: '我是动态传入的内容',\n        params: {\n          row,\n          flag: 'view',\n          materialTrialId: row.materialTrialId\n        },\n        closeTabPane: () => {\n          field.invoke('removeTab', name)\n        },\n      },\n      props: {\n        readOnly: true,\n      },\n    })\n  })\n}"
                                              }
                                            },
                                            "executeFunction": "function logicNode(ctx) {\n  console.log('ctx')\n  const row = $table.getRowByIndex($self.index)\n  $form.query('tabs').take((field) => {\n    const name = '自定义组件'\n    field.invoke('addTab', {\n      name,\n      label: row.trialNumber,\n      closable: true,\n      component: '物料试用跳转',\n      attrs: {\n        content: '我是动态传入的内容',\n        params: {\n          row,\n          flag: 'view',\n          materialTrialId: row.materialTrialId\n        },\n        closeTabPane: () => {\n          field.invoke('removeTab', name)\n        },\n      },\n      props: {\n        readOnly: true,\n      },\n    })\n  })\n}"
                                          },
                                          {
                                            "id": "8aba159d-19f8-4dc1-9981-39dd138938cf",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 4,
                                              "source": {
                                                "cell": "8eb26476-e37b-4115-8552-5de1a5d1d9ce",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "6eab6029-3526-4ad0-b387-c7e30e3b0c37",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "fd50eb9b-c7f2-4a35-9954-ac006d8f3193",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 5,
                                              "source": {
                                                "cell": "6eab6029-3526-4ad0-b387-c7e30e3b0c37",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "94a918c8-5ced-4542-b7d6-7d64d9985aec",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          }
                                        ]
                                      }
                                    ]
                                  },
                                  "x-validator": [],
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "name": "materialTrialNumber",
                                  "x-designable-id": "lk2rl0e7qj9",
                                  "x-index": 0
                                }
                              }
                            },
                            "companyStatus": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "width": 150,
                                "title": "供应商状态",
                                "sortable": true,
                                "resizable": true,
                                "id": "ziuhtlyfqv2",
                                "visible": true,
                                "isGroup": false
                              },
                              "x-index": 8,
                              "name": "companyStatus",
                              "x-designable-id": "ziuhtlyfqv2",
                              "x-display": "visible",
                              "properties": {
                                "companyStatus": {
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "Select",
                                  "x-validator": [],
                                  "x-component-props": {
                                    "size": "small",
                                    "multiple-limit": 0,
                                    "placeholder": "请选择",
                                    "style": {
                                      "opacity": 1
                                    },
                                    "fieldNames": {
                                      "label": "dictItemName",
                                      "value": "dictItemCode"
                                    },
                                    "@created": "{{() => {\n                    $queryEngine.request.baseRequest({\n                      type: 'base_dict_item_ide',\n                      action: 'query',\n                      payload: {\n                        filter: {\"$and\":{\"dictId\":{\"eq\":\"347786185572480\"}}},\n                        page: {\"pageSize\":9999,\"pageNum\":1,\"sort\":\"dictItemNo asc\"},\n                      },\n                      query: {\"*\":{}},\n                      actionConfig: { service: 'api-base' },\n                      queryConfig: { staleTime: 300000 }\n                    }).then(res => {\n                      $self.dataSource = res.data || []\n                    })\n                  }}}"
                                  },
                                  "x-designer-extend": {
                                    "source": {
                                      "sourceType": "MQL",
                                      "enum": [],
                                      "action": "query",
                                      "type": "base_dict_item_ide",
                                      "query": {
                                        "*": {}
                                      },
                                      "filter": {
                                        "$and": {
                                          "dictId": {
                                            "eq": "347786185572480"
                                          }
                                        }
                                      },
                                      "service": "api-base",
                                      "payload": {
                                        "page": {
                                          "sort": "dictItemNo asc"
                                        }
                                      },
                                      "conditions": [
                                        [
                                          {
                                            "dataName": "dictId",
                                            "comparison": "eq",
                                            "valueType": "fixed",
                                            "modelField": "347786185572480",
                                            "modelId": ""
                                          }
                                        ]
                                      ]
                                    },
                                    "events": []
                                  },
                                  "enum": [],
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "x-designable-id": "j9bbx52r0ef",
                                  "x-index": 0,
                                  "name": "companyStatus"
                                }
                              }
                            },
                            "serviceStatus": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "width": 150,
                                "title": "品类状态",
                                "sortable": true,
                                "resizable": true,
                                "id": "f4v886bblr8",
                                "visible": true,
                                "isGroup": false,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-index": 9,
                              "name": "serviceStatus",
                              "x-designable-id": "f4v886bblr8",
                              "x-display": "visible",
                              "properties": {
                                "serviceStatus": {
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "Select",
                                  "x-validator": [],
                                  "x-component-props": {
                                    "size": "small",
                                    "multiple-limit": 0,
                                    "placeholder": "请选择",
                                    "style": {
                                      "opacity": 1
                                    },
                                    "fieldNames": {
                                      "label": "dictItemName",
                                      "value": "dictItemCode"
                                    },
                                    "@created": "{{() => {\n                    $queryEngine.request.baseRequest({\n                      type: 'base_dict_item_ide',\n                      action: 'query',\n                      payload: {\n                        filter: {\"$and\":{\"dictId\":{\"eq\":\"14\"}}},\n                        page: {\"pageSize\":9999,\"pageNum\":1,\"sort\":\"dictItemNo asc\"},\n                      },\n                      query: {\"*\":{}},\n                      actionConfig: { service: 'api-base' },\n                      queryConfig: { staleTime: 300000 }\n                    }).then(res => {\n                      $self.dataSource = res.data || []\n                    })\n                  }}}"
                                  },
                                  "x-designer-extend": {
                                    "source": {
                                      "sourceType": "MQL",
                                      "enum": [],
                                      "action": "query",
                                      "type": "base_dict_item_ide",
                                      "query": {
                                        "*": {}
                                      },
                                      "filter": {
                                        "$and": {
                                          "dictId": {
                                            "eq": "14"
                                          }
                                        }
                                      },
                                      "service": "api-base",
                                      "payload": {
                                        "page": {
                                          "sort": "dictItemNo asc"
                                        }
                                      },
                                      "conditions": [
                                        [
                                          {
                                            "dataName": "dictId",
                                            "comparison": "eq",
                                            "valueType": "fixed",
                                            "modelField": "14",
                                            "modelId": ""
                                          }
                                        ]
                                      ]
                                    },
                                    "events": []
                                  },
                                  "enum": [],
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "name": "serviceStatus",
                                  "x-designable-id": "q9u5ekjmhwg",
                                  "x-index": 0
                                }
                              }
                            }
                          }
                        }
                      }
                    },
                    "t3jb6dnamjx": {
                      "type": "void",
                      "x-component": "FormCollapse.Item",
                      "x-component-props": {
                        "header": "Unnamed Title",
                        "title": "附件",
                        "disabled": false,
                        "style": {
                          "opacity": 1
                        }
                      },
                      "x-designable-id": "t3jb6dnamjx",
                      "x-index": 7,
                      "x-decorator-props": {
                        "style": {
                          "opacity": 1
                        }
                      },
                      "x-designer-extend": {},
                      "properties": {
                        "eatu8sh90cn": {
                          "type": "void",
                          "title": "按钮群组",
                          "x-component": "RenderButtonList",
                          "x-decorator-props": {
                            "style": {
                              "opacity": 1
                            }
                          },
                          "x-designer-extend": {},
                          "x-component-props": {
                            "max": 3,
                            "size": 12,
                            "style": {
                              "opacity": 1,
                              "margin": "0px 0px 20px 0px"
                            }
                          },
                          "x-validator": [],
                          "x-designable-id": "eatu8sh90cn",
                          "x-index": 0,
                          "properties": {
                            "da0n4w00tx3": {
                              "type": "void",
                              "title": "新增",
                              "x-component": "Button",
                              "x-component-props": {
                                "type": "primary",
                                "size": "small",
                                "plain": false,
                                "round": false,
                                "circle": false,
                                "style": {
                                  "opacity": 1
                                },
                                "@click": "{{(...args) => {\n        return new $LogicEngineRuntime({\n          nodes: [{\"id\":\"e004230e-0a80-4526-9fd9-b8c10235e9b7\",\"shape\":{\"name\":\"logic-start\",\"size\":{\"width\":50,\"height\":50},\"position\":{\"x\":371,\"y\":0},\"visible\":true,\"zIndex\":1},\"data\":{\"title\":\"开始\",\"value\":null}},{\"id\":\"29d16d29-1616-4df8-a62d-43bbde58d448\",\"shape\":{\"name\":\"logic-end\",\"size\":{\"width\":50,\"height\":50},\"position\":{\"x\":371,\"y\":319.2},\"visible\":true,\"zIndex\":2},\"data\":{\"title\":\"结束\",\"value\":null}},{\"id\":\"1a79b42f-dc78-44d4-a260-3b2092fdef97\",\"shape\":{\"name\":\"logic-table-row-insert\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":353,\"y\":160},\"visible\":true,\"zIndex\":3},\"data\":{\"title\":\"新增行\",\"value\":{\"method\":\"unshift\",\"tableId\":\"hgv8sls6jip\"}},\"executeFunction\":\"function logicNode(ctx) {\\n  \\n      $designable.query(\\\"hgv8sls6jip\\\").take()?.invoke(\\\"addRow\\\", \\\"unshift\\\")\\n    \\n}\"},{\"id\":\"4621ddb7-14b8-441e-a0bf-4a1dbf453e2c\",\"shape\":{\"name\":\"edge\",\"zIndex\":4,\"source\":{\"cell\":\"e004230e-0a80-4526-9fd9-b8c10235e9b7\",\"port\":\"bottom\"},\"target\":{\"cell\":\"1a79b42f-dc78-44d4-a260-3b2092fdef97\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"6ae0044c-3612-4d37-a237-40ad36325be5\",\"shape\":{\"name\":\"edge\",\"zIndex\":5,\"source\":{\"cell\":\"1a79b42f-dc78-44d4-a260-3b2092fdef97\",\"port\":\"bottom\"},\"target\":{\"cell\":\"29d16d29-1616-4df8-a62d-43bbde58d448\",\"port\":\"top\"}},\"data\":{\"value\":null}}],\n          payload: args,\n          scope: {\n            ...$root,\n            $designPages: $designPages,\n          }\n        }).run()\n      }}}"
                              },
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {
                                "events": [
                                  {
                                    "title": "新增行",
                                    "name": "click",
                                    "type": "logic",
                                    "args": [
                                      {
                                        "id": "e004230e-0a80-4526-9fd9-b8c10235e9b7",
                                        "shape": {
                                          "name": "logic-start",
                                          "size": {
                                            "width": 50,
                                            "height": 50
                                          },
                                          "position": {
                                            "x": 371,
                                            "y": 0
                                          },
                                          "visible": true,
                                          "zIndex": 1
                                        },
                                        "data": {
                                          "title": "开始",
                                          "value": null
                                        }
                                      },
                                      {
                                        "id": "29d16d29-1616-4df8-a62d-43bbde58d448",
                                        "shape": {
                                          "name": "logic-end",
                                          "size": {
                                            "width": 50,
                                            "height": 50
                                          },
                                          "position": {
                                            "x": 371,
                                            "y": 319.2
                                          },
                                          "visible": true,
                                          "zIndex": 2
                                        },
                                        "data": {
                                          "title": "结束",
                                          "value": null
                                        }
                                      },
                                      {
                                        "id": "1a79b42f-dc78-44d4-a260-3b2092fdef97",
                                        "shape": {
                                          "name": "logic-table-row-insert",
                                          "size": {
                                            "width": 86,
                                            "height": 30
                                          },
                                          "position": {
                                            "x": 353,
                                            "y": 160
                                          },
                                          "visible": true,
                                          "zIndex": 3
                                        },
                                        "data": {
                                          "title": "新增行",
                                          "value": {
                                            "method": "unshift",
                                            "tableId": "hgv8sls6jip"
                                          }
                                        },
                                        "executeFunction": "function logicNode(ctx) {\n  \n      $designable.query(\"hgv8sls6jip\").take()?.invoke(\"addRow\", \"unshift\")\n    \n}"
                                      },
                                      {
                                        "id": "4621ddb7-14b8-441e-a0bf-4a1dbf453e2c",
                                        "shape": {
                                          "name": "edge",
                                          "zIndex": 4,
                                          "source": {
                                            "cell": "e004230e-0a80-4526-9fd9-b8c10235e9b7",
                                            "port": "bottom"
                                          },
                                          "target": {
                                            "cell": "1a79b42f-dc78-44d4-a260-3b2092fdef97",
                                            "port": "top"
                                          }
                                        },
                                        "data": {
                                          "value": null
                                        }
                                      },
                                      {
                                        "id": "6ae0044c-3612-4d37-a237-40ad36325be5",
                                        "shape": {
                                          "name": "edge",
                                          "zIndex": 5,
                                          "source": {
                                            "cell": "1a79b42f-dc78-44d4-a260-3b2092fdef97",
                                            "port": "bottom"
                                          },
                                          "target": {
                                            "cell": "29d16d29-1616-4df8-a62d-43bbde58d448",
                                            "port": "top"
                                          }
                                        },
                                        "data": {
                                          "value": null
                                        }
                                      }
                                    ]
                                  }
                                ]
                              },
                              "x-validator": [],
                              "x-designable-id": "da0n4w00tx3",
                              "x-index": 0,
                              "x-reactions": {
                                "dependencies": [],
                                "fulfill": {
                                  "state": {
                                    "visible": "{{!$$safeGetScope('$readOnly')}}"
                                  }
                                }
                              }
                            }
                          }
                        },
                        "fileRecordIdes": {
                          "type": "array",
                          "x-component": "RenderTable",
                          "x-component-props": {
                            "preColumns": [
                              "seq"
                            ],
                            "cascadeDeletion": true,
                            "stripe": true,
                            "border": true,
                            "showHeader": true,
                            "openCustomTable": false,
                            "dblclickEditable": true,
                            "editMode": true,
                            "style": {
                              "opacity": 1
                            },
                            "pagination": {
                              "pageSizes": [
                                15,
                                30,
                                60,
                                120,
                                300,
                                600,
                                1000,
                                1500
                              ],
                              "pageSize": 15
                            }
                          },
                          "x-designer-extend": {
                            "page": {
                              "pagination": true,
                              "pageSize": 15,
                              "pageSizes": "15,30,60,120,300,600,1000,1500"
                            },
                            "events": []
                          },
                          "x-validator": [],
                          "x-designable-id": "hgv8sls6jip",
                          "x-index": 1,
                          "x-decorator-props": {
                            "style": {
                              "opacity": 1
                            }
                          },
                          "name": "fileRecordIdes",
                          "x-reactions": {
                            "dependencies": [],
                            "fulfill": {
                              "state": {
                                "componentProps": "{{{\r\n  ...$self.componentProps,\r\n    primaryKey: 'fileRecordId'\r\n}}}"
                              }
                            }
                          },
                          "properties": {
                            "u7r76agrxz8": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "title": "附件名称",
                                "sortable": true,
                                "resizable": true,
                                "visible": true,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                },
                                "id": "u7r76agrxz8",
                                "isGroup": false,
                                "minWidth": 150
                              },
                              "x-designable-id": "u7r76agrxz8",
                              "x-index": 0,
                              "x-display": "visible",
                              "properties": {
                                "templateDesc": {
                                  "type": "string",
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "Input",
                                  "x-validator": [],
                                  "x-component-props": {
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-designer-extend": {
                                    "events": []
                                  },
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "name": "templateDesc",
                                  "x-designable-id": "f34nlelpdpj",
                                  "x-index": 0
                                }
                              }
                            },
                            "chysz7cm035": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "title": "资料关键确认要素",
                                "sortable": true,
                                "resizable": true,
                                "visible": true,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                },
                                "id": "chysz7cm035",
                                "isGroup": false,
                                "minWidth": 150
                              },
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {},
                              "x-designable-id": "chysz7cm035",
                              "x-index": 1,
                              "x-display": "visible",
                              "properties": {
                                "keyConfirmElement": {
                                  "type": "string",
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "Input",
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "x-designer-extend": {
                                    "events": []
                                  },
                                  "x-component-props": {
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-validator": [],
                                  "name": "keyConfirmElement",
                                  "x-designable-id": "hg26nr088ij",
                                  "x-index": 0,
                                  "x-pattern": "disabled"
                                }
                              }
                            },
                            "f7iip050ov6": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "title": "模板下载",
                                "sortable": true,
                                "resizable": true,
                                "visible": true,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                },
                                "id": "f7iip050ov6",
                                "isGroup": false,
                                "minWidth": 150
                              },
                              "x-designable-id": "f7iip050ov6",
                              "x-index": 2,
                              "x-display": "visible",
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {},
                              "properties": {
                                "templateFileId": {
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "Upload",
                                  "x-component-props": {
                                    "buttonOptions": {
                                      "title": "上传附件",
                                      "type": "default",
                                      "icon": "el-icon-upload"
                                    },
                                    "allowDelete": false,
                                    "allowDownload": true,
                                    "isAsyncFileName": false,
                                    "isLogicallyDelete": false,
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "x-designer-extend": {
                                    "events": []
                                  },
                                  "x-validator": [],
                                  "name": "templateFileId",
                                  "x-pattern": "readPretty",
                                  "x-designable-id": "9jbkt6ls7mj",
                                  "x-index": 0,
                                  "x-reactions": {
                                    "dependencies": [],
                                    "fulfill": {
                                      "state": {
                                        "componentProps": "{{{\n  ...$self.componentProps,\n  fileName: $self.value ? $table.getRowByIndex($self.index).templateFileName : null\n}}}"
                                      },
                                      "run": ""
                                    }
                                  }
                                }
                              }
                            },
                            "ruegvswhm6z": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "title": "附件上传",
                                "sortable": true,
                                "resizable": true,
                                "visible": true,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                },
                                "id": "ruegvswhm6z",
                                "isGroup": false,
                                "minWidth": 150
                              },
                              "x-designable-id": "ruegvswhm6z",
                              "x-index": 3,
                              "x-display": "visible",
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {},
                              "properties": {
                                "fileId": {
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "Upload",
                                  "x-component-props": {
                                    "buttonOptions": {
                                      "title": "上传附件",
                                      "type": "default",
                                      "icon": "el-icon-upload"
                                    },
                                    "allowDelete": true,
                                    "allowDownload": true,
                                    "style": {
                                      "opacity": 1
                                    },
                                    "isAsyncFileName": false,
                                    "isLogicallyDelete": true,
                                    "@preview": "{{(...args) => {\n        return new $LogicEngineRuntime({\n          nodes: [{\"id\":\"10e58920-731b-4de4-b95c-b2eece68df3f\",\"shape\":{\"name\":\"logic-start\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":604,\"y\":0},\"visible\":true,\"zIndex\":1},\"data\":{\"title\":\"开始\",\"value\":null}},{\"id\":\"66b3d1aa-7dcc-47d8-90e2-ec497e724e28\",\"shape\":{\"name\":\"logic-end\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":604,\"y\":587.3},\"visible\":true,\"zIndex\":2},\"data\":{\"title\":\"结束\",\"value\":null}},{\"id\":\"58ba75fc-7196-4c04-a33e-49396f1c5fec\",\"shape\":{\"name\":\"logic-function\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":588.5,\"y\":304},\"visible\":true,\"zIndex\":3},\"data\":{\"title\":\"自定义函数\",\"value\":{\"code\":\"function logicNode(ctx) {\\n  const payload = ctx.getPayload()\\n  const openFilePreview = $$safeGetScope('$openFilePreview', ctx.getScope())\\n  if (openFilePreview && payload[0]) {\\n    openFilePreview({\\n      fileuploadId: payload[0].fileId,\\n      fileName: payload[0].fileName\\n    })\\n  }\\n}\"}},\"executeFunction\":\"function logicNode(ctx) {\\n  const payload = ctx.getPayload()\\n  const openFilePreview = $$safeGetScope('$openFilePreview', ctx.getScope())\\n  if (openFilePreview && payload[0]) {\\n    openFilePreview({\\n      fileuploadId: payload[0].fileId,\\n      fileName: payload[0].fileName\\n    })\\n  }\\n}\"},{\"id\":\"3205fbf0-83f0-47af-9cd7-a5aefa99bed0\",\"shape\":{\"name\":\"edge\",\"zIndex\":4,\"source\":{\"cell\":\"10e58920-731b-4de4-b95c-b2eece68df3f\",\"port\":\"bottom\"},\"target\":{\"cell\":\"58ba75fc-7196-4c04-a33e-49396f1c5fec\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"7c0429e0-19d7-4961-8941-b7ff69694a98\",\"shape\":{\"name\":\"edge\",\"zIndex\":5,\"source\":{\"cell\":\"58ba75fc-7196-4c04-a33e-49396f1c5fec\",\"port\":\"bottom\"},\"target\":{\"cell\":\"66b3d1aa-7dcc-47d8-90e2-ec497e724e28\",\"port\":\"top\"}},\"data\":{\"value\":null}}],\n          payload: args,\n          scope: {\n            ...$root,\n            $designPages: $designPages,\n          }\n        }).run()\n      }}}",
                                    "@change": "{{(...args) => {\n        return new $LogicEngineRuntime({\n          nodes: [{\"id\":\"d53424f8-a8e1-4d88-9054-970966be483e\",\"shape\":{\"name\":\"logic-start\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":505,\"y\":0},\"visible\":true,\"zIndex\":1},\"data\":{\"title\":\"开始\",\"value\":null}},{\"id\":\"a5971430-7424-4ff7-b382-7261615b8ecc\",\"shape\":{\"name\":\"logic-end\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":505,\"y\":728.6999999999999},\"visible\":true,\"zIndex\":2},\"data\":{\"title\":\"结束\",\"value\":null}},{\"id\":\"1d1d3323-2d32-4eea-bd68-bbeafea9843f\",\"shape\":{\"name\":\"logic-function\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":490,\"y\":279},\"visible\":true,\"zIndex\":3},\"data\":{\"title\":\"自定义函数\",\"value\":{\"code\":\"function logicNode(ctx) {\\n  console.log(ctx, 'ctx')\\n  const row = $table.getRowByIndex($self.index)\\n  row.fileName = ctx.payload?.[1]?.[0]?.fileName || ''\\n}\"}},\"executeFunction\":\"function logicNode(ctx) {\\n  console.log(ctx, 'ctx')\\n  const row = $table.getRowByIndex($self.index)\\n  row.fileName = ctx.payload?.[1]?.[0]?.fileName || ''\\n}\"},{\"id\":\"715f1fc7-18b5-484c-9bed-454ea827b39c\",\"shape\":{\"name\":\"edge\",\"zIndex\":4,\"source\":{\"cell\":\"d53424f8-a8e1-4d88-9054-970966be483e\",\"port\":\"bottom\"},\"target\":{\"cell\":\"1d1d3323-2d32-4eea-bd68-bbeafea9843f\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"595ecd70-aa45-464e-989e-da3d99d0348a\",\"shape\":{\"name\":\"edge\",\"zIndex\":5,\"source\":{\"cell\":\"1d1d3323-2d32-4eea-bd68-bbeafea9843f\",\"port\":\"bottom\"},\"target\":{\"cell\":\"a5971430-7424-4ff7-b382-7261615b8ecc\",\"port\":\"top\"}},\"data\":{\"value\":null}}],\n          payload: args,\n          scope: {\n            ...$root,\n            $designPages: $designPages,\n          }\n        }).run()\n      }}}"
                                  },
                                  "x-validator": [],
                                  "x-designer-extend": {
                                    "events": [
                                      {
                                        "title": "文件预览事件",
                                        "name": "preview",
                                        "type": "logic",
                                        "args": [
                                          {
                                            "id": "10e58920-731b-4de4-b95c-b2eece68df3f",
                                            "shape": {
                                              "name": "logic-start",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 604,
                                                "y": 0
                                              },
                                              "visible": true,
                                              "zIndex": 1
                                            },
                                            "data": {
                                              "title": "开始",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "66b3d1aa-7dcc-47d8-90e2-ec497e724e28",
                                            "shape": {
                                              "name": "logic-end",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 604,
                                                "y": 587.3
                                              },
                                              "visible": true,
                                              "zIndex": 2
                                            },
                                            "data": {
                                              "title": "结束",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "58ba75fc-7196-4c04-a33e-49396f1c5fec",
                                            "shape": {
                                              "name": "logic-function",
                                              "size": {
                                                "width": 86,
                                                "height": 30
                                              },
                                              "position": {
                                                "x": 588.5,
                                                "y": 304
                                              },
                                              "visible": true,
                                              "zIndex": 3
                                            },
                                            "data": {
                                              "title": "自定义函数",
                                              "value": {
                                                "code": "function logicNode(ctx) {\n  const payload = ctx.getPayload()\n  const openFilePreview = $$safeGetScope('$openFilePreview', ctx.getScope())\n  if (openFilePreview && payload[0]) {\n    openFilePreview({\n      fileuploadId: payload[0].fileId,\n      fileName: payload[0].fileName\n    })\n  }\n}"
                                              }
                                            },
                                            "executeFunction": "function logicNode(ctx) {\n  const payload = ctx.getPayload()\n  const openFilePreview = $$safeGetScope('$openFilePreview', ctx.getScope())\n  if (openFilePreview && payload[0]) {\n    openFilePreview({\n      fileuploadId: payload[0].fileId,\n      fileName: payload[0].fileName\n    })\n  }\n}"
                                          },
                                          {
                                            "id": "3205fbf0-83f0-47af-9cd7-a5aefa99bed0",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 4,
                                              "source": {
                                                "cell": "10e58920-731b-4de4-b95c-b2eece68df3f",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "58ba75fc-7196-4c04-a33e-49396f1c5fec",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "7c0429e0-19d7-4961-8941-b7ff69694a98",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 5,
                                              "source": {
                                                "cell": "58ba75fc-7196-4c04-a33e-49396f1c5fec",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "66b3d1aa-7dcc-47d8-90e2-ec497e724e28",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          }
                                        ]
                                      },
                                      {
                                        "title": "值改变事件",
                                        "name": "change",
                                        "type": "logic",
                                        "args": [
                                          {
                                            "id": "d53424f8-a8e1-4d88-9054-970966be483e",
                                            "shape": {
                                              "name": "logic-start",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 505,
                                                "y": 0
                                              },
                                              "visible": true,
                                              "zIndex": 1
                                            },
                                            "data": {
                                              "title": "开始",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "a5971430-7424-4ff7-b382-7261615b8ecc",
                                            "shape": {
                                              "name": "logic-end",
                                              "size": {
                                                "width": 55,
                                                "height": 55
                                              },
                                              "position": {
                                                "x": 505,
                                                "y": 728.6999999999999
                                              },
                                              "visible": true,
                                              "zIndex": 2
                                            },
                                            "data": {
                                              "title": "结束",
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "1d1d3323-2d32-4eea-bd68-bbeafea9843f",
                                            "shape": {
                                              "name": "logic-function",
                                              "size": {
                                                "width": 86,
                                                "height": 30
                                              },
                                              "position": {
                                                "x": 490,
                                                "y": 279
                                              },
                                              "visible": true,
                                              "zIndex": 3
                                            },
                                            "data": {
                                              "title": "自定义函数",
                                              "value": {
                                                "code": "function logicNode(ctx) {\n  console.log(ctx, 'ctx')\n  const row = $table.getRowByIndex($self.index)\n  row.fileName = ctx.payload?.[1]?.[0]?.fileName || ''\n}"
                                              }
                                            },
                                            "executeFunction": "function logicNode(ctx) {\n  console.log(ctx, 'ctx')\n  const row = $table.getRowByIndex($self.index)\n  row.fileName = ctx.payload?.[1]?.[0]?.fileName || ''\n}"
                                          },
                                          {
                                            "id": "715f1fc7-18b5-484c-9bed-454ea827b39c",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 4,
                                              "source": {
                                                "cell": "d53424f8-a8e1-4d88-9054-970966be483e",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "1d1d3323-2d32-4eea-bd68-bbeafea9843f",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          },
                                          {
                                            "id": "595ecd70-aa45-464e-989e-da3d99d0348a",
                                            "shape": {
                                              "name": "edge",
                                              "zIndex": 5,
                                              "source": {
                                                "cell": "1d1d3323-2d32-4eea-bd68-bbeafea9843f",
                                                "port": "bottom"
                                              },
                                              "target": {
                                                "cell": "a5971430-7424-4ff7-b382-7261615b8ecc",
                                                "port": "top"
                                              }
                                            },
                                            "data": {
                                              "value": null
                                            }
                                          }
                                        ]
                                      }
                                    ]
                                  },
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "name": "fileId",
                                  "x-designable-id": "o2tndvplyw2",
                                  "x-index": 0,
                                  "x-reactions": {
                                    "dependencies": [],
                                    "fulfill": {
                                      "state": {
                                        "componentProps": "{{{\n  ...$self.componentProps,\n  extraData: {\n    uploadType: 'DEF',\n    sourceType: 'WEB_APP',\n    fileModular: 'base',\n    fileFunction: 'commonFile',\n    fileType: 'excel'\n  },\n  fileName: $self.value ? $table.getRowByIndex($self.index).fileName : null\n}}}"
                                      },
                                      "run": ""
                                    }
                                  }
                                }
                              }
                            },
                            "kd9tgabmk3k": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "title": "有效期至",
                                "sortable": true,
                                "resizable": true,
                                "visible": true,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                },
                                "id": "kd9tgabmk3k",
                                "isGroup": false,
                                "minWidth": 150
                              },
                              "x-designable-id": "kd9tgabmk3k",
                              "x-index": 4,
                              "x-display": "visible",
                              "properties": {
                                "fileValidDate": {
                                  "type": "string",
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "DatePicker",
                                  "x-component-props": {
                                    "editable": true,
                                    "clearable": true,
                                    "placeholder": "请选择",
                                    "type": "date",
                                    "size": "small",
                                    "start-placeholder": "",
                                    "end-placeholder": "",
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-validator": [],
                                  "x-designer-extend": {
                                    "events": []
                                  },
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "name": "fileValidDate",
                                  "x-designable-id": "hsbf9q8aiwn",
                                  "x-index": 0
                                }
                              }
                            },
                            "141vsywzbih": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "title": "对接人员",
                                "sortable": true,
                                "resizable": true,
                                "visible": true,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                },
                                "id": "141vsywzbih",
                                "isGroup": false,
                                "minWidth": 150
                              },
                              "x-designable-id": "141vsywzbih",
                              "x-index": 5,
                              "x-display": "visible",
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {},
                              "properties": {
                                "reviewPeopleName": {
                                  "type": "string",
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "Input",
                                  "x-validator": [],
                                  "x-component-props": {
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-designer-extend": {
                                    "events": []
                                  },
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "name": "reviewPeopleName",
                                  "x-designable-id": "y7rbm18bti7",
                                  "x-index": 0
                                }
                              }
                            },
                            "t06uqpc0k1k": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "title": "供方对接人员",
                                "sortable": true,
                                "resizable": true,
                                "visible": true,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                },
                                "id": "t06uqpc0k1k",
                                "isGroup": false,
                                "minWidth": 150
                              },
                              "x-designable-id": "t06uqpc0k1k",
                              "x-index": 6,
                              "x-display": "visible",
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {},
                              "properties": {
                                "vendorAssessor": {
                                  "type": "string",
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "Input",
                                  "x-validator": [],
                                  "x-component-props": {
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-designer-extend": {
                                    "events": []
                                  },
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "name": "vendorAssessor",
                                  "x-designable-id": "fc5thwg7ebk",
                                  "x-index": 0
                                }
                              }
                            },
                            "ecjjpn0tyzj": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "title": "对接日期",
                                "sortable": true,
                                "resizable": true,
                                "visible": true,
                                "align": "left",
                                "headerAlign": "left",
                                "skipEditable": false,
                                "style": {
                                  "opacity": 1
                                },
                                "id": "ecjjpn0tyzj",
                                "isGroup": false,
                                "minWidth": 150
                              },
                              "x-designable-id": "ecjjpn0tyzj",
                              "x-index": 7,
                              "x-display": "visible",
                              "x-decorator-props": {
                                "style": {
                                  "opacity": 1
                                }
                              },
                              "x-designer-extend": {},
                              "properties": {
                                "reviewDate": {
                                  "type": "string",
                                  "title": "",
                                  "x-decorator": "FormItem",
                                  "x-component": "DatePicker",
                                  "x-component-props": {
                                    "editable": true,
                                    "clearable": true,
                                    "placeholder": "请选择",
                                    "type": "date",
                                    "size": "small",
                                    "start-placeholder": "",
                                    "end-placeholder": "",
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-validator": [],
                                  "x-designer-extend": {
                                    "events": []
                                  },
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    },
                                    "feedbackLayout": "popover"
                                  },
                                  "name": "reviewDate",
                                  "x-designable-id": "7louf28k7hw",
                                  "x-index": 0
                                }
                              }
                            },
                            "dqvc8yxg82w": {
                              "type": "void",
                              "x-component": "RenderTable.Column",
                              "x-component-props": {
                                "title": "操作",
                                "fixed": "right",
                                "width": 248,
                                "id": "dqvc8yxg82w",
                                "visible": true,
                                "resizable": true,
                                "sortable": true,
                                "isGroup": false
                              },
                              "x-designable-id": "dqvc8yxg82w",
                              "x-index": 8,
                              "x-display": "visible",
                              "properties": {
                                "e0cm1mhbd5f": {
                                  "type": "void",
                                  "x-component": "RenderButtonList",
                                  "x-component-props": {
                                    "max": 3,
                                    "size": 12,
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-designable-id": "e0cm1mhbd5f",
                                  "x-index": 0,
                                  "x-decorator-props": {
                                    "style": {
                                      "opacity": 1
                                    }
                                  },
                                  "x-designer-extend": {},
                                  "x-validator": [],
                                  "properties": {
                                    "jc98obqrk10": {
                                      "type": "void",
                                      "title": "删除",
                                      "x-component": "Button",
                                      "x-component-props": {
                                        "type": "text",
                                        "size": "small",
                                        "plain": false,
                                        "round": false,
                                        "circle": false,
                                        "style": {
                                          "opacity": 1
                                        },
                                        "@click": "{{(...args) => {\n        return new $LogicEngineRuntime({\n          nodes: [{\"id\":\"d646b6a6-52d2-4bf9-b552-51ca5a57b819\",\"shape\":{\"name\":\"logic-start\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":554.5,\"y\":0},\"visible\":true,\"zIndex\":1},\"data\":{\"title\":\"开始\",\"value\":null}},{\"id\":\"847c788f-ef54-4b51-a4f1-f7e834060d7a\",\"shape\":{\"name\":\"logic-end\",\"size\":{\"width\":55,\"height\":55},\"position\":{\"x\":554.5,\"y\":767.9},\"visible\":true,\"zIndex\":2},\"data\":{\"title\":\"结束\",\"value\":null}},{\"id\":\"5cd2e5a9-a26b-453f-992d-78b5c24c1ed5\",\"shape\":{\"name\":\"logic-table-row-delete\",\"size\":{\"width\":86,\"height\":30},\"position\":{\"x\":350,\"y\":290},\"visible\":true,\"zIndex\":3},\"data\":{\"title\":\"删除行\",\"value\":null},\"executeFunction\":\"function logicNode(ctx) {\\n  \\n        if ($table) {\\n          return $table.remove($self.index)\\n        }\\n        console.warn('该事件动作只能在表格行上触发')\\n      \\n}\"},{\"id\":\"9b7c86b4-4a4d-47c8-8f2e-f8b0dee0fd53\",\"shape\":{\"name\":\"edge\",\"zIndex\":4,\"source\":{\"cell\":\"d646b6a6-52d2-4bf9-b552-51ca5a57b819\",\"port\":\"bottom\"},\"target\":{\"cell\":\"5cd2e5a9-a26b-453f-992d-78b5c24c1ed5\",\"port\":\"top\"}},\"data\":{\"value\":null}},{\"id\":\"675c19cb-bf45-4ff6-9cb7-263701abc0d9\",\"shape\":{\"name\":\"edge\",\"zIndex\":5,\"source\":{\"cell\":\"5cd2e5a9-a26b-453f-992d-78b5c24c1ed5\",\"port\":\"bottom\"},\"target\":{\"cell\":\"847c788f-ef54-4b51-a4f1-f7e834060d7a\",\"port\":\"top\"}},\"data\":{\"value\":null}}],\n          payload: args,\n          scope: {\n            ...$root,\n            $designPages: $designPages,\n          }\n        }).run()\n      }}}"
                                      },
                                      "x-decorator-props": {
                                        "style": {
                                          "opacity": 1
                                        }
                                      },
                                      "x-designer-extend": {
                                        "events": [
                                          {
                                            "title": "删除行",
                                            "name": "click",
                                            "type": "logic",
                                            "args": [
                                              {
                                                "id": "d646b6a6-52d2-4bf9-b552-51ca5a57b819",
                                                "shape": {
                                                  "name": "logic-start",
                                                  "size": {
                                                    "width": 55,
                                                    "height": 55
                                                  },
                                                  "position": {
                                                    "x": 554.5,
                                                    "y": 0
                                                  },
                                                  "visible": true,
                                                  "zIndex": 1
                                                },
                                                "data": {
                                                  "title": "开始",
                                                  "value": null
                                                }
                                              },
                                              {
                                                "id": "847c788f-ef54-4b51-a4f1-f7e834060d7a",
                                                "shape": {
                                                  "name": "logic-end",
                                                  "size": {
                                                    "width": 55,
                                                    "height": 55
                                                  },
                                                  "position": {
                                                    "x": 554.5,
                                                    "y": 767.9
                                                  },
                                                  "visible": true,
                                                  "zIndex": 2
                                                },
                                                "data": {
                                                  "title": "结束",
                                                  "value": null
                                                }
                                              },
                                              {
                                                "id": "5cd2e5a9-a26b-453f-992d-78b5c24c1ed5",
                                                "shape": {
                                                  "name": "logic-table-row-delete",
                                                  "size": {
                                                    "width": 86,
                                                    "height": 30
                                                  },
                                                  "position": {
                                                    "x": 350,
                                                    "y": 290
                                                  },
                                                  "visible": true,
                                                  "zIndex": 3
                                                },
                                                "data": {
                                                  "title": "删除行",
                                                  "value": null
                                                },
                                                "executeFunction": "function logicNode(ctx) {\n  \n        if ($table) {\n          return $table.remove($self.index)\n        }\n        console.warn('该事件动作只能在表格行上触发')\n      \n}"
                                              },
                                              {
                                                "id": "9b7c86b4-4a4d-47c8-8f2e-f8b0dee0fd53",
                                                "shape": {
                                                  "name": "edge",
                                                  "zIndex": 4,
                                                  "source": {
                                                    "cell": "d646b6a6-52d2-4bf9-b552-51ca5a57b819",
                                                    "port": "bottom"
                                                  },
                                                  "target": {
                                                    "cell": "5cd2e5a9-a26b-453f-992d-78b5c24c1ed5",
                                                    "port": "top"
                                                  }
                                                },
                                                "data": {
                                                  "value": null
                                                }
                                              },
                                              {
                                                "id": "675c19cb-bf45-4ff6-9cb7-263701abc0d9",
                                                "shape": {
                                                  "name": "edge",
                                                  "zIndex": 5,
                                                  "source": {
                                                    "cell": "5cd2e5a9-a26b-453f-992d-78b5c24c1ed5",
                                                    "port": "bottom"
                                                  },
                                                  "target": {
                                                    "cell": "847c788f-ef54-4b51-a4f1-f7e834060d7a",
                                                    "port": "top"
                                                  }
                                                },
                                                "data": {
                                                  "value": null
                                                }
                                              }
                                            ]
                                          }
                                        ]
                                      },
                                      "x-validator": [],
                                      "x-designable-id": "jc98obqrk10",
                                      "x-index": 0,
                                      "x-reactions": {
                                        "dependencies": [],
                                        "fulfill": {
                                          "state": {
                                            "visible": "{{['', null,undefined].includes($table.getRowByIndex($self.index).remark) && !$$safeGetScope('$readOnly')}}"
                                          }
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                },
                "zev7622vivb": {
                  "type": "void",
                  "x-component": "FormBottomFixed",
                  "x-component-props": {
                    "positionAlign": "right",
                    "left": 0,
                    "right": 0,
                    "bottom": 0,
                    "style": {
                      "opacity": 1
                    }
                  },
                  "x-index": 1,
                  "name": "zev7622vivb",
                  "x-designable-id": "rp3keuejxtu",
                  "properties": {
                    "9t1r1d1fea4": {
                      "type": "void",
                      "title": "按钮群组",
                      "x-component": "RenderButtonList",
                      "x-component-props": {
                        "max": 3,
                        "size": 12,
                        "style": {
                          "opacity": 1
                        }
                      },
                      "x-index": 0,
                      "name": "9t1r1d1fea4",
                      "x-designable-id": "8nauyp3f62g"
                    }
                  }
                }
              }
            }
          },
          "form": {
            "labelCol": 6,
            "wrapperCol": 24,
            "colon": false,
            "feedbackLayout": "loose",
            "size": "default",
            "layout": "horizontal",
            "tooltipLayout": "icon",
            "labelAlign": "right",
            "wrapperAlign": "left",
            "shallow": false,
            "bordered": true,
            "style": {
              "opacity": 1,
              "position": "relative"
            },
            "x-decorator-props": {
              "style": {
                "opacity": 1
              }
            },
            "x-designer-extend": {
              "events": []
            },
            "events": {},
            "x-validator": []
          }
        },
        "LLP1671422650607554561": {
          "label": "资质审查单快查-V2",
          "value": "LLP1671422650607554561",
          "schema": {
            "sup_auth_review_form_ide": {
              "type": "void",
              "x-component": "QueryEngine",
              "x-query-engine": {
                "service": "api-sup",
                "actions": {
                  "paginationQuery": {
                    "autoFormatResult": true,
                    "immediate": true
                  }
                },
                "pagination": {
                  "pageSize": 15
                }
              },
              "properties": {
                "lqnads6q36t": {
                  "type": "object",
                  "x-component": "QueryFormByQueryEngine",
                  "x-component-props": {
                    "minWidth": 100,
                    "minColumns": 0,
                    "maxColumns": 3,
                    "columnGap": 10,
                    "rowGap": 5,
                    "colWrap": true,
                    "labelWidth": 80,
                    "immediateQueryForm": true,
                    "colon": true,
                    "style": {
                      "opacity": 1
                    }
                  },
                  "x-decorator-props": {
                    "style": "padding: 20px 20px 0;"
                  },
                  "x-index": 0,
                  "name": "lqnads6q36t",
                  "x-designable-id": "kq0ntzoslpk",
                  "x-designer-extend": {},
                  "x-validator": [],
                  "properties": {
                    "reviewFormNumber": {
                      "type": "string",
                      "title": "资质审查单号",
                      "x-decorator": "FormItem",
                      "x-decorator-props": {
                        "labelWidth": 80,
                        "style": {
                          "opacity": 1
                        },
                        "feedbackLayout": "loose"
                      },
                      "x-designer-extend": {
                        "model": {
                          "businessType": "sup_auth_review_form_ide",
                          "reference": null,
                          "modelId": "1668157711610859522",
                          "field": "reviewFormNumber",
                          "originalField": "reviewFormNumber"
                        },
                        "events": []
                      },
                      "x-component": "Input",
                      "x-component-props": {
                        "style": {
                          "opacity": 1
                        }
                      },
                      "x-index": 0,
                      "name": "reviewFormNumber",
                      "x-designable-id": "cfw4j36rzz8",
                      "x-validator": []
                    },
                    "quaReviewType": {
                      "title": "资质审查类型",
                      "x-decorator": "FormItem",
                      "x-component": "Select",
                      "x-decorator-props": {
                        "style": {
                          "opacity": 1
                        },
                        "feedbackLayout": "loose"
                      },
                      "x-designer-extend": {
                        "source": {
                          "sourceType": "MQL",
                          "enum": [],
                          "action": "query",
                          "type": "base_dict_item_ide",
                          "query": {
                            "*": {}
                          },
                          "filter": {
                            "$and": {
                              "dictId": {
                                "eq": "6763956529790976"
                              }
                            }
                          },
                          "service": "api-base",
                          "payload": {
                            "page": {
                              "sort": "dictItemNo asc"
                            }
                          },
                          "conditions": [
                            [
                              {
                                "dataName": "dictId",
                                "comparison": "eq",
                                "valueType": "fixed",
                                "modelField": "6763956529790976",
                                "modelId": ""
                              }
                            ]
                          ]
                        },
                        "events": []
                      },
                      "x-component-props": {
                        "size": "small",
                        "multiple-limit": 0,
                        "placeholder": "请选择",
                        "style": {
                          "opacity": 1
                        },
                        "fieldNames": {
                          "label": "dictItemName",
                          "value": "dictItemCode"
                        },
                        "@created": "{{() => {\n                    $queryEngine.request.baseRequest({\n                      type: 'base_dict_item_ide',\n                      action: 'query',\n                      payload: {\n                        filter: {\"$and\":{\"dictId\":{\"eq\":\"6763956529790976\"}}},\n                        page: {\"pageSize\":9999,\"pageNum\":1,\"sort\":\"dictItemNo asc\"},\n                      },\n                      query: {\"*\":{}},\n                      actionConfig: { service: 'api-base' },\n                      queryConfig: { staleTime: 300000 }\n                    }).then(res => {\n                      $self.dataSource = res.data || []\n                    })\n                  }}}"
                      },
                      "x-validator": [],
                      "enum": [],
                      "x-designable-id": "h3k2j77ap71",
                      "x-index": 1,
                      "name": "quaReviewType"
                    }
                  }
                },
                "table": {
                  "type": "array",
                  "x-decorator": "FormItem",
                  "x-component": "RenderTable",
                  "x-validator": [],
                  "x-decorator-props": {
                    "style": "padding: 0 20px;",
                    "feedbackLayout": "loose"
                  },
                  "x-designable-id": "1ygjowb3lr7",
                  "x-component-props": {
                    "stripe": false,
                    "border": true,
                    "show-header": true,
                    "size": "small",
                    "style": "width:100%",
                    "preColumns": [
                      "seq",
                      "radio"
                    ],
                    "editMode": false,
                    "openCustomTable": false,
                    "dblclickEditable": false,
                    "height": "500px",
                    "showHeader": true,
                    "pagination": {
                      "pageSizes": [
                        15,
                        30,
                        60,
                        120,
                        300,
                        600,
                        1000,
                        1500
                      ],
                      "pageSize": 15
                    }
                  },
                  "x-designer-extend": {
                    "page": {
                      "pagination": true,
                      "pageSize": 15,
                      "pageSizes": "15,30,60,120,300,600,1000,1500"
                    },
                    "events": []
                  },
                  "x-index": 1,
                  "name": "table",
                  "properties": {
                    "l7uecjgw3bx": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "title": "ID",
                        "sortable": true,
                        "resizable": true,
                        "visible": false,
                        "align": "left",
                        "headerAlign": "left",
                        "skipEditable": false,
                        "style": {
                          "opacity": 1
                        },
                        "minWidth": 150
                      },
                      "x-decorator-props": {
                        "style": {
                          "opacity": 1
                        }
                      },
                      "x-designer-extend": {},
                      "x-designable-id": "l7uecjgw3bx",
                      "x-index": 0,
                      "x-display": "hidden",
                      "properties": {
                        "reviewFormId": {
                          "type": "number",
                          "x-decorator": "FormItem",
                          "x-component": "InputNumber",
                          "x-component-props": {
                            "controls-position": "right",
                            "controls": true,
                            "placeholder": "请输入",
                            "style": {
                              "opacity": 1
                            }
                          },
                          "x-validator": "integer",
                          "name": "reviewFormId",
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sup_auth_review_form_ide",
                              "reference": null,
                              "modelId": "1668157711610859522",
                              "field": "reviewFormId",
                              "originalField": "reviewFormId"
                            },
                            "events": []
                          },
                          "x-decorator-props": {
                            "style": {
                              "opacity": 1
                            },
                            "feedbackLayout": "popover"
                          },
                          "x-designable-id": "9ectat4bpbd",
                          "x-index": 0
                        }
                      }
                    },
                    "reviewFormId": {
                      "type": "number",
                      "x-decorator": "FormItem",
                      "x-component": "InputNumber",
                      "x-component-props": {
                        "controls-position": "right",
                        "controls": true,
                        "placeholder": "请输入",
                        "style": {
                          "opacity": 1
                        }
                      },
                      "x-validator": "integer",
                      "name": "reviewFormId",
                      "x-designer-extend": {
                        "model": {
                          "businessType": "sup_auth_review_form_ide",
                          "reference": null,
                          "modelId": "1668157711610859522",
                          "field": "reviewFormId",
                          "originalField": "reviewFormId"
                        },
                        "events": []
                      },
                      "x-decorator-props": {
                        "style": {
                          "opacity": 1
                        },
                        "feedbackLayout": "popover"
                      },
                      "x-designable-id": "9ectat4bpbd",
                      "x-index": 1
                    },
                    "reviewFormNumber": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "title": "资质审查单号",
                        "sortable": true,
                        "resizable": true,
                        "visible": true,
                        "align": "left",
                        "headerAlign": "left",
                        "skipEditable": false,
                        "style": {
                          "opacity": 1
                        },
                        "id": "sifo80yagct",
                        "isGroup": false,
                        "minWidth": 150
                      },
                      "x-index": 2,
                      "name": "reviewFormNumber",
                      "x-designable-id": "sifo80yagct",
                      "x-decorator-props": {
                        "style": {
                          "opacity": 1
                        }
                      },
                      "x-designer-extend": {},
                      "x-display": "visible",
                      "properties": {
                        "reviewFormNumber": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sup_auth_review_form_ide",
                              "reference": null,
                              "modelId": "1668157711610859522",
                              "field": "reviewFormNumber",
                              "originalField": "reviewFormNumber"
                            }
                          },
                          "x-index": 0,
                          "name": "reviewFormNumber",
                          "x-designable-id": "k77iy7v7o7j"
                        }
                      }
                    },
                    "quaReviewType": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "title": "资质审查类型",
                        "sortable": true,
                        "resizable": true,
                        "visible": true,
                        "align": "left",
                        "headerAlign": "left",
                        "skipEditable": false,
                        "style": {
                          "opacity": 1
                        },
                        "id": "84cidq096ai",
                        "isGroup": false,
                        "minWidth": 150
                      },
                      "x-index": 3,
                      "name": "quaReviewType",
                      "x-designable-id": "84cidq096ai",
                      "x-decorator-props": {
                        "style": {
                          "opacity": 1
                        }
                      },
                      "x-designer-extend": {},
                      "x-display": "visible",
                      "properties": {
                        "quaReviewType": {
                          "title": "",
                          "x-decorator": "FormItem",
                          "x-component": "Select",
                          "x-decorator-props": {
                            "style": {
                              "opacity": 1
                            },
                            "feedbackLayout": "popover"
                          },
                          "x-designer-extend": {
                            "source": {
                              "sourceType": "MQL",
                              "enum": [],
                              "action": "query",
                              "type": "base_dict_item_ide",
                              "query": {
                                "*": {}
                              },
                              "filter": {
                                "$and": {
                                  "dictId": {
                                    "eq": "6763956529790976"
                                  }
                                }
                              },
                              "service": "api-base",
                              "payload": {
                                "page": {
                                  "sort": "dictItemNo asc"
                                }
                              },
                              "conditions": [
                                [
                                  {
                                    "dataName": "dictId",
                                    "comparison": "eq",
                                    "valueType": "fixed",
                                    "modelField": "6763956529790976",
                                    "modelId": ""
                                  }
                                ]
                              ]
                            },
                            "events": []
                          },
                          "x-component-props": {
                            "size": "small",
                            "multiple-limit": 0,
                            "placeholder": "请选择",
                            "style": {
                              "opacity": 1
                            },
                            "fieldNames": {
                              "label": "dictItemName",
                              "value": "dictItemCode"
                            },
                            "@created": "{{() => {\n                    $queryEngine.request.baseRequest({\n                      type: 'base_dict_item_ide',\n                      action: 'query',\n                      payload: {\n                        filter: {\"$and\":{\"dictId\":{\"eq\":\"6763956529790976\"}}},\n                        page: {\"pageSize\":9999,\"pageNum\":1,\"sort\":\"dictItemNo asc\"},\n                      },\n                      query: {\"*\":{}},\n                      actionConfig: { service: 'api-base' },\n                      queryConfig: { staleTime: 300000 }\n                    }).then(res => {\n                      $self.dataSource = res.data || []\n                    })\n                  }}}"
                          },
                          "x-validator": [],
                          "enum": [],
                          "name": "quaReviewType",
                          "x-designable-id": "vvmfd4qhc7b",
                          "x-index": 0
                        }
                      }
                    },
                    "rh53h5w77bx": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "title": "创建人",
                        "sortable": true,
                        "resizable": true,
                        "visible": true,
                        "align": "left",
                        "headerAlign": "left",
                        "skipEditable": false,
                        "style": {
                          "opacity": 1
                        },
                        "id": "rh53h5w77bx",
                        "isGroup": false,
                        "minWidth": 150
                      },
                      "x-decorator-props": {
                        "style": {
                          "opacity": 1
                        }
                      },
                      "x-designer-extend": {},
                      "x-designable-id": "rh53h5w77bx",
                      "x-index": 4,
                      "x-display": "visible",
                      "properties": {
                        "createdBy": {
                          "type": "string",
                          "x-decorator": "FormItem",
                          "x-component": "Input",
                          "name": "createdBy",
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sup_auth_review_form_ide",
                              "reference": null,
                              "modelId": "1668157711610859522",
                              "field": "createdBy",
                              "originalField": "createdBy"
                            },
                            "events": []
                          },
                          "x-decorator-props": {
                            "style": {
                              "opacity": 1
                            },
                            "feedbackLayout": "popover"
                          },
                          "x-component-props": {
                            "style": {
                              "opacity": 1
                            }
                          },
                          "x-validator": [],
                          "x-designable-id": "q92x598w9zx",
                          "x-index": 0
                        }
                      }
                    },
                    "u8mdtbftybk": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "title": "创建时间",
                        "sortable": true,
                        "resizable": true,
                        "visible": true,
                        "align": "left",
                        "headerAlign": "left",
                        "skipEditable": false,
                        "style": {
                          "opacity": 1
                        },
                        "id": "u8mdtbftybk",
                        "isGroup": false,
                        "minWidth": 150
                      },
                      "x-decorator-props": {
                        "style": {
                          "opacity": 1
                        }
                      },
                      "x-designer-extend": {},
                      "x-designable-id": "u8mdtbftybk",
                      "x-index": 5,
                      "x-display": "visible",
                      "properties": {
                        "creationDate": {
                          "type": "string",
                          "x-decorator": "FormItem",
                          "x-component": "DatePicker",
                          "x-component-props": {
                            "type": "datetime",
                            "editable": true,
                            "clearable": true,
                            "size": "small",
                            "placeholder": "请选择",
                            "style": {
                              "opacity": 1
                            },
                            "start-placeholder": "",
                            "end-placeholder": ""
                          },
                          "name": "creationDate",
                          "x-designer-extend": {
                            "model": {
                              "businessType": "sup_auth_review_form_ide",
                              "reference": null,
                              "modelId": "1668157711610859522",
                              "field": "creationDate",
                              "originalField": "creationDate"
                            },
                            "events": []
                          },
                          "x-decorator-props": {
                            "style": {
                              "opacity": 1
                            },
                            "feedbackLayout": "popover"
                          },
                          "x-validator": [],
                          "x-designable-id": "vqs5wl9yjql",
                          "x-index": 0,
                          "x-query-engine-sort": "desc"
                        }
                      }
                    }
                  }
                }
              }
            }
          },
          "form": {
            "labelCol": 6,
            "wrapperCol": 24,
            "colon": true,
            "feedbackLayout": "loose",
            "size": "default",
            "layout": "horizontal",
            "tooltipLayout": "icon",
            "labelAlign": "right",
            "wrapperAlign": "left",
            "shallow": false,
            "bordered": true,
            "x-decorator-props": {
              "style": {
                "opacity": 1
              }
            },
            "x-designer-extend": {
              "events": []
            },
            "style": {
              "opacity": 1
            },
            "events": {},
            "x-validator": []
          }
        },
        "LLP1668564193078726658": {
          "label": "银行快查",
          "value": "LLP1668564193078726658",
          "schema": {
            "base_erpBranchBank_ide": {
              "type": "void",
              "x-component": "QueryEngine",
              "x-query-engine": {
                "service": "api-base",
                "actions": {
                  "paginationQuery": {
                    "autoFormatResult": true,
                    "immediate": true
                  }
                },
                "pagination": {
                  "pageSize": 30
                }
              },
              "properties": {
                "od7fbr7fdz4": {
                  "type": "object",
                  "x-component": "QueryFormByQueryEngine",
                  "x-component-props": {
                    "immediateQueryForm": false,
                    "minWidth": 100,
                    "minColumns": 0,
                    "maxColumns": 4,
                    "columnGap": 10,
                    "rowGap": 5,
                    "colWrap": true,
                    "colon": true,
                    "style": {
                      "opacity": 1
                    },
                    "labelWidth": 80
                  },
                  "x-decorator-props": {
                    "style": "padding: 20px 20px 0;"
                  },
                  "x-index": 0,
                  "name": "od7fbr7fdz4",
                  "x-designable-id": "c2tj1sqo8q4",
                  "x-designer-extend": {},
                  "x-validator": [],
                  "properties": {
                    "bankNum": {
                      "type": "string",
                      "title": "银行编号",
                      "x-decorator": "FormItem",
                      "x-decorator-props": {
                        "labelWidth": 80,
                        "style": {
                          "opacity": 1
                        },
                        "feedbackLayout": "loose"
                      },
                      "x-designer-extend": {
                        "model": {
                          "businessType": "base_erpBranchBank_ide",
                          "reference": null,
                          "modelId": "1670599593048961025",
                          "field": "bankNum",
                          "originalField": "bankNum"
                        },
                        "events": []
                      },
                      "x-component": "Input",
                      "x-index": 0,
                      "name": "bankNum",
                      "x-designable-id": "zr6f25muins",
                      "x-validator": [],
                      "x-component-props": {
                        "style": {
                          "opacity": 1
                        }
                      }
                    },
                    "bankName": {
                      "type": "string",
                      "title": "银行名称",
                      "x-decorator": "FormItem",
                      "x-decorator-props": {
                        "labelWidth": 80,
                        "style": {
                          "opacity": 1
                        },
                        "feedbackLayout": "loose"
                      },
                      "x-designer-extend": {
                        "model": {
                          "businessType": "base_erpBranchBank_ide",
                          "reference": null,
                          "modelId": "1670599593048961025",
                          "field": "bankName",
                          "originalField": "bankName"
                        },
                        "events": []
                      },
                      "x-component": "Input",
                      "x-index": 1,
                      "name": "bankName",
                      "x-designable-id": "h6a6gepcwii",
                      "x-validator": [],
                      "x-component-props": {
                        "style": {
                          "opacity": 1
                        }
                      }
                    },
                    "branchBankNum": {
                      "type": "string",
                      "title": "分行编号",
                      "x-decorator": "FormItem",
                      "x-decorator-props": {
                        "labelWidth": 80,
                        "style": {
                          "opacity": 1
                        },
                        "feedbackLayout": "loose"
                      },
                      "x-designer-extend": {
                        "model": {
                          "businessType": "base_erpBranchBank_ide",
                          "reference": null,
                          "modelId": "1670599593048961025",
                          "field": "branchBankNum",
                          "originalField": "branchBankNum"
                        },
                        "events": []
                      },
                      "x-component": "Input",
                      "x-index": 2,
                      "name": "branchBankNum",
                      "x-designable-id": "hd89v9a9687",
                      "x-validator": [],
                      "x-component-props": {
                        "style": {
                          "opacity": 1
                        }
                      }
                    },
                    "branchBankName": {
                      "type": "string",
                      "title": "分行名称",
                      "x-decorator": "FormItem",
                      "x-decorator-props": {
                        "labelWidth": 80,
                        "style": {
                          "opacity": 1
                        },
                        "feedbackLayout": "loose"
                      },
                      "x-designer-extend": {
                        "model": {
                          "businessType": "base_erpBranchBank_ide",
                          "reference": null,
                          "modelId": "1670599593048961025",
                          "field": "branchBankName",
                          "originalField": "branchBankName"
                        },
                        "events": []
                      },
                      "x-component": "Input",
                      "x-index": 3,
                      "name": "branchBankName",
                      "x-designable-id": "up2bap053dn",
                      "x-validator": [],
                      "x-component-props": {
                        "style": {
                          "opacity": 1
                        }
                      }
                    }
                  }
                },
                "table": {
                  "type": "array",
                  "x-decorator": "FormItem",
                  "x-component": "RenderTable",
                  "x-validator": [],
                  "x-decorator-props": {
                    "style": "padding: 0 20px;",
                    "feedbackLayout": "loose"
                  },
                  "x-designable-id": "ky2hugo5eq4",
                  "x-component-props": {
                    "stripe": false,
                    "border": true,
                    "showHeader": true,
                    "openCustomTable": false,
                    "dblclickEditable": false,
                    "preColumns": [
                      "radio",
                      "seq"
                    ],
                    "editMode": "multi-row",
                    "style": "width:100%",
                    "show-header": true,
                    "size": "small",
                    "height": "500px",
                    "pagination": {
                      "pageSizes": [
                        15,
                        30,
                        60,
                        120,
                        300,
                        600,
                        1000,
                        1500
                      ],
                      "pageSize": 30
                    }
                  },
                  "x-designer-extend": {
                    "page": {
                      "pagination": true,
                      "pageSize": 30,
                      "pageSizes": "15,30,60,120,300,600,1000,1500"
                    },
                    "events": []
                  },
                  "x-index": 1,
                  "name": "table",
                  "properties": {
                    "bankNum": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "title": "银行编号",
                        "sortable": true,
                        "resizable": true,
                        "visible": true,
                        "align": "left",
                        "headerAlign": "left",
                        "skipEditable": false,
                        "style": {
                          "opacity": 1
                        },
                        "minWidth": 150
                      },
                      "x-index": 0,
                      "name": "bankNum",
                      "x-designable-id": "5b9fzs3qa12",
                      "x-display": "visible",
                      "x-decorator-props": {
                        "style": {
                          "opacity": 1
                        }
                      },
                      "x-designer-extend": {},
                      "properties": {
                        "bankNum": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {
                            "style": {
                              "opacity": 1
                            }
                          },
                          "x-designer-extend": {
                            "model": {
                              "businessType": "base_erpBranchBank_ide",
                              "reference": null,
                              "modelId": "1670599593048961025",
                              "field": "bankNum",
                              "originalField": "bankNum"
                            },
                            "events": []
                          },
                          "x-index": 0,
                          "name": "bankNum",
                          "x-designable-id": "ujp2g8l9hw9",
                          "x-decorator-props": {
                            "style": {
                              "opacity": 1
                            },
                            "feedbackLayout": "popover"
                          },
                          "x-validator": []
                        }
                      }
                    },
                    "bankName": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "title": "银行名称",
                        "sortable": true,
                        "resizable": true,
                        "visible": true,
                        "align": "left",
                        "headerAlign": "left",
                        "skipEditable": false,
                        "style": {
                          "opacity": 1
                        },
                        "minWidth": 150
                      },
                      "x-index": 1,
                      "name": "bankName",
                      "x-designable-id": "sd7h7v1sx1s",
                      "x-display": "visible",
                      "properties": {
                        "bankName": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "base_erpBranchBank_ide",
                              "reference": null,
                              "modelId": "1670599593048961025",
                              "field": "bankName",
                              "originalField": "bankName"
                            }
                          },
                          "x-index": 0,
                          "name": "bankName",
                          "x-designable-id": "apbveyh0khu"
                        }
                      }
                    },
                    "branchBankNum": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "title": "分行编号",
                        "sortable": true,
                        "resizable": true,
                        "visible": true,
                        "align": "left",
                        "headerAlign": "left",
                        "skipEditable": false,
                        "style": {
                          "opacity": 1
                        },
                        "minWidth": 150
                      },
                      "x-index": 2,
                      "name": "branchBankNum",
                      "x-designable-id": "53vb1ada7g4",
                      "x-display": "visible",
                      "properties": {
                        "branchBankNum": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "base_erpBranchBank_ide",
                              "reference": null,
                              "modelId": "1670599593048961025",
                              "field": "branchBankNum",
                              "originalField": "branchBankNum"
                            }
                          },
                          "x-index": 0,
                          "name": "branchBankNum",
                          "x-designable-id": "0yu9r2aczf0"
                        }
                      }
                    },
                    "branchBankName": {
                      "type": "void",
                      "x-component": "RenderTable.Column",
                      "x-component-props": {
                        "title": "分行名称",
                        "sortable": true,
                        "resizable": true,
                        "visible": true,
                        "align": "left",
                        "headerAlign": "left",
                        "skipEditable": false,
                        "style": {
                          "opacity": 1
                        },
                        "minWidth": 150
                      },
                      "x-index": 3,
                      "name": "branchBankName",
                      "x-designable-id": "u30xyniij6m",
                      "x-display": "visible",
                      "properties": {
                        "branchBankName": {
                          "type": "string",
                          "x-component": "Input",
                          "x-decorator": "FormItem",
                          "x-component-props": {},
                          "x-designer-extend": {
                            "model": {
                              "businessType": "base_erpBranchBank_ide",
                              "reference": null,
                              "modelId": "1670599593048961025",
                              "field": "branchBankName",
                              "originalField": "branchBankName"
                            }
                          },
                          "x-index": 0,
                          "name": "branchBankName",
                          "x-designable-id": "yq4c9pjp6dv"
                        }
                      }
                    }
                  }
                }
              }
            }
          },
          "form": {
            "labelCol": 6,
            "wrapperCol": 24,
            "colon": false,
            "feedbackLayout": "loose",
            "size": "default",
            "layout": "horizontal",
            "tooltipLayout": "icon",
            "labelAlign": "right",
            "wrapperAlign": "left",
            "shallow": false,
            "bordered": true,
            "style": {
              "opacity": 1
            }
          }
        }
      }
    }
  },
  {
    interceptFormat: accessProcessProcessing,
    pageId: 'LLP1668189666998013953',
    pageName: 'SupplierEffective'
  },
)
