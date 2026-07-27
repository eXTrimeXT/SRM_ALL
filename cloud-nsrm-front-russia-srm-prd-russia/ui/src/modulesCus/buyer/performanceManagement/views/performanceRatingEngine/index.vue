<script setup lang="ts">
import {
  defineSchemas,
  generateXindexInOrder,
  expression,
  i18nExpression, changeFieldVisibleByDeps
} from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import {
  dataTimeSelectorSegment,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
const { app } = usePageHelper()

const schema = defineSchemas({
  "sccPerfLevel": {
    "type": "void",
    "x-decorator": "QueryEngine",
    "x-query-engine": {
      service: 'pef',
      "actions": {
        "paginationQuery": {
          "autoFormatResult": true,
          "immediate": true
        }
      }
    },
    'x-component': 'PageContainer',
    "x-component-props": {
      class: 'performanceRating'
    },
    "properties": {
      "dialog": {
        "type": "void",
        "x-component": "RDialog",
        "x-component-props": {
          "visible": false,
          "title": "新增",
          "okText": "确认",
          "cancelText": "取消",
          "size": "middle",
          "modal": true,
          "beforeClose": "{{\n          (done, type) => {\n            const formInstance = $self.query('*.dialog.form').take()\n            if (type === 'ok' && !formInstance.readPretty) {\n              formInstance.submit(values => {\n                debugger\n                const operateFlag = (!!values.levelId ? \"update\" : \"create\")\n                $queryEngine.request[operateFlag]({\n                  ...values\n                })\n                  .then(() => {\n                    $message({ type: 'success', message: '保存成功' })\n                    $queryEngine.state.paginationManagement.refresh()\n                    done()\n                  })\n                  .catch(() => {\n                    $message({ type: 'error', message: '保存失败' })\n                  })\n              })\n            } else {\n              done()\n            }\n          }\n        }}"
        },
        "x-index": 0,
        "name": "dialog",
        "x-designable-id": "qpq7dke3ch6",
        "properties": {
          "form": {
            "type": "object",
            "x-component": "FormGrid",
            "x-component-props": {
              "maxColumns": 2,
              "columnGap": 32,
              "rowGap": 0
            },
            "name": "form",
            "x-designable-id": "m606gw20rj0",
            "x-index": 0,
            "properties": {
              "levelName": {
                "type": "string",
                "title": "等级名称",
                "x-decorator": "FormItem",
                "x-component": "Input",
                "name": "levelName",
                "x-business-props": {
                  "field": "levelName",
                  "modelField": "levelName"
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
                  }
                },
                "x-designable-id": "mhakqw71fqy",
                "x-index": 0
              },
              "organizationName": {
                "type": "string",
                "title": "采购组织",
                "x-decorator": "FormItem",
                "x-component": "OrganizationSelector",
                "name": "organizationName",
                "x-business-props": {
                  "field": "organizationName",
                  "modelField": "organizationName"
                },
                "x-validator": [],
                "x-component-props": {
                  "style": {
                    "opacity": 1
                  },
                  'parent-id': "-1",
                  'node-type': "OU",
                  multiple: "false",
                  'collapse-tags':"true",
                  '@select': expression(``)
                },
                "x-decorator-props": {
                  "style": {
                    "opacity": 1
                  }
                },
                "x-designable-id": "23lzfvife1v",
                "x-index": 1
              },
              "scoreStart": {
                "type": "string",
                "title": "综合绩效得分>",
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
                "name": "scoreStart",
                "x-business-props": {
                  "field": "scoreStart",
                  "modelField": "scoreStart"
                },
                "x-decorator-props": {
                  "style": {
                    "opacity": 1
                  }
                },
                "x-designable-id": "ezkimhvd14t",
                "x-index": 2
              },
              "scoreEnd": {
                "type": "string",
                "title": "综合绩效得分<=",
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
                "name": "scoreEnd",
                "x-business-props": {
                  "field": "scoreEnd",
                  "modelField": "scoreEnd"
                },
                "x-decorator-props": {
                  "style": {
                    "opacity": 1
                  }
                },
                "x-designable-id": "yadb9bn2psq",
                "x-index": 3
              },
              "status": {
                "title": "等级状态",
                "x-decorator": "FormItem",
                "x-component": "Select",
                "name": "status",
                "type": "string",
                "x-business-props": {
                  "field": "status",
                  "modelField": "status"
                },
                "x-validator": [],
                "x-component-props": {
                  "size": "small",
                  "multiple-limit": 0,
                  "placeholder": "请选择",
                  "style": {
                    "opacity": 1
                  }
                },
                "x-designer-extend": {
                  "source": {
                    "sourceType": "STATIC",
                    "enum": []
                  }
                },
                "x-decorator-props": {
                  "style": {
                    "opacity": 1
                  }
                },
                "x-designable-id": "pn6ou1d2lvw",
                "enum": [],
                "x-index": 4
              },
              "levelDescription": {
                "type": "string",
                "title": "等级说明",
                "x-decorator": "FormItem",
                "x-component": "Input.TextArea",
                "name": "levelDescription",
                "x-business-props": {
                  "field": "levelDescription",
                  "modelField": "levelDescription"
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
                  "fullness": true,
                  "gridSpan": 1
                },
                "x-designable-id": "7x46gxref6z",
                "x-index": 5
              }
            }
          }
        }
      },
      "k3lng0gdkej": {
        "type": "object",
        "x-component": "QueryFormByQueryEngine",
        "x-decorator": "FormItem",
        "x-validator": [],
        "x-component-props": {
          "minWidth": 100,
          "minColumns": 0,
          "maxColumns": 4,
          "columnGap": 10,
          "rowGap": 5,
          "colWrap": true,
          "labelWidth": 80,
          "immediateQueryForm": false,
          "style": {
            "opacity": 1
          }
        },
        "x-decorator-props": {
          "style": "padding: 20px 20px 0;"
        },
        "x-index": 1,
        "name": "k3lng0gdkej",
        "x-designable-id": "c33cvhyoe60",
        "properties": {
          "organizationName": {
            "type": "string",
            "title": "等级名称",
            "x-decorator": "FormItem",
            "x-decorator-props": {
              "labelWidth": 80,
              "style": {
                "opacity": 1
              }
            },
            "x-component": "Input",
            "x-component-props": {
              "style": {
                "opacity": 1
              }
            },
            "x-index": 0,
            "name": "organizationName",
            "x-designable-id": "bwg53fbhn0n",
            "x-validator": []
          },
          "scoreStart": {
            "type": "string",
            "title": "综合绩效得分",
            "x-decorator": "FormItem",
            "x-decorator-props": {
              "labelWidth": 80,
              "style": {
                "opacity": 1
              }
            },
            "x-component": "InputNumber",
            "x-component-props": {
              "controls-position": "right",
              "controls": true,
              "placeholder": "请输入",
              "style": {
                "opacity": 1
              }
            },
            "x-index": 1,
            "name": "scoreStart",
            "x-designable-id": "h5eoofe9w2j",
            "x-validator": []
          },
          "status": {
            "type": "string",
            "title": "等级状态",
            "x-decorator": "FormItem",
            "x-decorator-props": {
              "labelWidth": 80,
              "style": {
                "opacity": 1
              }
            },
            "x-component": "Select",
            "x-component-props": {
              "size": "small",
              "multiple-limit": 0,
              "placeholder": "请选择",
              "style": {
                "opacity": 1
              }
            },
            "x-index": 2,
            "name": "status",
            "x-designable-id": "sya7p7pflyw",
            "x-validator": [],
            "x-designer-extend": {
              "source": {
                "sourceType": "STATIC",
                "enum": []
              }
            },
            "enum": []
          }
        }
      },
      "tools": {
        "type": "void",
        "x-component": "RenderButtonList",
        "x-component-props": {
          "style": {
            "margin": " 0 20px 16px"
          }
        },
        "x-index": 2,
        "name": "tools",
        "x-designable-id": "4ntjp2osgfa",
        "properties": {
          "add": {
            "type": "void",
            "x-component": "Button",
            "x-component-props": {
              "type": "primary",
              text: "新增",
              "size": "small",
              "plain": false,
              "round": false,
              "circle": false,
              "style": {
                "opacity": 1
              },
              "@click": "{{() => {\n  $form\n    .query(\"*.dialog\")\n    .take()\n    .setComponentProps({ visible: true, title: \"新增\" })\n  setTimeout(() => {\n    $form.query(\"*.dialog.form\").take((field) => {\n      field.reset()\n    })\n  })\n}\n}}",
              "@@click": "{{[object Object]}}"
            },
            "x-index": 0,
            "name": "add",
            "x-designable-id": "8nirogazh1e",
            "x-designer-extend": {
              "events": {
                "click": {
                  "type": "CUSTOM",
                  "args": "() => {\n  $form\n    .query(\"*.dialog\")\n    .take()\n    .setComponentProps({ visible: true, title: \"新增\" })\n  setTimeout(() => {\n    $form.query(\"*.dialog.form\").take((field) => {\n      field.reset()\n    })\n  })\n}\n"
                },
                "@click": {
                  "type": "OPEN_TAB",
                  "args": {
                    "scope": "{\n}\n"
                  }
                }
              }
            },
            "x-validator": [],
            "title": "新增"
          },
          importExcel: {
            type: 'void',
            'x-component': 'ImportExcel',
            'x-visible': expression('$buyer()'),
            'x-component-props': {
              title: i18nExpression('common.excelImport'),
              type: 'default',
              extraData: {
                fileModular: 'perf',
                fileFunction: 'performanceRating',
                fileType: 'excel'
              },
              upLoadUrl: '/api-pef/perfLevel/importExcelInsertLevel',
              downloadTemplateOptions: {
                downloadUrl: '/api-pef/perfLevel/exportPerfLevelModel',
                fileName: expression(`$t('perfMod.levelTemp') + '.xlsx'`)
              },
              '@handleSuccess': expression(`() => {
                $bus.$emit('ContractHead')
              }`)
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
          "style": "padding: 0 20px;"
        },
        "x-component-props": {
          "stripe": false,
          "border": false,
          "show-header": true,
          "pagination": true,
          "size": "small",
          "height": "500px",
          "style": "width:100%",
          "preColumns": [
            "seq"
          ],
          "openCustomTable": true,
          "showHeader": true,
          "dblclickEditable": true,
          "editMode": false
        },
        "x-index": 3,
        "name": "table",
        "x-designable-id": "fpxns4jvtdg",
        "properties": {
          "organizationName": {
            "type": "void",
            "x-component": "RenderTable.Column",
            "x-component-props": {
              "width": 150,
              "title": "采购组织",
              "sortable": true,
              "resizable": true,
              "visible": true,
              "align": "left",
              "headerAlign": "left",
              "skipEditable": false,
              "style": {
                "opacity": 1
              },
              "id": "x9cbs0tjp54"
            },
            "x-index": 0,
            "name": "organizationName",
            "x-designable-id": "x9cbs0tjp54",
            "x-display": "visible",
            "properties": {
              "organizationName": {
                "type": "string",
                "x-component": "Input",
                "x-decorator": "FormItem",
                "x-component-props": {
                  "style": {
                    "opacity": 1
                  }
                },
                "x-index": 0,
                "name": "organizationName",
                "x-designable-id": "qdhnp54r42c",
                "x-validator": [],
                "x-decorator-props": {
                  "style": {
                    "opacity": 1
                  }
                }
              }
            }
          },
          "levelName": {
            "type": "void",
            "x-component": "RenderTable.Column",
            "x-component-props": {
              "width": 150,
              "title": "等级名称",
              "sortable": true,
              "resizable": true,
              "visible": true,
              "align": "left",
              "headerAlign": "left",
              "skipEditable": false,
              "style": {
                "opacity": 1
              },
              "id": "dkety95k3ta"
            },
            "x-index": 1,
            "name": "levelName",
            "x-designable-id": "dkety95k3ta",
            "x-display": "visible",
            "properties": {
              "levelName": {
                "type": "string",
                "x-component": "Input",
                "x-decorator": "FormItem",
                "x-component-props": {
                  "style": {
                    "opacity": 1
                  }
                },
                "x-index": 0,
                "name": "levelName",
                "x-designable-id": "ue8tc5z4jem",
                "x-validator": [],
                "x-decorator-props": {
                  "style": {
                    "opacity": 1
                  }
                }
              }
            }
          },
          "levelDescription": {
            "type": "void",
            "x-component": "RenderTable.Column",
            "x-component-props": {
              "width": 150,
              "title": "等级说明",
              "id": "49892bwk5dq",
              "visible": true,
              "resizable": true,
              "sortable": true
            },
            "x-index": 2,
            "name": "levelDescription",
            "x-designable-id": "49892bwk5dq",
            "x-display": "visible",
            "properties": {
              "levelDescription": {
                "type": "string",
                "x-component": "Input",
                "x-decorator": "FormItem",
                "x-component-props": {
                  "style": {
                    "opacity": 1
                  }
                },
                "x-index": 0,
                "name": "levelDescription",
                "x-designable-id": "yuf4vy185ey",
                "x-validator": [],
                "x-decorator-props": {
                  "style": {
                    "opacity": 1
                  }
                }
              }
            }
          },
          "scoreStart": {
            "type": "void",
            "x-component": "RenderTable.Column",
            "x-component-props": {
              "width": 150,
              "title": "综合绩效得分>",
              "sortable": true,
              "resizable": true,
              "visible": true,
              "align": "left",
              "headerAlign": "left",
              "skipEditable": false,
              "style": {
                "opacity": 1
              },
              "id": "dapbwoab3h9"
            },
            "x-index": 3,
            "name": "scoreStart",
            "x-designable-id": "dapbwoab3h9",
            "x-display": "visible",
            "properties": {
              "scoreStart": {
                "type": "string",
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
                "x-index": 0,
                "name": "scoreStart",
                "x-designable-id": "262zf7dstub"
              }
            }
          },
          "scoreEnd": {
            "type": "void",
            "x-component": "RenderTable.Column",
            "x-component-props": {
              "width": 150,
              "title": "综合绩效得分<=",
              "id": "36z0em3s1ko",
              "visible": true,
              "resizable": true,
              "sortable": true
            },
            "x-index": 4,
            "name": "scoreEnd",
            "x-designable-id": "36z0em3s1ko",
            "x-display": "visible",
            "properties": {
              "scoreEnd": {
                "type": "string",
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
                "x-index": 0,
                "name": "scoreEnd",
                "x-designable-id": "kooyc3pxnt0"
              }
            }
          },
          "status": {
            "type": "void",
            "x-component": "RenderTable.Column",
            "x-component-props": {
              "width": 150,
              "title": "等级状态",
              "id": "5jcr66gd78i",
              "visible": true,
              "resizable": true,
              "sortable": true
            },
            "x-index": 5,
            "name": "status",
            "x-designable-id": "5jcr66gd78i",
            "x-display": "visible",
            "properties": {
              "status": {
                "type": "string",
                "x-component": "Select",
                "x-decorator": "FormItem",
                "x-component-props": {},
                "x-index": 0,
                "name": "status",
                "x-designable-id": "d8yfq7ny3xc"
              }
            }
          },
          "operation": {
            "type": "void",
            "title": "操作",
            "x-component": "RenderTable.Column",
            "x-component-props": {
              "width": 248,
              "title": "操作",
              "fixed": "right",
              "sortable": true,
              "resizable": true,
              "visible": true,
              "align": "left",
              "headerAlign": "left",
              "skipEditable": false,
              "style": {
                "opacity": 1
              },
              "id": "b9uopv8eknt"
            },
            "name": "operation",
            "x-designable-id": "b9uopv8eknt",
            "x-index": 6,
            "x-display": "visible",
            "properties": {
              "operation": {
                "type": "void",
                "name": "operation",
                "x-component": "RenderButtonList",
                "x-index": 0,
                "x-designable-id": "8yimtz5f94y",
                "x-validator": [],
                "x-component-props": {
                  "max": 4,
                  "size": 12,
                  "style": {
                    "opacity": 1
                  }
                },
                "properties": {
                  "edit": {
                    "type": "void",
                    "title": "编辑",
                    "x-component": "Button",
                    "x-component-props": {
                      "type": "text",
                      "text": "编辑",
                      "size": "small",
                      "plain": false,
                      "round": false,
                      "circle": false,
                      "style": {
                        "opacity": 1
                      },
                      "@click": "{{() => {\n  const index = $self.index\n  const row = $table.getRowByIndex(index)\n  return $queryEngine.request\n    .read(row.levelId)\n    .then((result) => {\n      setTimeout(() => {\n        $form.query(\"*.dialog.form\").take((field) => {\n          field.readPretty = false\n          field.setValue(result.data[0])\n        })\n      }, 100)\n      $form\n        .query(\"*.dialog\")\n        .take()\n        .setComponentProps({ visible: true, title: \"编辑\" })\n    })\n    .catch((err) => {\n      $message({ type: \"error\", message: \"获取当前行数据异常\" })\n    })\n}\n}}"
                    },
                    "name": "edit",
                    "x-designable-id": "5q3wihc6r1i",
                    "x-designer-extend": {
                      "events": {
                        "click": {
                          "type": "CUSTOM",
                          "args": "() => {\n  const index = $self.index\n  const row = $table.getRowByIndex(index)\n  return $queryEngine.request\n    .read(row.levelId)\n    .then((result) => {\n      setTimeout(() => {\n        $form.query(\"*.dialog.form\").take((field) => {\n          field.readPretty = false\n          field.setValue(result.data[0])\n        })\n      }, 100)\n      $form\n        .query(\"*.dialog\")\n        .take()\n        .setComponentProps({ visible: true, title: \"编辑\" })\n    })\n    .catch((err) => {\n      $message({ type: \"error\", message: \"获取当前行数据异常\" })\n    })\n}\n"
                        },
                        "@click": {
                          "type": "CUSTOM"
                        }
                      }
                    },
                    "x-validator": [],
                    "x-index": 0
                  },
                  "delete": {
                    "type": "void",
                    "title": "删除",
                    "x-component": "Button",
                    "x-component-props": {
                      "type": "text",
                      "text": "删除",
                      "@click": "{{() => {\n            const index = $self.index\n            const row = $table.getRowByIndex(index)\n            return $queryEngine.request.delete(row.levelId)\n              .then(() => {\n                $message({ type: 'success', message: '删除成功' })\n                $queryEngine.state.paginationManagement.refresh()\n              })\n              .catch((err) => {\n                $message({ type: 'error', message: err.message || '操作异常' })\n              })\n          }}}"
                    },
                    "name": "delete",
                    "x-designable-id": "67tilo9gucz",
                    "x-designer-extend": {
                      "events": {
                        "click": {
                          "type": "CUSTOM",
                          "args": "() => {\n            const index = $self.index\n            const row = $table.getRowByIndex(index)\n            return $queryEngine.request.delete(row.levelId)\n              .then(() => {\n                $message({ type: 'success', message: '删除成功' })\n                $queryEngine.state.paginationManagement.refresh()\n              })\n              .catch((err) => {\n                $message({ type: 'error', message: err.message || '操作异常' })\n              })\n          }"
                        }
                      }
                    },
                    "x-index": 1
                  },
                  "enable": {
                    "type": "void",
                    "title": "启用",
                    "x-component": "Button",
                    "x-component-props": {
                      "type": "text",
                      "text": "查看",
                      "size": "small",
                      "plain": false,
                      "round": false,
                      "circle": false,
                      "style": {
                        "opacity": 1
                      },
                      "@click": "{{() => {\n  const index = $self.index\n  const row = $table.getRowByIndex(index)\n  return $queryEngine.request\n    .read(row.levelId)\n    .then((result) => {\n      setTimeout(() => {\n        $form.query(\"*.dialog.form\").take((field) => {\n          field.readPretty = true\n          field.setValue(result.data[0])\n        })\n      }, 100)\n      $form\n        .query(\"*.dialog\")\n        .take()\n        .setComponentProps({ visible: true, title: \"查看\" })\n    })\n    .catch((err) => {\n      $message({ type: \"error\", message: \"获取当前行数据异常\" })\n    })\n}\n}}"
                    },
                    "name": "enable",
                    "x-designable-id": "ufzl7gbum1w",
                    "x-designer-extend": {
                      "events": {
                        "click": {
                          "type": "CUSTOM",
                          "args": "() => {\n  const index = $self.index\n  const row = $table.getRowByIndex(index)\n  return $queryEngine.request\n    .read(row.levelId)\n    .then((result) => {\n      setTimeout(() => {\n        $form.query(\"*.dialog.form\").take((field) => {\n          field.readPretty = true\n          field.setValue(result.data[0])\n        })\n      }, 100)\n      $form\n        .query(\"*.dialog\")\n        .take()\n        .setComponentProps({ visible: true, title: \"查看\" })\n    })\n    .catch((err) => {\n      $message({ type: \"error\", message: \"获取当前行数据异常\" })\n    })\n}\n"
                        },
                        "@click": {
                          "type": "CUSTOM"
                        }
                      }
                    },
                    "x-index": 2,
                    "x-validator": []
                  },
                  "disable": {
                    "type": "void",
                    "title": "禁用",
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
                      "@click": "{{() => {}\n}}"
                    },
                    "x-validator": [],
                    "x-designer-extend": {
                      "events": {
                        "click": {
                          "type": "CUSTOM",
                          "args": "() => {}\n"
                        },
                        "@click": {
                          "type": "CUSTOM"
                        }
                      }
                    },
                    "name": "disable",
                    "x-designable-id": "pfzh4iagn2d",
                    "x-index": 3
                  }
                }
              }
            }
          }
        }
      }
    }
  }
})


const scope = {
  app,
  i18nExpression
}

const components = {

}

</script>

<template>
  <RenderEngine class="contractPaymentType" :schema="schema" :scope="scope" :components="components" />
</template>

<style lang="scss">
.performanceRating .render-page-container__body {
  padding: 0;
}
</style>
