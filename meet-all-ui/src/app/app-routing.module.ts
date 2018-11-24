import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {SecurityGuard} from "./security";

@NgModule({
    imports: [
        RouterModule.forRoot([
            {path: '', loadChildren: './tabs/tabs.module#TabsPageModule', canActivate: [SecurityGuard]},
            {path: 'login', loadChildren: './login/login.module#LoginModule'},
        ])
    ],
    exports: [RouterModule],
})
export class AppRoutingModule {
}
