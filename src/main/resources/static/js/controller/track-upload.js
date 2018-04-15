(function () {

    'use strict';

    const app = angular.module('ubtsMusicStore');

    app.controller('trackUploadCtrl', function (uploadService, $mdDialog) {

        this.form = {
            trackInfo: {},
            trackFiles: {},
            progressBar: {
                isVisible: false,
                counter: 0
            }
        };
        this.submitLock = false;

        this.submit = () => {
            if (this.submitLock) {
                return;
            }
            this.submitLock = true;
            uploadService.sendTrack(this.form.trackInfo, this.form.trackFiles, this.form.progressBar, () => {
                const alert = $mdDialog.alert().textContent('Пісню успішно завантажено').ok('Закрити');
                $mdDialog.show(alert);
                this.submitLock = false;
            }, res => {
                const errorText = (res.status !== 409) ? 'Не вдалося завантажити пісню' : 'Дана пісня вже збережена на сервері';
                const alert = $mdDialog.alert().title('Помилка').textContent(errorText).ok('Закрити');
                $mdDialog.show(alert);
                this.submitLock = false;
            });
        }
    });

})();
