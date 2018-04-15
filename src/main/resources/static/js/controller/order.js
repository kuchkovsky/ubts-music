(function () {

    'use strict';

    const app = angular.module('ubtsMusicStore');

    app.controller('orderViewCtrl', function ($rootScope, $stateParams, $mdDialog, $location, downloadService, orderService) {
        $rootScope.orderId = $stateParams.orderId;
        $rootScope.isMainSpinnerVisible = true;
        downloadService.getOrder($stateParams.orderId, order => {
            this.order = order;
            $rootScope.isMainSpinnerVisible = false;
        }, () => {
            const alert = $mdDialog.alert().title('Помилка')
                .textContent('Не вдалося завантажити замовлення').ok('Закрити');
            $mdDialog.show(alert);
            $rootScope.isMainSpinnerVisible = false;
        });

        function showError(text) {
            const alert = $mdDialog.alert().title('Помилка').textContent(text).ok('Закрити');
            $mdDialog.show(alert);
        }

        this.confirmOrder = () => {
            orderService.confirmOrder(this.order, () => {}, () => {
                showError('Не вдалося підтвердити замовлення');
            });
        };

        this.rejectOrder = () => {
            orderService.rejectOrder(this.order, () => {}, () => {
                showError('Не вдалося відхилити замовлення');
            });
        };

        this.deleteOrder = () => {
            const confirm = $mdDialog.confirm()
                .title('Підтвердження операції')
                .textContent('Ви дійсно бажаєте видалити замовлення?')
                .ok('Так')
                .cancel('Ні');
            $mdDialog.show(confirm).then(() => {
                orderService.deleteOrder(this.order, () => {
                    $location.path("/orders");
                }, () => {
                    showError('Не вдалося видалити замовлення');
                });
            });
        };

    });

})();