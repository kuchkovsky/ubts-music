(function () {

    'use strict';

    const app = angular.module('ubtsMusicStore');

    app.controller('subscriptionListCtrl', function ($mdDialog, $rootScope, downloadService, subscriptionService) {
        this.subscriptions = [];
        this.form = {};
        this.selectedNavItem = 'requestPendingSubscriptions';
        this.requestPending = true;

        this.search = () => {
            if (!this.form.query) {
                this.form.subscriptions = this.subscriptions.slice();
                return;
            }
            this.form.subscriptions = [];
            const query = this.form.query.toLowerCase();
            this.subscriptions.forEach(subscription => {
                const userName = subscription.user.firstName + ' ' + subscription.user.lastName;
                if (subscription.user.id === Number.parseInt(query) || userName.toLowerCase().indexOf(query) !== -1) {
                    this.form.subscriptions.push(subscription);
                }
            });
        };

        this.onSuccess = (subscriptions) => {
            this.subscriptions = subscriptions;
            this.form.subscriptions = this.subscriptions.slice();
            this.search();
            this.isSpinnerVisible = false;
        };

        this.onError = () => {
            const alert = $mdDialog.alert().title('Помилка').textContent('Не вдалося завантажити список').ok('Закрити');
            $mdDialog.show(alert);
            this.isSpinnerVisible = false;
        };

        this.loadSubscriptions = () => {
            this.isSpinnerVisible = true;
            downloadService.getSubscriptions(this.onSuccess, this.onError);
        };

        function showError(text) {
            const alert = $mdDialog.alert().title('Помилка').textContent(text).ok('Закрити');
            $mdDialog.show(alert);
        }

        this.activateSubscription = subscription => {
            subscriptionService.activateSubscription(subscription, () => {}, () => {
                showError('Не вдалося активувати підписку');
            });
        };

        this.deactivateSubscription = subscription => {
            subscriptionService.deactivateSubscription(subscription, () => {}, () => {
                showError('Не вдалося заблокувати підписку');
            });
        };

        this.loadSubscriptions();

    });

})();