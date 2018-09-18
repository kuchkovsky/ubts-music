(function () {

    'use strict';

    const app = angular.module('ubtsMusicStore');

    app.controller('trackUploadCtrl', function (uploadService, downloadService, $rootScope, $mdDialog, $state, $stateParams, $location) {

        this.form = {
            trackInfo: {},
            trackFiles: {},
            progressBar: {
                isVisible: false,
                counter: 0
            }
        };

        this.submitLock = false;

        this.isUpload = $state.current.name === 'trackUpload';

        if (!this.isUpload) {
            $rootScope.isMainSpinnerVisible = true;
            downloadService.getTracksByIds([$stateParams.trackId], tracks => {
                this.form.trackInfo = tracks[0];
                $rootScope.isMainSpinnerVisible = false;
            }, () => {
                const alert = $mdDialog.alert().title('Помилка')
                    .textContent('Не вдалося завантажити дані трека').ok('Закрити');
                $mdDialog.show(alert);
                $rootScope.isMainSpinnerVisible = false;
            });
        }

        this.submit = () => {
            if (this.submitLock) {
                return;
            }
            this.submitLock = true;
            if (this.isUpload) {
                uploadService.sendTrack(this.form.trackInfo, this.form.trackFiles, this.form.progressBar, () => {
                    const alert = $mdDialog.alert().textContent('Пісню успішно завантажено').ok('Закрити');
                    this.submitLock = false;
                    $mdDialog.show(alert).then(() => $state.reload());
                }, res => {
                    const errorText = (res.status !== 409) ? 'Не вдалося завантажити пісню' : 'Дана пісня вже збережена на сервері';
                    const alert = $mdDialog.alert().title('Помилка').textContent(errorText).ok('Закрити');
                    $mdDialog.show(alert);
                    this.submitLock = false;
                });
            } else {
                uploadService.editTrack($stateParams.trackId, this.form.trackInfo, this.form.trackFiles, this.form.progressBar, () => {
                    const alert = $mdDialog.alert().textContent('Пісню успішно завантажено').ok('Закрити');
                    this.submitLock = false;
                    $mdDialog.show(alert).then(() => $location.path("/"));
                }, () => {
                    const errorText = 'Не вдалося завантажити пісню';
                    const alert = $mdDialog.alert().title('Помилка').textContent(errorText).ok('Закрити');
                    $mdDialog.show(alert);
                    this.submitLock = false;
                });
            }
        }

    });

})();
