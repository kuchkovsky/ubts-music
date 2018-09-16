(function () {

    'use strict';

    const app = angular.module('ubtsMusicStore');

    app.controller('subscriptionCtrl', function ($mdDialog) {

        this.subscribe = () => {
            this.subscriptionRequest = {
                state: {
                    hasStarted: false,
                    inProgress: true,
                    success: false,
                    networkError: false
                }
            };
            $mdDialog.show({
                controller: function ($scope, $rootScope, $mdDialog, $mdToast, $location, subscriptionService, subscriptionRequest) {
                    $scope.subscriptionRequest = subscriptionRequest;
                    $scope.hide = () => {
                        $mdDialog.hide();
                    };
                    $scope.sendSubscriptionRequest = () => {
                        $scope.subscriptionRequest.state.hasStarted = true;
                        subscriptionService.sendSubscriptionRequest(() => {
                            $scope.subscriptionRequest.state.success = true;
                            $scope.subscriptionRequest.state.inProgress = false;
                        }, res => {
                            $scope.subscriptionRequest.state.success = false;
                            $scope.subscriptionRequest.state.networkError = res.status !== 424;
                            $scope.subscriptionRequest.state.inProgress = false;
                        });
                    };
                    $scope.showToast = () => {
                        $mdToast.show(
                            $mdToast.simple()
                                .textContent('Скопіювано в буфер обміну')
                                .position('bottom')
                                .hideDelay(3000)
                        );
                    };
                },
                templateUrl: 'templates/subscription-alert.html',
                parent: angular.element(document.body),
                clickOutsideToClose: false,
                locals: {
                    subscriptionRequest: this.subscriptionRequest
                }
            });
        };

    });

})();
