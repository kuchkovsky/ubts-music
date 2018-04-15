(function () {

    'use strict';

    const app = angular.module('ubtsMusicStore');

    app.controller('cartCtrl', function ($rootScope, $location, $mdDialog) {

        this.proceedToOrder = () => {
            if (!$rootScope.isAuthenticated) {
                $rootScope.toggleRightSidebar();
                $location.path("/login");
                return;
            }
            this.order = {
                state: {
                    hasStarted: false,
                    inProgress: true,
                    success: false,
                    networkError: false
                },
                total: $rootScope.cart.totalPrice
            };
            $mdDialog.show({
                controller: function ($scope, $rootScope, $mdDialog, $mdToast, $location, orderService, order) {
                    $scope.order = order;
                    $scope.hide = () => {
                        $mdDialog.hide();
                        if ($scope.order.state.success) {
                            $rootScope.cart.clearCart();
                            $rootScope.toggleRightSidebar();
                            $location.path("/orders");
                        }
                    };
                    $scope.sendOrder = () => {
                        $scope.order.state.hasStarted = true;
                        orderService.sendOrder(() => {
                            $scope.order.state.success = true;
                            $scope.order.state.inProgress = false;
                        }, res => {
                            $scope.order.state.success = false;
                            $scope.order.state.networkError = res.status !== 424;
                            $scope.order.state.inProgress = false;
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
                templateUrl: 'templates/order-alert.html',
                parent: angular.element(document.body),
                clickOutsideToClose: false,
                locals: {
                    order: this.order
                }
            });
        };
        
    });
    
})();
