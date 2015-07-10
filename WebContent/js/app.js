var app = angular.module("app", [ "ui.router", "ui.bootstrap",
		"ui.bootstrap.datetimepicker" ]);
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
			})
		} ]);

app
		.factory(
				'AuthService',
				function($http, $q, Session) {
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
									Session.create(data.auth, data.username);
									def.resolve(data.username);
									return data.username;
								}).error(function(error) {
							// called asynchronously if an error occurs
							// or server returns response with an error status.
							def.reject(error);
						});
						return def.promise;
					};
					var isAuthenticated = function() {
						return Session.auth;
					};

					var logout = function() {
						var deferred = $q.defer();
						Session.destroy();
						return deferred.promise;
					};
					return {
						login : login,
						isAuthenticated : isAuthenticated,
						logout : logout
					};

				});

app.factory('Session', function() {
	this.create = function(auth, username) {
		this.auth = auth;
		this.username = username;
	};
	this.destroy = function() {
		this.auth = false;
		this.username = null;
	};
	return this;
});

app.controller('LoginCtrl', function($scope, $location, AuthService) {
	$scope.credentials = {
		username : '',
		password : ''
	};
	$scope.login = function(credentials) {
		AuthService.login(credentials).then(function(username) {
			$scope.setErrorMessage(null);
			$scope.setCurrentUsername(username);
			$location.path('/ilan');
		}, function(response) {
			$window.alert("Invalid credentials");
			// $scope.setErrorMessage(response.data.error.message);
		});
	};
});

app.controller('IlanCtrl', function($scope, $http) {
	$scope.ilan = {
		ilanid : '',
		title : '',
		definition : '',
		description : '',
		aktif : '',
		pasif : '',

	};

	$scope.setilanDate = function(value) {
		$scope.ilan.aktif = value.toLocaleString();
	};

	$scope.setilanDate2 = function(value) {
		$scope.ilan.pasif = value.toLocaleString();
	};

	$scope.saveilan = function(ilan) {

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
		});

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

app.controller('HomeCtrl', function($scope, $http, $timeout) {

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

});

app.controller('AppCtrl', function($scope, AuthService) {
	$scope.currentUsername = null;
	$scope.isAuthenticated = AuthService.isAuthenticated;
	$scope.setErrorMessage = function(message) {
		$scope.errorMessage = message;
	};
	$scope.setCurrentUsername = function(username) {
		$scope.currentUsername = username;
	};
	$scope.logout = function() {
		AuthService.logout();
	}
	
});
