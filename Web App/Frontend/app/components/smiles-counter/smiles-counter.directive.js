(function () {
	angular.module("app.components")
	.directive("smilesCounter", Directive);
	
	function Directive(){
		 return {
	    	restrict: 'E',
	    	templateUrl: 'app/components/smiles-counter/smiles-counter.directive.html',
	    	controller: DirectiveController,
	    	controllerAs: 'ctrl',
	    	bindToController: true,
	    	scope: {
	    		'positions' : '=',
	    		'refreshTime' : '='
	    	}
	    };
	}
	
	var types = {
		GLOBAL : "global",
		LOCAL : "local"
	};
	
	DirectiveController.$inject = ['rest', '$interval', '$scope'];
	function DirectiveController(rest, $interval, $scope){
		var ctrl = this;	
		var resfreshInterval = null;	
		var smilesCount = 0;
		
		ctrl.type = types.GLOBAL;
		ctrl.getSmilesCount = getSmilesCount;
		
		init();
		
		////////////////////////////
		
		function init(){
			refreshSmilesCounter();
			refreshInterval = $interval(refreshSmilesCounter, (ctrl.refreshTime || 10) * 1000);
		}
		
		function refreshSmilesCounter(){
			rest.smiles.counter().then(function(data){
				smilesCount = data;
			});
		}
		
		function stopRefreshInterval(){
			if (angular.isDefined(refreshInterval)) {
				$interval.cancel(refreshInterval);
				refreshInterval = undefined;
			}
		}
		
		function getSmilesCount(){
			var result = "";
			if(ctrl.positions != null){
				var digits = smilesCount.toString().length;
			
				for(var i = 0; i < ctrl.positions - digits; i++){
					result += "0";
				}
			}
			
			result += smilesCount.toString();
			return result;
		}
		
		$scope.$on('$destroy', function() {
			stopRefreshInterval();
        });
	}
}());