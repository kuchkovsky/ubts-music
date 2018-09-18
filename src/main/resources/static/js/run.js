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
        $transitions.onStart({ }, function(transition) {
            $rootScope.isMainSpinnerVisible = true;
            if (!$rootScope.isAuthenticated) {
                if (transition.to().name === 'home') {
                    $rootScope.topInfoCard.message = 'Для завантаження музики увійдіть в свій акаунт або зареєструйтесь';
                    $rootScope.topInfoCard.showLoginButton = true;
                } else {
                    $rootScope.topInfoCard = {};
                }
            }
        });
        $transitions.onFinish({ }, function(transition) {
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
