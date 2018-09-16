(function () {

    'use strict';

    const app = angular.module('ubtsMusicStore');

    app.service('uploadService', function ($http, apiUrl) {

        this.sendTrack = (trackInfo, trackFiles, progressBar, onSuccess, onError) => {
            const formData = new FormData();
            formData.append('artist', trackInfo.artist);
            formData.append('title', trackInfo.title);
            formData.append('price', trackInfo.price);
            if (trackInfo.sampleAudioUrl) {
                formData.append('sampleAudioUrl', trackInfo.sampleAudioUrl);
            }
            if (trackFiles.sampleAudio[0]) {
                formData.append('sampleAudio', trackFiles.sampleAudio[0].lfFile);
            }
            formData.append('audio', trackFiles.audio[0].lfFile);
            formData.append('pdfChords', trackFiles.pdfChords[0].lfFile);
            formData.append('docChords', trackFiles.docChords[0].lfFile);
            formData.append('notes', trackFiles.notes[0].lfFile);
            formData.append('presentation', trackFiles.presentation[0].lfFile);
            progressBar.counter = 0;
            progressBar.isVisible = true;
            const application = angular.element(document.getElementById('main-content-top'));
            application.scrollTop(application[0].scrollHeight, 500);
            console.log('Posting files...');
            $http.post(apiUrl + '/files/tracks', formData, {
                transformRequest: angular.identity,
                headers: { 'Content-Type': undefined },
                uploadEventHandlers: {
                    progress: function (e) {
                        if (e.lengthComputable) {
                            progressBar.counter = (e.loaded / e.total) * 100;
                        }
                    }
                }
            }).then(res => {
                console.log('Response: ' + res.status);
                progressBar.isVisible = false;
                onSuccess(res);
            }, res => {
                progressBar.isVisible = false;
                onError(res);
            });
        };

    });

})();