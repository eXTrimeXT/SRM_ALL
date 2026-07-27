
define(function(require) {
	require("$UI/cdcui/js/jquery.slimscroll");
	require("$UI/cdcui/js/remote");
	var FileTool = require("$UI/cdcui/js/filetool"), filetool;
	var MdTool = require("$UI/cdcui/js/mdtool"), businessId, chatType, weX5, event, selected = [];

	var SrmIM = function MdTool(wex5_, e) {
		weX5 = wex5_;
		weX5.chattargetId = 0;
		this.websocket = null;
		event = e;
	};

	SrmIM.prototype.srmChat = function(conf) {
		var me = weX5;
		// 初始化
		init(conf);
		function init(conf) {
			var moduleArr = [ {
				mClass : 'commonChat',
				mId : 'chartModule'
			}, {
				mClass : 'sysMsg',
				mId : 'sysMsgModule'
			} 
				//, { mClass:'todoTip', mId :'todoTipModule' }
			];
			weX5.chattargetId = null;
			businessId = null;
			chatType = null;
			if (conf && conf.chatType) {
				chatType = conf.chatType;
				switch (conf.chatType) {
				case 'common':
					buildImTemplate(true, null);// 首先构建聊天框
					configModule(moduleArr);// 配置聊天模式
					swithModule(); // 切换模式
					clickToChat();// 点击用户聊天
					iMScroll();// 界面相关滚动条
					// closeChat();// 关闭聊天窗口
					searchUser();// 搜索联系人
					break;
				case 'buyer':
					buildImTemplate(false, conf.userList);// 首先构建聊天框
					iMScroll();// 界面相关滚动条
					// closeChat();// 关闭聊天窗口
					if (conf.businessId) {
						businessId = conf.businessId;
						weX5.userList = conf.userList;
						weX5.chattargetId = conf.businessId;
						var toChatUserInfo = $('#chartDailog').find(
								'.chartUser');
						toChatUserInfo.find('.chartUuser').html(conf.tenderNo);
						toChatUserInfo.find('.chartCompany').html(conf.title);

						getleftUserData();// 获取左边用户菜单，用于获取用户在线状态
						getChatData(conf.businessId);// 点击的时候获取历史聊天记录
					}
					break;
				case 'vendor':
					businessId = conf.businessId;
					buildImTemplate(false, null);// 首先构建聊天框
					iMScroll();// 界面相关滚动条
					if (conf.businessId) {
						businessId = conf.businessId;
						weX5.chattargetId = conf.businessId;
						var toChatUserInfo = $('#chartDailog').find(
								'.chartUser');
						toChatUserInfo.find('.chartUuser').html(conf.tenderNo);
						toChatUserInfo.find('.chartCompany').html(conf.title);
						getChatData(conf.businessId);// 点击的时候获取历史聊天记录
					}
					break;
				default:
					break;
				}
			}

			// 关闭聊天窗口
			$('#imCloseBtn').on('click', function() {
				$('#srmIM').hide();
				$('#srmIM').draggable('destroy');// 释放拖拽
				// 关闭websocket
				me.websocket.close();
			})
			initWebsocket();// 初始化Websocket
		}

		// 创建聊天框结构
		function buildImTemplate(searchUser, userList) {
			var imTemp = '<div id="srmIM">'
					+ '<div class="imChatModule">'
					+ '<div class="mypPofile"><img src="/cdc-app/UI2/cdcui/main/webIM/defaultProfile.jpg"></div>'
					+ '<div class="moduleList"><ul id="moduleUl"></ul></div>'
					+ '</div>'
					+ '<div class="chatWap">'
					+ '<div class="imOpation"><span id="imCloseBtn"></span></div>'
					+ '<div class="moduleTab" id="moduleTabCont">'
					+ '<div class="chatWapInner" id="chat">', style = ' style="padding-left:230px;"', userHtml = '', width = 862;
			if (searchUser) {
				imTemp += '<div class="leftSectionIM">'
						+ '<div class="imSearch">'
						+ '<div class="searchBar">'
						+ '<div class="searchInner">'
						+ '<input type="text" name="searchUser" class="imSearchInput"/><span class="imsearchBtn"></span>'
						+ '</div>'
						+ '<div class="searchDataList" id="seachFlowDiv">'
						+ '<div class="searchTit">联系人</div>'
						+ '<ul class="searchDataUl" id="imSearchUserUl"></ul>'
						+ '</div>'
						+ '</div>'
						+ '</div>'
						+ '<div class="imUserList" id="imListUser"><ul id="imUserUl"></ul></div>'
						+ '</div>';
			} else {
				if (userList && userList.length > 0) {
					imTemp += '<div class="leftSectionIM">'
							+ '<div class="imSearch">'
							+ '<div class="searchBar">'
							+ '<input type="checkbox" id="cbk_user"/>'
							+ '</div>'
							+ '</div>'
							+ '<div class="imUserList" id="imListUser"><ul id="imUserUl"></ul></div>'
							+ '</div>';
					userHtml = generateUserList();

				} else {
					style = '';
					width -= 230;
				}
			}
			imTemp += '<div class="chatSectionIm"' + style
					+ '><div id="chartDailog">'
					+ '<div class="toChartUserInfo">'
					+ '<div class="chartUser">'
					+ '<div class="chartUuser"></div>'
					+ '<div class="chartCompany"></div>' + '</div>' + '</div>'
					+ '<div class="chartContent">'
					+ '<div class="imMessageList" id="chatMsgList">'
					+ '<div id="dailogViewPort"></div>'
					// +'<div class="timeDivision">2019-6-19 16:07</div>'
					+ '</div>' + '</div>' + '<div class="sendMessage">'
					+ '<div class="sendText">'
					+ '<textarea id="sendTextarea"></textarea>' + '</div>'
					+ '<div class="sendInfo">'
					+ '<button class="toSendBtn" id="sendMsgBtn">发送</button>';
			if (chatType != 'common') {
				imTemp += '<button style="margin-left:5px;" class="toSendBtn" id="sendFileBtn">文件</button>';
			}
			imTemp += '</div>' + '</div>'
					+ '</div></div></div></div></div></div>';

			if ($('#srmIM').length < 1) {
				$('body').append(imTemp);
			} else {
				$('#srmIM').show();
			}
			$('#imUserUl').html(userHtml);
			if (chatType != 'common') {
				filetool = new FileTool(weX5);
				filetool
						.doRegisterUpload(
								event,
								'sendFileBtn',
								'common',
								function(data) {
									$
											.mdpAjax({
												url : "/common/chat/addChatdataToTargetCompany.ac",
												data : {
													data : JSON
															.stringify({
																fileId : data.fileRelationId, // 文件id
																fileName : data.originalFileName
															// 文件名称
															}),
													businessId : businessId,
													msgType : 'file',
													sendTo : selected.join()
												},
												successEx : function(result) {
													// 加到消息框中
													var msgArr = new Array();
													msgArr[0] = result.data;
													addChatdata(msgArr);
												},
												errorEx : function(
														XMLHttpRequest,
														textStatus, errorThrown) {
													toastr
															.error($
																	.mdpLanText("common_query_error")
																	+ XMLHttpRequest.readyState
																	+ " - "
																	+ textStatus); // 处理出错，错误代码：
												}
											});
								});
			}
			$('#srmIM').css('width', width);
			$('#imListUser').slimScroll({
				height : '425px'
			});
			$('#chatMsgList').slimScroll({
				height : '332px',
				start : 'bottom'
			});
			$('.vendorSelect').click(vendorSelect);
			// $("#imListUser").niceScroll("#imUserUl",{nativeparentscrolling:
			// true,});//联系人列表
			// $("#chatMsgList").niceScroll("#dailogViewPort",{nativeparentscrolling:
			// true,}); //设置滚动条
			$(window).trigger('resize');

			// 点击发送信息
			$("#sendMsgBtn").on('click', sendMsg);
			$('#sendFileBtn').click(function() {
				selected = [];
				if (chatType == 'buyer') {
					$.each($('.chk_user'), function(i, item) {
						var cbk = $(item);
						if (cbk.is(':checked')) {
							selected.push(cbk.val());
						}
					})
					if (weX5.vendorId) {
						selected.push(weX5.vendorId);
					}
					if (selected.length == 0) {
						$.cdc.warn('请选择供应商!');
						return false;
					}
				}
			})
			$('#cbk_user').click(function() {
				$('.chk_user').prop('checked', $(this).is(':checked'));
			});
			// 键盘点击
			$('#sendTextarea').on("keydown", function(event) {
				if (event.ctrlKey && event.keyCode == 13) {// ctrl+Enter实现换行
					event.preventDefault();
					var e = $(this).val();
					$(this).val(e + '\n');
				} else if (event.keyCode == 13) {// Enter实现发送
					event.returnValue = false;
					sendMsg();
					return false;
				}
			});

		}
		function generateUserList() {
			var userHtml = '';
			$
					.each(
							weX5.userList,
							function(i, item) {
								userHtml += '<div class="imUserDiv"><input type="checkbox" class="chk_user" name="chk_user" value="'
										+ item.userId
										+ '" id="chk_user_'
										+ item.userId
										+ '"/><label id="vendor_label_'
										+ item.userId
										+ '" for="chk_user_'
										+ item.userId
										+ '"><div class="vendor_label" title="'
										+ item.userName
										+ '">'
										+ item.userName
										+ '</div>(<span id="onlineStatus_'
										+ item.userId
										+ '">离线</span>)</label><img userId="'
										+ item.userId
										+ '" class="vendorSelect" src="/cdc-app/UI2/v_/cdcui/main/webIM/srmIM.png" style="width:16px;height:16px;cursor:pointer;"/></div>';
							});
			return userHtml;
		}
		function vendorSelect() {
			var vendorId = $(this).attr('userId');
			weX5.vendorId = vendorId;
			var html = '<label>' + $('#vendor_label_' + vendorId).html()
					+ '</label>';
			$('.searchBar')
					.html(
							'<i class="linear linear-volume vendorReturn" style="cursor:pointer"/>');
			$('#imUserUl').html(html);
			getleftUserData();
			getChatData(weX5.chattargetId, vendorId);// 点击的时候获取历史聊天记录
			// initWebsocket();
			$('.vendorReturn').click(function() {
				weX5.vendorId = null;
				$('#imUserUl').html(generateUserList());
				$('.searchBar').html('<input type="checkbox" id="cbk_user"/>');
				$('.vendorSelect').click(vendorSelect);
				$('#cbk_user').click(function() {
					$('.chk_user').prop('checked', $(this).is(':checked'));
				});
				getleftUserData();
				getChatData(weX5.chattargetId);// 点击的时候获取历史聊天记录
				// initWebsocket();
			})
		}
		// 系统信息模式html魔板
		function buildImSysMsgTemplate() {
			var msgTemp = '<div class="sysMsgInner" id="sysMessage" style="display: none">'
					+ '<div class="sysMsgListLeft">'
					+ '<div class="imSearch">'
					+ '<div class="searchBar" id="sysMsg">'
					+ '<div class="searchInner">'
					+ '<input type="text" name="searchMsg" class="imSearchInput"/>'
					+ '<span class="imsearchBtn"></span>'
					+ '</div>'
					+ '</div>'
					+ '<div class="sysMsgList" id="msgList">'
					+ '<ul id="sysMsgUl"></ul>'
					+ '</div>'
					+ '</div>'
					+ '</div>'
					+ '<div class="sysMsgShowContent">'
					+ '<div class="sysMsgShowInner">'
					+ '<div class="msgTitleBar">'
					+ '<span>消息查看</span>'
					+ '</div>'
					+ '<div class="showMsg" id="msgContent"><div id="msgContentViewPort"></div></div>'
					+ '</div>' + '</div>' + '</div>';

			if ($('#moduleTabCont').length > 0) {
				if ($('#sysMessage').length < 1) {
					$('#moduleTabCont').append(msgTemp);
				} else {
					$('#sysMessage').show();
				}
			}

			$('#msgList').slimScroll({
				height : '425px'
			});
			$('#msgContent').slimScroll({
				height : '420px'
			});
			$('#sendTextarea').slimScroll({
				height : '64px'
			});

			// $("#msgList").niceScroll("#sysMsgUl",{nativeparentscrolling:
			// true,});//系统信息类型列表
			// $("#msgContent").niceScroll("#msgContentViewPort",{nativeparentscrolling:
			// true,}); //设置滚动条
			$(window).trigger('resize');
			$('#msgContent').slimScroll().bind('slimscroll', function(e, pos) {
				console.log("Reached " + pos);
			});
		}

		// 配置模式
		function configModule(moduleArr) {
			var mArr = moduleArr;
			var moduleHtml = "";
			for (var k = 0; k < mArr.length; k++) {
				var mClass = mArr[k].mClass;
				var mId = mArr[k].mId;
				moduleHtml += '<li class="' + mClass + '" id="' + mId
						+ '"></li>';
			}
			$('#moduleUl').empty();
			$('#moduleUl').html(moduleHtml);
			$('#moduleUl').find('li').eq(0).addClass('currentModule');

			getleftUserData();// 获取左边用户菜单
		}

		// 点击模式切换效果
		function swithModule() {
			// 模式切换点击效果
			$(".moduleList").on(
					'click',
					'li',
					function() {
						$(this).addClass("currentModule").siblings("li")
								.removeClass("currentModule");
						var moduleVal = $(this).attr('id');

						if (moduleVal == 'chartModule') {// 畅聊模式
							$('#chat').show().siblings("div").hide();
							getleftUserData();// 获取左边用户菜单
						} else if (moduleVal == 'sysMsgModule') {// 查看系统信息
							buildImSysMsgTemplate();// 构建系统信息html结构
							$('#sysMessage').show().siblings("div").hide();
							getSysMsg();// 获取消息列表
						}
					})

		}

		// 左边菜单数据模拟
		// 数据返回希望是上最近聊天的放在前面
		function getleftUserData() {

			var userArr;
			// 获取通讯对象列表
			// 处理刷新引起的退出
			$.mdpAjax({
				url : "/common/chat/queryChattargetCurrentUserByBusiness.ac",
				data : {
					businessId : businessId
				},
				successEx : function(data) {
					userArr = data;

					if (userArr == null || userArr.length == 0) {
						return;
					}

					// 更新用户在线状态
					for (var i = 0; i < userArr.length; i++) {
						$('#onlineStatus_' + userArr[i]).html('在线');
						// alert(userArr[i] + " - 在线");
					}

				},
				errorEx : function(XMLHttpRequest, textStatus, errorThrown) {
					toastr.error($.mdpLanText("common_query_error")
							+ XMLHttpRequest.readyState + " - " + textStatus); // 处理出错，错误代码：
				}
			});

			// after success todo
			// $('#imUserUl').empty();
			// addChattargetTo(userArr, true)

		}
		// 构建联系人界面结构，初始化页面，在线动态添加
		function addChattargetTo(userArr, isInit) {
			var userHtml = $('#imUserUl').html();

			var userHtmlNew = "";
			for (var i = 0; i < userArr.length; i++) {
				var id = userArr[i].id; // 用户ID
				var userName = userArr[i].userName;// 用户名
				var company = "";
				if (userArr[i].company != null) {
					company = userArr[i].company; // 用户所在公司
				}
				var profileImgUrl = userArr[i].profileImgUrl ? userArr[i].profileImgUrl
						: '/cdc-app/UI2/cdcui/main/webIM/defaultProfile.jpg'; // 用户头像url
				var unreadMsg = 0;
				if (userArr[i].unreadMsg != null) {
					unreadMsg = userArr[i].unreadMsg; // 未读消息
				}
				var lineStatu = "";
				if (userArr[i].lineStatu != null) {
					lineStatu = userArr[i].lineStatu; // 是否在线
					lineStatu = "(" + lineStatu + ")";
				}
				var lastTime = "";
				if (userArr[i].lineStatu != null) {
					lastTime = userArr[i].lastTime; // 最后消息更新时间
				}
				var onClass = lineStatu == '(在线)' ? 'online' : 'unOnline';
				var msgHtml = unreadMsg > 0 ? '<span class="unRead">'
						+ unreadMsg + '</span>' : "";

				var liHtml = '<li id="' + id + '">' + '<div>'
						+ '<div class="profileImg"><img src="' + profileImgUrl
						+ '"></div>'
						+ '<div class="imUuser"><span class="userNameText">'
						+ userName + '</span><span class="lineState ' + onClass
						+ '">' + lineStatu + '</span>'
						+ '<span class="lastTime">' + lastTime
						+ '</span></div>' + '<div class="imCompany">' + company
						+ msgHtml + '</div>' + '</div></li>';
				if (chatType == 'common') {
					// 判断是否已经存在，而且是否是当前聊天
					if (isInit) {
						$('#imUserUl').append(liHtml);
					} else {
						// 先找有没有重复的
						var li = $("#imUserUl").find('#' + id);
						if (li.length > 0) {
							$("#imUserUl").find("#" + id).remove();
						}
						$('#imUserUl').prepend(liHtml);// 追加到第一个元素
					}
				}

			}
			if (chatType == 'common') {
				// 非初始化，采用即时添加节点
				if (isInit) {
					// 追加到最前
					userHtml = userHtmlNew + userHtml;
					$('#imUserUl').html(userHtml);
				}
			}

			// 暂时不需要
			// 默认获取第一个联系人的聊天记录
			// $("#imUserUl").find('li').eq(0).addClass("current");
			// var firstUserId = $("#imUserUl").find('li').eq(0).attr("id");
			// var firstUserName =
			// $("#imUserUl").find('li').eq(0).find(".userNameText").text();
			// var firstUsercompany =
			// $("#imUserUl").find('li').eq(0).find(".imCompany").html();
			// if(firstUserId){
			// var toChatUserInfo = $('#chartDailog').find('.chartUser');
			// toChatUserInfo.find('.chartUuser').html(firstUserName);
			// toChatUserInfo.find('.chartCompany').html(firstUsercompany);
			// getChatData(firstUserId);//获取第一个联系人聊天记录
			// }

		}

		// 点击选择联系人聊天
		function clickToChat() {
			// 点击样式效果
			$("#imUserUl").on(
					'click',
					'li',
					function() {
						$(this).addClass("current").siblings("li").removeClass(
								"current");
						var chattargetId = $(this).attr("id"); // id
						var curUserName = $(this).find(".imUuser").find(
								".userNameText").text(); // name
						var curUserCompany = $(this).find(".imCompany").html(); // 公司名称
						if (chattargetId) {
							weX5.chattargetId = chattargetId;
							var toChatUserInfo = $('#chartDailog').find(
									'.chartUser');
							toChatUserInfo.find('.chartUuser')
									.html(curUserName);
							toChatUserInfo.find('.chartCompany').html(
									curUserCompany);
							getChatData(chattargetId);// 点击的时候获取历史聊天记录
						}

					});

		}

		// 获取聊天记录
		function getChatData(chattargetId, companyId) {

			if (chattargetId == null || chattargetId == "") {
				return;
			}

			var msgArr;
			$.mdpAjax({
				url : "/common/chat/queryChatdataByBusiness.ac",
				data : {
					businessId : chattargetId,
					companyId : companyId
				},
				successEx : function(data) {
					msgArr = data;
				},
				errorEx : function(XMLHttpRequest, textStatus, errorThrown) {
					toastr.error($.mdpLanText("common_query_error")
							+ XMLHttpRequest.readyState + " - " + textStatus); // 处理出错，错误代码：
				}
			});

			$('#dailogViewPort').empty();
			addChatdata(msgArr);
		}

		// 添加消息到消息框
		function addChatdata(msgArr) {
			var msgListHtml = $('#dailogViewPort').html();
			var msgArrLength = msgArr.length;
			// 后台是降序返回的
			for (var j = 0; j < msgArrLength; j++) {
				var iIndex = msgArrLength - j - 1;
				var mId = msgArr[iIndex].mId; // id
				var userName = msgArr[iIndex].userName; // name
				// var company = "";
				// if (msgArr[iIndex].company != null) {
				// company = msgArr[iIndex].company; // 公司
				// }

				var profileImgUrl = msgArr[iIndex].profileImgUrl; // 头像url

				var msgContent = "";
				if (msgArr[iIndex].msgContent != null) {
					if (msgArr[iIndex].msgType == 'file') {
						var fileObj;
						if (msgArr[iIndex].msgContent) {
							fileObj = eval('(' + msgArr[iIndex].msgContent
									+ ')');
						}
						if (fileObj && fileObj.fileId && fileObj.fileName) {
							msgContent = '<a href="/cdc-app/common/file/downLoadFileByFileId.ac?fileRelationId='
									+ fileObj.fileId
									+ '">'
									+ fileObj.fileName
									+ '</a>';
						}
					} else {
						msgContent = msgArr[iIndex].msgContent; // 信息内容
					}
				}

				var msgStatu = "";
				if (msgArr[iIndex].msgStatu != null) {
					msgStatu = msgArr[iIndex].msgStatu; // 信息状态
				}

				var type = msgArr[iIndex].type; // 信息类型 接收|发送

				var typeClass = type == 'ChatDirectionTypeReceive' ? "receiveText" : "sendText";
				// 回复暂时不启用
				// var replyOrStateHtml = type == 'ChatDirectionTypeReceive' ?
				// '<div class="msgReply">回复</div>' : '<div
				// class="msgState">'+msgStatu+'</div>';
				// var replyOrStateHtml = type == 'ChatDirectionTypeReceive' ?
				// '<div class="msgReply">'+time+'</div>' : '<div
				// class="msgState">'+time+'</div>';
				var replyOrStateHtml = type == 'ChatDirectionTypeReceive' ? '<div class="msgReply"></div>'
						: '<div class="msgState"></div>';

				if (msgArr[iIndex].time != null) {
					var time = msgArr[iIndex].time; // 发送时间
					msgListHtml += '<div class="timeDivision">' + time
							+ '</div>';
				}

				// 截取名字做图标
				// var logoName = userName;
				// if (userName == null) {
				// logoName = "头像";
				// } else if (userName.length >= 4) {
				// logoName = userName.substring(userName.length -
				// 4,userName.length);
				// } else if (userName.length >= 2) {
				// logoName = userName.substring(userName.length - 2,
				// userName.length);
				// }
				// 用公司名
				// var logoName = company;
				// if (company == null) {
				// logoName = "头像";
				// } else if (company.length >= 4) {
				// logoName = company.substring(2, 4);
				// } else if (company.length >= 2) {
				// logoName = company.substring(0, 2);
				// }

				msgListHtml += '<div class="msgComm ' + typeClass + '">'
						+ '<div class="msgProfileImg">' + userName + '</div>'
						+ '<div class="reciveMsg">' + msgContent + '</div>'
						+ replyOrStateHtml + '</div>'
			}

			$('#dailogViewPort').html(msgListHtml);

			// 滚动条滚到最底端
			$('#chatMsgList').scrollTop($('#chatMsgList')[0].scrollHeight);

		}

		// 实时获取数据
		// 左边用户列表的状态和是否有未读消息 实时刷新
		// 实时获取当前对话框的消息更新情况 消息队列有新的信息就往信息列表里面追加
		function getRealTimeData() {

		}

		function getValueFamate(strValue) {
			return strValue.replace(/\r\n/g, '<br/>').replace(/\n/g, '<br/>')
					.replace(/\s/g, ' ');
		}

		// 聊天发送消息 将消息存入消息队列，并将发出去的信息返回到消息队列里面 追加到消息列表后面
		function sendMsg() {
			var sendTxt = $('#sendTextarea').val();
			selected = [];
			if (sendTxt == null || sendTxt == "") {
				return;
			}
			$.each($('.chk_user'), function(i, item) {
				var cbk = $(item);
				if (cbk.is(':checked')) {
					selected.push(cbk.val());
				}
			})
			if (weX5.vendorId) {
				selected.push(weX5.vendorId);
			}
			if ("buyer" == chatType) {
				if (selected.join() == null || selected.join() == "") {
					return;
				}
			}
			sendTxt = getValueFamate(sendTxt);// 获取换行\空格等格式一起发送

			// 发送请求
			$.mdpAjax({
				url : "/common/chat/addChatdataToTargetCompany.ac",
				data : {
					data : sendTxt,
					businessId : businessId,
					msgType : 'message',
					sendTo : selected.join()
				},
				successEx : function(data) {
					// 加到消息框中
					var msgArr = new Array();
					msgArr[0] = data.data;
					addChatdata(msgArr);
					$('#sendTextarea').val("");
				},
				errorEx : function(XMLHttpRequest, textStatus, errorThrown) {
					toastr.error($.mdpLanText("common_query_error")
							+ XMLHttpRequest.readyState + " - " + textStatus); // 处理出错，错误代码：
				}
			});

		}

		// 搜索联系人
		function searchUser() {
			var sInput = $('.searchInner').find("input[name='searchUser']");

			sInput.on('click', function() {
				$('#seachFlowDiv').show();
			});
			// 暂时用这种方式先
			$('#chartDailog').on('click', function() {
				$('#seachFlowDiv').hide();
			});

			sInput.on('keyup', function() {
				var keyVal = sInput.val();
				getSearch(keyVal);// 搜索联系人
			});

			// 请求获取数据
			function getSearch(keyVal) {
				// to do some ajax
				if (keyVal == null || keyVal == "") {
					return;
				}
				var userSearchArr;
				$
						.mdpAjax({
							url : "/common/chat/queryUserInfoForTarget.ac",
							data : {
								accountName : keyVal
							},
							successEx : function(data) {
								userSearchArr = data;
							},
							errorEx : function(XMLHttpRequest, textStatus,
									errorThrown) {
								toastr.error($.mdpLanText("common_query_error")
										+ XMLHttpRequest.readyState + " - "
										+ textStatus); // 处理出错，错误代码：
							}
						});

				var userSearchHtml = "";
				for (var q = 0; q < userSearchArr.length; q++) {
					var userName = userSearchArr[q].userName;
					var company = "";
					if (userSearchArr[q].company != null) {
						company = userSearchArr[q].company;
					}
					var profileImgUrl = userSearchArr[q].profileImgUrl ? userSearchArr[q].profileImgUrl
							: "/cdc-app/UI2/cdcui/main/webIM/defaultProfile.jpg";
					var uid = userSearchArr[q].id;
					userSearchHtml += '<li id="'
							+ uid
							+ '">'
							+ '<div>'
							+ '<div class="profileImg"><img src="'
							+ profileImgUrl
							+ '"></div>'
							+ '<div class="imUuser"><span class="userNameText">'
							+ userName + '</span></div>'
							+ '<div class="imCompany">' + company + '</div>'
							+ '</div></li>';
				}

				$('#imSearchUserUl').empty();
				$('#imSearchUserUl').html(userSearchHtml);
			}
			$('#imSearchUserUl').slimScroll({
				height : '314px'
			});

			addUserToChattarget();

		}

		// 点击搜索下拉的联系人，添加到通讯目标
		function addUserToChattarget() {
			$('#imSearchUserUl')
					.on(
							'click',
							'li',
							function() {
								var id = $(this).attr("id");
								var thisHtml = $(this).html();

								// 新增到系统
								// $.sccAjax({
								// url:
								// "/common/chat/addChattargetByTargetUserId.ac",
								// data: {targetUserId:id},
								// successEx: function(data) {
								// id =data.data;
								// me.chattargetId =id;
								//    			    
								// var liHtml ="";
								// //先找有没有重复的
								// var li = $("#imUserUl").find('#'+id);
								// if(li.length>0){
								// liHtml ='<li id="'+id+'">'+li.html()+'</li>';
								// $("#imUserUl").find("#"+id).remove();
								// }else{
								// liHtml = '<li id="'+id+'">'+thisHtml+'</li>';
								// }
								//    
								// $('#imUserUl').prepend(liHtml);//追加到第一个元素
								// 
								// //默认获取第一个联系人设置当前通讯对象
								// $("#imUserUl").find('li').eq(0).addClass("current").siblings('li').removeClass("current");
								// var firstUserId =
								// $("#imUserUl").find('li').eq(0).attr("id");
								// var firstUserName =
								// $("#imUserUl").find('li').eq(0).find(".userNameText").text();
								// var firstUsercompany =
								// $("#imUserUl").find('li').eq(0).find(".imCompany").html();
								// if(firstUserId){
								// var toChatUserInfo =
								// $('#chartDailog').find('.chartUser');
								// toChatUserInfo.find('.chartUuser').html(firstUserName);
								// toChatUserInfo.find('.chartCompany').html(firstUsercompany);
								//     		     			
								// //查询该对象的聊天记录，因为该对象有可能不是新增的
								// getChatData(id);//点击的时候获取历史聊天记录
								// }
								//    		  			 
								// $('#seachFlowDiv').hide();//隐藏掉弹出的搜索列表
								//    	    			 
								//    	    			 
								// },
								// errorEx: function(XMLHttpRequest, textStatus,
								// errorThrown) {}
								// },fastj,false);

								$
										.mdpAjax({
											url : "/common/chat/addChattargetByTargetUserId.ac",
											data : {
												targetUserId : id
											},
											successEx : function(data) {
												id = data.data;
												weX5.chattargetId = id;

												var liHtml = "";
												// 先找有没有重复的
												var li = $("#imUserUl").find(
														'#' + id);
												if (li.length > 0) {
													liHtml = '<li id="' + id
															+ '">' + li.html()
															+ '</li>';
													$("#imUserUl").find(
															"#" + id).remove();
												} else {
													liHtml = '<li id="' + id
															+ '">' + thisHtml
															+ '</li>';
												}

												$('#imUserUl').prepend(liHtml);// 追加到第一个元素

												// 默认获取第一个联系人设置当前通讯对象
												$("#imUserUl").find('li').eq(0)
														.addClass("current")
														.siblings('li')
														.removeClass("current");
												var firstUserId = $("#imUserUl")
														.find('li').eq(0).attr(
																"id");
												var firstUserName = $(
														"#imUserUl").find('li')
														.eq(0)
														.find(".userNameText")
														.text();
												var firstUsercompany = $(
														"#imUserUl").find('li')
														.eq(0).find(
																".imCompany")
														.html();
												if (firstUserId) {
													var toChatUserInfo = $(
															'#chartDailog')
															.find('.chartUser');
													toChatUserInfo
															.find('.chartUuser')
															.html(firstUserName);
													toChatUserInfo
															.find(
																	'.chartCompany')
															.html(
																	firstUsercompany);

													// 查询该对象的聊天记录，因为该对象有可能不是新增的
													getChatData(id);// 点击的时候获取历史聊天记录
												}

												$('#seachFlowDiv').hide();// 隐藏掉弹出的搜索列表
											},
											errorEx : function(XMLHttpRequest,
													textStatus, errorThrown) {
												toastr
														.error($
																.mdpLanText("common_query_error")
																+ XMLHttpRequest.readyState
																+ " - "
																+ textStatus); // 处理出错，错误代码：
											}
										});

							})

		}

		// ///////////////////系统消息查看模式/////////////////////////////

		function iMScroll() {

			// $("#msgList").niceScroll();
			// $("#msgContent").niceScroll();
			// $("#imSearchUserUl").niceScroll();
		}

		function getSysMsg() {
			var msgArr = [ {
				id : "1",
				noteType : "公告待办",
				notetitle : "建设中，敬请期待",
				profileImgUrl : "",
				unreadMsg : 0,
				lastTime : ''
			} ];

			var msgHtml = "";
			for (var i = 0; i < msgArr.length; i++) {
				var uid = msgArr[i].id; // 用户ID
				var noteType = msgArr[i].noteType;// 信息类型
				var notetitle = msgArr[i].notetitle; // 最近一条信息
				var profileImgUrl = msgArr[i].profileImgUrl ? msgArr[i].profileImgUrl
						: '/cdc-app/UI2/cdcui/main/webIM/defaultProfile.jpg'; // 用户头像url
				var unreadMsg = msgArr[i].unreadMsg; // 未读消息
				var lastTime = msgArr[i].lastTime; // 最后消息更新时间
				var msgNum = unreadMsg > 0 ? '<span class="unRead">'
						+ unreadMsg + '</span>' : "";

				msgHtml += '<li id="' + uid + '">' + '<div>'
						+ '<div class="profileImg"><img src="' + profileImgUrl
						+ '"></div>'
						+ '<div class="imUuser"><span class="userNameText">'
						+ noteType + '</span>' + '<span class="lastTime">'
						+ lastTime + '</span></div>'
						+ '<div class="imCompany">' + notetitle + msgNum
						+ '</div>' + '</div></li>';
			}

			$('#sysMsgUl').empty();
			$('#sysMsgUl').html(msgHtml);
			switchMsgDetail();
		}

		// 点击公告类型列表
		function switchMsgDetail() {
			$('#sysMsgUl').find('li').eq(0).addClass('current');
			var firstId = $('#sysMsgUl').find('li').eq(0).attr("id");
			getSysDetailMsg(firstId);
			$('#sysMsgUl').on(
					'click',
					'li',
					function() {
						$(this).addClass('current').siblings('li').removeClass(
								'current');
						var id = $(this).attr('id');
						if (id) {
							getSysDetailMsg(id);// 获取公告列表
						}
					});

		}

		// 点击类型获取对应的消息列表
		function getSysDetailMsg(catId) {
			var notesArr = [ {
				id : 11,
				title : '敬请期待',
				pbTime : '2019-06-25 00:00',
				content : '建设中，敬请期待',
				autor : '协作云系统'
			}

			];

			var noteHtml = "";
			for (var j = 0; j < notesArr.length; j++) {
				var id = notesArr[j].id;
				var title = notesArr[j].title;
				var pbTime = notesArr[j].pbTime;
				var content = notesArr[j].content;
				var autor = notesArr[j].autor;

				noteHtml += '<div class="msgSection">'
						+ '<div class="notePbTime">' + pbTime + '</div>'
						+ '<div class="noteDetail">'
						+ '<div class="notesTitle">' + title + '</div>'
						+ '<div class="notesContent">' + content + '</div>'
						+ '<div class="notesReadMore">查看详情</div>' + '</div>'
						+ '</div>'

			}

			$('#msgContentViewPort').empty();
			$('#msgContentViewPort').html(noteHtml);

			// 滚动条滚到最底端，不起作用
			// $('#msgContent').scrollTop( $('#msgContent')[0].scrollHeight);

			$('.msgSection').on('click', '.notesReadMore', function() {
				$(this).siblings('.notesContent').slideToggle();
				if ($(this).html() == '查看详情') {
					$(this).html("收起");
				} else {
					$(this).html("查看详情");
				}
			})
		}

		// 初始化Websocket
		function initWebsocket() {
			// host+port
			var domain = window.location.host;
			var url = "/websocket/chat.sock?billType=BillTypePrice&billId="
					+ businessId;
			if ('WebSocket' in window) {
				websocket = new WebSocket("ws://" + domain + "/cdc-app" + url);
				me.websocket = websocket;
			} else {
				console.log("not support websocket");
				return;
			}
			websocket.onmessage = function(evt) {
				console
						.log("Websocket - " + url + " - OnMessage - "
								+ evt.data);

				var jsonData = JSON.parse(evt.data);

				// 普通通讯
				if ("ChatTypeCommon" == jsonData.chatType) {
					// 判断控制类型
					if ("OnOffline" == jsonData.controlType) {
						// 在线离线
						var onlineStatus = '离线';
						if ("Online" == jsonData.onlineStatus) {
							// 更新界面的状态为“在线”
							onlineStatus = '在线';
							// alert(jsonData.companyId + " - 上线");
						} else {
							// 更新界面的状态为“离线”
							// alert(jsonData.companyId + " - 下线");
						}
						$('#onlineStatus_' + jsonData.companyId).html(
								onlineStatus);
					} else {
						// 通讯内容
						// 注意这个ChatDataDTO是基于发送方的
						var chatDataArray = new Array()
						chatDataArray[0] = jsonData.chatdata;
						chatDataArray[0].type = "ChatDirectionTypeReceive";
						addChatdata(chatDataArray);

					}
				}
			};
			websocket.onopen = function(evt) {
				console.log("Websocket - " + url + " - OnOpen");
			};
			websocket.onerror = function(evt) {
				console.log("Websocket - " + url + " - OnError");
			};
			websocket.onclose = function(evt) {
				console.log("Websocket - " + url + " - OnClose");
			};
		}
		// 关闭窗口
		function closeChat() {
			$('#imCloseBtn').on('click', function() {
				$('#srmIM').hide();
				$('#srmIM').draggable('destroy');// 释放拖拽
				// todo something

				// 关闭websocket
				me.websocket.close();
			})
		}

	}

	return SrmIM;
});

