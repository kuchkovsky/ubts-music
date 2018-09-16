(function () {

    'use strict';

    const app = angular.module('ubtsMusicStore');

    app.controller('accountCtrl', function ($rootScope, $mdDialog, $location, downloadService, userService) {

        this.submitLock = false;

        this.getUserData = () => {
            $rootScope.isMainSpinnerVisible = true;
            downloadService.getCurrentUser(user => {
                this.user = user;
                $rootScope.isMainSpinnerVisible = false;
            }, () => {
                $rootScope.isMainSpinnerVisible = false;
                const alert = $mdDialog.alert().title('Помилка')
                    .textContent('Не вдалося завантажити дані користувача').ok('Закрити');
                $mdDialog.show(alert);
            });
        };

        if ($rootScope.$state.current.name === 'account') {
            this.getUserData();
        }

        this.update = () => {
            if (this.submitLock) {
                return;
            }
            this.submitLock = true;
            userService.update(this.user, () => {
                const alert = $mdDialog.alert().textContent('Дані успішно оновлено').ok('Закрити');
                $mdDialog.show(alert);
                this.submitLock = false;
            }, () => {
                const alert = $mdDialog.alert().title('Помилка').textContent('Не вдалося оновити дані').ok('Закрити');
                $mdDialog.show(alert);
                this.submitLock = false;
            });
        };

        this.signup = () => {
            if (this.submitLock) {
                return;
            }
            this.submitLock = true;
            userService.signup(this.user, () => {
                const alert = $mdDialog.alert().title('Вітаємо').textContent('Акаунт успішно створено').ok('Закрити');
                $mdDialog.show(alert).then(() => {
                    $location.path("/login");
                });
                this.submitLock = false;
            }, res => {
                const errorText = (res.status === 409) ? 'Користувач з даним email вже зареєстрований в системі'
                    : 'Не вдалося створити акаунт';
                const alert = $mdDialog.alert().title('Помилка').textContent(errorText).ok('Закрити');
                $mdDialog.show(alert);
                this.submitLock = false;
            });
        };

    });

})();
