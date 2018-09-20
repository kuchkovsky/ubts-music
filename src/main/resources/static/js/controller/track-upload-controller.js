(function () {

    'use strict';

    const app = angular.module('ubtsMusicStore');

    app.controller('trackUploadCtrl', function (uploadService, downloadService, $rootScope, $mdDialog, $state, $stateParams, $location) {

        this.form = {
            trackInfo: {
                tags: [],
                newTag: ''
            },
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
                this.form.trackInfo.newTag = '';
                $rootScope.isMainSpinnerVisible = false;
            }, () => {
                const alert = $mdDialog.alert().title('Помилка')
                    .textContent('Не вдалося завантажити дані трека').ok('Закрити');
                $mdDialog.show(alert);
                $rootScope.isMainSpinnerVisible = false;
            });
        }

        this.addTag = () => {
            if (this.form.trackInfo.newTag !== '' && this.form.trackInfo.tags.findIndex(t => t.name === this.form.trackInfo.newTag) === -1) {
                this.form.trackInfo.tags.push({name: this.form.trackInfo.newTag});
            }
            this.form.trackInfo.newTag = '';
        };

        this.deleteTag = tag => this.form.trackInfo.tags = this.form.trackInfo.tags.filter(t => t !== tag);

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
