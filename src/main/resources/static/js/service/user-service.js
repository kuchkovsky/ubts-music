(function () {

    'use strict';

    const app = angular.module('ubtsMusicStore');

    app.service('userService', function ($http, apiUrl) {

        this.signup = (user, onSuccess, onError) => {
            $http({
                url: apiUrl + '/users',
                method: 'POST',
                data: user
            }).then(onSuccess, onError);
        };

        this.update = (user, onSuccess, onError) => {
            $http({
                url: apiUrl + '/users',
                method: 'PUT',
                data: user
            }).then(onSuccess, onError);
        };

    });

})();