(function () {

    'use strict';

    const app = angular.module('ubtsMusicStore');

    app.service('subscriptionService', function ($rootScope, $http, $mdDialog, downloadService, apiUrl) {

        function daysBetween(first, second) {
            const millisecondsPerDay = 1000 * 60 * 60 * 24;
            const millisBetween = second.getTime() - first.getTime();
            const days = millisBetween / millisecondsPerDay;
            return Math.floor(Math.abs(days));
        }

        this.checkSubscriptionStatus = () => {
            $rootScope.topInfoCard = {};
            if ($rootScope.isAuthenticated && !$rootScope.isAdmin) {
                $rootScope.isMainSpinnerVisible = true;
                $rootScope.isSubscriptionActive = false;
                downloadService.getCurrentUserSubscription(subscription => {
                    if (subscription.requestPending) {
                        $rootScope.topInfoCard.message = "Заявку надіслано. Очікуйте підтвердження оплати адміністратором"
                    } else if (subscription.banned) {
                        $rootScope.topInfoCard.message = "Запит на отримання підписки був відхилений або ваша підписка заблокована." +
                            " Для отримання доступу зв'яжіться з адміністрацією";
                    } else if (subscription.expired) {
                        $rootScope.topInfoCard.message = `Термін дії вашої підписки закінчився ${subscription.expirationDate}. Оформіть нову підписку`;
                        $rootScope.topInfoCard.showSubscribeButton = true;
                    } else if (!subscription.active) {
                        $rootScope.topInfoCard.message = "Для отримання доступу до музики необхідно оформити підписку";
                        $rootScope.topInfoCard.showSubscribeButton = true;
                    } else {
                        $rootScope.isSubscriptionActive = true;
                        if (subscription.expirationDate) {
                            const parts = subscription.expirationDate.split(".");
                            console.log(parts);
                            const expirationDate = new Date(parts[2], parts[1] - 1, parts[0]);
                            if (daysBetween(new Date(), expirationDate) < 3) {
                                $rootScope.topInfoCard.message = `Термін дії вашої підписки закінчується ${subscription.expirationDate}. Оформіть нову підписку`;
                                $rootScope.topInfoCard.showSubscribeButton = true;
                            }
                        }
                    }
                    $rootScope.isMainSpinnerVisible = false;
                }, () => {
                    $rootScope.isMainSpinnerVisible = false;
                    const alert = $mdDialog.alert().title('Помилка')
                        .textContent('Не вдалося завантажити інформацію про підписку').ok('Закрити');
                    $mdDialog.show(alert);
                });
            }
        };

        this.sendSubscriptionRequest = (onSuccess, onError) => {
            $http({
                url: apiUrl + '/subscriptions/requests',
                method: 'POST',
                data: { activate: true }
            }).then(() => {
                this.checkSubscriptionStatus();
                onSuccess();
            }, onError);
        };

        function updateSubscription(subscriptionRequest, userEmail, onSuccess, onError) {
            $http({
                url: apiUrl + '/subscriptions/requests/' + userEmail,
                method: 'POST',
                data: subscriptionRequest
            }).then(onSuccess, onError);
        }

        function changeStatus(activate, subscription, onSuccess, onError) {
            updateSubscription({
                activate: activate
            }, subscription.user.email, () => {
                subscription.requestPending = false;
                subscription.active = activate;
                onSuccess();
            }, onError);
        }

        this.activateSubscription = (subscription, onSuccess, onError) => {
            changeStatus(true, subscription, onSuccess, onError);
        };

        this.deactivateSubscription = (subscription, onSuccess, onError) => {
            changeStatus(false, subscription, onSuccess, onError);
        };

    })

})();