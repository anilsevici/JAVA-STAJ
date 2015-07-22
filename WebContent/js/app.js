var app = angular.module("app", [ "ui.router", "ui.bootstrap",
		"ui.bootstrap.datetimepicker", "ngCookies", "ngStorage" ]);
app.config([ '$stateProvider', '$urlRouterProvider', '$locationProvider',
		function($stateProvider, $urlRouterProvider, $locationProvider) {

			// For any unmatched url, redirect to /state1

			//
			// Now set up the states
			$stateProvider.state('home', {
				url : "/home",
				templateUrl : "home.html",
				controller : 'HomeCtrl',
				module : 'public'
			}).state('login', {
				url : "/login",
				templateUrl : "login.html",
				controller : 'LoginCtrl',
				module : 'public'
			}).state('ilan', {
				url : "/ilan",
				templateUrl : "ilan.html",
				controller : 'IlanCtrl'
			}).state('createjob', {
				url : "/createjob",
				templateUrl : "createjob.html",
				controller : 'IlanCtrl',
				module : 'private'
			}).state('basvuru', {
				url : "/basvuru",
				templateUrl : "basvuru.html",
				controller : 'BasvuruCtrl',
				module : 'public'
			}).state('profil', {
				url : "/profile",
				templateUrl : "profile.html",
				controller : 'ProfilCtrl',
				module : 'public'
			}).state('ikgoruntule', {
				url : "/ikgoruntule",
				templateUrl : "ikgoruntule.html",
				controller : 'IKGoruntuleCtrl',
				module : 'private'
			}).state('search', {
				url : "/search",
				templateUrl : "ElasticSearch.html",
				controller : 'ElasticCtrl',
				module : 'private'
			})

			$urlRouterProvider.otherwise("/home");

		} ]);

app.run(function($rootScope, $location, $state, $cookies, AuthService) {
	$rootScope.$on('$stateChangeStart', function(e, toState, toParams,
			fromState, fromParams) {

		if (toState.module === 'private' && !$cookies.get('name')) {
			// If logged out and transitioning to a logged in
			// page:
			console.log(toState);
			e.preventDefault();
			$location.path('/login');

		}

		if (toState.name === 'login' && $cookies.get('linkedlnid'))
			e.preventDefault();

	});
});

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

					var islinkedlnauth = function() {
						return $cookies.get('linkedlnid');
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
						logout : logout,
						islinkedlnauth : islinkedlnauth

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

app.controller('LoginCtrl', function($scope, $location, $window, $cookies,
		AuthService) {
	$scope.credentials = {
		username : '',
		password : ''
	};

	$scope.login = function(credentials) {
		AuthService.login(credentials).then(function(data) {
			$scope.setusername($cookies.get('name'));
			$location.path('/createjob');
			console.log($cookies.get('name'));
		}, function(response) {
			$window.alert("Invalid credentials");

		});
	};
});

app
		.controller(
				'IlanCtrl',
				function($scope, $http, $window, $state, $cookies, TagService) {
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

						$http(
								{
									method : 'GET',
									url : 'IlanGetirServlet',
									headers : {
										'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'
									},
									params : {
										hrid : $cookies.get('name')
									}

								}).success(
								function(data, status, headers, config) {
									$scope.ilanlar = data;

								}).error(function(error) {
							// called asynchronously if an error occurs
							// or server returns response with an error status.
						});

					}

					$scope.getilan();

					$scope.edit = true;
					$scope.aktif = false;
					$scope.pasif = false;

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
							$('#error-bucket').show().attr("tabIndex", -1)
									.focus();
							// track that the form was submitted and that field
							// level errors can
							// now be shown
							$scope.submitted = true;

						} else {

							$http({
								method : 'GET',
								url : 'IlanServlet',
								params : {
									ilan : ilan,
									edit : $scope.edit,
									aktif : $scope.aktif,
									pasif : $scope.pasif
								}

							})
									.success(
											function(data, status, headers,
													config) {

												$scope.success = true;
												$state.reload();
											})
									.error(
											function(error) {
												// called asynchronously if an
												// error occurs
												// or server returns response
												// with an error status.
												$window
														.alert("Bu ilan numarasına kayıtlı bir ilan var.Eklenemez");
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
				console.log('New date value aktif:' + value);
				$scope.setilanDate(value);
			}, true);

			$scope.$watch("date2", function(value) {
				console.log('New date value pasif:' + value);
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
				function($scope, $http, $timeout, $cookies, $state,
						$localStorage, $window) {

					var width = 960, height = 500, radius = Math.min(width,
							height) / 2;

					var color = d3.scale.ordinal().range(
							[ "#98abc5", "#8a89a6", "#7b6888", "#6b486b",
									"#a05d56", "#d0743c", "#ff8c00" ]);

					var arc = d3.svg.arc().outerRadius(radius - 10)
							.innerRadius(0);

					var pie = d3.layout.pie().sort(null).value(function(d) {
						return d.count;
					});

					var svg;
					if (!svg) {
						svg = d3.select("div#piechart").append("svg").attr(
								"width", width).attr("height", height).append(
								"g").attr(
								"transform",
								"translate(" + width / 2 + "," + height / 2
										+ ")");

					}

					$scope.getchart = function() {
						d3.json("IlanChartServlet", function(error, data) {

							data.forEach(function(d) {
								d.count = +d.count;
							});

							var g = svg.selectAll(".arc").data(pie(data))
									.enter().append("g").attr("class", "arc");

							g.append("path").attr("d", arc).style("fill",
									function(d) {
										return color(d.data._id);
									});

							g.append("text").attr("transform", function(d) {
								return "translate(" + arc.centroid(d) + ")";
							}).attr("dy", ".35em").style("text-anchor",
									"middle").text(function(d) {
								return d.data._id;
							});

						});
					}

					$scope.getchart();
					$scope.ilanlar = [];

					$scope.onTimeout = function() {

						$http({
							method : 'GET',
							url : 'IlanGorServlet',

						}).success(function(data, status, headers, config) {
							$scope.ilanlar = data;
							$scope.getchart();

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

						if ($cookies.get('linkedlnid')) {

							$http(
									{
										method : 'POST',
										url : 'SaveServlet',
										headers : {
											'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'
										},
										params : {
											userid : $cookies.get('linkedlnid'),
											ilanid : $scope.ilanlar[id]._id
										}
									})
									.success(
											function(data, status, headers,
													config) {
												$window
														.alert("Basariyla ilana basvurdunuz.Epostanızı kontrol ediniz.");
											})
									.error(
											function(data, status, headers,
													config) {
												// called asynchronously if an
												// error
												// occurs
												// or server returns response
												// with
												// an error
												// status.
												if (status === 400)
													$window
															.alert("Bu ilana daha önce basvuruda bulundunuz.");
												if (status === 404)
													$window
															.alert("Bu ilan icin Kara listedesiniz.IK ile irtibata geciniz.");

											});

						} else
							$window
									.alert("Ilana Basvurmak icin Linkedln hesabınızla giris yapiniz.");

					}

				});

app.controller('IlanGıtCtrl', function($scope, $localStorage) {

	$scope.ilan = $localStorage.data;

});

app
		.controller(
				'ProfilCtrl',
				function($scope, $cookies, $http) {

					$scope.profile = '';

					$scope.goprofile = function() {

						$http(
								{
									method : 'POST',
									url : 'ProfileServlet',
									headers : {
										'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'
									},
									params : {
										userid : $cookies.get('linkedlnid'),
										tempid : $cookies.get('tempid')
									}
								}).success(
								function(data, status, headers, config) {
									$scope.profile = data;

								}).error(function(error) {
							// called asynchronously if an error occurs
							// or server returns response with an error status.

						});

					}

					$scope.goprofile();

				});

app
		.controller(
				'ElasticCtrl',
				function($scope, $cookies, $http) {

					$scope.users = [];
					$scope.label = '';

					$scope.change = function() {

						$http(
								{
									method : 'GET',
									url : 'ElasticServlet',
									headers : {
										'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'
									},
									params : {
										label : $scope.label
									}
								}).success(
								function(data, status, headers, config) {
									$scope.users = data;

								}).error(function(error) {
							// called asynchronously if an error occurs
							// or server returns response with an error status.

						});

					}

				});

app
		.controller(
				'BasvuruCtrl',
				function($scope, $cookies, $http, $state, $localStorage) {

					$scope.basvurular = [];
					$scope.istemp = '';

					$scope.gobasvurular = function() {

						$http(
								{
									method : 'GET',
									url : 'BasvuruGoruntuleServlet',
									headers : {
										'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'
									},
									params : {
										id : $cookies.get('linkedlnid'),
										tempid : $cookies.get('tempid')
									}
								}).success(
								function(data, status, headers, config) {
									$scope.basvurular = data;
									$scope.istemp = $cookies.get('tempid');

								}).error(function(error) {
							// called asynchronously if an error occurs
							// or server returns response with an error status.

						});

					}

					$scope.gobasvurular();

					$scope.basvurucek = function(id) {

						$http(
								{
									method : 'GET',
									url : 'BasvuruCekServlet',
									headers : {
										'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'
									},
									params : {
										userid : $cookies.get('linkedlnid'),
										ilanid : id
									}
								}).success(
								function(data, status, headers, config) {
									$state.reload();

								}).error(function(error) {
							// called asynchronously if an error occurs
							// or server returns response with an error status.

						});

					}

					$scope.goilan = function(id) {
						$localStorage.data = $scope.basvurular[id];
					};

				});

app
		.controller(
				'IKGoruntuleCtrl',
				function($scope, $cookies, $http, $state, $window) {

					$scope.ilanlar = [];
					$scope.basvuranlar = [];
					$scope.forstatu = [];

					$scope.statufilter = [];
					$scope.durumlar = [];
					$scope.oneilan;
					$scope.blacklist = false;
					$scope.blackcontent = '';

					$scope.ilangetir = function() {

						$http(
								{
									method : 'GET',
									url : 'IlanGetirServlet',
									headers : {
										'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'
									},
									params : {
										hrid : $cookies.get('name')
									}
								}).success(
								function(data, status, headers, config) {
									$scope.ilanlar = data;

								}).error(function(error) {
							// called asynchronously if an error occurs
							// or server returns response with an error status.

						});

					}

					$scope.ilangetir();

					$scope.ilanbasvurugoruntule = function(ilanid) {

						$http(
								{
									method : 'GET',
									url : 'IlanaBasvuruServlet',
									headers : {
										'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'
									},
									params : {
										ilanid : ilanid
									}
								}).success(
								function(data, status, headers, config) {
									$scope.basvuranlar = data;
									$scope.oneilan = ilanid;

								}).error(function(error) {
							// called asynchronously if an error occurs
							// or server returns response with an error status.

						});

					}

					$scope.ilanonay = function(userid, durum, email) {

						$http(
								{
									method : 'GET',
									url : 'IlanOnayServlet',
									headers : {
										'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'
									},
									params : {
										userid : userid,
										durum : durum,
										ilanid : $scope.oneilan,
										email : email
									}
								})
								.success(
										function(data, status, headers, config) {
											$window
													.alert("Isleminiz basarıyla gerceklesti.");
											$state.reload();
										}).error(function(error) {
									// called asynchronously if an error occurs
									// or server returns response with an error
									// status.

								});

					}

					$scope.statufilter = function(ilanid, statu) {

						$http(
								{
									method : 'GET',
									url : 'StatuServlet',
									headers : {
										'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'
									},
									params : {
										statu : statu,
										ilanid : ilanid
									}
								}).success(
								function(data, status, headers, config) {
									$scope.forstatu = data;

								}).error(function(error) {
							// called asynchronously if an error occurs
							// or server returns response with an error status.

						});

					}

					$scope.getblacklist = function(userid, useremail, black) {

						if (!black)
							$window.alert("Blacklist kutucugunu seciniz")
						else {

							$http(
									{
										method : 'GET',
										url : 'BlackListServlet',
										headers : {
											'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'
										},
										params : {
											userid : userid,
											content : $scope.blackcontent,
											hrid : $cookies.get('name'),
											email : useremail
										}
									})
									.success(
											function(data, status, headers,
													config) {
												$state.reload();
												$window
														.alert("Kisi Kara listeye alındı.")

											}).error(function(error) {
										// called asynchronously if an error
										// occurs
										// or server returns response with an
										// error status.

									});

						}

					}

					$scope.other = function(id) {
						$cookies.put('tempid', id);
					};

					$scope.details = function(id) {
						$cookies.put('tempid', id);
					};

				});

app
		.controller(
				'AppCtrl',
				function($scope, $location, $rootScope, $cookies, $http,
						$state, AuthService) {

					$scope.setusername = function(user) {
						$scope.currentUsername = user;
					};

					$scope.isAuthenticated = AuthService.isAuthenticated;
					$scope.loggedUser = AuthService.islinkedlnauth;
					$scope.firstname = '';
					$scope.lastname = '';

					$scope.logout = function() {
						AuthService.logout();
					};

					$scope.signli = function() {

						IN.User.authorize(function() {

						});
					};

					$scope.getLinkedInData = function() {

						if (!$scope.hasOwnProperty("userprofile")) {
							IN.API
									.Profile("me")
									.fields(
											[ "id", "firstName", "lastName",
													"pictureUrl",
													"publicProfileUrl",
													"emailAddress",
													"positions", "educations",
													"industry,location" ])
									.result(
											function(result) {
												// set the model
												$rootScope
														.$apply(function() {
															var userprofile = result.values[0]
															$rootScope.userprofile = userprofile;

															$cookies
																	.put(
																			'firstname',
																			userprofile.firstName);
															$cookies
																	.put(
																			'lastname',
																			userprofile.lastName);

															$scope.firstname = $cookies
																	.get('firstname');
															$scope.lastname = $cookies
																	.get('lastname');
															$scope
																	.usersave(userprofile);
															// go to main
															console
																	.log(userprofile);
															$state.reload();
															$location
																	.path("/home");
														});
											}).error(function(err) {
										$scope.error = err;
									});
						}
					};

					$scope.usersave = function(userprofile) {

						$http(
								{
									method : 'POST',
									url : 'UserSaveServlet',
									headers : {
										'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'
									},
									params : {
										userprofile : userprofile
									}
								}).success(
								function(data, status, headers, config) {

								}).error(function(error) {
							// called asynchronously if an error occurs
							// or server returns response with an error status.

						});

					};

					$scope.logoutLinkedIn = function() {
						// retrieve values from LinkedIn
						IN.User.logout();
						delete $rootScope.userprofile;
						$cookies.remove('linkedlnid');
						$cookies.remove('firstname');
						$cookies.remove('lastname');
						$location.path("/home");
					};

				});
