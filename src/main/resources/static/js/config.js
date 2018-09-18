(function () {

    'use strict';

    const app = angular.module('ubtsMusicStore');

    app.config(function ($httpProvider) {
        $httpProvider.interceptors.push('authInterceptor');
    });

    app.config(function($mdThemingProvider) {
        $mdThemingProvider.definePalette('ubtsBrownOrange', {
            '50': 'fbe9e7',
            '100': 'ffccbc',
            '200': 'ffab91',
            '300': 'ff8a65',
            '400': 'ff7043',
            '500': 'ff5722',
            '600': 'f4511e',
            '700': 'e64a19',
            '800': 'bf5e3b',
            '900': 'bf360c',
            'A100': 'ff9e80',
            'A200': 'ff6e40',
            'A400': 'ff3d00',
            'A700': 'dd2c00'
        });
        $mdThemingProvider.theme('default')
            .primaryPalette('ubtsBrownOrange')
            .accentPalette('red');
    });

    app.config(function($stateProvider, $urlRouterProvider) {
        const resolveDelay = {
            delay: function($state, $timeout) {
                if ($state.current.name) {
                    return $timeout(function(){}, 300);
                }
            }
        };
        $urlRouterProvider.otherwise('/');
        $stateProvider
            .state('home', {
                url: '/',
                templateUrl : 'templates/track-list.html',
                data : { pageTitle: 'UBTS Music Store' },
                resolve: resolveDelay
            })
            .state('login', {
                url: "/login",
                templateUrl: 'templates/login.html',
                data: { pageTitle: 'Вхід в акаунт' },
                resolve: resolveDelay
            })
            .state('signup', {
                url: "/sign-up",
                templateUrl: 'templates/account.html',
                data: { pageTitle: 'Реєстрація' },
                resolve: resolveDelay
            })
            .state('account', {
                url: "/account",
                templateUrl: 'templates/account.html',
                data: { pageTitle: 'Особистий кабінет' },
                resolve: resolveDelay
            })
            .state('library', {
                url: "/library",
                templateUrl : 'templates/track-list.html',
                data: { pageTitle: 'Моя бібліотека' },
                resolve: resolveDelay
            })
            .state('subscriptions', {
                url: "/subscriptions",
                templateUrl : 'templates/subscription-list.html',
                data: { pageTitle: 'Список підписок' },
                resolve: resolveDelay
            })
            .state('trackUpload', {
                url: "/tracks/upload",
                templateUrl : 'templates/track-upload.html',
                data: { pageTitle: 'Додати пісню' },
                resolve: resolveDelay
            })
            .state('trackEdit', {
                url: "/tracks/{trackId}",
                templateUrl : 'templates/track-upload.html',
                data: { pageTitle: 'Редагувати пісню' },
                resolve: resolveDelay
            });
    });

})();
