var app = angular.module("app", [ "ui.router", "ui.bootstrap",
		"ui.bootstrap.datetimepicker", "ngCookies", "ngStorage" ]);
app.config([ '$stateProvider', '$urlRouterProvider', '$locationProvider',
		function($stateProvider, $urlRouterProvider, $locationProvider) {

			// For any unmatched url, redirect to /state1
			$urlRouterProvider.otherwise("/home");
			//
			// Now set up the states
			$stateProvider.state('home', {
				url : "/home",
				templateUrl : "home.html",
				controller : 'HomeCtrl'
			}).state('login', {
				url : "/login",
				templateUrl : "login.html",
				controller : 'LoginCtrl'
			}).state('ilan', {
				url : "/ilan",
				templateUrl : "ilan.html",
				controller : 'IlanCtrl'
			}).state('createjob', {
				url : "/createjob",
				templateUrl : "createjob.html",
				controller : 'IlanCtrl'
			})
		} ]);

app
		.factory(
				'AuthService',
				function($http, $q, $cookies) {
					var login = function(credentials) {
						var def = $q.defer();
						$http(
								{
									method : 'POST',
									url : 'IKServlet',
									headers : {
										'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'
									},
									params : {
										user : credentials
									}
								}).success(
								function(data, status, headers, config) {
									return def.resolve(data);

								}).error(function(error) {
							// called asynchronously if an error occurs
							// or server returns response with an error status.
							def.reject(error);
						});
						return def.promise
					};
					var isAuthenticated = function() {
						return $cookies.get('name');
					};

					var logout = function() {
						var deferred = $q.defer();
						$http({
							method : 'GET',
							url : 'IKLogoutServlet',

						}).success(function(data, status, headers, config) {

						}).error(function(error) {
							// called asynchronously if an error occurs
							// or server returns response with an error status.
						});
						return deferred.promise;
					};
					return {
						login : login,
						isAuthenticated : isAuthenticated,
						logout : logout

					};

				});

app.factory('TagService', function() {
	return {
		"ilantagList" : [ {
			'value' : 'AR-GE',
			'criterion' : 'AR-GE'
		}, {
			'value' : 'Bilgi İşlem',
			'criterion' : 'Bilgi İşlem'
		}, {
			'value' : 'Finans',
			'criterion' : 'Finans'
		}, {
			'value' : 'İthalat/İhracat',
			'criterion' : 'İthalat/İhracat'
		}, {
			'value' : 'Muhasebe',
			'criterion' : 'Muhasebe'
		}, {
			'value' : 'Müşteri İlişkileri',
			'criterion' : 'Müşteri İlişkileri'
		}, {
			'value' : 'Pazarlama',
			'criterion' : 'Pazarlama'
		}, {
			'value' : 'Satış',
			'criterion' : 'Satış'
		}, {
			'value' : 'Teknikerlik',
			'criterion' : 'Teknikerlik'
		}, {
			'value' : 'Üretim',
			'criterion' : 'Üretim'
		}, {
			'value' : 'Mühendis',
			'criterion' : 'Mühendis'
		}, {
			'value' : 'Other',
			'criterion' : 'Other'
		} ]
	};
});

app.controller('LoginCtrl', function($scope, $location, $window, AuthService) {
	$scope.credentials = {
		username : '',
		password : ''
	};
	$scope.login = function(credentials) {
		AuthService.login(credentials).then(function(data) {
			$location.path('/createjob');
		}, function(response) {
			$window.alert("Invalid credentials");

		});
	};
});

app.controller('IlanCtrl', function($scope, $http, TagService) {
	$scope.ilan = {
		_id : '',
		title : '',
		definition : '',
		description : '',
		aktif : '',
		pasif : '',
		tag : ''

	};

	$scope.ilanlar = [];

	$scope.ilantagList = TagService.ilantagList;

	$scope.setilanDate = function(value) {
		$scope.ilan.aktif = value.toLocaleString();
	};

	$scope.setilanDate2 = function(value) {
		$scope.ilan.pasif = value.toLocaleString();
	};

	$scope.getilan = function() {

		$http({
			method : 'GET',
			url : 'IlanGetirServlet',

		}).success(function(data, status, headers, config) {
			$scope.ilanlar = data;

		}).error(function(error) {
			// called asynchronously if an error occurs
			// or server returns response with an error status.
		});

	}

	$scope.getilan();

	$scope.edit = true;

	$scope.editilan = function(id) {
		if (id == 'new') {
			$scope.edit = true;
			$scope.ilan.ilanid = '';
			$scope.ilan.title = '';
			$scope.ilan.definition = '';
			$scope.ilan.description = '';
			$scope.ilan.aktif = '';
			$scope.ilan.pasif = '';
			$scope.ilan.tag = '';

		} else {
			$scope.edit = false;
			$scope.ilan = $scope.ilanlar[id];

		}
	};

	$scope.saveilan = function(ilan, inValid) {

		if (inValid) {
			// if there are errors
			// Set focus to the form level error warning
			$('#error-bucket').show().attr("tabIndex", -1).focus();
			// track that the form was submitted and that field level errors can
			// now be shown
			$scope.submitted = true;

		} else {

			$http({
				method : 'GET',
				url : 'IlanServlet',
				params : {
					ilan : ilan
				}

			}).success(function(data, status, headers, config) {

			}).error(function(error) {
				// called asynchronously if an error occurs
				// or server returns response with an error status.
				$window.alert("Bu ilan numarasına kayıtlı bir ilan var");
			});

		}

	};

});

app.controller('DateTimePickerDemoCtrl',
		function($scope, $timeout) {
			$scope.dateTimeNow = function() {
				$scope.date = new Date();

				$scope.date2 = new Date();

			};
			$scope.dateTimeNow();

			$scope.toggleMinDate = function() {
				$scope.minDate = $scope.minDate ? null : new Date();
			};

			$scope.maxDate = new Date('2014-06-22');
			$scope.toggleMinDate();

			$scope.dateOptions = {
				startingDay : 1,
				showWeeks : false
			};

			// Disable weekend selection
			$scope.disabled = function(calendarDate, mode) {
				return mode === 'day'
						&& (calendarDate.getDay() === 0 || calendarDate
								.getDay() === 6);
			};

			$scope.hourStep = 1;
			$scope.minuteStep = 15;

			$scope.timeOptions = {
				hourStep : [ 1, 2, 3 ],
				minuteStep : [ 1, 5, 10, 15, 25, 30 ]
			};

			$scope.showMeridian = true;
			$scope.timeToggleMode = function() {
				$scope.showMeridian = !$scope.showMeridian;
			};

			$scope.$watch("date", function(value) {
				console.log('New date value:' + value);
				$scope.setilanDate(value);
			}, true);

			$scope.$watch("date2", function(value) {
				console.log('New date value:' + value);
				$scope.setilanDate2(value);
			}, true);

			$scope.resetHours = function() {
				$scope.date.setHours(1);
				$scope.date2.setHours(1);
			};
		});

app
		.controller(
				'HomeCtrl',
				function($scope, $http, $timeout, $localStorage) {

					$scope.ilanlar = [];

					$scope.onTimeout = function() {

						$http({
							method : 'GET',
							url : 'IlanGorServlet',

						}).success(function(data, status, headers, config) {
							$scope.ilanlar = data;

						}).error(function(error) {
							// called asynchronously if an error occurs
							// or server returns response with an error status.
						});

						mytimeout = $timeout($scope.onTimeout, 120000);

					}

					var mytimeout = $timeout($scope.onTimeout, 120000);
					$scope.onTimeout();

					$scope.toggleIlanDetails = function(e, index) {
						if ($scope.index == index) {
							delete $scope.index;
						} else {
							$scope.index = index;
						}
						e.preventDefault();

					};

					$scope.ilanagit = function(id) {
						$localStorage.data = $scope.ilanlar[id];
					};

					$scope.basvurukaydet = function(id) {

						$scope.user = Data.getuserprofile();

						$http(
								{
									method : 'POST',
									url : 'SaveServlet',
									headers : {
										'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'
									},
									params : {
										userprofile : $scope.user,
										ilanid : $scope.ilanlar[id]._id
									}
								}).success(
								function(data, status, headers, config) {

								}).error(function(error) {
							// called asynchronously if an error occurs
							// or server returns response with an error status.

						});

					}

				});

app.controller('IlanGıtCtrl', function($scope, $localStorage) {

	$scope.ilan = $localStorage.data;

});

app.controller('AppCtrl', function($scope, $location, $rootScope, $cookies,
		$http, AuthService) {
	$scope.currentUsername = $cookies.get('name')
	$scope.isAuthenticated = AuthService.isAuthenticated;

	$scope.logout = function() {
		AuthService.logout();
	};

	$scope.signli = function() {

		IN.User.authorize(function() {

		});
	};

	$scope.getLinkedInData = function() {

		if (!$scope.hasOwnProperty("userprofile")) {
			IN.API.Profile("me").fields(
					[ "id", "firstName", "lastName", "pictureUrl",
							"publicProfileUrl", "emailAddress", "positions",
							"educations", "industry" ]).result(
					function(result) {
						// set the model
						$rootScope.$apply(function() {
							var userprofile = result.values[0]
							$rootScope.userprofile = userprofile;
							$rootScope.loggedUser = true;
							// go to main
							console.log($rootScope.userprofile);
							$location.path("/home");
						});
					}).error(function(err) {
				$scope.error = err;
			});
		}
	};

	$scope.logoutLinkedIn = function() {
		// retrieve values from LinkedIn
		IN.User.logout();
		delete $rootScope.userprofile;
		$rootScope.loggedUser = false;
		$location.path("/home");
	};

});
