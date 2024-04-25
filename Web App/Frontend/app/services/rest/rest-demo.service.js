(function () {
	angular.module("app.services")
	.service("restDemo", Service);
	
	Service.$inject = ['demoData', '$q', '$timeout'];
	function Service(demoData, $q, $timeout){
		var service = this;
		var demoServices = null;
		var detectedSmiles = null;
		var sentSnapshots = 0;
		
		service.getDemoPromise = getDemoPromise;
		
		init();
		
		////////////////////
		
		function init(){
			registerDemoSerives();
			initDetectedSmiles();
		}
		
		function initDetectedSmiles(){
			detectedSmiles = demoData.detectedSmiles || [];
		}
		
		function registerDemoSerives(){
			demoServices = [
				{type: "GET", pattern: /smiles\/counter/, service: getSmilesCounter},
				{type: "GET", pattern: /smiles($)|(\?start=)/, service: getSmiles},
				{type: "POST", pattern: /smiles\/detect/, service: detectSmilesOnSnapshot},
			];
		}
		
		function getDemoPromise(url, type, params, data){
			var result = null;
			for(var i = 0; i < demoServices.length; i++){
				var pattern = demoServices[i].pattern;
				var serviceType = demoServices[i].type;
				
				if(serviceType == type && pattern.test(url)){
					result = demoServices[i].service(url, params, data);
					break;
				}
			}
			return $q.when(result);
		}
		
		function getSmilesCounter(){
			return detectedSmiles.length;
		}
		
		function getSmiles(url, params, data){
			return detectedSmiles;
		}
		
		function detectSmilesOnSnapshot(url, params, data){
			sentSnapshots = (sentSnapshots + 1) % 10;
			if(sentSnapshots == 0){
				return {
					content: data.content,
					detectedSmiles:[face]
				};
			}
			return null;
		}
	}
}());