grammar QuoteAttrFormula;
line : expr EOF ;

expr :
    ' '* '(' ' '* expr ' '* ')' ' '*        # expr_normal
    | expr ' '* ('*' | '/' | '%') ' '* expr # expr_operate
    | expr ' '* ('+' | '-') ' '* expr       # expr_operate
    | ' '* ref ' '*                         # expr_ref
    | ' '* fun ' '*                         # expr_fun
    | ' '* group_func ' '*                  # expr_group
    | ' '* DECIMAL ' '*                     # expr_decimal
    | ' '* VARIABLE ' '*                    # expr_variable
    ;

// ref语句处理
ref : KEY_REF '.' VARIABLE_NAME '(' ' '* ref_select ' '* ')';
ref_select :
    ','* (ref_select_frag ' '* (',' ' '*)+)* ref_return_frag (',' ' '*)*                                    # ref_select_valid1
    | ','* ref_return_frag ' '* (',' ' '*)+ (ref_select_frag ' '* (',' ' '*)+)* ref_select_frag (',' ' '*)* # ref_select_valid2
    | ','* ref_where_frag ' '* (',' ' '*)+ ref_return_frag (',' ' '*)*                                      # ref_select_valid3
    | ','* ref_return_frag ' '* (',' ' '*)+ ref_where_frag (',' ' '*)*                                      # ref_select_valid4
    | ','* ((ref_select_frag | ref_where_frag) ' '* (',' ' '*)+)*
      (ref_select_frag | ref_where_frag) (',' ' '*)*                                                        # ref_select_error1 // 错误处理: 缺少 return 语句
    | ','* ((ref_select_frag | ref_where_frag) ' '* (',' ' '*)+)*
      (ref_return_frag ' '* (',' ' '*)+)+
      ((ref_select_frag | ref_where_frag) ' '* (',' ' '*)+)*
      ((ref_return_frag ' '* (',' ' '*)+)+
      (ref_select_frag | ref_where_frag) | (ref_return_frag)) (',' ' '*)*                                   # ref_select_error2 // 错误处理: 存在多个 return 语句
    ;
ref_select_frag :
    VARIABLE_REF (' '* ('=' | '!=' | KEY_LIKE) ' '*) ref_select_expr_s                      # ref_select_frag_s
    | VARIABLE_REF (' '* ('=' | '!=' | '>' | '>=' | '<' | '<=' | KEY_LIKE) ' '*) ref_select_expr_ns   # ref_select_frag_ns
    | (VARIABLE | ERROR_VAR_COIN_BEGIN1) (' '* ('=' | '!=' | '>' | '>=' | '<' | '<=' | KEY_LIKE) ' '*)
      (ref_select_expr_s | ref_select_expr_ns)                                              # ref_select_frag_error1 // 错误处理 开头变量是 ${xx}
    | ((VARIABLE_NAME ' '*)* VARIABLE_NAME) (' '* ('=' | '!=' | '>' | '>=' | '<' | '<=' | KEY_LIKE) ' '*)
      (ref_select_expr_s | ref_select_expr_ns)                                              # ref_select_frag_error2 // 错误处理 开头变量是 abc 等
    ;
ref_return_frag : KEY_RETURN ' '* '=' ' '* ref_return_expr;
ref_select_expr_s :
    '(' ' '* ref_select_expr_s ' '* ')'                                                     # ref_select_expr_s_normal
    | ref_select_expr_s ' '* '+' ' '* ref_select_expr_s                                     # ref_select_expr_s_add
    | ref_select_expr_s ' '* '+' ' '* ref_select_expr_ns                                    # ref_select_expr_s_add
    | ref_select_expr_ns ' '* '+' ' '* ref_select_expr_s                                    # ref_select_expr_s_add
    | STRING                                                                                # ref_select_expr_s_terminal
    ;
ref_select_expr_ns :
    '(' ' '* ref_select_expr_ns ' '* ')'                                                    # ref_select_expr_ns_normal
    | ref_select_expr_ns ' '* ('*' | '/' | '%') ' '* ref_select_expr_ns                     # ref_select_expr_ns_operate
    | ref_select_expr_ns ' '* ('+' | '-') ' '* ref_select_expr_ns                           # ref_select_expr_ns_operate
    | DECIMAL                                                                               # ref_select_expr_ns_terminal
    | VARIABLE                                                                              # ref_select_expr_ns_variable
    | VARIABLE_REF                                                                          # ref_select_expr_ns_field
    | VARIABLE_NAME                                                                         # ref_select_expr_ns_error1 // 错误处理 无法识别的变量信息
    | ((DIGIT+ '.') | ('.' DIGIT+) | (DIGIT+ '.' (DIGIT* '.' DIGIT*)+))                     # ref_select_expr_ns_error2 // 错误处理 数字格式错误
    ;
ref_return_expr :
    '(' ' '* ref_return_expr ' '* ')'                                                       # ref_return_expr_normal
    | ref_return_expr ' '* ('*' | '/' | '%') ' '* ref_return_expr                           # ref_return_expr_operate
    | ref_return_expr ' '* ('+' | '-') ' '* ref_return_expr                                 # ref_return_expr_operate
    | DECIMAL                                                                               # ref_return_expr_terminal
    | VARIABLE                                                                              # ref_return_expr_variable
    | VARIABLE_REF                                                                          # ref_return_expr_field
    | VARIABLE_NAME                                                                         # ref_return_expr_error1 // 错误处理 无法识别的变量信息
    | ((DIGIT+ '.') | ('.' DIGIT+) | (DIGIT+ '.' (DIGIT* '.' DIGIT*)+))                     # ref_return_expr_error2 // 错误处理 数字格式错误
    ;
ref_where_frag: KEY_WHERE '=' '"' ref_where_frag_mini '"';  // TODO: where语句语法校验暂时不做深入，目前只是提供功能
ref_where_frag_mini:
    '(' ref_where_frag_mini ')'                                     # ref_where_frag_mini_normal
    | ref_where_frag_mini_frag (KEY_AND ref_where_frag_mini_frag)*  # ref_where_frag_mini_and
    ;
ref_where_frag_mini_frag:
    | VARIABLE_REF ( '=' | '!=' | '>' | '>=' | '<' | '<=' | KEY_LIKE ) ref_where_expr
    ;
ref_where_expr:
    '(' ref_where_expr ')'                                                  # ref_where_expr_normal
    | ref_where_expr ( '+' | '-' | '*' | '/' | '%' ) ref_where_expr         # ref_where_expr_operate
    | VARIABLE_REF (( KEY_IS KEY_NULL ) | ( KEY_IS KEY_NOT KEY_NULL ))      # ref_where_expr_null
    | DECIMAL                                                               # ref_where_expr_terminal
    | VARIABLE                                                              # ref_where_expr_variable
    | VARIABLE_REF                                                          # ref_where_expr_field
    ;

// fun语句处理
fun : KEY_FUN '.' VARIABLE_NAME '(' ' '* fun_param ' '* ')';
fun_param : (',' ' '*)* fun_param_frag? | ((fun_param_frag ' '* (',' ' '*)+)+ fun_param_frag) (',' ' '*)*;
fun_param_frag :
    VARIABLE_NAME ' '* ('=' | '=' ' '* fun_param_expr_s)?                   # fun_param_frag_s
    | VARIABLE_NAME ' '* ('=' | '=' ' '* fun_param_expr_ns)?                # fun_param_frag_ns
    ;
fun_param_expr_s :
    '(' ' '* fun_param_expr_s ' '* ')'                                    # fun_param_expr_s_normal
    | fun_param_expr_s ' '* '+' ' '* fun_param_expr_s                     # fun_param_expr_s_operate
    | fun_param_expr_s ' '* '+' ' '* fun_param_expr_ns                    # fun_param_expr_s_operate
    | fun_param_expr_ns ' '* '+' ' '* fun_param_expr_s                    # fun_param_expr_s_operate
    | STRING                                                              # fun_param_expr_s_terminal
    ;
fun_param_expr_ns :
    '(' ' '* fun_param_expr_ns ' '* ')'                                   # fun_param_expr_ns_normal
    | fun_param_expr_ns ' '* ('*' | '/' | '%') ' '* fun_param_expr_ns   # fun_param_expr_ns_operate
    | fun_param_expr_ns ' '* ('+' | '-') ' '* fun_param_expr_ns         # fun_param_expr_ns_operate
    | DECIMAL                                                   # fun_param_expr_ns_terminal
    | VARIABLE                                                  # fun_param_expr_ns_variable
    ;

// 聚合函数处理
group_func : sum | max | min | avg;
sum : KEY_SUM '(' ' '* group_frag ' '* ')';
max : KEY_MAX '(' ' '* group_frag ' '* ')';
min : KEY_MIN '(' ' '* group_frag ' '* ')';
avg : KEY_AVG '(' ' '* group_frag ' '* ')';
group_frag :
    ref         # group_frag_ref
    | fun       # group_frag_fun
    ;

// 语法中的关键字定义
KEY_LIKE : L I K E;
KEY_RETURN : R E T U R N;
KEY_WHERE : W H E R E;
KEY_IS: I S;
KEY_NULL: N U L L;
KEY_NOT: N O T;
KEY_AND : ' ' A N D ' ';
KEY_SUM : S U M;
KEY_MAX : M A X;
KEY_MIN : M I N;
KEY_AVG : A V G;
KEY_REF : R E F;
KEY_FUN : F U N;
KEY_WORD:
    KEY_LIKE
    | KEY_RETURN
    | KEY_SUM
    | KEY_MAX
    | KEY_MIN
    | KEY_AVG
    | KEY_REF
    | KEY_FUN
    ;

// 数字定义
DECIMAL :
    DIGIT+ '.' DIGIT+
    | DIGIT+
    ;
DIGIT : [0-9];

// 变量相关定义
VARIABLE : '${' VARIABLE_NAME+ '}';  // 变量
VARIABLE_REF : '&{' VARIABLE_NAME+ '}';  // 被引用的属性的变量
VARIABLE_NAME : ([a-zA-Z\u4e00-\u9fa5] AVAILABLE_CHAR*) | ( [0-9]+ [a-zA-Z\u4e00-\u9fa5_]+ AVAILABLE_CHAR*);     // 变量名
STRING : '\'' AVAILABLE_CHAR+ '\'';    // 字符串(目前只支持单引号)
AVAILABLE_CHAR : [a-zA-Z0-9\u4e00-\u9fa5_]; // 变量的可支持字符集

// 错误符号集
ERROR_VAR_COIN_BEGIN1 : (('${' VARIABLE_NAME) | ('$'+ ' '* '{'+ ' '* VARIABLE_NAME));

// 用于忽略大小写
fragment A : [aA];
fragment B : [bB];
fragment C : [cC];
fragment D : [dD];
fragment E : [eE];
fragment F : [fF];
fragment G : [gG];
fragment H : [hH];
fragment I : [iI];
fragment J : [jJ];
fragment K : [kK];
fragment L : [lL];
fragment M : [mM];
fragment N : [nN];
fragment O : [oO];
fragment P : [pP];
fragment Q : [qQ];
fragment R : [rR];
fragment S : [sS];
fragment T : [tT];
fragment U : [uU];
fragment V : [vV];
fragment W : [wW];
fragment X : [xX];
fragment Y : [yY];
fragment Z : [zZ];

WS : [ \t\r\n]+ -> skip;
