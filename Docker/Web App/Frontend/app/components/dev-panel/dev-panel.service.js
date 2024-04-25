(function () {
	angular.module("app.services")
	.service("devPanelService", Service);
	
	Service.$inject = ['$rootScope'];
	function Service($rootScope){
		var service = this;
		
		service.countWatchers = countWatchers;
		service.countDomElements = countDomElements;

		/////////////////////
		
		function countDomElements(element){
			var el = element || document;
			return el.getElementsByTagName('*').length;
		}
		
		function countWatchers(element, watchersArray, watchersWithoutDuplicatesArray) {
			var watchers = watchersArray || [];
			var watchersWithoutDuplicates = watchersWithoutDuplicatesArray || [];
			
			// Count all watchers
	        angular.forEach(['$scope', '$isolateScope'], function (scopeProperty) { 
	            if (element.data() && element.data().hasOwnProperty(scopeProperty)) {
	                angular.forEach(element.data()[scopeProperty].$$watchers, function (watcher) {
	                    watchers.push(watcher);
	                });
	            }
	        });

	        angular.forEach(element.children(), function (childElement) {
	        	countWatchers(angular.element(childElement), watchers, watchersWithoutDuplicates);
	        });
	        
	        // Count watchers without duplicates
	        angular.forEach(watchers, function(item) {
	            if(watchersWithoutDuplicates.indexOf(item) < 0) {
	                 watchersWithoutDuplicates.push(item);
	            }
	        });
	        
	        return {
	        	watchers: watchers.length,
	        	watchersWithoutDuplicates: watchersWithoutDuplicates.length
	        }
	    };
	}
}());