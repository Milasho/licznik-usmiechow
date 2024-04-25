(function () {
	angular.module("app.services")
	.service("rest", Service);
	
	Service.$inject = ['restConfig'];
	function Service(restConfig){
		var service = this;
		
		service.smiles = {
			counter: getGlobalSmilesCount,
			photosCounter: getSmilesWithPhotoCount,
			counterRange: getGlobalSmilesBetweenDates,
			get: getGlobalSmiles,
			detect: detectSmilesOnSnapshot,
            detectAndSave: detectAndSave,
			bestLocalisations: getBestLocalisations,
            chartsData: getChartsData
		};

		service.servicesTest = {
			send: sendServiceTest
		};

		service.config = {
			init: initializeServices
		};
			
		//////////////////////////		
		
		function getGlobalSmilesCount(useCache){
			return restConfig.GET("/smiles/counter", null, useCache);
		}

		function getSmilesWithPhotoCount(useCache){
            return restConfig.GET("/smiles/photosCounter", null, useCache);
		}

		function getGlobalSmilesBetweenDates(dateFrom, dateTo){
            return restConfig.GET("/smiles/counter?dateFrom=" + dateFrom.getTime() + "&dateTo=" + dateTo.getTime(), null, true);
		}
		
		function getGlobalSmiles(start, count, useCache){
			var params = (start || count) ? ("?start=" + (start || 0) + (count? ("&count=" + count) : "") ): "";
			return restConfig.GET("/smiles" + params, null, useCache);
		}
		
		function detectSmilesOnSnapshot(snapshot, service){
			var params = '';
			if(service){
				params = '?service=' + service;
			}
			return restConfig.POST("/smiles/detect" + params, snapshot);
		}

		function detectAndSave(snapshots){
            return restConfig.POST("/smiles/detectAndSave", snapshots);
		}

		function sendServiceTest(serviceTest){
            return restConfig.POST("/serviceTest/send", serviceTest);
		}

		function getBestLocalisations(limit){
            return restConfig.GET("/smiles/localisations/" + limit + "/", null, false);
		}

		function getChartsData(dateFrom, dateTo){
            return restConfig.GET("/smiles/chartsData/" + dateFrom + "/" + dateTo + "/", null, false);
		}

		function initializeServices(){
            return restConfig.POST("/config/init");
		}
	}
}());