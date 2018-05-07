(function () {

    'use strict';

    const app = angular.module('ubtsMusicStore');

    app.service('authService', function ($rootScope, $window, $http, $location) {

        function url_base64_decode(str) {
            let output = str.replace('-', '+').replace('_', '/');
            switch (output.length % 4) {
                case 0:
                    break;
                case 2:
                    output += '==';
                    break;
                case 3:
                    output += '=';
                    break;
                default:
                    throw 'Illegal base64url string!';
            }
            return window.atob(output);
        }

        function isAdmin() {
            const token = $window.localStorage.token;
            if (token) {
                const encodedProfile = token.split('.')[1];
                const profile = JSON.parse(url_base64_decode(encodedProfile));
                return profile.roles.includes('ROLE_ADMIN');
            }
            return false;
        }

        this.checkLoginStatus = () => {
            $rootScope.isAuthenticated = !!$window.localStorage.token;
            $rootScope.isAdmin = isAdmin();
        };

        this.login = (credentials, onSuccess, onFailure) => {
            $http({
                url: '/api/auth/login',
                method: 'POST',
                data: credentials
            }).then(res => {
                $window.localStorage.token = res.headers('Authorization').replace("Bearer ", "");
                $rootScope.isAuthenticated = true;
                $rootScope.isAdmin = isAdmin();
                onSuccess();
            }, () => {
                delete $window.localStorage.token;
                $rootScope.isAuthenticated = false;
                $rootScope.isAdmin = false;
                onFailure();
            });
        };

        this.logout = () => {
            delete $window.localStorage.token;
            $rootScope.isAuthenticated = false;
            $rootScope.isAdmin = false;
            $location.path("/login");
        };

    });
    
    app.service('userService', function ($http) {
        
        this.signup = (user, onSuccess, onError) => {
            $http({
                url: '/api/users',
                method: 'POST',
                data: user
            }).then(onSuccess, onError);
        };

        this.update = (user, onSuccess, onError) => {
            $http({
                url: '/api/users',
                method: 'PUT',
                data: user
            }).then(onSuccess, onError);
        };

    });

    app.service('downloadService', function ($http) {

        function download(url, onSuccess, onError) {
            $http.get(url).then(response => onSuccess(response.data), onError);
        }

        this.getTracks = (onSuccess, onError) => {
            download('/api/tracks', onSuccess, onError);
        };

        this.getTracksByIds = (trackIds, onSuccess, onError) => {
            $http.get('/api/tracks', {
                params: {
                    id: trackIds
                }
            }).then(response => onSuccess(response.data), onError);
        };
        
        this.getCurrentUser = (onSuccess, onError) => {
            download('/api/users/me', onSuccess, onError);
        };

        this.getUserTracks = (onSuccess, onError) => {
            download('/api/tracks/my', onSuccess, onError);
        };

        this.getCurrentUserOrders = (onSuccess, onError) => {
            download('/api/orders/my', onSuccess, onError);
        };

        this.getAllUserOrders = (onSuccess, onError) => {
            download('/api/orders', onSuccess, onError);
        };

        this.getOrder = (id, onSuccess, onError) => {
            download('/api/orders/' + id, onSuccess, onError);
        };

        function secureTrackFileDownload(track, fileType, onError) {
            download('/api/tokens/tracks/' + track.id, data => {
                const anchor = angular.element('<a/>');
                anchor.attr({
                    href: 'api/files/tracks/' + fileType + '/' + track.id + '?token=' + data.token,
                })[0].click();
                anchor.remove();
            }, onError);
        }

        this.getSampleAudio = track => {
            const anchor = angular.element('<a/>');
            if (track.sampleAudioUrl) {
                anchor.attr({
                    href: track.sampleAudioUrl,
                    target: '_blank'
                })[0].click();
            } else {
                anchor.attr({
                    href: 'api/files/tracks/sample/' + track.id,
                })[0].click();
            }
            anchor.remove();
        };

        this.getAudio = (track, onError) => secureTrackFileDownload(track, 'audio', onError);

        this.getPdfChords = (track, onError) => secureTrackFileDownload(track, 'chords/pdf', onError);

        this.getDocChords = (track, onError) => secureTrackFileDownload(track, 'chords/doc', onError);

        this.getNotes = (track, onError) => secureTrackFileDownload(track, 'notes', onError);

        this.getPresentation = (track, onError) => secureTrackFileDownload(track, 'presentation', onError);

    });

    app.service('uploadService', function ($http) {

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
            $http.post('/api/files/tracks', formData, {
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

    app.service('cartService', function ($window, downloadService) {

        this.trackIdList = [];
        this.trackList = [];
        this.totalPrice = 0;

        this.addToCart = track => {
            this.trackIdList.push(track.id);
            this.trackList.push(track);
            this.totalPrice += track.price;
            this.saveToLocalStorage();
        };

        this.removeFromCart = track => {
            this.trackIdList = this.trackIdList.filter(t => t !== track.id);
            this.trackList = this.trackList.filter(t => t.id !== track.id);
            this.totalPrice -= track.price;
            this.saveToLocalStorage();
        };

        this.isTrackInCart = track => this.trackIdList.indexOf(track.id) !== -1;

        this.clearCart = () => {
            this.trackIdList = [];
            this.trackList = [];
            this.totalPrice = 0;
            this.saveToLocalStorage();
        };

        this.isCartEmpty = () => this.trackIdList.length === 0;

        this.saveToLocalStorage = () => $window.localStorage.trackIdList = JSON.stringify(this.trackIdList);

        this.loadFromLocalStorage = () => this.trackIdList = JSON.parse($window.localStorage.trackIdList);

        if ($window.localStorage.trackIdList) {
            this.loadFromLocalStorage();
            if (!this.isCartEmpty()) {
                downloadService.getTracksByIds(this.trackIdList, trackList => {
                    this.trackList = trackList;
                    this.trackList.forEach(track => this.totalPrice += track.price);
                });
            }
        } else {
            this.saveToLocalStorage();
        }

    });

    app.service('trackService', function ($http) {

        this.deleteTrack = (track, onSuccess, onError) => {
            $http({
                url: '/api/tracks/' + track.id,
                method: 'DELETE'
            }).then(onSuccess, onError);
        }

    });

    app.service('orderService', function ($http, cartService) {

        this.sendOrder = (onSuccess, onError) => {
            $http({
                url: '/api/orders',
                method: 'POST',
                data: { id: cartService.trackIdList }
            }).then(onSuccess, onError);
        };

        function updateOrder(order, onSuccess, onError) {
            $http({
                url: '/api/orders',
                method: 'PUT',
                data: order
            }).then(onSuccess, onError);
        }

        function changeStatus(confirm, order, onSuccess, onError) {
            updateOrder({
                id: order.id,
                confirmed: confirm,
                pending: false
            }, () => {
                order.confirmed = confirm;
                order.pending = false;
                onSuccess();
            }, onError);
        }

        this.confirmOrder = (order, onSuccess, onError) => {
            changeStatus(true, order, onSuccess, onError);
        };

        this.rejectOrder = (order, onSuccess, onError) => {
            changeStatus(false, order, onSuccess, onError);
        };

        this.deleteOrder = (order, onSuccess, onError) => {
            $http({
                url: '/api/orders/' + order.id,
                method: 'DELETE'
            }).then(onSuccess, onError);
        };

    });

})();
