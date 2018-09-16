(function () {

    'use strict';

    const app = angular.module('ubtsMusicStore');

    app.controller('loginCtrl', function (authService, $location) {
        this.credentials = {};
        this.error = false;

        this.login = () => {
            authService.login(this.credentials, () => {
                    $location.path("/");
                    this.error = false;
                }, () => {
                    this.error = true;
                });
        };

    });

})();
