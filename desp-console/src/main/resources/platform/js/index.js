var vue;
var middleBarShow = false;
var treeData;
var subMenus = [];
var casIndexUrl;

$(function(){
	
	vue = new Vue({
		el: '#app',
		data: function(){
			return {
				currentUserBaseInfo:{},
				level2MenuName:"",
				tabItems: []
			};
		},
		methods:{
			showEditPasswordBox: function(){//弹出修改密码框体
				$("#updatePwdModal").modal("show");
			},
			logout: function(){//注销
				location.href = contextPath + "/logout";
			},
			updatePassword: function(){//更新密码
				$('#changeFasswordForm').bootstrapValidator('validate');
				var isValid = $('#changeFasswordForm').data('bootstrapValidator').isValid();
				if(isValid){
					PlatformUI.ajax({
						url: contextPath + "/updatePassword",
						type: "post",
						data:{newPassword:$("#newPassword").val()},
						afterOperation: function(){
							$('#updatePwdModal').modal("hide");
						},
						message: message
					});
				}else{
					message({message:"表单验证失败", type: "warning"});
				}
			},
			linkToPage: function(route){//链接跳转
				location.href = contextPath + "/" + route;
			},
			showUserBaseInfo: function(){//弹出用户基本信息模态框
				$("#userInfoModal").modal("show");
				var me = this;
				PlatformUI.ajax({
					url: contextPath + "/currentUser/baseInfo",
					afterOperation: function(data){
						me.currentUserBaseInfo = {};
						$.extend(me.currentUserBaseInfo, data);
					}
				});
			},
			tabClick: function(name){
				$(this.tabItems).each(function(){
					if(this.name == name){
						this.selected = true;
					}else{
						this.selected = false;
					}
				});
			},
			tabCloseClick: function(name){
				var index = 0;
				var currentSelectedIndex = -1;
				for(var i=0; i < this.tabItems.length; i++){
					if(this.tabItems[i].name == name){
						index = i;
					}
					if(this.tabItems[i].selected){
						currentSelectedIndex = i;
					}
				}
				this.tabItems.splice(index, 1);
				if(currentSelectedIndex == index){
					this.tabItems[this.tabItems.length - 1].selected = true;
				}
			}
		}
	});
	
	//获取casIndexUrl
	PlatformUI.ajax({
		url: contextPath + "/getCasIndexUrl",
		afterOperation: function(data){
			casIndexUrl = data.responseText; 
		}
	});
	
	//初始化菜单
	initMenu();
	
	//初始化密码框
	initPasswordEditBox();
	
	bindTopToolHover();
	
	bindMiddleBar();
	
	$("iframe").height(document.body.clientHeight - 105);
});


/*********************方法区*********************/

//初始化菜单
function initMenu(){
	PlatformUI.ajax({
		url: contextPath + "/loadMenu",
		afterOperation: function(data, textStatus,jqXHR){
			$("#level1Menu").empty();
			buildTreeData(data);
			var template = "";
			$(treeData).each(function(){
					template += "<p class=\"menu_head current\"><i class='arrow_triangle-right'></i><span>" + this.name + "</span></p>"
					if(this.children && this.children.length > 0){
						template += "<ul class=\"menu_body\">";
						$(this.children).each(function(){
							subMenus.push(this);
							if(!this.icon){
								this.icon = "icon_creditcard";
							}
							template += "<li id='" + this.id + "' onclick=\"level1MenuOperation('" + this.id + "', '" + this.name + "', '" + this.url + "', arguments[0]);\"><a><i class=\"" + this.icon + "\"></i><span>" + this.name + "</span></a></li>";
						});
						template += "</ul>";
					}
			});
			$("#level1Menu").append(template);
			
			//一级菜单动态事件绑定
			bindLevel1MenuEvent();
		}
	});
}

//一，二级菜单操作
function level1MenuOperation(id, name, url, e){
	$("#level1Menu li").removeClass("selected");
	$("#" + id).addClass("selected");
	$("#level2MenuDiv").empty();
	vue.level2MenuName = name;
	$(subMenus).each(function(){
		if(this.id == id){//三级菜单
			if(this.children && this.children.length > 0){
				var template = "";
				$(this.children).each(function(){
					 if(this.children && this.children.length > 0){//四级菜单
					 	template += "<li class=\"menu_head\"><i class='arrow_triangle-right'></i><a>" + this.name + "</a></li>"
					 	template += "<ul class=\"menu_body\">";
					 	$(this.children).each(function(){
							template += "<li onclick=\"linkFrame('" + this.name + "','" + this.url + "')\"><a>" + this.name + "</a></li>"				 	
					 	});
					 }else{
					 	template += "<li onclick=\"linkFrame('" + this.name + "','" + this.url + "')\"><a>" + this.name + "</a></li>";
					 	template += "<ul class=\"menu_body\">";
					 }
					 template += "</ul>"; 
					 $("#level2MenuDiv").append(template);
				});
				//二级事件绑定
				bindLevel2Menu();
				if(!middleBarShow){
					animateShowLevel2Menu();
					middleBarShow = !middleBarShow;
				}
			}else{//直接跳转
				if(middleBarShow){
					setTimeout(function(){
						animateHideLevel2Menu();
						middleBarShow = !middleBarShow;
					}, 500);
				}
				linkFrame(this.name, url)
				return false;
			}
		}
	});
}

function buildTreeData(data){
	var normalTreeData = [];
	$(data).each(function(){
		var nodeData = {};
		nodeData.id = this.id;
		nodeData.name = this.menuName;
		nodeData.url = this.menuValue;
		nodeData.icon = this.icon;
		if(this.parent){
			nodeData.pId = this.parent.id;
		}else{
			nodeData.pId = null;
		}
		normalTreeData.push(nodeData);
	});
	treeData = PlatformUI.formatEasyUITreeData(normalTreeData);
}

function bindLevel1MenuEvent(){
	$("#level1Menu p.menu_head").click(function(){
 		$(this).siblings().find('i').removeClass('arrow_triangle-down').addClass('arrow_triangle-right');
		$(this).addClass("current").next("ul.menu_body").slideToggle(300).siblings("ul.menu_body").slideUp("slow");
		$(this).siblings().removeClass("current");
	
		if($('p.current').find("i").hasClass('arrow_triangle-right')){
            $('p.current').find("i").toggleClass('arrow_triangle-right');
			$('p.current').find("i").addClass('arrow_triangle-down');		
		}
		else{
            $('p.current').find("i").toggleClass('arrow_triangle-down');
			$('p.current').find("i").addClass('arrow_triangle-right');		
		}

	});
	
	$(".icon_1").click(function(){
		var $lefty = $(".left") 
		var $lefty1 = $(".left_fixed") 
		if($lefty.width()!=50){
			$("p.menu_head span").show();
			$("ul.menu_body li a span").show();
			$("p.menu_head span").fadeOut("fast");
			$("ul.menu_body li a span").fadeOut("fast");
			$lefty.animate({ width:50}); 
			$lefty1.animate({width:70});
			$(".icon_1").animate({width:50});
			$(".right").animate({left:"50px"});
		}else{
			$("p.menu_head span").hide();
			$("ul.menu_body li a span").hide();
			$lefty.animate({width:200});
			$lefty1.animate({width:220});  
			setTimeout(function(){
				$("p.menu_head span").show();
				$("ul.menu_body li a span").show();
			}, 200);
			$(".icon_1").animate({width:200});
			$(".right").animate({left:"200px"});
		}
	});
	
}

function bindLevel2Menu(){
	$(".menu_main li.menu_head").click(function(){
 		$(this).siblings().find('i').removeClass('arrow_triangle-down').addClass('arrow_triangle-right');
		$(this).addClass("current").next("ul.menu_body").slideToggle(300).siblings("ul.menu_body").slideUp("slow");
		$(this).siblings().removeClass("current");
	
		if($('li.current').find("i").hasClass('arrow_triangle-right')){
            $('li.current').find("i").toggleClass('arrow_triangle-right');
			$('li.current').find("i").addClass('arrow_triangle-down');		
		}else{
            $('li.current').find("i").toggleClass('arrow_triangle-down');
			$('li.current').find("i").addClass('arrow_triangle-right');		
		}

	});
}

function bindMiddleBar(){
	$("#middleBar").click(function(){
		if(!middleBarShow){
			animateShowLevel2Menu();
		}else{
			animateHideLevel2Menu();
		}
		middleBarShow = !middleBarShow;
	});
}

function animateShowLevel2Menu(){
	$(".left_menu").animate({ width:"180px"},300);
	$(".hide_left").animate({ left:"180px"},300);
	$(".main_bor").animate({ left:"180px"},300);
}

function animateHideLevel2Menu(){
	$(".left_menu").animate({ width:0},300);
	$(".hide_left").animate({ left:0},300);
	$(".main_bor").animate({ left:0},300);
}

function linkFrame(name, url){
	var flag = false;
	$(vue.tabItems).each(function(){
		if(this.name == name){
			this.selected = true;
			flag = true;
		}else{
			this.selected = false;
		}
	});
	if(!flag){
		if(vue.tabItems.length > 6){//不能让tab item的个数超过6个
			vue.tabItems.splice(vue.tabItems.length - 1, 1);
			vue.tabItems.splice(1, 0, {name:name, selected:true, closeable:true, url:contextPath + "/" + url});
		}else{
			vue.tabItems.push({name:name, selected:true, closeable:true, url:contextPath + "/" + url});
		}
	}
	setTimeout(function(){
		$("iframe").height(document.body.clientHeight - 105);
	}, 600);
}

//绑定顶部hover事件
function bindTopToolHover(){
	$('.yhm').hover(function(){
		$(".yhm").css("background-color","#313232");
		$(".info").show()},function(){
		$(".info").hide();
		$(".yhm").css("background-color","#373d41");
	});
	$('.info').hover(function(){
		$(".yhm").css("background-color","#313232");
		$(".info").show()},function(){
		$(".info").hide();
		$(".yhm").css("background-color","#373d41");
	});
}

//初始化密码修改框
function initPasswordEditBox(){
	//密码框验证
	$('#changeFasswordForm').bootstrapValidator({
		message: 'This value is not valid',
        feedbackIcons: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
		fields:{
			oldPassword: {
                validators: {
                    remote: {
                    	url: contextPath + "/checkPassword",
                        message: '请输入正确的密码'
                    }
                }
            },
            newPassword: {
                validators: {
                    notEmpty: {
                        message: '请输入新密码'
                    }
                }
            },
            confirmPassword: {
                validators: {
                    notEmpty: {
                        message: '请确认新密码'
                    },
                    identical: {
                        field: 'newPassword',
                        message: '请确认输入的新密码一致'
                    }
                }
            }
		}
	});
	
	//弹出modal时，重置密码框form
	$("#updatePwdModal").on("shown.bs.modal", function(e){
		$('#changeFasswordForm').data('bootstrapValidator').resetForm(true);
	});	
	
}

//全局消息发生器，子页面的消息提示都通过此方法进行
var message = function(msgParam){
	vue.$message(msgParam);
}

//主框架刷新，让子页面可以通知父页面进行刷新
function commonRefresh(){
	location.href = "";
}