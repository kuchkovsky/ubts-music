(function () {

    'use strict';

    const app = angular.module('ubtsMusicStore');

    app.run(function (authService) {
        authService.checkLoginStatus();
    });

    app.run(function ($rootScope, $state, $stateParams) {
        $rootScope.$state = $state;
        $rootScope.$stateParams = $stateParams;
    });

    app.run(function($transitions, $rootScope) {
        $transitions.onStart({ }, function(trans) {
            $rootScope.isMainSpinnerVisible = true;
        });
        $transitions.onFinish({ }, function(trans) {
            $rootScope.isMainSpinnerVisible = false;
        });
    });

    app.run(function($mdSidenav, $rootScope, $mdDialog, authService) {
        $rootScope.toggleLeftSidebar = () => $mdSidenav('left').toggle();
        $rootScope.logout = () => authService.logout();
    });

    app.run(function (subscriptionService) {
        subscriptionService.checkSubscriptionStatus();
    })

})();
