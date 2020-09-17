import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {SecurityGuard} from './security';

@NgModule({
    imports: [
        RouterModule.forRoot([
            {path: 'login', loadChildren: './login/login.module#LoginModule'},
            {path: '', loadChildren: './tabs/tabs.module#TabsPageModule', canActivate: [SecurityGuard]}
        ])
    ],
    exports: [RouterModule],
})
export class AppRoutingModule {
}
