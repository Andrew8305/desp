var vue;
var unDeployMachineGrid;
var deployedMachineGrid;

$(function(){
	
	vue = new Vue({
		el: '#app',
		data: function(){
			return {
				uploadUrl:contextPath + "/application/upload?id=" + $("#id").val(),
				dialogUploadVisible: false,
				dialogDeployVisible: false,
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
					}
				}
			};
		},
		methods:{
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
						}
					});
		        });
			},
			deploy: function(){//选择部署到指定的物理机中
				var unDeployMachineGridIds = unDeployMachineGrid.jqGrid ('getGridParam', 'selarrrow');
				var deployedMachineGridIds = deployedMachineGrid.jqGrid ('getGridParam', 'selarrrow');
				if(unDeployMachineGridIds.length == 0 && deployedMachineGridIds.length == 0){
					PlatformUI.message({message:"请至少选择一条要删除的数据!", type:"warning"});
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
