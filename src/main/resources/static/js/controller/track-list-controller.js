(function () {

    'use strict';

    const app = angular.module('ubtsMusicStore');

    app.controller('trackListCtrl', function ($rootScope, $mdDialog, downloadService, trackService) {

        this.tracks = [];
        this.form = {};

        this.search = () => {
            if (!this.form.query) {
                this.form.tracks = this.tracks.slice();
                return;
            }
            this.form.tracks = [];
            const query = this.form.query.toLowerCase();
            this.tracks.forEach(track => {
                const fullName = track.artist.toLowerCase() + ' - ' + track.title.toLowerCase();
                const hasTag = track.tags.some(tag => tag.name.toLowerCase().indexOf(query) !== -1);
                if (fullName.indexOf(query) !== -1 || hasTag) {
                    this.form.tracks.push(track);
                }
            });
        };

        this.searchByTag = tag => {
            this.form.query = tag.name;
            this.search();
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

        downloadService.getTracks(tracks => onSuccess(tracks), onFailure);

        function showDownloadError() {
            const alert = $mdDialog.alert().title('Помилка').textContent('Не вдалося завантажити файл').ok('Закрити');
            $mdDialog.show(alert);
        }

        this.getSampleAudio = track => downloadService.getSampleAudio(track);

        this.getAudio = track => downloadService.getAudio(track, () => showDownloadError());

        this.getPdfChords = track => downloadService.getPdfChords(track, () => showDownloadError());

        this.getDocChords = track => downloadService.getDocChords(track, () => showDownloadError());

        this.getNotes = track => downloadService.getNotes(track, () => showDownloadError());

        this.getPresentation = track => downloadService.getPresentation(track, () => showDownloadError());

        this.deleteTrack = track => {
            const confirm = $mdDialog.confirm()
                .title('Підтвердження операції')
                .textContent('Ви дійсно бажаєте видалити цю пісню?')
                .ok('Так')
                .cancel('Ні');
            $mdDialog.show(confirm).then(() => {
                trackService.deleteTrack(track, () => {
                    this.tracks = this.tracks.filter(t => t.id !== track.id);
                    this.form.tracks = this.form.tracks.filter(t => t.id !== track.id);
                }, () => {
                    const alert = $mdDialog.alert().title('Помилка').textContent('Не вдалося видалити пісню').ok('Закрити');
                    $mdDialog.show(alert);
                });
            });
        }

    });

})();
