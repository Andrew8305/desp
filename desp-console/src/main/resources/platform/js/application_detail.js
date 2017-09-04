var vue;
var unDeployMachineGrid;
var deployedMachineGrid;
var deployedStaticMachineGrid;
var startableAppGrid;
var stoppableAppGrid;
var deploySerialMachineGrid;//曾经部署过的物理机列表
var deploySerialGrid;//发布流水

$(function(){
	
	vue = new Vue({
		el: '#app',
		data: function(){
			return {
				uploadUrl:contextPath + "/application/upload?id=" + $("#id").val(),
				dialogStartableAppVisible: false,
				dialogUploadVisible: false,
				dialogDeployVisible: false,
				dialogStoppableAppVisible: false,
				dialogDeleteAppVisible: false, 
				dialogRollbackAppVisible: false,
				uploadFileList:[],
				button: {
					start: {
						disabled: false					
					},
					deploy: {
						disabled: false
					},
					stop: {
						disabled: false
					},
					rollback: {
						disabled: false
					},
					del: {
						disabled: false
					}
				},
				grid:{
					unDeployMachine: {
						searchVal: null
					},
					deployedMachine: {
						searchVal: null
					},
					startable: {
						searchVal: null
					},
					stoppable: {
						searchVal: null
					},
					deploySerialMachine:{
						searchVal: null
					}
				}
			};
		},
		methods:{
			rollback: function(){//应用回滚
				var deploySerialGridIds = deploySerialGrid.jqGrid ('getGridParam', 'selarrrow');
				if(deploySerialGridIds.length != 1){
					PlatformUI.message({message:"请选择一条发布流水数据", type:"warning"});
					return;
				}
				this.$confirm('是否确定回滚应用到指定版本', '提示', {
		          confirmButtonText: '回滚',
		          cancelButtonText: '取消',
		          type: 'warning'
		        }).then(function(){
		        	PlatformUI.ajax({
						url: contextPath + "/application/rollback",
						type: "post",
						message: PlatformUI.message,
						data: {deploySerialId:deploySerialGridIds[0]},
						afterOperation: function(){
							initDeploySerialMachineGrid();
							initDeploySerialGrid("-1");
						}
					});
		        });
			},
			showRollbackDialog: function(){
				this.dialogRollbackAppVisible = true
				setTimeout(function(){
					initDeploySerialMachineGrid();
					initDeploySerialGrid("-1");
				}, 500);			
			},
			deleteApp: function(){//删除单个app对应的运行实例
				var context = this;
				var deployedStaticMachineGridIds = deployedStaticMachineGrid.jqGrid ('getGridParam', 'selarrrow');
				if(deployedStaticMachineGridIds.length == 0){
					PlatformUI.message({message:"请选择页面底部部署实例列表数据!", type:"warning"});
					return;
				}
				PlatformUI.ajax({
					url: contextPath + "/application/delete",
					type: "post",
					message: PlatformUI.message,
					data: {appInstanceIds:deployedStaticMachineGridIds},
					afterOperation: function(){
						context.dialogDeleteAppVisible = false;
						PlatformUI.refreshGrid(deployedStaticMachineGrid, {sortname:"ai.createDate",sortorder:"desc"});
					}
				});
			},
			deleAllApp: function(){//删除应用
				var context = this;
				this.$confirm('是否删除整个应用', '提示', {
		          confirmButtonText: '删除',
		          cancelButtonText: '取消',
		          type: 'warning'
		        })
		        .then(function(){
		        	PlatformUI.ajax({
						url: contextPath + "/application/deleteAll",
						type: "post",
						message: PlatformUI.message,
						data: {appPrimary: $("#id").val()},
						afterOperation: function(){
							context.dialogDeleteAppVisible = false;
							PlatformUI.refreshGrid(deployedStaticMachineGrid, {sortname:"ai.createDate",sortorder:"desc"});
							setTimeout(function(){
								location.href = contextPath + "/application/index";
							}, 1000);
						}
					});
		        });
			},
			showStopDialog: function(){
				this.dialogStoppableAppVisible = true
				setTimeout(function(){
					initStoppableAppGrid();
				}, 500);
			},
			stop: function(){//停止应用
				var stoppableAppGridIds = stoppableAppGrid.jqGrid ('getGridParam', 'selarrrow');
				if(stoppableAppGridIds.length == 0){
					PlatformUI.message({message:"请至少选择一条要操作的数据!", type:"warning"});
					return;
				}
				PlatformUI.ajax({
					url: contextPath + "/application/stop",
					type: "post",
					message: PlatformUI.message,
					data: {appInstanceIds:stoppableAppGridIds},
					afterOperation: function(){
						PlatformUI.refreshGrid(stoppableAppGrid, {sortname:"ai.createDate",sortorder:"desc"});
						PlatformUI.refreshGrid(deployedStaticMachineGrid, {sortname:"ai.createDate",sortorder:"desc"});
					}
				});
			},
			stopAll: function(){//停止所有应用
				this.$confirm('是否要停止全部应用', '提示', {
		          confirmButtonText: '确定',
		          cancelButtonText: '取消',
		          type: 'warning'
		        }).then(function(){
		        	PlatformUI.ajax({
						url: contextPath + "/application/stopAll",
						type: "post",
						message: PlatformUI.message,
						data: {appPrimary: $("#id").val()},
						afterOperation: function(){
							PlatformUI.refreshGrid(stoppableAppGrid, {sortname:"ai.createDate",sortorder:"desc"});
							PlatformUI.refreshGrid(deployedStaticMachineGrid, {sortname:"ai.createDate",sortorder:"desc"});
						}
					});
		        });
			},			
			showStartDialog: function(){
				this.dialogStartableAppVisible = true
				setTimeout(function(){
					initStartableAppGrid();
				}, 500);
			},
			start: function(){//启动应用
				var startableAppGridIds = startableAppGrid.jqGrid ('getGridParam', 'selarrrow');
				if(startableAppGridIds.length == 0){
					PlatformUI.message({message:"请至少选择一条要操作的数据!", type:"warning"});
					return;
				}
				PlatformUI.ajax({
					url: contextPath + "/application/start",
					type: "post",
					message: PlatformUI.message,
					data: {appInstanceIds:startableAppGridIds},
					afterOperation: function(){
						PlatformUI.refreshGrid(startableAppGrid, {sortname:"ai.createDate",sortorder:"desc"});
						PlatformUI.refreshGrid(deployedStaticMachineGrid, {sortname:"ai.createDate",sortorder:"desc"});
					}
				});
			},
			startAll: function(){//启动全部应用
				this.$confirm('是否要启动全部应用', '提示', {
		          confirmButtonText: '确定',
		          cancelButtonText: '取消',
		          type: 'warning'
		        }).then(function(){
		        	PlatformUI.ajax({
						url: contextPath + "/application/startAll",
						type: "post",
						message: PlatformUI.message,
						data: {appPrimary: $("#id").val()},
						afterOperation: function(){
							PlatformUI.refreshGrid(startableAppGrid, {sortname:"ai.createDate",sortorder:"desc"});
							PlatformUI.refreshGrid(deployedStaticMachineGrid, {sortname:"ai.createDate",sortorder:"desc"});
						}
					});
		        });
			},			
			allDeploy: function(){//将jar包部署到所有物理机实例中
				this.$confirm('是否要将应用部署到所有物理机实例中', '提示', {
		          confirmButtonText: '确定',
		          cancelButtonText: '取消',
		          type: 'warning'
		        }).then(function(){
		        	PlatformUI.ajax({
						url: contextPath + "/application/deployAll",
						type: "post",
						message: PlatformUI.message,
						data: {appPrimary: $("#id").val()},
						afterOperation: function(){
							PlatformUI.refreshGrid(unDeployMachineGrid, {sortname:"m.createDate",sortorder:"desc"});
							PlatformUI.refreshGrid(deployedMachineGrid, {sortname:"m.createDate",sortorder:"desc"});
							PlatformUI.refreshGrid(deployedStaticMachineGrid, {sortname:"ai.createDate",sortorder:"desc"});
						}
					});
		        });
			},
			deploy: function(){//选择部署到指定的物理机中
				var unDeployMachineGridIds = unDeployMachineGrid.jqGrid ('getGridParam', 'selarrrow');
				var deployedMachineGridIds = deployedMachineGrid.jqGrid ('getGridParam', 'selarrrow');
				if(unDeployMachineGridIds.length == 0 && deployedMachineGridIds.length == 0){
					PlatformUI.message({message:"请至少选择一条要操作的数据!", type:"warning"});
					return;
				}
				unDeployMachineGridIds.push.apply(unDeployMachineGridIds, deployedMachineGridIds);
				PlatformUI.ajax({
					url: contextPath + "/application/deploy",
					type: "post",
					message: PlatformUI.message,
					data: {appPrimary: $("#id").val(), mids:unDeployMachineGridIds},
					afterOperation: function(){
						PlatformUI.refreshGrid(unDeployMachineGrid, {sortname:"m.createDate",sortorder:"desc"});
						PlatformUI.refreshGrid(deployedMachineGrid, {sortname:"m.createDate",sortorder:"desc"});
						PlatformUI.refreshGrid(deployedStaticMachineGrid, {sortname:"ai.createDate",sortorder:"desc"});
					}
				});
			},
			showDeployDialog: function(){
				this.dialogDeployVisible = true
				setTimeout(function(){
					initUnDeployMachineGrid()
					initDeployedMachineGrid()
				}, 500);
			},
			beforeUploadJar: function(file){//上传jar文件之前的简单校验
				var fileName = file.name;
				var index = fileName.lastIndexOf(".");
 				var ext = fileName.substr(index+1);
				if (ext == 'jar' || ext == 'zip'){
					return true;
				}else{
					PlatformUI.message({type:"warning", message: "上传文件必须是jar或者zip"});
					return false;	
				}
			},
			uploadJarSuccess: function(response, file, fileList){//上传文件成功回调
				this.dialogUploadVisible = false;
				if (response == "success"){
					PlatformUI.message({type:"success", message: "上传成功"});
					setTimeout(function(){
						location.reload();
					}, 1000);
				}else{
					PlatformUI.message({type:"danger", message: "上传失败"});
				}
				this.uploadFileList = [];
			},
			uploadJarError: function(err, file, fileList){
				this.dialogUploadVisible = false;
				PlatformUI.message({type:"danger", message: "上传失败"});
				this.uploadFileList = [];
			},
			returnMainList: function(){
				location.href = contextPath + "/application/index";
			}
		}
	});
	
	if (!$("#spanJarName").html()){
		vue.button.start.disabled = true;
		vue.button.deploy.disabled = true;
		vue.button.stop.disabled = true;
		vue.button.rollback.disabled = true;
	}
	
	//初始化已发布的物理机实例列表(静态指得是物理机的状态可以为空)
	initDeployedStaticMachineGrid();
	
});

/***********************方法区***************************/

function initUnDeployMachineGrid(){
	if (unDeployMachineGrid){
		PlatformUI.refreshGrid(unDeployMachineGrid, {sortname:"m.createDate",sortorder:"desc"});
		return;	
	}
	unDeployMachineGrid = $("#unDeployMachineList").jqGrid({
        url: contextPath + "/machineInstance/list/forUnDeployApp?appId=" + $("#appId").val(),
        datatype: "json",
        autowidth: true,
        height:150,
        mtype: "GET",
        multiselect: true,
        colNames: ['ID','实例名称','CPU/内存','内网IP','外网IP','mac地址','创建时间'],
        colModel: [
			{ name: 'id', index:'m.id',hidden: true},
			{ name: 'machineInstanceName', index:'machineInstanceName', align:'center', sortable: true},
			{ name: 'cpuAndMemory', index:'cpuAndMemory', align:'center', sortable: true},
			{ name: 'innerIP', index:'innerIP', align:'center', sortable: true},
			{ name: 'outterIP', index:'outterIP', align:'center', sortable: true},
			{ name: 'macAddress', index:'macAddress', align:'center', sortable: true},
			{ name: 'createDate', index:'m.createDate',align:'center', expType:'date',expValue:'yyyy-MM-dd',searchoptions:{dataInit:PlatformUI.defaultJqueryUIDatePick}, sortable: true ,formatter:'date',formatoptions: { srcformat: 'U', newformat: 'Y-m-d H:i:s' }}
        ],
        pager: "#unDeployMachinePager",
        rowNum: 10,
        rowList: [10,20,30],
        sortname: "m.createDate",
        sortorder: "desc",
        viewrecords: true,
        gridview: true,
        autoencode: true,
        caption: "未部署的物理机实例"
    });
    setInterval(function(){
    	PlatformUI.refreshGrid(unDeployMachineGrid, {sortname:"m.createDate",sortorder:"desc"});
    }, 10000);
}

function initDeployedMachineGrid(){
	if (deployedMachineGrid){
		PlatformUI.refreshGrid(deployedMachineGrid, {sortname:"m.createDate",sortorder:"desc"});
		return;	
	}
	deployedMachineGrid = $("#deployedMachineList").jqGrid({
        url: contextPath + "/machineInstance/list/forDeployedApp?appId=" + $("#appId").val(),
        datatype: "json",
        autowidth: true,
        height:150,
        mtype: "GET",
        multiselect: true,
        colNames: ['ID','实例名称','CPU/内存','内网IP','外网IP','mac地址','程序包','应用运行状态','创建时间'],
        colModel: [
			{ name: 'id', index:'id',hidden: true},
			{ name: 'machineInstanceName', index:'machineInstanceName', align:'center', sortable: true},
			{ name: 'cpuAndMemory', index:'cpuAndMemory', align:'center', sortable: true},
			{ name: 'innerIP', index:'innerIP', align:'center', sortable: true},
			{ name: 'outterIP', index:'outterIP', align:'center', sortable: true},
			{ name: 'macAddress', index:'macAddress', align:'center', sortable: true},
			{ name: 'jarName', index:'jarName', align:'center', sortable: true},
			{ name: "status",align:'center',index:"status", sortable: true, searchable:false, formatter: appExecuteStatusFormatter},
			{ name: 'createDate', index:'m.createDate',align:'center', expType:'date',expValue:'yyyy-MM-dd',searchoptions:{dataInit:PlatformUI.defaultJqueryUIDatePick}, sortable: true ,formatter:'date',formatoptions: { srcformat: 'U', newformat: 'Y-m-d H:i:s' }}
        ],
        pager: "#deployedMachinePager",
        rowNum: 10,
        rowList: [10,20,30],
        sortname: "m.createDate",
        sortorder: "desc",
        viewrecords: true,
        gridview: true,
        rowattr: function (rd) {
		    if (rd.status == 0) {
		        return {"class": "color-red"};
		    }else if (rd.status == 1){
		    	return {"class": "color-green"};
		    }else if (rd.status == 2 || rd.status == 3 || rd.status == 4){
		   	 	return {"class": "color-yellow"};
		    }
		},
        autoencode: true,
        caption: "已部署的物理机实例"
    });
    setInterval(function(){
    	PlatformUI.refreshGrid(deployedMachineGrid, {sortname:"m.createDate",sortorder:"desc"});
    }, 10000);
}

function initDeployedStaticMachineGrid(){
	if (deployedStaticMachineGrid){
		PlatformUI.refreshGrid(deployedStaticMachineGrid, {sortname:"ai.createDate",sortorder:"desc"});
		return;	
	}
	deployedStaticMachineGrid = $("#list").jqGrid({
        url: contextPath + "/machineInstance/list/forDeployedStaticApp?appId=" + $("#appId").val(),
        datatype: "json",
        autowidth: true,
        height:150,
        mtype: "GET",
        multiselect: true,
        colNames: ['ID','实例名称','CPU/内存','内网IP','外网IP','mac地址','程序包','应用运行状态','agent状态','agent版本','创建时间'],
        colModel: [
			{ name: 'id', index:'id',hidden: true},
			{ name: 'machineInstanceName', index:'machineInstanceName', align:'center', sortable: true},
			{ name: 'cpuAndMemory', index:'cpuAndMemory', align:'center', sortable: true},
			{ name: 'innerIP', index:'innerIP', align:'center', sortable: true},
			{ name: 'outterIP', index:'outterIP', align:'center', sortable: true},
			{ name: 'macAddress', index:'macAddress', align:'center', sortable: true},
			{ name: 'jarName', index:'jarName', align:'center', sortable: true},
			{ name: "status",align:'center',index:"status", sortable: true, searchable:false, formatter: appExecuteStatusFormatter},
			{ name: 'agentStatus', index:'agentStatus', align:'center', sortable: true},
			{ name: 'agentVersion', index:'agentVersion', align:'center', sortable: true},
			{ name: 'createDate', index:'ai.createDate',align:'center', expType:'date',expValue:'yyyy-MM-dd',searchoptions:{dataInit:PlatformUI.defaultJqueryUIDatePick}, sortable: true ,formatter:'date',formatoptions: { srcformat: 'U', newformat: 'Y-m-d H:i:s' }}
        ],
        pager: "#pager",
        rowNum: 10,
        rowList: [10,20,30],
        sortname: "ai.createDate",
        sortorder: "desc",
        viewrecords: true,
        gridview: true,
        rowattr: function (rd) {
		    if (rd.status == 0) {
		        return {"class": "color-red"};
		    }else if (rd.status == 1){
		    	return {"class": "color-green"};
		    }else if (rd.status == 2 || rd.status == 3 || rd.status == 4 || rd.status == 5){
		   	 	return {"class": "color-yellow"};
		    }
		},
        autoencode: true,
        caption: "已部署的物理机实例"
    });
    setInterval(function(){
    	PlatformUI.refreshGrid(deployedStaticMachineGrid, {sortname:"ai.createDate",sortorder:"desc"});
    }, 10000);
}

function initStartableAppGrid(){
	if (startableAppGrid){
		PlatformUI.refreshGrid(startableAppGrid, {sortname:"ai.createDate",sortorder:"desc"});
		return;	
	}
	startableAppGrid = $("#startableAppList").jqGrid({
        url: contextPath + "/appInstance/list/forStartableApp?appId=" + $("#appId").val(),
        datatype: "json",
        autowidth: true,
        height:150,
        mtype: "GET",
        multiselect: true,
        colNames: ['ID','实例名称','CPU/内存','内网IP','外网IP','mac地址','程序包','应用运行状态','创建时间'],
        colModel: [
			{ name: 'id', index:'id',hidden: true},
			{ name: 'machineInstanceName', index:'machineInstanceName', align:'center', sortable: true},
			{ name: 'cpuAndMemory', index:'cpuAndMemory', align:'center', sortable: true},
			{ name: 'innerIP', index:'innerIP', align:'center', sortable: true},
			{ name: 'outterIP', index:'outterIP', align:'center', sortable: true},
			{ name: 'macAddress', index:'macAddress', align:'center', sortable: true},
			{ name: 'jarName', index:'jarName', align:'center', sortable: true},
			{ name: "status",align:'center',index:"status", sortable: true, searchable:false, formatter: appExecuteStatusFormatter},
			{ name: 'createDate', index:'ai.createDate',align:'center', expType:'date',expValue:'yyyy-MM-dd',searchoptions:{dataInit:PlatformUI.defaultJqueryUIDatePick}, sortable: true ,formatter:'date',formatoptions: { srcformat: 'U', newformat: 'Y-m-d H:i:s' }}
        ],
        pager: "#startableAppPager",
        rowNum: 10,
        rowList: [10,20,30],
        sortname: "ai.createDate",
        sortorder: "desc",
        viewrecords: true,
        gridview: true,
        rowattr: function (rd) {
		    if (rd.status == 0) {
		        return {"class": "color-red"};
		    }else if (rd.status == 1){
		    	return {"class": "color-green"};
		    }else if (rd.status == 2 || rd.status == 3 || rd.status == 4 || rd.status == 5){
		   	 	return {"class": "color-yellow"};
		    }
		},
        autoencode: true,
        caption: "可启动的物理机实例"
    });
    setInterval(function(){
    	PlatformUI.refreshGrid(startableAppGrid, {sortname:"ai.createDate",sortorder:"desc"});
    }, 10000);
}

function initStoppableAppGrid(){
	if (stoppableAppGrid){
		PlatformUI.refreshGrid(stoppableAppGrid, {sortname:"ai.createDate",sortorder:"desc"});
		return;	
	}
	stoppableAppGrid = $("#stppableAppList").jqGrid({
        url: contextPath + "/appInstance/list/forStoppableApp?appId=" + $("#appId").val(),
        datatype: "json",
        autowidth: true,
        height:150,
        mtype: "GET",
        multiselect: true,
        colNames: ['ID','实例名称','CPU/内存','内网IP','外网IP','mac地址','程序包','应用运行状态','创建时间'],
        colModel: [
			{ name: 'id', index:'id',hidden: true},
			{ name: 'machineInstanceName', index:'machineInstanceName', align:'center', sortable: true},
			{ name: 'cpuAndMemory', index:'cpuAndMemory', align:'center', sortable: true},
			{ name: 'innerIP', index:'innerIP', align:'center', sortable: true},
			{ name: 'outterIP', index:'outterIP', align:'center', sortable: true},
			{ name: 'macAddress', index:'macAddress', align:'center', sortable: true},
			{ name: 'jarName', index:'jarName', align:'center', sortable: true},
			{ name: "status",align:'center',index:"status", sortable: true, searchable:false, formatter: appExecuteStatusFormatter},
			{ name: 'createDate', index:'ai.createDate',align:'center', expType:'date',expValue:'yyyy-MM-dd',searchoptions:{dataInit:PlatformUI.defaultJqueryUIDatePick}, sortable: true ,formatter:'date',formatoptions: { srcformat: 'U', newformat: 'Y-m-d H:i:s' }}
        ],
        pager: "#stppableAppPager",
        rowNum: 10,
        rowList: [10,20,30],
        sortname: "ai.createDate",
        sortorder: "desc",
        viewrecords: true,
        gridview: true,
        rowattr: function (rd) {
		    if (rd.status == 0) {
		        return {"class": "color-red"};
		    }else if (rd.status == 1){
		    	return {"class": "color-green"};
		    }else if (rd.status == 2 || rd.status == 3 || rd.status == 4 || rd.status == 5){
		   	 	return {"class": "color-yellow"};
		    }
		},
        autoencode: true,
        caption: "可停止的物理机实例"
    });
    setInterval(function(){
    	PlatformUI.refreshGrid(stoppableAppGrid, {sortname:"ai.createDate",sortorder:"desc"});
    }, 10000);
}

function initDeploySerialGrid(mid){
	if (deploySerialGrid){
		PlatformUI.refreshGrid(deploySerialGrid, {
			url: contextPath + "/deploySerial/pageList/" + $("#id").val() + "?mid=" + mid,
			sortname:"d.deployDate",
			sortorder:"desc"
		});
		return;	
	}
	deploySerialGrid = $("#deploySerialList").jqGrid({
        url: contextPath + "/deploySerial/pageList/" + $("#id").val() + "?mid=" + mid,
        datatype: "json",
        autowidth: true,
        height:150,
        mtype: "GET",
        multiselect: true,
        colNames: ['ID','jarRealName','jar名称','发布日期'],
        colModel: [
			{ name: 'id', index:'id',hidden: true},
			{ name: 'jarRealName', index:'jarRealName',hidden: true},
			{ name: 'jarName', index:'jarName', align:'center', sortable: true},
			{ name: 'deployDate', index:'d.deployDate',align:'center', expType:'date',expValue:'yyyy-MM-dd',searchoptions:{dataInit:PlatformUI.defaultJqueryUIDatePick}, sortable: true ,formatter:'date',formatoptions: { srcformat: 'U', newformat: 'Y-m-d H:i:s' }}
        ],
        pager: "#deploySerialPager",
        rowNum: 10,
        rowList: [10,20,30],
        sortname: "d.deployDate",
        sortorder: "desc",
        viewrecords: true,
        gridview: true,
        autoencode: true,
        caption: "发布流水"
    });
}

function initDeploySerialMachineGrid(){
	if (deploySerialMachineGrid){
		PlatformUI.refreshGrid(deploySerialMachineGrid, {sortname:"m.createDate",sortorder:"desc"});
		return;	
	}
	deploySerialMachineGrid = $("#deploySerialMachineList").jqGrid({
        url: contextPath + "/machineInstance/list/forDeploySerial?appPrimary=" + $("#id").val(),
        datatype: "json",
        autowidth: true,
        height:150,
        mtype: "GET",
        multiselect: false,
        colNames: ['ID','实例名称','CPU/内存','内网IP','外网IP','mac地址','创建时间'],
        colModel: [
			{ name: 'id', index:'m.id',hidden: true},
			{ name: 'machineInstanceName', index:'machineInstanceName', align:'center', sortable: true},
			{ name: 'cpuAndMemory', index:'cpuAndMemory', align:'center', sortable: true},
			{ name: 'innerIP', index:'innerIP', align:'center', sortable: true},
			{ name: 'outterIP', index:'outterIP', align:'center', sortable: true},
			{ name: 'macAddress', index:'macAddress', align:'center', sortable: true},
			{ name: 'createDate', index:'m.createDate',align:'center', expType:'date',expValue:'yyyy-MM-dd',searchoptions:{dataInit:PlatformUI.defaultJqueryUIDatePick}, sortable: true ,formatter:'date',formatoptions: { srcformat: 'U', newformat: 'Y-m-d H:i:s' }}
        ],
        pager: "#deploySerialMachinePager",
        rowNum: 10,
        rowList: [10,20,30],
        sortname: "m.createDate",
        sortorder: "desc",
        viewrecords: true,
        gridview: true,
        autoencode: true,
        onSelectRow: function(id){
        	initDeploySerialGrid(id);
        },
        caption: "曾经部署过的机器"
    });
}

function appExecuteStatusFormatter(cellvalue, options, rowObject){
	if(cellvalue == -1){
		return "未知";
	}else if(cellvalue == 0){
		return "已停止";
	}else if(cellvalue == 1){
		return "运行中";
	}else if(cellvalue == 2){
		return "停止中";
	}else if(cellvalue == 3){
		return "部署中";
	}else if(cellvalue == 4){
		return "删除中";
	}else if(cellvalue == 5){
		return "启动中";
	}else{
		return "未知";
	}
}

//已部署的物理机实例grid搜索
function deployedMachineGridSearch(){
	var value;
	if (vue.grid.deployedMachine.searchVal != ""){
		value = vue.grid.deployedMachine.searchVal.trim();
	}else{
		value = vue.grid.deployedMachine.searchVal;
	}
	var rules = [
			{"field":"machineInstanceName","op":"cn","data":value}
			];
	var filters = {"groupOp":"AND","rules":rules};
	deployedMachineGrid.jqGrid("setGridParam", {
		postData: {filters:JSON.stringify(filters)},
		page: 1
	}).trigger("reloadGrid");
}

//未部署的物理机实例grid搜索
function unDeployMachineGridSearch(){
	if (vue.grid.unDeployMachine.searchVal != ""){
		value = vue.grid.unDeployMachine.searchVal.trim();
	}else{
		value = vue.grid.unDeployMachine.searchVal;
	}
	var rules = [
			{"field":"machineInstanceName","op":"cn","data":value}
			];
	var filters = {"groupOp":"AND","rules":rules};
	unDeployMachineGrid.jqGrid("setGridParam", {
		postData: {filters:JSON.stringify(filters)},
		page: 1
	}).trigger("reloadGrid");
}

//可运行的物理机列表搜索
function startableGridSearch(){
	if (vue.grid.startable.searchVal != ""){
		value = vue.grid.startable.searchVal.trim();
	}else{
		value = vue.grid.startable.searchVal;
	}
	var rules = [
			{"field":"m.machineInstanceName","op":"cn","data":value}
			];
	var filters = {"groupOp":"AND","rules":rules};
	startableAppGrid.jqGrid("setGridParam", {
		postData: {filters:JSON.stringify(filters)},
		page: 1
	}).trigger("reloadGrid");
}

//可停止的物理机列表搜索
function stoppableGridSearch(){
	if (vue.grid.stoppable.searchVal != ""){
		value = vue.grid.stoppable.searchVal.trim();
	}else{
		value = vue.grid.stoppable.searchVal;
	}
	var rules = [
			{"field":"m.machineInstanceName","op":"cn","data":value}
			];
	var filters = {"groupOp":"AND","rules":rules};
	stoppableAppGrid.jqGrid("setGridParam", {
		postData: {filters:JSON.stringify(filters)},
		page: 1
	}).trigger("reloadGrid");
}

//发不过流水的机器列表搜索
function deploySerialMachineGridSearch(){
	if (vue.grid.deploySerialMachine.searchVal != ""){
		value = vue.grid.deploySerialMachine.searchVal.trim();
	}else{
		value = vue.grid.deploySerialMachine.searchVal;
	}
	var rules = [
			{"field":"m.machineInstanceName","op":"cn","data":value}
			];
	var filters = {"groupOp":"AND","rules":rules};
	deploySerialMachineGrid.jqGrid("setGridParam", {
		postData: {filters:JSON.stringify(filters)},
		page: 1
	}).trigger("reloadGrid");
}

