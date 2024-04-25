(function () {
	angular.module("app.pages")
	.directive("webcamPage", Directive);
	
	function Directive(){
		 return {
	    	restrict: 'E',
	    	templateUrl: 'app/pages/webcam/webcam.page.directive.html',
	    	controller: DirectiveController,
	    	controllerAs: 'ctrl',
	    	bindToController: true,
	    	scope: {}
	    };
	}

	var SERVICES = {
		OPEN_CV: "OPEN_CV",
		LUXAND: "LUXAND",
		CUSTOM: "CUSTOM",
        OPENIMAJ: "OPENIMAJ"
	};

	var VIEWS = {
		INTRO: 'app/pages/webcam/views/test_intro.html',
        TEST_SERVICE: 'app/pages/webcam/views/test_service.html',
		SUMMARY: 'app/pages/webcam/views/test_summary.html',
		SEND_SNAPSHOTS: 'app/pages/webcam/views/send_snapshots.html',
		TEST_RESULTS: 'app/pages/webcam/views/test_results.html'
	};
	
	DirectiveController.$inject = ['$stateParams', 'rest', 'pages', 'config', '$scope', '$translate'];
	function DirectiveController($stateParams, rest, pages, config, $scope, $translate){
		var ctrl = this;
		ctrl.step = 0;
		ctrl.servicesOrder = config.servicesToTest || [SERVICES.LUXAND, SERVICES.OPEN_CV, SERVICES.CUSTOM, SERVICES.OPENIMAJ];
		ctrl.services = SERVICES;
		ctrl.views = VIEWS;
		ctrl.view = VIEWS.INTRO;
		ctrl.service = $stateParams.service || SERVICES.LUXAND;
		ctrl.statistics = {};
		ctrl.handlers = {};
		ctrl.formData = {};
		ctrl.sendingProcessing = false;
        ctrl.snapshotsSent = false;

		ctrl.startTesting = startTesting;
		ctrl.goToNextStep = goToNextStep;
		ctrl.goToSummary = goToSummary;
		ctrl.sendFormData = sendFormData;
		ctrl.goToSnapshotsView = goToSnapshotsView;
        ctrl.saveSnapshots = saveSnapshots;
		ctrl.goToResultsView = goToResultsView;
		ctrl.endTest = endTest;
		ctrl.getSelectedSmileTypes = getSelectedSmileTypes;

        init();
		////////////////////

		function init(){
			shuffleArray(ctrl.servicesOrder);
		}

		function startTesting(){
            ctrl.step = 0;
            ctrl.service = ctrl.servicesOrder[0];
            ctrl.view = VIEWS.TEST_SERVICE;
            ctrl.statistics = {};
		}

		function goToNextStep(){
			ctrl.statistics[ctrl.service] = ctrl.handlers.getStats();
			ctrl.step++;
			ctrl.service = ctrl.servicesOrder[ctrl.step];
			ctrl.handlers.clear();
		}

		function goToSummary(){
			ctrl.view = VIEWS.SUMMARY;
            ctrl.statistics[ctrl.service] = ctrl.handlers.getStats();
            ctrl.handlers.clear();

            ctrl.formData = {};
            for(var i = 0; i < ctrl.servicesOrder.length; i++){
                ctrl.formData[ctrl.servicesOrder[i]] = {};
            }
		}

		function goToSnapshotsView(){
            ctrl.view = VIEWS.SEND_SNAPSHOTS;
            ctrl.persmissionsToSave = {};

            var scroll = document.getElementById('webcam-scroll');
            if(scroll){
            	Ps.update(scroll);
			}
		}

		function goToResultsView(){
            ctrl.view = VIEWS.TEST_RESULTS;
		}

        function shuffleArray(array) {
            var counter = array.length;
            while (counter > 0) {
                var index = Math.floor(Math.random() * counter);
                counter--;
                var temp = array[counter];
                array[counter] = array[index];
                array[index] = temp;
            }
            return array;
        }

        function sendFormData(){
			var realFormData = prepareFormData();

            ctrl.sendingProcessing = true;
			rest.servicesTest.send(realFormData).then(function(){
                ctrl.sendingProcessing = false;

                if(ctrl.handlers.confirmedSmiles && ctrl.handlers.confirmedSmiles.length > 0){
                    goToSnapshotsView();
				}
				else{
                    goToResultsView();
				}
			});
		}

		function saveSnapshots(withSelected){
        	var snapshotsToSave = [];
			for(var i = 0; i < ctrl.handlers.confirmedSmiles.length; i++){
                snapshotsToSave.push({
					permissionToSave: withSelected && ctrl.persmissionsToSave[i],
					content: ctrl.handlers.confirmedSmiles[i]
				});
			}

			ctrl.snapshotsSent = true;
            rest.smiles.detectAndSave(snapshotsToSave);
            goToResultsView();
		}

		function prepareFormData(){
        	var serviceTestResults = [];

        	for(var i = 0; i < ctrl.servicesOrder.length; i++){
        		serviceTestResults.push(gatherDataAboutService(ctrl.servicesOrder[i]));
			}

        	return {
				gender: ctrl.formData.gender,
				age: ctrl.formData.age,
                affectiveFuture: ctrl.formData.affectiveFuture,
                additionalData: ctrl.formData.additionalData,
				groupingSmiles: ctrl.groupingSmiles,
                serviceTestResults: serviceTestResults
			};
		}

		function endTest(){
			pages.home();
		}

		function getSelectedSmileTypes(types){
			var result = '';
			if(types.wideOpen){
				result += $translate.instant("webcam.page.results.summary.smileTypes.wideOpen");
			}
			if(types.wideOpen && types.open && types.closed){
				result += ", ";
			}
            else if(types.wideOpen && types.open && !types.closed){
                result += $translate.instant("webcam.page.results.summary.smileTypes.and");
            }
            if(types.open){
                result += $translate.instant("webcam.page.results.summary.smileTypes.open");
            }
            if((types.wideOpen || types.open) && types.closed){
                result += $translate.instant("webcam.page.results.summary.smileTypes.and");
            }
            if(types.closed){
                result += $translate.instant("webcam.page.results.summary.smileTypes.closed");
            }
			return result;
		}

		function gatherDataAboutService(service){
			return {
				service: service,
				order: ctrl.servicesOrder.indexOf(service),
				detectedSmiles: ctrl.statistics[service].detectedSmiles,
                smiling: ctrl.statistics[service].smiling,
                notSmiling: ctrl.statistics[service].notSmiling,
                timeSpent: ctrl.statistics[service].timeSpent,
                generalDetection: ctrl.formData[service].generalDetection,
                smileTypes: ctrl.formData[service].smileTypes,
                additionalData: ctrl.formData[service].additionalData,
                averageResponseTime: ctrl.statistics[service].averageResponseTime
			};
		}

        $scope.$on('$destroy', function(){
        	if(VIEWS.SEND_SNAPSHOTS === ctrl.view && !ctrl.snapshotsSent){
                saveSnapshots(false);
			}
		});
	}
}());