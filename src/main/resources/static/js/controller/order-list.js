(function () {

    'use strict';

    const app = angular.module('ubtsMusicStore');

    app.controller('orderListCtrl', function ($mdDialog, $rootScope, downloadService, orderService) {
        this.orders = [];
        this.form = {};
        this.selectedNavItem = 'newOrders';

        this.search = () => {
            if (!this.form.query) {
                this.form.orders = this.orders.slice();
                return;
            }
            this.form.orders = [];
            const query = this.form.query.toLowerCase();
            this.orders.forEach(order => {
                let userName = '';
                if ($rootScope.isAdmin) {
                    userName = order.user.firstName + ' ' + order.user.lastName;
                }
                if (order.id === Number.parseInt(query) || userName.toLowerCase().indexOf(query) !== -1) {
                    this.form.orders.push(order);
                }
            });
        };

        this.onSuccess = (orders) => {
            this.orders = orders;
            this.form.orders = this.orders.slice();
            this.search();
            this.isSpinnerVisible = false;
        };

        this.onError = () => {
            const alert = $mdDialog.alert().title('Помилка').textContent('Не вдалося завантажити список').ok('Закрити');
            $mdDialog.show(alert);
            this.isSpinnerVisible = false;
        };

        this.loadNewOrders = () => {
            this.isSpinnerVisible = true;
            downloadService.getAllUserOrders(this.onSuccess, this.onError);
        };

        this.loadArchiveOrders = () => {
            this.orders = [];
            this.form.orders = [];
        };

        this.loadCurrentUserOrders = () => {
            this.isSpinnerVisible = true;
            downloadService.getCurrentUserOrders(this.onSuccess, this.onError);
        };

        function showError(text) {
            const alert = $mdDialog.alert().title('Помилка').textContent(text).ok('Закрити');
            $mdDialog.show(alert);
        }

        this.confirmOrder = order => {
            orderService.confirmOrder(order, () => {}, () => {
                showError('Не вдалося підтвердити замовлення');
            });
        };

        this.rejectOrder = order => {
            orderService.rejectOrder(order, () => {}, () => {
                showError('Не вдалося відхилити замовлення');
            });
        };

        this.deleteOrder = order => {
            const confirm = $mdDialog.confirm()
                .title('Підтвердження операції')
                .textContent('Ви дійсно бажаєте видалити замовлення?')
                .ok('Так')
                .cancel('Ні');
            $mdDialog.show(confirm).then(() => {
                orderService.deleteOrder(order, () => {
                    this.orders = this.orders.filter(o => o.id !== order.id);
                    this.form.orders = this.form.orders.filter(o => o.id !== order.id);
                }, () => {
                    showError('Не вдалося видалити замовлення');
                });
            });
        };

        if ($rootScope.isAdmin) {
            this.loadNewOrders();
        } else {
            this.loadCurrentUserOrders();
        }

    });

})();