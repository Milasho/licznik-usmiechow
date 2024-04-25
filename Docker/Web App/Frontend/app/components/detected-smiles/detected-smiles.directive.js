(function () {
	angular.module("app.components")
	.directive("detectedSmiles", Directive);
	
	function Directive(){
		 return {
	    	restrict: 'E',
	    	templateUrl: 'app/components/detected-smiles/detected-smiles.directive.html',
	    	controller: DirectiveController,
	    	controllerAs: 'ctrl',
	    	bindToController: true,
	    	scope: {
	    		maxImages: '='
	    	}
	    };
	}
	
	DirectiveController.$inject = ['rest', 'pages', '$interval', 'config', '$scope'];
	function DirectiveController(rest, pages, $interval, config, $scope){
		var ctrl = this;
		var loading = false;
		var allSmiles = 0;
		var allSmilesWithPhoto = 0;

		ctrl.smilesToday = 0;
		ctrl.smilesWeek = 0;
		ctrl.smilesMonth = 0;

        ctrl.bestLocalisations = [];
		ctrl.detectedSmiles = null;
		ctrl.imagesLoading = false;

		ctrl.isLoading = isLoading;
		ctrl.showMoreButton = showMoreButton;
		ctrl.getSmilesCount = getSmilesCount;
		ctrl.showAll = showAll;
		
		init();
		
		//////////////////////
		
		function init(){
            refreshAll();
			var refreshTime = config.refreshTime;
            var refreshInterval = $interval(refreshAll, refreshTime * 1000);
            $scope.$on("$destroy", function(){
                if (angular.isDefined(refreshInterval)) {
                    $interval.cancel(refreshInterval);
                    refreshInterval = undefined;
                }
			})
		}

		function refreshAll(){
            refreshDetectedSmiles();
            refreshAllSmiles();
            refreshSmilesFromToday();
            refreshSmilesFromWeek();
            refreshSmilesFromMonth();
            refreshBestLocalisations();
            refreshSmilesWithPhoto();
		}

		function refreshBestLocalisations(){
            return rest.smiles.bestLocalisations(5).then(function(data){
            	ctrl.bestLocalisations = data;
			});
		}

        function refreshSmilesFromToday() {
			var now = new Date();
			var from = new Date(now.getFullYear(), now.getMonth(), now.getDate() - 1);
			var to = new Date(now.getFullYear(), now.getMonth(), now.getDate() + 1);

			loading = true;
			return rest.smiles.counterRange(from, to).then(function(data){
                ctrl.smilesToday = data || 0;
                loading = false;
			});
        }

        function refreshSmilesFromWeek() {
            var now = new Date();
            var from = new Date(now.getFullYear(), now.getMonth(), now.getDate() - 7);
            var to = new Date(now.getFullYear(), now.getMonth(), now.getDate() + 1);

            loading = true;
            return rest.smiles.counterRange(from, to).then(function(data){
                ctrl.smilesWeek = data || 0;
                loading = false;
            });
        }

        function refreshSmilesFromMonth() {
            var now = new Date();
            var from = new Date(now.getFullYear(), now.getMonth() - 1, now.getDate());
            var to = new Date(now.getFullYear(), now.getMonth(), now.getDate() + 1);

            loading = true;
            return rest.smiles.counterRange(from, to).then(function(data){
                ctrl.smilesMonth = data || 0;
                loading = false;
            });
        }

		function refreshDetectedSmiles(){
			loading = true;
            ctrl.imagesLoading = true;
			rest.smiles.get(0, 10, true).then(function(data){
				ctrl.detectedSmiles = data;
				loading = false;
                ctrl.imagesLoading = false;
			});
		}
		
		function refreshAllSmiles(){
			rest.smiles.counter().then(function(data){
				allSmiles = data;
			});
		}

        function refreshSmilesWithPhoto() {
            loading = true;
            return rest.smiles.photosCounter(true).then(function(data){
            	allSmilesWithPhoto = data;
                loading = false;
            });
        }
		
		function isLoading(){
			return loading;
		}
		
		function showMoreButton(){
			return allSmilesWithPhoto > 0 && ctrl.maxImages && ctrl.maxImages < allSmilesWithPhoto
				&& ctrl.detectedSmiles != null && ctrl.detectedSmiles.length > 0
				&& ctrl.detectedSmiles.length > allSmilesWithPhoto;
		}
		
		function getSmilesCount(){
			return allSmiles;
		}
		
		function showAll(){
			pages.allDetectedSmiles();
		}
	}
}());