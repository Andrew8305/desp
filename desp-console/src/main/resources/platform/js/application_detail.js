var grid;
var vue;

$(function(){
	
	vue = new Vue({
		el: '#app',
		data: function(){
			return {
				uploadUrl:contextPath + "/application/upload?id=" + $("#id").val(),
				dialogUploadVisible: false,
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
					del: {
						disabled: false
					}
				}
			};
		},
		methods:{
			beforeUploadJar: function(file){//上传jar文件之前的简单校验
				var fileName = file.name;
				var index = fileName.lastIndexOf(".");
 				var ext = fileName.substr(index+1);
				if (ext != 'jar'){
					PlatformUI.message({type:"warning", message: "上传文件必须是jar"});
					return false;				
				}
				return true;
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
	}
	
	
});

/***********************方法区***************************/


