1. [Create app](https://ionicframework.com/docs/intro/cli)

  ```shell
  npm install -g @ionic/cli
  ionic start
  cd meet-all
  ```

2. [PWA](https://ionicframework.com/docs/angular/pwa)

  ```shell
  ng add @angular/pwa
  ```

3. [i18n](https://angular.io/guide/i18n-common-add-package)

  ```shell
  ng add @angular/localize
  ```

:warning: Keep same `@angular/*` version (ex: `~12.1.1` instead of `^12.2.0`)

4. [GPS](https://ionicframework.com/docs/native/geolocation)

  ```shell
  npm install cordova-plugin-geolocation
  npm install @awesome-cordova-plugins/geolocation
  ionic cap sync
  ```

5. Dependencies

  ```shell
  npm install --save-dev nock protractor-backend-mock-plugin
  ```
