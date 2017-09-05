var vue;
var agentInfo;

$(function(){

	vue = new Vue({
		el: "#app",
		data: function(){
			return {
				cpus: []
			};
		},
		methods: {
			returnMainList: function(){
				location.href = contextPath + "/machineInstance/index";	
			}
		}
	});
	
	PlatformUI.ajax({
		url: contextPath + "/machineInstance/monitor/" + $("#id").val(),
		afterOperation: function(data){
			agentInfo = data;
			if (agentInfo.cpus && agentInfo.cpus.length > 0){
				for (var i = 0; i < agentInfo.cpus.length; i++) {
					vue.cpus.push(agentInfo.cpus[i]);	
				}
				setTimeout(function(){
					initCpuChart(agentInfo);
					initMemoryChart(agentInfo);
				}, 2000);
			}
		}
	});
    
	setInterval(function(){
		location.reload();	
	}, 25000);
});

function initCpuChart(agentInfo){
	for (var i = 0; i < agentInfo.cpus.length; i++) {
		var cpu = agentInfo.cpus[i];
		// 基于准备好的dom，初始化echarts实例
		var cpuChart = echarts.init(document.getElementById('cpuChart' + (i + 1)));
		// 指定图表的配置项和数据
	    option = {
	    	title : [
	    		{
		        text: '第' + (i+1) + '块cpu',
		        left:'25%',
	            top:'1%'
		    }
	    	],
	    	tooltip : {
		        trigger: 'item',
		        formatter: "{b} : {d}%"
		    },
		    color:['#F7BA2A', '#FF4949', '#8492A6'],
		    series : [
		        {
		            type: 'pie',
		            radius : '60%',
		            center: ['50%', '50%'],
		            data:[
		                {value:cpu.sysTime, name:'系统使用率'},
		                {value:cpu.userTime, name:'用户使用率'},
		                {value:cpu.idleTime, name:'空闲使用率'}
		            ],
		            itemStyle: {
		                emphasis: {
		                    shadowBlur: 10,
		                    shadowOffsetX: 0,
		                    shadowColor: 'rgba(0, 0, 0, 0.5)'
		                }
		            }
		        }
		    ]
		};
	    // 使用刚指定的配置项和数据显示图表。
	    cpuChart.setOption(option);
	}
}

function initMemoryChart(agentInfo){
	var memChart = echarts.init(document.getElementById("memChart"));
	// 指定图表的配置项和数据
    option = {
    	title : [
    		{
	        text: '系统内存(' + agentInfo.memory.totalUnitG + ')',
	        x:'center'
	    }
    	],
    	tooltip : {
	        trigger: 'item',
	        formatter: "{b} : {d}%"
	    },
	    color:['#FF4949', '#8492A6'],
	    series : [
	        {
	            type: 'pie',
	            radius : '60%',
	            center: ['50%', '50%'],
	            data:[
	                {value:agentInfo.memory.used, name:'已使用(' + agentInfo.memory.usedUnitG + ')'},
	                {value:agentInfo.memory.free, name:'未使用(' + agentInfo.memory.freeUnitG + ')'}
	            ],
	            itemStyle: {
	                emphasis: {
	                    shadowBlur: 10,
	                    shadowOffsetX: 0,
	                    shadowColor: 'rgba(0, 0, 0, 0.5)'
	                }
	            }
	        }
	    ]
	};
    // 使用刚指定的配置项和数据显示图表。
    memChart.setOption(option);
}