import {NgModule} from '@angular/core';
import {PreloadAllModules, RouterModule} from '@angular/router';
import {SecurityGuard} from './security';

@NgModule({
    imports: [
        RouterModule.forRoot(
          [
              {path: 'login', loadChildren: () => import('./login/login.module').then(m => m.LoginModule)},
              {path: '', loadChildren: () => import('./tabs/tabs.module').then(m => m.TabsPageModule), canActivate: [SecurityGuard]}
          ],
          {preloadingStrategy: PreloadAllModules})
    ],
    exports: [RouterModule],
})
export class AppRoutingModule {
}
