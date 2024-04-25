(function () {
	angular.module("app.components")
	.directive("devPanel", Directive);
	
	function Directive(){
		 return {
	    	restrict: 'E',
	    	templateUrl: 'app/components/dev-panel/dev-panel.directive.html',
	    	controller: DirectiveController,
	    	controllerAs: 'ctrl',
	    	bindToController: true,
	    	scope: {
	    		autoRefresh: '='
	    	}
	    };
	}
	
	DirectiveController.$inject = ['$timeout', '$scope', 'devPanelService'];
	function DirectiveController($timeout, $scope, devPanelService){
		var ctrl = this;
		var root = angular.element(document.getElementsByTagName('body'));
		var watchersRefreshing = false;
		var unregisterRefreshEvent = null;
		
		ctrl.watchers = 0;
		ctrl.watchersWithoutDuplicates = 0;
		ctrl.domElements = 0;
		ctrl.isLoading = isLoading;
		ctrl.panelEnabled = true;
		
		ctrl.refresh = refresh;
		ctrl.toggleEnabled = toggleEnabled;
		
		if(ctrl.autoRefresh != false){
			unregisterRefreshEvent = $scope.$watch(refresh);
			$scope.$on("$destroy", function(){unregisterRefreshEvent();});
		}

		///////////////////////
		
		function refresh(){
			refreshWatchers();
			refreshDomElements();
		}
		
		function refreshDomElements(){
			ctrl.domElements = devPanelService.countDomElements();
		}
		
		function refreshWatchers(){
			watchersRefreshing = true;
			
			var watchers = devPanelService.countWatchers(root);
			ctrl.watchers = watchers.watchers;
			ctrl.watchersWithoutDuplicates = watchers.watchersWithoutDuplicates; 
			
			watchersRefreshing = false;
		}

	    function isLoading(){
	    	return watchersRefreshing;
	    }
	    
	    function toggleEnabled(){
	    	ctrl.panelEnabled = !ctrl.panelEnabled;
	    	if(ctrl.panelEnabled){
	    		unregisterRefreshEvent = $scope.$watch(refresh);
	    	}
	    	else if(unregisterRefreshEvent != null){
	    		unregisterRefreshEvent();
	    	}
	    }
	}
}());