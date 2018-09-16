(function () {

    'use strict';

    const app = angular.module('ubtsMusicStore');

    app.service('trackService', function ($http, apiUrl) {

        this.deleteTrack = (track, onSuccess, onError) => {
            $http({
                url: apiUrl + '/tracks/' + track.id,
                method: 'DELETE'
            }).then(onSuccess, onError);
        }

    });

})();