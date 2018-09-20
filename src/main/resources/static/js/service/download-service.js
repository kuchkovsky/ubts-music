(function () {

    'use strict';

    const app = angular.module('ubtsMusicStore');

    app.service('downloadService', function ($http, apiUrl) {

        function download(url, onSuccess, onError) {
            $http.get(url).then(response => onSuccess(response.data), onError);
        }

        this.getTracks = (onSuccess, onError) => {
            download(apiUrl + '/tracks', onSuccess, onError);
        };

        this.getTracksByIds = (trackIds, onSuccess, onError) => {
            $http.get(apiUrl + '/tracks', {
                params: {
                    id: trackIds
                }
            }).then(response => onSuccess(response.data), onError);
        };

        this.getCurrentUser = (onSuccess, onError) => {
            download(apiUrl + '/users/me', onSuccess, onError);
        };

        this.getCurrentUserSubscription = (onSuccess, onError) => {
            download(apiUrl + '/subscriptions/my', onSuccess, onError);
        };

        this.getSubscriptions = (onSuccess, onError) => {
            download(apiUrl + '/subscriptions', onSuccess, onError);
        };

        function secureTrackFileDownload(track, fileType, onError) {
            download(apiUrl + '/tokens/tracks/' + track.id, data => {
                const anchor = angular.element('<a/>');
                anchor.attr({
                    href: apiUrl + '/files/tracks/' + fileType + '/' + track.id + '?token=' + data.token,
                })[0].click();
                anchor.remove();
            }, onError);
        }

        this.getSampleAudioUrl = track => apiUrl + '/files/tracks/sample/' + track.id;

        this.getSampleAudio = track => {
            const anchor = angular.element('<a/>');
            anchor.attr({
                href: this.getSampleAudioUrl(track),
            })[0].click();
            anchor.remove();
        };

        this.getPdfChords = (track, onError) => secureTrackFileDownload(track, 'chords/pdf', onError);

        this.getDocChords = (track, onError) => secureTrackFileDownload(track, 'chords/doc', onError);

        this.getNotes = (track, onError) => secureTrackFileDownload(track, 'notes', onError);

        this.getPresentation = (track, onError) => secureTrackFileDownload(track, 'presentation', onError);

    });

})();