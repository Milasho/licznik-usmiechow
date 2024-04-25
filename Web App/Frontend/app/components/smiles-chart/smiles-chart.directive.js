(function () {
	angular.module("app.components")
	.directive("smilesChart", Directive);
	
	function Directive(){
		 return {
	    	restrict: 'E',
	    	templateUrl: 'app/components/smiles-chart/smiles-chart.directive.html',
	    	controller: DirectiveController,
	    	controllerAs: 'ctrl',
	    	bindToController: true,
	    	scope: {
	    		daysBefore: '=',
                chartType: '@',
                textColor: '@'
	    	}
	    };
	}
	
	DirectiveController.$inject = ['rest', 'dateFilter'];
	function DirectiveController(rest, dateFilter){
		var ctrl = this;
        var dateFormat = 'EEE, dd/MM';
        ctrl.charts = null;

        init();

        /////////////////////

        function init(){
            ctrl.charts = {
            	smiles: {
            		labels: null, data: null, dates: null,
					options: createSmilesChartOptions(),
					colors: initSmilesChartColors()
				},
				photos: {
					labels: ["With photos", "Without photos"],
                    data: [],
					options: initPhotosChartOptions()
				},
				services: {
                    labels: ["Time spent", "Detected smiles", "Real smiles", "Fake smiles", "Response time"],
                    data: [],
					options: createServicesChartOptions(),
					optionsBlack: createServicesChartOptions('black', 'rgba(0,0,0,0.2)'),
                    colors: initServicesChartColors()
				}
            };
            refreshChartsData();
		}

		function refreshChartsData(){
        	var minDate = ctrl.daysBefore || 30;
        	var start = changeDatePriorToDays(-minDate);
        	var end = changeDatePriorToDays(1);

            ctrl.loading = true;
            ctrl.charts.smiles.content = false;
        	return rest.smiles.chartsData(start.getTime(), end.getTime()).then(function(data){
        		if(data != null){
                    refreshSmilesChartData(data.smilesChart, minDate);
                    refreshPhotosChartData(data.photosChart);
                    refreshTestServicesChart(data.servicesChart);
				}
                ctrl.loading = false;
			});
		}

		function refreshTestServicesChart(data){
            if(data != null && data.length > 0) {
                ctrl.charts.services.content = true;
                var maxValues = findMaxValues(data);
                updateLabels(maxValues);

                ctrl.charts.services.series = [];
                for(var i = 0; i < data.length; i++){
                	var service = data[i];
                    ctrl.charts.services.series.push(service.service);
                    ctrl.charts.services.data.push([
                        Math.round(service.timeSpent / 10 / maxValues[0]) / 100,
                        Math.round(service.detectedSmiles / maxValues[1] * 100) / 100,
                        Math.round(service.smiling / maxValues[2] * 100) / 100,
                        Math.round(service.notSmiling / maxValues[3] * 100) / 100,
                        Math.round(service.averageResponseTime / 10 / maxValues[4]) / 100
					]);
				}
            }
		}

		function findMaxValues(data){
            var maxValues = [1,1,1,1,1];
            for(var j = 0; j < data.length; j++){
                var service = data[j];
                if(service.timeSpent / 1000 > maxValues[0]){
                    maxValues[0] = service.timeSpent / 1000;
                }
                if(service.detectedSmiles > maxValues[1]){
                    maxValues[1] = service.detectedSmiles ;
                }
                if(service.smiling > maxValues[2]){
                    maxValues[2] = service.smiling;
                }
                if(service.notSmiling> maxValues[3]){
                    maxValues[3] = service.notSmiling;
                }
                if(service.averageResponseTime / 1000 > maxValues[4]){
                    maxValues[4] = service.averageResponseTime / 1000;
                }
            }
            return maxValues;
		}

		function updateLabels(maxValues){
            ctrl.charts.services.labels[0] = 'Time spent (' + Math.round(maxValues[0]) + ' s)';
            ctrl.charts.services.labels[1] = 'Detected smiles (' + Math.round(maxValues[1]) + ')';
            ctrl.charts.services.labels[2] = 'Real smiles (' + Math.round(maxValues[2]) + ')';
            ctrl.charts.services.labels[3] = 'Fake smiles (' + Math.round(maxValues[3]) + ')';
            ctrl.charts.services.labels[4] = 'Response time (' + Math.round(maxValues[4]) + ' s)';
        }

		function refreshPhotosChartData(data){
            if(data != null) {
                ctrl.charts.photos.content = true;
                ctrl.charts.photos.data = [
                	data.smilesWithPhoto,
                	data.smilesWithoutPhoto
				];
            }
		}

		function refreshSmilesChartData(data, minDate){
            if(data != null && data.length > 0){
                ctrl.charts.smiles.content =  true;
                ctrl.charts.smiles.labels = [];
                ctrl.charts.smiles.data = [];
                ctrl.charts.smiles.dates = [];

                for(var i = minDate - 1; i >= 0; i--){
                    var date = changeDatePriorToDays(-i);
                    ctrl.charts.smiles.dates.push(date);
                    ctrl.charts.smiles.labels.push(dateFilter(date, dateFormat));
                    ctrl.charts.smiles.data.push(0);
                }
                for(var j = 0; j < data.length; j++){
                    processChartData(data[j]);
                }
            }

            ctrl.charts.smiles.loading = false;
		}

		function processChartData(data){
            var smilesDate = new Date(data.date);

            for(var i = 0; i < ctrl.charts.smiles.dates.length; i++){
               var date = ctrl.charts.smiles.dates[i];

               if(areDatesEqual(smilesDate, date)){
                   ctrl.charts.smiles.data[i] = data.smiles;
				   break;
			   }
            }
		}

		function areDatesEqual(d1, d2){
			return d1.getDate() === d2.getDate() && d1.getMonth() === d2.getMonth() && d1.getFullYear() === d2.getFullYear();
		}

		function changeDatePriorToDays(days){
			var today = new Date();
			return new Date(today.getFullYear(), today.getMonth(), today.getDate() + days);
		}

		function initSmilesChartColors(){
			return ['rgba(253,180,92,0.7)', '#fff', '#fff', '#fff', '#fff', '#fff', '#fff'];
		}

		function initServicesChartColors(){
			return ['#DCDCDC', '#cf18d2', '#FDB45C'];
		}

		function createSmilesChartOptions(){
			return {
                scales: {
                    yAxes: [{
                        type: 'linear',
                        display: true,
                        position: 'left',
                        gridLines: {
                            color: 'rgba(255,255,255,0.5)'
                        },
                        ticks: {
                            fontColor: 'white'
                        }
                    }],
                    xAxes: [{
                        gridLines: {
                            display: false,
                            color: 'rgba(255,255,255,0.5)'
                        },
                        ticks: {
                            fontColor: 'white'
                        }
                    }]
                }
            };
		}

		function createServicesChartOptions(textColor, gridColor){
		    var textColor = textColor || 'white';
		    var gridColor = gridColor || 'rgba(255,255,255,0.5)';

            return {
                scale: {
                	legend: {
                		display: true,
                        position: 'bottom'
					},
                    gridLines: {
                        color: gridColor
                    },
                    ticks: {
                    	display: false,
                        fontColor: textColor,
                        beginAtZero: true,
                        min: 0,
                        max: 1,
                        stepSize: 0.2
                    },
                    pointLabels:{
                        fontColor: textColor
                    }
                },
                legend: {
                    display: true,
                    labels:{fontColor: textColor},
                    position: 'bottom'
                }
            }
		}

		function initPhotosChartOptions(){
            return {
                legend: {
                    display: true,
                    labels:{fontColor: 'white'},
                    position: 'bottom'
                }
            }
        }
	}
}());