(function () {

    'use strict';

    const app = angular.module('ubtsMusicStore');

    app.controller('trackListCtrl', function ($rootScope, $mdDialog, downloadService, trackService) {

        this.tracks = [];
        this.form = {};
        this.isMainList = $rootScope.$state.$current.name === 'home';

        this.search = () => {
            if (!this.form.query) {
                this.form.tracks = this.tracks.slice();
                return;
            }
            this.form.tracks = [];
            const query = this.form.query.toLowerCase();
            this.tracks.forEach(track => {
                const fullName = track.artist.toLowerCase() + ' - ' + track.title.toLowerCase();
                if (fullName.indexOf(query) !== -1) {
                    this.form.tracks.push(track);
                }
            });
        };

        const onSuccess = tracks => {
            this.tracks = tracks;
            this.form.tracks = this.tracks.slice();
            this.isSpinnerVisible = false;
        };

        const onFailure = () => {
            const alert = $mdDialog.alert().title('Помилка').textContent('Не вдалося завантажити список').ok('Закрити');
            $mdDialog.show(alert);
            this.isSpinnerVisible = false;
        };

        this.isSpinnerVisible = true;

        if (this.isMainList) {
            downloadService.getTracks(tracks => onSuccess(tracks), onFailure);
        } else {
            downloadService.getUserTracks(tracks => onSuccess(tracks), onFailure);
        }

        function showDownloadError() {
            const alert = $mdDialog.alert().title('Помилка').textContent('Не вдалося завантажити файл').ok('Закрити');
            $mdDialog.show(alert);
        }

        this.getSampleAudio = t => downloadService.getSampleAudio(t);

        this.getAudio = t => downloadService.getAudio(t, () => showDownloadError());

        this.getChords = t => downloadService.getChords(t, () => showDownloadError());

        this.getNotes = t => downloadService.getNotes(t, () => showDownloadError());

        this.getPresentation = t => downloadService.getPresentation(t, () => showDownloadError());

        this.deleteTrack = t => {
            const confirm = $mdDialog.confirm()
                .title('Підтвердження операції')
                .textContent('Ви дійсно бажаєте видалити цю пісню?')
                .ok('Так')
                .cancel('Ні');
            $mdDialog.show(confirm).then(() => {
                trackService.deleteTrack(t, () => {
                    const trackIndex = this.tracks.findIndex(track => track.id === t.id);
                    this.tracks.splice(trackIndex, 1);
                    const trackFormIndex = this.form.tracks.findIndex(track => track.id === t.id);
                    this.form.tracks.splice(trackFormIndex, 1);
                }, () => {
                    const alert = $mdDialog.alert().title('Помилка').textContent('Не вдалося видалити пісню').ok('Закрити');
                    $mdDialog.show(alert);
                });
            });
        }

    });

})();
