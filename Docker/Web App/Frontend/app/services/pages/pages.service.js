(function () {
	angular.module("app.services")
	.service("pages", Service);
	
	Service.$inject = ['$state'];
	function Service($state){
		var service = this;
		
		service.home = function(params){
			$state.go("home", params);
		};
		
		service.allDetectedSmiles = function(params){
			$state.go("allSmiles", params);
		};
		
		service.webcam = function(params){
			$state.go("webcam", params);
		};

		service.download = function(params){
			$state.go("download", params);
		};

		service.error = function(error){
			$state.go("error", {
				error: error
			});
		};	
		
		service.isPageOpened = function(stateName){
			return $state.is(stateName);
		};
		
		service.getCurrentPagetitle = function(){
			var title = $state.params.title;
			var result = $translate.instant("application.title");
			
			if(title){
				result = $translate.instant(title) + " - " + result;
			}
			return result;
		};
		
		////////////////////
		
		function parseUrlParameter(parameter){
			var result = null;
			if(parameter){
				result = parameter.toString().toLowerCase().replace(/\s+/g, "-");
			}
			return result;
		}
	}
}());