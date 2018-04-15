(function () {

    'use strict';

    const app = angular.module('ubtsMusicStore');
    app.directive('compareTo', compareTo);
    compareTo.$inject = [];

    function compareTo() {
        return {
            require: "ngModel",
            scope: {
                compareTolValue: "=compareTo"
            },
            link: function(scope, element, attributes, ngModel) {
                ngModel.$validators.compareTo = function(modelValue) {
                    return modelValue === scope.compareTolValue;
                };
                scope.$watch("compareTolValue", function() {
                    ngModel.$validate();
                });
            }
        };
    }

    app.directive('windowWidth', function ($window) {
        return {
            restrict: 'A',
            link: function (scope){
                scope.width = $window.innerWidth;
                function onResize(){
                    if (scope.width !== $window.innerWidth) {
                        scope.width = $window.innerWidth;
                        scope.$digest();
                    }
                }
                function cleanUp() {
                    angular.element($window).off('resize', onResize);
                }
                angular.element($window).on('resize', onResize);
                scope.$on('$destroy', cleanUp);
            }
        };
    });

})();
