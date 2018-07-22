const gulp = require('gulp');
const argv = require('yargs').argv;
const addStream = require('add-stream');
const ngConfig = require('gulp-ng-config');
const babel = require('gulp-babel');
const concat = require('gulp-concat');
const ngAnnotate = require('gulp-ng-annotate');
const plumber = require('gulp-plumber');
const uglify = require('gulp-uglify');
const bytediff = require('gulp-bytediff');

function makeConfig() {
    return gulp.src('app-config.json')
        .pipe(ngConfig('config', {
            environment: argv.environment
        }));
}

gulp.task('lib', function () {
    return gulp.src('src/main/resources/static/js/lib/*.js')
        .pipe(plumber())
        .pipe(concat('vendor.bundle.js'))
        .pipe(ngAnnotate({add: true}))
        .pipe(uglify({mangle: false}))
        .pipe(plumber.stop())
        .pipe(gulp.dest('target/classes/static/js/'));
});

gulp.task('dev', ['lib'], function () {
    return gulp.src(['src/main/resources/static/js/app.js', 'src/main/resources/static/js/**/*.js', '!**/lib/*.js'])
        .pipe(plumber())
        .pipe(addStream.obj(makeConfig()))
        .pipe(concat('app.bundle.js'))
        .pipe(babel({presets: ['env']}))
        .pipe(ngAnnotate({add: true}))
        .pipe(plumber.stop())
        .pipe(gulp.dest('target/classes/static/js/'));
});

gulp.task('build', ['dev'], function () {
    return gulp.src('target/classes/static/js/app.bundle.js')
        .pipe(plumber())
        .pipe(bytediff.start())
        .pipe(uglify({mangle: false}))
        .pipe(bytediff.stop())
        .pipe(plumber.stop())
        .pipe(gulp.dest('target/classes/static/js/'));
});

gulp.task('copy-resources', function() {
    return gulp.src(['src/main/resources/static/**/*', '!**/js/**'])
        .pipe(gulp.dest('target/classes/static/'));
});

gulp.task('watch-js', ['dev'], function () {
    return gulp.watch('src/main/resources/static/js/**/*.js', ['dev']);
});

gulp.task('watch-resources', ['copy-resources'], function () {
    return gulp.watch(['src/main/resources/static/**/*', '!**/js/**'], ['copy-resources']);
});
